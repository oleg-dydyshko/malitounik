package by.carkva_gazeta.malitounik.admin

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar

@Composable
fun DialogPairlinyEdit(
    day: Int,
    mun: Int,
    onDismiss: () -> Unit,
) {
    val context = LocalActivity.current as MainActivity
    var result by remember { mutableStateOf("") }
    var isProgressVisable by remember { mutableStateOf(false) }
    val piarlin = remember { ArrayList<ArrayList<String>>() }
    var time by remember { mutableLongStateOf(0) }
    var position by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        val localFile = File("${context.filesDir}/piarliny.json")
        if (localFile.exists()) {
            try {
                val builder = localFile.readText()
                val gson = Gson()
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                piarlin.addAll(gson.fromJson(builder, type))
            } catch (_: Throwable) {
            }
        }
        val cal = GregorianCalendar()
        for (i in piarlin.indices) {
            cal.timeInMillis = piarlin[i][0].toLong() * 1000
            if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                result = piarlin[i][1]
                time = piarlin[i][0].toLong() * 1000
                position = i
                break
            }
        }
    }
    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.piarliny2, day, stringArrayResource(R.array.meciac_smoll)[mun - 1]).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                if (isProgressVisable) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Column(
                    modifier = Modifier
                        .weight(1f, false)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    TextField(
                        textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                        placeholder = { Text(stringResource(R.string.piarliny), fontSize = Settings.fontInterface.sp) },
                        value = result,
                        onValueChange = {
                            result = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (result.isNotEmpty()) {
                                if (time == 0L) {
                                    val gc = GregorianCalendar(2020, Settings.data[Settings.caliandarPosition][2].toInt(), Settings.data[Settings.caliandarPosition][1].toInt())
                                    time = gc.timeInMillis / 1000
                                    val resultArray2 = ArrayList<String>()
                                    resultArray2.add(time.toString())
                                    resultArray2.add(result)
                                    piarlin.add(resultArray2)
                                } else {
                                    piarlin[position][1] = result
                                }
                                sendPostRequest(context, piarlin) {
                                    isProgressVisable = it
                                    if (!isProgressVisable) onDismiss()
                                }
                            } else {
                                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                            }
                        })
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
                    TextButton(
                        onClick = {
                            if (result.isNotEmpty()) {
                                if (time == 0L) {
                                    val gc = GregorianCalendar(2020, Settings.data[Settings.caliandarPosition][2].toInt(), Settings.data[Settings.caliandarPosition][1].toInt())
                                    time = gc.timeInMillis / 1000
                                    val resultArray2 = ArrayList<String>()
                                    resultArray2.add(time.toString())
                                    resultArray2.add(result)
                                    piarlin.add(resultArray2)
                                } else {
                                    piarlin[position][1] = result
                                }
                                sendPostRequest(context, piarlin) {
                                    isProgressVisable = it
                                    if (!isProgressVisable) onDismiss()
                                }
                            } else {
                                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                            }
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.save), contentDescription = "")
                        Text(stringResource(R.string.save_sabytie), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun sendPostRequest(context: Context, resultArray: ArrayList<ArrayList<String>>, isLoad: (Boolean) -> Unit) {
    if (Settings.isNetworkAvailable(context)) {
        CoroutineScope(Dispatchers.Main).launch {
            isLoad(true)
            try {
                val gson = Gson()
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                val piarliny = gson.toJson(resultArray, type)
                val localFile = File("${context.filesDir}/cache/cache.txt")
                localFile.writer().use {
                    it.write(piarliny)
                }
                Malitounik.referens.child("/chytanne/piarliny.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                }.await()
            } catch (_: Throwable) {
                Toast.makeText(context, context.getString(R.string.error_ch), Toast.LENGTH_SHORT).show()
            }
            isLoad(false)
        }
    } else {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}