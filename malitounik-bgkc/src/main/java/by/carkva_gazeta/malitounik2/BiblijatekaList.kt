package by.carkva_gazeta.malitounik2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AllDestinations
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import by.carkva_gazeta.malitounik2.views.HtmlText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class FilterBiblijatekaModel : ViewModel() {
    private val items = SnapshotStateList<ArrayList<String>>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: MutableStateFlow<SnapshotStateList<ArrayList<String>>> = _filteredItems

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

var biblijatekaJob: Job? = null

@Composable
fun BiblijtekaList(navController: NavHostController, biblijateka: String, innerPadding: PaddingValues, searchText: Boolean, search: String) {
    val context = LocalContext.current
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var fileName by remember { mutableStateOf("") }
    var fileListPosition by remember { mutableIntStateOf(0) }
    val viewModel: FilterBiblijatekaModel = viewModel()
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDialogBiblijatekaVisable by rememberSaveable { mutableStateOf(false) }
    var isDialogNoWIFIVisable by rememberSaveable { mutableStateOf(false) }
    var isDialogNoIntent by rememberSaveable { mutableStateOf(false) }
    val bibliatekaList = remember { SnapshotStateList<ArrayList<String>>() }
    LaunchedEffect(Unit) {
        biblijatekaJob?.cancel()
        biblijatekaJob = CoroutineScope(Dispatchers.IO).launch {
            getBibliateka(
                context,
                bibliatekaList = { list ->
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
                            var newList = list.filter { it[4].toInt() == 1 } as ArrayList<ArrayList<String>>
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_MALITOUNIKI -> {
                            var newList = list.filter { it[4].toInt() == 2 } as ArrayList<ArrayList<String>>
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_SPEUNIKI -> {
                            var newList = list.filter { it[4].toInt() == 3 } as ArrayList<ArrayList<String>>
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> {
                            var newList = list.filter { it[4].toInt() == 4 } as ArrayList<ArrayList<String>>
                            bibliatekaList.addAll(newList)
                        }

                        AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> {
                            var newList = list.filter { it[4].toInt() == 5 } as ArrayList<ArrayList<String>>
                            bibliatekaList.addAll(newList)
                        }
                    }
                },
                progressVisable = { progress ->
                    isProgressVisable = progress
                })
        }
    }
    if (searchText) {
        viewModel.clear()
        viewModel.addAllItemList(bibliatekaList)
        viewModel.filterItem(search)
    }
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
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
            onDismissRequest = {
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
            onDismissRequest = {
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
        if (searchText) {
            BiblijatekaListItems(
                filteredItems, navigationActions, innerPadding,
                setFileListPosition = { fileListPosition = it },
                setIsDialogBiblijatekaVisable = { isDialogBiblijatekaVisable = it }
            )
        } else {
            BiblijatekaListItems(
                bibliatekaList, navigationActions, innerPadding,
                setFileListPosition = { fileListPosition = it },
                setIsDialogBiblijatekaVisable = { isDialogBiblijatekaVisable = it }
            )
        }
    }
    if (isProgressVisable) {
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun BiblijatekaListItems(
    listItem: SnapshotStateList<ArrayList<String>>, navigationActions: AppNavigationActions, innerPadding: PaddingValues,
    setFileListPosition: (Int) -> Unit,
    setIsDialogBiblijatekaVisable: (Boolean) -> Unit
) {
    val context = LocalContext.current
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
                        .clickable {
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp, 5.dp),
                        painter = painterResource(R.drawable.poiter),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
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
                            .clickable {
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
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = ""
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

fun addNiadaunia(
    context: Context,
    bibliatekaList: ArrayList<String>,
) {
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
            for (i in 0..2) {
                error = downloadPdfFile(context, url)
                if (!error) break
            }
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
    var size: Int
    while ((fileInputStream.read(buffer).also { size = it }) != -1) {
        output.write(buffer, 0, size)
    }
    fileInputStream.close()
    output.close()
    fileInput.delete()
}

private suspend fun downloadPdfFile(context: Context, url: String): Boolean {
    var error = false
    val dir = File("${context.filesDir}/cache")
    if (!dir.exists()) dir.mkdir()
    val pathReference = MainActivity.referens.child("/data/bibliateka/$url")
    val localFile = File("${context.filesDir}/cache/$url")
    pathReference.getFile(localFile).addOnFailureListener {
        error = true
    }.await()
    return error
}

private suspend fun getBibliateka(
    context: Context,
    progressVisable: (Boolean) -> Unit = { },
    bibliatekaList: (ArrayList<ArrayList<String>>) -> Unit = { }
) {
    withContext(Dispatchers.IO) {
        progressVisable(true)
        try {
            val temp = ArrayList<ArrayList<String>>()
            var sb = ""
            for (i in 0..2) {
                sb = getBibliatekaJson(context)
                if (sb != "") break
            }
            if (sb != "") {
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
                    val imageName = pdf.substring(0, t1) + ".png"
                    saveImagePdf(context, imageName)
                    mySqlList.add(imageName)
                    temp.add(mySqlList)
                }
                bibliatekaList(temp)
            } else {
                withContext(Dispatchers.Main) {
                    val toast = Toast.makeText(
                        context,
                        context.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }
            progressVisable(false)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

private suspend fun getBibliatekaJson(context: Context): String {
    var text = ""
    val pathReference = MainActivity.referens.child("/bibliateka.json")
    val localFile = File("${context.filesDir}/bibliateka.json")
    if (!localFile.exists() && Settings.isNetworkAvailable(context)) {
        pathReference.getFile(localFile).addOnCompleteListener {
            if (it.isSuccessful) text = localFile.readText()
        }.await()
    } else {
        text = localFile.readText()
    }
    return text
}

private suspend fun saveImagePdf(context: Context, image: String) {
    val dir = File("${context.filesDir}/bibliatekaImage")
    if (!dir.exists()) dir.mkdir()
    val imageFile = File("${context.filesDir}/bibliatekaImage/$image")
    if (!imageFile.exists() && Settings.isNetworkAvailable(context)) {
        MainActivity.referens.child("/images/bibliateka/$image").getFile(imageFile).await()
    }
}

@Composable
fun DialogBiblijateka(
    content: String,
    pdfFileSize: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.krest), contentDescription = "")
        },
        title = {
            Text(stringResource(R.string.download_file))
        },
        text = {
            HtmlText(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                text = content,
                fontSize = Settings.fontInterface.sp
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.close), fontSize = Settings.fontInterface.sp)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(
                    stringResource(R.string.download_bibliateka_file, pdfFileSize),
                    fontSize = Settings.fontInterface.sp
                )
            }
        }
    )
}

@Composable
fun DialogNoWiFI(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.signal_wifi_off), contentDescription = "")
        },
        title = {
            Text(stringResource(R.string.wifi_error))
        },
        text = {
            Text(stringResource(R.string.download_bibliateka), fontSize = Settings.fontInterface.sp)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cansel), fontSize = Settings.fontInterface.sp)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.ok), fontSize = Settings.fontInterface.sp)
            }
        }
    )
}