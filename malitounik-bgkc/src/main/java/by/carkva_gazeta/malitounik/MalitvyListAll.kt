@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class FilterMalitvyPrynagodnyiaModel : ViewModel() {
    private val items = ArrayList<BogaslujbovyiaListData>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: StateFlow<ArrayList<BogaslujbovyiaListData>> = _filteredItems

    fun addItemList(item: ArrayList<BogaslujbovyiaListData>) {
        items.addAll(item)
    }

    fun clear() {
        items.clear()
    }

    fun sortWith() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            items.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            items.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
    }

    fun filterItem(search: String) {
        _filteredItems.value = items.filter { it.title.contains(search, ignoreCase = true) } as ArrayList<BogaslujbovyiaListData>
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MalitvyListAll(
    navController: NavHostController, title: String, menuItem: Int, subTitle: String = ""
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = !Settings.dzenNoch.value
        }
    }
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    val listPrynagodnyia = when (menuItem) {
        Settings.MENU_MALITVY_PRYNAGODNYIA -> {
            val arrayList = ArrayList<MineiaList>()
            arrayList.add(MineiaList(0, 0, stringResource(R.string.prynad_1), "", "", 0))
            arrayList.add(MineiaList(0, 0, stringResource(R.string.prynad_2), "", "", 0))
            arrayList.add(MineiaList(0, 0, stringResource(R.string.prynad_3), "", "", 0))
            arrayList.add(MineiaList(0, 0, stringResource(R.string.prynad_4), "", "", 0))
            arrayList.add(MineiaList(0, 0, stringResource(R.string.prynad_5), "", "", 0))
            arrayList.add(MineiaList(0, 0, stringResource(R.string.prynad_6), "", "", 0))
            arrayList
        }

        Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA -> {
            val list = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA)
            val arrayList = ArrayList<MineiaList>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.dayOfMonth == item.dayOfMonth && it.month == item.month) isAdd = false
                }
                if (isAdd) arrayList.add(item)
            }
            arrayList
        }

        Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA -> {
            val list = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA)
            val arrayList = ArrayList<MineiaList>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.dayOfMonth == item.dayOfMonth && it.month == item.month) isAdd = false
                }
                if (isAdd) arrayList.add(item)
            }
            arrayList
        }

        Settings.MENU_TRYEDZ_KVETNAIA -> {
            val list = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_KVETNAIA)
            val arrayList = ArrayList<MineiaList>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.dayOfMonth == item.dayOfMonth && it.month == item.month) isAdd = false
                }
                if (isAdd) {
                    arrayList.add(item)
                }
            }
            arrayList
        }

        Settings.MENU_MINEIA_MESIACHNAIA -> {
            val list = getMineiaMesiachnaia(subTitle)
            val arrayList = ArrayList<MineiaList>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.dayOfMonth == item.dayOfMonth && it.month == item.month) isAdd = false
                }
                if (isAdd) arrayList.add(item)
            }
            arrayList
        }

        else -> ArrayList()
    }
    val list = when (menuItem) {
        Settings.MENU_AKTOIX -> getAktoix()
        Settings.MENU_VIACHERNIA -> getViachernia()
        Settings.MENU_TRAPARY_KANDAKI_NIADZELNYIA -> getTraparyKandakiNiadzelnyia()
        Settings.MENU_TRAPARY_KANDAKI_SHTODZENNYIA -> getTraparyKandakiShtodzennyia()
        Settings.MENU_MALITVY_PASLIA_PRYCHASCIA -> getMalitvyPasliaPrychascia()
        Settings.MENU_TREBNIK -> getTrebnik()
        Settings.MENU_MINEIA_AGULNAIA -> getMineiaAgulnaia()
        Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH -> getMineiaMesiachnaiaMounth()
        Settings.MENU_TRYEDZ -> getTtyedz()
        Settings.MENU_TRYEDZ_POSNAIA -> getTtyedzPosnaia()
        Settings.MENU_MALITVY_RUJANEC -> getRujanec()
        Settings.MENU_TRYEDZ_POSNAIA_1 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_1)
        Settings.MENU_TRYEDZ_POSNAIA_2 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_2)
        Settings.MENU_TRYEDZ_POSNAIA_3 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_3)
        Settings.MENU_TRYEDZ_POSNAIA_4 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_4)
        Settings.MENU_TRYEDZ_POSNAIA_5 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_5)
        Settings.MENU_TRYEDZ_POSNAIA_6 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_6)
        else -> ArrayList()
    }
    var searchText by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var textFieldValueState by remember { mutableStateOf("") }
    var backPressHandled by remember { mutableStateOf(false) }
    val collapsedState = remember(listPrynagodnyia) { listPrynagodnyia.map { AppNavGraphState.setItemsValue(it.title, true) }.toMutableStateList() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!searchText) {
                        Column {
                            Text(
                                modifier = Modifier.clickable {
                                    maxLine.intValue = Int.MAX_VALUE
                                    coroutineScope.launch {
                                        delay(5000L)
                                        maxLine.intValue = 1
                                    }
                                }, text = title.uppercase(), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis, fontSize = Settings.fontInterface.sp
                            )
                            if (subTitle != "") {
                                Text(
                                    modifier = Modifier.clickable {
                                        maxLine.intValue = Int.MAX_VALUE
                                        coroutineScope.launch {
                                            delay(5000L)
                                            maxLine.intValue = 1
                                        }
                                    }, text = subTitle.uppercase(), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis, fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                    } else {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onGloballyPositioned {
                                    if (!textFieldLoaded) {
                                        focusRequester.requestFocus()
                                        textFieldLoaded = true
                                    }
                                }, value = textFieldValueState, onValueChange = { newText ->
                                var edit = newText
                                edit = edit.replace("и", "і")
                                edit = edit.replace("щ", "ў")
                                edit = edit.replace("И", "І")
                                edit = edit.replace("Щ", "Ў")
                                edit = edit.replace("ъ", "'")
                                textFieldValueState = edit
                            }, singleLine = true, leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.search), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                                )
                            }, trailingIcon = {
                                IconButton(onClick = { textFieldValueState = "" }) {
                                    Icon(
                                        painter = if (textFieldValueState.isNotEmpty()) painterResource(R.drawable.close) else painterResource(R.drawable.empty), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }, colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.onTertiary, unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary, focusedTextColor = PrimaryTextBlack, focusedIndicatorColor = PrimaryTextBlack, unfocusedIndicatorColor = PrimaryTextBlack, cursorColor = PrimaryTextBlack, unfocusedTextColor = PrimaryTextBlack
                            )
                        )
                    }
                }, navigationIcon = {
                    if (searchText) {
                        IconButton(onClick = {
                            searchText = false
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.close), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                            )
                        })
                    } else {
                        IconButton(onClick = {
                            if (!backPressHandled) {
                                backPressHandled = true
                                navController.popBackStack()
                            }
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                            )
                        })
                    }
                }, actions = {
                    if (!searchText && menuItem == Settings.MENU_MALITVY_PRYNAGODNYIA) {
                        IconButton({
                            searchText = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.search), tint = PrimaryTextBlack, contentDescription = ""
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }) { innerPadding ->
        if (searchText) {
            val viewModel: FilterMalitvyPrynagodnyiaModel = viewModel()
            viewModel.clear()
            viewModel.addItemList(getPrynagodnyia1())
            viewModel.addItemList(getPrynagodnyia2())
            viewModel.addItemList(getPrynagodnyia3())
            viewModel.addItemList(getPrynagodnyia4())
            viewModel.addItemList(getPrynagodnyia5())
            viewModel.addItemList(getPrynagodnyia6())
            viewModel.sortWith()
            val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
            viewModel.filterItem(textFieldValueState)
            PynagodnyiaList(filteredItems, navigationActions, innerPadding)
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        innerPadding.calculateStartPadding(LayoutDirection.Ltr), innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
                    )
                    .fillMaxSize()
            ) {
                if (menuItem == Settings.MENU_MALITVY_PRYNAGODNYIA || menuItem == Settings.MENU_MINEIA_MESIACHNAIA || menuItem == Settings.MENU_TRYEDZ_KVETNAIA || menuItem == Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA || menuItem == Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA) {
                    listPrynagodnyia.forEachIndexed { i, dataItem ->
                        val collapsed = collapsedState[i]
                        item(key = "header_$i") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        AppNavGraphState.setItemsValue(dataItem.title)
                                        collapsedState[i] = !collapsed
                                    }) {
                                Icon(
                                    painter = if (collapsed) painterResource(R.drawable.keyboard_arrow_down)
                                    else painterResource(R.drawable.keyboard_arrow_up),
                                    contentDescription = "",
                                    tint = Divider,
                                )
                                val title = if (menuItem == Settings.MENU_MINEIA_MESIACHNAIA) dataItem.dayOfMonth.toString()
                                else dataItem.title
                                Text(
                                    title.uppercase(), modifier = Modifier
                                        .animateItem(
                                            fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                                                stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntOffset.VisibilityThreshold
                                            )
                                        )
                                        .padding(10.dp)
                                        .weight(1f), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp, fontWeight = if (menuItem == Settings.MENU_MINEIA_MESIACHNAIA && Calendar.getInstance()[Calendar.DATE] == dataItem.dayOfMonth && Calendar.getInstance()[Calendar.MONTH] == dataItem.month) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            HorizontalDivider()
                        }
                        if (!collapsed) {
                            val subList = when (menuItem) {
                                Settings.MENU_MALITVY_PRYNAGODNYIA -> {
                                    val list = when (i) {
                                        0 -> getPrynagodnyia1()
                                        1 -> getPrynagodnyia2()
                                        2 -> getPrynagodnyia3()
                                        3 -> getPrynagodnyia4()
                                        4 -> getPrynagodnyia5()
                                        5 -> getPrynagodnyia6()
                                        else -> getPrynagodnyia1()
                                    }
                                    val arrayList = ArrayList<MineiaList>()
                                    list.forEach {
                                        arrayList.add(MineiaList(0, 0, it.title, it.title, it.resource, 0))
                                    }
                                    arrayList
                                }

                                Settings.MENU_MINEIA_MESIACHNAIA -> {
                                    val listMineiaList = getMineiaMesiachnaia(subTitle)
                                    val arrayList = ArrayList<MineiaList>()
                                    listMineiaList.forEach {
                                        if (dataItem.dayOfMonth == it.dayOfMonth && dataItem.month == it.month) {
                                            arrayList.add(it)
                                        }
                                    }
                                    arrayList
                                }

                                Settings.MENU_TRYEDZ_KVETNAIA -> {
                                    val listMineiaList = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_KVETNAIA)
                                    val arrayList = ArrayList<MineiaList>()
                                    listMineiaList.forEach {
                                        if (dataItem.dayOfMonth == it.dayOfMonth && dataItem.month == it.month) {
                                            arrayList.add(it)
                                        }
                                    }
                                    arrayList
                                }

                                Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA -> {
                                    val listMineiaList = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA)
                                    val arrayList = ArrayList<MineiaList>()
                                    listMineiaList.forEach {
                                        if (dataItem.dayOfMonth == it.dayOfMonth && dataItem.month == it.month) {
                                            arrayList.add(it)
                                        }
                                    }
                                    arrayList
                                }

                                Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA -> {
                                    val listMineiaList = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA)
                                    val arrayList = ArrayList<MineiaList>()
                                    listMineiaList.forEach {
                                        if (dataItem.dayOfMonth == it.dayOfMonth && dataItem.month == it.month) {
                                            arrayList.add(it)
                                        }
                                    }
                                    arrayList
                                }

                                else -> {
                                    ArrayList()
                                }
                            }
                            items(subList.size) { index ->
                                Row(
                                    modifier = Modifier
                                        .padding(start = 30.dp)
                                        .clickable {
                                            navigationActions.navigateToBogaslujbovyia(
                                                subList[index].title, subList[index].resource
                                            )
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                                    )
                                    val titleSluzba = if (menuItem == Settings.MENU_MINEIA_MESIACHNAIA) subList[index].title + ". " + subList[index].titleSluzba
                                    else subList[index].titleSluzba
                                    Text(
                                        titleSluzba, modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                } else {
                    items(list.size) { index ->
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    when (menuItem) {
                                        Settings.MENU_TRYEDZ_POSNAIA -> {
                                            when (list[index].resource) {
                                                "1" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA_1, list[index].title
                                                    )
                                                }

                                                "2" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA_2, list[index].title
                                                    )
                                                }

                                                "3" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA_3, list[index].title
                                                    )
                                                }

                                                "4" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA_4, list[index].title
                                                    )
                                                }

                                                "5" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA_5, list[index].title
                                                    )
                                                }

                                                "6" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA_6, list[index].title
                                                    )
                                                }
                                            }
                                        }

                                        Settings.MENU_TRYEDZ -> {
                                            when (list[index].resource) {
                                                "10" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_POSNAIA, list[index].title
                                                    )
                                                }

                                                "11" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA, list[index].title
                                                    )
                                                }

                                                "12" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA, list[index].title
                                                    )
                                                }

                                                "13" -> {
                                                    navigationActions.navigateToMalitvyListAll(
                                                        title, Settings.MENU_TRYEDZ_KVETNAIA, list[index].title
                                                    )
                                                }
                                            }
                                        }

                                        Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH -> {
                                            navigationActions.navigateToMalitvyListAll(
                                                title, Settings.MENU_MINEIA_MESIACHNAIA, list[index].title
                                            )
                                        }

                                        else -> {
                                            navigationActions.navigateToBogaslujbovyia(
                                                list[index].title, list[index].resource
                                            )
                                        }
                                    }
                                }, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = if (menuItem == Settings.MENU_TRYEDZ || menuItem == Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH || menuItem == Settings.MENU_TRYEDZ_POSNAIA) Modifier.size(17.dp, 17.dp) else Modifier.size(5.dp, 5.dp), painter = if (menuItem == Settings.MENU_TRYEDZ || menuItem == Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH || menuItem == Settings.MENU_TRYEDZ_POSNAIA) painterResource(R.drawable.folder)
                                else painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                            )
                            Text(
                                text = if (menuItem == Settings.MENU_TRYEDZ || menuItem == Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH || menuItem == Settings.MENU_TRYEDZ_POSNAIA) list[index].title.uppercase() else list[index].title, modifier = Modifier.padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontWeight = if (menuItem == Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH) {
                                    if (Calendar.getInstance()[Calendar.MONTH] == index) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                                } else {
                                    FontWeight.Normal
                                }, fontSize = Settings.fontInterface.sp
                            )
                            if (menuItem == Settings.MENU_TRAPARY_KANDAKI_NIADZELNYIA) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(end = 20.dp)
                                            .align(Alignment.End)
                                            .clickable {
                                                val uri = "https://soundcloud.com/24dwbqqpu9sk/trapar-${index + 1}?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing".toUri()
                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                context.startActivity(intent)
                                            }, painter = painterResource(R.drawable.play_arrow), contentDescription = "", tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
                if (menuItem == Settings.MENU_MINEIA_AGULNAIA) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clickable {
                                    navigationActions.navigateToMalitvyListAll(
                                        "ТРАПАРЫ І КАНДАКІ ШТОДЗЁННЫЯ - НА КОЖНЫ ДЗЕНЬ ТЫДНЯ", Settings.MENU_TRAPARY_KANDAKI_SHTODZENNYIA
                                    )
                                }, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(17.dp, 17.dp), painter = painterResource(R.drawable.folder), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                            )
                            Text(
                                "ТРАПАРЫ І КАНДАКІ ШТОДЗЁННЫЯ - НА КОЖНЫ ДЗЕНЬ ТЫДНЯ", modifier = Modifier
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
}

