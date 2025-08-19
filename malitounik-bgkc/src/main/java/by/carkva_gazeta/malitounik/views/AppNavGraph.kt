package by.carkva_gazeta.malitounik.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import by.carkva_gazeta.malitounik.BibliaList
import by.carkva_gazeta.malitounik.BibliaMenu
import by.carkva_gazeta.malitounik.Biblijateka
import by.carkva_gazeta.malitounik.BiblijtekaList
import by.carkva_gazeta.malitounik.Bogaslujbovyia
import by.carkva_gazeta.malitounik.BogaslujbovyiaMenu
import by.carkva_gazeta.malitounik.CytanniList
import by.carkva_gazeta.malitounik.DialogDelite
import by.carkva_gazeta.malitounik.KaliandarKnigaView
import by.carkva_gazeta.malitounik.KaliandarScreen
import by.carkva_gazeta.malitounik.KaliandarScreenInfo
import by.carkva_gazeta.malitounik.KaliandarScreenMounth
import by.carkva_gazeta.malitounik.KaliandarScreenYear
import by.carkva_gazeta.malitounik.LogView
import by.carkva_gazeta.malitounik.MaeNatatki
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MalitvyListAll
import by.carkva_gazeta.malitounik.PadzeiaView
import by.carkva_gazeta.malitounik.ParafiiBGKC
import by.carkva_gazeta.malitounik.Pashalia
import by.carkva_gazeta.malitounik.PiesnyList
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SearchBible
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.Settings.isNetworkAvailable
import by.carkva_gazeta.malitounik.SettingsView
import by.carkva_gazeta.malitounik.SviatyList
import by.carkva_gazeta.malitounik.SviatyiaView
import by.carkva_gazeta.malitounik.VybranaeList
import by.carkva_gazeta.malitounik.formatFigureTwoPlaces
import by.carkva_gazeta.malitounik.rawAsset
import by.carkva_gazeta.malitounik.removeZnakiAndSlovy
import by.carkva_gazeta.malitounik.searchJob
import by.carkva_gazeta.malitounik.searchList
import by.carkva_gazeta.malitounik.searchListSvityia
import by.carkva_gazeta.malitounik.setNotificationNon
import by.carkva_gazeta.malitounik.ui.theme.BackgroundTolBarDark
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryBlack
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Calendar
import kotlin.random.Random

data class AppNavGraphStateScroll(val title: String, var scrollPosition: Int)

data class AppNavGraphStateItems(val title: String, var isExpandet: Boolean = false)

object AppNavGraphState {
    var bibleItem by mutableStateOf(false)
    var biblijatekaItem by mutableStateOf(false)
    var piesnyItem by mutableStateOf(false)
    var underItem by mutableStateOf(false)
    var bottomSheetScaffoldIsVisible by mutableStateOf(false)
    val itemsValue = ArrayList<AppNavGraphStateItems>()
    val scrollValueList = ArrayList<AppNavGraphStateScroll>()
    var setAlarm = true
    var cytata = AnnotatedString("")
    var randomCytata = 0
    var autoDzenNochTime = System.currentTimeMillis()
    var searchSettings by mutableStateOf(false)
    var searchBogaslujbovyia by mutableStateOf("")

    fun setItemsValue(title: String, isInit: Boolean = false): Boolean {
        var result = true
        var find = false
        for (i in itemsValue.indices) {
            if (title == itemsValue[i].title) {
                if (!isInit) {
                    itemsValue[i].isExpandet = !itemsValue[i].isExpandet
                }
                result = itemsValue[i].isExpandet
                find = true
                break
            }
        }
        if (!find) {
            itemsValue.add(AppNavGraphStateItems(title, true))
            result = true
        }
        return result
    }

    fun getScrollValuePosition(title: String): Int {
        var result = 0
        for (i in scrollValueList.indices) {
            if (title == scrollValueList[i].title) {
                result = scrollValueList[i].scrollPosition
                break
            }
        }
        return result
    }

    fun setScrollValuePosition(title: String, position: Int) {
        var result = false
        for (i in scrollValueList.indices) {
            if (title == scrollValueList[i].title) {
                scrollValueList[i].scrollPosition = position
                result = true
                break
            }
        }
        if (!result) {
            scrollValueList.add(AppNavGraphStateScroll(title, position))
        }
    }

    fun getCytata(context: MainActivity, isNewCytata: Boolean) {
        val text = openAssetsResources(context, "citata.txt")
        val citataList = ArrayList<String>()
        val listText = text.split("\n")
        listText.forEach {
            val line = StringBuilder()
            val t1 = it.indexOf("(")
            if (t1 != -1) {
                line.append(it.substring(0, t1).trim())
                line.append("\n")
                line.append(it.substring(t1))
                citataList.add(line.toString())
            }
        }
        if (isNewCytata) randomCytata = Random.nextInt(citataList.size)
        cytata = AnnotatedString.Builder(citataList[randomCytata]).apply {
            addStyle(
                SpanStyle(
                    fontFamily = FontFamily(Font(R.font.andantinoscript)), fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = if (Settings.dzenNoch.value) PrimaryBlack else Primary, fontSize = (Settings.fontInterface + 4).sp
                ), 0, 1
            )
            addStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.comici))), 1, this.length)
        }.toAnnotatedString()
    }
}

