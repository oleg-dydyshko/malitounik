package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import java.util.Calendar
import java.util.GregorianCalendar

@Composable
fun SviatyList(navController: NavHostController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    LazyColumn {
        for (i in 1..6) {
            val listSviat = getPrazdnik(context, i)
            items(listSviat.size) { index ->
                Column {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                val calendar = Calendar.getInstance()
                                calendar[Calendar.DAY_OF_YEAR] = listSviat[index].dayOfYear
                                for (e in Settings.data.indices) {
                                    if (calendar[Calendar.DATE] == Settings.data[e][1].toInt() && calendar[Calendar.MONTH] == Settings.data[e][2].toInt() && calendar[Calendar.YEAR] == Settings.data[e][3].toInt()) {
                                        Settings.caliandarPosition = e
                                        break
                                    }
                                }
                                if (k.getBoolean(
                                        "caliandarList",
                                        false
                                    )
                                ) navigationActions.navigateToKaliandarYear()
                                else navigationActions.navigateToKaliandar()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp, 12.dp),
                            painter = painterResource(R.drawable.krest),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Column {
                            if (index == 0) {
                                val title = when (i) {
                                    1 -> {
                                        listSviat[index].opisanie
                                    }

                                    2 -> {
                                        context.getString(R.string.dvunad_sv)
                                    }

                                    3 -> {
                                        context.getString(R.string.vial_sv)
                                    }

                                    4 -> {
                                        context.getString(R.string.dni_yspamin_pam)
                                    }

                                    5 -> {
                                        context.getString(R.string.carkva_pamiat_data)
                                    }

                                    6 -> {
                                        context.getString(R.string.parafia_sv)
                                    }

                                    else -> {
                                        listSviat[index].opisanie
                                    }
                                }
                                Text(
                                    text = title,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                            if (!(index == 0 && i == 1)) {
                                val t1 = listSviat[index].opisanie.indexOf(":")
                                val annotatedString = if (t1 != -1) {
                                    buildAnnotatedString {
                                        append(listSviat[index].opisanie)
                                        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t1 + 1)
                                    }
                                } else {
                                    AnnotatedString(listSviat[index].opisanie)
                                }
                                Text(
                                    text = annotatedString,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                                    fontWeight = if (i < 3) FontWeight.Bold else FontWeight.Normal,
                                    color = if (i < 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                            Text(
                                text = listSviat[index].opisanieData,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

fun getPrazdnik(
    context: Context,
    razdel: Int,
    yearG: Int = Calendar.getInstance().get(Calendar.YEAR)
): ArrayList<Prazdniki> {
    val a = yearG % 19
    val b = yearG % 4
    val cx = yearG % 7
    val ks = yearG / 100
    val p = (13 + 8 * ks) / 25
    val q = ks / 4
    val m = (15 - p + ks - q) % 30
    val n = (4 + ks - q) % 7
    val d = (19 * a + m) % 30
    val ex = (2 * b + 4 * cx + 6 * d + n) % 7
    val monthP: Int
    var dataP: Int
    if (d + ex <= 9) {
        dataP = d + ex + 22
        monthP = 3
    } else {
        dataP = d + ex - 9
        if (d == 29 && ex == 6) dataP = 19
        if (d == 28 && ex == 6) dataP = 18
        monthP = 4
    }
    val monthName = context.resources.getStringArray(R.array.meciac_smoll)
    val nedelName = context.resources.getStringArray(R.array.dni_nedeli)
    val prazdnikiAll = ArrayList<Prazdniki>()
    val c = GregorianCalendar(yearG, monthP - 1, dataP)
    when (razdel) {
        1 -> {
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S1),
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + " " + yearG + " году, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    1,
                )
            )
        }

        2 -> {
            c.set(yearG, Calendar.JANUARY, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S2),
                    "6 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, 1, 2)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S3),
                    "2 лютага, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.MARCH, 25)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S4),
                    "25 сакавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            c.add(Calendar.DATE, -7)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S5),
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.add(Calendar.DATE, 46)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S6),
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.add(Calendar.DATE, 10)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S7),
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.AUGUST, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S8),
                    "6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.AUGUST, 15)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S9),
                    "15 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 8)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S10),
                    "8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 14)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S11),
                    "14 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.NOVEMBER, 21)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S12),
                    "21 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.DECEMBER, 25)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S13),
                    "25 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
        }

        3 -> {
            c.set(yearG, Calendar.JANUARY, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S14),
                    "1 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.JUNE, 24)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S15),
                    "24 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.JUNE, 29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S16),
                    "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.AUGUST, 29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S17),
                    "29 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.OCTOBER, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S18),
                    "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
        }

        4 -> {
            c.add(Calendar.DATE, -57)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Мясапусная задушная субота",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -50)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін усіх сьвятых айцоў, манахаў і посьнікаў",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Субота 3-га тыдня Вялікага посту",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -22)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Субота 4-га тыдня Вялікага посту",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 9)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Радаўніца",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 48)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Траецкая бацькоўская субота",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            for (i in 19..25) {
                c.set(yearG, Calendar.OCTOBER, i)
                val dayofweek = c[Calendar.DAY_OF_WEEK]
                if (7 == dayofweek) {
                    prazdnikiAll.add(
                        Prazdniki(
                            c[Calendar.DAY_OF_YEAR],
                            "Зьмітраўская бацькоўская субота",
                            c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                        )
                    )
                }
            }
            c.set(yearG, Calendar.NOVEMBER, 2)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Дзяды, дзень успаміну памёрлых",
                    "2 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
        }

        5 -> {
            c.set(yearG, Calendar.JULY, 11)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін мучаніцкай сьмерці ў катэдры сьв. Сафіі ў Полацку 5 манахаў-базыльянаў",
                    "11 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    5,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 15)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін Бабровіцкай трагедыі (зьнішчэньне ў 1942 г. жыхароў уніяцкай парафіі в. Бабровічы Івацэвіцкага р-ну)",
                    "15 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    5,
                )
            )
            c.set(yearG, Calendar.OCTOBER, 18)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін Берасьцейскай царкоўнай Уніі 1596 году",
                    "18(8) кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    5,
                )
            )
        }

        6 -> {
            c.set(yearG, Calendar.JANUARY, 30)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Гомель: Трох Сьвяціцеляў",
                    "30 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Антвэрпан: Уваскрасеньня Хрыстовага",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Віцебск: Уваскрасеньня Хрыстовага",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MARCH, 28)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Пінск: сьвятога Кірылы Тураўскага",
                    "28 красавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Маладэчна: Хрыста Чалавекалюбцы",
                    "1 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 7)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Івацэвічы: Маці Божай Жыровіцкай",
                    "7 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 11)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Баранавічы: сьвятых роўнаапостальных Кірылы і Мятода",
                    "11 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 13)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Горадня: Маці Божай Фацімскай",
                    "13 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            c.add(Calendar.DATE, 56)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Слонім: Сьвятой Тройцы",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.add(Calendar.DATE, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: Сьвятога Духа",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.JUNE, 27)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: Маці Божай Нястомнай Дапамогі",
                    "27 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.JUNE, 29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Лондан: сьвятых апосталаў Пятра і Паўла",
                    "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Жодзіна: сьвятых апосталаў Пятра і Паўла",
                    "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            val chisla = intArrayOf(24, 25, 26, 27, 28, 29, 30)
            var brest = 24
            for (aChisla in chisla) {
                val cal = GregorianCalendar(yearG, 5, aChisla)
                val deyNed = cal[Calendar.DAY_OF_WEEK]
                if (deyNed == Calendar.SATURDAY) brest = aChisla
            }
            c.set(yearG, Calendar.JUNE, brest)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Берасьце: сьвятых братоў-апосталаў Пятра і Андрэя",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.JULY, 24)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Наваградак: сьв. Барыса і Глеба",
                    "24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Полацак: манастыр сьв. Барыса і Глеба",
                    "24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.AUGUST, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Заслаўе: Перамяненьня Гасподняга",
                    "6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 8)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Магілёў: Бялыніцкай іконы Маці Божай",
                    "8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 16)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Ліда: сьвятамучаніка Язафата Полацкага",
                    "16 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.OCTOBER, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Ворша: Покрыва Найсьвяцейшай Багародзіцы",
                    "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Мар’іна Горка: Покрыва Найсьвяцейшай Багародзіцы",
                    "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.NOVEMBER, 8)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Барысаў: сьвятога Арханёла Міхаіла",
                    "8 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.NOVEMBER, 12)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Полацак: сьвятамучаніка Язафата",
                    "12 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.DECEMBER, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: сьвятога Мікалая Цудатворцы",
                    "6 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            val dayofweekrastvo =
                GregorianCalendar(yearG, Calendar.DECEMBER, 25)[Calendar.DAY_OF_WEEK]
            val chislaJiazep = intArrayOf(26, 27, 28, 29, 30, 31)
            var minsk = 26
            for (aChisla in chislaJiazep) {
                val deyNed =
                    GregorianCalendar(yearG, Calendar.DECEMBER, aChisla)[Calendar.DAY_OF_WEEK]
                if (dayofweekrastvo != Calendar.SUNDAY) {
                    if (deyNed == Calendar.SUNDAY) minsk = aChisla
                } else {
                    if (deyNed == Calendar.MONDAY) minsk = aChisla
                }
            }
            c.set(yearG, Calendar.DECEMBER, minsk)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: праведнага Язэпа",
                    c[Calendar.DATE].toString() + " сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
        }
    }
    prazdnikiAll.sortBy { it.dayOfYear }
    return prazdnikiAll
}

data class Prazdniki(
    val dayOfYear: Int,
    val opisanie: String,
    val opisanieData: String,
    val typeSviat: Int
)