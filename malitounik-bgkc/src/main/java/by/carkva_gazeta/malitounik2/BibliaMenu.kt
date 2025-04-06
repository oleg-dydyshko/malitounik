package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.ui.theme.Primary
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik2.ui.theme.TitleCalendarMounth
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import by.carkva_gazeta.malitounik2.views.HtmlText
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun BibliaMenu(
    navController: NavHostController,
    innerPadding: PaddingValues,
    navigateToSearchBible: (String) -> Unit,
    navigateToCytanniList: (String, String) -> Unit,
    navigateToBogaslujbovyia: (String, Int) -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val perevod by remember {
        mutableStateOf(
            k.getString(
                "perevodBibileMenu",
                Settings.PEREVODSEMUXI
            ) ?: Settings.PEREVODSEMUXI
        )
    }
    val list = if (k.getBoolean("sinoidal_bible", false)) {
        listOf(
            stringResource(R.string.title_biblia2),
            stringResource(R.string.title_biblia_bokun2),
            stringResource(R.string.title_psalter),
            stringResource(R.string.title_biblia_charniauski2),
            stringResource(R.string.bsinaidal2)
        )
    } else {
        listOf(
            stringResource(R.string.title_biblia2),
            stringResource(R.string.title_biblia_bokun2),
            stringResource(R.string.title_psalter),
            stringResource(R.string.title_biblia_charniauski2)
        )
    }
    val getPerevod = when (perevod) {
        Settings.PEREVODSEMUXI -> 0
        Settings.PEREVODBOKUNA -> 1
        Settings.PEREVODNADSAN -> 2
        Settings.PEREVODCARNIAUSKI -> 3
        Settings.PEREVODSINOIDAL -> 4
        else -> 0
    }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = {
        list.size
    }, initialPage = getPerevod)
    var bibleTime by remember { mutableStateOf(false) }
    LaunchedEffect(bibleTime) {
        if (bibleTime) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                var newPerevod = Settings.PEREVODSEMUXI
                val prevodName = when (page) {
                    0 -> {
                        newPerevod = Settings.PEREVODSEMUXI
                        "biblia"
                    }

                    1 -> {
                        newPerevod = Settings.PEREVODBOKUNA
                        "bokuna"
                    }

                    2 -> {
                        newPerevod = Settings.PEREVODNADSAN
                        "nadsan"
                    }

                    3 -> {
                        newPerevod = Settings.PEREVODCARNIAUSKI
                        "carniauski"
                    }

                    4 -> {
                        newPerevod = Settings.PEREVODSINOIDAL
                        "sinaidal"
                    }

                    else -> "biblia"
                }
                val knigaText = k.getString("bible_time_${prevodName}_kniga", "Быц") ?: "Быц"
                val kniga = knigaBiblii(knigaText)
                Settings.bibleTime = true
                bibleTime = false
                navigationActions.navigateToBibliaList(kniga >= 50, newPerevod)
            }
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val savePerevod = when (page) {
                0 -> Settings.PEREVODSEMUXI
                1 -> Settings.PEREVODBOKUNA
                2 -> Settings.PEREVODNADSAN
                3 -> Settings.PEREVODCARNIAUSKI
                4 -> Settings.PEREVODSINOIDAL
                else -> Settings.PEREVODSEMUXI
            }
            k.edit {
                putString("perevodBibileMenu", savePerevod)
            }
        }
    }
    var dialogVisable by remember { mutableStateOf(false) }
    if (dialogVisable) {
        DialogSemuxa(pagerState.currentPage == 0) {
            dialogVisable = false
        }
    }
    var pesnyView by rememberSaveable { mutableStateOf(false) }
    var dialogPeryiadyView by rememberSaveable { mutableStateOf(false) }
    if (dialogPeryiadyView) {
        DialogPeryaidy {
            dialogPeryiadyView = false
        }
    }
    var dialogImageView by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SecondaryScrollableTabRow(
            modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(pagerState.currentPage),
                    width = Dp.Unspecified,
                    shape = RoundedCornerShape(
                        topStart = 5.dp,
                        topEnd = 5.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp,
                    )
                )
            }
        ) {
            list.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        val savePerevod = when (index) {
                            0 -> Settings.PEREVODSEMUXI
                            1 -> Settings.PEREVODBOKUNA
                            2 -> Settings.PEREVODNADSAN
                            3 -> Settings.PEREVODCARNIAUSKI
                            4 -> Settings.PEREVODSINOIDAL
                            else -> Settings.PEREVODSEMUXI
                        }
                        k.edit {
                            putString("perevodBibileMenu", savePerevod)
                        }
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = Settings.fontInterface.sp,
                            lineHeight = Settings.fontInterface.sp * 1.2f,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { page ->
            val savePerevod = when (page) {
                0 -> Settings.PEREVODSEMUXI
                1 -> Settings.PEREVODBOKUNA
                2 -> Settings.PEREVODNADSAN
                3 -> Settings.PEREVODCARNIAUSKI
                4 -> Settings.PEREVODSINOIDAL
                else -> Settings.PEREVODSEMUXI
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = {
                        navigationActions.navigateToBibliaList(false, savePerevod)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                        .size(width = 200.dp, height = Dp.Unspecified),
                    colors = ButtonColors(
                        Primary,
                        Color.Unspecified,
                        Color.Unspecified,
                        Color.Unspecified
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        if (savePerevod == Settings.PEREVODNADSAN) stringResource(R.string.psalter)
                        else stringResource(R.string.stary_zapaviet),
                        fontSize = Settings.fontInterface.sp,
                        color = PrimaryTextBlack
                    )
                }
                if (savePerevod != Settings.PEREVODNADSAN) {
                    TextButton(
                        onClick = {
                            navigationActions.navigateToBibliaList(true, savePerevod)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Primary,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.novy_zapaviet),
                            fontSize = Settings.fontInterface.sp,
                            color = PrimaryTextBlack
                        )
                    }
                }
                TextButton(
                    onClick = {
                        bibleTime = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                        .size(width = 200.dp, height = Dp.Unspecified),
                    colors = ButtonColors(
                        Divider,
                        Color.Unspecified,
                        Color.Unspecified,
                        Color.Unspecified
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.bible_time), fontSize = Settings.fontInterface.sp, color = PrimaryText)
                }
                TextButton(
                    onClick = {
                        navigateToSearchBible(savePerevod)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                        .size(width = 200.dp, height = Dp.Unspecified),
                    colors = ButtonColors(
                        Divider,
                        Color.Unspecified,
                        Color.Unspecified,
                        Color.Unspecified
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.poshuk), fontSize = Settings.fontInterface.sp, color = PrimaryText)
                }
                if (savePerevod == Settings.PEREVODNADSAN) {
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                            .size(width = 400.dp, height = Dp.Unspecified)
                    ) {
                        Row {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .weight(1f)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(TitleCalendarMounth)
                                    .padding(10.dp),
                                text = stringResource(R.string.kafizma),
                                fontSize = Settings.fontInterface.sp,
                                textAlign = TextAlign.Center,
                                color = PrimaryTextBlack
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 1..5) {
                                Text(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .weight(1f)
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .background(Divider)
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            val index = when (i) {
                                                1 -> "1"
                                                2 -> "9"
                                                3 -> "17"
                                                4 -> "24"
                                                5 -> "32"
                                                else -> "1"
                                            }
                                            navigateToCytanniList(
                                                "Пс $index",
                                                savePerevod
                                            )
                                        },
                                    text = i.toString(),
                                    fontSize = Settings.fontInterface.sp,
                                    textAlign = TextAlign.Center,
                                    color = PrimaryText
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 6..10) {
                                Text(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .weight(1f)
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .background(Divider)
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            val index = when (i) {
                                                6 -> "37"
                                                7 -> "46"
                                                8 -> "55"
                                                9 -> "64"
                                                10 -> "70"
                                                else -> "37"
                                            }
                                            navigateToCytanniList(
                                                "Пс $index",
                                                savePerevod
                                            )
                                        },
                                    text = i.toString(),
                                    fontSize = Settings.fontInterface.sp,
                                    textAlign = TextAlign.Center,
                                    color = PrimaryText
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 11..15) {
                                Text(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .weight(1f)
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .background(Divider)
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            val index = when (i) {
                                                11 -> "77"
                                                12 -> "85"
                                                13 -> "91"
                                                14 -> "101"
                                                15 -> "105"
                                                else -> "77"
                                            }
                                            navigateToCytanniList(
                                                "Пс $index",
                                                savePerevod
                                            )
                                        },
                                    text = i.toString(),
                                    fontSize = Settings.fontInterface.sp,
                                    textAlign = TextAlign.Center,
                                    color = PrimaryText
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 16..20) {
                                Text(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .weight(1f)
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .background(Divider)
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            val index = when (i) {
                                                16 -> "109"
                                                17 -> "118"
                                                18 -> "119"
                                                19 -> "134"
                                                20 -> "143"
                                                else -> "109"
                                            }
                                            navigateToCytanniList(
                                                "Пс $index",
                                                savePerevod
                                            )
                                        },
                                    text = i.toString(),
                                    fontSize = Settings.fontInterface.sp,
                                    textAlign = TextAlign.Center,
                                    color = PrimaryText
                                )
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            navigateToBogaslujbovyia(context.getString(R.string.malitva_pered), R.raw.nadsan_pered)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.malitva_pered), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                    }
                    TextButton(
                        onClick = {
                            navigateToBogaslujbovyia(context.getString(R.string.malitva_posle), R.raw.nadsan_posle)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.malitva_posle), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                    }
                    TextButton(
                        onClick = {
                            pesnyView = !pesnyView
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.pesni), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                    }
                    AnimatedVisibility(
                        pesnyView, enter = fadeIn(
                            tween(
                                durationMillis = 1000, easing = LinearOutSlowInEasing
                            )
                        ), exit = fadeOut(tween(durationMillis = 1000, easing = LinearOutSlowInEasing))
                    ) {
                        Column {
                            for (i in 1..9) {
                                Row(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .clickable {
                                            navigationActions.navigateToBogaslujbovyia(
                                                context.getString(R.string.pesnia, i),
                                                when (i) {
                                                    1 -> R.raw.nadsan_pesni_1
                                                    2 -> R.raw.nadsan_pesni_2
                                                    3 -> R.raw.nadsan_pesni_3
                                                    4 -> R.raw.nadsan_pesni_4
                                                    5 -> R.raw.nadsan_pesni_5
                                                    6 -> R.raw.nadsan_pesni_6
                                                    7 -> R.raw.nadsan_pesni_7
                                                    8 -> R.raw.nadsan_pesni_8
                                                    9 -> R.raw.nadsan_pesni_9
                                                    else -> R.raw.nadsan_pesni_1
                                                }
                                            )
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.size(12.dp, 12.dp),
                                        painter = painterResource(R.drawable.description),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = null
                                    )
                                    Text(
                                        stringResource(R.string.pesnia, i),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            dialogPeryiadyView = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.peryiady), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                    }
                    TextButton(
                        onClick = {
                            dialogImageView = !dialogImageView
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.title_psalter_privila), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                    }
                }
                AnimatedVisibility(
                    dialogImageView, enter = fadeIn(
                        tween(
                            durationMillis = 1000, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 1000, easing = LinearOutSlowInEasing))
                ) {
                    Image(painter = painterResource(R.drawable.pravily_chytannia_psaltyria), contentDescription = "", modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(), contentScale = ContentScale.FillWidth)
                }
                if (savePerevod == Settings.PEREVODSEMUXI || savePerevod == Settings.PEREVODBOKUNA) {
                    TextButton(
                        onClick = {
                            dialogVisable = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .size(width = 200.dp, height = Dp.Unspecified),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.alesyaSemukha2),
                            fontSize = Settings.fontInterface.sp,
                            color = PrimaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun DialogSemuxa(
    isSemuxa: Boolean,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.copyright), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.alesyaSemukha))
        },
        text = {
            val context = LocalContext.current
            val inputStream =
                if (isSemuxa) context.resources.openRawResource(R.raw.all_rights_reserved_semuxa)
                else context.resources.openRawResource(R.raw.all_rights_reserved_bokun)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                HtmlText(text = reader.readText(), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
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

@Composable
fun DialogPeryaidy(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.info), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.peryiady))
        },
        text = {
            val inputStream = LocalContext.current.resources.openRawResource(R.raw.nadsan_periody)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            HtmlText(
                modifier = Modifier.padding(start = 10.dp),
                text = reader.readText(),
                fontSize = Settings.fontInterface.sp
            )
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

@Composable
fun DialogImage(
    painter: Painter,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Image(painter = painter, contentDescription = "", Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()), contentScale = ContentScale.FillWidth)
        }
    }
    /*(
        icon = {
            Icon(painter = painterResource(R.drawable.info), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.peryiady))
        },
        text = {
            Image(painter = painter, contentDescription = "", Modifier.fillMaxWidth().verticalScroll(rememberScrollState()))
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
    )*/
}