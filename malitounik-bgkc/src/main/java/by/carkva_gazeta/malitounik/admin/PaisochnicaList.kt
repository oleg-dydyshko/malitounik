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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.DialogContextMenu
import by.carkva_gazeta.malitounik.DialogDelite
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

object PasochnicaList {
    const val NONE = 0
    const val FILE = 1
    const val WWW = 2
    const val ADD = 3
    const val DEL_ALL_PASOCHNICA = 5
    var pasochnicaAction by mutableIntStateOf(NONE)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasochnicaList(navController: NavHostController, innerPadding: PaddingValues, viewModel: Piasochnica) {
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
    val fileList = remember { mutableStateListOf<PaisochnicaFileList>() }
    LaunchedEffect(Unit) {
        if (Settings.isNetworkAvailable(context)) {
            viewModel.viewModelScope.launch {
                viewModel.isProgressVisable = true
                fileList.clear()
                fileList.addAll(viewModel.getPasochnicaFileList())
                fileList.sortWith(
                    compareByDescending {
                        it.updatedTimeMillis
                    }
                )
                viewModel.isProgressVisable = false
            }
        }
    }
    var isDialogNet by remember { mutableStateOf(false) }
    if (isDialogNet) {
        DialogNetFileExplorer(viewModel = viewModel, setFile = { dirToFile ->
            viewModel.isProgressVisable = true
            viewModel.isHTML = dirToFile.contains(".html")
            val t1 = dirToFile.lastIndexOf("/")
            val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
            else dirToFile
            if (viewModel.isFilePiasochnicaExitst(fileName, fileList)) {
                coroutineScope.launch {
                    viewModel.isProgressVisable = true
                    viewModel.getPasochnicaFile(fileName, result = { text ->
                        val html = if (viewModel.isHTML) {
                            SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                        } else {
                            SpannableStringBuilder(text)
                        }
                        viewModel.htmlText = html
                        navigationActions.navigateToPiasochnica(fileName)
                    })
                    viewModel.isProgressVisable = false
                    viewModel.isProgressVisable = false
                    isDialogNet = false
                }
            } else {
                viewModel.getFileCopyPostRequest(dirToFile = dirToFile, isProgressVisable = {
                    viewModel.isProgressVisable = it
                }) { text, fileName ->
                    val html = if (viewModel.isHTML) {
                        SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    } else {
                        SpannableStringBuilder(text)
                    }
                    viewModel.htmlText = html
                    navigationActions.navigateToPiasochnica(fileName)
                    viewModel.isProgressVisable = false
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
                viewModel.isHTML = resours.contains(".html")
                if (viewModel.isFilePiasochnicaExitst(resours, fileList)) {
                    coroutineScope.launch {
                        viewModel.isProgressVisable = true
                        viewModel.getPasochnicaFile(resours, result = { text ->
                            val html = if (viewModel.isHTML) {
                                SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                            } else {
                                SpannableStringBuilder(text)
                            }
                            viewModel.htmlText = html
                            navigationActions.navigateToPiasochnica(resours)
                        })
                        viewModel.isProgressVisable = false
                    }
                } else {
                    var text = file.readText()
                    text = if (Settings.dzenNoch) text.replace("#d00505", "#ff6666", true)
                    else text
                    val htmltext = if (viewModel.isHTML) SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    else SpannableStringBuilder(text)
                    viewModel.htmlText = htmltext
                    navigationActions.navigateToPiasochnica(resours)
                }
            }
        }
    }
    var dialogContextMenu by remember { mutableStateOf(false) }
    var dialogDelite by remember { mutableStateOf(false) }
    var dialogDeliteAllPiasochnica by remember { mutableStateOf(false) }
    var dialogSetFileName by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }
    if (dialogContextMenu) {
        DialogContextMenu(title = fileList[position].fileName, editTitle = stringResource(R.string.rename_file), onEdit = {
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
        val error = stringResource(R.string.error_ch)
        val noInternet = stringResource(R.string.no_internet)
        DialogDelite(title = stringResource(R.string.del_all_pasochnica), onConfirmation = {
            if (Settings.isNetworkAvailable(context)) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val list = Malitounik.referens.child("/admin/piasochnica").list(1000).await()
                        list.items.forEach {
                            it.delete().await()
                        }
                    } catch (_: Throwable) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                    fileList.clear()
                }
            } else {
                Toast.makeText(context, noInternet, Toast.LENGTH_SHORT).show()
            }
            dialogDeliteAllPiasochnica = false
        }) {
            dialogDeliteAllPiasochnica = false
        }
    }
    if (dialogDelite) {
        DialogDelite(title = fileList[position].fileName, onConfirmation = {
            val title = fileList[position].fileName
            val isSite = !title.contains("/admin/piasochnica")
            fileList.removeAt(position)
            viewModel.getFileUnlinkPostRequest(title, isSite)
            dialogDelite = false
        }) {
            dialogDelite = false
        }
    }
    if (dialogSetFileName) {
        DialogSetNameFile(fileList[position].fileName, setFileName = { fileName ->
            val oldFileName = fileList[position].fileName
            fileList[position].fileName = fileList[position].fileName.replace(oldFileName, fileName)
            viewModel.getFileRenamePostRequest(oldFileName, fileName, false)
            dialogSetFileName = false
        }) {
            dialogSetFileName = false
        }
    }
    val vybracFile = stringResource(R.string.vybrac_file)
    LaunchedEffect(PasochnicaList.pasochnicaAction) {
        when (PasochnicaList.pasochnicaAction) {
            PasochnicaList.FILE -> {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/html", "text/plain"))
                mActivityResultFile.launch(Intent.createChooser(intent, vybracFile))
                PasochnicaList.pasochnicaAction = PasochnicaList.NONE
            }

            PasochnicaList.WWW -> {
                isDialogNet = true
                PasochnicaList.pasochnicaAction = PasochnicaList.NONE
            }

            PasochnicaList.ADD -> {
                var resours = "newFile.html"
                var count = 1
                while (true) {
                    if (viewModel.isFilePiasochnicaExitst(resours, fileList)) {
                        resours = "newFile$count.html"
                        count++
                    } else break
                }
                viewModel.isHTML = true
                viewModel.htmlText = SpannableStringBuilder("")
                viewModel.crateNewFilePiasochnica(resours)
                navigationActions.navigateToPiasochnica(resours)
                PasochnicaList.pasochnicaAction = PasochnicaList.NONE
            }

            PasochnicaList.DEL_ALL_PASOCHNICA -> {
                if (fileList.isNotEmpty()) {
                    dialogDeliteAllPiasochnica = true
                }
                PasochnicaList.pasochnicaAction = PasochnicaList.NONE
            }
        }
    }
    Column {
        if (viewModel.isProgressVisable) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(fileList.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .combinedClickable(onClick = {
                            Settings.vibrate()
                            val fileName = fileList[index].fileName
                            coroutineScope.launch {
                                viewModel.isProgressVisable = true
                                viewModel.isHTML = fileName.contains(".html")
                                var isBackCopyExists = false
                                val dir = context.getExternalFilesDir("PiasochnicaBackCopy")
                                if (dir?.exists() != true) dir?.mkdir()
                                val list = dir?.list()
                                list?.forEach {
                                    if (fileName == it) {
                                        isBackCopyExists = true
                                        return@forEach
                                    }
                                }
                                if (isBackCopyExists) {
                                    val file = File(context.getExternalFilesDir("PiasochnicaBackCopy"), fileName)
                                    if (file.exists()) {
                                        var text = file.readText()
                                        text = if (Settings.dzenNoch) text.replace("#d00505", "#ff6666", true)
                                        else text
                                        val html = if (viewModel.isHTML) {
                                            SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                        } else {
                                            SpannableStringBuilder(text)
                                        }
                                        viewModel.htmlText = html
                                        navigationActions.navigateToPiasochnica(fileName)
                                    }
                                } else {
                                    viewModel.getPasochnicaFile(fileName, result = { text ->
                                        val html = if (viewModel.isHTML) {
                                            SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                        } else {
                                            SpannableStringBuilder(text)
                                        }
                                        viewModel.htmlText = html
                                        navigationActions.navigateToPiasochnica(fileName)
                                    })
                                }
                                viewModel.isProgressVisable = false
                            }
                        }, onLongClick = {
                            Settings.vibrate(true)
                            position = index
                            dialogContextMenu = true
                        }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp),
                        painter = painterResource(R.drawable.poiter),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = ""
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = fileList[index].fileName,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp,
                            lineHeight = Settings.fontInterface.sp * 1.15f
                        )
                        if (!fileList[index].isFileExists) {
                            Text(
                                text = stringResource(R.string.file_not_find),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun DialogNetFileExplorer(
    fileName: String = "", viewModel: Piasochnica, setFile: (String) -> Unit, onDismiss: () -> Unit
) {
    var dir by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.getDirListRequest(dir)
    }
    val context = LocalContext.current
    var dialogContextMenu by remember { mutableStateOf(false) }
    var dialogDelite by remember { mutableStateOf(false) }
    var dialogSetFileName by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }
    if (dialogContextMenu) {
        DialogContextMenu(title = dir + "/" + viewModel.fileList[position].title, editTitle = stringResource(R.string.rename_file), onEdit = {
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
        DialogDelite(title = dir + "/" + viewModel.fileList[position].title, onConfirmation = {
            val title = dir + "/" + viewModel.fileList[position].title
            val isSite = title.contains("/admin/piasochnica")
            viewModel.getFileUnlinkPostRequest(title, isSite)
            viewModel.fileList.removeAt(position)
            dialogDelite = false
        }) {
            dialogDelite = false
        }
    }
    if (dialogSetFileName) {
        DialogSetNameFile(viewModel.fileList[position].title, setFileName = { fileName ->
            val oldFileName = dir + "/" + viewModel.fileList[position].title
            val isSite = !oldFileName.contains("/admin/piasochnica")
            viewModel.getFileRenamePostRequest(oldFileName, "$dir/$fileName", isSite, update = {
                viewModel.getDirListRequest(dir)
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
        DialogFileExists(fileName = textFieldValueState, setFileName = {
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
                if (viewModel.isProgressVisable) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                val setfileName = stringResource(R.string.setfile_name)
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
                                Toast.makeText(context, setfileName, Toast.LENGTH_SHORT).show()
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
                    items(viewModel.fileList.size) { index ->
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .combinedClickable(onClick = {
                                    Settings.vibrate()
                                    when (viewModel.fileList[index].resources) {
                                        R.drawable.directory_up -> {
                                            val t1 = dir.lastIndexOf("/")
                                            dir = dir.take(t1)
                                            viewModel.getDirListRequest(dir)
                                        }

                                        R.drawable.directory_icon -> {
                                            dir = dir + "/" + viewModel.fileList[index].title
                                            viewModel.getDirListRequest(dir)
                                        }

                                        else -> {
                                            if (fileName.isNotEmpty()) textFieldValueState = viewModel.fileList[index].title
                                            else setFile(dir + "/" + viewModel.fileList[index].title)
                                        }
                                    }
                                }, onLongClick = {
                                    Settings.vibrate(true)
                                    if (!(viewModel.fileList[index].resources == R.drawable.directory_up || viewModel.fileList[index].resources == R.drawable.directory_icon)) {
                                        position = index
                                        dialogContextMenu = true
                                    }
                                }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(48.dp, 48.dp),
                                painter = painterResource(viewModel.fileList[index].resources),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                            Text(
                                text = viewModel.fileList[index].title,
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
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    if (fileName.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                Settings.vibrate()
                                if (textFieldValueState.isEmpty()) {
                                    Toast.makeText(context, setfileName, Toast.LENGTH_SHORT).show()
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
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
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
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
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

data class PaisochnicaFileList(var fileName: String, val updatedTimeMillis: Long, val isFileExists: Boolean)

data class MyNetFile(val resources: Int, val title: String)