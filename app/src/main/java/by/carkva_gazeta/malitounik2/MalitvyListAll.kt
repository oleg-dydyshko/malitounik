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
import androidx.compose.ui.res.stringArrayResource
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
import java.util.Calendar
import java.util.GregorianCalendar

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
    val listPrynagodnyia = when (menuItem) {
        Settings.MENU_MALITVY_PRYNAGODNYIA -> {
            val arrayList = ArrayList<Malitvy>()
            arrayList.add(Malitvy(stringResource(R.string.prynad_1)))
            arrayList.add(Malitvy(stringResource(R.string.prynad_2)))
            arrayList.add(Malitvy(stringResource(R.string.prynad_3)))
            arrayList.add(Malitvy(stringResource(R.string.prynad_4)))
            arrayList.add(Malitvy(stringResource(R.string.prynad_5)))
            arrayList.add(Malitvy(stringResource(R.string.prynad_6)))
            arrayList
        }

        Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA -> {
            val list = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA)
            val arrayList = ArrayList<Malitvy>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.day == item.dayOfMonth) isAdd = false
                }
                if (isAdd) arrayList.add(
                    Malitvy(
                        item.dayOfMonth.toString() + " " + stringArrayResource(
                            R.array.meciac_smoll
                        )[item.month], item.dayOfMonth
                    )
                )
            }
            arrayList
        }

        Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA -> {
            val list = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA)
            val arrayList = ArrayList<Malitvy>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.day == item.dayOfMonth) isAdd = false
                }
                if (isAdd) arrayList.add(
                    Malitvy(
                        item.dayOfMonth.toString() + " " + stringArrayResource(
                            R.array.meciac_smoll
                        )[item.month], item.dayOfMonth
                    )
                )
            }
            arrayList
        }

        Settings.MENU_TRYEDZ_KVETNAIA -> {
            val list = getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_KVETNAIA)
            val arrayList = ArrayList<Malitvy>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.day == item.dayOfMonth) isAdd = false
                }
                if (isAdd) arrayList.add(
                    Malitvy(
                        item.dayOfMonth.toString() + " " + stringArrayResource(
                            R.array.meciac_smoll
                        )[item.month], item.dayOfMonth
                    )
                )
            }
            arrayList
        }

        Settings.MENU_MINEIA_MESIACHNAIA -> {
            val list = getMineiaMesiachnaia(subTitle)
            val arrayList = ArrayList<Malitvy>()
            list.forEach { item ->
                var isAdd = true
                arrayList.forEach {
                    if (it.day == item.dayOfMonth) isAdd = false
                }
                if (isAdd) arrayList.add(Malitvy(item.dayOfMonth.toString(), item.dayOfMonth))
            }
            arrayList
        }

        else -> ArrayList()
    }
    val list = when (menuItem) {
        Settings.MENU_AKTOIX -> getAktoix()
        Settings.MENU_VIACHERNIA -> getViachernia()
        Settings.MENU_TRAPARY_KANDAKI_NIADZELNYIA -> getTraparyKandakiNiadzelnyia()
        Settings.MENU_TRAPARY_KANDAKI_SHTODZENNYIA -> getTraparyKandakiShtodzennyia()
        Settings.MENU_MALITVY_PASLIA_PRYCHASCIA -> getMalitvyPasliaPrychascia()
        Settings.MENU_TREBNIK -> getTrebnik()
        Settings.MENU_MINEIA_AGULNAIA -> getMineiaAgulnaia()
        Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH -> getMineiaMesiachnaiaMounth()
        Settings.MENU_TRYEDZ -> getTtyedz()
        Settings.MENU_TRYEDZ_POSNAIA -> getTtyedzPosnaia()
        Settings.MENU_TRYEDZ_POSNAIA_1 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_1)
        Settings.MENU_TRYEDZ_POSNAIA_2 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_2)
        Settings.MENU_TRYEDZ_POSNAIA_3 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_3)
        Settings.MENU_TRYEDZ_POSNAIA_4 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_4)
        Settings.MENU_TRYEDZ_POSNAIA_5 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_5)
        Settings.MENU_TRYEDZ_POSNAIA_6 -> getTtyedzPosnaia(Settings.MENU_TRYEDZ_POSNAIA_6)
        else -> ArrayList()
    }
    val collapsedState =
        remember(listPrynagodnyia) { listPrynagodnyia.map { true }.toMutableStateList() }
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
                                painter = painterResource(R.drawable.arrow_back),
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
            if (menuItem == Settings.MENU_MALITVY_PRYNAGODNYIA || menuItem == Settings.MENU_MINEIA_MESIACHNAIA || menuItem == Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA || menuItem == Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA || menuItem == Settings.MENU_TRYEDZ_KVETNAIA) {
                listPrynagodnyia.forEachIndexed { i, dataItem ->
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
                        val subList = when (menuItem) {
                            Settings.MENU_MALITVY_PRYNAGODNYIA -> when (i) {
                                0 -> getPrynagodnyia1()
                                1 -> getPrynagodnyia2()
                                2 -> getPrynagodnyia3()
                                3 -> getPrynagodnyia4()
                                4 -> getPrynagodnyia5()
                                5 -> getPrynagodnyia6()
                                else -> getPrynagodnyia1()
                            }

                            Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA -> {
                                val listMineiaList =
                                    getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA)
                                val arrayList = ArrayList<BogaslujbovyiaListData>()
                                listMineiaList.forEach {
                                    if (dataItem.day == it.dayOfMonth) {
                                        arrayList.add(
                                            BogaslujbovyiaListData(
                                                it.title,
                                                it.resource
                                            )
                                        )
                                    }
                                }
                                arrayList
                            }

                            Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA -> {
                                val listMineiaList =
                                    getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA)
                                val arrayList = ArrayList<BogaslujbovyiaListData>()
                                listMineiaList.forEach {
                                    if (dataItem.day == it.dayOfMonth) {
                                        arrayList.add(
                                            BogaslujbovyiaListData(
                                                it.title,
                                                it.resource
                                            )
                                        )
                                    }
                                }
                                arrayList
                            }

                            Settings.MENU_TRYEDZ_KVETNAIA -> {
                                val listMineiaList =
                                    getTtyedzBialikagaTydnia(Settings.MENU_TRYEDZ_KVETNAIA)
                                val arrayList = ArrayList<BogaslujbovyiaListData>()
                                listMineiaList.forEach {
                                    if (dataItem.day == it.dayOfMonth) {
                                        arrayList.add(
                                            BogaslujbovyiaListData(
                                                it.title,
                                                it.resource
                                            )
                                        )
                                    }
                                }
                                arrayList
                            }

                            Settings.MENU_MINEIA_MESIACHNAIA -> {
                                val listMineiaList = getMineiaMesiachnaia(subTitle)
                                val arrayList = ArrayList<BogaslujbovyiaListData>()
                                listMineiaList.forEach {
                                    if (dataItem.day == it.dayOfMonth) {
                                        arrayList.add(
                                            BogaslujbovyiaListData(
                                                it.title,
                                                it.resource
                                            )
                                        )
                                    }
                                }
                                arrayList
                            }

                            else -> {
                                ArrayList()
                            }
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
                                            navigationActions.navigateToBogaslujbovyia(
                                                subList[index].title,
                                                subList[index].resurs
                                            )
                                        },
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            } else {
                items(list.size) { index ->
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                when (menuItem) {
                                    Settings.MENU_TRYEDZ_POSNAIA -> {
                                        when (list[index].resurs) {
                                            1 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA_1,
                                                    list[index].title
                                                )
                                            }

                                            2 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA_2,
                                                    list[index].title
                                                )
                                            }

                                            3 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA_3,
                                                    list[index].title
                                                )
                                            }

                                            4 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA_4,
                                                    list[index].title
                                                )
                                            }

                                            5 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA_5,
                                                    list[index].title
                                                )
                                            }

                                            6 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA_6,
                                                    list[index].title
                                                )
                                            }
                                        }
                                    }

                                    Settings.MENU_TRYEDZ -> {
                                        when (list[index].resurs) {
                                            10 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_POSNAIA,
                                                    list[index].title
                                                )
                                            }

                                            11 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA,
                                                    list[index].title
                                                )
                                            }

                                            12 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA,
                                                    list[index].title
                                                )
                                            }

                                            13 -> {
                                                navigationActions.navigateToMalitvyListAll(
                                                    title,
                                                    Settings.MENU_TRYEDZ_KVETNAIA,
                                                    list[index].title
                                                )
                                            }
                                        }
                                    }

                                    Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH -> {
                                        navigationActions.navigateToMalitvyListAll(
                                            title,
                                            Settings.MENU_MINEIA_MESIACHNAIA,
                                            list[index].title
                                        )
                                    }

                                    else -> {
                                        navigationActions.navigateToBogaslujbovyia(
                                            list[index].title,
                                            list[index].resurs
                                        )
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp, 12.dp),
                            painter = painterResource(R.drawable.krest),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            list[index].title,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = if (menuItem == Settings.MENU_MINEIA_MESIACHNAIA_MOUNTH) {
                                if (Calendar.getInstance()[Calendar.MONTH] == index) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                    HorizontalDivider()
                }
            }
            if (menuItem == Settings.MENU_MINEIA_AGULNAIA) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                navigationActions.navigateToMalitvyListAll(
                                    "ТРАПАРЫ І КАНДАКІ ШТОДЗЁННЫЯ - НА КОЖНЫ ДЗЕНЬ ТЫДНЯ",
                                    Settings.MENU_TRAPARY_KANDAKI_SHTODZENNYIA
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp, 12.dp),
                            painter = painterResource(R.drawable.folder),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            "ТРАПАРЫ І КАНДАКІ ШТОДЗЁННЫЯ - НА КОЖНЫ ДЗЕНЬ ТЫДНЯ",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider()
                }
            }
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
fun getTtyedzPosnaia(menuItem: Int): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    val subList = getTtyedzBialikagaTydnia(menuItem)
    subList.forEach { item ->
        list.add(
            BogaslujbovyiaListData(
                item.dayOfMonth.toString() + " " + stringArrayResource(
                    R.array.meciac_smoll
                )[item.month] + "\n" + item.title, item.resource
            )
        )
    }
    return list
}

