package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Button
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.findCaliandarToDay
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Bogaslujbovyia(
    navController: NavHostController, title: String, resurs: String,
    navigateTo: (String, skipUtran: Boolean) -> Unit = { _, _ -> }
) {
    val resursEncode = URLDecoder.decode(resurs, "UTF8")
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    val vybranoeList = remember { ArrayList<VybranaeDataAll>() }
    var initVybranoe by remember { mutableStateOf(true) }
    var showDropdown by remember { mutableStateOf(false) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var isVybranoe by remember { mutableStateOf(false) }
    var autoScroll by rememberSaveable { mutableStateOf(false) }
    var autoScrollSensor by rememberSaveable { mutableStateOf(false) }
    var autoScrollSpeed by remember { mutableIntStateOf(k.getInt("autoscrollSpid", 60)) }
    var autoScrollTextVisable by remember { mutableStateOf(false) }
    var autoScrollText by remember { mutableStateOf("") }
    var autoScrollTextColor by remember { mutableStateOf(Primary) }
    var autoScrollTextColor2 by remember { mutableStateOf(PrimaryTextBlack) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var saveVybranoe by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var searshString by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val scrollStateDop = rememberScrollState()
    val gson = Gson()
    val type =
        TypeToken.getParameterized(ArrayList::class.java, VybranaeDataAll::class.java).type
    val file = File("${LocalContext.current.filesDir}/vybranoe_all.json")
    if (initVybranoe) {
        vybranoeList.clear()
        if (file.exists()) {
            vybranoeList.addAll(gson.fromJson(file.readText(), type))
        }
        initVybranoe = false
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isVybranoe = false
            if (vybranoeList.isNotEmpty()) {
                for (i in 0 until vybranoeList.size) {
                    if (resursEncode == vybranoeList[i].resource) {
                        isVybranoe = true
                        break
                    }
                }
            }
        }
    }
    LaunchedEffect(saveVybranoe) {
        if (saveVybranoe) {
            if (isVybranoe) {
                var pos = 0
                for (i in 0 until vybranoeList.size) {
                    if (resursEncode == vybranoeList[i].resource) {
                        pos = i
                        break
                    }
                }
                vybranoeList.removeAt(pos)
                Toast.makeText(context, context.getString(R.string.removeVybranoe), Toast.LENGTH_SHORT).show()
            } else {
                vybranoeList.add(
                    0,
                    VybranaeDataAll(
                        Calendar.getInstance().timeInMillis,
                        title,
                        resursEncode,
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
    }
    LaunchedEffect(autoScroll) {
        if (autoScroll) {
            autoScrollJob?.cancel()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    while (true) {
                        delay(autoScrollSpeed.toLong())
                        scrollState.scrollBy(2f)
                        AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                    }
                }
            }
        } else {
            autoScrollJob?.cancel()
        }
    }
    var searchTextResult by remember { mutableStateOf(AnnotatedString("")) }
    var backPressHandled by remember { mutableStateOf(false) }
    var iskniga by remember { mutableStateOf(false) }
    var bottomSheetScaffoldIsVisible by remember { mutableStateOf(AppNavGraphState.bottomSheetScaffoldIsVisible) }
    var isWebViewVisible by remember { mutableStateOf(false) }
    val actyvity = LocalActivity.current as MainActivity
    if (autoScrollSensor) {
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
    BackHandler(!backPressHandled || showDropdown || iskniga || searchText) {
        when {
            searchText -> {
                searchText = false
                searchTextResult = AnnotatedString("")
            }

            iskniga -> {
                if (bottomSheetScaffoldIsVisible) {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.show()
                    }
                }
                if (autoScrollSensor) autoScroll = true
                iskniga = false
            }

            showDropdown -> {
                showDropdown = false
                if (autoScrollSensor) autoScroll = true
            }

            !backPressHandled -> {
                fullscreen = false
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
                scrollState.scrollTo(0)
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
    var htmlText by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        htmlText = openAssetsResources(context, resursEncode)
    }
    var findBack by remember { mutableStateOf(false) }
    var findForward by remember { mutableStateOf(false) }
    var subTitle by remember { mutableStateOf("") }
    var subText by remember { mutableStateOf("") }
    val result = remember { mutableStateListOf<ArrayList<Int>>() }
    var resultPosition by remember { mutableIntStateOf(0) }
    val textLayout = remember { mutableStateOf<TextLayoutResult?>(null) }
    LaunchedEffect(searshString) {
        if (searshString.trim().length >= 3) {
            if (searchJob?.isActive == true) {
                searchJob?.cancel()
            }
            textLayout.value?.let { layout ->
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    result.clear()
                    resultPosition = 0
                    result.addAll(findAllAsanc(layout.layoutInput.text.text, searshString))
                    textLayout.value?.let { layout ->
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
                                val y = layout.getLineTop(line)
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(y.toInt())
                                }
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
    LaunchedEffect(findForward) {
        textLayout.value?.let { layout ->
            if (findForward) {
                if (result.isNotEmpty()) {
                    if (result.size - 1 > resultPosition) {
                        resultPosition += 1
                    }
                    val t1 = result[resultPosition][0]
                    if (t1 != -1) {
                        val line = layout.getLineForOffset(t1)
                        val y = layout.getLineTop(line)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(y.toInt())
                        }
                        findForward = false
                    }
                }
            }
        }
    }
    LaunchedEffect(findBack) {
        textLayout.value?.let { layout ->
            if (findBack) {
                if (result.isNotEmpty()) {
                    if (resultPosition > 0) {
                        resultPosition -= 1
                    }
                    val t1 = result[resultPosition][0]
                    if (t1 != -1) {
                        val line = layout.getLineForOffset(t1)
                        val y = layout.getLineTop(line)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(y.toInt())
                        }
                        findBack = false
                    }
                }
            }
        }
    }
    var isDialogNoWIFIVisable by remember { mutableStateOf(false) }
    var printFile by remember { mutableStateOf("") }
    if (isDialogNoWIFIVisable) {
        DialogNoWiFI(
            onDismiss = {
                isWebViewVisible = true
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
    if (isWebViewVisible) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    this.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.loadDataWithBaseURL(null, htmlText, "text/HTML", "UTF-8", null)
                    this.webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            val printAdapter = view.createPrintDocumentAdapter(title)
                            val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                            printManager.print(htmlText, printAdapter, printAttributes)
                            isWebViewVisible = false
                        }
                    }
                }
            }
        )
    }
    val isViachernia = resursEncode == "bogashlugbovya/viaczernia_niadzelnaja.html" || resursEncode == "bogashlugbovya/viaczernia_na_kozny_dzen.html" || resursEncode == "bogashlugbovya/viaczernia_u_vialikim_poscie.html" || resursEncode == "bogashlugbovya/viaczerniaja_sluzba_sztodzionnaja_biez_sviatara.html" || resursEncode == "bogashlugbovya/viaczernia_svietly_tydzien.html"
    val isUtran = resursEncode == "bogashlugbovya/jutran_niadzelnaja.html"
    val isLiturgia = resursEncode == "bogashlugbovya/lit_jana_zalatavusnaha.html" || resursEncode == "bogashlugbovya/lit_jan_zalat_vielikodn.html" || resursEncode == "bogashlugbovya/lit_vasila_vialikaha.html" || resursEncode == "bogashlugbovya/abiednica.html" || resursEncode == "bogashlugbovya/vialikdzien_liturhija.html"
    val dataCal = findCaliandarToDay(context)
    val isNoLiturgia = dataCal[22].toInt() == -53 || dataCal[22].toInt() == -51 || (dataCal[22].toInt() in -48..-2 && !(dataCal[0].toInt() == Calendar.SATURDAY || dataCal[0].toInt() == Calendar.SUNDAY))
    val cytanneVisable = (isLiturgia && !isNoLiturgia) || (isViachernia && isNoLiturgia) || isUtran
    val data = findCaliandarToDay(context)
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
            coroutineScope.launch {
                if (bottomSheetScaffoldIsVisible) {
                    bottomSheetScaffoldState.bottomSheetState.show()
                } else {
                    bottomSheetScaffoldState.bottomSheetState.hide()
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
                                    subTitle = listResource[i].title
                                    subText = openAssetsResources(context, listResource[i].resource)
                                    iskniga = true
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.hide()
                                    }
                                    autoScroll = false
                                    autoScrollSensor = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
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
                        val chytanneList = ArrayList<BogaslujbovyiaListData>()
                        if (data[9].isNotEmpty()) {
                            var chtenie = data[9]
                            if (isLiturgia && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.substring(t1 + 1)
                                skipUtran = true
                            }
                            if (isUtran && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.substring(0, t1)
                            }
                            chytanneList.add(BogaslujbovyiaListData(chtenie, "9"))
                        }
                        if (data[10].isNotEmpty()) {
                            chytanneList.add(BogaslujbovyiaListData(data[10], "10"))
                        }
                        if (data[11].isNotEmpty()) {
                            chytanneList.add(BogaslujbovyiaListData(data[11], "11"))
                        }
                        for (i in chytanneList.indices) {
                            val navigate = when (chytanneList[i].resurs) {
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
                                        autoScroll = false
                                        autoScrollSensor = false
                                        navigateTo(navigate, skipUtran)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp, 5.dp),
                                    painter = painterResource(R.drawable.poiter),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.padding(start = 10.dp),
                                    text = chytanneList[i].title,
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
                            if (!searchText) {
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
                                    value = searshString,
                                    onValueChange = { newText ->
                                        var edit = newText
                                        edit = edit.replace("и", "і")
                                        edit = edit.replace("щ", "ў")
                                        edit = edit.replace("И", "І")
                                        edit = edit.replace("Щ", "Ў")
                                        edit = edit.replace("ъ", "'")
                                        searchTextResult = AnnotatedString("")
                                        searshString = edit
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
                                        if (searchTextResult.isNotEmpty()) {
                                            IconButton(onClick = {
                                                searshString = ""
                                            }) {
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
                                        unfocusedTextColor = PrimaryTextBlack,
                                        unfocusedIndicatorColor = PrimaryTextBlack,
                                        cursorColor = PrimaryTextBlack
                                    ),
                                    textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                                )
                            }
                        },
                        navigationIcon = {
                            if (iskniga || searchText) {
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
                                            searchText = false
                                            searchTextResult = AnnotatedString("")
                                        }
                                        if (autoScrollSensor) autoScroll = true
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
                                                if (autoScrollSensor) autoScroll = true
                                            }

                                            showDropdown -> {
                                                showDropdown = false
                                                if (autoScrollSensor) autoScroll = true
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
                            if (searchText) {
                                IconButton(onClick = {
                                    findBack = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_upward),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                IconButton(onClick = {
                                    findForward = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_downward),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            } else {
                                if (listResource.isNotEmpty()) {
                                    IconButton(onClick = {
                                        showDropdown = false
                                        coroutineScope.launch {
                                            bottomSheetScaffoldIsVisible = !bottomSheetScaffoldIsVisible
                                            AppNavGraphState.bottomSheetScaffoldIsVisible = bottomSheetScaffoldIsVisible
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.book_red),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
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
                if (!searchText) {
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
                                    properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                                    onDismissRequest = {
                                        showDropdown = false
                                        if (autoScrollSensor) autoScroll = true
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
                                            steps = 10,
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
                            BottomAppBar(containerColor = MaterialTheme.colorScheme.onTertiary) {
                                var expanded by remember { mutableStateOf(false) }
                                val launcherShare = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                    if (autoScrollSensor) autoScroll = true
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    if (k.getBoolean("admin", false)) {
                                        DropdownMenuItem(
                                            onClick = {
                                                showDropdown = false
                                                autoScroll = false
                                                expanded = false
                                                if ((context as MainActivity).checkmodulesAdmin()) {
                                                    val intent = Intent()
                                                    intent.setClassName(context, "by.carkva_gazeta.admin.PasochnicaList")
                                                    val t1 = resursEncode.lastIndexOf("/")
                                                    val t2 = resursEncode.lastIndexOf(".")
                                                    val resursAdmin = if (t1 != -1) {
                                                        if (t2 != -1) resursEncode.substring(t1 + 1, t2)
                                                        else resursEncode.substring(t1 + 1)
                                                    } else {
                                                        if (t2 != -1) resursEncode.substring(0, t2)
                                                        else resursEncode
                                                    }
                                                    intent.putExtra("resours", resursAdmin)
                                                    intent.putExtra("title", title)
                                                    intent.putExtra("text", htmlText.replace("#ff6666", "#d00505", true))
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
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropdown = false
                                            autoScroll = false
                                            expanded = false
                                            val slugbovyiaTextu = SlugbovyiaTextu()
                                            var res = slugbovyiaTextu.getTydzen1()
                                            res.forEach {
                                                if (resursEncode == it.resource) printFile = "Tydzien-1 VP_2012.pdf"
                                            }
                                            res = slugbovyiaTextu.getTydzen2()
                                            res.forEach {
                                                if (resursEncode == it.resource) printFile = "Tydzien-2 VP_2012.pdf"
                                            }
                                            res = slugbovyiaTextu.getTydzen3()
                                            res.forEach {
                                                if (resursEncode == it.resource) printFile = "Tydzien-3 VP_2014.pdf"
                                            }
                                            res = slugbovyiaTextu.getTydzen4()
                                            res.forEach {
                                                if (resursEncode == it.resource) printFile = "Tydzien-4 VP_2014.pdf"
                                            }
                                            res = slugbovyiaTextu.getTydzen5()
                                            res.forEach {
                                                if (resursEncode == it.resource) printFile = "Tydzien-5 VP_2015.pdf"
                                            }
                                            res = slugbovyiaTextu.getTydzen6()
                                            res.forEach {
                                                if (resursEncode == it.resource) printFile = "Tydzien-6 VP_2015.pdf"
                                            }
                                            if (resursEncode == "bogashlugbovya/lit_jana_zalatavusnaha.html") printFile = "LITURGIJA Jana Zlt.pdf"
                                            if (resursEncode == "bogashlugbovya/kanon_andreja_kryckaha.html") printFile = "Kanon_A-Kryckaha.pdf"
                                            if (resursEncode == "bogashlugbovya/akafist4.html") printFile = "Akafist-Padl-muczanikam.pdf"
                                            if (resursEncode == "bogashlugbovya/akafist6.html") printFile = "Akafist da Ducha Sviatoha.pdf"
                                            if (resursEncode == "bogashlugbovya/vialikaja_piatnica_jutran_12jevanhellau.html") printFile = "Vial-Piatnica-jutran-12-Evang.pdf"
                                            if (printFile.isNotEmpty()) {
                                                if (fileExistsBiblijateka(context, printFile)) {
                                                    val printAdapter = PdfDocumentAdapter(context, printFile)
                                                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                                                    val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                                                    printManager.print(printFile, printAdapter, printAttributes)
                                                } else {
                                                    if (Settings.isNetworkAvailable(
                                                            context,
                                                            Settings.TRANSPORT_CELLULAR
                                                        )
                                                    ) isDialogNoWIFIVisable = true
                                                    else {
                                                        writeFile(
                                                            context, printFile, loadComplete = {
                                                                val printAdapter = PdfDocumentAdapter(context, printFile)
                                                                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                                                                val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                                                                printManager.print(printFile, printAdapter, printAttributes)
                                                            },
                                                            inProcess = {
                                                            })
                                                    }
                                                }
                                            } else {
                                                isWebViewVisible = true
                                            }
                                        },
                                        text = { Text(stringResource(R.string.print), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.print),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropdown = !showDropdown
                                            autoScroll = false
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.format_size),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropdown = false
                                            autoScroll = false
                                            expanded = false
                                            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                            val isTextFound = textLayout.value?.layoutInput?.text?.text?.contains(clipboard.primaryClip?.getItemAt(0)?.text ?: "@#$") == true
                                            val sent = if (isTextFound) clipboard.primaryClip?.getItemAt(0)?.text
                                            else textLayout.value?.layoutInput?.text?.text
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
                                        },
                                        text = { Text(stringResource(R.string.share), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.share),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    IconButton(
                                        onClick = { expanded = true }) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        expanded = false
                                        if (autoScrollSensor) autoScroll = true
                                        searchText = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.search),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        expanded = false
                                        if (autoScrollSensor) autoScroll = true
                                        fullscreen = true
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.fullscreen),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        expanded = false
                                        saveVybranoe = true
                                    }) {
                                        val icon = if (isVybranoe) painterResource(R.drawable.stars)
                                        else painterResource(R.drawable.star)
                                        Icon(
                                            painter = icon,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    if (scrollState.canScrollForward) {
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
                                    } else if (scrollState.canScrollBackward) {
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
                            AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            android.util.Log.d("Oleg", scrollState.value.toString())
                            return super.onPreScroll(available, source)
                        }

                        override suspend fun onPostFling(
                            consumed: Velocity,
                            available: Velocity
                        ): Velocity {
                            isScrollRun = false
                            if (autoScrollSensor) autoScroll = true
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
                                        autoScroll = false
                                    }
                                    if (autoScrollSensor && event.type == PointerEventType.Release && !isScrollRun) {
                                        autoScroll = true
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
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Top
                ) {
                    val padding = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp
                    if (autoScrollSensor) {
                        HtmlText(
                            modifier = Modifier.padding(top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(10.dp)),
                            text = htmlText,
                            fontSize = fontSize.sp,
                            isNoLiturgia = isLiturgia && !isNoLiturgia,
                            searchText = searchTextResult,
                            scrollState = scrollState,
                            navigateTo = { navigate ->
                                navigateTo(navigate, false)
                            },
                            textLayoutResult = { layout ->
                                textLayout.value = layout
                            }
                        )
                    } else {
                        SelectionContainer {
                            HtmlText(
                                modifier = Modifier.padding(top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(10.dp)),
                                text = htmlText,
                                fontSize = fontSize.sp,
                                isNoLiturgia = isLiturgia && !isNoLiturgia,
                                searchText = searchTextResult,
                                scrollState = scrollState,
                                navigateTo = { navigate ->
                                    navigateTo(navigate, false)
                                },
                                textLayoutResult = { layout ->
                                    coroutineScope.launch {
                                        scrollState.scrollTo(AppNavGraphState.getScrollValuePosition(title))
                                    }
                                    textLayout.value = layout
                                }
                            )
                        }
                    }
                    if (scrollState.lastScrolledForward && !scrollState.canScrollForward) {
                        autoScroll = false
                        autoScrollSensor = false
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
                                    painter = painterResource(R.drawable.minus_auto_scroll),
                                    contentDescription = "",
                                    modifier = Modifier
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
                                                autoScrollTextVisableJob =
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        delay(3000)
                                                        autoScrollTextVisable = false
                                                    }
                                                k.edit {
                                                    putInt("autoscrollSpid", autoScrollSpeed)
                                                }
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
                                        if (autoScrollSpeed in 20..135) {
                                            autoScrollSpeed -= 5
                                            val proc = 100 - (autoScrollSpeed - 15) * 100 / 115
                                            autoScrollTextColor = Primary
                                            autoScrollTextColor2 = PrimaryTextBlack
                                            autoScrollText = "$proc%"
                                            autoScrollTextVisable = true
                                            autoScrollTextVisableJob?.cancel()
                                            autoScrollTextVisableJob =
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    delay(3000)
                                                    autoScrollTextVisable = false
                                                }
                                            k.edit {
                                                putInt("autoscrollSpid", autoScrollSpeed)
                                            }
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
            Column {
                Text(
                    text = title.uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
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

data class VybranaeDataAll(
    val id: Long,
    val title: String,
    val resource: String,
)