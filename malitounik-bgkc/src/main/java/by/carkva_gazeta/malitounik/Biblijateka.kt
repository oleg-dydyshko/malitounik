package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.graphics.createBitmap
import androidx.core.util.lruCache
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import coil3.Canvas
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

class PdfBitmapConverter(
    private val density: Float,
    private val file: File,
) : PagingSource<Int, Bitmap>() {

    companion object {
        private const val CACHE_SIZE = 20
    }

    private var renderer: PdfRenderer? = null

    override fun getRefreshKey(state: PagingState<Int, Bitmap>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }

    suspend fun loadSection(startPosition: Int, loadSize: Int): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val bitmaps = mutableListOf<Bitmap>()
            renderer?.close()
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                with(PdfRenderer(descriptor)) {
                    renderer = this
                    for (index in startPosition until (startPosition + loadSize).coerceAtMost(pageCount)) {
                        openPage(index).use { page ->
                            val bitmap = drawBitmapLogic(page)
                            bitmaps.add(bitmap)
                        }
                    }
                }
            }
            return@withContext bitmaps
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bitmap> {
        val position = params.key ?: 0
        val bitmaps = loadSection(position, params.loadSize)
        return LoadResult.Page(
            data = bitmaps,
            prevKey = if (position == 0) null else position - params.loadSize,
            nextKey = if (position + params.loadSize >= getPageCount()) null else position + params.loadSize,
        )
    }

    override val jumpingSupported: Boolean = true

    internal fun clearCache() {
        bitmapCache.evictAll()
    }

    private val bitmapCache = lruCache<Int, Bitmap>(
        CACHE_SIZE,
        onEntryRemoved = { evicted, _, oldBitmap, _ ->
            if (evicted) {
                oldBitmap.recycle()
            }
        },
    )

    suspend fun getPageCount(): Int {
        return withContext(Dispatchers.IO) {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                with(PdfRenderer(descriptor)) {
                    return@with pageCount
                }
            }
        }
    }

    private fun drawBitmapLogic(page: PdfRenderer.Page): Bitmap {
        val bitmap = createBitmap((page.width * density).toInt(), (page.height * density).toInt())
        Canvas(bitmap).apply {
            drawColor(Color.WHITE)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        page.render(
            bitmap,
            null,
            null,
            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
        )
        return bitmap
    }

    suspend fun loadAllPages(): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val bitmaps = mutableListOf<Bitmap>()
            renderer?.close()
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                with(PdfRenderer(descriptor)) {
                    renderer = this
                    return@with (0 until pageCount).map { index ->
                        openPage(index).use { page ->
                            if (bitmapCache[index] != null) {
                                return@map bitmapCache[index]
                            } else {
                                val bitmap = drawBitmapLogic(page)
                                bitmapCache.put(index, bitmap)
                                bitmaps.add(bitmap)
                            }
                        }
                    }
                }
            }
            return@withContext bitmaps
        }
    }
}

class PdfViewModel(private val converter: PdfBitmapConverter) : ViewModel() {
    private val _displayState = MutableStateFlow<PdfDisplayState>(PdfDisplayState.Loading)
    val displayState = _displayState.asStateFlow()
    private val pager by lazy {
        Pager(
            PagingConfig(
                pageSize = 5,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 7,
                maxSize = 100,
                jumpThreshold = 2,
            ),
            initialKey = 0,
            pagingSourceFactory = {
                converter
            },
        )
    }

    suspend fun itemsCount(): Int {
        return withContext(Dispatchers.Main) {
            return@withContext converter.getPageCount()
        }
    }

    init {
        viewModelScope.launch {
            val pageCount = converter.getPageCount()
            when {
                pageCount == 0 -> {
                    _displayState.value = PdfDisplayState.Error
                }

                pageCount < 5 -> {
                    _displayState.value = PdfDisplayState.AllLoadedContent(converter.loadAllPages())
                }

                else -> {
                    _displayState.value = PdfDisplayState.PartiallyLoadedContent(pager.flow)
                }
            }
        }
    }

