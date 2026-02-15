package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import by.carkva_gazeta.malitounik.Settings.isNetworkAvailable
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.MalitounikTheme
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WidgetConfigRadioMaryia : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MalitounikTheme {
                DialogWidgetConfigRadioMaryia {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogWidgetConfigRadioMaryia(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.padie_maryia), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
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
                                .size(22.dp, 22.dp),
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
                                    Settings.vibrate()
                                    if (radioMaryiaListPosition != index) {
                                        radioMaryiaListPosition = index
                                        expandedSviaty = false
                                        k.edit {
                                            putInt("radioMaryiaListPosition", index)
                                        }
                                        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
                                                intent2.putExtra("action", ServiceRadyjoMaryia.STOP)
                                                ContextCompat.startForegroundService(context, intent2)
                                                delay(200)
                                                if (isNetworkAvailable(context)) {
                                                    val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
                                                    intent2.putExtra("action", ServiceRadyjoMaryia.PLAY_PAUSE)
                                                    ContextCompat.startForegroundService(context, intent2)
                                                } else {
                                                    val intent3 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
                                                    intent3.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    intent3.putExtra("checkInternet", true)
                                                    context.startActivity(intent3)
                                                }
                                            }
                                        }
                                    }
                                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding, colors = MenuDefaults.itemColors(textColor = PrimaryText)
                            )
                        }
                    }
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
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}