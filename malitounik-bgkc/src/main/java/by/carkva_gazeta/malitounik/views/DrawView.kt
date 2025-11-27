package by.carkva_gazeta.malitounik.views

import android.content.Context
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import by.carkva_gazeta.malitounik.DialogNoInternet
import by.carkva_gazeta.malitounik.DialogProgramRadoiMaryia
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.ServiceRadyjoMaryia
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.WidgetRadyjoMaryia
import by.carkva_gazeta.malitounik.ui.theme.BackgroundDrawelMenu
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawView(
    drawerScrollStete: ScrollState,
    route: String,
    navigateToRazdel: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
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
            .verticalScroll(drawerScrollStete)
    ) {
        DrawerHeader {
            navigateToRazdel(AllDestinations.CYTATY_MENU)
        }
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationItem(
            label = stringResource(id = R.string.kaliandar2),
            selected = route.contains(AllDestinations.KALIANDAR),
            onClick = {
                navigateToRazdel(AllDestinations.KALIANDAR)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.bogaslugbovyia_teksty),
            selected = route == AllDestinations.BOGASLUJBOVYIA_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.BOGASLUJBOVYIA_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier
                        .size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.liturgikon),
            selected = route == AllDestinations.LITURGIKON_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.LITURGIKON_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.chasaslou),
            selected = route == AllDestinations.CHASASLOU_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.CHASASLOU_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.malitvy),
            selected = route == AllDestinations.MALITVY_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.MALITVY_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.akafisty),
            selected = route == AllDestinations.AKAFIST_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.AKAFIST_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.maje_natatki),
            selected = route == AllDestinations.MAE_NATATKI_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.MAE_NATATKI_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        NavigationItem(
            label = stringResource(id = R.string.MenuVybranoe),
            selected = route == AllDestinations.VYBRANAE_LIST,
            onClick = {
                navigateToRazdel(AllDestinations.VYBRANAE_LIST)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            }
        )
        if (k.getBoolean("admin", false)) {
            NavigationItem(
                label = stringResource(id = R.string.pasochnica_up),
                selected = route == AllDestinations.PIASOCHNICA_LIST,
                onClick = {
                    navigateToRazdel(AllDestinations.PIASOCHNICA_LIST)
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        painter = painterResource(R.drawable.krest),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = ""
                    )
                }
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        AppNavGraphState.bibleItem = !AppNavGraphState.bibleItem
                        if (AppNavGraphState.bibleItem && !AppNavGraphState.biblijatekaItem && !AppNavGraphState.piesnyItem && !AppNavGraphState.underItem) {
                            coroutineScope.launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.bibliaAll),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(22.dp),
                    painter = painterResource(if (AppNavGraphState.bibleItem) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = ""
                )
            }
            AnimatedVisibility(
                AppNavGraphState.bibleItem, enter = fadeIn(
                    tween(
                        durationMillis = 500, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
                Column {
                    NavigationItem(
                        label = stringResource(id = R.string.title_biblia),
                        selected = route == AllDestinations.BIBLIA_SEMUXA,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIA_SEMUXA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.title_biblia_bokun),
                        selected = route == AllDestinations.BIBLIA_BOKUNA,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIA_BOKUNA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.title_psalter),
                        selected = route == AllDestinations.BIBLIA_NADSAN,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIA_NADSAN)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.title_biblia_charniauski),
                        selected = route == AllDestinations.BIBLIA_CHARNIAUSKI,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIA_CHARNIAUSKI)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    if (k.getBoolean("catolik_bible", false)) {
                        NavigationItem(
                            label = stringResource(id = R.string.title_biblia_catolik),
                            selected = route == AllDestinations.BIBLIA_CATOLIK,
                            onClick = {
                                navigateToRazdel(AllDestinations.BIBLIA_CATOLIK)
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(12.dp, 12.dp),
                                    painter = painterResource(R.drawable.krest),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                    if (k.getBoolean("sinoidal_bible", false)) {
                        NavigationItem(
                            label = stringResource(id = R.string.bsinaidal),
                            selected = route == AllDestinations.BIBLIA_SINODAL,
                            onClick = {
                                navigateToRazdel(AllDestinations.BIBLIA_SINODAL)
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(12.dp, 12.dp),
                                    painter = painterResource(R.drawable.krest),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                    if (k.getBoolean("newkingjames_bible", false)) {
                        NavigationItem(
                            label = stringResource(id = R.string.perevod_new_king_james),
                            selected = route == AllDestinations.BIBLIA_NEW_KING_JAMES,
                            onClick = {
                                navigateToRazdel(AllDestinations.BIBLIA_NEW_KING_JAMES)
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(12.dp, 12.dp),
                                    painter = painterResource(R.drawable.krest),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(22.dp),
                painter = painterResource(R.drawable.krest),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = ""
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
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(22.dp)
                )
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
            val icon = if (!ServiceRadyjoMaryia.isPlayingRadyjoMaryia) painterResource(R.drawable.play_arrow)
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
                                ServiceRadyjoMaryia.isPlayingRadyjoMaryia = true
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
                                    ServiceRadyjoMaryia.isPlayingRadyjoMaryia = isPlayingRadioMaria()
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
                    .clickable {
                        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                            if (context.isConnectServise) {
                                context.unbindService(context.mConnection)
                            }
                            context.isConnectServise = false
                            context.mRadyjoMaryiaService?.stopServiceRadioMaria()
                            ServiceRadyjoMaryia.isPlayingRadyjoMaryia = false
                        }
                    },
                painter = painterResource(R.drawable.stop),
                contentDescription = ""
            )
        }
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp),
                        painter = painterResource(R.drawable.poiter),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = ""
                    )
                    Text(
                        text = ServiceRadyjoMaryia.titleRadyjoMaryia,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                }
                var expandedSviaty by remember { mutableStateOf(false) }
                val radioMaryiaList = stringArrayResource(R.array.radio_maryia_list)
                var radioMaryiaListPosition by remember { mutableIntStateOf(k.getInt("radioMaryiaListPosition", 0)) }
                ExposedDropdownMenuBox(
                    modifier = Modifier.padding(10.dp),
                    expanded = expandedSviaty,
                    onExpandedChange = { expandedSviaty = it },
                ) {
                    Row(
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .clip(MaterialTheme.shapes.small)
                            .clickable {}
                            .background(Divider)
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(1f),
                            text = radioMaryiaList[radioMaryiaListPosition],
                            fontSize = (Settings.fontInterface - 2).sp,
                            color = PrimaryText,
                        )
                        Icon(
                            modifier = Modifier
                                .padding(start = 21.dp, end = 2.dp)
                                .size(22.dp),
                            painter = painterResource(if (expandedSviaty) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                            tint = PrimaryText,
                            contentDescription = ""
                        )
                    }
                    ExposedDropdownMenu(
                        containerColor = Divider,
                        expanded = expandedSviaty,
                        onDismissRequest = { expandedSviaty = false },
                    ) {
                        radioMaryiaList.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = { Text(text = option, fontSize = (Settings.fontInterface - 2).sp) }, onClick = {
                                    if (radioMaryiaListPosition != index) {
                                        radioMaryiaListPosition = index
                                        expandedSviaty = false
                                        k.edit {
                                            putInt("radioMaryiaListPosition", index)
                                        }
                                        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                                            if (context.isConnectServise) {
                                                context.unbindService(context.mConnection)
                                            }
                                            context.isConnectServise = false
                                            context.mRadyjoMaryiaService?.stopServiceRadioMaria()
                                            ServiceRadyjoMaryia.isPlayingRadyjoMaryia = false
                                        }
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
                                                ServiceRadyjoMaryia.isPlayingRadyjoMaryia = true
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
                                                    ServiceRadyjoMaryia.isPlayingRadyjoMaryia = isPlayingRadioMaria()
                                                }
                                            }
                                        } else {
                                            dialogNoInternet = true
                                        }
                                    }
                                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding, colors = MenuDefaults.itemColors(textColor = PrimaryText)
                            )
                        }
                    }
                }
            }
        }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        AppNavGraphState.biblijatekaItem = !AppNavGraphState.biblijatekaItem
                        if (AppNavGraphState.biblijatekaItem && !AppNavGraphState.piesnyItem && !AppNavGraphState.underItem) {
                            coroutineScope.launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.bibliateka_carkvy),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(22.dp),
                    painter = painterResource(if (AppNavGraphState.biblijatekaItem) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = ""
                )
            }
            AnimatedVisibility(
                AppNavGraphState.biblijatekaItem, enter = fadeIn(
                    tween(
                        durationMillis = 500, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
                Column {
                    NavigationItem(
                        label = stringResource(id = R.string.bibliateka_niadaunia),
                        selected = route == AllDestinations.BIBLIJATEKA_NIADAUNIA,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIJATEKA_NIADAUNIA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.bibliateka_gistoryia_carkvy),
                        selected = route == AllDestinations.BIBLIJATEKA_GISTORYIA,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIJATEKA_GISTORYIA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.bibliateka_malitouniki),
                        selected = route == AllDestinations.BIBLIJATEKA_MALITOUNIKI,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIJATEKA_MALITOUNIKI)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.bibliateka_speuniki),
                        selected = route == AllDestinations.BIBLIJATEKA_SPEUNIKI,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIJATEKA_SPEUNIKI)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.bibliateka_rel_litaratura),
                        selected = route == AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIJATEKA_RELIGIJNAIA_LITARATURA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.arx_num_gaz),
                        selected = route == AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU,
                        onClick = {
                            navigateToRazdel(AllDestinations.BIBLIJATEKA_ARXIU_NUMAROU)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                }
            }
        }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        AppNavGraphState.piesnyItem = !AppNavGraphState.piesnyItem
                        if (AppNavGraphState.piesnyItem && !AppNavGraphState.underItem) {
                            coroutineScope.launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.song),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(22.dp),
                    painter = painterResource(if (AppNavGraphState.piesnyItem) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = ""
                )
            }
            AnimatedVisibility(
                AppNavGraphState.piesnyItem, enter = fadeIn(
                    tween(
                        durationMillis = 500, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
                Column {
                    NavigationItem(
                        label = stringResource(id = R.string.pesny1),
                        selected = route == AllDestinations.PIESNY_PRASLAULENNIA,
                        onClick = {
                            navigateToRazdel(AllDestinations.PIESNY_PRASLAULENNIA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.pesny2),
                        selected = route == AllDestinations.PIESNY_ZA_BELARUS,
                        onClick = {
                            navigateToRazdel(AllDestinations.PIESNY_ZA_BELARUS)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.pesny3),
                        selected = route == AllDestinations.PIESNY_DA_BAGARODZICY,
                        onClick = {
                            navigateToRazdel(AllDestinations.PIESNY_DA_BAGARODZICY)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.pesny4),
                        selected = route == AllDestinations.PIESNY_KALIADNYIA,
                        onClick = {
                            navigateToRazdel(AllDestinations.PIESNY_KALIADNYIA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.pesny5),
                        selected = route == AllDestinations.PIESNY_TAIZE,
                        onClick = {
                            navigateToRazdel(AllDestinations.PIESNY_TAIZE)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(12.dp, 12.dp),
                                painter = painterResource(R.drawable.krest),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        AppNavGraphState.underItem = !AppNavGraphState.underItem
                        if (AppNavGraphState.underItem) {
                            coroutineScope.launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    text = stringResource(R.string.other),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(22.dp),
                    painter = painterResource(if (AppNavGraphState.underItem) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = ""
                )
            }
            AnimatedVisibility(
                AppNavGraphState.underItem, enter = fadeIn(
                    tween(
                        durationMillis = 500, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
            ) {
                Column {
                    NavigationItem(
                        label = stringResource(id = R.string.spovedz),
                        selected = route == AllDestinations.UNDER_PADRYXTOUKA,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PADRYXTOUKA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.pamiatka),
                        selected = route == AllDestinations.UNDER_PAMIATKA,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PAMIATKA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.sviaty),
                        selected = route == AllDestinations.UNDER_SVAITY_MUNU,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_SVAITY_MUNU)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.parafii),
                        selected = route == AllDestinations.UNDER_PARAFII_BGKC,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PARAFII_BGKC)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.paschalia),
                        selected = route == AllDestinations.UNDER_PASHALIA,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PASHALIA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.help),
                        selected = route == AllDestinations.HELP,
                        onClick = {
                            navigateToRazdel(AllDestinations.HELP)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                    NavigationItem(
                        label = stringResource(id = R.string.pra_nas),
                        selected = route == AllDestinations.PRANAS,
                        onClick = {
                            navigateToRazdel(AllDestinations.PRANAS)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationItem(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val color = if (selected) {
        if (Settings.dzenNoch) BackgroundDrawelMenu else Divider
    } else {
        Color.Unspecified
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clip(MaterialTheme.shapes.small)
            .background(color)
            .clickable {
                onClick()
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(
                modifier = Modifier
                    .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                    .weight(1f),
                text = label,
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun DrawerHeader(onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(bottom = 10.dp),
            text = AppNavGraphState.cytata,
            fontSize = (Settings.fontInterface - 2).sp,
            textAlign = TextAlign.End,
            fontStyle = FontStyle.Italic,
            color = SecondaryText,
        )
        Icon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp), painter = painterResource(R.drawable.lahatyp), contentDescription = "", tint = MaterialTheme.colorScheme.primary
        )
        Icon(modifier = Modifier.fillMaxWidth(), painter = painterResource(R.drawable.lahatyp_apis), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
    }
}