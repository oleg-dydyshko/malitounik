package by.carkva_gazeta.malitounik.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
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
import androidx.compose.material3.DrawerValue
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
import androidx.compose.material3.TooltipAnchorPosition
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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
import by.carkva_gazeta.malitounik.Cytaty
import by.carkva_gazeta.malitounik.DialogDelite
import by.carkva_gazeta.malitounik.DialogHelpCustomSort
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
import by.carkva_gazeta.malitounik.SearchBibleViewModel
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.SettingsView
import by.carkva_gazeta.malitounik.SviatyList
import by.carkva_gazeta.malitounik.SviatyiaView
import by.carkva_gazeta.malitounik.SviatyiaViewModel
import by.carkva_gazeta.malitounik.VybranaeList
import by.carkva_gazeta.malitounik.admin.DialogEditSvityiaAndSviaty
import by.carkva_gazeta.malitounik.admin.Icony
import by.carkva_gazeta.malitounik.admin.PasochnicaList
import by.carkva_gazeta.malitounik.admin.Piasochnica
import by.carkva_gazeta.malitounik.formatFigureTwoPlaces
import by.carkva_gazeta.malitounik.removeZnakiAndSlovy
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
import by.carkva_gazeta.malitounik.zamena
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Calendar
import kotlin.random.Random

data class AppNavGraphStateScroll(val title: String, var scrollPosition: Int, var offset: Int = 0)

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
    var appUpdate by mutableStateOf(true)

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

    fun getScrollValueOffset(title: String): Int {
        var result = 0
        for (i in scrollValueList.indices) {
            if (title == scrollValueList[i].title) {
                result = scrollValueList[i].offset
                break
            }
        }
        return result
    }

    fun setScrollValuePosition(title: String, position: Int, offset: Int = 0) {
        var result = false
        for (i in scrollValueList.indices) {
            if (title == scrollValueList[i].title) {
                scrollValueList[i].scrollPosition = position
                scrollValueList[i].offset = offset
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
                    fontFamily = FontFamily(Font(R.font.andantinoscript)), fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = if (Settings.dzenNoch) PrimaryBlack else Primary, fontSize = (Settings.fontInterface + 4).sp
                ), 0, 1
            )
            addStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.comici))), 1, this.length)
        }.toAnnotatedString()
    }
}