fun openAssetsResources(context: Context, fileName: String): String {
    var result: String
    try {
        val inputStream = context.assets.open(fileName)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        if (fileName.contains("chytanne/")) {
            val builder = StringBuilder()
            reader.forEachLine {
                line = it
                if (line.contains("//")) {
                    val t1 = line.indexOf("//")
                    line = line.substring(0, t1).trim()
                    if (line.isNotEmpty()) builder.append(line).append("\n")
                } else {
                    builder.append(line).append("\n")
                }
            }
            result = builder.toString()
        } else {
            result = reader.readText()
        }
    } catch (_: FileNotFoundException) {
        val inputStream = context.assets.open("bogashlugbovya_error.html")
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        result = reader.readText()
    }
    return result
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val drawerScrollStete = rememberScrollState()
    val searchBibleState = rememberLazyListState()
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var start by remember { mutableStateOf(k.getString("navigate", AllDestinations.KALIANDAR) ?: AllDestinations.KALIANDAR) }
    val context = LocalActivity.current
    val data = context?.intent
    if (data?.data != null) {
        when {
            data.data.toString().contains("shortcuts=1") -> {
                start = AllDestinations.VYBRANAE_LIST
            }

            data.data.toString().contains("shortcuts=3") -> {
                start = AllDestinations.MAE_NATATKI_MENU
            }

            data.data.toString().contains("shortcuts=2") -> {
                start = AllDestinations.BIBLIJATEKA_NIADAUNIA
            }
        }
        Settings.destinations = start
        k.edit {
            putString("navigate", start)
        }
        context.intent?.data = null
    }
    val extras = context?.intent?.extras
    if (extras != null) {
        val widgetday = "widget_day"
        val widgetmun = "widget_mun"
        if (extras.getBoolean(widgetmun, false) || extras.getBoolean(widgetday, false) || extras.getBoolean("sabytie", false)) {
            start = if (k.getBoolean("caliandarList", false)) AllDestinations.KALIANDAR_YEAR
            else AllDestinations.KALIANDAR
            Settings.destinations = start
            k.edit {
                putString("navigate", start)
            }
        }
    }
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    NavHost(navController = navController, startDestination = start, enterTransition = { fadeIn(tween(durationMillis = 1000, easing = LinearOutSlowInEasing)) }, exitTransition = { fadeOut(tween(durationMillis = 1000, easing = LinearOutSlowInEasing)) }, popEnterTransition = { fadeIn(tween(durationMillis = 1000, easing = LinearOutSlowInEasing)) }, popExitTransition = { fadeOut(tween(durationMillis = 1000, easing = LinearOutSlowInEasing)) }) {
        composable(AllDestinations.KALIANDAR) {
            Settings.destinations = AllDestinations.KALIANDAR
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.AKAFIST_MENU) {
            Settings.destinations = AllDestinations.AKAFIST_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.LITURGIKON_MENU) {
            Settings.destinations = AllDestinations.LITURGIKON_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.CHASASLOU_MENU) {
            Settings.destinations = AllDestinations.CHASASLOU_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.MAE_NATATKI_MENU) {
            Settings.destinations = AllDestinations.MAE_NATATKI_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BOGASLUJBOVYIA_MENU) {
            Settings.destinations = AllDestinations.BOGASLUJBOVYIA_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.MALITVY_MENU) {
            Settings.destinations = AllDestinations.MALITVY_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIA_SEMUXA) {
            Settings.destinations = AllDestinations.BIBLIA_SEMUXA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIA_BOKUNA) {
            Settings.destinations = AllDestinations.BIBLIA_BOKUNA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIA_NADSAN) {
            Settings.destinations = AllDestinations.BIBLIA_NADSAN
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIA_CHARNIAUSKI) {
            Settings.destinations = AllDestinations.BIBLIA_CHARNIAUSKI
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIA_SINODAL) {
            Settings.destinations = AllDestinations.BIBLIA_SINODAL
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.KALIANDAR_YEAR) {
            Settings.destinations = AllDestinations.KALIANDAR_YEAR
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.VYBRANAE_LIST) {
            Settings.destinations = AllDestinations.VYBRANAE_LIST
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIJATEKA_NIADAUNIA) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_NIADAUNIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIJATEKA_SPEUNIKI) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_SPEUNIKI
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIJATEKA_GISTORYIA) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_GISTORYIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIJATEKA_MALITOUNIKI) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_MALITOUNIKI
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.PIESNY_PRASLAULENNIA) {
            Settings.destinations = AllDestinations.PIESNY_PRASLAULENNIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.PIESNY_DA_BAGARODZICY) {
            Settings.destinations = AllDestinations.PIESNY_DA_BAGARODZICY
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.PIESNY_ZA_BELARUS) {
            Settings.destinations = AllDestinations.PIESNY_ZA_BELARUS
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.PIESNY_KALIADNYIA) {
            Settings.destinations = AllDestinations.PIESNY_KALIADNYIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.PIESNY_TAIZE) {
            Settings.destinations = AllDestinations.PIESNY_TAIZE
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.UNDER_PADRYXTOUKA) {
            Bogaslujbovyia(navController, stringResource(R.string.spovedz), "padryxtouka_da_spovedzi.html")
        }

        composable(AllDestinations.SETTINGS_VIEW) {
            SettingsView(navController)
        }

        composable(AllDestinations.PADZEI_VIEW) {
            PadzeiaView(navController)
        }

        composable(AllDestinations.UMOUNIA_ZNACHENNI) {
            KaliandarScreenInfo(navController)
        }

        composable(AllDestinations.SVITYIA_VIEW + "/{svity}/{position}", arguments = listOf(navArgument("svity") { type = NavType.BoolType }, navArgument("position") { type = NavType.IntType })) { stackEntry ->
            val svity = stackEntry.arguments?.getBoolean("svity") == true
            val position = stackEntry.arguments?.getInt("position") ?: Settings.caliandarPosition
            SviatyiaView(navController, svity, position)
        }

        composable(AllDestinations.UNDER_PAMIATKA) {
            Bogaslujbovyia(navController, stringResource(R.string.pamiatka), "pamiatka.html")
        }

        composable(AllDestinations.PRANAS) {
            Bogaslujbovyia(navController, stringResource(R.string.pra_nas), "onas.html")
        }

        composable(AllDestinations.HELP) {
            Bogaslujbovyia(navController, stringResource(R.string.help), "help.html")
        }

        composable(AllDestinations.UNDER_SVAITY_MUNU) {
            Settings.destinations = AllDestinations.UNDER_SVAITY_MUNU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.UNDER_PARAFII_BGKC) {
            Settings.destinations = AllDestinations.UNDER_PARAFII_BGKC
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(AllDestinations.UNDER_PASHALIA) {
            Settings.destinations = AllDestinations.UNDER_PASHALIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA + "/{title}/{fileName}"
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val fileName = stackEntry.arguments?.getString("fileName") ?: ""
            Biblijateka(navController, title, fileName)
        }

        composable(
            AllDestinations.MALITVY_LIST_ALL + "/{title}/{menuItem}/{subTitle}", arguments = listOf(navArgument("menuItem") { type = NavType.IntType })
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val subTitle = stackEntry.arguments?.getString("subTitle") ?: ""
            val menuItemt = stackEntry.arguments?.getInt("menuItem") ?: Settings.MENU_BOGASLUJBOVYIA
            MalitvyListAll(navController, title, menuItemt, subTitle)
        }

        composable(
            AllDestinations.BOGASLUJBOVYIA + "/{title}/{resurs}",
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val context = LocalContext.current
            val resources = LocalResources.current
            val resurs = stackEntry.arguments?.getString("resurs") ?: "bogashlugbovya_error.html"
            Bogaslujbovyia(
                navController, title, resurs, navigateTo = { navigate, skipUtran ->
                    when (navigate) {
                        "error" -> {
                            navigationActions.navigateToBogaslujbovyia(context.getString(R.string.error_ch2), "bogashlugbovya_error.html")
                        }

                        "malitvypasliaprychastia" -> {
                            navigationActions.navigateToMalitvyListAll("МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ", Settings.MENU_MALITVY_PASLIA_PRYCHASCIA)
                        }

                        "litciaiblaslavennechl" -> {
                            navigationActions.navigateToBogaslujbovyia("Ліцьця і блаславеньне хлябоў", "bogashlugbovya/viaczernia_liccia_i_blaslavenne_chliabou.html")
                        }

                        "gliadzitutdabraveshchane" -> {
                            navigationActions.navigateToBogaslujbovyia("Дабравешчаньне Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_25_03_dabravieszczannie_viaczernia_z_liturhijaj.html")
                        }

                        "cytanne", "cytannedop" -> {
                            val data = findCaliandarToDay(context)
                            val titleCh = context.getString(
                                R.string.czytanne3, data[1].toInt(), resources.getStringArray(R.array.meciac_smoll)[2]
                            )
                            val skip = if (skipUtran) -2 else -1
                            navigationActions.navigateToCytanniList(
                                titleCh, removeZnakiAndSlovy(if (navigate == "cytanne") data[9] else data[11]), Settings.CHYTANNI_LITURGICHNYIA, Settings.PEREVODSEMUXI, skip
                            )
                        }

                        "cytannesvityx" -> {
                            val data = findCaliandarToDay(context)
                            val titleCh = context.getString(
                                R.string.czytanne3, data[1].toInt(), resources.getStringArray(R.array.meciac_smoll)[2]
                            )
                            navigationActions.navigateToCytanniList(
                                titleCh, removeZnakiAndSlovy(data[10]), Settings.CHYTANNI_LITURGICHNYIA, Settings.PEREVODSEMUXI, -1
                            )
                        }
                    }
                })
        }

        composable(
            AllDestinations.CYTANNI_LIST + "/{cytanne}/{title}/{biblia}/{perevod}/{position}", arguments = listOf(navArgument("biblia") { type = NavType.IntType }, navArgument("position") { type = NavType.IntType })
        ) { stackEntry ->
            val cytanne = stackEntry.arguments?.getString("cytanne") ?: ""
            val title = stackEntry.arguments?.getString("title") ?: ""
            val biblia = stackEntry.arguments?.getInt("biblia", Settings.CHYTANNI_LITURGICHNYIA) ?: Settings.CHYTANNI_LITURGICHNYIA
            Settings.destinations = AllDestinations.CYTANNI_LIST
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI
            val position = stackEntry.arguments?.getInt("position", 0) ?: 0
            CytanniList(navController, title, cytanne, biblia, perevod, position)
        }

        composable(
            AllDestinations.BIBLIA_LIST + "/{novyZapavet}/{perevod}", arguments = listOf(
                navArgument("novyZapavet") { type = NavType.BoolType })
        ) { stackEntry ->
            val isNovyZapavet = stackEntry.arguments?.getBoolean("novyZapavet", false) == true
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI
            BibliaList(
                navController, isNovyZapavet, perevod, navigateToCytanniList = { chytanne, perevod2 ->
                    navigationActions.navigateToCytanniList(
                        "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, -1
                    )
                })
        }

        composable(
            AllDestinations.SEARCH_BIBLIA + "/{perevod}/{searchBogaslujbovyia}", arguments = listOf(navArgument("searchBogaslujbovyia") { type = NavType.BoolType })
        ) { stackEntry ->
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI
            val searchBogaslujbovyia = stackEntry.arguments?.getBoolean("searchBogaslujbovyia", false) == true
            SearchBible(navController, searchBibleState, perevod, searchBogaslujbovyia, navigateToCytanniList = { chytanne, position, perevod2 ->
                navigationActions.navigateToCytanniList(
                    "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, position
                )
            }, navigateToBogaslujbovyia = { title, resurs ->
                navigationActions.navigateToBogaslujbovyia(title, resurs)
            })
        }
    }
}

