package by.carkva_gazeta.malitounik2.views

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import by.carkva_gazeta.malitounik2.BibliaList
import by.carkva_gazeta.malitounik2.BibliaMenu
import by.carkva_gazeta.malitounik2.Bogaslujbovyia
import by.carkva_gazeta.malitounik2.BogaslujbovyiaMenu
import by.carkva_gazeta.malitounik2.CytanniList
import by.carkva_gazeta.malitounik2.Dialog
import by.carkva_gazeta.malitounik2.KaliandarScreen
import by.carkva_gazeta.malitounik2.KaliandarScreenMounth
import by.carkva_gazeta.malitounik2.KaliandarScreenYear
import by.carkva_gazeta.malitounik2.MainActivity
import by.carkva_gazeta.malitounik2.MalitvyListAll
import by.carkva_gazeta.malitounik2.R
import by.carkva_gazeta.malitounik2.SearchBible
import by.carkva_gazeta.malitounik2.Settings
import by.carkva_gazeta.malitounik2.VybranaeList
import by.carkva_gazeta.malitounik2.ui.theme.BezPosta
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.ui.theme.Post
import by.carkva_gazeta.malitounik2.ui.theme.Primary
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik2.ui.theme.StrogiPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Calendar

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    BackHandler(
        enabled = drawerState.isClosed,
    ) {
        coroutineScope.launch { drawerState.open() }
    }
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val remove = k.getString("navigate", "Biblia_Cemuxa")
    if (remove == "Biblia_Cemuxa" || remove == "Biblia_Bokuna" || remove == "Biblia_Charniauski" || remove == "Biblia_Nadsan" || remove == "Biblia_Sinodal")
        k.edit().remove("navigate").apply()
    val start = k.getString("navigate", AllDestinations.KALIANDAR) ?: AllDestinations.KALIANDAR
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable(AllDestinations.KALIANDAR) {
            Settings.destinations = AllDestinations.KALIANDAR
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.BOGASLUJBOVYIA_MENU) {
            Settings.destinations = AllDestinations.BOGASLUJBOVYIA_MENU
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.MALITVY_MENU) {
            Settings.destinations = AllDestinations.MALITVY_MENU
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.BIBLIA) {
            Settings.destinations = AllDestinations.BIBLIA
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.KALIANDAR_YEAR) {
            Settings.destinations = AllDestinations.KALIANDAR_YEAR
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.VYBRANAE_LIST) {
            Settings.destinations = AllDestinations.VYBRANAE_LIST
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(
            AllDestinations.MALITVY_LIST_ALL + "/{title}/{menuItem}/{subTitle}",
            arguments = listOf(navArgument("menuItem") { type = NavType.IntType })
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val subTitle = stackEntry.arguments?.getString("subTitle") ?: ""
            val menuItemt = stackEntry.arguments?.getInt("menuItem") ?: Settings.MENU_BOGASLUJBOVYIA
            MalitvyListAll(navController, title, menuItemt, subTitle)
        }

        composable(
            AllDestinations.BOGASLUJBOVYIA + "/{title}/{resurs}",
            arguments = listOf(navArgument("resurs") { type = NavType.IntType })
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val resurs = stackEntry.arguments?.getInt("resurs") ?: R.raw.bogashlugbovya_error
            Bogaslujbovyia(navController, title, resurs)
        }

        composable(
            AllDestinations.CYTANNI_LIST + "/{cytanne}/{title}/{biblia}/{perevod}/{position}",
            arguments = listOf(
                navArgument("biblia") { type = NavType.IntType },
                navArgument("position") { type = NavType.IntType })
        ) { stackEntry ->
            val cytanne = stackEntry.arguments?.getString("cytanne") ?: ""
            val title = stackEntry.arguments?.getString("title") ?: ""
            val biblia = stackEntry.arguments?.getInt("biblia", Settings.CHYTANNI_LITURGICHNYIA)
                ?: Settings.CHYTANNI_LITURGICHNYIA
            Settings.destinations = AllDestinations.CYTANNI_LIST
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI)
                ?: Settings.PEREVODSEMUXI
            val position = stackEntry.arguments?.getInt("position", 0) ?: 0
            CytanniList(navController, title, cytanne, biblia, perevod, position)
        }

        composable(
            AllDestinations.BIBLIA_LIST + "/{novyZapavet}/{perevod}",
            arguments = listOf(
                navArgument("novyZapavet") { type = NavType.BoolType })
        ) { stackEntry ->
            val isNovyZapavet = stackEntry.arguments?.getBoolean("novyZapavet", false) ?: false
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI)
                ?: Settings.PEREVODSEMUXI
            BibliaList(
                navController,
                isNovyZapavet,
                perevod,
                navigateToCytanniList = { chytanne, perevod2 ->
                    navigationActions.navigateToCytanniList(
                        "",
                        chytanne,
                        Settings.CHYTANNI_BIBLIA,
                        perevod2,
                        -1
                    )
                })
        }

        composable(
            AllDestinations.SEARCH_BIBLIA + "/{perevod}"
        ) { stackEntry ->
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI)
                ?: Settings.PEREVODSEMUXI
            SearchBible(
                navController,
                perevod,
                navigateToCytanniList = { chytanne, position, perevod2 ->
                    navigationActions.navigateToCytanniList(
                        "",
                        chytanne,
                        Settings.CHYTANNI_BIBLIA,
                        perevod2,
                        position
                    )
                }
            )
        }
    }
}

