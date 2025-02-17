package by.carkva_gazeta.malitounik2

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import by.carkva_gazeta.malitounik2.views.HtmlText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun BibliaMenu(
    navController: NavHostController,
    setTitle: (String) -> Unit = { },
    navigateToSearchBible: (String) -> Unit = { }
) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var perevod by remember { mutableStateOf(k.getString("perevodBibileMenu", Settings.PEREVODSEMUXI) ?: Settings.PEREVODSEMUXI) }
    var bibleTime by remember { mutableStateOf(false) }
    var dialogVisable by remember { mutableStateOf(false) }
    if (dialogVisable) {
        DialogSemuxa(perevod == Settings.PEREVODSEMUXI) {
            dialogVisable = false
        }
    }
    if (bibleTime) {
        val prevodName = when (perevod) {
            Settings.PEREVODSEMUXI -> "biblia"
            Settings.PEREVODBOKUNA -> "bokuna"
            Settings.PEREVODCARNIAUSKI -> "carniauski"
            Settings.PEREVODNADSAN -> "nadsan"
            Settings.PEREVODSINOIDAL -> "sinaidal"
            else -> "biblia"
        }
        val knigaText = k.getString("bible_time_${prevodName}_kniga", "Быц") ?: "Быц"
        val kniga = knigaBiblii(knigaText)
        Settings.bibleTime = true
        bibleTime = false
        navigationActions.navigateToBibliaList(kniga >= 50, perevod)
    }
    val lazyRowState = rememberLazyListState()
    val list = listOf(
        Settings.PEREVODSEMUXI,
        Settings.PEREVODBOKUNA,
        Settings.PEREVODNADSAN,
        Settings.PEREVODCARNIAUSKI,
        Settings.PEREVODSINOIDAL
    )
    val context = LocalContext.current
    val selectState = remember(list) { list.map { false }.toMutableStateList() }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            when (perevod) {
                Settings.PEREVODSEMUXI -> {
                    selectState[0] = true
                    lazyRowState.scrollToItem(0)
                }

                Settings.PEREVODBOKUNA -> {
                    selectState[1] = true
                    lazyRowState.scrollToItem(1)
                }

                Settings.PEREVODNADSAN -> {
                    selectState[2] = true
                    lazyRowState.scrollToItem(2)
                }

                Settings.PEREVODCARNIAUSKI -> {
                    selectState[3] = true
                    lazyRowState.scrollToItem(3)
                }

                Settings.PEREVODSINOIDAL -> {
                    selectState[4] = true
                    lazyRowState.scrollToItem(4)
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        LazyRow(state = lazyRowState, modifier = Modifier.fillMaxWidth()) {
            items(list.size) { index ->
                val titlePerevod = when (index) {
                    0 -> stringResource(R.string.title_biblia2)
                    1 -> stringResource(R.string.title_biblia_bokun2)
                    2 -> stringResource(R.string.title_psalter)
                    3 -> stringResource(R.string.title_biblia_charniauski2)
                    4 -> stringResource(R.string.bsinaidal2)
                    else -> stringResource(R.string.title_biblia2)
                }
                FilterChip(
                    modifier = Modifier.padding(end = 10.dp),
                    onClick = {
                        for (i in 0..4)
                            selectState[i] = false
                        selectState[index] = !selectState[index]
                        val edit = k.edit()
                        when (index) {
                            0 -> {
                                edit.putString("perevodBibileMenu", Settings.PEREVODSEMUXI)
                                setTitle(context.getString(R.string.title_biblia))
                                perevod = Settings.PEREVODSEMUXI

                            }
                            1 -> {
                                edit.putString("perevodBibileMenu", Settings.PEREVODBOKUNA)
                                setTitle(context.getString(R.string.title_biblia_bokun))
                                perevod = Settings.PEREVODBOKUNA
                            }
                            2 -> {
                                edit.putString("perevodBibileMenu", Settings.PEREVODNADSAN)
                                setTitle(context.getString(R.string.title_psalter))
                                perevod = Settings.PEREVODNADSAN
                            }
                            3 -> {
                                edit.putString("perevodBibileMenu", Settings.PEREVODCARNIAUSKI)
                                setTitle(context.getString(R.string.title_biblia_charniauski))
                                perevod = Settings.PEREVODCARNIAUSKI
                            }
                            4 -> {
                                edit.putString("perevodBibileMenu", Settings.PEREVODSINOIDAL)
                                setTitle(context.getString(R.string.bsinaidal))
                                perevod = Settings.PEREVODSINOIDAL
                            }
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            lazyRowState.scrollToItem(index)
                        }
                        edit.apply()
                    },
                    label = {
                        Text(titlePerevod, fontSize = 18.sp)
                    },
                    selected = selectState[index],
                    leadingIcon = if (selectState[index]) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
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
                bibleTime = true
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
                navigateToSearchBible(perevod)
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
        if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA) {
            TextButton(
                onClick = {
                    dialogVisable = true
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
}

@Composable
fun DialogSemuxa(
    isSemuxa: Boolean,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.warning), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.alesyaSemukha))
        },
        text = {
            val context = LocalContext.current
            val inputStream = if (isSemuxa) context.resources.openRawResource(R.raw.all_rights_reserved_semuxa)
            else context.resources.openRawResource(R.raw.all_rights_reserved_bokun)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                HtmlText(text = reader.readText(), fontSize = 18.sp)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.close), fontSize = 18.sp)
            }
        }
    )
}