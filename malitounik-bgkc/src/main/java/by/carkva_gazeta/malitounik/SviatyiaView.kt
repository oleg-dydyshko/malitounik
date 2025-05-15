package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.HtmlText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SviatyiaView(navController: NavHostController, svity: Boolean, position: Int) {
    val year = Settings.data[position][3].toInt()
    val mun = Settings.data[position][2].toInt() + 1
    val day = Settings.data[position][1].toInt()
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    val lazyListState = rememberLazyListState()
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val context = LocalContext.current
    val sviatyiaList = remember { SnapshotStateList<OpisanieData>() }
    val sviatyiaListLocale = remember { SnapshotStateList<OpisanieData>() }
    val dirList = remember { mutableStateListOf<DirList>() }
    var dialoNoIntent by remember { mutableStateOf(false) }
    var dialoNoWIFI by remember { mutableStateOf(false) }
    var isloadIcons by remember { mutableStateOf(false) }
    var isProgressVisable by remember { mutableStateOf(false) }
    if (dialoNoIntent) {
        DialogNoInternet {
            dialoNoIntent = false
        }
    }
    if (dialoNoWIFI) {
        DialogNoWiFI(onDismiss = {
            dialoNoWIFI = false
            sviatyiaList.addAll(sviatyiaListLocale)
        }) {
            dialoNoWIFI = false
            isloadIcons = true
            coroutineScope.launch {
                isProgressVisable = true
                getIcons(context, dirList, sviatyiaListLocale, svity, isloadIcons, position, wiFiExists = {})
                sviatyiaList.clear()
                sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, svity, position))
                isProgressVisable = false
            }
        }
    }
    var showDropdown by remember { mutableStateOf(false) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var backPressHandled by remember { mutableStateOf(false) }
    var menuPosition by remember { mutableIntStateOf(0) }
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    var imageFull by remember { mutableStateOf(false) }
    var checkPiarliny by remember { mutableStateOf(false) }
    var viewPiarliny by remember { mutableStateOf(false) }
    var fullImagePathVisable by remember { mutableStateOf("") }
    BackHandler(!backPressHandled || imageFull || showDropdown) {
        when {
            imageFull -> imageFull = false
            showDropdown -> {
                showDropdown = false
            }

            !backPressHandled -> {
                backPressHandled = true
                navController.popBackStack()
            }
        }
    }
    if (viewPiarliny) {
        DialogPairlinyView(day, mun) {
            viewPiarliny = false
        }
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isProgressVisable = true
            val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
            val fileSvity = File("${context.filesDir}/opisanie_sviat.json")
            if (!Settings.isNetworkAvailable(context)) {
                if (svity) {
                    if (fileSvity.exists()) {
                        val sviatyiaListLocale = loadOpisanieSviat(context, mun, day, position)
                        sviatyiaList.clear()
                        sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, true, position))
                    } else {
                        dialoNoIntent = true
                    }
                } else {
                    if (fileOpisanie.exists()) {
                        val sviatyiaListLocale = loadOpisanieSviatyia(context, year, mun, day)
                        sviatyiaList.clear()
                        sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, false, position))
                    } else {
                        dialoNoIntent = true
                    }
                }
            } else {
                try {
                    if (Settings.isNetworkAvailable(context)) {
                        if (svity) {
                            if (fileSvity.exists()) {
                                sviatyiaListLocale.clear()
                                sviatyiaListLocale.addAll(loadOpisanieSviat(context, mun, day, position))
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, true, position))
                            }
                            downloadOpisanieSviat(context)
                            sviatyiaListLocale.clear()
                            sviatyiaListLocale.addAll(loadOpisanieSviat(context, mun, day, position))
                            getIcons(context, dirList, sviatyiaListLocale, true, isloadIcons, position, wiFiExists = { dialoNoWIFI = true })
                            if (!dialoNoWIFI) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, true, position))
                            }
                        } else {
                            if (fileOpisanie.exists()) {
                                sviatyiaListLocale.clear()
                                sviatyiaListLocale.addAll(loadOpisanieSviatyia(context, year, mun, day))
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, true, position))
                            }
                            downloadOpisanieSviatyia(context, mun)
                            sviatyiaListLocale.clear()
                            sviatyiaListLocale.addAll(loadOpisanieSviatyia(context, year, mun, day))
                            getIcons(context, dirList, sviatyiaListLocale, false, isloadIcons, position, wiFiExists = { dialoNoWIFI = true })
                            if (!dialoNoWIFI) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, false, position))
                            }
                        }
                        getPiarliny(context)
                        checkPiarliny = checkParliny(context, mun, day)
                    } else {
                        dialoNoIntent = true
                    }
                } catch (_: Throwable) {
                }
            }
            isProgressVisable = false
        }
    }
    var width by remember { mutableIntStateOf(0) }
    var zoomAll by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    if (zoomAll == 1f) {
        offsetX = 0f
        offsetY = 0f
    }
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
    Scaffold(
        topBar = {
            if (!fullscreen) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                modifier = Modifier.clickable {
                                    maxLine.intValue = Int.MAX_VALUE
                                    coroutineScope.launch {
                                        delay(5000L)
                                        maxLine.intValue = 1
                                    }
                                },
                                text = stringResource(R.string.zmiest).uppercase(),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontWeight = FontWeight.Bold,
                                maxLines = maxLine.intValue,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                    },
                    navigationIcon = {
                        if (imageFull) {
                            IconButton(
                                onClick = {
                                    imageFull = false
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
                                        showDropdown -> {
                                            showDropdown = false
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
                        if (checkPiarliny) {
                            IconButton({
                                viewPiarliny = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.book_white),
                                    tint = PrimaryTextBlack,
                                    contentDescription = ""
                                )
                            }
                        }
                        var expanded by remember { mutableStateOf(false) }
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
                                    expanded = false
                                    menuPosition = 1
                                },
                                text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.format_size),
                                        contentDescription = ""
                                    )
                                }
                            )
                            if (k.getBoolean("admin", false)) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    onClick = {
                                        if ((context as MainActivity).checkmodulesAdmin()) {
                                            val intent = Intent()
                                            intent.setClassName(context, "by.carkva_gazeta.admin.Sviatyia")
                                            intent.putExtra("dayOfYear", Settings.data[Settings.caliandarPosition][24].toInt())
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

                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
                )
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
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = {
                    showDropdown = false
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
                                        k.edit {
                                            putFloat("font_biblia", it)
                                        }
                                        fontSize = it
                                    }
                                )
                            }
                            Column(
                                modifier = Modifier
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
            if (imageFull) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                width = coordinates.size.width
                            }
                            .fillMaxSize()
                            .clipToBounds()
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .graphicsLayer {
                                scaleX = zoomAll
                                scaleY = zoomAll
                            }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown()
                                    do {
                                        val event = awaitPointerEvent()
                                        if (event.changes.size == 2) {
                                            var zoom = zoomAll
                                            zoom *= event.calculateZoom()
                                            zoom = zoom.coerceIn(1f, 5f)
                                            zoomAll = zoom
                                            event.changes.forEach { pointerInputChange: PointerInputChange ->
                                                pointerInputChange.consume()
                                            }
                                        }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x * 3
                                    offsetY += dragAmount.y * 3
                                }
                            }
                            .align(Alignment.CenterHorizontally), bitmap = BitmapFactory.decodeFile(fullImagePathVisable).asImageBitmap(), contentDescription = ""
                    )
                }
            } else {
                Column {
                    if (isProgressVisable) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        fullscreen = !fullscreen
                                    }
                                )
                            },
                        state = lazyListState
                    ) {
                        item {
                            Spacer(Modifier.padding(top = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp))
                        }
                        items(sviatyiaList.size) { index ->
                            val file = File(sviatyiaList[index].image)
                            SelectionContainer {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .weight(1f), text = sviatyiaList[index].title, fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary
                                        )
                                        Icon(
                                            modifier = Modifier
                                                .padding(end = 10.dp)
                                                .clickable {
                                                    val sb = StringBuilder()
                                                    sb.append(sviatyiaList[index].text)
                                                    sb.append(sviatyiaList[index].text.trim())
                                                    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText(context.getString(R.string.copy_text), sb.toString())
                                                    clipboard.setPrimaryClip(clip)
                                                    if (file.exists()) {
                                                        val sendIntent = Intent(Intent.ACTION_SEND)
                                                        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "by.carkva_gazeta.malitounik.fileprovider", file))
                                                        sendIntent.putExtra(Intent.EXTRA_TEXT, sviatyiaList[index].text.trim())
                                                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, sviatyiaList[index].text.trim())
                                                        sendIntent.type = "image/*"
                                                        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.zmiest)))
                                                    } else {
                                                        val sendIntent = Intent(Intent.ACTION_SEND)
                                                        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString())
                                                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getText(R.string.zmiest))
                                                        sendIntent.type = "text/plain"
                                                        context.startActivity(Intent.createChooser(sendIntent, context.getText(R.string.zmiest)))
                                                    }
                                                    Toast.makeText(context, context.getString(R.string.copy), Toast.LENGTH_SHORT).show()
                                                }, painter = painterResource(R.drawable.share), contentDescription = "", tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    if (file.exists()) {
                                        try {
                                            BitmapFactory.decodeFile(sviatyiaList[index].image).asImageBitmap()
                                        } catch (_: Throwable) {
                                            file.delete()
                                        }
                                    }
                                    if (file.exists()) {
                                        val image = BitmapFactory.decodeFile(sviatyiaList[index].image).asImageBitmap()
                                        var imW = image.width.toFloat()
                                        var imH = image.height.toFloat()
                                        val imageScale: Float = imW / imH
                                        if (imW > 150F) {
                                            imW = 150F
                                            imH = 150F / imageScale
                                        }
                                        Image(
                                            modifier = Modifier
                                                .size(imW.dp, imH.dp)
                                                .align(Alignment.CenterHorizontally)
                                                .clickable {
                                                    fullImagePathVisable = file.absolutePath
                                                    imageFull = true
                                                }, bitmap = image, contentDescription = ""
                                        )
                                    }
                                    if (sviatyiaList[index].text.isNotEmpty()) {
                                        Text(modifier = Modifier.padding(10.dp), text = sviatyiaList[index].text, fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                        }
                    }
                }
            }
        }
    }
}

