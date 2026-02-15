@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.Collator
import java.util.Locale

class VybranaeListViewModel : ViewModel() {
    val lazyColumnBibleState = ArrayList<LazyListState>()
    val vybranaeListTitleBible = SnapshotStateList<VybranaeListData>()
    val vybranaeListTitleBibleIndex = ArrayList<Int>()
    val vybranaeListFile = SnapshotStateList<VybranaeDataAll>()

    init {
        initVybranae()
    }

    fun getPrevodName(perevod: String): String {
        return when (perevod) {
            Settings.PEREVODSEMUXI -> "biblia"
            Settings.PEREVODBOKUNA -> "bokuna"
            Settings.PEREVODCARNIAUSKI -> "carniauski"
            Settings.PEREVODCATOLIK -> "catolik"
            Settings.PEREVODNADSAN -> "nadsan"
            Settings.PEREVODSINOIDAL -> "sinaidal"
            Settings.PEREVODNEWAMERICANBIBLE -> "english"
            else -> "biblia"
        }
    }

    fun initVybranae() {
        val context = Malitounik.applicationContext()
        val gson = Gson()
        val typeBibleList = TypeToken.getParameterized(
            ArrayList::class.java, VybranaeData::class.java
        ).type
        val typeFileList = TypeToken.getParameterized(
            ArrayList::class.java, VybranaeDataAll::class.java
        ).type
        vybranaeListTitleBible.clear()
        lazyColumnBibleState.clear()
        vybranaeListFile.clear()
        for (i in 1..7) {
            val vybranoeList = SnapshotStateList<VybranaeData>()
            val titlePerevod = when (i.toString()) {
                Settings.PEREVODSEMUXI -> context.getString(R.string.title_biblia2)
                Settings.PEREVODSINOIDAL -> context.getString(R.string.bsinaidal2)
                Settings.PEREVODNADSAN -> context.getString(R.string.title_psalter)
                Settings.PEREVODBOKUNA -> context.getString(R.string.title_biblia_bokun2)
                Settings.PEREVODCARNIAUSKI -> context.getString(R.string.title_biblia_charniauski2)
                Settings.PEREVODCATOLIK -> context.getString(R.string.title_biblia_catolik2)
                Settings.PEREVODNEWAMERICANBIBLE -> context.getString(R.string.perevod_new_american_bible_2)
                else -> context.getString(R.string.title_biblia2)
            }
            val file = File("${context.filesDir}/vybranoe_${getPrevodName(i.toString())}.json")
            if (file.exists()) {
                vybranoeList.addAll(gson.fromJson(file.readText(), typeBibleList))
                vybranaeListTitleBible.add(VybranaeListData(vybranoeList[0].id, titlePerevod, vybranoeList, ""))
                lazyColumnBibleState.add(LazyListState())
                vybranaeListTitleBibleIndex.add(vybranaeListTitleBible.size - 1)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            vybranaeListTitleBible.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            vybranaeListTitleBible.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
        val file2 = File("${context.filesDir}/vybranoe_all.json")
        if (file2.exists()) {
            vybranaeListFile.addAll(gson.fromJson(file2.readText(), typeFileList))
        }
    }
}

@Composable
fun VybranaeList(
    navigateToCytanniList: (String, Int, String) -> Unit, navigateToBogaslujbovyia: (String, String) -> Unit, sort: Int, removeAllVybranae: Boolean, innerPadding: PaddingValues, viewModel: VybranaeListViewModel = viewModel()
) {
    LaunchedEffect(sort) {
        when (sort) {
            Settings.SORT_BY_ABC -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                    viewModel.vybranaeListFile.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
                } else {
                    viewModel.vybranaeListFile.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
                }
                for (i in viewModel.vybranaeListTitleBible.indices) {
                    viewModel.vybranaeListTitleBible[i].listBible.sortWith(
                        compareBy({
                            it.knigaText
                        }, {
                            it.glava
                        })
                    )
                }
            }

            Settings.SORT_BY_TIME -> {
                viewModel.vybranaeListFile.sortByDescending { it.id }
                for (i in viewModel.vybranaeListTitleBible.indices) {
                    viewModel.vybranaeListTitleBible[i].listBible.sortByDescending { it.id }
                }
            }

            Settings.SORT_BY_CUSTOM -> {
                viewModel.initVybranae()
            }
        }
    }
    if (removeAllVybranae) {
        val context = LocalContext.current
        for (perevod in 1..7) {
            val prevodName = when (perevod.toString()) {
                Settings.PEREVODSEMUXI -> "biblia"
                Settings.PEREVODBOKUNA -> "bokuna"
                Settings.PEREVODCARNIAUSKI -> "carniauski"
                Settings.PEREVODCATOLIK -> "catolik"
                Settings.PEREVODNADSAN -> "nadsan"
                Settings.PEREVODSINOIDAL -> "sinaidal"
                Settings.PEREVODNEWAMERICANBIBLE -> "english"
                else -> "biblia"
            }
            val file = File("${context.filesDir}/vybranoe_${prevodName}.json")
            if (file.exists()) file.delete()
        }
        val file = File("${context.filesDir}/vybranoe_all.json")
        if (file.exists()) file.delete()
        viewModel.vybranaeListFile.clear()
        viewModel.vybranaeListTitleBible.clear()
    }
    VybranaeListBox(innerPadding, viewModel, navigateToCytanniList = { chytanne, position, perevod2 ->
        navigateToCytanniList(chytanne, position, perevod2)
    }, navigateToBogaslujbovyia = { title, resourse ->
        navigateToBogaslujbovyia(title, resourse)
    })
}