fun openBibleResources(context: Context, fileName: String): String {
    var result: String
    try {
        val file = File("${context.filesDir}$fileName")
        result = file.readText()
    } catch (_: FileNotFoundException) {
        val inputStream = context.assets.open("bogashlugbovya_error.html")
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        result = reader.readText()
    }
    return result
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
                    line = line.take(t1).trim()
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
fun AppNavGraph(navController: NavHostController = rememberNavController(), viewModel: SearchBibleViewModel = viewModel(), sviatyiaViewModel: SviatyiaViewModel = viewModel(), adminViewModel: Piasochnica = viewModel()) {
    val drawerScrollStete = rememberScrollState()
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
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.AKAFIST_MENU) {
            Settings.destinations = AllDestinations.AKAFIST_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.LITURGIKON_MENU) {
            Settings.destinations = AllDestinations.LITURGIKON_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.CHASASLOU_MENU) {
            Settings.destinations = AllDestinations.CHASASLOU_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.MAE_NATATKI_MENU) {
            Settings.destinations = AllDestinations.MAE_NATATKI_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BOGASLUJBOVYIA_MENU) {
            Settings.destinations = AllDestinations.BOGASLUJBOVYIA_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.MALITVY_MENU) {
            Settings.destinations = AllDestinations.MALITVY_MENU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_SEMUXA) {
            Settings.destinations = AllDestinations.BIBLIA_SEMUXA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_BOKUNA) {
            Settings.destinations = AllDestinations.BIBLIA_BOKUNA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_NADSAN) {
            Settings.destinations = AllDestinations.BIBLIA_NADSAN
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_CHARNIAUSKI) {
            Settings.destinations = AllDestinations.BIBLIA_CHARNIAUSKI
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_CATOLIK) {
            Settings.destinations = AllDestinations.BIBLIA_CATOLIK
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_SINODAL) {
            Settings.destinations = AllDestinations.BIBLIA_SINODAL
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE) {
            Settings.destinations = AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.KALIANDAR_YEAR) {
            Settings.destinations = AllDestinations.KALIANDAR_YEAR
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.VYBRANAE_LIST) {
            Settings.destinations = AllDestinations.VYBRANAE_LIST
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIJATEKA_NIADAUNIA) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_NIADAUNIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIJATEKA_SPEUNIKI) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_SPEUNIKI
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIJATEKA_GISTORYIA) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_GISTORYIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIJATEKA_MALITOUNIKI) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_MALITOUNIKI
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.PIESNY_PRASLAULENNIA) {
            Settings.destinations = AllDestinations.PIESNY_PRASLAULENNIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.PIESNY_DA_BAGARODZICY) {
            Settings.destinations = AllDestinations.PIESNY_DA_BAGARODZICY
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.PIESNY_ZA_BELARUS) {
            Settings.destinations = AllDestinations.PIESNY_ZA_BELARUS
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.PIESNY_KALIADNYIA) {
            Settings.destinations = AllDestinations.PIESNY_KALIADNYIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.PIESNY_TAIZE) {
            Settings.destinations = AllDestinations.PIESNY_TAIZE
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.UNDER_PADRYXTOUKA) {
            Bogaslujbovyia(navController, stringResource(R.string.spovedz), "padryxtouka_da_spovedzi.html", adminViewModel = adminViewModel)
        }

        composable(AllDestinations.SETTINGS_VIEW) {
            SettingsView(navController, viewModel)
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
            SviatyiaView(navController, svity, position, sviatyiaViewModel)
        }

        composable(AllDestinations.UNDER_PAMIATKA) {
            Bogaslujbovyia(navController, stringResource(R.string.pamiatka), "pamiatka.html", adminViewModel = adminViewModel)
        }

        composable(AllDestinations.PRANAS) {
            Bogaslujbovyia(navController, stringResource(R.string.pra_nas), "onas.html", adminViewModel = adminViewModel)
        }

        composable(AllDestinations.HELP) {
            Bogaslujbovyia(navController, stringResource(R.string.help), "help.html", adminViewModel = adminViewModel)
        }

        composable(AllDestinations.UNDER_SVAITY_MUNU) {
            Settings.destinations = AllDestinations.UNDER_SVAITY_MUNU
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.UNDER_PARAFII_BGKC) {
            Settings.destinations = AllDestinations.UNDER_PARAFII_BGKC
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.UNDER_PASHALIA) {
            Settings.destinations = AllDestinations.UNDER_PASHALIA
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
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
            AllDestinations.BOGASLUJBOVYIA + "/{title}/{resurs}"
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val context = LocalContext.current
            val resurs = stackEntry.arguments?.getString("resurs") ?: "bogashlugbovya_error.html"
            val error = stringResource(R.string.error_ch)
            val data = findCaliandarToDay()
            val titleCh = stringResource(R.string.czytanne3, data[1].toInt(), LocalResources.current.getStringArray(R.array.meciac_smoll)[2])
            Bogaslujbovyia(
                navController, title, resurs, navigateTo = { navigate, skipUtran ->
                    when (navigate) {
                        "error" -> {
                            navigationActions.navigateToBogaslujbovyia(error, "bogashlugbovya_error.html")
                        }

                        "malitvypasliaprychastia" -> {
                            navigationActions.navigateToMalitvyListAll("МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ", Settings.MENU_MALITVY_PASLIA_PRYCHASCIA)
                        }

                        "litciaiblaslavennechl" -> {
                            navigationActions.navigateToBogaslujbovyia("Ліцьця і блаславеньне хлябоў", "bogashlugbovya/viaczernia_liccia_i_blaslavenne_chliabou.html")
                        }

                        "gliadzitutdabraveshchane" -> {
                            navigationActions.navigateToBogaslujbovyia("Дабравешчаньне Найсьвяцейшай Багародзіцы (Вячэрня з Літургіяй)", "bogashlugbovya/mm_25_03_dabravieszczannie_viaczernia_z_liturhijaj.html")
                        }

                        "cytanne", "cytannedop" -> {
                            val skip = if (skipUtran) -2 else -1
                            val cytanne = removeZnakiAndSlovy(if (navigate == "cytanne") data[9] else data[11])
                            viewModel.setPerevod(context, Settings.CHYTANNI_LITURGICHNYIA, cytanne, Settings.PEREVODSEMUXI)
                            navigationActions.navigateToCytanniList(
                                titleCh, cytanne, Settings.CHYTANNI_LITURGICHNYIA, Settings.PEREVODSEMUXI, skip
                            )
                        }

                        "cytannesvityx" -> {
                            val cytanne = removeZnakiAndSlovy(data[10])
                            viewModel.setPerevod(context, Settings.CHYTANNI_LITURGICHNYIA, cytanne, Settings.PEREVODSEMUXI)
                            navigationActions.navigateToCytanniList(
                                titleCh, cytanne, Settings.CHYTANNI_LITURGICHNYIA, Settings.PEREVODSEMUXI, -1
                            )
                        }
                    }
                }, adminViewModel = adminViewModel
            )
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
            CytanniList(navController, title, cytanne, biblia, perevod, position, viewModel)
        }

        composable(
            AllDestinations.BIBLIA_LIST + "/{novyZapavet}/{perevod}", arguments = listOf(
                navArgument("novyZapavet") { type = NavType.BoolType })
        ) { stackEntry ->
            val isNovyZapavet = stackEntry.arguments?.getBoolean("novyZapavet", false) == true
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI
            val context = LocalContext.current
            BibliaList(
                navController, isNovyZapavet, perevod, viewModel, navigateToCytanniList = { chytanne, perevod2 ->
                    viewModel.setPerevod(context, Settings.CHYTANNI_BIBLIA, chytanne, perevod2)
                    navigationActions.navigateToCytanniList(
                        "", chytanne, Settings.CHYTANNI_BIBLIA, perevod2, -1
                    )
                })
        }

        composable(AllDestinations.CYTATY_MENU) {
            Cytaty(navController)
        }

        composable(AllDestinations.PIASOCHNICA_LIST) {
            Settings.destinations = AllDestinations.PIASOCHNICA_LIST
            MainConteiner(
                navController = navController, drawerScrollStete = drawerScrollStete, viewModel = viewModel, sviatyiaviewModel = sviatyiaViewModel, adminViewModel = adminViewModel
            )
        }

        composable(AllDestinations.PIASOCHNICA + "/{resurs}") { stackEntry ->
            val resurs = stackEntry.arguments?.getString("resurs") ?: "bogashlugbovya_error.html"
            Piasochnica(navController, resurs, adminViewModel)
        }

        composable(AllDestinations.EDIT_ICON) {
            Icony(navController, sviatyiaViewModel)
        }
    }
}