suspend fun downloadOpisanieSviat(context: Context, count: Int = 0) {
    val pathReference = MainActivity.referens.child("/opisanie_sviat.json")
    val file = File("${context.filesDir}/opisanie_sviat.json")
    var error = false
    if (!file.exists()) {
        pathReference.getFile(file).addOnCompleteListener {
            if (!it.isSuccessful) {
                error = true
            }
        }.await()
    }
    var read = ""
    if (file.exists()) read = file.readText()
    if (read == "") error = true
    if (error && count < 3) {
        downloadOpisanieSviat(context, count + 1)
    }
}

suspend fun downloadOpisanieSviatyia(context: Context, mun: Int, count: Int = 0) {
    val dir = File("${context.filesDir}/sviatyja/")
    if (!dir.exists()) dir.mkdir()
    val pathReference = MainActivity.referens.child("/chytanne/sviatyja/opisanie$mun.json")
    val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    var error = false
    if (!fileOpisanie.exists() || Settings.isNetworkAvailable(context, Settings.TRANSPORT_WIFI)) {
        pathReference.getFile(fileOpisanie).addOnCompleteListener {
            if (!it.isSuccessful) {
                error = true
            }
        }.await()
    }
    var read = ""
    if (fileOpisanie.exists()) read = fileOpisanie.readText()
    if (read == "") error = true
    val pathReference13 = MainActivity.referens.child("/chytanne/sviatyja/opisanie13.json")
    val fileOpisanie13 = File("${context.filesDir}/sviatyja/opisanie13.json")
    if (!fileOpisanie13.exists() || Settings.isNetworkAvailable(context, Settings.TRANSPORT_WIFI)) {
        pathReference13.getFile(fileOpisanie13).addOnCompleteListener {
            if (!it.isSuccessful) {
                error = true
            }
        }.await()
    }
    var read13 = ""
    if (fileOpisanie13.exists()) read13 = fileOpisanie13.readText()
    if (read13 == "") error = true
    if (error && count < 3) {
        downloadOpisanieSviatyia(context, mun, count + 1)
        return
    }
}

