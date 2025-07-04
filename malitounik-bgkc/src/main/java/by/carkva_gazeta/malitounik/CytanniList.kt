package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Button
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

val cytanniListItemData = MutableStateFlow(ArrayList<CytanniListItemData>())
var autoScrollJob: Job? = null
var autoScrollTextVisableJob: Job? = null

class CytanniListItems(
    biblia: Int, private val page: Int, cytanne: String, perevod: String
) {
    private val t1 = cytanne.indexOf(";")
    private val knigaText = if (t1 == -1) cytanne.substringBeforeLast(" ")
    else {
        val sb = cytanne.substring(0, t1)
        sb.substringBeforeLast(" ")
    }
    private val chteniaNewPage = knigaText + " ${page + 1}"
    private val mChekList = checkList()
    private val _filteredItems = MutableStateFlow(
        if (mChekList.isEmpty()) {
            val resultPage = if (biblia == Settings.CHYTANNI_BIBLIA) getBible(
                chteniaNewPage, perevod, biblia
            )
            else getBible(cytanne, perevod, biblia, true)
            cytanniListItemData.value.add(CytanniListItemData(page, resultPage))
            resultPage
        } else {
            mChekList
        }
    )
    var filteredItems: StateFlow<ArrayList<CytanniListData>> = _filteredItems
    private fun checkList(): ArrayList<CytanniListData> {
        val result = ArrayList<CytanniListData>()
        val removeList = ArrayList<CytanniListItemData>()
        for (i in 0 until cytanniListItemData.value.size) {
            if (cytanniListItemData.value[i].page !in page - 1..page + 1) {
                removeList.add(cytanniListItemData.value[i])
            }
            if (cytanniListItemData.value[i].page == page) {
                result.addAll(cytanniListItemData.value[i].item)
            }
        }
        cytanniListItemData.value.removeAll(removeList.toSet())
        return result
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CytanniList(
    navController: NavHostController, title: String, cytanne: String, biblia: Int, perevodRoot: String, position: Int, list: ArrayList<LazyListState>
) {
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
    var skipUtran by remember { mutableStateOf(position == -2) }
    var positionRemember by rememberSaveable { mutableIntStateOf(position) }
    var utranEndPosition by remember { mutableIntStateOf(0) }
    val listState = remember { list }
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window, view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    val maxLine = remember { mutableIntStateOf(1) }
    var isToDay = -1
    val calendar = Calendar.getInstance()
    for (i in Settings.data.indices) {
        if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
            isToDay = i
            break
        }
    }
    var subTitle by remember { mutableStateOf("") }
    val colorTollBar = if (isToDay == Settings.caliandarPosition || biblia == Settings.CHYTANNI_BIBLIA || biblia == Settings.CHYTANNI_VYBRANAE) MaterialTheme.colorScheme.onTertiary
    else StrogiPost
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var showDropdown by remember { mutableStateOf(false) }
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    var autoScroll by rememberSaveable { mutableStateOf(false) }
    var autoScrollSensor by rememberSaveable { mutableStateOf(false) }
    var autoScrollSpeed by remember { mutableIntStateOf(k.getInt("autoscrollSpid", 60)) }
    var autoScrollTextVisable by remember { mutableStateOf(false) }
    var autoScrollText by remember { mutableStateOf("") }
    var autoScrollTextColor by remember { mutableStateOf(Primary) }
    var autoScrollTextColor2 by remember { mutableStateOf(PrimaryTextBlack) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var isParallel by remember {
        mutableStateOf(
            when (biblia) {
                Settings.CHYTANNI_MARANATA -> k.getBoolean("paralel_maranata", true)
                Settings.CHYTANNI_BIBLIA -> {
                    perevodRoot != Settings.PEREVODNADSAN
                }

                else -> false
            }
        )
    }
    var isParallelVisable by remember { mutableStateOf(false) }
    var paralelChtenia by rememberSaveable { mutableStateOf("") }
    var menuPosition by remember { mutableIntStateOf(0) }
    var perevod by remember {
        mutableStateOf(
            when (biblia) {
                Settings.CHYTANNI_LITURGICHNYIA -> k.getString("perevod", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI

                Settings.CHYTANNI_MARANATA -> k.getString("perevodMaranata", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI

                else -> perevodRoot
            }
        )
    }
    val prevodName = when (perevod) {
        Settings.PEREVODSEMUXI -> "biblia"
        Settings.PEREVODBOKUNA -> "bokuna"
        Settings.PEREVODCARNIAUSKI -> "carniauski"
        Settings.PEREVODNADSAN -> "nadsan"
        Settings.PEREVODSINOIDAL -> "sinaidal"
        else -> "biblia"
    }
    val titleBible = title.ifEmpty {
        when (perevod) {
            Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia)
            Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun)
            Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski)
            Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
            Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal)
            else -> stringResource(R.string.title_biblia)
        }
    }
    val initPage = if (biblia == Settings.CHYTANNI_BIBLIA) {
        if (Settings.bibleTimeList) k.getInt("bible_time_${prevodName}_glava", 0)
        else cytanne.substringAfterLast(" ").toInt() - 1
    } else 0
    val pagerState = rememberPagerState(pageCount = {
        listState.size
    }, initialPage = initPage)
    val lazyRowState = rememberLazyListState()
    val fling = PagerDefaults.flingBehavior(
        state = pagerState, pagerSnapDistance = PagerSnapDistance.atMost(1)
    )
    var selectedIndex by remember {
        mutableIntStateOf(if (biblia == Settings.CHYTANNI_BIBLIA) initPage else 0)
    }
    var selectPerevod by remember { mutableStateOf(false) }
    var selectOldPerevod by remember { mutableStateOf(perevod) }
    var isBottomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
    if (Settings.bibleTimeList) {
        Settings.bibleTimeList = false
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                listState[selectedIndex].scrollToItem(k.getInt("bible_time_${prevodName}_stix", 0))
            }
        }
    }
    if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODNADSAN || perevod == Settings.PEREVODSINOIDAL) {
        if (knigaBiblii(knigaText) == 21 && listState.size == 150) {
            listState.add(rememberLazyListState())
        }
    }
    if (perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODCARNIAUSKI) {
        if (knigaBiblii(knigaText) == 21 && listState.size == 151) {
            listState.removeAt(150)
            if (selectedIndex > 149) selectedIndex = 149
        }
    }
    if (perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODSINOIDAL) {
        if (knigaBiblii(knigaText) == 33 && listState.size == 12) {
            listState.add(rememberLazyListState())
            listState.add(rememberLazyListState())
        }
    }
    if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA) {
        if (knigaBiblii(knigaText) == 33 && listState.size == 14) {
            listState.removeAt(13)
            listState.removeAt(12)
            if (selectedIndex > 11) selectedIndex = 11
        }
    }
    if (perevod == Settings.PEREVODCARNIAUSKI) {
        if (knigaBiblii(knigaText) == 31 && listState.size == 5) {
            listState.add(rememberLazyListState())
        }
    }
    if (biblia == Settings.CHYTANNI_BIBLIA && selectPerevod) {
        selectPerevod = false
        if (!(selectOldPerevod == Settings.PEREVODCARNIAUSKI || selectOldPerevod == Settings.PEREVODBOKUNA)) {
            if (perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODBOKUNA) {
                if (knigaBiblii(knigaText) == 21) {
                    if (selectedIndex in 10..112) selectedIndex += 1
                    if (selectedIndex == 113) selectedIndex = 114
                    if (selectedIndex == 114 || selectedIndex == 115) selectedIndex = 116
                    if (selectedIndex in 116..145) selectedIndex += 1
                    if (selectedIndex == 146) selectedIndex = 147
                    if (selectedIndex == 9) {
                        selectedIndex = 10
                    }
                }
            }
        }
        if (!(selectOldPerevod == Settings.PEREVODSEMUXI || selectOldPerevod == Settings.PEREVODNADSAN || selectOldPerevod == Settings.PEREVODSINOIDAL)) {
            if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODNADSAN || perevod == Settings.PEREVODSINOIDAL) {
                if (knigaBiblii(knigaText) == 21) {
                    if (selectedIndex == 10) selectedIndex = 9
                    if (selectedIndex in 11..113) selectedIndex -= 1
                    if (selectedIndex == 114 || selectedIndex == 115) selectedIndex = 113
                    if (selectedIndex == 116) selectedIndex = 114
                    if (selectedIndex in 117..146) selectedIndex -= 1
                    if (selectedIndex == 147) selectedIndex = 146
                    if (selectedIndex == 10) {
                        selectedIndex = 9
                    }
                }
            }
        }
        if (perevod == Settings.PEREVODSINOIDAL) {
            if (knigaBiblii(knigaText) == 31 && listState.size == 6) {
                if (selectedIndex == 5) {
                    for (i in 5 downTo 1) listState.removeAt(i)
                    selectedIndex = 0
                    knigaText = "Пасл Ер"
                } else {
                    listState.removeAt(5)
                }
            }
        }
        if (perevod == Settings.PEREVODCARNIAUSKI) {
            if (knigaBiblii(knigaText) == 30) {
                (1..5).forEach { _ ->
                    listState.add(rememberLazyListState())
                }
                selectedIndex = 5
                knigaText = "Бар"
            }
        }
    }
    val vybranoeList = remember { SnapshotStateList<VybranaeData>() }
    var isPerevodError by remember { mutableStateOf(false) }
    var initVybranoe by remember { mutableStateOf(true) }
    var isVybranoe by remember { mutableStateOf(false) }
    var saveVybranoe by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val gson = Gson()
    val type = TypeToken.getParameterized(ArrayList::class.java, VybranaeData::class.java).type
    val file = File("${LocalContext.current.filesDir}/vybranoe_${prevodName}.json")
    if (initVybranoe) {
        vybranoeList.clear()
        if (file.exists()) {
            vybranoeList.addAll(gson.fromJson(file.readText(), type))
        }
        initVybranoe = false
        isVybranoe = false
        if (vybranoeList.isNotEmpty()) {
            for (i in 0 until vybranoeList.size) {
                if (knigaText == vybranoeList[i].knigaText && vybranoeList[i].glava == selectedIndex) {
                    isVybranoe = true
                    break
                }
            }
        }
    }
    if (saveVybranoe) {
        if (isVybranoe) {
            var pos = 0
            for (i in 0 until vybranoeList.size) {
                if (knigaText == vybranoeList[i].knigaText && vybranoeList[i].glava == selectedIndex) {
                    pos = i
                    break
                }
            }
            vybranoeList.removeAt(pos)
            Toast.makeText(context, context.getString(R.string.removeVybranoe), Toast.LENGTH_SHORT).show()
        } else {
            val kniga = knigaBiblii(knigaText)
            val bibleCount = bibleCount(perevod, kniga >= 50)
            var titleBibleVybranoe = ""
            for (w in 0 until bibleCount.size) {
                if (bibleCount[w].subTitle == knigaText) {
                    titleBibleVybranoe = bibleCount[w].title
                    break
                }
            }
            vybranoeList.add(
                0, VybranaeData(
                    Calendar.getInstance().timeInMillis, titleBibleVybranoe, knigaText, selectedIndex, perevod
                )
            )
            Toast.makeText(context, context.getString(R.string.addVybranoe), Toast.LENGTH_SHORT).show()
        }
        isVybranoe = !isVybranoe
        if (vybranoeList.isEmpty() && file.exists()) {
            file.delete()
        } else {
            file.writer().use {
                it.write(gson.toJson(vybranoeList, type))
            }
        }
        saveVybranoe = false
    }
    LaunchedEffect(autoScroll) {
        if (autoScroll) {
            autoScrollJob?.cancel()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    while (true) {
                        delay(autoScrollSpeed.toLong())
                        listState[selectedIndex].scrollBy(2f)
                    }
                }
            }
        } else {
            autoScrollJob?.cancel()
        }
    }
    var isSelectMode by rememberSaveable { mutableStateOf(false) }
    var backPressHandled by remember { mutableStateOf(false) }
    val actyvity = LocalActivity.current as MainActivity
    if (autoScrollSensor) {
        actyvity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    BackHandler(!backPressHandled || isSelectMode || isParallelVisable || showDropdown) {
        when {
            isSelectMode -> {
                if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) actyvity.setlightSensor()
                isSelectMode = false
            }

            isParallelVisable -> isParallelVisable = false
            showDropdown -> {
                showDropdown = false
                if (autoScrollSensor) autoScroll = true
            }

            !backPressHandled -> {
                fullscreen = false
                val prefEditors = k.edit()
                if (biblia == Settings.CHYTANNI_BIBLIA) {
                    prefEditors.putString("bible_time_${prevodName}_kniga", knigaText)
                    prefEditors.putInt("bible_time_${prevodName}_glava", selectedIndex)
                    prefEditors.putInt(
                        "bible_time_${prevodName}_stix", listState[selectedIndex].firstVisibleItemIndex
                    )
                }
                prefEditors.apply()
                autoScrollJob?.cancel()
                autoScrollTextVisableJob?.cancel()
                backPressHandled = true
                actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                navController.popBackStack()
            }
        }
    }
    var isUpList by remember { mutableStateOf(false) }
    if (isUpList) {
        LaunchedEffect(Unit) {
            isUpList = false
            coroutineScope.launch {
                listState[selectedIndex].scrollToItem(0)
            }
        }
    }
    LaunchedEffect(fullscreen) {
        val controller = WindowCompat.getInsetsController((view.context as Activity).window, view)
        if (fullscreen) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.show(WindowInsetsCompat.Type.navigationBars())
        }
    }
    var isCopyMode by remember { mutableStateOf(false) }
    var isShareMode by remember { mutableStateOf(false) }
    var isSelectAll by remember { mutableStateOf(false) }
    var dialogRazdel by remember { mutableStateOf(false) }
    if (dialogRazdel) {
        DialogRazdzel(listState, autoScrollSensor, setSelectedIndex = { selectedIndex = it }, setAutoScroll = { autoScroll = it }) {
            dialogRazdel = false
        }
    }
    Scaffold(topBar = {
        AnimatedVisibility(
            !fullscreen, enter = fadeIn(
                tween(
                    durationMillis = 500, easing = LinearOutSlowInEasing
                )
            ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
        ) {
            TopAppBar(
                title = {
                    if (!isSelectMode) {
                        Column {
                            if (!isParallelVisable) {
                                Text(
                                    modifier = Modifier.clickable {
                                        maxLine.intValue = Int.MAX_VALUE
                                        coroutineScope.launch {
                                            delay(5000L)
                                            maxLine.intValue = 1
                                        }
                                    }, text = titleBible.uppercase(), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis, fontSize = Settings.fontInterface.sp
                                )
                                Text(
                                    modifier = Modifier.clickable {
                                        maxLine.intValue = Int.MAX_VALUE
                                        coroutineScope.launch {
                                            delay(5000L)
                                            maxLine.intValue = 1
                                        }
                                    }, text = subTitle, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis, fontSize = Settings.fontInterface.sp
                                )
                            } else {
                                Text(
                                    modifier = Modifier.clickable {
                                        maxLine.intValue = Int.MAX_VALUE
                                        coroutineScope.launch {
                                            delay(5000L)
                                            maxLine.intValue = 1
                                        }
                                    }, text = stringResource(R.string.paralel), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis, fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (isSelectMode || isParallelVisable) {
                        IconButton(onClick = {
                            if (isSelectMode) {
                                if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) actyvity.setlightSensor()
                                isSelectMode = false
                            } else isParallelVisable = false
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.close), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                            )
                        })
                    } else {
                        IconButton(onClick = {
                            when {
                                isParallelVisable -> isParallelVisable = false
                                showDropdown -> {
                                    showDropdown = false
                                    if (autoScrollSensor) autoScroll = true
                                }

                                else -> {
                                    if (!backPressHandled) {
                                        backPressHandled = true
                                        fullscreen = false
                                        k.edit {
                                            if (biblia == Settings.CHYTANNI_BIBLIA) {
                                                putString("bible_time_${prevodName}_kniga", knigaText)
                                                putInt("bible_time_${prevodName}_glava", selectedIndex)
                                                putInt(
                                                    "bible_time_${prevodName}_stix", listState[selectedIndex].firstVisibleItemIndex
                                                )
                                            }
                                        }
                                        autoScrollJob?.cancel()
                                        autoScrollTextVisableJob?.cancel()
                                        actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                            )
                        })
                    }
                }, actions = {
                    if (isSelectMode) {
                        IconButton(onClick = {
                            isSelectAll = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.select_all), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        IconButton(onClick = {
                            isCopyMode = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.content_copy), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        IconButton(onClick = {
                            isShareMode = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.share), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    } else {
                        if (!isBottomBar) {
                            var expandedUp by remember { mutableStateOf(false) }
                            if (listState[selectedIndex].canScrollForward) {
                                val iconAutoScroll =
                                    if (autoScrollSensor) painterResource(R.drawable.stop_circle)
                                    else painterResource(R.drawable.play_circle)
                                IconButton(onClick = {
                                    autoScroll = !autoScroll
                                    autoScrollSensor = !autoScrollSensor
                                    if (autoScrollSensor) actyvity.window.addFlags(
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                    )
                                    else actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                }) {
                                    Icon(
                                        iconAutoScroll,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            } else if (listState[selectedIndex].canScrollBackward) {
                                IconButton(onClick = {
                                    isUpList = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_upward),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            if (biblia == Settings.CHYTANNI_BIBLIA) {
                                IconButton(
                                    onClick = {
                                        saveVybranoe = true
                                    }) {
                                    val icon = if (isVybranoe) painterResource(R.drawable.stars)
                                    else painterResource(R.drawable.star)
                                    Icon(
                                        painter = icon, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            IconButton(onClick = { expandedUp = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            DropdownMenu(
                                expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                                if (biblia == Settings.CHYTANNI_BIBLIA && listState.size - 1 > 1) {
                                    DropdownMenuItem(onClick = {
                                        expandedUp = false
                                        dialogRazdel = true
                                        autoScroll = false
                                    }, text = { Text(stringResource(R.string.pazdel), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.apps), contentDescription = ""
                                        )
                                    })
                                }
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    fullscreen = true
                                }, text = { Text(stringResource(R.string.fullscreen), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.fullscreen), contentDescription = ""
                                    )
                                })
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    showDropdown = !showDropdown
                                    autoScroll = false
                                    menuPosition = 2
                                }, text = { Text(stringResource(R.string.perevody), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.book_red), contentDescription = ""
                                    )
                                })
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    showDropdown = !showDropdown
                                    autoScroll = false
                                    menuPosition = 1
                                }, text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.format_size), contentDescription = ""
                                    )
                                })
                            }
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = colorTollBar)
            )
        }
    }, bottomBar = {
        if (showDropdown) {
            ModalBottomSheet(
                scrimColor = Color.Transparent, properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false), containerColor = MaterialTheme.colorScheme.surfaceContainer, onDismissRequest = {
                    showDropdown = false
                    if (autoScrollSensor) autoScroll = true
                }) {
                Column {
                    if (menuPosition == 2) {
                        Column(Modifier.selectableGroup()) {
                            if (isPerevodError) {
                                Text(
                                    stringResource(R.string.biblia_error), modifier = Modifier.padding(start = 10.dp, top = 10.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontSize = Settings.fontInterface.sp
                                )
                            }
                            Text(
                                stringResource(R.string.perevody), modifier = Modifier.padding(start = 10.dp, top = 10.dp), textAlign = TextAlign.Center, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                            )
                            val edit = k.edit()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cytanniListItemData.value.clear()
                                        selectOldPerevod = perevod
                                        perevod = Settings.PEREVODSEMUXI
                                        initVybranoe = true
                                        selectPerevod = true
                                        if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                            "perevodMaranata", perevod
                                        )
                                        if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                            "perevod", perevod
                                        )
                                        edit.apply()
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = perevod == Settings.PEREVODSEMUXI, onClick = {
                                        cytanniListItemData.value.clear()
                                        selectOldPerevod = perevod
                                        perevod = Settings.PEREVODSEMUXI
                                        initVybranoe = true
                                        selectPerevod = true
                                        if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                            "perevodMaranata", perevod
                                        )
                                        if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                            "perevod", perevod
                                        )
                                        edit.apply()
                                    })
                                Text(
                                    stringResource(R.string.title_biblia2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cytanniListItemData.value.clear()
                                        selectOldPerevod = perevod
                                        perevod = Settings.PEREVODBOKUNA
                                        initVybranoe = true
                                        selectPerevod = true
                                        if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                            "perevodMaranata", perevod
                                        )
                                        if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                            "perevod", perevod
                                        )
                                        edit.apply()
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = perevod == Settings.PEREVODBOKUNA, onClick = {
                                        cytanniListItemData.value.clear()
                                        selectOldPerevod = perevod
                                        perevod = Settings.PEREVODBOKUNA
                                        initVybranoe = true
                                        selectPerevod = true
                                        if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                            "perevodMaranata", perevod
                                        )
                                        if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                            "perevod", perevod
                                        )
                                        edit.apply()
                                    })
                                Text(
                                    stringResource(R.string.title_biblia_bokun2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cytanniListItemData.value.clear()
                                        selectOldPerevod = perevod
                                        perevod = Settings.PEREVODCARNIAUSKI
                                        initVybranoe = true
                                        selectPerevod = true
                                        if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                            "perevodMaranata", perevod
                                        )
                                        if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                            "perevod", perevod
                                        )
                                        edit.apply()
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = perevod == Settings.PEREVODCARNIAUSKI, onClick = {
                                        cytanniListItemData.value.clear()
                                        selectOldPerevod = perevod
                                        perevod = Settings.PEREVODCARNIAUSKI
                                        initVybranoe = true
                                        selectPerevod = true
                                        if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                            "perevodMaranata", perevod
                                        )
                                        if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                            "perevod", perevod
                                        )
                                        edit.apply()
                                    })
                                Text(
                                    stringResource(R.string.title_biblia_charniauski2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                )
                            }
                            if (biblia == Settings.CHYTANNI_BIBLIA || biblia == Settings.CHYTANNI_VYBRANAE) {
                                val kniga = knigaBiblii(knigaText)
                                if (kniga == 21) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                cytanniListItemData.value.clear()
                                                selectOldPerevod = perevod
                                                perevod = Settings.PEREVODNADSAN
                                                initVybranoe = true
                                                selectPerevod = true
                                            }, verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = perevod == Settings.PEREVODNADSAN, onClick = {
                                                cytanniListItemData.value.clear()
                                                selectOldPerevod = perevod
                                                perevod = Settings.PEREVODNADSAN
                                                initVybranoe = true
                                                selectPerevod = true
                                            })
                                        Text(
                                            stringResource(R.string.title_psalter), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                        )
                                    }
                                }
                            }
                            if (biblia != Settings.CHYTANNI_LITURGICHNYIA) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            cytanniListItemData.value.clear()
                                            selectOldPerevod = perevod
                                            perevod = Settings.PEREVODSINOIDAL
                                            initVybranoe = true
                                            selectPerevod = true
                                            if (biblia == Settings.CHYTANNI_MARANATA) {
                                                edit.putString(
                                                    "perevodMaranata", perevod
                                                )
                                            }
                                            edit.apply()
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODSINOIDAL, onClick = {
                                            cytanniListItemData.value.clear()
                                            selectOldPerevod = perevod
                                            perevod = Settings.PEREVODSINOIDAL
                                            initVybranoe = true
                                            selectPerevod = true
                                            if (biblia == Settings.CHYTANNI_MARANATA) {
                                                edit.putString(
                                                    "perevodMaranata", perevod
                                                )
                                            }
                                            edit.apply()
                                        })
                                    Text(
                                        stringResource(R.string.bsinaidal2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                        }
                    }
                    if (menuPosition == 1) {
                        Text(
                            stringResource(R.string.menu_font_size_app), modifier = Modifier.padding(start = 10.dp, top = 10.dp), fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                        Slider(
                            modifier = Modifier.padding(horizontal = 10.dp), valueRange = 18f..58f, value = fontSize, onValueChange = {
                                k.edit {
                                    putFloat("font_biblia", it)
                                }
                                fontSize = it
                            }, colors = SliderDefaults.colors(inactiveTrackColor = Divider)
                        )
                    }
                }
            }
        }
        if (!isSelectMode) {
            if (isBottomBar && !isParallelVisable) {
                AnimatedVisibility(
                    !fullscreen, enter = fadeIn(
                        tween(
                            durationMillis = 500, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                ) {
                    BottomAppBar(containerColor = colorTollBar) {
                        Row(
                            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(
                                onClick = {
                                    showDropdown = !showDropdown
                                    autoScroll = false
                                    menuPosition = 1
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.format_size), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            IconButton(
                                onClick = {
                                    showDropdown = !showDropdown
                                    autoScroll = false
                                    menuPosition = 2
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.book_red), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            IconButton(
                                onClick = {
                                    fullscreen = true
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.fullscreen), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            if (biblia == Settings.CHYTANNI_BIBLIA && listState.size - 1 > 1) {
                                IconButton(
                                    onClick = {
                                        autoScroll = false
                                        dialogRazdel = true
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.apps), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            if (biblia == Settings.CHYTANNI_BIBLIA) {
                                IconButton(
                                    onClick = {
                                        saveVybranoe = true
                                    }) {
                                    val icon = if (isVybranoe) painterResource(R.drawable.stars)
                                    else painterResource(R.drawable.star)
                                    Icon(
                                        painter = icon, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            if (!isParallelVisable) {
                                if (listState[selectedIndex].canScrollForward) {
                                    val iconAutoScroll = if (autoScrollSensor) painterResource(R.drawable.stop_circle)
                                    else painterResource(R.drawable.play_circle)
                                    IconButton(onClick = {
                                        autoScroll = !autoScroll
                                        autoScrollSensor = !autoScrollSensor
                                        if (autoScrollSensor) actyvity.window.addFlags(
                                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                        )
                                        else actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                    }) {
                                        Icon(
                                            iconAutoScroll, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                } else if (listState[selectedIndex].canScrollBackward) {
                                    IconButton(onClick = {
                                        isUpList = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_upward), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            } else {
                                autoScroll = false
                            }
                        }
                    }
                }
            }
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr), if (fullscreen) 0.dp else innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
            )
        ) {
            if (biblia == Settings.CHYTANNI_BIBLIA) {
                LaunchedEffect(selectedIndex) {
                    coroutineScope.launch {
                        isVybranoe = false
                        if (vybranoeList.isNotEmpty()) {
                            for (i in 0 until vybranoeList.size) {
                                if (knigaText == vybranoeList[i].knigaText && vybranoeList[i].glava == selectedIndex) {
                                    isVybranoe = true
                                    break
                                }
                            }
                        }
                        pagerState.scrollToPage(selectedIndex)
                        lazyRowState.scrollToItem(selectedIndex)
                    }
                }
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        selectedIndex = page
                        if (perevodRoot == Settings.PEREVODNADSAN) {
                            var kafizma = 1
                            if (page + 1 in 9..16) kafizma = 2
                            if (page + 1 in 17..23) kafizma = 3
                            if (page + 1 in 24..31) kafizma = 4
                            if (page + 1 in 32..36) kafizma = 5
                            if (page + 1 in 37..45) kafizma = 6
                            if (page + 1 in 46..54) kafizma = 7
                            if (page + 1 in 55..63) kafizma = 8
                            if (page + 1 in 64..69) kafizma = 9
                            if (page + 1 in 70..76) kafizma = 10
                            if (page + 1 in 77..84) kafizma = 11
                            if (page + 1 in 85..90) kafizma = 12
                            if (page + 1 in 91..100) kafizma = 13
                            if (page + 1 in 101..104) kafizma = 14
                            if (page + 1 in 105..108) kafizma = 15
                            if (page + 1 in 109..117) kafizma = 16
                            if (page + 1 == 118) kafizma = 17
                            if (page + 1 in 119..133) kafizma = 18
                            if (page + 1 in 134..142) kafizma = 19
                            if (page + 1 in 143..151) kafizma = 20
                            subTitle = context.getString(R.string.kafizma2, kafizma)
                        }
                    }
                }
            }
            Column {
                if (!fullscreen && biblia == Settings.CHYTANNI_BIBLIA && listState.size - 1 != 0) {
                    LazyRow(
                        state = lazyRowState
                    ) {
                        items(listState.size) { page ->
                            val color = if (selectedIndex == page) BezPosta
                            else Color.Unspecified
                            val textColor = if (selectedIndex == page) PrimaryText
                            else MaterialTheme.colorScheme.secondary
                            Text(
                                (page + 1).toString(), modifier = Modifier
                                    .clickable {
                                        selectedIndex = page
                                    }
                                    .padding(10.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .border(
                                        width = 1.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(color)
                                    .padding(5.dp), color = textColor, fontSize = Settings.fontInterface.sp)
                        }
                    }
                }
                var isScrollRun by remember { mutableStateOf(false) }
                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset, source: NestedScrollSource
                        ): Offset {
                            isScrollRun = true
                            return super.onPreScroll(available, source)
                        }

                        override suspend fun onPostFling(
                            consumed: Velocity, available: Velocity
                        ): Velocity {
                            isScrollRun = false
                            if (autoScrollSensor) autoScroll = true
                            return super.onPostFling(consumed, available)
                        }
                    }
                }
                if (biblia == Settings.CHYTANNI_BIBLIA && positionRemember != -1) {
                    LaunchedEffect(positionRemember) {
                        coroutineScope.launch {
                            listState[selectedIndex].scrollToItem(positionRemember)
                            positionRemember = -1
                        }
                    }
                }
                if (biblia == Settings.CHYTANNI_LITURGICHNYIA && skipUtran && utranEndPosition > 0) {
                    LaunchedEffect(utranEndPosition) {
                        coroutineScope.launch {
                            listState[selectedIndex].scrollToItem(utranEndPosition)
                            positionRemember = -1
                            skipUtran = false
                        }
                    }
                }
                HorizontalPager(
                    pageSpacing = 10.dp, state = pagerState, flingBehavior = fling, verticalAlignment = Alignment.Top, userScrollEnabled = biblia == Settings.CHYTANNI_BIBLIA
                ) { page ->
                    val viewModel = CytanniListItems(biblia, page, cytanne, perevod)
                    val resultPage by viewModel.filteredItems.collectAsStateWithLifecycle()
                    if (biblia != Settings.CHYTANNI_BIBLIA && positionRemember != -1) {
                        var resultCount = 0
                        if (positionRemember != 0) {
                            var tit = ""
                            var cnt = 0
                            for (i in 0 until resultPage.size) {
                                if (tit.isNotEmpty() && resultPage[i].title != tit) {
                                    cnt++
                                    if (cnt == positionRemember) {
                                        resultCount = i
                                        break
                                    }
                                }
                                tit = resultPage[i].title
                            }
                        }
                        LaunchedEffect(resultCount) {
                            coroutineScope.launch {
                                listState[selectedIndex].scrollToItem(resultCount)
                            }
                        }
                        positionRemember = -1
                    }
                    if (resultPage.isEmpty()) {
                        resultPage.add(CytanniListData(0, subTitle, openAssetsResources(context, "biblia_error.txt")))
                        isPerevodError = true
                    } else {
                        isPerevodError = false
                    }
                    if (skipUtran && !isPerevodError) {
                        val tit = resultPage[0].title
                        for (i in 0 until resultPage.size) {
                            if (resultPage[i].title != tit) {
                                utranEndPosition = i
                                break
                            }
                        }
                    }
                    val selectState = remember(resultPage) { resultPage.map { false }.toMutableStateList() }
                    if (biblia == Settings.CHYTANNI_BIBLIA && !isPerevodError && perevodRoot != Settings.PEREVODNADSAN) {
                        subTitle = resultPage[0].title.substringBeforeLast(" ")
                    }
                    if (biblia != Settings.CHYTANNI_BIBLIA) {
                        LaunchedEffect(listState[page]) {
                            snapshotFlow { listState[page].firstVisibleItemIndex }.collect { index ->
                                if (subTitle != resultPage[index].title) subTitle = resultPage[index].title
                            }
                        }
                    }
                    if (isSelectAll) {
                        isSelectAll = false
                        if (biblia == Settings.CHYTANNI_BIBLIA) {
                            selectState.forEachIndexed { index, _ ->
                                selectState[index] = true
                            }
                        } else {
                            var findTitle = ""
                            resultPage.forEachIndexed { index, text ->
                                if (selectState[index]) {
                                    findTitle = text.title
                                    return@forEachIndexed
                                }
                            }
                            resultPage.forEachIndexed { index, text ->
                                if (findTitle == text.title) {
                                    selectState[index] = true
                                }
                            }
                        }
                    }
                    if (!isSelectMode) {
                        selectState.forEachIndexed { index, _ ->
                            selectState[index] = false
                        }
                    }
                    if (isCopyMode || isShareMode) {
                        val sb = StringBuilder()
                        resultPage.forEachIndexed { index, text ->
                            if (selectState[index]) {
                                sb.append(
                                    AnnotatedString.fromHtml(text.text).toString() + "\n"
                                )
                            }
                        }
                        if (isCopyMode) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "", sb.toString()
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, context.getString(R.string.copy), Toast.LENGTH_SHORT).show()
                        }
                        if (isShareMode) {
                            val sendIntent = Intent()
                            sendIntent.action = Intent.ACTION_SEND
                            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString())
                            sendIntent.type = "text/plain"
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }
                        isCopyMode = false
                        isShareMode = false
                        isSelectMode = false
                        if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) actyvity.setlightSensor()
                    }
                    LazyColumn(
                        Modifier
                            .pointerInput(PointerEventType.Press) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Press) {
                                            autoScroll = false
                                        }
                                        if (autoScrollSensor && event.type == PointerEventType.Release && !isScrollRun) {
                                            autoScroll = true
                                        }
                                    }
                                }
                            }
                            .nestedScroll(nestedScrollConnection), state = listState[page]) {
                        items(resultPage.size, key = { index -> resultPage[index].id }) { index ->
                            if (index == 0) {
                                Spacer(Modifier.padding(top = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp))
                                if (!(biblia == Settings.CHYTANNI_BIBLIA || biblia == Settings.CHYTANNI_VYBRANAE)) {
                                    val titlePerevod = when (perevod) {
                                        Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia2)
                                        Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal2)
                                        Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
                                        Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun2)
                                        Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski2)
                                        else -> stringResource(R.string.title_biblia2)
                                    }
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 10.dp, end = 10.dp, top = 10.dp), text = titlePerevod, fontSize = fontSize.sp, lineHeight = fontSize.sp * 1.15, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            HtmlText(
                                modifier = if (!autoScrollSensor && !showDropdown) {
                                    Modifier
                                        .fillMaxWidth()
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = {
                                                if (!isSelectMode && isParallel && resultPage[index].parallel != "+-+") {
                                                    isParallelVisable = true
                                                    paralelChtenia = resultPage[index].parallel
                                                }
                                                if (isSelectMode) {
                                                    selectState[index] = !selectState[index]
                                                }
                                            }, onLongPress = {
                                                if (!fullscreen) {
                                                    isSelectMode = true
                                                    actyvity.removelightSensor()
                                                    selectState[index] = !selectState[index]
                                                }
                                            }, onDoubleTap = {
                                                fullscreen = !fullscreen
                                            })
                                        }
                                } else {
                                    Modifier
                                        .fillMaxWidth()
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onDoubleTap = {
                                                    fullscreen = !fullscreen
                                                })
                                        }
                                }
                                    .padding(horizontal = 10.dp)
                                    .background(if (selectState[index]) Post else Color.Unspecified), text = resultPage[index].text, fontSize = fontSize.sp, color = if (selectState[index]) PrimaryText else MaterialTheme.colorScheme.secondary)
                            if (isParallel && resultPage[index].parallel != "+-+") {
                                Text(
                                    text = resultPage[index].parallel, modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp), fontSize = (Settings.fontInterface - 4).sp, lineHeight = (Settings.fontInterface - 4).sp * 1.15, color = SecondaryText
                                )
                            }
                        }
                        item {
                            Spacer(Modifier.padding(bottom = if (fullscreen) 10.dp else innerPadding.calculateBottomPadding() + 10.dp))
                            if (listState[page].lastScrolledForward && !listState[page].canScrollForward) {
                                autoScroll = false
                                autoScrollSensor = false
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                AnimatedVisibility(
                    autoScrollTextVisable, enter = fadeIn(
                        tween(
                            durationMillis = 700, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 10.dp, end = 10.dp)
                    ) {
                        Spacer(modifier = Modifier.padding(start = 50.dp))
                        Text(
                            text = autoScrollText, modifier = Modifier
                                .align(Alignment.Bottom)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(autoScrollTextColor)
                                .padding(5.dp)
                                .align(Alignment.CenterVertically), color = autoScrollTextColor2, fontSize = Settings.fontInterface.sp
                        )
                    }
                }
                AnimatedVisibility(
                    autoScrollSensor, enter = fadeIn(
                        tween(
                            durationMillis = 700, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 10.dp, end = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .align(Alignment.Bottom)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.minus_auto_scroll), contentDescription = "", modifier = Modifier
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(Button)
                                    .size(40.dp)
                                    .padding(5.dp)
                                    .clickable {
                                        if (autoScrollSpeed in 10..125) {
                                            autoScrollSpeed += 5
                                            val proc = 100 - (autoScrollSpeed - 15) * 100 / 115
                                            autoScrollTextColor = Post
                                            autoScrollTextColor2 = PrimaryText
                                            autoScrollText = "$proc%"
                                            autoScrollTextVisable = true
                                            autoScrollTextVisableJob?.cancel()
                                            autoScrollTextVisableJob = CoroutineScope(Dispatchers.Main).launch {
                                                delay(3000)
                                                autoScrollTextVisable = false
                                            }
                                            k.edit {
                                                putInt("autoscrollSpid", autoScrollSpeed)
                                            }
                                        }
                                    })
                        }
                        Image(
                            painter = painterResource(R.drawable.plus_auto_scroll), contentDescription = "", modifier = Modifier
                                .align(Alignment.Bottom)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Button)
                                .size(40.dp)
                                .padding(5.dp)
                                .clickable {
                                    if (autoScrollSpeed in 20..135) {
                                        autoScrollSpeed -= 5
                                        val proc = 100 - (autoScrollSpeed - 15) * 100 / 115
                                        autoScrollTextColor = Primary
                                        autoScrollTextColor2 = PrimaryTextBlack
                                        autoScrollText = "$proc%"
                                        autoScrollTextVisable = true
                                        autoScrollTextVisableJob?.cancel()
                                        autoScrollTextVisableJob = CoroutineScope(Dispatchers.Main).launch {
                                            delay(3000)
                                            autoScrollTextVisable = false
                                        }
                                        k.edit {
                                            putInt("autoscrollSpid", autoScrollSpeed)
                                        }
                                    }
                                })
                    }
                }
            }
        }
        if (isParallelVisable) {
            Column(
                modifier = Modifier
                    .padding(
                        innerPadding.calculateStartPadding(LayoutDirection.Ltr), 0.dp, innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
                    )
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                val resultParalel = getBible(paralelChtenia, perevod, biblia, true)
                Spacer(Modifier.padding(bottom = innerPadding.calculateTopPadding()))
                for (i in resultParalel.indices) {
                    HtmlText(
                        modifier = Modifier.padding(horizontal = 10.dp), text = resultParalel[i].text, fontSize = fontSize.sp
                    )
                }
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun DialogRazdzel(
    listState: ArrayList<LazyListState>, autoScrollSensor: Boolean, setSelectedIndex: (Int) -> Unit, setAutoScroll: (Boolean) -> Unit, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.data_search), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f), columns = GridCells.Adaptive(60.dp)
                ) {
                    items(listState.size) { item ->
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .border(
                                    width = 1.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp)
                                )
                                .background(Divider)
                                .clickable {
                                    setSelectedIndex(item)
                                    if (autoScrollSensor) setAutoScroll(true)
                                    onDismiss()
                                }) {
                            Text(
                                (item + 1).toString(), modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp), textAlign = TextAlign.Center, color = PrimaryText, fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Column {
                        TextButton(
                            onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                            Text(stringResource(R.string.cansel), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

fun getBible(
    cytanne: String, perevod: String, biblia: Int, isTitle: Boolean = false
): ArrayList<CytanniListData> {
    val context = MainActivity.applicationContext()
    val result = ArrayList<CytanniListData>()
    var id = 0
    try {
        val list = cytanne.split(";")
        var knigaText = ""
        var knigaStyxi = ""
        for (i in list.indices) {
            val itemList = list[i].trim()
            if (itemList != "") {
                val list2 = itemList.split(",")
                var glavaStart = 0
                var glavaEnd = 0
                var t7 = itemList.indexOf(" ")
                if (t7 != -1) {
                    val check = itemList.substring(t7 + 1, t7 + 2)
                    if (check.isNotEmpty() && !check.isDigitsOnly()) {
                        t7 = itemList.indexOf(" ", t7 + 1)
                    }
                    knigaStyxi = itemList.substring(t7 + 1)
                }
                for (e in list2.indices) {
                    val itemList2 = list2[e].trim()
                    var t1 = itemList2.indexOf(" ")
                    if (t1 != -1) {
                        val check = itemList2.substring(t1 + 1, t1 + 2)
                        if (check.isNotEmpty() && !check.isDigitsOnly()) {
                            t1 = itemList2.indexOf(" ", t1 + 1)
                        }
                        knigaText = itemList2.substring(0, t1)
                    } else {
                        if (list.size > 1) glavaStart = glavaEnd
                    }
                    var styxStart = 0
                    var styxEnd = 0
                    val t2 = itemList2.indexOf("-", t1 + 1)
                    var t3: Int
                    if (t2 == -1) {
                        val t4 = itemList2.indexOf(".", t1 + 1)
                        if (t4 != -1) {
                            glavaStart = itemList2.substring(t1 + 1, t4).toInt()
                            styxStart = itemList2.substring(t4 + 1).toInt()
                            styxEnd = styxStart
                        } else {
                            if (list2.size > 1) {
                                styxStart = itemList2.substring(t1 + 1).toInt()
                                styxEnd = styxStart
                            } else {
                                glavaStart = itemList2.substring(t1 + 1).toInt()
                            }
                        }
                        glavaEnd = glavaStart
                    } else {
                        val subItemList = itemList2.substring(t1 + 1, t2)
                        t3 = subItemList.indexOf(".")
                        if (t3 != -1) {
                            glavaStart = subItemList.substring(0, t3).toInt()
                            styxStart = subItemList.substring(t3 + 1).toInt()
                        } else {
                            if (list2.size > 1) {
                                styxStart = subItemList.toInt()
                            } else {
                                glavaStart = subItemList.toInt()
                            }
                        }
                        val subItemList2 = itemList2.substring(t2 + 1)
                        val t4 = subItemList2.indexOf(".")
                        if (t4 != -1) {
                            glavaEnd = subItemList2.substring(0, t4).toInt()
                            styxEnd = subItemList2.substring(t4 + 1).toInt()
                        } else {
                            if (t3 != -1) {
                                styxEnd = subItemList2.toInt()
                                glavaEnd = glavaStart
                            } else {
                                if (list2.size > 1) {
                                    styxEnd = subItemList2.toInt()
                                } else {
                                    glavaEnd = subItemList2.toInt()
                                }
                            }
                        }
                    }
                    var run = true
                    for (glava in glavaStart..glavaEnd) {
                        var perevodNew = perevod
                        val knigiBiblii = knigaBiblii(knigaText)
                        var kniga = getRealBook(knigiBiblii, perevodNew)
                        if (biblia != Settings.CHYTANNI_BIBLIA) {
                            if (kniga == -1) {
                                perevodNew = Settings.PEREVODCARNIAUSKI
                                kniga = getRealBook(knigiBiblii, perevodNew)
                            }
                            if (biblia != Settings.CHYTANNI_LITURGICHNYIA && kniga == -1) {
                                perevodNew = Settings.PEREVODSINOIDAL
                                kniga = getRealBook(knigiBiblii, perevodNew)
                            }
                            if (biblia != Settings.CHYTANNI_LITURGICHNYIA && (knigiBiblii == 21 && glava == 151 && (perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODBOKUNA))) {
                                perevodNew = Settings.PEREVODSINOIDAL
                                kniga = getRealBook(knigiBiblii, perevodNew)
                            }
                            if ((knigiBiblii == 33 && (glavaEnd == 13 || glavaEnd == 14)) && (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA)) {
                                perevodNew = Settings.PEREVODCARNIAUSKI
                                kniga = getRealBook(knigiBiblii, perevodNew)
                            }
                            if (biblia != Settings.CHYTANNI_LITURGICHNYIA && (knigiBiblii == 13 && glava == 37 && perevod != Settings.PEREVODSINOIDAL)) {
                                perevodNew = Settings.PEREVODSINOIDAL
                                kniga = getRealBook(knigiBiblii, perevodNew)
                            }
                        }
                        try {
                            val textBible = if (styxStart == 0 && styxEnd == 0) {
                                biblia(context, knigiBiblii, glava, glava, styxStart, styxEnd, perevodNew)
                            } else {
                                biblia(context, knigiBiblii, glavaStart, glavaEnd, styxStart, styxEnd, perevodNew)
                            }
                            if (run) {
                                if (!(styxStart == 0 && styxEnd == 0)) run = false
                                for (w in textBible.indices) {
                                    var t5 = textBible[w].styx.indexOf("<br>")
                                    if (t5 == -1) t5 = 0
                                    else t5 += 4
                                    val t6 = textBible[w].styx.indexOf(" ", t5)
                                    val isInt = if (t6 != -1) {
                                        val item = textBible[w].styx.substring(t5, t6)
                                        item.isNotEmpty() && item.isDigitsOnly()
                                    } else false
                                    if (w == 0) {
                                        if (e > 0) {
                                            result.add(
                                                CytanniListData(
                                                    id, "${
                                                        getNameBook(
                                                            context, kniga, perevodNew, knigiBiblii >= 50
                                                        )
                                                    } ${textBible[w].glava}", "[&#8230;]"
                                                )
                                            )
                                        } else {
                                            if (isTitle) {
                                                result.add(
                                                    CytanniListData(
                                                        id, "${
                                                            getNameBook(
                                                                context, kniga, perevodNew, knigiBiblii >= 50
                                                            )
                                                        } ${textBible[w].glava}", if (biblia == Settings.CHYTANNI_LITURGICHNYIA) {
                                                            val eGlavy = knigaStyxi.ifEmpty { glava.toString() }
                                                            "<strong><br>" + getNameBook(
                                                                context, kniga, perevodNew, knigiBiblii >= 50
                                                            ) + " $eGlavy<strong><br>"
                                                        } else {
                                                            "<strong><br>" + getNameBook(
                                                                context, kniga, perevodNew, knigiBiblii >= 50
                                                            ) + " $glava<strong><br>"
                                                        }))
                                            }
                                        }
                                        id++
                                    }
                                    var text = textBible[w].styx
                                    if (isInt) {
                                        val styx = textBible[w].styx.substring(t5, t6).toInt()
                                        text = textBible[w].styx.substring(
                                            0, t5
                                        ) + "<font color=#D00505>$styx. </font>" + textBible[w].styx.substring(t6)
                                    }
                                    result.add(
                                        CytanniListData(
                                            id, "${
                                                getNameBook(
                                                    context, kniga, perevodNew, knigiBiblii >= 50
                                                )
                                            } ${textBible[w].glava}", text, textBible[w].paralelStyx
                                        )
                                    )
                                    id++
                                }
                            }
                        } catch (_: Throwable) {
                            result.add(CytanniListData(id, title = "", text = openAssetsResources(context, "biblia_error.txt")))
                            id++
                        }
                    }
                }
            }
        }
    } catch (_: Throwable) {
        result.add(CytanniListData(id, title = "", text = openAssetsResources(context, "biblia_error.txt")))
        id++
    }
    return result
}

fun knigaBiblii(kniga: String): Int {
    var bible = 0
    if (kniga == "Быц") bible = 0
    if (kniga == "Вых") bible = 1
    if (kniga == "Ляв") bible = 2
    if (kniga == "Лікі") bible = 3
    if (kniga == "Дрг") bible = 4
    if (kniga == "Нав") bible = 5
    if (kniga == "Суд") bible = 6
    if (kniga == "Рут") bible = 7
    if (kniga == "1 Цар") bible = 8
    if (kniga == "2 Цар") bible = 9
    if (kniga == "3 Цар") bible = 10
    if (kniga == "4 Цар") bible = 11
    if (kniga == "1 Лет") bible = 12
    if (kniga == "2 Лет") bible = 13
    if (kniga == "1 Эзд") bible = 14
    if (kniga == "Нээм") bible = 15
    if (kniga == "2 Эзд") bible = 16
    if (kniga == "Тав") bible = 17
    if (kniga == "Юдт") bible = 18
    if (kniga == "Эст") bible = 19
    if (kniga == "Ёва") bible = 20
    if (kniga == "Пс") bible = 21
    if (kniga == "Высл" || kniga == "Прып") bible = 22
    if (kniga == "Экл") bible = 23
    if (kniga == "Псн") bible = 24
    if (kniga == "Мдр") bible = 25
    if (kniga == "Сір") bible = 26
    if (kniga == "Іс") bible = 27
    if (kniga == "Ер" || kniga == "Ярэм") bible = 28
    if (kniga == "Плач") bible = 29
    if (kniga == "Пасл Ер") bible = 30
    if (kniga == "Бар") bible = 31
    if (kniga == "Езк") bible = 32
    if (kniga == "Дан") bible = 33
    if (kniga == "Ас") bible = 34
    if (kniga == "Ёіл") bible = 35
    if (kniga == "Ам") bible = 36
    if (kniga == "Аўдз") bible = 37
    if (kniga == "Ёны") bible = 38
    if (kniga == "Міх") bible = 39
    if (kniga == "Нвм") bible = 40
    if (kniga == "Абк") bible = 41
    if (kniga == "Саф") bible = 42
    if (kniga == "Аг") bible = 43
    if (kniga == "Зах") bible = 44
    if (kniga == "Мал") bible = 45
    if (kniga == "1 Мак") bible = 46
    if (kniga == "2 Мак") bible = 47
    if (kniga == "3 Мак") bible = 48
    if (kniga == "3 Эзд") bible = 49
    if (kniga == "Мц") bible = 50
    if (kniga == "Мк") bible = 51
    if (kniga == "Лк") bible = 52
    if (kniga == "Ян") bible = 53
    if (kniga == "Дз") bible = 54
    if (kniga == "Як") bible = 55
    if (kniga == "1 Пт") bible = 56
    if (kniga == "2 Пт") bible = 57
    if (kniga == "1 Ян") bible = 58
    if (kniga == "2 Ян") bible = 59
    if (kniga == "3 Ян") bible = 60
    if (kniga == "Юд") bible = 61
    if (kniga == "Рым") bible = 62
    if (kniga == "1 Кар") bible = 63
    if (kniga == "2 Кар") bible = 64
    if (kniga == "Гал") bible = 65
    if (kniga == "Эф") bible = 66
    if (kniga == "Плп") bible = 67
    if (kniga == "Клс") bible = 68
    if (kniga == "1 Фес") bible = 69
    if (kniga == "2 Фес") bible = 70
    if (kniga == "1 Цім") bible = 71
    if (kniga == "2 Цім") bible = 72
    if (kniga == "Ціт") bible = 73
    if (kniga == "Флм") bible = 74
    if (kniga == "Гбр") bible = 75
    if (kniga == "Адкр") bible = 76
    return bible
}

fun translateToBelarus(paralelString: String): String {
    var paralel = paralelString
    paralel = paralel.replace("Ёва", "Ёў")
    paralel = paralel.replace("Флп", "Плп")
    paralel = paralel.replace("Кал", "Клс")
    paralel = paralel.replace("Езэк", "Езк")
    paralel = paralel.replace("1 Сал", "1 Фес")
    paralel = paralel.replace("2 Сал", "2 Фес")
    paralel = paralel.replace("Яэль", "Ёіл")
    paralel = paralel.replace("Габ", "Гбр")
    paralel = paralel.replace("Муд", "Мдр")
    paralel = paralel.replace("Друг", "Дрг")
    paralel = paralel.replace("Быт", "Быц")
    paralel = paralel.replace("Исх", "Вых")
    paralel = paralel.replace("Лев", "Ляв")
    paralel = paralel.replace("Чис", "Лікі")
    paralel = paralel.replace("Втор", "Дрг")
    paralel = paralel.replace("Руфь", "Рут")
    paralel = paralel.replace("1 Пар", "1 Лет")
    paralel = paralel.replace("2 Пар", "2 Лет")
    paralel = paralel.replace("1 Езд", "1 Эзд")
    paralel = paralel.replace("Неем", "Нээм")
    paralel = paralel.replace("2 Езд", "2 Эзд")
    paralel = paralel.replace("Тов", "Тав")
    paralel = paralel.replace("Иудифь", "Юдт")
    paralel = paralel.replace("Есф", "Эст")
    paralel = paralel.replace("Иов", "Ёва")
    paralel = paralel.replace("Притч", "Высл")
    paralel = paralel.replace("Еккл", "Экл")
    paralel = paralel.replace("Песн", "Псн")
    paralel = paralel.replace("Прем", "Мдр")
    paralel = paralel.replace("Сир", "Сір")
    paralel = paralel.replace("Ис", "Іс")
    paralel = paralel.replace("Посл Иер", "Пасл Ер")
    paralel = paralel.replace("Иер", "Ер")
    paralel = paralel.replace("Иез", "Езк")
    paralel = paralel.replace("Ос", "Ас")
    paralel = paralel.replace("Иоил", "Ёіл")
    paralel = paralel.replace("Авд", "Аўдз")
    paralel = paralel.replace("Иона", "Ёны")
    paralel = paralel.replace("Мих", "Міх")
    paralel = paralel.replace("Наум", "Нвм")
    paralel = paralel.replace("Авв", "Абк")
    paralel = paralel.replace("Соф", "Саф")
    paralel = paralel.replace("Агг", "Аг")
    paralel = paralel.replace("3 Езд", "3 Эзд")
    paralel = paralel.replace("Мф", "Мц")
    paralel = paralel.replace("Ин", "Ян")
    paralel = paralel.replace("Деян", "Дз")
    paralel = paralel.replace("Иак", "Як")
    paralel = paralel.replace("1 Пет", "1 Пт")
    paralel = paralel.replace("2 Пет", "2 Пт")
    paralel = paralel.replace("1 Ин", "1 Ян")
    paralel = paralel.replace("2 Ин", "2 Ян")
    paralel = paralel.replace("3 Ин", "3 Ян")
    paralel = paralel.replace("Иуд", "Юд")
    paralel = paralel.replace("Рим", "Рым")
    paralel = paralel.replace("1 Кор", "1 Кар")
    paralel = paralel.replace("2 Кор", "2 Кар")
    paralel = paralel.replace("Еф", "Эф")
    paralel = paralel.replace("Флп", "Плп")
    paralel = paralel.replace("Кол", "Клс")
    paralel = paralel.replace("1 Тим", "1 Цім")
    paralel = paralel.replace("2 Тим", "2 Цім")
    paralel = paralel.replace("Тит", "Ціт")
    paralel = paralel.replace("Евр", "Гбр")
    paralel = paralel.replace("Откр", "Адкр")
    return paralel
}

fun getParalel(kniga: Int, glava: Int, styx: Int, isPsaltyrGreek: Boolean): String {
    val parallel = BibliaParallelChtenia()
    var res = "+-+"
    if (kniga == 0) {
        res = parallel.kniga1(glava, styx)
    }
    if (kniga == 1) {
        res = parallel.kniga2(glava, styx)
    }
    if (kniga == 2) {
        res = parallel.kniga3(glava, styx)
    }
    if (kniga == 3) {
        res = parallel.kniga4(glava, styx)
    }
    if (kniga == 4) {
        res = parallel.kniga5(glava, styx)
    }
    if (kniga == 5) {
        res = parallel.kniga6(glava, styx)
    }
    if (kniga == 6) {
        res = parallel.kniga7(glava, styx)
    }
    if (kniga == 7) {
        res = parallel.kniga8(glava, styx)
    }
    if (kniga == 8) {
        res = parallel.kniga9(glava, styx)
    }
    if (kniga == 9) {
        res = parallel.kniga10(glava, styx)
    }
    if (kniga == 10) {
        res = parallel.kniga11(glava, styx)
    }
    if (kniga == 11) {
        res = parallel.kniga12(glava, styx)
    }
    if (kniga == 12) {
        res = parallel.kniga13(glava, styx)
    }
    if (kniga == 13) {
        res = parallel.kniga14(glava, styx)
    }
    if (kniga == 14) {
        res = parallel.kniga15(glava, styx)
    }
    if (kniga == 15) {
        res = parallel.kniga16(glava, styx)
    }
    if (kniga == 16) {
        res = parallel.kniga17(glava, styx)
    }
    if (kniga == 17) {
        res = parallel.kniga18(glava, styx)
    }
    if (kniga == 18) {
        res = parallel.kniga19(glava, styx)
    }
    if (kniga == 19) {
        res = parallel.kniga20(glava, styx)
    }
    if (kniga == 20) {
        res = parallel.kniga21(glava, styx)
    }
    if (kniga == 21) {
        res = if (isPsaltyrGreek) parallel.kniga22(glava, styx)
        else parallel.kniga22Masoretskaya(glava, styx)
    }
    if (kniga == 22) {
        res = parallel.kniga23(glava, styx)
    }
    if (kniga == 23) {
        res = parallel.kniga24(glava, styx)
    }
    if (kniga == 24) {
        res = parallel.kniga25(glava, styx)
    }
    if (kniga == 25) {
        res = parallel.kniga26(glava, styx)
    }
    if (kniga == 26) {
        res = parallel.kniga27(glava, styx)
    }
    if (kniga == 27) {
        res = parallel.kniga28(glava, styx)
    }
    if (kniga == 28) {
        res = parallel.kniga29(glava, styx)
    }
    if (kniga == 29) {
        res = parallel.kniga30(glava, styx)
    }
    if (kniga == 30) {
        res = parallel.kniga31(glava, styx)
    }
    if (kniga == 31) {
        res = parallel.kniga32(glava, styx)
    }
    if (kniga == 32) {
        res = parallel.kniga33(glava, styx)
    }
    if (kniga == 33) {
        res = parallel.kniga34(glava, styx)
    }
    if (kniga == 34) {
        res = parallel.kniga35(glava, styx)
    }
    if (kniga == 35) {
        res = parallel.kniga36(glava, styx)
    }
    if (kniga == 36) {
        res = parallel.kniga37(glava, styx)
    }
    if (kniga == 37) {
        res = parallel.kniga38(glava, styx)
    }
    if (kniga == 38) {
        res = parallel.kniga39(glava, styx)
    }
    if (kniga == 39) {
        res = parallel.kniga40(glava, styx)
    }
    if (kniga == 40) {
        res = parallel.kniga41(glava, styx)
    }
    if (kniga == 41) {
        res = parallel.kniga42(glava, styx)
    }
    if (kniga == 42) {
        res = parallel.kniga43(glava, styx)
    }
    if (kniga == 43) {
        res = parallel.kniga44(glava, styx)
    }
    if (kniga == 44) {
        res = parallel.kniga45(glava, styx)
    }
    if (kniga == 45) {
        res = parallel.kniga46(glava, styx)
    }
    if (kniga == 46) {
        res = parallel.kniga47(glava, styx)
    }
    if (kniga == 47) {
        res = parallel.kniga48(glava, styx)
    }
    if (kniga == 48) {
        res = parallel.kniga49(glava, styx)
    }
    if (kniga == 49) {
        res = parallel.kniga50(glava, styx)
    }
    if (kniga == 50) {
        res = parallel.kniga51(glava, styx)
    }
    if (kniga == 51) {
        res = parallel.kniga52(glava, styx)
    }
    if (kniga == 52) {
        res = parallel.kniga53(glava, styx)
    }
    if (kniga == 53) {
        res = parallel.kniga54(glava, styx)
    }
    if (kniga == 54) {
        res = parallel.kniga55(glava, styx)
    }
    if (kniga == 55) {
        res = parallel.kniga56(glava, styx)
    }
    if (kniga == 56) {
        res = parallel.kniga57(glava, styx)
    }
    if (kniga == 57) {
        res = parallel.kniga58(glava, styx)
    }
    if (kniga == 58) {
        res = parallel.kniga59(glava, styx)
    }
    if (kniga == 59) {
        res = parallel.kniga60(glava, styx)
    }
    if (kniga == 60) {
        res = parallel.kniga61(glava, styx)
    }
    if (kniga == 61) {
        res = parallel.kniga62(glava, styx)
    }
    if (kniga == 62) {
        res = parallel.kniga63(glava, styx)
    }
    if (kniga == 63) {
        res = parallel.kniga64(glava, styx)
    }
    if (kniga == 64) {
        res = parallel.kniga65(glava, styx)
    }
    if (kniga == 65) {
        res = parallel.kniga66(glava, styx)
    }
    if (kniga == 66) {
        res = parallel.kniga67(glava, styx)
    }
    if (kniga == 67) {
        res = parallel.kniga68(glava, styx)
    }
    if (kniga == 68) {
        res = parallel.kniga69(glava, styx)
    }
    if (kniga == 69) {
        res = parallel.kniga70(glava, styx)
    }
    if (kniga == 70) {
        res = parallel.kniga71(glava, styx)
    }
    if (kniga == 71) {
        res = parallel.kniga72(glava, styx)
    }
    if (kniga == 72) {
        res = parallel.kniga73(glava, styx)
    }
    if (kniga == 73) {
        res = parallel.kniga74(glava, styx)
    }
    if (kniga == 74) {
        res = parallel.kniga75(glava, styx)
    }
    if (kniga == 75) {
        res = parallel.kniga76(glava, styx)
    }
    if (kniga == 76) {
        res = parallel.kniga77(glava, styx)
    }
    return translateToBelarus(res)
}

data class CytanniListItemData(val page: Int, val item: ArrayList<CytanniListData>)

data class CytanniListData(
    val id: Int, val title: String, val text: String = "", val parallel: String = "+-+"
)

data class VybranaeData(
    val id: Long, val title: String, val knigaText: String, val glava: Int, val perevod: String
)