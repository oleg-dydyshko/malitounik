package by.carkva_gazeta.malitounik

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import by.carkva_gazeta.malitounik.RadyjoMaryiaClickActionCallback.Companion.getClickTypeActionParameterKey
import by.carkva_gazeta.malitounik.Settings.isNetworkAvailable
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.RadyjoMaryia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlanceWidgetRadyjoMaryia : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                RadyjoMaryia(context)
            }
        }
    }
}

class RadyjoMaryiaClickActionCallback : ActionCallback {
    companion object {
        const val TYPE_PROGRAM = 1
        const val TYPE_PLAY_PAUSE = 2
        const val TYPE_STOP = 3

        fun getClickTypeActionParameterKey(): ActionParameters.Key<Int> {
            return ActionParameters.Key("action")
        }
    }

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val clickType = requireNotNull(parameters[getClickTypeActionParameterKey()])
        if (clickType == TYPE_PROGRAM) {
            withContext(Dispatchers.Main) {
                val intent2 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
                intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent2)
            }
            return
        }
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            var action = it[intPreferencesKey("action")] ?: TYPE_STOP
            when (clickType) {
                TYPE_PLAY_PAUSE -> {
                    if (isNetworkAvailable(context)) {
                        if (!ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                            action = ServiceRadyjoMaryia.START
                        }
                        val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
                        intent2.putExtra("action", ServiceRadyjoMaryia.PLAY_PAUSE)
                        ContextCompat.startForegroundService(context, intent2)
                    } else {
                        val intent3 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
                        intent3.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent3.putExtra("checkInternet", true)
                        context.startActivity(intent3)
                    }
                }

                TYPE_STOP -> {
                    if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                        val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
                        intent2.putExtra("action", ServiceRadyjoMaryia.STOP)
                        ContextCompat.startForegroundService(context, intent2)
                    }
                    action = TYPE_STOP
                }
            }
            it.toMutablePreferences().apply {
                this[intPreferencesKey("action")] = action
            }
        }
        GlanceWidgetRadyjoMaryia().update(context, glanceId)
    }
}

private fun getRadyjoMaryiaActionCallback(clickType: Int): Action {
    return actionRunCallback<RadyjoMaryiaClickActionCallback>(
        actionParametersOf(
            getClickTypeActionParameterKey() to clickType
        )
    )
}

@Composable
fun RadyjoMaryia(context: Context) {
    val prefs = currentState<Preferences>()
    val title = prefs[stringPreferencesKey("title")] ?: context.getString(R.string.padie_maryia_s)
    val action = prefs[intPreferencesKey("action")] ?: RadyjoMaryiaClickActionCallback.TYPE_STOP
    val isPlaying = prefs[booleanPreferencesKey("isPlaying")] ?: false
    Row(modifier = GlanceModifier.fillMaxWidth().background(RadyjoMaryia), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = title,
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(10.dp),
                style = TextStyle(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack), fontSize = 18.sp),
                maxLines = 1
            )
            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = GlanceModifier.padding(10.dp).clickable(getRadyjoMaryiaActionCallback(RadyjoMaryiaClickActionCallback.TYPE_PROGRAM))) {
                    Image(
                        modifier = GlanceModifier.size(24.dp, 24.dp), provider = ImageProvider(R.drawable.programm_rado_maria2), contentDescription = "", colorFilter = ColorFilter.tint(ColorProvider(PrimaryTextBlack, PrimaryTextBlack))
                    )
                }
                if (action == ServiceRadyjoMaryia.START) {
                    CircularProgressIndicator(color = ColorProvider(PrimaryTextBlack, PrimaryTextBlack))
                } else {
                    Box(modifier = GlanceModifier.padding(10.dp).clickable(getRadyjoMaryiaActionCallback(RadyjoMaryiaClickActionCallback.TYPE_PLAY_PAUSE))) {
                        Image(
                            modifier = GlanceModifier.size(24.dp, 24.dp), provider = if (isPlaying) ImageProvider(R.drawable.pause3) else ImageProvider(R.drawable.play3), contentDescription = "", colorFilter = ColorFilter.tint(ColorProvider(PrimaryTextBlack, PrimaryTextBlack))
                        )
                    }
                }
                Box(modifier = GlanceModifier.padding(10.dp).clickable(getRadyjoMaryiaActionCallback(RadyjoMaryiaClickActionCallback.TYPE_STOP))) {
                    Image(
                        modifier = GlanceModifier.size(24.dp, 24.dp), provider = ImageProvider(R.drawable.stop3), contentDescription = "", colorFilter = ColorFilter.tint(ColorProvider(PrimaryTextBlack, PrimaryTextBlack))
                    )
                }
            }
        }
        Image(modifier = GlanceModifier.size(75.dp, 75.dp), provider = ImageProvider(R.drawable.maria), contentDescription = "")
    }
}

class WidgetRadyjoMaryia : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = GlanceWidgetRadyjoMaryia()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit { putBoolean("WIDGET_RADYJO_MARYIA_ENABLED", true) }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit { putBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.extras?.getInt("action", 0) ?: 0
        val title = intent.extras?.getString("title", ServiceRadyjoMaryia.titleRadyjoMaryia) ?: ServiceRadyjoMaryia.titleRadyjoMaryia
        val isPlaying = intent.extras?.getBoolean("isPlaying", ServiceRadyjoMaryia.isPlayingRadyjoMaryia) ?: ServiceRadyjoMaryia.isPlayingRadyjoMaryia
        CoroutineScope(Dispatchers.Main).launch {
            val manager = GlanceAppWidgetManager(context)
            val widget = GlanceWidgetRadyjoMaryia()
            val glanceIds = manager.getGlanceIds(widget.javaClass)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) {
                    it[stringPreferencesKey("title")] = if (title != "") title
                    else context.getString(R.string.padie_maryia_s)
                    it[intPreferencesKey("action")] = action
                    it[booleanPreferencesKey("isPlaying")] = isPlaying
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    AppWidgetManager.getInstance(context).setWidgetPreview(
                        ComponentName(context, WidgetRadyjoMaryia::class.java),
                        AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                        GlanceWidgetRadyjoMaryia().compose(context = context)
                    )
                }
                widget.update(context, glanceId)
            }
        }
    }
}