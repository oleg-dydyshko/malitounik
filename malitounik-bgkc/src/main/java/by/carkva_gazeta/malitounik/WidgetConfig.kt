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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import by.carkva_gazeta.malitounik.ui.theme.MalitounikTheme


class WidgetConfig : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_OK)
        setContent {
            MalitounikTheme {
                DialogWidgetConfig(
                    isWidgetMun = false,
                    onConfirmRequest = {
                        val resultValue = Intent(this, Widget::class.java)
                        setResult(RESULT_OK, resultValue)
                        sendBroadcast(resultValue)
                        finish()
                    }
                ) {
                    finish()
                }
            }
        }
    }
}

@Composable
fun DialogWidgetConfig(
    isWidgetMun: Boolean,
    onConfirmRequest: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var modeNight by remember {
        mutableIntStateOf(
            if (isWidgetMun) k.getInt("mode_night_widget_mun", Settings.MODE_NIGHT_SYSTEM)
            else k.getInt("mode_night_widget_day", Settings.MODE_NIGHT_SYSTEM)
        )
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.vygliad2).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    Modifier
                        .selectableGroup()
                        .padding(10.dp)
                ) {
                    Text(
                        stringResource(R.string.dzen_noch),
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Settings.vibrate()
                                modeNight = Settings.MODE_NIGHT_SYSTEM
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = modeNight == Settings.MODE_NIGHT_SYSTEM,
                            onClick = {
                                Settings.vibrate()
                                modeNight = Settings.MODE_NIGHT_SYSTEM
                            }
                        )
                        Text(
                            stringResource(R.string.system),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Settings.vibrate()
                                modeNight = Settings.MODE_NIGHT_NO
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = modeNight == Settings.MODE_NIGHT_NO,
                            onClick = {
                                Settings.vibrate()
                                modeNight = Settings.MODE_NIGHT_NO
                            }
                        )
                        Text(
                            stringResource(R.string.day),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Settings.vibrate()
                                modeNight = Settings.MODE_NIGHT_YES
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = modeNight == Settings.MODE_NIGHT_YES,
                            onClick = {
                                Settings.vibrate()
                                modeNight = Settings.MODE_NIGHT_YES
                            }
                        )
                        Text(
                            stringResource(R.string.widget_day_d_n),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
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
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            k.edit {
                                if (isWidgetMun) putInt("mode_night_widget_mun", modeNight)
                                else putInt("mode_night_widget_day", modeNight)
                            }
                            onConfirmRequest()
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                        Text(stringResource(R.string.save_sabytie), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}