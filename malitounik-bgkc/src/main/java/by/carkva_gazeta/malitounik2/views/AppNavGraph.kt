package by.carkva_gazeta.malitounik2.views

import android.app.Activity
import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import by.carkva_gazeta.malitounik2.BibliaList
import by.carkva_gazeta.malitounik2.BibliaMenu
import by.carkva_gazeta.malitounik2.Biblijateka
import by.carkva_gazeta.malitounik2.BiblijtekaList
import by.carkva_gazeta.malitounik2.Bogaslujbovyia
import by.carkva_gazeta.malitounik2.BogaslujbovyiaMenu
import by.carkva_gazeta.malitounik2.CytanniList
import by.carkva_gazeta.malitounik2.Dialog
import by.carkva_gazeta.malitounik2.KaliandarScreen
import by.carkva_gazeta.malitounik2.KaliandarScreenMounth
import by.carkva_gazeta.malitounik2.KaliandarScreenYear
import by.carkva_gazeta.malitounik2.LogView
import by.carkva_gazeta.malitounik2.MaeNatatki
import by.carkva_gazeta.malitounik2.MainActivity
import by.carkva_gazeta.malitounik2.MalitvyListAll
import by.carkva_gazeta.malitounik2.ParafiiBGKC
import by.carkva_gazeta.malitounik2.Pashalia
import by.carkva_gazeta.malitounik2.PiesnyList
import by.carkva_gazeta.malitounik2.R
import by.carkva_gazeta.malitounik2.SearchBible
import by.carkva_gazeta.malitounik2.SearchSviatyia
import by.carkva_gazeta.malitounik2.Settings
import by.carkva_gazeta.malitounik2.SettingsView
import by.carkva_gazeta.malitounik2.SviatyList
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
import java.io.IOException
import java.io.InputStreamReader
import java.util.Calendar