fun findCaliandarToDay(isGlobal: Boolean = true): ArrayList<String> {
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

fun findCaliandarPosition() {
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
        if (Settings.isNetworkAvailable(context)) {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    if (Settings.isNetworkAvailable(context, Settings.TRANSPORT_CELLULAR)) {
                        totalBytesToDownload = appUpdateInfo.totalBytesToDownload().toFloat()
                        noWIFI = true
                    } else {
                        appUpdateManager.registerListener(installStateUpdatedListener)
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, launcher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                    }
                }
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    onDismiss()
                }
            }
            appUpdateInfoTask.addOnFailureListener {
                onDismiss()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConteiner(
    navController: NavHostController, drawerScrollStete: ScrollState, viewModel: SearchBibleViewModel, sviatyiaviewModel: SviatyiaViewModel, adminViewModel: Piasochnica
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: AllDestinations.KALIANDAR
    val context = LocalActivity.current as MainActivity
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var isInstallApp by remember { mutableStateOf(k.getBoolean("isInstallApp", false)) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var progressApp by remember { mutableFloatStateOf(0f) }
    if (AppNavGraphState.appUpdate) {
        CheckUpdateMalitounik(onDownloadComplet = {
            isInstallApp = true
            k.edit {
                putBoolean("isInstallApp", true)
            }
            AppNavGraphState.appUpdate = false
        }, updateDownloadProgress = { isVisable, progress ->
            isProgressVisable = isVisable
            progressApp = progress
        }) {
            AppNavGraphState.appUpdate = false
        }
    }
    if (Settings.data.isEmpty() || Settings.caliandarPosition == -1) {
        findCaliandarPosition()
    }
    val lazyColumnState = rememberLazyListState()
    val lazyColumnStateSearchSvityia = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = {
        Settings.data.size
    }, initialPage = Settings.caliandarPosition)
    var showDropdown by remember { mutableStateOf(false) }
    var showDropdownMenuPos by rememberSaveable { mutableIntStateOf(1) }
    BackHandler(drawerState.isClosed || showDropdown || viewModel.searchText || viewModel.isEditMode || viewModel.natatkaVisable) {
        when {
            viewModel.searchText || viewModel.searchFullText || viewModel.isEditMode || viewModel.natatkaVisable -> {
                viewModel.searchText = false
                viewModel.searchFullText = false
                viewModel.isEditMode = false
                viewModel.natatkaVisable = false
                viewModel.addFileNatatki = false
            }

            drawerState.isClosed -> coroutineScope.launch { drawerState.open() }
        }
        showDropdown = false
    }
    val view = LocalView.current
    var isAppearanceLight by remember { mutableStateOf(false) }
    LaunchedEffect(isAppearanceLight, Unit, drawerState.isOpen) {
        isAppearanceLight = if (drawerState.isOpen) {
            !Settings.dzenNoch
        } else {
            if (currentRoute == AllDestinations.KALIANDAR) {
                when {
                    Settings.data[Settings.caliandarPosition][7].toInt() == 3 -> false
                    Settings.data[Settings.caliandarPosition][5].toInt() > 0 -> false
                    else -> true
                }
            } else {
                false
            }
        }
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
                    if (k.getBoolean("caliandarList", false)) {
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
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = isAppearanceLight
            isAppearanceLightNavigationBars = if (Settings.destinations == AllDestinations.KALIANDAR) {
                isAppearanceLight
            } else {
                !Settings.dzenNoch
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
    var removeAllVybranaeDialog by remember { mutableStateOf(false) }
    var removeAllNatatkiDialog by remember { mutableStateOf(false) }
    var removeAllVybranae by remember { mutableStateOf(false) }
    var logView by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    if (logView) {
        DialogLogProgramy {
            logView = false
            if (!k.getBoolean("power", false)) {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
    if (removeAllVybranaeDialog || removeAllNatatkiDialog) {
        DialogDelite(
            title = if (removeAllVybranaeDialog) stringResource(R.string.del_all_vybranoe)
            else stringResource(R.string.delite_all_natatki), onConfirmation = {
                if (removeAllVybranaeDialog) {
                    removeAllVybranae = true
                    removeAllVybranaeDialog = false
                } else {
                    val dir = File("${context.filesDir}/Malitva")
                    if (dir.exists()) dir.deleteRecursively()
                    removeAllNatatkiDialog = false
                    viewModel.fileList.clear()
                }
            }) {
            removeAllVybranaeDialog = false
            removeAllNatatkiDialog = false
        }
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
    var addBiblijateka by remember { mutableStateOf(false) }
    var dialogHelpCustomSort by remember { mutableStateOf(false) }
    if (dialogHelpCustomSort) {
        DialogHelpCustomSort {
            if (it) {
                k.edit {
                    putBoolean("isCustomSortHelp", false)
                }
            }
            dialogHelpCustomSort = false
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            DrawView(
                drawerScrollStete = drawerScrollStete,
                route = currentRoute,
                navigateToRazdel = { razdzel ->
                    coroutineScope.launch {
                        drawerState.close()
                        viewModel.textFieldValueState = TextFieldValue("")
                        Settings.textFieldValueLatest = ""
                        when (razdzel) {
                            AllDestinations.KALIANDAR -> {
                                if (k.getBoolean("caliandarList", false)) navigationActions.navigateToKaliandarYear()
                                else navigationActions.navigateToKaliandar()
                            }

                            AllDestinations.BOGASLUJBOVYIA_MENU -> navigationActions.navigateToBogaslujbovyiaMenu()
                            AllDestinations.MALITVY_MENU -> navigationActions.navigateToMalitvyMenu()
                            AllDestinations.BIBLIA_SEMUXA -> {
                                navigationActions.navigateToBibliaSemuxa()
                            }

                            AllDestinations.BIBLIA_BOKUNA -> {
                                navigationActions.navigateToBibliaBokuna()
                            }

                            AllDestinations.BIBLIA_NADSAN -> {
                                navigationActions.navigateToBibliaNadsan()
                            }

                            AllDestinations.BIBLIA_CHARNIAUSKI -> {
                                navigationActions.navigateToBibliaCharniauski()
                            }

                            AllDestinations.BIBLIA_CATOLIK -> {
                                navigationActions.navigateToBibliaCatolik()
                            }

                            AllDestinations.BIBLIA_SINODAL -> {
                                navigationActions.navigateToBibliaSinodal()
                            }

                            AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE -> {
                                navigationActions.navigateToBibliaNewAmericanBible()
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
                            AllDestinations.UNDER_PADRYXTOUKA -> navigationActions.navigateToPadryxtouka()
                            AllDestinations.UNDER_PAMIATKA -> navigationActions.navigateToPamiatka()
                            AllDestinations.PRANAS -> navigationActions.navigateToPraNas()
                            AllDestinations.HELP -> navigationActions.navigateToHelp()
                            AllDestinations.UNDER_SVAITY_MUNU -> navigationActions.navigateToSviaty()
                            AllDestinations.UNDER_PARAFII_BGKC -> navigationActions.navigateToParafiiBgkc()
                            AllDestinations.UNDER_PASHALIA -> navigationActions.navigateToPashalia()
                            AllDestinations.CYTATY_MENU -> navigationActions.navigateToCytaty()
                            AllDestinations.PIASOCHNICA_LIST -> navigationActions.navigateToPiasochnicaList()
                        }
                    }
                },
            )
        }, drawerState = drawerState
    ) {
        var tollBarColor by remember { mutableStateOf(if (Settings.dzenNoch) BackgroundTolBarDark else Primary) }
        var textTollBarColor by remember { mutableStateOf(PrimaryTextBlack) }
        var title by rememberSaveable { mutableStateOf("") }
        var isBottomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
        viewModel.isIconSort = currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.MAE_NATATKI_MENU || currentRoute == AllDestinations.BIBLIA_SEMUXA || currentRoute == AllDestinations.BIBLIA_BOKUNA || currentRoute == AllDestinations.BIBLIA_NADSAN || currentRoute == AllDestinations.BIBLIA_CHARNIAUSKI || currentRoute == AllDestinations.BIBLIA_SINODAL || currentRoute == AllDestinations.BIBLIA_CATOLIK || currentRoute == AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE
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
            AllDestinations.BIBLIA_CATOLIK -> stringResource(R.string.title_biblia_catolik)
            AllDestinations.BIBLIA_SINODAL -> stringResource(R.string.bsinaidal)
            AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE -> stringResource(R.string.perevod_new_american_bible)
            AllDestinations.PIASOCHNICA_LIST -> stringResource(R.string.pasochnica)
            else -> ""
        }
        var expandedUp by remember { mutableStateOf(false) }
        val searchBibleState = rememberLazyListState()
        val maxLine = remember { mutableIntStateOf(2) }
        var dialogEditSvityiaAndSviaty by rememberSaveable { mutableStateOf(false) }
        if (dialogEditSvityiaAndSviaty) {
            DialogEditSvityiaAndSviaty(adminViewModel) {
                dialogEditSvityiaAndSviaty = false
            }
        }
        LaunchedEffect(viewModel.textFieldValueState) {
            if (!currentRoute.contains("Biblia_")) {
                val edit = zamena(viewModel.textFieldValueState.text)
                if (edit != viewModel.textFieldValueState.text) {
                    val selection = TextRange(edit.length)
                    viewModel.textFieldValueState = TextFieldValue(edit, selection)
                }
            }
        }
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    if (!(viewModel.searchText || viewModel.searchFullText || viewModel.isEditMode)) {
                        Text(
                            modifier = Modifier.clickable {
                                maxLine.intValue = Int.MAX_VALUE
                                coroutineScope.launch {
                                    delay(5000L)
                                    maxLine.intValue = 2
                                }
                            }, text = if (viewModel.natatkaVisable) viewModel.fileList[viewModel.natatkaPosition].title else title.uppercase(), color = textTollBarColor, fontWeight = FontWeight.Bold, fontSize = Settings.fontInterface.sp, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis
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
                                }, value = viewModel.textFieldValueState, placeholder = { Text(stringResource(if (viewModel.isEditMode) R.string.natatka_name else R.string.poshuk), fontSize = Settings.fontInterface.sp, color = textTollBarColor) }, onValueChange = { newText ->
                                viewModel.textFieldValueState = newText
                            }, singleLine = true, trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.textFieldValueState = TextFieldValue("")
                                }) {
                                    Icon(
                                        painter = if (viewModel.textFieldValueState.text.isNotEmpty()) painterResource(R.drawable.close) else painterResource(R.drawable.empty), contentDescription = "", tint = textTollBarColor
                                    )
                                }
                            }, colors = TextFieldDefaults.colors(
                                focusedContainerColor = tollBarColor, unfocusedContainerColor = tollBarColor, focusedTextColor = textTollBarColor, unfocusedTextColor = textTollBarColor, focusedIndicatorColor = textTollBarColor, unfocusedIndicatorColor = textTollBarColor, cursorColor = textTollBarColor
                            ), textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                        )
                    }
                }, navigationIcon = {
                    if (viewModel.searchText || viewModel.searchFullText || viewModel.isEditMode || viewModel.natatkaVisable) {
                        PlainTooltip(stringResource(R.string.close), TooltipAnchorPosition.Below) {
                            IconButton(onClick = {
                                viewModel.searchText = false
                                viewModel.searchFullText = false
                                viewModel.isEditMode = false
                                viewModel.natatkaVisable = false
                                viewModel.addFileNatatki = false
                            }, content = {
                                Icon(
                                    painter = painterResource(R.drawable.close), tint = textTollBarColor, contentDescription = ""
                                )
                            })
                        }
                    } else {
                        PlainTooltip(stringResource(R.string.show_menu), TooltipAnchorPosition.Below) {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }, content = {
                                Icon(
                                    painter = painterResource(R.drawable.menu), tint = textTollBarColor, contentDescription = ""
                                )
                            })
                        }
                    }
                }, actions = {
                    if (!(viewModel.searchText || viewModel.searchFullText || viewModel.isEditMode || viewModel.natatkaVisable)) {
                        if (!isBottomBar && (currentRoute == AllDestinations.KALIANDAR || currentRoute == AllDestinations.KALIANDAR_YEAR)) {
                            PlainTooltip(stringResource(R.string.set_data), TooltipAnchorPosition.Below) {
                                Text(
                                    text = Calendar.getInstance()[Calendar.DATE].toString(), modifier = Modifier
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
                                        .padding(horizontal = 5.dp), fontSize = 14.sp, color = if (isToDay) PrimaryText else PrimaryTextBlack)
                            }
                            PlainTooltip(stringResource(if (k.getBoolean("caliandarList", false)) R.string.set_book_caliandar else R.string.set_list_caliandar), TooltipAnchorPosition.Below) {
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
                        }
                        if (currentRoute == AllDestinations.MAE_NATATKI_MENU) {
                            PlainTooltip(stringResource(R.string.add_natatku), TooltipAnchorPosition.Below) {
                                IconButton({
                                    viewModel.addFileNatatki = true
                                    viewModel.isEditMode = true
                                    viewModel.natatkaVisable = true
                                    viewModel.textFieldValueState = TextFieldValue("")
                                    viewModel.textFieldValueNatatkaContent = TextFieldValue("")
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.add), tint = textTollBarColor, contentDescription = ""
                                    )
                                }
                            }
                        }
                        if (viewModel.isIconSort) {
                            var expandedSort by remember { mutableStateOf(false) }
                            val isSortedVybranae = currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.BIBLIA_SEMUXA || currentRoute == AllDestinations.BIBLIA_BOKUNA || currentRoute == AllDestinations.BIBLIA_NADSAN || currentRoute == AllDestinations.BIBLIA_CHARNIAUSKI || currentRoute == AllDestinations.BIBLIA_SINODAL || currentRoute == AllDestinations.BIBLIA_CATOLIK || currentRoute == AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE
                            AppDropdownMenu(expanded = expandedSort, onDismissRequest = { expandedSort = false }) {
                                sortedVybranae = k.getInt("sortedVybranae", Settings.SORT_BY_ABC)
                                sortedNatatki = k.getInt("natatki_sort", Settings.SORT_BY_ABC)
                                DropdownMenuItem(onClick = {
                                    expandedSort = false
                                    sortedVybranae = Settings.SORT_BY_ABC
                                    sortedNatatki = Settings.SORT_BY_ABC
                                    k.edit {
                                        if (isSortedVybranae) {
                                            putInt(
                                                "sortedVybranae", sortedVybranae
                                            )
                                        } else putInt("natatki_sort", sortedNatatki)
                                    }
                                }, text = { Text(stringResource(R.string.sort_abc), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Row {
                                        if ((isSortedVybranae && sortedVybranae == Settings.SORT_BY_ABC) || (currentRoute == AllDestinations.MAE_NATATKI_MENU && sortedNatatki == Settings.SORT_BY_ABC)) {
                                            Icon(
                                                painter = painterResource(R.drawable.check), contentDescription = ""
                                            )
                                        }
                                        Icon(
                                            painter = painterResource(R.drawable.sort_by_az), contentDescription = ""
                                        )
                                    }
                                })
                                DropdownMenuItem(onClick = {
                                    expandedSort = false
                                    sortedVybranae = Settings.SORT_BY_TIME
                                    sortedNatatki = Settings.SORT_BY_TIME
                                    k.edit {
                                        if (isSortedVybranae) {
                                            putInt(
                                                "sortedVybranae", sortedVybranae
                                            )
                                        } else putInt("natatki_sort", sortedNatatki)
                                    }
                                }, text = { Text(stringResource(R.string.sort_time), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Row {
                                        if ((isSortedVybranae && sortedVybranae == Settings.SORT_BY_TIME) || (currentRoute == AllDestinations.MAE_NATATKI_MENU && sortedNatatki == Settings.SORT_BY_TIME)) {
                                            Icon(
                                                painter = painterResource(R.drawable.check), contentDescription = ""
                                            )
                                        }
                                        Icon(
                                            painter = painterResource(R.drawable.sort_by_time), contentDescription = ""
                                        )
                                    }
                                })
                                DropdownMenuItem(onClick = {
                                    expandedSort = false
                                    sortedVybranae = Settings.SORT_BY_CUSTOM
                                    sortedNatatki = Settings.SORT_BY_CUSTOM
                                    if (k.getBoolean("isCustomSortHelp", true)) {
                                        dialogHelpCustomSort = true
                                    }
                                    k.edit {
                                        if (isSortedVybranae) {
                                            putInt(
                                                "sortedVybranae", sortedVybranae
                                            )
                                        } else putInt("natatki_sort", sortedNatatki)
                                    }
                                }, text = { Text(stringResource(R.string.sort_custom), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Row {
                                        if ((isSortedVybranae && sortedVybranae == Settings.SORT_BY_CUSTOM) || (currentRoute == AllDestinations.MAE_NATATKI_MENU && sortedNatatki == Settings.SORT_BY_CUSTOM)) {
                                            Icon(
                                                painter = painterResource(R.drawable.check), contentDescription = ""
                                            )
                                        }
                                        Icon(
                                            painter = painterResource(R.drawable.sort), contentDescription = ""
                                        )
                                    }
                                })
                            }
                            PlainTooltip(stringResource(R.string.sort), TooltipAnchorPosition.Below) {
                                IconButton(onClick = {
                                    expandedSort = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.sort), contentDescription = "", tint = textTollBarColor
                                    )
                                }
                            }
                        }
                        if (currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.MAE_NATATKI_MENU) {
                            PlainTooltip(stringResource(if (currentRoute == AllDestinations.VYBRANAE_LIST) R.string.vybranae_all_remove else R.string.vybranae_remove_all_natatka), TooltipAnchorPosition.Below) {
                                IconButton({
                                    if (currentRoute == AllDestinations.VYBRANAE_LIST) removeAllVybranaeDialog = !removeAllVybranaeDialog
                                    else removeAllNatatkiDialog = !removeAllNatatkiDialog
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.delete), tint = textTollBarColor, contentDescription = ""
                                    )
                                }
                            }
                        }
                        if (currentRoute == AllDestinations.LITURGIKON_MENU || currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.CHASASLOU_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute.contains("BIBLIJATEKA", ignoreCase = true) || currentRoute.contains("PIESNY", ignoreCase = true) || currentRoute == AllDestinations.UNDER_PASHALIA || currentRoute.contains("BIBLIA", ignoreCase = true)) {
                            PlainTooltip(stringResource(R.string.poshuk), TooltipAnchorPosition.Below) {
                                IconButton({
                                    viewModel.searchText = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.search), tint = textTollBarColor, contentDescription = ""
                                    )
                                }
                            }
                        }
                        if (currentRoute == AllDestinations.PIASOCHNICA_LIST) {
                            PlainTooltip(stringResource(R.string.pasochnica_add_folder), TooltipAnchorPosition.Below) {
                                IconButton(onClick = {
                                    PasochnicaList.pasochnicaAction = PasochnicaList.FILE
                                }) {
                                    Icon(
                                        modifier = Modifier.size(24.dp), painter = painterResource(R.drawable.directory_icon_menu), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            PlainTooltip(stringResource(R.string.pasochnica_add_basa), TooltipAnchorPosition.Below) {
                                IconButton(onClick = {
                                    PasochnicaList.pasochnicaAction = PasochnicaList.WWW
                                }) {
                                    Icon(
                                        modifier = Modifier.size(24.dp), painter = painterResource(R.drawable.www_icon), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            PlainTooltip(stringResource(R.string.add_file), TooltipAnchorPosition.Below) {
                                IconButton(onClick = {
                                    PasochnicaList.pasochnicaAction = PasochnicaList.ADD
                                }) {
                                    Icon(
                                        modifier = Modifier.size(24.dp), painter = painterResource(R.drawable.plus), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                        PlainTooltip(stringResource(R.string.more_items), TooltipAnchorPosition.Below) {
                            IconButton(onClick = { expandedUp = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = textTollBarColor
                                )
                            }
                        }
                    }
                    if (currentRoute == AllDestinations.MAE_NATATKI_MENU && viewModel.natatkaVisable && !viewModel.isEditMode) {
                        PlainTooltip(stringResource(R.string.redagaktirovat)) {
                            IconButton(onClick = { viewModel.isEditMode = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.edit), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        PlainTooltip(stringResource(R.string.delite)) {
                            IconButton(onClick = { viewModel.isDeliteNatatka = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.delete), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                    if (currentRoute == AllDestinations.MAE_NATATKI_MENU && viewModel.isEditMode) {
                        PlainTooltip(stringResource(R.string.save_sabytie)) {
                            IconButton(onClick = { viewModel.saveFileNatatki = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.save), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                    if (viewModel.searchFullText || (viewModel.searchText && currentRoute.contains("BIBLIA", ignoreCase = true))) {
                        PlainTooltip(stringResource(R.string.settings_bible)) {
                            IconButton(onClick = { viewModel.searchSettings = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.settings), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                    AppDropdownMenu(
                        expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                        DropdownMenuItem(onClick = {
                            expandedUp = false
                            navigationActions.navigateToSettingsView()
                        }, text = { Text(stringResource(R.string.tools_item), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.settings), contentDescription = ""
                            )
                        })
                        if (currentRoute == AllDestinations.PIASOCHNICA_LIST) {
                            DropdownMenuItem(onClick = {
                                PasochnicaList.pasochnicaAction = PasochnicaList.DEL_ALL_PASOCHNICA
                                expandedUp = false
                            }, text = { Text(stringResource(R.string.del_pasochnica), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.delete), contentDescription = ""
                                )
                            })
                        }
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
                                    viewModel.searchText = true
                                }, text = { Text(stringResource(R.string.poshuk_sviatych), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.search), contentDescription = ""
                                    )
                                })
                            }
                        }
                        if (k.getBoolean("admin", false) || k.getBoolean("adminOnlyNotifications", false)) {
                            HorizontalDivider()
                            if (!k.getBoolean("adminOnlyNotifications", false)) {
                                if (currentRoute.contains(AllDestinations.KALIANDAR) || (currentRoute.contains("BIBLIJATEKA", ignoreCase = true) && currentRoute != AllDestinations.BIBLIJATEKA_NIADAUNIA)) {
                                    DropdownMenuItem(onClick = {
                                        expandedUp = false
                                        if (currentRoute.contains(AllDestinations.KALIANDAR)) {
                                            dialogEditSvityiaAndSviaty = true
                                        } else {
                                            addBiblijateka = true
                                        }
                                    }, text = { Text(stringResource(if (currentRoute.contains(AllDestinations.KALIANDAR)) R.string.redagaktirovat else R.string.add_file), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.edit), contentDescription = ""
                                        )
                                    })
                                }
                                if (currentRoute == AllDestinations.LITURGIKON_MENU || currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.CHASASLOU_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU) {
                                    DropdownMenuItem(onClick = {
                                        expandedUp = false
                                        viewModel.searchFullText = true
                                    }, text = { Text(stringResource(R.string.searche_bogasluz_text), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.search), contentDescription = ""
                                        )
                                    })
                                }
                            }
                            if (!k.getBoolean("adminNotifications", false)) {
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    if (Settings.isNetworkAvailable(context)) {
                                        logView = true
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                                    }
                                }, text = { Text(stringResource(R.string.log_m), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.description), contentDescription = ""
                                    )
                                })
                            }
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(tollBarColor)
            )
        }, bottomBar = {
            if (!(currentRoute == AllDestinations.MAE_NATATKI_MENU || currentRoute == AllDestinations.LITURGIKON_MENU || currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.CHASASLOU_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute.contains("BIBLIJATEKA", ignoreCase = true) || currentRoute.contains("PIESNY", ignoreCase = true) || currentRoute == AllDestinations.UNDER_PASHALIA || currentRoute == AllDestinations.UNDER_PARAFII_BGKC || currentRoute == AllDestinations.UNDER_SVAITY_MUNU || currentRoute.contains("BIBLIA", ignoreCase = true) || currentRoute == AllDestinations.VYBRANAE_LIST || currentRoute == AllDestinations.PIASOCHNICA_LIST)) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                if (!(viewModel.searchText || viewModel.searchFullText)) {
                    if (showDropdown) {
                        ModalBottomSheet(
                            sheetState = sheetState, properties = ModalBottomSheetProperties(
                                isAppearanceLightStatusBars = isAppearanceLight, isAppearanceLightNavigationBars = if (Settings.destinations == AllDestinations.KALIANDAR) isAppearanceLight
                                else !Settings.dzenNoch
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
                                PlainTooltip(stringResource(R.string.poshuk_sviatych)) {
                                    IconButton(onClick = {
                                        viewModel.searchText = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.search), contentDescription = "", tint = textTollBarColor
                                        )
                                    }
                                }
                                PlainTooltip(stringResource(R.string.sabytie)) {
                                    IconButton(onClick = {
                                        navigationActions.navigateToPadzeiView()
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.event), contentDescription = "", tint = textTollBarColor
                                        )
                                    }
                                }
                                PlainTooltip(stringResource(if (k.getBoolean("caliandarList", false)) R.string.set_book_caliandar else R.string.set_list_caliandar)) {
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
                                }
                                PlainTooltip(stringResource(R.string.set_data)) {
                                    Text(
                                        text = Calendar.getInstance()[Calendar.DATE].toString(), modifier = Modifier
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
                                            .padding(horizontal = 5.dp), fontSize = 14.sp, color = if (isToDay) PrimaryText else PrimaryTextBlack)
                                }
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
                        val dataToDay = findCaliandarToDay(false)
                        val fling = PagerDefaults.flingBehavior(
                            state = pagerState, pagerSnapDistance = PagerSnapDistance.atMost(1)
                        )
                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.currentPage }.collect { page ->
                                Settings.caliandarPosition = page
                                val data = Settings.data[page]
                                isToDay = data[1] == dataToDay[1] && data[2] == dataToDay[2] && data[3] == dataToDay[3]
                                var colorText = PrimaryText
                                when {
                                    data[7].toInt() == 2 -> {
                                        colorBlackboard = Post
                                        isAppearanceLight = true
                                    }

                                    data[7].toInt() == 1 -> {
                                        colorBlackboard = BezPosta
                                        isAppearanceLight = true
                                    }

                                    data[7].toInt() == 3 -> {
                                        colorBlackboard = StrogiPost
                                        colorText = PrimaryTextBlack
                                        isAppearanceLight = false
                                    }

                                    data[5].toInt() > 0 -> {
                                        colorBlackboard = Primary
                                        colorText = PrimaryTextBlack
                                        isAppearanceLight = false
                                    }

                                    else -> {
                                        colorBlackboard = Divider
                                        isAppearanceLight = true
                                    }
                                }
                                tollBarColor = colorBlackboard
                                textTollBarColor = colorText
                            }
                        }
                        if (viewModel.searchText) {
                            SearchSviatyia(lazyColumnStateSearchSvityia, innerPadding, setCaliandarPage = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(Settings.caliandarPosition)
                                }
                                viewModel.searchText = false
                            }, viewModel)
                        } else {
                            HorizontalPager(
                                pageSpacing = 10.dp, state = pagerState, flingBehavior = fling, verticalAlignment = Alignment.Top, modifier = Modifier.padding(10.dp)
                            ) { page ->
                                KaliandarScreen(page, innerPadding, navigateToCytanneList = { title, chytanne, biblia ->
                                    viewModel.setPerevod(context, biblia, chytanne, Settings.PEREVODSEMUXI)
                                    navigationActions.navigateToCytanniList(
                                        title, chytanne, biblia, Settings.PEREVODSEMUXI, -1
                                    )
                                }, navigateToSvityiaView = { svity, position ->
                                    sviatyiaviewModel.initState = true
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
                            navController, innerPadding, Settings.MENU_BOGASLUJBOVYIA, searchBibleState, viewModel = viewModel
                        )
                    }

                    AllDestinations.AKAFIST_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_AKAFIST, searchBibleState, viewModel
                        )
                    }

                    AllDestinations.BIBLIJATEKA_NIADAUNIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_NIADAUNIA, innerPadding, false, viewModel) {}
                    }

                    AllDestinations.BIBLIJATEKA_SPEUNIKI -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_SPEUNIKI, innerPadding, addBiblijateka, viewModel) {
                            addBiblijateka = false
                        }
                    }

                    AllDestinations.BIBLIJATEKA_MALITOUNIKI -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_MALITOUNIKI, innerPadding, addBiblijateka, viewModel) {
                            addBiblijateka = false
                        }
                    }

                    AllDestinations.BIBLIJATEKA_GISTORYIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_GISTORYIA, innerPadding, addBiblijateka, viewModel) {
                            addBiblijateka = false
                        }
                    }

                    AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA, innerPadding, addBiblijateka, viewModel) {
                            addBiblijateka = false
                        }
                    }

                    AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU, innerPadding, addBiblijateka, viewModel) {
                            addBiblijateka = false
                        }
                    }

                    AllDestinations.PIESNY_PRASLAULENNIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_PRASLAULENNIA, innerPadding, viewModel)
                    }

                    AllDestinations.PIESNY_ZA_BELARUS -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_ZA_BELARUS, innerPadding, viewModel)
                    }

                    AllDestinations.PIESNY_DA_BAGARODZICY -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_DA_BAGARODZICY, innerPadding, viewModel)
                    }

                    AllDestinations.PIESNY_KALIADNYIA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_KALIADNYIA, innerPadding, viewModel)
                    }

                    AllDestinations.PIESNY_TAIZE -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PiesnyList(navController, AllDestinations.PIESNY_TAIZE, innerPadding, viewModel)
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
                        if (!viewModel.searchText) viewModel.textFieldValueState = TextFieldValue("")
                        Pashalia(navController, innerPadding, viewModel.searchText, viewModel)
                    }

                    AllDestinations.CHASASLOU_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_CHASASLOU, searchBibleState, viewModel
                        )
                    }

                    AllDestinations.LITURGIKON_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_LITURGIKON, searchBibleState, viewModel
                        )
                    }

                    AllDestinations.MAE_NATATKI_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        MaeNatatki(
                            innerPadding, sortedNatatki, viewModel
                        )
                    }

                    AllDestinations.MALITVY_MENU -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BogaslujbovyiaMenu(
                            navController, innerPadding, Settings.MENU_MALITVY, searchBibleState, viewModel
                        )
                    }

                    AllDestinations.BIBLIA_SEMUXA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODSEMUXI, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.BIBLIA_BOKUNA -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODBOKUNA, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.BIBLIA_NADSAN -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODNADSAN, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.BIBLIA_CHARNIAUSKI -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODCARNIAUSKI, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.BIBLIA_CATOLIK -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODCATOLIK, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.BIBLIA_SINODAL -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODSINOIDAL, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.BIBLIA_NEW_AMERICAN_BIBLE -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        BibliaMenu(navController, Settings.PEREVODNEWAMERICANBIBLE, innerPadding, searchBibleState, sortedVybranae, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
                            viewModel.setPerevod(context, biblia, chytanne, perevod2)
                            navigationActions.navigateToCytanniList(
                                "", chytanne, biblia, perevod2, position
                            )
                        }, navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }, viewModel)
                    }

                    AllDestinations.KALIANDAR_YEAR -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        val dataToDay = findCaliandarToDay(false)
                        LaunchedEffect(lazyColumnState) {
                            snapshotFlow { lazyColumnState.firstVisibleItemIndex }.collect { index ->
                                val data = Settings.data[index]
                                isToDay = data[1] == dataToDay[1] && data[2] == dataToDay[2] && data[3] == dataToDay[3]
                            }
                        }
                        if (viewModel.searchText) {
                            SearchSviatyia(lazyColumnStateSearchSvityia, innerPadding, setCaliandarPage = {
                                coroutineScope.launch {
                                    lazyColumnState.scrollToItem(Settings.caliandarPosition)
                                }
                                viewModel.searchText = false
                            }, viewModel)
                        } else {
                            KaliandarScreenYear(
                                coroutineScope = coroutineScope, lazyColumnState = lazyColumnState, innerPadding, navigateToSvityiaView = { svity, position ->
                                    sviatyiaviewModel.initState = true
                                    navigationActions.navigateToSvityiaView(svity, position)
                                })
                        }
                    }

                    AllDestinations.VYBRANAE_LIST -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        VybranaeList(
                            navigateToCytanniList = { chytanne, position, perevod2 ->
                                viewModel.setPerevod(context, Settings.CHYTANNI_VYBRANAE, chytanne, perevod2)
                                navigationActions.navigateToCytanniList(
                                    "", chytanne, Settings.CHYTANNI_VYBRANAE, perevod2, position
                                )
                            }, navigateToBogaslujbovyia = { title, resourse ->
                                navigationActions.navigateToBogaslujbovyia(title, resourse)
                            }, sortedVybranae, removeAllVybranae, innerPadding
                        )
                    }

                    AllDestinations.PIASOCHNICA_LIST -> {
                        tollBarColor = MaterialTheme.colorScheme.onTertiary
                        textTollBarColor = PrimaryTextBlack
                        PasochnicaList(
                            navController, innerPadding, adminViewModel
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
                sviatyiaviewModel.initState = true
                dialogKniga = false
                navigationActions.navigateToSvityiaView(svity, position)
            }) {
                dialogKniga = false
            }
        }
    }
}