@Composable
fun PynagodnyiaList(prynagodnyaList: ArrayList<BogaslujbovyiaListData>, navigationActions: AppNavigationActions, innerPadding: PaddingValues) {
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
    LazyColumn(
        modifier = Modifier
            .padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr), innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
            )
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        items(
            prynagodnyaList.size, key = { index -> prynagodnyaList[index].title + index }) { index ->
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            navigationActions.navigateToBogaslujbovyia(
                                prynagodnyaList[index].title, prynagodnyaList[index].resource
                            )
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                    )
                    Text(
                        text = prynagodnyaList[index].title, modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

@Composable
fun getTtyedzPosnaia(menuItem: Int): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    val subList = getTtyedzBialikagaTydnia(menuItem)
    subList.forEach { item ->
        list.add(
            BogaslujbovyiaListData(
                item.title, item.resource
            )
        )
    }
    return list
}

fun getTtyedzPosnaia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Службы 1-га тыдня Вялікага посту", "1"))
    list.add(BogaslujbovyiaListData("Службы 2-га тыдня Вялікага посту", "2"))
    list.add(BogaslujbovyiaListData("Службы 3-га тыдня Вялікага посту", "3"))
    list.add(BogaslujbovyiaListData("Службы 4-га тыдня Вялікага посту", "4"))
    list.add(BogaslujbovyiaListData("Службы 5-га тыдня Вялікага посту", "5"))
    list.add(BogaslujbovyiaListData("Службы 6-га тыдня Вялікага посту", "6"))
    return list
}

