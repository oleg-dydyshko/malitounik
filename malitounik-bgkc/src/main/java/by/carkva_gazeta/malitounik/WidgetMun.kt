package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.compose
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import by.carkva_gazeta.malitounik.UpdateDataClickActionCallback.Companion.getClickTypeActionParameterKey
import by.carkva_gazeta.malitounik.ui.theme.BackgroundDark
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import by.carkva_gazeta.malitounik.ui.theme.TitleCalendarMounth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar
import java.util.GregorianCalendar

class CaliandarWidgetMun : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                if (Settings.data.isEmpty()) {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(
                        ArrayList::class.java, TypeToken.getParameterized(
                            ArrayList::class.java, String::class.java
                        ).type
                    ).type
                    val inputStream = context.resources.openRawResource(R.raw.caliandar)
                    val isr = InputStreamReader(inputStream)
                    val reader = BufferedReader(isr)
                    val builder = reader.use {
                        it.readText()
                    }
                    if (Settings.data.isEmpty()) {
                        Settings.data.addAll(gson.fromJson(builder, type))
                    }
                }
                CalendarMun(context)
            }
        }
    }
}

class UpdateDataClickActionCallback : ActionCallback {
    companion object {
        const val TYPE_PLUS = 1
        const val TYPE_MINUS = 2

        fun getClickTypeActionParameterKey(): ActionParameters.Key<Int> {
            return ActionParameters.Key("position")
        }
    }

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val clickType = requireNotNull(parameters[getClickTypeActionParameterKey()])
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            var position = it[intPreferencesKey("position")] ?: Settings.caliandarPosition
            when (clickType) {
                TYPE_PLUS -> {
                    position = getDataKaliandar(1, position)
                }

                TYPE_MINUS -> {
                    position = getDataKaliandar(-1, position)
                }
            }
            it.toMutablePreferences().apply {
                this[intPreferencesKey("position")] = position
            }
        }
        val reset = Intent(context, WidgetMun::class.java)
        reset.action = Settings.RESET_WIDGET_MUN
        val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pReset)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
        }
        CaliandarWidgetMun().update(context, glanceId)
    }
}

private fun getImageActionCallback(clickType: Int): Action {
    return actionRunCallback<UpdateDataClickActionCallback>(
        actionParametersOf(
            getClickTypeActionParameterKey() to clickType
        )
    )
}

private fun findDefaultCaliandarMun(): Int {
    var caliandarPosition = Settings.caliandarPosition
    val calendar = Calendar.getInstance()
    for (i in Settings.data.indices) {
        if (1 == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
            caliandarPosition = i
            break
        }
    }
    return caliandarPosition
}

private fun getDataKaliandar(date: Int, position: Int): Int {
    val oldData = Settings.data[position]
    var newPosition = position
    val calendar = if (date == 0) Calendar.getInstance() as GregorianCalendar
    else GregorianCalendar(oldData[3].toInt(), oldData[2].toInt(), oldData[1].toInt())
    calendar.add(Calendar.MONTH, date)
    for (i in 0 until Settings.data.size) {
        if ((calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt())) {
            newPosition = Settings.data[i][25].toInt()
            break
        }
    }
    return newPosition
}

