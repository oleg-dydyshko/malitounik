@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.views.HtmlText
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
    viewModel: SearchBibleViewModel
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    LaunchedEffect(Unit) {
        if (viewModel.fileList.isEmpty()) {
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
                    viewModel.fileList.add(MaeNatatkiItem(lRTE, res[0], content, name))
                }
            }
        }
    }
    if (sort == Settings.SORT_BY_ABC) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            viewModel.fileList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            viewModel.fileList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
    } else {
        viewModel.fileList.sortByDescending { it.lastModified }
    }
    var removeNatatka by remember { mutableStateOf(false) }
    var dialogContextMenu by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    if (removeNatatka) {
        DialogDelite(
            stringResource(R.string.vybranoe_biblia_delite, viewModel.fileList[viewModel.natatkaPosition].title),
            onConfirmation = {
                val filedel = File(
                    context.filesDir.toString().plus("/Malitva/")
                        .plus(viewModel.fileList[viewModel.natatkaPosition].fileName)
                )
                if (filedel.exists()) filedel.delete()
                viewModel.fileList.removeAt(viewModel.natatkaPosition)
                removeNatatka = false
            }
        ) { removeNatatka = false }
    }
    if (viewModel.saveFileNatatki) {
        write(context, viewModel.textFieldValueState.text, viewModel.textFieldValueNatatkaContent.text, if (viewModel.addFileNatatki) "" else viewModel.fileList[viewModel.natatkaPosition].fileName, viewModel.addFileNatatki, onFileEdit = { title, fileName, time ->
            if (viewModel.addFileNatatki) {
                viewModel.fileList.add(MaeNatatkiItem(time, title, viewModel.textFieldValueNatatkaContent.text, fileName))
            } else {
                viewModel.fileList[viewModel.natatkaPosition].title = title
                viewModel.fileList[viewModel.natatkaPosition].content = viewModel.textFieldValueNatatkaContent.text
            }
            val sortedNatatki = k.getInt("natatki_sort", Settings.SORT_BY_ABC)
            if (sortedNatatki == Settings.SORT_BY_ABC) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                    viewModel.fileList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
                } else {
                    viewModel.fileList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
                }
            } else {
                viewModel.fileList.sortByDescending { it.lastModified }
            }
            viewModel.saveFileNatatki = false
            viewModel.addFileNatatki = false
            viewModel.isEditMode = false
            viewModel.natatkaVisable = false
        })
    }
    if (dialogContextMenu) {
        DialogContextMenu(
            viewModel.fileList[viewModel.natatkaPosition].title,
            onEdit = {
                dialogContextMenu = false
                viewModel.isEditMode = true
                viewModel.natatkaVisable = true
                viewModel.textFieldValueState = TextFieldValue(viewModel.fileList[viewModel.natatkaPosition].title)
                viewModel.textFieldValueNatatkaContent = TextFieldValue(viewModel.fileList[viewModel.natatkaPosition].content)
            },
            onDelite = {
                dialogContextMenu = false
                removeNatatka = true
            }
        ) {
            dialogContextMenu = false
        }
    }
    val lazyListState = rememberLazyListState()
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    if (viewModel.natatkaVisable) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            if (viewModel.isEditMode) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        },
                    placeholder = { Text(stringResource(R.string.natatka), fontSize = Settings.fontInterface.sp) },
                    value = viewModel.textFieldValueNatatkaContent,
                    onValueChange = {
                        viewModel.textFieldValueNatatkaContent = it
                    },
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp)
                )
            } else {
                SelectionContainer {
                    HtmlText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = viewModel.textFieldValueNatatkaContent.text, fontSize = fontSize.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    } else {
        LazyColumn(state = lazyListState) {
            items(viewModel.fileList.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .combinedClickable(onClick = {
                            viewModel.natatkaPosition = index
                            viewModel.natatkaVisable = true
                            viewModel.textFieldValueState = TextFieldValue(viewModel.fileList[viewModel.natatkaPosition].title)
                            viewModel.textFieldValueNatatkaContent = TextFieldValue(viewModel.fileList[viewModel.natatkaPosition].content)
                        }, onLongClick = {
                            viewModel.natatkaPosition = index
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
                    Text(
                        viewModel.fileList[index].title,
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
}

private fun write(
    context: Context,
    title: String,
    content: String,
    fileName: String,
    isAddFile: Boolean,
    onFileEdit: (String, String, Long) -> Unit,
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
    onFileEdit(newNazva, imiafile, time)
}

data class MaeNatatkiItem(
    val lastModified: Long,
    var title: String,
    var content: String,
    val fileName: String
)