fun getTtyedzBialikagaTydnia(menuItem: Int): ArrayList<MineiaList> {
    val slugbovyiaTextu = SlugbovyiaTextu()
    val mineia = when (menuItem) {
        Settings.MENU_TRYEDZ_VIALIKAGA_TYDNIA -> slugbovyiaTextu.getVilikiTydzen()
        Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA -> slugbovyiaTextu.getSvetlyTydzen()
        Settings.MENU_TRYEDZ_KVETNAIA -> slugbovyiaTextu.getMineiaKvetnaia()
        Settings.MENU_TRYEDZ_POSNAIA_1 -> slugbovyiaTextu.getTydzen1()
        Settings.MENU_TRYEDZ_POSNAIA_2 -> slugbovyiaTextu.getTydzen2()
        Settings.MENU_TRYEDZ_POSNAIA_3 -> slugbovyiaTextu.getTydzen3()
        Settings.MENU_TRYEDZ_POSNAIA_4 -> slugbovyiaTextu.getTydzen4()
        Settings.MENU_TRYEDZ_POSNAIA_5 -> slugbovyiaTextu.getTydzen5()
        Settings.MENU_TRYEDZ_POSNAIA_6 -> slugbovyiaTextu.getTydzen6()
        else -> ArrayList()
    }
    var dayOfYear: Int
    val mineiaList = ArrayList<MineiaList>()
    val cal = Calendar.getInstance()
    for (i in mineia.indices) {
        dayOfYear = slugbovyiaTextu.getRealDay(mineia[i].day, Calendar.getInstance()[Calendar.DAY_OF_YEAR], Calendar.getInstance()[Calendar.YEAR], mineia[i].pasxa)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val opisanie = if (menuItem == Settings.MENU_TRYEDZ_POSNAIA_1 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_2 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_3 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_4 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_5 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_6) ""
        else slugbovyiaTextu.getNazouSluzby(mineia[i].sluzba)
        mineiaList.add(
            MineiaList(
                cal[Calendar.DATE], cal[Calendar.MONTH], mineia[i].title, opisanie, mineia[i].resource, mineia[i].sluzba
            )
        )
    }
    mineiaList.sortWith(
        compareBy({
            it.month
        }, {
            it.dayOfMonth
        }, {
            it.sluzba
        })
    )
    return mineiaList
}