fun getTtyedzPosnaia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Службы 1-га тыдня Вялікага посту", 1))
    list.add(BogaslujbovyiaListData("Службы 2-га тыдня Вялікага посту", 2))
    list.add(BogaslujbovyiaListData("Службы 3-га тыдня Вялікага посту", 3))
    list.add(BogaslujbovyiaListData("Службы 4-га тыдня Вялікага посту", 4))
    list.add(BogaslujbovyiaListData("Службы 5-га тыдня Вялікага посту", 5))
    list.add(BogaslujbovyiaListData("Службы 6-га тыдня Вялікага посту", 6))
    return list
}

fun getTtyedzBialikagaTydnia(menuItem: Int): ArrayList<MineiaList> {
    val slugbovyiaTextu = SlugbovyiaTextu()
    val mineia = when (menuItem) {
        Settings.MENU_TRYEDZ_BIALIKAGA_TYDNIA -> slugbovyiaTextu.getVilikiTydzen()
        Settings.MENU_TRYEDZ_SVETLAGA_TYDNIA -> slugbovyiaTextu.getSvetlyTydzen()
        Settings.MENU_TRYEDZ_KVETNAIA -> slugbovyiaTextu.getMineiaKvetnaia()
        Settings.MENU_TRYEDZ_POSNAIA_1 -> slugbovyiaTextu.getTydzen1()
        Settings.MENU_TRYEDZ_POSNAIA_2 -> slugbovyiaTextu.getTydzen2()
        Settings.MENU_TRYEDZ_POSNAIA_3 -> slugbovyiaTextu.getTydzen3()
        Settings.MENU_TRYEDZ_POSNAIA_4 -> slugbovyiaTextu.getTydzen4()
        Settings.MENU_TRYEDZ_POSNAIA_5 -> slugbovyiaTextu.getTydzen5()
        Settings.MENU_TRYEDZ_POSNAIA_6 -> slugbovyiaTextu.getTydzen6()
        else -> ArrayList()
    }
    var dayOfYear: Int
    val mineiaList = ArrayList<MineiaList>()
    val cal = Calendar.getInstance()
    for (i in mineia.indices) {
        dayOfYear = slugbovyiaTextu.getRealDay(mineia[i].day, Calendar.getInstance()[Calendar.DAY_OF_YEAR], Calendar.getInstance()[Calendar.YEAR], mineia[i].pasxa)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val opisanie = if (menuItem == Settings.MENU_TRYEDZ_POSNAIA_1 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_2 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_3 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_4 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_5 || menuItem == Settings.MENU_TRYEDZ_POSNAIA_6) ""
        else ". " + slugbovyiaTextu.getNazouSluzby(mineia[i].sluzba)
        mineiaList.add(
            MineiaList(
                cal[Calendar.DATE],
                cal[Calendar.MONTH],
                mineia[i].title + opisanie,
                mineia[i].resource,
                mineia[i].sluzba
            )
        )
    }
    mineiaList.sortWith(
        compareBy({
            it.month
        }, {
            it.dayOfMonth
        }, {
            it.sluzba
        })
    )
    return mineiaList
}

