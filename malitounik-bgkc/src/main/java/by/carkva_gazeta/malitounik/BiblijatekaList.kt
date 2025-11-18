package by.carkva_gazeta.malitounik

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.admin.DialogEditBiblijteka
import by.carkva_gazeta.malitounik.admin.saveBibliateka
import by.carkva_gazeta.malitounik.views.AllDestinations
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.HtmlText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class FilterBiblijatekaModel : ViewModel() {
    val listAllBiblijateka = ArrayList<ArrayList<String>>()
    private val items = mutableStateListOf<ArrayList<String>>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: MutableStateFlow<SnapshotStateList<ArrayList<String>>> = _filteredItems

    fun addAllBiblijateka(list: ArrayList<ArrayList<String>>) {
        listAllBiblijateka.addAll(list)
    }

    fun findIndex(fileSize: Long): Int {
        for (i in listAllBiblijateka.indices) {
            if (listAllBiblijateka[i][3].toLong() == fileSize) {
                return i
            }
        }
        return 0
    }

    fun clear() {
        items.clear()
    }

    fun addAllItemList(item: SnapshotStateList<ArrayList<String>>) {
        items.addAll(item)
    }

    fun filterItem(search: String) {
        if (search.isNotEmpty()) {
            _filteredItems.value =
                items.filter { it[1].contains(search, ignoreCase = true) } as SnapshotStateList<ArrayList<String>>
        }
    }
}

