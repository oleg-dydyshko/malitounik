package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaeNatatki(
    innerPadding: PaddingValues,
    sort: Int,
    addFile: Boolean,
    removeAllNatatki: Boolean,
    onDismissAddFile: () -> Unit,
) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val context = LocalContext.current
    val fileList = remember { mutableStateListOf<MaeNatatkiItem>() }
    LaunchedEffect(Unit) {
        File(context.filesDir.toString().plus("/Malitva")).walk().forEach {
            if (it.isFile) {
                val name = it.name
                val inputStream = FileReader(it)
                val reader = BufferedReader(inputStream)
                val res = reader.readText().split("<MEMA></MEMA>")
                inputStream.close()
                var lRTE: Long = 1
                var content = res[1]
                if (res[1].contains("<RTE></RTE>")) {
                    val start = res[1].indexOf("<RTE></RTE>")
                    content = res[1].substring(0, start)
                    val end = res[1].length
                    lRTE = res[1].substring(start + 11, end).toLong()
                }
                if (lRTE <= 1) {
                    lRTE = it.lastModified()
                }
                fileList.add(MaeNatatkiItem(lRTE, res[0], content, name))
            }
        }
    }
    var isNatatkaVisable by rememberSaveable { mutableStateOf(false) }
    var natatkaPosition by remember { mutableIntStateOf(0) }
    var removeNatatka by remember { mutableStateOf(false) }
    if (removeNatatka) {
        DialogDelite(
            stringResource(R.string.vybranoe_biblia_delite, fileList[natatkaPosition].title),
            onConfirmation = {
                val filedel = File(
                    context.filesDir.toString().plus("/Malitva/")
                        .plus(fileList[natatkaPosition].fileName)
                )
                if (filedel.exists()) filedel.delete()
                fileList.removeAt(natatkaPosition)
                removeNatatka = false
            },
            onDismissRequest = { removeNatatka = false }
        )
    }
    if (addFile) {
        DialogMyNatatli(
            "",
            "",
            onDismissRequest = {
                onDismissAddFile()
            },
            onConfirmation = { title, content ->
                write(context, title, content, "", true, onFileEdit = { fileName, time ->
                    fileList.add(MaeNatatkiItem(time, title, content, fileName))
                })
                onDismissAddFile()
            },
            isEditMode = true
        )
    }
    if (isNatatkaVisable) {
        DialogMyNatatli(
            fileList[natatkaPosition].title,
            fileList[natatkaPosition].content,
            onDismissRequest = {
                isNatatkaVisable = false
            },
            onConfirmation = { title, content ->
                write(
                    context,
                    title,
                    content,
                    fileList[natatkaPosition].fileName,
                    false,
                    onFileEdit = { fileName, time ->
                        fileList[natatkaPosition] = MaeNatatkiItem(time, title, content, fileName)
                    })
                isNatatkaVisable = false
            })
    }
    if (sort == Settings.SORT_BY_ABC) {
        fileList.sortBy { it.title }
    } else {
        fileList.sortByDescending { it.lastModified }
    }
    if (removeAllNatatki) {
        fileList.clear()
    }
    LazyColumn {
        items(fileList.size) { index ->
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .combinedClickable(
                        onClick = {
                            natatkaPosition = index
                            isNatatkaVisable = true
                        },
                        onLongClick = {
                            natatkaPosition = index
                            removeNatatka = true
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(12.dp, 12.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    fileList[index].title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
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

@Composable
fun DialogMyNatatli(
    title: String,
    content: String,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    isEditMode: Boolean = false
) {
    var editMode by rememberSaveable { mutableStateOf(isEditMode) }
    var editTitle by rememberSaveable { mutableStateOf(title) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = content,
                selection = TextRange(content.length)
            )
        )
    }
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            if (editMode) {
                TextField(
                    placeholder = { Text(stringResource(R.string.natatka_name), fontSize = Settings.fontInterface.sp) },
                    value = TextFieldValue(editTitle, selection = TextRange(editTitle.length)),
                    onValueChange = {
                        editTitle = it.text
                    }
                )
            } else {
                Text(editTitle, fontSize = Settings.fontInterface.sp)
            }
        },
        text = {
            if (editMode) {
                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        },
                    placeholder = { Text(stringResource(R.string.natatka), fontSize = Settings.fontInterface.sp) },
                    value = textFieldValueState,
                    onValueChange = {
                        textFieldValueState = it
                    },
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp)
                )
            } else {
                Text(textFieldValueState.text, fontSize = Settings.fontInterface.sp)
            }
        },
        onDismissRequest = {
            if (editMode) onConfirmation(editTitle, textFieldValueState.text)
            else onDismissRequest()
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (editMode) onDismissRequest()
                    else editMode = true
                }
            ) {
                if (editMode) Text(stringResource(R.string.cansel), fontSize = Settings.fontInterface.sp)
                else Text(stringResource(R.string.redagaktirovat), fontSize = Settings.fontInterface.sp)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editMode) onConfirmation(editTitle, textFieldValueState.text)
                    else onDismissRequest()
                }
            ) {
                if (editMode) Text(stringResource(R.string.save_sabytie), fontSize = Settings.fontInterface.sp)
                else Text(stringResource(R.string.close), fontSize = Settings.fontInterface.sp)
            }
        }
    )
}

private fun write(
    context: Context,
    title: String,
    content: String,
    fileName: String,
    isAddFile: Boolean,
    onFileEdit: (String, Long) -> Unit,
) {
    var newNazva = title
    var imiafile = fileName
    val gc = Calendar.getInstance()
    //val editMd5 = md5Sum("$nazva<MEMA></MEMA>$content")
    var i: Long = 1
    //if (md5sum != editMd5) {
    if (isAddFile) {
        val dir = File("${context.filesDir}/Malitva")
        if (!dir.exists()) dir.mkdir()
        while (true) {
            imiafile = "Mae_malitvy_$i"
            val fileN = File("${context.filesDir}/Malitva/$imiafile")
            if (fileN.exists()) {
                i++
            } else {
                break
            }
        }
    }
    if (newNazva == "") {
        val mun = context.resources.getStringArray(R.array.meciac_smoll)
        newNazva =
            gc[Calendar.DATE].toString() + " " + mun[gc[Calendar.MONTH]] + " " + gc[Calendar.YEAR] + " " + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
    }
    //val fileName = File("${context.filesDir}/Natatki.json")
    val file = File("${context.filesDir}/Malitva/$imiafile")
    /*fileName.writer().use {
        val gson = Gson()
        val type = TypeToken.getParameterized(
            java.util.ArrayList::class.java,
            MyNatatkiFiles::class.java
        ).type
        it.write(gson.toJson(MenuNatatki.myNatatkiFiles, type))
    }*/
    val time = gc.timeInMillis
    file.writer().use {
        it.write("$newNazva<MEMA></MEMA>$content<RTE></RTE>$time")
    }
    onFileEdit(imiafile, time)
    //val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //imm.hideSoftInputFromWindow(binding.EditText.windowToken, 0)
    //filename = file.name
    //redak = 2
    //}
}

/*private fun md5Sum(st: String): String {
    val messageDigest = MessageDigest.getInstance("MD5")
    messageDigest.reset()
    messageDigest.update(st.toByteArray())
    val digest = messageDigest.digest()
    val bigInt = BigInteger(1, digest)
    val md5Hex = StringBuilder(bigInt.toString(16))
    while (md5Hex.length < 32) {
        md5Hex.insert(0, "0")
    }
    return md5Hex.toString()
}*/

data class MaeNatatkiItem(
    val lastModified: Long,
    var title: String,
    val content: String,
    val fileName: String
)