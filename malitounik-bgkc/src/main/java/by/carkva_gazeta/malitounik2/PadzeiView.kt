package by.carkva_gazeta.malitounik2

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.ui.theme.Divider
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PadzeiaView(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    var addPadzeia by remember { mutableStateOf(false) }
    var deliteAll by remember { mutableStateOf(false) }
    val listPadzeia = remember { mutableStateListOf<Padzeia>() }
    LaunchedEffect(Unit) {
        listPadzeia.addAll(setListPadzeia(context))
    }
    val lazyListState = rememberLazyListState()
    val view = LocalView.current
    val day = Calendar.getInstance()
    val colors = stringArrayResource(R.array.colors)
    LaunchedEffect(Unit) {
        val c2 = Calendar.getInstance()
        var nol1 = ""
        var nol2 = ""
        if (c2[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
        if (c2[Calendar.MONTH] < 9) nol2 = "0"
        val daInit = nol1 + c2[Calendar.DAY_OF_MONTH] + "." + nol2 + (c2[Calendar.MONTH] + 1) + "." + c2[Calendar.YEAR]
        var initPosition = -1
        for (i in 0 until listPadzeia.size) {
            if (daInit == listPadzeia[i].dat) {
                initPosition = i
                break
            }
        }
        if (initPosition == -1) initPosition = 0
        coroutineScope.launch {
            lazyListState.scrollToItem(initPosition)
        }
    }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    BackHandler(showDropdown || addPadzeia) {
        if (addPadzeia) addPadzeia = false
        else showDropdown = !showDropdown
    }
    var kalendarMun by remember { mutableStateOf(false) }
    var kalendarMun2 by remember { mutableStateOf(false) }
    var kalendarMun3 by remember { mutableStateOf(false) }
    var initData = ""
    var initTime = ""
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.HOUR, 1)
    for (i in Settings.data.indices) {
        if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
            var nol1 = ""
            var nol2 = ""
            if (calendar[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
            if (calendar[Calendar.MONTH] < 9) nol2 = "0"
            initData = nol1 + calendar[Calendar.DAY_OF_MONTH] + "." + nol2 + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR]
            initTime = "${calendar[Calendar.HOUR_OF_DAY]}:00"
            break
        }
    }
    var data by remember { mutableStateOf(initData) }
    var data2 by remember { mutableStateOf(initData) }
    var data3 by remember { mutableStateOf(initData) }
    var dialogTimePickerDialog by remember { mutableStateOf(false) }
    var dialogTimePickerDialog2 by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf(initTime) }
    var time2 by remember { mutableStateOf(initTime) }
    var savePadzia by remember { mutableStateOf(false) }
    if (dialogTimePickerDialog || dialogTimePickerDialog2) {
        MyTimePickerDialog(onConfirm = { state ->
            val nyl = if (state.minute < 10) "0" else ""
            if (dialogTimePickerDialog) {
                time = "${state.hour}:$nyl${state.minute}"
                time2 = "${state.hour}:${state.minute}"
            } else time2 = "${state.hour}:${state.minute}"
            dialogTimePickerDialog = false
            dialogTimePickerDialog2 = false
        }) {
            dialogTimePickerDialog = false
            dialogTimePickerDialog2 = false
        }
    }
    var showPadzia by remember { mutableStateOf(false) }
    var showPadziaPosition by remember { mutableIntStateOf(0) }
    if (showPadzia) {
        val p = listPadzeia[showPadziaPosition]
        val title = p.padz
        val data1 = p.dat
        val time1 = p.tim
        val dataK = p.datK
        val timeK = p.timK
        val paz = p.paznic
        val konecSabytie = p.konecSabytie
        //val color = p.color
        var res = stringResource(R.string.sabytie_no_pavedam)
        val gc = Calendar.getInstance()
        val realTime = gc.timeInMillis
        var paznicia = false
        if (paz != 0L) {
            gc.timeInMillis = paz
            var nol11 = ""
            var nol21 = ""
            var nol3 = ""
            if (gc[Calendar.DAY_OF_MONTH] < 10) nol11 = "0"
            if (gc[Calendar.MONTH] < 9) nol21 = "0"
            if (gc[Calendar.MINUTE] < 10) nol3 = "0"
            res = "Паведаміць: " + nol11 + gc[Calendar.DAY_OF_MONTH] + "." + nol21 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR] + " у " + gc[Calendar.HOUR_OF_DAY] + ":" + nol3 + gc[Calendar.MINUTE]
            if (realTime > paz) paznicia = true
        }
        val textR = if (konecSabytie) {
            stringResource(R.string.sabytie_kali, data1, time1, res)
        } else {
            stringResource(R.string.sabytie_pachatak_show, data1, time1, dataK, timeK, res)
        }
        DialogSabytieShow(
            title,
            showPadziaPosition,
            textR,
            paznicia,
            onEdit = {
            },
            onDismiss = {
                showPadzia = false
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            modifier = Modifier.clickable {
                                maxLine.intValue = Int.MAX_VALUE
                                coroutineScope.launch {
                                    delay(5000L)
                                    maxLine.intValue = 1
                                }
                            },
                            text = stringResource(R.string.sabytie),
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                },
                navigationIcon = {
                    if (addPadzeia) {
                        IconButton(onClick = {
                            addPadzeia = false
                        },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    contentDescription = ""
                                )
                            })
                    } else {
                        IconButton(onClick = {
                            navController.popBackStack()
                        },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back),
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    contentDescription = ""
                                )
                            })
                    }
                },
                actions = {
                    if (addPadzeia) {
                        IconButton({
                            savePadzia = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.save),
                                tint = PrimaryTextBlack,
                                contentDescription = ""
                            )
                        }
                    } else {
                        IconButton({
                            addPadzeia = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                tint = PrimaryTextBlack,
                                contentDescription = ""
                            )
                        }
                        IconButton({
                            deliteAll = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                tint = PrimaryTextBlack,
                                contentDescription = ""
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                0.dp
            )
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(listPadzeia.size) { index ->
                    val padzeia = listPadzeia[index]
                    val dataList = padzeia.dat.split(".")
                    val gc = GregorianCalendar(dataList[2].toInt(), dataList[1].toInt() - 1, dataList[0].toInt())
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .combinedClickable(
                                onClick = {
                                    showPadziaPosition = index
                                    showPadzia = true
                                },
                                onLongClick = {

                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp, 12.dp)
                                .background(Color(colors[padzeia.color].toColorInt()))
                        )
                        Text(
                            text = stringResource(R.string.sabytie_data_name, padzeia.dat, padzeia.padz),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = if (gc[Calendar.DAY_OF_YEAR] == day[Calendar.DAY_OF_YEAR] && gc[Calendar.YEAR] == day[Calendar.YEAR]) FontWeight.Bold
                            else FontWeight.Normal,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                }
            }
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = { showDropdown = false }
            ) {
                AnimatedVisibility(
                    showDropdown,
                    enter = slideInVertically(
                        tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                ) {
                    if (kalendarMun || kalendarMun2 || kalendarMun3) {
                        KaliandarScreenMounth(colorBlackboard = MaterialTheme.colorScheme.onTertiary, setPageCaliandar = { date ->
                            var nol1 = ""
                            var nol2 = ""
                            val kal = Settings.data[date]
                            if (kal[1].toInt() < 10) nol1 = "0"
                            if (kal[2].toInt() < 9) nol2 = "0"
                            if (kalendarMun) {
                                data = nol1 + kal[1] + "." + nol2 + (kal[2].toInt() + 1) + "." + kal[3]
                                data2 = nol1 + kal[1] + "." + nol2 + (kal[2].toInt() + 1) + "." + kal[3]
                            } else if (kalendarMun2) {
                                data2 = nol1 + kal[1] + "." + nol2 + (kal[2].toInt() + 1) + "." + kal[3]
                            } else {
                                data3 = nol1 + kal[1] + "." + nol2 + (kal[2].toInt() + 1) + "." + kal[3]
                            }
                            kalendarMun = false
                            kalendarMun2 = false
                            kalendarMun3 = false
                            showDropdown = false
                        })
                    }
                }
            }
            if (addPadzeia) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    AddPadzeia(listPadzeia, savePadzia, data, data2, data3, time, time2, setShowTimePicker = {
                        if (it == 1) dialogTimePickerDialog = true
                        else dialogTimePickerDialog2 = true
                    }, setShowKalendar = {
                        showDropdown = true
                        when (it) {
                            1 -> kalendarMun = true
                            2 -> kalendarMun2 = true
                            else -> kalendarMun3 = true
                        }
                    }, isSave = {
                        addPadzeia = false
                        savePadzia = false
                    })
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPadzeia(
    listPadzeia: SnapshotStateList<Padzeia>,
    save: Boolean,
    data: String,
    data2: String,
    data3: String,
    time: String,
    time2: String,
    setShowTimePicker: (Int) -> Unit,
    setShowKalendar: (Int) -> Unit,
    isSave: () -> Unit
) {
    val context = LocalContext.current
    val padzeiaText = stringResource(R.string.sabytie_name)
    var padzeia by remember { mutableStateOf("") }
    var setTimeZa by remember { mutableStateOf("") }
    var setPautorRaz by remember { mutableStateOf("5") }
    var modeRepit by remember { mutableIntStateOf(1) }
    var textFieldStatePosition by remember { mutableIntStateOf(0) }
    var textFieldState2Position by remember { mutableIntStateOf(0) }
    val optionsColors = stringArrayResource(R.array.colors)
    var color by remember { mutableStateOf(optionsColors[0]) }
    var colorPosition by remember { mutableIntStateOf(0) }
    var dialodNotificatin by rememberSaveable { mutableStateOf(false) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        var permissionCheck2 = PackageManager.PERMISSION_GRANTED
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val permissionCheck = alarmManager.canScheduleExactAlarms()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionCheck2 = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        }
        if (setTimeZa.isNotEmpty() && (!permissionCheck || permissionCheck2 == PackageManager.PERMISSION_DENIED)) {
            dialodNotificatin = true
        }
    }
    if (dialodNotificatin) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (it) {
                when (k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)) {
                    1 -> setNotificationOnly(context)
                    2 -> setNotificationFull(context)
                }
            } else {
                val prefEditor = k.edit()
                prefEditor.putInt("notification", Settings.NOTIFICATION_SVIATY_NONE)
                prefEditor.apply()
                setNotificationNon(context)
                setTimeZa = ""
            }
            dialodNotificatin = false
        }
        DialogNotification(onConfirm = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionCheck2 = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                if (permissionCheck2 == PackageManager.PERMISSION_DENIED) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent()
                    intent.action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    intent.setData(Uri.parse("package:" + context.packageName))
                    context.startActivity(intent)
                }
            }
            dialodNotificatin = false
        }, onDismiss = {
            dialodNotificatin = false
            setTimeZa = ""
        })
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = padzeia,
            placeholder = { Text(stringResource(R.string.sabytie_name), fontSize = Settings.fontInterface.sp) },
            onValueChange = { newText ->
                padzeia = newText
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
            trailingIcon = {
                if (padzeia.isNotEmpty()) {
                    IconButton(onClick = {
                        padzeia = ""
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
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.sabytie_pachatak), fontSize = Settings.fontInterface.sp)
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        setShowKalendar(1)
                    }, text = data, fontSize = Settings.fontInterface.sp
            )
            Icon(modifier = Modifier.clickable {
                setShowKalendar(1)
            }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        setShowTimePicker(1)
                    }, text = time, fontSize = Settings.fontInterface.sp
            )
            Icon(modifier = Modifier.clickable {
                setShowTimePicker(1)
            }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
        }
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.Sabytie_end), fontSize = Settings.fontInterface.sp)
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        setShowKalendar(2)
                    }, text = data2, fontSize = Settings.fontInterface.sp
            )
            Icon(modifier = Modifier.clickable {
                setShowKalendar(2)
            }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable {
                        setShowTimePicker(2)
                    }, text = time2, fontSize = Settings.fontInterface.sp
            )
            Icon(modifier = Modifier.clickable {
                setShowTimePicker(2)
            }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
        }
        val options = stringArrayResource(R.array.sabytie_izmerenie)
        var expanded2 by remember { mutableStateOf(false) }
        val textFieldState2 = rememberTextFieldState(options[0])
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.Sabytie_uved), fontSize = Settings.fontInterface.sp)
            TextField(
                modifier = Modifier
                    .size(width = 90.dp, height = Dp.Unspecified)
                    .padding(10.dp),
                value = setTimeZa,
                onValueChange = { newText ->
                    if (newText.length < 4) {
                        if (newText.isNotEmpty() && newText.isDigitsOnly()) {
                            setTimeZa = newText
                        }
                        if (newText.isEmpty()) setTimeZa = newText
                    }
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            ExposedDropdownMenuBox(
                modifier = Modifier.padding(10.dp),
                expanded = expanded2,
                onExpandedChange = { expanded2 = it },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    state = textFieldState2,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                )
                ExposedDropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false },
                ) {
                    options.forEachIndexed { position, option ->
                        DropdownMenuItem(
                            text = { Text(option, style = MaterialTheme.typography.bodyLarge, fontSize = Settings.fontInterface.sp) },
                            onClick = {
                                textFieldState2Position = position
                                textFieldState2.setTextAndPlaceCursorAtEnd(option)
                                expanded2 = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            var textPavedamic2 = stringResource(R.string.sabytie_no_pavedam)
            var colorText = Color.Unspecified
            if (setTimeZa.isNotEmpty()) {
                val days = data.split(".")
                val times = time.split(":")
                val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                val result = gc.timeInMillis
                var londs = setTimeZa.toLong()
                when (textFieldState2.text) {
                    options[0] -> londs *= 60000L
                    options[1] -> londs *= 3600000L
                    options[2] -> londs *= 86400000L
                    options[3] -> londs *= 604800000L
                }
                val londs2 = result - londs
                gc.timeInMillis = londs2
                var nol1 = ""
                var nol2 = ""
                var nol3 = ""
                if (gc[Calendar.DATE] < 10) nol1 = "0"
                if (gc[Calendar.MONTH] < 9) nol2 = "0"
                if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                textPavedamic2 = stringResource(R.string.sabytie_pavedam, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR], gc[Calendar.HOUR_OF_DAY], nol3, gc[Calendar.MINUTE])
                val gcReal = Calendar.getInstance()
                if (gcReal.timeInMillis > londs2) {
                    colorText = MaterialTheme.colorScheme.primary
                }
            }
            Text(textPavedamic2, fontSize = Settings.fontInterface.sp, color = colorText)
        }
        val sabytieRepit = stringArrayResource(R.array.sabytie_repit)
        var expanded by remember { mutableStateOf(false) }
        val textFieldState = rememberTextFieldState(sabytieRepit[0])
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.Sabytie_repit), fontSize = Settings.fontInterface.sp)
            ExposedDropdownMenuBox(
                modifier = Modifier.padding(10.dp),
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
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    sabytieRepit.forEachIndexed { position, option ->
                        DropdownMenuItem(
                            text = { Text(option, style = MaterialTheme.typography.bodyLarge, fontSize = Settings.fontInterface.sp) },
                            onClick = {
                                textFieldStatePosition = position
                                textFieldState.setTextAndPlaceCursorAtEnd(option)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
        if (textFieldState.text != sabytieRepit[0]) {
            Row {
                Column(Modifier.selectableGroup())
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                modeRepit = 1
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = modeRepit == 1,
                            onClick = {
                                modeRepit = 1
                            }
                        )
                        Text(
                            stringResource(R.string.Sabytie_no_data_zakan),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                modeRepit = 2
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = modeRepit == 2,
                            onClick = {
                                modeRepit = 2
                            }
                        )
                        Text(
                            stringResource(R.string.Sabytie_install_kolkast_paz),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    if (modeRepit == 2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                modifier = Modifier
                                    .size(width = 90.dp, height = Dp.Unspecified)
                                    .padding(10.dp),
                                value = setPautorRaz,
                                onValueChange = { newText ->
                                    if (newText.length < 4) {
                                        if (newText.isNotEmpty() && newText.isDigitsOnly()) {
                                            setPautorRaz = newText
                                        }
                                        if (newText.isEmpty()) setPautorRaz = newText
                                    }
                                },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                            Text(
                                stringResource(R.string.Sabytie_paz),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                modeRepit = 3
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = modeRepit == 3,
                            onClick = {
                                modeRepit = 3
                            }
                        )
                        Text(
                            stringResource(R.string.Sabytie_install_data_end),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    if (modeRepit == 3) {
                        Text(
                            data3,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clickable {
                                    setShowKalendar(3)
                                },
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.color_padzei), fontSize = Settings.fontInterface.sp)
            var expanded1 by remember { mutableStateOf(false) }
            val textFieldState1 = rememberTextFieldState(padzeia)
            LaunchedEffect(padzeia) {
                textFieldState1.setTextAndPlaceCursorAtEnd(padzeia.ifEmpty { padzeiaText })
            }
            ExposedDropdownMenuBox(
                modifier = Modifier.padding(10.dp),
                expanded = expanded1,
                onExpandedChange = { expanded1 = it },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    state = textFieldState1,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(color.toColorInt()),
                        unfocusedContainerColor = Color(color.toColorInt()),
                        focusedTextColor = PrimaryTextBlack,
                        focusedIndicatorColor = PrimaryTextBlack,
                        unfocusedIndicatorColor = PrimaryTextBlack,
                        cursorColor = PrimaryTextBlack
                    ),
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp)
                )
                ExposedDropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false },
                ) {
                    optionsColors.forEachIndexed { position, option ->
                        DropdownMenuItem(
                            modifier = Modifier.background(Color(option.toColorInt())),
                            text = { Text(padzeia, style = MaterialTheme.typography.bodyLarge, fontSize = Settings.fontInterface.sp) },
                            onClick = {
                                colorPosition = position
                                color = option
                                expanded1 = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
    if (save) {
        savePadzeia(
            LocalContext.current,
            listPadzeia,
            padzeia,
            setTimeZa,
            data,
            data2,
            time,
            time2,
            textFieldState2Position,
            textFieldStatePosition,
            modeRepit,
            setPautorRaz,
            data3,
            colorPosition,
            isSave = { isSave() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()
    currentTime.add(Calendar.HOUR, 1)
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cansel), fontSize = Settings.fontInterface.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(stringResource(R.string.ok), fontSize = Settings.fontInterface.sp)
            }
        },
        text = { content() }
    )
}

fun savePadzeia(
    context: Context,
    padzeiaList: SnapshotStateList<Padzeia>,
    padzeiaNazva: String,
    pavedamicZaText: String,
    data: String,
    data2: String,
    time: String,
    time2: String,
    pavedamicZaPosit: Int,
    repit: Int,
    repitSettings: Int,
    repitSettingsCountText: String,
    repitSettingsDataText: String,
    color: Int,
    isSave: () -> Unit
) {
    val edit = padzeiaNazva.trim()
    var edit2 = pavedamicZaText
    var result: Long
    var timeRepit: String
    val c = Calendar.getInstance()
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or 0
    } else {
        0
    }
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (edit != "") {
        var londs: Long = 0
        var londs2: Long = 0
        val days = data.split(".")
        val times = time.split(":")
        val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
        result = gc.timeInMillis
        if (edit2 != "") {
            londs = edit2.toLong()
            when (pavedamicZaPosit) {
                0 -> londs *= 60000L
                1 -> londs *= 3600000L
                2 -> londs *= 86400000L
                3 -> londs *= 604800000L
            }
        } else {
            edit2 = "-1"
        }
        when (repit) {
            0 -> {
                timeRepit = "0"
                if (edit2 != "-1") {
                    londs2 = result - londs
                    val londs3 = londs2 / 100000L
                    if (londs2 > c.timeInMillis) {
                        val intent = Settings.createIntentSabytie(context, edit, data, time)
                        val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                            } else {
                                am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                            }
                        } catch (_: SecurityException) {
                        }
                    }
                }
                padzeiaList.add(Padzeia(edit, data, time, londs2, pavedamicZaPosit, edit2, data2, time2, repit, timeRepit, color, false))
            }

            1 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                val dayof = gc[Calendar.DAY_OF_YEAR]
                var leapYear = 365 - dayof + 365 + 1
                if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            val intent = Settings.createIntentSabytie(context, edit, data, time)
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                }
                            } catch (_: SecurityException) {
                            }
                        }
                    }
                    var nol1 = ""
                    var nol2 = ""
                    var nol3 = ""
                    var nol4 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    if (gc2[Calendar.DAY_OF_MONTH] < 10) nol3 = "0"
                    if (gc2[Calendar.MONTH] < 9) nol4 = "0"
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2[Calendar.DAY_OF_MONTH] + "." + nol4 + (gc2[Calendar.MONTH] + 1) + "." + gc2[Calendar.YEAR], time2, repit, timeRepit, color, false))
                    gc.add(Calendar.DATE, 1)
                    gc2.add(Calendar.DATE, 1)
                    i++
                }
            }

            2 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                val dayof = gc[Calendar.DAY_OF_YEAR]
                var leapYear = 365 - dayof + 365 + 1
                if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (gc[Calendar.DAY_OF_WEEK] in 2..6) {
                        if (edit2 != "-1") {
                            londs2 = result - londs
                            val londs3 = londs2 / 100000L
                            if (londs2 > c.timeInMillis) {
                                val intent = Settings.createIntentSabytie(context, edit, data, time)
                                val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    }
                                } catch (_: SecurityException) {
                                }
                            }
                        }
                        var nol1 = ""
                        var nol2 = ""
                        var nol3 = ""
                        var nol4 = ""
                        if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                        if (gc[Calendar.MONTH] < 9) nol2 = "0"
                        if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                        if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                        padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    }
                    gc.add(Calendar.DATE, 1)
                    gc2.add(Calendar.DATE, 1)
                    i++
                }
            }

            3 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                val dayof = gc[Calendar.DAY_OF_YEAR]
                var leapYear = 365 - dayof + 365 + 1
                if (gc.isLeapYear(gc[Calendar.YEAR])) leapYear = 365 - dayof + 366 + 1
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var schet = 0
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (schet < 2) {
                        if (edit2 != "-1") {
                            londs2 = result - londs
                            val londs3 = londs2 / 100000L
                            if (londs2 > c.timeInMillis) {
                                val intent = Settings.createIntentSabytie(context, edit, data, time)
                                val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    } else {
                                        am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                    }
                                } catch (_: SecurityException) {
                                }
                            }
                        }
                        var nol1 = ""
                        var nol2 = ""
                        var nol3 = ""
                        var nol4 = ""
                        if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                        if (gc[Calendar.MONTH] < 9) nol2 = "0"
                        if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                        if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                        padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    }
                    schet++
                    gc.add(Calendar.DATE, 1)
                    gc2.add(Calendar.DATE, 1)
                    if (schet == 4) schet = 0
                    i++
                }
            }

            4 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                val dayof = gc[Calendar.WEEK_OF_YEAR]
                var leapYear = 52 - dayof + 52 + 1
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.WEEK_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            val intent = Settings.createIntentSabytie(context, edit, data, time)
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                }
                            } catch (_: SecurityException) {
                            }
                        }
                    }
                    var nol1 = ""
                    var nol2 = ""
                    var nol3 = ""
                    var nol4 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                    if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    gc.add(Calendar.DATE, 7)
                    gc2.add(Calendar.DATE, 7)
                    i++
                }
            }

            5 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)

                val dayof = gc[Calendar.WEEK_OF_YEAR]
                var leapYear = 26 - dayof / 2 + 26 + 1
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            val intent = Settings.createIntentSabytie(context, edit, data, time)
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                }
                            } catch (_: SecurityException) {
                            }
                        }
                    }
                    var nol1 = ""
                    var nol2 = ""
                    var nol3 = ""
                    var nol4 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                    if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    gc.add(Calendar.DATE, 14)
                    gc2.add(Calendar.DATE, 14)
                    i++
                }
            }

            6 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)

                val dayof = gc[Calendar.WEEK_OF_YEAR]
                var leapYear = 13 - dayof / 4 + 13
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            val intent = Settings.createIntentSabytie(context, edit, data, time)
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                }
                            } catch (_: SecurityException) {
                            }
                        }
                    }
                    var nol1 = ""
                    var nol2 = ""
                    var nol3 = ""
                    var nol4 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                    if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    gc.add(Calendar.DATE, 28)
                    gc2.add(Calendar.DATE, 28)
                    i++
                }
            }

            7 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                val dayof = gc[Calendar.MONTH] + 1
                var leapYear = 12 - dayof + 12 + 1
                if (repitSettings == 3) {
                    timeRepit = repitSettingsDataText
                    val tim = timeRepit.split(".")
                    val gc3 = GregorianCalendar(tim[2].toInt(), tim[1].toInt() - 1, tim[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                    var resd = gc3[Calendar.DAY_OF_YEAR] - dayof
                    if (gc[Calendar.YEAR] < gc3[Calendar.YEAR]) {
                        var yeav = 365
                        if (gc.isLeapYear(gc[Calendar.YEAR])) yeav = 366
                        resd = yeav - dayof + gc3[Calendar.DAY_OF_YEAR]
                    }
                    leapYear = resd + 1
                }
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            val intent = Settings.createIntentSabytie(context, edit, data, time)
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                }
                            } catch (_: SecurityException) {
                            }
                        }
                    }
                    var nol1 = ""
                    var nol2 = ""
                    var nol3 = ""
                    var nol4 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                    if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    gc.add(Calendar.MONTH, 1)
                    gc2.add(Calendar.MONTH, 1)
                    i++
                }
            }

            8 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = data2.split(".")
                val gc2 = GregorianCalendar(rdat2[2].toInt(), rdat2[1].toInt() - 1, rdat2[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
                var leapYear = 10
                if (repitSettings == 2) {
                    timeRepit = repitSettingsCountText
                    if (timeRepit == "") timeRepit = "1"
                    leapYear = timeRepit.toInt()
                }
                var i = 0
                while (i < leapYear) {
                    result = gc.timeInMillis
                    if (edit2 != "-1") {
                        londs2 = result - londs
                        val londs3 = londs2 / 100000L
                        if (londs2 > c.timeInMillis) {
                            val intent = Settings.createIntentSabytie(context, edit, data, time)
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, londs2, pIntent)
                                }
                            } catch (_: SecurityException) {
                            }
                        }
                    }
                    var nol1 = ""
                    var nol2 = ""
                    var nol3 = ""
                    var nol4 = ""
                    if (gc[Calendar.DAY_OF_MONTH] < 10) nol1 = "0"
                    if (gc[Calendar.MONTH] < 9) nol2 = "0"
                    if (gc2.get(Calendar.DAY_OF_MONTH) < 10) nol3 = "0"
                    if (gc2.get(Calendar.MONTH) < 9) nol4 = "0"
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), time2, repit, timeRepit, color, false))
                    gc.add(Calendar.YEAR, 1)
                    gc2.add(Calendar.YEAR, 1)
                    i++
                }
            }
        }
        val gson = Gson()
        val outputStream = FileWriter("${context.filesDir}/Sabytie.json")
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
        outputStream.write(gson.toJson(padzeiaList, type))
        outputStream.close()
        padzeiaList.sort()
        isSave()
        Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun DialogSabytieShow(
    title: String,
    position: Int,
    padzeia: String,
    paznicia: Boolean,
    onDismiss: () -> Unit,
    onEdit: (Int) -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(text = title)
        },
        text = {
            val annotatedString = buildAnnotatedString {
                append(padzeia)
                var t1 = padzeia.indexOf("Паведаміць:")
                if (padzeia.contains("Ніколі")) t1 = -1
                if (paznicia && t1 != -1) addStyle(SpanStyle(color = MaterialTheme.colorScheme.primary), t1, padzeia.length)
            }
            Text(annotatedString, fontSize = Settings.fontInterface.sp)
        },
        onDismissRequest = {
            onDismiss()
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEdit(position)
                }
            ) {
                Text(stringResource(R.string.redagaktirovat), fontSize = Settings.fontInterface.sp)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok), fontSize = Settings.fontInterface.sp)
            }
        }
    )
}

@Preview
@Composable
fun Test() {
    AddPadzeia(SnapshotStateList(), false, "15.02.2025", "15.02.2025", "15.02.2025", "15:00", "15:00", setShowKalendar = {}, setShowTimePicker = {}, isSave = {})
}