@Composable
fun CalendarMun(context: Context) {
    val prefs = currentState<Preferences>()
    val position = prefs[intPreferencesKey("position")] ?: findDefaultCaliandarMun()
    val dzenNoch = prefs[booleanPreferencesKey("dzenNoch")] == true
    val data = Settings.data
    val mun = data[position][2].toInt()
    val year = data[position][3].toInt()
    val c = Calendar.getInstance()
    val munTudey = mun == c[Calendar.MONTH] && year == c[Calendar.YEAR]
    val calendarFull = GregorianCalendar(year, mun, 1)
    val wik = calendarFull[Calendar.DAY_OF_WEEK]
    val munAll = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendarFull.add(Calendar.MONTH, -1)
    val oldMunAktual = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
    var oldDay = oldMunAktual - wik + 1
    var day: String
    var i = 0
    var newDay = 0
    var end = 42
    if (42 - (munAll + wik) >= 6) {
        end -= 7
    }
    if (munAll + wik == 29) {
        end -= 7
    }
    val monthName = context.resources.getStringArray(R.array.meciac2)
    var e = 1
    val destinationKey = ActionParameters.Key<Boolean>("widget_mun")
    val destinationValue = ActionParameters.Key<Int>("position")
    Column(modifier = GlanceModifier.background(if (dzenNoch) BackgroundDark else PrimaryTextBlack).padding(15.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (!(Settings.GET_CALIANDAR_YEAR_MIN == year && mun == Calendar.JANUARY)) {
                Box(modifier = GlanceModifier.padding(10.dp).clickable(getImageActionCallback(UpdateDataClickActionCallback.TYPE_MINUS))) {
                    Image(
                        modifier = GlanceModifier.size(24.dp, 24.dp), provider = ImageProvider(R.drawable.levo_catedra_blak_31), contentDescription = "", colorFilter = ColorFilter.tint(if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText))
                    )
                }
            }
            Text(
                text = if (c[Calendar.YEAR] == year) monthName[mun] else monthName[mun].plus(", ").plus(year),
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = if (munTudey) FontWeight.Bold else FontWeight.Normal)
            )
            if (!(Settings.GET_CALIANDAR_YEAR_MAX == year && mun == Calendar.DECEMBER)) {
                Box(modifier = GlanceModifier.padding(10.dp).clickable(getImageActionCallback(UpdateDataClickActionCallback.TYPE_PLUS))) {
                    Image(
                        modifier = GlanceModifier.size(24.dp, 24.dp), provider = ImageProvider(R.drawable.pravo_catedra_blak_31), contentDescription = "", colorFilter = ColorFilter.tint(if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText))
                    )
                }
            }
        }
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = context.getString(R.string.ndz),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(Primary),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Text(
                text = context.getString(R.string.pn),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(TitleCalendarMounth),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Text(
                text = context.getString(R.string.au),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(TitleCalendarMounth),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Text(
                text = context.getString(R.string.sp),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(TitleCalendarMounth),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Text(
                text = context.getString(R.string.ch),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(TitleCalendarMounth),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Text(
                text = context.getString(R.string.pt),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(TitleCalendarMounth),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Text(
                text = context.getString(R.string.sb),
                modifier = GlanceModifier
                    .defaultWeight()
                    .background(TitleCalendarMounth),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
        }
        (1..end / 7).forEach { _ ->
            Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight(), verticalAlignment = Alignment.CenterVertically) {
                (1..7).forEach { _ ->
                    if (e < wik) {
                        oldDay++
                        day = "start"
                    } else if (e < munAll + wik) {
                        i++
                        day = i.toString()
                    } else {
                        newDay++
                        day = "end"
                        i = 0
                    }
                    when (day) {
                        "start" -> {
                            val fon = if (e == 1) BezPosta
                            else Divider
                            Column(
                                modifier = GlanceModifier
                                    .fillMaxHeight()
                                    .defaultWeight()
                                    .background(fon),
                                verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    oldDay.toString(),
                                    style = TextStyle(color = ColorProvider(SecondaryText, SecondaryText), fontSize = 20.sp, textAlign = TextAlign.Center)
                                )
                            }
                        }

                        "end" -> {
                            Column(
                                modifier = GlanceModifier
                                    .fillMaxHeight()
                                    .defaultWeight()
                                    .background(Divider),
                                verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    newDay.toString(),
                                    style = TextStyle(color = ColorProvider(SecondaryText, SecondaryText), fontSize = 20.sp, textAlign = TextAlign.Center)
                                )
                            }
                        }

                        else -> {
                            val bold =
                                if (data[position + i - 1][4].contains("<font color=#d00505><strong>") || data[position + i - 1][5].toInt() == 1 || data[position + i - 1][5].toInt() == 3) FontWeight.Bold
                                else FontWeight.Normal
                            val color =
                                if (data[position + i - 1][5].toInt() == 1 || data[position + i - 1][5].toInt() == 2) Primary
                                else if (data[position + i - 1][5].toInt() == 3 || data[position + i - 1][7].toInt() == 1) BezPosta
                                else if (data[position + i - 1][7].toInt() == 2) Post
                                else if (data[position + i - 1][7].toInt() == 3) StrogiPost
                                else Divider
                            val color2 =
                                if (data[position + i - 1][5].toInt() == 1 || data[position + i - 1][5].toInt() == 2 || data[position + i - 1][7].toInt() == 3) PrimaryTextBlack
                                else PrimaryText
                            val clickPos = position + i - 1
                            Column(
                                modifier = GlanceModifier
                                    .fillMaxHeight()
                                    .defaultWeight()
                                    .clickable(actionStartActivity<MainActivity>(actionParametersOf(destinationKey to true, destinationValue to clickPos)))
                                    .background(color),
                                verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    Box(modifier = GlanceModifier.padding(2.dp).background(color2)) {
                                        Text(
                                            day,
                                            modifier = GlanceModifier.background(color).padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = TextStyle(color = ColorProvider(color2, color2), fontSize = 20.sp, textAlign = TextAlign.Center, fontWeight = bold)
                                        )
                                    }
                                } else {
                                    Text(
                                        day,
                                        style = TextStyle(color = ColorProvider(color2, color2), fontSize = 20.sp, textAlign = TextAlign.Center, fontWeight = bold)
                                    )
                                }
                            }
                        }
                    }
                    e++
                }
            }
        }
    }
}

