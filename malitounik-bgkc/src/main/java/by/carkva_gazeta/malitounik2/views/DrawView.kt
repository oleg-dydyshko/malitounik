package by.carkva_gazeta.malitounik2.views

import android.content.Context
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik2.DialogNoInternet
import by.carkva_gazeta.malitounik2.DialogProgramRadoiMaryia
import by.carkva_gazeta.malitounik2.MainActivity
import by.carkva_gazeta.malitounik2.R
import by.carkva_gazeta.malitounik2.ServiceRadyjoMaryia
import by.carkva_gazeta.malitounik2.Settings
import by.carkva_gazeta.malitounik2.WidgetRadyjoMaryia
import by.carkva_gazeta.malitounik2.ui.theme.SecondaryText
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

@Composable
fun DrawView(
    modifier: Modifier = Modifier,
    route: String,
    navigateToRazdel: (String) -> Unit = { }
) {
    val context = LocalActivity.current as MainActivity
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var dialogNoInternet by remember { mutableStateOf(false) }
    var dialogProgram by remember { mutableStateOf(false) }
    if (dialogNoInternet) {
        DialogNoInternet {
            dialogNoInternet = false
        }
    }
    if (dialogProgram) {
        DialogProgramRadoiMaryia {
            dialogProgram = false
        }
    }
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        DrawerHeader(modifier)
        HorizontalDivider(
            modifier = modifier.padding(bottom = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.kaliandar2),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route.contains(AllDestinations.KALIANDAR),
            onClick = {
                navigateToRazdel(AllDestinations.KALIANDAR)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.liturgikon),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.BOGASLUJBOVYIA_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.BOGASLUJBOVYIA_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.malitvy),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.MALITVY_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.MALITVY_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.akafisty),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.AKAFIST_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.AKAFIST_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.ruzanec),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.RUJANEC_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.RUJANEC_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.maje_natatki),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.MAE_NATATKI_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.MAE_NATATKI_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.MenuVybranoe),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.VYBRANAE_LIST,
            onClick = {
                navigateToRazdel(AllDestinations.VYBRANAE_LIST)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        HorizontalDivider(
            modifier = modifier.padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.bibliaAll),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.BIBLIA,
            onClick = {
                navigateToRazdel(AllDestinations.BIBLIA)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 21.dp, end = 2.dp)
                    .size(24.dp, 24.dp),
                painter = painterResource(R.drawable.krest),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                text = stringResource(id = R.string.padie_maryia),
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            if (Settings.isProgressVisableRadyjoMaryia.value) {
                CircularProgressIndicator(modifier = Modifier.padding(horizontal = 10.dp).size(24.dp, 24.dp))
            }
            Icon(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        dialogProgram = true
                    },
                painter = painterResource(R.drawable.description),
                contentDescription = ""
            )
            val icon = if (!Settings.isPlayRadyjoMaryia.value) painterResource(R.drawable.play_arrow)
            else painterResource(R.drawable.pause)
            Icon(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        if (Settings.isNetworkAvailable(context)) {
                            if (!ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                                Settings.isProgressVisableRadyjoMaryia.value = true
                                val intent = Intent(context, ServiceRadyjoMaryia::class.java)
                                ContextCompat.startForegroundService(context, intent)
                                context.bindService(
                                    intent,
                                    context.mConnection,
                                    Context.BIND_AUTO_CREATE
                                )
                                Settings.isPlayRadyjoMaryia.value = true
                            } else {
                                context.mRadyjoMaryiaService?.apply {
                                    if (k.getBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false)) {
                                        context.sendBroadcast(
                                            Intent(
                                                context,
                                                WidgetRadyjoMaryia::class.java
                                            )
                                        )
                                    }
                                    playOrPause()
                                    Settings.isPlayRadyjoMaryia.value = isPlayingRadioMaria()
                                }
                            }
                        } else {
                            dialogNoInternet = true
                        }
                    },
                painter = icon,
                contentDescription = ""
            )
            Icon(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(end = 10.dp)
                    .clickable {
                        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                            if (context.isConnectServise) {
                                context.unbindService(context.mConnection)
                            }
                            context.isConnectServise = false
                            context.mRadyjoMaryiaService?.stopServiceRadioMaria()
                            Settings.isPlayRadyjoMaryia.value = false
                        }
                    },
                painter = painterResource(R.drawable.stop),
                contentDescription = ""
            )
        }
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(12.dp, 12.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    Settings.titleRadioMaryia.value,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = Settings.fontInterface.sp
                )
            }
        }
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.bibliateka_carkvy),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.BIBLIJATEKA_LIST,
            onClick = {
                navigateToRazdel(AllDestinations.BIBLIJATEKA_LIST)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.song),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.PIESNY_LIST,
            onClick = {
                navigateToRazdel(AllDestinations.PIESNY_LIST)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        HorizontalDivider(
            modifier = modifier.padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.spovedz),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.PADRYXTOUKA,
            onClick = {
                navigateToRazdel(AllDestinations.PADRYXTOUKA)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.pamiatka),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.PAMIATKA,
            onClick = {
                navigateToRazdel(AllDestinations.PAMIATKA)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.sviaty),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.SVAITY_MUNU,
            onClick = {
                navigateToRazdel(AllDestinations.SVAITY_MUNU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.parafii),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.PARAFII_BGKC,
            onClick = {
                navigateToRazdel(AllDestinations.PARAFII_BGKC)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.paschalia),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = route == AllDestinations.PASHALIA,
            onClick = {
                navigateToRazdel(AllDestinations.PASHALIA)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
    }
}

@Composable
fun DrawerHeader(modifier: Modifier) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        val inputStream = context.resources.openRawResource(R.raw.citata)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val citataList = ArrayList<String>()
        reader.forEachLine {
            val line = StringBuilder()
            val t1 = it.indexOf("(")
            if (t1 != -1) {
                line.append(it.substring(0, t1).trim())
                line.append("\n")
                line.append(it.substring(t1))
                citataList.add(line.toString())
            }
        }
        val annotated = AnnotatedString.Builder(citataList[Random.nextInt(citataList.size)]).apply {
            addStyle(
                SpanStyle(
                    fontFamily = FontFamily(Font(R.font.andantinoscript)),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = (Settings.fontInterface + 4).sp
                ), 0, 1
            )
            addStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.comici))), 1, this.length)
        }.toAnnotatedString()

        Text(
            modifier = modifier.fillMaxWidth(),
            text = annotated,
            fontSize = (Settings.fontInterface - 2).sp,
            textAlign = TextAlign.End,
            fontStyle = FontStyle.Italic,
            color = SecondaryText,
        )

        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.malitounik_name),
            textAlign = TextAlign.Center,
            fontSize = (Settings.fontInterface + 8).sp,
            lineHeight = ((Settings.fontInterface + 8) * 1.15).sp,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.bgkc_resource),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = (Settings.fontInterface - 2).sp
        )
    }
}