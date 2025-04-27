package by.carkva_gazeta.malitounik

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.TitleCalendarMounth
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.HtmlText
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun BibliaMenu(
    navController: NavHostController,
    perevod: String,
    innerPadding: PaddingValues,
    navigateToSearchBible: (String) -> Unit,
    navigateToCytanniList: (String, String) -> Unit,
    navigateToBogaslujbovyia: (String, Int) -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var bibleTime by remember { mutableStateOf(false) }
    if (bibleTime) {
        val prevodName = when (perevod) {
            Settings.PEREVODSEMUXI -> {
                "biblia"
            }

            Settings.PEREVODBOKUNA -> {
                "bokuna"
            }

            Settings.PEREVODNADSAN -> {
                "nadsan"
            }

            Settings.PEREVODCARNIAUSKI -> {
                "carniauski"
            }

            Settings.PEREVODSINOIDAL -> {
                "sinaidal"
            }

            else -> "biblia"
        }
        val knigaText = k.getString("bible_time_${prevodName}_kniga", "Быц") ?: "Быц"
        val kniga = knigaBiblii(knigaText)
        Settings.bibleTime = true
        bibleTime = false
        navigationActions.navigateToBibliaList(kniga >= 50, perevod)
    }
    var dialogVisable by remember { mutableStateOf(false) }
    if (dialogVisable) {
        DialogSemuxa(perevod == Settings.PEREVODSEMUXI) {
            dialogVisable = false
        }
    }
    var pesnyView by rememberSaveable { mutableStateOf(false) }
    var dialogPeryiadyView by rememberSaveable { mutableStateOf(false) }
    if (dialogPeryiadyView) {
        DialogPeryaidy {
            dialogPeryiadyView = false
        }
    }
    var dialogImageView by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    if (perevod == Settings.PEREVODNADSAN) stringResource(R.string.psalter)
                    else stringResource(R.string.stary_zapaviet),
                    fontSize = Settings.fontInterface.sp,
                    color = PrimaryTextBlack,
                    textAlign = TextAlign.Center
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        stringResource(R.string.novy_zapaviet),
                        fontSize = Settings.fontInterface.sp,
                        color = PrimaryTextBlack,
                        textAlign = TextAlign.Center
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
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.bible_time), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
            }
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
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.poshuk), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
            }
            if (perevod == Settings.PEREVODNADSAN) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .align(Alignment.CenterHorizontally)
                        .size(width = 400.dp, height = Dp.Unspecified)
                ) {
                    Row {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(TitleCalendarMounth)
                                .padding(10.dp),
                            text = stringResource(R.string.kafizma),
                            fontSize = Settings.fontInterface.sp,
                            textAlign = TextAlign.Center,
                            color = PrimaryTextBlack
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            Text(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .weight(1f)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(Divider)
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        val index = when (i) {
                                            1 -> "1"
                                            2 -> "9"
                                            3 -> "17"
                                            4 -> "24"
                                            5 -> "32"
                                            else -> "1"
                                        }
                                        navigateToCytanniList(
                                            "Пс $index",
                                            perevod
                                        )
                                    },
                                text = i.toString(),
                                fontSize = Settings.fontInterface.sp,
                                textAlign = TextAlign.Center,
                                color = PrimaryText
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 6..10) {
                            Text(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .weight(1f)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(Divider)
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        val index = when (i) {
                                            6 -> "37"
                                            7 -> "46"
                                            8 -> "55"
                                            9 -> "64"
                                            10 -> "70"
                                            else -> "37"
                                        }
                                        navigateToCytanniList(
                                            "Пс $index",
                                            perevod
                                        )
                                    },
                                text = i.toString(),
                                fontSize = Settings.fontInterface.sp,
                                textAlign = TextAlign.Center,
                                color = PrimaryText
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 11..15) {
                            Text(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .weight(1f)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(Divider)
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        val index = when (i) {
                                            11 -> "77"
                                            12 -> "85"
                                            13 -> "91"
                                            14 -> "101"
                                            15 -> "105"
                                            else -> "77"
                                        }
                                        navigateToCytanniList(
                                            "Пс $index",
                                            perevod
                                        )
                                    },
                                text = i.toString(),
                                fontSize = Settings.fontInterface.sp,
                                textAlign = TextAlign.Center,
                                color = PrimaryText
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 16..20) {
                            Text(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .weight(1f)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(Divider)
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        val index = when (i) {
                                            16 -> "109"
                                            17 -> "118"
                                            18 -> "119"
                                            19 -> "134"
                                            20 -> "143"
                                            else -> "109"
                                        }
                                        navigateToCytanniList(
                                            "Пс $index",
                                            perevod
                                        )
                                    },
                                text = i.toString(),
                                fontSize = Settings.fontInterface.sp,
                                textAlign = TextAlign.Center,
                                color = PrimaryText
                            )
                        }
                    }
                }
                TextButton(
                    onClick = {
                        navigateToBogaslujbovyia(context.getString(R.string.malitva_pered), R.raw.nadsan_pered)
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.malitva_pered), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                }
                TextButton(
                    onClick = {
                        navigateToBogaslujbovyia(context.getString(R.string.malitva_posle), R.raw.nadsan_posle)
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.malitva_posle), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                }
                TextButton(
                    onClick = {
                        pesnyView = !pesnyView
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.pesni), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                }
                AnimatedVisibility(
                    pesnyView, enter = fadeIn(
                        tween(
                            durationMillis = 1000, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 1000, easing = LinearOutSlowInEasing))
                ) {
                    Column {
                        for (i in 1..9) {
                            Row(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clickable {
                                        navigationActions.navigateToBogaslujbovyia(
                                            context.getString(R.string.pesnia, i),
                                            when (i) {
                                                1 -> R.raw.nadsan_pesni_1
                                                2 -> R.raw.nadsan_pesni_2
                                                3 -> R.raw.nadsan_pesni_3
                                                4 -> R.raw.nadsan_pesni_4
                                                5 -> R.raw.nadsan_pesni_5
                                                6 -> R.raw.nadsan_pesni_6
                                                7 -> R.raw.nadsan_pesni_7
                                                8 -> R.raw.nadsan_pesni_8
                                                9 -> R.raw.nadsan_pesni_9
                                                else -> R.raw.nadsan_pesni_1
                                            }
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp, 5.dp),
                                    painter = painterResource(R.drawable.poiter),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                                Text(
                                    stringResource(R.string.pesnia, i),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = Settings.fontInterface.sp
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
                TextButton(
                    onClick = {
                        dialogPeryiadyView = true
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.peryiady), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                }
                TextButton(
                    onClick = {
                        dialogImageView = !dialogImageView
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.title_psalter_privila), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
                }
            }
            AnimatedVisibility(
                dialogImageView, enter = fadeIn(
                    tween(
                        durationMillis = 1000, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 1000, easing = LinearOutSlowInEasing))
            ) {
                Image(
                    painter = painterResource(R.drawable.pravily_chytannia_psaltyria), contentDescription = "", modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(), contentScale = ContentScale.FillWidth
                )
            }
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
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        stringResource(R.string.alesyaSemukha2),
                        fontSize = Settings.fontInterface.sp,
                        color = PrimaryText,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

@Composable
fun DialogSemuxa(
    isSemuxa: Boolean,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.alesyaSemukha).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                val context = LocalContext.current
                val inputStream =
                    if (isSemuxa) context.resources.openRawResource(R.raw.all_rights_reserved_semuxa)
                    else context.resources.openRawResource(R.raw.all_rights_reserved_bokun)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
                    HtmlText(text = reader.readText(), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogPeryaidy(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.peryiady).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                val inputStream = LocalContext.current.resources.openRawResource(R.raw.nadsan_periody)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                Row(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    HtmlText(
                        modifier = Modifier.padding(10.dp),
                        text = reader.readText(),
                        fontSize = Settings.fontInterface.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogImage(
    painter: Painter,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painter, contentDescription = "", Modifier
                        .fillMaxWidth(), contentScale = ContentScale.FillWidth
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}