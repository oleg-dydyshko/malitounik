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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.ui.window.Popup
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
import by.carkva_gazeta.malitounik.SearchSviatyia
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.Settings.isNetworkAvailable
import by.carkva_gazeta.malitounik.SettingsView
import by.carkva_gazeta.malitounik.ShtoNovaga
import by.carkva_gazeta.malitounik.SviatyList
import by.carkva_gazeta.malitounik.SviatyiaView
import by.carkva_gazeta.malitounik.VybranaeList
import by.carkva_gazeta.malitounik.bibleCount
import by.carkva_gazeta.malitounik.formatFigureTwoPlaces
import by.carkva_gazeta.malitounik.knigaBiblii
import by.carkva_gazeta.malitounik.removeZnakiAndSlovy
import by.carkva_gazeta.malitounik.setNotificationNon
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
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Calendar
import kotlin.random.Random

object AppNavGraphState {
    private val k = MainActivity.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var bibleItem by mutableStateOf(k.getString("navigate", AllDestinations.BIBLIA_SEMUXA)?.contains("Biblia", ignoreCase = true) == true)
    var biblijatekaItem by mutableStateOf(k.getString("navigate", AllDestinations.BIBLIJATEKA_NIADAUNIA)?.contains("Biblijateka", ignoreCase = true) == true)
    var piesnyItem by mutableStateOf(k.getString("navigate", AllDestinations.PIESNY_PRASLAULENNIA)?.contains("Piesny", ignoreCase = true) == true)
    var underItem by mutableStateOf(k.getString("navigate", AllDestinations.PIESNY_PRASLAULENNIA)?.contains("Under", ignoreCase = true) == true)
    var scrollValue = 0
    var checkUpdate = true
    var setAlarm = true

    fun getCytata(context: MainActivity): AnnotatedString {
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
        return AnnotatedString.Builder(citataList[Random.nextInt(citataList.size)]).apply {
            addStyle(
                SpanStyle(
                    fontFamily = FontFamily(Font(R.font.andantinoscript)),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = if (context.dzenNoch) PrimaryBlack else Primary,
                    fontSize = (Settings.fontInterface + 4).sp
                ), 0, 1
            )
            addStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.comici))), 1, this.length)
        }.toAnnotatedString()
    }
}

