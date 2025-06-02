@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.Collator
import java.util.Locale

class FilterBogaslujbovyiaListModel {
    private val items = ArrayList<BogaslujbovyiaListData>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: StateFlow<ArrayList<BogaslujbovyiaListData>> = _filteredItems

    fun addAllItemList(item: ArrayList<BogaslujbovyiaListData>) {
        items.addAll(item)
    }

    fun filterItem(search: String) {
        _filteredItems.value = items.filter { it.title.contains(search, ignoreCase = true) } as ArrayList<BogaslujbovyiaListData>
    }
}

@Composable
fun BogaslujbovyiaMenu(
    navController: NavHostController, innerPadding: PaddingValues, menuItem: Int, searchText: Boolean, search: String
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset, source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    val folderList = stringArrayResource(R.array.bogaslugbovyia_folder_list)
    val viewModel = FilterBogaslujbovyiaListModel()
    if (searchText) {
        val listAll = ArrayList<BogaslujbovyiaListData>()
        listAll.addAll(getBogaslujbovyia())
        listAll.addAll(getMalitvy())
        listAll.addAll(getAkafist())
        listAll.addAll(getRujanec())
        listAll.addAll(getAktoix())
        listAll.addAll(getViachernia())
        listAll.addAll(getTraparyKandakiShtodzennyia())
        listAll.addAll(getTraparyKandakiNiadzelnyia())
        listAll.addAll(getMalitvyPasliaPrychascia())
        listAll.addAll(getTrebnik())
        listAll.addAll(getMineiaAgulnaia())
        val slugbovyiaTextu = SlugbovyiaTextu()
        val listPast = slugbovyiaTextu.getAllSlugbovyiaTextu()
        listPast.forEach { slugbovyiaTextuData ->
            listAll.add(BogaslujbovyiaListData(slugbovyiaTextuData.title + ". " + slugbovyiaTextu.getNazouSluzby(slugbovyiaTextuData.sluzba), slugbovyiaTextuData.resource))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            listAll.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            listAll.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
        viewModel.addAllItemList(listAll)
        viewModel.filterItem(search)
    } else {
        val listAll = when (menuItem) {
            Settings.MENU_BOGASLUJBOVYIA -> getBogaslujbovyia()
            Settings.MENU_MALITVY -> getMalitvy()
            Settings.MENU_AKAFIST -> getAkafist()
            Settings.MENU_RUJANEC -> getRujanec()
            else -> ArrayList()
        }
        if (menuItem == Settings.MENU_BOGASLUJBOVYIA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                listAll.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
            } else {
                listAll.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
            }
        }
        viewModel.addAllItemList(listAll)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        folderList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it })
    } else {
        folderList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it })
    }
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        if (!searchText && menuItem == Settings.MENU_BOGASLUJBOVYIA) {
            items(folderList.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            if (folderList[index] == "АКТОІХ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_AKTOIX
                                )
                            }
                            if (folderList[index] == "ТРАПАРЫ І КАНДАКІ НЯДЗЕЛЬНЫЯ ВАСЬМІ ТОНАЎ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_TRAPARY_KANDAKI_NIADZELNYIA
                                )
                            }
                            if (folderList[index] == "МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_MALITVY_PASLIA_PRYCHASCIA
                                )
                            }
                            if (folderList[index] == "ТРЭБНІК") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_TREBNIK
                                )
                            }
                            if (folderList[index] == "МІНЭЯ АГУЛЬНАЯ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_MINEIA_AGULNAIA
                                )
                            }
                            if (folderList[index] == "МІНЭЯ МЕСЯЧНАЯ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH
                                )
                            }
                            if (folderList[index] == "ТРЫЁДЗЬ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_TRYEDZ
                                )
                            }
                            if (folderList[index] == "ЧАСАСЛОЎ") {
                                navigationActions.navigateToMalitvyListAll(
                                    folderList[index], Settings.MENU_CHASASLOU
                                )
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                    )
                    Text(
                        folderList[index].uppercase(), modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
                HorizontalDivider()
            }
        }
        items(filteredItems.size) { index ->
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        if (menuItem != Settings.MENU_MAE_NATATKI) {
                            navigationActions.navigateToBogaslujbovyia(
                                filteredItems[index].title, filteredItems[index].resurs
                            )
                        }
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                )
                Text(
                    filteredItems[index].title, modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                )
            }
            HorizontalDivider()
        }
        if (!searchText && menuItem == Settings.MENU_MALITVY) {
            item {
                val title = stringResource(R.string.prynagodnyia)
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            navigationActions.navigateToMalitvyListAll(
                                title, Settings.MENU_MALITVY_PRYNAGODNYIA
                            )
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                    )
                    Text(
                        title.uppercase(), modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
                HorizontalDivider()
            }
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

fun getAkafist(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Пра Акафіст", "bogashlugbovya/akafist0.html"))
    list.add(BogaslujbovyiaListData("Найсьвяцейшай Багародзіцы", "bogashlugbovya/akafist1.html"))
    list.add(BogaslujbovyiaListData("Маці Божай Нястомнай Дапамогі", "bogashlugbovya/akafist2.html"))
    list.add(BogaslujbovyiaListData("перад Жыровіцкай іконай", "bogashlugbovya/akafist3.html"))
    list.add(BogaslujbovyiaListData("у гонар Падляшскіх мучанікаў", "bogashlugbovya/akafist4.html"))
    list.add(BogaslujbovyiaListData("Імю Ісусаваму", "bogashlugbovya/akafist5.html"))
    list.add(BogaslujbovyiaListData("да Духа Сьвятога", "bogashlugbovya/akafist6.html"))
    list.add(BogaslujbovyiaListData("сьв. Апосталам Пятру і Паўлу", "bogashlugbovya/akafist7.html"))
    list.add(BogaslujbovyiaListData("Акафіст сьв. Язэпу", "bogashlugbovya/akafist_praviednamu_jazepu.html"))
    list.add(
        BogaslujbovyiaListData(
            "Акафіст Росіцкім мучанікам", "bogashlugbovya/akafist_rosickim_muczanikam.html"
        )
    )
    return list
}

fun getRujanec(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітвы на вяровіцы", "bogashlugbovya/ruzanec0.html"))
    list.add(BogaslujbovyiaListData("Молімся на ружанцы", "bogashlugbovya/ruzanec2.html"))
    list.add(BogaslujbovyiaListData("Разважаньні на Ружанец", "bogashlugbovya/ruzanec1.html"))
    list.add(BogaslujbovyiaListData("Частка I. Радасныя таямніцы (пн, сб)", "bogashlugbovya/ruzanec3.html"))
    list.add(BogaslujbovyiaListData("Частка II. Балесныя таямніцы (аўт, пт)", "bogashlugbovya/ruzanec4.html"))
    list.add(BogaslujbovyiaListData("Частка III. Слаўныя таямніцы (ср, ндз)", "bogashlugbovya/ruzanec5.html"))
    list.add(BogaslujbovyiaListData("Частка IV. Таямніцы сьвятла (чц)", "bogashlugbovya/ruzanec6.html"))
    return list
}

fun getMalitvy(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Ранішняя малітвы", "bogashlugbovya/malitvy_ranisznija.html"))
    list.add(BogaslujbovyiaListData("Вячэрнія малітвы", "bogashlugbovya/malitvy_viaczernija.html"))
    return list
}

fun getBogaslujbovyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія сьв. Яна Залатавуснага", "bogashlugbovya/lit_jana_zalatavusnaha.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія ў Велікодны перыяд", "bogashlugbovya/lit_jan_zalat_vielikodn.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія сьв. Васіля Вялікага", "bogashlugbovya/lit_vasila_vialikaha.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Літургія раней асьвячаных дароў", "bogashlugbovya/lit_raniej_asviaczanych_darou.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", "bogashlugbovya/nabazenstva_maci_bozaj_niast_dap.html"
        )
    )
    list.add(BogaslujbovyiaListData("Абедніца", "bogashlugbovya/abiednica.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малебны канон Найсьвяцейшай Багародзіцы", "bogashlugbovya/kanon_malebny_baharodzicy.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вялікі пакаянны канон сьвятога Андрэя Крыцкага", "bogashlugbovya/kanon_andreja_kryckaha.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малебен сьв. Кірылу і Мятоду, настаўнікам славянскім", "bogashlugbovya/malebien_kiryla_miatod.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вялікі пакаянны канон сьвятога Андрэя Крыцкага(у 4-х частках)", "bogashlugbovya/kanon_andreja_kryckaha_4_czastki.html"
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

data class BogaslujbovyiaListData(val title: String, val resurs: String)