package by.carkva_gazeta.malitounik.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.DialogContextMenu
import by.carkva_gazeta.malitounik.DialogDelite
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.views.AppDropdownMenu
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasochnicaListNew(navController: NavHostController, dirToFile: String) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    if (Piasochnica.findDirAsSave.isEmpty()) {
        Piasochnica.getFindFileListAsSave()
    }
    var isProgressVisable by remember { mutableStateOf(false) }
    val fileList = remember { mutableStateListOf<String>() }
    val backCopy = remember { mutableStateListOf<String>() }
    var expandedUp by remember { mutableStateOf(false) }
    LaunchedEffect(Unit, Settings.bibleTime) {
        val dir = context.getExternalFilesDir("PiasochnicaBackCopy")
        if (dir?.exists() == true) {
            var list = dir.list()
            list?.forEach {
                val file = File("$dir/$it")
                val systemTime = System.currentTimeMillis()
                val lastModified = GregorianCalendar()
                lastModified.timeInMillis = file.lastModified()
                lastModified.add(Calendar.DATE, 7)
                if (lastModified.timeInMillis < systemTime) {
                    file.delete()
                }
            }
            list = dir.list()
            list?.forEach {
                backCopy.add(it)
            }
            backCopy.sort()
        }
        if (Settings.isNetworkAvailable(context)) {
            coroutineScope.launch {
                isProgressVisable = true
                fileList.clear()
                fileList.addAll(getPasochnicaFileList())
                fileList.sort()
                if (Settings.bibleTime) {
                    Settings.bibleTime = false
                    Piasochnica.isHTML = dirToFile.contains(".html")
                    Piasochnica.history.clear()
                    val t1 = dirToFile.lastIndexOf("/")
                    val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
                    else dirToFile
                    if (Piasochnica.isFilePiasochnicaExitst(fileName, fileList)) {
                        coroutineScope.launch {
                            isProgressVisable = true
                            Piasochnica.getPasochnicaFile(fileName, result = { sb, text ->
                                Piasochnica.addHistory(sb, 0)
                                val html = if (Piasochnica.isHTML) {
                                    sb
                                } else {
                                    SpannableStringBuilder(text)
                                }
                                Piasochnica.htmlText = html
                                navigationActions.navigateToPiasochnica(fileName)
                            })
                        }
                    } else {
                        Piasochnica.getFileCopyPostRequest(dirToFile = Piasochnica.findResoursDir(fileName), isProgressVisable = {
                            isProgressVisable = it
                        }) { text, fileName ->
                            val html = if (Piasochnica.isHTML) {
                                SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                            } else {
                                SpannableStringBuilder(text)
                            }
                            Piasochnica.addHistory(html, 0)
                            Piasochnica.htmlText = html
                            navigationActions.navigateToPiasochnica(fileName)
                            isProgressVisable = false
                        }
                    }
                }
                isProgressVisable = false
            }
        }
    }
    var isDialogNet by remember { mutableStateOf(false) }
    if (isDialogNet) {
        DialogNetFileExplorer(setFile = { dirToFile ->
            isProgressVisable = true
            Piasochnica.isHTML = dirToFile.contains(".html")
            Piasochnica.history.clear()
            val t1 = dirToFile.lastIndexOf("/")
            val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
            else dirToFile
            if (Piasochnica.isFilePiasochnicaExitst(fileName, fileList)) {
                coroutineScope.launch {
                    isProgressVisable = true
                    Piasochnica.getPasochnicaFile(fileName, result = { sb, text ->
                        Piasochnica.addHistory(sb, 0)
                        val html = if (Piasochnica.isHTML) {
                            sb
                        } else {
                            SpannableStringBuilder(text)
                        }
                        Piasochnica.htmlText = html
                        navigationActions.navigateToPiasochnica(fileName)
                    })
                    isProgressVisable = false
                    isProgressVisable = false
                    isDialogNet = false
                }
            } else {
                Piasochnica.getFileCopyPostRequest(dirToFile = dirToFile, isProgressVisable = {
                    isProgressVisable = it
                }) { text, fileName ->
                    val html = if (Piasochnica.isHTML) {
                        SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    } else {
                        SpannableStringBuilder(text)
                    }
                    Piasochnica.addHistory(html, 0)
                    Piasochnica.htmlText = html
                    navigationActions.navigateToPiasochnica(fileName)
                    isProgressVisable = false
                    isDialogNet = false
                }
            }
        }) {
            isDialogNet = false
        }
    }
    val mActivityResultFile = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { data
        ->
        if (data.resultCode == Activity.RESULT_OK) {
            val fileUri = data.data?.data
            val file = File("${context.filesDir}/cache/cache.txt")
            val output = FileOutputStream(file)
            fileUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.let { inputStream ->
                    val buffer = ByteArray(1024)
                    var size: Int
                    while ((inputStream.read(buffer).also { size = it }) != -1) {
                        output.write(buffer, 0, size)
                    }
                    uri.path
                    inputStream.close()
                }
                val path = uri.path ?: ""
                val t1 = path.lastIndexOf("/")
                val resours = if (t1 != -1) path.substring(t1 + 1) else path
                Piasochnica.isHTML = resours.contains(".html")
                Piasochnica.history.clear()
                if (Piasochnica.isFilePiasochnicaExitst(resours, fileList)) {
                    coroutineScope.launch {
                        isProgressVisable = true
                        Piasochnica.getPasochnicaFile(resours, result = { sb, text ->
                            Piasochnica.addHistory(sb, 0)
                            val html = if (Piasochnica.isHTML) {
                                SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                            } else {
                                SpannableStringBuilder(text)
                            }
                            Piasochnica.htmlText = html
                            navigationActions.navigateToPiasochnica(resours)
                        })
                        isProgressVisable = false
                    }
                } else {
                    var text = file.readText()
                    text = if (Settings.dzenNoch.value) text.replace("#d00505", "#ff6666", true)
                    else text
                    val htmltext = if (Piasochnica.isHTML) SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    else SpannableStringBuilder(text)
                    Piasochnica.addHistory(htmltext, 0)
                    Piasochnica.htmlText = htmltext
                    navigationActions.navigateToPiasochnica(resours)
                }
            }
        }
    }
    var dialogContextMenu by remember { mutableStateOf(false) }
    var dialogDelite by remember { mutableStateOf(false) }
    var dialogDeliteAllPiasochnica by remember { mutableStateOf(false) }
    var dialogDeliteAllBackCopy by remember { mutableStateOf(false) }
    var dialogDeliteBackCopy by remember { mutableStateOf(false) }
    var dialogSetFileName by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }
    if (dialogContextMenu) {
        DialogContextMenu(title = fileList[position], editTitle = stringResource(R.string.rename_file), onEdit = {
            dialogSetFileName = true
            dialogContextMenu = false
        }, onDelite = {
            dialogDelite = true
            dialogContextMenu = false
        }) {
            dialogContextMenu = false
        }
    }
    if (dialogDeliteAllPiasochnica) {
        DialogDelite(title = stringResource(R.string.del_all_pasochnica), onConfirmation = {
            if (Settings.isNetworkAvailable(context)) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val list = Malitounik.referens.child("/admin/piasochnica").list(1000).await()
                        list.items.forEach {
                            it.delete().await()
                        }
                    } catch (_: Throwable) {
                        Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                    }
                    fileList.clear()
                }
            } else {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
            dialogDeliteAllPiasochnica = false
        }) {
            dialogDeliteAllPiasochnica = false
        }
    }
    if (dialogDeliteAllBackCopy) {
        DialogDelite(title = stringResource(R.string.del_all_back_copy), onConfirmation = {
            val dir = context.getExternalFilesDir("PiasochnicaBackCopy")
            if (dir?.exists() == true) {
                dir.deleteRecursively()
                backCopy.clear()
            }
            dialogDeliteAllBackCopy = false
        }) {
            dialogDeliteAllBackCopy = false
        }
    }
    if (dialogDelite) {
        DialogDelite(title = fileList[position], onConfirmation = {
            val title = fileList[position]
            val isSite = !title.contains("/admin/piasochnica")
            fileList.removeAt(position)
            Piasochnica.getFileUnlinkPostRequest(title, isSite)
            dialogDelite = false
        }) {
            dialogDelite = false
        }
    }
    if (dialogDeliteBackCopy) {
        DialogDelite(title = backCopy[position], onConfirmation = {
            val title = backCopy[position]
            val fileOld = File(context.getExternalFilesDir("PiasochnicaBackCopy"), title)
            if (fileOld.exists()) fileOld.delete()
            backCopy.removeAt(position)
            dialogDeliteBackCopy = false
        }) {
            dialogDeliteBackCopy = false
        }
    }
    if (dialogSetFileName) {
        DialogSetNameFile(fileList[position], setFileName = { fileName ->
            val oldFileName = fileList[position]
            fileList[position] = fileList[position].replace(oldFileName, fileName)
            Piasochnica.getFileRenamePostRequest(oldFileName, fileName, false)
            dialogSetFileName = false
        }) {
            dialogSetFileName = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.pasochnica),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = Settings.fontInterface.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent()
                        intent.type = "*/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/html", "text/plain"))
                        mActivityResultFile.launch(Intent.createChooser(intent, context.getString(R.string.vybrac_file)))
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.directory_icon_menu), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    IconButton(onClick = {
                        isDialogNet = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.www_icon), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    IconButton(onClick = {
                        var resours = "newFile.html"
                        var count = 1
                        while (true) {
                            if (Piasochnica.isFilePiasochnicaExitst(resours, fileList)) {
                                resours = "newFile$count.html"
                                count++
                            } else break
                        }
                        Piasochnica.isHTML = true
                        Piasochnica.history.clear()
                        val text = SpannableStringBuilder("")
                        Piasochnica.addHistory(text, 0)
                        Piasochnica.htmlText = text
                        Piasochnica.crateNewFilePiasochnica(resours)
                        navigationActions.navigateToPiasochnica(resours)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.plus), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    if (fileList.isNotEmpty() || backCopy.isNotEmpty()) {
                        IconButton(onClick = { expandedUp = true }) {
                            Icon(
                                painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        AppDropdownMenu(
                            expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                            if (backCopy.isNotEmpty()) {
                                DropdownMenuItem(onClick = {
                                    dialogDeliteAllBackCopy = true
                                    expandedUp = false
                                }, text = { Text(stringResource(R.string.del_all_copy), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.delete), contentDescription = ""
                                    )
                                })
                            }
                            if (fileList.isNotEmpty()) {
                                DropdownMenuItem(onClick = {
                                    dialogDeliteAllPiasochnica = true
                                    expandedUp = false
                                }, text = { Text(stringResource(R.string.del_pasochnica), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.delete), contentDescription = ""
                                    )
                                })
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            LazyColumn {
                items(fileList.size) { index ->
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .combinedClickable(onClick = {
                                val fileName = fileList[index]
                                coroutineScope.launch {
                                    isProgressVisable = true
                                    Piasochnica.isHTML = fileName.contains(".html")
                                    Piasochnica.history.clear()
                                    Piasochnica.getPasochnicaFile(fileName, result = { sb, text ->
                                        Piasochnica.addHistory(sb, 0)
                                        val html = if (Piasochnica.isHTML) {
                                            SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                        } else {
                                            SpannableStringBuilder(text)
                                        }
                                        Piasochnica.htmlText = html
                                        navigationActions.navigateToPiasochnica(fileName)
                                    })
                                    isProgressVisable = false
                                }
                            }, onLongClick = {
                                position = index
                                dialogContextMenu = true
                            }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = ""
                        )
                        Text(
                            text = fileList[index],
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
                items(backCopy.size) { index ->
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .combinedClickable(onClick = {
                                val fileName = backCopy[index]
                                val file = File(context.getExternalFilesDir("PiasochnicaBackCopy"), fileName)
                                if (file.exists()) {
                                    var text = file.readText()
                                    text = if (Settings.dzenNoch.value) text.replace("#d00505", "#ff6666", true)
                                    else text
                                    val html = if (Piasochnica.isHTML) {
                                        SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                    } else {
                                        SpannableStringBuilder(text)
                                    }
                                    Piasochnica.htmlText = html
                                    navigationActions.navigateToPiasochnica(fileName)
                                }
                            }, onLongClick = {
                                position = index
                                dialogDeliteBackCopy = true
                            }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = ""
                        )
                        Text(
                            text = backCopy[index],
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = Settings.fontInterface.sp
                        )
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
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun DialogNetFileExplorer(
    fileName: String = "", setFile: (String) -> Unit, onDismiss: () -> Unit
) {
    var dir by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        getDirListRequest(dir)
    }
    val context = LocalContext.current
    var dialogContextMenu by remember { mutableStateOf(false) }
    var dialogDelite by remember { mutableStateOf(false) }
    var dialogSetFileName by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }
    if (dialogContextMenu) {
        DialogContextMenu(title = dir + "/" + Piasochnica.fileList[position].title, editTitle = stringResource(R.string.rename_file), onEdit = {
            dialogSetFileName = true
            dialogContextMenu = false
        }, onDelite = {
            dialogDelite = true
            dialogContextMenu = false
        }) {
            dialogContextMenu = false
        }
    }
    if (dialogDelite) {
        DialogDelite(title = dir + "/" + Piasochnica.fileList[position].title, onConfirmation = {
            val title = dir + "/" + Piasochnica.fileList[position].title
            val isSite = !title.contains("/admin/piasochnica")
            Piasochnica.getFileUnlinkPostRequest(title, isSite)
            Piasochnica.fileList.removeAt(position)
            dialogDelite = false
        }) {
            dialogDelite = false
        }
    }
    if (dialogSetFileName) {
        DialogSetNameFile(Piasochnica.fileList[position].title, setFileName = { fileName ->
            val oldFileName = dir + "/" + Piasochnica.fileList[position].title
            val isSite = !oldFileName.contains("/admin/piasochnica")
            Piasochnica.getFileRenamePostRequest(oldFileName, "$dir/$fileName", isSite, update = {
                getDirListRequest(dir)
            })
            dialogSetFileName = false
        }) {
            dialogSetFileName = false
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValueState by remember { mutableStateOf(fileName) }
    var dialogFileExists by remember { mutableStateOf(false) }
    if (dialogFileExists) {
        DialogFileExists(fileName = fileName, setFileName = {
            setFile("$dir/$textFieldValueState")
            onDismiss()
            dialogFileExists = false
        }) {
            dialogFileExists = false
        }
    }
    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.vybrac_file),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                if (Piasochnica.isProgressVisable) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                if (fileName.isNotEmpty()) {
                    val focusRequester = remember { FocusRequester() }
                    var textFieldLoaded by remember { mutableStateOf(false) }
                    TextField(
                        textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                        placeholder = { Text(stringResource(R.string.set_file_name), fontSize = Settings.fontInterface.sp) },
                        value = textFieldValueState,
                        onValueChange = {
                            textFieldValueState = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                if (!textFieldLoaded) {
                                    focusRequester.requestFocus()
                                    textFieldLoaded = true
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (textFieldValueState.isEmpty()) {
                                Toast.makeText(context, context.getString(R.string.setfile_name), Toast.LENGTH_SHORT).show()
                            } else {
                                Malitounik.referens.child("$dir/$textFieldValueState").downloadUrl.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        dialogFileExists = true
                                    } else {
                                        setFile("$dir/$textFieldValueState")
                                        onDismiss()
                                        keyboardController?.hide()
                                    }
                                }
                            }
                        })
                    )
                }
                LazyColumn(modifier = Modifier.weight(1f, false)) {
                    items(Piasochnica.fileList.size) { index ->
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .combinedClickable(onClick = {
                                    when (Piasochnica.fileList[index].resources) {
                                        R.drawable.directory_up -> {
                                            val t1 = dir.lastIndexOf("/")
                                            dir = dir.take(t1)
                                            getDirListRequest(dir)
                                        }

                                        R.drawable.directory_icon -> {
                                            dir = dir + "/" + Piasochnica.fileList[index].title
                                            getDirListRequest(dir)
                                        }

                                        else -> {
                                            if (fileName.isNotEmpty()) textFieldValueState = Piasochnica.fileList[index].title
                                            else setFile(dir + "/" + Piasochnica.fileList[index].title)
                                        }
                                    }
                                }, onLongClick = {
                                    if (!(Piasochnica.fileList[index].resources == R.drawable.directory_up || Piasochnica.fileList[index].resources == R.drawable.directory_icon)) {
                                        position = index
                                        dialogContextMenu = true
                                    }
                                }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(48.dp, 48.dp),
                                painter = painterResource(Piasochnica.fileList[index].resources),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                            Text(
                                text = Piasochnica.fileList[index].title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                        HorizontalDivider()
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    if (fileName.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                if (textFieldValueState.isEmpty()) {
                                    Toast.makeText(context, context.getString(R.string.setfile_name), Toast.LENGTH_SHORT).show()
                                } else {
                                    Malitounik.referens.child("$dir/$textFieldValueState").downloadUrl.addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            dialogFileExists = true
                                        } else {
                                            setFile("$dir/$textFieldValueState")
                                            onDismiss()
                                        }
                                    }
                                }
                            }, shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                            Text(stringResource(R.string.ok), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogSetNameFile(
    fileName: String, setFileName: (String) -> Unit, onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var textFieldValueState by remember { mutableStateOf(fileName) }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.set_file_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                TextField(
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                    placeholder = { Text(stringResource(R.string.set_file_name), fontSize = Settings.fontInterface.sp) },
                    value = textFieldValueState,
                    onValueChange = {
                        textFieldValueState = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onDone = {
                        setFileName(textFieldValueState)
                        onDismiss()
                    })
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            setFileName(textFieldValueState)
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogFileExists(
    fileName: String, setFileName: (String) -> Unit, onDismiss: () -> Unit
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
                    text = stringResource(R.string.file_exists),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    text = stringResource(R.string.file_exists_opis, fileName),
                    fontSize = Settings.fontInterface.sp
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            setFileName(fileName)
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun getDirListRequest(dir: String) {
    val context = Malitounik.applicationContext()
    if (Settings.isNetworkAvailable(context)) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Piasochnica.isProgressVisable = true
                Piasochnica.fileList.clear()
                val temp = ArrayList<MyNetFile>()
                val list = Malitounik.referens.child("/$dir").list(1000).await()
                if (dir != "") {
                    val t1 = dir.lastIndexOf("/")
                    temp.add(MyNetFile(R.drawable.directory_up, dir.substring(t1 + 1)))
                }
                list.prefixes.forEach {
                    temp.add(MyNetFile(R.drawable.directory_icon, it.name))
                }
                list.items.forEach {
                    if (it.name.contains(".htm")) {
                        temp.add(MyNetFile(R.drawable.file_html_icon, it.name))
                    } else if (it.name.contains(".json")) {
                        temp.add(MyNetFile(R.drawable.file_json_icon, it.name))
                    } else if (it.name.contains(".php")) {
                        temp.add(MyNetFile(R.drawable.file_php_icon, it.name))
                    } else {
                        temp.add(MyNetFile(R.drawable.file_txt_icon, it.name))
                    }
                }
                Piasochnica.fileList.addAll(temp)
            } catch (_: Throwable) {
                Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
            }
            Piasochnica.isProgressVisable = false
        }
    } else {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}

suspend fun getPasochnicaFileList(count: Int = 0): ArrayList<String> {
    var error = false
    val fileList = ArrayList<String>()
    try {
        val list = Malitounik.referens.child("/admin/piasochnica").list(500).addOnFailureListener {
            error = true
        }.await()
        list.items.forEach {
            fileList.add(it.name)
        }
    } catch (_: Throwable) {
        error = true
    }
    if (error && count < 3) {
        getPasochnicaFileList(count + 1)
        return ArrayList()
    }
    return fileList
}

data class MyNetFile(val resources: Int, val title: String)