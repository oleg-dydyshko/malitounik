package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PadzeiaView(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    var editMode by remember { mutableStateOf(false) }
    var editPadzeia by remember { mutableStateOf(false) }
    var editPadzeiaInit by remember { mutableStateOf(true) }
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
    if (deliteAll) {
        DialogDelitePadsei(
            onDismiss = { deliteAll = false },
            onDelOld = {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or 0
                } else {
                    0
                }
                val c2 = Calendar.getInstance()
                c2.set(Calendar.SECOND, 0)
                val del = ArrayList<Padzeia>()
                for (p in listPadzeia) {
                    if (p.repit == 0) {
                        val days = p.datK.split(".")
                        val time = p.timK.split(":")
                        val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), time[0].toInt(), time[1].toInt(), 0)
                        if (c2.timeInMillis >= gc.timeInMillis) {
                            if (p.sec != "-1") {
                                val intent = Settings.createIntentSabytie(context, p.padz, p.dat, p.tim)
                                val londs3 = p.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                                alarmManager.cancel(pIntent)
                                pIntent.cancel()
                            }
                            del.add(p)
                        }
                    } else {
                        val days = p.dat.split(".")
                        val time = p.timK.split(":")
                        val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), time[0].toInt(), time[1].toInt(), 0)
                        if (c2.timeInMillis >= gc.timeInMillis) {
                            if (p.sec != "-1") {
                                val intent = Settings.createIntentSabytie(context, p.padz, p.dat, p.tim)
                                val londs3 = p.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                                alarmManager.cancel(pIntent)
                                pIntent.cancel()
                            }
                            del.add(p)
                        }
                    }
                }
                if (del.isNotEmpty()) {
                    listPadzeia.removeAll(del.toSet())
                    val outputStream = FileWriter("${context.filesDir}/Sabytie.json")
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
                    outputStream.write(gson.toJson(listPadzeia, type))
                    outputStream.close()
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                    }
                }
                deliteAll = false
            },
            onDelAll = {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PendingIntent.FLAG_IMMUTABLE or 0
                        } else {
                            0
                        }
                        for (p in listPadzeia) {
                            if (p.sec != "-1") {
                                val intent = Settings.createIntentSabytie(context, p.padz, p.dat, p.tim)
                                val londs3 = p.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                                alarmManager.cancel(pIntent)
                                pIntent.cancel()
                            }
                        }
                        val file = File("${context.filesDir}/Sabytie.json")
                        if (file.exists()) file.delete()
                    }
                    listPadzeia.clear()
                    Toast.makeText(context, context.getString(R.string.remove_padzea), Toast.LENGTH_SHORT).show()
                }
                deliteAll = false
            }
        )
    }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    BackHandler(showDropdown || editMode) {
        if (editMode) {
            editMode = false
            editPadzeiaInit = true
        } else showDropdown = !showDropdown
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
                time2 = "${state.hour}:$nyl${state.minute}"
            } else time2 = "${state.hour}:$nyl${state.minute}"
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
            stringResource(R.string.sabytie_pachatak_show, data1, time1, dataK, timeK, res)
        } else {
            stringResource(R.string.sabytie_kali, data1, time1, res)
        }
        DialogSabytieShow(
            title,
            showPadziaPosition,
            textR,
            paznicia,
            onEdit = {
                editMode = true
                editPadzeiaInit = true
                editPadzeia = true
                showPadzia = false
            },
            onDismiss = {
                showPadzia = false
            }
        )
    }
    var delitePadzia by remember { mutableStateOf(false) }
    var dialogContextMenu by remember { mutableStateOf(false) }
    if (dialogContextMenu) {
        val p = listPadzeia[showPadziaPosition]
        val title = p.padz
        DialogContextMenu(
            title,
            onEdit = {
                dialogContextMenu = false
                editMode = true
                editPadzeiaInit = true
                editPadzeia = true
                showPadzia = false
            },
            onDelite = {
                dialogContextMenu = false
                delitePadzia = true
            }) {
            dialogContextMenu = false
        }
    }
    if (delitePadzia) {
        DialogDelite(listPadzeia[showPadziaPosition].padz, onConfirmation = {
            val sab = listPadzeia[showPadziaPosition]
            val filen = sab.padz
            val del = ArrayList<Padzeia>()
            for (p in listPadzeia) {
                if (p.padz == filen) {
                    del.add(p)
                }
            }
            listPadzeia.removeAll(del.toSet())
            val outputStream = FileWriter("${context.filesDir}/Sabytie.json")
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
            outputStream.write(gson.toJson(listPadzeia, type))
            outputStream.close()
            CoroutineScope(Dispatchers.IO).launch {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (sab.count == "0") {
                    if (sab.repit == 1 || sab.repit == 4 || sab.repit == 5 || sab.repit == 6) {
                        if (sab.sec != "-1") {
                            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                PendingIntent.FLAG_IMMUTABLE or 0
                            } else {
                                0
                            }
                            val intent = Settings.createIntentSabytie(context, sab.padz, sab.dat, sab.tim)
                            val londs3 = sab.paznic / 100000L
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            alarmManager.cancel(pIntent)
                            pIntent.cancel()
                        }
                    } else {
                        for (p in del) {
                            if (p.padz.contains(filen)) {
                                if (p.sec != "-1") {
                                    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        PendingIntent.FLAG_IMMUTABLE or 0
                                    } else {
                                        0
                                    }
                                    val intent = Settings.createIntentSabytie(context, p.padz, p.dat, p.tim)
                                    val londs3 = p.paznic / 100000L
                                    val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                                    alarmManager.cancel(pIntent)
                                    pIntent.cancel()
                                }
                            }
                        }
                    }
                } else {
                    for (p in del) {
                        if (p.sec != "-1") {
                            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                PendingIntent.FLAG_IMMUTABLE or 0
                            } else {
                                0
                            }
                            val intent = Settings.createIntentSabytie(context, p.padz, p.dat, p.tim)
                            val londs3 = p.paznic / 100000L
                            val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                            alarmManager.cancel(pIntent)
                            pIntent.cancel()
                        }
                    }
                }
            }
            Toast.makeText(context, context.getString(R.string.remove_padzea), Toast.LENGTH_SHORT).show()
            delitePadzia = false
        }, onDismiss = {
            delitePadzia = false
        })
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
                            text = stringResource(R.string.sabytie).uppercase(),
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                },
                navigationIcon = {
                    if (editMode) {
                        IconButton(
                            onClick = {
                                editMode = false
                                editPadzeiaInit = true
                            },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    contentDescription = ""
                                )
                            })
                    } else {
                        IconButton(
                            onClick = {
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
                    if (editMode) {
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
                            editMode = true
                            editPadzeia = false
                            editPadzeiaInit = false
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
                                    showPadziaPosition = index
                                    dialogContextMenu = true
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
                        KaliandarScreenMounth(
                            colorBlackboard = MaterialTheme.colorScheme.onTertiary, setPageCaliandar = { date ->
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
                            },
                            close = {
                                kalendarMun = false
                                kalendarMun2 = false
                                kalendarMun3 = false
                                showDropdown = false
                            })
                    }
                }
            }
            if (editMode) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (editPadzeiaInit) {
                        editPadzeiaInit = false
                        val p = listPadzeia[showPadziaPosition]
                        data = p.dat
                        data2 = p.datK
                        data3 = p.count
                        time = p.tim
                        time2 = p.timK
                    }
                    AddPadzeia(savePadzia, data, data2, data3, time, time2, editPadzeia, listPadzeia, showPadziaPosition, setShowTimePicker = {
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
                        editMode = false
                        savePadzia = false
                        editPadzeiaInit = true
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
    save: Boolean,
    data: String,
    data2: String,
    data3: String,
    time: String,
    time2: String,
    editPadzeia: Boolean,
    listPadzeia: SnapshotStateList<Padzeia>,
    position: Int,
    setShowTimePicker: (Int) -> Unit,
    setShowKalendar: (Int) -> Unit,
    isSave: () -> Unit
) {
    val p = if (editPadzeia) listPadzeia[position]
    else Padzeia("", data, time, 0, 0, "-1", data2, time2, 0, data3, 0, false)
    val context = LocalContext.current
    var padzeia by remember { mutableStateOf(p.padz) }
    var setTimeZa by remember { mutableStateOf(if (p.sec == "-1") "" else p.sec) }
    var setPautorRaz by remember { mutableStateOf("5") }
    var modeRepit by remember { mutableIntStateOf(p.repit) }
    var textFieldState2Position by remember { mutableIntStateOf(p.vybtime) }
    var textFieldStatePosition by remember { mutableIntStateOf(p.repit) }
    val optionsColors = stringArrayResource(R.array.colors)
    var countText = data3
    val count = p.count.split(".")
    var konecSabytie by remember { mutableStateOf(p.konecSabytie) }
    LaunchedEffect(Unit) {
        if (editPadzeia) {
            when {
                p.count == "0" -> {
                    modeRepit = 1
                    setPautorRaz = "5"
                    countText = p.datK
                }

                count.size == 1 -> {
                    modeRepit = 2
                    setPautorRaz = p.count
                    countText = p.datK
                }

                else -> {
                    modeRepit = 3
                    setPautorRaz = "5"
                    countText = p.count
                }
            }
        } else {
            modeRepit = 1
            setPautorRaz = "5"
            countText = p.datK
        }
        //countText = if (position != -1) p.datK else data3
    }
    var color by remember { mutableStateOf(optionsColors[p.color]) }
    var colorPosition by remember { mutableIntStateOf(p.color) }
    var dialodNotificatin by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(setTimeZa) {
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
                k.edit {
                    putInt("notification", Settings.NOTIFICATION_SVIATY_NONE)
                }
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
                    intent.data = ("package:" + context.packageName).toUri()
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
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        )
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.sabytie_pachatak), fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        setShowKalendar(1)
                    }, text = data, fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(modifier = Modifier.clickable {
                setShowKalendar(1)
            }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        setShowTimePicker(1)
                    }, text = time, fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(modifier = Modifier.clickable {
                setShowTimePicker(1)
            }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
            Checkbox(
                checked = !konecSabytie,
                onCheckedChange = {
                    konecSabytie = !konecSabytie
                }
            )
            Text(
                stringResource(R.string.sabytie_bez_kanca),
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        if (konecSabytie) {
            Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.Sabytie_end), fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            setShowKalendar(2)
                        }, text = data2, fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Icon(modifier = Modifier.clickable {
                    setShowKalendar(2)
                }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            setShowTimePicker(2)
                        }, text = time2, fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Icon(modifier = Modifier.clickable {
                    setShowTimePicker(2)
                }, painter = painterResource(R.drawable.keyboard_arrow_down), contentDescription = "", tint = Divider)
            }
        }
        val options = stringArrayResource(R.array.sabytie_izmerenie)
        var expanded2 by remember { mutableStateOf(false) }
        val textFieldState2 = rememberTextFieldState(options[textFieldState2Position])
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.Sabytie_uved), fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
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
                            text = {
                                Text(
                                    option, fontSize = Settings.fontInterface.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
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
            var colorText = MaterialTheme.colorScheme.secondary
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
        val textFieldState = rememberTextFieldState(sabytieRepit[textFieldStatePosition])
        Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.Sabytie_repit), fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
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
                            text = {
                                Text(
                                    option, fontSize = Settings.fontInterface.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
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
                            countText,
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
            Text(
                stringResource(R.string.color_padzei), fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            var expanded1 by remember { mutableStateOf(false) }
            val textFieldState1 = rememberTextFieldState(padzeia)
            LaunchedEffect(padzeia) {
                textFieldState1.setTextAndPlaceCursorAtEnd(padzeia.ifEmpty { context.getString(R.string.sabytie_name) })
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
                    trailingIcon = {
                        val image = if (expanded1) painterResource(R.drawable.keyboard_arrow_up)
                        else painterResource(R.drawable.keyboard_arrow_down)
                        Icon(painter = image, contentDescription = "", tint = PrimaryTextBlack)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(color.toColorInt()),
                        unfocusedContainerColor = Color(color.toColorInt()),
                        unfocusedTextColor = PrimaryTextBlack,
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
                            text = {
                                Text(
                                    textFieldState1.text.toString(), style = MaterialTheme.typography.bodyLarge, fontSize = Settings.fontInterface.sp,
                                    color = PrimaryTextBlack
                                )
                            },
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
            position,
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
            countText,
            konecSabytie,
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
        val colorText = if ((LocalActivity.current as MainActivity).dzenNoch) PrimaryText
        else PrimaryTextBlack
        val color = TimePickerDefaults.colors().copy(timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary, timeSelectorSelectedContentColor = colorText)
        TimePicker(
            colors = color,
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
    position: Int,
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
    konecSabytie: Boolean,
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
    var dataK = data2
    var timeK = time2
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (edit != "") {
        var londs: Long = 0
        var londs2: Long = 0
        val days = data.split(".")
        val times = time.split(":")
        val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), times[0].toInt(), times[1].toInt(), 0)
        result = gc.timeInMillis
        if (!konecSabytie) {
            dataK = data
            timeK = time
        }
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
        if (position != -1) {
            val p = padzeiaList[position]
            val del = ArrayList<Padzeia>()
            padzeiaList.forEach {
                if (p.padz == it.padz) {
                    del.add(it)
                    if (it.sec != "-1") {
                        val intent = Settings.createIntentSabytie(context, it.padz, it.dat, it.tim)
                        val londs3 = it.paznic / 100000L
                        val pIntent = PendingIntent.getBroadcast(context, londs3.toInt(), intent, flags)
                        am.cancel(pIntent)
                        pIntent.cancel()
                    }
                }
            }
            padzeiaList.removeAll(del.toSet())
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
                padzeiaList.add(Padzeia(edit, data, time, londs2, pavedamicZaPosit, edit2, dataK, timeK, repit, timeRepit, color, konecSabytie))
            }

            1 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = dataK.split(".")
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
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2[Calendar.DAY_OF_MONTH] + "." + nol4 + (gc2[Calendar.MONTH] + 1) + "." + gc2[Calendar.YEAR], timeK, repit, timeRepit, color, konecSabytie))
                    gc.add(Calendar.DATE, 1)
                    gc2.add(Calendar.DATE, 1)
                    i++
                }
            }

            2 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = dataK.split(".")
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
                        padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
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
                val rdat2 = dataK.split(".")
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
                        padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
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
                val rdat2 = dataK.split(".")
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
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
                    gc.add(Calendar.DATE, 7)
                    gc2.add(Calendar.DATE, 7)
                    i++
                }
            }

            5 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = dataK.split(".")
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
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
                    gc.add(Calendar.DATE, 14)
                    gc2.add(Calendar.DATE, 14)
                    i++
                }
            }

            6 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = dataK.split(".")
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
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
                    gc.add(Calendar.DATE, 28)
                    gc2.add(Calendar.DATE, 28)
                    i++
                }
            }

            7 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = dataK.split(".")
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
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
                    gc.add(Calendar.MONTH, 1)
                    gc2.add(Calendar.MONTH, 1)
                    i++
                }
            }

            8 -> {
                timeRepit = "0"
                val rdat = data.split(".")
                gc[rdat[2].toInt(), rdat[1].toInt() - 1, rdat[0].toInt(), times[0].toInt(), times[1].toInt()] = 0
                val rdat2 = dataK.split(".")
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
                    padzeiaList.add(Padzeia(edit, nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR], time, londs2, pavedamicZaPosit, edit2, nol3 + gc2.get(Calendar.DAY_OF_MONTH) + "." + nol4 + (gc2.get(Calendar.MONTH) + 1) + "." + gc2.get(Calendar.YEAR), timeK, repit, timeRepit, color, konecSabytie))
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
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column {
                Text(
                    text = title, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                val annotatedString = buildAnnotatedString {
                    append(padzeia)
                    var t1 = padzeia.indexOf("Паведаміць:")
                    if (padzeia.contains("Ніколі")) t1 = -1
                    if (paznicia && t1 != -1) addStyle(SpanStyle(color = MaterialTheme.colorScheme.primary), t1, padzeia.length)
                }
                Text(
                    text = annotatedString, fontSize = Settings.fontInterface.sp,
                    modifier = Modifier.padding(10.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onEdit(position) },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.edit), contentDescription = "")
                        Text(stringResource(R.string.redagaktirovat), fontSize = 22.sp)
                    }
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDelitePadsei(
    onDismiss: () -> Unit,
    onDelAll: () -> Unit,
    onDelOld: () -> Unit
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
                    text = stringResource(R.string.del_sabytie).uppercase(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = stringResource(R.string.remove_sabytie_iak), fontSize = Settings.fontInterface.sp,
                    modifier = Modifier.padding(10.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDelAll() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.delete), contentDescription = "")
                        Text(stringResource(R.string.sabytie_del_all), fontSize = 22.sp)
                    }
                    TextButton(
                        onClick = { onDelOld() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.delete), contentDescription = "")
                        Text(stringResource(R.string.sabytie_del_old), fontSize = 22.sp)
                    }
                }
                TextButton(
                    onClick = { onDismiss() },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                    Text(stringResource(R.string.cansel), fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
fun DialogContextMenu(
    title: String,
    onEdit: () -> Unit,
    onDelite: () -> Unit,
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
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp),
                )
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable {
                            onEdit()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(22.dp, 22.dp),
                        painter = painterResource(R.drawable.edit),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.redagaktirovat),
                        modifier = Modifier
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable {
                            onDelite()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(22.dp, 22.dp),
                        painter = painterResource(R.drawable.delete),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.delite),
                        modifier = Modifier
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                }
                HorizontalDivider()
                TextButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.End),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                    Text(stringResource(R.string.cansel), fontSize = 18.sp)
                }
            }
        }
    }
}