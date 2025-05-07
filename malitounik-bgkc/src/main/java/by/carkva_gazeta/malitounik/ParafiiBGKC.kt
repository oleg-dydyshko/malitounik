package by.carkva_gazeta.malitounik

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
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.views.AppNavigationActions

@Composable
fun ParafiiBGKC(navController: NavHostController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val curyia = BogaslujbovyiaListData("Курыя Апостальскай Візітатуры БГКЦ", "parafii_bgkc/dzie_kuryja")
    val listCent = ArrayList<BogaslujbovyiaListData>()
    val listUsx = ArrayList<BogaslujbovyiaListData>()
    val listZax = ArrayList<BogaslujbovyiaListData>()
    val listZamejja = ArrayList<BogaslujbovyiaListData>()
    listCent.add(BogaslujbovyiaListData("Цэнтральны дэканат", "parafii_bgkc/dzie_centr_dekan.html"))
    listCent.add(BogaslujbovyiaListData("Маладэчна", "parafii_bgkc/dzie_maladechna.html"))
    listCent.add(BogaslujbovyiaListData("Менск", "parafii_bgkc/dzie_miensk.html"))
    listUsx.add(BogaslujbovyiaListData("Усходні дэканат", "parafii_bgkc/dzie_usxod_dekan.html"))
    listUsx.add(BogaslujbovyiaListData("Віцебск", "parafii_bgkc/dzie_viciebsk.html"))
    listUsx.add(BogaslujbovyiaListData("Ворша", "parafii_bgkc/dzie_vorsha.html"))
    listUsx.add(BogaslujbovyiaListData("Гомель", "parafii_bgkc/dzie_homel.html"))
    listUsx.add(BogaslujbovyiaListData("Магілёў", "parafii_bgkc/dzie_mahilou.html"))
    listUsx.add(BogaslujbovyiaListData("Полацак", "parafii_bgkc/dzie_polacak.html"))
    listZax.add(BogaslujbovyiaListData("Заходні дэканат", "parafii_bgkc/dzie_zaxod_dekan.html"))
    listZax.add(BogaslujbovyiaListData("Баранавічы", "parafii_bgkc/dzie_baranavichy.html"))
    listZax.add(BogaslujbovyiaListData("Берасьце", "parafii_bgkc/dzie_bierascie.html"))
    listZax.add(BogaslujbovyiaListData("Горадня", "parafii_bgkc/dzie_horadnia.html"))
    listZax.add(BogaslujbovyiaListData("Івацэвічы", "parafii_bgkc/dzie_ivacevichy.html"))
    listZax.add(BogaslujbovyiaListData("Ліда", "parafii_bgkc/dzie_lida.html"))
    listZamejja.add(BogaslujbovyiaListData("Антвэрпан (Бельгія)", "parafii_bgkc/dzie_antverpan.html"))
    listZamejja.add(BogaslujbovyiaListData("Беласток (Польшча)", "parafii_bgkc/dzie_bielastok.html"))
    listZamejja.add(BogaslujbovyiaListData("Варшава (Польшча)", "parafii_bgkc/dzie_varshava.html"))
    listZamejja.add(BogaslujbovyiaListData("Вена (Аўстрыя)", "parafii_bgkc/dzie_viena.html"))
    listZamejja.add(BogaslujbovyiaListData("Вільня (Літва)", "parafii_bgkc/dzie_vilnia.html"))
    listZamejja.add(BogaslujbovyiaListData("Калінінград (Расея)", "parafii_bgkc/dzie_kalininhrad.html"))
    listZamejja.add(BogaslujbovyiaListData("Кракаў (Польшча)", "parafii_bgkc/dzie_krakau.html"))
    listZamejja.add(BogaslujbovyiaListData("Лондан (Вялікабрытанія)", "parafii_bgkc/dzie_londan.html"))
    listZamejja.add(BogaslujbovyiaListData("Прага (Чэхія)", "parafii_bgkc/dzie_praha.html"))
    listZamejja.add(BogaslujbovyiaListData("Рым (Італія)", "parafii_bgkc/dzie_rym.html"))
    listZamejja.add(BogaslujbovyiaListData("Санкт-Пецярбург (Расея)", "parafii_bgkc/dzie_sanktpieciarburg.html"))
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