fun findCaliandarToDay(context: Context, isGlobal: Boolean = true): ArrayList<String> {
    if (Settings.data.isEmpty()) {
        val gson = Gson()
        val type = TypeToken.getParameterized(
            ArrayList::class.java, TypeToken.getParameterized(
                ArrayList::class.java, String::class.java
            ).type
        ).type
        val inputStream = context.resources.openRawResource(R.raw.caliandar)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = reader.use {
            it.readText()
        }
        Settings.data.addAll(gson.fromJson(builder, type))
    }
    var caliandarPosition = Settings.caliandarPosition
    val calendar = Calendar.getInstance()
    for (i in Settings.data.indices) {
        if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
            caliandarPosition = i
            if (isGlobal) {
                Settings.caliandarPosition = i
            }
            break
        }
    }
    return Settings.data[caliandarPosition]
}

fun findCaliandarPosition(context: Context) {
    if (Settings.data.isEmpty()) {
        val gson = Gson()
        val type = TypeToken.getParameterized(
            ArrayList::class.java, TypeToken.getParameterized(
                ArrayList::class.java, String::class.java
            ).type
        ).type
        val inputStream = context.resources.openRawResource(R.raw.caliandar)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = reader.use {
            it.readText()
        }
        Settings.data.addAll(gson.fromJson(builder, type))
    }
    val calendar = Calendar.getInstance()
    for (i in Settings.data.indices) {
        if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
            Settings.caliandarPosition = i
            break
        }
    }
}

