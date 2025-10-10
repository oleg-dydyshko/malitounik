package by.carkva_gazeta.malitounik.admin

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.databinding.AdminChytannyBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar

class Chytanny : BaseActivity() {
    private lateinit var binding: AdminChytannyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val data = ArrayList<String>()
    private var isError = false
    private val list = ArrayList<CytanneList>()
    private lateinit var adapter: RecyclerViewAdapter

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    private fun loadChytanny(year: Int) {
        urlJob?.cancel()
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            list.clear()
            binding.progressBar2.visibility = View.VISIBLE
            val localFile = File("$filesDir/cache/cache.txt")
            var text = ""
            try {
                Malitounik.referens.child("/calendar-cytanne_$year.php").getFile(localFile).addOnSuccessListener {
                    text = localFile.readText()
                }.await()
            } catch (_: StorageException) {
                isError = true
                Toast.makeText(this@Chytanny, getString(R.string.error), Toast.LENGTH_SHORT).show()
                binding.progressBar2.visibility = View.GONE
                return@launch
            }
            isError = false
            val a = year % 19
            val b = year % 4
            val cx = year % 7
            val k = year / 100
            val p = (13 + 8 * k) / 25
            val q = k / 4
            val m = (15 - p + k - q) % 30
            val n = (4 + k - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            var monthP: Int
            var dataP: Int
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            val fileLine = text.split("\n")
            val nedelName = resources.getStringArray(R.array.dni_nedeli)
            val monName2 = resources.getStringArray(R.array.meciac_smoll)
            var countDay = 0
            for (fw in fileLine) {
                if (fw.contains("\$calendar[]")) {
                    val t1 = fw.indexOf("\"cviaty\"=>\"")
                    val t2 = fw.indexOf("\", \"")
                    val t3 = fw.indexOf("\".\$ahref.\"")
                    val t4 = fw.indexOf("</a>\"")
                    val c = GregorianCalendar(year, monthP - 1, dataP + countDay)
                    var data = c[Calendar.DATE]
                    var ned = c[Calendar.DAY_OF_WEEK]
                    var mon = c[Calendar.MONTH]
                    val data2 = c[Calendar.YEAR]
                    var datefull = SpannableString(nedelName[ned] + ", " + data + " " + monName2[mon] + " " + year)
                    countDay++
                    if (data2 != year) {
                        monthP = 1
                        dataP = 1
                        countDay = if (c.isLeapYear(year)) {
                            1
                        } else {
                            0
                        }
                        data = c[Calendar.DATE]
                        ned = c[Calendar.DAY_OF_WEEK]
                        mon = c[Calendar.MONTH]
                        datefull = SpannableString(nedelName[ned] + ", " + data + " " + monName2[mon] + " " + year)
                        countDay++
                    }
                    val c1 = nedelName[ned].length
                    val c2 = data.toString().length
                    val c3 = monName2[mon].length
                    datefull.setSpan(StyleSpan(Typeface.BOLD), c1 + 2, c1 + 2 + c2 + c3 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    val font = createFont(Typeface.NORMAL)
                    val font2 = createFont(Typeface.BOLD)
                    datefull.setSpan(CustomTypefaceSpan("", font), 0, c1 + 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    datefull.setSpan(CustomTypefaceSpan("", font2), c1 + 2, c1 + 2 + c2 + c3 + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    datefull.setSpan(CustomTypefaceSpan("", font), c1 + 2 + c2 + c3 + 1, datefull.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    list.add(CytanneList(datefull, fw.substring(t1 + 11, t2), fw.substring(t3 + 10, t4)))
                    //binding.linear.addView(grateTextView(datefull))
                    //binding.linear.addView(grateEditView(1, fw.substring(t1 + 11, t2)))
                    //binding.linear.addView(grateEditView(2, fw.substring(t3 + 10, t4)))
                } else {
                    list.add(CytanneList(SpannableString(""), fw, ""))
                    //binding.linear.addView(grateEditViewHidden(fw))
                }
            }
            binding.progressBar2.visibility = View.GONE
            adapter.notifyItemRangeChanged(0, list.size)
        }
    }

    fun createFont(style: Int): Typeface? {
        return when (style) {
            Typeface.BOLD -> ResourcesCompat.getFont(this, R.font.roboto_condensed_bold)
            Typeface.ITALIC -> ResourcesCompat.getFont(this, R.font.roboto_condensed_italic)
            Typeface.BOLD_ITALIC -> ResourcesCompat.getFont(this, R.font.roboto_condensed_bold_italic)
            else -> ResourcesCompat.getFont(this, R.font.roboto_condensed_regular)
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminChytannyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.getInsetsController(
            window,
            binding.root
        ).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        adapter = RecyclerViewAdapter(this, list)
        binding.recyclerView.adapter = adapter
        binding.root.setOnApplyWindowInsetsListener { view, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val inset = windowInsets.getInsets(WindowInsets.Type.systemBars())
                view.updatePadding(left = inset.left, top = inset.top, right = inset.right, bottom = inset.bottom)
            } else {
                val windowInsets = view.rootWindowInsets
                if (windowInsets != null) {
                    view.updatePadding(
                        windowInsets.stableInsetLeft, windowInsets.stableInsetTop,
                        windowInsets.stableInsetRight, windowInsets.stableInsetBottom
                    )
                }
            }
            windowInsets
        }
        isError = savedInstanceState?.getBoolean("isError", false) == true
        for (i in Settings.GET_CALIANDAR_YEAR_MIN..Settings.GET_CALIANDAR_YEAR_MAX) data.add(i.toString())
        binding.spinnerYear.adapter = SpinnerAdapter(this, data)
        binding.spinnerYear.setSelection(2)
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadChytanny(data[position].toInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(R.string.czytanne2)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
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
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            if (!isError) {
                val sb = StringBuilder()
                list.forEach { item ->
                    if (item.data.isEmpty()) {
                        sb.append(item.title + "\n")
                    } else {
                        sb.append("\$calendar[]=array(\"cviaty\"=>\"${item.title}\", \"cytanne\"=>\"\".\$ahref.\"${item.cynanne}</a>\");\n")
                    }
                }
                val year = data[binding.spinnerYear.selectedItemPosition].toInt()
                sendPostRequest(sb.toString().trim(), year)
            }
            return true
        }
        return false
    }

    private suspend fun saveLogFile(count: Int = 0) {
        val logFile = File("$filesDir/cache/log.txt")
        var error = false
        logFile.writer().use {
            it.write(getString(R.string.check_update_resourse))
        }
        Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
            Toast.makeText(this@Chytanny, getString(R.string.error), Toast.LENGTH_SHORT).show()
            error = true
        }.await()
        if (error && count < 3) {
            saveLogFile(count + 1)
        }
    }

    private fun sendPostRequest(cytanni: String, year: Int) {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = File("$filesDir/cache/cache.txt")
                    localFile.writer().use {
                        it.write(cytanni)
                    }
                    Malitounik.referens.child("/calendar-cytanne_$year.php").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@Chytanny, getString(R.string.save), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Chytanny, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                } catch (_: Throwable) {
                    Toast.makeText(this@Chytanny, getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                saveLogFile()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            Toast.makeText(this@Chytanny, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_chytanny, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isError", isError)
    }

    private class RecyclerViewAdapter(val context: Context, val list: ArrayList<CytanneList>) : RecyclerView.Adapter<RecyclerViewHolder>() {

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            if (list[position].data.isNotEmpty()) {
                holder.data.visibility = View.VISIBLE
                holder.title.visibility = View.VISIBLE
                holder.cytenne.visibility = View.VISIBLE
                holder.data.text = list[position].data
                holder.title.setText(list[position].title)
                holder.title.addTextChangedListener(TitleChangedListener(list, true, position))
                holder.cytenne.setText(list[position].cynanne)
                holder.cytenne.addTextChangedListener(TitleChangedListener(list, false, position))
            } else {
                holder.data.visibility = View.GONE
                holder.title.visibility = View.GONE
                holder.cytenne.visibility = View.GONE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.simple_list_item_cytanny, parent, false)
            return RecyclerViewHolder(view)
        }
    }

    private inner class SpinnerAdapter(private val activity: Activity, private val data: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_1, data) {

        private val gc = Calendar.getInstance()

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = data[position]
            if (gc[Calendar.YEAR] == data[position].toInt()) textView.typeface = createFont(Typeface.BOLD)
            else textView.typeface = createFont(Typeface.NORMAL)
            textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = data[position]
            if (gc[Calendar.YEAR] == data[position].toInt()) viewHolder.text.typeface = createFont(Typeface.BOLD)
            else viewHolder.text.typeface = createFont(Typeface.NORMAL)
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class RecyclerViewHolder : RecyclerView.ViewHolder {
        var data: TextViewCustom
        var title: EditTextCustom
        var cytenne: EditTextCustom

        constructor(view: View) : super(view) {
            data = view.findViewById<TextViewCustom>(R.id.data)
            title = view.findViewById<EditTextCustom>(R.id.title)
            cytenne = view.findViewById<EditTextCustom>(R.id.chytanne)
        }
    }

    private class TitleChangedListener(val list: ArrayList<CytanneList>, val isTitleEdit: Boolean, val listPosition: Int) : TextWatcher {
        private var editPosition = 0
        private var check = 0
        private var editch = true

        override fun afterTextChanged(s: Editable?) {
            if (editch) {
                val edit = s.toString()
                if (isTitleEdit) {
                    list[listPosition].title = edit
                } else {
                    list[listPosition].cynanne = edit
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            editch = count != after
            check = after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }
    }

    private class ViewHolder(var text: TextView)

    private data class CytanneList(val data: SpannableString, var title: String, var cynanne: String)
}