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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AppNavigationActions

@Composable
fun ParafiiBGKC(navController: NavHostController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val list = ArrayList<BogaslujbovyiaListData>()
    list.add(BogaslujbovyiaListData("Курыя Апостальскай Візітатуры БГКЦ", R.raw.dzie_kuryja))
    list.add(BogaslujbovyiaListData("Цэнтральны дэканат:", R.raw.dzie_centr_dekan))
    list.add(BogaslujbovyiaListData("Маладэчна", R.raw.dzie_maladechna))
    list.add(BogaslujbovyiaListData("Менск", R.raw.dzie_miensk))
    list.add(BogaslujbovyiaListData("Усходні дэканат:", R.raw.dzie_usxod_dekan))
    list.add(BogaslujbovyiaListData("Віцебск", R.raw.dzie_viciebsk))
    list.add(BogaslujbovyiaListData("Ворша", R.raw.dzie_vorsha))
    list.add(BogaslujbovyiaListData("Гомель", R.raw.dzie_homel))
    list.add(BogaslujbovyiaListData("Магілёў", R.raw.dzie_mahilou))
    list.add(BogaslujbovyiaListData("Полацак", R.raw.dzie_polacak))
    list.add(BogaslujbovyiaListData("Заходні дэканат:", R.raw.dzie_zaxod_dekan))
    list.add(BogaslujbovyiaListData("Баранавічы", R.raw.dzie_baranavichy))
    list.add(BogaslujbovyiaListData("Берасьце", R.raw.dzie_bierascie))
    list.add(BogaslujbovyiaListData("Горадня", R.raw.dzie_horadnia))
    list.add(BogaslujbovyiaListData("Івацэвічы", R.raw.dzie_ivacevichy))
    list.add(BogaslujbovyiaListData("Ліда", R.raw.dzie_lida))
    list.add(BogaslujbovyiaListData("Замежжа:\nАнтвэрпан (Бельгія)", R.raw.dzie_antverpan))
    list.add(BogaslujbovyiaListData("Беласток (Польшча)", R.raw.dzie_bielastok))
    list.add(BogaslujbovyiaListData("Варшава (Польшча)", R.raw.dzie_varshava))
    list.add(BogaslujbovyiaListData("Вена (Аўстрыя)", R.raw.dzie_viena))
    list.add(BogaslujbovyiaListData("Вільня (Літва)", R.raw.dzie_vilnia))
    list.add(BogaslujbovyiaListData("Калінінград (Расея)", R.raw.dzie_kalininhrad))
    list.add(BogaslujbovyiaListData("Кракаў (Польшча)", R.raw.dzie_krakau))
    list.add(BogaslujbovyiaListData("Лондан (Вялікабрытанія)", R.raw.dzie_londan))
    list.add(BogaslujbovyiaListData("Прага (Чэхія)", R.raw.dzie_praha))
    list.add(BogaslujbovyiaListData("Рым (Італія)", R.raw.dzie_rym))
    list.add(BogaslujbovyiaListData("Санкт-Пецярбург (Расея)", R.raw.dzie_sanktpieciarburg))
    LazyColumn {
        items(list.size) { index ->
            Column {
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
                        text = list[index].title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}