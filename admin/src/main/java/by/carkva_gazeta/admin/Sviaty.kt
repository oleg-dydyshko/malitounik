package by.carkva_gazeta.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
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
import java.util.Calendar

class Sviaty : BaseActivity(), View.OnClickListener, DialogEditImage.DialogEditImageListener {
    private lateinit var binding: AdminSviatyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val newArrayList = ArrayList<ArrayList<String>>()
    private var edittext: AppCompatEditText? = null
    private var caliandarArrayList = ArrayList<String>()
    private lateinit var adapter: SpinnerSviaty

    @SuppressLint("SetTextI18n")
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && newArrayList.isNotEmpty()) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                caliandarArrayList.clear()
                caliandarArrayList.addAll(MenuCaliandar.getPositionCaliandar(position))
                binding.calandar.text = caliandarArrayList[1] + " " + resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[caliandarArrayList[2].toInt()] + " " + caliandarArrayList[3]
                var check = false
                for (i in newArrayList.indices) {
                    if ((newArrayList[i][0] == caliandarArrayList[22] && newArrayList[i][2].toInt() == Settings.PASHA) || (newArrayList[i][0] == caliandarArrayList[1] && newArrayList[i][1].toInt() == caliandarArrayList[2].toInt() + 1 && newArrayList[i][2].toInt()  == Settings.CALAINDAR)) {
                        binding.spinnerSviaty.setSelection(i)
                        binding.sviaty.setText(newArrayList[i][3])
                        binding.spinnerIsPasxa.setSelection(newArrayList[i][2].toInt())
                        binding.sviaty.visibility = View.VISIBLE
                        binding.spinnerIsPasxa.visibility = View.VISIBLE
                        binding.spinnerSviaty.visibility = View.VISIBLE
                        check = true
                        break
                    }
                }
                if (!check) {
                    binding.sviaty.setText("<font color=\"#d00505\"><strong>${caliandarArrayList[6]}</strong></font><br><br>")
                    binding.sviaty.visibility = View.VISIBLE
                    binding.spinnerIsPasxa.visibility = View.VISIBLE
                }
            }
        }
    }

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
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionBr.setOnClickListener(this)
        binding.sviaty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) edittext = v as? AppCompatEditText
        }
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            getFileSviat()
        }
        binding.calandar.setOnClickListener {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("getData", true)
            caliandarMunLauncher.launch(i)
        }
        binding.spinnerSviaty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (newArrayList.isEmpty()) return
                binding.sviaty.setText(newArrayList[position][3])
                binding.spinnerIsPasxa.setSelection(newArrayList[position][2].toInt())
                caliandarArrayList.clear()
                val dat = MenuCaliandar.getDataCalaindar(year = Calendar.getInstance()[Calendar.YEAR])
                for (i in dat.indices) {
                    if (newArrayList[position][2].toInt() == Settings.PASHA) {
                        if (dat[i][22] == newArrayList[position][0]) {
                            caliandarArrayList.addAll(MenuCaliandar.getPositionCaliandar(dat[i][25].toInt()))
                            break
                        }
                    } else {
                        if (dat[i][1] == newArrayList[position][0] && dat[i][2].toInt() + 1 == newArrayList[position][1].toInt()) {
                            caliandarArrayList.addAll(MenuCaliandar.getPositionCaliandar(dat[i][25].toInt()))
                            break
                        }
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val fileIcon = File("$filesDir/icons/v_" + newArrayList[binding.spinnerSviaty.selectedItemPosition][0] + "_" + newArrayList[binding.spinnerSviaty.selectedItemPosition][1] + "_1.jpg")
                        if (!fileIcon.exists()) {
                            val pathReference = MainActivity.referens.child("/chytanne/icons/" + fileIcon.name)
                            pathReference.getFile(fileIcon).await()
                        }
                    } catch (_: Throwable) {
                    }
                }
                binding.calandar.text = caliandarArrayList[1] + " " + resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[caliandarArrayList[2].toInt()] + " " + caliandarArrayList[3]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        adapter = SpinnerSviaty(this@Sviaty, newArrayList)
        binding.spinnerSviaty.adapter = adapter
        binding.spinnerIsPasxa.adapter = SpinnerAdapterPasha(this@Sviaty, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.admin_svity_data))
        setTollbarTheme()
    }

    @SuppressLint("SetTextI18n")
    private suspend fun getFileSviat(count: Int = 0) {
        var error = false
        binding.progressBar2.visibility = View.VISIBLE
        try {
            val localFile = File("$filesDir/cache/cache.txt")
            MainActivity.referens.child("/sviaty.json").getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        val builder = localFile.readText()
                        val gson = Gson()
                        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                        newArrayList.addAll(gson.fromJson(builder, type))
                        adapter.notifyDataSetChanged()
                        val extras = intent.extras
                        if (extras != null) {
                            for (i in newArrayList.indices) {
                                if (newArrayList[i][0].toInt() == extras.getInt("day") && newArrayList[i][1].toInt() == extras.getInt("mun")) {
                                    binding.spinnerSviaty.setSelection(i)
                                    binding.sviaty.setText(newArrayList[i][3])
                                    binding.spinnerIsPasxa.setSelection(newArrayList[i][2].toInt())
                                    extras.clear()
                                    break
                                }
                            }
                        } else {
                            binding.spinnerSviaty.setSelection(0)
                            binding.sviaty.setText(newArrayList[0][3])
                            binding.spinnerIsPasxa.setSelection(newArrayList[0][2].toInt())
                        }
                        caliandarArrayList.clear()
                        val dat = MenuCaliandar.getDataCalaindar(year = Calendar.getInstance()[Calendar.YEAR])
                        for (i in dat.indices) {
                            if (newArrayList[0][2].toInt() == Settings.PASHA) {
                                if (dat[i][22] == newArrayList[0][0]) {
                                    caliandarArrayList.addAll(MenuCaliandar.getPositionCaliandar(dat[i][25].toInt()))
                                    break
                                }
                            } else {
                                if (dat[i][1] == newArrayList[0][0] && dat[i][2].toInt() + 1 == newArrayList[0][1].toInt()) {
                                    caliandarArrayList.addAll(MenuCaliandar.getPositionCaliandar(dat[i][25].toInt()))
                                    break
                                }
                            }
                        }
                        binding.calandar.text = caliandarArrayList[1] + " " + resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[caliandarArrayList[2].toInt()] + " " + caliandarArrayList[3]
                    } catch (_: Throwable) {
                        Toast.makeText(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                        binding.progressBar2.visibility = View.GONE
                    }
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
                val fileName = "v_" + newArrayList[binding.spinnerSviaty.selectedItemPosition][0] + "_" + newArrayList[binding.spinnerSviaty.selectedItemPosition][1] + "_1.jpg"
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
            if (id == R.id.action_br) {
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, endSelect))
                    append("<br>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 4)
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
        menu.findItem(R.id.action_add).isVisible = binding.spinnerSviaty.isVisible
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            binding.spinnerSviaty.visibility = View.GONE
            binding.calandar.text = ""
            binding.sviaty.setText("")
            binding.sviaty.visibility = View.GONE
            binding.spinnerIsPasxa.setSelection(Settings.CALAINDAR)
            binding.spinnerIsPasxa.visibility = View.GONE
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_upload_image) {
            val dialog = DialogEditImage.getInstance("$filesDir/icons/v_" + newArrayList[binding.spinnerSviaty.selectedItemPosition][0] + "_" + newArrayList[binding.spinnerSviaty.selectedItemPosition][1] + "_1.jpg")
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
                var pos = position
                try {
                    val data = if (binding.spinnerIsPasxa.selectedItemPosition == Settings.PASHA) {
                        caliandarArrayList[22]
                    } else {
                        caliandarArrayList[1]
                    }
                    val mun = if (binding.spinnerIsPasxa.selectedItemPosition == Settings.PASHA) {
                        1
                    } else {
                        caliandarArrayList[2].toInt() + 1
                    }
                    if (binding.spinnerSviaty.isVisible) {
                        newArrayList[position][0] = data
                        newArrayList[position][1] = mun.toString()
                        newArrayList[position][2] = binding.spinnerIsPasxa.selectedItemPosition.toString()
                        newArrayList[position][3] = apisanne
                    } else {
                        val arrayList = ArrayList<String>()
                        arrayList.add(data)
                        arrayList.add(mun.toString())
                        arrayList.add(binding.spinnerIsPasxa.selectedItemPosition.toString())
                        arrayList.add(apisanne)
                        newArrayList.add(arrayList)
                        pos = newArrayList.size - 1
                    }
                    if (newArrayList.isNotEmpty()) {
                        val localFile = File("$filesDir/cache/cache.txt")
                        val gson = Gson()
                        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                        localFile.writer().use {
                            it.write(gson.toJson(newArrayList, type))
                        }
                        MainActivity.referens.child("/sviaty.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
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
                adapter.notifyDataSetChanged()
                binding.spinnerSviaty.visibility = View.VISIBLE
                binding.spinnerSviaty.setSelection(pos)
                invalidateOptionsMenu()
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

    private class SpinnerAdapterPasha(private val activity: Activity, private val data: Array<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = data[position]
            textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount() = data.size

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

    private class SpinnerSviaty(private val activity: Activity, private val data: ArrayList<ArrayList<String>>) : ArrayAdapter<ArrayList<String>>(activity, R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            val datam = data[position][3]
            val t1 = datam.indexOf("</strong>")
            val title = if (t1 != -1) HtmlCompat.fromHtml(datam.substring(0, t1), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            else HtmlCompat.fromHtml(datam.substring(0, 30), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            textView.text = title
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
            val datam = data[position][3]
            val t1 = datam.indexOf("</strong>")
            val title = if (t1 != -1) HtmlCompat.fromHtml(datam.substring(0, t1), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            else HtmlCompat.fromHtml(datam.substring(0, 30), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            viewHolder.text.text = title
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)
}