fun loadOpisanieSviatyia(context: Context, year: Int, mun: Int, day: Int): SnapshotStateList<OpisanieData> {
    val sviatyiaList = SnapshotStateList<OpisanieData>()
    val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    if (fileOpisanie.exists()) {
        val builder = fileOpisanie.readText()
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        var res = ""
        val arrayList = ArrayList<String>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            res = arrayList[day - 1]
        }
        if ((context as MainActivity).dzenNoch) res = res.replace("#d00505", "#ff6666")
        val title = ArrayList<String>()
        val listRes = res.split("<strong>")
        var sb = ""
        for (i in listRes.size - 1 downTo 0) {
            val text = listRes[i].replace("<!--image-->", "")
            if (text.trim() != "") {
                if (text.contains("Трапар") || text.contains("Кандак")) {
                    sb = "<strong>$text$sb"
                    continue
                } else {
                    sb = "<strong>$text$sb"
                    title.add(0, sb)
                    sb = ""
                }
            }
        }
        title.forEachIndexed { index, text ->
            val t1 = text.indexOf("</strong>")
            var textTitle = ""
            var fulText = ""
            if (t1 != -1) {
                textTitle = text.substring(0, t1 + 9)
                fulText = text.substring(t1 + 9)
            }
            val spannedtitle = AnnotatedString.fromHtml(textTitle)
            val spanned = AnnotatedString.fromHtml(fulText)
            sviatyiaList.add(OpisanieData(index + 1, day, mun, spannedtitle, spanned, "", ""))
        }
    }
    val fileOpisanie13 = File("${context.filesDir}/sviatyja/opisanie13.json")
    if (fileOpisanie13.exists()) {
        val builder = fileOpisanie13.readText()
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        val arrayList = ArrayList<ArrayList<String>>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            val pasha = GregorianCalendar(year, Calendar.DECEMBER, 25)
            val pastvoW = pasha[Calendar.DAY_OF_WEEK]
            for (i in 26..31) {
                val pastvo = GregorianCalendar(year, Calendar.DECEMBER, i)
                val iazepW = pastvo[Calendar.DAY_OF_WEEK]
                for (e in 0 until arrayList.size) {
                    if (pastvoW != Calendar.SUNDAY) {
                        if (arrayList[e][1].toInt() == 0 && mun - 1 == Calendar.DECEMBER && day == i && Calendar.SUNDAY == iazepW) {
                            val t1 = arrayList[e][2].indexOf("</strong>")
                            var textTitle = ""
                            var fulText = ""
                            if (t1 != -1) {
                                textTitle = arrayList[e][2].substring(0, t1 + 9)
                                fulText = arrayList[e][2].substring(t1 + 9)
                            }
                            val spannedtitle = AnnotatedString.fromHtml(textTitle)
                            val spanned = AnnotatedString.fromHtml(fulText)
                            sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                        }
                    } else {
                        if (arrayList[e][1].toInt() == 0 && mun - 1 == Calendar.DECEMBER && day == i && Calendar.MONDAY == iazepW) {
                            val t1 = arrayList[e][2].indexOf("</strong>")
                            var textTitle = ""
                            var fulText = ""
                            if (t1 != -1) {
                                textTitle = arrayList[e][2].substring(0, t1 + 9)
                                fulText = arrayList[e][2].substring(t1 + 9)
                            }
                            val spannedtitle = AnnotatedString.fromHtml(textTitle)
                            val spanned = AnnotatedString.fromHtml(fulText)
                            sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                        }
                    }
                }
            }
            val gc = GregorianCalendar()
            val dayF = if (gc.isLeapYear(year)) 29
            else 28
            for (e in 0 until arrayList.size) {
                if (arrayList[e][1].toInt() == 1 && mun - 1 == Calendar.FEBRUARY && day == dayF) {
                    val t1 = arrayList[e][2].indexOf("</strong>")
                    var textTitle = ""
                    var fulText = ""
                    if (t1 != -1) {
                        textTitle = arrayList[e][2].substring(0, t1 + 9)
                        fulText = arrayList[e][2].substring(t1 + 9)
                    }
                    val spannedtitle = AnnotatedString.fromHtml(textTitle)
                    val spanned = AnnotatedString.fromHtml(fulText)
                    sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                }
            }
        }
    }
    return sviatyiaList
}

