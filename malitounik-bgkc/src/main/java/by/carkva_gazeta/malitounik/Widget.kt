package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class Widget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (widgetID in appWidgetIds) {
            kaliandar(context, appWidgetManager, widgetID)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit { putBoolean("WIDGET_ENABLED", true) }
        val intent = Intent(context, Widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }

            else -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        context.getSharedPreferences("biblia", Context.MODE_PRIVATE).edit {
            for (widgetID in appWidgetIds) {
                remove("dzen_noch_widget_day$widgetID")
            }
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit {
            for ((key) in sp.all) {
                if (key.contains("dzen_noch_widget_day")) {
                    remove(key)
                }
            }
            putBoolean("WIDGET_ENABLED", false)
        }
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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val extSettings = intent.extras?.getBoolean("settings", false) == true
        if (extSettings) {
            isSettingsCulling = true
            val settings = Intent(context, WidgetConfig::class.java)
            settings.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            settings.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            context.startActivity(settings)
        }
        if (intent.extras?.getBoolean("actionEndLoad", false) == true) {
            isSettingsCulling = false
        }
        if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            kaliandar(context, appWidgetManager, widgetID)
        }
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            onUpdate(context, appWidgetManager, ids)
            val intentUpdate = Intent(context, Widget::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 50, intentUpdate, PendingIntent.FLAG_IMMUTABLE or 0)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }

                else -> {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }
            }
        }
    }

    private fun prazdnik(context: Context, updateViews: RemoteViews) {
        updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPrimary))
        updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorWhite))
        updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPrimary))
        updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPrimary))
        updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorWhite))
        updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorWhite))
    }

    private fun getBaseDzenNoch(context: Context, widgetID: Int): Boolean {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val modeNight = k.getInt("mode_night_widget_day$widgetID", Settings.MODE_NIGHT_SYSTEM)
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

    private fun kaliandar(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val updateViews = RemoteViews(context.packageName, R.layout.widget)
        val calendar = Calendar.getInstance()
        if (Settings.data.isEmpty()) {
            val gson = Gson()
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
        val data = Settings.data[kalPosition]
        val dzenNoch = getBaseDzenNoch(context, widgetID)
        val rColorColorPrimaryText: Int
        val rColorColorPrimary: Int
        if (dzenNoch) {
            rColorColorPrimary = R.color.colorPrimary_black
            rColorColorPrimaryText = R.color.colorWhite
        } else {
            rColorColorPrimary = R.color.colorPrimary
            rColorColorPrimaryText = R.color.colorPrimary_text
        }
        val month = data[2].toInt()
        val dayofmounth = data[1].toInt()
        val nedel = data[0].toInt()
        val intent = Intent(context, MainActivity::class.java)
        val widgetDay = "widget_day"
        intent.putExtra(widgetDay, true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pIntent = PendingIntent.getActivity(context, 500, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews.setOnClickPendingIntent(R.id.fullCaliandar, pIntent)
        val settings = Intent(context, Widget::class.java)
        settings.putExtra("settings", true)
        settings.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        val pSsettings = PendingIntent.getBroadcast(context, 40 + widgetID, settings, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews.setOnClickPendingIntent(R.id.settings, pSsettings)
        if (dzenNoch) {
            updateViews.setInt(R.id.Layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorbackground_material_dark))
            updateViews.setTextColor(R.id.textSviatyia, ContextCompat.getColor(context, R.color.colorWhite))
        } else {
            updateViews.setInt(R.id.Layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setTextColor(R.id.textSviatyia, ContextCompat.getColor(context, R.color.colorPrimary_text))
        }
        if (isSettingsCulling) {
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView7, R.drawable.load_kalendar_black)
            else updateViews.setImageViewResource(R.id.imageView7, R.drawable.load_kalendar)
        } else {
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView7, R.drawable.settings_widget)
            else updateViews.setImageViewResource(R.id.imageView7, R.drawable.settings_black)
        }
        updateViews.setTextViewText(R.id.textPost, "Пост")
        updateViews.setViewVisibility(R.id.textPost, View.GONE)
        updateViews.setTextColor(R.id.textPost, ContextCompat.getColor(context, R.color.colorPrimary_text))
        if (dzenNoch) updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_whate) else updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe)
        updateViews.setViewVisibility(R.id.imageView4, View.GONE)
        updateViews.setViewVisibility(R.id.znakTipicona, View.GONE)
        updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.GONE)
        updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorDivider))
        updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorDivider))
        updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorDivider))
        updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorPrimary_text))
        updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorPrimary_text))
        updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorPrimary_text))
        updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimary))
        updateViews.setViewVisibility(R.id.textSviatyia, View.GONE)
        updateViews.setTextViewText(R.id.textChislo, dayofmounth.toString())
        if (data[7].toInt() == 1) {
            updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
        }
        if (!(nedel == 1 || nedel == 7)) {
            if (data[7].toInt() == 1) {
                updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                if (nedel == 6) {
                    updateViews.setTextViewText(R.id.textPost, "Посту няма")
                    updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                    updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
                }
            }
        }
        if (!(nedel == 1 || nedel == 7)) {
            if (data[7].toInt() == 2) {
                updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            }
        }
        if (!data[6].contains("no_sviaty")) {
            val svita = data[6].replace("\n", "<br>")
            if (data[5].contains("1")) updateViews.setTextViewText(R.id.textCviatyGlavnyia, HtmlCompat.fromHtml("<strong>$svita</strong>", HtmlCompat.FROM_HTML_MODE_LEGACY))
            else updateViews.setTextViewText(R.id.textCviatyGlavnyia, HtmlCompat.fromHtml(svita, HtmlCompat.FROM_HTML_MODE_LEGACY))
            updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.VISIBLE)
        }
        if (data[6].contains("Пачатак") || data[6].contains("Вялікі") || data[6].contains("Вялікая") || data[6].contains("ВЕЧАР") || data[6].contains("Палова")) {
            updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimaryText))
            updateViews.setTextViewText(R.id.textCviatyGlavnyia, HtmlCompat.fromHtml(data[6], HtmlCompat.FROM_HTML_MODE_LEGACY))
            updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.VISIBLE)
        }
        if (data[6].contains("Сьветл")) {
            updateViews.setTextViewText(R.id.textCviatyGlavnyia, HtmlCompat.fromHtml("<strong>${data[6]}</strong>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        var dataSviatyia = ""
        if (!data[4].contains("no_sviatyia")) {
            dataSviatyia = data[4]
        }
        if (data[8] != "") {
            dataSviatyia = data[8] + "<br>" + dataSviatyia
        }
        if (data[18] == "1") {
            dataSviatyia = dataSviatyia + "<br><strong>" + context.getString(R.string.pamerlyia) + "</strong>"
        }
        if (dataSviatyia != "") {
            if (dzenNoch) dataSviatyia = dataSviatyia.replace("#d00505", "#ff6666")
            updateViews.setTextViewText(R.id.textSviatyia, HtmlCompat.fromHtml(dataSviatyia, HtmlCompat.FROM_HTML_MODE_LEGACY))
            updateViews.setViewVisibility(R.id.textSviatyia, View.VISIBLE)
        }
        if (data[7].contains("2")) {
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            if (nedel == 6) {
                updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
                updateViews.setViewVisibility(R.id.imageView4, View.VISIBLE)
            }
        } else if (data[7].contains("3")) {
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setTextViewText(R.id.textPost, "Строгі пост")
            updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textPost, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
            updateViews.setViewVisibility(R.id.imageView4, View.VISIBLE)
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_red_black) else updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_red)
        }
        if (data[5].contains("1") || data[5].contains("2") || data[5].contains("3")) {
            updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimary))
            prazdnik(context, updateViews)
        }
        when (data[12].toInt()) {
            1 -> {
                if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest_black) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest)
                updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
            }

            2 -> {
                var rDrawableZnakiKrestVKruge = R.drawable.znaki_krest_v_kruge
                if (dzenNoch) rDrawableZnakiKrestVKruge = R.drawable.znaki_krest_v_kruge_black
                updateViews.setImageViewResource(R.id.znakTipicona, rDrawableZnakiKrestVKruge)
                updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
            }

            3 -> {
                if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest_v_polukruge_black) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest_v_polukruge)
                updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
            }

            4 -> {
                if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk_black_black) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk)
                updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
            }

            5 -> {
                if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk_whate) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk_black)
                updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
            }
        }
        val nedelName = context.resources.getStringArray(R.array.dni_nedeli)
        updateViews.setTextViewText(R.id.textDenNedeli, nedelName[nedel])
        if (nedel == 1) prazdnik(context, updateViews)
        val monthName = context.resources.getStringArray(R.array.meciac)
        if (month == Calendar.OCTOBER) updateViews.setFloat(R.id.textMesiac, "setTextSize", 12f)
        else updateViews.setFloat(R.id.textMesiac, "setTextSize", 14f)
        if (nedel == Calendar.MONDAY) updateViews.setFloat(R.id.textDenNedeli, "setTextSize", 12f)
        else updateViews.setFloat(R.id.textDenNedeli, "setTextSize", 14f)
        updateViews.setTextViewText(R.id.textMesiac, monthName[month])
        appWidgetManager.updateAppWidget(widgetID, updateViews)
    }

    companion object {
        private var isSettingsCulling = false
    }
}