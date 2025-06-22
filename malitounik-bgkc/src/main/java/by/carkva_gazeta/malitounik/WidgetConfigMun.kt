package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import by.carkva_gazeta.malitounik.ui.theme.MalitounikTheme


class WidgetConfigMun : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_OK)
        setContent {
            MalitounikTheme {
                DialogWidgetConfig(
                    isWidgetMun = true,
                    onConfirmRequest = {
                        val resultValue = Intent(this, WidgetMun::class.java)
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