fun getTtyedz(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Трыёдзь посная", "10"))
    list.add(BogaslujbovyiaListData("Службы Вялікага тыдня", "11"))
    list.add(BogaslujbovyiaListData("Службы Сьветлага тыдня", "12"))
    list.add(BogaslujbovyiaListData("Трыёдзь Кветная", "13"))
    return list
}

fun getChasaslou(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Гадзіна 1", "bogashlugbovya/kan_hadz_hadzina_1.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Гадзіна 6", "bogashlugbovya/kan_hadz_hadzina_6.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Гадзіна 6 у вялікі пост", "bogashlugbovya/kan_hadz_hadzina_6_vialiki_post.html"
        )
    )
    list.add(BogaslujbovyiaListData("Павячэрніца малая", "bogashlugbovya/paviaczernica_malaja.html"))
    list.add(BogaslujbovyiaListData("Павячэрніца вялікая", "bogashlugbovya/paviaczernica_vialikaja.html"))
    list.add(BogaslujbovyiaListData("Ютрань нядзельная (у скароце)", "bogashlugbovya/jutran_niadzelnaja.html"))
    return list
}

fun getMineiaMesiachnaia(subTitle: String): ArrayList<MineiaList> {
    val slugbovyiaTextu = SlugbovyiaTextu()
    val mounth = when (subTitle) {
        "Студзень" -> Calendar.JANUARY
        "Люты" -> Calendar.FEBRUARY
        "Сакавік" -> Calendar.MARCH
        "Красавік" -> Calendar.APRIL
        "Травень" -> Calendar.MAY
        "Чэрвень" -> Calendar.JUNE
        "Ліпень" -> Calendar.JULY
        "Жнівень" -> Calendar.AUGUST
        "Верасень" -> Calendar.SEPTEMBER
        "Кастрычнік" -> Calendar.OCTOBER
        "Лістапад" -> Calendar.NOVEMBER
        "Сьнежань" -> Calendar.DECEMBER
        else -> 0
    }
    val mineia = slugbovyiaTextu.getMineiaMesiachnaia()
    var dayOfYear: Int
    val mineiaList = ArrayList<MineiaList>()
    val cal = Calendar.getInstance() as GregorianCalendar
    for (i in mineia.indices) {
        dayOfYear = slugbovyiaTextu.getRealDay(mineia[i].day, Calendar.getInstance()[Calendar.DAY_OF_YEAR], Calendar.getInstance()[Calendar.YEAR], mineia[i].pasxa)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val opisanie = slugbovyiaTextu.getNazouSluzby(mineia[i].sluzba)
        if (cal[Calendar.MONTH] == mounth) {
            mineiaList.add(
                MineiaList(
                    cal[Calendar.DATE], cal[Calendar.MONTH], mineia[i].title, opisanie, mineia[i].resource, mineia[i].sluzba
                )
            )
        }
    }
    mineiaList.sortWith(
        compareBy({
            it.dayOfMonth
        }, {
            it.sluzba
        })
    )
    return mineiaList
}

