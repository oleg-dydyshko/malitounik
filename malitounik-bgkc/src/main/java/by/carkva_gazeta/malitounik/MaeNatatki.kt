@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.Collator
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaeNatatki(
    innerPadding: PaddingValues,
    sort: Int,
    addFile: Boolean,
    removeAllNatatki: Boolean,
    onDismissAddFile: () -> Unit,
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
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
    if (sort == Settings.SORT_BY_ABC) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            fileList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            fileList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
    } else {
        fileList.sortByDescending { it.lastModified }
    }
    var isNatatkaVisable by rememberSaveable { mutableStateOf(false) }
    var natatkaPosition by rememberSaveable { mutableIntStateOf(0) }
    var removeNatatka by remember { mutableStateOf(false) }
    var dialogContextMenu by remember { mutableStateOf(false) }
    var dialogContextMenuEdit by remember { mutableStateOf(false) }
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
            onDismiss = { removeNatatka = false }
        )
    }
    if (addFile) {
        DialogMyNatatki(
            "",
            "",
            onDismiss = {
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
    if (isNatatkaVisable && fileList.isNotEmpty()) {
        DialogMyNatatki(
            fileList[natatkaPosition].title,
            fileList[natatkaPosition].content,
            onDismiss = {
                isNatatkaVisable = false
                dialogContextMenuEdit = false
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
                dialogContextMenuEdit = false
            },
            isEditMode = dialogContextMenuEdit
        )
    }
    if (dialogContextMenu) {
        DialogContextMenu(
            fileList[natatkaPosition].title,
            onEdit = {
                dialogContextMenu = false
                isNatatkaVisable = true
                dialogContextMenuEdit = true
            },
            onDelite = {
                dialogContextMenu = false
                removeNatatka = true
            }
        ) {
            dialogContextMenu = false
        }
    }
    if (removeAllNatatki) {
        fileList.clear()
    }
    LazyColumn {
        items(fileList.size) { index ->
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .combinedClickable(onClick = {
                        natatkaPosition = index
                        isNatatkaVisable = true
                    }, onLongClick = {
                        natatkaPosition = index
                        dialogContextMenu = true
                    }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(5.dp, 5.dp),
                    painter = painterResource(R.drawable.poiter),
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
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}

@Composable
fun DialogMyNatatki(
    title: String,
    content: String,
    onDismiss: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    isEditMode: Boolean = false
) {
    var editMode by rememberSaveable { mutableStateOf(isEditMode) }
    var editTitle by rememberSaveable { mutableStateOf(title) }
    var editContent by rememberSaveable { mutableStateOf(content) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = editContent,
                selection = TextRange(editContent.length)
            )
        )
    }
    Dialog(onDismissRequest = {
        if (editMode) {
            if (editTitle.isNotEmpty() || textFieldValueState.text.isNotEmpty()) {
                onConfirmation(editTitle, textFieldValueState.text)
            }
        } else onDismiss()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                if (editMode) {
                    TextField(
                        textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                        placeholder = { Text(stringResource(R.string.natatka_name), fontSize = Settings.fontInterface.sp, color = PrimaryTextBlack) },
                        value = editTitle,
                        onValueChange = {
                            editTitle = it
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                            focusedTextColor = PrimaryTextBlack,
                            focusedIndicatorColor = PrimaryTextBlack,
                            unfocusedTextColor = PrimaryTextBlack,
                            unfocusedIndicatorColor = PrimaryTextBlack,
                            cursorColor = PrimaryTextBlack
                        )
                    )
                } else {
                    SelectionContainer {
                        Text(
                            text = editTitle, fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onTertiary)
                                .padding(10.dp), color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Column(modifier = Modifier
                    .imePadding()
                    .verticalScroll(rememberScrollState())) {
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
                                editContent = it.text
                            },
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp)
                        )
                    } else {
                        SelectionContainer {
                            Text(modifier = Modifier.padding(10.dp), text = textFieldValueState.text, fontSize = Settings.fontInterface.sp)
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
                                if (editMode) {
                                    onDismiss()
                                } else editMode = true
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            if (editMode) {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                                Text(stringResource(R.string.cansel), fontSize = 18.sp)
                            } else {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.edit), contentDescription = "")
                                Text(stringResource(R.string.redagaktirovat), fontSize = 18.sp)
                            }
                        }
                        TextButton(
                            onClick = {
                                if (editMode) {
                                    onConfirmation(editTitle, textFieldValueState.text)
                                } else onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            if (editMode) {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.save), contentDescription = "")
                                Text(stringResource(R.string.save_sabytie), fontSize = 18.sp)
                            } else {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                                Text(stringResource(R.string.close), fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
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
    var i: Long = 1
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
    val file = File("${context.filesDir}/Malitva/$imiafile")
    val time = gc.timeInMillis
    file.writer().use {
        it.write("$newNazva<MEMA></MEMA>$content<RTE></RTE>$time")
    }
    onFileEdit(imiafile, time)
}

data class MaeNatatkiItem(
    val lastModified: Long,
    var title: String,
    val content: String,
    val fileName: String
)