fun openAssetsResources(context: Context, fileName: String): String {
    var result = ""
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
fun AppNavGraph(cytata: AnnotatedString) {
    val navController: NavHostController = rememberNavController()
    val drawerScrollStete = rememberScrollState()
    val searchBibleState = rememberLazyListState()
    val cytanniListState = remember { ArrayList<LazyListState>() }
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var start = k.getString("navigate", AllDestinations.KALIANDAR) ?: AllDestinations.KALIANDAR
    if (start.contains("Biblijateka_List")) {
        k.edit {
            remove("navigate")
        }
        start = AllDestinations.KALIANDAR
    }
    if (start.contains("Piesny_List")) {
        k.edit {
            remove("navigate")
        }
        start = AllDestinations.KALIANDAR
    }
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    NavHost(
        navController = navController,
        startDestination = start,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) }
    ) {
        composable(
            AllDestinations.KALIANDAR,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.KALIANDAR
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.AKAFIST_MENU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.AKAFIST_MENU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.RUJANEC_MENU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.RUJANEC_MENU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.MAE_NATATKI_MENU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.MAE_NATATKI_MENU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BOGASLUJBOVYIA_MENU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BOGASLUJBOVYIA_MENU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.MALITVY_MENU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.MALITVY_MENU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIA_SEMUXA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIA_SEMUXA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIA_BOKUNA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIA_BOKUNA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIA_NADSAN,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIA_NADSAN
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIA_CHARNIAUSKI,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIA_CHARNIAUSKI
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIA_SINODAL,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIA_SINODAL
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.KALIANDAR_YEAR,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.KALIANDAR_YEAR
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.VYBRANAE_LIST,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.VYBRANAE_LIST
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA_NIADAUNIA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_NIADAUNIA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA_SPEUNIKI,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_SPEUNIKI
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA_GISTORYIA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_GISTORYIA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA_MALITOUNIKI,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_MALITOUNIKI
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.PIESNY_PRASLAULENNIA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.PIESNY_PRASLAULENNIA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.PIESNY_DA_BAGARODZICY,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.PIESNY_DA_BAGARODZICY
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.PIESNY_ZA_BELARUS,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.PIESNY_ZA_BELARUS
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.PIESNY_KALIADNYIA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.PIESNY_KALIADNYIA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.PIESNY_TAIZE,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.PIESNY_TAIZE
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.UNDER_PADRYXTOUKA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Bogaslujbovyia(navController, stringResource(R.string.spovedz), "padryxtouka_da_spovedzi.html")
        }

        composable(AllDestinations.SEARCH_SVITYIA) {
            SearchSviatyia(navController)
        }

        composable(AllDestinations.SETTINGS_VIEW) {
            SettingsView(navController)
        }

        composable(AllDestinations.PADZEI_VIEW) {
            PadzeiaView(navController)
        }

        composable(AllDestinations.SHTO_NOVAGA) {
            ShtoNovaga(navController)
        }

        composable(
            AllDestinations.SVITYIA_VIEW + "/{svity}/{position}",
            arguments = listOf(
                navArgument("svity") { type = NavType.BoolType },
                navArgument("position") { type = NavType.IntType }
            )
        ) { stackEntry ->
            val svity = stackEntry.arguments?.getBoolean("svity") == true
            val position = stackEntry.arguments?.getInt("position") ?: Settings.caliandarPosition
            SviatyiaView(navController, svity, position)
        }

        composable(
            AllDestinations.UNDER_PAMIATKA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Bogaslujbovyia(navController, stringResource(R.string.pamiatka), "pamiatka.html")
        }

        composable(AllDestinations.PRANAS) {
            Bogaslujbovyia(navController, stringResource(R.string.pra_nas), "onas.html")
        }

        composable(AllDestinations.HELP) {
            Bogaslujbovyia(navController, stringResource(R.string.help), "help.html")
        }

        composable(
            AllDestinations.UNDER_SVAITY_MUNU,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.UNDER_SVAITY_MUNU
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.UNDER_PARAFII_BGKC,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.UNDER_PARAFII_BGKC
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
            )
        }

        composable(
            AllDestinations.UNDER_PASHALIA,
            enterTransition = {
                fadeIn(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            }) {
            Settings.destinations = AllDestinations.UNDER_PASHALIA
            MainConteiner(
                navController = navController,
                drawerScrollStete = drawerScrollStete,
                cytata = cytata
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
        ) { stackEntry ->
            val title = stackEntry.arguments?.getString("title") ?: ""
            val context = LocalContext.current
            val resurs = stackEntry.arguments?.getString("resurs") ?: "bogashlugbovya_error.html"
            Bogaslujbovyia(
                navController, title, resurs,
                navigateTo = { navigate ->
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

                        "cytanne" -> {
                            val data = findCaliandarToDay(context)
                            val titleCh = context.getString(
                                R.string.czytanne3,
                                data[1].toInt(),
                                context.resources.getStringArray(R.array.meciac_smoll)[2]
                            )
                            navigationActions.navigateToCytanniList(
                                titleCh,
                                removeZnakiAndSlovy(data[9]),
                                Settings.CHYTANNI_LITURGICHNYIA,
                                Settings.PEREVODSEMUXI,
                                -1
                            )
                        }

                        "cytannesvityx" -> {
                            val data = findCaliandarToDay(context)
                            val titleCh = context.getString(
                                R.string.czytanne3,
                                data[1].toInt(),
                                context.resources.getStringArray(R.array.meciac_smoll)[2]
                            )
                            navigationActions.navigateToCytanniList(
                                titleCh,
                                removeZnakiAndSlovy(data[10]),
                                Settings.CHYTANNI_LITURGICHNYIA,
                                Settings.PEREVODSEMUXI,
                                -1
                            )
                        }

                        "cytannedop" -> {
                            val data = findCaliandarToDay(context)
                            val titleCh = context.getString(
                                R.string.czytanne3,
                                data[1].toInt(),
                                context.resources.getStringArray(R.array.meciac_smoll)[2]
                            )
                            navigationActions.navigateToCytanniList(
                                titleCh,
                                removeZnakiAndSlovy(data[11]),
                                Settings.CHYTANNI_LITURGICHNYIA,
                                Settings.PEREVODSEMUXI,
                                -1
                            )
                        }
                    }
                })
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
            val t1 = cytanne.indexOf(";")
            var knigaText by remember {
                mutableStateOf(
                    if (biblia == Settings.CHYTANNI_BIBLIA || biblia == Settings.CHYTANNI_VYBRANAE) {
                        if (t1 == -1) cytanne.substringBeforeLast(" ")
                        else {
                            val sb = cytanne.substring(0, t1)
                            sb.substringBeforeLast(" ")
                        }
                    } else cytanne
                )
            }
            val count = if (biblia == Settings.CHYTANNI_BIBLIA) remember {
                bibleCount(
                    knigaBiblii(knigaText), perevod
                )
            }
            else 1
            if (count != cytanniListState.size) {
                cytanniListState.clear()
                for (i in 0 until count) {
                    cytanniListState.add(rememberLazyListState())
                }
            }
            CytanniList(navController, title, cytanne, biblia, perevod, position, cytanniListState)
        }

        composable(
            AllDestinations.BIBLIA_LIST + "/{novyZapavet}/{perevod}",
            arguments = listOf(
                navArgument("novyZapavet") { type = NavType.BoolType })
        ) { stackEntry ->
            val isNovyZapavet = stackEntry.arguments?.getBoolean("novyZapavet", false) == true
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
            AllDestinations.SEARCH_BIBLIA + "/{perevod}/{searchBogaslujbovyia}",
            arguments = listOf(navArgument("searchBogaslujbovyia") { type = NavType.BoolType })
        ) { stackEntry ->
            val perevod = stackEntry.arguments?.getString("perevod", Settings.PEREVODSEMUXI)
                ?: Settings.PEREVODSEMUXI
            val searchBogaslujbovyia = stackEntry.arguments?.getBoolean("searchBogaslujbovyia", false) == true
            SearchBible(
                navController,
                searchBibleState,
                perevod,
                searchBogaslujbovyia,
                navigateToCytanniList = { chytanne, position, perevod2 ->
                    navigationActions.navigateToCytanniList(
                        "",
                        chytanne,
                        Settings.CHYTANNI_BIBLIA,
                        perevod2,
                        position
                    )
                },
                navigateToBogaslujbovyia = { title, resurs ->
                    navigationActions.navigateToBogaslujbovyia(title, resurs)
                }
            )
        }
    }
}

