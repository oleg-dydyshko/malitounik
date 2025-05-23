package by.carkva_gazeta.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.admin.databinding.AdminBibleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StaryZapavietSemuxa : BaseActivity(), DialogBibleRazdel.DialogBibleRazdelListener {
    private var trak = false
    private var fullglav = 0
    private var kniga = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var setedit = false
    private var title = ""
    private lateinit var binding: AdminBibleBinding
    private var resetTollbarJob: Job? = null

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onComplete(glava: Int) {
        binding.pager.setCurrentItem(glava, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", MODE_PRIVATE)
        if (savedInstanceState != null) {
            setedit = savedInstanceState.getBoolean("setedit")
        }
        super.onCreate(savedInstanceState)
        binding = AdminBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        kniga = intent.extras?.getInt("kniga", 0) ?: 0
        glava = intent.extras?.getInt("glava", 0) ?: 0
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
            trak = true
        }
        binding.pager.offscreenPageLimit = 1
        val adapterViewPager = MyPagerAdapter(this)
        binding.pager.adapter = adapterViewPager
        when (kniga) {
            0 -> {
                title = "Быцьцё"
                fullglav = 50
            }
            1 -> {
                title = "Выхад"
                fullglav = 40
            }
            2 -> {
                title = "Лявіт"
                fullglav = 27
            }
            3 -> {
                title = "Лікі"
                fullglav = 36
            }
            4 -> {
                title = "Другі Закон"
                fullglav = 34
            }
            5 -> {
                title = "Ісуса сына Нава"
                fullglav = 24
            }
            6 -> {
                title = "Судзьдзяў"
                fullglav = 21
            }
            7 -> {
                title = "Рут"
                fullglav = 4
            }
            8 -> {
                title = "1-я Царстваў"
                fullglav = 31
            }
            9 -> {
                title = "2-я Царстваў"
                fullglav = 24
            }
            10 -> {
                title = "3-я Царстваў"
                fullglav = 22
            }
            11 -> {
                title = "4-я Царстваў"
                fullglav = 25
            }
            12 -> {
                title = "1-я Летапісаў"
                fullglav = 29
            }
            13 -> {
                title = "2-я Летапісаў"
                fullglav = 36
            }
            14 -> {
                title = "Эздры"
                fullglav = 10
            }
            15 -> {
                title = "Нээміі"
                fullglav = 13
            }
            16 -> {
                title = "Эстэр"
                fullglav = 10
            }
            17 -> {
                title = "Ёва"
                fullglav = 42
            }
            18 -> {
                title = "Псалтыр"
                fullglav = 151
            }
            19 -> {
                title = "Выслоўяў Саламонавых"
                fullglav = 31
            }
            20 -> {
                title = "Эклезіяста"
                fullglav = 12
            }
            21 -> {
                title = "Найвышэйшая Песьня Саламонава"
                fullglav = 8
            }
            22 -> {
                title = "Ісаі"
                fullglav = 66
            }
            23 -> {
                title = "Ераміі"
                fullglav = 52
            }
            24 -> {
                title = "Ераміін Плач"
                fullglav = 5
            }
            25 -> {
                title = "Езэкііля"
                fullglav = 48
            }
            26 -> {
                title = "Данііла"
                fullglav = 12
            }
            27 -> {
                title = "Асіі"
                fullglav = 14
            }
            28 -> {
                title = "Ёіля"
                fullglav = 3
            }
            29 -> {
                title = "Амоса"
                fullglav = 9
            }
            30 -> {
                title = "Аўдзея"
                fullglav = 1
            }
            31 -> {
                title = "Ёны"
                fullglav = 4
            }
            32 -> {
                title = "Міхея"
                fullglav = 7
            }
            33 -> {
                title = "Навума"
                fullglav = 3
            }
            34 -> {
                title = "Абакума"
                fullglav = 3
            }
            35 -> {
                title = "Сафона"
                fullglav = 3
            }
            36 -> {
                title = "Агея"
                fullglav = 2
            }
            37 -> {
                title = "Захарыі"
                fullglav = 14
            }
            38 -> {
                title = "Малахіі"
                fullglav = 4
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = if (kniga == 18) resources.getString(by.carkva_gazeta.malitounik.R.string.psalom) + " " + (position + 1) else resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (position + 1)
        }.attach()
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (glava != position) fierstPosition = 0
                invalidateOptionsMenu()
            }
        })
        binding.pager.setCurrentItem(glava, false)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.stary_zapaviet)
        binding.subtitleToolbar.text = title
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_glava) {
            val dialogBibleRazdel = DialogBibleRazdel.getInstance(fullglav)
            dialogBibleRazdel.show(supportFragmentManager, "full_glav")
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_bible, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount() = fullglav

        override fun createFragment(position: Int): StaryZapavietSemuxaFragment {
            val styx = if (glava != position) 0
            else fierstPosition
            return StaryZapavietSemuxaFragment.newInstance(position, kniga, styx)
        }
    }

    companion object {
        var fierstPosition = 0
    }
}