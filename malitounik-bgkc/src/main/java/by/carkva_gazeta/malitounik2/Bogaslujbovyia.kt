package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.BezPosta
import by.carkva_gazeta.malitounik2.ui.theme.Button
import by.carkva_gazeta.malitounik2.ui.theme.Post
import by.carkva_gazeta.malitounik2.ui.theme.Primary
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik2.views.HtmlText
import by.carkva_gazeta.malitounik2.views.findCaliandarToDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bogaslujbovyia(
    navController: NavHostController, title: String, resurs: Int,
    navigateTo: (String) -> Unit = {}
) {
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
    var menuPosition by remember { mutableIntStateOf(0) }
    var saveVybranoe by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var searshString by rememberSaveable { mutableStateOf("") }
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = searshString,
                selection = TextRange("".length)
            )
        )
    }
    var modeNight by remember {
        mutableIntStateOf(
            k.getInt(
                "mode_night",
                Settings.MODE_NIGHT_SYSTEM
            )
        )
    }
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
    val resursText = context.resources.getResourceEntryName(resurs)
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isVybranoe = false
            if (vybranoeList.isNotEmpty()) {
                for (i in 0 until vybranoeList.size) {
                    if (resursText == vybranoeList[i].resource) {
                        isVybranoe = true
                        break
                    }
                }
            }
        }
    }
    if (saveVybranoe) {
        if (isVybranoe) {
            var pos = 0
            for (i in 0 until vybranoeList.size) {
                if (resursText == vybranoeList[i].resource) {
                    pos = i
                    break
                }
            }
            vybranoeList.removeAt(pos)
            isVybranoe = false
        } else {
            vybranoeList.add(
                0,
                VybranaeDataAll(
                    Calendar.getInstance().timeInMillis,
                    title,
                    resursText,
                )
            )
        }
        if (vybranoeList.isEmpty() && file.exists()) {
            file.delete()
            isVybranoe = false
        } else {
            file.writer().use {
                it.write(gson.toJson(vybranoeList, type))
            }
            isVybranoe = true
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
                        scrollState.scrollBy(2f)
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
    val actyvity = LocalActivity.current as MainActivity
    if (autoScrollSensor) {
        actyvity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    BackHandler(!backPressHandled || fullscreen || showDropdown || iskniga || searchText) {
        when {
            fullscreen -> fullscreen = false
            searchText -> {
                searchText = false
                searchTextResult = AnnotatedString("")
            }
            iskniga -> {
                showDropdown = true
                iskniga = false
            }

            showDropdown -> {
                showDropdown = false
                if (autoScrollSensor) autoScroll = true
            }

            !backPressHandled -> {
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
        ).isAppearanceLightStatusBars = false
    }
    val maxLine = remember { mutableIntStateOf(1) }
    var htmlText by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val inputStream = context.resources.openRawResource(resurs)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        htmlText = reader.readText()
    }
    var findBack by remember { mutableStateOf(false) }
    var findForward by remember { mutableStateOf(false) }
    var subTitle by remember { mutableStateOf("") }
    var subText by remember { mutableStateOf("") }
    val result = remember { mutableStateListOf<ArrayList<Int>>() }
    var resultPosition by remember { mutableIntStateOf(0) }
    val textLayout = remember { mutableStateOf<TextLayoutResult?>(null) }
    LaunchedEffect(textFieldValueState.text, searshString) {
        if (textFieldValueState.text.trim().length >= 3) {
            if (searchJob?.isActive == true) {
                searchJob?.cancel()
            }
            textLayout.value?.let { layout ->
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    result.clear()
                    result.addAll(findAllAsanc(layout.layoutInput.text.text, textFieldValueState.text))
                    textLayout.value?.let { layout ->
                        if (result.isNotEmpty()) {
                            searchTextResult = AnnotatedString("")
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
                        }
                    }
                }
            }
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
    Scaffold(
        topBar = {
            if (!fullscreen) {
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
                                text = if (iskniga) subTitle else title,
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
                                    searshString = textFieldValueState.text
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
                                        IconButton(onClick = {
                                            textFieldValueState = TextFieldValue("", TextRange("".length))
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
                                    unfocusedIndicatorColor = PrimaryTextBlack,
                                    cursorColor = PrimaryTextBlack
                                ),
                                textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                            )
                        }
                    },
                    navigationIcon = {
                        if (iskniga || searchText) {
                            IconButton(onClick = {
                                if (iskniga) {
                                    iskniga = false
                                    showDropdown = true
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
                            IconButton(onClick = {
                                when {
                                    fullscreen -> fullscreen = false
                                    iskniga -> {
                                        showDropdown = true
                                        iskniga = false
                                    }

                                    showDropdown -> {
                                        showDropdown = false
                                        if (autoScrollSensor) autoScroll = true
                                    }

                                    else -> {
                                        navController.popBackStack()
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
                        if (!searchText) {
                            if (!iskniga) {
                                var expanded by remember { mutableStateOf(false) }
                                if (resurs == R.raw.lit_jana_zalatavusnaha || resurs == R.raw.lit_jan_zalat_vielikodn || resurs == R.raw.lit_vasila_vialikaha || resurs == R.raw.abiednica || resurs == R.raw.vialikdzien_liturhija || resurs == R.raw.viaczernia_niadzelnaja || resurs == R.raw.viaczernia_na_kozny_dzen || resurs == R.raw.viaczernia_u_vialikim_poscie || resurs == R.raw.viaczerniaja_sluzba_sztodzionnaja_biez_sviatara || resurs == R.raw.viaczernia_svietly_tydzien || resurs == R.raw.jutran_niadzelnaja) {
                                    IconButton(onClick = {
                                        showDropdown = true
                                        menuPosition = 2
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.book_red),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
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
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.more_vert),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            if (autoScrollSensor) autoScroll = true
                                            searchText = true
                                        },
                                        text = { Text(stringResource(R.string.searche_text), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.search),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            saveVybranoe = true
                                        },
                                        text = {
                                            if (isVybranoe) Text(stringResource(R.string.vybranoe_del), fontSize = (Settings.fontInterface - 2).sp)
                                            else Text(stringResource(R.string.vybranoe), fontSize = (Settings.fontInterface - 2).sp)
                                        },
                                        trailingIcon = {
                                            val icon = if (isVybranoe) painterResource(R.drawable.stars)
                                            else painterResource(R.drawable.star)
                                            Icon(
                                                painter = icon,
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            if (autoScrollSensor) autoScroll = true
                                            fullscreen = true
                                        },
                                        text = { Text(stringResource(R.string.fullscreen), fontSize = (Settings.fontInterface - 2).sp) },
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.fullscreen),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropdown = !showDropdown
                                            autoScroll = false
                                            expanded = false
                                            menuPosition = 1
                                        },
                                        text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            showDropdown = !showDropdown
                                            autoScroll = false
                                            expanded = false
                                            menuPosition = 3
                                        },
                                        text = { Text(stringResource(R.string.dzen_noch), fontSize = (Settings.fontInterface - 2).sp) }
                                    )
                                }
                            }
                        } else {
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
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
                )
            }
        }, modifier = Modifier
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
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = {
                    if (menuPosition != 2) {
                        showDropdown = false
                        if (autoScrollSensor) autoScroll = true
                    }
                }
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                shape = RoundedCornerShape(
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.onTertiary)
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                            .background(MaterialTheme.colorScheme.tertiary)
                    ) {
                        Column {
                            if (menuPosition == 2) {
                                Column {
                                    val data = findCaliandarToDay(context)
                                    val listResource = ArrayList<SlugbovyiaTextuData>()
                                    when (resurs) {
                                        R.raw.lit_jana_zalatavusnaha, R.raw.lit_jan_zalat_vielikodn, R.raw.lit_vasila_vialikaha, R.raw.abiednica -> {
                                            if (data[0].toInt() == Calendar.SUNDAY) {
                                                if (data[20] != "0") {
                                                    val trapary = getTraparyKandakiNiadzelnyia()
                                                    listResource.add(SlugbovyiaTextuData(0, trapary[data[20].toInt() - 1].title, trapary[data[20].toInt() - 1].resurs, SlugbovyiaTextu.LITURHIJA))
                                                }
                                            } else {
                                                val trapary = getTraparyKandakiShtodzennyia()
                                                listResource.add(SlugbovyiaTextuData(0, trapary[data[0].toInt() - 2].title.replace("\n", ": "), trapary[data[0].toInt() - 2].resurs, SlugbovyiaTextu.LITURHIJA))
                                            }
                                            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.LITURHIJA, data[24].toInt(), data[3].toInt()))
                                        }

                                        R.raw.jutran_niadzelnaja -> {
                                            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.JUTRAN, data[24].toInt(), data[3].toInt()))
                                        }

                                        else -> {
                                            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.VIACZERNIA, data[24].toInt(), data[3].toInt()))
                                        }
                                    }
                                    for (i in listResource.indices) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .clickable {
                                                    subTitle = listResource[i].title
                                                    val inputStream = context.resources.openRawResource(listResource[i].resource)
                                                    val isr = InputStreamReader(inputStream)
                                                    val reader = BufferedReader(isr)
                                                    subText = reader.readText()
                                                    iskniga = true
                                                    showDropdown = false
                                                    autoScroll = false
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(12.dp, 12.dp),
                                                painter = painterResource(R.drawable.krest),
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
                                    val chytanneList = ArrayList<BogaslujbovyiaListData>()
                                    if (data[9].isNotEmpty()) {
                                        chytanneList.add(BogaslujbovyiaListData(data[9], 9))
                                    }
                                    if (data[10].isNotEmpty()) {
                                        chytanneList.add(BogaslujbovyiaListData(data[10], 10))
                                    }
                                    if (data[11].isNotEmpty()) {
                                        chytanneList.add(BogaslujbovyiaListData(data[11], 11))
                                    }
                                    for (i in chytanneList.indices) {
                                        val navigate = when (chytanneList[i].resurs) {
                                            10 -> "cytannesvityx"
                                            11 -> "cytannedop"
                                            else -> "cytanne"
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .clickable {
                                                    showDropdown = false
                                                    autoScroll = false
                                                    navigateTo(navigate)
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(12.dp, 12.dp),
                                                painter = painterResource(R.drawable.krest),
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
                            if (menuPosition == 3) {
                                Column(Modifier.selectableGroup())
                                {
                                    val isSystemInDarkTheme = isSystemInDarkTheme()
                                    Text(
                                        stringResource(R.string.dzen_noch),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                        textAlign = TextAlign.Center,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                    val edit = k.edit()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                modeNight = Settings.MODE_NIGHT_SYSTEM
                                                edit.putInt(
                                                    "mode_night",
                                                    Settings.MODE_NIGHT_SYSTEM
                                                )
                                                edit.apply()
                                                actyvity.dzenNoch = isSystemInDarkTheme
                                                if (actyvity.dzenNoch != actyvity.checkDzenNoch)
                                                    actyvity.recreate()
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = modeNight == Settings.MODE_NIGHT_SYSTEM,
                                            onClick = {
                                                modeNight = Settings.MODE_NIGHT_SYSTEM
                                                edit.putInt(
                                                    "mode_night",
                                                    Settings.MODE_NIGHT_SYSTEM
                                                )
                                                edit.apply()
                                                actyvity.dzenNoch = isSystemInDarkTheme
                                                if (actyvity.dzenNoch != actyvity.checkDzenNoch)
                                                    actyvity.recreate()
                                            }
                                        )
                                        Text(
                                            stringResource(R.string.system),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                modeNight = Settings.MODE_NIGHT_NO
                                                edit.putInt("mode_night", Settings.MODE_NIGHT_NO)
                                                edit.apply()
                                                actyvity.dzenNoch = false
                                                if (actyvity.checkDzenNoch)
                                                    actyvity.recreate()
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = modeNight == Settings.MODE_NIGHT_NO,
                                            onClick = {
                                                modeNight = Settings.MODE_NIGHT_NO
                                                edit.putInt("mode_night", Settings.MODE_NIGHT_NO)
                                                edit.apply()
                                                actyvity.dzenNoch = false
                                                if (actyvity.checkDzenNoch)
                                                    actyvity.recreate()
                                            }
                                        )
                                        Text(
                                            stringResource(R.string.day),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                modeNight = Settings.MODE_NIGHT_YES
                                                edit.putInt(
                                                    "mode_night",
                                                    Settings.MODE_NIGHT_YES
                                                )
                                                edit.apply()
                                                actyvity.dzenNoch = true
                                                if (!actyvity.checkDzenNoch)
                                                    actyvity.recreate()
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = modeNight == Settings.MODE_NIGHT_YES,
                                            onClick = {
                                                modeNight = Settings.MODE_NIGHT_YES
                                                edit.putInt(
                                                    "mode_night",
                                                    Settings.MODE_NIGHT_YES
                                                )
                                                edit.apply()
                                                actyvity.dzenNoch = true
                                                if (!actyvity.checkDzenNoch)
                                                    actyvity.recreate()
                                            }
                                        )
                                        Text(
                                            stringResource(R.string.widget_day_d_n),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                modeNight = Settings.MODE_NIGHT_AUTO
                                                edit.putInt(
                                                    "mode_night",
                                                    Settings.MODE_NIGHT_AUTO
                                                )
                                                edit.apply()
                                                actyvity.recreate()
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = modeNight == Settings.MODE_NIGHT_AUTO,
                                            onClick = {
                                                modeNight = Settings.MODE_NIGHT_AUTO
                                                edit.putInt(
                                                    "mode_night",
                                                    Settings.MODE_NIGHT_AUTO
                                                )
                                                edit.apply()
                                                actyvity.recreate()
                                            }
                                        )
                                        Text(
                                            stringResource(R.string.auto_widget_day_d_n),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }
                                }
                            }
                            if (menuPosition == 1) {
                                Text(
                                    stringResource(R.string.menu_font_size_app),
                                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = Settings.fontInterface.sp
                                )
                                Slider(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    valueRange = 18f..58f,
                                    steps = 10,
                                    value = fontSize,
                                    onValueChange = {
                                        val edit = k.edit()
                                        edit.putFloat("font_biblia", it)
                                        edit.apply()
                                        fontSize = it
                                    }
                                )
                            }
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onTertiary)
                                .clickable {
                                    showDropdown = false
                                }) {
                                Icon(modifier = Modifier.align(Alignment.End), painter = painterResource(R.drawable.keyboard_arrow_up), contentDescription = "", tint = PrimaryTextBlack)
                            }
                        }
                    }
                }
            }
            var isScrollRun by remember { mutableStateOf(false) }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        isScrollRun = true
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
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top
            ) {
                val padding = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp
                HtmlText(
                    modifier = Modifier.padding(top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(10.dp)),
                    text = htmlText,
                    fontSize = fontSize.sp,
                    searchText = searchTextResult,
                    scrollState = scrollState,
                    navigateTo = { navigate ->
                        navigateTo(navigate)
                    },
                    textLayoutResult = { layout ->
                        textLayout.value = layout
                    }
                )
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
                if (autoScrollTextVisable) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 10.dp, end = 10.dp)
                    ) {
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
                if (autoScrollSensor) {
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
                                            val prefEditors = k.edit()
                                            prefEditors.putInt("autoscrollSpid", autoScrollSpeed)
                                            prefEditors.apply()
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
                                        val prefEditors = k.edit()
                                        prefEditors.putInt("autoscrollSpid", autoScrollSpeed)
                                        prefEditors.apply()
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

@Composable
fun DialogLiturgia(
    chast: Int,
    onDismissRequest: () -> Unit,
) {
    val context = LocalActivity.current as MainActivity
    var title by remember { mutableStateOf("") }
    var item by remember { mutableStateOf("") }
    val builder = StringBuilder()
    val r = context.resources
    var inputStream = r.openRawResource(R.raw.bogashlugbovya1_1)
    when (chast) {
        1 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_1)
            title = stringResource(R.string.ps_102)
        }

        2 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_2)
            title = stringResource(R.string.ps_91)
        }

        3 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_3)
            title = stringResource(R.string.ps_145)
        }

        4 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_4)
            title = stringResource(R.string.ps_92)
        }

        5 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_5)
            title = stringResource(R.string.mc_5_3_12)
        }

        6 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_6)
            title = stringResource(R.string.malitva_za_pamerlyx)
        }

        7 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_7)
            title = stringResource(R.string.malitva_za_paclicanyx)
        }

        10 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_8)
            title = stringResource(R.string.ps_94)
        }

        11 -> {
            inputStream = r.openRawResource(R.raw.viaczernia_bierascie_1)
            title = stringResource(R.string.viaczernia_bierascie_1)
        }

        13 -> {
            inputStream = r.openRawResource(R.raw.viaczernia_bierascie_3)
            title = stringResource(R.string.viaczernia_bierascie_3)
        }

        14 -> {
            inputStream = r.openRawResource(R.raw.bogashlugbovya1_9)
            title = stringResource(R.string.malitva_za_paclicanyx_i_jyvyx)
        }
    }
    val isr = InputStreamReader(inputStream)
    val reader = BufferedReader(isr)
    var line: String
    reader.forEachLine {
        line = it
        if (context.dzenNoch) line = line.replace("#d00505", "#ff6666")
        builder.append(line).append("\n")
    }
    inputStream.close()
    item = builder.toString()
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(text = title)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                HtmlText(text = item, fontSize = Settings.fontInterface.sp)
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
                Text(stringResource(R.string.close), fontSize = Settings.fontInterface.sp)
            }
        }
    )
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