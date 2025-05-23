package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.util.Calendar

class ReceiverBroad : BroadcastReceiver() {
    private var sabytieSet = false
    override fun onReceive(ctx: Context, intent: Intent) {
        val g = Calendar.getInstance()
        val dayofyear = g[Calendar.DAY_OF_YEAR]
        val year = g[Calendar.YEAR]
        val sabytie = intent.getBooleanExtra("sabytieSet", false)
        if (sabytie) {
            val idString = intent.extras?.getString("dataString", dayofyear.toString() + g[Calendar.MONTH].toString() + g[Calendar.HOUR_OF_DAY] + g[Calendar.MINUTE]) ?: "205"
            val newId = idString.toInt()
            id = if (newId <= id) id + 1
            else newId
            sabytieSet = true
        }
        sendNotif(ctx, intent.getStringExtra("title"), intent.getStringExtra("extra"), intent.getIntExtra("dayofyear", dayofyear), intent.getIntExtra("year", year))
    }

    private fun sendNotif(context: Context, sviata: String?, name: String?, dayofyear: Int, year: Int) {
        if (sviata == null || name == null) return
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.action = id.toString()
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        notificationIntent.putExtra("data", dayofyear)
        notificationIntent.putExtra("year", year)
        notificationIntent.putExtra("sabytie", true)
        if (sabytieSet) {
            notificationIntent.putExtra("sabytieView", true)
            notificationIntent.putExtra("sabytieTitle", sviata)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val contentIntent = PendingIntent.getActivity(context, id, notificationIntent, flags)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var bigIcon = R.drawable.calendar_full
        if (!sabytieSet) {
            bigIcon = R.drawable.krest
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (sabytieSet) {
                Settings.notificationChannel(context, Settings.NOTIFICATION_CHANNEL_ID_SABYTIE)
            } else {
                Settings.notificationChannel(context)
            }
        } else {
            var sound = chin.getInt("soundnotification", 0)
            if (!sabytieSet) sound = 0
            uri = when (sound) {
                1 -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                2 -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                3 -> chin.getString("soundURI", "")?.toUri()
                else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
        }
        val builder = if (sabytieSet) {
            NotificationCompat.Builder(context, Settings.NOTIFICATION_CHANNEL_ID_SABYTIE)
        } else {
            NotificationCompat.Builder(context, Settings.NOTIFICATION_CHANNEL_ID_SVIATY)
        }
        builder.setContentIntent(contentIntent).setWhen(System.currentTimeMillis()).setShowWhen(true).setSmallIcon(R.drawable.krest).setLargeIcon(BitmapFactory.decodeResource(context.resources, bigIcon)).setAutoCancel(true).setPriority(NotificationManagerCompat.IMPORTANCE_HIGH).setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000).setContentTitle(name).setContentText(sviata)
        if (sabytieSet) builder.setStyle(NotificationCompat.BigTextStyle().bigText(sviata))
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || chin.getInt("guk", 1) == 1) builder.setSound(uri)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || chin.getInt("vibra", 1) == 1) builder.setVibrate(Settings.vibrate)
        val notification = builder.build()
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(id, notification)
        }
    }

    companion object {
        private var id = 205
    }
}