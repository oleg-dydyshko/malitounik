package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminBibliatekaListBinding
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBibliotekaBinding
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

class BibliatekaList : BaseActivity(), DialogBiblijatekaContextMenu.DialogPiarlinyContextMenuListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminBibliatekaListBinding
    private var sqlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var width = 0
    private val arrayList = ArrayList<ArrayList<String>>()
    private lateinit var adapter: BibliotekaAdapter
    private lateinit var rubrikaAdapter: RubrikaAdapter
    private var position = -1
    private val mActivityResultImageFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val imageUri = it.data?.data
            imageUri?.let { image ->
                val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(contentResolver, image)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                binding.imagePdf.setImageBitmap(bitmap)
            }
        }
    }
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val fileUri = it.data?.data
            fileUri?.let { file ->
                binding.pdfTextView.setText(file.toString())
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
        sqlJob?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("editVisibility", binding.edit.isVisible)
        outState.putString("pdfTextView", binding.pdfTextView.text.toString())
        outState.putParcelable("BitmapImage", binding.imagePdf.drawable?.toBitmap())
        outState.putInt("position", position)
    }

    override fun onBack() {
        if (binding.edit.isVisible) {
            binding.edit.visibility = View.GONE
            binding.listView.visibility = View.VISIBLE
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            invalidateOptionsMenu()
        } else {
            val intent = Intent()
            intent.putExtra("rubrika", intent.extras?.getInt("rubrika", 2) ?: 2)
            setResult(RESULT_OK, intent)
            super.onBack()
        }
    }

    private fun setImageSize(imageView: View) {
        imageView.setBackgroundResource(R.drawable.frame_image_biblioteka)
        imageView.layoutParams?.width = width / 2
        imageView.layoutParams?.height = (width / 2 * 1.4F).toInt()
        imageView.requestLayout()
    }

    override fun onDialogEditClick(position: Int) {
        binding.edit.visibility = View.VISIBLE
        binding.listView.visibility = View.GONE
        invalidateOptionsMenu()
        this.position = position
        if (position != -1) {
            binding.textViewTitle.setText(arrayList[position][0])
            binding.rubrika.setSelection(arrayList[position][4].toInt() - 1)
            if (arrayList[position].size == 3) {
                if (arrayList[position][2] != "") {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile(arrayList[position][2], options)
                        }
                        binding.imagePdf.setImageBitmap(bitmap)
                    }
                }
            } else {
                val t1 = arrayList[position][5].lastIndexOf("/")
                val file = File("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1))
                if (file.exists()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1), options)
                        }
                        binding.imagePdf.setImageBitmap(bitmap)
                    }
                }
            }
            binding.pdfTextView.setText(arrayList[position][2])
            var edit = arrayList[position][1]
            edit = edit.replace("<div style=\"font-family: Arial,sans-serif; font-size: 15px; text-align: justify;\">", "")
            edit = edit.replace("</div>", "")
            binding.opisanie.setText(edit)
        } else {
            binding.imagePdf.setImageDrawable(null)
            binding.textViewTitle.text?.clear()
            binding.rubrika.setSelection(1)
            binding.pdfTextView.text = null
            binding.opisanie.text?.clear()
        }
        setImageSize(binding.imagePdf)
        binding.imagePdf.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            mActivityResultImageFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
        }
        binding.admin.setOnClickListener {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
            mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
        }
    }

    private suspend fun getPdfFileLength(url: Uri, count: Int = 0): String {
        val t1 = url.path?.lastIndexOf("/") ?: 0
        val fileName = url.path?.substring(t1 + 1) ?: ""
        var error = false
        val pathReference = MainActivity.referens.child("/data/bibliateka/$fileName")
        val metaData = pathReference.metadata.addOnFailureListener {
            error = true
        }.await()
        val size = metaData.sizeBytes
        if (error && count < 3) {
            getPdfFileLength(url, count + 1)
        }
        return size.toString()
    }

    private fun saveBibliateka() {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    val rubrika = (binding.rubrika.selectedItemPosition + 1).toString()
                    val link = binding.textViewTitle.text.toString()
                    val str = binding.opisanie.text.toString()
                    val pdf = binding.pdfTextView.text.toString().toUri()
                    val t2 = pdf.path?.lastIndexOf("/") ?: 0
                    val pdfName = pdf.path?.substring(t2 + 1) ?: ""
                    val t1 = pdfName.lastIndexOf(".")
                    if (t1 == -1 || link == "" || binding.imagePdf.drawable == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                            binding.progressBar2.visibility = View.GONE
                        }
                        return@withContext
                    }
                    val imageLocal = "$filesDir/image_temp/" + pdfName.substring(0, t1) + ".png"
                    if (position == -1) {
                        val mySqlList = ArrayList<String>()
                        mySqlList.add(link)
                        mySqlList.add(str)
                        mySqlList.add(pdfName)
                        mySqlList.add(getPdfFileLength(binding.pdfTextView.text.toString().toUri()))
                        mySqlList.add(rubrika)
                        mySqlList.add(imageLocal)
                        arrayList.add(0, mySqlList)
                    } else {
                        arrayList[position][0] = link
                        arrayList[position][1] = str
                        arrayList[position][2] = pdfName
                        if (binding.pdfTextView.text.toString() != "") arrayList[position][3] = getPdfFileLength(binding.pdfTextView.text.toString().toUri())
                        arrayList[position][4] = rubrika
                        arrayList[position][5] = imageLocal
                    }
                    val dir = File("$filesDir/image_temp")
                    if (!dir.exists()) dir.mkdir()
                    val file = File(imageLocal)
                    val bitmap = binding.imagePdf.drawable?.toBitmap()
                    bitmap?.let {
                        val out = FileOutputStream(file)
                        it.compress(Bitmap.CompressFormat.PNG, 90, out)
                        out.flush()
                        out.close()
                    }
                    saveBibliatekaJson()
                    val filePdf = File(binding.pdfTextView.text.toString())
                    if (filePdf.exists()) {
                        if (binding.pdfTextView.text.toString() != "") MainActivity.referens.child("/data/bibliateka/$pdf").putFile(Uri.fromFile(filePdf)).await()
                    }
                    if (file.exists()) {
                        MainActivity.referens.child("/images/bibliateka/" + file.name).putFile(Uri.fromFile(file)).await()
                    }
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.save), Toast.LENGTH_SHORT).show()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            Toast.makeText(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveBibliatekaJson() {
        val localFile = File("$filesDir/cache/cache.txt")
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        localFile.writer().use {
            it.write(gson.toJson(arrayList, type))
        }
        MainActivity.referens.child("/bibliateka.json").putFile(Uri.fromFile(localFile)).await()
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialogDelite = DialogDelite.getInstance(position, name, false)
        dialogDelite.show(supportFragmentManager, "DialogDelite")
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.referens.child("/data/bibliateka/" + arrayList[position][2]).delete().await()
            val file = File(arrayList[position][5])
            if (file.exists()) {
                file.delete()
            }
            MainActivity.referens.child("/images/bibliateka/" + file.name).delete().await()
            arrayList.removeAt(position)
            adapter.notifyDataSetChanged()
            saveBibliatekaJson()
        }
    }

    private fun getSql() {
        if (Settings.isNetworkAvailable(this)) {
            try {
                sqlJob = CoroutineScope(Dispatchers.Main).launch {
                    val sb = getBibliatekaJson()
                    if (sb != "") {
                        binding.progressBar2.visibility = View.VISIBLE
                        val gson = Gson()
                        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(String::class.java).type).type).type
                        val biblioteka = gson.fromJson<ArrayList<ArrayList<String>>>(sb, type)
                        for (i in 0 until biblioteka.size) {
                            val mySqlList = ArrayList<String>()
                            val kniga = biblioteka[i]
                            val rubrika = kniga[4]
                            val link = kniga[0]
                            val str = kniga[1]
                            val pdf = kniga[2]
                            val pdfFileSize = kniga[3]
                            var image = kniga[5]
                            if (image.contains("/")) {
                                val t1 = image.lastIndexOf("/")
                                image = image.substring(t1 + 1)
                            }
                            mySqlList.add(link)
                            mySqlList.add(str)
                            mySqlList.add(pdf)
                            mySqlList.add(pdfFileSize)
                            mySqlList.add(rubrika)
                            val t1 = pdf.lastIndexOf(".")
                            mySqlList.add(pdf.substring(0, t1) + ".png")
                            val dir = File("$filesDir/image_temp")
                            if (!dir.exists()) dir.mkdir()
                            val file = File(image)
                            if (!file.exists()) {
                                saveImagePdf(pdf, image)
                            }
                            arrayList.add(mySqlList)
                        }
                        val json = gson.toJson(arrayList, type)
                        val k = getSharedPreferences("biblia", MODE_PRIVATE)
                        k.edit {
                            putString("Biblioteka", json)
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                    }
                    binding.progressBar2.visibility = View.GONE
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveImagePdf(pdf: String, image: String) {
        val t1 = pdf.lastIndexOf(".")
        val imageTempFile = File("$filesDir/image_temp/" + pdf.substring(0, t1) + ".png")
        MainActivity.referens.child("/images/bibliateka/$image").getFile(imageTempFile).addOnFailureListener {
            Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
        }.await()
    }

    private suspend fun getBibliatekaJson(): String {
        var text = ""
        val pathReference = MainActivity.referens.child("/bibliateka.json")
        val localFile = File("$filesDir/cache/cache.txt")
        pathReference.getFile(localFile).addOnCompleteListener {
            if (it.isSuccessful) text = localFile.readText()
            else Toast.makeText(this, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
        }.await()
        return text
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = windowManager.currentWindowMetrics
            val bounds = display.bounds
            bounds.width()
        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }
        binding = AdminBibliatekaListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            val dialog = DialogBiblijatekaContextMenu.getInstance(position, arrayList[position][0])
            dialog.show(supportFragmentManager, "DialogPiarlinyContextMenu")
            return@setOnItemLongClickListener true
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            onDialogEditClick(position)
        }
        val array = arrayOf(getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy), getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki), getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki), getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura), getString(by.carkva_gazeta.malitounik.R.string.arx_num_gaz))
        rubrikaAdapter = RubrikaAdapter(this, array)
        binding.rubrika.adapter = rubrikaAdapter
        adapter = BibliotekaAdapter(this)
        binding.listView.adapter = adapter
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("editVisibility")) {
                binding.edit.visibility = View.VISIBLE
                binding.listView.visibility = View.GONE
            }
            position = savedInstanceState.getInt("position")
            binding.pdfTextView.setText(savedInstanceState.getString("pdfTextView"))
            binding.imagePdf.setImageBitmap(savedInstanceState.getParcelable("BitmapImage"))
            setImageSize(binding.imagePdf)
        }
        getSql()
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblijateka)
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
        if (binding.edit.isVisible) {
            menu.findItem(R.id.action_plus).isVisible = false
            menu.findItem(R.id.action_save).isVisible = true
        } else {
            menu.findItem(R.id.action_plus).isVisible = true
            menu.findItem(R.id.action_save).isVisible = false
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            saveBibliateka()
            return true
        }
        if (id == R.id.action_plus) {
            onDialogEditClick(-1)
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_bibliateka_list, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    internal inner class BibliotekaAdapter(context: Activity) : ArrayAdapter<ArrayList<String>>(context, R.layout.admin_simple_list_item_biblioteka, arrayList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = AdminSimpleListItemBibliotekaBinding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label, binding.imageView2, binding.rubrika)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            setImageSize(viewHolder.imageView)
            if (arrayList[position].size == 3) {
                if (arrayList[position][2] != "") {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile(arrayList[position][2], options)
                        }
                        viewHolder.imageView.setImageBitmap(bitmap)
                        viewHolder.imageView.visibility = View.VISIBLE
                    }
                } else {
                    viewHolder.imageView.visibility = View.GONE
                }
            } else {
                val t1 = arrayList[position][5].lastIndexOf("/")
                val file = File("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1))
                if (file.exists()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1), options)
                        }
                        viewHolder.imageView.setImageBitmap(bitmap)
                        viewHolder.imageView.visibility = View.VISIBLE
                    }
                }
            }
            viewHolder.text.text = arrayList[position][0]
            val rubrika = when (arrayList[position][4].toInt()) {
                1 -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy)
                2 -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki)
                3 -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki)
                4 -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura)
                5 -> getString(by.carkva_gazeta.malitounik.R.string.arx_num_gaz)
                else -> ""
            }
            viewHolder.rubrika.text = rubrika
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var imageView: ImageView, var rubrika: TextView)

    private class RubrikaAdapter(private val activity: Activity, private val dataRubrika: Array<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_1, dataRubrika) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = dataRubrika[position]
            textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataRubrika.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderRubrika
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolderRubrika(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderRubrika
            }
            viewHolder.text.text = dataRubrika[position]
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolderRubrika(var text: TextView)
}