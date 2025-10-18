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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.admin.Sviaty
import by.carkva_gazeta.malitounik.admin.Sviatyia
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.HtmlText
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    val context = LocalContext.current
    val sviatyiaList = remember { SnapshotStateList<OpisanieData>() }
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
        val sviatyiaListLocale = if (svity) {
            loadOpisanieSviat(context, position)
        } else {
            loadOpisanieSviatyia(context, year, mun, day)
        }
        DialogNoWiFI(onDismiss = {
            dialoNoWIFI = false
            sviatyiaList.clear()
            sviatyiaList.addAll(loadIconsOnImageView(context, sviatyiaListLocale, svity, position))
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
            val fileSvity = File("${context.filesDir}/sviaty.json")
            if (!Settings.isNetworkAvailable(context)) {
                if (svity) {
                    if (fileSvity.exists()) {
                        val sviatyiaListLocale = loadOpisanieSviat(context, position)
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
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviat(context, position), true, position))
                            }
                            downloadOpisanieSviat(context)
                            getIcons(context, dirList, loadOpisanieSviat(context, position), true, isloadIcons, position, wiFiExists = { dialoNoWIFI = true })
                            if (!dialoNoWIFI) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviat(context, position), true, position))
                            }
                        } else {
                            if (fileOpisanie.exists()) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviatyia(context, year, mun, day), false, position))
                            }
                            downloadOpisanieSviatyia(context, mun)
                            getIcons(context, dirList, loadOpisanieSviatyia(context, year, mun, day), false, isloadIcons, position, wiFiExists = { dialoNoWIFI = true })
                            if (!dialoNoWIFI) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviatyia(context, year, mun, day), false, position))
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
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 700) {
            coroutineScope.launch {
                try {
                    if (Settings.isNetworkAvailable(context)) {
                        val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
                        val fileSvity = File("${context.filesDir}/sviaty.json")
                        if (svity) {
                            if (fileSvity.exists()) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviat(context, position), true, position))
                            }
                            downloadOpisanieSviat(context)
                            getIcons(context, dirList, loadOpisanieSviat(context, position), true, isloadIcons, position, wiFiExists = { dialoNoWIFI = true })
                            if (!dialoNoWIFI) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviat(context, position), true, position))
                            }
                        } else {
                            if (fileOpisanie.exists()) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviatyia(context, year, mun, day), false, position))
                            }
                            downloadOpisanieSviatyia(context, mun)
                            getIcons(context, dirList, loadOpisanieSviatyia(context, year, mun, day), false, isloadIcons, position, wiFiExists = { dialoNoWIFI = true })
                            if (!dialoNoWIFI) {
                                sviatyiaList.clear()
                                sviatyiaList.addAll(loadIconsOnImageView(context, loadOpisanieSviatyia(context, year, mun, day), false, position))
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
        }
    }
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
            AnimatedVisibility(
                !fullscreen, enter = fadeIn(
                    tween(
                        durationMillis = 500, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
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
                        IconButton(onClick = {
                            fullscreen = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.fullscreen),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        if (k.getBoolean("admin", false)) {
                            VerticalDivider()
                            IconButton(onClick = {
                                if (svity) {
                                    val intent = Intent(context, Sviaty::class.java)
                                    intent.putExtra("day", sviatyiaList[0].date)
                                    intent.putExtra("mun", sviatyiaList[0].mun)
                                    launcher.launch(intent)
                                } else {
                                    val intent = Intent(context, Sviatyia::class.java)
                                    intent.putExtra("dayOfYear", Settings.data[Settings.caliandarPosition][24].toInt())
                                    launcher.launch(intent)
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onSecondary
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
            AnimatedVisibility(
                imageFull, enter = fadeIn(tween(durationMillis = 500, easing = LinearOutSlowInEasing)),
                exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        modifier = Modifier
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
            }
            AnimatedVisibility(
                !imageFull, enter = fadeIn(
                    tween(
                        durationMillis = 500, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
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
                                        val t3 = file.name.lastIndexOf(".")
                                        val fileNameT = file.name.substring(0, t3) + ".txt"
                                        val fileImageOpis = File("${context.filesDir}/iconsApisanne/$fileNameT")
                                        if (fileImageOpis.exists()) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .fillMaxWidth(), text = fileImageOpis.readText(), fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center, fontStyle = FontStyle.Italic
                                            )
                                        }
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
    val pathReference = Malitounik.referens.child("/sviaty.json")
    val dir = File("${context.filesDir}/cache")
    if (!dir.exists()) dir.mkdir()
    val file = File("${context.filesDir}/cache/cache.txt")
    var error = false
    val metadata = pathReference.metadata.addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        downloadOpisanieSviat(context, count + 1)
        return
    }
    val size = metadata.sizeBytes
    if (size != file.length()) error = true
    pathReference.getFile(file).addOnFailureListener {
        error = true
    }.await()
    var read = ""
    if (file.exists()) read = file.readText()
    if (read == "") error = true
    if (error && count < 3) {
        downloadOpisanieSviat(context, count + 1)
        return
    }
    val fileResult = File("${context.filesDir}/sviaty.json")
    file.copyTo(fileResult, overwrite = true)
}

suspend fun downloadOpisanieSviatyia(context: Context, mun: Int, count: Int = 0) {
    val dir = File("${context.filesDir}/sviatyja/")
    if (!dir.exists()) dir.mkdir()
    val dir2 = File("${context.filesDir}/cache")
    if (!dir2.exists()) dir2.mkdir()
    val fileOpisanie = File("${context.filesDir}/cache/cache.txt")
    val pathReference = Malitounik.referens.child("/chytanne/sviatyja/opisanie$mun.json")
    var error = false
    val metadata = pathReference.metadata.addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        downloadOpisanieSviatyia(context, mun, count + 1)
        return
    }
    val size = metadata.sizeBytes
    pathReference.getFile(fileOpisanie).addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        downloadOpisanieSviatyia(context, mun, count + 1)
        return
    }
    if (size != fileOpisanie.length()) error = true

    var read = ""
    if (fileOpisanie.exists()) read = fileOpisanie.readText()
    if (read == "") error = true
    if (error && count < 3) {
        downloadOpisanieSviatyia(context, mun, count + 1)
        return
    }
    val fileOpisanieResult = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    fileOpisanie.copyTo(fileOpisanieResult, overwrite = true)
    val pathReference13 = Malitounik.referens.child("/chytanne/sviatyja/opisanie13.json")
    val fileOpisanie13 = File("${context.filesDir}/cache/cache.txt")
    val metadata13 = pathReference13.metadata.addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        downloadOpisanieSviatyia(context, mun, count + 1)
        return
    }
    val size13 = metadata13.sizeBytes
    pathReference13.getFile(fileOpisanie13).addOnFailureListener {
        error = true
    }.await()
    if (size13 != fileOpisanie13.length()) error = true
    var read13 = ""
    if (fileOpisanie13.exists()) read13 = fileOpisanie13.readText()
    if (read13 == "") error = true
    if (error && count < 3) {
        downloadOpisanieSviatyia(context, mun, count + 1)
        return
    }
    val fileOpisanieResult13 = File("${context.filesDir}/sviatyja/opisanie13.json")
    fileOpisanie13.copyTo(fileOpisanieResult13, overwrite = true)
}

fun loadOpisanieSviatyia(context: Context, year: Int, mun: Int, day: Int): SnapshotStateList<OpisanieData> {
    val sviatyiaList = SnapshotStateList<OpisanieData>()
    val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    if (fileOpisanie.exists()) {
        try {
            val builder = fileOpisanie.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
            var res = ""
            val arrayList = ArrayList<String>()
            if (builder.isNotEmpty()) {
                arrayList.addAll(gson.fromJson(builder, type))
                res = arrayList[day - 1]
            }
            if (Settings.dzenNoch.value) res = res.replace("#d00505", "#ff6666", true)
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
                sviatyiaList.add(OpisanieData(index + 1, day, mun, Settings.CALAINDAR, spannedtitle, spanned, "", ""))
            }
        } catch (_: JsonSyntaxException) {
            fileOpisanie.delete()
        } catch (_: Throwable) {
        }
    }
    val fileOpisanie13 = File("${context.filesDir}/sviatyja/opisanie13.json")
    if (fileOpisanie13.exists()) {
        try {
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
                                sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), Settings.CALAINDAR, spannedtitle, spanned, "", ""))
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
                                sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), Settings.CALAINDAR, spannedtitle, spanned, "", ""))
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
                        sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), Settings.CALAINDAR, spannedtitle, spanned, "", ""))
                    }
                }
            }
        } catch (_: JsonSyntaxException) {
            fileOpisanie13.delete()
        } catch (_: Throwable) {
        }
    }
    return sviatyiaList
}