@Composable
fun BiblijtekaList(navController: NavHostController, biblijateka: String, innerPadding: PaddingValues, searchText: Boolean, addItem: Boolean, editDismiss: () -> Unit) {
    val context = LocalContext.current
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var fileName by remember { mutableStateOf("") }
    var fileListPosition by remember { mutableIntStateOf(0) }
    val viewModel: FilterBiblijatekaModel = viewModel()
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDialogBiblijatekaVisable by remember { mutableStateOf(false) }
    var isDialogNoWIFIVisable by remember { mutableStateOf(false) }
    var isDialogNoIntent by remember { mutableStateOf(false) }
    val bibliatekaList = remember { mutableStateListOf<ArrayList<String>>() }
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    val editList = remember { ArrayList<String>() }
    var editItem by remember { mutableStateOf(false) }
    var allPosition by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    if (addItem || editItem) {
        if (addItem) {
            allPosition = -1
            editList.clear()
            editList.add("")
            editList.add("")
            editList.add(stringResource(R.string.niama_pdf))
            editList.add("0")
            editList.add("1")
            editList.add("")
        }
        DialogEditBiblijteka(editList, onSave = { title: String, rubrika: Int, apisanne: String, pdfFile: String ->
            saveBibliateka(context, viewModel.listAllBiblijateka, allPosition, title, rubrika, apisanne, pdfFile) {
                isProgressVisable = it
                if (!isProgressVisable) {
                    editDismiss()
                    editItem = false
                }
            }
        }) { fileName ->
            val cacheImage = File("${context.filesDir}/cache/cache.png")
            if (cacheImage.exists() && fileName.isNotEmpty()) {
                val t1 = fileName.lastIndexOf("/")
                val image = File("${context.filesDir}/bibliatekaImage/" + fileName.substring(t1 + 1))
                cacheImage.copyTo(image, overwrite = true)
                cacheImage.delete()
            }
            val image = File("${context.filesDir}/bibliatekaImage/cacheNew.png")
            if (image.exists()) image.delete()
            val localPdfFile = File("${context.filesDir}/cache/cache.pdf")
            if (localPdfFile.exists()) {
                localPdfFile.delete()
            }
            editDismiss()
            editItem = false
        }
    }
    LaunchedEffect(Unit) {
        if (Settings.isNetworkAvailable(context)) {
            coroutineScope.launch {
                getBibliateka(
                    context,
                    biblijateka,
                    bibliatekaList = { list ->
                        viewModel.addAllBiblijateka(list)
                        when (biblijateka) {
                            AllDestinations.BIBLIJATEKA_NIADAUNIA -> {
                                val gson = Gson()
                                val type = TypeToken.getParameterized(
                                    ArrayList::class.java,
                                    TypeToken.getParameterized(
                                        ArrayList::class.java,
                                        String::class.java
                                    ).type
                                ).type
                                val fileNadaunia = File("${context.filesDir}/biblijateka_latest.json")
                                if (fileNadaunia.exists()) {
                                    bibliatekaList.addAll(gson.fromJson(fileNadaunia.readText(), type))
                                }
                            }

                            AllDestinations.BIBLIJATEKA_GISTORYIA -> {
                                val newList = list.filter { it[4].toInt() == 1 } as ArrayList<ArrayList<String>>
                                bibliatekaList.addAll(newList)
                            }

                            AllDestinations.BIBLIJATEKA_MALITOUNIKI -> {
                                val newList = list.filter { it[4].toInt() == 2 } as ArrayList<ArrayList<String>>
                                bibliatekaList.addAll(newList)
                            }

                            AllDestinations.BIBLIJATEKA_SPEUNIKI -> {
                                val newList = list.filter { it[4].toInt() == 3 } as ArrayList<ArrayList<String>>
                                bibliatekaList.addAll(newList)
                            }

                            AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> {
                                val newList = list.filter { it[4].toInt() == 4 } as ArrayList<ArrayList<String>>
                                bibliatekaList.addAll(newList)
                            }

                            AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> {
                                val newList = list.filter { it[4].toInt() == 5 } as ArrayList<ArrayList<String>>
                                bibliatekaList.addAll(newList)
                                bibliatekaList.sortByDescending {
                                    val t1 = it[0].indexOf("(")
                                    val t2 = it[0].indexOf(")")
                                    if (t2 != -1) {
                                        it[0].substring(t1 + 1, t2)
                                    } else {
                                        it[0]
                                    }
                                }
                            }
                        }
                    },
                    progressVisable = { progress ->
                        isProgressVisable = progress
                    })
            }
        }
    }
    if (searchText) {
        viewModel.clear()
        viewModel.addAllItemList(bibliatekaList)
        viewModel.filterItem(Settings.textFieldValueState.value)
    }
    if (isDialogBiblijatekaVisable) {
        fileName = if (searchText) filteredItems[fileListPosition][2]
        else {
            bibliatekaList[fileListPosition][2]
        }
        var opisanie = if (searchText) filteredItems[fileListPosition][1]
        else {
            bibliatekaList[fileListPosition][1]
        }
        val t1 = opisanie.indexOf("</span><br>")
        if (t1 != -1) opisanie = opisanie.substring(t1 + 11)
        val dirCount = if (searchText) filteredItems[fileListPosition][3].toInt()
        else {
            bibliatekaList[fileListPosition][3].toInt()
        }
        val izm = if (dirCount / 1024 > 1000) {
            formatFigureTwoPlaces(
                BigDecimal
                    .valueOf(dirCount.toFloat() / 1024 / 1024.toDouble())
                    .setScale(2, RoundingMode.HALF_UP)
                    .toFloat()
            ) + " Мб"
        } else {
            formatFigureTwoPlaces(
                BigDecimal
                    .valueOf(dirCount.toFloat() / 1024.toDouble())
                    .setScale(2, RoundingMode.HALF_UP)
                    .toFloat()
            ) + " Кб"
        }
        val listItem = if (searchText) filteredItems[fileListPosition]
        else {
            bibliatekaList[fileListPosition]
        }
        DialogBiblijateka(
            content = opisanie,
            pdfFileSize = izm,
            onDismiss = {
                isDialogBiblijatekaVisable = false
            },
            onConfirmation = {
                when {
                    !Settings.isNetworkAvailable(context) -> isDialogNoIntent = true
                    Settings.isNetworkAvailable(
                        context,
                        Settings.TRANSPORT_CELLULAR
                    ) -> isDialogNoWIFIVisable = true

                    else -> {
                        writeFile(
                            context, fileName, loadComplete = {
                                addNiadaunia(context, listItem)
                                navigationActions.navigateToBiblijateka(
                                    listItem[0],
                                    listItem[2]
                                )
                            },
                            inProcess = {
                                isProgressVisable = it
                            })
                    }
                }
                isDialogBiblijatekaVisable = false
            }
        )
    }
    if (isDialogNoWIFIVisable) {
        val listItem = if (searchText) filteredItems[fileListPosition]
        else {
            bibliatekaList[fileListPosition]
        }
        DialogNoWiFI(
            onDismiss = {
                isDialogNoWIFIVisable = false
            },
            onConfirmation = {
                writeFile(
                    context, fileName, loadComplete = {
                        addNiadaunia(context, listItem)
                        navigationActions.navigateToBiblijateka(
                            listItem[0],
                            listItem[2]
                        )
                    },
                    inProcess = {
                        isProgressVisable = it
                    })
                isDialogNoWIFIVisable = false
            }
        )
    }
    if (isDialogNoIntent) {
        DialogNoInternet {
            isDialogNoIntent = false
        }
    }
    Column {
        if (isProgressVisable) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        BiblijatekaListItems(
            if (searchText) filteredItems else bibliatekaList, biblijateka, navigationActions, innerPadding, searchText,
            setFileListPosition = { fileListPosition = it },
            setIsDialogBiblijatekaVisable = { isDialogBiblijatekaVisable = it },
            editListItem = {
                allPosition = viewModel.findIndex(it[3].toLong())
                editList.clear()
                editList.addAll(it)
                editItem = true
            }
        )
    }
}