fun findCaliandarToDay(context: Context, isGlobal: Boolean = true): ArrayList<String> {
    if (Settings.data.isEmpty()) {
        val gson = Gson()
        val type = TypeToken.getParameterized(
            ArrayList::class.java,
            TypeToken.getParameterized(
                ArrayList::class.java,
                String::class.java
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

@Composable
fun findCaliandarPosition(position: Int): ArrayList<ArrayList<String>> {
    if (Settings.data.isEmpty()) {
        val gson = Gson()
        val type = TypeToken.getParameterized(
            ArrayList::class.java,
            TypeToken.getParameterized(
                ArrayList::class.java,
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

@Composable
fun CheckUpdateMalitounik() {
    var dialogUpdateMalitounik by remember { mutableStateOf(false) }
    var noWIFI by remember { mutableStateOf(false) }
    var totalBytesToDownload by remember { mutableFloatStateOf(0f) }
    var bytesDownload by remember { mutableFloatStateOf(0f) }
    var isCompletDownload by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dialogUpdateMalitounik = true
        }
    }
    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            bytesDownload = state.bytesDownloaded().toFloat()
            totalBytesToDownload = state.totalBytesToDownload().toFloat()
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            dialogUpdateMalitounik = false
            isCompletDownload = true
        }
    }
    if (isCompletDownload) {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
        appUpdateManager.completeUpdate()
        isCompletDownload = false
    }
    if (dialogUpdateMalitounik) {
        DialogUpdateMalitounik(totalBytesToDownload, bytesDownload) {
            dialogUpdateMalitounik = false
        }
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
        }) { noWIFI = false }
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
    drawerScrollStete: ScrollState,
    cytata: AnnotatedString,
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
    if (AppNavGraphState.checkUpdate) {
        CheckUpdateMalitounik()
        AppNavGraphState.checkUpdate = false
    }
    val initPage = if (Settings.caliandarPosition == -1) {
        findCaliandarPosition(-1)
        Settings.initCaliandarPosition
    } else Settings.caliandarPosition
    val lazyColumnState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = {
        Settings.data.size
    }, initialPage = initPage)
    var showDropdown by remember { mutableStateOf(false) }
    var showDropdownMenuPos by rememberSaveable { mutableIntStateOf(1) }
    BackHandler(drawerState.isClosed || showDropdown) {
        if (drawerState.isClosed) coroutineScope.launch { drawerState.open() }
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
        val data = context.intent
        if (data?.data != null) {
            when {
                data.data.toString().contains("shortcuts=1") -> {
                    if (Settings.destinations != AllDestinations.VYBRANAE_LIST) {
                        navigationActions.navigateToVybranaeList()
                    }
                }

                data.data.toString().contains("shortcuts=3") -> {
                    if (Settings.destinations != AllDestinations.MAE_NATATKI_MENU) {
                        navigationActions.navigateToMaeNatatkiMenu()
                    }
                }

                data.data.toString().contains("shortcuts=2") -> {
                    if (Settings.destinations.contains("Biblijateka", ignoreCase = true)) {
                        navigationActions.navigateToBiblijatekaList(k.getString("navigate", AllDestinations.BIBLIJATEKA_NIADAUNIA) ?: AllDestinations.BIBLIJATEKA_NIADAUNIA)
                    }
                }
            }
            context.intent?.data = null
        }
        val extras = context.intent?.extras
        if (extras != null) {
            val widgetday = "widget_day"
            val widgetmun = "widget_mun"
            if (extras.getBoolean(widgetmun, false)) {
                val caliandarPosition = extras.getInt("position", Settings.caliandarPosition)
                if (Settings.destinations == AllDestinations.KALIANDAR || Settings.destinations == AllDestinations.KALIANDAR_YEAR) {
                    coroutineScope.launch {
                        if (k.getBoolean(
                                "caliandarList",
                                false
                            )
                        ) {
                            Settings.caliandarPosition = caliandarPosition
                            lazyColumnState.scrollToItem(caliandarPosition)
                        } else pagerState.scrollToPage(caliandarPosition)
                    }
                } else {
                    if (k.getBoolean("caliandarList", false)) navigationActions.navigateToKaliandarYear()
                    else navigationActions.navigateToKaliandar()
                }
            }
            if (extras.getBoolean(widgetday, false)) {
                val calendar = Calendar.getInstance()
                var caliandarPosition = Settings.caliandarPosition
                for (i in Settings.data.indices) {
                    if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
                        caliandarPosition = i
                        break
                    }
                }
                if (k.getBoolean("caliandarList", false)) {
                    if (Settings.destinations == AllDestinations.KALIANDAR_YEAR) {
                        coroutineScope.launch {
                            lazyColumnState.scrollToItem(caliandarPosition)
                        }
                    } else {
                        navigationActions.navigateToKaliandarYear()
                    }
                } else {
                    if (Settings.destinations == AllDestinations.KALIANDAR) {
                        coroutineScope.launch {
                            pagerState.scrollToPage(caliandarPosition)
                        }
                    } else {
                        Settings.caliandarPosition = caliandarPosition
                        navigationActions.navigateToKaliandar()
                    }
                }
            }
            if (extras.getBoolean("sabytie", false)) {
                val calendar = Calendar.getInstance()
                val chyt = extras.getInt("data")
                val year = extras.getInt("year")
                calendar.set(Calendar.DAY_OF_YEAR, chyt)
                calendar.set(Calendar.YEAR, year)
                for (i in Settings.data.indices) {
                    if (calendar[Calendar.DAY_OF_YEAR] == Settings.data[i][24].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
                        Settings.caliandarPosition = i
                        break
                    }
                }
                if (Settings.destinations == AllDestinations.KALIANDAR || Settings.destinations == AllDestinations.KALIANDAR_YEAR) {
                    coroutineScope.launch {
                        if (k.getBoolean(
                                "caliandarList",
                                false
                            )
                        ) lazyColumnState.scrollToItem(Settings.caliandarPosition)
                        else pagerState.scrollToPage(Settings.caliandarPosition)
                    }
                } else {
                    if (k.getBoolean("caliandarList", false)) navigationActions.navigateToKaliandarYear()
                    else navigationActions.navigateToKaliandar()
                }
            }
        }
        context.intent = null
    }
    if (drawerState.isOpen) isAppearanceLight = !context.dzenNoch
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
        dialogUmounyiaZnachenni = false
        navigationActions.navigateToShtoNovaga()
    }
    var textFieldValueState by remember { mutableStateOf("") }
    if (logView) {
        DialogLogProgramy {
            logView = false
        }
    }
    if (removeAllVybranaeDialog || removeAllNatatkiDialog) {
        DialogDelite(
            title = if (removeAllVybranaeDialog) stringResource(R.string.del_all_vybranoe)
            else stringResource(R.string.delite_all_natatki),
            onDismiss = {
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
    ModalNavigationDrawer(drawerContent = {
        DrawView(
            drawerScrollStete = drawerScrollStete,
            route = currentRoute,
            cytata = cytata,
            navigateToRazdel = { razdzel ->
                when (razdzel) {
                    AllDestinations.KALIANDAR -> {
                        if (k.getBoolean("caliandarList", false)) navigationActions.navigateToKaliandarYear()
                        else navigationActions.navigateToKaliandar()
                    }

                    AllDestinations.BOGASLUJBOVYIA_MENU -> navigationActions.navigateToBogaslujbovyiaMenu()
                    AllDestinations.MALITVY_MENU -> navigationActions.navigateToMalitvyMenu()
                    AllDestinations.BIBLIA_SEMUXA -> navigationActions.navigateToBibliaSemuxa()
                    AllDestinations.BIBLIA_BOKUNA -> navigationActions.navigateToBibliaBokuna()
                    AllDestinations.BIBLIA_NADSAN -> navigationActions.navigateToBibliaNadsan()
                    AllDestinations.BIBLIA_CHARNIAUSKI -> navigationActions.navigateToBibliaCharniauski()
                    AllDestinations.BIBLIA_SINODAL -> navigationActions.navigateToBibliaSinodal()
                    AllDestinations.VYBRANAE_LIST -> navigationActions.navigateToVybranaeList()
                    AllDestinations.AKAFIST_MENU -> navigationActions.navigateToAkafistMenu()
                    AllDestinations.RUJANEC_MENU -> navigationActions.navigateToRujanecMenu()
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
                    AllDestinations.UNDER_SVAITY_MUNU -> navigationActions.navigateToSviaty()
                    AllDestinations.UNDER_PARAFII_BGKC -> navigationActions.navigateToParafiiBgkc()
                    AllDestinations.UNDER_PASHALIA -> navigationActions.navigateToPashalia()
                }
                coroutineScope.launch {
                    drawerState.close()
                }
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (!searchText) {
                            Text(
                                title.uppercase(),
                                color = textTollBarColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = Settings.fontInterface.sp
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
                                    var edit = newText
                                    edit = edit.replace("и", "і")
                                    edit = edit.replace("щ", "ў")
                                    edit = edit.replace("И", "І")
                                    edit = edit.replace("Щ", "Ў")
                                    edit = edit.replace("ъ", "'")
                                    textFieldValueState = edit
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
                                    if (textFieldValueState.isNotEmpty()) {
                                        IconButton(onClick = { textFieldValueState = "" }) {
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
                                ),
                                textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                            )
                        }
                    },
                    navigationIcon = {
                        if (searchText) {
                            IconButton(
                                onClick = {
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
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
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
                            if (currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.RUJANEC_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute.contains("BIBLIJATEKA", ignoreCase = true) || currentRoute.contains("PIESNY", ignoreCase = true) || currentRoute == AllDestinations.UNDER_PASHALIA) {
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
                                IconButton({
                                    showDropdownMenuPos = 1
                                    showDropdown = !showDropdown
                                }) {
                                    Icon(
                                        painter = if (isToDay) painterResource(R.drawable.event) else painterResource(R.drawable.event_upcoming),
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
                                    text = { Text(stringResource(R.string.tools_item), fontSize = (Settings.fontInterface - 2).sp) },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.settings),
                                            contentDescription = ""
                                        )
                                    }
                                )
                                if (currentRoute.contains(AllDestinations.KALIANDAR)) {
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            dialogUmounyiaZnachenni = true
                                        },
                                        text = { Text(stringResource(R.string.munu_symbols), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.info),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            navigationActions.navigateToPadzeiView()
                                        },
                                        text = { Text(stringResource(R.string.sabytie), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.event),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            navigationActions.navigateToSearchSvityia()
                                        },
                                        text = { Text(stringResource(R.string.search_svityia), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.search),
                                                contentDescription = ""
                                            )
                                        }
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
                                                if (sortedNatatki == Settings.SORT_BY_ABC) Settings.SORT_BY_TIME
                                                else Settings.SORT_BY_ABC
                                            k.edit {
                                                if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) putInt(
                                                    "sortedVybranae",
                                                    sortedVybranae
                                                )
                                                else putInt("natatki_sort", sortedNatatki)
                                            }
                                        },
                                        text = {
                                            if (currentRoute.contains(AllDestinations.VYBRANAE_LIST)) {
                                                if (sortedVybranae == Settings.SORT_BY_TIME) Text(
                                                    stringResource(
                                                        R.string.sort_alf
                                                    )
                                                )
                                                else Text(stringResource(R.string.sort_add), fontSize = (Settings.fontInterface - 2).sp)
                                            } else {
                                                if (sortedNatatki == Settings.SORT_BY_TIME) Text(
                                                    stringResource(
                                                        R.string.sort_alf
                                                    ), fontSize = (Settings.fontInterface - 2).sp
                                                )
                                                else Text(stringResource(R.string.sort_add), fontSize = (Settings.fontInterface - 2).sp)
                                            }
                                        },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.sort),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        navigationActions.navigateToPraNas()
                                    },
                                    text = { Text(stringResource(R.string.pra_nas), fontSize = (Settings.fontInterface - 2).sp) },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.info),
                                            contentDescription = ""
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        navigationActions.navigateToHelp()
                                    },
                                    text = { Text(stringResource(R.string.help), fontSize = (Settings.fontInterface - 2).sp) },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.favorite),
                                            contentDescription = ""
                                        )
                                    }
                                )
                                if (k.getBoolean("admin", false)) {
                                    HorizontalDivider()
                                    if (currentRoute == AllDestinations.AKAFIST_MENU || currentRoute == AllDestinations.RUJANEC_MENU || currentRoute == AllDestinations.MALITVY_MENU || currentRoute == AllDestinations.BOGASLUJBOVYIA_MENU || currentRoute.contains("BIBLIJATEKA", ignoreCase = true) || currentRoute.contains("PIESNY", ignoreCase = true) || currentRoute == AllDestinations.UNDER_PASHALIA) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                navigationActions.navigateToSearchBiblia(Settings.PEREVODSEMUXI, true)
                                            },
                                            text = { Text(stringResource(R.string.searche_bogasluz_text), fontSize = (Settings.fontInterface - 2).sp) },
                                            trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.search),
                                                    contentDescription = ""
                                                )
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            logView = true
                                        },
                                        text = { Text(stringResource(R.string.log_m), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.description),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    if (currentRoute.contains(AllDestinations.KALIANDAR) || currentRoute.contains("BIBLIJATEKA", ignoreCase = true)) {
                                        DropdownMenuItem(
                                            onClick = {
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
                                            },
                                            text = { Text(stringResource(R.string.redagaktirovat), fontSize = (Settings.fontInterface - 2).sp) },
                                            trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.edit),
                                                    contentDescription = ""
                                                )
                                            }
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
                when (Settings.destinations) {
                    AllDestinations.KALIANDAR -> {
                        val dataToDay = findCaliandarToDay(context, false)
                        val fling = PagerDefaults.flingBehavior(
                            state = pagerState,
                            pagerSnapDistance = PagerSnapDistance.atMost(1)
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
                                page,
                                innerPadding,
                                navigateToCytanneList = { title, chytanne, biblia ->
                                    navigationActions.navigateToCytanniList(
                                        title,
                                        chytanne,
                                        biblia,
                                        Settings.PEREVODSEMUXI,
                                        -1
                                    )
                                },
                                navigateToSvityiaView = { svity, position ->
                                    navigationActions.navigateToSvityiaView(svity, position)
                                },
                                navigateToBogaslujbovyia = { title, resurs ->
                                    navigationActions.navigateToBogaslujbovyia(title, resurs)
                                },
                                navigateToKniga = {
                                    dialogKniga = true
                                }
                            )
                        }
                    }

                    AllDestinations.BOGASLUJBOVYIA_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_BOGASLUJBOVYIA,
                        searchText,
                        textFieldValueState
                    )

                    AllDestinations.AKAFIST_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_AKAFIST,
                        searchText,
                        textFieldValueState
                    )

                    AllDestinations.BIBLIJATEKA_NIADAUNIA -> BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_NIADAUNIA, innerPadding, searchText, textFieldValueState)

                    AllDestinations.BIBLIJATEKA_SPEUNIKI -> BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_SPEUNIKI, innerPadding, searchText, textFieldValueState)

                    AllDestinations.BIBLIJATEKA_MALITOUNIKI -> BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_MALITOUNIKI, innerPadding, searchText, textFieldValueState)

                    AllDestinations.BIBLIJATEKA_GISTORYIA -> BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_GISTORYIA, innerPadding, searchText, textFieldValueState)

                    AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA -> BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA, innerPadding, searchText, textFieldValueState)

                    AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU -> BiblijtekaList(navController, AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU, innerPadding, searchText, textFieldValueState)

                    AllDestinations.PIESNY_PRASLAULENNIA -> PiesnyList(navController, AllDestinations.PIESNY_PRASLAULENNIA, innerPadding, searchText, textFieldValueState)

                    AllDestinations.PIESNY_ZA_BELARUS -> PiesnyList(navController, AllDestinations.PIESNY_ZA_BELARUS, innerPadding, searchText, textFieldValueState)

                    AllDestinations.PIESNY_DA_BAGARODZICY -> PiesnyList(navController, AllDestinations.PIESNY_DA_BAGARODZICY, innerPadding, searchText, textFieldValueState)

                    AllDestinations.PIESNY_KALIADNYIA -> PiesnyList(navController, AllDestinations.PIESNY_KALIADNYIA, innerPadding, searchText, textFieldValueState)

                    AllDestinations.PIESNY_TAIZE -> PiesnyList(navController, AllDestinations.PIESNY_TAIZE, innerPadding, searchText, textFieldValueState)

                    AllDestinations.UNDER_SVAITY_MUNU -> SviatyList(navController, innerPadding)

                    AllDestinations.UNDER_PARAFII_BGKC -> ParafiiBGKC(navController, innerPadding)

                    AllDestinations.UNDER_PASHALIA -> {
                        if (!searchText) textFieldValueState = ""
                        Pashalia(navController, innerPadding, searchText, textFieldValueState)
                    }

                    AllDestinations.RUJANEC_MENU -> BogaslujbovyiaMenu(
                        navController,
                        innerPadding,
                        Settings.MENU_RUJANEC,
                        searchText,
                        textFieldValueState
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
                        textFieldValueState
                    )

                    AllDestinations.BIBLIA_SEMUXA -> BibliaMenu(
                        navController,
                        Settings.PEREVODSEMUXI,
                        innerPadding,
                        navigateToSearchBible = { perevod ->
                            navigationActions.navigateToSearchBiblia(perevod, false)
                        },
                        navigateToCytanniList = { chytanne, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_BIBLIA,
                                perevod2,
                                -1
                            )
                        },
                        navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }
                    )

                    AllDestinations.BIBLIA_BOKUNA -> BibliaMenu(
                        navController,
                        Settings.PEREVODBOKUNA,
                        innerPadding,
                        navigateToSearchBible = { perevod ->
                            navigationActions.navigateToSearchBiblia(perevod, false)
                        },
                        navigateToCytanniList = { chytanne, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_BIBLIA,
                                perevod2,
                                -1
                            )
                        },
                        navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }
                    )

                    AllDestinations.BIBLIA_NADSAN -> BibliaMenu(
                        navController,
                        Settings.PEREVODNADSAN,
                        innerPadding,
                        navigateToSearchBible = { perevod ->
                            navigationActions.navigateToSearchBiblia(perevod, false)
                        },
                        navigateToCytanniList = { chytanne, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_BIBLIA,
                                perevod2,
                                -1
                            )
                        },
                        navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }
                    )

                    AllDestinations.BIBLIA_CHARNIAUSKI -> BibliaMenu(
                        navController,
                        Settings.PEREVODCARNIAUSKI,
                        innerPadding,
                        navigateToSearchBible = { perevod ->
                            navigationActions.navigateToSearchBiblia(perevod, false)
                        },
                        navigateToCytanniList = { chytanne, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_BIBLIA,
                                perevod2,
                                -1
                            )
                        },
                        navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }
                    )

                    AllDestinations.BIBLIA_SINODAL -> BibliaMenu(
                        navController,
                        Settings.PEREVODSINOIDAL,
                        innerPadding,
                        navigateToSearchBible = { perevod ->
                            navigationActions.navigateToSearchBiblia(perevod, false)
                        },
                        navigateToCytanniList = { chytanne, perevod2 ->
                            navigationActions.navigateToCytanniList(
                                "",
                                chytanne,
                                Settings.CHYTANNI_BIBLIA,
                                perevod2,
                                -1
                            )
                        },
                        navigateToBogaslujbovyia = { title, resurs ->
                            navigationActions.navigateToBogaslujbovyia(title, resurs)
                        }
                    )

                    AllDestinations.KALIANDAR_YEAR -> {
                        val dataToDay = findCaliandarToDay(context, false)
                        LaunchedEffect(lazyColumnState) {
                            snapshotFlow { lazyColumnState.firstVisibleItemIndex }.collect { index ->
                                val data = Settings.data[index]
                                isToDay = data[1] == dataToDay[1] && data[2] == dataToDay[2] && data[3] == dataToDay[3]
                            }
                        }
                        KaliandarScreenYear(
                            coroutineScope = coroutineScope,
                            lazyColumnState = lazyColumnState,
                            innerPadding,
                            navigateToSvityiaView = { svity, position ->
                                navigationActions.navigateToSvityiaView(svity, position)
                            }
                        )
                    }

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
                        if (showDropdownMenuPos == 1) {
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
                                },
                                close = { showDropdown = false })
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            dialogKniga,
            enter = fadeIn(
                tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ),
            exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
        ) {
            KaliandarKnigaView(
                colorBlackboard,
                navigateToBogaslujbovyia = { title, resourse ->
                    dialogKniga = false
                    navigationActions.navigateToBogaslujbovyia(title, resourse)
                },
                navigateToSvityiaView = { svity, position ->
                    dialogKniga = false
                    navigationActions.navigateToSvityiaView(svity, position)
                }) {
                dialogKniga = false
            }
        }
    }
}