fun getMineiaMesiachnaiaMounth(): ArrayList<BogaslujbovyiaListData> {
    val context = Malitounik.applicationContext()
    val list = ArrayList<BogaslujbovyiaListData>()
    val mounthList = context.resources.getStringArray(R.array.meciac3)
    mounthList.forEachIndexed { index, item ->
        list.add(BogaslujbovyiaListData(item, index.toString()))
    }
    return list
}

fun getMineiaAgulnaia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная Найсьвяцейшай Багародзіцы", "bogashlugbovya/viachernia_mineia_agulnaia1.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная Яну Хрысьціцелю", "bogashlugbovya/viachernia_mineia_agulnaia2.html"
        )
    )
    list.add(BogaslujbovyiaListData("Вячэрня агульная прароку", "bogashlugbovya/viachernia_mineia_agulnaia3.html"))
    list.add(BogaslujbovyiaListData("Вячэрня агульная апосталу", "bogashlugbovya/viachernia_mineia_agulnaia4.html"))
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная апосталам", "bogashlugbovya/viachernia_mineia_agulnaia5.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятаначальніку", "bogashlugbovya/viachernia_mineia_agulnaia6.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятаначальнікам", "bogashlugbovya/viachernia_mineia_agulnaia7.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная посьніку, манаху і пустэльніку", "bogashlugbovya/viachernia_mineia_agulnaia8.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная посьнікам, манахам і пустэльнікам", "bogashlugbovya/viachernia_mineia_agulnaia9.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная вызнаўцу і настаўніку царкоўнаму", "bogashlugbovya/viachernia_mineia_agulnaia10.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніку", "bogashlugbovya/viachernia_mineia_agulnaia11.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніку (іншая)", "bogashlugbovya/viachernia_mineia_agulnaia12.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучанікам", "bogashlugbovya/viachernia_mineia_agulnaia13.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятамучаніку", "bogashlugbovya/viachernia_mineia_agulnaia14.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятамучанікам", "bogashlugbovya/viachernia_mineia_agulnaia15.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніку сьвятару або манаху", "bogashlugbovya/viachernia_mineia_agulnaia16.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучанікам сьвятарам або манахам", "bogashlugbovya/viachernia_mineia_agulnaia17.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцы", "bogashlugbovya/viachernia_mineia_agulnaia18.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцы (іншая)", "bogashlugbovya/viachernia_mineia_agulnaia19.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцам", "bogashlugbovya/viachernia_mineia_agulnaia20.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцы манахіні", "bogashlugbovya/viachernia_mineia_agulnaia21.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятой жанчыне", "bogashlugbovya/viachernia_mineia_agulnaia22.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятым жанчынам", "bogashlugbovya/viachernia_mineia_agulnaia23.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная бескарысьлівым лекарам і цудатворцам", "bogashlugbovya/viachernia_mineia_agulnaia24.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба апосталу або апосталам", "bogashlugbovya/sluzba_apostalu_apostalam.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба настаўніку царкоўнаму і вызнаўцу", "bogashlugbovya/sluzba_nastauniku_cark_vyznaucu.html"
        )
    )
    list.add(BogaslujbovyiaListData("Служба сьвятаначальнікам", "bogashlugbovya/sluzba_sviatanaczalnikam.html"))
    list.add(BogaslujbovyiaListData("Служба сьвятаначальніку", "bogashlugbovya/sluzba_sviatanaczalniku.html"))
    list.add(
        BogaslujbovyiaListData(
            "Служба Найсьвяцейшай Багародзіцы", "bogashlugbovya/sluzba_najsviaciejszaj_baharodzicy.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба за памерлых на кожны дзень тыдня", "bogashlugbovya/sluzba_za_pamierlych_na_kozny_dzien_tydnia.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба бескарысьлівым лекарам і цудатворцам", "bogashlugbovya/sluzba_bieskaryslivym_lekaram_cudatvorcam.html"
        )
    )
    list.add(BogaslujbovyiaListData("Служба мучаніцам", "bogashlugbovya/sluzba_muczanicam.html"))
    list.add(BogaslujbovyiaListData("Служба мучаніцы", "bogashlugbovya/sluzba_muczanicy.html"))
    list.add(BogaslujbovyiaListData("Служба мучанікам", "bogashlugbovya/sluzba_muczanikam.html"))
    list.add(
        BogaslujbovyiaListData(
            "Служба мучанікам сьвятарам і манахам", "bogashlugbovya/sluzba_muczanikam_sviataram_i_manacham.html"
        )
    )
    list.add(BogaslujbovyiaListData("Служба мучаніку", "bogashlugbovya/sluzba_muczaniku.html"))
    list.add(
        BogaslujbovyiaListData(
            "Служба мучаніку сьвятару і манаху", "bogashlugbovya/sluzba_muczaniku_sviataru_i_manachu.html"
        )
    )
    list.add(BogaslujbovyiaListData("Служба сьвятой жанчыне", "bogashlugbovya/sluzba_sviatoj_zanczynie.html"))
    list.add(BogaslujbovyiaListData("Служба сьвятым жанчынам", "bogashlugbovya/sluzba_sviatym_zanczynam.html"))
    list.add(
        BogaslujbovyiaListData(
            "Служба посьнікам, манахам і пустэльнікам", "bogashlugbovya/sluzba_posnikam_manacham_pustelnikam.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба посьніку / аскету, манаху і пустэльніку", "bogashlugbovya/sluzba_posniku_manachu_pustelniku.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба сьвятому Яну Хрысьціцелю", "bogashlugbovya/sluzba_janu_chryscicielu.html"
        )
    )
    list.add(BogaslujbovyiaListData("Служба прароку", "bogashlugbovya/sluzba_praroku.html"))
    list.add(BogaslujbovyiaListData("Служба сьвятым анёлам", "bogashlugbovya/sluzba_aniolam.html"))
    list.add(BogaslujbovyiaListData("Служба сьвятому крыжу", "bogashlugbovya/sluzba_kryzu.html"))
    list.add(BogaslujbovyiaListData("Служба сьвятамучанікам", "bogashlugbovya/sluzba_sviatamuczanikam.html"))
    list.add(BogaslujbovyiaListData("Служба сьвятамучаніку", "bogashlugbovya/sluzba_sviatamuczaniku.html"))
    return list
}

