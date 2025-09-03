package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import by.carkva_gazeta.malitounik.Settings.isNetworkAvailable
import by.carkva_gazeta.malitounik.ui.theme.MalitounikTheme
import by.carkva_gazeta.malitounik.views.AllDestinations
import by.carkva_gazeta.malitounik.views.AppNavGraph
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Calendar

object Settings {
    const val TRANSPORT_ALL = 100
    const val TRANSPORT_WIFI = 101
    const val TRANSPORT_CELLULAR = 102
    const val GET_CALIANDAR_YEAR_MIN = 2024
    const val GET_CALIANDAR_YEAR_MAX = 2027
    const val NOTIFICATION_CHANNEL_ID_SABYTIE = "3003"
    const val NOTIFICATION_CHANNEL_ID_SVIATY = "2003"
    const val NOTIFICATION_CHANNEL_ID_RADIO_MARYIA = "4007"
    const val NOTIFICATION_SVIATY_NONE = 0
    const val NOTIFICATION_SVIATY_ONLY = 1
    const val NOTIFICATION_SVIATY_FULL = 2
    const val RESET_WIDGET_MUN = "reset_widget_mun"
    const val PEREVODSEMUXI = "1"
    const val PEREVODSINOIDAL = "2"
    const val PEREVODNADSAN = "3"
    const val PEREVODBOKUNA = "4"
    const val PEREVODCARNIAUSKI = "5"
    const val MODE_NIGHT_SYSTEM = 1
    const val MODE_NIGHT_NO = 2
    const val MODE_NIGHT_YES = 3
    const val MODE_NIGHT_AUTO = 4
    const val CHYTANNI_LITURGICHNYIA = 10
    const val CHYTANNI_MARANATA = 11
    const val CHYTANNI_BIBLIA = 12
    const val CHYTANNI_VYBRANAE = 13
    const val SORT_BY_ABC = 1
    const val SORT_BY_TIME = 2
    const val MENU_BOGASLUJBOVYIA = 100
    const val MENU_MALITVY = 101
    const val MENU_AKTOIX = 102
    const val MENU_MALITVY_PRYNAGODNYIA = 103
    const val MENU_VIACHERNIA = 104
    const val MENU_TRAPARY_KANDAKI_NIADZELNYIA = 105
    const val MENU_TRAPARY_KANDAKI_SHTODZENNYIA = 106
    const val MENU_MALITVY_PASLIA_PRYCHASCIA = 107
    const val MENU_TREBNIK = 108
    const val MENU_MINEIA_AGULNAIA = 109
    const val MENU_MINEIA_MESIACHNAIA_MOUNTH = 110
    const val MENU_MINEIA_MESIACHNAIA = 111
    const val MENU_TRYEDZ = 112
    const val MENU_TRYEDZ_POSNAIA = 113
    const val MENU_TRYEDZ_VIALIKAGA_TYDNIA = 114
    const val MENU_TRYEDZ_SVETLAGA_TYDNIA = 115
    const val MENU_TRYEDZ_KVETNAIA = 116
    const val MENU_TRYEDZ_POSNAIA_1 = 117
    const val MENU_TRYEDZ_POSNAIA_2 = 118
    const val MENU_TRYEDZ_POSNAIA_3 = 119
    const val MENU_TRYEDZ_POSNAIA_4 = 120
    const val MENU_TRYEDZ_POSNAIA_5 = 121
    const val MENU_TRYEDZ_POSNAIA_6 = 122
    const val MENU_CHASASLOU = 123
    const val MENU_AKAFIST = 124
    const val MENU_MALITVY_RUJANEC = 125
    const val MENU_MAE_NATATKI = 126
    const val MENU_LITURGIKON = 127
    const val CALAINDAR = 0
    const val PASHA = 1
    const val UNDER = 2
    var bibleTime = false
    var bibleTimeList = false
    var destinations = AllDestinations.KALIANDAR
    var caliandarPosition = -1
    var data = ArrayList<ArrayList<String>>()
    var fontInterface by mutableFloatStateOf(22F)
    val vibrate = longArrayOf(0, 1000, 700, 1000)
    var isProgressVisableRadyjoMaryia = mutableStateOf(false)
    val textFieldValueState = mutableStateOf("")
    val textFieldValueLatest = mutableStateOf("")
    val dzenNoch = mutableStateOf(false)