class WidgetMun : GlanceAppWidgetReceiver() {
    private val munPlus = "mun_plus"
    private val munMinus = "mun_minus"

    override val glanceAppWidget = CaliandarWidgetMun()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CoroutineScope(Dispatchers.Main).launch {
            val manager = GlanceAppWidgetManager(context)
            val widget = CaliandarWidgetMun()
            val glanceIds = manager.getGlanceIds(widget.javaClass)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) {
                    it[booleanPreferencesKey("dzenNoch")] = getBaseDzenNoch(context)
                    it[intPreferencesKey("position")] = getDataKaliandar(0, Settings.caliandarPosition)
                }
                widget.update(context, glanceId)
            }
            val intent = Intent(context, WidgetMun::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
        }
    }

    private fun getBaseDzenNoch(context: Context): Boolean {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val modeNight = k.getInt("mode_night_widget_mun", Settings.MODE_NIGHT_SYSTEM)
        var dzenNoch = false
        when (modeNight) {
            Settings.MODE_NIGHT_SYSTEM -> {
                val configuration = Resources.getSystem().configuration
                dzenNoch = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            }

            Settings.MODE_NIGHT_YES -> {
                dzenNoch = true
            }

            Settings.MODE_NIGHT_NO -> {
                dzenNoch = false
            }
        }
        return dzenNoch
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reset = Intent(context, WidgetMun::class.java)
        reset.action = Settings.RESET_WIDGET_MUN
        val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_IMMUTABLE or 0)
        alarmManager.cancel(pIntent)
        alarmManager.cancel(pReset)
    }

    private fun mkTime(addDate: Int = 0): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, addDate)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        CoroutineScope(Dispatchers.Main).launch {
            val manager = GlanceAppWidgetManager(context)
            val widget = CaliandarWidgetMun()
            val glanceIds = manager.getGlanceIds(widget.javaClass)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) {
                    it[booleanPreferencesKey("dzenNoch")] = getBaseDzenNoch(context)
                    it[intPreferencesKey("position")] = getDataKaliandar(0, it[intPreferencesKey("position")] ?: findDefaultCaliandarMun())
                    if (intent.action == munPlus) {
                        it[intPreferencesKey("position")] = getDataKaliandar(1, it[intPreferencesKey("position")] ?: findDefaultCaliandarMun())
                    }
                    if (intent.action == munMinus) {
                        it[intPreferencesKey("position")] = getDataKaliandar(-1, it[intPreferencesKey("position")] ?: findDefaultCaliandarMun())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        AppWidgetManager.getInstance(context).setWidgetPreview(
                            ComponentName(context, WidgetMun::class.java),
                            AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                            CaliandarWidgetMun().compose(context = context)
                        )
                    }
                }
                widget.update(context, glanceId)
            }
            val intentUpdate = Intent(context, WidgetMun::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 60, intentUpdate, PendingIntent.FLAG_IMMUTABLE or 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
            }
        }
    }
}