fun getTrebnik(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Служба аб вызваленьні бязьвінна зьняволеных", "bogashlugbovya/sluzba_vyzvalen_biazvinna_zniavolenych.html"
        )
    )
    list.add(BogaslujbovyiaListData("Служба за памерлых — Малая паніхіда", "bogashlugbovya/panichida_malaja.html"))
    list.add(
        BogaslujbovyiaListData(
            "Чын асьвячэньня транспартнага сродку", "bogashlugbovya/czyn_asviaczennia_transpartnaha_srodku.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Асьвячэньне крыжа на сьвятой Літургіі", "bogashlugbovya/asviaczennie_kryza.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Блаславеньне ўсялякае рэчы", "bogashlugbovya/mltv_blaslaviennie_usialakaj_reczy.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва на асьвячэньне памятнай табліцы Слузе Божаму", "bogashlugbovya/mltv_asviacz_pamiatnaj_tablicy.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва на асьвячэньне памятнай табліцы Слузе Божаму, які пацярпеў за Беларусь", "bogashlugbovya/mltv_asviacz_pamiatnaj_tablicy_paciarpieu_za_bielarus1.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва на асьвячэньне памятнай табліцы ўсім бязьвінным ахвярам, якія пацярпелі за Беларусь", "bogashlugbovya/mltv_asviacz_pamiatnaj_tablicy_biazvinnym_achviaram_paciarpieli_za_bielarus.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітоўны чын сьвятарскіх адведзінаў парафіянаў", "bogashlugbovya/malitouny_czyn_sviatarskich_adviedzinau_parafijanau.html"
        )
    )
    return list
}

fun getMalitvyPasliaPrychascia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва падзякі", "bogashlugbovya/paslia_prychascia1.html"))
    list.add(BogaslujbovyiaListData("Малітва сьв. Васіля Вялікага", "bogashlugbovya/paslia_prychascia2.html"))
    list.add(BogaslujbovyiaListData("Малітва Сымона Мэтафраста", "bogashlugbovya/paslia_prychascia3.html"))
    list.add(BogaslujbovyiaListData("Іншая малітва", "bogashlugbovya/paslia_prychascia4.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Найсьвяцейшай Багародзіцы", "bogashlugbovya/paslia_prychascia5.html"
        )
    )
    return list
}

fun getTraparyKandakiNiadzelnyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Тон 1", "bogashlugbovya/ton1.html"))
    list.add(BogaslujbovyiaListData("Тон 2", "bogashlugbovya/ton2.html"))
    list.add(BogaslujbovyiaListData("Тон 3", "bogashlugbovya/ton3.html"))
    list.add(BogaslujbovyiaListData("Тон 4", "bogashlugbovya/ton4.html"))
    list.add(BogaslujbovyiaListData("Тон 5", "bogashlugbovya/ton5.html"))
    list.add(BogaslujbovyiaListData("Тон 6", "bogashlugbovya/ton6.html"))
    list.add(BogaslujbovyiaListData("Тон 7", "bogashlugbovya/ton7.html"))
    list.add(BogaslujbovyiaListData("Тон 8", "bogashlugbovya/ton8.html"))
    return list
}

fun getTraparyKandakiShtodzennyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам", "bogashlugbovya/ton1_budni.html"))
    list.add(BogaslujbovyiaListData("АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю", "bogashlugbovya/ton2_budni.html"))
    list.add(
        BogaslujbovyiaListData(
            "СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу", "bogashlugbovya/ton3_budni.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю", "bogashlugbovya/ton4_budni.html"
        )
    )
    list.add(BogaslujbovyiaListData("ПЯТНІЦА\nСлужба Крыжу Гасподняму", "bogashlugbovya/ton5_budni.html"))
    list.add(BogaslujbovyiaListData("СУБОТА\nСлужба ўсім сьвятым і памёрлым", "bogashlugbovya/ton6_budni.html"))
    return list
}

