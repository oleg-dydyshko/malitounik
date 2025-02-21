package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AppNavigationActions

@Composable
fun BogaslujbovyiaMenu(navController: NavHostController, innerPadding: PaddingValues, menuItem: Int) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val folderList = stringArrayResource(R.array.bogaslugbovyia_folder_list)
    val list = when(menuItem) {
        Settings.MENU_BOGASLUJBOVYIA -> getBogaslujbovyia()
        Settings.MENU_MALITVY -> getMalitvy()
        else -> ArrayList()
    }
    folderList.sort()
    if (menuItem == Settings.MENU_BOGASLUJBOVYIA) {
        list.sortBy {
            it.title
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (menuItem == Settings.MENU_BOGASLUJBOVYIA) {
            items(folderList.size) { index ->
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            if (folderList[index] == "АКТОІХ") {
                                navigationActions.navigateToMalitvyListAll(folderList[index], Settings.MENU_AKTOIX)
                            }
                            if (folderList[index] == "ВЯЧЭРНЯ") {
                                navigationActions.navigateToMalitvyListAll(folderList[index], Settings.MENU_VIACHERNIA)
                            }
                            if (folderList[index] == "ТРАПАРЫ І КАНДАКІ НЯДЗЕЛЬНЫЯ ВАСЬМІ ТОНАЎ") {
                                navigationActions.navigateToMalitvyListAll(folderList[index], Settings.MENU_TRAPARY_KANDAKI_NIADZELNYIA)
                            }
                            if (folderList[index] == "МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ") {
                                navigationActions.navigateToMalitvyListAll(folderList[index], Settings.MENU_MALITVY_PASLIA_PRYCHASCIA)
                            }
                            if (folderList[index] == "ТРЭБНІК") {
                                navigationActions.navigateToMalitvyListAll(folderList[index], Settings.MENU_TREBNIK)
                            }
                            if (folderList[index] == "МІНЭЯ АГУЛЬНАЯ") {
                                navigationActions.navigateToMalitvyListAll(folderList[index], Settings.MENU_MINEIA_AGULNAIA)
                            }
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
                        folderList[index],
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                HorizontalDivider()
            }
        }
        items(list.size) { index ->
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        navigationActions.navigateToBogaslujbovyia(list[index].title, list[index].resurs)
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
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            HorizontalDivider()
        }
        if (menuItem == Settings.MENU_MALITVY) {
            item {
                val title = stringResource(R.string.prynagodnyia)
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            navigationActions.navigateToMalitvyListAll(title, Settings.MENU_MALITVY_PRYNAGODNYIA)
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
                        title,
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

fun getMalitvy(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Ранішняя малітвы", R.raw.malitvy_ranisznija))
    list.add(BogaslujbovyiaListData("Вячэрнія малітвы", R.raw.malitvy_viaczernija))
    return list
}

fun getBogaslujbovyia(): ArrayList<BogaslujbovyiaListData> {
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Боская Літургія сьв. Яна Залатавуснага", R.raw.lit_jana_zalatavusnaha))
    list.add(BogaslujbovyiaListData("Боская Літургія ў Велікодны перыяд", R.raw.lit_jan_zalat_vielikodn))
    list.add(BogaslujbovyiaListData("Боская Літургія сьв. Васіля Вялікага", R.raw.lit_vasila_vialikaha))
    list.add(BogaslujbovyiaListData("Літургія раней асьвячаных дароў", R.raw.lit_raniej_asviaczanych_darou))
    list.add(BogaslujbovyiaListData("Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", R.raw.nabazenstva_maci_bozaj_niast_dap))
    list.add(BogaslujbovyiaListData("Ютрань нядзельная (у скароце)", R.raw.jutran_niadzelnaja))
    list.add(BogaslujbovyiaListData("Абедніца", R.raw.abiednica))
    list.add(BogaslujbovyiaListData("Малебны канон Найсьвяцейшай Багародзіцы", R.raw.kanon_malebny_baharodzicy))
    list.add(BogaslujbovyiaListData("Вялікі пакаянны канон сьвятога Андрэя Крыцкага", R.raw.kanon_andreja_kryckaha))
    list.add(BogaslujbovyiaListData("Малебен сьв. Кірылу і Мятоду, настаўнікам славянскім", R.raw.malebien_kiryla_miatod))
    list.add(BogaslujbovyiaListData("Павячэрніца малая", R.raw.paviaczernica_malaja))
    list.add(BogaslujbovyiaListData("Вялікі пакаянны канон сьвятога Андрэя Крыцкага(у 4-х частках)", R.raw.kanon_andreja_kryckaha_4_czastki))
    return list
}

data class BogaslujbovyiaListData(val title: String, val resurs: Int)