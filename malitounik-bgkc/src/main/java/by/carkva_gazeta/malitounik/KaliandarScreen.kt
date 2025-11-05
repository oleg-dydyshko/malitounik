@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Divider2
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost2
import by.carkva_gazeta.malitounik.views.HtmlText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Calendar
import java.util.GregorianCalendar

@Composable
fun measureTextWidth(text: String, fontSize: TextUnit): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style = TextStyle(fontSize = fontSize, fontWeight = FontWeight.Bold)).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

@Composable
fun KaliandarScreen(
    position: Int,
    innerPadding: PaddingValues,
    navigateToCytanneList: (String, String, Int) -> Unit,
    navigateToSvityiaView: (svity: Boolean, position: Int) -> Unit,
    navigateToBogaslujbovyia: (title: String, resurs: String) -> Unit,
    navigateToKniga: () -> Unit
) {
    val data = Settings.data[position]
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        var text = ""
        var colorBlackboard = Divider
        var colorBlackboardBook = Divider2
        var colorText = PrimaryText
        if (data[7].toInt() == 2) {
            if (data[0].toInt() == Calendar.FRIDAY) text = stringResource(id = R.string.Post)
            colorBlackboard = Post
            colorBlackboardBook = Post
        }
        if (data[7].toInt() == 1) {
            if (data[0].toInt() == Calendar.FRIDAY) text = stringResource(id = R.string.No_post)
            colorBlackboard = BezPosta
            colorBlackboardBook = BezPosta
        }
        if (data[7].toInt() == 3 && !(data[0].toInt() == Calendar.SUNDAY || data[0].toInt() == Calendar.SATURDAY)) {
            text = stringResource(id = R.string.Strogi_post)
            colorBlackboard = StrogiPost
            colorBlackboardBook = StrogiPost2
            colorText = PrimaryTextBlack
        }
        if (data[5].toInt() > 0) {
            colorBlackboard = Primary
            colorBlackboardBook = MaterialTheme.colorScheme.primary
            colorText = PrimaryTextBlack
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.size(80.dp, 100.dp), verticalArrangement = Arrangement.Top) {
                if (data[20] != "0" && data[0].toInt() == Calendar.SUNDAY) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                val trapary = getTraparyKandakiNiadzelnyia()
                                navigateToBogaslujbovyia(trapary[data[20].toInt() - 1].title, trapary[data[20].toInt() - 1].resource)
                            }, text = "Тон ${data[20]}", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontSize = Settings.fontInterface.sp
                    )
                } else if (data[7].toInt() > 0) {
                    if (text != "") {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally), text = text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                    }
                    if (data[7].toInt() > 1) {
                        if (data[7].toInt() == 3 || data[0].toInt() == Calendar.FRIDAY) {
                            val color = if (data[7].toInt() == 3) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                            Icon(
                                painter = painterResource(R.drawable.fishe), contentDescription = "", tint = color, modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
            val mounth = if (Calendar.getInstance()[Calendar.YEAR] == data[3].toInt()) stringArrayResource(
                id = R.array.meciac
            )[data[2].toInt()]
            else stringArrayResource(id = R.array.meciac)[data[2].toInt()] + ", " + data[3]
            var dateColumnWidth = measureTextWidth(mounth, fontSize = Settings.fontInterface.sp)
            if (dateColumnWidth < 140.dp) dateColumnWidth = 140.dp
            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = dateColumnWidth)
                    .background(colorBlackboard), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Bold, color = colorText, text = stringArrayResource(R.array.dni_nedeli)[data[0].toInt()], fontSize = Settings.fontInterface.sp
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally), text = data[1], fontSize = 80.sp, fontWeight = FontWeight.Bold, color = colorText
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(Alignment.CenterHorizontally), text = mounth, fontWeight = FontWeight.Bold, color = colorText, fontSize = Settings.fontInterface.sp
                )
                if (k.getBoolean("adminDayInYear", false)) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(Alignment.CenterHorizontally), text = stringResource(R.string.admin_show_day_in_year, data[24], data[22]), color = colorText, fontSize = Settings.fontInterface.sp
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.book_red), contentDescription = "", tint = colorBlackboardBook, modifier = Modifier
                    .padding(start = 50.dp)
                    .align(Alignment.Bottom)
                    .size(30.dp)
                    .clickable {
                        navigateToKniga()
                    })
        }
        if (data[5].toInt() > 0 || data[6].isNotEmpty()) {
            val padding1 = if (data[4].isNotEmpty()) 0.dp
            else 10.dp
            val svaity = loadOpisanieSviat(context, position)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = padding1)
                    .clickable(svaity.isNotEmpty()) {
                        navigateToSvityiaView(true, position)
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                var padding = 0.dp
                if (data[5].toInt() == 1 || data[5].toInt() == 2) {
                    Icon(
                        painter = painterResource(R.drawable.znaki_krest_v_kruge), tint = MaterialTheme.colorScheme.primary, contentDescription = "", modifier = Modifier
                            .padding(end = 10.dp)
                            .size(22.dp)
                    )
                    padding = 35.dp
                }
                if (data[6].isNotEmpty()) {
                    val weight = if (data[5].toInt() == 1 || data[0].toInt() == Calendar.SUNDAY || (data[22].toInt() in 1..6)) FontWeight.Bold
                    else FontWeight.Normal
                    var color = MaterialTheme.colorScheme.primary
                    if (data[6].contains("Пачатак") || data[6].contains(
                            "Вялікі"
                        ) || data[6].contains("Вялікая") || data[6].contains("ВЕЧАР") || data[6].contains(
                            "Палова"
                        )
                    ) {
                        color = MaterialTheme.colorScheme.secondary
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = padding), contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(), fontWeight = weight, text = data[6], color = color, textAlign = TextAlign.Center, fontSize = Settings.fontInterface.sp
                        )
                    }
                }
            }
        }
        if (data[8].isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp), horizontalArrangement = Arrangement.Center
            ) {
                HtmlText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .padding(bottom = if (data[4].isEmpty()) 10.dp else 0.dp), text = data[8], color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center, fontSize = Settings.fontInterface.sp
                )
            }
        }
        if (data[4].isNotEmpty()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
                    .size(1.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            val list = data[4].split("<br>")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateToSvityiaView(false, position)
                    }) {
                for (i in list.indices) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        var icon: Painter? = null
                        var iconTint = MaterialTheme.colorScheme.primary
                        when (data[12].toInt()) {
                            1 -> icon = painterResource(R.drawable.znaki_krest)
                            3 -> icon = painterResource(R.drawable.znaki_krest_v_polukruge)
                            4 -> icon = painterResource(R.drawable.znaki_ttk)
                            5 -> {
                                icon = painterResource(R.drawable.znaki_ttk_black)
                                iconTint = MaterialTheme.colorScheme.secondary
                            }
                        }
                        if (icon != null && i == 0) {
                            Box(modifier = Modifier.padding(start = 10.dp)) {
                                Icon(
                                    painter = icon, contentDescription = "", tint = iconTint, modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        HtmlText(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .align(Alignment.CenterVertically), text = list[i], fontSize = Settings.fontInterface.sp
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .size(1.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(5.dp))
                .background(colorBlackboard)
                .padding(vertical = 10.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp), text = stringResource(R.string.chytanne), fontStyle = FontStyle.Italic, color = colorText, fontSize = Settings.fontInterface.sp
            )
            if (data[9].isNotEmpty()) {
                val title = stringResource(
                    R.string.czytanne3, data[1].toInt(), stringArrayResource(R.array.meciac_smoll)[data[2].toInt()]
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                        .clickable { navigateToCytanneList(title, removeZnakiAndSlovy(data[9]), Settings.CHYTANNI_LITURGICHNYIA) }, text = data[9], color = colorText, fontSize = Settings.fontInterface.sp
                )
            }
            if (isLiturgia(data)) {
                if (data[10].isNotEmpty()) {
                    val title = stringResource(
                        R.string.czytanne3, data[1].toInt(), stringArrayResource(R.array.meciac_smoll)[data[2].toInt()]
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .clickable { navigateToCytanneList(title, removeZnakiAndSlovy(data[10]), Settings.CHYTANNI_LITURGICHNYIA) }, text = data[10], fontStyle = FontStyle.Italic, color = colorText, fontSize = Settings.fontInterface.sp
                    )
                }
                if (data[11].isNotEmpty()) {
                    val title = stringResource(
                        R.string.czytanne3, data[1].toInt(), stringArrayResource(R.array.meciac_smoll)[data[2].toInt()]
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .clickable { navigateToCytanneList(title, removeZnakiAndSlovy(data[11]), Settings.CHYTANNI_LITURGICHNYIA) }, text = data[11], fontStyle = FontStyle.Italic, color = colorText, fontSize = Settings.fontInterface.sp
                    )
                }
            }
            if (data[18].toInt() == 1 || data[21].isNotEmpty()) {
                val textPamAndBlas = if (data[18].toInt() == 1) stringResource(R.string.pamerlyia)
                else data[21]
                Text(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp), text = textPamAndBlas, color = colorText, fontStyle = FontStyle.Italic, fontSize = Settings.fontInterface.sp
                )
            }
        }
        if (k.getBoolean("maranafa", false)) {
            Spacer(Modifier.size(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(colorBlackboard)
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp), text = stringResource(id = R.string.maranata), fontStyle = FontStyle.Italic, color = colorText, fontSize = Settings.fontInterface.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                val title = stringResource(
                    R.string.maranata2, data[1].toInt(), stringArrayResource(R.array.meciac_smoll)[data[2].toInt()]
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable { navigateToCytanneList(title, data[13], Settings.CHYTANNI_MARANATA) }, text = data[13], color = colorText, fontSize = Settings.fontInterface.sp
                )
            }
        }
        val padzeia = setListPadzeia(context)
        if (padzeia.isNotEmpty()) {
            val gc = Calendar.getInstance() as GregorianCalendar
            for (index in padzeia.indices) {
                val p = padzeia[index]
                val r1 = p.dat.split(".")
                val r2 = p.datK.split(".")
                gc[r1[2].toInt(), r1[1].toInt() - 1] = r1[0].toInt()
                gc[r2[2].toInt(), r2[1].toInt() - 1] = r2[0].toInt()
                val yaerw = gc[Calendar.YEAR]
                gc[r1[2].toInt(), r1[1].toInt() - 1] = r1[0].toInt()
                var dayofyear = gc[Calendar.DAY_OF_YEAR]
                if (!gc.isLeapYear(yaerw) && dayofyear > 59) {
                    dayofyear++
                }
                if (dayofyear == data[24].toInt() && gc[Calendar.YEAR] == data[3].toInt()) {
                    val title = p.padz
                    val data2 = p.dat
                    val time = p.tim
                    val dataK = p.datK
                    val timeK = p.timK
                    val paz = p.paznic
                    var res = stringResource(R.string.sabytie_no_pavedam)
                    val konecSabytie = p.konecSabytie
                    val realTime = Calendar.getInstance().timeInMillis
                    var paznicia = false
                    if (paz != 0L) {
                        gc.timeInMillis = paz
                        var nol1 = ""
                        var nol2 = ""
                        var nol3 = ""
                        if (gc[Calendar.DATE] < 10) nol1 = "0"
                        if (gc[Calendar.MONTH] < 9) nol2 = "0"
                        if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                        res = stringResource(
                            R.string.sabytie_pavedam, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR], gc[Calendar.HOUR_OF_DAY], nol3, gc[Calendar.MINUTE]
                        )
                        if (realTime > paz) paznicia = true
                    }
                    val spannable = if (!konecSabytie) {
                        stringResource(
                            R.string.sabytieKali, data2, time, res
                        )
                    } else {
                        stringResource(
                            R.string.sabytieDoKuda, data2, time, dataK, timeK, res
                        )
                    }
                    SetPadzeia(title, spannable, p.color, paznicia, res)
                }
            }
        }
        var svityDrugasnuia = AnnotatedString.Builder("").apply {
            if (k.getBoolean("s_pkc", false) && data[19] != "") {
                if (data[19].isNotEmpty()) {
                    append(data[19])
                    append("\n\n")
                }
            }
            if (k.getBoolean("s_pravas", false) && data[14].isNotEmpty()) {
                if (data[14].isNotEmpty()) {
                    append(data[14])
                    append("\n\n")
                }
            }
            if (k.getBoolean("s_gosud", false)) {
                if (data[16].isNotEmpty()) {
                    append(data[16])
                    append("\n\n")
                }
                if (data[15].isNotEmpty()) {
                    val svityDrugasnuiaLength = this.length
                    val sviata = data[15]
                    append(sviata)
                    addStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary), svityDrugasnuiaLength, this.length
                    )
                    append("\n\n")
                }
            }
            if (k.getBoolean("s_pafesii", false) && data[17].isNotEmpty()) {
                if (data[17].isNotEmpty()) {
                    append(data[17])
                    append("\n\n")
                }
            }
        }.toAnnotatedString()
        if (svityDrugasnuia.isNotEmpty()) {
            val t1 = svityDrugasnuia.lastIndexOf("\n\n")
            if (t1 != -1) {
                svityDrugasnuia = svityDrugasnuia.subSequence(0, t1)
            }
            Spacer(Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .align(Alignment.CenterVertically), text = svityDrugasnuia, textAlign = TextAlign.End, fontStyle = FontStyle.Italic, fontSize = Settings.fontInterface.sp, color = SecondaryText
                )
            }
        }
        Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
    }
}

