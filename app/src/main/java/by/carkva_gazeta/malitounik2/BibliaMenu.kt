package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.ui.theme.Primary
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik2.views.AppNavigationActions

@Composable
fun BibliaMenu(
    navController: NavHostController,
    perevod: String
) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val bibleTime = remember { mutableStateOf(false) }
    if (bibleTime.value) {
        val prevodName = when (perevod) {
            Settings.PEREVODSEMUXI -> "biblia"
            Settings.PEREVODBOKUNA -> "bokuna"
            Settings.PEREVODCARNIAUSKI -> "carniauski"
            Settings.PEREVODSINOIDAL -> "sinaidal"
            else -> "biblia"
        }
        val knigaText = k.getString("bible_time_${prevodName}_kniga", "Быц") ?: "Быц"
        val kniga = knigaBiblii(knigaText)
        Settings.bibleTime = true
        bibleTime.value = false
        navigationActions.navigateToBibliaList(kniga >= 50, perevod)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        TextButton(
            onClick = {
                navigationActions.navigateToBibliaList(false, perevod)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Primary,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                if (perevod == Settings.PEREVODNADSAN) stringResource(R.string.psalter)
                else stringResource(R.string.stary_zapaviet),
                fontSize = 18.sp,
                color = PrimaryTextBlack
            )
        }
        if (perevod != Settings.PEREVODNADSAN) {
            TextButton(
                onClick = {
                    navigationActions.navigateToBibliaList(true, perevod)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Primary,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    stringResource(R.string.novy_zapaviet),
                    fontSize = 18.sp,
                    color = PrimaryTextBlack
                )
            }
        }
        TextButton(
            onClick = {
                bibleTime.value = true
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.bible_time), fontSize = 18.sp, color = PrimaryText)
        }
        /*TextButton(
            onClick = {
                navigationActions.navigateToVybranaeList(perevod)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.str_short_label1), fontSize = 18.sp, color = PrimaryText)
        }*/
        TextButton(
            onClick = {
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.poshuk), fontSize = 18.sp, color = PrimaryText)
        }
        if (perevod == Settings.PEREVODNADSAN) {
        }
        /*TextButton(
            onClick = {
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.zakladki_bible), fontSize = 18.sp, color = PrimaryText)
        }
        TextButton(
            onClick = {
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.natatki_biblii), fontSize = 18.sp, color = PrimaryText)
        }*/
        TextButton(
            onClick = {
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.alesyaSemukha2), fontSize = 18.sp, color = PrimaryText)
        }
    }
}