@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.Collator
import java.util.Calendar
import java.util.Locale

class DragDropState
internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit,
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    internal val scrollChannel = Channel<Float>()

    private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
    private var draggingItemInitialOffset by mutableIntStateOf(0)
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
        } ?: 0f

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == draggingItemIndex }

    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    internal var previousItemOffset = Animatable(0f)
        private set

    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo.firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }?.also {
            draggingItemIndex = it.index
            draggingItemInitialOffset = it.offset
        }
    }

    internal fun onDragInterrupted() {
        if (draggingItemIndex != null) {
            previousIndexOfDraggedItem = draggingItemIndex
            val startOffset = draggingItemOffset
            scope.launch {
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    0f,
                    spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f),
                )
                previousIndexOfDraggedItem = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemIndex = null
        draggingItemInitialOffset = 0
    }

    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset.y

        val draggingItem = draggingItemLayoutInfo ?: return
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffset = startOffset + draggingItem.size
        val middleOffset = startOffset + (endOffset - startOffset) / 2f

        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            middleOffset.toInt() in item.offset..item.offsetEnd && draggingItem.index != item.index
        }
        if (targetItem != null) {
            if (draggingItem.index == state.firstVisibleItemIndex || targetItem.index == state.firstVisibleItemIndex) {
                state.requestScrollToItem(
                    state.firstVisibleItemIndex,
                    state.firstVisibleItemScrollOffset,
                )
            }
            onMove.invoke(draggingItem.index, targetItem.index)
            draggingItemIndex = targetItem.index
        } else {
            val overscroll = when {
                draggingItemDraggedDelta > 0 -> (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)

                draggingItemDraggedDelta < 0 -> (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)

                else -> 0f
            }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }
        }
    }

    private val LazyListItemInfo.offsetEnd: Int
        get() = this.offset + this.size
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaeNatatki(
    innerPadding: PaddingValues, sort: Int, viewModel: SearchBibleViewModel
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
                    val t1 = name.lastIndexOf("_")
                    val id = name.substring(t1 + 1).toInt()
                    viewModel.fileList.add(MaeNatatkiItem(id, lRTE, res[0], content, name))
                }
            }
        }
    }
    LaunchedEffect(sort) {
        when (sort) {
            Settings.SORT_BY_ABC -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                    viewModel.fileList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
                } else {
                    viewModel.fileList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
                }
            }

            Settings.SORT_BY_TIME -> {
                viewModel.fileList.sortByDescending { it.lastModified }
            }

            Settings.SORT_BY_CUSTOM -> {
                val file = File("${context.filesDir}/MaeNatatkiList.json")
                val gson = Gson()
                val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
                val list = ArrayList<String>()
                list.addAll(gson.fromJson(file.readText(), type))
                viewModel.fileList.clear()
                for (i in list.indices) {
                    val file = File(context.filesDir.toString().plus("/Malitva/" + list[i]))
                    if (file.exists()) {
                        val name = file.name
                        val inputStream = FileReader(file)
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
                            lRTE = file.lastModified()
                        }
                        val t1 = name.lastIndexOf("_")
                        val id = name.substring(t1 + 1).toInt()
                        viewModel.fileList.add(MaeNatatkiItem(id, lRTE, res[0], content, name))
                    }
                }
            }
        }
    }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    if (viewModel.isDeliteNatatka) {
        DialogDelite(
            stringResource(R.string.vybranoe_biblia_delite, viewModel.fileList[viewModel.natatkaPosition].title), onConfirmation = {
                val filedel = File(
                    context.filesDir.toString().plus("/Malitva/").plus(viewModel.fileList[viewModel.natatkaPosition].fileName)
                )
                if (filedel.exists()) filedel.delete()
                viewModel.fileList.removeAt(viewModel.natatkaPosition)
                viewModel.isEditMode = false
                viewModel.natatkaVisable = false
                viewModel.isDeliteNatatka = false
            }) { viewModel.isDeliteNatatka = false }
    }
    if (viewModel.saveFileNatatki) {
        write(context, viewModel.textFieldValueState.text, viewModel.textFieldValueNatatkaContent.text, if (viewModel.addFileNatatki) "" else viewModel.fileList[viewModel.natatkaPosition].fileName, viewModel.addFileNatatki, onFileEdit = { title, fileName, time ->
            if (viewModel.addFileNatatki) {
                val t1 = fileName.lastIndexOf("_")
                val id = fileName.substring(t1 + 1).toInt()
                viewModel.fileList.add(MaeNatatkiItem(id, time, title, viewModel.textFieldValueNatatkaContent.text, fileName))
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
    val lazyListState = rememberLazyListState()
    val dragDropState = rememberDragDropState(lazyListState) { fromIndex, toIndex ->
        if (fromIndex < viewModel.fileList.size && toIndex < viewModel.fileList.size) {
            viewModel.fileList.apply { add(toIndex, removeAt(fromIndex)) }
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
            val saveList = ArrayList<String>()
            for (i in viewModel.fileList.indices) {
                saveList.add(viewModel.fileList[i].fileName)
            }
            val localFile = File("${context.filesDir}/MaeNatatkiList.json")
            localFile.writer().use {
                it.write(gson.toJson(saveList, type))
            }
            k.edit {
                putInt("natatki_sort", Settings.SORT_BY_CUSTOM)
            }
        }
    }
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
                        }, placeholder = { Text(stringResource(R.string.natatka), fontSize = Settings.fontInterface.sp) }, value = viewModel.textFieldValueNatatkaContent, onValueChange = {
                        viewModel.textFieldValueNatatkaContent = it
                    }, textStyle = TextStyle(fontSize = Settings.fontInterface.sp)
                )
            } else {
                SelectionContainer {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp), text = viewModel.textFieldValueNatatkaContent.text, fontSize = fontSize.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.dragContainer(dragDropState), state = lazyListState) {
            itemsIndexed(viewModel.fileList, key = { _, item -> item.id }) { index, item ->
                Column {
                    DraggableItem(dragDropState, index) {
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clickable {
                                    viewModel.natatkaPosition = index
                                    viewModel.natatkaVisable = true
                                    viewModel.textFieldValueState = TextFieldValue(viewModel.fileList[viewModel.natatkaPosition].title)
                                    viewModel.textFieldValueNatatkaContent = TextFieldValue(viewModel.fileList[viewModel.natatkaPosition].content)
                                }, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                            )
                            Text(
                                item.title, modifier = Modifier
                                    .weight(1f)
                                    .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                            )
                            if (viewModel.fileList.size > 1) {
                                Icon(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(24.dp),
                                    painter = painterResource(R.drawable.menu_move), tint = MaterialTheme.colorScheme.secondary, contentDescription = ""
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
            }
        }
    }
}

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState, onMove: (Int, Int) -> Unit
): DragDropState {
    val scope = rememberCoroutineScope()
    val state = remember(lazyListState) {
        DragDropState(state = lazyListState, onMove = onMove, scope = scope)
    }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
    val vibrate = Malitounik.applicationContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val pattern = longArrayOf(1000)
    return pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset = offset)
            },
            onDragStart = { offset ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrate.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                } else {
                    vibrate.vibrate(pattern, 0)
                }
                dragDropState.onDragStart(offset)
            },
            onDragEnd = { dragDropState.onDragInterrupted() },
            onDragCancel = { dragDropState.onDragInterrupted() },
        )
    }
}

