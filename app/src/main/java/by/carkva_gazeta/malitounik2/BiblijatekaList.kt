package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import by.carkva_gazeta.malitounik2.views.HtmlText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val items = ArrayList<ArrayList<String>>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: StateFlow<ArrayList<ArrayList<String>>> = _filteredItems

    fun clear() {
        items.clear()
    }

    fun addItemList(item: ArrayList<String>) {
        items.add(item)
    }

    fun remove(item: ArrayList<String>) {
        items.remove(item)
    }

    fun filterItem(rubrika: String) {
        _filteredItems.value = items.filter { it[4] == rubrika } as ArrayList<ArrayList<String>>
    }
}

var biblijatekaJob: Job? = null

@Composable
fun BiblijtekaList(navController: NavHostController, innerPadding: PaddingValues) {
    val view = LocalView.current
    val context = LocalContext.current
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var fileName by remember { mutableStateOf("") }
    var fileListPosition by remember { mutableIntStateOf(0) }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val viewModel: FilterBiblijatekaModel = viewModel()
    var isInit by remember { mutableStateOf(true) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDialogBiblijatekaVisable by rememberSaveable { mutableStateOf(false) }
    var isDialogNoWIFIVisable by rememberSaveable { mutableStateOf(false) }
    var isDialogNoIntent by rememberSaveable { mutableStateOf(false) }
    var rubrika by remember {
        mutableIntStateOf(
            k.getInt(
                "pubrikaBiblijatekiMenu",
                2
            )
        )
    }
    if (isInit) {
        LaunchedEffect(Unit) {
            isInit = false
            viewModel.clear()
            biblijatekaJob?.cancel()
            biblijatekaJob = CoroutineScope(Dispatchers.IO).launch {
                getBibliateka(context,
                    bibliatekaList = { list ->
                        for (i in list.indices) {
                            viewModel.addItemList(list[i])
                        }
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
                            for (i in biblioteka.indices) {
                                viewModel.addItemList(biblioteka[i])
                            }
                        }
                    },
                    progressVisable = { progress ->
                        isProgressVisable = progress
                    })
                viewModel.filterItem(rubrika.toString())
            }
        }
    }
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    if (isDialogBiblijatekaVisable) {
        fileName = filteredItems[fileListPosition][2]
        var opisanie = filteredItems[fileListPosition][1]
        val t1 = opisanie.indexOf("</span><br>")
        if (t1 != -1) opisanie = opisanie.substring(t1 + 11)
        val dirCount = filteredItems[fileListPosition][3].toInt()
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
        DialogBiblijateka(
            content = opisanie,
            pdfFileSize = izm,
            onDismissRequest = {
                isDialogBiblijatekaVisable = false
            },
            onConfirmation = {
                if (Settings.isNetworkAvailable(context, Settings.TRANSPORT_CELLULAR)) {
                    isDialogNoWIFIVisable = true
                } else {
                    writeFile(context, fileName, loadComplete = {
                        addNiadaunia(context, filteredItems, fileListPosition, viewModel)
                        navigationActions.navigateToBiblijateka(
                            filteredItems[fileListPosition][0],
                            filteredItems[fileListPosition][2]
                        )
                    },
                        inProcess = {
                            isProgressVisable = it
                        })
                }
                isDialogBiblijatekaVisable = false
            }
        )
    }
    if (isDialogNoWIFIVisable) {
        DialogNoWiFI(
            onDismissRequest = {
                isDialogNoWIFIVisable = false
            },
            onConfirmation = {
                writeFile(context, fileName, loadComplete = {
                    addNiadaunia(context, filteredItems, fileListPosition, viewModel)
                    navigationActions.navigateToBiblijateka(
                        filteredItems[fileListPosition][0],
                        filteredItems[fileListPosition][2]
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
    val lazyRowState = rememberLazyListState()
    val list = listOf(
        0,
        1,
        2,
        3,
        4
    )
    val selectState = remember(list) { list.map { false }.toMutableStateList() }
    LaunchedEffect(rubrika) {
        CoroutineScope(Dispatchers.Main).launch {
            selectState[rubrika] = true
            lazyRowState.scrollToItem(rubrika)
            viewModel.filterItem(rubrika.toString())
        }
    }
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        LazyRow(state = lazyRowState, modifier = Modifier.fillMaxWidth()) {
            items(list.size) { index ->
                val title = when (index) {
                    0 -> stringResource(R.string.bibliateka_niadaunia)
                    1 -> stringResource(R.string.bibliateka_gistoryia_carkvy)
                    2 -> stringResource(R.string.bibliateka_malitouniki)
                    3 -> stringResource(R.string.bibliateka_speuniki)
                    4 -> stringResource(R.string.bibliateka_rel_litaratura)
                    else -> stringResource(R.string.bibliateka_niadaunia)
                }
                FilterChip(
                    modifier = Modifier.padding(end = 10.dp),
                    onClick = {
                        for (i in 0..4)
                            selectState[i] = false
                        selectState[index] = !selectState[index]
                        val edit = k.edit()
                        edit.putInt("pubrikaBiblijatekiMenu", index)
                        rubrika = index
                        CoroutineScope(Dispatchers.Main).launch {
                            lazyRowState.scrollToItem(index)
                        }
                        edit.apply()
                    },
                    label = {
                        Text(title, fontSize = 18.sp)
                    },
                    selected = selectState[index],
                    leadingIcon = if (selectState[index]) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = "",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
        LazyColumn {
            items(
                filteredItems.size,
                key = { index -> filteredItems[index][2] + filteredItems[index][4] }) { index ->
                Column {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                if (fileExistsBiblijateka(context, filteredItems[index][2])) {
                                    addNiadaunia(context, filteredItems, index, viewModel)
                                    navigationActions.navigateToBiblijateka(
                                        filteredItems[index][0],
                                        filteredItems[index][2]
                                    )
                                } else {
                                    fileListPosition = index
                                    isDialogBiblijatekaVisable = true
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp, 12.dp),
                            painter = painterResource(R.drawable.krest),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            text = if (filteredItems[index][0] != "") filteredItems[index][0]
                            else filteredItems[index][2],
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (filteredItems[index][5] != "") {
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        val bitmap = BitmapFactory.decodeFile(
                            "${context.filesDir}/bibliatekaImage/${filteredItems[index][5]}",
                            options
                        )
                        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        Image(
                            modifier = Modifier
                                .size(width = 200.dp, height = (200 / aspectRatio).dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 10.dp)
                                .clickable {
                                    if (fileExistsBiblijateka(context, filteredItems[index][2])) {
                                        addNiadaunia(context, filteredItems, index, viewModel)
                                        navigationActions.navigateToBiblijateka(
                                            filteredItems[index][0],
                                            filteredItems[index][2]
                                        )
                                    } else {
                                        fileListPosition = index
                                        isDialogBiblijatekaVisable = true
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

fun addNiadaunia(
    context: Context,
    filteredItems: ArrayList<ArrayList<String>>,
    index: Int,
    viewModel: FilterBiblijatekaModel
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
            if (filteredItems[index][2] == biblioteka[i][2]) {
                inDel = i
                break
            }
        }
        if (inDel != -1) {
            biblioteka.removeAt(inDel)
            viewModel.remove(filteredItems[index])
        }
        if (biblioteka.size > 4) {
            biblioteka.removeAt(4)
            viewModel.remove(filteredItems[index])
        }
    }
    filteredItems[index][4] = "0"
    biblioteka.add(0, filteredItems[index])
    fileNadaunia.writer().use {
        it.write(gson.toJson(biblioteka, type))
    }
    viewModel.addItemList(filteredItems[index])
}

fun formatFigureTwoPlaces(value: Float): String {
    val myFormatter = DecimalFormat("##0.00")
    return myFormatter.format(value.toDouble())
}

fun fileExistsBiblijateka(context: Context, fileName: String): Boolean {
    val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
    return file.exists()
}

private fun writeFile(
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
        } catch (_: Throwable) {
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
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(stringResource(R.string.download_file))
        },
        text = {
            HtmlText(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                text = content,
                fontSize = 18.sp
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
                Text(stringResource(R.string.close), fontSize = 18.sp)
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
                    fontSize = 18.sp
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
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(stringResource(R.string.wifi_error))
        },
        text = {
            Text(stringResource(R.string.download_bibliateka), fontSize = 18.sp)
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
                Text(stringResource(R.string.cansel), fontSize = 18.sp)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.ok), fontSize = 18.sp)
            }
        }
    )
}