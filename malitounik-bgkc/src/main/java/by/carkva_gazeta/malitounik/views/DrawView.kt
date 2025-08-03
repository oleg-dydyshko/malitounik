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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.DialogNoInternet
import by.carkva_gazeta.malitounik.DialogProgramRadoiMaryia
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.ServiceRadyjoMaryia
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.WidgetRadyjoMaryia
import by.carkva_gazeta.malitounik.ui.theme.BackgroundDrawelMenu
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawView(
    drawerScrollStete: ScrollState,
    route: String,
    navigateToRazdel: (String) -> Unit
) {
    val context = LocalActivity.current as MainActivity
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var dialogNoInternet by remember { mutableStateOf(false) }
    var dialogProgram by remember { mutableStateOf(false) }
    val navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(selectedContainerColor = if (Settings.dzenNoch.value) BackgroundDrawelMenu else Divider)
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
        DrawerHeader()
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.kaliandar2),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route.contains(AllDestinations.KALIANDAR),
            onClick = {
                navigateToRazdel(AllDestinations.KALIANDAR)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.bogaslugbovyia_teksty),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.BOGASLUJBOVYIA_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.BOGASLUJBOVYIA_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.liturgikon),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.LITURGIKON_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.LITURGIKON_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.chasaslou),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.CHASASLOU_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.CHASASLOU_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.malitvy),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.MALITVY_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.MALITVY_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.akafisty),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.AKAFIST_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.AKAFIST_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.maje_natatki),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.MAE_NATATKI_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.MAE_NATATKI_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.MenuVybranoe),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            selected = route == AllDestinations.VYBRANAE_LIST,
            onClick = {
                navigateToRazdel(AllDestinations.VYBRANAE_LIST)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 5.dp),
            colors = navigationDrawerItemColors
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .clickable {
                        AppNavGraphState.bibleItem = !AppNavGraphState.bibleItem
                        if (AppNavGraphState.bibleItem && !AppNavGraphState.biblijatekaItem && !AppNavGraphState.piesnyItem && !AppNavGraphState.underItem) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.bibliaAll),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 21.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
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
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.title_biblia),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.title_biblia_bokun),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.title_psalter),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.title_biblia_charniauski),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    if (k.getBoolean("sinoidal_bible", false)) {
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = stringResource(id = R.string.bsinaidal),
                                    fontSize = Settings.fontInterface.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
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
                            },
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            colors = navigationDrawerItemColors
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
                    .padding(start = 21.dp, end = 2.dp)
                    .size(22.dp, 22.dp),
                painter = painterResource(R.drawable.krest),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                text = stringResource(id = R.string.padie_maryia),
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (Settings.isProgressVisableRadyjoMaryia.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(22.dp, 22.dp)
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
                    modifier = Modifier.size(5.dp, 5.dp),
                    painter = painterResource(R.drawable.poiter),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
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
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .clickable {
                        AppNavGraphState.biblijatekaItem = !AppNavGraphState.biblijatekaItem
                        if (AppNavGraphState.biblijatekaItem && !AppNavGraphState.piesnyItem && !AppNavGraphState.underItem) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.bibliateka_carkvy),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 21.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
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
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.bibliateka_niadaunia),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.bibliateka_gistoryia_carkvy),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.bibliateka_malitouniki),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.bibliateka_speuniki),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.bibliateka_rel_litaratura),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.arx_num_gaz),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                }
            }
        }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .clickable {
                        AppNavGraphState.piesnyItem = !AppNavGraphState.piesnyItem
                        if (AppNavGraphState.piesnyItem && !AppNavGraphState.underItem) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.song),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 21.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
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
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pesny1),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pesny2),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pesny3),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pesny4),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pesny5),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
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
                    .padding(horizontal = 5.dp)
                    .clickable {
                        AppNavGraphState.underItem = !AppNavGraphState.underItem
                        if (AppNavGraphState.underItem) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100)
                                drawerScrollStete.scrollTo(drawerScrollStete.maxValue)
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    text = stringResource(R.string.other),
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 21.dp, end = 2.dp)
                        .size(22.dp, 22.dp),
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
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.spovedz),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.UNDER_PADRYXTOUKA,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PADRYXTOUKA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pamiatka),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.UNDER_PAMIATKA,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PAMIATKA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.sviaty),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.UNDER_SVAITY_MUNU,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_SVAITY_MUNU)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.parafii),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.UNDER_PARAFII_BGKC,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PARAFII_BGKC)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.paschalia),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.UNDER_PASHALIA,
                        onClick = {
                            navigateToRazdel(AllDestinations.UNDER_PASHALIA)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.help),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.HELP,
                        onClick = {
                            navigateToRazdel(AllDestinations.HELP)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(id = R.string.pra_nas),
                                fontSize = Settings.fontInterface.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        selected = route == AllDestinations.PRANAS,
                        onClick = {
                            navigateToRazdel(AllDestinations.PRANAS)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(5.dp, 5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        colors = navigationDrawerItemColors
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            text = AppNavGraphState.cytata,
            fontSize = (Settings.fontInterface - 2).sp,
            textAlign = TextAlign.End,
            fontStyle = FontStyle.Italic,
            color = SecondaryText,
        )
        Icon(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp), painter = painterResource(R.drawable.lahatyp), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
        Icon(modifier = Modifier.fillMaxWidth(), painter = painterResource(R.drawable.lahatyp_apis), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
    }
}