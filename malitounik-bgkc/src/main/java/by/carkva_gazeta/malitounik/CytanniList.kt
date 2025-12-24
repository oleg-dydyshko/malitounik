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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import by.carkva_gazeta.malitounik.views.AppDropdownMenu
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.PlainTooltip
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

open class CytanniListViewModel : ViewModel() {
    val selectState = mutableStateListOf<Boolean>()
    val listState = mutableStateListOf<CytanniListItemData>()
    var selectedIndex by mutableIntStateOf(-1)
    var knigaText by mutableStateOf("")
    val vybranoeList = mutableStateListOf<VybranaeData>()
    var isVybranoe by mutableStateOf(false)
    var perevodName by mutableStateOf("")
    var dialogDownLoad by mutableStateOf(false)
    var setPerevod by mutableStateOf(Settings.PEREVODSINOIDAL)
    var autoScroll by mutableStateOf(false)
    var autoScrollSensor by mutableStateOf(false)
    var autoScrollSpeed by mutableIntStateOf(60)
    var autoScrollTextVisable by mutableStateOf(false)
    var fireBaseVersionUpdate by mutableStateOf(false)
    var fireBaseVersion = 1
    private var autoScrollJob: Job? = null
    private var autoScrollTextVisableJob: Job? = null
    private var isFirstDialodVisable = true
    private var newCytanne = ""
    private val gson = Gson()
    private val type = TypeToken.getParameterized(ArrayList::class.java, VybranaeData::class.java).type