@Composable
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(isDragging: Boolean) -> Unit,
) {
    val dragging = index == dragDropState.draggingItemIndex
    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .graphicsLayer { translationY = dragDropState.draggingItemOffset }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.previousItemOffset.value
            }
    } else {
        Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
    }
    Row(modifier = modifier.then(draggingModifier), verticalAlignment = Alignment.CenterVertically) { content(dragging) }
}

@Composable
fun DialogHelpCustomSort(onDismiss: (Boolean) -> Unit) {
    var isCheck by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDismiss(isCheck) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.sort_custom).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(text = stringResource(R.string.sort_custom_help), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCheck, onCheckedChange = {
                        isCheck = !isCheck
                    })
                    Text(text = stringResource(R.string.not_show), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss(isCheck) }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
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
        newNazva = gc[Calendar.DATE].toString() + " " + mun[gc[Calendar.MONTH]] + " " + gc[Calendar.YEAR] + " " + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
    }
    val file = File("${context.filesDir}/Malitva/$imiafile")
    val time = gc.timeInMillis
    file.writer().use {
        it.write("$newNazva<MEMA></MEMA>$content<RTE></RTE>$time")
    }
    onFileEdit(newNazva, imiafile, time)
}

data class MaeNatatkiItem(
    val id: Int, val lastModified: Long, var title: String, var content: String, val fileName: String
)