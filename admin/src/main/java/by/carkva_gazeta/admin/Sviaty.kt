package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminSviatyBinding
import by.carkva_gazeta.admin.databinding.SimpleListItem1Binding
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class Sviaty : BaseActivity(), View.OnClickListener, DialogEditImage.DialogEditImageListener {
    private lateinit var binding: AdminSviatyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val sviaty = ArrayList<SviatyData>()
    private val arrayList = ArrayList<ArrayList<String>>()
    private var edittext: AppCompatEditText? = null

    override fun imageFileEdit(bitmap: Bitmap?, opisanie: String) {
        fileUpload(bitmap, opisanie)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminSviatyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sviaty.add(SviatyData(-1, 0, "Уваход у Ерусалім"))
        sviaty.add(SviatyData(-1, 1, "Уваскрасеньне"))
        sviaty.add(SviatyData(-1, 2, "Узьнясеньне"))
        sviaty.add(SviatyData(-1, 3, "Тройца"))
        sviaty.add(SviatyData(1, 1, "1 Студзеня"))
        sviaty.add(SviatyData(6, 1, "6 Студзеня"))
        sviaty.add(SviatyData(2, 2, "2 Лютага"))
        sviaty.add(SviatyData(25, 3, "25 Сакавіка"))
        sviaty.add(SviatyData(24, 6, "24 Чэрвеня"))
        sviaty.add(SviatyData(29, 6, "29 Чэрвеня"))
        sviaty.add(SviatyData(6, 8, "6 Жніўня"))
        sviaty.add(SviatyData(15, 8, "15 Жніўня"))
        sviaty.add(SviatyData(29, 8, "29 Жніўня"))
        sviaty.add(SviatyData(8, 9, "8 Верасьня"))
        sviaty.add(SviatyData(14, 9, "14 Верасьня"))
        sviaty.add(SviatyData(1, 10, "1 Кастрычніка"))
        sviaty.add(SviatyData(21, 11, "21 Лістапада"))
        sviaty.add(SviatyData(25, 12, "25 Сьнежня"))
        sviaty.add(SviatyData(-1, 4, "Айцоў першых 6-ці Ўсяленскіх сабораў"))
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.sviaty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) edittext = v as? AppCompatEditText
        }
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            getFileSviat()
        }
        setTollbarTheme()
    }

    private suspend fun getFileSviat(count: Int = 0) {
        var error = false
        binding.progressBar2.visibility = View.VISIBLE
        try {
            val localFile = File("$filesDir/cache/cache.txt")
            MainActivity.referens.child("/opisanie_sviat.json").getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) {
                    val builder = localFile.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    arrayList.addAll(gson.fromJson(builder, type))
                } else {
                    error = true
                }
            }.await()
        } catch (_: Throwable) {
            error = true
        }
        if (error && count < 3) {
            getFileSviat(count + 1)
            return
        }
        if (error) {
            Toast.makeText(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
            binding.progressBar2.visibility = View.GONE
            return
        }
        for (i in 0 until arrayList.size) {
            for (e in 0 until sviaty.size) {
                if (arrayList[i][0].toInt() == sviaty[e].data && arrayList[i][1].toInt() == sviaty[e].mun) {
                    sviaty[e].opisanie = arrayList[i][2]
                    sviaty[e].utran = arrayList[i][3]
                    sviaty[e].liturgia = arrayList[i][4]
                    sviaty[e].viachernia = arrayList[i][5]
                    break
                }
            }
        }
        binding.spinnerSviaty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.sviaty.setText(sviaty[position].opisanie)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.spinnerSviaty.adapter = SpinnerAdapter(this@Sviaty, sviaty)
        if (intent.extras != null) {
            for (i in 0 until sviaty.size) {
                if (intent.extras?.getInt("day") == sviaty[i].data && intent.extras?.getInt("mun") == sviaty[i].mun) {
                    binding.spinnerSviaty.setSelection(i)
                    break
                }
            }
        } else {
            binding.spinnerSviaty.setSelection(0)
        }
        binding.progressBar2.visibility = View.GONE
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.sviaty)
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

    private fun fileUpload(bitmap: Bitmap?, text: String) {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = File("$filesDir/cache/cache.txt")
                val fileName = "v_" + sviaty[binding.spinnerSviaty.selectedItemPosition].data.toString() + "_" + sviaty[binding.spinnerSviaty.selectedItemPosition].mun.toString() + "_1.jpg"
                bitmap?.let {
                    withContext(Dispatchers.IO) {
                        val out = FileOutputStream(localFile)
                        it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                    }
                    MainActivity.referens.child("/chytanne/icons/$fileName").putFile(Uri.fromFile(localFile)).await()
                }
                val t1 = fileName.lastIndexOf(".")
                val fileNameT = fileName.substring(0, t1) + ".txt"
                if (text != "") {
                    localFile.writer().use {
                        it.write(text)
                    }
                    MainActivity.referens.child("/chytanne/iconsApisanne/$fileNameT").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                        val file = File("$filesDir/iconsApisanne/$fileNameT")
                        localFile.copyTo(file, true)
                    }.await()
                } else {
                    try {
                        MainActivity.referens.child("/chytanne/iconsApisanne/$fileNameT").delete().await()
                    } catch (_: Throwable) {
                    }
                }
                loadFilesMetaData()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadFilesMetaData() {
        val sb = StringBuilder()
        val list = MainActivity.referens.child("/chytanne/icons").list(1000).await()
        list.items.forEach {
            val meta = it.metadata.await()
            sb.append(it.name).append("<-->").append(meta.sizeBytes).append("<-->").append(meta.updatedTimeMillis).append("\n")
        }
        val fileIcon = File("$filesDir/iconsMataData.txt")
        fileIcon.writer().use {
            it.write(sb.toString())
        }
        MainActivity.referens.child("/chytanne/iconsMataData.txt").putFile(Uri.fromFile(fileIcon)).await()
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        edittext?.let {
            if (id == R.id.action_bold) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<strong>")
                    append(text.substring(startSelect, endSelect))
                    append("</strong>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 17)
            }
            if (id == R.id.action_em) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<em>")
                    append(text.substring(startSelect, endSelect))
                    append("</em>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 9)
            }
            if (id == R.id.action_red) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<font color=\"#d00505\">")
                    append(text.substring(startSelect, endSelect))
                    append("</font>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 29)
            }
            if (id == R.id.action_p) {
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, endSelect))
                    append("<p>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 3)
            }
        }
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
        val id = item.itemId
        if (id == R.id.action_upload_image) {
            val dialog = DialogEditImage.getInstance("$filesDir/icons/v_" + sviaty[binding.spinnerSviaty.selectedItemPosition].data.toString() + "_" + sviaty[binding.spinnerSviaty.selectedItemPosition].mun.toString() + "_1.jpg")
            dialog.show(supportFragmentManager, "DialogEditImage")
            return true
        }
        if (id == R.id.action_save) {
            sendPostRequest(binding.spinnerSviaty.selectedItemPosition, binding.sviaty.text.toString())
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.isVisible) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            } else {
                binding.preView.text = HtmlCompat.fromHtml(binding.sviaty.text.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY).trim()
                binding.scrollpreView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.sviaty.windowToken, 0)
                invalidateOptionsMenu()
            }
            return true
        }
        return false
    }

    private fun sendPostRequest(position: Int, apisanne: String) {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    var day = -1
                    var mun = 0
                    when (position) {
                        0 -> {
                            day = -1
                            mun = 0
                        }

                        1 -> {
                            day = -1
                            mun = 1
                        }

                        2 -> {
                            day = -1
                            mun = 2
                        }

                        3 -> {
                            day = -1
                            mun = 3
                        }

                        4 -> {
                            day = 1
                            mun = 1
                        }

                        5 -> {
                            day = 6
                            mun = 1
                        }

                        6 -> {
                            day = 2
                            mun = 2
                        }

                        7 -> {
                            day = 25
                            mun = 3
                        }

                        8 -> {
                            day = 24
                            mun = 6
                        }

                        9 -> {
                            day = 29
                            mun = 6
                        }

                        10 -> {
                            day = 6
                            mun = 8
                        }

                        11 -> {
                            day = 15
                            mun = 8
                        }

                        12 -> {
                            day = 29
                            mun = 8
                        }

                        13 -> {
                            day = 8
                            mun = 9
                        }

                        14 -> {
                            day = 14
                            mun = 9
                        }

                        15 -> {
                            day = 1
                            mun = 10
                        }

                        16 -> {
                            day = 21
                            mun = 11
                        }

                        17 -> {
                            day = 25
                            mun = 12
                        }

                        18 -> {
                            day = -1
                            mun = 4
                        }
                    }
                    var utran = ""
                    var linur = ""
                    var viach = ""
                    var index = -5
                    for (i in 0 until arrayList.size) {
                        if (day == arrayList[i][0].toInt() && mun == arrayList[i][1].toInt()) {
                            utran = arrayList[i][3]
                            linur = arrayList[i][4]
                            viach = arrayList[i][5]
                            index = i
                            break
                        }
                    }
                    if (index == -5) {
                        val temp = ArrayList<String>()
                        temp.add(day.toString())
                        temp.add(mun.toString())
                        temp.add(apisanne)
                        temp.add(utran)
                        temp.add(linur)
                        temp.add(viach)
                        arrayList.add(temp)
                    } else {
                        arrayList[index][2] = apisanne
                        arrayList[index][3] = utran
                        arrayList[index][4] = linur
                        arrayList[index][5] = viach
                    }
                    if (arrayList.isNotEmpty()) {
                        val localFile = File("$filesDir/cache/cache.txt")
                        val gson = Gson()
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                        localFile.writer().use {
                            it.write(gson.toJson(arrayList, type))
                        }
                        MainActivity.referens.child("/opisanie_sviat.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.save), Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                            }
                        }.await()
                    } else {
                        Toast.makeText(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Throwable) {
                    Toast.makeText(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviaty, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    private class SpinnerAdapter(private val activity: Activity, private val data: ArrayList<SviatyData>) : ArrayAdapter<SviatyData>(activity, R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = data[position].title
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
            viewHolder.text.text = data[position].title
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class SviatyData(val data: Int, val mun: Int, val title: String, var opisanie: String = "", var utran: String = "", var liturgia: String = "", var viachernia: String = "")
}