fun loadOpisanieSviat(context: Context, mun: Int, day: Int, position: Int): SnapshotStateList<OpisanieData> {
    val sviatyiaList = SnapshotStateList<OpisanieData>()
    val fileOpisanieSviat = File("${context.filesDir}/opisanie_sviat.json")
    if (fileOpisanieSviat.exists()) {
        val builder = fileOpisanieSviat.readText()
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
        arrayList?.forEach { strings ->
            var puxomuia = false
            if (strings[0] == "-1" && strings[1] == "0" && Settings.data[position][22] == "-7") {
                puxomuia = true
            }
            if (strings[0] == "-1" && strings[1] == "1" && Settings.data[position][22] == "0") {
                puxomuia = true
            }
            if (strings[0] == "-1" && strings[1] == "2" && Settings.data[position][22] == "39") {
                puxomuia = true
            }
            if (strings[0] == "-1" && strings[1] == "3" && Settings.data[position][22] == "49") {
                puxomuia = true
            }
            if (strings[0] == "-1" && strings[1] == "4") {
                val pasha = GregorianCalendar()
                for (i in 13..19) {
                    pasha.set(Settings.data[position][3].toInt(), Calendar.JULY, i)
                    val wik = pasha[Calendar.DAY_OF_WEEK]
                    if (wik == Calendar.SUNDAY && day == pasha[Calendar.DATE] && mun - 1 == pasha[Calendar.MONTH]) {
                        puxomuia = true
                    }
                }
            }
            if (puxomuia || (day == strings[0].toInt() && mun == strings[1].toInt())) {
                var res = strings[2]
                if ((context as MainActivity).dzenNoch) res = res.replace("#d00505", "#ff6666")
                val t1 = res.indexOf("</strong>")
                var textTitle = ""
                var fulText = ""
                if (t1 != -1) {
                    textTitle = res.substring(0, t1 + 9)
                    fulText = res.substring(t1 + 9)
                }
                val spannedtitle = AnnotatedString.fromHtml(textTitle)
                val spanned = AnnotatedString.fromHtml(fulText)
                sviatyiaList.add(OpisanieData(1, day, mun, spannedtitle, spanned, "", ""))
            }
        }
    }
    return sviatyiaList
}

