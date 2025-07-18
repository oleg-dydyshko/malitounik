package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminPiarlinyBinding
import by.carkva_gazeta.admin.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Settings
import com.google.android.play.core.splitcompat.SplitCompat
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
import java.util.GregorianCalendar

class Piarliny : BaseActivity(), View.OnClickListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminPiarlinyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val piarliny = ArrayList<PiarlinyData>()
    private var edit = -1
    private var timeListCalendar = Calendar.getInstance()
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                timeListCalendar.set(VYSOCOSNYI_GOD, arrayList[2].toInt(), arrayList[1].toInt(), 0, 0, 0)
                timeListCalendar.set(Calendar.MILLISECOND, 0)
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, timeListCalendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[timeListCalendar.get(Calendar.MONTH)])
            }
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onBack() {
        if (binding.addPiarliny.isVisible) {
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
        } else {
            super.onBack()
        }
    }

    private fun onDialogEditClick(position: Int) {
        edit = position
        binding.addPiarliny.setText(piarliny[edit].data)
        binding.addPiarliny.setSelection(piarliny[edit].data.length)
        timeListCalendar.timeInMillis = piarliny[edit].time * 1000
        timeListCalendar.set(Calendar.YEAR, VYSOCOSNYI_GOD)
        timeListCalendar.set(Calendar.MILLISECOND, 0)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, timeListCalendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[timeListCalendar.get(Calendar.MONTH)])
        binding.listView.visibility = View.GONE
        binding.addPiarliny.visibility = View.VISIBLE
        binding.linearLayout2.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        piarliny.removeAt(position)
        piarliny.sort()
        val gson = Gson()
        val resultArray = ArrayList<ArrayList<String>>()
        for (i in 0 until piarliny.size) {
            val resultArray2 = ArrayList<String>()
            resultArray2.add(piarliny[i].time.toString())
            resultArray2.add(piarliny[i].data)
            resultArray.add(resultArray2)
        }
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        sendPostRequest(gson.toJson(resultArray, type))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminPiarlinyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionBr.setOnClickListener(this)

        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            try {
                val localFile = File("$filesDir/cache/cache.txt")
                MainActivity.referens.child("/chytanne/piarliny.json").getFile(localFile).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val jsonFile = localFile.readText()
                        val gson = Gson()
                        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                        val piarlin = ArrayList<ArrayList<String>>()
                        piarlin.addAll(gson.fromJson(jsonFile, type))
                        piarlin.forEach {
                            piarliny.add(PiarlinyData(it[0].toLong(), it[1]))
                        }
                        piarliny.sort()
                        binding.listView.adapter = PiarlinyListAdaprer(this@Piarliny)
                    } else {
                        Toast.makeText(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                    }
                    invalidateOptionsMenu()
                    binding.progressBar2.visibility = View.GONE
                    if (intent.extras != null) {
                        val time = intent.extras?.getLong("time") ?: Calendar.getInstance().timeInMillis
                        val cal = GregorianCalendar()
                        cal.timeInMillis = time
                        val day = cal[Calendar.DATE]
                        val mun = cal[Calendar.MONTH]
                        val cal2 = GregorianCalendar()
                        for (i in 0 until piarliny.size) {
                            val t = piarliny[i].time * 1000
                            cal2.timeInMillis = t
                            val day2 = cal2[Calendar.DATE]
                            val mun2 = cal2[Calendar.MONTH]
                            if (day == day2 && mun == mun2) {
                                onDialogEditClick(i)
                                break
                            }
                        }
                    }
                }.await()
            } catch (_: Throwable) {
                Toast.makeText(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
            }
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            var text = piarliny[position].data
            if (text.length > 30) {
                text = text.substring(0, 30)
                text = "$text..."
            }
            val dialogDelite = DialogDelite.getInstance(position, text, false)
            dialogDelite.show(supportFragmentManager, "DialogDelite")
            return@setOnItemLongClickListener true
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            onDialogEditClick(position)
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
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

    override fun onPrepareMenu(menu: Menu) {
        val plus = menu.findItem(R.id.action_plus)
        val save = menu.findItem(R.id.action_save)
        val glava = menu.findItem(R.id.action_glava)
        if (piarliny.isNotEmpty()) {
            if (binding.addPiarliny.isVisible) {
                plus.isVisible = false
                save.isVisible = true
                glava.isVisible = true
            } else {
                plus.isVisible = true
                save.isVisible = false
                glava.isVisible = false
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piarliny, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_save) {
            val text = binding.addPiarliny.text.toString().trim()
            if (text != "") {
                if (edit != -1) {
                    piarliny[edit].time = timeListCalendar.timeInMillis / 1000
                    piarliny[edit].data = text
                } else {
                    piarliny.add(PiarlinyData(timeListCalendar.timeInMillis / 1000, text))
                }
                piarliny.sort()
                val gson = Gson()
                val resultArray = ArrayList<ArrayList<String>>()
                for (i in 0 until piarliny.size) {
                    val resultArray2 = ArrayList<String>()
                    resultArray2.add(piarliny[i].time.toString())
                    resultArray2.add(piarliny[i].data)
                    resultArray.add(resultArray2)
                }
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                sendPostRequest(gson.toJson(resultArray, type))
            }
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("getData", true)
            caliandarMunLauncher.launch(i)
            return true
        }
        if (id == R.id.action_plus) {
            edit = -1
            binding.listView.visibility = View.GONE
            binding.addPiarliny.visibility = View.VISIBLE
            binding.linearLayout2.visibility = View.VISIBLE
            binding.addPiarliny.text?.clear()
            val c = Calendar.getInstance()
            timeListCalendar.set(VYSOCOSNYI_GOD, c[Calendar.MONTH], c[Calendar.DATE], 0, 0, 0)
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, timeListCalendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[timeListCalendar.get(Calendar.MONTH)])
            invalidateOptionsMenu()
            return true
        }
        return false
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_bold) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 29)
        }
        if (id == R.id.action_br) {
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<br>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 4)
        }
    }

    private fun sendPostRequest(piarliny: String) {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = File("$filesDir/cache/cache.txt")
                    localFile.writer().use {
                        it.write(piarliny)
                    }
                    MainActivity.referens.child("/chytanne/piarliny.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.save), Toast.LENGTH_SHORT).show()
                            binding.addPiarliny.text?.clear()
                            edit = -1
                        } else {
                            Toast.makeText(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                } catch (_: Throwable) {
                    Toast.makeText(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                val adapter = binding.listView.adapter as PiarlinyListAdaprer
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private inner class PiarlinyListAdaprer(context: Activity) : ArrayAdapter<PiarlinyData>(context, R.layout.simple_list_item_2, R.id.label, piarliny) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val calendar = GregorianCalendar()
            calendar.timeInMillis = piarliny[position].time * 1000
            val munName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[calendar.get(Calendar.MONTH)]
            viewHolder.text.text = HtmlCompat.fromHtml(calendar.get(Calendar.DATE).toString() + " " + munName, HtmlCompat.FROM_HTML_MODE_LEGACY)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class PiarlinyData(var time: Long, var data: String) : Comparable<PiarlinyData> {
        override fun compareTo(other: PiarlinyData): Int {
            if (this.time > other.time) {
                return 1
            } else if (this.time < other.time) {
                return -1
            }
            return 0
        }
    }

    companion object {
        private const val VYSOCOSNYI_GOD = 2020
    }
}