fun getViachernia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Вячэрня ў нядзелі і сьвяты", "bogashlugbovya/viaczernia_niadzelnaja.html"))
    list.add(
        BogaslujbovyiaListData(
            "Ліцьця і блаславеньне хлябоў", "bogashlugbovya/viaczernia_liccia_i_blaslavenne_chliabou.html"
        )
    )
    list.add(BogaslujbovyiaListData("Вячэрня ў звычайныя дні", "bogashlugbovya/viaczernia_na_kozny_dzen.html"))
    list.add(BogaslujbovyiaListData("Вячэрня ў Вялікім посьце", "bogashlugbovya/viaczernia_u_vialikim_poscie.html"))
    list.add(
        BogaslujbovyiaListData(
            "Вячэрняя служба штодзённая (без сьвятара)", "bogashlugbovya/viaczerniaja_sluzba_sztodzionnaja_biez_sviatara.html"
        )
    )
    list.add(BogaslujbovyiaListData("Вячэрня на Сьветлым тыдні", "bogashlugbovya/viaczernia_svietly_tydzien.html"))
    return list
}

fun getAktoix(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 1", "bogashlugbovya/viaczernia_ton1.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 2", "bogashlugbovya/viaczernia_ton2.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 3", "bogashlugbovya/viaczernia_ton3.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 4", "bogashlugbovya/viaczernia_ton4.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 5", "bogashlugbovya/viaczernia_ton5.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 6", "bogashlugbovya/viaczernia_ton6.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 7", "bogashlugbovya/viaczernia_ton7.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 8", "bogashlugbovya/viaczernia_ton8.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Багародзічныя адпушчальныя", "bogashlugbovya/viaczernia_baharodzicznyja_adpuszczalnyja.html"
        )
    )
    return list
}