class Biblijateka(private val fileName: String) : PagingSource<Int, Int>() {
    override fun getRefreshKey(state: PagingState<Int, Int>): Int? {
        return state.anchorPosition?.let { state.closestItemToPosition(it) }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
        return try {
            val list = ArrayList<Int>()
            var count = 0
            try {
                val file = File("${MainActivity.applicationContext().filesDir}/bibliatekaPdf/$fileName")
                val fileReader = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileReader!!)
                count = pdfRenderer.pageCount
                for (i in 0 until count) {
                    list.add(i)
                }
            } catch (_: Throwable) {
            }
            val pageNumber = params.key ?: 0
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (count > pageNumber + 1) pageNumber + 1 else null
            LoadResult.Page(
                data = list,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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

        composable(AllDestinations.AKAFIST_MENU) {
            Settings.destinations = AllDestinations.AKAFIST_MENU
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.RUJANEC_MENU) {
            Settings.destinations = AllDestinations.RUJANEC_MENU
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.MAE_NATATKI_MENU) {
            Settings.destinations = AllDestinations.MAE_NATATKI_MENU
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

        composable(AllDestinations.BIBLIJATEKA_LIST) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_LIST
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.PIESNY_LIST) {
            Settings.destinations = AllDestinations.PIESNY_LIST
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.PADRYXTOUKA) {
            Bogaslujbovyia(navController, stringResource(R.string.spovedz), R.raw.padryxtouka_da_spovedzi)
        }

        composable(AllDestinations.SEARCH_SVITYIA) {
            SearchSviatyia(navController)
        }

        composable(AllDestinations.SETTINGS_VIEW) {
            SettingsView(navController)
        }

        composable(AllDestinations.PAMIATKA) {
            Bogaslujbovyia(navController, stringResource(R.string.pamiatka), R.raw.pamiatka)
        }

        composable(AllDestinations.PRANAS) {
            Bogaslujbovyia(navController, stringResource(R.string.pra_nas), R.raw.onas)
        }

        composable(AllDestinations.HELP) {
            Bogaslujbovyia(navController, stringResource(R.string.help), R.raw.help)
        }

        composable(AllDestinations.SVAITY_MUNU) {
            Settings.destinations = AllDestinations.SVAITY_MUNU
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.PARAFII_BGKC) {
            Settings.destinations = AllDestinations.PARAFII_BGKC
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(AllDestinations.PASHALIA) {
            Settings.destinations = AllDestinations.PASHALIA
            MainConteiner(
                navController = navController,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA + "/{title}/{fileName}",
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val fileName = stackEntry.arguments?.getString("fileName") ?: ""
            val page = Pager(PagingConfig(1)) {
                Biblijateka(fileName)
            }
            Biblijateka(navController, title, fileName, page)
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
    var sortedVybranae by remember {
        mutableIntStateOf(
            k.getInt(
                "sortedVybranae",
                Settings.SORT_BY_ABC
            )
        )
    }
    var sortedNatatki by remember {
        mutableIntStateOf(
            k.getInt("natatki_sort", Settings.SORT_BY_ABC)
        )
    }
    var addFileNatatki by remember { mutableStateOf(false) }
    var removeAllVybranaeDialog by remember { mutableStateOf(false) }
    var removeAllNatatkiDialog by remember { mutableStateOf(false) }
    var removeAllVybranae by remember { mutableStateOf(false) }
    var removeAllNatatki by remember { mutableStateOf(false) }
    var logView by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var dialogUmounyiaZnachenni by remember { mutableStateOf(false) }
    if (dialogUmounyiaZnachenni) {
        DialogUmounyiaZnachenni {
            dialogUmounyiaZnachenni = false
        }
    }
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange("".length)
            )
        )
    }
    if (logView) {
        DialogLogProgramy {
            logView = false
        }
    }
    if (removeAllVybranaeDialog || removeAllNatatkiDialog) {
        Dialog(
            title = if (removeAllVybranaeDialog) stringResource(R.string.del_all_vybranoe)
            else stringResource(R.string.delite_all_natatki),
            onDismissRequest = {
                removeAllVybranaeDialog = false
                removeAllNatatkiDialog = false
            },
            onConfirmation = {
                if (removeAllVybranaeDialog) {
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
                } else {
                    val dir = File("${context.filesDir}/Malitva")
                    if (dir.exists()) dir.deleteRecursively()
                    removeAllNatatkiDialog = false
                    removeAllNatatki = true
                }
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
                    AllDestinations.VYBRANAE_LIST -> navigationActions.navigateToVybranaeList()
                    AllDestinations.AKAFIST_MENU -> navigationActions.navigateToAkafistMenu()
                    AllDestinations.RUJANEC_MENU -> navigationActions.navigateToRujanecMenu()
                    AllDestinations.MAE_NATATKI_MENU -> navigationActions.navigateToMaeNatatkiMenu()
                    AllDestinations.BIBLIJATEKA_LIST -> navigationActions.navigateToBiblijatekaList()
                    AllDestinations.PIESNY_LIST -> navigationActions.navigateToPiesnyList()
                    AllDestinations.PADRYXTOUKA -> navigationActions.navigateToPadryxtouka()
                    AllDestinations.PAMIATKA -> navigationActions.navigateToPamiatka()
                    AllDestinations.SVAITY_MUNU -> navigationActions.navigateToSviaty()
                    AllDestinations.PARAFII_BGKC -> navigationActions.navigateToParafiiBgkc()
                    AllDestinations.PASHALIA -> navigationActions.navigateToPashalia()
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
            AllDestinations.AKAFIST_MENU -> stringResource(R.string.akafisty)
            AllDestinations.RUJANEC_MENU -> stringResource(R.string.ruzanec)
            AllDestinations.MALITVY_MENU -> stringResource(R.string.malitvy)
            AllDestinations.VYBRANAE_LIST -> stringResource(R.string.MenuVybranoe)
            AllDestinations.MAE_NATATKI_MENU -> stringResource(R.string.maje_natatki)
            AllDestinations.BIBLIJATEKA_LIST -> stringResource(R.string.bibliateka_carkvy)
            AllDestinations.PIESNY_LIST -> stringResource(R.string.song)
            AllDestinations.SVAITY_MUNU -> stringResource(R.string.sviaty)
            AllDestinations.PARAFII_BGKC -> stringResource(R.string.parafii)
            AllDestinations.PASHALIA -> stringResource(R.string.paschalia)
            AllDestinations.BIBLIA -> stringResource(R.string.bibliaAll)
            else -> ""
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (!searchText) {
                            Text(
                                title,
                                color = textTollBarColor,
                                fontWeight = FontWeight.Bold
                            )
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
                                    },
                                value = textFieldValueState,
                                onValueChange = { newText ->
                                    textFieldValueState = newText
                                    var edit = textFieldValueState.text
                                    edit = edit.replace("и", "і")
                                    edit = edit.replace("щ", "ў")
                                    edit = edit.replace("И", "І")
                                    edit = edit.replace("Щ", "Ў")
                                    edit = edit.replace("ъ", "'")
                                    textFieldValueState = TextFieldValue(edit, TextRange(edit.length))
                                },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        contentDescription = ""
                                    )
                                },
                                trailingIcon = {
                                    if (textFieldValueState.text.isNotEmpty()) {
                                        IconButton(onClick = { textFieldValueState = TextFieldValue("", TextRange("".length)) }) {
                                            Icon(
                                                painter = painterResource(R.drawable.close),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                                    focusedTextColor = PrimaryTextBlack,
                                    focusedIndicatorColor = PrimaryTextBlack,
                                    unfocusedIndicatorColor = PrimaryTextBlack,
                                    cursorColor = PrimaryTextBlack
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        if (searchText) {
                            IconButton(onClick = {
                                searchText = false
                            },
                                content = {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        contentDescription = ""
                                    )
                                })
                        } else {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } },
                                content = {
                                    Icon(
                                        painter = painterResource(R.drawable.menu),
                                        tint = textTollBarColor,
                                        contentDescription = ""
                                    )
                                })
                        }
                    },
                    actions = {
                        if (!searchText) {
                            if (currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.RUJANEC_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute == AllDestinations.BIBLIJATEKA_LIST || currentRoute == AllDestinations.PIESNY_LIST || currentRoute == AllDestinations.PASHALIA) {
                                IconButton({
                                    searchText = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        tint = textTollBarColor,
                                        contentDescription = ""
                                    )
                                }
                            }
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
                            if (currentRoute == AllDestinations.MAE_NATATKI_MENU) {
                                IconButton({
                                    addFileNatatki = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.add),
                                        tint = textTollBarColor,
                                        contentDescription = ""
                                    )
                                }
                            }
                            if (currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.MAE_NATATKI_MENU) {
                                IconButton({
                                    if (currentRoute == AllDestinations.VYBRANAE_LIST) removeAllVybranaeDialog =
                                        !removeAllVybranaeDialog
                                    else removeAllNatatkiDialog = !removeAllNatatkiDialog
                                }) {
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
                                        painter = painterResource(R.drawable.more_vert),
                                        contentDescription = "",
                                        tint = textTollBarColor
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            navigationActions.navigateToSettingsView()
                                        },
                                        text = { Text(stringResource(R.string.tools_item)) }
                                    )
                                    if (currentRoute.contains(AllDestinations.KALIANDAR)) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                dialogUmounyiaZnachenni = true
                                            },
                                            text = { Text(stringResource(R.string.munu_symbols)) }
                                        )
                                        DropdownMenuItem(
                                            onClick = { },
                                            text = { Text(stringResource(R.string.sabytie)) }
                                        )
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                navigationActions.navigateToSearchSvityia()
                                            },
                                            text = { Text(stringResource(R.string.search_svityia)) }
                                        )
                                    }
                                    if (currentRoute.contains(AllDestinations.VYBRANAE_LIST) || currentRoute.contains(
                                            AllDestinations.MAE_NATATKI_MENU
                                        )
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                sortedVybranae =
                                                    if (sortedVybranae == Settings.SORT_BY_ABC) Settings.SORT_BY_TIME
                                                    else Settings.SORT_BY_ABC
                                                sortedNatatki =
                                                    if (sortedVybranae == Settings.SORT_BY_ABC) Settings.SORT_BY_TIME
                                                    else Settings.SORT_BY_ABC
                                                val edit = k.edit()
                                                if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) edit.putInt(
                                                    "sortedVybranae",
                                                    sortedVybranae
                                                )
                                                else edit.putInt("natatki_sort", sortedNatatki)
                                                edit.apply()
                                            },
                                            text = {
                                                if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) {
                                                    if (sortedVybranae == Settings.SORT_BY_TIME) Text(
                                                        stringResource(
                                                            R.string.sort_alf
                                                        )
                                                    )
                                                    else Text(stringResource(R.string.sort_add))
                                                } else {
                                                    if (sortedNatatki == Settings.SORT_BY_TIME) Text(
                                                        stringResource(
                                                            R.string.sort_alf
                                                        )
                                                    )
                                                    else Text(stringResource(R.string.sort_add))
                                                }
                                            }
                                        )
                                    }
                                    /*if (currentRoute.contains(AllDestinations.BOGASLUJBOVYIA)) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                searchText = true
                                            },
                                            text = { Text(stringResource(R.string.searche_text)) }
                                        )
                                    }*/
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            navigationActions.navigateToPraNas()
                                        },
                                        text = { Text(stringResource(R.string.pra_nas)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            navigationActions.navigateToHelp()
                                        },
                                        text = { Text(stringResource(R.string.help)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            logView = true
                                        },
                                        text = { Text(stringResource(R.string.log_m)) }
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
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(tollBarColor)
                )
            }
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
                                val data = Settings.data[page]
                                var colorText = PrimaryText
                                isAppearanceLight = false
                                when {
                                    data[7].toInt() == 2 -> colorBlackboard = Post
                                    data[7].toInt() == 1 -> colorBlackboard = BezPosta
                                    data[7].toInt() == 3 && !(data[0].toInt() == Calendar.SUNDAY || data[0].toInt() == Calendar.SATURDAY) -> {
                                        colorBlackboard = StrogiPost
                                        colorText = PrimaryTextBlack
                                        isAppearanceLight = true
                                    }

                                    data[5].toInt() > 0 -> {
                                        colorBlackboard = Primary
                                        colorText = PrimaryTextBlack
                                        isAppearanceLight = true
                                    }

                                    else -> {
                                        colorBlackboard = Divider
                                    }
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
                            verticalAlignment = Alignment.Top,
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

                    AllDestinations.BOGASLUJBOVYIA_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_BOGASLUJBOVYIA,
                        searchText,
                        textFieldValueState.text
                    )

                    AllDestinations.AKAFIST_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_AKAFIST,
                        searchText,
                        textFieldValueState.text
                    )

                    AllDestinations.BIBLIJATEKA_LIST -> BiblijtekaList(navController, innerPadding, searchText, textFieldValueState.text)

                    AllDestinations.PIESNY_LIST -> PiesnyList(navController, innerPadding, searchText, textFieldValueState.text)

                    AllDestinations.SVAITY_MUNU -> SviatyList(navController, innerPadding)

                    AllDestinations.PARAFII_BGKC -> ParafiiBGKC(navController, innerPadding)

                    AllDestinations.PASHALIA -> {
                        if (!searchText) textFieldValueState = TextFieldValue("", TextRange("".length))
                        Pashalia(navController, innerPadding, searchText, textFieldValueState.text)
                    }

                    AllDestinations.RUJANEC_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_RUJANEC,
                        searchText,
                        textFieldValueState.text
                    )

                    AllDestinations.MAE_NATATKI_MENU -> {
                        MaeNatatki(
                            innerPadding,
                            sortedNatatki,
                            addFileNatatki,
                            removeAllNatatki,
                            onDismissAddFile = {
                                addFileNatatki = false
                            })
                    }

                    AllDestinations.MALITVY_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_MALITVY,
                        searchText,
                        textFieldValueState.text
                    )

                    AllDestinations.BIBLIA -> BibliaMenu(
                        navController,
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
                        sortedVybranae,
                        removeAllVybranae,
                        innerPadding
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

@Composable
fun DialogLogProgramy(
    onDismissRequest: () -> Unit,
) {
    val context = LocalActivity.current as MainActivity
    var item by remember { mutableStateOf("") }
    val logView = LogView(context)
    logView.setLogViewListinner(object : LogView.LogViewListinner {
        override fun logView(log: String) {
            item = log
        }
    })
    LaunchedEffect(Unit) {
        logView.upDateLog()
    }
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.log))
        },
        text = {
            Text(item, fontSize = 18.sp)
        },
        onDismissRequest = {
        },
        confirmButton = {
            TextButton(
                onClick = {
                    logView.createAndSentFile()
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.set_log), fontSize = 18.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.close), fontSize = 18.sp)
            }
        }
    )
}

