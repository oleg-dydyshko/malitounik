@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.Collator
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VybranaeList(
    navigateToCytanniList: (String, Int, String) -> Unit = { _, _, _ -> },
    navigateToBogaslujbovyia: (String, String) -> Unit = { _, _ -> },
    sorted: Int,
    removeAllVybranae: Boolean,
    innerPadding: PaddingValues
) {
    var initVybranoe by remember { mutableStateOf(true) }
    val gson = Gson()
    val type =
        TypeToken.getParameterized(
            ArrayList::class.java,
            VybranaeData::class.java
        ).type
    val type2 =
        TypeToken.getParameterized(
            ArrayList::class.java,
            VybranaeDataAll::class.java
        ).type
    val list = remember { SnapshotStateList<VybranaeListData>() }
    val vybranoeList2 = remember { SnapshotStateList<VybranaeDataAll>() }
    if (initVybranoe) {
        initVybranoe = false
        for (i in 1..5) {
            val vybranoeList = SnapshotStateList<VybranaeData>()
            val prevodName = when (i.toString()) {
                Settings.PEREVODSEMUXI -> "biblia"
                Settings.PEREVODBOKUNA -> "bokuna"
                Settings.PEREVODCARNIAUSKI -> "carniauski"
                Settings.PEREVODNADSAN -> "nadsan"
                Settings.PEREVODSINOIDAL -> "sinaidal"
                else -> "biblia"
            }
            val titlePerevod = when (i.toString()) {
                Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia2)
                Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal2)
                Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
                Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun2)
                Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski2)
                else -> stringResource(R.string.title_biblia2)
            }
            val file = File("${LocalContext.current.filesDir}/vybranoe_${prevodName}.json")
            if (file.exists()) {
                vybranoeList.addAll(gson.fromJson(file.readText(), type))
                list.add(VybranaeListData(vybranoeList[0].id, titlePerevod, vybranoeList, ""))
            }
        }
        val file2 = File("${LocalContext.current.filesDir}/vybranoe_all.json")
        if (file2.exists()) {
            vybranoeList2.addAll(gson.fromJson(file2.readText(), type2))
            for (e in vybranoeList2.indices) {
                list.add(
                    VybranaeListData(
                        vybranoeList2[e].id,
                        vybranoeList2[e].title,
                        SnapshotStateList(),
                        vybranoeList2[e].resource
                    )
                )
            }
        }
    }
    if (sorted == Settings.SORT_BY_ABC) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
    } else {
        list.sortByDescending { it.id }
    }
    val collapsedState =
        remember(list) { list.map { AppNavGraphState.setItemsValue(it.title, true) }.toMutableStateList() }
    val lazyColumnState = rememberLazyListState()
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var removeItem by remember { mutableIntStateOf(-1) }
    var removeItemBible by remember { mutableIntStateOf(-1) }
    var removeItemBibleAll by remember { mutableStateOf(false) }
    var removeResourse by remember { mutableStateOf("-1") }
    if (removeItem != -1) {
        val titleVybrenae = stringResource(
            R.string.vybranoe_biblia_delite,
            if (removeItemBible != -1) list[removeItem].listBible[removeItemBible].title + " " + (list[removeItem].listBible[removeItemBible].glava + 1)
            else list[removeItem].title
        )
        DialogDelite(
            title = titleVybrenae,
            onDismiss = {
                removeItem = -1
                removeItemBible = -1
                removeItemBibleAll = false
            },
            onConfirmation = {
                val perevod = if (list[removeItem].recourse.isEmpty()) list[removeItem].listBible[0].perevod
                else Settings.PEREVODSEMUXI
                if (removeItemBible != -1) {
                    val prevodName = when (perevod) {
                        Settings.PEREVODSEMUXI -> "biblia"
                        Settings.PEREVODBOKUNA -> "bokuna"
                        Settings.PEREVODCARNIAUSKI -> "carniauski"
                        Settings.PEREVODNADSAN -> "nadsan"
                        Settings.PEREVODSINOIDAL -> "sinaidal"
                        else -> "biblia"
                    }
                    val file = File("${context.filesDir}/vybranoe_${prevodName}.json")
                    list[removeItem].listBible.removeAt(removeItemBible)
                    if (list[removeItem].listBible.isEmpty() && file.exists()) {
                        list.removeAt(removeItem)
                        file.delete()
                    } else {
                        file.writer().use {
                            it.write(gson.toJson(list[removeItem].listBible, type))
                        }
                    }
                    removeItemBible = -1
                } else {
                    var index = -1
                    for (i in vybranoeList2.indices) {
                        if (removeResourse == vybranoeList2[i].resource) {
                            index = i
                            break
                        }
                    }
                    if (index != -1) {
                        vybranoeList2.removeAt(index)
                        val file2 = File("${context.filesDir}/vybranoe_all.json")
                        if (vybranoeList2.isEmpty() && file2.exists()) {
                            file2.delete()
                        } else {
                            file2.writer().use {
                                it.write(gson.toJson(vybranoeList2, type2))
                            }
                        }
                    }
                    list.removeAt(removeItem)
                }
                if (removeItemBibleAll) {
                    val prevodName = when (perevod) {
                        Settings.PEREVODSEMUXI -> "biblia"
                        Settings.PEREVODBOKUNA -> "bokuna"
                        Settings.PEREVODCARNIAUSKI -> "carniauski"
                        Settings.PEREVODNADSAN -> "nadsan"
                        Settings.PEREVODSINOIDAL -> "sinaidal"
                        else -> "biblia"
                    }
                    val file = File("${context.filesDir}/vybranoe_${prevodName}.json")
                    if (file.exists()) {
                        file.delete()
                    }
                }
                removeItem = -1
            }
        )
    }
    if (removeAllVybranae) {
        list.clear()
    }
    LazyColumn(
        state = lazyColumnState
    ) {
        list.forEachIndexed { i, dataItem ->
            val collapsed = collapsedState[i]
            if (dataItem.recourse.isEmpty()) {
                item(key = "header_$i") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    AppNavGraphState.setItemsValue(dataItem.title)
                                    collapsedState[i] = !collapsed
                                },
                                onLongClick = {
                                    removeItemBibleAll = true
                                    removeItem = i
                                }
                            )
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = if (collapsed)
                                painterResource(R.drawable.keyboard_arrow_down)
                            else
                                painterResource(R.drawable.keyboard_arrow_up),
                            contentDescription = "",
                            tint = Divider,
                        )
                        Text(
                            dataItem.title,
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        visibilityThreshold = IntOffset.VisibilityThreshold
                                    )
                                )
                                .padding(10.dp)
                                .weight(1f),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
                if (!collapsed) {
                    if (sorted == Settings.SORT_BY_ABC) {
                        dataItem.listBible.sortWith(
                            compareBy({
                                it.knigaText
                            }, {
                                it.glava
                            })
                        )
                    } else {
                        dataItem.listBible.sortByDescending { it.id }
                    }
                    items(dataItem.listBible.size) { index ->
                        Row(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = {
                                        val newList = StringBuilder()
                                        for (r in 0 until dataItem.listBible.size) {
                                            val char = if (r == dataItem.listBible.size - 1) ""
                                            else ";"
                                            newList.append(dataItem.listBible[r].knigaText + " " + (dataItem.listBible[r].glava + 1) + char)
                                        }
                                        navigateToCytanniList(
                                            newList.toString(),
                                            index,
                                            dataItem.listBible[index].perevod
                                        )
                                    },
                                    onLongClick = {
                                        removeItemBible = index
                                        removeItem = i
                                    }
                                )
                                .padding(start = 30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                            Text(
                                dataItem.listBible[index].title + " " + (dataItem.listBible[index].glava + 1),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                        HorizontalDivider()
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    navigateToBogaslujbovyia(dataItem.title, dataItem.recourse)
                                },
                                onLongClick = {
                                    removeItemBible = -1
                                    removeItem = i
                                    removeResourse = dataItem.recourse
                                }
                            )
                            .padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            dataItem.title,
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        visibilityThreshold = IntOffset.VisibilityThreshold
                                    )
                                )
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}

@Composable
fun DialogDelite(
    title: String,
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
            Column {
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
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.sabytie_no), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        shape = MaterialTheme.shapes.small
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
    val id: Long,
    val title: String,
    val listBible: SnapshotStateList<VybranaeData>,
    val recourse: String
)