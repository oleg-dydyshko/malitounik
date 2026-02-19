package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.content.edit
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun BiblijtekaList(navController: NavHostController, biblijateka: String, innerPadding: PaddingValues, addItem: Boolean, viewModel: SearchBibleViewModel, editDismiss: () -> Unit) {
    val context = LocalContext.current
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var fileName by remember { mutableStateOf("") }
    var fileListPosition by remember { mutableIntStateOf(0) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDialogBiblijatekaVisable by remember { mutableStateOf(false) }
    var isDialogNoWIFIVisable by remember { mutableStateOf(false) }
    var isDialogNoIntent by remember { mutableStateOf(false) }
    val biblijatekaAllList = remember { mutableStateListOf<ArrayList<String>>() }
    val bibliatekaList = remember { mutableStateListOf<ArrayList<String>>() }
    val filteredItems = remember { mutableStateListOf<ArrayList<String>>() }
    val editList = remember { ArrayList<String>() }
    var editItem by remember { mutableStateOf(false) }
    var share by remember { mutableStateOf(false) }
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
            saveBibliateka(context, biblijatekaAllList, allPosition, title, rubrika, apisanne, pdfFile) {
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
            getBibliateka(
                context,
                biblijateka,
                bibliatekaList = { list ->
                    biblijatekaAllList.addAll(list)
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
                            val newList = list.filter { it[4].toInt() == 1 }
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_MALITOUNIKI -> {
                            val newList = list.filter { it[4].toInt() == 2 }
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_SPEUNIKI -> {
                            val newList = list.filter { it[4].toInt() == 3 }
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> {
                            val newList = list.filter { it[4].toInt() == 4 }
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> {
                            val newList = list.filter { it[4].toInt() == 5 }
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
    LaunchedEffect(viewModel.textFieldValueState.text, viewModel.searchText) {
        filteredItems.clear()
        if (viewModel.searchText) {
            if (viewModel.textFieldValueState.text.isNotEmpty()) {
                val filterList = biblijatekaAllList.filter { it[1].contains(viewModel.textFieldValueState.text, ignoreCase = true) }
                filteredItems.addAll(filterList)
            } else {
                filteredItems.addAll(biblijatekaAllList)
            }
        } else {
            filteredItems.addAll(bibliatekaList)
        }
    }
    if (isDialogBiblijatekaVisable) {
        fileName = if (viewModel.searchText) filteredItems[fileListPosition][2]
        else {
            bibliatekaList[fileListPosition][2]
        }
        var opisanie = if (viewModel.searchText) filteredItems[fileListPosition][1]
        else {
            bibliatekaList[fileListPosition][1]
        }
        val t1 = opisanie.indexOf("</span><br>")
        if (t1 != -1) opisanie = opisanie.substring(t1 + 11)
        val dirCount = if (viewModel.searchText) filteredItems[fileListPosition][3].toInt()
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
        val listItem = if (viewModel.searchText) filteredItems[fileListPosition]
        else {
            bibliatekaList[fileListPosition]
        }
        DialogBiblijateka(
            content = opisanie,
            isShare = share,
            pdfFileSize = izm,
            onDismiss = {
                isDialogBiblijatekaVisable = false
            },
            onConfirmation = {
                when {
                    !Settings.isNetworkAvailable(context) -> isDialogNoIntent = true
                    Settings.isNetworkAvailable(
                        context
                    ) -> isDialogNoWIFIVisable = true

                    else -> {
                        writeFile(
                            context, fileName, loadComplete = {
                                if (share) {
                                    sharePdfFile(context, listItem[2])
                                } else {
                                    addNiadaunia(context, listItem)
                                    navigationActions.navigateToBiblijateka(
                                        listItem[0],
                                        listItem[2]
                                    )
                                }
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
        val listItem = if (viewModel.searchText) filteredItems[fileListPosition]
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
                        if (share) {
                            sharePdfFile(context, listItem[2])
                        } else {
                            addNiadaunia(context, listItem)
                            navigationActions.navigateToBiblijateka(
                                listItem[0],
                                listItem[2]
                            )
                        }
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
            if (viewModel.searchText) filteredItems else bibliatekaList, biblijateka, navigationActions, innerPadding, viewModel.searchText,
            setIsDialogBiblijatekaVisable = { position, isVisable, isShare ->
                share = isShare
                fileListPosition = position
                isDialogBiblijatekaVisable = isVisable
            },
            editListItem = {
                var position = 0
                for (i in biblijatekaAllList.indices) {
                    if (biblijatekaAllList[i][3].toLong() == it[3].toLong()) {
                        position = i
                        break
                    }
                }
                allPosition = position
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
    setIsDialogBiblijatekaVisable: (Int, Boolean, Boolean) -> Unit,
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
    var shreIndex by remember { mutableIntStateOf(0) }
    var isShare by remember { mutableStateOf(false) }
    LaunchedEffect(isShare) {
        if (isShare) {
            if (fileExistsBiblijateka(context, listItem[shreIndex][2])) {
                sharePdfFile(context, listItem[shreIndex][2])
            } else {
                setIsDialogBiblijatekaVisable(shreIndex, true, true)
            }
            isShare = false
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
                                Settings.vibrate()
                                if (fileExistsBiblijateka(context, listItem[index][2])) {
                                    addNiadaunia(context, listItem[index])
                                    navigationActions.navigateToBiblijateka(
                                        listItem[index][0],
                                        listItem[index][2]
                                    )
                                } else {
                                    setIsDialogBiblijatekaVisable(index, true, false)
                                }
                            },
                            onLongClick = {
                                Settings.vibrate(true)
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
                        contentDescription = null
                    )
                    Text(
                        text = if (listItem[index][0] != "") listItem[index][0]
                        else listItem[index][2],
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                    Icon(
                        modifier = Modifier
                            .clickable {
                                Settings.vibrate()
                                shreIndex = index
                                isShare = true
                            }
                            .padding(10.dp),
                        painter = painterResource(R.drawable.share),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = stringResource(R.string.share)
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
                                        Settings.vibrate()
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
                                            setIsDialogBiblijatekaVisable(index, true, false)
                                        }
                                    },
                                    onLongClick = {
                                        Settings.vibrate(true)
                                        if (k.getBoolean("admin", false) && biblijateka != AllDestinations.BIBLIJATEKA_NIADAUNIA && !searchText) {
                                            editListItem(listItem[index])
                                        }
                                    }
                                ),
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null
                        )
                    }
                    val maxLine = remember { mutableIntStateOf(2) }
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                Settings.vibrate()
                                maxLine.intValue = if (maxLine.intValue == Int.MAX_VALUE) 2
                                else Int.MAX_VALUE
                            }, text = AnnotatedString.fromHtml(listItem[index][1]), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}

fun sharePdfFile(context: Context, fileName: String) {
    val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
    val uri =
        FileProvider.getUriForFile(context, "by.carkva_gazeta.malitounik.fileprovider", file)
    val sendIntent = Intent(Intent.ACTION_SEND)
    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
    sendIntent.putExtra(
        Intent.EXTRA_SUBJECT,
        context.getString(R.string.set_log_file)
    )
    sendIntent.type = "text/html"
    context.startActivity(
        Intent.createChooser(
            sendIntent,
            context.getString(R.string.set_log_file)
        )
    )
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
            if (rubrika == rub) {
                saveImagePdf(context, imageName)
            } else {
                withContext(Dispatchers.IO) {
                    saveImagePdf(context, imageName)
                }
            }
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
    isShare: Boolean,
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
                if (isShare) {
                    Text(
                        text = stringResource(R.string.shere_help), modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.primary
                    )
                }
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
                            onClick = {
                                Settings.vibrate()
                                onConfirmation()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                            Text(stringResource(R.string.download_bibliateka_file, pdfFileSize), fontSize = 18.sp)
                        }
                        TextButton(
                            modifier = Modifier.align(Alignment.End),
                            onClick = {
                                Settings.vibrate()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
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
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onConfirmation()
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}