fun loadOpisanieSviat(context: Context, position: Int): SnapshotStateList<OpisanieData> {
    val sviatyiaList = SnapshotStateList<OpisanieData>()
    val fileOpisanieSviat = File("${context.filesDir}/sviaty.json")
    if (fileOpisanieSviat.exists()) {
        try {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
            val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
            arrayList?.forEach { strings ->
                var puxomuia = false
                val day = if (strings[2].toInt() == Settings.PASHA) Settings.data[position][22].toInt()
                else Settings.data[position][1].toInt()
                val mun = if (strings[2].toInt() == Settings.PASHA) 1
                else Settings.data[position][2].toInt() + 1
                if (strings[2].toInt() == Settings.UNDER) {
                    if (strings[3].contains("Айцоў першых 6-ці Ўсяленскіх сабораў", true) && Settings.data[position][1].toInt() >= 13 && Settings.data[position][1].toInt() <= 19 && Settings.data[position][2].toInt() == Calendar.JULY) {
                        puxomuia = true
                    }
                }
                if (puxomuia || (day == strings[0].toInt() && mun == strings[1].toInt())) {
                    var res = strings[3]
                    if (Settings.dzenNoch.value) res = res.replace("#d00505", "#ff6666", true)
                    val t1 = res.indexOf("</strong>")
                    var textTitle = ""
                    var fulText = ""
                    if (t1 != -1) {
                        textTitle = res.substring(0, t1 + 9)
                        fulText = res.substring(t1 + 9)
                    }
                    val spannedtitle = AnnotatedString.fromHtml(textTitle)
                    val spanned = AnnotatedString.fromHtml(fulText)
                    sviatyiaList.add(OpisanieData(1, strings[0].toInt(), strings[1].toInt(), strings[2].toInt(), spannedtitle, spanned, "", ""))
                }
            }
        } catch (_: JsonSyntaxException) {
            fileOpisanieSviat.delete()
        } catch (_: Throwable) {
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
    val dir3 = File("${context.filesDir}/cache")
    if (!dir3.exists()) dir3.mkdir()
    dirList.clear()
    var size = 0L
    var error = false
    val sb = StringBuilder()
    val fileIconMataData = File("${context.filesDir}/cache/cache.txt")
    val pathReferenceMataData = Malitounik.referens.child("/chytanne/iconsMataData.txt")
    val metadata = pathReferenceMataData.metadata.addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        getIcons(context, dirList, sviatyiaList, svity, isLoadIcon, position, wiFiExists, count + 1)
        return
    }
    val sizeBytes = metadata.sizeBytes
    pathReferenceMataData.getFile(fileIconMataData).addOnFailureListener {
        error = true
    }.await()
    if (sizeBytes != fileIconMataData.length()) error = true
    if (error && count < 3) {
        getIcons(context, dirList, sviatyiaList, svity, isLoadIcon, position, wiFiExists, count + 1)
        return
    }
    val fileIconMataDataResult = File("${context.filesDir}/iconsMataData.txt")
    fileIconMataData.copyTo(fileIconMataDataResult, overwrite = true)
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
                        "-7" -> "v_-7_1_1"
                        "0" -> "v_0_1_1"
                        "39" -> "v_39_1_1"
                        "49" -> "v_49_1_1"
                        else -> "v_${sviatyiaList[i].date}_${sviatyiaList[i].mun}"
                    }
                }
                if (name.contains(imageSrc)) {
                    val t3 = name.lastIndexOf(".")
                    val fileNameT = name.substring(0, t3) + ".txt"
                    val file = File("${context.filesDir}/iconsApisanne/$fileNameT")
                    try {
                        Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").getFile(file).addOnFailureListener {
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
                val pathReference = Malitounik.referens.child("/chytanne/icons/" + dirList[i].name)
                val metadata = pathReference.metadata.addOnFailureListener {
                    error = true
                }.await()
                if (error && count < 3) {
                    getIcons(context, dirList, sviatyiaList, svity, isLoadIcon, position, wiFiExists, count + 1)
                    return
                }
                val sizeBytes = metadata.sizeBytes
                pathReference.getFile(fileIcon).addOnFailureListener {
                    error = true
                }.await()
                if (sizeBytes != fileIcon.length()) {
                    if (fileIcon.exists()) fileIcon.delete()
                    error = true
                }
                if (error && count < 3) {
                    getIcons(context, dirList, sviatyiaList, svity, isLoadIcon, position, wiFiExists, count + 1)
                    return
                }
            } catch (_: Throwable) {
                if (count < 3) {
                    getIcons(context, dirList, sviatyiaList, svity, isLoadIcon, position, wiFiExists, count + 1)
                }
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
                    "-7" -> "v_-7_1_1"
                    "0" -> "v_0_1_1"
                    "39" -> "v_39_1_1"
                    "49" -> "v_49_1_1"
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

suspend fun getPiarliny(context: Context, count: Int = 0) {
    var error = false
    val dir = File("${context.filesDir}/cache")
    if (!dir.exists()) dir.mkdir()
    val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
    val metadata = pathReference.metadata.addOnFailureListener {
        error = true
    }.await()
    if (error && count < 3) {
        getPiarliny(context, count + 1)
        return
    }
    val size = metadata.sizeBytes
    val localFile = File("${context.filesDir}/cache/cache.txt")
    pathReference.getFile(localFile).addOnFailureListener {
        error = true
    }.await()
    if (size != localFile.length()) error = true
    if (error && count < 3) {
        getPiarliny(context, count + 1)
        return
    }
    val localFileResult = File("${context.filesDir}/piarliny.json")
    localFile.copyTo(localFileResult, overwrite = true)
}

fun checkParliny(context: Context, mun: Int, day: Int): Boolean {
    val piarliny = ArrayList<ArrayList<String>>()
    val filePiarliny = File("${context.filesDir}/piarliny.json")
    if (filePiarliny.exists()) {
        try {
            val builder = filePiarliny.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
            piarliny.addAll(gson.fromJson(builder, type))
        } catch (_: JsonSyntaxException) {
            filePiarliny.delete()
        } catch (_: Throwable) {
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
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.piarliny2, day, stringArrayResource(R.array.meciac_smoll)[mun - 1]).uppercase(), modifier = Modifier
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

data class OpisanieData(val index: Int, val date: Int, val mun: Int, var dataCaliandar: Int, val title: AnnotatedString, val text: AnnotatedString, var image: String, var textApisanne: String)