@Composable
fun DialogUmounyiaZnachenni(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.info), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.munu_symbols))
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(R.string.Znaki_cviat),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(24.dp, 24.dp), painter = painterResource(R.drawable.znaki_krest_v_kruge), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    val text = stringResource(R.string.dvuna_i_vial)
                    val t1 = text.indexOf("\n")
                    val annotatedString =
                        buildAnnotatedString {
                            append(text)
                            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t1)
                        }
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = annotatedString,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(24.dp, 24.dp), painter = painterResource(R.drawable.znaki_krest_v_polukruge), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(R.string.Z_Lic_na_ve),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(24.dp, 24.dp), painter = painterResource(R.drawable.znaki_krest), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(R.string.Z_v_v_v_u_n_u),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(24.dp, 24.dp), painter = painterResource(R.drawable.znaki_ttk), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(R.string.Z_sh_v_v_u_u),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(24.dp, 24.dp), painter = painterResource(R.drawable.znaki_ttk_black), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(R.string.Z_sh_v_m_u_u),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = stringResource(R.string.tipicon_fon),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(Primary)
                            .padding(10.dp),
                        text = stringResource(R.string.niadzeli_i_sviaty),
                        fontSize = 18.sp,
                        color = PrimaryTextBlack
                    )
                Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(Divider)
                            .padding(10.dp),
                        text = stringResource(R.string.zvychaynye_dny),
                        fontSize = 18.sp,
                        color = PrimaryText
                    )
                Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(BezPosta)
                            .padding(10.dp),
                        text = stringResource(R.string.No_post_n),
                        fontSize = 18.sp,
                        color = PrimaryText
                    )
                Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(Post)
                            .padding(10.dp),
                        text = stringResource(R.string.Post),
                        fontSize = 18.sp,
                        color = PrimaryText
                    )
                Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(StrogiPost)
                            .padding(10.dp),
                        text = stringResource(R.string.Strogi_post_n),
                        fontSize = 18.sp,
                        color = PrimaryTextBlack
                    )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.close), fontSize = 18.sp)
            }
        }
    )
}