    override fun onCleared() {
        converter.clearCache()
    }
}

sealed class PdfDisplayState {
    object Loading : PdfDisplayState()
    object Error : PdfDisplayState()
    data class AllLoadedContent(val list: List<Bitmap>) : PdfDisplayState()
    data class PartiallyLoadedContent(val flow: Flow<PagingData<Bitmap>>) : PdfDisplayState()
}

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
    var pageState by remember { mutableStateOf("") }
    var isShare by remember { mutableStateOf(false) }
    if (isShare) {
        val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
        val uri =
            FileProvider.getUriForFile(context, "by.carkva_gazeta.malitounik.fileprovider", file)
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
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
    }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    val file = File("${context.filesDir}/bibliatekaPdf/$fileName")
    var backPressHandled by remember { mutableStateOf(false) }
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    BackHandler(!backPressHandled || fullscreen) {
        if (fullscreen) {
            fullscreen = false
        } else {
            backPressHandled = true
            navController.popBackStack()
        }
    }
    LaunchedEffect(Unit) {
        val page = k.getInt(fileName, 0)
        lazyListState.scrollToItem(page)
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
                        Text(
                            modifier = Modifier.clickable {
                                maxLine.intValue = Int.MAX_VALUE
                                coroutineScope.launch {
                                    delay(5000L)
                                    maxLine.intValue = 1
                                }
                            },
                            text = title.uppercase(),
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = Settings.fontInterface.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
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
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "by.carkva_gazeta.malitounik.fileprovider",
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
                                    fullscreen = true
                                },
                                text = {
                                    Text(stringResource(R.string.fullscreen), fontSize = (Settings.fontInterface - 2).sp)
                                },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.fullscreen),
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    0.dp,
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            var zoomAll by remember { mutableFloatStateOf(1f) }
            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }
            if (zoomAll == 1f) {
                offsetX = 0f
                offsetY = 0f
            }
            val density = LocalDensity.current
            val pager = remember { PdfViewModel(PdfBitmapConverter(density.density, file)) }
            val modifier = Modifier
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
            LaunchedEffect(lazyListState) {
                snapshotFlow { lazyListState.firstVisibleItemIndex }.collect { page ->
                    k.edit {
                        putInt(fileName, page)
                    }
                    pageState = (page + 1).toString() + "/" + pager.itemsCount()
                }
            }
            when (val state = pager.displayState.collectAsState().value) {
                is PdfDisplayState.AllLoadedContent -> {
                    LazyColumn(
                        modifier = modifier,
                        state = lazyListState
                    ) {
                        item {
                            Spacer(modifier = Modifier.padding(top = innerPadding.calculateTopPadding()))
                        }
                        items(state.list.size) { page ->
                            PdfPage(page = state.list[page])
                        }
                        item {
                            Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                        }
                    }
                }

                is PdfDisplayState.Error -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding(), start = 10.dp),
                        text = stringResource(R.string.error_ch2),
                        fontSize = Settings.fontInterface.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                PdfDisplayState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                is PdfDisplayState.PartiallyLoadedContent -> {
                    val items = state.flow.collectAsLazyPagingItems()
                    when (items.loadState.refresh) {
                        is LoadState.Error -> {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding(), start = 10.dp),
                                text = stringResource(R.string.error_ch2),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        LoadState.Loading -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }

                        is LoadState.NotLoading -> {
                            LazyColumn(
                                modifier = modifier,
                                state = lazyListState
                            ) {
                                item {
                                    Spacer(modifier = Modifier.padding(top = innerPadding.calculateTopPadding()))
                                }
                                items(items.itemCount) { index ->
                                    val page = items[index]
                                    if (page != null) {
                                        PdfPage(page = page)
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
        }
    }
}

@Composable
private fun PdfPage(page: Bitmap) {
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
    )
}