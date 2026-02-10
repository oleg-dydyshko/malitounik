package by.carkva_gazeta.malitounik.admin

import android.content.Context
import android.icu.util.Calendar
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.views.DropdownMenuBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

@Composable
fun DialogEditSvityiaAndSviaty(adminViewModel: Piasochnica, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            val context = LocalContext.current
            val resourse = LocalResources.current
            val activity = LocalActivity.current as? MainActivity
            val page = Settings.caliandarPosition
            val munName = stringArrayResource(R.array.meciac_smoll)
            var textFieldValueStateTitle by rememberSaveable { mutableStateOf("") }
            var textFieldValueStateCytanne by rememberSaveable { mutableStateOf("") }
            var textFieldStateTitleCytanne by rememberSaveable { mutableStateOf("") }
            var textFieldStateCytanne by rememberSaveable { mutableStateOf("") }
            var style by rememberSaveable { mutableIntStateOf(0) }
            var tipicon by rememberSaveable { mutableIntStateOf(0) }
            val arrayList = remember { mutableStateListOf<Tipicon>() }
            val arrayList1 = remember { mutableStateListOf<String>() }
            var dayPascha by remember { mutableIntStateOf(0) }
            var isProgressVisable by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                if (activity?.savedInstanceState == null) {
                    setDate(context = context, isLoad = {
                        isProgressVisable = it
                    }) { sviatyiaList, titleCytanny, cytanny, dayOfPascha ->
                        dayPascha = dayOfPascha
                        textFieldValueStateTitle = sviatyiaList[0]
                        textFieldValueStateCytanne = sviatyiaList[1]
                        var position = 2
                        when (sviatyiaList[2].toInt()) {
                            6 -> position = 0
                            7 -> position = 1
                            8 -> position = 2
                        }
                        style = position
                        val znaki = sviatyiaList[3]
                        val position2 = if (znaki == "") 0
                        else znaki.toInt()
                        tipicon = position2
                        textFieldStateTitleCytanne = titleCytanny
                        textFieldStateCytanne = cytanny
                        arrayList.add(Tipicon(R.drawable.empty, "Няма"))
                        arrayList.add(Tipicon(R.drawable.znaki_krest, "З вялікай вячэрняй і вялікім услаўленьнем на ютрані"))
                        arrayList.add(Tipicon(R.drawable.znaki_krest_v_kruge, "Двунадзясятыя і вялікія сьвяты"))
                        arrayList.add(Tipicon(R.drawable.znaki_krest_v_polukruge, "З ліцьцёй на вячэрні"))
                        arrayList.add(Tipicon(R.drawable.znaki_ttk, "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані"))
                        arrayList.add(Tipicon(R.drawable.znaki_ttk_black, "З штодзённай вячэрняй і малым услаўленьнем на ютрані"))
                        arrayList1.addAll(resourse.getStringArray(R.array.admin_svity))
                    }
                } else {
                    arrayList.add(Tipicon(R.drawable.empty, "Няма"))
                    arrayList.add(Tipicon(R.drawable.znaki_krest, "З вялікай вячэрняй і вялікім услаўленьнем на ютрані"))
                    arrayList.add(Tipicon(R.drawable.znaki_krest_v_kruge, "Двунадзясятыя і вялікія сьвяты"))
                    arrayList.add(Tipicon(R.drawable.znaki_krest_v_polukruge, "З ліцьцёй на вячэрні"))
                    arrayList.add(Tipicon(R.drawable.znaki_ttk, "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані"))
                    arrayList.add(Tipicon(R.drawable.znaki_ttk_black, "З штодзённай вячэрняй і малым услаўленьнем на ютрані"))
                    arrayList1.addAll(resourse.getStringArray(R.array.admin_svity))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.admin_date, Settings.data[page][1].toInt(), munName[Settings.data[page][2].toInt()]),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                if (isProgressVisable) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    item {
                        TextField(
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                            placeholder = { Text(stringResource(R.string.sviatyia), fontSize = Settings.fontInterface.sp) },
                            value = textFieldValueStateTitle,
                            onValueChange = {
                                textFieldValueStateTitle = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                adminViewModel.sendPostRequest(textFieldValueStateTitle, textFieldValueStateCytanne, style, tipicon.toString(), textFieldStateTitleCytanne, textFieldStateCytanne, dayPascha) {
                                    isProgressVisable = it
                                    if (!isProgressVisable) onDismiss()
                                }
                            })
                        )
                        TextField(
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                            placeholder = { Text(stringResource(R.string.czytanne), fontSize = Settings.fontInterface.sp) },
                            value = textFieldValueStateCytanne,
                            onValueChange = {
                                textFieldValueStateCytanne = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                adminViewModel.sendPostRequest(textFieldValueStateTitle, textFieldValueStateCytanne, style, tipicon.toString(), textFieldStateTitleCytanne, textFieldStateCytanne, dayPascha) {
                                    isProgressVisable = it
                                    if (!isProgressVisable) onDismiss()
                                }
                            })
                        )
                        if (arrayList1.isNotEmpty()) {
                            DropdownMenuBox(
                                initValue = style,
                                menuList = arrayList1.toTypedArray()
                            ) {
                                style = it
                            }
                        }
                        if (arrayList.isNotEmpty()) {
                            DropdownMenuBoxTipicon(
                                position = tipicon,
                                menuList = arrayList
                            ) {
                                tipicon = it
                            }
                        }
                        TextField(
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                            value = textFieldStateTitleCytanne,
                            onValueChange = {
                                textFieldStateTitleCytanne = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                adminViewModel.sendPostRequest(textFieldValueStateTitle, textFieldValueStateCytanne, style, tipicon.toString(), textFieldStateTitleCytanne, textFieldStateCytanne, dayPascha) {
                                    isProgressVisable = it
                                    if (!isProgressVisable) onDismiss()
                                }
                            })
                        )
                        TextField(
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                            placeholder = { Text(stringResource(R.string.czytanne), fontSize = Settings.fontInterface.sp) },
                            value = textFieldStateCytanne,
                            onValueChange = {
                                textFieldStateCytanne = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                adminViewModel.sendPostRequest(textFieldValueStateTitle, textFieldValueStateCytanne, style, tipicon.toString(), textFieldStateTitleCytanne, textFieldStateCytanne, dayPascha) {
                                    isProgressVisable = it
                                    if (!isProgressVisable) onDismiss()
                                }
                            })
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.End)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                            ) {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                                Text(stringResource(R.string.cansel), fontSize = 18.sp)
                            }
                            TextButton(
                                onClick = {
                                    adminViewModel.sendPostRequest(textFieldValueStateTitle, textFieldValueStateCytanne, style, tipicon.toString(), textFieldStateTitleCytanne, textFieldStateCytanne, dayPascha) {
                                        isProgressVisable = it
                                        if (!isProgressVisable) onDismiss()
                                    }
                                }, shape = MaterialTheme.shapes.small
                            ) {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.save), contentDescription = "")
                                Text(stringResource(R.string.save_sabytie), fontSize = 18.sp)
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.imePadding().padding(bottom = 10.dp))
                    }
                }
            }
        }
    }
}

