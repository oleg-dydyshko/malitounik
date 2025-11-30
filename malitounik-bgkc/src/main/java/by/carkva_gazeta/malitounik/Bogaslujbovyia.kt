package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.SpannableStringBuilder
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.admin.PaisochnicaFileList
import by.carkva_gazeta.malitounik.admin.Piasochnica
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Button
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.AppDropdownMenu
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.DialogListinner
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.findCaliandarToDay
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder
import java.util.Calendar

class BogaslujbovyiaViewModel : ViewModel() {
    var autoScroll by mutableStateOf(false)
    var autoScrollSensor by mutableStateOf(false)
    var autoScrollSpeed by mutableIntStateOf(60)
    var autoScrollTextVisable by mutableStateOf(false)
    val vybranoeList = mutableStateListOf<VybranaeDataAll>()
    var isVybranoe by mutableStateOf(false)
    val scrollState = ScrollState(0)
    val result = mutableStateListOf<ArrayList<Int>>()
    var resultPosition by mutableIntStateOf(0)
    var scrollToY = 0f
    var find by mutableStateOf(false)
    private var autoScrollJob: Job? = null
    private var autoScrollTextVisableJob: Job? = null
    private var searchJob: Job? = null
    var searchText by mutableStateOf(AppNavGraphState.searchBogaslujbovyia.isNotEmpty())
    var searshString by mutableStateOf(TextFieldValue(AppNavGraphState.searchBogaslujbovyia, TextRange(AppNavGraphState.searchBogaslujbovyia.length)))
    var searchTextResult by mutableStateOf(AnnotatedString(""))
    val searchList = mutableStateListOf<SearchBibleItem>()
    var htmlText by mutableStateOf("")
    private val gson = Gson()
    private val type = TypeToken.getParameterized(ArrayList::class.java, VybranaeDataAll::class.java).type