fun getPrynagodnyia1(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Браслаўскай, Валадаркі Азёраў", "prynagodnyia/prynagodnyia_7.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Будслаўскай, Апякункі Беларусі", "prynagodnyia/prynagodnyia_8.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Нястомнай Дапамогі", "prynagodnyia/prynagodnyia_9.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва да Маці Божай Берасьцейскай", "prynagodnyia/prynagodnyia_30.html"))
    list.add(BogaslujbovyiaListData("Малітва да Маці Божай Лагішынскай", "prynagodnyia/prynagodnyia_31.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Будслаўскай", "prynagodnyia/mltv_mb_budslauskaja.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Божае Маці перад іконай Ейнай Менскай", "prynagodnyia/mltv_mb_mienskaja.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Найсьвяцейшай Дзевы Марыі Барунскай", "prynagodnyia/mltv_mb_barunskaja.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Багародзіцы, праслаўленай у цудатворнай Жыровіцкай іконе", "prynagodnyia/mltv_mb_zyrovickaja.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва да Маці Божай Бялыніцкай", "prynagodnyia/mltv_mb_bialynickaja.html"))
    list.add(BogaslujbovyiaListData("Малітва да Найсьвяцейшае Багародзіцы з пакорным і скрушлівым сэрцам", "prynagodnyia/mltv_da_baharodzicy_skaryna.html"))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

fun getPrynagodnyia2(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Малітва аб дапамозе ў выбары жыцьцёвай дарогі дзіцяці", "prynagodnyia/prynagodnyia_1.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва бацькоў за дзяцей («Божа, у Тройцы Адзіны...»)", "prynagodnyia/mltv_backou_za_dziaciej_boza_u_trojcy_adziny.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва бацькоў за дзяцей", "prynagodnyia/prynagodnyia_4.html"))
    list.add(BogaslujbovyiaListData("Малітва за дарослых дзяцей", "prynagodnyia/prynagodnyia_11.html"))
    list.add(BogaslujbovyiaListData("Малітва за бацькоў", "prynagodnyia/mltv_za_backou.html"))
    list.add(BogaslujbovyiaListData("Малітва за хворае дзіця", "prynagodnyia/prynagodnyia_15.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва сям’і аб Божым бласлаўленьні на час адпачынку і вакацыяў", "prynagodnyia/prynagodnyia_33.html"
        )
    )
    list.add(BogaslujbovyiaListData("Блаславеньне маці (Матчына малітва)", "prynagodnyia/prynagodnyia_40.html"))
    list.add(BogaslujbovyiaListData("Малітва за хросьнікаў", "prynagodnyia/mltv_za_chrosnikau.html"))
    list.add(BogaslujbovyiaListData("Малітва да сьв. Язэпа", "prynagodnyia/prynagodnyia_37.html"))
    list.add(BogaslujbovyiaListData("Малітва мужа і бацькі да сьв. Язэпа", "prynagodnyia/prynagodnyia_38.html"))
    list.add(BogaslujbovyiaListData("Малітва да сьв. Язэпа за мужчынаў", "prynagodnyia/prynagodnyia_39.html"))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

fun getPrynagodnyia3(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва за Беларусь", "prynagodnyia/prynagodnyia_10.html"))
    list.add(BogaslujbovyiaListData("Малітва за Айчыну - Ян Павел II", "prynagodnyia/prynagodnyia_36.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за ўсіх, што пацярпелі за Беларусь", "prynagodnyia/mltv_paciarpieli_za_bielarus.html"
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

fun getPrynagodnyia4(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва аб еднасьці", "prynagodnyia/mltv_ab_jednasci.html"))
    list.add(BogaslujbovyiaListData("Малітва за парафію", "prynagodnyia/prynagodnyia_13.html"))
    list.add(BogaslujbovyiaListData("Малітва за хрысьціянскую еднасьць", "prynagodnyia/prynagodnyia_16.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітвы за сьвятароў і сьвятарскія пакліканьні", "prynagodnyia/prynagodnyia_24.html"
        )
    )
    list.add(BogaslujbovyiaListData("Цябе, Бога, хвалім", "prynagodnyia/pesny_prasl_70.html"))
    list.add(BogaslujbovyiaListData("Малітва за Царкву", "prynagodnyia/mltv_za_carkvu.html"))
    list.add(BogaslujbovyiaListData("Малітва за Царкву 2", "prynagodnyia/mltv_za_carkvu_2.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за царкоўную еднасьць", "prynagodnyia/mltv_za_carkounuju_jednasc.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва разам з Падляшскімі мучанікамі аб еднасьці", "prynagodnyia/mltv_razam_z_padlaszskimi_muczanikami_ab_jednasci.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва аб еднасьці царквы (Экзарха Леаніда Фёдарава)", "prynagodnyia/mltv_ab_jednasci_carkvy_leanida_fiodarava.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва за нашую зямлю", "prynagodnyia/mltv_za_naszuju_ziamlu.html"))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

fun getPrynagodnyia5(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Малітва за хворага («Міласэрны Божа»)", "prynagodnyia/mltv_za_chvoraha_milaserny_boza.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва за хворага («Лекару душ і целаў»)", "prynagodnyia/mltv_za_chvoraha_lekaru_dush_cielau.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва ў часе хваробы", "prynagodnyia/mltv_u_czasie_chvaroby.html"))
    list.add(BogaslujbovyiaListData("Малітва падчас згубнай пошасьці", "prynagodnyia/prynagodnyia_28.html"))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

fun getPrynagodnyia6(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва перад пачаткам навучаньня", "prynagodnyia/prynagodnyia_21.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за дзяцей перад пачаткам навукі", "prynagodnyia/prynagodnyia_12.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва вучняў перад навучаньнем", "prynagodnyia/prynagodnyia_29.html"))
    list.add(BogaslujbovyiaListData("Малітва вучня", "prynagodnyia/prynagodnyia_6.html"))
    list.add(BogaslujbovyiaListData("Малітвы за памерлых", "prynagodnyia/mltv_za_pamierlych.html"))
    list.add(BogaslujbovyiaListData("Намер ісьці за Хрыстом", "prynagodnyia/prynagodnyia_26.html"))
    list.add(BogaslujbovyiaListData("Малітва пілігрыма", "prynagodnyia/prynagodnyia_32.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва да ўкрыжаванага Хрыста (Францішак Скарына)", "prynagodnyia/mltv_da_ukryzavanaha_chrysta_skaryna.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва аб блаславеньні", "prynagodnyia/prynagodnyia_0.html"))
    list.add(BogaslujbovyiaListData("Малітва кіроўцы", "prynagodnyia/mltv_kiroucy.html"))
    list.add(BogaslujbovyiaListData("Малітва за ўмацаваньне ў любові", "prynagodnyia/prynagodnyia_17.html"))
    list.add(BogaslujbovyiaListData("Малітва маладога чалавека", "prynagodnyia/prynagodnyia_18.html"))
    list.add(BogaslujbovyiaListData("Малітва на ўсякую патрэбу", "prynagodnyia/prynagodnyia_19.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва падзякі за атрыманыя дабрадзействы", "prynagodnyia/prynagodnyia_20.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва перад іспытамі", "prynagodnyia/prynagodnyia_22.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва ранішняга намеру (Опціных старцаў)", "prynagodnyia/prynagodnyia_23.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва ў час адпачынку", "prynagodnyia/prynagodnyia_34.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за бязьвінных ахвяраў перасьледу", "prynagodnyia/prynagodnyia_35.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітвы перад ядою і пасьля яды", "prynagodnyia/mltv_pierad_jadoj_i_pasla.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва за ўсіх і за ўсё", "prynagodnyia/mltv_za_usich_i_za_usio.html"))
    list.add(BogaslujbovyiaListData("Малітва за вязьняў", "prynagodnyia/mltv_za_viazniau.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва перад пачаткам і пасьля кожнай справы", "prynagodnyia/mltv_pierad_i_pasla_koznaj_spravy.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва ў дзень нараджэньня", "prynagodnyia/mltv_dzien_naradzennia.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітва аб духу любові", "prynagodnyia/mltv_ab_duchu_lubovi_sv_franciszak.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва на кожны час", "prynagodnyia/mltv_na_kozny_czas.html"))
    list.add(
        BogaslujbovyiaListData(
            "Малітвы за памерлых («Божа духаў і ўсякага цялеснага стварэньня»)", "prynagodnyia/mltv_za_pamierlych_boza_duchau.html"
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Юбілейная малітва", "prynagodnyia/mltv_jubilejnaja.html"
        )
    )
    list.add(BogaslujbovyiaListData("Малітва аб муках Госпада нашага Ісуса Хрыста", "prynagodnyia/mltv_ab_mukach_hospada.html"))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        list.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
    } else {
        list.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    return list
}

data class MineiaList(
    val dayOfMonth: Int, val month: Int, val title: String, val titleSluzba: String, val resource: String, val sluzba: Int
)