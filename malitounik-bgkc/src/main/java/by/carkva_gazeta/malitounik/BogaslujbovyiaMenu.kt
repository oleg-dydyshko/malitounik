@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import java.text.Collator
import java.util.Calendar
import java.util.Locale

fun mineiaMesichnaiaMounth(day: Int, isPasha: Boolean): Int {
    val year = Calendar.getInstance()[Calendar.YEAR]
    Settings.data.forEach {
        if (isPasha) {
            if (it[22].toInt() == day && it[3].toInt() == year) {
                return it[2].toInt()
            }
        } else {
            if (it[24].toInt() == day && it[3].toInt() == year) {
                return it[2].toInt()
            }
        }
    }
    return Calendar.JANUARY
}

fun getAllBogaslujbovyia(context: Context): SnapshotStateList<BogaslujbovyiaListData> {
    val listAll = SnapshotStateList<BogaslujbovyiaListData>()
    listAll.addAll(getBogaslujbovyia())
    listAll.addAll(getMalitvy())
    listAll.addAll(getAkafist())
    listAll.addAll(getLiturgikon())
    listAll.addAll(getChasaslou())
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
        val path = when (slugbovyiaTextuData.mineia) {
            SlugbovyiaTextu.MINEIA_KVETNAIA -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ КВЕТНАЯ"
            SlugbovyiaTextu.MINEIA_VIALIKI_POST_1 -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ ПОСНАЯ -> СЛУЖБЫ 1-ГА ТЫДНЯ ВЯЛІКАГА ПОСТУ"
            SlugbovyiaTextu.MINEIA_VIALIKI_POST_2 -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ ПОСНАЯ -> СЛУЖБЫ 2-ГА ТЫДНЯ ВЯЛІКАГА ПОСТУ"
            SlugbovyiaTextu.MINEIA_VIALIKI_POST_3 -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ ПОСНАЯ -> СЛУЖБЫ 3-ГА ТЫДНЯ ВЯЛІКАГА ПОСТУ"
            SlugbovyiaTextu.MINEIA_VIALIKI_POST_4 -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ ПОСНАЯ -> СЛУЖБЫ 4-ГА ТЫДНЯ ВЯЛІКАГА ПОСТУ"
            SlugbovyiaTextu.MINEIA_VIALIKI_POST_5 -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ ПОСНАЯ -> СЛУЖБЫ 5-ГА ТЫДНЯ ВЯЛІКАГА ПОСТУ"
            SlugbovyiaTextu.MINEIA_VIALIKI_POST_6 -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> ТРЫЁДЗЬ ПОСНАЯ -> СЛУЖБЫ 6-ГА ТЫДНЯ ВЯЛІКАГА ПОСТУ"
            SlugbovyiaTextu.MINEIA_VIALIKI_TYDZEN -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> СЛУЖБЫ ВЯЛІКАГА ТЫДНЯ"
            SlugbovyiaTextu.MINEIA_SVITLY_TYDZEN -> "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> ТРЫЁДЗЬ -> СЛУЖБЫ СЬВЕТЛАГА ТЫДНЯ"
            else -> {
                val mount = context.resources.getStringArray(R.array.meciac2)
                "БОГАСЛУЖБОВЫЯ ТЭКСТЫ -> МІНЭЯ МЕСЯЧНАЯ -> " + mount[mineiaMesichnaiaMounth(slugbovyiaTextuData.day, slugbovyiaTextuData.pasxa)]
            }
        }
        var nazvaSluzby = ". " + slugbovyiaTextu.getNazouSluzby(slugbovyiaTextuData.sluzba)
        if (slugbovyiaTextuData.title.contains(nazvaSluzby)) nazvaSluzby = ""
        listAll.add(BogaslujbovyiaListData(slugbovyiaTextuData.title + nazvaSluzby, slugbovyiaTextuData.resource, path))
    }
    return listAll
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BogaslujbovyiaMenu(
    navController: NavHostController, innerPadding: PaddingValues, menuItem: Int, searchBibleState: LazyListState, viewModel: SearchBibleViewModel
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
    val listAll = remember { mutableStateListOf<BogaslujbovyiaListData>() }
    val listItems = remember { mutableStateListOf<BogaslujbovyiaListData>() }
    val filteredItems = remember { mutableStateListOf<BogaslujbovyiaListData>() }
    LaunchedEffect(Unit) {
        listAll.addAll(getAllBogaslujbovyia(context))
        listItems.addAll(
            when (menuItem) {
                Settings.MENU_BOGASLUJBOVYIA -> getBogaslujbovyia()
                Settings.MENU_MALITVY -> getMalitvy()
                Settings.MENU_AKAFIST -> getAkafist()
                Settings.MENU_CHASASLOU -> getChasaslou()
                Settings.MENU_LITURGIKON -> getLiturgikon()
                else -> SnapshotStateList()
            }
        )
        if (menuItem == Settings.MENU_BOGASLUJBOVYIA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                listAll.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
                listItems.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
                folderList.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it })
            } else {
                listAll.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
                listItems.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
                folderList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it })
            }
        }
        filteredItems.addAll(listItems)
    }
    LaunchedEffect(AppNavGraphState.searchSettings) {
        if (AppNavGraphState.searchSettings) {
            viewModel.search(context, isBogaslujbovyiaSearch = true)
            AppNavGraphState.searchSettings = false
        }
    }
    LaunchedEffect(viewModel.textFieldValueState.text, viewModel.searchText, viewModel.searchFullText) {
        if (viewModel.searchFullText) {
            viewModel.search(context, isBogaslujbovyiaSearch = true)
            if (AppNavGraphState.searchSettings) AppNavGraphState.searchSettings = false
        } else {
            filteredItems.clear()
            if (viewModel.searchText) {
                if (viewModel.textFieldValueState.text.isNotEmpty()) {
                    val filterList = listAll.filter { it.title.contains(viewModel.textFieldValueState.text, ignoreCase = true) }
                    filteredItems.addAll(filterList)
                } else {
                    filteredItems.addAll(listAll)
                }
            } else {
                filteredItems.addAll(listItems)
            }
        }

    }
    var isRegistr by remember { mutableStateOf(k.getBoolean("pegistrbukv", true)) }
    var isDakladnaeSupadzenne by remember { mutableIntStateOf(k.getInt("slovocalkam", 0)) }
    if (viewModel.searchFullText) {
        if (viewModel.searchSettings) {
            ModalBottomSheet(
                scrimColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                onDismissRequest = {
                    viewModel.searchSettings = false
                }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            Settings.vibrate()
                            isRegistr = !isRegistr
                            k.edit {
                                putBoolean("pegistrbukv", isRegistr)
                            }
                            AppNavGraphState.searchSettings = true
                        }) {
                        Checkbox(
                            checked = !isRegistr,
                            onCheckedChange = {
                                Settings.vibrate()
                                isRegistr = !isRegistr
                                k.edit {
                                    putBoolean("pegistrbukv", isRegistr)
                                }
                                AppNavGraphState.searchSettings = true
                            }
                        )
                        Text(
                            stringResource(R.string.registr),
                            fontSize = Settings.fontInterface.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            Settings.vibrate()
                            isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                            else 0
                            k.edit {
                                putInt("slovocalkam", isDakladnaeSupadzenne)
                            }
                            AppNavGraphState.searchSettings = true
                        }) {
                        Checkbox(
                            checked = isDakladnaeSupadzenne == 1,
                            onCheckedChange = {
                                Settings.vibrate()
                                isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                                else 0
                                k.edit {
                                    putInt("slovocalkam", isDakladnaeSupadzenne)
                                }
                                AppNavGraphState.searchSettings = true
                            }
                        )
                        Text(
                            stringResource(R.string.dakladnae_supadzenne),
                            fontSize = Settings.fontInterface.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        Column {
            if (viewModel.isProgressVisable) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                text = stringResource(R.string.searh_sviatyia_result, viewModel.searchList.size),
                fontStyle = FontStyle.Italic,
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            LazyColumn(
                Modifier.nestedScroll(nestedScrollConnection),
                state = searchBibleState
            ) {
                items(viewModel.searchList.size) { index ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                Settings.vibrate()
                                viewModel.oldTextFieldValueState = viewModel.textFieldValueState.text
                                AppNavGraphState.searchBogaslujbovyia = viewModel.textFieldValueState.text
                                navigationActions.navigateToBogaslujbovyia(viewModel.searchList[index].title, viewModel.searchList[index].resource)
                            },
                        text = viewModel.searchList[index].text.toAnnotatedString(),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                    HorizontalDivider()
                }
                item {
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            if (!viewModel.searchText && menuItem == Settings.MENU_BOGASLUJBOVYIA) {
                items(folderList.size) { index ->
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                Settings.vibrate()
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
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
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
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            Settings.vibrate()
                            if (menuItem != Settings.MENU_MAE_NATATKI) {
                                navigationActions.navigateToBogaslujbovyia(
                                    filteredItems[index].title, filteredItems[index].resource
                                )
                            }
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                        )
                        Text(
                            text = filteredItems[index].title, modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                    }
                    if (viewModel.searchText) {
                        Text(
                            text = filteredItems[index].path, modifier = Modifier
                                .padding(start = 15.dp, end = 10.dp, bottom = 10.dp), color = SecondaryText, fontSize = Settings.fontInterface.sp
                        )
                    }
                }
                HorizontalDivider()
            }
            if (!viewModel.searchText && menuItem == Settings.MENU_LITURGIKON) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                Settings.vibrate()
                                navigationActions.navigateToMalitvyListAll(
                                    "МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ", Settings.MENU_MALITVY_PASLIA_PRYCHASCIA
                                )
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                        )
                        Text(
                            "МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ", modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
            }
            if (!viewModel.searchText && menuItem == Settings.MENU_CHASASLOU) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                Settings.vibrate()
                                navigationActions.navigateToMalitvyListAll(
                                    "ВЯЧЭРНЯ", Settings.MENU_VIACHERNIA
                                )
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                        )
                        Text(
                            "ВЯЧЭРНЯ", modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
            }
            if (!viewModel.searchText && menuItem == Settings.MENU_MALITVY) {
                item {
                    val title = stringResource(R.string.prynagodnyia)
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                Settings.vibrate()
                                navigationActions.navigateToMalitvyListAll(
                                    title, Settings.MENU_MALITVY_PRYNAGODNYIA
                                )
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                        )
                        Text(
                            title.uppercase(), modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    val title = stringResource(R.string.ruzanec)
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                Settings.vibrate()
                                navigationActions.navigateToMalitvyListAll(
                                    title, Settings.MENU_MALITVY_RUJANEC
                                )
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
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
}

fun getChasaslou(): SnapshotStateList<BogaslujbovyiaListData> {
    val list = SnapshotStateList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Гадзіна 1", "bogashlugbovya/kan_hadz_hadzina_1.html", "ЧАСАСЛОЎ"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Гадзіна 6", "bogashlugbovya/kan_hadz_hadzina_6.html", "ЧАСАСЛОЎ"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Гадзіна 6 у вялікі пост", "bogashlugbovya/kan_hadz_hadzina_6_vialiki_post.html", "ЧАСАСЛОЎ"
        )
    )
    list.add(BogaslujbovyiaListData("Павячэрніца малая", "bogashlugbovya/paviaczernica_malaja.html", "ЧАСАСЛОЎ"))
    list.add(BogaslujbovyiaListData("Павячэрніца вялікая", "bogashlugbovya/paviaczernica_vialikaja.html", "ЧАСАСЛОЎ"))
    list.add(BogaslujbovyiaListData("Ютрань нядзельная (у скароце)", "bogashlugbovya/jutran_niadzelnaja.html", "ЧАСАСЛОЎ"))
    return list
}

fun getLiturgikon(): SnapshotStateList<BogaslujbovyiaListData> {
    val list = SnapshotStateList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія сьв. Яна Залатавуснага", "bogashlugbovya/lit_jana_zalatavusnaha.html", "ЛІТУРГІКОН"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія ў Велікодны перыяд", "bogashlugbovya/lit_jan_zalat_vielikodn.html", "ЛІТУРГІКОН"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Боская Літургія сьв. Васіля Вялікага", "bogashlugbovya/lit_vasila_vialikaha.html", "ЛІТУРГІКОН"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Літургія раней асьвячаных дароў", "bogashlugbovya/lit_raniej_asviaczanych_darou.html", "ЛІТУРГІКОН"
        )
    )
    list.add(BogaslujbovyiaListData("Абедніца", "bogashlugbovya/abiednica.html", "ЛІТУРГІКОН"))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