@Composable
fun BiblijatekaListItems(
    listItem: SnapshotStateList<ArrayList<String>>, biblijateka: String, navigationActions: AppNavigationActions, innerPadding: PaddingValues, searchText: Boolean,
    setFileListPosition: (Int) -> Unit,
    setIsDialogBiblijatekaVisable: (Boolean) -> Unit,
    editListItem: (ArrayList<String>) -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    LazyColumn(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        items(
            listItem.size,
            key = { index -> listItem[index] + index }) { index ->
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .combinedClickable(
                            onClick = {
                                if (fileExistsBiblijateka(context, listItem[index][2])) {
                                    addNiadaunia(context, listItem[index])
                                    navigationActions.navigateToBiblijateka(
                                        listItem[index][0],
                                        listItem[index][2]
                                    )
                                } else {
                                    setFileListPosition(index)
                                    setIsDialogBiblijatekaVisable(true)
                                }
                            },
                            onLongClick = {
                                if (k.getBoolean("admin", false) && biblijateka != AllDestinations.BIBLIJATEKA_NIADAUNIA && !searchText) {
                                    editListItem(listItem[index])
                                }
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp),
                        painter = painterResource(R.drawable.poiter),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = ""
                    )
                    Text(
                        text = if (listItem[index][0] != "") listItem[index][0]
                        else listItem[index][2],
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                }
                if (listItem[index][5] != "") {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val bitmap = BitmapFactory.decodeFile(
                        "${context.filesDir}/bibliatekaImage/${listItem[index][5]}",
                        options
                    )
                    if (bitmap != null) {
                        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        Image(
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    shape = RectangleShape
                                )
                                .size(width = 200.dp, height = (200 / aspectRatio).dp)
                                .align(Alignment.CenterHorizontally)
                                .combinedClickable(
                                    onClick = {
                                        if (fileExistsBiblijateka(
                                                context,
                                                listItem[index][2]
                                            )
                                        ) {
                                            navigationActions.navigateToBiblijateka(
                                                listItem[index][0],
                                                listItem[index][2]
                                            )
                                            addNiadaunia(context, listItem[index])
                                        } else {
                                            setFileListPosition(index)
                                            setIsDialogBiblijatekaVisable(true)
                                        }
                                    },
                                    onLongClick = {
                                        if (k.getBoolean("admin", false) && biblijateka != AllDestinations.BIBLIJATEKA_NIADAUNIA && !searchText) {
                                            editListItem(listItem[index])
                                        }
                                    }
                                ),
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = ""
                        )
                    }
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}

fun addNiadaunia(
    context: Context,
    bibliatekaList: ArrayList<String>
) {
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val gson = Gson()
    val type = TypeToken.getParameterized(
        ArrayList::class.java,
        TypeToken.getParameterized(
            ArrayList::class.java,
            String::class.java
        ).type
    ).type
    val fileNadaunia =
        File("${context.filesDir}/biblijateka_latest.json")
    val biblioteka = ArrayList<ArrayList<String>>()
    if (fileNadaunia.exists()) {
        biblioteka.addAll(gson.fromJson(fileNadaunia.readText(), type))
        var inDel = -1
        for (i in biblioteka.indices) {
            if (bibliatekaList[2] == biblioteka[i][2]) {
                inDel = i
                break
            }
        }
        if (inDel != -1) {
            biblioteka.removeAt(inDel)
        }
        if (biblioteka.size > 4) {
            k.edit {
                remove(biblioteka[4][2])
            }
            biblioteka.removeAt(4)
        }
    }
    bibliatekaList[4] = "0"
    biblioteka.add(0, bibliatekaList)
    fileNadaunia.writer().use {
        it.write(gson.toJson(biblioteka, type))
    }
}

fun formatFigureTwoPlaces(value: Float): String {
    val myFormatter = DecimalFormat("##0.00")
    return myFormatter.format(value.toDouble())
}

fun fileExistsBiblijateka(context: Context, fileName: String): Boolean {
    val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
    return file.exists()
}

fun writeFile(
    context: Context,
    url: String,
    loadComplete: () -> Unit,
    inProcess: (Boolean) -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        inProcess(true)
        var error = false
        try {
            downloadPdfFile(context, url)
        } catch (_: Throwable) {
            error = true
        }
        if (!error) {
            saveFile(context, url)
            loadComplete()
        }
        inProcess(false)
    }
}

private fun saveFile(context: Context, fileName: String) {
    val dir = File("${context.filesDir}/bibliatekaPdf")
    if (!dir.exists()) dir.mkdir()
    val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
    val fileInput = File("${context.filesDir}/cache/$fileName")
    val fileInputStream = FileInputStream(fileInput)
    val output = FileOutputStream(file)
    val buffer = ByteArray(1024)
    var size = 0
    while ((fileInputStream.read(buffer).also { size = it }) != -1) {
        output.write(buffer, 0, size)
    }
    fileInputStream.close()
    output.close()
    fileInput.delete()
}

private suspend fun downloadPdfFile(context: Context, url: String, count: Int = 0) {
    var error = false
    val dir = File("${context.filesDir}/cache")
    if (!dir.exists()) dir.mkdir()
    val pathReference = Malitounik.referens.child("/data/bibliateka/$url")
    val metadata = pathReference.metadata.addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        downloadPdfFile(context, url, count + 1)
        return
    }
    val size = metadata.sizeBytes
    val localFile = File("${context.filesDir}/cache/$url")
    pathReference.getFile(localFile).addOnFailureListener {
        error = true
    }.await()
    if (size != localFile.length()) error = true
    if (error && count < 3) {
        downloadPdfFile(context, url, count + 1)
    }
}