@Composable
fun DialogLogProgramy(
    onDismiss: () -> Unit
) {
    val context = LocalActivity.current as MainActivity
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    context.removelightSensor()
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
            Column {
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
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            logView.createAndSentFile()
                            onDismiss()
                            if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) context.setlightSensor()
                        },
                        shape = MaterialTheme.shapes.small
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
fun DialogUpdateMalitounik(
    total: Float,
    bytesDownload: Float,
    onDismiss: () -> Unit
) {
    val totalSizeUpdate = if (total / 1024 > 1000) {
        " ${formatFigureTwoPlaces(total / 1024 / 1024)} Мб "
    } else {
        " ${formatFigureTwoPlaces(total / 1024)} Кб "
    }
    val bytesDownloadUpdate = if (bytesDownload / 1024 > 1000) {
        " ${formatFigureTwoPlaces(bytesDownload / 1024 / 1024)} Мб "
    } else {
        " ${formatFigureTwoPlaces(bytesDownload / 1024)} Кб "
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.update_title).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column {
                    Text(modifier = Modifier.padding(bottom = 10.dp), text = stringResource(R.string.update_program_progress, bytesDownloadUpdate, totalSizeUpdate), fontSize = Settings.fontInterface.sp)
                    LinearProgressIndicator(
                        progress = { (bytesDownload / total).toFloat() }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogUpdateNoWiFI(
    totalBytesToDownload: Float,
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit
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
            Column {
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
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}