@Composable
fun findCaliandarPosition(position: Int): ArrayList<ArrayList<String>> {
    if (Settings.data.isEmpty()) {
        val gson = Gson()
        val type = TypeToken.getParameterized(
            java.util.ArrayList::class.java,
            TypeToken.getParameterized(
                java.util.ArrayList::class.java,
                String::class.java
            ).type
        ).type
        val inputStream = LocalContext.current.resources.openRawResource(R.raw.caliandar)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = reader.use {
            it.readText()
        }
        Settings.data.addAll(gson.fromJson(builder, type))
    }
    if (position == -1 && Settings.initCaliandarPosition == 0) {
        val calendar = Calendar.getInstance()
        for (i in Settings.data.indices) {
            if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
                Settings.caliandarPosition = i
                Settings.initCaliandarPosition = i
                break
            }
        }
    } else Settings.caliandarPosition = position
    return Settings.data
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConteiner(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: AllDestinations.KALIANDAR
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val initPage = if (Settings.caliandarPosition == -1) {
        findCaliandarPosition(-1)
        Settings.initCaliandarPosition
    } else Settings.caliandarPosition
    val lazyColumnState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = {
        Settings.data.size
    }, initialPage = initPage)
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    BackHandler(showDropdown) {
        showDropdown = !showDropdown
    }
    val view = LocalView.current
    var isAppearanceLight = false
    if (Settings.destinations == AllDestinations.KALIANDAR) {
        if (Settings.data[Settings.caliandarPosition][7].toInt() == 3 && !(Settings.data[Settings.caliandarPosition][0].toInt() == Calendar.SUNDAY || Settings.data[Settings.caliandarPosition][0].toInt() == Calendar.SATURDAY)) {
            isAppearanceLight = true
        }
        if (Settings.data[Settings.caliandarPosition][5].toInt() > 0) {
            isAppearanceLight = true
        }
        isAppearanceLight = !isAppearanceLight
    }

    if (drawerState.isOpen) isAppearanceLight =
        !(LocalActivity.current as MainActivity).dzenNoch //!isSystemInDarkTheme()
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = isAppearanceLight
    }
    var sorted by remember { mutableIntStateOf(k.getInt("sortedVybranae", Settings.SORT_BY_ABC)) }
    var removeAllVybranaeDialog by remember { mutableStateOf(false) }
    var removeAllVybranae by remember { mutableStateOf(false) }
    if (removeAllVybranaeDialog) {
        Dialog(
            title = stringResource(R.string.del_all_vybranoe),
            onDismissRequest = {
                removeAllVybranaeDialog = false
            },
            onConfirmation = {
                for (perevod in 1..5) {
                    val prevodName = when (perevod.toString()) {
                        Settings.PEREVODSEMUXI -> "biblia"
                        Settings.PEREVODBOKUNA -> "bokuna"
                        Settings.PEREVODCARNIAUSKI -> "carniauski"
                        Settings.PEREVODNADSAN -> "nadsan"
                        Settings.PEREVODSINOIDAL -> "sinaidal"
                        else -> "biblia"
                    }
                    val file = File("${context.filesDir}/vybranoe_${prevodName}.json")
                    if (file.exists()) file.delete()

                }
                removeAllVybranae = true
                removeAllVybranaeDialog = false
            }
        )
    }
    ModalNavigationDrawer(drawerContent = {
        DrawView(
            route = currentRoute,
            navigateToRazdel = { razdzel ->
                when (razdzel) {
                    AllDestinations.KALIANDAR -> navigationActions.navigateToKaliandar()
                    AllDestinations.BOGASLUJBOVYIA_MENU -> navigationActions.navigateToBogaslujbovyiaMenu()
                    AllDestinations.MALITVY_MENU -> navigationActions.navigateToMalitvyMenu()
                    AllDestinations.BIBLIA -> navigationActions.navigateToBiblia()
                    AllDestinations.VYBRANAE_LIST -> {
                        navigationActions.navigateToVybranaeList()
                    }
                }
                coroutineScope.launch { drawerState.close() }
            },
        )
    }, drawerState = drawerState) {
        val col = MaterialTheme.colorScheme.onTertiary
        var tollBarColor by remember { mutableStateOf(col) }
        var textTollBarColor by remember { mutableStateOf(PrimaryTextBlack) }
        var title by rememberSaveable {
            mutableStateOf("")
        }
        title = when (currentRoute) {
            AllDestinations.KALIANDAR -> stringResource(R.string.kaliandar2)
            AllDestinations.KALIANDAR_YEAR -> stringResource(R.string.kaliandar2)
            AllDestinations.BOGASLUJBOVYIA_MENU -> stringResource(R.string.liturgikon)
            AllDestinations.MALITVY_MENU -> stringResource(R.string.malitvy)
            AllDestinations.VYBRANAE_LIST -> stringResource(R.string.MenuVybranoe)
            AllDestinations.BIBLIA -> {
                when (k.getString("perevodBibileMenu", Settings.PEREVODSEMUXI)
                    ?: Settings.PEREVODSEMUXI) {
                    Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia)
                    Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun)
                    Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski)
                    Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
                    Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal)
                    else -> stringResource(R.string.kaliandar2)
                }
            }

            else -> ""
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            title,
                            color = textTollBarColor,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    tint = textTollBarColor,
                                    contentDescription = ""
                                )
                            })
                    },
                    actions = {
                        if (currentRoute == AllDestinations.KALIANDAR || currentRoute == AllDestinations.KALIANDAR_YEAR) {
                            IconButton({
                                val edit = k.edit()
                                if (k.getBoolean("caliandarList", false)) {
                                    navigationActions.navigateToKaliandar()
                                    edit.putBoolean("caliandarList", false)
                                } else {
                                    edit.putBoolean("caliandarList", true)
                                    navigationActions.navigateToKaliandarYear()
                                }
                                edit.apply()
                            }) {
                                val icon = if (k.getBoolean(
                                        "caliandarList",
                                        false
                                    )
                                ) painterResource(R.drawable.calendar_today)
                                else painterResource(R.drawable.list)
                                Icon(
                                    painter = icon,
                                    tint = textTollBarColor,
                                    contentDescription = ""
                                )
                            }
                            IconButton({ showDropdown = !showDropdown }) {
                                Icon(
                                    painter = painterResource(R.drawable.event_upcoming),
                                    tint = textTollBarColor,
                                    contentDescription = ""
                                )
                            }
                        }
                        if (currentRoute == AllDestinations.VYBRANAE_LIST) {
                            IconButton({ removeAllVybranaeDialog = !removeAllVybranaeDialog }) {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    tint = textTollBarColor,
                                    contentDescription = ""
                                )
                            }
                        }
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "",
                                    tint = textTollBarColor
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    onClick = { },
                                    text = { Text(stringResource(R.string.tools_item)) }
                                )
                                if (currentRoute.contains(AllDestinations.KALIANDAR)) {
                                    DropdownMenuItem(
                                        onClick = { },
                                        text = { Text(stringResource(R.string.munu_symbols)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = { },
                                        text = { Text(stringResource(R.string.sabytie)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = { },
                                        text = { Text(stringResource(R.string.search_svityia)) }
                                    )
                                }
                                if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) {
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            sorted =
                                                if (sorted == Settings.SORT_BY_ABC) Settings.SORT_BY_TIME
                                                else Settings.SORT_BY_ABC
                                            val edit = k.edit()
                                            edit.putInt("sortedVybranae", sorted)
                                            edit.apply()
                                        },
                                        text = {
                                            if (sorted == Settings.SORT_BY_TIME) Text(
                                                stringResource(
                                                    R.string.sort_alf
                                                )
                                            )
                                            else Text(stringResource(R.string.sort_add))
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    onClick = { },
                                    text = { Text(stringResource(R.string.pra_nas)) }
                                )
                                DropdownMenuItem(
                                    onClick = { },
                                    text = { Text(stringResource(R.string.help)) }
                                )
                                if (k.getBoolean("admin", false)) {
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        onClick = { },
                                        text = { Text(stringResource(R.string.redagaktirovat)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = { },
                                        text = { Text(stringResource(R.string.log_m)) }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(tollBarColor)
                )
            },
            modifier = Modifier,
            /*bottomBar = {
                BottomAppBar {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        //MenuKalendra(expanded = true, isExpanded = { expanded = false })
                        Icon(Icons.Filled.Menu, contentDescription = "Меню")
                    }
                    IconButton(onClick = { showDropdown = !showDropdown }) {
                        Icon(Icons.Filled.Info, contentDescription = "О приложении")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Search, contentDescription = "Поиск")
                    }
                }
            }*/
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
            ) {
                val color = MaterialTheme.colorScheme.onTertiary
                var colorBlackboard by remember { mutableStateOf(color) }
                when (Settings.destinations) {
                    AllDestinations.KALIANDAR -> {
                        val fling = PagerDefaults.flingBehavior(
                            state = pagerState,
                            pagerSnapDistance = PagerSnapDistance.atMost(1)
                        )
                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.currentPage }.collect { page ->
                                Settings.caliandarPosition = page
                                colorBlackboard = Divider
                                val data = Settings.data[page]
                                var colorText = PrimaryText
                                isAppearanceLight = false
                                if (data[7].toInt() == 2) {
                                    colorBlackboard = Post
                                }
                                if (data[7].toInt() == 1) {
                                    colorBlackboard = BezPosta
                                }
                                if (data[7].toInt() == 3 && !(data[0].toInt() == Calendar.SUNDAY || data[0].toInt() == Calendar.SATURDAY)) {
                                    colorBlackboard = StrogiPost
                                    colorText = PrimaryTextBlack
                                    isAppearanceLight = true
                                }
                                if (data[5].toInt() > 0) {
                                    colorBlackboard = Primary
                                    colorText = PrimaryTextBlack
                                    isAppearanceLight = true
                                }
                                tollBarColor = colorBlackboard
                                textTollBarColor = colorText
                                val window = (view.context as Activity).window
                                WindowCompat.getInsetsController(
                                    window,
                                    view
                                ).isAppearanceLightStatusBars = !isAppearanceLight
                            }
                        }
                        HorizontalPager(
                            pageSpacing = 10.dp,
                            state = pagerState,
                            flingBehavior = fling,
                            modifier = Modifier.padding(10.dp)
                        ) { page ->
                            KaliandarScreen(
                                data = Settings.data[page],
                                navigateToCytanneList = { title, chytanne, biblia ->
                                    navigationActions.navigateToCytanniList(
                                        title,
                                        chytanne,
                                        biblia,
                                        Settings.PEREVODSEMUXI,
                                        -1
                                    )
                                },
                                innerPadding
                            )
                        }
                    }

                    AllDestinations.BOGASLUJBOVYIA_MENU -> BogaslujbovyiaMenu(navController, innerPadding, Settings.MENU_BOGASLUJBOVYIA)

                    AllDestinations.MALITVY_MENU -> BogaslujbovyiaMenu(navController, innerPadding, Settings.MENU_MALITVY)

                    AllDestinations.BIBLIA -> BibliaMenu(
                        navController,
                        setTitle = {
                            title = it
                        },
                        navigateToSearchBible = { perevod ->
                            navigationActions.navigateToSearchBiblia(perevod)
                        },
                        navigateToCytanniList = { chytanne, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_BIBLIA,
                                perevod2,
                                -1
                            )
                        }
                    )

                    AllDestinations.KALIANDAR_YEAR -> KaliandarScreenYear(
                        coroutineScope = coroutineScope,
                        lazyColumnState = lazyColumnState,
                        innerPadding
                    )

                    AllDestinations.VYBRANAE_LIST -> VybranaeList(
                        navigateToCytanniList = { chytanne, position, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_VYBRANAE,
                                perevod2,
                                position
                            )
                        },
                        navigateToBogaslujbovyia = { title, resourse ->
                            navigationActions.navigateToBogaslujbovyia(title, resourse)
                        },
                        sorted,
                        removeAllVybranae
                    )
                }
                Popup(
                    alignment = Alignment.TopCenter,
                    onDismissRequest = { showDropdown = false }
                ) {
                    AnimatedVisibility(
                        showDropdown,
                        enter = slideInVertically(
                            tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                    ) {
                        KaliandarScreenMounth(
                            colorBlackboard = colorBlackboard,
                            setPageCaliandar = { date ->
                                showDropdown = false
                                coroutineScope.launch {
                                    if (k.getBoolean(
                                            "caliandarList",
                                            false
                                        )
                                    ) lazyColumnState.scrollToItem(date)
                                    else pagerState.scrollToPage(date)
                                }
                            })
                    }
                }
            }
        }
    }
}