    fun dataCaliandar() {
        if (data.isEmpty()) {
            val gson = Gson()
            val type = TypeToken.getParameterized(
                ArrayList::class.java, TypeToken.getParameterized(
                    ArrayList::class.java, String::class.java
                ).type
            ).type
            val inputStream = MainActivity.applicationContext().resources.openRawResource(R.raw.caliandar)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            if (data.isEmpty()) {
                data.addAll(gson.fromJson(builder, type))
            }
        }
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context, typeTransport: Int = TRANSPORT_ALL): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        when (typeTransport) {
            TRANSPORT_CELLULAR -> {
                if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            }

            TRANSPORT_WIFI -> {
                if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
            }

            TRANSPORT_ALL -> {
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true

                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

                    else -> false
                }
            }
        }
        return false
    }

    private fun mkTime(year: Int, month: Int, day: Int, hour: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[year, month, day, hour, 0] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    private fun mkTimeDayPosition(year: Int, month: Int, day: Int): Int {
        for (i in data.indices) {
            if (year == data[i][3].toInt() && month == data[i][2].toInt() && day == data[i][1].toInt()) {
                return i
            }
        }
        return caliandarPosition
    }

    private fun createIntent(
        context: Context, title: String, extra: String, caliandarPosition: Int = Settings.caliandarPosition
    ): Intent {
        val intent = Intent(context, ReceiverBroad::class.java)
        intent.action = "by.carkva_gazeta.malitounik.sviaty"
        intent.putExtra("title", title)
        intent.putExtra("extra", extra)
        intent.putExtra("caliandarPosition", caliandarPosition)
        intent.`package` = context.packageName
        return intent
    }

    private fun mkTime(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[year, month, day, 0, 0] = 0
        return calendar.timeInMillis
    }

    fun createIntentSabytie(context: Context, title: String, data: String, time: String): Intent {
        val intent = Intent(context, ReceiverBroad::class.java)
        intent.action = "by.carkva_gazeta.malitounik.sviaty"
        intent.putExtra("title", title)
        intent.putExtra("sabytieSet", true)
        intent.putExtra("extra", "Падзея $data у $time")
        val dateN = data.split(".")
        for (i in Settings.data.indices) {
            if (dateN[2].toInt() == Settings.data[i][3].toInt() && dateN[1].toInt() - 1 == Settings.data[i][2].toInt() && dateN[0].toInt() == Settings.data[i][1].toInt()) {
                intent.putExtra("caliandarPosition", i)
                break
            }
        }
        val timeN = time.split(":")
        intent.putExtra("dataString", dateN[0] + dateN[1] + timeN[0] + timeN[1])
        return intent
    }

    private fun setAlarm(
        context: Context, timeAlarm: Long, pendingIntent: PendingIntent?, padzeia: Boolean = false
    ) {
        pendingIntent?.let {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (padzeia && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlarm, it)
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, timeAlarm, it)
                }
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlarm, it)
            }
        }
    }

    fun setNotifications(context: Context, notifications: Int) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        var intent: Intent
        var pIntent: PendingIntent?
        if (chin.getBoolean("WIDGET_MUN_ENABLED", false)) {
            val cw = Calendar.getInstance()
            val munAk = cw[Calendar.MONTH]
            val yearAk = cw[Calendar.YEAR]
            var resetWid = false
            intent = Intent(context, WidgetMun::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)
            if (pIntent != null) {
                cw.add(Calendar.DATE, 1)
            }
            pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or 0)
            setAlarm(context, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
            val thisAppWidget = ComponentName(context.packageName, context.packageName + ".Widget_mun")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            for (i in ids) {
                val munS = chin.getInt("WIDGET$i", munAk)
                val yearS = chin.getInt("WIDGETYEAR$i", yearAk)
                if (!(munS == munAk && yearS == yearAk)) resetWid = true
            }
            if (resetWid) {
                val reset = Intent(context, WidgetMun::class.java)
                reset.action = RESET_WIDGET_MUN
                val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                setAlarm(context, System.currentTimeMillis() + 120000L, pReset)
            }
        }
        if (chin.getBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false)) {
            val cw = Calendar.getInstance()
            intent = Intent(context, WidgetRadyjoMaryia::class.java)
            pIntent = PendingIntent.getBroadcast(context, 52, intent, PendingIntent.FLAG_IMMUTABLE or 0)
            setAlarm(
                context, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent
            )
        }
        val c = Calendar.getInstance()
        val padzeia = setListPadzeia(context)
        padzeia.forEach {
            if (it.sec != "-1") {
                val timerepit = it.paznic
                if (timerepit > c.timeInMillis) {
                    intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                    pIntent = PendingIntent.getBroadcast(
                        context, (timerepit / 100000).toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, timerepit, pIntent, true)
                }
            }
        }
        var year = c[Calendar.YEAR]
        var dataP: Int
        var monthP: Int
        val timeNotification = chin.getInt("timeNotification", 2) + 6
        for (i in 0..1) {
            year += i
            val a = year % 19
            val b = year % 4
            val cx = year % 7
            val k = year / 100
            val p = (13 + 8 * k) / 25
            val q = k / 4
            val m = (15 - p + k - q) % 30
            val n = (4 + k - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            if (notifications != 0) {
                if (c.timeInMillis < mkTime(year, monthP - 1, dataP - 1, 19)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, monthP - 1, dataP)
                    ) // Абавязковае
                    val code = "1$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, monthP - 1, dataP - 1, 19), pIntent)
                }
                if (c.timeInMillis < mkTime(year, monthP - 1, dataP, timeNotification)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, monthP - 1, dataP)
                    ) // Абавязковае
                    val code = "2$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, monthP - 1, dataP, timeNotification), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 0, 5, 19)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, 0, 6)
                    ) // Абавязковае
                    val code = "3$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 0, 5, 19), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 0, 6, timeNotification)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, 0, 6)
                    ) // Абавязковае
                    val code = "4$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 0, 6, timeNotification), pIntent)
                }
                val cet = Calendar.getInstance()
                cet[year, monthP - 1] = dataP - 1
                cet.add(Calendar.DATE, -7)
                if (c.timeInMillis < mkTime(
                        year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19
                    )
                ) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)
                    ) // Абавязковае
                    val code = "5$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(
                        context, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent
                    )
                }
                cet.add(Calendar.DATE, 1)
                if (c.timeInMillis < mkTime(
                        year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification
                    )
                ) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)
                    ) // Абавязковае
                    val code = "6$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(
                        context, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent
                    )
                }
                cet[year, monthP - 1] = dataP - 1
                cet.add(Calendar.DATE, +39)
                if (c.timeInMillis < mkTime(
                        year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19
                    )
                ) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)
                    ) // Абавязковае
                    val code = "7$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(
                        context, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent
                    )
                }
                cet.add(Calendar.DATE, 1)
                if (c.timeInMillis < mkTime(
                        year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification
                    )
                ) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)
                    ) // Абавязковае
                    val code = "8$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(
                        context, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent
                    )
                }
                cet[year, monthP - 1] = dataP - 1
                cet.add(Calendar.DATE, +49)
                if (c.timeInMillis < mkTime(
                        year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19
                    )
                ) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)
                    ) // Абавязковае
                    val code = "9$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(
                        context, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent
                    )
                }
                cet.add(Calendar.DATE, 1)
                if (c.timeInMillis < mkTime(
                        year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification
                    )
                ) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)
                    ) // Абавязковае
                    val code = "10$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(
                        context, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent
                    )
                }
                if (c.timeInMillis < mkTime(year, 2, 24, 19)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, 2, 25)
                    ) // Абавязковае
                    val code = "11$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 2, 24, 19), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 2, 25, timeNotification)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, 2, 25)
                    ) // Абавязковае
                    val code = "12$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 2, 25, timeNotification), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 7, 14, 19)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, 7, 15)
                    ) // Абавязковае
                    val code = "13$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 7, 14, 19), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 7, 15, timeNotification)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, 7, 15)
                    ) // Абавязковае
                    val code = "14$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 7, 15, timeNotification), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 11, 24, 19)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, 11, 25)
                    ) // Абавязковае
                    val code = "15$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 11, 24, 19), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 11, 25, timeNotification)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, 11, 25)
                    ) // Абавязковае
                    val code = "16$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 11, 25, timeNotification), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 5, 28, 19)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv3), mkTimeDayPosition(year, 5, 29)
                    ) // Абавязковае
                    val code = "17$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 5, 28, 19), pIntent)
                }
                if (c.timeInMillis < mkTime(year, 5, 29, timeNotification)) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv4), mkTimeDayPosition(year, 5, 29)
                    ) // Абавязковае
                    val code = "18$year"
                    pIntent = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    setAlarm(context, mkTime(year, 5, 29, timeNotification), pIntent)
                }
                if (notifications == 2) {
                    if (c.timeInMillis < mkTime(year, 1, 1, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 1, 2))
                        val code = "19$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 1, 1, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 1, 2, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 1, 2))
                        val code = "20$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 1, 2, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 7, 5, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 7, 6))
                        val code = "21$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 7, 5, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 7, 6, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 7, 6))
                        val code = "22$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 7, 6, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 8, 7, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 8, 8))
                        val code = "23$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 8, 7, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 8, 8, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 8, 8))
                        val code = "24$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 8, 8, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 8, 13, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 8, 14))
                        val code = "25$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 8, 13, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 8, 14, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 8, 14))
                        val code = "26$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 8, 14, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 10, 20, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 10, 21))
                        val code = "27$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 10, 20, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 10, 21, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 10, 21))
                        val code = "28$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 10, 21, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 11, 31, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year + 1, 0, 1))
                        val code = "29$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 11, 31, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 0, 1, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year + 1, 0, 1))
                        val code = "30$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 0, 1, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 5, 23, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 5, 24))
                        val code = "31$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 5, 23, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 5, 24, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 5, 24))
                        val code = "32$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 5, 24, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 7, 28, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 7, 29))
                        val code = "33$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 7, 28, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 7, 29, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 7, 29))
                        val code = "34$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 7, 29, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 8, 30, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv1), mkTimeDayPosition(year, 9, 1))
                        val code = "35$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 8, 30, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 9, 1, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv2), mkTimeDayPosition(year, 9, 1))
                        val code = "36$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0)
                        setAlarm(context, mkTime(year, 9, 1, timeNotification), pIntent)
                    }
                }
            }
            if (notifications == 1 || notifications == 0) {
                var code: String
                if (notifications != 1) {
                    intent = createIntent(
                        context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "1$year"
                    var pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "2$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "3$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "4$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "5$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "6$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "7$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "8$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "9$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "10$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "11$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "12$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "13$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "14$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "15$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "16$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv1)
                    ) // Абавязковае
                    code = "17$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                    intent = createIntent(
                        context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv2)
                    ) // Абавязковае
                    code = "18$year"
                    pIntent1 = PendingIntent.getBroadcast(
                        context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                    )
                    am.cancel(pIntent1)
                }
                intent = createIntent(
                    context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv1)
                )
                code = "19$year"
                var pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv2)
                )
                code = "20$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv1)
                )
                code = "21$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv2)
                )
                code = "22$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv1)
                )
                code = "23$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv2)
                )
                code = "24$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv1)
                )
                code = "25$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv2)
                )
                code = "26$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv1)
                )
                code = "27$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv2)
                )
                code = "28$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv1)
                )
                code = "29$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv2)
                )
                code = "30$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv1)
                )
                code = "31$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv2)
                )
                code = "32$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv1)
                )
                code = "33$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv2)
                )
                code = "34$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv1)
                )
                code = "35$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
                intent = createIntent(
                    context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv2)
                )
                code = "36$year"
                pIntent2 = PendingIntent.getBroadcast(
                    context, code.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
                )
                am.cancel(pIntent2)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationChannel(context: Context, channelID: String = NOTIFICATION_CHANNEL_ID_SVIATY) {
        val name = if (channelID == NOTIFICATION_CHANNEL_ID_SVIATY) context.getString(R.string.sviaty)
        else context.getString(R.string.sabytie)
        val channel = NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.description = name
        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.enableLights(true)
        channel.lightColor = Color.RED
        val att = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att)
        channel.enableVibration(true)
        channel.vibrationPattern = vibrate
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        notificationManager.deleteNotificationChannel("by.carkva-gazeta")
        notificationManager.deleteNotificationChannel("3000")
        notificationManager.deleteNotificationChannel("2000")
        notificationManager.deleteNotificationChannel("3001")
        notificationManager.deleteNotificationChannel("2001")
        notificationManager.deleteNotificationChannel("3002")
        notificationManager.deleteNotificationChannel("2002")
    }
}

