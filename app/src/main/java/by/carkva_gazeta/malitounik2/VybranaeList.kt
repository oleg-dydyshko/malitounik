package by.carkva_gazeta.malitounik2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

@Composable
fun VybranaeList(
    navController: NavHostController,
    navigateToCytanniList: (String, String, String, Int) -> Unit = { _, _, _, _ -> }
) {
    val title = stringResource(R.string.MenuVybranoe)
    var initVybranoe by remember { mutableStateOf(true) }
    val gson = Gson()
    val type =
        TypeToken.getParameterized(
            java.util.ArrayList::class.java,
            VybranoeData::class.java
        ).type
    val list = remember { ArrayList<VybranaeListData>() }
    if (initVybranoe) {
        list.add(VybranaeListData("Літургія Яна", ArrayList(), "lit_jiaj_ksjh"))
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
                list.add(VybranaeListData(titlePerevod, vybranoeList, ""))
            }
        }
        list.add(VybranaeListData("Літургія Васіля", ArrayList(), "lit_jiaj_ksjh"))
    }
    val collapsedState =
        remember(list) { list.map { true }.toMutableStateList() }
    val lazyColumnState = rememberLazyListState()
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
                            .clickable {
                                collapsedState[i] = !collapsed
                            }
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
                                .padding(10.dp)
                                .weight(1f),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider()
                }
            }
            if (dataItem.recourse == "") {
                if (!collapsed) {
                    items(dataItem.listBible.size) { index ->
                        Row(
                            modifier = Modifier.padding(start = 20.dp),
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
                                    .padding(10.dp)
                                    .clickable {
                                        val newList = StringBuilder()
                                        for (r in 0 until dataItem.listBible.size) {
                                            val char = if (r == dataItem.listBible.size - 1) ""
                                            else ";"
                                            newList.append(dataItem.listBible[r].knigaText + " " + (dataItem.listBible[r].glava + 1) + char)
                                        }
                                        navigateToCytanniList(
                                            title,
                                            newList.toString(),
                                            dataItem.listBible[index].perevod,
                                            dataItem.listBible[index].count
                                        )
                                    },
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        HorizontalDivider()
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.padding(start = 10.dp),
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
                                .fillMaxSize()
                                .padding(10.dp)
                                .clickable {
                                },
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

data class VybranaeListData(
    val title: String,
    val listBible: ArrayList<VybranoeData>,
    val recourse: String
)