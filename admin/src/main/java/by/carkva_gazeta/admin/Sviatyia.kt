package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminSviatyiaBinding
import by.carkva_gazeta.admin.databinding.SimpleListItem1Binding
import by.carkva_gazeta.admin.databinding.SimpleListItemTipiconBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Settings
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar

class Sviatyia : BaseActivity(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var setedit = false
    private lateinit var binding: AdminSviatyiaBinding
    private var resetTollbarJob: Job? = null
    private var urlJob: Job? = null
    private val sviatyiaNew1 = ArrayList<ArrayList<String>>()
    private lateinit var caliandarDayOfYearList: ArrayList<ArrayList<String>>
    private val array: Array<String>
        get() = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.admin_svity)
    private val arrayList = ArrayList<Tipicon>()
    private var mLastClickTime: Long = 0
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                caliandarDayOfYearList.clear()
                caliandarDayOfYearList.add(arrayList)
                setDate()
            }
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onResume() {
        super.onResume()
        binding.apisanne.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.linearLayout2.visibility = View.VISIBLE
            } else {
                binding.linearLayout2.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        urlJob?.cancel()
        resetTollbarJob?.cancel()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.appBarLayout2.windowToken, 0)
        binding.apisanne.onFocusChangeListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", MODE_PRIVATE)
        if (savedInstanceState != null) {
            setedit = savedInstanceState.getBoolean("setedit")
        }
        binding = AdminSviatyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cal = Calendar.getInstance()
        val dayOfYear = intent.extras?.getInt("dayOfYear") ?: cal[Calendar.DAY_OF_YEAR]
        caliandarDayOfYearList = MenuCaliandar.getDataCalaindar(dayOfYear = dayOfYear, year = cal[Calendar.YEAR])
        arrayList.add(Tipicon(0, "Няма"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest, "З вялікай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_kruge, "Двунадзясятыя і вялікія сьвяты"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_polukruge, "З ліцьцёй на вячэрні"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk, "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk_black, "З штодзённай вячэрняй і малым услаўленьнем на ютрані"))
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionBr.setOnClickListener(this)
        binding.imageViewLeft.setOnClickListener(this)
        binding.imageViewRight.setOnClickListener(this)
        setDate()
        setTollbarTheme()
    }

    private fun setDate(count: Int = 0) {
        if (Settings.isNetworkAvailable(this)) {
            binding.progressBar2.visibility = View.VISIBLE
            val munName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
            binding.date.text = getString(by.carkva_gazeta.malitounik.R.string.admin_date, caliandarDayOfYearList[0][1].toInt(), munName[caliandarDayOfYearList[0][2].toInt()])
            urlJob?.cancel()
            urlJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    var builder = ""
                    val localFile = File("$filesDir/cache/cache.txt")
                    referens.child("/chytanne/sviatyja/opisanie" + (caliandarDayOfYearList[0][2].toInt() + 1) + ".json").getFile(localFile).addOnCompleteListener {
                        if (it.isSuccessful) builder = localFile.readText()
                        else Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                    }.await()
                    val gson = Gson()
                    builder = if (builder != "") {
                        val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
                        val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                        arrayList[caliandarDayOfYearList[0][1].toInt() - 1]
                    } else {
                        getString(by.carkva_gazeta.malitounik.R.string.error)
                    }
                    binding.apisanne.setText(builder)
                    val localFile2 = File("$filesDir/cache/cache2.txt")
                    var builder2 = ""
                    referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnCompleteListener {
                        if (it.isSuccessful) builder2 = localFile2.readText()
                        else Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                    }.await()
                    if (builder2 != "") {
                        val line = builder2.split("\n")
                        for (element in line) {
                            val reg = element.split("<>")
                            val list = ArrayList<String>()
                            for (element2 in reg) {
                                list.add(element2)
                            }
                            sviatyiaNew1.add(list)
                        }
                        binding.sviaty.setSelection(0)
                        binding.chytanne.setSelection(0)
                        binding.spinnerStyle.adapter = SpinnerAdapter(this@Sviatyia, array)
                        binding.spinnerZnak.adapter = SpinnerAdapterTipicon(this@Sviatyia, arrayList)
                        binding.sviaty.setText(sviatyiaNew1[caliandarDayOfYearList[0][24].toInt() - 1][0])
                        binding.chytanne.setText(sviatyiaNew1[caliandarDayOfYearList[0][24].toInt() - 1][1])
                        var position = 0
                        when (sviatyiaNew1[caliandarDayOfYearList[0][24].toInt() - 1][2].toInt()) {
                            6 -> position = 0
                            7 -> position = 1
                            8 -> position = 2
                        }
                        binding.spinnerStyle.setSelection(position)
                        val znaki = sviatyiaNew1[caliandarDayOfYearList[0][24].toInt() - 1][3]
                        val position2 = if (znaki == "") 0
                        else znaki.toInt()
                        binding.spinnerZnak.setSelection(position2)
                    } else {
                        binding.sviaty.setText(getString(by.carkva_gazeta.malitounik.R.string.error))
                        Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Throwable) {
                    Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                if ((binding.apisanne.text.toString() == getString(by.carkva_gazeta.malitounik.R.string.error) || binding.sviaty.text.toString() == getString(by.carkva_gazeta.malitounik.R.string.error)) && count < 3) {
                    setDate(count + 1)
                } else {
                    binding.progressBar2.visibility = View.GONE
                }
            }
        } else {
            Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.sviatyia)
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

    override fun onBack() {
        if (binding.scrollpreView.isVisible) {
            binding.scrollpreView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        } else {
            setResult(700)
            super.onBack()
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val editItem = menu.findItem(R.id.action_preview)
        if (binding.scrollpreView.isGone) {
            editItem.icon = ContextCompat.getDrawable(this, R.drawable.natatka_edit)
        } else {
            editItem.icon = ContextCompat.getDrawable(this, R.drawable.natatka)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_sviaty_pyx) {
            startActivity(Intent(this, SviatyiaPyxomyia::class.java))
            return true
        }
        if (id == R.id.action_bible) {
            val dialogSvityiaBible = DialogSvityiaBible()
            dialogSvityiaBible.show(supportFragmentManager, "dialogSvityiaBible")
            return true
        }
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            i.putExtra("day", caliandarDayOfYearList[0][1].toInt())
            i.putExtra("year", caliandarDayOfYearList[0][3].toInt())
            i.putExtra("mun", caliandarDayOfYearList[0][2].toInt())
            i.putExtra("getData", true)
            caliandarMunLauncher.launch(i)
            return true
        }
        if (id == R.id.action_upload_image) {
            val i = Intent(this, SviatyiaImage::class.java)
            i.putExtra("day", caliandarDayOfYearList[0][1].toInt())
            i.putExtra("mun", caliandarDayOfYearList[0][2].toInt() + 1)
            startActivity(i)
            return true
        }
        if (id == R.id.action_save) {
            sendPostRequest(binding.sviaty.text.toString(), binding.chytanne.text.toString(), binding.spinnerStyle.selectedItemPosition, binding.spinnerZnak.selectedItemPosition.toString(), binding.apisanne.text.toString())
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.isVisible) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            } else {
                var textApisanne = binding.apisanne.text.toString()
                if (textApisanne.contains("<!--image-->")) {
                    textApisanne = textApisanne.replace("<!--image-->", "<br><br>")
                }
                binding.preView.text = HtmlCompat.fromHtml(textApisanne, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()
                binding.scrollpreView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                invalidateOptionsMenu()
            }
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviatyia, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.imageViewLeft) {
            val cal = Calendar.getInstance()
            var dayOfYear = caliandarDayOfYearList[0][24].toInt()
            if (dayOfYear == 1) dayOfYear = 366
            else dayOfYear--
            caliandarDayOfYearList = MenuCaliandar.getDataCalaindar(dayOfYear = dayOfYear, year = cal[Calendar.YEAR])
            setDate()
        }
        if (id == R.id.imageViewRight) {
            val cal = Calendar.getInstance()
            var dayOfYear = caliandarDayOfYearList[0][24].toInt()
            if (dayOfYear == 366) dayOfYear = 1
            else dayOfYear++
            caliandarDayOfYearList = MenuCaliandar.getDataCalaindar(dayOfYear = dayOfYear, year = cal[Calendar.YEAR])
            setDate()
        }
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 29)
        }
        if (id == R.id.action_br) {
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<br>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 4)
        }
    }

    private suspend fun saveLogFile(count: Int = 0) {
        val logFile = File("$filesDir/cache/log.txt")
        var error = false
        logFile.writer().use {
            it.write(getString(by.carkva_gazeta.malitounik.R.string.check_update_resourse))
        }
        MainActivity.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
            Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
            error = true
        }.await()
        if (error && count < 3) {
            saveLogFile(count + 1)
        }
    }

    private fun sendPostRequest(name: String, chtenie: String, bold: Int, tipicon: String, spaw: String) {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                var checkSviatyai = false
                val data = caliandarDayOfYearList[0][1].toInt()
                val mun = caliandarDayOfYearList[0][2].toInt()
                if (!(name == getString(by.carkva_gazeta.malitounik.R.string.error) || name == "")) {
                    var style = 8
                    when (bold) {
                        0 -> style = 6
                        1 -> style = 7
                        2 -> style = 8
                    }
                    binding.progressBar2.visibility = View.VISIBLE
                    val localFile2 = File("$filesDir/cache/cache2.txt")
                    val sviatyiaNewList = ArrayList<ArrayList<String>>()
                    referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val sviatyiaNew = localFile2.readLines()
                            for (element in sviatyiaNew) {
                                val re1 = element.split("<>")
                                val list = ArrayList<String>()
                                for (element2 in re1) {
                                    list.add(element2)
                                }
                                sviatyiaNewList.add(list)
                            }
                            if (sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][0] != name || sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][1] != chtenie || sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][2] != style.toString() || sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][3] != tipicon) {
                                sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][0] = name
                                sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][1] = chtenie
                                sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][2] = style.toString()
                                sviatyiaNewList[caliandarDayOfYearList[0][24].toInt() - 1][3] = tipicon
                                checkSviatyai = true
                            }
                        } else {
                            Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                    var sw3 = ""
                    val sb = StringBuilder()
                    for (i in 0 until 366) {
                        if (sviatyiaNewList[i][3] != "0") sw3 = sviatyiaNewList[i][3]
                        sb.append(sviatyiaNewList[i][0] + "<>" + sviatyiaNewList[i][1] + "<>" + sviatyiaNewList[i][2] + "<>" + sw3 + "\n")
                    }
                    val localFile3 = File("$filesDir/cache/cache3.txt")
                    if (sviatyiaNewList.isNotEmpty()) {
                        localFile3.writer().use {
                            it.write(sb.toString())
                        }
                    }
                    sb.clear()
                    if (sviatyiaNewList.isNotEmpty()) {
                        referens.child("/calendarsviatyia.txt").putFile(Uri.fromFile(localFile3)).await()
                    }
                } else {
                    Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                }
                if (!(spaw == getString(by.carkva_gazeta.malitounik.R.string.error) || spaw == "")) {
                    val localFile = File("$filesDir/cache/cache.txt")
                    val localFile4 = File("$filesDir/cache/cache4.txt")
                    var builder = ""
                    referens.child("/chytanne/sviatyja/opisanie" + (mun + 1) + ".json").getFile(localFile).addOnCompleteListener {
                        if (it.isSuccessful) {
                            builder = localFile.readText()
                        } else {
                            Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                    val gson = Gson()
                    if (builder != "") {
                        val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
                        val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                        arrayList[data - 1] = spaw.replace(" ", " ")
                        localFile4.writer().use {
                            it.write(gson.toJson(arrayList, type))
                        }
                    }
                    if (builder != "") {
                        referens.child("/chytanne/sviatyja/opisanie" + (mun + 1) + ".json").putFile(Uri.fromFile(localFile4)).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.save), Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                            }
                        }.await()
                    }
                } else {
                    Toast.makeText(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                }
                if (checkSviatyai) saveLogFile()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private class SpinnerAdapter(private val activity: Activity, private val data: Array<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = data[position]
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
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private class SpinnerAdapterTipicon(private val activity: Activity, private val data: ArrayList<Tipicon>) : BaseAdapter() {

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolderImage: ViewHolderImage
            if (convertView == null) {
                val binding = SimpleListItemTipiconBinding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolderImage = ViewHolderImage(binding.image, binding.text1)
                rootView.tag = viewHolderImage
            } else {
                rootView = convertView
                viewHolderImage = rootView.tag as ViewHolderImage
            }
            if (data[position].imageResource == 0) {
                viewHolderImage.image.visibility = View.GONE
            } else {
                viewHolderImage.image.setImageResource(data[position].imageResource)
            }
            viewHolderImage.text.text = data[position].title
            viewHolderImage.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolderImage(var image: ImageView, var text: TextView)

    private data class Tipicon(val imageResource: Int, val title: String)
}