fun getTtyedz(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Трыёдзь посная", 10))
    list.add(BogaslujbovyiaListData("Службы Вялікага тыдня", 11))
    list.add(BogaslujbovyiaListData("Службы Сьветлага тыдня", 12))
    list.add(BogaslujbovyiaListData("Трыёдзь Кветная", 13))
    return list
}

fun getMineiaMesiachnaia(subTitle: String): ArrayList<MineiaList> {
    val slugbovyiaTextu = SlugbovyiaTextu()
    val mounth = when (subTitle) {
        "Студзень" -> Calendar.JANUARY
        "Люты" -> Calendar.FEBRUARY
        "Сакавік" -> Calendar.MARCH
        "Красавік" -> Calendar.APRIL
        "Травень" -> Calendar.MAY
        "Чэрвень" -> Calendar.JUNE
        "Ліпень" -> Calendar.JULY
        "Жнівень" -> Calendar.AUGUST
        "Верасень" -> Calendar.SEPTEMBER
        "Кастрычнік" -> Calendar.OCTOBER
        "Лістапад" -> Calendar.NOVEMBER
        "Сьнежань" -> Calendar.DECEMBER
        else -> 0
    }
    val mineia = slugbovyiaTextu.getMineiaMesiachnaia()
    var dayOfYear: Int
    val mineiaList = ArrayList<MineiaList>()
    val cal = Calendar.getInstance() as GregorianCalendar
    for (i in mineia.indices) {
        dayOfYear = slugbovyiaTextu.getRealDay(mineia[i].day, Calendar.getInstance()[Calendar.DAY_OF_YEAR], Calendar.getInstance()[Calendar.YEAR], mineia[i].pasxa)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val opisanie = ". " + slugbovyiaTextu.getNazouSluzby(mineia[i].sluzba)
        if (cal[Calendar.MONTH] == mounth) {
            mineiaList.add(
                MineiaList(
                    cal[Calendar.DATE],
                    cal[Calendar.MONTH],
                    mineia[i].title + opisanie,
                    mineia[i].resource,
                    mineia[i].sluzba
                )
            )
        }
    }
    mineiaList.sortWith(
        compareBy({
            it.dayOfMonth
        }, {
            it.sluzba
        })
    )
    return mineiaList
}