enum class SystemNavigation {
    THREE_BUTTON, TWO_BUTTON, GESTURE;

    companion object {
        fun create(context: Context) = entries.getOrNull(
            android.provider.Settings.Secure.getInt(context.contentResolver, "navigation_mode", -1)
        )
    }
}

@Composable
fun DialogSztoHovaha(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.chto_novaga_title).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                val content = openAssetsResources(LocalContext.current, "a_szto_novaha.txt")
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = content, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun getFontInterface(context: Context): Float {
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    return k.getFloat("fontSizeInterface", 20f)
}

class MainActivity : ComponentActivity(), SensorEventListener, ServiceRadyjoMaryia.ServiceRadyjoMaryiaListener {
    private var backPressed: Long = 0
    private var isGesture = false
    var isConnectServise = false
    var mRadyjoMaryiaService: ServiceRadyjoMaryia? = null
    val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ServiceRadyjoMaryia.ServiceRadyjoMaryiaBinder
            mRadyjoMaryiaService = binder.getService()
            mRadyjoMaryiaService?.setServiceRadyjoMaryiaListener(this@MainActivity)
            isConnectServise = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnectServise = false
            mRadyjoMaryiaService = null
        }
    }

    override fun errorRadioMaria() {
        val view = Toast.makeText(this, getString(R.string.error_ch2), Toast.LENGTH_LONG)
        view.show()
    }

    override fun setTitleRadioMaryia(title: String) {
        ServiceRadyjoMaryia.titleRadyjoMaryia = title
    }

    override fun unBinding() {
        if (isConnectServise) {
            unbindService(mConnection)
        }
        isConnectServise = false
        Settings.isProgressVisableRadyjoMaryia.value = false
    }

    override fun playingRadioMaria(isPlayingRadioMaria: Boolean) {
        ServiceRadyjoMaryia.isPlayingRadyjoMaryia = isPlayingRadioMaria
    }

    override fun playingRadioMariaStateReady() {
        ServiceRadyjoMaryia.isPlayingRadyjoMaryia = true
        Settings.isProgressVisableRadyjoMaryia.value = false
        setTitleRadioMaryia(ServiceRadyjoMaryia.titleRadyjoMaryia)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) {
            setlightSensor()
        }
        if (k.getBoolean("power", false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        val start = k.getString("navigate", AllDestinations.KALIANDAR) ?: AllDestinations.KALIANDAR
        AppNavGraphState.bibleItem = start.contains("Biblia", ignoreCase = true) == true
        AppNavGraphState.biblijatekaItem = start.contains("Biblijateka", ignoreCase = true) == true
        AppNavGraphState.piesnyItem = start.contains("Piesny", ignoreCase = true) == true
        AppNavGraphState.underItem = start.contains("Under", ignoreCase = true) == true
        Settings.fontInterface = getFontInterface(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
        if (savedInstanceState == null) {
            val modeNight = k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM)
            val configuration = Resources.getSystem().configuration
            Settings.dzenNoch.value = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            if (modeNight == Settings.MODE_NIGHT_NO) Settings.dzenNoch.value = false
            if (modeNight == Settings.MODE_NIGHT_YES) Settings.dzenNoch.value = true
            if (modeNight == Settings.MODE_NIGHT_AUTO) Settings.dzenNoch.value = k.getBoolean("dzenNoch", false)
        }
        Settings.dataCaliandar()
        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            MalitounikTheme(darkTheme = Settings.dzenNoch.value) {
                AppNavGraphState.getCytata(this, savedInstanceState == null)
                AppNavGraph()
                var dialogSztoHovahaVisable by rememberSaveable { mutableStateOf(checkASztoNovagaMD5Sum()) }
                if (dialogSztoHovahaVisable) {
                    DialogSztoHovaha {
                        dialogSztoHovahaVisable = false
                        k.edit { putInt("chtoNavaha", BuildConfig.VERSION_CODE) }
                    }
                }
                SideEffect {
                    isGesture = when (SystemNavigation.create(this)) {
                        SystemNavigation.THREE_BUTTON, SystemNavigation.TWO_BUTTON -> false

                        SystemNavigation.GESTURE -> true

                        else -> false
                    }
                }
            }
        }
        if (k.getBoolean("admin", false)) {
            CoroutineScope(Dispatchers.IO).launch {
                if (isNetworkAvailable(this@MainActivity)) {
                    val dir = File("$filesDir/cache")
                    if (!dir.exists()) dir.mkdir()
                    val localFile = File("$filesDir/cache/cache.txt")
                    referens.child("/admin/log.txt").getFile(localFile).addOnFailureListener {
                        val mes = Toast.makeText(this@MainActivity, getString(R.string.error), Toast.LENGTH_SHORT)
                        mes.show()
                    }.await()
                    val log = localFile.readText()
                    if (log != "") {
                        withContext(Dispatchers.Main) {
                            val mes = Toast.makeText(this@MainActivity, getString(R.string.check_update_resourse), Toast.LENGTH_SHORT)
                            mes.show()
                        }
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            cacheDir?.listFiles()?.forEach {
                it?.deleteRecursively()
            }
            if (AppNavGraphState.setAlarm) {
                val notify = k.getInt("notification", Settings.NOTIFICATION_SVIATY_FULL)
                Settings.setNotifications(this@MainActivity, notify)
                if (isNetworkAvailable(this@MainActivity)) {
                    downloadOpisanieSviat(this@MainActivity)
                }
                AppNavGraphState.setAlarm = false
            }
        }
    }

    fun checkmodulesAdmin(): Boolean {
        val muduls = SplitInstallManagerFactory.create(this).installedModules
        for (mod in muduls) {
            if (mod == "admin") {
                return true
            }
        }
        return false
    }

    fun checkASztoNovagaMD5Sum(): Boolean {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(openAssetsResources(this, "a_szto_novaha.txt").toByteArray())
        val digest = messageDigest.digest()
        val bigInt = BigInteger(1, digest)
        val md5Hex = StringBuilder(bigInt.toString(16))
        while (md5Hex.length < 32) {
            md5Hex.insert(0, "0")
        }
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        val checkMD5 = k.getString("chtoNavahaMD5", "0")
        k.edit {
            putString("chtoNavahaMD5", md5Hex.toString())
        }
        return checkMD5 != md5Hex.toString()
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        FirebaseApp.initializeApp(context)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            sensorChangeDzenNoch(sensorEvent.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun sensorChangeDzenNoch(sensorValue: Float) {
        when {
            sensorValue <= 4f -> {
                if (System.currentTimeMillis() - AppNavGraphState.autoDzenNochTime >= 5000) {
                    Settings.dzenNoch.value = true
                    AppNavGraphState.autoDzenNochTime = System.currentTimeMillis()
                }
            }

            sensorValue >= 21f -> {
                if (System.currentTimeMillis() - AppNavGraphState.autoDzenNochTime >= 5000) {
                    Settings.dzenNoch.value = false
                    AppNavGraphState.autoDzenNochTime = System.currentTimeMillis()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isConnectServise) {
            unbindService(mConnection)
        }
        mRadyjoMaryiaService = null
        isConnectServise = false
    }

    override fun onStart() {
        super.onStart()
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
            val intent = Intent(this, ServiceRadyjoMaryia::class.java)
            bindService(intent, mConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) {
            setlightSensor()
        }
    }

    private fun onBack() {
        if (isGesture || backPressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true)
            val k = getSharedPreferences("biblia", MODE_PRIVATE)
            k.edit {
                putBoolean("setAlarm", true)
                if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) {
                    putBoolean("dzenNoch", Settings.dzenNoch.value)
                }
            }
            AppNavGraphState.bibleItem = false
            AppNavGraphState.piesnyItem = false
            AppNavGraphState.biblijatekaItem = false
            AppNavGraphState.underItem = false
            AppNavGraphState.scrollValueList.clear()
            AppNavGraphState.itemsValue.clear()
            AppNavGraphState.setAlarm = true
            AppNavGraphState.appUpdate = true
            finish()
        } else {
            backPressed = System.currentTimeMillis()
            val mes = Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT)
            mes.show()
        }
    }

    fun setlightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun removelightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.unregisterListener(this, lightSensor)
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null
        private val storage: FirebaseStorage
            get() = Firebase.storage
        val referens: StorageReference
            get() = storage.reference

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}