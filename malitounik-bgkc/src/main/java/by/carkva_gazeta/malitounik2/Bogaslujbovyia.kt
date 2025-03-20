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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Button
import by.carkva_gazeta.malitounik2.ui.theme.Post
import by.carkva_gazeta.malitounik2.ui.theme.Primary
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik2.views.HtmlText
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
fun Bogaslujbovyia(navController: NavHostController, title: String, resurs: Int) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    val vybranoeList = remember { ArrayList<VybranaeDataAll>() }
    var initVybranoe by remember { mutableStateOf(true) }
    var showDropdown by remember { mutableStateOf(false) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var isVybranoe by remember { mutableStateOf(false) }
    var autoScroll by rememberSaveable { mutableStateOf(false) }
    var autoScrollSensor by remember { mutableStateOf(false) }
    var autoScrollSpeed by remember { mutableIntStateOf(k.getInt("autoscrollSpid", 60)) }
    var autoScrollTextVisable by remember { mutableStateOf(false) }
    var autoScrollText by remember { mutableStateOf("") }
    var autoScrollTextColor by remember { mutableStateOf(Primary) }
    var autoScrollTextColor2 by remember { mutableStateOf(PrimaryTextBlack) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var menuPosition by remember { mutableIntStateOf(0) }
    var saveVybranoe by remember { mutableStateOf(false) }
    var modeNight by remember {
        mutableIntStateOf(
            k.getInt(
                "mode_night",
                Settings.MODE_NIGHT_SYSTEM
            )
        )
    }
    //val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
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
    var backPressHandled by remember { mutableStateOf(false) }
    BackHandler(!backPressHandled || fullscreen || showDropdown) {
        when {
            fullscreen -> fullscreen = false
            showDropdown -> {
                showDropdown = false
                if (autoScrollSensor) autoScroll = true
            }

            !backPressHandled -> {
                backPressHandled = true
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
    var anotatedString by remember { mutableStateOf(AnnotatedString("")) }
    var anotatedStringLoad by remember { mutableStateOf(true) }
    val linkStyles = TextLinkStyles(
        SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    )
    LaunchedEffect(Unit) {
        val inputStream = context.resources.openRawResource(resurs)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val text = reader.readText()
        val dzenHoch = (context as MainActivity).dzenNoch
        val newText = if (dzenHoch) text.replace("#d00505", "#ff6666", true)
        else text
        anotatedString = AnnotatedString.fromHtml(newText)
    }
    var dialogLiturgia by remember { mutableStateOf(false) }
    var chast by remember { mutableIntStateOf(0) }
    if (dialogLiturgia) {
        DialogLiturgia(chast) {
            dialogLiturgia = false
        }
    }
    Scaffold(
        topBar = {
            if (!fullscreen) {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.clickable {
                                maxLine.intValue = Int.MAX_VALUE
                                coroutineScope.launch {
                                    delay(5000L)
                                    maxLine.intValue = 1
                                }
                            },
                            text = title,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = Settings.fontInterface.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when {
                                fullscreen -> fullscreen = false
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
                    },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }
                        if (scrollState.canScrollForward) {
                            val iconAutoScroll =
                                if (autoScrollSensor) painterResource(R.drawable.stop_circle)
                                else painterResource(R.drawable.play_circle)
                            val actyvity = LocalActivity.current
                            IconButton(onClick = {
                                autoScroll = !autoScroll
                                autoScrollSensor = !autoScrollSensor
                                if (autoScrollSensor) actyvity?.window?.addFlags(
                                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                )
                                else actyvity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
                                    saveVybranoe = true
                                },
                                text = {
                                    if (isVybranoe) Text(stringResource(R.string.vybranoe_del), fontSize = Settings.fontInterface.sp)
                                    else Text(stringResource(R.string.vybranoe), fontSize = Settings.fontInterface.sp)
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
                                text = { Text(stringResource(R.string.fullscreen), fontSize = Settings.fontInterface.sp) },
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
                                text = { Text(stringResource(R.string.menu_font_size_app), fontSize = Settings.fontInterface.sp) }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    showDropdown = !showDropdown
                                    autoScroll = false
                                    expanded = false
                                    menuPosition = 3
                                },
                                text = { Text(stringResource(R.string.dzen_noch), fontSize = Settings.fontInterface.sp) }
                            )
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
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = {
                    showDropdown = false
                    if (autoScrollSensor) autoScroll = true
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
                            if (menuPosition == 3) {
                                Column(Modifier.selectableGroup())
                                {
                                    val actyvity = LocalActivity.current as MainActivity
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
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
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
                Text(
                    text = anotatedString,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.15).sp,
                    color = MaterialTheme.colorScheme.secondary,
                    onTextLayout = { layout ->
                        if (anotatedStringLoad && anotatedString.isNotEmpty()) {
                            anotatedStringLoad = false
                            anotatedString = buildAnnotatedString {
                                val spanned = anotatedString
                                append(spanned)
                                var string = "Пасьля чытаецца ікас 1 і кандак 1."
                                var strLig = string.length
                                var t1 = spanned.indexOf(string)
                                if (t1 != -1) {
                                    addLink(
                                        LinkAnnotation.Clickable(
                                            string, linkInteractionListener = {
                                                coroutineScope.launch {
                                                    scrollState.animateScrollTo(0)
                                                }
                                            }, styles = linkStyles
                                        ), t1, t1 + strLig
                                    )
                                }
                                string = "Малітвы пасьля сьвятога прычасьця"
                                strLig = string.length
                                t1 = spanned.indexOf(string)
                                if (t1 != -1) {
                                    addLink(
                                        LinkAnnotation.Clickable(
                                            string, linkInteractionListener = {
                                                //val intent = Intent(this@Bogashlugbovya, MalitvyPasliaPrychascia::class.java)
                                                //startActivity(intent)
                                            }, styles = linkStyles
                                        ), t1, t1 + strLig
                                    )
                                }
                                val string11 = "[1]"
                                val strLig11 = string11.length
                                val t11 = spanned.indexOf(string11)
                                if (t11 != -1) {
                                    addLink(
                                        LinkAnnotation.Clickable(
                                            string, linkInteractionListener = {
                                                coroutineScope.launch {
                                                    val strPosition = spanned.indexOf("ПЕРШАЯ ГАДЗІНА", t11 + strLig11)
                                                    val line = layout.getLineForOffset(strPosition)
                                                    val y = layout.getLineTop(line)
                                                    scrollState.animateScrollTo(y.toInt())
                                                }
                                            }, styles = linkStyles
                                        ), t11, t11 + strLig11
                                    )
                                }
                                val string3 = "[3]"
                                val strLig3 = string3.length
                                val t3 = spanned.indexOf(string3)
                                if (t3 != -1) {
                                    addLink(
                                        LinkAnnotation.Clickable(
                                            string, linkInteractionListener = {
                                                coroutineScope.launch {
                                                    val strPosition = spanned.indexOf("ТРЭЦЯЯ ГАДЗІНА", t3 + strLig3)
                                                    val line = layout.getLineForOffset(strPosition)
                                                    val y = layout.getLineTop(line)
                                                    scrollState.animateScrollTo(y.toInt())
                                                }
                                            }, styles = linkStyles
                                        ), t3, t3 + strLig3
                                    )
                                }
                                val string6 = "[6]"
                                val strLig6 = string6.length
                                val t6 = spanned.indexOf(string6)
                                if (t6 != -1) {
                                    addLink(
                                        LinkAnnotation.Clickable(
                                            string, linkInteractionListener = {
                                                coroutineScope.launch {
                                                    val strPosition = spanned.indexOf("ШОСТАЯ ГАДЗІНА", t6 + strLig6)
                                                    val line = layout.getLineForOffset(strPosition)
                                                    val y = layout.getLineTop(line)
                                                    scrollState.animateScrollTo(y.toInt())
                                                }
                                            }, styles = linkStyles
                                        ), t6, t6 + strLig6
                                    )
                                }
                                var string9 = "[9]"
                                var strLig9 = string9.length
                                var t9 = spanned.indexOf(string9)
                                if (t9 != -1) {
                                    addLink(
                                        LinkAnnotation.Clickable(
                                            string, linkInteractionListener = {
                                                coroutineScope.launch {
                                                    val strPosition = spanned.indexOf("ДЗЯВЯТАЯ ГАДЗІНА", t9 + strLig9)
                                                    val line = layout.getLineForOffset(strPosition)
                                                    val y = layout.getLineTop(line)
                                                    scrollState.animateScrollTo(y.toInt())
                                                }
                                            }, styles = linkStyles
                                        ), t9, t9 + strLig9
                                    )
                                }
                                if (resurs == R.raw.abiednica) {
                                    val stringBSA = "Заканчэньне ў час Вялікага посту гл. ніжэй"
                                    val strLigBSA = stringBSA.length
                                    val bsat1 = spanned.indexOf(stringBSA)
                                    if (bsat1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("Заканчэньне абедніцы", bsat1 + strLigBSA, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), bsat1, bsat1 + strLigBSA
                                        )
                                    }
                                }
                                if (resurs == R.raw.viaczernia_niadzelnaja) {
                                    string = "ліцьця і блаславеньне хлябоў"
                                    strLig = string.length
                                    t1 = spanned.indexOf(string)
                                    if (t1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    /*val intent = Intent(this@Bogashlugbovya, Bogashlugbovya::class.java)
                                                intent.putExtra("autoscrollOFF", autoscroll)
                                                intent.putExtra("title", "Ліцьця і блаславеньне хлябоў")
                                                intent.putExtra("resurs", "viaczernia_liccia_i_blaslavenne_chliabou")
                                                startActivity(intent)*/
                                                }, styles = linkStyles
                                            ), t1, t1 + strLig
                                        )
                                    }
                                }
                                if (resurs == R.raw.lit_raniej_asviaczanych_darou) {
                                    val stringVB = "зусім прапускаюцца"
                                    val strLigVB = stringVB.length
                                    val vbt1 = spanned.indexOf(stringVB)
                                    if (vbt1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("Калі ёсьць 10 песьняў", vbt1 + strLigVB, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), vbt1, vbt1 + strLigVB
                                        )
                                    }
                                }
                                if (resurs == R.raw.viaczerniaja_sluzba_sztodzionnaja_biez_sviatara) {
                                    var stringVB = "выбраныя вершы з псалмаў 1-3"
                                    var strLigVB = stringVB.length
                                    var vbt1 = spanned.indexOf(stringVB)
                                    if (vbt1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 11
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), vbt1, vbt1 + strLigVB
                                        )
                                    }
                                    stringVB = "прапускаюцца"
                                    strLigVB = stringVB.length
                                    vbt1 = spanned.indexOf(stringVB)
                                    if (vbt1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("Вызваль з вязьніцы душу маю", vbt1 + strLigVB, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), vbt1, vbt1 + strLigVB
                                        )
                                    }
                                    stringVB = "гл. тут."
                                    strLigVB = stringVB.length
                                    vbt1 = spanned.indexOf(stringVB)
                                    if (vbt1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 13
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), vbt1, vbt1 + strLigVB
                                        )
                                    }
                                    stringVB = "«Госпадзе, Цябе клічу»"
                                    strLigVB = stringVB.length
                                    vbt1 = spanned.indexOf(stringVB)
                                    if (vbt1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("Псалом 140", vbt1 + strLigVB, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), vbt1, vbt1 + strLigVB
                                        )
                                    }
                                    val stringVB2 = "гл. ніжэй."
                                    val strLigVB2 = stringVB2.length
                                    val vbt2 = spanned.indexOf(stringVB2)
                                    if (vbt2 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("ЗАКАНЧЭНЬНЕ ВЯЧЭРНІ Ў ВЯЛІКІ ПОСТ", vbt2 + strLigVB2, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), vbt2, vbt2 + strLigVB2
                                        )
                                    }
                                }
                                if (resurs == R.raw.lit_jana_zalatavusnaha || resurs == R.raw.lit_jan_zalat_vielikodn || resurs == R.raw.lit_vasila_vialikaha || resurs == R.raw.abiednica || resurs == R.raw.vialikdzien_liturhija) {
                                    var stringBS = "Пс 102 (гл. тут)."
                                    var strLigBS = stringBS.length
                                    var bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 1
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Пс 91. (Гл. тут)."
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 2
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "(Пс 145). (Гл. тут)."
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 3
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Пс 92. (Гл. тут)."
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 4
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Пс 94. (Гл. тут)."
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 10
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Іншы антыфон сьвяточны і нядзельны (Мц 5:3-12):"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 5
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Малітва за памерлых"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 6
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Малітва за пакліканых"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 7
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "Успамін памерлых і жывых"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 14
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    var stringBS2 = "Адзінародны Сыне (гл. тут)"
                                    var strLigBS2 = stringBS2.length
                                    var bst2 = spanned.indexOf(stringBS2)
                                    if (bst2 == -1) {
                                        stringBS2 = "«Адзінародны Сыне» (↓ гл. тут)"
                                        strLigBS2 = stringBS2.length
                                        bst2 = spanned.indexOf(stringBS2)
                                    }
                                    if (bst2 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("Адзінародны Сыне", bst2 + strLigBS2, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), bst2, bst2 + strLigBS2
                                        )
                                    }
                                }
                                if (resurs == R.raw.lit_jana_zalatavusnaha) {
                                    var stringBS = "«Блаславі, душа мая, Госпада...»"
                                    var strLigBS = stringBS.length
                                    var bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 1
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "«Добра ёсьць славіць Госпада...»"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 2
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "«Хвалі, душа мая, Госпада...»"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 3
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "«Госпад пануе, Ён апрануўся ў красу...»"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 4
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                    stringBS = "«У валадарстве Тваім успомні нас, Госпадзе...»"
                                    strLigBS = stringBS.length
                                    bst1 = spanned.indexOf(stringBS)
                                    if (bst1 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    chast = 5
                                                    dialogLiturgia = true
                                                }, styles = linkStyles
                                            ), bst1, bst1 + strLigBS
                                        )
                                    }
                                }
                                if (resurs == R.raw.mm_25_03_dabravieszczannie_liturhija_subota_niadziela) {
                                    string9 = "Гл. тут"
                                    strLig9 = string9.length
                                    t9 = spanned.indexOf(string9)
                                    if (t9 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    /*val intent = Intent(this@Bogashlugbovya, Bogashlugbovya::class.java)
                                                intent.putExtra("autoscrollOFF", autoscroll)
                                                intent.putExtra("title", "Дабравешчаньне Найсьвяцейшай Багародзіцы")
                                                intent.putExtra("resurs", "mm_25_03_dabravieszczannie_viaczernia_z_liturhijaj")
                                                startActivity(intent)*/
                                                }, styles = linkStyles
                                            ), t9, t9 + strLig9
                                        )
                                    }
                                }
                                if (resurs == R.raw.kanon_andreja_kryckaha_4_czastki) {
                                    string9 = "Аўторак ↓"
                                    strLig9 = string9.length
                                    t9 = spanned.indexOf(string9)
                                    if (t9 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("АЎТОРАК", t9 + strLig9, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), t9, t9 + strLig9
                                        )
                                    }
                                    string9 = "Серада ↓"
                                    strLig9 = string9.length
                                    t9 = spanned.indexOf(string9)
                                    if (t9 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("СЕРАДА", t9 + strLig9, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), t9, t9 + strLig9
                                        )
                                    }
                                    string9 = "Чацьвер ↓"
                                    strLig9 = string9.length
                                    t9 = spanned.indexOf(string9)
                                    if (t9 != -1) {
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                string, linkInteractionListener = {
                                                    coroutineScope.launch {
                                                        val strPosition = spanned.indexOf("ЧАЦЬВЕР", t9 + strLig9, true)
                                                        val line = layout.getLineForOffset(strPosition)
                                                        val y = layout.getLineTop(line)
                                                        scrollState.animateScrollTo(y.toInt())
                                                    }
                                                }, styles = linkStyles
                                            ), t9, t9 + strLig9
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
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

        /*8 -> {
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne).uppercase()
            arguments?.let {
                activity.setArrayData(MenuCaliandar.getDataCalaindar(it.getInt("date"), it.getInt("month"), it.getInt("year")))
            }
            builder.append(activity.sviatyiaView(1))
        }

        9 -> {
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne).uppercase()
            arguments?.let {
                activity.setArrayData(MenuCaliandar.getDataCalaindar(it.getInt("date"), it.getInt("month"), it.getInt("year")))
            }
            builder.append(activity.sviatyiaView(0))
        }*/

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
    //if (!(chast == 8 || chast == 9)) {
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
    //}
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(text = title)
        },
        text = {
            HtmlText(text = item, fontSize = Settings.fontInterface.sp)
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

data class VybranaeDataAll(
    val id: Long,
    val title: String,
    val resource: String,
)