suspend fun getIcons(
    context: Context,
    dirList: SnapshotStateList<DirList>,
    sviatyiaList: SnapshotStateList<OpisanieData>,
    svity: Boolean,
    isLoadIcon: Boolean,
    position: Int,
    wiFiExists: () -> Unit,
    count: Int = 0
) {
    val dir = File("${context.filesDir}/icons/")
    if (!dir.exists()) dir.mkdir()
    val dir2 = File("${context.filesDir}/iconsApisanne")
    if (!dir2.exists()) dir2.mkdir()
    if (count < 3) {
        getIcons(context, dirList, sviatyiaList, svity, isLoadIcon, position, wiFiExists, count + 1)
        return
    }
    dirList.clear()
    var size = 0L
    val sb = StringBuilder()
    val fileIconMataData = File("${context.filesDir}/iconsMataData.txt")
    val pathReferenceMataData = MainActivity.referens.child("/chytanne/iconsMataData.txt")
    pathReferenceMataData.getFile(fileIconMataData).await()
    val list = fileIconMataData.readText().split("\n")
    for (i in 0 until sviatyiaList.size) {
        list.forEach {
            val t1 = it.indexOf("<-->")
            if (t1 != -1) {
                val t2 = it.indexOf("<-->", t1 + 4)
                val name = it.substring(0, t1)
                val pref = if (svity) "v"
                else "s"
                sb.append(name)
                var imageSrc = "${pref}_${sviatyiaList[i].date}_${sviatyiaList[i].mun}"
                if (svity) {
                    imageSrc = when (Settings.data[position][22]) {
                        "-7" -> "v_-1_0"
                        "0" -> "v_-1_1"
                        "39" -> "v_-1_2"
                        "49" -> "v_-1_3"
                        else -> "v_${sviatyiaList[i].date}_${sviatyiaList[i].mun}"
                    }
                }
                if (name.contains(imageSrc)) {
                    val t3 = name.lastIndexOf(".")
                    val fileNameT = name.substring(0, t3) + ".txt"
                    val file = File("${context.filesDir}/iconsApisanne/$fileNameT")
                    try {
                        MainActivity.referens.child("/chytanne/iconsApisanne/$fileNameT").getFile(file).addOnFailureListener {
                            if (file.exists()) file.delete()
                        }.await()
                    } catch (_: Throwable) {
                    }
                    val fileIcon = File("${context.filesDir}/icons/${name}")
                    val time = fileIcon.lastModified()
                    val update = it.substring(t2 + 4).toLong()
                    if (!fileIcon.exists() || time < update) {
                        val updateFile = it.substring(t1 + 4, t2).toLong()
                        dirList.add(DirList(name, updateFile))
                        size += updateFile
                    }
                }
            }
        }
    }
    val fileList = File("${context.filesDir}/icons").list()
    fileList?.forEach {
        if (!sb.toString().contains(it)) {
            val file = File("${context.filesDir}/icons/$it")
            if (file.exists()) file.delete()
            val t3 = file.name.lastIndexOf(".")
            val fileNameT = file.name.substring(0, t3) + ".txt"
            val fileOpis = File("${context.filesDir}/iconsApisanne/$fileNameT")
            if (fileOpis.exists()) fileOpis.delete()
        }

    }
    if (!isLoadIcon && Settings.isNetworkAvailable(context, Settings.TRANSPORT_CELLULAR)) {
        if (dirList.isNotEmpty()) {
            wiFiExists()
        }
    } else {
        for (i in 0 until dirList.size) {
            try {
                val fileIcon = File("${context.filesDir}/icons/" + dirList[i].name)
                val pathReference = MainActivity.referens.child("/chytanne/icons/" + dirList[i].name)
                pathReference.getFile(fileIcon).await()
            } catch (_: Throwable) {
            }
        }
    }
}

