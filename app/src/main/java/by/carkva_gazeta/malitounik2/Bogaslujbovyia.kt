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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import by.carkva_gazeta.malitounik2.ui.theme.Divider
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
    val listState = rememberLazyListState()
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
                        listState.scrollBy(2f)
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
                listState.scrollToItem(0)
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
                            overflow = TextOverflow.Ellipsis
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
                        if (listState.canScrollForward) {
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
                        } else if (listState.canScrollBackward) {
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
                                    if (isVybranoe) Text(stringResource(R.string.vybranoe_del))
                                    else Text(stringResource(R.string.vybranoe))
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
                                text = { Text(stringResource(R.string.fullscreen)) },
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
                                text = { Text(stringResource(R.string.menu_font_size_app)) }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    showDropdown = !showDropdown
                                    autoScroll = false
                                    expanded = false
                                    menuPosition = 3
                                },
                                text = { Text(stringResource(R.string.dzen_noch)) }
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
                            .padding(bottom = 10.dp)
                            .clip(
                                shape = RoundedCornerShape(
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.onTertiary)
                            .padding(10.dp)
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
                                        color = MaterialTheme.colorScheme.secondary
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
                                            color = MaterialTheme.colorScheme.secondary
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
                                            color = MaterialTheme.colorScheme.secondary
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
                                            color = MaterialTheme.colorScheme.secondary
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
                                            color = MaterialTheme.colorScheme.secondary
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
                                    color = MaterialTheme.colorScheme.secondary
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
                            if (menuPosition != 4) {
                                TextButton(
                                    onClick = {
                                        showDropdown = false
                                        if (autoScrollSensor) autoScroll = true
                                    },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(5.dp),
                                    colors = ButtonColors(
                                        Divider,
                                        Color.Unspecified,
                                        Color.Unspecified,
                                        Color.Unspecified
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        stringResource(R.string.close),
                                        fontSize = 18.sp,
                                        color = PrimaryText
                                    )
                                }
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
            LazyColumn(
                modifier = Modifier
                    .padding(10.dp)
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
                    .nestedScroll(nestedScrollConnection),
                state = listState
            ) {
                val inputStream = context.resources.openRawResource(resurs)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                val text = reader.readText()
                item {
                    HtmlText(
                        text = text,
                        fontSize = fontSize.sp
                    )
                }
                item {
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                    if (listState.lastScrolledForward && !listState.canScrollForward) {
                        autoScroll = false
                        autoScrollSensor = false
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
                            color = autoScrollTextColor2
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

data class VybranaeDataAll(
    val id: Long,
    val title: String,
    val resource: String,
)