package by.carkva_gazeta.malitounik2

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
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
import androidx.core.content.edit
import by.carkva_gazeta.malitounik2.ui.theme.MalitounikTheme


class WidgetConfig : ComponentActivity() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        setResult(RESULT_OK)
        setContent {
            MalitounikTheme {
                DialogWidgetConfig(
                    isWidgetMun = false,
                    widgetID = widgetID,
                    onConfirmRequest = {
                        val resultValue = Intent(this, Widget::class.java)
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
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
    widgetID: Int,
    onConfirmRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var modeNight by remember {
        mutableIntStateOf(
            if (isWidgetMun) k.getInt("mode_night_widget_mun$widgetID", Settings.MODE_NIGHT_SYSTEM)
            else k.getInt("mode_night_widget_day$widgetID", Settings.MODE_NIGHT_SYSTEM)
        )
    }
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.contrast), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.vygliad2))
        },
        text = {
            Column(Modifier.selectableGroup())
            {
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
                            modeNight = Settings.MODE_NIGHT_SYSTEM
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_SYSTEM,
                        onClick = {
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
                            modeNight = Settings.MODE_NIGHT_NO
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_NO,
                        onClick = {
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
                            modeNight = Settings.MODE_NIGHT_YES
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = modeNight == Settings.MODE_NIGHT_YES,
                        onClick = {
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
            if (!isWidgetMun) {
                val intent = Intent(context, Widget::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
                intent.putExtra("actionEndLoad", true)
                context.sendBroadcast(intent)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cansel), fontSize = Settings.fontInterface.sp)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    k.edit {
                        if (isWidgetMun) putInt("mode_night_widget_mun$widgetID", modeNight)
                        else putInt("mode_night_widget_day$widgetID", modeNight)
                    }
                    onConfirmRequest()
                }
            ) {
                Text(stringResource(R.string.save_sabytie), fontSize = Settings.fontInterface.sp)
            }
        }
    )
}