fun loadIconsOnImageView(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, svity: Boolean, position: Int): SnapshotStateList<OpisanieData> {
    val pref = if (svity) "v"
    else "s"
    val fileList = File("${context.filesDir}/icons").list()
    for (i in 0 until sviatyiaList.size) {
        val indexImg = if (sviatyiaList[i].date == -1) 1
        else sviatyiaList[i].index
        fileList?.forEach {
            var imageSrc = "${pref}_${sviatyiaList[i].date}_${sviatyiaList[i].mun}_${indexImg}"
            if (svity) {
                imageSrc = when (Settings.data[position][22]) {
                    "-7" -> "v_-1_0_${indexImg}"
                    "0" -> "v_-1_1_${indexImg}"
                    "39" -> "v_-1_2_${indexImg}"
                    "49" -> "v_-1_3_${indexImg}"
                    else -> "v_${sviatyiaList[i].date}_${sviatyiaList[i].mun}_${indexImg}"
                }
            }
            if (it.contains(imageSrc)) {
                val t1 = it.lastIndexOf(".")
                val fileNameT = it.substring(0, t1) + ".txt"
                val file = File("${context.filesDir}/iconsApisanne/$fileNameT")
                if (file.exists()) {
                    sviatyiaList[i].textApisanne = file.readText()
                } else {
                    sviatyiaList[i].textApisanne = ""
                }
                val file2 = File("${context.filesDir}/icons/$it")
                if (file2.exists()) {
                    sviatyiaList[i].image = file2.absolutePath
                    return@forEach
                }
            }
        }
    }
    return sviatyiaList
}

suspend fun getPiarliny(context: Context) {
    val pathReference = MainActivity.referens.child("/chytanne/piarliny.json")
    val localFile = File("${context.filesDir}/piarliny.json")
    pathReference.getFile(localFile).await()
}

fun checkParliny(context: Context, mun: Int, day: Int): Boolean {
    val piarliny = ArrayList<ArrayList<String>>()
    val fileOpisanieSviat = File("${context.filesDir}/piarliny.json")
    if (fileOpisanieSviat.exists()) {
        try {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
            piarliny.addAll(gson.fromJson(builder, type))
        } catch (_: Throwable) {
            fileOpisanieSviat.delete()
        }
        val cal = GregorianCalendar()
        piarliny.forEach {
            cal.timeInMillis = it[0].toLong() * 1000
            if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                return true
            }
        }
    }
    return false
}

@Composable
fun DialogPairlinyView(
    day: Int,
    mun: Int,
    onDismiss: () -> Unit,
) {
    val context = LocalActivity.current as MainActivity
    var result by remember { mutableStateOf("") }
    val piarlin = ArrayList<ArrayList<String>>()
    val localFile = File("${context.filesDir}/piarliny.json")
    if (localFile.exists()) {
        try {
            val builder = localFile.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
            piarlin.addAll(gson.fromJson(builder, type))
        } catch (_: Throwable) {
        }
    }
    val cal = GregorianCalendar()
    piarlin.forEach { piarliny ->
        cal.timeInMillis = piarliny[0].toLong() * 1000
        if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
            result = piarliny[1]
        }
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
                    text = stringResource(R.string.piarliny2, day, stringArrayResource(R.array.meciac_smoll)[mun - 1]).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .padding(10.dp).weight(1f).verticalScroll(rememberScrollState())
                ) {
                    HtmlText(
                        text = result, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
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

data class DirList(val name: String?, val sizeBytes: Long)

data class OpisanieData(val index: Int, val date: Int, val mun: Int, val title: AnnotatedString, val text: AnnotatedString, var image: String, var textApisanne: String)