    fun search(textLayout: TextLayoutResult?) {
        if (searshString.text.trim().length >= 3) {
            searchJob?.cancel()
            textLayout?.let { layout ->
                searchJob = viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        result.clear()
                        resultPosition = 0
                        result.addAll(findAllAsanc(AnnotatedString.fromHtml(htmlText).text, searshString.text))
                        if (result.isNotEmpty()) {
                            val opiginalText = layout.layoutInput.text
                            val annotatedString = buildAnnotatedString {
                                append(opiginalText)
                                for (i in result.indices) {
                                    val size = result[i].size - 1
                                    addStyle(SpanStyle(background = BezPosta, color = PrimaryText), result[i][0], result[i][size])
                                }
                            }
                            searchTextResult = annotatedString
                            val t1 = result[0][0]
                            if (t1 != -1) {
                                val line = layout.getLineForOffset(t1)
                                scrollToY = layout.getLineTop(line)
                                find = !find
                            }
                        } else {
                            searchTextResult = AnnotatedString("")
                        }
                    }
                }
            }
        } else {
            searchTextResult = AnnotatedString("")
        }
    }

    fun findForward(textLayout: TextLayoutResult?) {
        textLayout?.let { layout ->
            if (result.isNotEmpty()) {
                if (result.size - 1 > resultPosition) {
                    resultPosition += 1
                }
                val t1 = result[resultPosition][0]
                if (t1 != -1) {
                    val line = layout.getLineForOffset(t1)
                    scrollToY = layout.getLineTop(line)
                    find = !find
                }
            }
        }
    }

    fun findBack(textLayout: TextLayoutResult?) {
        textLayout?.let { layout ->
            if (result.isNotEmpty()) {
                if (resultPosition > 0) {
                    resultPosition -= 1
                }
                val t1 = result[resultPosition][0]
                if (t1 != -1) {
                    val line = layout.getLineForOffset(t1)
                    scrollToY = layout.getLineTop(line)
                    find = !find
                }
            }
        }
    }

    fun initVybranoe(context: Context, resurs: String) {
        if (htmlText.isEmpty()) {
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            autoScrollSpeed = k.getInt("autoscrollSpid", 60)
            htmlText = openAssetsResources(context, resurs)
        }
        val file = File("${context.filesDir}/vybranoe_all.json")
        if (file.exists() && vybranoeList.isEmpty()) {
            vybranoeList.addAll(gson.fromJson(file.readText(), type))
            if (vybranoeList.isNotEmpty()) {
                for (i in 0 until vybranoeList.size) {
                    if (resurs == vybranoeList[i].resource) {
                        isVybranoe = true
                        break
                    }
                }
            }
        }
    }

    fun saveVybranoe(context: Context, title: String, resurs: String) {
        if (isVybranoe) {
            var pos = 0
            for (i in 0 until vybranoeList.size) {
                if (resurs == vybranoeList[i].resource) {
                    pos = i
                    break
                }
            }
            vybranoeList.removeAt(pos)
            Toast.makeText(context, context.getString(R.string.removeVybranoe), Toast.LENGTH_SHORT).show()
        } else {
            vybranoeList.add(0, VybranaeDataAll(Calendar.getInstance().timeInMillis, title, resurs))
            Toast.makeText(context, context.getString(R.string.addVybranoe), Toast.LENGTH_SHORT).show()
        }
        isVybranoe = !isVybranoe
        val file = File("${context.filesDir}/vybranoe_all.json")
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
                            scrollState.scrollBy(2f)
                            AppNavGraphState.setScrollValuePosition(title, scrollState.value)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bogaslujbovyia(
    navController: NavHostController, title: String, resurs: String,
    navigateTo: (String, skipUtran: Boolean) -> Unit = { _, _ -> }, viewModel: BogaslujbovyiaViewModel = viewModel(), adminViewModel: Piasochnica
) {
    val resursEncode = URLDecoder.decode(resurs, "UTF8")
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    var showDropdown by remember { mutableStateOf(false) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var autoScrollText by remember { mutableStateOf("") }
    var autoScrollTextColor by remember { mutableStateOf(Primary) }
    var autoScrollTextColor2 by remember { mutableStateOf(PrimaryTextBlack) }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var adminResourceEditPosition by remember { mutableIntStateOf(0) }
    val scrollStateDop = rememberScrollState()
    viewModel.initVybranoe(context, resursEncode)
    var isBottomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
    var backPressHandled by remember { mutableStateOf(false) }
    var iskniga by rememberSaveable { mutableStateOf(false) }
    var bottomSheetScaffoldIsVisible by rememberSaveable { mutableStateOf(AppNavGraphState.bottomSheetScaffoldIsVisible) }
    val actyvity = LocalActivity.current as MainActivity
    if (viewModel.autoScrollSensor) {
        actyvity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        SheetState(
            skipHiddenState = false,
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            initialValue = SheetValue.Hidden
        )
    )
    LaunchedEffect(viewModel.find) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.scrollState.animateScrollTo(viewModel.scrollToY.toInt())
                AppNavGraphState.setScrollValuePosition(title, viewModel.scrollState.value)
            }
        }
    }
    LifecycleResumeEffect(Unit) {
        if (resursEncode.contains("akafist")) {
            AppNavGraphState.setScrollValuePosition(title, k.getInt(resursEncode, 0))
            coroutineScope.launch {
                viewModel.scrollState.animateScrollTo(AppNavGraphState.getScrollValuePosition(title))
            }
        }
        onPauseOrDispose {
            if (resursEncode.contains("akafist")) {
                k.edit {
                    putInt(resursEncode, viewModel.scrollState.value)
                }
            }
        }
    }
    BackHandler(!backPressHandled || showDropdown || iskniga || viewModel.searchText) {
        when {
            viewModel.searchText -> {
                viewModel.searchText = false
                viewModel.searchTextResult = AnnotatedString("")
            }

            iskniga -> {
                if (bottomSheetScaffoldIsVisible) {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.show()
                    }
                }
                iskniga = false
            }

            showDropdown -> {
                showDropdown = false
                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            }

            !backPressHandled -> {
                AppNavGraphState.searchBogaslujbovyia = ""
                fullscreen = false
                backPressHandled = true
                if (!k.getBoolean("power", false)) actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                navController.popBackStack()
            }
        }
    }
    var isUpList by remember { mutableStateOf(false) }
    if (isUpList) {
        LaunchedEffect(Unit) {
            isUpList = false
            coroutineScope.launch {
                viewModel.scrollState.animateScrollTo(0)
            }
        }
    }
    val view = LocalView.current
    LaunchedEffect(fullscreen) {
        val controller =
            WindowCompat.getInsetsController((view.context as Activity).window, view)
        if (fullscreen) {
            bottomSheetScaffoldIsVisible = false
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.show(WindowInsetsCompat.Type.navigationBars())
        }
    }
    var dialogLiturgia by rememberSaveable { mutableStateOf(false) }
    var chast by rememberSaveable { mutableIntStateOf(0) }
    if (dialogLiturgia) {
        DialogLiturgia(chast) {
            dialogLiturgia = false
        }
    }
    var dialogQrCode by rememberSaveable { mutableStateOf(false) }
    if (dialogQrCode) {
        DialogImage(painter = painterResource(R.drawable.qr_code_google_play)) {
            dialogQrCode = false
        }
    }
    var dialogSztoHovahaVisable by remember { mutableStateOf(false) }
    if (dialogSztoHovahaVisable) {
        DialogSztoHovaha {
            dialogSztoHovahaVisable = false
        }
    }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    val maxLine = remember { mutableIntStateOf(1) }
    var subTitle by rememberSaveable { mutableStateOf("") }
    var subText by rememberSaveable { mutableStateOf("") }
    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDialogNoWIFIVisable by remember { mutableStateOf(false) }
    var printFile by remember { mutableStateOf("") }
    if (isDialogNoWIFIVisable) {
        DialogNoWiFI(
            onDismiss = {
                isDialogNoWIFIVisable = false
            },
            onConfirmation = {
                writeFile(
                    context, printFile, loadComplete = {
                        val printAdapter = PdfDocumentAdapter(context, printFile)
                        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                        val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                        printManager.print(printFile, printAdapter, printAttributes)
                    },
                    inProcess = {
                    })
                isDialogNoWIFIVisable = false
            }
        )
    }
    val isViachernia = resursEncode == "bogashlugbovya/viaczernia_niadzelnaja.html" || resursEncode == "bogashlugbovya/viaczernia_na_kozny_dzen.html" || resursEncode == "bogashlugbovya/viaczernia_u_vialikim_poscie.html" || resursEncode == "bogashlugbovya/viaczerniaja_sluzba_sztodzionnaja_biez_sviatara.html" || resursEncode == "bogashlugbovya/viaczernia_svietly_tydzien.html"
    val isUtran = resursEncode == "bogashlugbovya/jutran_niadzelnaja.html"
    val isLiturgia = resursEncode == "bogashlugbovya/lit_jana_zalatavusnaha.html" || resursEncode == "bogashlugbovya/lit_jan_zalat_vielikodn.html" || resursEncode == "bogashlugbovya/lit_vasila_vialikaha.html" || resursEncode == "bogashlugbovya/abiednica.html" || resursEncode == "bogashlugbovya/vialikdzien_liturhija.html"
    val data = findCaliandarToDay()
    val cytanneVisable = (isLiturgia && isLiturgia(data)) || isViachernia || isUtran
    val listResource = ArrayList<SlugbovyiaTextuData>()
    when {
        isLiturgia -> {
            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.LITURHIJA, data[24].toInt(), data[3].toInt()))
        }

        isUtran -> {
            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.JUTRAN, data[24].toInt(), data[3].toInt()))
        }

        isViachernia -> {
            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.VIACZERNIA, data[24].toInt(), data[3].toInt()))
        }
    }
    LaunchedEffect(bottomSheetScaffoldIsVisible) {
        if (listResource.isNotEmpty()) {
            if (!iskniga) {
                coroutineScope.launch {
                    if (bottomSheetScaffoldIsVisible) {
                        bottomSheetScaffoldState.bottomSheetState.show()
                    } else {
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }
        } else {
            AppNavGraphState.bottomSheetScaffoldIsVisible = false
        }
    }
    var paddingValues by remember { mutableStateOf(PaddingValues()) }
    BottomSheetScaffold(
        sheetDragHandle = { },
        sheetSwipeEnabled = false,
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Column(Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                Column {
                    for (i in listResource.indices) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    adminResourceEditPosition = i
                                    subTitle = listResource[i].title
                                    subText = openAssetsResources(context, listResource[i].resource)
                                    iskniga = true
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.hide()
                                    }
                                    viewModel.autoScroll(title, false)
                                    viewModel.autoScrollSensor = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = listResource[i].title,
                                fontSize = Settings.fontInterface.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        HorizontalDivider()
                    }
                    if (cytanneVisable) {
                        var skipUtran = false
                        if (data[9].isNotEmpty()) {
                            var chtenie = data[9]
                            if (isLiturgia && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.substring(t1 + 1)
                                skipUtran = true
                            }
                            if (isUtran && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.take(t1)
                            }
                            listResource.add(SlugbovyiaTextuData(0, chtenie, "9", SlugbovyiaTextu.LITURHIJA))
                        }
                        if (data[10].isNotEmpty()) {
                            listResource.add(SlugbovyiaTextuData(0, data[10], "10", SlugbovyiaTextu.LITURHIJA))
                        }
                        if (data[11].isNotEmpty()) {
                            var chtenie = data[11]
                            if (isLiturgia && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.substring(t1 + 1)
                                skipUtran = true
                            }
                            if (isUtran && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.take(t1)
                            }
                            listResource.add(SlugbovyiaTextuData(0, chtenie, "11", SlugbovyiaTextu.LITURHIJA))
                        }
                        for (i in listResource.indices) {
                            if (!(listResource[i].resource == "9" || listResource[i].resource == "10" || listResource[i].resource == "11")) continue
                            val navigate = when (listResource[i].resource) {
                                "10" -> "cytannesvityx"
                                "11" -> "cytannedop"
                                else -> "cytanne"
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.hide()
                                        }
                                        viewModel.autoScroll(title, false)
                                        viewModel.autoScrollSensor = false
                                        navigateTo(navigate, skipUtran)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp),
                                    painter = painterResource(R.drawable.poiter),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = ""
                                )
                                Text(
                                    modifier = Modifier.padding(start = 10.dp),
                                    text = listResource[i].title,
                                    fontSize = Settings.fontInterface.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        paddingValues = innerPadding
        var shareIsLaunch by remember { mutableStateOf(false) }
        val launcherShare = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            shareIsLaunch = false
        }
        var dialogHelpShare by remember { mutableStateOf(false) }
        if (dialogHelpShare) {
            DialogHelpShare {
                if (it) {
                    k.edit {
                        putBoolean("isShareHelp", false)
                    }
                }
                dialogHelpShare = false
                shareIsLaunch = true
            }
        }
        var expandedUp by remember { mutableStateOf(false) }
        val interactionSourse = remember { MutableInteractionSource() }
        if (shareIsLaunch) {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val isTextFound = textLayout?.layoutInput?.text?.text?.contains(clipboard.primaryClip?.getItemAt(0)?.text ?: "@#$") == true
            val sent = if (isTextFound) clipboard.primaryClip?.getItemAt(0)?.text
            else textLayout?.layoutInput?.text?.text
            sent?.let { shareText ->
                if (!isTextFound) {
                    val clip = ClipData.newPlainText(context.getString(R.string.copy_text), shareText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, context.getString(R.string.copy), Toast.LENGTH_SHORT).show()
                }
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                sendIntent.type = "text/plain"
                launcherShare.launch(Intent.createChooser(sendIntent, title))
            }
        }
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    !fullscreen, enter = fadeIn(
                        tween(
                            durationMillis = 500, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                ) {
                    TopAppBar(
                        title = {
                            if (!viewModel.searchText) {
                                Text(
                                    modifier = Modifier.clickable {
                                        maxLine.intValue = Int.MAX_VALUE
                                        coroutineScope.launch {
                                            delay(5000L)
                                            maxLine.intValue = 1
                                        }
                                    },
                                    text = if (iskniga) subTitle.replace("\n", " ").uppercase() else title.replace("\n", " ").uppercase(),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = maxLine.intValue,
                                    overflow = TextOverflow.Ellipsis,
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
                                    value = viewModel.searshString,
                                    onValueChange = { newText ->
                                        var edit = newText.text
                                        edit = edit.replace("и", "і")
                                        edit = edit.replace("щ", "ў")
                                        edit = edit.replace("И", "І")
                                        edit = edit.replace("Щ", "Ў")
                                        edit = edit.replace("ъ", "'")
                                        viewModel.searchTextResult = AnnotatedString("")
                                        viewModel.searshString = TextFieldValue(edit, newText.selection)
                                        AppNavGraphState.searchBogaslujbovyia = viewModel.searshString.text
                                        viewModel.search(textLayout)
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
                                        IconButton(onClick = {
                                            viewModel.searshString = TextFieldValue("")
                                            viewModel.searchList.clear()
                                        }) {
                                            Icon(
                                                painter = if (viewModel.searshString.text.isNotEmpty()) painterResource(R.drawable.close) else painterResource(R.drawable.empty),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                                        focusedTextColor = PrimaryTextBlack,
                                        focusedIndicatorColor = PrimaryTextBlack,
                                        unfocusedTextColor = PrimaryTextBlack,
                                        unfocusedIndicatorColor = PrimaryTextBlack,
                                        cursorColor = PrimaryTextBlack
                                    ),
                                    textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                                )
                            }
                        },
                        navigationIcon = {
                            if (iskniga || viewModel.searchText) {
                                IconButton(
                                    onClick = {
                                        if (iskniga) {
                                            if (bottomSheetScaffoldIsVisible) {
                                                coroutineScope.launch {
                                                    bottomSheetScaffoldState.bottomSheetState.show()
                                                }
                                            }
                                            iskniga = false
                                        } else {
                                            viewModel.searchText = false
                                            viewModel.searshString = TextFieldValue("")
                                            viewModel.searchTextResult = AnnotatedString("")
                                            AppNavGraphState.searchBogaslujbovyia = ""
                                        }
                                        if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
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
                                    onClick = {
                                        when {
                                            iskniga -> {
                                                coroutineScope.launch {
                                                    bottomSheetScaffoldState.bottomSheetState.show()
                                                }
                                                iskniga = false
                                                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                            }

                                            showDropdown -> {
                                                showDropdown = false
                                                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                            }

                                            else -> {
                                                if (!backPressHandled) {
                                                    backPressHandled = true
                                                    navController.popBackStack()
                                                }
                                            }
                                        }
                                    },
                                    content = {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_back),
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            contentDescription = ""
                                        )
                                    })
                            }
                        },
                        actions = {
                            if (viewModel.searchText) {
                                IconButton(onClick = {
                                    viewModel.findBack(textLayout)
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_upward),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.findForward(textLayout)
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_downward),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            } else {
                                if (!iskniga && listResource.isNotEmpty()) {
                                    IconButton(onClick = {
                                        showDropdown = false
                                        coroutineScope.launch {
                                            bottomSheetScaffoldIsVisible = !bottomSheetScaffoldIsVisible
                                            AppNavGraphState.bottomSheetScaffoldIsVisible = bottomSheetScaffoldIsVisible
                                        }
                                    }) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            painter = painterResource(R.drawable.book_red),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    if (k.getBoolean("admin", false) && isBottomBar) {
                                        VerticalDivider()
                                    }
                                }
                                if (!iskniga && !isBottomBar) {
                                    if (viewModel.scrollState.canScrollForward) {
                                        val iconAutoScroll =
                                            if (viewModel.autoScrollSensor) painterResource(R.drawable.stop_circle)
                                            else painterResource(R.drawable.play_circle)
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
                                                iconAutoScroll,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    } else if (viewModel.scrollState.canScrollBackward) {
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
                                    if (listResource.isEmpty()) {
                                        IconButton(onClick = {
                                            viewModel.saveVybranoe(context, title, resursEncode)
                                        }) {
                                            val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                            else painterResource(R.drawable.star)
                                            Icon(
                                                painter = icon,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    IconButton(onClick = { expandedUp = true }) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    AppDropdownMenu(
                                        expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                                        if (listResource.isNotEmpty()) {
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                viewModel.saveVybranoe(context, title, resursEncode)
                                            }, text = { Text(stringResource(if (viewModel.isVybranoe) R.string.vybranae_remove else R.string.vybranae_add), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                                else painterResource(R.drawable.star)
                                                Icon(
                                                    painter = icon, contentDescription = ""
                                                )
                                            })
                                        }
                                        DropdownMenuItem(onClick = {
                                            expandedUp = false
                                            viewModel.autoScroll(title, false)
                                            if (k.getBoolean("isShareHelp", true)) {
                                                dialogHelpShare = true
                                            } else {
                                                shareIsLaunch = true
                                            }
                                        }, text = { Text(stringResource(R.string.share), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.share), contentDescription = ""
                                            )
                                        })
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
                                            viewModel.searchText = true
                                        }, text = { Text(stringResource(R.string.searche_text), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.search), contentDescription = ""
                                            )
                                        })
                                        DropdownMenuItem(onClick = {
                                            expandedUp = false
                                            showDropdown = !showDropdown
                                            viewModel.autoScroll(title, false)
                                        }, text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.format_size), contentDescription = ""
                                            )
                                        })
                                        if (k.getBoolean("admin", false)) {
                                            HorizontalDivider()
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                viewModel.autoScroll(title, false)
                                                viewModel.viewModelScope.launch {
                                                    isProgressVisable = true
                                                    val fileList = SnapshotStateList<PaisochnicaFileList>()
                                                    fileList.addAll(adminViewModel.getPasochnicaFileList())
                                                    val dirToFile = if (iskniga) listResource[adminResourceEditPosition].resource
                                                    else resursEncode
                                                    Settings.bibleTime = false
                                                    adminViewModel.isHTML = dirToFile.contains(".html")
                                                    adminViewModel.history.clear()
                                                    val t1 = dirToFile.lastIndexOf("/")
                                                    val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
                                                    else dirToFile
                                                    if (adminViewModel.isFilePiasochnicaExitst(fileName, fileList)) {
                                                        coroutineScope.launch {
                                                            isProgressVisable = true
                                                            adminViewModel.getPasochnicaFile(fileName, result = { sb, text ->
                                                                adminViewModel.addHistory(sb, 0)
                                                                val html = if (adminViewModel.isHTML) {
                                                                    sb
                                                                } else {
                                                                    SpannableStringBuilder(text)
                                                                }
                                                                adminViewModel.htmlText = html
                                                                navigationActions.navigateToPiasochnica(fileName)
                                                            })
                                                        }
                                                    } else {
                                                        if (adminViewModel.findDirAsSave.isEmpty()) {
                                                            adminViewModel.getFindFileListAsSave()
                                                        }
                                                        adminViewModel.getFileCopyPostRequest(dirToFile = adminViewModel.findResoursDir(fileName), isProgressVisable = {
                                                            isProgressVisable = it
                                                        }) { text, fileName ->
                                                            val html = if (adminViewModel.isHTML) {
                                                                SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                            } else {
                                                                SpannableStringBuilder(text)
                                                            }
                                                            adminViewModel.addHistory(html, 0)
                                                            adminViewModel.htmlText = html
                                                            navigationActions.navigateToPiasochnica(fileName)
                                                            isProgressVisable = false
                                                        }
                                                    }
                                                    isProgressVisable = false
                                                }
                                            }, text = { Text(stringResource(R.string.redagaktirovat), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.edit), contentDescription = ""
                                                )
                                            })
                                        }
                                    }
                                }
                                if (k.getBoolean("admin", false) && (isBottomBar || iskniga)) {
                                    IconButton(onClick = {
                                        viewModel.autoScroll(title, false)
                                        viewModel.viewModelScope.launch {
                                            isProgressVisable = true
                                            val fileList = SnapshotStateList<PaisochnicaFileList>()
                                            fileList.addAll(adminViewModel.getPasochnicaFileList())
                                            val dirToFile = if (iskniga) listResource[adminResourceEditPosition].resource
                                            else resursEncode
                                            Settings.bibleTime = false
                                            adminViewModel.isHTML = dirToFile.contains(".html")
                                            adminViewModel.history.clear()
                                            val t1 = dirToFile.lastIndexOf("/")
                                            val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
                                            else dirToFile
                                            if (adminViewModel.isFilePiasochnicaExitst(fileName, fileList)) {
                                                coroutineScope.launch {
                                                    isProgressVisable = true
                                                    adminViewModel.getPasochnicaFile(fileName, result = { sb, text ->
                                                        adminViewModel.addHistory(sb, 0)
                                                        val html = if (adminViewModel.isHTML) {
                                                            sb
                                                        } else {
                                                            SpannableStringBuilder(text)
                                                        }
                                                        adminViewModel.htmlText = html
                                                        navigationActions.navigateToPiasochnica(fileName)
                                                    })
                                                }
                                            } else {
                                                if (adminViewModel.findDirAsSave.isEmpty()) {
                                                    adminViewModel.getFindFileListAsSave()
                                                }
                                                adminViewModel.getFileCopyPostRequest(dirToFile = adminViewModel.findResoursDir(fileName), isProgressVisable = {
                                                    isProgressVisable = it
                                                }) { text, fileName ->
                                                    val html = if (adminViewModel.isHTML) {
                                                        SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                    } else {
                                                        SpannableStringBuilder(text)
                                                    }
                                                    adminViewModel.addHistory(html, 0)
                                                    adminViewModel.htmlText = html
                                                    navigationActions.navigateToPiasochnica(fileName)
                                                    isProgressVisable = false
                                                }
                                            }
                                            isProgressVisable = false
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.edit), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
                    )
                }
            },
            bottomBar = {
                if (!viewModel.searchText) {
                    AnimatedVisibility(
                        !fullscreen, enter = fadeIn(
                            tween(
                                durationMillis = 500, easing = LinearOutSlowInEasing
                            )
                        ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                    ) {
                        if (!iskniga) {
                            if (showDropdown) {
                                ModalBottomSheet(
                                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                                    scrimColor = Color.Transparent,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                                    onDismissRequest = {
                                        showDropdown = false
                                        if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                    }
                                ) {
                                    Column {
                                        Text(
                                            stringResource(R.string.menu_font_size_app),
                                            modifier = Modifier.padding(start = 10.dp),
                                            fontStyle = FontStyle.Italic,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                        Slider(
                                            modifier = Modifier.padding(10.dp),
                                            valueRange = 18f..58f,
                                            value = fontSize,
                                            onValueChange = {
                                                k.edit {
                                                    putFloat("font_biblia", it)
                                                }
                                                fontSize = it
                                            }, colors = SliderDefaults.colors(inactiveTrackColor = Divider)
                                        )
                                    }
                                }
                            }
                            if (isBottomBar) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(interactionSource = interactionSourse, indication = null) {}
                                        .padding(top = 10.dp)
                                        .background(MaterialTheme.colorScheme.onTertiary)
                                        .navigationBarsPadding(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    IconButton(onClick = {
                                        showDropdown = !showDropdown
                                        viewModel.autoScroll(title, false)
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.format_size),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        showDropdown = false
                                        viewModel.autoScroll(title, false)
                                        if (k.getBoolean("isShareHelp", true)) {
                                            dialogHelpShare = true
                                        } else {
                                            shareIsLaunch = true
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.share),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.searchText = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.search),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        fullscreen = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.fullscreen),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.saveVybranoe(context, title, resursEncode)
                                    }) {
                                        val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                        else painterResource(R.drawable.star)
                                        Icon(
                                            painter = icon,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    if (viewModel.scrollState.canScrollForward) {
                                        val iconAutoScroll =
                                            if (viewModel.autoScrollSensor) painterResource(R.drawable.stop_circle)
                                            else painterResource(R.drawable.play_circle)
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
                                                iconAutoScroll,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    } else if (viewModel.scrollState.canScrollBackward) {
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
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(
                        innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        if (fullscreen) 0.dp else innerPadding.calculateTopPadding(),
                        innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                        0.dp
                    )
            ) {
                var isScrollRun by remember { mutableStateOf(false) }
                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            isScrollRun = true
                            AppNavGraphState.setScrollValuePosition(title, viewModel.scrollState.value)
                            return super.onPreScroll(available, source)
                        }

                        override suspend fun onPostFling(
                            consumed: Velocity,
                            available: Velocity
                        ): Velocity {
                            isScrollRun = false
                            if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                            return super.onPostFling(consumed, available)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
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
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    fullscreen = !fullscreen
                                }
                            )
                        }
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(viewModel.scrollState),
                    verticalArrangement = Arrangement.Top
                ) {
                    if (isProgressVisable) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    val padding = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp
                    if (viewModel.autoScrollSensor) {
                        HtmlText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(if (isBottomBar) 0.dp else 10.dp))
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        awaitFirstDown()
                                        do {
                                            val event = awaitPointerEvent()
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
                                        } while (event.changes.any { it.pressed })
                                    }
                                },
                            text = viewModel.htmlText,
                            title = title,
                            fontSize = fontSize.sp,
                            isLiturgia = isLiturgia && isLiturgia(data),
                            searchText = viewModel.searchTextResult,
                            scrollState = viewModel.scrollState,
                            navigateTo = { navigate ->
                                navigateTo(navigate, false)
                            },
                            textLayoutResult = { layout ->
                                textLayout = layout
                            },
                            isDialogListinner = { dialog, chastka ->
                                when (dialog) {
                                    DialogListinner.DIALOGQRCODE.name -> {
                                        dialogQrCode = true
                                    }

                                    DialogListinner.DIALOGSZTOHOVAHA.name -> {
                                        dialogSztoHovahaVisable = true
                                    }

                                    DialogListinner.DIALOGLITURGIA.name -> {
                                        chast = chastka
                                        dialogLiturgia = true
                                    }
                                }
                            }
                        )
                    } else {
                        SelectionContainer {
                            HtmlText(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(if (isBottomBar) 0.dp else 10.dp))
                                    .pointerInput(Unit) {
                                        awaitEachGesture {
                                            awaitFirstDown()
                                            do {
                                                val event = awaitPointerEvent()
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
                                            } while (event.changes.any { it.pressed })
                                        }
                                    },
                                text = viewModel.htmlText,
                                title = title,
                                fontSize = fontSize.sp,
                                isLiturgia = isLiturgia && isLiturgia(data),
                                searchText = viewModel.searchTextResult,
                                scrollState = viewModel.scrollState,
                                navigateTo = { navigate ->
                                    var skipUtran = false
                                    if (navigate == "cytanne") {
                                        if (data[9].isNotEmpty()) {
                                            var chtenie = data[9]
                                            if (isLiturgia && chtenie.contains("На ютрані", ignoreCase = true)) {
                                                val t1 = chtenie.indexOf("\n")
                                                if (t1 != -1) chtenie = chtenie.substring(t1 + 1)
                                                skipUtran = true
                                            }
                                        }
                                    }
                                    navigateTo(navigate, skipUtran)
                                },
                                textLayoutResult = { layout ->
                                    if (!viewModel.searchText) {
                                        coroutineScope.launch {
                                            viewModel.scrollState.animateScrollTo(AppNavGraphState.getScrollValuePosition(title))
                                        }
                                    }
                                    textLayout = layout
                                },
                                isDialogListinner = { dialog, chastka ->
                                    when (dialog) {
                                        DialogListinner.DIALOGQRCODE.name -> {
                                            dialogQrCode = true
                                        }

                                        DialogListinner.DIALOGSZTOHOVAHA.name -> {
                                            dialogSztoHovahaVisable = true
                                        }

                                        DialogListinner.DIALOGLITURGIA.name -> {
                                            chast = chastka
                                            dialogLiturgia = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                    if (viewModel.scrollState.lastScrolledForward && !viewModel.scrollState.canScrollForward) {
                        viewModel.autoScroll(title, false)
                        viewModel.autoScrollSensor = false
                        if (!k.getBoolean("power", false)) {
                            actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
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
                                .align(Alignment.End)
                                .padding(bottom = 10.dp, end = 10.dp)
                        ) {
                            Spacer(modifier = Modifier.padding(start = 50.dp))
                            Text(
                                text = autoScrollText,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(autoScrollTextColor)
                                    .padding(5.dp)
                                    .align(Alignment.CenterVertically),
                                color = autoScrollTextColor2,
                                fontSize = Settings.fontInterface.sp
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
                                .align(Alignment.End)
                                .padding(bottom = if (!isBottomBar || fullscreen) 10.dp else 0.dp, end = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .align(Alignment.Bottom)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.minus_auto_scroll),
                                    contentDescription = "",
                                    modifier = Modifier
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
                                                viewModel.autoScrollTextVisable = true
                                                viewModel.autoScrollSpeed(context)
                                            }
                                        }
                                )
                            }
                            Image(
                                painter = painterResource(R.drawable.plus_auto_scroll),
                                contentDescription = "",
                                modifier = Modifier
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
                                            viewModel.autoScrollTextVisable = true
                                            viewModel.autoScrollSpeed(context)
                                        }
                                    }
                            )
                        }
                    }
                }
            }
            if (iskniga) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            0.dp,
                            innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                            0.dp
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollStateDop)
                ) {
                    HtmlText(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = innerPadding.calculateTopPadding().plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(10.dp)),
                        text = subText,
                        fontSize = fontSize.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DialogLiturgia(
    chast: Int,
    onDismiss: () -> Unit,
) {
    val context = LocalActivity.current as MainActivity
    var title by remember { mutableStateOf("") }
    var item by remember { mutableStateOf("") }
    var filename = "bogashlugbovya/bogashlugbovya1_1.html"
    when (chast) {
        1 -> {
            filename = "bogashlugbovya/bogashlugbovya1_1.html"
            title = stringResource(R.string.ps_102)
        }

        2 -> {
            filename = "bogashlugbovya/bogashlugbovya1_2.html"
            title = stringResource(R.string.ps_91)
        }

        3 -> {
            filename = "bogashlugbovya/bogashlugbovya1_3.html"
            title = stringResource(R.string.ps_145)
        }

        4 -> {
            filename = "bogashlugbovya/bogashlugbovya1_4.html"
            title = stringResource(R.string.ps_92)
        }

        5 -> {
            filename = "bogashlugbovya/bogashlugbovya1_5.html"
            title = stringResource(R.string.mc_5_3_12)
        }

        6 -> {
            filename = "bogashlugbovya/bogashlugbovya1_6.html"
            title = stringResource(R.string.malitva_za_pamerlyx)
        }

        7 -> {
            filename = "bogashlugbovya/bogashlugbovya1_7.html"
            title = stringResource(R.string.malitva_za_paclicanyx)
        }

        10 -> {
            filename = "bogashlugbovya/bogashlugbovya1_8.html"
            title = stringResource(R.string.ps_94)
        }

        11 -> {
            filename = "bogashlugbovya/viaczernia_bierascie_1.html"
            title = stringResource(R.string.viaczernia_bierascie_1)
        }

        13 -> {
            filename = "bogashlugbovya/viaczernia_bierascie_3.html"
            title = stringResource(R.string.viaczernia_bierascie_3)
        }

        14 -> {
            filename = "bogashlugbovya/bogashlugbovya1_9.html"
            title = stringResource(R.string.malitva_za_paclicanyx_i_jyvyx)
        }
    }
    item = openAssetsResources(context, filename)
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = title.uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f, false)
                        .verticalScroll(rememberScrollState())
                ) {
                    HtmlText(text = item, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
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
fun DialogHelpShare(onDismiss: (Boolean) -> Unit) {
    var isCheck by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDismiss(isCheck) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.share).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(text = stringResource(R.string.share_help), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCheck, onCheckedChange = {
                        isCheck = !isCheck
                    })
                    Text(text = stringResource(R.string.not_show), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss(isCheck) }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

suspend fun findAllAsanc(text: String, search: String): ArrayList<ArrayList<Int>> {
    val result = withContext(Dispatchers.Main) {
        return@withContext findAll(text, search.trim())
    }
    return result
}

fun findChars(text: String, searchChars: String): ArrayList<FindString> {
    var strSub = 0
    val list = searchChars.toCharArray()
    val stringBuilder = StringBuilder()
    val result = ArrayList<FindString>()
    while (true) {
        val strSub1Pos = text.indexOf(list[0].toString(), strSub, true)
        if (strSub1Pos != -1) {
            stringBuilder.clear()
            strSub = strSub1Pos + 1
            val subChar2 = StringBuilder()
            for (i in 1 until list.size) {
                if (text.length >= strSub + 1) {
                    if (list[i].isLetterOrDigit()) {
                        var subChar = text.substring(strSub, strSub + 1)
                        if (subChar == "́") {
                            stringBuilder.append(subChar)
                            strSub++
                            if (text.length >= strSub + 1) {
                                subChar = text.substring(strSub, strSub + 1)
                            }
                        }
                        val strSub2Pos = subChar.indexOf(list[i], ignoreCase = true)
                        if (strSub2Pos != -1) {
                            if (stringBuilder.isEmpty()) stringBuilder.append(text.substring(strSub1Pos, strSub1Pos + 1))
                            if (subChar2.isNotEmpty()) stringBuilder.append(subChar2.toString())
                            stringBuilder.append(list[i].toString())
                            subChar2.clear()
                            strSub++
                        } else {
                            stringBuilder.clear()
                            break
                        }
                    } else {
                        while (true) {
                            if (text.length >= strSub + 1) {
                                val subChar = text.substring(strSub, strSub + 1).toCharArray()
                                if (!subChar[0].isLetterOrDigit()) {
                                    subChar2.append(subChar[0])
                                    strSub++
                                } else {
                                    break
                                }
                            } else {
                                break
                            }
                        }
                        if (subChar2.isEmpty()) {
                            stringBuilder.clear()
                            break
                        }
                    }
                } else {
                    stringBuilder.clear()
                    break
                }
            }
            if (stringBuilder.toString().isNotEmpty()) {
                result.add(FindString(stringBuilder.toString(), strSub))
            }
        } else {
            break
        }
    }
    return result
}

fun findAll(search: String, searchChars: String): ArrayList<ArrayList<Int>> {
    val findList = ArrayList<ArrayList<Int>>()
    val arraySearsh = ArrayList<FindString>()
    if (searchChars.length >= 3) {
        val findString = findChars(search, searchChars)
        if (findString.isNotEmpty()) arraySearsh.addAll(findString)
        for (i in 0 until arraySearsh.size) {
            val searchLig = arraySearsh[i].str.length
            val strPosition = arraySearsh[i].position - searchLig
            if (strPosition != -1) {
                val list = ArrayList<Int>()
                for (e in strPosition..strPosition + searchLig) {
                    list.add(e)
                }
                findList.add(list)
            }
        }
    }
    return findList
}

fun isLiturgia(dataDayList: ArrayList<String>): Boolean {
    val dayIfYear = dataDayList[24].toInt()
    val dayInPasha = dataDayList[22].toInt()
    val dayOfNedel = dataDayList[0].toInt()
    return when {
        dayIfYear == 85 -> true
        dayInPasha == -53 -> false
        dayInPasha == -51 -> false
        dayInPasha == -2 -> false
        dayInPasha in -48..-4 -> dayOfNedel == Calendar.SATURDAY || dayOfNedel == Calendar.SUNDAY
        else -> true
    }
}

data class VybranaeDataAll(
    val id: Long,
    val title: String,
    val resource: String,
)