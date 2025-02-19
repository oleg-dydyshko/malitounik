package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MalitvyListAll(
    navController: NavHostController,
    title: String,
    menuItem: Int,
    subTitle: String = ""
) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    val list = arrayOf(
        stringResource(R.string.prynad_1),
        stringResource(R.string.prynad_2),
        stringResource(R.string.prynad_3),
        stringResource(R.string.prynad_4),
        stringResource(R.string.prynad_5),
        stringResource(R.string.prynad_6)
    )
    val collapsedState = remember(list) { list.map { true }.toMutableStateList() }
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
                            text = title,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subTitle != "") {
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
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
                .fillMaxSize()
        ) {
            list.forEachIndexed { i, dataItem ->
                val collapsed = collapsedState[i]
                item(key = "header_$i") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                collapsedState[i] = !collapsed
                            }
                    ) {
                        Icon(
                            Icons.Default.run {
                                if (collapsed)
                                    KeyboardArrowDown
                                else
                                    KeyboardArrowUp
                            },
                            contentDescription = "",
                            tint = Divider,
                        )
                        Text(
                            dataItem,
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        visibilityThreshold = IntOffset.VisibilityThreshold
                                    )
                                )
                                .padding(10.dp)
                                .weight(1f),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider()
                }
                if (!collapsed) {
                    val subList = when (i) {
                        0 -> getPrynagodnyia1()
                        1 -> getPrynagodnyia2()
                        2 -> getPrynagodnyia3()
                        3 -> getPrynagodnyia4()
                        4 -> getPrynagodnyia5()
                        5 -> getPrynagodnyia6()
                        else -> getPrynagodnyia1()
                    }
                    items(subList.size) { index ->
                        Row(
                            modifier = Modifier
                                .padding(start = 30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                            Text(
                                subList[index].title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .clickable {
                                        navigationActions.navigateToBogaslujbovyia(subList[index].title, subList[index].resurs)
                                    },
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
            /*items(list.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp, 12.dp),
                        painter = painterResource(R.drawable.krest),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                    Text(
                        list[index],
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                HorizontalDivider()
            }*/
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}

fun getPrynagodnyia1(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Браслаўскай, Валадаркі Азёраў",
            R.raw.prynagodnyia_7
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Будслаўскай, Апякункі Беларусі",
            R.raw.prynagodnyia_8
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Нястомнай Дапамогі",
            R.raw.prynagodnyia_9
        )
    )
    list.add(BogaslujbovyiaListData("Малітва да Маці Божай Берасьцейскай", R.raw.prynagodnyia_30))
    list.add(BogaslujbovyiaListData("Малітва да Маці Божай Лагішынскай", R.raw.prynagodnyia_31))
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Маці Божай Будслаўскай",
            R.raw.mltv_mb_budslauskaja
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Божае Маці перад іконай Ейнай Менскай",
            R.raw.mltv_mb_mienskaja
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Найсьвяцейшай Дзевы Марыі Барунскай",
            R.raw.mltv_mb_barunskaja
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Багародзіцы, праслаўленай у цудатворнай Жыровіцкай іконе",
            R.raw.mltv_mb_zyrovickaja
        )
    )
    list.add(BogaslujbovyiaListData("Малітва да Маці Божай Бялыніцкай", R.raw.mltv_mb_bialynickaja))
    list.sortBy {
        it.title
    }
    return list
}

fun getPrynagodnyia2(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Малітва аб дапамозе ў выбары жыцьцёвай дарогі дзіцяці",
            R.raw.prynagodnyia_1
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва бацькоў за дзяцей («Божа, у Тройцы Адзіны...»)",
            R.raw.mltv_backou_za_dziaciej_boza_u_trojcy_adziny
        )
    )
    list.add(BogaslujbovyiaListData("Малітва бацькоў за дзяцей", R.raw.prynagodnyia_4))
    list.add(BogaslujbovyiaListData("Малітва за дарослых дзяцей", R.raw.prynagodnyia_11))
    list.add(BogaslujbovyiaListData("Малітва за бацькоў", R.raw.mltv_za_backou))
    list.add(BogaslujbovyiaListData("Малітва за хворае дзіця", R.raw.prynagodnyia_15))
    list.add(
        BogaslujbovyiaListData(
            "Малітва сям’і аб Божым бласлаўленьні на час адпачынку і вакацыяў",
            R.raw.prynagodnyia_33
        )
    )
    list.add(BogaslujbovyiaListData("Блаславеньне маці (Матчына малітва)", R.raw.prynagodnyia_40))
    list.add(BogaslujbovyiaListData("Малітва за хросьнікаў", R.raw.mltv_za_chrosnikau))
    list.add(BogaslujbovyiaListData("Малітва да сьв. Язэпа", R.raw.prynagodnyia_37))
    list.add(BogaslujbovyiaListData("Малітва мужа і бацькі да сьв. Язэпа", R.raw.prynagodnyia_38))
    list.add(BogaslujbovyiaListData("Малітва да сьв. Язэпа за мужчынаў", R.raw.prynagodnyia_39))
    list.sortBy {
        it.title
    }
    return list
}

fun getPrynagodnyia3(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва за Беларусь", R.raw.prynagodnyia_10))
    list.add(BogaslujbovyiaListData("Малітва за Айчыну - Ян Павел II", R.raw.prynagodnyia_36))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за ўсіх, што пацярпелі за Беларусь",
            R.raw.mltv_paciarpieli_za_bielarus
        )
    )
    list.sortBy {
        it.title
    }
    return list
}

