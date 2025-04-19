package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.Settings.NOTIFICATION_CHANNEL_ID_SVIATY
import by.carkva_gazeta.malitounik.Settings.setNotifications
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(navController: NavHostController) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    var modeNight by remember { mutableIntStateOf(k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM)) }
    var dialodLogin by rememberSaveable { mutableStateOf(false) }
    var dialodNotificatin by rememberSaveable { mutableStateOf(false) }
    var admin by remember { mutableStateOf(k.getBoolean("admin", false)) }
    Settings.fontInterface = remember { getFontInterface(context) }
    if (dialodLogin) {
        DialogLogin { isLogin ->
            if (isLogin) {
                val module = SettingsModules(context as MainActivity)
                k.edit {
                    putBoolean("admin", true)
                }
                if (!module.checkmodulesAdmin()) {
                    module.downloadDynamicModule("admin")
                }
                admin = true
            }
            dialodLogin = false
        }
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (dialodNotificatin) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                when (k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)) {
                    Settings.NOTIFICATION_SVIATY_ONLY -> setNotificationOnly(context)
                    Settings.NOTIFICATION_SVIATY_FULL -> setNotificationFull(context)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        val intent = Intent()
                        intent.action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        intent.data = ("package:" + context.packageName).toUri()
                        context.startActivity(intent)
                    }
                }
            } else {
                k.edit {
                    putInt("notification", Settings.NOTIFICATION_SVIATY_NONE)
                }
                setNotificationNon(context)
            }
            dialodNotificatin = false
        }
        DialogNotification(onConfirm = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    dialodNotificatin = false
                    val intent = Intent()
                    intent.action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    intent.data = ("package:" + context.packageName).toUri()
                    context.startActivity(intent)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionCheck2 = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                if (permissionCheck2 == PackageManager.PERMISSION_DENIED) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            dialodNotificatin = false
        }, onDismiss = {
            dialodNotificatin = false
        })
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
        }) {
            dialodClearChache = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.clickable {
                            maxLine.intValue = Int.MAX_VALUE
                            coroutineScope.launch {
                                delay(5000L)
                                maxLine.intValue = 1
                            }
                        },
                        text = stringResource(R.string.tools_item).uppercase(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = Settings.fontInterface.sp,
                        maxLines = maxLine.intValue,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        val interactionSourse = remember { MutableInteractionSource() }
        var adminItemCount by remember { mutableIntStateOf(0) }
        var adminClickTime by remember { mutableLongStateOf(0) }
        Column(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(
                        interactionSource = interactionSourse,
                        indication = null
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
                    },
                text = stringResource(R.string.vygliad),
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Column(Modifier.selectableGroup())
            {
                val actyvity = LocalActivity.current as MainActivity
                val isSystemInDarkTheme = isSystemInDarkTheme()
                val edit = k.edit()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNight = Settings.MODE_NIGHT_SYSTEM
                            edit.putInt(
                                "mode_night",
                                Settings.MODE_NIGHT_SYSTEM
                            )
                            edit.apply()
                            actyvity.dzenNoch = isSystemInDarkTheme
                            (context as MainActivity).removelightSensor()
                            if (context.checkDzenNoch != context.dzenNoch) actyvity.recreate()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_SYSTEM,
                        onClick = {
                            modeNight = Settings.MODE_NIGHT_SYSTEM
                            edit.putInt(
                                "mode_night",
                                Settings.MODE_NIGHT_SYSTEM
                            )
                            edit.apply()
                            actyvity.dzenNoch = isSystemInDarkTheme
                            (context as MainActivity).removelightSensor()
                            if (context.checkDzenNoch != context.dzenNoch) actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.system),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNight = Settings.MODE_NIGHT_NO
                            edit.putInt("mode_night", Settings.MODE_NIGHT_NO)
                            edit.apply()
                            actyvity.dzenNoch = false
                            (context as MainActivity).removelightSensor()
                            if (context.checkDzenNoch != context.dzenNoch) actyvity.recreate()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_NO,
                        onClick = {
                            modeNight = Settings.MODE_NIGHT_NO
                            edit.putInt("mode_night", Settings.MODE_NIGHT_NO)
                            edit.apply()
                            actyvity.dzenNoch = false
                            (context as MainActivity).removelightSensor()
                            if (context.checkDzenNoch != context.dzenNoch) actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.day),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNight = Settings.MODE_NIGHT_YES
                            edit.putInt(
                                "mode_night",
                                Settings.MODE_NIGHT_YES
                            )
                            edit.apply()
                            actyvity.dzenNoch = true
                            (context as MainActivity).removelightSensor()
                            if (context.checkDzenNoch != context.dzenNoch) actyvity.recreate()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_YES,
                        onClick = {
                            modeNight = Settings.MODE_NIGHT_YES
                            edit.putInt(
                                "mode_night",
                                Settings.MODE_NIGHT_YES
                            )
                            edit.apply()
                            actyvity.dzenNoch = true
                            (context as MainActivity).removelightSensor()
                            if (context.checkDzenNoch != context.dzenNoch) actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.widget_day_d_n),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNight = Settings.MODE_NIGHT_AUTO
                            edit.putInt(
                                "mode_night",
                                Settings.MODE_NIGHT_AUTO
                            )
                            edit.apply()
                            (context as MainActivity).setlightSensor()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_AUTO,
                        onClick = {
                            modeNight = Settings.MODE_NIGHT_AUTO
                            edit.putInt(
                                "mode_night",
                                Settings.MODE_NIGHT_AUTO
                            )
                            edit.apply()
                            (context as MainActivity).setlightSensor()
                        }
                    )
                    Text(
                        stringResource(R.string.auto_widget_day_d_n),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
            }
            var fontSizeInterface by remember { mutableFloatStateOf(k.getFloat("fontSizeInterface", 20f)) }
            Text(
                stringResource(R.string.settengs_font_size_app),
                fontStyle = FontStyle.Italic,
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Slider(
                valueRange = 18f..26f, steps = 4, value = fontSizeInterface, onValueChange = {
                    k.edit {
                        putFloat("fontSizeInterface", it)
                    }
                    fontSizeInterface = it
                    Settings.fontInterface = it
                })
            var adminDayInYearState by remember { mutableStateOf(k.getBoolean("adminDayInYear", false)) }
            if (admin) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        adminDayInYearState = !adminDayInYearState
                        k.edit {
                            putBoolean("adminDayInYear", adminDayInYearState)
                        }
                    }) {
                    Text(
                        stringResource(R.string.admin_day_in_year),
                        modifier = Modifier
                            .weight(1f).padding(end = 10.dp),
                        fontSize = (Settings.fontInterface - 2).sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        modifier = Modifier.scale(0.8f),
                        checked = adminDayInYearState,
                        onCheckedChange = {
                            adminDayInYearState = it
                            k.edit {
                                putBoolean("adminDayInYear", adminDayInYearState)
                            }
                        }
                    )
                }
                TextButton(
                    onClick = {
                        val module = SettingsModules(context as MainActivity)
                        if (module.checkmodulesAdmin()) {
                            SplitInstallHelper.updateAppInfo(context)
                            val intent = Intent()
                            intent.setClassName(context, "by.carkva_gazeta.admin.AdminMain")
                            context.startActivity(intent)
                        } else {
                            module.setDownloadDynamicModuleListener(object : SettingsModules.DownloadDynamicModuleListener {
                                override fun dynamicModuleDownloading(totalBytesToDownload: Double, bytesDownloaded: Double) {
                                }

                                override fun dynamicModuleInstalled() {
                                    SplitInstallHelper.updateAppInfo(context)
                                    val intent = Intent()
                                    intent.setClassName(context, "by.carkva_gazeta.admin.AdminMain")
                                    context.startActivity(intent)
                                }
                            })
                            module.downloadDynamicModule("admin")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp),
                    colors = ButtonColors(
                        Divider,
                        Color.Unspecified,
                        Color.Unspecified,
                        Color.Unspecified
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.site_admin), fontSize = (Settings.fontInterface - 2).sp, color = PrimaryText)
                }
            }
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.biblia),
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var sinoidalState by remember { mutableStateOf(k.getBoolean("sinoidal_bible", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        sinoidalState = !sinoidalState
                        k.edit {
                            putBoolean("sinoidal_bible", sinoidalState)
                        }
                    }
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    stringResource(R.string.bsinaidal),
                    modifier = Modifier
                        .weight(1f).padding(end = 10.dp),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = sinoidalState,
                    onCheckedChange = {
                        sinoidalState = it
                        k.edit {
                            putBoolean("sinoidal_bible", sinoidalState)
                        }
                    }
                )
            }
            var maranafaState by remember { mutableStateOf(k.getBoolean("maranafa", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        maranafaState = !maranafaState
                        k.edit {
                            putBoolean("maranafa", maranafaState)
                        }
                    }
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    stringResource(R.string.maranata_opis),
                    modifier = Modifier
                        .weight(1f).padding(end = 10.dp),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = maranafaState,
                    onCheckedChange = {
                        maranafaState = it
                        k.edit {
                            putBoolean("maranafa", maranafaState)
                        }
                    }
                )
            }
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.sviaty_notifi),
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var modeNotification by remember { mutableIntStateOf(k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)) }
            Column(Modifier.selectableGroup())
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNotification = Settings.NOTIFICATION_SVIATY_ONLY
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck || !alarmManager.canScheduleExactAlarms()) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationOnly(context)
                                }
                            } else {
                                setNotificationOnly(context)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_ONLY,
                        onClick = {
                            modeNotification = Settings.NOTIFICATION_SVIATY_ONLY
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck || !alarmManager.canScheduleExactAlarms()) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationOnly(context)
                                }
                            } else {
                                setNotificationOnly(context)
                            }
                        }
                    )
                    Text(
                        stringResource(R.string.apav_only),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNotification = Settings.NOTIFICATION_SVIATY_FULL
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck || !alarmManager.canScheduleExactAlarms()) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationFull(context)
                                }
                            } else {
                                setNotificationFull(context)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_FULL,
                        onClick = {
                            modeNotification = Settings.NOTIFICATION_SVIATY_FULL
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                if (PackageManager.PERMISSION_DENIED == permissionCheck || !alarmManager.canScheduleExactAlarms()) {
                                    dialodNotificatin = true
                                } else {
                                    setNotificationFull(context)
                                }
                            } else {
                                setNotificationFull(context)
                            }
                        }
                    )
                    Text(
                        stringResource(R.string.apav_all),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNotification = Settings.NOTIFICATION_SVIATY_NONE
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            setNotificationNon(context)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_NONE,
                        onClick = {
                            modeNotification = Settings.NOTIFICATION_SVIATY_NONE
                            k.edit {
                                putInt("notification", modeNotification)
                            }
                            setNotificationNon(context)
                        }
                    )
                    Text(
                        stringResource(R.string.apav_no),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = (Settings.fontInterface - 2).sp
                    )
                }
            }
            Text(
                stringResource(R.string.pavedamiс_title),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = (Settings.fontInterface - 2).sp
            )
            val dataTimes = ArrayList<DataTime>()
            for (i in 6..17) {
                dataTimes.add(DataTime(stringResource(R.string.pavedamic, i), i))
            }
            val textFieldNotificstionState = rememberTextFieldState(dataTimes[k.getInt("timeNotification", 2)].string)
            var expandedSviaty by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier.padding(vertical = 10.dp),
                expanded = expandedSviaty,
                onExpandedChange = { expandedSviaty = it },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    state = textFieldNotificstionState,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    textStyle = TextStyle(fontSize = (Settings.fontInterface - 2).sp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSviaty) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expandedSviaty,
                    onDismissRequest = { expandedSviaty = false },
                ) {
                    dataTimes.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(text = option.string, fontSize = (Settings.fontInterface - 2).sp) },
                            onClick = {
                                textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option.string)
                                expandedSviaty = false
                                k.edit {
                                    putInt("timeNotification", index)
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TextButton(
                    onClick = {
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
                                val toast = Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }
                    },
                    colors = ButtonColors(
                        Divider,
                        Color.Unspecified,
                        Color.Unspecified,
                        Color.Unspecified
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.settings_notifi_sviata), fontSize = (Settings.fontInterface - 2).sp, color = PrimaryText)
                }
            }
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.sviaty_under),
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var modePkcSvaity by remember { mutableStateOf(k.getBoolean("s_pkc", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        modePkcSvaity = !modePkcSvaity
                        k.edit {
                            putBoolean("s_pkc", modePkcSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    stringResource(R.string.pkc),
                    modifier = Modifier
                        .weight(1f).padding(end = 10.dp),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = modePkcSvaity,
                    onCheckedChange = {
                        modePkcSvaity = it
                        k.edit {
                            putBoolean("s_pkc", modePkcSvaity)
                        }
                    }
                )
            }
            var modePravasSvaity by remember { mutableStateOf(k.getBoolean("s_pravas", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        modePravasSvaity = !modePravasSvaity
                        k.edit {
                            putBoolean("s_pravas", modePravasSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    stringResource(R.string.sviaty_ulian),
                    modifier = Modifier
                        .weight(1f).padding(end = 10.dp),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = modePravasSvaity,
                    onCheckedChange = {
                        modePravasSvaity = it
                        k.edit {
                            putBoolean("s_pravas", modePravasSvaity)
                        }
                    }
                )
            }
            var modeGosudSvaity by remember { mutableStateOf(k.getBoolean("s_gosud", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        modeGosudSvaity = !modeGosudSvaity
                        k.edit {
                            putBoolean("s_gosud", modeGosudSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    stringResource(R.string.sviaty_dziar),
                    modifier = Modifier
                        .weight(1f).padding(end = 10.dp),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = modeGosudSvaity,
                    onCheckedChange = {
                        modeGosudSvaity = it
                        k.edit {
                            putBoolean("s_gosud", modeGosudSvaity)
                        }
                    }
                )
            }
            var modePafesiiSvaity by remember { mutableStateOf(k.getBoolean("s_pafesii", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        modePafesiiSvaity = !modePafesiiSvaity
                        k.edit {
                            putBoolean("s_pafesii", modePafesiiSvaity)
                        }
                    }
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    stringResource(R.string.sviaty_pfes),
                    modifier = Modifier
                        .weight(1f).padding(end = 10.dp),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = modePafesiiSvaity,
                    onCheckedChange = {
                        modePafesiiSvaity = it
                        k.edit {
                            putBoolean("s_pafesii", modePafesiiSvaity)
                        }
                    }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            TextButton(
                onClick = {
                    k.edit {
                        val noDelite = ArrayList<String>()
                        noDelite.add("WIDGET")
                        noDelite.add("bible_time")
                        noDelite.add("admin")
                        for ((key) in k.all) {
                            var del = true
                            for (i in 0 until noDelite.size) {
                                if (key.contains(noDelite[i], true)) {
                                    del = false
                                    break
                                }
                            }
                            if (del) remove(key)
                        }
                        Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                        putFloat("font_biblia", 22f)
                        putInt("Settings.fontInterface", 1)
                        putInt("mode_night", Settings.MODE_NIGHT_SYSTEM)
                        putBoolean("s_pravas", false)
                        putBoolean("s_pkc", false)
                        putBoolean("s_gosud", false)
                        putBoolean("s_pafesii", false)
                        putInt("notification", Settings.NOTIFICATION_SVIATY_FULL)
                        putInt("sinoidal", 0)
                        putInt("maranata", 0)
                        putString("perevod", Settings.PEREVODSEMUXI)
                        putString("perevodMaranata", Settings.PEREVODSEMUXI)
                        putBoolean("pegistrbukv", true)
                        putInt("slovocalkam", 0)
                        putBoolean("admin", false)
                        putBoolean("adminDayInYear", false)
                        putBoolean("paralel_biblia", true)
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
                    sinoidalState = false
                    maranafaState = false
                    modePkcSvaity = false
                    modePravasSvaity = false
                    modeGosudSvaity = false
                    modePafesiiSvaity = false
                    textFieldNotificstionState.setTextAndPlaceCursorAtEnd(dataTimes[k.getInt("timeNotification", 2)].string)
                    if ((context as MainActivity).checkDzenNoch != context.dzenNoch) context.recreate()
                    else setNotificationFull(context)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp),
                colors = ButtonColors(
                    Primary,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.settings_default), fontSize = (Settings.fontInterface - 2).sp, color = PrimaryTextBlack)
            }
            TextButton(
                onClick = {
                    dialodClearChache = true
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp),
                colors = ButtonColors(
                    Divider,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.clear_chash), fontSize = (Settings.fontInterface - 2).sp, color = PrimaryText)
            }
            Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

@Composable
fun DialogClearChash(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.warning), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.clear_chash).uppercase())
        },
        text = {
            Text(text = stringResource(R.string.clear_chash_opis), fontSize = (Settings.fontInterface - 2).sp)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(stringResource(R.string.delite), fontSize = (Settings.fontInterface - 2).sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.cansel), fontSize = (Settings.fontInterface - 2).sp)
            }
        }
    )
}

@Composable
fun DialogLogin(
    onLogin: (Boolean) -> Unit
) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.local_police), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.admin_panel).uppercase())
        },
        text = {
            Column {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = login,
                    onValueChange = { newText ->
                        login = newText
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = (Settings.fontInterface - 2).sp),
                    trailingIcon = {
                        if (login.isNotEmpty()) {
                            IconButton(onClick = {
                                login = ""
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    value = password,
                    onValueChange = { newText ->
                        password = newText
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = (Settings.fontInterface - 2).sp),
                    trailingIcon = {
                        if (showPassword) {
                            IconButton(onClick = { showPassword = false }) {
                                Icon(
                                    painter = painterResource(R.drawable.visibility),
                                    contentDescription = "hide_password"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.visibility_off),
                                    contentDescription = "hide_password"
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
        },
        onDismissRequest = {
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onLogin(login.trim() == "Царква" && password.trim() == "Дворнікава63")
                }
            ) {
                Text(stringResource(R.string.ok), fontSize = (Settings.fontInterface - 2).sp)
            }
        }
    )
}

@Composable
fun DialogNotification(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.notifications), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.notifi).uppercase())
        },
        text = {
            Text(stringResource(R.string.help_notifications_api33), fontSize = (Settings.fontInterface - 2).sp)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(stringResource(R.string.dazvolic), fontSize = (Settings.fontInterface - 2).sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.cansel), fontSize = (Settings.fontInterface - 2).sp)
            }
        }
    )
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