@Composable
fun CheckUpdateMalitounik(
    onDownloadComplet: () -> Unit, updateDownloadProgress: (Boolean, Float) -> Unit, onDismiss: () -> Unit
) {
    var noWIFI by remember { mutableStateOf(false) }
    var totalBytesToDownload by remember { mutableFloatStateOf(0f) }
    var bytesDownload by remember { mutableFloatStateOf(0f) }
    var isCompletDownload by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            updateDownloadProgress(true, 0f)
        } else {
            onDismiss()
        }
    }
    val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            bytesDownload = state.bytesDownloaded().toFloat()
            totalBytesToDownload = state.totalBytesToDownload().toFloat()
            updateDownloadProgress(true, bytesDownload / totalBytesToDownload)
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            updateDownloadProgress(false, 1f)
            isCompletDownload = true
            onDownloadComplet()
        }
    }
    if (isCompletDownload) {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
        isCompletDownload = false
    }
    if (noWIFI) {
        DialogUpdateNoWiFI(totalBytesToDownload, {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager.registerListener(installStateUpdatedListener)
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, launcher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                }
            }
            noWIFI = false
        }) {
            onDismiss()
            noWIFI = false
        }
    }
    LaunchedEffect(Unit) {
        if (isNetworkAvailable(context)) {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    if (isNetworkAvailable(context, Settings.TRANSPORT_CELLULAR)) {
                        totalBytesToDownload = appUpdateInfo.totalBytesToDownload().toFloat()
                        noWIFI = true
                    } else {
                        appUpdateManager.registerListener(installStateUpdatedListener)
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, launcher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConteiner(
    navController: NavHostController,
    drawerScrollStete: ScrollState
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: AllDestinations.KALIANDAR
    val context = LocalActivity.current as MainActivity
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var isInstallApp by remember { mutableStateOf(k.getBoolean("isInstallApp", false)) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var progressApp by remember { mutableFloatStateOf(0f) }
    var appUpdate by remember { mutableStateOf(false) }
    if (appUpdate) {
        CheckUpdateMalitounik(onDownloadComplet = {
            isInstallApp = true
            k.edit {
                putBoolean("isInstallApp", true)
            }
        }, updateDownloadProgress = { isVisable, progress ->
            isProgressVisable = isVisable
            progressApp = progress
        }) {
            appUpdate = false
        }
    }
    LaunchedEffect(Unit) {
        appUpdate = true
    }
    if (Settings.data.isEmpty() || Settings.caliandarPosition == -1) {
        findCaliandarPosition(context)
    }
    val lazyColumnState = rememberLazyListState()
    val lazyColumnStateSearchSvityia = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = {
        Settings.data.size
    }, initialPage = Settings.caliandarPosition)
    var showDropdown by remember { mutableStateOf(false) }
    var showDropdownMenuPos by rememberSaveable { mutableIntStateOf(1) }
    var searchText by rememberSaveable { mutableStateOf(false) }
    BackHandler(drawerState.isClosed || showDropdown || searchText) {
        when {
            searchText -> searchText = false
            drawerState.isClosed -> coroutineScope.launch { drawerState.open() }
        }
        showDropdown = false
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
    LaunchedEffect(Unit) {
        val extras = context.intent?.extras
        if (extras != null) {
            val widgetday = "widget_day"
            val widgetmun = "widget_mun"
            if (extras.getBoolean(widgetmun, false)) {
                val caliandarPosition = extras.getInt("position", Settings.caliandarPosition)
                coroutineScope.launch {
                    if (k.getBoolean(
                            "caliandarList", false
                        )
                    ) {
                        Settings.caliandarPosition = caliandarPosition
                        lazyColumnState.scrollToItem(caliandarPosition)
                    } else pagerState.scrollToPage(caliandarPosition)
                }
            }
            if (extras.getBoolean(widgetday, false)) {
                val caliandarPosition = extras.getInt("position", Settings.caliandarPosition)
                if (k.getBoolean("caliandarList", false)) {
                    coroutineScope.launch {
                        Settings.caliandarPosition = caliandarPosition
                        lazyColumnState.scrollToItem(caliandarPosition)
                    }
                } else {
                    coroutineScope.launch {
                        pagerState.scrollToPage(caliandarPosition)
                    }
                }
            }
            if (extras.getBoolean("sabytie", false)) {
                val svitaPosition = extras.getInt("caliandarPosition")
                coroutineScope.launch {
                    if (k.getBoolean(
                            "caliandarList", false
                        )
                    ) {
                        Settings.caliandarPosition = svitaPosition
                        lazyColumnState.scrollToItem(svitaPosition)
                    } else {
                        pagerState.scrollToPage(svitaPosition)
                    }
                }
            }
        }
        context.intent = null
    }
    var navigateIsSpecial by remember { mutableStateOf(false) }
    if (drawerState.isOpen) isAppearanceLight = !Settings.dzenNoch.value
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = if (navigateIsSpecial) {
                false
            } else {
                isAppearanceLight
            }
            isAppearanceLightNavigationBars = when {
                navigateIsSpecial -> {
                    false
                }

                Settings.destinations == AllDestinations.KALIANDAR -> isAppearanceLight
                else -> !Settings.dzenNoch.value
            }
        }
    }
    var sortedVybranae by remember {
        mutableIntStateOf(
            k.getInt(
                "sortedVybranae", Settings.SORT_BY_ABC
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
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    if (logView) {
        DialogLogProgramy {
            logView = false
        }
    }
    if (removeAllVybranaeDialog || removeAllNatatkiDialog) {
        DialogDelite(
            title = if (removeAllVybranaeDialog) stringResource(R.string.del_all_vybranoe)
            else stringResource(R.string.delite_all_natatki), onDismiss = {
                removeAllVybranaeDialog = false
                removeAllNatatkiDialog = false
            }, onConfirmation = {
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
            })
    }
    if (k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL) != Settings.NOTIFICATION_SVIATY_NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                k.edit {
                    putInt("notification", Settings.NOTIFICATION_SVIATY_NONE)
                }
                setNotificationNon(context)
            }
        }
    }
    var isToDay by remember { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.onTertiary
    var colorBlackboard by remember { mutableStateOf(color) }
    var dialogKniga by remember { mutableStateOf(false) }
    ModalNavigationDrawer(
        drawerContent = {
            DrawView(
                drawerScrollStete = drawerScrollStete,
                route = currentRoute,
                navigateToRazdel = { razdzel ->
                    if (razdzel == AllDestinations.KALIANDAR || razdzel == AllDestinations.KALIANDAR_YEAR) {
                        searchListSvityia.clear()
                        Settings.textFieldValueState.value = ""
                        Settings.textFieldValueLatest.value = ""
                    }
                    when (razdzel) {
                        AllDestinations.KALIANDAR -> {
                            if (k.getBoolean("caliandarList", false)) navigationActions.navigateToKaliandarYear()
                            else navigationActions.navigateToKaliandar()
                        }

                        AllDestinations.BOGASLUJBOVYIA_MENU -> navigationActions.navigateToBogaslujbovyiaMenu()
                        AllDestinations.MALITVY_MENU -> navigationActions.navigateToMalitvyMenu()
                        AllDestinations.BIBLIA_SEMUXA -> {
                            searchList.clear()
                            Settings.textFieldValueState.value = ""
                            Settings.textFieldValueLatest.value = ""
                            navigationActions.navigateToBibliaSemuxa()
                        }

                        AllDestinations.BIBLIA_BOKUNA -> {
                            searchList.clear()
                            Settings.textFieldValueState.value = ""
                            Settings.textFieldValueLatest.value = ""
                            navigationActions.navigateToBibliaBokuna()
                        }

                        AllDestinations.BIBLIA_NADSAN -> {
                            searchList.clear()
                            Settings.textFieldValueState.value = ""
                            Settings.textFieldValueLatest.value = ""
                            navigationActions.navigateToBibliaNadsan()
                        }

                        AllDestinations.BIBLIA_CHARNIAUSKI -> {
                            searchList.clear()
                            Settings.textFieldValueState.value = ""
                            Settings.textFieldValueLatest.value = ""
                            navigationActions.navigateToBibliaCharniauski()
                        }

                        AllDestinations.BIBLIA_SINODAL -> {
                            searchList.clear()
                            Settings.textFieldValueState.value = ""
                            Settings.textFieldValueLatest.value = ""
                            navigationActions.navigateToBibliaSinodal()
                        }

                        AllDestinations.VYBRANAE_LIST -> navigationActions.navigateToVybranaeList()
                        AllDestinations.AKAFIST_MENU -> navigationActions.navigateToAkafistMenu()
                        AllDestinations.LITURGIKON_MENU -> navigationActions.navigateToLiturgikonMenu()
                        AllDestinations.CHASASLOU_MENU -> navigationActions.navigateToChasaslouMenu()
                        AllDestinations.MAE_NATATKI_MENU -> navigationActions.navigateToMaeNatatkiMenu()
                        AllDestinations.BIBLIJATEKA_NIADAUNIA -> navigationActions.navigateToBiblijatekaList(AllDestinations.BIBLIJATEKA_NIADAUNIA)
                        AllDestinations.BIBLIJATEKA_MALITOUNIKI -> navigationActions.navigateToBiblijatekaList(AllDestinations.BIBLIJATEKA_MALITOUNIKI)
                        AllDestinations.BIBLIJATEKA_GISTORYIA -> navigationActions.navigateToBiblijatekaList(AllDestinations.BIBLIJATEKA_GISTORYIA)
                        AllDestinations.BIBLIJATEKA_SPEUNIKI -> navigationActions.navigateToBiblijatekaList(AllDestinations.BIBLIJATEKA_SPEUNIKI)
                        AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> navigationActions.navigateToBiblijatekaList(AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU)
                        AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> navigationActions.navigateToBiblijatekaList(AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA)
                        AllDestinations.PIESNY_PRASLAULENNIA -> navigationActions.navigateToPiesnyList(AllDestinations.PIESNY_PRASLAULENNIA)
                        AllDestinations.PIESNY_DA_BAGARODZICY -> navigationActions.navigateToPiesnyList(AllDestinations.PIESNY_DA_BAGARODZICY)
                        AllDestinations.PIESNY_ZA_BELARUS -> navigationActions.navigateToPiesnyList(AllDestinations.PIESNY_ZA_BELARUS)
                        AllDestinations.PIESNY_KALIADNYIA -> navigationActions.navigateToPiesnyList(AllDestinations.PIESNY_KALIADNYIA)
                        AllDestinations.PIESNY_TAIZE -> navigationActions.navigateToPiesnyList(AllDestinations.PIESNY_TAIZE)
                        AllDestinations.UNDER_PADRYXTOUKA -> {
                            navigateIsSpecial = true
                            navigationActions.navigateToPadryxtouka()
                        }

                        AllDestinations.UNDER_PAMIATKA -> {
                            navigateIsSpecial = true
                            navigationActions.navigateToPamiatka()
                        }

                        AllDestinations.PRANAS -> {
                            navigateIsSpecial = true
                            navigationActions.navigateToPraNas()
                        }

                        AllDestinations.HELP -> {
                            navigateIsSpecial = true
                            navigationActions.navigateToHelp()
                        }

                        AllDestinations.UNDER_SVAITY_MUNU -> navigationActions.navigateToSviaty()
                        AllDestinations.UNDER_PARAFII_BGKC -> navigationActions.navigateToParafiiBgkc()
                        AllDestinations.UNDER_PASHALIA -> navigationActions.navigateToPashalia()
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
            )
        }, drawerState = drawerState
    ) {
        var tollBarColor by remember { mutableStateOf(if (Settings.dzenNoch.value) BackgroundTolBarDark else Primary) }
        var textTollBarColor by remember { mutableStateOf(PrimaryTextBlack) }
        var title by rememberSaveable {
            mutableStateOf("")
        }
        var isBottomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
        title = when (currentRoute) {
            AllDestinations.KALIANDAR -> stringResource(R.string.kaliandar2)
            AllDestinations.KALIANDAR_YEAR -> stringResource(R.string.kaliandar2)
            AllDestinations.BOGASLUJBOVYIA_MENU -> stringResource(R.string.bogaslugbovyia_teksty)
            AllDestinations.AKAFIST_MENU -> stringResource(R.string.akafisty)
            AllDestinations.CHASASLOU_MENU -> stringResource(R.string.chasaslou)
            AllDestinations.LITURGIKON_MENU -> stringResource(R.string.liturgikon)
            AllDestinations.MALITVY_MENU -> stringResource(R.string.malitvy)
            AllDestinations.VYBRANAE_LIST -> stringResource(R.string.MenuVybranoe)
            AllDestinations.MAE_NATATKI_MENU -> stringResource(R.string.maje_natatki)
            AllDestinations.BIBLIJATEKA_NIADAUNIA -> stringResource(R.string.bibliateka_niadaunia)
            AllDestinations.BIBLIJATEKA_GISTORYIA -> stringResource(R.string.bibliateka_gistoryia_carkvy)
            AllDestinations.BIBLIJATEKA_MALITOUNIKI -> stringResource(R.string.bibliateka_malitouniki)
            AllDestinations.BIBLIJATEKA_SPEUNIKI -> stringResource(R.string.bibliateka_speuniki)
            AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> stringResource(R.string.bibliateka_rel_litaratura)
            AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> stringResource(R.string.arx_num_gaz)
            AllDestinations.PIESNY_PRASLAULENNIA -> stringResource(R.string.pesny1)
            AllDestinations.PIESNY_ZA_BELARUS -> stringResource(R.string.pesny2)
            AllDestinations.PIESNY_DA_BAGARODZICY -> stringResource(R.string.pesny3)
            AllDestinations.PIESNY_KALIADNYIA -> stringResource(R.string.pesny4)
            AllDestinations.PIESNY_TAIZE -> stringResource(R.string.pesny5)
            AllDestinations.UNDER_SVAITY_MUNU -> stringResource(R.string.sviaty)
            AllDestinations.UNDER_PARAFII_BGKC -> stringResource(R.string.parafii)
            AllDestinations.UNDER_PASHALIA -> stringResource(R.string.paschalia)
            AllDestinations.BIBLIA_SEMUXA -> stringResource(R.string.title_biblia)
            AllDestinations.BIBLIA_BOKUNA -> stringResource(R.string.title_biblia_bokun)
            AllDestinations.BIBLIA_NADSAN -> stringResource(R.string.title_psalter)
            AllDestinations.BIBLIA_CHARNIAUSKI -> stringResource(R.string.title_biblia_charniauski)
            AllDestinations.BIBLIA_SINODAL -> stringResource(R.string.bsinaidal)
            else -> ""
        }
        var expandedUp by remember { mutableStateOf(false) }
        val searchBibleState = rememberLazyListState()
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    if (!searchText) {
                        Text(
                            title.uppercase(), color = textTollBarColor, fontWeight = FontWeight.Bold, fontSize = Settings.fontInterface.sp
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
                                }, value = Settings.textFieldValueState.value, onValueChange = { newText ->
                                var edit = newText
                                edit = edit.replace("и", "і")
                                edit = edit.replace("щ", "ў")
                                edit = edit.replace("И", "І")
                                edit = edit.replace("Щ", "Ў")
                                edit = edit.replace("ъ", "'")
                                Settings.textFieldValueState.value = edit
                            }, singleLine = true, leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.search), tint = textTollBarColor, contentDescription = ""
                                )
                            }, trailingIcon = {
                                if (Settings.textFieldValueState.value.isNotEmpty()) {
                                    IconButton(onClick = {
                                        Settings.textFieldValueState.value = ""
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.close), contentDescription = "", tint = textTollBarColor
                                        )
                                    }
                                }
                            }, colors = TextFieldDefaults.colors(
                                focusedContainerColor = tollBarColor, unfocusedContainerColor = tollBarColor, focusedTextColor = textTollBarColor, unfocusedTextColor = textTollBarColor, focusedIndicatorColor = textTollBarColor, unfocusedIndicatorColor = textTollBarColor, cursorColor = textTollBarColor
                            ), textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                        )
                    }
                }, navigationIcon = {
                    if (searchText) {
                        IconButton(onClick = {
                            searchText = false
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.close), tint = textTollBarColor, contentDescription = ""
                            )
                        })
                    } else {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }, content = {
                            Icon(
                                painter = painterResource(R.drawable.menu), tint = textTollBarColor, contentDescription = ""
                            )
                        })
                    }
                }, actions = {
                    if (!searchText) {
                        if (!isBottomBar && (currentRoute == AllDestinations.KALIANDAR || currentRoute == AllDestinations.KALIANDAR_YEAR)) {
                            Text(
                                text = Calendar.getInstance()[Calendar.DATE].toString(),
                                modifier = Modifier
                                    .clickable {
                                        showDropdownMenuPos = 1
                                        showDropdown = true
                                    }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                                    .clip(shape = RoundedCornerShape(3.dp))
                                    .background(textTollBarColor)
                                    .padding(1.dp)
                                    .clip(shape = RoundedCornerShape(3.dp))
                                    .background(if (isToDay) Divider else StrogiPost)
                                    .padding(horizontal = 5.dp),
                                fontSize = 14.sp,
                                color = if (isToDay) PrimaryText else PrimaryTextBlack
                            )
                            IconButton(onClick = {
                                k.edit {
                                    if (k.getBoolean("caliandarList", false)) {
                                        navigationActions.navigateToKaliandar()
                                        putBoolean("caliandarList", false)
                                    } else {
                                        putBoolean("caliandarList", true)
                                        navigationActions.navigateToKaliandarYear()
                                    }
                                }
                            }) {
                                val icon = if (k.getBoolean("caliandarList", false)) painterResource(R.drawable.calendar_today)
                                else painterResource(R.drawable.list)
                                Icon(
                                    painter = icon, contentDescription = "", tint = textTollBarColor
                                )
                            }
                        }
                        if (currentRoute == AllDestinations.MAE_NATATKI_MENU) {
                            IconButton({
                                addFileNatatki = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.add), tint = textTollBarColor, contentDescription = ""
                                )
                            }
                        }
                        if (currentRoute.contains(AllDestinations.VYBRANAE_LIST) || currentRoute.contains(AllDestinations.MAE_NATATKI_MENU)) {
                            IconButton(onClick = {
                                expandedUp = false
                                sortedVybranae = if (sortedVybranae == Settings.SORT_BY_ABC) Settings.SORT_BY_TIME
                                else Settings.SORT_BY_ABC
                                sortedNatatki = if (sortedNatatki == Settings.SORT_BY_ABC) Settings.SORT_BY_TIME
                                else Settings.SORT_BY_ABC
                                k.edit {
                                    if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) putInt(
                                        "sortedVybranae", sortedVybranae
                                    )
                                    else putInt("natatki_sort", sortedNatatki)
                                }
                            }) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) {
                                        if (sortedVybranae == Settings.SORT_BY_TIME) {
                                            painterResource(R.drawable.sort_by_az)
                                        } else {
                                            painterResource(R.drawable.sort)
                                        }
                                    } else {
                                        if (sortedNatatki == Settings.SORT_BY_TIME) {
                                            painterResource(R.drawable.sort_by_az)
                                        } else {
                                            painterResource(R.drawable.sort)
                                        }
                                    }, contentDescription = "", tint = textTollBarColor
                                )
                            }
                        }
                        if (currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.MAE_NATATKI_MENU) {
                            IconButton({
                                if (currentRoute == AllDestinations.VYBRANAE_LIST) removeAllVybranaeDialog = !removeAllVybranaeDialog
                                else removeAllNatatkiDialog = !removeAllNatatkiDialog
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.delete), tint = textTollBarColor, contentDescription = ""
                                )
                            }
                        }
                        if (currentRoute == AllDestinations.LITURGIKON_MENU || currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.CHASASLOU_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute.contains("BIBLIJATEKA", ignoreCase = true) || currentRoute.contains("PIESNY", ignoreCase = true) || currentRoute == AllDestinations.UNDER_PASHALIA || currentRoute.contains("BIBLIA", ignoreCase = true)) {
                            IconButton({
                                searchText = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.search), tint = textTollBarColor, contentDescription = ""
                                )
                            }
                        }
                        IconButton(onClick = { expandedUp = true }) {
                            Icon(
                                painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = textTollBarColor
                            )
                        }
                    }
                    if (searchText && currentRoute.contains("BIBLIA", ignoreCase = true)) {
                        IconButton(onClick = { AppNavGraphState.searchSettings = true }) {
                            Icon(
                                painter = painterResource(R.drawable.settings), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                        DropdownMenuItem(onClick = {
                            expandedUp = false
                            navigationActions.navigateToSettingsView()
                        }, text = { Text(stringResource(R.string.tools_item), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.settings), contentDescription = ""
                            )
                        })
                        if (currentRoute == AllDestinations.KALIANDAR || currentRoute == AllDestinations.KALIANDAR_YEAR) {
                            DropdownMenuItem(onClick = {
                                expandedUp = false
                                navigationActions.navigateToUmouniaZnachenni()
                            }, text = { Text(stringResource(R.string.symbols_menu), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.info), contentDescription = ""
                                )
                            })
                            if (!isBottomBar) {
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    navigationActions.navigateToPadzeiView()
                                }, text = { Text(stringResource(R.string.sabytie), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.event), contentDescription = ""
                                    )
                                })
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    searchText = true
                                }, text = { Text(stringResource(R.string.poshuk), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.search), contentDescription = ""
                                    )
                                })
                            }
                        }
                        if (k.getBoolean("admin", false)) {
                            HorizontalDivider()
                            if (currentRoute.contains(AllDestinations.KALIANDAR) || currentRoute.contains("BIBLIJATEKA", ignoreCase = true)) {
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    if (context.checkmodulesAdmin()) {
                                        val intent = Intent()
                                        if (currentRoute.contains(AllDestinations.KALIANDAR)) {
                                            intent.setClassName(context, "by.carkva_gazeta.admin.Sviatyia")
                                            intent.putExtra("dayOfYear", Settings.data[Settings.caliandarPosition][24].toInt())
                                        } else {
                                            intent.setClassName(context, "by.carkva_gazeta.admin.BibliatekaList")
                                        }
                                        context.startActivity(intent)
                                    }
                                }, text = { Text(stringResource(R.string.redagaktirovat), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.edit), contentDescription = ""
                                    )
                                })
                            }
                            if (currentRoute == AllDestinations.LITURGIKON_MENU || currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.CHASASLOU_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU) {
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    navigationActions.navigateToSearchBiblia(Settings.PEREVODSEMUXI, true)
                                }, text = { Text(stringResource(R.string.searche_bogasluz_text), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.search), contentDescription = ""
                                    )
                                })
                            }
                            DropdownMenuItem(onClick = {
                                expandedUp = false
                                logView = true
                            }, text = { Text(stringResource(R.string.log_m), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.description), contentDescription = ""
                                )
                            })
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(tollBarColor)
            )
        }, bottomBar = {
            if (!(currentRoute == AllDestinations.LITURGIKON_MENU || currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.CHASASLOU_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute.contains("BIBLIJATEKA", ignoreCase = true) || currentRoute.contains("PIESNY", ignoreCase = true) || currentRoute == AllDestinations.UNDER_PASHALIA || currentRoute == AllDestinations.UNDER_PARAFII_BGKC || currentRoute == AllDestinations.UNDER_SVAITY_MUNU || currentRoute.contains("BIBLIA", ignoreCase = true) || currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.MAE_NATATKI_MENU)) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                if (!searchText) {
                    if (showDropdown) {
                        ModalBottomSheet(
                            sheetState = sheetState, properties = ModalBottomSheetProperties(
                                isAppearanceLightStatusBars = isAppearanceLight, isAppearanceLightNavigationBars = if (Settings.destinations == AllDestinations.KALIANDAR) isAppearanceLight
                                else !Settings.dzenNoch.value
                            ), containerColor = MaterialTheme.colorScheme.surfaceContainer, onDismissRequest = { showDropdown = false }) {
                            if (showDropdownMenuPos == 1) {
                                KaliandarScreenMounth(
                                    setPageCaliandar = { date ->
                                        showDropdown = false
                                        coroutineScope.launch {
                                            if (k.getBoolean(
                                                    "caliandarList", false
                                                )
                                            ) lazyColumnState.scrollToItem(date)
                                            else pagerState.scrollToPage(date)
                                        }
                                    })
                            }
                        }
                    }
                    if (isBottomBar) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(tollBarColor)
                                .navigationBarsPadding(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentRoute == AllDestinations.KALIANDAR || currentRoute == AllDestinations.KALIANDAR_YEAR) {
                                IconButton(onClick = {
                                    searchText = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.search), contentDescription = "", tint = textTollBarColor
                                    )
                                }
                                IconButton(onClick = {
                                    navigationActions.navigateToPadzeiView()
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.event), contentDescription = "", tint = textTollBarColor
                                    )
                                }
                                IconButton({
                                    k.edit {
                                        if (k.getBoolean("caliandarList", false)) {
                                            navigationActions.navigateToKaliandar()
                                            putBoolean("caliandarList", false)
                                        } else {
                                            putBoolean("caliandarList", true)
                                            navigationActions.navigateToKaliandarYear()
                                        }
                                    }
                                }) {
                                    val icon = if (k.getBoolean("caliandarList", false)) painterResource(R.drawable.calendar_today)
                                    else painterResource(R.drawable.list)
                                    Icon(
                                        painter = icon, tint = textTollBarColor, contentDescription = ""
                                    )
                                }
                                Text(
                                    text = Calendar.getInstance()[Calendar.DATE].toString(),
                                    modifier = Modifier
                                        .clickable {
                                            showDropdownMenuPos = 1
                                            showDropdown = true
                                        }
                                        .padding(horizontal = 10.dp, vertical = 7.dp)
                                        .clip(shape = RoundedCornerShape(3.dp))
                                        .background(textTollBarColor)
                                        .padding(1.dp)
                                        .clip(shape = RoundedCornerShape(3.dp))
                                        .background(if (isToDay) Divider else StrogiPost)
                                        .padding(horizontal = 5.dp),
                                    fontSize = 14.sp,
                                    color = if (isToDay) PrimaryText else PrimaryTextBlack
                                )
                            }
                        }
                    }
                }
            }
        }, snackbarHost = {
            if (isProgressVisable) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = { progressApp })
            }
            if (isInstallApp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tollBarColor), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.update_program), modifier = Modifier.padding(start = 5.dp), color = textTollBarColor, fontSize = 18.sp
                    )
                    TextButton(
                        onClick = {
                            val appUpdateManager = AppUpdateManagerFactory.create(context)
                            appUpdateManager.completeUpdate()
                            k.edit {
                                putBoolean("isInstallApp", false)
                            }
                            isInstallApp = false
                        }, modifier = Modifier.padding(5.dp), colors = ButtonColors(
                            Divider, Color.Unspecified, Color.Unspecified, Color.Unspecified
                        ), shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = stringResource(R.string.restsrt_program), color = PrimaryText, fontSize = 18.sp
                        )
                    }
                }
            }
        }) { innerPadding ->
            Box(
                modifier = Modifier.padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr), innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
                )
            ) {
                when (Settings.destinations) {
                    AllDestinations.KALIANDAR -> {
                        val dataToDay = findCaliandarToDay(context, false)
                        val fling = PagerDefaults.flingBehavior(
                            state = pagerState, pagerSnapDistance = PagerSnapDistance.atMost(1)
                        )
                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.currentPage }.collect { page ->
                                Settings.caliandarPosition = page
                                val data = Settings.data[page]
                                isToDay = data[1] == dataToDay[1] && data[2] == dataToDay[2] && data[3] == dataToDay[3]
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
                                    window, view
                                ).apply {
                                    isAppearanceLightStatusBars = !isAppearanceLight
                                    isAppearanceLightNavigationBars = !isAppearanceLight
                                }
                            }
                        }
                        if (searchText) {
                            SearchSviatyia(lazyColumnStateSearchSvityia, innerPadding, setCaliandarPage = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(Settings.caliandarPosition)
                                }
                                searchText = false
                            })
                        } else {
                            HorizontalPager(
                                pageSpacing = 10.dp, state = pagerState, flingBehavior = fling, verticalAlignment = Alignment.Top, modifier = Modifier.padding(10.dp)
                            ) { page ->
                                KaliandarScreen(page, innerPadding, navigateToCytanneList = { title, chytanne, biblia ->
                                    navigationActions.navigateToCytanniList(
                                        title, chytanne, biblia, Settings.PEREVODSEMUXI, -1
                                    )
                                }, navigateToSvityiaView = { svity, position ->
                                    navigationActions.navigateToSvityiaView(svity, position)
                                }, navigateToBogaslujbovyia = { title, resurs ->
                                    navigationActions.navigateToBogaslujbovyia(title, resurs)
                                }, navigateToKniga = {
                                    dialogKniga = true
                                })
                            }
                        }
                    }

                    AllDestinations.BOGASLUJBOVYIA_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_BOGASLUJBOVYIA, searchText
                        )
                    }

                    AllDestinations.AKAFIST_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_AKAFIST, searchText
                        )
                    }

                    AllDestinations.BIBLIJATEKA_NIADAUNIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_NIADAUNIA, innerPadding, searchText)
                    }

                    AllDestinations.BIBLIJATEKA_SPEUNIKI -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_SPEUNIKI, innerPadding, searchText)
                    }

                    AllDestinations.BIBLIJATEKA_MALITOUNIKI -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_MALITOUNIKI, innerPadding, searchText)
                    }

                    AllDestinations.BIBLIJATEKA_GISTORYIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_GISTORYIA, innerPadding, searchText)
                    }

                    AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA, innerPadding, searchText)
                    }

                    AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU, innerPadding, searchText)
                    }

                    AllDestinations.PIESNY_PRASLAULENNIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_PRASLAULENNIA, innerPadding, searchText)
                    }

                    AllDestinations.PIESNY_ZA_BELARUS -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_ZA_BELARUS, innerPadding, searchText)
                    }

                    AllDestinations.PIESNY_DA_BAGARODZICY -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_DA_BAGARODZICY, innerPadding, searchText)
                    }

                    AllDestinations.PIESNY_KALIADNYIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_KALIADNYIA, innerPadding, searchText)
                    }

                    AllDestinations.PIESNY_TAIZE -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_TAIZE, innerPadding, searchText)
                    }

                    AllDestinations.UNDER_SVAITY_MUNU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        SviatyList(navController, innerPadding)
                    }

                    AllDestinations.UNDER_PARAFII_BGKC -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        ParafiiBGKC(navController, innerPadding)
                    }

                    AllDestinations.UNDER_PASHALIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        if (!searchText) Settings.textFieldValueState.value = ""
                        Pashalia(navController, innerPadding, searchText)
                    }

                    AllDestinations.CHASASLOU_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_CHASASLOU, searchText
                        )
                    }

                    AllDestinations.LITURGIKON_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_LITURGIKON, searchText
                        )
                    }

                    AllDestinations.MAE_NATATKI_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        MaeNatatki(
                            innerPadding, sortedNatatki, addFileNatatki, removeAllNatatki, onDismissAddFile = {
                                addFileNatatki = false
                            })
                    }

                    AllDestinations.MALITVY_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_MALITVY, searchText
                        )
                    }

                    AllDestinations.BIBLIA_SEMUXA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODSEMUXI, innerPadding, searchText, searchBibleState, navigateToCytanniList = { chytanne, position, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        })
                    }

                    AllDestinations.BIBLIA_BOKUNA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODBOKUNA, innerPadding, searchText, searchBibleState, navigateToCytanniList = { chytanne, position, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        })
                    }

                    AllDestinations.BIBLIA_NADSAN -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODNADSAN, innerPadding, searchText, searchBibleState, navigateToCytanniList = { chytanne, position, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        })
                    }

                    AllDestinations.BIBLIA_CHARNIAUSKI -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODCARNIAUSKI, innerPadding, searchText, searchBibleState, navigateToCytanniList = { chytanne, position, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        })
                    }

                    AllDestinations.BIBLIA_SINODAL -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODSINOIDAL, innerPadding, searchText, searchBibleState, navigateToCytanniList = { chytanne, position, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        })
                    }

                    AllDestinations.KALIANDAR_YEAR -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        val dataToDay = findCaliandarToDay(context, false)
                        LaunchedEffect(lazyColumnState) {
                            snapshotFlow { lazyColumnState.firstVisibleItemIndex }.collect { index ->
                                val data = Settings.data[index]
                                isToDay = data[1] == dataToDay[1] && data[2] == dataToDay[2] && data[3] == dataToDay[3]
                            }
                        }
                        if (searchText) {
                            SearchSviatyia(lazyColumnStateSearchSvityia, innerPadding, setCaliandarPage = {
                                coroutineScope.launch {
                                    lazyColumnState.scrollToItem(Settings.caliandarPosition)
                                }
                                searchText = false
                            })
                        } else {
                            KaliandarScreenYear(
                                coroutineScope = coroutineScope, lazyColumnState = lazyColumnState, innerPadding, navigateToSvityiaView = { svity, position ->
                                    navigationActions.navigateToSvityiaView(svity, position)
                                })
                        }
                    }

                    AllDestinations.VYBRANAE_LIST -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        VybranaeList(
                            navigateToCytanniList = { chytanne, position, perevod2 ->
                                navigationActions.navigateToCytanniList(
                                    "", chytanne, Settings.CHYTANNI_VYBRANAE, perevod2, position
                                )
                            }, navigateToBogaslujbovyia = { title, resourse ->
                                navigationActions.navigateToBogaslujbovyia(title, resourse)
                            }, sortedVybranae, removeAllVybranae, innerPadding
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            dialogKniga, enter = fadeIn(
                tween(
                    durationMillis = 500, easing = LinearOutSlowInEasing
                )
            ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
        ) {
            KaliandarKnigaView(colorBlackboard, navigateToBogaslujbovyia = { title, resourse ->
                dialogKniga = false
                navigationActions.navigateToBogaslujbovyia(title, resourse)
            }, navigateToSvityiaView = { svity, position ->
                dialogKniga = false
                navigationActions.navigateToSvityiaView(svity, position)
            }) {
                dialogKniga = false
            }
        }
    }
}

