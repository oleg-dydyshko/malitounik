package by.carkva_gazeta.malitounik

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import by.carkva_gazeta.malitounik.ui.theme.BackgroundTolBarDark
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryDark
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import by.carkva_gazeta.malitounik.ui.theme.TitleCalendarMounth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar

fun getFindPage(mounth: Int, year: Int): Int {
    var calPas = Settings.caliandarPosition
    for (find in 0 until Settings.data.size) {
        if (Settings.data[find][2].toInt() == mounth && Settings.data[find][3].toInt() == year) {
            calPas = Settings.data[find][23].toInt()
            break
        }
    }
    return calPas
}

@Composable
fun KaliandarScreenMounth(
    colorBlackboard: Color = Primary,
    setPageCaliandar: (Int) -> Unit,
    close: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .background(colorBlackboard)
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Top)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorBlackboard)
                    .clickable {
                        close()
                    }) {
                val tint = if (colorBlackboard == Primary || colorBlackboard == StrogiPost || colorBlackboard == BackgroundTolBarDark) PrimaryTextBlack
                else PrimaryText
                Icon(modifier = Modifier.align(Alignment.Start), painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = tint)
            }
            val initDate = Settings.data[Settings.caliandarPosition]
            var mun1 by remember { mutableIntStateOf(initDate[2].toInt()) }
            var year1 by remember { mutableIntStateOf(initDate[3].toInt()) }
            val initPage =
                (initDate[3].toInt() - Settings.GET_CALIANDAR_YEAR_MIN) * 12 + initDate[2].toInt()
            val pagerState = rememberPagerState(pageCount = {
                (Settings.GET_CALIANDAR_YEAR_MAX - Settings.GET_CALIANDAR_YEAR_MIN + 1) * 12
            }, initialPage = initPage)
            val fling = PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(1)
            )
            var expanded by remember { mutableStateOf(false) }
            var expanded2 by remember { mutableStateOf(false) }
            if (expanded) {
                DialogSetDataCaliandar(pagerState, mun1, year1, true) {
                    expanded = false
                }
            }
            if (expanded2) {
                DialogSetDataCaliandar(pagerState, mun1, year1, false) {
                    expanded2 = false
                }
            }
            val list = stringArrayResource(R.array.meciac2)
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(modifier = Modifier.padding(10.dp)) {
                    TextButton(
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier
                            .padding(5.dp),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            stringResource(R.string.vybor_mun),
                            fontSize = Settings.fontInterface.sp,
                            color = PrimaryText
                        )
                    }
                }
                Box(modifier = Modifier.padding(10.dp)) {
                    TextButton(
                        onClick = {
                            expanded2 = true
                        },
                        modifier = Modifier
                            .padding(5.dp),
                        colors = ButtonColors(
                            Divider,
                            Color.Unspecified,
                            Color.Unspecified,
                            Color.Unspecified
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            stringResource(R.string.vybor_year),
                            fontSize = Settings.fontInterface.sp,
                            color = PrimaryText
                        )
                    }
                }
            }
            var textMounthYear by remember { mutableStateOf(list[mun1] + ", $year1") }
            Text(
                textMounthYear, modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = Settings.fontInterface.sp
            )
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    var calPas = Settings.caliandarPosition
                    for (find in 0 until Settings.data.size) {
                        if (Settings.data[find][23].toInt() == page) {
                            calPas = Settings.data[find][25].toInt()
                            break
                        }
                    }
                    mun1 = Settings.data[calPas][2].toInt()
                    year1 = Settings.data[calPas][3].toInt()
                    textMounthYear = list[mun1] + ", $year1"
                }
            }
            HorizontalPager(
                pageSpacing = 10.dp,
                state = pagerState,
                flingBehavior = fling,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(10.dp)
            ) { page ->
                var calPas = Settings.caliandarPosition
                for (find in 0 until Settings.data.size) {
                    if (Settings.data[find][23].toInt() == page) {
                        calPas = Settings.data[find][25].toInt()
                        break
                    }
                }
                val mun = Settings.data[calPas][2].toInt()
                val year = Settings.data[calPas][3].toInt()
                val c = Calendar.getInstance()
                val munTudey = mun == c[Calendar.MONTH] && year == c[Calendar.YEAR]
                val calendarFull = GregorianCalendar(year, mun, 1)
                val wik = calendarFull[Calendar.DAY_OF_WEEK]
                val munAll = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendarFull.add(Calendar.MONTH, -1)
                val oldMunAktual = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
                var oldDay = oldMunAktual - wik + 1
                var day: String
                var i = 0
                var newDay = 0
                var end = 42
                if (42 - (munAll + wik) >= 6) {
                    end -= 7
                }
                if (munAll + wik == 29) {
                    end -= 7
                }
                var e = 1
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.ndz),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(Primary)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                        Text(
                            stringResource(R.string.pn),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(TitleCalendarMounth)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                        Text(
                            stringResource(R.string.au),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(TitleCalendarMounth)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                        Text(
                            stringResource(R.string.sp),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(TitleCalendarMounth)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                        Text(
                            stringResource(R.string.ch),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(TitleCalendarMounth)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                        Text(
                            stringResource(R.string.pt),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(TitleCalendarMounth)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                        Text(
                            stringResource(R.string.sb),
                            fontSize = Settings.fontInterface.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .background(TitleCalendarMounth)
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                    }
                    (1..end / 7).forEach {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            (1..7).forEach {
                                if (e < wik) {
                                    oldDay++
                                    day = "start"
                                } else if (e < munAll + wik) {
                                    i++
                                    day = i.toString()
                                } else {
                                    newDay++
                                    day = "end"
                                    i = 0
                                }
                                when (day) {
                                    "start" -> {
                                        val fon = if (e == 1) BezPosta
                                        else Divider
                                        Text(
                                            oldDay.toString(),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(1.dp)
                                                .background(fon)
                                                .padding(5.dp),
                                            textAlign = TextAlign.Center,
                                            color = SecondaryText,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }

                                    "end" -> {
                                        Text(
                                            newDay.toString(),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(1.dp)
                                                .background(Divider)
                                                .padding(5.dp),
                                            textAlign = TextAlign.Center,
                                            color = SecondaryText,
                                            fontSize = Settings.fontInterface.sp
                                        )
                                    }

                                    else -> {
                                        val bold =
                                            if (Settings.data[calPas + i - 1][4].contains("<font color=#d00505><strong>") || Settings.data[calPas + i - 1][5].toInt() == 1 || Settings.data[calPas + i - 1][5].toInt() == 3) FontWeight.Bold
                                            else FontWeight.Normal
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                            val color =
                                                if (Settings.data[calPas + i - 1][5].toInt() == 1 || Settings.data[calPas + i - 1][5].toInt() == 2) Primary
                                                else if (Settings.data[calPas + i - 1][5].toInt() == 3 || Settings.data[calPas + i - 1][7].toInt() == 1) BezPosta
                                                else if (Settings.data[calPas + i - 1][7].toInt() == 2) Post
                                                else if (Settings.data[calPas + i - 1][7].toInt() == 3) StrogiPost
                                                else Divider
                                            val color2 =
                                                if (Settings.data[calPas + i - 1][5].toInt() == 1 || Settings.data[calPas + i - 1][5].toInt() == 2 || Settings.data[calPas + i - 1][7].toInt() == 3) PrimaryTextBlack
                                                else PrimaryText
                                            val clickPos = calPas + i - 1
                                            Text(
                                                day,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        Settings.caliandarPosition = clickPos
                                                        setPageCaliandar(clickPos)
                                                    }
                                                    .padding(1.dp)
                                                    .background(PrimaryDark)
                                                    .padding(5.dp)
                                                    .background(color),
                                                textAlign = TextAlign.Center,
                                                fontWeight = bold,
                                                color = color2,
                                                fontSize = Settings.fontInterface.sp
                                            )
                                        } else {
                                            val color =
                                                if (Settings.data[calPas + i - 1][5].toInt() == 1 || Settings.data[calPas + i - 1][5].toInt() == 2) Primary
                                                else if (Settings.data[calPas + i - 1][5].toInt() == 3 || Settings.data[calPas + i - 1][7].toInt() == 1) BezPosta
                                                else if (Settings.data[calPas + i - 1][7].toInt() == 2) Post
                                                else if (Settings.data[calPas + i - 1][7].toInt() == 3) StrogiPost
                                                else Divider
                                            val color2 =
                                                if (Settings.data[calPas + i - 1][5].toInt() == 1 || Settings.data[calPas + i - 1][5].toInt() == 2 || Settings.data[calPas + i - 1][7].toInt() == 3) PrimaryTextBlack
                                                else PrimaryText
                                            val clickPos = calPas + i - 1
                                            Text(
                                                day,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        Settings.caliandarPosition = clickPos
                                                        setPageCaliandar(clickPos)
                                                    }
                                                    .padding(1.dp)
                                                    .background(color)
                                                    .padding(5.dp),
                                                textAlign = TextAlign.Center,
                                                fontWeight = bold,
                                                color = color2,
                                                fontSize = Settings.fontInterface.sp
                                            )
                                        }
                                    }
                                }
                                e++
                            }
                        }
                    }
                }
            }
            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    for (i in Settings.data.indices) {
                        if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
                            Settings.caliandarPosition = i
                            break
                        }
                    }
                    setPageCaliandar(Settings.caliandarPosition)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp),
                colors = ButtonColors(
                    Divider,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.search_call), fontSize = Settings.fontInterface.sp, color = PrimaryText)
            }
        }
    }
}

@Composable
fun DialogSetDataCaliandar(
    pagerState: PagerState,
    mun: Int,
    year: Int,
    isMun: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column {
                Text(
                    text = if (isMun) stringResource(R.string.vybor_mun) else stringResource(R.string.vybor_year), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                if (isMun) {
                    val list = stringArrayResource(R.array.meciac2)
                    for (i in list.indices) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp)
                                    .clickable {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            pagerState.scrollToPage(getFindPage(i, year))
                                        }
                                        android.util.Log.d("Oleg", getFindPage(mun, i).toString() + " " + mun + " " + i)
                                        onDismiss()
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                                )
                                Text(
                                    text = list[i], modifier = Modifier
                                        .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                } else {
                    for (i in Settings.GET_CALIANDAR_YEAR_MIN..Settings.GET_CALIANDAR_YEAR_MAX) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp)
                                    .clickable {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            pagerState.scrollToPage(getFindPage(mun, i))
                                        }
                                        onDismiss()
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                                )
                                Text(
                                    text = i.toString(), modifier = Modifier
                                        .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
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
                    Text(stringResource(R.string.cansel), fontSize = 18.sp)
                }
            }
        }
    }
}