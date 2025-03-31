package by.carkva_gazeta.malitounik2

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class WidgetConfigMun : ComponentActivity() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        setResult(RESULT_OK)
        setContent {
            DialogWidgetConfig(
                isWidgetMun = true,
                widgetID = widgetID,
                onConfirmRequest = {
                    val resultValue = Intent(this, WidgetMun::class.java)
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