fun pasha(day: Int): Int {
    val year = Calendar.getInstance()[Calendar.YEAR]
    var dataP: Int
    val monthP: Int
    val a = year % 19
    val b = year % 4
    val cx = year % 7
    val k = year / 100
    val p = (13 + 8 * k) / 25
    val q = k / 4
    val m = (15 - p + k - q) % 30
    val n = (4 + k - q) % 7
    val d = (19 * a + m) % 30
    val ex = (2 * b + 4 * cx + 6 * d + n) % 7
    if (d + ex <= 9) {
        dataP = d + ex + 22
        monthP = Calendar.MARCH
    } else {
        dataP = d + ex - 9
        if (d == 29 && ex == 6) dataP = 19
        if (d == 28 && ex == 6) dataP = 18
        monthP = Calendar.APRIL
    }
    val gCalendar = GregorianCalendar(year, monthP, dataP)
    gCalendar.add(Calendar.DATE, day)
    return gCalendar[Calendar.DAY_OF_YEAR]
}

@Composable
fun getMineiaMesiachnaiaMounth(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    val mounthList = stringArrayResource(R.array.meciac3)
    mounthList.forEachIndexed { index, item ->
        list.add(BogaslujbovyiaListData(item, index))
    }
    return list
}

fun getMineiaAgulnaia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная Найсьвяцейшай Багародзіцы",
            R.raw.viachernia_mineia_agulnaia1
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная Яну Хрысьціцелю",
            R.raw.viachernia_mineia_agulnaia2
        )
    )
    list.add(BogaslujbovyiaListData("Вячэрня агульная прароку", R.raw.viachernia_mineia_agulnaia3))
    list.add(BogaslujbovyiaListData("Вячэрня агульная апосталу", R.raw.viachernia_mineia_agulnaia4))
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная апосталам",
            R.raw.viachernia_mineia_agulnaia5
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятаначальніку",
            R.raw.viachernia_mineia_agulnaia6
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятаначальнікам",
            R.raw.viachernia_mineia_agulnaia7
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная посьніку, манаху і пустэльніку",
            R.raw.viachernia_mineia_agulnaia8
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная посьнікам, манахам і пустэльнікам",
            R.raw.viachernia_mineia_agulnaia9
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная вызнаўцу і настаўніку царкоўнаму",
            R.raw.viachernia_mineia_agulnaia10
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніку",
            R.raw.viachernia_mineia_agulnaia11
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніку (іншая)",
            R.raw.viachernia_mineia_agulnaia12
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучанікам",
            R.raw.viachernia_mineia_agulnaia13
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятамучаніку",
            R.raw.viachernia_mineia_agulnaia14
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятамучанікам",
            R.raw.viachernia_mineia_agulnaia15
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніку сьвятару або манаху",
            R.raw.viachernia_mineia_agulnaia16
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучанікам сьвятарам або манахам",
            R.raw.viachernia_mineia_agulnaia17
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцы",
            R.raw.viachernia_mineia_agulnaia18
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцы (іншая)",
            R.raw.viachernia_mineia_agulnaia19
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцам",
            R.raw.viachernia_mineia_agulnaia20
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная мучаніцы манахіні",
            R.raw.viachernia_mineia_agulnaia21
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятой жанчыне",
            R.raw.viachernia_mineia_agulnaia22
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная сьвятым жанчынам",
            R.raw.viachernia_mineia_agulnaia23
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня агульная бескарысьлівым лекарам і цудатворцам",
            R.raw.viachernia_mineia_agulnaia24
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба апосталу або апосталам",
            R.raw.sluzba_apostalu_apostalam
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба настаўніку царкоўнаму і вызнаўцу",
            R.raw.sluzba_nastauniku_cark_vyznaucu
        )
    )
    list.add(BogaslujbovyiaListData("Служба сьвятаначальнікам", R.raw.sluzba_sviatanaczalnikam))
    list.add(BogaslujbovyiaListData("Служба сьвятаначальніку", R.raw.sluzba_sviatanaczalniku))
    list.add(
        BogaslujbovyiaListData(
            "Служба Найсьвяцейшай Багародзіцы",
            R.raw.sluzba_najsviaciejszaj_baharodzicy
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба за памерлых на кожны дзень тыдня",
            R.raw.sluzba_za_pamierlych_na_kozny_dzien_tydnia
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба бескарысьлівым лекарам і цудатворцам",
            R.raw.sluzba_bieskaryslivym_lekaram_cudatvorcam
        )
    )
    list.add(BogaslujbovyiaListData("Служба мучаніцам", R.raw.sluzba_muczanicam))
    list.add(BogaslujbovyiaListData("Служба мучаніцы", R.raw.sluzba_muczanicy))
    list.add(BogaslujbovyiaListData("Служба мучанікам", R.raw.sluzba_muczanikam))
    list.add(
        BogaslujbovyiaListData(
            "Служба мучанікам сьвятарам і манахам",
            R.raw.sluzba_muczanikam_sviataram_i_manacham
        )
    )
    list.add(BogaslujbovyiaListData("Служба мучаніку", R.raw.sluzba_muczaniku))
    list.add(
        BogaslujbovyiaListData(
            "Служба мучаніку сьвятару і манаху",
            R.raw.sluzba_muczaniku_sviataru_i_manachu
        )
    )
    list.add(BogaslujbovyiaListData("Служба сьвятой жанчыне", R.raw.sluzba_sviatoj_zanczynie))
    list.add(BogaslujbovyiaListData("Служба сьвятым жанчынам", R.raw.sluzba_sviatym_zanczynam))
    list.add(
        BogaslujbovyiaListData(
            "Служба посьнікам, манахам і пустэльнікам",
            R.raw.sluzba_posnikam_manacham_pustelnikam
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба посьніку / аскету, манаху і пустэльніку",
            R.raw.sluzba_posniku_manachu_pustelniku
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Служба сьвятому Яну Хрысьціцелю",
            R.raw.sluzba_janu_chryscicielu
        )
    )
    list.add(BogaslujbovyiaListData("Служба прароку", R.raw.sluzba_praroku))
    list.add(BogaslujbovyiaListData("Служба сьвятым анёлам", R.raw.sluzba_aniolam))
    list.add(BogaslujbovyiaListData("Служба сьвятому крыжу", R.raw.sluzba_kryzu))
    list.add(BogaslujbovyiaListData("Служба сьвятамучанікам", R.raw.sluzba_sviatamuczanikam))
    list.add(BogaslujbovyiaListData("Служба сьвятамучаніку", R.raw.sluzba_sviatamuczaniku))
    return list
}