fun getPrynagodnyia4(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва аб еднасьці", R.raw.mltv_ab_jednasci))
    list.add(BogaslujbovyiaListData("Малітва за парафію", R.raw.prynagodnyia_13))
    list.add(BogaslujbovyiaListData("Малітва за хрысьціянскую еднасьць", R.raw.prynagodnyia_16))
    list.add(
        BogaslujbovyiaListData(
            "Малітвы за сьвятароў і сьвятарскія пакліканьні",
            R.raw.prynagodnyia_24
        )
    )
    list.add(BogaslujbovyiaListData("Цябе, Бога, хвалім", R.raw.pesny_prasl_70))
    list.add(BogaslujbovyiaListData("Малітва за Царкву", R.raw.mltv_za_carkvu))
    list.add(BogaslujbovyiaListData("Малітва за Царкву 2", R.raw.mltv_za_carkvu_2))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за царкоўную еднасьць",
            R.raw.mltv_za_carkounuju_jednasc
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва разам з Падляшскімі мучанікамі аб еднасьці",
            R.raw.mltv_razam_z_padlaszskimi_muczanikami_ab_jednasci
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва аб еднасьці царквы (Экзарха Леаніда Фёдарава)",
            R.raw.mltv_ab_jednasci_carkvy_leanida_fiodarava
        )
    )
    list.add(BogaslujbovyiaListData("Малітва за нашую зямлю", R.raw.mltv_za_naszuju_ziamlu))
    list.sortBy {
        it.title
    }
    return list
}

fun getPrynagodnyia5(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Малітва за хворага («Міласэрны Божа»)",
            R.raw.mltv_za_chvoraha_milaserny_boza
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва за хворага («Лекару душ і целаў»)",
            R.raw.mltv_za_chvoraha_lekaru_dush_cielau
        )
    )
    list.add(BogaslujbovyiaListData("Малітва ў часе хваробы", R.raw.mltv_u_czasie_chvaroby))
    list.add(BogaslujbovyiaListData("Малітва падчас згубнай пошасьці", R.raw.prynagodnyia_28))
    list.sortBy {
        it.title
    }
    return list
}

fun getPrynagodnyia6(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва перад пачаткам навучаньня", R.raw.prynagodnyia_21))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за дзяцей перад пачаткам навукі",
            R.raw.prynagodnyia_12
        )
    )
    list.add(BogaslujbovyiaListData("Малітва вучняў перад навучаньнем", R.raw.prynagodnyia_29))
    list.add(BogaslujbovyiaListData("Малітва вучня", R.raw.prynagodnyia_6))
    list.add(BogaslujbovyiaListData("Малітвы за памерлых", R.raw.mltv_za_pamierlych))
    list.add(BogaslujbovyiaListData("Намер ісьці за Хрыстом", R.raw.prynagodnyia_26))
    list.add(BogaslujbovyiaListData("Малітва пілігрыма", R.raw.prynagodnyia_32))
    list.add(
        BogaslujbovyiaListData(
            "Малітва да ўкрыжаванага Хрыста (Францішак Скарына)",
            R.raw.mltv_da_ukryzavanaha_chrysta_skaryna
        )
    )
    list.add(BogaslujbovyiaListData("Малітва аб блаславеньні", R.raw.prynagodnyia_0))
    list.add(BogaslujbovyiaListData("Малітва кіроўцы", R.raw.mltv_kiroucy))
    list.add(BogaslujbovyiaListData("Малітва за ўмацаваньне ў любові", R.raw.prynagodnyia_17))
    list.add(BogaslujbovyiaListData("Малітва маладога чалавека", R.raw.prynagodnyia_18))
    list.add(BogaslujbovyiaListData("Малітва на ўсякую патрэбу", R.raw.prynagodnyia_19))
    list.add(
        BogaslujbovyiaListData(
            "Малітва падзякі за атрыманыя дабрадзействы",
            R.raw.prynagodnyia_20
        )
    )
    list.add(BogaslujbovyiaListData("Малітва перад іспытамі", R.raw.prynagodnyia_22))
    list.add(
        BogaslujbovyiaListData(
            "Малітва ранішняга намеру (Опціных старцаў)",
            R.raw.prynagodnyia_23
        )
    )
    list.add(BogaslujbovyiaListData("Малітва ў час адпачынку", R.raw.prynagodnyia_34))
    list.add(
        BogaslujbovyiaListData(
            "Малітва за бязьвінных ахвяраў перасьледу",
            R.raw.prynagodnyia_35
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітвы перад ядою і пасьля яды",
            R.raw.mltv_pierad_jadoj_i_pasla
        )
    )
    list.add(BogaslujbovyiaListData("Малітва за ўсіх і за ўсё", R.raw.mltv_za_usich_i_za_usio))
    list.add(BogaslujbovyiaListData("Малітва за вязьняў", R.raw.mltv_za_viazniau))
    list.add(
        BogaslujbovyiaListData(
            "Малітва перад пачаткам і пасьля кожнай справы",
            R.raw.mltv_pierad_i_pasla_koznaj_spravy
        )
    )
    list.add(BogaslujbovyiaListData("Малітва ў дзень нараджэньня", R.raw.mltv_dzien_naradzennia))
    list.add(
        BogaslujbovyiaListData(
            "Малітва аб духу любові",
            R.raw.mltv_ab_duchu_lubovi_sv_franciszak
        )
    )
    list.add(BogaslujbovyiaListData("Малітва на кожны час", R.raw.mltv_na_kozny_czas))
    list.add(
        BogaslujbovyiaListData(
            "Малітвы за памерлых («Божа духаў і ўсякага цялеснага стварэньня»)",
            R.raw.mltv_za_pamierlych_boza_duchau
        )
    )
    list.sortBy {
        it.title
    }
    return list
}