@Composable
fun SearchSviatyia(lazyColumnStateSearchSvityia: LazyListState, innerPadding: PaddingValues, setCaliandarPage: () -> Unit) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Settings.textFieldValueState.value) {
        if (Settings.textFieldValueState.value.trim().length >= 3 && Settings.textFieldValueState.value.trim() != Settings.textFieldValueLatest.value.trim()) {
            Settings.textFieldValueLatest.value = Settings.textFieldValueState.value.trim()
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                searchListSvityia.clear()
                val list = withContext(Dispatchers.IO) {
                    return@withContext rawAsset(context, Settings.textFieldValueState.value.trim())
                }
                searchListSvityia.addAll(list)
            }
        } else {
            searchJob?.cancel()
        }
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    Column {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = stringResource(R.string.searh_sviatyia_result, searchListSvityia.size),
            fontStyle = FontStyle.Italic,
            fontSize = Settings.fontInterface.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        LazyColumn(
            Modifier.nestedScroll(nestedScrollConnection),
            state = lazyColumnStateSearchSvityia
        ) {
            items(searchListSvityia.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            val calendar = Calendar.getInstance()
                            calendar[Calendar.DAY_OF_YEAR] = searchListSvityia[index].dayOfYear
                            for (e in Settings.data.indices) {
                                if (calendar[Calendar.DATE] == Settings.data[e][1].toInt() && calendar[Calendar.MONTH] == Settings.data[e][2].toInt() && calendar[Calendar.YEAR] == Settings.data[e][3].toInt()) {
                                    Settings.caliandarPosition = e
                                    break
                                }
                            }
                            setCaliandarPage()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp, 5.dp),
                        painter = painterResource(R.drawable.poiter),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                    Column {
                        Text(
                            text = searchListSvityia[index].opisanieData,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                        when (searchListSvityia[index].typeSviat) {
                            0 -> {
                                HtmlText(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    text = searchListSvityia[index].opisanie,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }

                            1 -> {
                                Text(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    text = searchListSvityia[index].opisanie,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }

                            2 -> {
                                Text(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    text = searchListSvityia[index].opisanie,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }

                            3 -> {
                                Text(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    text = searchListSvityia[index].opisanie,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }

                            else -> {
                                val t1 = searchListSvityia[index].opisanie.indexOf(":")
                                val annotatedString = if (t1 != -1) {
                                    buildAnnotatedString {
                                        append(searchListSvityia[index].opisanie)
                                        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t1 + 1)
                                    }
                                } else {
                                    AnnotatedString(searchListSvityia[index].opisanie)
                                }
                                Text(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    text = annotatedString,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun DialogLogProgramy(
    onDismiss: () -> Unit
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
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.log).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HtmlText(
                        modifier = Modifier.clickable {
                            logView.checkFiles()
                        }, text = item, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            logView.createAndSentFile()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.set_log), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogUpdateNoWiFI(
    totalBytesToDownload: Float, onConfirmation: () -> Unit, onDismiss: () -> Unit
) {
    val sizeProgram = if (totalBytesToDownload == 0f) {
        " "
    } else {
        " ${formatFigureTwoPlaces(totalBytesToDownload / 1024 / 1024)} Мб "
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.update_title2), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(text = stringResource(R.string.download_opisanie, sizeProgram), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = { onConfirmation() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}