@Composable
fun SearchSviatyia(lazyColumnStateSearchSvityia: LazyListState, innerPadding: PaddingValues, setCaliandarPage: () -> Unit, viewModel: SearchBibleViewModel) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(viewModel.textFieldValueState.text) {
        viewModel.searchSvityia(context)
    }
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
    Column {
        Text(
            modifier = Modifier.padding(start = 10.dp), text = stringResource(R.string.searh_sviatyia_result, viewModel.searchListSvityia.size), fontStyle = FontStyle.Italic, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
        )
        LazyColumn(
            Modifier.nestedScroll(nestedScrollConnection), state = lazyColumnStateSearchSvityia
        ) {
            items(viewModel.searchListSvityia.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            val calendar = Calendar.getInstance()
                            calendar[Calendar.DAY_OF_YEAR] = viewModel.searchListSvityia[index].dayOfYear
                            for (e in Settings.data.indices) {
                                if (calendar[Calendar.DATE] == Settings.data[e][1].toInt() && calendar[Calendar.MONTH] == Settings.data[e][2].toInt() && calendar[Calendar.YEAR] == Settings.data[e][3].toInt()) {
                                    Settings.caliandarPosition = e
                                    break
                                }
                            }
                            setCaliandarPage()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                    )
                    Column {
                        Text(
                            text = viewModel.searchListSvityia[index].opisanieData, modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                        when (viewModel.searchListSvityia[index].typeSviat) {
                            0 -> {
                                HtmlText(
                                    modifier = Modifier.padding(10.dp), text = viewModel.searchListSvityia[index].opisanie, fontSize = Settings.fontInterface.sp
                                )
                            }

                            1 -> {
                                Text(
                                    modifier = Modifier.padding(10.dp), text = viewModel.searchListSvityia[index].opisanie, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = Settings.fontInterface.sp
                                )
                            }

                            2 -> {
                                Text(
                                    modifier = Modifier.padding(10.dp), text = viewModel.searchListSvityia[index].opisanie, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = Settings.fontInterface.sp
                                )
                            }

                            3 -> {
                                Text(
                                    modifier = Modifier.padding(10.dp), text = viewModel.searchListSvityia[index].opisanie, color = MaterialTheme.colorScheme.primary, fontSize = Settings.fontInterface.sp
                                )
                            }

                            else -> {
                                val t1 = viewModel.searchListSvityia[index].opisanie.indexOf(":")
                                val annotatedString = if (t1 != -1) {
                                    buildAnnotatedString {
                                        append(viewModel.searchListSvityia[index].opisanie)
                                        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t1 + 1)
                                    }
                                } else {
                                    AnnotatedString(viewModel.searchListSvityia[index].opisanie)
                                }
                                Text(
                                    modifier = Modifier.padding(10.dp), text = annotatedString, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
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
    viewModel: LogView = viewModel(), onDismiss: () -> Unit
) {
    val context = LocalActivity.current as MainActivity
    context.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    LaunchedEffect(Unit) {
        viewModel.upDateLog(context)
    }
    Dialog(onDismissRequest = { onDismiss() }, DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)) {
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
                        .weight(1f, false)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HtmlText(
                        modifier = Modifier.clickable {
                            viewModel.checkFiles(context)
                        }, text = viewModel.logViewText, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (viewModel.isLogJob) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.onDismiss()
                                onDismiss()
                            }, shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                            Text(stringResource(R.string.cansel), fontSize = 18.sp)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.onDismiss()
                                onDismiss()
                            }, shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                            Text(stringResource(R.string.close), fontSize = 18.sp)
                        }
                        TextButton(
                            onClick = {
                                viewModel.createAndSentFile(context)
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