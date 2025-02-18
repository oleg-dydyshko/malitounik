package by.carkva_gazeta.malitounik2

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VybranaeList(
    navigateToCytanniList: (String, Int, String) -> Unit = { _, _, _ -> },
    sorted: Int,
    removeAllVybranae: Boolean
) {
    var initVybranoe by remember { mutableStateOf(true) }
    val gson = Gson()
    val type =
        TypeToken.getParameterized(
            java.util.ArrayList::class.java,
            VybranoeData::class.java
        ).type
    val list = remember { ArrayList<VybranaeListData>() }
    if (initVybranoe) {
        val time = Calendar.getInstance().timeInMillis
        //list.add(VybranaeListData(time, "Літургія Яна", ArrayList(), "lit_jiaj_ksjh"))
        initVybranoe = false
        for (i in 1..5) {
            val vybranoeList = ArrayList<VybranoeData>()
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
                list.add(VybranaeListData(time, titlePerevod, vybranoeList, ""))
            }
        }
        //list.add(VybranaeListData(time, "Я вауацугш ушепоц", ArrayList(), "lit_jiaj_ksjh"))
        //list.add(VybranaeListData(time, "Літургія Васіля", ArrayList(), "lit_jiaj_ksjh"))
    }
    if (sorted == Settings.SORT_BY_ABC) {
        list.sortBy { it.title }
    } else {
        list.sortByDescending { it.id }
    }
    val collapsedState =
        remember(list) { list.map { true }.toMutableStateList() }
    val lazyColumnState = rememberLazyListState()
    val context = LocalContext.current
    var removeItem by remember { mutableIntStateOf(-1) }
    var removeItemBible by remember { mutableIntStateOf(-1) }
    var removeItemBibleAll by remember { mutableStateOf(false) }
    if (removeItem != -1) {
        val titleVybrenae = stringResource(
            R.string.vybranoe_biblia_delite,
            if (removeItemBible != -1) list[removeItem].listBible[removeItemBible].title + " " + (list[removeItem].listBible[removeItemBible].glava + 1)
            else list[removeItem].title
        )
        Dialog(
            title = titleVybrenae,
            onDismissRequest = {
                removeItem = -1
                removeItemBible = -1
                removeItemBibleAll = false
            },
            onConfirmation = {
                val perevod = list[removeItem].listBible[0].perevod
                val prevodName = when (perevod) {
                    Settings.PEREVODSEMUXI -> "biblia"
                    Settings.PEREVODBOKUNA -> "bokuna"
                    Settings.PEREVODCARNIAUSKI -> "carniauski"
                    Settings.PEREVODNADSAN -> "nadsan"
                    Settings.PEREVODSINOIDAL -> "sinaidal"
                    else -> "biblia"
                }
                val file = File("${context.filesDir}/vybranoe_${prevodName}.json")
                if (removeItemBible != -1) {
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
                    list.removeAt(removeItem)
                }
                if (removeItemBibleAll) {
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
            if (dataItem.recourse == "") {
                item(key = "header_$i") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
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
                            Icons.Default.run {
                                if (collapsed)
                                    KeyboardArrowDown
                                else
                                    KeyboardArrowUp
                            },
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
                            color = MaterialTheme.colorScheme.secondary
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
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                            Text(
                                dataItem.listBible[index].title + " " + (dataItem.listBible[index].glava + 1),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.secondary
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
                                },
                                onLongClick = {
                                    removeItemBible = -1
                                    removeItem = i
                                }
                            )
                            .padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp, 12.dp),
                            painter = painterResource(R.drawable.krest),
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
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun Dialog(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.delete), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.remove))
        },
        text = {
            Text(text = title, fontSize = 18.sp)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.ok), fontSize = 18.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cansel), fontSize = 18.sp)
            }
        }
    )
}

data class VybranaeListData(
    val id: Long,
    val title: String,
    val listBible: ArrayList<VybranoeData>,
    val recourse: String
)