suspend fun loadCalendarSviatyiaFile(context: Context, count: Int = 0): String {
    var error = false
    val localFile = File("${context.filesDir}/cache/cache.txt")
    var builder = ""
    Malitounik.referens.child("/calendarsviatyia.txt").getFile(localFile).addOnCompleteListener {
        if (it.isSuccessful) {
            builder = localFile.readText()
            if (builder == "") error = true
        } else {
            error = true
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }.await()
    if (error && count < 3) {
        loadCalendarSviatyiaFile(context = context, count = count + 1)
        return builder
    }
    return builder
}

fun setDate(context: Context, count: Int = 0, isLoad: (Boolean) -> Unit, dataList: (ArrayList<String>, String, String, Int) -> Unit) {
    if (Settings.isNetworkAvailable(context)) {
        isLoad(true)
        var error = false
        val dayOfPascha = Settings.data[Settings.caliandarPosition][22].toInt()
        val year = Settings.data[Settings.caliandarPosition][3].toInt()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val builder = loadCalendarSviatyiaFile(context)
                val sviatyia = ArrayList<ArrayList<String>>()
                val line = builder.split("\n")
                for (element in line) {
                    val reg = element.split("<>")
                    val list = ArrayList<String>()
                    for (element2 in reg) {
                        list.add(element2)
                    }
                    sviatyia.add(list)
                }
                val localFile1 = File("${context.filesDir}/cache/cache1.txt")
                Malitounik.referens.child("/calendar-cytanne_$year.txt").getFile(localFile1).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var countDay = 0
                        var countDayNovyGog = 0
                        var calPos = 0
                        var calPosNovyGod = -1
                        var isNovyYear = false
                        Settings.data.forEachIndexed { index, strings ->
                            if (strings[3].toInt() == year && calPosNovyGod == -1) {
                                calPosNovyGod = index
                            }
                            if (strings[22].toInt() == 0 && strings[3].toInt() == year) {
                                calPos = index
                                return@forEachIndexed
                            }
                        }
                        localFile1.forEachLine { fw ->
                            if (fw.isNotEmpty()) {
                                val c = if (isNovyYear) Settings.data[calPosNovyGod + countDayNovyGog]
                                else Settings.data[calPos + countDay]
                                val myDayOfPasha = c[22].toInt()
                                if (isNovyYear) countDayNovyGog++
                                else countDay++
                                if (c[3].toInt() == year && c[2].toInt() == Calendar.DECEMBER && c[1].toInt() == 31) {
                                    isNovyYear = true
                                }
                                if (dayOfPascha == myDayOfPasha) {
                                    val preList = fw.split("<>")
                                    val title = preList[2]
                                    val cytanne = preList[3]
                                    dataList(if (sviatyia.isNotEmpty()) sviatyia[c[24].toInt() - 1] else ArrayList(), title, cytanne, dayOfPascha)
                                    return@forEachLine
                                }
                            }
                        }
                    } else {
                        error = true
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                }.await()
            } catch (_: Throwable) {
                error = true
                Toast.makeText(context, context.getString(R.string.error_ch), Toast.LENGTH_SHORT).show()
            }
            if (error && count < 3) {
                setDate(context = context, count = count + 1, isLoad = {}, dataList = { _, _, _, _ -> })
            }
            isLoad(false)
        }
    } else {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBoxTipicon(
    position: Int,
    menuList: SnapshotStateList<Tipicon>,
    onClickItem: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldNotificstionState = rememberTextFieldState(menuList[position].title)
    var textFieldMyPosition by remember { mutableIntStateOf(position) }
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it },
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
            Image(
                modifier = Modifier
                    .padding(start = 5.dp, end = 10.dp)
                    .size(22.dp, 22.dp),
                painter = painterResource(menuList[textFieldMyPosition].imageResource),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                text = textFieldNotificstionState.text.toString(),
                fontSize = (Settings.fontInterface - 2).sp,
                color = PrimaryText,
            )
            Icon(
                modifier = Modifier
                    .padding(start = 21.dp, end = 2.dp)
                    .size(22.dp, 22.dp),
                painter = painterResource(if (expanded) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                tint = PrimaryText,
                contentDescription = ""
            )
        }
        ExposedDropdownMenu(
            containerColor = Divider,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            menuList.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Image(
                                modifier = Modifier
                                    .padding(start = 5.dp, end = 10.dp)
                                    .size(22.dp, 22.dp),
                                painter = painterResource(option.imageResource),
                                contentDescription = ""
                            )
                            Text(option.title, fontSize = Settings.fontInterface.sp)
                        }
                    }, onClick = {
                        textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option.title)
                        textFieldMyPosition = index
                        expanded = false
                        onClickItem(index)
                    }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding, colors = MenuDefaults.itemColors(textColor = PrimaryText)
                )
            }
        }
    }
}

data class Tipicon(val imageResource: Int, val title: String)