fun setListPadzeia(context: Context): ArrayList<Padzeia> {
    val padzeia = ArrayList<Padzeia>()
    val gson = Gson()
    val type = TypeToken.getParameterized(ArrayList::class.java, Padzeia::class.java).type
    val dir = File(context.filesDir.toString() + "/Sabytie")
    if (dir.exists()) {
        dir.walk().forEach { file ->
            if (file.isFile && file.exists()) {
                val inputStream = FileReader(file)
                val reader = BufferedReader(inputStream)
                reader.forEachLine {
                    val line = it.trim()
                    if (line != "") {
                        val t1 = line.split(" ")
                        try {
                            if (t1.size == 11) padzeia.add(
                                Padzeia(
                                    t1[0].replace("_", " "), t1[1], t1[2], t1[3].toLong(), t1[4].toInt(), t1[5], t1[6], t1[7], t1[8].toInt(), t1[9], 0, false
                                )
                            ) else padzeia.add(
                                Padzeia(
                                    t1[0].replace("_", " "), t1[1], t1[2], t1[3].toLong(), t1[4].toInt(), t1[5], t1[6], t1[7], t1[8].toInt(), t1[9], t1[11].toInt(), false
                                )
                            )
                        } catch (_: Throwable) {
                            file.delete()
                        }
                    }
                }
                inputStream.close()
            }
        }
        val file = File(context.filesDir.toString() + "/Sabytie.json")
        file.writer().use {
            it.write(gson.toJson(padzeia, type))
        }
        dir.deleteRecursively()
    } else {
        val file = File(context.filesDir.toString() + "/Sabytie.json")
        if (file.exists()) {
            try {
                padzeia.addAll(gson.fromJson(file.readText(), type))
            } catch (_: Throwable) {
                file.delete()
            }
        }
    }
    padzeia.sort()
    return padzeia
}

