package by.carkva_gazeta.malitounik

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
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import java.util.Calendar

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
                                        "caliandarList", false
                                    )
                                ) navigationActions.navigateToKaliandarYear()
                                else navigationActions.navigateToKaliandar()
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
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
                                    text = title, modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = Settings.fontInterface.sp
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
                                    text = annotatedString, modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 10.dp, end = 10.dp, top = 10.dp), fontWeight = if (i < 3) FontWeight.Bold else FontWeight.Normal, color = if (i < 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                                )
                            }
                            Text(
                                text = listSviat[index].opisanieData, modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp), fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}