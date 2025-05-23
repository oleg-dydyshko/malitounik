package by.carkva_gazeta.admin

import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.CalendarBinding
import by.carkva_gazeta.malitounik.Settings
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


class CaliandarMun : BaseActivity(), CaliandarMunTab1.CaliandarMunTab1Listener, CaliandarMunTab2.CaliandarMunTab2Listener, DialogCaliandarMunDate.DialogCaliandarMunDateListener {
    private var yearG1 = 0
    private var posMun1 = 0
    private var day1 = 0
    private var yearG2 = 0
    private var posMun2 = 0
    private var day2 = 0
    private lateinit var chin: SharedPreferences
    private var getData = false
    private lateinit var binding: CalendarBinding
    private var resetTollbarJob: Job? = null

    override fun setDataCalendar(dataCalendar: Int) {
        val fragment = supportFragmentManager.findFragmentByTag("mun") as? CaliandarMunTab1
        fragment?.setDataCalendar(dataCalendar)
    }

    override fun setDayAndMun1(day: Int, mun: Int, year: Int) {
        day1 = day
        posMun1 = mun
        yearG1 = year
    }

    override fun setDayAndMun2(day: Int, mun: Int, year: Int, cviatyGlavnyia: String) {
        day2 = day
        posMun2 = mun
        yearG2 = year
        if (!cviatyGlavnyia.contains("no_sviaty")) {
            binding.subtitleToolbar.text = HtmlCompat.fromHtml(cviatyGlavnyia, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.subtitleToolbar.visibility = View.VISIBLE
        } else {
            binding.subtitleToolbar.visibility = View.GONE
        }
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
        binding.subtitleToolbar.isSingleLine = true
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", MODE_PRIVATE)
        binding = CalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        getData = intent.getBooleanExtra("getData", false)
        if (getData) binding.titleToolbar.setText(by.carkva_gazeta.malitounik.R.string.get_date)
        else binding.titleToolbar.setText(by.carkva_gazeta.malitounik.R.string.kaliandar)
        val c = Calendar.getInstance()
        if (savedInstanceState != null) {
            day1 = savedInstanceState.getInt("day")
            posMun1 = savedInstanceState.getInt("mun")
            yearG1 = savedInstanceState.getInt("year")
            day2 = savedInstanceState.getInt("day2")
            posMun2 = savedInstanceState.getInt("mun2")
            yearG2 = savedInstanceState.getInt("year2")
        } else {
            posMun1 = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
            yearG1 = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
            day1 = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
            posMun2 = posMun1
            if (yearG1 > Settings.GET_CALIANDAR_YEAR_MAX) yearG1 = Settings.GET_CALIANDAR_YEAR_MAX
            yearG2 = yearG1
            day2 = day1
        }
        val nedelia = chin.getInt("nedelia", 0)
        binding.tabLayout.getTabAt(nedelia)?.select()
        if (nedelia == 0) {
            replaceFragment(CaliandarMunTab1.getInstance(posMun1, yearG1, day1))
            binding.subtitleToolbar.visibility = View.GONE
        } else {
            replaceFragment(CaliandarMunTab2.getInstance(posMun2, yearG2, day2))
            binding.subtitleToolbar.visibility = View.VISIBLE
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                chin.edit {
                    putInt("nedelia", position)
                }
                if (position == 0) {
                    replaceFragment(CaliandarMunTab1.getInstance(posMun1, yearG1, day1))
                    binding.subtitleToolbar.visibility = View.GONE
                } else {
                    replaceFragment(CaliandarMunTab2.getInstance(posMun2, yearG2, day2))
                    binding.subtitleToolbar.visibility = View.VISIBLE
                }
                invalidateOptionsMenu()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val pos = chin.getInt("nedelia", 0)
        val tag = if (pos == 0) "mun"
        else "niadzelia"
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainer.id, fragment, tag)
        transaction.commit()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("day", day1)
        outState.putInt("mun", posMun1)
        outState.putInt("year", yearG1)
        outState.putInt("day2", day2)
        outState.putInt("mun2", posMun2)
        outState.putInt("year2", yearG1)
    }
}