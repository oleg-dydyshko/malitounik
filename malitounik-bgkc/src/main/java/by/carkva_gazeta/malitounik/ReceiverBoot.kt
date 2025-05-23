package by.carkva_gazeta.malitounik

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceiverBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.QUICKBOOT_POWERON" || intent.action == "com.htc.intent.action.QUICKBOOT_POWERON") {
            CoroutineScope(Dispatchers.IO).launch {
                val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val notify = chin.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)
                Settings.setNotifications(context, notify)
            }
        }
    }
}