@Composable
fun SetPadzeia(title: String, apisanne: String, color: Int, raznica: Boolean, res: String) {
    Spacer(Modifier.size(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(5.dp))
            .background(Divider), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var message by remember { mutableStateOf(false) }
        val colors = stringArrayResource(R.array.colors)
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(colors[color].toColorInt()))
                    .clickable { message = !message }, horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    val image = if (message) painterResource(R.drawable.keyboard_arrow_up)
                    else painterResource(R.drawable.keyboard_arrow_down)
                    Image(
                        painter = image, contentDescription = "", modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), text = title, fontWeight = FontWeight.Bold, color = PrimaryTextBlack, fontSize = Settings.fontInterface.sp
                )
            }
            if (message) {
                val t1 = apisanne.indexOf(res)
                val newApisanne = AnnotatedString.Builder(apisanne).apply {
                    if (raznica) {
                        addStyle(
                            SpanStyle(color = Primary), t1, apisanne.length
                        )
                    }
                }.toAnnotatedString()
                Text(
                    modifier = Modifier.padding(start = 10.dp), text = newApisanne, color = PrimaryText, fontSize = Settings.fontInterface.sp
                )
            }
        }
    }
}

fun removeZnakiAndSlovy(ctenie: String): String {
    var cytanne = ctenie
    cytanne = cytanne.replace("\n", ";")
    cytanne = cytanne.replace("[", "")
    cytanne = cytanne.replace("?", "")
    cytanne = cytanne.replace("!", "")
    cytanne = cytanne.replace("(", "")
    cytanne = cytanne.replace(")", "")
    cytanne = cytanne.replace("#", "")
    cytanne = cytanne.replace("\"", "")
    cytanne = cytanne.replace(":", "")
    cytanne = cytanne.replace("|", "")
    cytanne = cytanne.replace("]", "")
    cytanne = cytanne.replace("Тон 1.", "")
    cytanne = cytanne.replace("Тон 2.", "")
    cytanne = cytanne.replace("Тон 3.", "")
    cytanne = cytanne.replace("Тон 4.", "")
    cytanne = cytanne.replace("Тон 5.", "")
    cytanne = cytanne.replace("Тон 6.", "")
    cytanne = cytanne.replace("Тон 7.", "")
    cytanne = cytanne.replace("Тон 8.", "")
    cytanne = cytanne.replace("Ганьне", "")
    cytanne = cytanne.replace("Вялікія гадзіны", "")
    cytanne = cytanne.replace("На асьвячэньне вады", "")
    cytanne = cytanne.replace("Багародзіцы", "")
    cytanne = cytanne.replace("Дабравешчаньне", "")
    cytanne = cytanne.replace("Сустрэчы", "")
    cytanne = cytanne.replace("Літургіі няма", "")
    cytanne = cytanne.replace("На вячэрні", "")
    cytanne = cytanne.replace("Строгі пост", "")
    cytanne = cytanne.replace("Вялікі", "")
    cytanne = cytanne.replace("канон", "")
    cytanne = cytanne.replace("Чын", "")
    cytanne = cytanne.replace("паднясеньня", "")
    cytanne = cytanne.replace("Пачэснага", "")
    cytanne = cytanne.replace("Крыжа", "")
    cytanne = cytanne.replace("Андрэя", "")
    cytanne = cytanne.replace("Крыцкага", "")
    cytanne = cytanne.replace("Літургія", "")
    cytanne = cytanne.replace("раней", "")
    cytanne = cytanne.replace("асьвячаных", "")
    cytanne = cytanne.replace("дароў", "")
    cytanne = cytanne.replace("Яна", "")
    cytanne = cytanne.replace("Яну", "")
    cytanne = cytanne.replace("Залатавуснага", "")
    cytanne = cytanne.replace("сьвятога", "")
    cytanne = cytanne.replace("Васіля", "")
    cytanne = cytanne.replace("Вялікага", "")
    cytanne = cytanne.replace("Блаславеньне", "")
    cytanne = cytanne.replace("вербаў", "")
    cytanne = cytanne.replace("з вячэрняй", "")
    cytanne = cytanne.replace("На ютрані", "")
    cytanne = cytanne.replace("Посту няма", "")
    cytanne = cytanne.replace("Пам.", "")
    cytanne = cytanne.replace("Перадсьв.", "")
    cytanne = cytanne.replace("Сьв.", "", true)
    cytanne = cytanne.replace("Вялеб.", "")
    cytanne = cytanne.replace("Пакл.", "")
    cytanne = cytanne.replace("Багар.", "")
    cytanne = cytanne.replace("Вялікамуч.", "")
    cytanne = cytanne.replace("Ап.", "")
    cytanne = cytanne.replace("Айцам.", "")
    cytanne = cytanne.replace("Айцам", "")
    cytanne = cytanne.replace("Прар.", "")
    cytanne = cytanne.replace("Муч.", "")
    cytanne = cytanne.replace("Крыжу", "")
    cytanne = cytanne.replace("Вобр.", "")
    cytanne = cytanne.replace("Новаму году.", "")
    cytanne = cytanne.replace("Вял.", "")
    cytanne = cytanne.replace("Арх.", "")
    cytanne = cytanne.replace("Абнаўл.", "")
    cytanne = cytanne.replace("Сьвятамуч.", "")
    cytanne = cytanne.replace("Саб.", "")
    cytanne = cytanne.replace("Першамуч.", "")
    cytanne = cytanne.replace("Суб.", "")
    cytanne = cytanne.replace("Нядз.", "")
    cytanne = cytanne.replace("Абр", "")
    cytanne = cytanne.replace("Снч.", "")
    cytanne = cytanne.trim()
    return cytanne
}