    fun initViewModel(context: Context, biblia: Int, cytanne: String, perevod: String) {
        if (perevod == Settings.PEREVODCATOLIK || perevod == Settings.PEREVODSINOIDAL || perevod == Settings.PEREVODNEWAMERICANBIBLE) {
            checkVersionBible(context, perevod)
        }
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (isFirstDialodVisable) {
            var isBibleEnable = k.getBoolean("catolik_bible", false)
            var dir = File("${context.filesDir}/Catolik")
            if (isBibleEnable && perevod == Settings.PEREVODCATOLIK && !dir.exists()) {
                dialogDownLoad = true
                setPerevod = Settings.PEREVODCATOLIK
            }
            isBibleEnable = k.getBoolean("sinoidal_bible", false)
            dir = File("${context.filesDir}/Sinodal")
            if (isBibleEnable && perevod == Settings.PEREVODSINOIDAL && !dir.exists()) {
                dialogDownLoad = true
                setPerevod = Settings.PEREVODSINOIDAL
            }
            isBibleEnable = k.getBoolean("newkingjames_translate", false)
            dir = File("${context.filesDir}/NewAmericanBible")
            if (isBibleEnable && perevod == Settings.PEREVODNEWAMERICANBIBLE && !dir.exists()) {
                dialogDownLoad = true
                setPerevod = Settings.PEREVODNEWAMERICANBIBLE
            }
            isFirstDialodVisable = false
        }
        if (listState.isEmpty()) {
            autoScrollSpeed = k.getInt("autoscrollSpid", 60)
            if (newCytanne != cytanne) newCytanne = cytanne
            perevodName = when (perevod) {
                Settings.PEREVODSEMUXI -> "biblia"
                Settings.PEREVODBOKUNA -> "bokuna"
                Settings.PEREVODCARNIAUSKI -> "carniauski"
                Settings.PEREVODCATOLIK -> "catolik"
                Settings.PEREVODNADSAN -> "nadsan"
                Settings.PEREVODSINOIDAL -> "sinaidal"
                Settings.PEREVODNEWAMERICANBIBLE -> "english"
                else -> "biblia"
            }
            initVybranoe(context)
            val initPage = if (biblia == Settings.CHYTANNI_BIBLIA) {
                if (Settings.bibleTimeList) k.getInt("bible_time_${perevodName}_glava", 0)
                else newCytanne.substringAfterLast(" ").toInt() - 1
            } else 0
            if (selectedIndex == -1) selectedIndex = if (biblia == Settings.CHYTANNI_BIBLIA) initPage else 0
            val t1 = newCytanne.indexOf(";")
            if (knigaText.isEmpty()) {
                knigaText = if (biblia == Settings.CHYTANNI_BIBLIA || biblia == Settings.CHYTANNI_VYBRANAE) {
                    if (t1 == -1) newCytanne.substringBeforeLast(" ")
                    else {
                        val sb = newCytanne.take(t1)
                        sb.substringBeforeLast(" ")
                    }
                } else newCytanne
            }
            if (perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODNEWAMERICANBIBLE) {
                if (knigaBiblii(knigaText) == 21) {
                    if (selectedIndex > 149) selectedIndex = 149
                }
            }
            if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA) {
                if (knigaBiblii(knigaText) == 33) {
                    if (selectedIndex > 11) selectedIndex = 11
                }
                if (knigaBiblii(knigaText) == 31) {
                    if (selectedIndex > 4) selectedIndex = 4
                }
            }
            val count = if (biblia == Settings.CHYTANNI_BIBLIA) bibleCount(knigaBiblii(knigaText), perevod)
            else 1
            (0 until count).forEach { _ ->
                listState.add(CytanniListItemData(SnapshotStateList(), LazyListState()))
            }
        }
    }

    fun updatePage(biblia: Int, page: Int, perevod: String) {
        if (listState[page].item.isEmpty()) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val chteniaNewPage = knigaText + " ${page + 1}"
                    val resultPage = if (biblia == Settings.CHYTANNI_BIBLIA) {
                        getBible(chteniaNewPage, perevod, biblia)
                    } else {
                        getBible(newCytanne, perevod, biblia, true)
                    }
                    if (listState[page].item.isEmpty()) {
                        listState[page].item.addAll(resultPage)
                    }
                }
            }
        }
    }

    fun setPerevod(context: Context, biblia: Int, cytanne: String, perevod: String) {
        selectedIndex = -1
        knigaText = ""
        listState.clear()
        initViewModel(context, biblia, cytanne, perevod)
    }

    fun setPerevodBible(context: Context, biblia: Int, cytanne: String, perevod: String, oldPerevod: String) {
        listState.clear()
        if (biblia == Settings.CHYTANNI_BIBLIA) {
            if (oldPerevod == Settings.PEREVODSEMUXI || oldPerevod == Settings.PEREVODNADSAN || oldPerevod == Settings.PEREVODSINOIDAL) {
                if (perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODNEWAMERICANBIBLE) {
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
            if (oldPerevod == Settings.PEREVODCARNIAUSKI || oldPerevod == Settings.PEREVODBOKUNA || oldPerevod == Settings.PEREVODNEWAMERICANBIBLE) {
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
                if (knigaBiblii(knigaText) == 31) {
                    if (selectedIndex == 5) {
                        selectedIndex = 0
                        knigaText = "Пасл Ер"
                        newCytanne = "Пасл Ер 1"
                    }
                }
            }
            if (oldPerevod == Settings.PEREVODSINOIDAL) {
                if (perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODNEWAMERICANBIBLE) {
                    if (knigaBiblii(knigaText) == 30) {
                        selectedIndex = 5
                        knigaText = "Вар"
                        newCytanne = "Вар 6"
                    }
                }
            }
        }
        initViewModel(context, biblia, cytanne, perevod)
    }

    fun initVybranoe(context: Context) {
        val file = File("${context.filesDir}/vybranoe_${perevodName}.json")
        vybranoeList.clear()
        if (file.exists()) {
            vybranoeList.addAll(gson.fromJson(file.readText(), type))
        }
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

    fun saveVybranoe(context: Context, perevod: String) {
        val file = File("${context.filesDir}/vybranoe_${perevodName}.json")
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
            val bibleCount = bibleCount(context, perevod, kniga >= 50)
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
    }

    fun autoScroll(title: String, isPlay: Boolean) {
        if (isPlay) {
            if (autoScrollJob?.isActive != true) {
                autoScrollJob = viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        while (true) {
                            delay(autoScrollSpeed.toLong())
                            listState[selectedIndex].lazyListState.scrollBy(2f)
                            AppNavGraphState.setScrollValuePosition(title, listState[selectedIndex].lazyListState.firstVisibleItemIndex, listState[selectedIndex].lazyListState.firstVisibleItemScrollOffset)
                        }
                    }
                }
            }
        } else {
            autoScrollJob?.cancel()
        }
        autoScroll = autoScrollJob?.isActive == true
    }

    fun autoScrollSpeed(context: Context) {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        autoScrollTextVisable = true
        autoScrollTextVisableJob?.cancel()
        autoScrollTextVisableJob = viewModelScope.launch {
            delay(3000)
            autoScrollTextVisable = false
        }
        k.edit {
            putInt("autoscrollSpid", autoScrollSpeed)
        }
    }

    suspend fun downLoadBibile(context: Context, count: Int = 0) {
        var error = false
        try {
            val file = File("${context.filesDir}/cache/cache.zip")
            when (setPerevod) {
                Settings.PEREVODCATOLIK -> Malitounik.referens.child("/chytanne/Catolik/catolik.zip").getFile(file).addOnFailureListener {
                    error = true
                }.await()

                Settings.PEREVODSINOIDAL -> Malitounik.referens.child("/chytanne/Sinodal/sinaidal.zip").getFile(file).addOnFailureListener {
                    error = true
                }.await()

                Settings.PEREVODNEWAMERICANBIBLE -> Malitounik.referens.child("/chytanne/NewAmericanBible/NewAmericanBible.zip").getFile(file).addOnFailureListener {
                    error = true
                }.await()
            }
        } catch (_: Throwable) {
            error = true
        }
        if (error && count < 3) {
            downLoadBibile(context, count + 1)
        }
    }

    fun checkVersionBible(context: Context, perevod: String) {
        try {
            viewModelScope.launch {
                fireBaseVersion = downLoadVersionBibile(context, perevod)
                val localFileVirsion = when (perevod) {
                    Settings.PEREVODCATOLIK -> File("${context.filesDir}/catolikVersion.txt")

                    Settings.PEREVODSINOIDAL -> File("${context.filesDir}/sinaidalVersion.txt")

                    Settings.PEREVODNEWAMERICANBIBLE -> File("${context.filesDir}/NewAmericanBibleVersion.txt")

                    else -> File("${context.filesDir}/sinaidalVersion.txt")
                }
                val localVersion = if (localFileVirsion.exists()) {
                    localFileVirsion.readText().trim().toInt()
                } else 1
                if (fireBaseVersion > localVersion) {
                    val destinationDir = when (perevod) {
                        Settings.PEREVODCATOLIK -> File("${context.filesDir}/Catolik")

                        Settings.PEREVODSINOIDAL -> File("${context.filesDir}/Sinodal")

                        Settings.PEREVODNEWAMERICANBIBLE -> File("${context.filesDir}/NewAmericanBible")

                        else -> File("${context.filesDir}/Sinodal")
                    }
                    when (perevod) {
                        Settings.PEREVODCATOLIK -> {
                            fireBaseVersionUpdate = destinationDir.exists()
                            setPerevod = Settings.PEREVODCATOLIK
                            dialogDownLoad = true
                        }

                        Settings.PEREVODSINOIDAL -> {
                            fireBaseVersionUpdate = destinationDir.exists()
                            setPerevod = Settings.PEREVODSINOIDAL
                            dialogDownLoad = true
                        }

                        Settings.PEREVODNEWAMERICANBIBLE -> {
                            fireBaseVersionUpdate = destinationDir.exists()
                            setPerevod = Settings.PEREVODNEWAMERICANBIBLE
                            dialogDownLoad = true
                        }
                    }
                }
            }
        } catch (_: Throwable) {
        }
    }

    suspend fun downLoadVersionBibile(context: Context, perevod: String, count: Int = 0): Int {
        var error = false
        var result = 1
        try {
            val file = File("${context.filesDir}/cache/cache1.txt")
            when (perevod) {
                Settings.PEREVODCATOLIK -> Malitounik.referens.child("/chytanne/Catolik/catolikVersion.txt").getFile(file).addOnCompleteListener {
                    if (it.isSuccessful) result = file.readText().trim().toInt()
                    else error = true
                }.await()

                Settings.PEREVODSINOIDAL -> Malitounik.referens.child("/chytanne/Sinodal/sinaidalVersion.txt").getFile(file).addOnCompleteListener {
                    if (it.isSuccessful) result = file.readText().trim().toInt()
                    else error = true
                }.await()

                Settings.PEREVODNEWAMERICANBIBLE -> Malitounik.referens.child("/chytanne/NewAmericanBible/NewAmericanBibleVersion.txt").getFile(file).addOnCompleteListener {
                    if (it.isSuccessful) result = file.readText().trim().toInt()
                    else error = true
                }.await()
            }
        } catch (_: Throwable) {
            error = true
        }
        if (error && count < 3) {
            downLoadVersionBibile(context, perevod, count + 1)
        }
        return result
    }

    fun saveVersionFile(context: Context) {
        if (fireBaseVersion > 1) {
            when (setPerevod) {
                Settings.PEREVODCATOLIK -> {
                    val file = File("${context.filesDir}/catolikVersion.txt")
                    file.writer().use {
                        it.write(fireBaseVersion.toString())
                    }
                }

                Settings.PEREVODSINOIDAL -> {
                    val file = File("${context.filesDir}/sinaidalVersion.txt")
                    file.writer().use {
                        it.write(fireBaseVersion.toString())
                    }
                }

                Settings.PEREVODNEWAMERICANBIBLE -> {
                    val file = File("${context.filesDir}/NewAmericanBibleVersion.txt")
                    file.writer().use {
                        it.write(fireBaseVersion.toString())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CytanniList(
    navController: NavHostController, title: String, cytanne: String, biblia: Int, perevodRoot: String, position: Int, viewModel: SearchBibleViewModel
) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var perevod by remember {
        mutableStateOf(
            when (biblia) {
                Settings.CHYTANNI_LITURGICHNYIA -> k.getString("perevod", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI

                Settings.CHYTANNI_MARANATA -> k.getString("perevodMaranata", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI

                else -> perevodRoot
            }
        )
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initViewModel(context, biblia, cytanne, perevod)
    }
    var skipUtran by remember { mutableStateOf(position == -2) }
    var positionRemember by rememberSaveable { mutableIntStateOf(position) }
    var utranEndPosition by remember { mutableIntStateOf(0) }
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
    var showDropdown by remember { mutableStateOf(false) }
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
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
    val titleBible = title.ifEmpty {
        when (perevod) {
            Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia)
            Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun)
            Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski)
            Settings.PEREVODCATOLIK -> stringResource(R.string.title_biblia_catolik)
            Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
            Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal)
            Settings.PEREVODNEWAMERICANBIBLE -> stringResource(R.string.perevod_new_american_bible)
            else -> stringResource(R.string.title_biblia)
        }
    }
    val initPage = if (biblia == Settings.CHYTANNI_BIBLIA) {
        if (Settings.bibleTimeList) k.getInt("bible_time_${viewModel.perevodName}_glava", 0)
        else cytanne.substringAfterLast(" ").toInt() - 1
    } else 0
    val pagerState = rememberPagerState(pageCount = {
        viewModel.listState.size
    }, initialPage = initPage)
    val lazyRowState = rememberLazyListState()
    val fling = PagerDefaults.flingBehavior(
        state = pagerState, pagerSnapDistance = PagerSnapDistance.atMost(1)
    )
    var isBottomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
    if (viewModel.listState[viewModel.selectedIndex].item.isNotEmpty() && Settings.bibleTimeList) {
        Settings.bibleTimeList = false
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                viewModel.listState[viewModel.selectedIndex].lazyListState.scrollToItem(k.getInt("bible_time_${viewModel.perevodName}_stix", 0))
            }
        }
    }
    var isSelectMode by rememberSaveable { mutableStateOf(false) }
    var backPressHandled by remember { mutableStateOf(false) }
    val actyvity = LocalActivity.current as MainActivity
    if (viewModel.autoScrollSensor) {
        actyvity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    BackHandler(!backPressHandled || isSelectMode || isParallelVisable || showDropdown) {
        when {
            isSelectMode -> {
                isSelectMode = false
            }

            isParallelVisable -> isParallelVisable = false
            showDropdown -> {
                showDropdown = false
                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            }

            !backPressHandled -> {
                fullscreen = false
                val prefEditors = k.edit()
                if (biblia == Settings.CHYTANNI_BIBLIA) {
                    prefEditors.putString("bible_time_${viewModel.perevodName}_kniga", viewModel.knigaText)
                    prefEditors.putInt("bible_time_${viewModel.perevodName}_glava", viewModel.selectedIndex)
                    prefEditors.putInt(
                        "bible_time_${viewModel.perevodName}_stix", viewModel.listState[viewModel.selectedIndex].lazyListState.firstVisibleItemIndex
                    )
                }
                prefEditors.apply()
                backPressHandled = true
                if (!k.getBoolean("power", false)) actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                viewModel.autoScroll(title, false)
                viewModel.autoScrollSensor = false
                navController.popBackStack()
            }
        }
    }
    var isUpList by remember { mutableStateOf(false) }
    if (isUpList) {
        LaunchedEffect(Unit) {
            isUpList = false
            coroutineScope.launch {
                viewModel.listState[viewModel.selectedIndex].lazyListState.animateScrollToItem(0)
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
    val interactionSourse = remember { MutableInteractionSource() }
    if (dialogRazdel) {
        DialogRazdzel(viewModel.listState.size, viewModel.autoScrollSensor, setSelectedIndex = { viewModel.selectedIndex = it }, setAutoScroll = { viewModel.autoScroll(title, it) }) {
            dialogRazdel = false
        }
    }
    if (viewModel.dialogDownLoad) {
        DialogDownLoadBible(viewModel, onConfirmation = {
            perevod = viewModel.setPerevod
            k.edit {
                if (biblia == Settings.CHYTANNI_MARANATA) {
                    putString(
                        "perevodMaranata", perevod
                    )
                }
                apply()
            }
            if (viewModel.fireBaseVersionUpdate) {
                viewModel.saveVersionFile(context)
                viewModel.fireBaseVersionUpdate = false
            }
            viewModel.dialogDownLoad = false
            viewModel.setPerevod(context, biblia, cytanne, perevod)
        }) {
            viewModel.dialogDownLoad = false
        }
    }
    LaunchedEffect(Unit) {
        when (perevod) {
            Settings.PEREVODCATOLIK -> {
                val dir = File("${context.filesDir}/Catolik")
                if (!dir.exists()) {
                    viewModel.setPerevod = Settings.PEREVODCATOLIK
                    viewModel.dialogDownLoad = true
                }
            }

            Settings.PEREVODSINOIDAL -> {
                val dir = File("${context.filesDir}/Sinodal")
                if (!dir.exists()) {
                    viewModel.setPerevod = Settings.PEREVODSINOIDAL
                    viewModel.dialogDownLoad = true
                }
            }

            Settings.PEREVODNEWAMERICANBIBLE -> {
                val dir = File("${context.filesDir}/NewAmericanBible")
                if (!dir.exists()) {
                    viewModel.setPerevod = Settings.PEREVODNEWAMERICANBIBLE
                    viewModel.dialogDownLoad = true
                }
            }
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
                }, navigationIcon = {
                    if (isSelectMode || isParallelVisable) {
                        PlainTooltip(stringResource(R.string.close), TooltipAnchorPosition.Below) {
                            IconButton(onClick = {
                                if (isSelectMode) {
                                    viewModel.selectState.clear()
                                    isSelectMode = false
                                } else isParallelVisable = false
                            }, content = {
                                Icon(
                                    painter = painterResource(R.drawable.close), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                                )
                            })
                        }
                    } else {
                        PlainTooltip(stringResource(R.string.exit_page), TooltipAnchorPosition.Below) {
                            IconButton(onClick = {
                                when {
                                    isParallelVisable -> isParallelVisable = false
                                    showDropdown -> {
                                        showDropdown = false
                                        if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                    }

                                    else -> {
                                        if (!backPressHandled) {
                                            backPressHandled = true
                                            fullscreen = false
                                            k.edit {
                                                if (biblia == Settings.CHYTANNI_BIBLIA) {
                                                    putString("bible_time_${viewModel.perevodName}_kniga", viewModel.knigaText)
                                                    putInt("bible_time_${viewModel.perevodName}_glava", viewModel.selectedIndex)
                                                    putInt(
                                                        "bible_time_${viewModel.perevodName}_stix", viewModel.listState[viewModel.selectedIndex].lazyListState.firstVisibleItemIndex
                                                    )
                                                }
                                            }
                                            if (!k.getBoolean("power", false)) actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                            viewModel.autoScroll(title, false)
                                            viewModel.autoScrollSensor = false
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
                    }
                }, actions = {
                    if (isSelectMode) {
                        PlainTooltip(stringResource(R.string.select_all), TooltipAnchorPosition.Below) {
                            IconButton(onClick = {
                                isSelectAll = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.select_all), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        PlainTooltip(stringResource(R.string.copy_list), TooltipAnchorPosition.Below) {
                            IconButton(onClick = {
                                isCopyMode = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.content_copy), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        PlainTooltip(stringResource(R.string.share), TooltipAnchorPosition.Below) {
                            IconButton(onClick = {
                                isShareMode = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.share), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    } else {
                        if (!isBottomBar) {
                            var expandedUp by remember { mutableStateOf(false) }
                            if (viewModel.listState[viewModel.selectedIndex].lazyListState.canScrollForward) {
                                val iconAutoScroll = if (viewModel.autoScrollSensor) painterResource(R.drawable.stop_circle)
                                else painterResource(R.drawable.play_circle)
                                PlainTooltip(stringResource(if (viewModel.autoScrollSensor) R.string.auto_stop else R.string.auto_play), TooltipAnchorPosition.Below) {
                                    IconButton(onClick = {
                                        viewModel.autoScrollSensor = !viewModel.autoScrollSensor
                                        viewModel.autoScroll(title, viewModel.autoScrollSensor)
                                        if (viewModel.autoScrollSensor) {
                                            actyvity.window.addFlags(
                                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                            )
                                        } else if (!k.getBoolean("power", false)) {
                                            actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                        }
                                    }) {
                                        Icon(
                                            iconAutoScroll, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            } else if (viewModel.listState[viewModel.selectedIndex].lazyListState.canScrollBackward) {
                                PlainTooltip(stringResource(R.string.auto_up), TooltipAnchorPosition.Below) {
                                    IconButton(onClick = {
                                        isUpList = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_upward), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                            if (biblia == Settings.CHYTANNI_BIBLIA) {
                                PlainTooltip(stringResource(if (viewModel.isVybranoe) R.string.vybranae_remove else R.string.vybranae_add), TooltipAnchorPosition.Below) {
                                    IconButton(
                                        onClick = {
                                            viewModel.saveVybranoe(context, perevod)
                                        }) {
                                        val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                        else painterResource(R.drawable.star)
                                        Icon(
                                            painter = icon, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                            PlainTooltip(stringResource(R.string.more_items), TooltipAnchorPosition.Below) {
                                IconButton(onClick = { expandedUp = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            AppDropdownMenu(
                                expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                                if (biblia == Settings.CHYTANNI_BIBLIA && viewModel.listState.size - 1 > 1) {
                                    DropdownMenuItem(onClick = {
                                        expandedUp = false
                                        dialogRazdel = true
                                        viewModel.autoScroll(title, false)
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
                                    viewModel.autoScroll(title, false)
                                    menuPosition = 2
                                }, text = { Text(stringResource(R.string.perevody), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(R.drawable.book_red), contentDescription = ""
                                    )
                                })
                                DropdownMenuItem(onClick = {
                                    expandedUp = false
                                    showDropdown = !showDropdown
                                    viewModel.autoScroll(title, false)
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
                    if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                }) {
                Column {
                    if (menuPosition == 2) {
                        Column(Modifier.selectableGroup()) {
                            Text(
                                stringResource(R.string.perevody), modifier = Modifier.padding(start = 10.dp, top = 10.dp), textAlign = TextAlign.Center, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                            )
                            val edit = k.edit()
                            if (getRealBook(knigaBiblii(viewModel.knigaText), Settings.PEREVODSEMUXI) != -1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODSEMUXI
                                            if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                "perevodMaranata", perevod
                                            )
                                            if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                "perevod", perevod
                                            )
                                            edit.apply()
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODSEMUXI, onClick = {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODSEMUXI
                                            if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                "perevodMaranata", perevod
                                            )
                                            if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                "perevod", perevod
                                            )
                                            edit.apply()
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        })
                                    Text(
                                        stringResource(R.string.title_biblia2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                            if (getRealBook(knigaBiblii(viewModel.knigaText), Settings.PEREVODBOKUNA) != -1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODBOKUNA
                                            if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                "perevodMaranata", perevod
                                            )
                                            if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                "perevod", perevod
                                            )
                                            edit.apply()
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODBOKUNA, onClick = {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODBOKUNA
                                            if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                "perevodMaranata", perevod
                                            )
                                            if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                "perevod", perevod
                                            )
                                            edit.apply()
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        })
                                    Text(
                                        stringResource(R.string.title_biblia_bokun2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                            if (getRealBook(knigaBiblii(viewModel.knigaText), Settings.PEREVODCARNIAUSKI) != -1 || knigaBiblii(viewModel.knigaText) == 30) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODCARNIAUSKI
                                            if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                "perevodMaranata", perevod
                                            )
                                            if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                "perevod", perevod
                                            )
                                            edit.apply()
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODCARNIAUSKI, onClick = {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODCARNIAUSKI
                                            if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                "perevodMaranata", perevod
                                            )
                                            if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                "perevod", perevod
                                            )
                                            edit.apply()
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        })
                                    Text(
                                        stringResource(R.string.title_biblia_charniauski2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                            if (k.getBoolean("catolik_bible", false) && ((biblia == Settings.CHYTANNI_BIBLIA && knigaBiblii(viewModel.knigaText) >= 50) || biblia == Settings.CHYTANNI_LITURGICHNYIA || biblia == Settings.CHYTANNI_VYBRANAE)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val dir = File("${context.filesDir}/Catolik")
                                            if (!dir.exists()) {
                                                viewModel.setPerevod = Settings.PEREVODCATOLIK
                                                viewModel.dialogDownLoad = true
                                            } else {
                                                val oldPerevod = perevod
                                                perevod = Settings.PEREVODCATOLIK
                                                if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                    "perevodMaranata", perevod
                                                )
                                                if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                    "perevod", perevod
                                                )
                                                edit.apply()
                                                viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                            }
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODCATOLIK, onClick = {
                                            val dir = File("${context.filesDir}/Catolik")
                                            if (!dir.exists()) {
                                                viewModel.setPerevod = Settings.PEREVODCATOLIK
                                                viewModel.dialogDownLoad = true
                                            } else {
                                                val oldPerevod = perevod
                                                perevod = Settings.PEREVODCATOLIK
                                                if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                    "perevodMaranata", perevod
                                                )
                                                if (biblia == Settings.CHYTANNI_LITURGICHNYIA) edit.putString(
                                                    "perevod", perevod
                                                )
                                                edit.apply()
                                                viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                            }
                                        })
                                    Text(
                                        stringResource(R.string.title_biblia_catolik2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                            if ((biblia == Settings.CHYTANNI_BIBLIA && viewModel.knigaText == "Пс") || biblia == Settings.CHYTANNI_VYBRANAE) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODNADSAN
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODNADSAN, onClick = {
                                            val oldPerevod = perevod
                                            perevod = Settings.PEREVODNADSAN
                                            viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                        })
                                    Text(
                                        stringResource(R.string.title_psalter), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                            if (k.getBoolean("sinoidal_bible", false) && biblia != Settings.CHYTANNI_LITURGICHNYIA) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val dir = File("${context.filesDir}/Sinodal")
                                            if (!dir.exists()) {
                                                viewModel.setPerevod = Settings.PEREVODSINOIDAL
                                                viewModel.dialogDownLoad = true
                                            } else {
                                                val oldPerevod = perevod
                                                perevod = Settings.PEREVODSINOIDAL
                                                if (biblia == Settings.CHYTANNI_MARANATA) {
                                                    edit.putString(
                                                        "perevodMaranata", perevod
                                                    )
                                                }
                                                edit.apply()
                                                viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                            }
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = perevod == Settings.PEREVODSINOIDAL, onClick = {
                                            val dir = File("${context.filesDir}/Sinodal")
                                            if (!dir.exists()) {
                                                viewModel.setPerevod = Settings.PEREVODSINOIDAL
                                                viewModel.dialogDownLoad = true
                                            } else {
                                                val oldPerevod = perevod
                                                perevod = Settings.PEREVODSINOIDAL
                                                if (biblia == Settings.CHYTANNI_MARANATA) {
                                                    edit.putString(
                                                        "perevodMaranata", perevod
                                                    )
                                                }
                                                edit.apply()
                                                viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                            }
                                        })
                                    Text(
                                        stringResource(R.string.bsinaidal2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                            if (k.getBoolean("newkingjames_bible", false) && biblia != Settings.CHYTANNI_LITURGICHNYIA) {
                                if (getRealBook(knigaBiblii(viewModel.knigaText), Settings.PEREVODNEWAMERICANBIBLE) != -1 || knigaBiblii(viewModel.knigaText) == 30) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val dir = File("${context.filesDir}/NewAmericanBible")
                                                if (!dir.exists()) {
                                                    viewModel.setPerevod = Settings.PEREVODNEWAMERICANBIBLE
                                                    viewModel.dialogDownLoad = true
                                                } else {
                                                    val oldPerevod = perevod
                                                    perevod = Settings.PEREVODNEWAMERICANBIBLE
                                                    if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                        "perevodMaranata", perevod
                                                    )
                                                    edit.apply()
                                                    viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                                }
                                            }, verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = perevod == Settings.PEREVODNEWAMERICANBIBLE, onClick = {
                                                val dir = File("${context.filesDir}/NewAmericanBible")
                                                if (!dir.exists()) {
                                                    viewModel.setPerevod = Settings.PEREVODNEWAMERICANBIBLE
                                                    viewModel.dialogDownLoad = true
                                                } else {
                                                    val oldPerevod = perevod
                                                    perevod = Settings.PEREVODNEWAMERICANBIBLE
                                                    if (biblia == Settings.CHYTANNI_MARANATA) edit.putString(
                                                        "perevodMaranata", perevod
                                                    )
                                                    edit.apply()
                                                    viewModel.setPerevodBible(context, biblia, cytanne, perevod, oldPerevod)
                                                }
                                            })
                                        Text(
                                            stringResource(R.string.perevod_new_american_bible_2), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                        )
                                    }
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = interactionSourse, indication = null) {}
                            .padding(top = 10.dp)
                            .background(colorTollBar)
                            .navigationBarsPadding(), horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PlainTooltip(stringResource(R.string.menu_font_size_app_info)) {
                            IconButton(
                                onClick = {
                                    showDropdown = !showDropdown
                                    viewModel.autoScroll(title, false)
                                    menuPosition = 1
                                }) {
                                Icon(
                                    modifier = Modifier.size(24.dp), painter = painterResource(R.drawable.format_size), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        PlainTooltip(stringResource(R.string.set_perakvad_biblii)) {
                            IconButton(
                                onClick = {
                                    showDropdown = !showDropdown
                                    viewModel.autoScroll(title, false)
                                    menuPosition = 2
                                }) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(R.drawable.book_red), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        PlainTooltip(stringResource(R.string.fullscreen_apis)) {
                            IconButton(
                                onClick = {
                                    fullscreen = true
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.fullscreen), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        if (biblia == Settings.CHYTANNI_BIBLIA && viewModel.listState.size - 1 > 1) {
                            PlainTooltip(stringResource(R.string.set_glava_biblii)) {
                                IconButton(
                                    onClick = {
                                        viewModel.autoScroll(title, false)
                                        dialogRazdel = true
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.apps), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                        if (biblia == Settings.CHYTANNI_BIBLIA) {
                            PlainTooltip(stringResource(if (viewModel.isVybranoe) R.string.vybranae_remove else R.string.vybranae_add)) {
                                IconButton(
                                    onClick = {
                                        viewModel.saveVybranoe(context, perevod)
                                    }) {
                                    val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                    else painterResource(R.drawable.star)
                                    Icon(
                                        painter = icon, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                        if (!isParallelVisable) {
                            if (viewModel.listState[viewModel.selectedIndex].lazyListState.canScrollForward) {
                                val iconAutoScroll = if (viewModel.autoScrollSensor) painterResource(R.drawable.stop_circle)
                                else painterResource(R.drawable.play_circle)
                                PlainTooltip(stringResource(if (viewModel.autoScrollSensor) R.string.auto_stop else R.string.auto_play)) {
                                    IconButton(onClick = {
                                        viewModel.autoScrollSensor = !viewModel.autoScrollSensor
                                        viewModel.autoScroll(title, viewModel.autoScrollSensor)
                                        if (viewModel.autoScrollSensor) {
                                            actyvity.window.addFlags(
                                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                            )
                                        } else if (!k.getBoolean("power", false)) {
                                            actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                        }
                                    }) {
                                        Icon(
                                            painter = iconAutoScroll, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            } else if (viewModel.listState[viewModel.selectedIndex].lazyListState.canScrollBackward) {
                                PlainTooltip(stringResource(R.string.auto_up)) {
                                    IconButton(onClick = {
                                        isUpList = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_upward), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                        } else {
                            viewModel.autoScroll(title, false)
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
                LaunchedEffect(viewModel.selectedIndex) {
                    coroutineScope.launch {
                        viewModel.isVybranoe = false
                        if (viewModel.vybranoeList.isNotEmpty()) {
                            for (i in 0 until viewModel.vybranoeList.size) {
                                if (viewModel.knigaText == viewModel.vybranoeList[i].knigaText && viewModel.vybranoeList[i].glava == viewModel.selectedIndex) {
                                    viewModel.isVybranoe = true
                                    break
                                }
                            }
                        }
                        pagerState.scrollToPage(viewModel.selectedIndex)
                        lazyRowState.scrollToItem(viewModel.selectedIndex)
                    }
                }
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        viewModel.selectedIndex = page
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
                            subTitle = "Катызма $kafizma"
                        }
                    }
                }
            }
            Column {
                if (!fullscreen && biblia == Settings.CHYTANNI_BIBLIA && viewModel.listState.size - 1 != 0) {
                    LazyRow(
                        state = lazyRowState
                    ) {
                        items(viewModel.listState.size) { page ->
                            val color = if (viewModel.selectedIndex == page) BezPosta
                            else Color.Unspecified
                            val textColor = if (viewModel.selectedIndex == page) PrimaryText
                            else MaterialTheme.colorScheme.secondary
                            Text(
                                (page + 1).toString(), modifier = Modifier
                                    .clickable {
                                        viewModel.selectedIndex = page
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
                            AppNavGraphState.setScrollValuePosition(title, viewModel.listState[viewModel.selectedIndex].lazyListState.firstVisibleItemIndex, viewModel.listState[viewModel.selectedIndex].lazyListState.firstVisibleItemScrollOffset)
                            return super.onPreScroll(available, source)
                        }

                        override suspend fun onPostFling(
                            consumed: Velocity, available: Velocity
                        ): Velocity {
                            isScrollRun = false
                            if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                            return super.onPostFling(consumed, available)
                        }
                    }
                }
                if (viewModel.listState[viewModel.selectedIndex].item.isNotEmpty()) {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            viewModel.listState[viewModel.selectedIndex].lazyListState.scrollToItem(AppNavGraphState.getScrollValuePosition(title), AppNavGraphState.getScrollValueOffset(title))
                        }
                    }
                    if (biblia == Settings.CHYTANNI_BIBLIA && positionRemember != -1) {
                        LaunchedEffect(positionRemember) {
                            coroutineScope.launch {
                                viewModel.listState[viewModel.selectedIndex].lazyListState.scrollToItem(positionRemember)
                                positionRemember = -1
                            }
                        }
                    }
                    if (biblia == Settings.CHYTANNI_LITURGICHNYIA && skipUtran && utranEndPosition > 0) {
                        var pos = AppNavGraphState.getScrollValuePosition(title)
                        val offset = AppNavGraphState.getScrollValueOffset(title)
                        if (pos == 0) pos = utranEndPosition
                        LaunchedEffect(pos) {
                            coroutineScope.launch {
                                viewModel.listState[viewModel.selectedIndex].lazyListState.scrollToItem(pos, offset)
                                positionRemember = -1
                                skipUtran = false
                            }
                        }
                    }
                }
                HorizontalPager(
                    pageSpacing = 10.dp, state = pagerState, flingBehavior = fling, verticalAlignment = Alignment.Top, userScrollEnabled = biblia == Settings.CHYTANNI_BIBLIA
                ) { page ->
                    viewModel.updatePage(biblia, page, perevod)
                    val resultPage = viewModel.listState[page].item
                    if (resultPage.isNotEmpty() && biblia != Settings.CHYTANNI_BIBLIA && positionRemember != -1) {
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
                                viewModel.listState[viewModel.selectedIndex].lazyListState.scrollToItem(resultCount)
                                positionRemember = -1
                            }
                        }
                    }
                    if (resultPage.isNotEmpty() && skipUtran) {
                        val tit = resultPage[viewModel.selectedIndex].title
                        for (i in 0 until resultPage.size) {
                            if (resultPage[i].title != tit) {
                                utranEndPosition = i
                                break
                            }
                        }
                    }
                    if (resultPage.isNotEmpty() && viewModel.selectState.isEmpty()) {
                        viewModel.selectState.addAll(resultPage.map { false }.toMutableStateList())
                    }
                    if (resultPage.isNotEmpty()) {
                        if (biblia == Settings.CHYTANNI_BIBLIA && perevodRoot != Settings.PEREVODNADSAN) {
                            subTitle = resultPage[0].title.substringBeforeLast(" ")
                        }
                        if (biblia != Settings.CHYTANNI_BIBLIA) {
                            LaunchedEffect(viewModel.listState[page]) {
                                snapshotFlow { viewModel.listState[page].lazyListState.firstVisibleItemIndex }.collect { index ->
                                    if (subTitle != resultPage[index].title) {
                                        subTitle = resultPage[index].title
                                    }
                                }
                            }
                        }
                    }
                    if (isSelectAll) {
                        isSelectAll = false
                        if (biblia == Settings.CHYTANNI_BIBLIA) {
                            viewModel.selectState.forEachIndexed { index, _ ->
                                viewModel.selectState[index] = true
                            }
                        } else {
                            var findTitle = ""
                            resultPage.forEachIndexed { index, text ->
                                if (viewModel.selectState[index]) {
                                    findTitle = text.title
                                    return@forEachIndexed
                                }
                            }
                            resultPage.forEachIndexed { index, text ->
                                if (findTitle == text.title) {
                                    viewModel.selectState[index] = true
                                }
                            }
                        }
                    }
                    if (!isSelectMode) {
                        viewModel.selectState.forEachIndexed { index, _ ->
                            viewModel.selectState[index] = false
                        }
                    }
                    if (isCopyMode || isShareMode) {
                        val sb = StringBuilder()
                        resultPage.forEachIndexed { index, text ->
                            if (viewModel.selectState[index]) {
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
                            Toast.makeText(context, stringResource(R.string.copy), Toast.LENGTH_SHORT).show()
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
                    }
                    LazyColumn(
                        Modifier
                            .pointerInput(PointerEventType.Press) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Press) {
                                            viewModel.autoScroll(title, false)
                                        }
                                        if (viewModel.autoScrollSensor && event.type == PointerEventType.Release && !isScrollRun) {
                                            viewModel.autoScroll(title, true)
                                        }
                                        if (event.changes.size == 2) {
                                            fontSize *= event.calculateZoom()
                                            fontSize = fontSize.coerceIn(18f, 58f)
                                            k.edit {
                                                putFloat("font_biblia", fontSize)
                                            }
                                            event.changes.forEach { pointerInputChange: PointerInputChange ->
                                                pointerInputChange.consume()
                                            }
                                        }
                                    }
                                }
                            }
                            .nestedScroll(nestedScrollConnection), state = viewModel.listState[page].lazyListState) {
                        items(resultPage.size, key = { index -> resultPage[index].id }) { index ->
                            Column(
                                if (!viewModel.autoScrollSensor && !showDropdown) {
                                    Modifier
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = {
                                                if (!isSelectMode && isParallel && resultPage[index].parallel != "+-+") {
                                                    isParallelVisable = true
                                                    paralelChtenia = resultPage[index].parallel
                                                }
                                                if (isSelectMode) {
                                                    viewModel.selectState[index] = !viewModel.selectState[index]
                                                }
                                            }, onLongPress = {
                                                if (!fullscreen) {
                                                    isSelectMode = true
                                                    viewModel.selectState[index] = !viewModel.selectState[index]
                                                }
                                            }, onDoubleTap = {
                                                fullscreen = !fullscreen
                                            })
                                        }
                                } else {
                                    Modifier
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onDoubleTap = {
                                                    fullscreen = !fullscreen
                                                })
                                        }
                                }
                            ) {
                                if (index == 0) {
                                    Spacer(Modifier.padding(top = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp))
                                    if (!(biblia == Settings.CHYTANNI_BIBLIA || biblia == Settings.CHYTANNI_VYBRANAE)) {
                                        val titlePerevod = when (perevod) {
                                            Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia2)
                                            Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal2)
                                            Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
                                            Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun2)
                                            Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski2)
                                            Settings.PEREVODCATOLIK -> stringResource(R.string.title_biblia_catolik2)
                                            Settings.PEREVODNEWAMERICANBIBLE -> stringResource(R.string.perevod_new_american_bible_2)
                                            else -> stringResource(R.string.title_biblia2)
                                        }
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 10.dp, end = 10.dp, top = 10.dp), text = titlePerevod, fontSize = fontSize.sp, lineHeight = fontSize.sp * 1.15, color = SecondaryText
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelectMode) {
                                        Icon(
                                            painter = painterResource(if (viewModel.selectState[index]) R.drawable.select_check_box else R.drawable.check_box_outline_blank), contentDescription = "", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier
                                                .padding(start = 5.dp)
                                                .clickable {
                                                    viewModel.selectState[index] = !viewModel.selectState[index]
                                                })
                                    }
                                    HtmlText(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp), text = resultPage[index].text, fontSize = fontSize.sp, color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                if (resultPage[index].translate.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp),
                                        text = resultPage[index].translate, fontSize = fontSize.sp, color = SecondaryText, fontStyle = FontStyle.Italic
                                    )
                                }
                                if (isParallel && resultPage[index].parallel != "+-+") {
                                    Text(
                                        text = resultPage[index].parallel, modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp), fontSize = (Settings.fontInterface - 4).sp, lineHeight = (Settings.fontInterface - 4).sp * 1.15, color = SecondaryText
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.padding(bottom = if (fullscreen) 10.dp else innerPadding.calculateBottomPadding().plus(if (isBottomBar) 0.dp else 10.dp)))
                            if (viewModel.listState[page].lazyListState.lastScrolledForward && !viewModel.listState[page].lazyListState.canScrollForward) {
                                viewModel.autoScroll(title, false)
                                viewModel.autoScrollSensor = false
                                if (!k.getBoolean("power", false)) {
                                    actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                }
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
                    viewModel.autoScrollTextVisable, enter = fadeIn(
                        tween(
                            durationMillis = 700, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
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
                    viewModel.autoScrollSensor, enter = fadeIn(
                        tween(
                            durationMillis = 700, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (!isBottomBar || fullscreen) 10.dp else 0.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            painter = painterResource(R.drawable.minus_auto_scroll), contentDescription = "", modifier = Modifier
                                .padding(end = 10.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Button)
                                .size(40.dp)
                                .padding(5.dp)
                                .clickable {
                                    if (viewModel.autoScrollSpeed in 10..125) {
                                        viewModel.autoScrollSpeed += 5
                                        val proc = 100 - (viewModel.autoScrollSpeed - 15) * 100 / 115
                                        autoScrollTextColor = Post
                                        autoScrollTextColor2 = PrimaryText
                                        autoScrollText = "$proc%"
                                        viewModel.autoScrollSpeed(context)
                                    }
                                })
                        Image(
                            painter = painterResource(R.drawable.plus_auto_scroll), contentDescription = "", modifier = Modifier
                                .align(Alignment.Bottom)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Button)
                                .size(40.dp)
                                .padding(5.dp)
                                .clickable {
                                    if (viewModel.autoScrollSpeed in 20..135) {
                                        viewModel.autoScrollSpeed -= 5
                                        val proc = 100 - (viewModel.autoScrollSpeed - 15) * 100 / 115
                                        autoScrollTextColor = Primary
                                        autoScrollTextColor2 = PrimaryTextBlack
                                        autoScrollText = "$proc%"
                                        viewModel.autoScrollSpeed(context)
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
    listSize: Int, autoScrollSensor: Boolean, setSelectedIndex: (Int) -> Unit, setAutoScroll: (Boolean) -> Unit, onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var textFieldValueState by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.data_search),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    text = stringResource(R.string.razdzel_count, listSize), fontSize = 18.sp, fontStyle = FontStyle.Italic
                )
                TextField(
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                    placeholder = { Text(stringResource(R.string.set_razdel), fontSize = Settings.fontInterface.sp) },
                    value = textFieldValueState,
                    onValueChange = {
                        if (it.length <= 3 && (it.matches(Regex("^\\d+$")) || it.isEmpty())) textFieldValueState = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (textFieldValueState.isNotEmpty() && textFieldValueState.toInt() > 0 && textFieldValueState.toInt() <= listSize) {
                            setSelectedIndex(textFieldValueState.toInt() - 1)
                        }
                        if (autoScrollSensor) setAutoScroll(true)
                        onDismiss()
                    })
                )
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
                        onClick = {
                            if (textFieldValueState.isNotEmpty() && textFieldValueState.toInt() > 0 && textFieldValueState.toInt() <= listSize) {
                                setSelectedIndex(textFieldValueState.toInt() - 1)
                            }
                            if (autoScrollSensor) setAutoScroll(true)
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun getBible(cytanne: String, perevod: String, biblia: Int, isTitle: Boolean = false): ArrayList<CytanniListData> {
    val context = Malitounik.applicationContext()
    val result = ArrayList<CytanniListData>()
    var id = 0
    try {
        var chytNew = cytanne
        if (cytanne.contains("Пасл Ер 1") && (perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODNEWAMERICANBIBLE)) {
            chytNew = chytNew.replace("Пасл Ер 1", "Вар 6")
        }
        val list = chytNew.split(";")
        var knigaText = ""
        var knigaStyxi = ""
        var perevodOld = perevod
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
                        knigaText = itemList2.take(t1)
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
                            glavaStart = subItemList.take(t3).toInt()
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
                            glavaEnd = subItemList2.take(t4).toInt()
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
                        if (kniga == -1) {
                            perevodNew = Settings.PEREVODCARNIAUSKI
                            kniga = getRealBook(knigiBiblii, perevodNew)
                        }
                        if (biblia != Settings.CHYTANNI_LITURGICHNYIA && kniga == -1) {
                            perevodNew = Settings.PEREVODSINOIDAL
                            kniga = getRealBook(knigiBiblii, perevodNew)
                        }
                        if (biblia != Settings.CHYTANNI_LITURGICHNYIA && (knigiBiblii == 21 && glava == 151 && (perevod == Settings.PEREVODCARNIAUSKI || perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODNEWAMERICANBIBLE))) {
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
                        var titlePerevod = ""
                        if (perevodOld != perevodNew) {
                            when (perevodNew) {
                                Settings.PEREVODSEMUXI -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.title_biblia2) + "</font><br>"
                                Settings.PEREVODSINOIDAL -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.bsinaidal2) + "</font><br>"
                                Settings.PEREVODNADSAN -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.title_psalter) + "</font><br>"
                                Settings.PEREVODBOKUNA -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.title_biblia_bokun2) + "</font><br>"
                                Settings.PEREVODCARNIAUSKI -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.title_biblia_charniauski2) + "</font><br>"
                                Settings.PEREVODCATOLIK -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.title_biblia_catolik2) + "</font><br>"
                                Settings.PEREVODNEWAMERICANBIBLE -> titlePerevod = "<br><font color=\"#999999\">" + context.getString(R.string.perevod_new_american_bible_2) + "</font><br>"
                            }
                            perevodOld = perevodNew
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
                                    var textBibleItem = textBible[w].styx
                                    val listBr = textBibleItem.split("<br>")
                                    if (listBr.size > 1) {
                                        val sb = StringBuilder()
                                        for (e in listBr.indices) {
                                            val br = if (listBr.size - 1 == e) "" else "<br>"
                                            val t1 = listBr[e].indexOf(" ")
                                            val isInt = if (t1 != -1) {
                                                val item = listBr[e].substring(0, t1)
                                                item.isNotEmpty() && item.isDigitsOnly()
                                            } else false
                                            if (isInt) {
                                                val styx = listBr[e].substring(0, t1).toInt()
                                                sb.append("<font color=#D00505>$styx. </font>" + listBr[e].substring(t1)).append(br)
                                            } else {
                                                sb.append(listBr[e]).append(br)
                                            }
                                        }
                                        textBibleItem = sb.toString()
                                    }
                                    var t5 = textBibleItem.indexOf("<br>")
                                    if (t5 == -1) t5 = 0
                                    else t5 += 4
                                    val t6 = textBibleItem.indexOf(" ", t5)
                                    val isInt = if (t6 != -1) {
                                        val item = textBibleItem.substring(t5, t6)
                                        item.isNotEmpty() && item.isDigitsOnly()
                                    } else false
                                    if (w == 0) {
                                        if (e > 0) {
                                            result.add(
                                                CytanniListData(
                                                    id = id, title = "${
                                                        getNameBook(
                                                            context, kniga, perevodNew, knigiBiblii >= 50
                                                        )
                                                    } ${textBible[w].glava}", text = "[&#8230;]"
                                                )
                                            )
                                        } else {
                                            if (isTitle) {
                                                result.add(
                                                    CytanniListData(
                                                        id = id, title = "${
                                                            getNameBook(
                                                                context, kniga, perevodNew, knigiBiblii >= 50
                                                            )
                                                        } ${textBible[w].glava}", text = if (biblia == Settings.CHYTANNI_LITURGICHNYIA) {
                                                            val eGlavy = knigaStyxi.ifEmpty { glava.toString() }
                                                            "$titlePerevod<strong><br>" + getNameBook(
                                                                context, kniga, perevodNew, knigiBiblii >= 50
                                                            ) + " $eGlavy<strong><br>"
                                                        } else {
                                                            "$titlePerevod<strong><br>" + getNameBook(
                                                                context, kniga, perevodNew, knigiBiblii >= 50
                                                            ) + " $glava<strong><br>"
                                                        }))
                                            }
                                        }
                                        id++
                                    }
                                    var text = textBibleItem
                                    if (isInt) {
                                        val styx = textBibleItem.substring(t5, t6).toInt()
                                        text = textBibleItem.take(t5) + "<font color=#D00505>$styx. </font>" + textBibleItem.substring(t6)
                                    }
                                    result.add(
                                        CytanniListData(
                                            id = id, title = "${
                                                getNameBook(
                                                    context, kniga, perevodNew, knigiBiblii >= 50
                                                )
                                            } ${textBible[w].glava}", text = text, parallel = textBible[w].paralelStyx, translate = textBible[w].translate
                                        )
                                    )
                                    id++
                                }
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            result.add(CytanniListData(id = id, title = "", text = openAssetsResources(context, "biblia_error.txt")))
                            id++
                        }
                    }
                }
            }
        }
    } catch (_: Throwable) {
        result.add(CytanniListData(id = id, title = "", text = openAssetsResources(context, "biblia_error.txt")))
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
    if (kniga == "Вар") bible = 31
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
    if (kniga == "Юды") bible = 61
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

data class CytanniListItemData(val item: SnapshotStateList<CytanniListData>, val lazyListState: LazyListState)

data class CytanniListData(
    val id: Int, val title: String, val text: String = "", val parallel: String = "+-+", val translate: String = ""
)

data class VybranaeData(
    val id: Long, val title: String, val knigaText: String, val glava: Int, val perevod: String
)