@Composable
fun VybranaeListBox(innerPadding: PaddingValues, viewModel: VybranaeListViewModel, navigateToCytanniList: (String, Int, String) -> Unit, navigateToBogaslujbovyia: (String, String) -> Unit) {
    BoxWithConstraints {
        VybranoeListBox(innerPadding, viewModel, navigateToCytanniList = { chytanne, position, perevod2 ->
            navigateToCytanniList(chytanne, position, perevod2)
        }, navigateToBogaslujbovyia = { title, resourse ->
            navigateToBogaslujbovyia(title, resourse)
        })
    }
}

@Composable
fun BoxWithConstraintsScope.VybranoeListBox(innerPadding: PaddingValues, viewModel: VybranaeListViewModel, navigateToCytanniList: (String, Int, String) -> Unit, navigateToBogaslujbovyia: (String, String) -> Unit) {
    val parentConstraints = this.constraints
    val context = LocalContext.current
    val gson = Gson()
    val typeBibleList = TypeToken.getParameterized(
        ArrayList::class.java, VybranaeData::class.java
    ).type
    val typeFileList = TypeToken.getParameterized(
        ArrayList::class.java, VybranaeDataAll::class.java
    ).type
    var removeItem by remember { mutableIntStateOf(0) }
    var removeItemBible by remember { mutableIntStateOf(0) }
    var removeBible by remember { mutableStateOf(false) }
    var removeBibleAll by remember { mutableStateOf(false) }
    var removeResourse by remember { mutableStateOf(false) }
    if (removeBible) {
        DialogDelite(
            title = stringResource(R.string.vybranoe_biblia_delite, viewModel.vybranaeListTitleBible[removeItem].listBible[removeItemBible].title + " " + (viewModel.vybranaeListTitleBible[removeItem].listBible[removeItemBible].glava + 1)), onConfirmation = {
                val file = File("${context.filesDir}/vybranoe_${viewModel.getPrevodName(viewModel.vybranaeListTitleBible[removeItem].listBible[removeItemBible].perevod)}.json")
                viewModel.vybranaeListTitleBible[removeItem].listBible.removeAt(removeItemBible)
                if (viewModel.vybranaeListTitleBible[removeItem].listBible.isEmpty() && file.exists()) {
                    viewModel.vybranaeListTitleBible[removeItem].title = ""
                    file.delete()
                } else {
                    file.writer().use {
                        it.write(gson.toJson(viewModel.vybranaeListTitleBible[removeItem].listBible, typeBibleList))
                    }
                }
                removeBible = false
            }, onDismiss = {
                removeBible = false
            }
        )
    }
    if (removeBibleAll) {
        DialogDelite(
            title = stringResource(R.string.vybranoe_biblia_delite, viewModel.vybranaeListTitleBible[removeItem].title), onConfirmation = {
                val file = File("${context.filesDir}/vybranoe_${viewModel.getPrevodName(viewModel.vybranaeListTitleBible[removeItem].listBible[0].perevod)}.json")
                if (file.exists()) {
                    file.delete()
                }
                viewModel.vybranaeListTitleBible[removeItem].title = ""
                removeBibleAll = false
            }, onDismiss = {
                removeBibleAll = false
            }
        )
    }
    if (removeResourse) {
        DialogDelite(
            title = stringResource(R.string.vybranoe_biblia_delite, viewModel.vybranaeListFile[removeItem].title), onConfirmation = {
                viewModel.vybranaeListFile.removeAt(removeItem)
                val file = File("${context.filesDir}/vybranoe_all.json")
                if (viewModel.vybranaeListFile.isEmpty() && file.exists()) {
                    file.delete()
                } else {
                    file.writer().use {
                        it.write(gson.toJson(viewModel.vybranaeListFile, typeFileList))
                    }
                }
                removeResourse = false
            }, onDismiss = {
                removeResourse = false
            }
        )
    }
    val lazyColumnFileState = rememberLazyListState()
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val dragDropFileState = rememberDragDropState(lazyColumnFileState) { fromIndex, toIndex ->
        if (fromIndex < viewModel.vybranaeListFile.size && toIndex < viewModel.vybranaeListFile.size) {
            viewModel.vybranaeListFile.apply { add(toIndex, removeAt(fromIndex)) }
            val localFile = File("${context.filesDir}/vybranoe_all.json")
            localFile.writer().use {
                it.write(gson.toJson(viewModel.vybranaeListFile, typeFileList))
            }
            k.edit {
                putInt("sortedVybranae", Settings.SORT_BY_CUSTOM)
            }
        }
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        LazyColumn(
            modifier = Modifier
                .dragContainer(dragDropFileState)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(maxHeight = parentConstraints.maxHeight)
                    )

                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, 0)
                    }
                },
            state = lazyColumnFileState
        ) {
            itemsIndexed(viewModel.vybranaeListFile, key = { _, item -> item.id }) { index, item ->
                Column {
                    DraggableItem(dragDropFileState, index) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .combinedClickable(
                                    onClick = {
                                        Settings.vibrate()
                                        navigateToBogaslujbovyia(item.title, item.resource)
                                    },
                                    onLongClick = {
                                        Settings.vibrate(true)
                                        removeItem = index
                                        removeResourse = true
                                    }
                                )
                                .padding(start = 10.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                            )
                            Text(
                                item.title, modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                                            stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntOffset.VisibilityThreshold
                                        )
                                    )
                                    .fillMaxSize()
                                    .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                            )
                        }
                        if (viewModel.vybranaeListFile.size > 1) {
                            Icon(
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 5.dp, end = 15.dp, bottom = 10.dp)
                                    .size(24.dp),
                                painter = painterResource(R.drawable.menu_move), tint = Divider, contentDescription = ""
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
        for (i in viewModel.vybranaeListTitleBible.indices) {
            val dragDropBibleState = rememberDragDropState(viewModel.lazyColumnBibleState[i]) { fromIndex, toIndex ->
                if (fromIndex < viewModel.vybranaeListTitleBible[i].listBible.size && toIndex < viewModel.vybranaeListTitleBible[i].listBible.size) {
                    viewModel.vybranaeListTitleBible[i].listBible.apply { add(toIndex, removeAt(fromIndex)) }
                    val file = File("${context.filesDir}/vybranoe_${viewModel.getPrevodName(viewModel.vybranaeListTitleBible[i].listBible[0].perevod)}.json")
                    file.writer().use {
                        it.write(gson.toJson(viewModel.vybranaeListTitleBible[i].listBible, typeBibleList))
                    }
                    k.edit {
                        putInt("sortedVybranae", Settings.SORT_BY_CUSTOM)
                    }
                }
            }
            var collapsed by remember { mutableStateOf(AppNavGraphState.setItemsValue(viewModel.vybranaeListTitleBible[i].title, true)) }
            if (viewModel.vybranaeListTitleBible[i].title.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                Settings.vibrate()
                                AppNavGraphState.setItemsValue(viewModel.vybranaeListTitleBible[i].title)
                                collapsed = !collapsed
                            },
                            onLongClick = {
                                Settings.vibrate(true)
                                removeItem = i
                                removeBibleAll = true
                            }
                        )
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = if (collapsed) painterResource(R.drawable.keyboard_arrow_down)
                        else painterResource(R.drawable.keyboard_arrow_up),
                        contentDescription = "",
                        tint = Divider,
                    )
                    Text(
                        viewModel.vybranaeListTitleBible[i].title, modifier = Modifier
                            .padding(10.dp)
                            .weight(1f), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
                HorizontalDivider()
            } else {
                collapsed = true
            }
            if (!collapsed) {
                LazyColumn(
                    modifier = Modifier
                        .dragContainer(dragDropBibleState)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(
                                constraints.copy(maxHeight = parentConstraints.maxHeight)
                            )

                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(0, 0)
                            }
                        },
                    state = viewModel.lazyColumnBibleState[i]
                ) {
                    itemsIndexed(viewModel.vybranaeListTitleBible[i].listBible, key = { _, item -> item.id }) { index, item ->
                        Column {
                            DraggableItem(dragDropBibleState, index) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .combinedClickable(
                                            onClick = {
                                                Settings.vibrate()
                                                val newList = StringBuilder()
                                                for (r in 0 until viewModel.vybranaeListTitleBible[i].listBible.size) {
                                                    val char = if (r == viewModel.vybranaeListTitleBible[i].listBible.size - 1) ""
                                                    else ";"
                                                    newList.append(viewModel.vybranaeListTitleBible[i].listBible[r].knigaText + " " + (viewModel.vybranaeListTitleBible[i].listBible[r].glava + 1) + char)
                                                }
                                                navigateToCytanniList(
                                                    newList.toString(), index, item.perevod
                                                )
                                            },
                                            onLongClick = {
                                                Settings.vibrate(true)
                                                removeItem = i
                                                removeItemBible = index
                                                removeBible = true
                                            }
                                        )
                                        .padding(start = 30.dp), verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                                    )
                                    Text(
                                        item.title + " " + (item.glava + 1), modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                                if (viewModel.vybranaeListTitleBible[i].listBible.size > 1) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(top = 10.dp, start = 5.dp, end = 15.dp, bottom = 10.dp)
                                            .size(24.dp),
                                        painter = painterResource(R.drawable.menu_move), tint = Divider, contentDescription = ""
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
        Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
    }
}

@Composable
fun DialogDelite(
    title: String, onConfirmation: () -> Unit, onDismiss: () -> Unit
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
                    text = stringResource(R.string.remove).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = title, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
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
                        Text(stringResource(R.string.sabytie_no), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onConfirmation()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.sabytie_yes), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

data class VybranaeListData(
    val id: Long, var title: String, val listBible: SnapshotStateList<VybranaeData>, val recourse: String
)