package by.carkva_gazeta.malitounik

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.coroutineScope
import java.util.Calendar
import java.util.GregorianCalendar

@Composable
fun Pashalia(navController: NavHostController, innerPadding: PaddingValues, searchText: Boolean, viewModel: SearchBibleViewModel) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val listAll = remember { mutableStateListOf<Pashalii>() }
    val filteredItems = remember { mutableStateListOf<Pashalii>() }
    val lazyListState = rememberLazyListState()
    var findIndex by remember { mutableIntStateOf(0) }
    val cal = Calendar.getInstance()
    LaunchedEffect(Unit) {
        for (year in 1582..2499) {
            listAll.add(pasxa(context, year))
            if (year == cal[Calendar.YEAR] - 3) {
                findIndex = year
                coroutineScope {
                    lazyListState.scrollToItem(findIndex - 1582)
                }
            }
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    LaunchedEffect(viewModel.textFieldValueState.text, searchText) {
        filteredItems.clear()
        if (searchText) {
            if (viewModel.textFieldValueState.text.isNotEmpty()) {
                val filterList = listAll.filter { it.katolic.contains(viewModel.textFieldValueState.text, ignoreCase = true) }
                filteredItems.addAll(filterList)
            } else {
                filteredItems.addAll(listAll)
            }
        } else {
            filteredItems.addAll(listAll)
            coroutineScope {
                lazyListState.scrollToItem(findIndex - 1582)
            }
        }
    }
    Column(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        if (!searchText) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.hryharyjan),
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                    Text(
                        text = stringResource(R.string.juljan),
                        modifier = Modifier,
                        color = SecondaryText,
                        fontSize = Settings.fontInterface.sp
                    )
                }
                val paschaKaliandarBel = stringResource(R.string.pascha_kaliandar_bel)
                TextButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp),
                    colors = ButtonColors(
                        Divider,
                        Color.Unspecified,
                        Color.Unspecified,
                        Color.Unspecified
                    ),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        navigationActions.navigateToBogaslujbovyia(
                            paschaKaliandarBel,
                            "pasxa.html"
                        )
                    }
                ) {
                    Text(
                        stringResource(R.string.paschalia),
                        fontSize = Settings.fontInterface.sp,
                        lineHeight = Settings.fontInterface.sp * 1.2f
                    )
                }
            }
        }
        LazyColumn(state = lazyListState) {
            items(
                filteredItems.size,
                key = { index -> filteredItems[index].year }
            ) { index ->
                Column {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = ""
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = filteredItems[index].katolic,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = if (filteredItems[index].year == cal[Calendar.YEAR]) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary,
                                fontSize = Settings.fontInterface.sp
                            )
                            if (!filteredItems[index].sovpadenie) {
                                Text(
                                    text = filteredItems[index].pravas,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    color = SecondaryText,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
            }
        }
    }
}

fun pasxa(context: Context, year: Int): Pashalii {
    var dataP: Int
    val monthP: Int
    val dataPrav: Int
    val monthPrav: Int
    val monthName = context.resources.getStringArray(R.array.meciac_smoll)
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
        monthP = 3
    } else {
        dataP = d + ex - 9
        if (d == 29 && ex == 6) dataP = 19
        if (d == 28 && ex == 6) dataP = 18
        monthP = 4
    }
    val a2 = (19 * (year % 19) + 15) % 30
    val b2 = (2 * (year % 4) + 4 * (year % 7) + 6 * a2 + 6) % 7
    if (a2 + b2 > 9) {
        dataPrav = a2 + b2 - 9
        monthPrav = 4
    } else {
        dataPrav = 22 + a2 + b2
        monthPrav = 3
    }
    val pravas = GregorianCalendar(year, monthPrav - 1, dataPrav)
    val katolic = GregorianCalendar(year, monthP - 1, dataP)
    val vek = if (year > 1582) year.toString().substring(0, 2)
    else ""
    when (vek) {
        "15", "16" -> pravas.add(Calendar.DATE, 10)
        "17" -> pravas.add(Calendar.DATE, 11)
        "18" -> pravas.add(Calendar.DATE, 12)
        "19", "20" -> pravas.add(Calendar.DATE, 13)
    }
    var sovpadenie = false
    if (katolic[Calendar.DAY_OF_YEAR] == pravas[Calendar.DAY_OF_YEAR]) sovpadenie = true
    return Pashalii(
        dataP.toString() + " " + monthName[monthP - 1] + " " + year,
        pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]],
        year,
        sovpadenie
    )
}

data class Pashalii(val katolic: String, val pravas: String, val year: Int, val sovpadenie: Boolean)