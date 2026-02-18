package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.Settings.NOTIFICATION_CHANNEL_ID_SVIATY
import by.carkva_gazeta.malitounik.Settings.setNotifications
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.views.PlainTooltip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(navController: NavHostController, viewModel: SearchBibleViewModel) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window, view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    var modeNight by remember { mutableIntStateOf(k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM)) }
    var dialodLogin by rememberSaveable { mutableStateOf(false) }
    var dialodNotificatin by rememberSaveable { mutableStateOf(false) }
    var admin by remember { mutableStateOf(k.getBoolean("admin", false) || k.getBoolean("adminOnlyNotifications", false)) }
    var backPressHandled by remember { mutableStateOf(false) }
    var dialodAdmitExit by rememberSaveable { mutableStateOf(false) }
    if (dialodAdmitExit) {
        DialogAdminExit(onConfirm = {
            admin = false
            k.edit {
                putBoolean("admin", false)
                putBoolean("adminOnlyNotifications", false)
            }
            dialodAdmitExit = false
        }) {
            dialodAdmitExit = false
        }
    }
    Settings.fontInterface = remember { getFontInterface(context) }
    if (dialodLogin) {
        DialogLogin { isLogin ->
            if (isLogin) {
                k.edit {
                    putBoolean("admin", true)
                }
                admin = true
            }
            dialodLogin = false
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            when (k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)) {
                Settings.NOTIFICATION_SVIATY_ONLY -> setNotificationOnly(context)
                Settings.NOTIFICATION_SVIATY_FULL -> setNotificationFull(context)
            }
        } else {
            k.edit {
                putInt("notification", Settings.NOTIFICATION_SVIATY_NONE)
            }
            setNotificationNon(context)
        }
    }
    if (dialodNotificatin) {
        DialogNotification(onConfirm = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionCheck2 = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                if (permissionCheck2 == PackageManager.PERMISSION_DENIED) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            dialodNotificatin = false
        }) {
            dialodNotificatin = false
        }
    }
    var dialodClearChache by rememberSaveable { mutableStateOf(false) }
    if (dialodClearChache) {
        DialogClearChash(onConfirm = {
            dialodClearChache = false
            var file = File("${context.filesDir}/bibliatekaImage")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/bibliatekaPdf")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/icons")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/iconsApisanne")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/image_temp")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/sviatyia")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/bibliateka.json")
            if (file.exists()) file.delete()
            file = File("${context.filesDir}/opisanie_sviat.json")
            if (file.exists()) file.delete()
            file = File("${context.filesDir}/piarliny.json")
            if (file.exists()) file.delete()
            file = File("${context.filesDir}/Catolik")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/Sinodal")
            if (file.exists()) file.deleteRecursively()
            file = File("${context.filesDir}/NewAmericanBible")
            if (file.exists()) file.deleteRecursively()
        }) {
            dialodClearChache = false
        }
    }
    var dialogDownLoad by remember { mutableStateOf(false) }
    var perevod by remember { mutableStateOf(Settings.PEREVODSEMUXI) }
    if (dialogDownLoad) {
        DialogDownLoadBible(viewModel, perevod, onConfirmation = {
            dialogDownLoad = false
        }) {
            dialogDownLoad = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.clickable {
                            Settings.vibrate()
                            maxLine.intValue = Int.MAX_VALUE
                            coroutineScope.launch {
                                delay(5000L)
                                maxLine.intValue = 1
                            }
                        }, text = stringResource(R.string.tools_item).uppercase(), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, fontSize = Settings.fontInterface.sp, maxLines = maxLine.intValue, overflow = TextOverflow.Ellipsis
                    )
                }, navigationIcon = {
                    PlainTooltip(stringResource(R.string.exit_page), TooltipAnchorPosition.Below) {
                        IconButton(onClick = {
                            Settings.vibrate()
                            if (!backPressHandled) {
                                backPressHandled = true
                                navController.popBackStack()
                            }
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = stringResource(R.string.exit_page)
                            )
                        })
                    }
                }, actions = {
                    if (admin) {
                        IconButton(onClick = {
                            Settings.vibrate()
                            dialodAdmitExit = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.logout), contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }) { innerPadding ->
        val interactionSourse = remember { MutableInteractionSource() }
        var adminItemCount by remember { mutableIntStateOf(0) }
        var adminClickTime by remember { mutableLongStateOf(0) }
        Column(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr), innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
                )
                .padding(horizontal = 10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(
                        interactionSource = interactionSourse, indication = null
                    ) {
                        if (SystemClock.elapsedRealtime() - adminClickTime < 2000) {
                            adminItemCount++
                        } else {
                            adminItemCount = 1
                        }
                        adminClickTime = SystemClock.elapsedRealtime()
                        if (adminItemCount == 7) {
                            dialodLogin = true
                        }
                    }, text = stringResource(R.string.vygliad), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Column(Modifier.selectableGroup()) {
                val isSystemInDarkTheme = isSystemInDarkTheme()
                val edit = k.edit()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_SYSTEM
                            edit.putInt(
                                "mode_night", Settings.MODE_NIGHT_SYSTEM
                            )
                            edit.apply()
                            Settings.dzenNoch = isSystemInDarkTheme
                            (context as MainActivity).removelightSensor()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_SYSTEM, onClick = {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_SYSTEM
                            edit.putInt(
                                "mode_night", Settings.MODE_NIGHT_SYSTEM
                            )
                            edit.apply()
                            Settings.dzenNoch = isSystemInDarkTheme
                            (context as MainActivity).removelightSensor()
                        })
                    Text(
                        stringResource(R.string.system), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_NO
                            edit.putInt("mode_night", Settings.MODE_NIGHT_NO)
                            edit.apply()
                            Settings.dzenNoch = false
                            (context as MainActivity).removelightSensor()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_NO, onClick = {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_NO
                            edit.putInt("mode_night", Settings.MODE_NIGHT_NO)
                            edit.apply()
                            Settings.dzenNoch = false
                            (context as MainActivity).removelightSensor()
                        })
                    Text(
                        stringResource(R.string.day), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_YES
                            edit.putInt(
                                "mode_night", Settings.MODE_NIGHT_YES
                            )
                            edit.apply()
                            Settings.dzenNoch = true
                            (context as MainActivity).removelightSensor()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_YES, onClick = {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_YES
                            edit.putInt(
                                "mode_night", Settings.MODE_NIGHT_YES
                            )
                            edit.apply()
                            Settings.dzenNoch = true
                            (context as MainActivity).removelightSensor()
                        })
                    Text(
                        stringResource(R.string.widget_day_d_n), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_AUTO
                            edit.putInt(
                                "mode_night", Settings.MODE_NIGHT_AUTO
                            )
                            edit.apply()
                            (context as MainActivity).setlightSensor()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_AUTO, onClick = {
                            Settings.vibrate()
                            modeNight = Settings.MODE_NIGHT_AUTO
                            edit.putInt(
                                "mode_night", Settings.MODE_NIGHT_AUTO
                            )
                            edit.apply()
                            (context as MainActivity).setlightSensor()
                        })
                    Text(
                        stringResource(R.string.auto_widget_day_d_n), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
            }
            var fontSizeInterface by remember { mutableFloatStateOf(k.getFloat("fontSizeInterface", 20f)) }
            Text(
                modifier = Modifier.padding(top = 10.dp), text = stringResource(R.string.settengs_font_size_app), fontStyle = FontStyle.Italic, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
            )
            Slider(
                valueRange = 18f..26f, value = fontSizeInterface, onValueChange = {
                    k.edit {
                        putFloat("fontSizeInterface", it)
                    }
                    if (k.getFloat("font_biblia", 22f) <= 26f) {
                        k.edit {
                            putFloat("font_biblia", it)
                        }
                    }
                    fontSizeInterface = it
                    Settings.fontInterface = it
                }, colors = SliderDefaults.colors(inactiveTrackColor = Divider)
            )
            Text(
                modifier = Modifier.padding(top = 10.dp), text = stringResource(R.string.settings_title_panel), fontStyle = FontStyle.Italic, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
            )
            Column(Modifier.selectableGroup()) {
                var buttomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
                val edit = k.edit()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            buttomBar = false
                            edit.putBoolean("bottomBar", false)
                            edit.apply()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !buttomBar, onClick = {
                            Settings.vibrate()
                            buttomBar = false
                            edit.putBoolean("bottomBar", false)
                            edit.apply()
                        })
                    Text(
                        stringResource(R.string.settings_top_panel), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            buttomBar = true
                            edit.putBoolean("bottomBar", true)
                            edit.apply()
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = buttomBar, onClick = {
                            Settings.vibrate()
                            buttomBar = true
                            edit.putBoolean("bottomBar", true)
                            edit.apply()
                        })
                    Text(
                        stringResource(R.string.settings_battom_panel), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
            }
            var adminDayInYearState by remember { mutableStateOf(k.getBoolean("adminDayInYear", false)) }
            var adminOnlyNotificationsState by remember { mutableStateOf(k.getBoolean("adminOnlyNotifications", false)) }
            var adminNotificationsState by remember { mutableStateOf(k.getBoolean("adminNotifications", false)) }
            if (admin) {
                Text(
                    modifier = Modifier.padding(top = 10.dp), text = stringResource(R.string.admin), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clickable {
                            Settings.vibrate()
                            adminDayInYearState = !adminDayInYearState
                            k.edit {
                                putBoolean("adminDayInYear", adminDayInYearState)
                            }
                        }) {
                    Text(
                        stringResource(R.string.admin_day_in_year), modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        modifier = Modifier.scale(0.8f), checked = adminDayInYearState, onCheckedChange = {
                            Settings.vibrate()
                            adminDayInYearState = it
                            k.edit {
                                putBoolean("adminDayInYear", adminDayInYearState)
                            }
                        })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clickable {
                            Settings.vibrate()
                            adminOnlyNotificationsState = !adminOnlyNotificationsState
                            k.edit {
                                putBoolean("adminOnlyNotifications", adminOnlyNotificationsState)
                                putBoolean("admin", !adminOnlyNotificationsState)
                            }
                        }) {
                    Text(
                        stringResource(R.string.update_resourse_only), modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        modifier = Modifier.scale(0.8f), checked = adminOnlyNotificationsState, onCheckedChange = {
                            Settings.vibrate()
                            adminOnlyNotificationsState = it
                            k.edit {
                                putBoolean("adminOnlyNotifications", adminOnlyNotificationsState)
                                putBoolean("admin", !adminOnlyNotificationsState)
                            }
                        })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clickable {
                            Settings.vibrate()
                            adminNotificationsState = !adminNotificationsState
                            k.edit {
                                putBoolean("adminNotifications", adminNotificationsState)
                            }
                        }) {
                    Text(
                        stringResource(R.string.apav_no), modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        modifier = Modifier.scale(0.8f), checked = adminNotificationsState, onCheckedChange = {
                            Settings.vibrate()
                            adminNotificationsState = it
                            k.edit {
                                putBoolean("adminNotifications", adminNotificationsState)
                            }
                        })
                }
            }
            Text(
                modifier = Modifier.padding(top = 10.dp), text = stringResource(R.string.econom_enargi), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var power by remember { mutableStateOf(k.getBoolean("power", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        power = !power
                        k.edit {
                            putBoolean("power", power)
                        }
                        if (power) (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        else (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.econom_enargi_no), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = power, onCheckedChange = {
                        Settings.vibrate()
                        power = it
                        k.edit {
                            putBoolean("power", power)
                        }
                        if (power) (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        else (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    })
            }
            Text(
                modifier = Modifier.padding(top = 10.dp), text = stringResource(R.string.biblia), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var sinoidalState by remember { mutableStateOf(k.getBoolean("sinoidal_bible", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        sinoidalState = !sinoidalState
                        k.edit {
                            putBoolean("sinoidal_bible", sinoidalState)
                            if (!sinoidalState && k.getString("perevodMaranata", Settings.PEREVODSEMUXI) == Settings.PEREVODSINOIDAL) {
                                putString("perevodMaranata", Settings.PEREVODSEMUXI)
                            }
                        }
                        if (sinoidalState) {
                            val dir = File("${context.filesDir}/Sinodal")
                            if (!dir.exists()) {
                                perevod = Settings.PEREVODSINOIDAL
                                dialogDownLoad = true
                            }
                        }
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.bsinaidal), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = sinoidalState, onCheckedChange = {
                        Settings.vibrate()
                        sinoidalState = it
                        k.edit {
                            putBoolean("sinoidal_bible", sinoidalState)
                            if (!sinoidalState && k.getString("perevodMaranata", Settings.PEREVODSEMUXI) == Settings.PEREVODSINOIDAL) {
                                putString("perevodMaranata", Settings.PEREVODSEMUXI)
                            }
                        }
                        if (sinoidalState) {
                            val dir = File("${context.filesDir}/Sinodal")
                            if (!dir.exists()) {
                                perevod = Settings.PEREVODSINOIDAL
                                dialogDownLoad = true
                            }
                        }
                    })
            }
            var catolikState by remember { mutableStateOf(k.getBoolean("catolik_bible", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        catolikState = !catolikState
                        k.edit {
                            putBoolean("catolik_bible", catolikState)
                            if (!catolikState && k.getString("perevod", Settings.PEREVODSEMUXI) == Settings.PEREVODCATOLIK) {
                                putString("perevod", Settings.PEREVODSEMUXI)
                            }
                        }
                        if (catolikState) {
                            val dir = File("${context.filesDir}/Catolik")
                            if (!dir.exists()) {
                                perevod = Settings.PEREVODCATOLIK
                                dialogDownLoad = true
                            }
                        }
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.title_biblia_catolik), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = catolikState, onCheckedChange = {
                        Settings.vibrate()
                        catolikState = it
                        k.edit {
                            putBoolean("catolik_bible", catolikState)
                            if (!catolikState && k.getString("perevod", Settings.PEREVODSEMUXI) == Settings.PEREVODCATOLIK) {
                                putString("perevod", Settings.PEREVODSEMUXI)
                            }
                        }
                        if (catolikState) {
                            val dir = File("${context.filesDir}/Catolik")
                            if (!dir.exists()) {
                                perevod = Settings.PEREVODCATOLIK
                                dialogDownLoad = true
                            }
                        }
                    })
            }
            var newkingjamesState by remember { mutableStateOf(k.getBoolean("newkingjames_bible", false)) }
            var newkingjamesTranslate by remember { mutableStateOf(k.getBoolean("newkingjames_translate", false)) }
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .border(
                        width = 1.dp,
                        color = SecondaryText,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clickable {
                            Settings.vibrate()
                            newkingjamesState = !newkingjamesState
                            k.edit {
                                putBoolean("newkingjames_bible", newkingjamesState)
                                if (!newkingjamesState && k.getString("perevodMaranata", Settings.PEREVODSEMUXI) == Settings.PEREVODNEWAMERICANBIBLE) {
                                    putString("perevodMaranata", Settings.PEREVODSEMUXI)
                                }
                                if (newkingjamesState) {
                                    putBoolean("newkingjames_translate", false)
                                    newkingjamesTranslate = false
                                }
                            }
                            if (newkingjamesState) {
                                val dir = File("${context.filesDir}/NewAmericanBible")
                                if (!dir.exists()) {
                                    perevod = Settings.PEREVODNEWAMERICANBIBLE
                                    dialogDownLoad = true
                                }
                            }
                        }
                        .padding(vertical = 5.dp)) {
                    Text(
                        stringResource(R.string.perevod_new_american_bible), modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        modifier = Modifier.scale(0.8f), checked = newkingjamesState, onCheckedChange = {
                            Settings.vibrate()
                            newkingjamesState = it
                            k.edit {
                                putBoolean("newkingjames_bible", newkingjamesState)
                                if (!newkingjamesState && k.getString("perevodMaranata", Settings.PEREVODSEMUXI) == Settings.PEREVODNEWAMERICANBIBLE) {
                                    putString("perevodMaranata", Settings.PEREVODSEMUXI)
                                }
                                if (newkingjamesState) {
                                    putBoolean("newkingjames_translate", false)
                                    newkingjamesTranslate = false
                                }
                            }
                            if (newkingjamesState) {
                                val dir = File("${context.filesDir}/NewAmericanBible")
                                if (!dir.exists()) {
                                    perevod = Settings.PEREVODNEWAMERICANBIBLE
                                    dialogDownLoad = true
                                }
                            }
                        })
                }
                if (newkingjamesState) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .clickable {
                                Settings.vibrate()
                                newkingjamesTranslate = !newkingjamesTranslate
                                k.edit {
                                    putBoolean("newkingjames_translate", newkingjamesTranslate)
                                }
                            }
                            .padding(vertical = 5.dp)) {
                        Text(
                            stringResource(R.string.translate), modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                        )
                        Switch(
                            modifier = Modifier.scale(0.8f), checked = newkingjamesTranslate, onCheckedChange = {
                                Settings.vibrate()
                                newkingjamesTranslate = it
                                k.edit {
                                    putBoolean("newkingjames_translate", newkingjamesTranslate)
                                }
                            })
                    }
                }
            }
            var maranafaState by remember { mutableStateOf(k.getBoolean("maranafa", false)) }
            var paralelState by remember { mutableStateOf(k.getBoolean("paralel_maranata", true)) }
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .border(
                        width = 1.dp,
                        color = SecondaryText,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clickable {
                            Settings.vibrate()
                            maranafaState = !maranafaState
                            k.edit {
                                putBoolean("maranafa", maranafaState)
                                if (maranafaState) {
                                    putBoolean("paralel_maranata", true)
                                    paralelState = true
                                }
                            }
                        }
                        .padding(vertical = 5.dp)) {
                    Text(
                        stringResource(R.string.maranata_opis), modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        modifier = Modifier.scale(0.8f), checked = maranafaState, onCheckedChange = {
                            Settings.vibrate()
                            maranafaState = it
                            k.edit {
                                putBoolean("maranafa", maranafaState)
                                if (maranafaState) {
                                    putBoolean("paralel_maranata", true)
                                    paralelState = true
                                }
                            }
                        })
                }
                if (maranafaState) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .clickable {
                                Settings.vibrate()
                                paralelState = !paralelState
                                k.edit {
                                    putBoolean("paralel_maranata", paralelState)
                                }
                            }
                            .padding(vertical = 5.dp)) {
                        Text(
                            stringResource(R.string.paralel), modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                        )
                        Switch(
                            modifier = Modifier.scale(0.8f), checked = paralelState, onCheckedChange = {
                                Settings.vibrate()
                                paralelState = it
                                k.edit {
                                    putBoolean("paralel_maranata", paralelState)
                                }
                            })
                    }
                }
            }
            Text(
                modifier = Modifier.padding(top = 20.dp), text = stringResource(R.string.sviaty_notifi), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var modeNotification by remember { mutableIntStateOf(k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)) }
            Column(Modifier.selectableGroup()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNotification = Settings.NOTIFICATION_SVIATY_ONLY
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationOnly(context)
                                }
                            } else {
                                setNotificationOnly(context)
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_ONLY, onClick = {
                            Settings.vibrate()
                            modeNotification = Settings.NOTIFICATION_SVIATY_ONLY
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationOnly(context)
                                }
                            } else {
                                setNotificationOnly(context)
                            }
                        })
                    Text(
                        stringResource(R.string.apav_only), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNotification = Settings.NOTIFICATION_SVIATY_FULL
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationFull(context)
                                }
                            } else {
                                setNotificationFull(context)
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_FULL, onClick = {
                            Settings.vibrate()
                            modeNotification = Settings.NOTIFICATION_SVIATY_FULL
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationFull(context)
                                }
                            } else {
                                setNotificationFull(context)
                            }
                        })
                    Text(
                        stringResource(R.string.apav_all), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Settings.vibrate()
                            modeNotification = Settings.NOTIFICATION_SVIATY_NONE
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            setNotificationNon(context)
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_NONE, onClick = {
                            Settings.vibrate()
                            modeNotification = Settings.NOTIFICATION_SVIATY_NONE
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            setNotificationNon(context)
                        })
                    Text(
                        stringResource(R.string.apav_no), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                    )
                }
            }
            val dataTimes = ArrayList<DataTime>()
            for (i in 6..17) {
                dataTimes.add(DataTime(stringResource(R.string.pavedamic, i), i))
            }
            val textFieldNotificstionState = rememberTextFieldState(dataTimes[k.getInt("timeNotification", 2)].title)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.pavedamiс_title), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp
                )
                var expandedSviaty by remember { mutableStateOf(false) }
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
                            text = textFieldNotificstionState.text.toString(),
                            fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp,
                            color = PrimaryText,
                        )
                        Icon(
                            modifier = Modifier
                                .padding(start = 21.dp, end = 2.dp)
                                .size(22.dp, 22.dp),
                            painter = painterResource(if (expandedSviaty) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                            tint = PrimaryText,
                            contentDescription = if (expandedSviaty) stringResource(R.string.open_menu) else stringResource(R.string.close_menu)
                        )
                    }
                    ExposedDropdownMenu(
                        containerColor = Divider,
                        expanded = expandedSviaty,
                        onDismissRequest = { expandedSviaty = false },
                    ) {
                        dataTimes.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = { Text(text = option.title, fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp) }, onClick = {
                                    Settings.vibrate()
                                    textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option.title)
                                    expandedSviaty = false
                                    k.edit {
                                        putInt("timeNotification", index)
                                    }
                                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding, colors = MenuDefaults.itemColors(textColor = PrimaryText)
                            )
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val error = stringResource(R.string.error_ch)
                TextButton(
                    onClick = {
                        Settings.vibrate()
                        try {
                            val intent = Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                            intent.putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID_SVIATY)
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            try {
                                val intent = Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                                context.startActivity(intent)
                            } catch (_: ActivityNotFoundException) {
                                val toast = Toast.makeText(context, error, Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }
                    }, colors = ButtonColors(
                        Divider, Color.Unspecified, Color.Unspecified, Color.Unspecified
                    ), shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.settings_notifi_sviata), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = PrimaryText)
                }
            }
            Text(
                modifier = Modifier.padding(top = 20.dp), text = stringResource(R.string.sviaty_under), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var modePkcSvaity by remember { mutableStateOf(k.getBoolean("s_pkc", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        modePkcSvaity = !modePkcSvaity
                        k.edit {
                            putBoolean("s_pkc", modePkcSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.pkc), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = modePkcSvaity, onCheckedChange = {
                        Settings.vibrate()
                        modePkcSvaity = it
                        k.edit {
                            putBoolean("s_pkc", modePkcSvaity)
                        }
                    })
            }
            var modePravasSvaity by remember { mutableStateOf(k.getBoolean("s_pravas", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        modePravasSvaity = !modePravasSvaity
                        k.edit {
                            putBoolean("s_pravas", modePravasSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.sviaty_ulian), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = modePravasSvaity, onCheckedChange = {
                        Settings.vibrate()
                        modePravasSvaity = it
                        k.edit {
                            putBoolean("s_pravas", modePravasSvaity)
                        }
                    })
            }
            var modeGosudSvaity by remember { mutableStateOf(k.getBoolean("s_gosud", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        modeGosudSvaity = !modeGosudSvaity
                        k.edit {
                            putBoolean("s_gosud", modeGosudSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.sviaty_dziar), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = modeGosudSvaity, onCheckedChange = {
                        Settings.vibrate()
                        modeGosudSvaity = it
                        k.edit {
                            putBoolean("s_gosud", modeGosudSvaity)
                        }
                    })
            }
            var modePafesiiSvaity by remember { mutableStateOf(k.getBoolean("s_pafesii", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        Settings.vibrate()
                        modePafesiiSvaity = !modePafesiiSvaity
                        k.edit {
                            putBoolean("s_pafesii", modePafesiiSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)) {
                Text(
                    stringResource(R.string.sviaty_pfes), modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f), checked = modePafesiiSvaity, onCheckedChange = {
                        Settings.vibrate()
                        modePafesiiSvaity = it
                        k.edit {
                            putBoolean("s_pafesii", modePafesiiSvaity)
                        }
                    })
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            val save = stringResource(R.string.save)
            TextButton(
                onClick = {
                    Settings.vibrate()
                    k.edit {
                        for ((key) in k.all) {
                            if (key.contains("WIDGET", true) || key.contains("bible_time", true) || key.contains("akafist", true)) {
                                continue
                            }
                            remove(key)
                        }
                        Toast.makeText(context, save, Toast.LENGTH_SHORT).show()
                    }
                    modeNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                            Settings.NOTIFICATION_SVIATY_NONE
                        } else {
                            Settings.NOTIFICATION_SVIATY_FULL
                        }
                    } else {
                        Settings.NOTIFICATION_SVIATY_FULL
                    }
                    modeNight = Settings.MODE_NIGHT_SYSTEM
                    admin = false
                    Settings.fontInterface = 22f
                    adminDayInYearState = false
                    adminOnlyNotificationsState = false
                    adminNotificationsState = false
                    catolikState = false
                    paralelState = false
                    modePkcSvaity = false
                    modePravasSvaity = false
                    modeGosudSvaity = false
                    modePafesiiSvaity = false
                    textFieldNotificstionState.setTextAndPlaceCursorAtEnd(dataTimes[k.getInt("timeNotification", 2)].title)
                    setNotificationFull(context)
                }, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp), colors = ButtonColors(
                    Primary, Color.Unspecified, Color.Unspecified, Color.Unspecified
                ), shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.settings_default), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = PrimaryTextBlack)
            }
            TextButton(
                onClick = {
                    Settings.vibrate()
                    dialodClearChache = true
                }, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp), colors = ButtonColors(
                    Divider, Color.Unspecified, Color.Unspecified, Color.Unspecified
                ), shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.clear_chash), fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp, color = PrimaryText)
            }
            Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

@Composable
fun DialogClearChash(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.clear_chash).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.clear_chash_opis), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onConfirm()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.delete), contentDescription = null)
                        Text(stringResource(R.string.delite), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogLogin(
    onLogin: (Boolean) -> Unit
) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.admin_panel).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    TextField(modifier = Modifier.fillMaxWidth(), value = login, onValueChange = { newText ->
                        login = newText
                    }, singleLine = true, textStyle = TextStyle(fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp), trailingIcon = {
                        IconButton(onClick = {
                            Settings.vibrate()
                            login = ""
                        }) {
                            Icon(
                                painter = if (login.isNotEmpty()) painterResource(R.drawable.close) else painterResource(R.drawable.empty), contentDescription = null, tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    })
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        value = password,
                        onValueChange = { newText ->
                            password = newText
                        },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = (Settings.fontInterface - 2).sp, lineHeight = ((Settings.fontInterface - 2) * 1.15f).sp),
                        trailingIcon = {
                            if (showPassword) {
                                IconButton(onClick = {
                                    Settings.vibrate()
                                    showPassword = false
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.visibility), contentDescription = "hide_password"
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        Settings.vibrate()
                                        showPassword = true
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.visibility_off), contentDescription = "hide_password"
                                    )
                                }
                            }
                        },
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onLogin(login.trim() == "Царква" && password.trim() == "Дворнікава63")
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogAdminExit(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.admin_exit), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_exit_opis), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onConfirm()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogNotification(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.notifi).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.help_notifications_api33), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onConfirm()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                        Text(stringResource(R.string.dazvolic), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun setNotificationOnly(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        setNotifications(context, Settings.NOTIFICATION_SVIATY_ONLY)
    }
}

fun setNotificationFull(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        setNotifications(context, Settings.NOTIFICATION_SVIATY_FULL)
    }
}

fun setNotificationNon(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        setNotifications(context, Settings.NOTIFICATION_SVIATY_NONE)
    }
}

class DataTime(val title: String, val data: Int)