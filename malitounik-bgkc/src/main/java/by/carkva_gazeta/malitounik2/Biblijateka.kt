package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import coil3.compose.rememberAsyncImagePainter
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Biblijateka(
    navController: NavHostController,
    title: String,
    fileName: String
) {
    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val maxLine = remember { mutableIntStateOf(1) }
    var selectPage by remember { mutableIntStateOf(1) }
    var pageState by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }
    var goToSelectPage by remember { mutableStateOf(false) }
    var isShare by remember { mutableStateOf(false) }
    if (goToSelectPage) {
        goToSelectPage = false
        LaunchedEffect(Unit) {
            lazyListState.scrollToItem(selectPage - 1)
        }
    }
    if (isShare) {
        val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
        val uri =
            FileProvider.getUriForFile(context, "by.carkva_gazeta.malitounik2.fileprovider", file)
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
        sendIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            stringResource(R.string.set_log_file)
        )
        sendIntent.type = "text/html"
        context.startActivity(
            Intent.createChooser(
                sendIntent,
                stringResource(R.string.set_log_file)
            )
        )
        isShare = false
    }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val renderer by produceState<PdfRenderer?>(null, file) {
        rendererScope.launch(Dispatchers.IO) {
            val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            value = PdfRenderer(input)
        }
        awaitDispose {
            val currentRenderer = value
            rendererScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    currentRenderer?.close()
                }
            }
        }
    }
    val imageLoader = LocalContext.current.imageLoader
    val imageLoadingScope = rememberCoroutineScope()
    val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }
    Scaffold(
        topBar = {
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
                        navController.popBackStack()
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
                    Text(
                        text = pageState,
                        fontSize = Settings.fontInterface.sp,
                        color = PrimaryTextBlack
                    )
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
                                showDropdown = true
                            },
                            text = {
                                Text(stringResource(R.string.set_page_bibliateka), fontSize = (Settings.fontInterface - 2).sp)
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.apps),
                                    contentDescription = ""
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                isShare = true
                            },
                            text = {
                                Text(stringResource(R.string.share), fontSize = (Settings.fontInterface - 2).sp)
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = ""
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "by.carkva_gazeta.malitounik2.fileprovider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                intent.setDataAndType(uri, "application/pdf")
                                context.startActivity(intent)
                            },
                            text = {
                                Text(stringResource(R.string.open_in), fontSize = (Settings.fontInterface - 2).sp)
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.open_in_new),
                                    contentDescription = ""
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                val printAdapter = PdfDocumentAdapter(context, fileName)
                                val printManager =
                                    context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                                val printAttributes =
                                    PrintAttributes.Builder()
                                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                                printManager.print(fileName, printAdapter, printAttributes)
                            },
                            text = {
                                Text(stringResource(R.string.print), fontSize = (Settings.fontInterface - 2).sp)
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.print),
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
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
                    Column(
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
                        DropdownMenuPdf(pageCount, selectPage = { selectPage = it })
                        TextButton(
                            onClick = {
                                showDropdown = false
                                goToSelectPage = true
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
                                stringResource(R.string.ok),
                                fontSize = Settings.fontInterface.sp,
                                color = PrimaryText
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
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSecondary)
            ) {
                val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
                val height = (width * sqrt(2f)).toInt()
                var widthZoom by remember { mutableIntStateOf(0) }
                var zoomAll by remember { mutableFloatStateOf(1f) }
                var offsetX by remember { mutableFloatStateOf(0f) }
                var offsetY by remember { mutableFloatStateOf(0f) }
                if (zoomAll == 1f) {
                    offsetX = 0f
                    offsetY = 0f
                }
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coordinates ->
                            widthZoom = coordinates.size.width
                        }
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
                ) {
                    items(
                        count = pageCount,
                        key = { index -> "${file.name}-$index" }
                    ) { index ->
                        LaunchedEffect(index) {
                            pageState = (lazyListState.firstVisibleItemIndex + 1).toString() + "/" + pageCount
                        }
                        val cacheKey = MemoryCache.Key("${file.name}-$index")
                        val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.image?.toBitmap()

                        var bitmap: Bitmap? by remember { mutableStateOf(cacheValue) }
                        if (bitmap == null) {
                            DisposableEffect(file, index) {
                                val job = imageLoadingScope.launch(Dispatchers.IO) {
                                    val destinationBitmap =
                                        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                    mutex.withLock {
                                        if (!coroutineContext.isActive) return@launch
                                        try {
                                            renderer?.let {
                                                it.openPage(index).use { page ->
                                                    page.render(
                                                        destinationBitmap,
                                                        null,
                                                        null,
                                                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                                    )
                                                }
                                            }
                                        } catch (_: Exception) {
                                            return@launch
                                        }
                                    }
                                    bitmap = destinationBitmap
                                }
                                onDispose {
                                    job.cancel()
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxWidth()
                            )
                        } else {
                            val request = ImageRequest.Builder(context)
                                .size(width, height)
                                .memoryCacheKey(cacheKey)
                                .data(bitmap)
                                .build()
                            Image(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .border(1.dp, MaterialTheme.colorScheme.background)
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit,
                                painter = rememberAsyncImagePainter(request),
                                contentDescription = "Page ${index + 1} of $pageCount"
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuPdf(
    count: Int,
    selectPage: (Int) -> Unit
) {
    val options = ArrayList<Int>()
    for (i in 1..count) {
        options.add(i)
    }
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState("1")
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString(), style = MaterialTheme.typography.bodyLarge, fontSize = Settings.fontInterface.sp) },
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(option.toString())
                        expanded = false
                        selectPage(option)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
