package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    navigateToSearchBible: (String) -> Unit = { },
    navigateToCytanniList: (String, String) -> Unit = { _, _ -> }
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
            val edit = k.edit()
            edit.putString("perevodBibileMenu", savePerevod)
            edit.apply()
        }
    }
    var dialogVisable by remember { mutableStateOf(false) }
    if (dialogVisable) {
        DialogSemuxa(pagerState.currentPage == 0) {
            dialogVisable = false
        }
    }
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
                        val edit = k.edit()
                        edit.putString("perevodBibileMenu", savePerevod)
                        edit.apply()
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = Settings.fontInterface.sp,
                            lineHeight = 18.sp * 1.15f,
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
                /*TextButton(
            onClick = {
                navigationActions.navigateToVybranaeList(savePerevod)
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
            Text(stringResource(R.string.str_short_label1), fontSize = Settings.fontInterface.sp, color = PrimaryText)
        }*/
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
                }
                /*TextButton(
            onClick = {
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
            Text(stringResource(R.string.zakladki_bible), fontSize = Settings.fontInterface.sp, color = PrimaryText)
        }
        TextButton(
            onClick = {
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
            Text(stringResource(R.string.natatki_biblii), fontSize = Settings.fontInterface.sp, color = PrimaryText)
        }*/
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
                            color = PrimaryText
                        )
                    }
                }
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
                HtmlText(text = reader.readText(), fontSize = Settings.fontInterface.sp)
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