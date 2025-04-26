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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.Collator
import java.util.Locale

class FilterBogaslujbovyiaListModel : ViewModel() {
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
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
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
    val list = if (searchText) {
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
            listPast.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            listPast.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
        viewModel.addAllItemList(listAll)
        viewModel.filterItem(search)
        listAll
    } else {
        val listAll = when (menuItem) {
            Settings.MENU_BOGASLUJBOVYIA -> getBogaslujbovyia()
            Settings.MENU_MALITVY -> getMalitvy()
            Settings.MENU_AKAFIST -> getAkafist()
            Settings.MENU_RUJANEC -> getRujanec()
            else -> ArrayList()
        }
        viewModel.addAllItemList(listAll)
        listAll
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        folderList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it })
    } else {
        folderList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it })
    }
    if (menuItem == Settings.MENU_BOGASLUJBOVYIA) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
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
    list.add(BogaslujbovyiaListData("Пра Акафіст", R.raw.akafist0))
    list.add(BogaslujbovyiaListData("Найсьвяцейшай Багародзіцы", R.raw.akafist1))
    list.add(BogaslujbovyiaListData("Маці Божай Нястомнай Дапамогі", R.raw.akafist2))
    list.add(BogaslujbovyiaListData("перад Жыровіцкай іконай", R.raw.akafist3))
    list.add(BogaslujbovyiaListData("у гонар Падляшскіх мучанікаў", R.raw.akafist4))
    list.add(BogaslujbovyiaListData("Імю Ісусаваму", R.raw.akafist5))
    list.add(BogaslujbovyiaListData("да Духа Сьвятога", R.raw.akafist6))
    list.add(BogaslujbovyiaListData("сьв. Апосталам Пятру і Паўлу", R.raw.akafist7))
    list.add(BogaslujbovyiaListData("Акафіст сьв. Язэпу", R.raw.akafist_praviednamu_jazepu))
    list.add(
        BogaslujbovyiaListData(
            "Акафіст Росіцкім мучанікам", R.raw.akafist_rosickim_muczanikam
        )
    )
    return list
}

fun getRujanec(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітвы на вяровіцы", R.raw.ruzanec0))
    list.add(BogaslujbovyiaListData("Молімся на ружанцы", R.raw.ruzanec2))
    list.add(BogaslujbovyiaListData("Разважаньні на Ружанец", R.raw.ruzanec1))
    list.add(BogaslujbovyiaListData("Частка I. Радасныя таямніцы (пн, сб)", R.raw.ruzanec3))
    list.add(BogaslujbovyiaListData("Частка II. Балесныя таямніцы (аўт, пт)", R.raw.ruzanec4))
    list.add(BogaslujbovyiaListData("Частка III. Слаўныя таямніцы (ср, ндз)", R.raw.ruzanec5))
    list.add(BogaslujbovyiaListData("Частка IV. Таямніцы сьвятла (чц)", R.raw.ruzanec6))
    return list
}

fun getMalitvy(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Ранішняя малітвы", R.raw.malitvy_ranisznija))
    list.add(BogaslujbovyiaListData("Вячэрнія малітвы", R.raw.malitvy_viaczernija))
    return list
}

fun getBogaslujbovyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія сьв. Яна Залатавуснага", R.raw.lit_jana_zalatavusnaha
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія ў Велікодны перыяд", R.raw.lit_jan_zalat_vielikodn
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія сьв. Васіля Вялікага", R.raw.lit_vasila_vialikaha
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Літургія раней асьвячаных дароў", R.raw.lit_raniej_asviaczanych_darou
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", R.raw.nabazenstva_maci_bozaj_niast_dap
        )
    )
    list.add(BogaslujbovyiaListData("Абедніца", R.raw.abiednica))
    list.add(
        BogaslujbovyiaListData(
            "Малебны канон Найсьвяцейшай Багародзіцы", R.raw.kanon_malebny_baharodzicy
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вялікі пакаянны канон сьвятога Андрэя Крыцкага", R.raw.kanon_andreja_kryckaha
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малебен сьв. Кірылу і Мятоду, настаўнікам славянскім", R.raw.malebien_kiryla_miatod
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вялікі пакаянны канон сьвятога Андрэя Крыцкага(у 4-х частках)", R.raw.kanon_andreja_kryckaha_4_czastki
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

data class BogaslujbovyiaListData(val title: String, val resurs: Int)