fun getAkafist(): SnapshotStateList<BogaslujbovyiaListData> {
    val list = SnapshotStateList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Пра Акафіст", "bogashlugbovya/akafist0.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст Найсьвяцейшай Багародзіцы", "bogashlugbovya/akafist1.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст Маці Божай Нястомнай Дапамогі", "bogashlugbovya/akafist2.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст перад Жыровіцкай іконай Маці Божай", "bogashlugbovya/akafist3.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст у гонар мучанікаў Падляшскіх", "bogashlugbovya/akafist4.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст Імю Ісусаваму", "bogashlugbovya/akafist5.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст да Духа Сьвятога", "bogashlugbovya/akafist6.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст сьвятым Апосталам Пятру і Паўлу", "bogashlugbovya/akafist7.html", "АКАФІСТЫ"))
    list.add(BogaslujbovyiaListData("Акафіст сьвятому Язэпу, Абручніку Найсьвяцейшай Дзевы Марыі", "bogashlugbovya/akafist_praviednamu_jazepu.html", "АКАФІСТЫ"))
    list.add(
        BogaslujbovyiaListData(
            "Акафіст Росіцкім мучанікам блаславёным Антону і Юрыю", "bogashlugbovya/akafist_rosickim_muczanikam.html", "АКАФІСТЫ"
        )
    )
    return list
}

