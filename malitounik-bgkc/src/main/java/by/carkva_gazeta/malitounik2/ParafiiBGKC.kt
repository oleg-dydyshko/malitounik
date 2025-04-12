package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.views.AppNavigationActions

@Composable
fun ParafiiBGKC(navController: NavHostController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val curyia = BogaslujbovyiaListData("Курыя Апостальскай Візітатуры БГКЦ", R.raw.dzie_kuryja)
    val listCent = ArrayList<BogaslujbovyiaListData>()
    val listUsx = ArrayList<BogaslujbovyiaListData>()
    val listZax = ArrayList<BogaslujbovyiaListData>()
    val listZamejja = ArrayList<BogaslujbovyiaListData>()
    listCent.add(BogaslujbovyiaListData("Цэнтральны дэканат", R.raw.dzie_centr_dekan))
    listCent.add(BogaslujbovyiaListData("Маладэчна", R.raw.dzie_maladechna))
    listCent.add(BogaslujbovyiaListData("Менск", R.raw.dzie_miensk))
    listUsx.add(BogaslujbovyiaListData("Усходні дэканат", R.raw.dzie_usxod_dekan))
    listUsx.add(BogaslujbovyiaListData("Віцебск", R.raw.dzie_viciebsk))
    listUsx.add(BogaslujbovyiaListData("Ворша", R.raw.dzie_vorsha))
    listUsx.add(BogaslujbovyiaListData("Гомель", R.raw.dzie_homel))
    listUsx.add(BogaslujbovyiaListData("Магілёў", R.raw.dzie_mahilou))
    listUsx.add(BogaslujbovyiaListData("Полацак", R.raw.dzie_polacak))
    listZax.add(BogaslujbovyiaListData("Заходні дэканат", R.raw.dzie_zaxod_dekan))
    listZax.add(BogaslujbovyiaListData("Баранавічы", R.raw.dzie_baranavichy))
    listZax.add(BogaslujbovyiaListData("Берасьце", R.raw.dzie_bierascie))
    listZax.add(BogaslujbovyiaListData("Горадня", R.raw.dzie_horadnia))
    listZax.add(BogaslujbovyiaListData("Івацэвічы", R.raw.dzie_ivacevichy))
    listZax.add(BogaslujbovyiaListData("Ліда", R.raw.dzie_lida))
    listZamejja.add(BogaslujbovyiaListData("Антвэрпан (Бельгія)", R.raw.dzie_antverpan))
    listZamejja.add(BogaslujbovyiaListData("Беласток (Польшча)", R.raw.dzie_bielastok))
    listZamejja.add(BogaslujbovyiaListData("Варшава (Польшча)", R.raw.dzie_varshava))
    listZamejja.add(BogaslujbovyiaListData("Вена (Аўстрыя)", R.raw.dzie_viena))
    listZamejja.add(BogaslujbovyiaListData("Вільня (Літва)", R.raw.dzie_vilnia))
    listZamejja.add(BogaslujbovyiaListData("Калінінград (Расея)", R.raw.dzie_kalininhrad))
    listZamejja.add(BogaslujbovyiaListData("Кракаў (Польшча)", R.raw.dzie_krakau))
    listZamejja.add(BogaslujbovyiaListData("Лондан (Вялікабрытанія)", R.raw.dzie_londan))
    listZamejja.add(BogaslujbovyiaListData("Прага (Чэхія)", R.raw.dzie_praha))
    listZamejja.add(BogaslujbovyiaListData("Рым (Італія)", R.raw.dzie_rym))
    listZamejja.add(BogaslujbovyiaListData("Санкт-Пецярбург (Расея)", R.raw.dzie_sanktpieciarburg))
    val listTitle = ArrayList<ParafiiBGKCItem>()
    listTitle.add(ParafiiBGKCItem("Цэнтральны дэканат", listCent))
    listTitle.add(ParafiiBGKCItem("Усходні дэканат", listUsx))
    listTitle.add(ParafiiBGKCItem("Заходні дэканат", listZax))
    listTitle.add(ParafiiBGKCItem("Замежжа", listZamejja))
    val collapsedState = remember(listTitle) { listTitle.map { true }.toMutableStateList() }
    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        navigationActions.navigateToBogaslujbovyia(
                            curyia.title, curyia.resurs
                        )
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                )
                Text(
                    curyia.title, modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                )
            }
            HorizontalDivider()
        }
        listTitle.forEachIndexed { i, dataItem ->
            val collapsed = collapsedState[i]
            item(key = "header_$i") {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            collapsedState[i] = !collapsed
                        }) {
                    Icon(
                        painter = if (collapsed) painterResource(R.drawable.keyboard_arrow_down)
                        else painterResource(R.drawable.keyboard_arrow_up),
                        contentDescription = "",
                        tint = Divider,
                    )
                    Text(
                        dataItem.title, modifier = Modifier
                            .animateItem(
                                fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntOffset.VisibilityThreshold
                                )
                            )
                            .padding(10.dp)
                            .weight(1f), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
                HorizontalDivider()
            }
            if (!collapsed) {
                items(dataItem.list.size) { index ->
                    Row(
                        modifier = Modifier
                            .padding(start = 30.dp)
                            .clickable {
                                navigationActions.navigateToBogaslujbovyia(
                                    dataItem.list[index].title, dataItem.list[index].resurs
                                )
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                        )
                        Text(
                            dataItem.list[index].title, modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
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

data class ParafiiBGKCItem(val title: String, val list: ArrayList<BogaslujbovyiaListData>)