class SettingsModules(val context: MainActivity) {
    private var downloadDynamicModuleListener: DownloadDynamicModuleListener? = null

    interface DownloadDynamicModuleListener {
        fun dynamicModuleDownloading(totalBytesToDownload: Double, bytesDownloaded: Double)
        fun dynamicModuleInstalled()
    }

    fun setDownloadDynamicModuleListener(listener: DownloadDynamicModuleListener) {
        downloadDynamicModuleListener = listener
    }

    fun checkmodulesAdmin(): Boolean {
        val muduls = SplitInstallManagerFactory.create(context).installedModules
        for (mod in muduls) {
            if (mod == "admin") {
                return true
            }
        }
        return false
    }

    fun downloadDynamicModule(moduleName: String) {
        val splitInstallManager = SplitInstallManagerFactory.create(context)

        val request = SplitInstallRequest.newBuilder().addModule(moduleName).build()

        val listener = SplitInstallStateUpdatedListener { state ->
            if (state.status() == SplitInstallSessionStatus.FAILED) {
                downloadDynamicModule(moduleName)
                return@SplitInstallStateUpdatedListener
            }
            if (state.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                splitInstallManager.startConfirmationDialogForResult(state, context, 150)
            }
            if (state.sessionId() == sessionId) {
                when (state.status()) {
                    SplitInstallSessionStatus.PENDING -> {
                    }

                    SplitInstallSessionStatus.DOWNLOADED -> {
                    }

                    SplitInstallSessionStatus.DOWNLOADING -> {
                        downloadDynamicModuleListener?.dynamicModuleDownloading(state.totalBytesToDownload().toDouble(), state.bytesDownloaded().toDouble())
                    }

                    SplitInstallSessionStatus.INSTALLED -> {
                        downloadDynamicModuleListener?.dynamicModuleInstalled()
                    }

                    SplitInstallSessionStatus.CANCELED -> {
                    }

                    SplitInstallSessionStatus.CANCELING -> {
                    }

                    SplitInstallSessionStatus.FAILED -> {
                    }

                    SplitInstallSessionStatus.INSTALLING -> {
                    }

                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    }

                    SplitInstallSessionStatus.UNKNOWN -> {
                    }
                }
            }
        }

        splitInstallManager.registerListener(listener)

        splitInstallManager.startInstall(request).addOnFailureListener {
            if ((it as SplitInstallException).errorCode == SplitInstallErrorCode.NETWORK_ERROR) {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }.addOnSuccessListener {
            sessionId = it
        }
    }

    companion object {
        private var sessionId = 0
    }
}

class DataTime(val string: String, val data: Int)