private suspend fun getBibliateka(
    context: Context,
    biblijateka: String,
    progressVisable: (Boolean) -> Unit = { },
    bibliatekaList: (ArrayList<ArrayList<String>>) -> Unit = { }
) {
    progressVisable(true)
    try {
        val temp = ArrayList<ArrayList<String>>()
        val sb = getBibliatekaJson(context)
        val gson = Gson()
        val type = TypeToken.getParameterized(
            ArrayList::class.java,
            TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        ).type
        val biblioteka: ArrayList<ArrayList<String>> = gson.fromJson(sb, type)
        for (i in 0 until biblioteka.size) {
            val mySqlList = ArrayList<String>()
            val kniga = biblioteka[i]
            val rubrika = kniga[4]
            val link = kniga[0]
            val str = kniga[1]
            val pdf = kniga[2]
            val pdfFileSize = kniga[3]
            mySqlList.add(link)
            mySqlList.add(str)
            mySqlList.add(pdf)
            mySqlList.add(pdfFileSize)
            mySqlList.add(rubrika)
            val t1 = pdf.lastIndexOf(".")
            val imageName = pdf.take(t1) + ".png"
            var rub = "2"
            when (biblijateka) {
                AllDestinations.BIBLIJATEKA_GISTORYIA -> rub = "1"
                AllDestinations.BIBLIJATEKA_MALITOUNIKI -> rub = "2"
                AllDestinations.BIBLIJATEKA_SPEUNIKI -> rub = "3"
                AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> rub = "4"
                AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> rub = "5"
            }
            if (rubrika == rub) saveImagePdf(context, imageName)
            mySqlList.add(imageName)
            temp.add(mySqlList)
        }
        bibliatekaList(temp)
        progressVisable(false)
    } catch (_: Throwable) {
    }
}

private suspend fun getBibliatekaJson(context: Context, count: Int = 0): String {
    val localFile = File("${context.filesDir}/bibliateka.json")
    var error = false
    var result = ""
    val metadata = Malitounik.referens.child("/bibliateka.json").metadata.addOnFailureListener {
        error = true
    }.await()
    val size = metadata.sizeBytes
    if (localFile.length() != size) {
        if (Settings.isNetworkAvailable(context)) {
            Malitounik.referens.child("/bibliateka.json").getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) {
                    result = localFile.readText()
                } else {
                    error = true
                }
            }.await()
            if (size != localFile.length()) error = true
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
        if (error && count < 3) {
            getBibliatekaJson(context, count + 1)
            return ""
        }
    } else {
        result = localFile.readText()
    }
    return result
}

private suspend fun saveImagePdf(context: Context, image: String) {
    val dir = File("${context.filesDir}/bibliatekaImage")
    if (!dir.exists()) dir.mkdir()
    val imageFile = File("${context.filesDir}/bibliatekaImage/$image")
    if (!imageFile.exists() && Settings.isNetworkAvailable(context)) {
        Malitounik.referens.child("/images/bibliateka/$image").getFile(imageFile).await()
    }
}

@Composable
fun DialogBiblijateka(
    content: String,
    pdfFileSize: String,
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.download_file).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f, false)
                ) {
                    HtmlText(text = content, modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Column {
                        TextButton(
                            modifier = Modifier.align(Alignment.End),
                            onClick = { onConfirmation() },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                            Text(stringResource(R.string.download_bibliateka_file, pdfFileSize), fontSize = 18.sp)
                        }
                        TextButton(
                            modifier = Modifier.align(Alignment.End),
                            onClick = { onDismiss() },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                            Text(stringResource(R.string.close), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogNoWiFI(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.wifi_error).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(text = stringResource(R.string.download_bibliateka), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}