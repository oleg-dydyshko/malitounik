package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(navController: NavHostController) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
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
                        text = stringResource(R.string.tools_item),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = maxLine.intValue,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() },
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
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.vygliad),
                fontSize = 18.sp,
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
                            if (actyvity.dzenNoch != actyvity.checkDzenNoch)
                                actyvity.recreate()
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
                            if (actyvity.dzenNoch != actyvity.checkDzenNoch)
                                actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.system),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
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
                            if (actyvity.checkDzenNoch)
                                actyvity.recreate()
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
                            if (actyvity.checkDzenNoch)
                                actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.day),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
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
                            if (!actyvity.checkDzenNoch)
                                actyvity.recreate()
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
                            if (!actyvity.checkDzenNoch)
                                actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.widget_day_d_n),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
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
                            actyvity.recreate()
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
                            actyvity.recreate()
                        }
                    )
                    Text(
                        stringResource(R.string.auto_widget_day_d_n),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            val options = stringArrayResource(R.array.fonts)
            var expanded by remember { mutableStateOf(false) }
            val textFieldState = rememberTextFieldState(options[k.getInt("fontInterface", 1)])
            Text(
                stringResource(R.string.settengs_font_size_app),
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            ExposedDropdownMenuBox(
                modifier = Modifier.padding(vertical = 10.dp),
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    state = textFieldState,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                            onClick = {
                                textFieldState.setTextAndPlaceCursorAtEnd(option)
                                expanded = false
                                val prefEditors = k.edit()
                                prefEditors.putInt("fontInterface", index)
                                prefEditors.apply()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            var adminDayInYearState by remember { mutableStateOf(k.getBoolean("adminDayInYear", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    adminDayInYearState = !adminDayInYearState
                    val edit = k.edit()
                    edit.putBoolean("adminDayInYear", adminDayInYearState)
                    edit.apply()
                }) {
                Icon(
                    modifier = Modifier.size(12.dp, 12.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    stringResource(R.string.admin_day_in_year),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    checked = adminDayInYearState,
                    onCheckedChange = {
                        adminDayInYearState = it
                        val edit = k.edit()
                        edit.putBoolean("adminDayInYear", adminDayInYearState)
                        edit.apply()
                    }
                )
            }
            TextButton(
                onClick = {
                    /*if (checkmodulesAdmin()) {
                        dynamicModuleInstalled()
                    } else {
                        val dialog = DialogUpdateMalitounik.getInstance(getString(R.string.title_download_module2))
                        dialog.isCancelable = false
                        dialog.show(supportFragmentManager, "DialogUpdateMalitounik")
                        setDownloadDynamicModuleListener(this)
                        downloadDynamicModule("admin")
                    }*/
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
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.site_admin), fontSize = 18.sp, color = PrimaryText)
            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.biblia),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            var sinoidalState by remember { mutableStateOf(k.getBoolean("sinoidal_bible", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    sinoidalState = !sinoidalState
                    val edit = k.edit()
                    edit.putBoolean("sinoidal_bible", sinoidalState)
                    edit.apply()
                }) {
                Icon(
                    modifier = Modifier.size(12.dp, 12.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    stringResource(R.string.admin_day_in_year),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    checked = sinoidalState,
                    onCheckedChange = {
                        sinoidalState = it
                        val edit = k.edit()
                        edit.putBoolean("sinoidal_bible", sinoidalState)
                        edit.apply()
                    }
                )
            }
            var maranafaState by remember { mutableStateOf(k.getBoolean("maranafa", false)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    maranafaState = !maranafaState
                    val edit = k.edit()
                    edit.putBoolean("maranafa", maranafaState)
                    edit.apply()
                }) {
                Icon(
                    modifier = Modifier.size(12.dp, 12.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    stringResource(R.string.admin_day_in_year),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    checked = maranafaState,
                    onCheckedChange = {
                        maranafaState = it
                        val edit = k.edit()
                        edit.putBoolean("maranafa", maranafaState)
                        edit.apply()
                    }
                )
            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.sviaty_notifi),
                fontSize = 18.sp,
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
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_ONLY,
                        onClick = {
                            modeNotification = Settings.NOTIFICATION_SVIATY_ONLY
                        }
                    )
                    Text(
                        stringResource(R.string.apav_only),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNotification = Settings.NOTIFICATION_SVIATY_FULL
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_FULL,
                        onClick = {
                            modeNotification = Settings.NOTIFICATION_SVIATY_FULL
                        }
                    )
                    Text(
                        stringResource(R.string.apav_all),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            modeNotification = Settings.NOTIFICATION_SVIATY_NONE
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNotification == Settings.NOTIFICATION_SVIATY_NONE,
                        onClick = {
                            modeNotification = Settings.NOTIFICATION_SVIATY_NONE
                        }
                    )
                    Text(
                        stringResource(R.string.apav_no),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            val dataTimes = ArrayList<DataTime>()
            for (i in 6..17) {
                dataTimes.add(DataTime(stringResource(R.string.pavedamic, i), i))
            }
            val textFieldNotificstionState = rememberTextFieldState(dataTimes[k.getInt("timeNotification", 8) - 6].string)
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSviaty) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expandedSviaty,
                    onDismissRequest = { expandedSviaty = false },
                ) {
                    dataTimes.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option.string, style = MaterialTheme.typography.bodyLarge) },
                            onClick = {
                                textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option.string)
                                expandedSviaty = false
                                val prefEditors = k.edit()
                                prefEditors.putInt("fontInterface", index)
                                prefEditors.apply()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

class DataTime(val string: String, val data: Int)