fun getRujanec(): SnapshotStateList<BogaslujbovyiaListData> {
    val list = SnapshotStateList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітвы на вяровіцы", "bogashlugbovya/ruzanec0.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    list.add(BogaslujbovyiaListData("Молімся на ружанцы", "bogashlugbovya/ruzanec2.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    list.add(BogaslujbovyiaListData("Разважаньні на Ружанец", "bogashlugbovya/ruzanec1.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    list.add(BogaslujbovyiaListData("Частка I. Радасныя таямніцы (пн, сб)", "bogashlugbovya/ruzanec3.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    list.add(BogaslujbovyiaListData("Частка II. Балесныя таямніцы (аўт, пт)", "bogashlugbovya/ruzanec4.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    list.add(BogaslujbovyiaListData("Частка III. Слаўныя таямніцы (ср, ндз)", "bogashlugbovya/ruzanec5.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    list.add(BogaslujbovyiaListData("Частка IV. Таямніцы сьвятла (чц)", "bogashlugbovya/ruzanec6.html", "МАЛІТВЫ -> РУЖАНЕЦ"))
    return list
}

fun getMalitvy(): SnapshotStateList<BogaslujbovyiaListData> {
    val list = SnapshotStateList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Ранішняя малітвы", "bogashlugbovya/malitvy_ranisznija.html", "МАЛІТВЫ"))
    list.add(BogaslujbovyiaListData("Вячэрнія малітвы", "bogashlugbovya/malitvy_viaczernija.html", "МАЛІТВЫ"))
    return list
}

fun getBogaslujbovyia(): SnapshotStateList<BogaslujbovyiaListData> {
    val list = SnapshotStateList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", "bogashlugbovya/nabazenstva_maci_bozaj_niast_dap.html", "БОГАСЛУЖБОВЫЯ ТЭКСТЫ"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малебны канон Найсьвяцейшай Багародзіцы", "bogashlugbovya/kanon_malebny_baharodzicy.html", "БОГАСЛУЖБОВЫЯ ТЭКСТЫ"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вялікі пакаянны канон сьвятога Андрэя Крыцкага", "bogashlugbovya/kanon_andreja_kryckaha.html", "БОГАСЛУЖБОВЫЯ ТЭКСТЫ"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малебен сьв. Кірылу і Мятоду, настаўнікам славянскім", "bogashlugbovya/malebien_kiryla_miatod.html", "БОГАСЛУЖБОВЫЯ ТЭКСТЫ"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вялікі пакаянны канон сьвятога Андрэя Крыцкага(у 4-х частках)", "bogashlugbovya/kanon_andreja_kryckaha_4_czastki.html", "БОГАСЛУЖБОВЫЯ ТЭКСТЫ"
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

data class BogaslujbovyiaListData(val title: String, val resource: String, val path: String = "")