fun getTrebnik(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Служба аб вызваленьні бязьвінна зьняволеных",
            R.raw.sluzba_vyzvalen_biazvinna_zniavolenych
        )
    )
    list.add(BogaslujbovyiaListData("Служба за памерлых — Малая паніхіда", R.raw.panichida_malaja))
    list.add(
        BogaslujbovyiaListData(
            "Чын асьвячэньня транспартнага сродку",
            R.raw.czyn_asviaczennia_transpartnaha_srodku
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Асьвячэньне крыжа на сьвятой Літургіі",
            R.raw.asviaczennie_kryza
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Блаславеньне ўсялякае рэчы",
            R.raw.mltv_blaslaviennie_usialakaj_reczy
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва на асьвячэньне памятнай табліцы Слузе Божаму",
            R.raw.mltv_asviacz_pamiatnaj_tablicy
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва на асьвячэньне памятнай табліцы Слузе Божаму, які пацярпеў за Беларусь",
            R.raw.mltv_asviacz_pamiatnaj_tablicy_paciarpieu_za_bielarus1
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітва на асьвячэньне памятнай табліцы ўсім бязьвінным ахвярам, якія пацярпелі за Беларусь",
            R.raw.mltv_asviacz_pamiatnaj_tablicy_biazvinnym_achviaram_paciarpieli_za_bielarus
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Малітоўны чын сьвятарскіх адведзінаў парафіянаў",
            R.raw.malitouny_czyn_sviatarskich_adviedzinau_parafijanau
        )
    )
    return list
}

