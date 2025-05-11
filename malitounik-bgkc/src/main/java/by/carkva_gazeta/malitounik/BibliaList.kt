package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliaList(
    navController: NavHostController,
    isNovyZapavet: Boolean,
    perevod: String,
    navigateToCytanniList: (String, String) -> Unit = { _, _ -> }
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    val title = when (perevod) {
        Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia)
        Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun)
        Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski)
        Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
        Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal)
        else -> stringResource(R.string.kaliandar2)
    }
    if (Settings.bibleTime) {
        Settings.bibleTime = false
        Settings.bibleTimeList = true
        val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prevodName = when (perevod) {
            Settings.PEREVODSEMUXI -> "biblia"
            Settings.PEREVODBOKUNA -> "bokuna"
            Settings.PEREVODCARNIAUSKI -> "carniauski"
            Settings.PEREVODSINOIDAL -> "sinaidal"
            else -> "biblia"
        }
        val knigaText = k.getString("bible_time_${prevodName}_kniga", "Быц") ?: "Быц"
        val glava = k.getInt("bible_time_${prevodName}_glava", 0)
        navigateToCytanniList("$knigaText ${glava + 1}", perevod)
    }
    val subTitle = if (isNovyZapavet) stringResource(R.string.novy_zapaviet)
    else stringResource(R.string.stary_zapaviet)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    var backPressHandled by remember { mutableStateOf(false) }
    BackHandler(!backPressHandled) {
        navController.popBackStack()
        AppNavGraphState.bibleListPosition = -1
        backPressHandled = true
    }
    Scaffold(
        topBar = {
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
                            text = title.uppercase(),
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = Settings.fontInterface.sp
                        )
                        if (perevod != Settings.PEREVODNADSAN) {
                            Text(
                                modifier = Modifier.clickable {
                                    maxLine.intValue = Int.MAX_VALUE
                                    coroutineScope.launch {
                                        delay(5000L)
                                        maxLine.intValue = 1
                                    }
                                },
                                text = subTitle,
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontWeight = FontWeight.Bold,
                                maxLines = maxLine.intValue,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                            AppNavGraphState.bibleListPosition = -1
                            backPressHandled = true
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        val lazyColumnState = rememberLazyListState()
        val bibleList = bibleCount(perevod, isNovyZapavet)
        val collapsedState = remember(bibleList) { bibleList.map { true }.toMutableStateList() }
        LaunchedEffect(Unit) {
            if (AppNavGraphState.bibleListPosition != -1) {
                collapsedState[AppNavGraphState.bibleListPosition] = false
                lazyColumnState.scrollToItem(AppNavGraphState.bibleListPosition)
            }
        }
        BoxWithConstraints(
            modifier = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                0.dp
            )
        ) {
            val parentConstraints = this.constraints
            LazyColumn(
                state = lazyColumnState
            ) {
                bibleList.forEachIndexed { i, dataItem ->
                    val collapsed = collapsedState[i]
                    if (perevod != Settings.PEREVODNADSAN) {
                        item(key = "header_$i") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        collapsedState[i] = !collapsed
                                        CoroutineScope(Dispatchers.Main).launch {
                                            lazyColumnState.scrollToItem(i)
                                        }
                                    }
                            ) {
                                Icon(
                                    painter = if (collapsed)
                                        painterResource(R.drawable.keyboard_arrow_down)
                                    else
                                        painterResource(R.drawable.keyboard_arrow_up),
                                    contentDescription = "",
                                    tint = Divider,
                                )
                                Text(
                                    dataItem.title,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(1f),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                            HorizontalDivider()
                        }
                    } else {
                        collapsedState[i] = false
                    }
                    if (!collapsedState[i]) {
                        items(1) {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .layout { measurable, constraints ->
                                        val placeable = measurable.measure(
                                            constraints.copy(maxHeight = parentConstraints.maxHeight)
                                        )

                                        layout(placeable.width, placeable.height) {
                                            placeable.placeRelative(0, 0)
                                        }
                                    },
                                columns = GridCells.Adaptive(60.dp)
                            ) {
                                items(dataItem.count) { item ->
                                    Box(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .clip(shape = RoundedCornerShape(10.dp))
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.secondary,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(Divider)
                                            .clickable {
                                                AppNavGraphState.bibleListPosition = i
                                                navigateToCytanniList(
                                                    dataItem.subTitle + " " + (item + 1).toString(),
                                                    perevod
                                                )
                                            }
                                    ) {
                                        Text(
                                            (item + 1).toString(),
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(5.dp),
                                            textAlign = TextAlign.Center,
                                            color = PrimaryText,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
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

@Composable
fun bibleCount(perevod: String, isNovyZapavet: Boolean): ArrayList<BibliaList> {
    val result = ArrayList<BibliaList>()
    when (perevod) {
        Settings.PEREVODSEMUXI -> {
            if (isNovyZapavet) {
                val list = stringArrayResource(R.array.semuxan)
                result.addAll(setNovyZapavet(list, perevod))
            } else {
                val list = stringArrayResource(R.array.semuxas)
                result.addAll(setStaryZapavet(list, perevod))
            }
        }

        Settings.PEREVODBOKUNA -> {
            if (isNovyZapavet) {
                val list = stringArrayResource(R.array.bokunan)
                result.addAll(setNovyZapavet(list, perevod))
            } else {
                val list = stringArrayResource(R.array.bokunas)
                result.addAll(setStaryZapavet(list, perevod))
            }
        }

        Settings.PEREVODCARNIAUSKI -> {
            if (isNovyZapavet) {
                val list = stringArrayResource(R.array.charniauskin)
                result.addAll(setNovyZapavet(list, perevod))
            } else {
                val list = stringArrayResource(R.array.charniauskis)
                result.addAll(setStaryZapavet(list, perevod))
            }
        }

        Settings.PEREVODNADSAN -> {
            val list = stringArrayResource(R.array.psalter_list)
            result.addAll(setStaryZapavet(list, perevod))
        }

        Settings.PEREVODSINOIDAL -> {
            if (isNovyZapavet) {
                val list = stringArrayResource(R.array.sinoidaln)
                result.addAll(setNovyZapavet(list, perevod))
            } else {
                val list = stringArrayResource(R.array.sinoidals)
                result.addAll(setStaryZapavet(list, perevod))
            }
        }

        else -> {
            if (isNovyZapavet) {
                val list = stringArrayResource(R.array.semuxan)
                result.addAll(setNovyZapavet(list, perevod))
            } else {
                val list = stringArrayResource(R.array.semuxas)
                result.addAll(setStaryZapavet(list, perevod))
            }
        }
    }
    return result
}

fun bibleCount(kniga: Int, perevod: String): Int {
    val result: Int
    if (perevod == Settings.PEREVODNADSAN) {
        result = 151
        return result
    }
    result = when (kniga) {
        0 -> 50
        1 -> 40
        2 -> 27
        3 -> 36
        4 -> 34
        5 -> 24
        6 -> 21
        7 -> 4
        8 -> 31
        9 -> 24
        10 -> 22
        11 -> 25
        12 -> 29
        13 -> 37
        14 -> 10
        15 -> 13
        16 -> 9
        17 -> 14
        18 -> 16
        19 -> 10
        20 -> {
            if (perevod == Settings.PEREVODSINOIDAL) 43
            else 42
        }

        21 -> {
            if (perevod == Settings.PEREVODSEMUXI) 151
            else 150
        }

        22 -> 31
        23 -> 12
        24 -> 8
        25 -> 19
        26 -> 51
        27 -> 66
        28 -> 52
        29 -> 5
        30 -> 1
        31 -> {
            if (perevod == Settings.PEREVODCARNIAUSKI) 6
            else 5
        }

        32 -> 48
        33 -> 14
        34 -> 14
        35 -> 3
        36 -> 9
        37 -> 1
        38 -> 4
        39 -> 7
        40 -> 3
        41 -> 3
        42 -> 3
        43 -> 2
        44 -> 14
        45 -> 4
        46 -> 16
        47 -> 15
        48 -> 7
        49 -> 16
        50 -> 28
        51 -> 16
        52 -> 24
        53 -> 21
        54 -> 28
        55 -> 5
        56 -> 5
        57 -> 3
        58 -> 5
        59 -> 1
        60 -> 1
        61 -> 1
        62 -> 16
        63 -> 16
        64 -> 13
        65 -> 6
        66 -> 6
        67 -> 4
        68 -> 4
        69 -> 5
        70 -> 3
        71 -> 6
        72 -> 4
        73 -> 3
        74 -> 1
        75 -> 13
        76 -> 22
        else -> 1
    }
    return result
}

fun setStaryZapavet(list: Array<String>, perevod: String): ArrayList<BibliaList> {
    val result = ArrayList<BibliaList>()
    if (perevod == Settings.PEREVODNADSAN) {
        result.add(BibliaList(list[0], "Пс", 151))
        return result
    }
    result.add(BibliaList(list[0], "Быц", 50))
    result.add(BibliaList(list[1], "Вых", 40))
    result.add(BibliaList(list[2], "Ляв", 27))
    result.add(BibliaList(list[3], "Лікі", 36))
    result.add(BibliaList(list[4], "Дрг", 34))
    result.add(BibliaList(list[5], "Нав", 24))
    result.add(BibliaList(list[6], "Суд", 21))
    result.add(BibliaList(list[7], "Рут", 4))
    result.add(BibliaList(list[8], "1 Цар", 31))
    result.add(BibliaList(list[9], "2 Цар", 24))
    result.add(BibliaList(list[10], "3 Цар", 22))
    result.add(BibliaList(list[11], "4 Цар", 25))
    result.add(BibliaList(list[12], "1 Лет", 29))
    result.add(BibliaList(list[13], "2 Лет", 37))
    result.add(BibliaList(list[14], "1 Эзд", 10))
    result.add(BibliaList(list[15], "Нээм", 13))
    if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODCARNIAUSKI) {
        result.add(BibliaList(list[16], "Эст", 10))
        result.add(BibliaList(list[17], "Ёва", 42))
        if (perevod == Settings.PEREVODSEMUXI)
            result.add(BibliaList(list[18], "Пс", 151))
        else
            result.add(BibliaList(list[18], "Пс", 150))
        result.add(BibliaList(list[19], "Высл", 31))
        result.add(BibliaList(list[20], "Экл", 12))
        result.add(BibliaList(list[21], "Псн", 8))
        result.add(BibliaList(list[22], "Іс", 66))
        result.add(BibliaList(list[23], "Ер", 52))
        result.add(BibliaList(list[24], "Плач", 5))
        result.add(BibliaList(list[25], "Езк", 48))
        result.add(BibliaList(list[26], "Дан", 14))
        result.add(BibliaList(list[27], "Ас", 14))
        result.add(BibliaList(list[28], "Ёіл", 3))
        result.add(BibliaList(list[29], "Ам", 9))
        result.add(BibliaList(list[30], "Аўдз", 1))
        result.add(BibliaList(list[31], "Ёны", 4))
        result.add(BibliaList(list[32], "Міх", 7))
        result.add(BibliaList(list[33], "Нвм", 3))
        result.add(BibliaList(list[34], "Абк", 3))
        result.add(BibliaList(list[35], "Саф", 3))
        result.add(BibliaList(list[36], "Аг", 2))
        result.add(BibliaList(list[37], "Зах", 14))
        result.add(BibliaList(list[38], "Мал", 4))
        if (perevod == Settings.PEREVODCARNIAUSKI) {
            result.add(BibliaList(list[39], "Тав", 14))
            result.add(BibliaList(list[40], "Юдт", 16))
            result.add(BibliaList(list[41], "Мдр", 19))
            result.add(BibliaList(list[42], "Сір", 51))
            result.add(BibliaList(list[43], "Бар", 6))
            result.add(BibliaList(list[44], "1 Мак", 16))
            result.add(BibliaList(list[45], "2 Мак", 15))
        }
    } else {
        result.add(BibliaList(list[16], "2 Эзд", 9))
        result.add(BibliaList(list[17], "Тав", 14))
        result.add(BibliaList(list[18], "Юдт", 16))
        result.add(BibliaList(list[19], "Эст", 10))
        result.add(BibliaList(list[20], "Ёва", 43))
        result.add(BibliaList(list[21], "Пс", 151))
        result.add(BibliaList(list[22], "Высл", 31))
        result.add(BibliaList(list[23], "Экл", 12))
        result.add(BibliaList(list[24], "Псн", 8))
        result.add(BibliaList(list[25], "Мдр", 19))
        result.add(BibliaList(list[26], "Сір", 51))
        result.add(BibliaList(list[27], "Іс", 66))
        result.add(BibliaList(list[28], "Ер", 52))
        result.add(BibliaList(list[29], "Плач", 5))
        result.add(BibliaList(list[30], "Пасл Ер", 1))
        result.add(BibliaList(list[31], "Бар", 5))
        result.add(BibliaList(list[32], "Езк", 48))
        result.add(BibliaList(list[33], "Дан", 14))
        result.add(BibliaList(list[34], "Ас", 14))
        result.add(BibliaList(list[35], "Ёіл", 3))
        result.add(BibliaList(list[36], "Ам", 9))
        result.add(BibliaList(list[37], "Аўдз", 1))
        result.add(BibliaList(list[38], "Ёны", 4))
        result.add(BibliaList(list[39], "Міх", 7))
        result.add(BibliaList(list[40], "Нвм", 3))
        result.add(BibliaList(list[41], "Абк", 3))
        result.add(BibliaList(list[42], "Саф", 3))
        result.add(BibliaList(list[43], "Аг", 2))
        result.add(BibliaList(list[44], "Зах", 14))
        result.add(BibliaList(list[45], "Мал", 4))
        result.add(BibliaList(list[46], "1 Мак", 16))
        result.add(BibliaList(list[47], "2 Мак", 15))
        result.add(BibliaList(list[48], "3 Мак", 7))
        result.add(BibliaList(list[49], "3 Эзд", 16))
    }
    return result
}

fun setNovyZapavet(list: Array<String>, perevod: String): ArrayList<BibliaList> {
    val result = ArrayList<BibliaList>()
    result.add(BibliaList(list[0], "Мц", 28))
    result.add(BibliaList(list[1], "Мк", 16))
    result.add(BibliaList(list[2], "Лк", 24))
    result.add(BibliaList(list[3], "Ян", 21))
    result.add(BibliaList(list[4], "Дз", 28))
    if (perevod != Settings.PEREVODCARNIAUSKI) {
        result.add(BibliaList(list[5], "Як", 5))
        result.add(BibliaList(list[6], "1 Пт", 5))
        result.add(BibliaList(list[7], "2 Пт", 3))
        result.add(BibliaList(list[8], "1 Ян", 5))
        result.add(BibliaList(list[9], "2 Ян", 1))
        result.add(BibliaList(list[10], "3 Ян", 1))
        result.add(BibliaList(list[11], "Юды", 1))
        result.add(BibliaList(list[12], "Рым", 16))
        result.add(BibliaList(list[13], "1 Кар", 16))
        result.add(BibliaList(list[14], "2 Кар", 13))
        result.add(BibliaList(list[15], "Гал", 6))
        result.add(BibliaList(list[16], "Эф", 6))
        result.add(BibliaList(list[17], "Плп", 4))
        result.add(BibliaList(list[18], "Клс", 4))
        result.add(BibliaList(list[19], "1 Фес", 5))
        result.add(BibliaList(list[20], "2 Фес", 3))
        result.add(BibliaList(list[21], "1 Цім", 6))
        result.add(BibliaList(list[22], "2 Цім", 4))
        result.add(BibliaList(list[23], "Ціт", 3))
        result.add(BibliaList(list[24], "Флм", 1))
        result.add(BibliaList(list[25], "Гбр", 13))
    } else {
        result.add(BibliaList(list[5], "Рым", 16))
        result.add(BibliaList(list[6], "1 Кар", 16))
        result.add(BibliaList(list[7], "2 Кар", 13))
        result.add(BibliaList(list[8], "Гал", 6))
        result.add(BibliaList(list[9], "Эф", 6))
        result.add(BibliaList(list[10], "Плп", 4))
        result.add(BibliaList(list[11], "Клс", 4))
        result.add(BibliaList(list[12], "1 Фес", 5))
        result.add(BibliaList(list[13], "2 Фес", 3))
        result.add(BibliaList(list[14], "1 Цім", 6))
        result.add(BibliaList(list[15], "2 Цім", 4))
        result.add(BibliaList(list[16], "Ціт", 3))
        result.add(BibliaList(list[17], "Флм", 1))
        result.add(BibliaList(list[18], "Гбр", 13))
        result.add(BibliaList(list[19], "Як", 5))
        result.add(BibliaList(list[20], "1 Пт", 5))
        result.add(BibliaList(list[21], "2 Пт", 3))
        result.add(BibliaList(list[22], "1 Ян", 5))
        result.add(BibliaList(list[23], "2 Ян", 1))
        result.add(BibliaList(list[24], "3 Ян", 1))
        result.add(BibliaList(list[25], "Юды", 1))
    }
    result.add(BibliaList(list[26], "Адкр", 22))
    return result
}

data class BibliaList(val title: String, val subTitle: String, val count: Int)