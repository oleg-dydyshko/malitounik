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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
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
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.compose
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import by.carkva_gazeta.malitounik.ui.theme.BackgroundDark
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryBlack
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import by.carkva_gazeta.malitounik.views.findCaliandarToDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class CaliandarWidget : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Caliandar()
            }
        }
    }
}

@Composable
private fun Caliandar() {
    val prefs = currentState<Preferences>()
    val position = prefs[intPreferencesKey("position_widget_day")] ?: findCaliandarToDay(LocalContext.current, false)[25].toInt()
    val dzenNoch = prefs[booleanPreferencesKey("dzenNoch")] == true
    val data = Settings.data[position]
    val context = LocalContext.current
    val month = data[2].toInt()
    val monthName = context.resources.getStringArray(R.array.meciac)
    val dayofmounth = data[1]
    val nedel = data[0].toInt()
    val nedelName = context.resources.getStringArray(R.array.dni_nedeli)
    val colorBackground = when {
        data[7].toInt() == 2 -> Post
        data[7].toInt() == 1 -> BezPosta
        data[7].toInt() == 3 -> StrogiPost
        data[5].toInt() > 0 -> Primary
        else -> Divider
    }
    val colorText = if (data[7].toInt() == 3 || data[5].toInt() > 0) PrimaryTextBlack
    else PrimaryText
    val destinationKey = ActionParameters.Key<Boolean>("widget_day")
    val destinationPosition = ActionParameters.Key<Int>("position")
    Box(
        modifier = GlanceModifier.fillMaxSize().background(Color.Transparent)
    ) {
        Box(
            modifier = GlanceModifier.background(if (dzenNoch) BackgroundDark else PrimaryTextBlack).cornerRadius(20.dp).clickable(actionStartActivity<MainActivity>(actionParametersOf(destinationKey to true, destinationPosition to position)))
        ) {
            Column(
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.Top, modifier = GlanceModifier.fillMaxWidth().background(colorBackground)) {
                    Text(text = nedelName[nedel], style = TextStyle(color = ColorProvider(colorText, colorText), fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold))
                    Text(text = dayofmounth, style = TextStyle(color = ColorProvider(colorText, colorText), fontSize = 40.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold))
                    Text(text = monthName[month], style = TextStyle(color = ColorProvider(colorText, colorText), fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold))
                }
                Column(modifier = GlanceModifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                    if (data[6].isNotEmpty()) {
                        if (data[5].toInt() == 1 || data[5].toInt() == 2) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(provider = ImageProvider(R.drawable.znaki_krest_v_kruge), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = ColorFilter.tint(if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary)))
                                Text(modifier = GlanceModifier.padding(start = 5.dp), text = data[6], style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = if (data[5].toInt() == 2) FontWeight.Normal else FontWeight.Bold))
                            }
                        } else {
                            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = data[6], style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = if (data[5].toInt() == 2) FontWeight.Normal else FontWeight.Bold))
                            }
                        }
                    }
                    if (data[8].isNotEmpty()) {
                        Text(text = AnnotatedString.fromHtml(data[8]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp, textAlign = TextAlign.Center, fontStyle = FontStyle.Italic))
                    }
                    if (data[4].isNotEmpty()) {
                        var icon = 0
                        when (data[12].toInt()) {
                            1 -> icon = R.drawable.znaki_krest
                            3 -> icon = R.drawable.znaki_krest_v_polukruge
                            4 -> icon = R.drawable.znaki_ttk
                            5 -> icon = R.drawable.znaki_ttk_black
                        }
                        val svityia = data[4]
                        val colorFilter = if (data[12].toInt() == 5) ColorFilter.tint(if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText))
                        else ColorFilter.tint(if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary))
                        if (svityia.contains("<font")) {
                            if (svityia.contains("<strong>")) {
                                if (svityia.contains("<br>")) {
                                    val text = AnnotatedString.fromHtml(data[4]).toString()
                                    val t1 = text.indexOf("\n")
                                    if (icon != 0) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(provider = ImageProvider(icon), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = colorFilter)
                                            Text(modifier = GlanceModifier.padding(start = 5.dp), text = text.substring(0, t1), style = TextStyle(color = ColorProvider(Primary, PrimaryBlack), fontSize = 18.sp, fontWeight = FontWeight.Bold))
                                        }
                                        Text(text = text.substring(t1 + 1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                    } else {
                                        Text(text = text.substring(0, t1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp, fontWeight = FontWeight.Bold))
                                        Text(text = text.substring(t1 + 1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                    }
                                } else {
                                    if (icon != 0) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(provider = ImageProvider(icon), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = colorFilter)
                                            Text(modifier = GlanceModifier.padding(start = 5.dp), text = AnnotatedString.fromHtml(data[4]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp, fontWeight = FontWeight.Bold))
                                        }
                                    } else {
                                        Text(text = AnnotatedString.fromHtml(data[4]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp, fontWeight = FontWeight.Bold))
                                    }
                                }
                            } else {
                                if (svityia.contains("<br>")) {
                                    val text = AnnotatedString.fromHtml(data[4]).toString()
                                    val t1 = text.indexOf("\n")
                                    if (icon != 0) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(provider = ImageProvider(icon), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = colorFilter)
                                            Text(modifier = GlanceModifier.padding(start = 5.dp), text = text.substring(0, t1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp))
                                        }
                                        Text(text = text.substring(t1 + 1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                    } else {
                                        Text(text = text.substring(0, t1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp))
                                        Text(text = text.substring(t1 + 1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                    }
                                } else {
                                    if (icon != 0) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(provider = ImageProvider(icon), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = colorFilter)
                                            Text(modifier = GlanceModifier.padding(start = 5.dp), text = AnnotatedString.fromHtml(data[4]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp))
                                        }
                                    } else {
                                        Text(text = AnnotatedString.fromHtml(data[4]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryBlack, PrimaryBlack) else ColorProvider(Primary, Primary), fontSize = 18.sp))
                                    }
                                }
                            }
                        } else {
                            if (svityia.contains("<br>")) {
                                val text = AnnotatedString.fromHtml(data[4]).toString()
                                val t1 = text.indexOf("\n")
                                if (icon != 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(provider = ImageProvider(icon), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = colorFilter)
                                        Text(modifier = GlanceModifier.padding(start = 5.dp), text = text.substring(0, t1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                    }
                                    Text(text = text.substring(t1 + 1), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                } else {
                                    Text(text = text, style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                }
                            } else {
                                if (icon != 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(provider = ImageProvider(icon), contentDescription = "", modifier = GlanceModifier.size(20.dp, 20.dp), colorFilter = colorFilter)
                                        Text(modifier = GlanceModifier.padding(start = 5.dp), text = AnnotatedString.fromHtml(data[4]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                    }
                                } else {
                                    Text(text = AnnotatedString.fromHtml(data[4]).toString(), style = TextStyle(color = if (dzenNoch) ColorProvider(PrimaryTextBlack, PrimaryTextBlack) else ColorProvider(PrimaryText, PrimaryText), fontSize = 18.sp))
                                }
                            }
                        }
                    }
                }
                if ((data[7].toInt() > 0 && nedel == Calendar.FRIDAY) || (data[7].toInt() == 3 && nedel != Calendar.SATURDAY && nedel != Calendar.SUNDAY)) {
                    val post = when (data[7].toInt()) {
                        1 -> context.resources.getString(R.string.No_post_n)
                        3 -> context.resources.getString(R.string.Strogi_post_n)
                        else -> context.resources.getString(R.string.Post)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.Bottom) {
                        Row(modifier = GlanceModifier.fillMaxWidth().background(colorBackground), horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
                            if (data[7].toInt() != 1) {
                                Image(provider = ImageProvider(if (data[7].toInt() == 3) R.drawable.fishe_red_black else R.drawable.fishe), contentDescription = "", modifier = GlanceModifier.size(26.dp, 13.dp))
                            }
                            Text(modifier = GlanceModifier.padding(start = 10.dp), text = post, style = TextStyle(color = ColorProvider(colorText, colorText), fontSize = 18.sp, textAlign = TextAlign.Center))
                        }
                    }
                }
                if (data[20] != "0" && data[0].toInt() == Calendar.SUNDAY) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.Bottom) {
                        Row(modifier = GlanceModifier.fillMaxWidth().background(Primary), horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
                            Text(modifier = GlanceModifier.padding(start = 10.dp), text = "Тон ${data[20]}", style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 18.sp, textAlign = TextAlign.Center))
                        }
                    }
                }
            }
        }
    }
}