fun getMalitvyPasliaPrychascia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Малітва падзякі", R.raw.paslia_prychascia1))
    list.add(BogaslujbovyiaListData("Малітва сьв. Васіля Вялікага", R.raw.paslia_prychascia2))
    list.add(BogaslujbovyiaListData("Малітва Сымона Мэтафраста", R.raw.paslia_prychascia3))
    list.add(BogaslujbovyiaListData("Iншая малітва", R.raw.paslia_prychascia4))
    list.add(
        BogaslujbovyiaListData(
            "Малітва да Найсьвяцейшай Багародзіцы",
            R.raw.paslia_prychascia5
        )
    )
    return list
}

fun getTraparyKandakiNiadzelnyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Тон 1", R.raw.ton1))
    list.add(BogaslujbovyiaListData("Тон 2", R.raw.ton2))
    list.add(BogaslujbovyiaListData("Тон 3", R.raw.ton3))
    list.add(BogaslujbovyiaListData("Тон 4", R.raw.ton4))
    list.add(BogaslujbovyiaListData("Тон 5", R.raw.ton5))
    list.add(BogaslujbovyiaListData("Тон 6", R.raw.ton6))
    list.add(BogaslujbovyiaListData("Тон 7", R.raw.ton7))
    list.add(BogaslujbovyiaListData("Тон 8", R.raw.ton8))
    return list
}

fun getTraparyKandakiShtodzennyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам", R.raw.ton1_budni))
    list.add(BogaslujbovyiaListData("АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю", R.raw.ton2_budni))
    list.add(
        BogaslujbovyiaListData(
            "СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу",
            R.raw.ton3_budni
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю",
            R.raw.ton4_budni
        )
    )
    list.add(BogaslujbovyiaListData("ПЯТНІЦА\nСлужба Крыжу Гасподняму", R.raw.ton5_budni))
    list.add(BogaslujbovyiaListData("СУБОТА\nСлужба ўсім сьвятым і памёрлым", R.raw.ton6_budni))
    return list
}

fun getViachernia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Вячэрня ў нядзелі і сьвяты", R.raw.viaczernia_niadzelnaja))
    list.add(
        BogaslujbovyiaListData(
            "Ліцьця і блаславеньне хлябоў",
            R.raw.viaczernia_liccia_i_blaslavenne_chliabou
        )
    )
    list.add(BogaslujbovyiaListData("Вячэрня ў звычайныя дні", R.raw.viaczernia_na_kozny_dzen))
    list.add(BogaslujbovyiaListData("Вячэрня ў Вялікім посьце", R.raw.viaczernia_u_vialikim_poscie))
    list.add(
        BogaslujbovyiaListData(
            "Вячэрняя служба штодзённая (без сьвятара)",
            R.raw.viaczerniaja_sluzba_sztodzionnaja_biez_sviatara
        )
    )
    list.add(BogaslujbovyiaListData("Вячэрня на Сьветлым тыдні", R.raw.viaczernia_svietly_tydzien))
    return list
}

fun getAktoix(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 1",
            R.raw.viaczernia_ton1
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 2",
            R.raw.viaczernia_ton2
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 3",
            R.raw.viaczernia_ton3
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 4",
            R.raw.viaczernia_ton4
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 5",
            R.raw.viaczernia_ton5
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 6",
            R.raw.viaczernia_ton6
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 7",
            R.raw.viaczernia_ton7
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Вячэрня Тон 8",
            R.raw.viaczernia_ton8
        )
    )
    list.add(
        BogaslujbovyiaListData(
            "Багародзічныя адпушчальныя",
            R.raw.viaczernia_baharodzicznyja_adpuszczalnyja
        )
    )
    return list
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
    list.add(
        BogaslujbovyiaListData(
            "Юбілейная малітва",
            R.raw.mltv_jubilejnaja
        )
    )
    list.sortBy {
        it.title
    }
    return list
}

data class MineiaList(
    val dayOfMonth: Int,
    val month: Int,
    val title: String,
    val resource: Int,
    val sluzba: Int
)

data class Malitvy(val title: String, val day: Int = 0)