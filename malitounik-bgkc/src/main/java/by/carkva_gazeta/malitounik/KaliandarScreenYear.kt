package by.carkva_gazeta.malitounik

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import by.carkva_gazeta.malitounik.views.HtmlText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun KaliandarScreenYear(
    coroutineScope: CoroutineScope,
    lazyColumnState: LazyListState,
    innerPadding: PaddingValues,
    navigateToSvityiaView: (svity: Boolean, position: Int) -> Unit,
) {
    val data = Settings.data
    val state by remember { derivedStateOf { lazyColumnState.firstVisibleItemIndex } }
    if (state != 0) Settings.caliandarPosition = data[state][25].toInt()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp),
        state = lazyColumnState
    ) {
        coroutineScope.launch {
            lazyColumnState.scrollToItem(Settings.caliandarPosition)
        }
        items(data.size) { index ->
            var text = ""
            var colorBlackboard = Divider
            var colorText = PrimaryText
            var colorIcon = MaterialTheme.colorScheme.secondary
            if (data[index][7].toInt() == 2) {
                if (data[index][0].toInt() == Calendar.FRIDAY) text =
                    stringResource(id = R.string.Post)
                colorBlackboard = Post
            }
            if (data[index][7].toInt() == 1) {
                if (data[index][0].toInt() == Calendar.FRIDAY) text =
                    stringResource(id = R.string.No_post_n)
                colorBlackboard = BezPosta
            }
            if (data[index][7].toInt() == 3 && !(data[index][0].toInt() == Calendar.SUNDAY || data[index][0].toInt() == Calendar.SATURDAY)) {
                text = stringResource(R.string.Strogi_post_n)
                colorBlackboard = StrogiPost
                colorText = PrimaryTextBlack
                colorIcon = MaterialTheme.colorScheme.primary
            }
            if (data[index][5].toInt() > 0) {
                colorBlackboard = Primary
                colorText = PrimaryTextBlack
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(colorBlackboard)
                    .padding(2.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
            ) {
                var title =
                    stringArrayResource(R.array.dni_nedeli_up)[data[index][0].toInt()] + ", " + data[index][1] + " " + stringArrayResource(
                        R.array.meciac
                    )[data[index][2].toInt()]
                val c = Calendar.getInstance()
                if (c[Calendar.YEAR] != data[index][3].toInt()) title =
                    title + " " + data[index][3].toInt()
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorBlackboard)
                        .padding(5.dp),
                    text = title,
                    fontSize = Settings.fontInterface.sp,
                    color = colorText,
                    textAlign = TextAlign.Center
                )
                if (text.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
                    ) {
                        if (data[index][7].toInt() != 1) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(22.dp), painter = painterResource(R.drawable.fishe), contentDescription = "", tint = colorIcon
                            )
                        }
                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                }
                if (data[index][5].toInt() > 0) {
                    val padding1 = if (data[index][4] != "no_sviatyia") 0.dp
                    else 10.dp
                    val svaity = data[index][6]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = padding1)
                            .clickable(svaity.contains("уваход у ерусалім", true) || svaity.contains("уваскрасеньне", true) || svaity.contains("узьнясеньне", true) || svaity.contains("зыход", true) || svaity.contains("Айцоў першых 6-ці Ўсяленскіх сабораў", true)) {
                                navigateToSvityiaView(true, index)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var padding = 0.dp
                        if (data[index][5].toInt() == 1 || data[index][5].toInt() == 2) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.znaki_krest_v_kruge),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(22.dp)
                                )
                            }
                            padding = 35.dp
                        }
                        if (data[index][6].isNotEmpty()) {
                            val weight = if (data[index][5].toInt() == 1 || data[index][0].toInt() == Calendar.SUNDAY) FontWeight.Bold
                            else FontWeight.Normal
                            var color = MaterialTheme.colorScheme.primary
                            if (data[index][6].contains("Пачатак") || data[index][6].contains(
                                    "Вялікі"
                                ) || data[index][6].contains("Вялікая") || data[index][6].contains("ВЕЧАР") || data[index][6].contains(
                                    "Палова"
                                )
                            ) {
                                color = MaterialTheme.colorScheme.secondary
                            }
                            val svaity = loadOpisanieSviat(LocalContext.current, index)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(svaity.isNotEmpty()) {
                                        navigateToSvityiaView(true, index)
                                    }
                                    .padding(end = padding),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    fontWeight = weight,
                                    text = data[index][6],
                                    color = color,
                                    textAlign = TextAlign.Center,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                    }
                }
                if (data[index][8].isNotEmpty()) {
                    HtmlText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        text = data[index][8],
                        fontSize = Settings.fontInterface.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
                if (data[index][4].isNotEmpty()) {
                    val list = data[index][4].split("<br>")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Settings.vibrate()
                                navigateToSvityiaView(false, index)
                            }
                    ) {
                        for (i in list.indices) {
                            val toppadding = if (i == 0) 10.dp else 0.dp
                            val toppaddingEnd = if (i == list.size - 1) 10.dp else 0.dp
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = toppadding, start = 10.dp, bottom = toppaddingEnd),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var icon: Painter? = null
                                var iconTint = MaterialTheme.colorScheme.primary
                                when (data[index][12].toInt()) {
                                    1 -> icon = painterResource(R.drawable.znaki_krest)
                                    3 -> icon = painterResource(R.drawable.znaki_krest_v_polukruge)
                                    4 -> icon = painterResource(R.drawable.znaki_ttk)
                                    5 -> {
                                        icon = painterResource(R.drawable.znaki_ttk_black)
                                        iconTint = MaterialTheme.colorScheme.secondary
                                    }
                                }
                                var tpadd = 0.dp
                                if (icon != null && i == 0) {
                                    Icon(
                                        painter = icon,
                                        contentDescription = "",
                                        tint = iconTint,
                                        modifier = Modifier
                                            .size(22.dp)
                                    )
                                    tpadd = 10.dp
                                }
                                HtmlText(
                                    modifier = Modifier
                                        .padding(start = tpadd, end = 10.dp)
                                        .align(Alignment.CenterVertically),
                                    text = list[i],
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                    }
                }
                if (data[index][18].toInt() == 1 || data[index][21].isNotEmpty()) {
                    val textPamAndBlas = if (data[index][18].toInt() == 1) stringResource(R.string.pamerlyia)
                    else data[index][21]
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp, start = 10.dp), text = textPamAndBlas, color = MaterialTheme.colorScheme.secondary, fontStyle = FontStyle.Italic, fontSize = Settings.fontInterface.sp
                    )
                }
                var svityDrugasnuia = AnnotatedString.Builder("").apply {
                    val context = LocalContext.current
                    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                    if (k.getBoolean("s_pkc", false) && data[index][19] != "") {
                        if (data[index][19].isNotEmpty()) {
                            append(data[index][19])
                            append("\n\n")
                        }
                    }
                    if (k.getBoolean("s_pravas", false) && data[index][14].isNotEmpty()) {
                        if (data[index][14].isNotEmpty()) {
                            append(data[index][14])
                            append("\n\n")
                        }
                    }
                    if (k.getBoolean("s_gosud", false)) {
                        if (data[index][16].isNotEmpty()) {
                            append(data[index][16])
                            append("\n\n")
                        }
                        if (data[index][15].isNotEmpty()) {
                            val svityDrugasnuiaLength = this.length
                            val sviata = data[index][15]
                            append(sviata)
                            addStyle(
                                SpanStyle(color = MaterialTheme.colorScheme.primary),
                                svityDrugasnuiaLength,
                                this.length
                            )
                            append("\n\n")
                        }
                    }
                    if (k.getBoolean("s_pafesii", false) && data[index][17].isNotEmpty()) {
                        if (data[index][17].isNotEmpty()) {
                            append(data[index][17])
                            append("\n\n")
                        }
                    }
                }.toAnnotatedString()
                if (svityDrugasnuia.isNotEmpty()) {
                    val t1 = svityDrugasnuia.lastIndexOf("\n\n")
                    if (t1 != -1) {
                        svityDrugasnuia = svityDrugasnuia.subSequence(0, t1)
                    }
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .align(Alignment.CenterVertically),
                            text = svityDrugasnuia,
                            textAlign = TextAlign.End,
                            fontStyle = FontStyle.Italic,
                            fontSize = Settings.fontInterface.sp,
                            color = SecondaryText
                        )
                    }
                }
            }
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}