class Widget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = CaliandarWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CoroutineScope(Dispatchers.Main).launch {
            val position = getDataKaliandar(context)
            val manager = GlanceAppWidgetManager(context)
            val widget = CaliandarWidget()
            val glanceIds = manager.getGlanceIds(widget.javaClass)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) {
                    it[intPreferencesKey("position_widget_day")] = position
                    it[booleanPreferencesKey("dzenNoch")] = getBaseDzenNoch(context)
                }
                widget.update(context, glanceId)
            }
            val intentUpdate = Intent(context, Widget::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 50, intentUpdate, PendingIntent.FLAG_IMMUTABLE or 0)
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

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val intent = Intent(context, Widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_IMMUTABLE or 0)
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

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val intent = Intent(context, Widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pIntent)
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

    private fun getBaseDzenNoch(context: Context): Boolean {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val modeNight = k.getInt("mode_night_widget_day", Settings.MODE_NIGHT_SYSTEM)
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

    private fun getDataKaliandar(context: Context): Int {
        val calendar = Calendar.getInstance()
        val gson = Gson()
        if (Settings.data.isEmpty()) {
            val type = TypeToken.getParameterized(
                ArrayList::class.java,
                TypeToken.getParameterized(
                    ArrayList::class.java,
                    String::class.java
                ).type
            ).type
            val inputStream = context.resources.openRawResource(R.raw.caliandar)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            Settings.data.addAll(gson.fromJson(builder, type))
        }
        var kalPosition = 0
        for (i in Settings.data.indices) {
            if (calendar[Calendar.DATE] == Settings.data[i][1].toInt() && calendar[Calendar.MONTH] == Settings.data[i][2].toInt() && calendar[Calendar.YEAR] == Settings.data[i][3].toInt()) {
                kalPosition = i
                break
            }
        }
        return kalPosition
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        CoroutineScope(Dispatchers.Main).launch {
            val position = getDataKaliandar(context)
            val manager = GlanceAppWidgetManager(context)
            val widget = CaliandarWidget()
            val glanceIds = manager.getGlanceIds(widget.javaClass)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) {
                    it[intPreferencesKey("position_widget_day")] = position
                    it[booleanPreferencesKey("dzenNoch")] = getBaseDzenNoch(context)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    AppWidgetManager.getInstance(context).setWidgetPreview(
                        ComponentName(context, Widget::class.java),
                        AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                        CaliandarWidget().compose(context = context)
                    )
                }
                widget.update(context, glanceId)
            }
        }
        val intentUpdate = Intent(context, Widget::class.java)
        intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intentUpdate, PendingIntent.FLAG_IMMUTABLE or 0)
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