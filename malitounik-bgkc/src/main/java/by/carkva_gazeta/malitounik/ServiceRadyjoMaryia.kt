package by.carkva_gazeta.malitounik

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer
import java.util.TimerTask


class ServiceRadyjoMaryia : Service() {

    inner class ServiceRadyjoMaryiaBinder : Binder() {
        fun getService() = this@ServiceRadyjoMaryia
    }

    companion object {
        const val START = 1
        const val PLAY_PAUSE = 2
        const val STOP = 3
        var isServiceRadioMaryiaRun by mutableStateOf(false)
        var isPlayingRadyjoMaryia by mutableStateOf(false)
        var titleRadyjoMaryia by mutableStateOf("")
    }

    private var player: ExoPlayer? = null
    private val isPlaying: Boolean
        get() {
            val play = player?.isPlaying == true
            isPlayingRadyjoMaryia = play
            return play
        }
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var listener: ServiceRadyjoMaryiaListener? = null
    private var isPlaybackStateReady = false

    interface ServiceRadyjoMaryiaListener {
        fun setTitleRadioMaryia(title: String)
        fun unBinding()
        fun playingRadioMaria(isPlayingRadioMaria: Boolean)
        fun playingRadioMariaStateReady()
        fun errorRadioMaria()
    }

    fun setServiceRadyjoMaryiaListener(serviceRadyjoMaryiaListener: ServiceRadyjoMaryiaListener) {
        listener = serviceRadyjoMaryiaListener
    }

    private fun setDataToWidget(action: Int, isPlaying: Boolean, title: String) {
        val sp = getSharedPreferences("biblia", MODE_PRIVATE)
        if (sp.getBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false)) {
            val intent = Intent(this@ServiceRadyjoMaryia, WidgetRadyjoMaryia::class.java)
            intent.putExtra("action", action)
            intent.putExtra("isPlaying", isPlaying)
            intent.putExtra("title", title)
            sendBroadcast(intent)
        }
    }

    private fun initRadioMaria() {
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        val radioMaryiaList = resources.getStringArray(R.array.radio_maryia_url_list)
        val radioMaryiaListPosition = k.getInt("radioMaryiaListPosition", 0)
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(radioMaryiaList[radioMaryiaListPosition].toUri()))
            prepare()
            addListener(object : Player.Listener {

                override fun onPlayerError(error: PlaybackException) {
                    stopServiceRadioMaria(true)
                    val intent = Intent(Intent.ACTION_VIEW, "https://radiomaria.by/player/".toUri())
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        } else {
                            0
                        }
                        ServiceCompat.startForeground(this@ServiceRadyjoMaryia, 300, setRadioNotification(), flag)
                        listener?.playingRadioMariaStateReady()
                        setDataToWidget(PLAY_PAUSE, isPlaying, titleRadyjoMaryia)
                        isPlaybackStateReady = true
                    }
                }
            })
        }
    }

    override fun onCreate() {
        super.onCreate()
        startTimer()
        isServiceRadioMaryiaRun = true
        initRadioMaria()
    }

    fun stopServiceRadioMaria(isError: Boolean = false) {
        stopPlay()
        stopSelf()
        isServiceRadioMaryiaRun = false
        setDataToWidget(STOP, false, "")
        listener?.setTitleRadioMaryia("")
        listener?.unBinding()
        isPlaybackStateReady = false
        isPlayingRadyjoMaryia = false
        titleRadyjoMaryia = ""
        if (isError) listener?.errorRadioMaria()
    }

    fun playOrPause() {
        if (isPlaying) {
            player?.pause()
        } else {
            player?.play()
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
        ServiceCompat.startForeground(this, 300, setRadioNotification(), flag)
    }

    fun isPlayingRadioMaria() = isPlaying

    private fun stopPlay() {
        player?.stop()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(100)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        isServiceRadioMaryiaRun = false
        stopPlay()
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
        ServiceCompat.stopForeground(this, flag)
    }

    private fun startTimer() {
        stopTimer()
        timerTask = object : TimerTask() {
            override fun run() {
                sendTitlePadioMaryia()
            }
        }
        timer = Timer()
        timer.schedule(timerTask, 0, 20000)
    }

    private fun stopTimer() {
        timer.cancel()
        timerTask = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
        ServiceCompat.startForeground(this, 300, setRadioNotification(), flag)
        val action = intent?.extras?.getInt("action") ?: PLAY_PAUSE
        if (action == PLAY_PAUSE) {
            playOrPause()
            if (isPlaybackStateReady) {
                setDataToWidget(PLAY_PAUSE, isPlaying, titleRadyjoMaryia)
            }
            listener?.playingRadioMaria(isPlaying)
        } else {
            stopServiceRadioMaria()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendTitlePadioMaryia() {
        if (Settings.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    withContext(Dispatchers.IO) {
                        try {
                            val mURL = URL("https://radiomaria.by/player/hintbackend.php")
                            with(mURL.openConnection() as HttpURLConnection) {
                                val sb = StringBuilder()
                                BufferedReader(InputStreamReader(inputStream)).use {
                                    var inputLine = it.readLine()
                                    while (inputLine != null) {
                                        sb.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                }
                                var text = AnnotatedString.fromHtml(sb.toString()).text.trim()
                                val t1 = text.indexOf(":", ignoreCase = true)
                                if (t1 != -1) {
                                    text = text.substring(t1 + 1)
                                }
                                val t2 = text.indexOf(">", ignoreCase = true)
                                if (t2 != -1) {
                                    text = text.substring(t2 + 1)
                                }
                                if (titleRadyjoMaryia != text.trim()) {
                                    titleRadyjoMaryia = text.trim()
                                    withContext(Dispatchers.Main) {
                                        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                                        } else {
                                            0
                                        }
                                        ServiceCompat.startForeground(this@ServiceRadyjoMaryia, 300, setRadioNotification(), flag)
                                        setDataToWidget(PLAY_PAUSE, isPlaying, titleRadyjoMaryia)
                                        listener?.setTitleRadioMaryia(titleRadyjoMaryia)
                                    }
                                }
                            }
                        } catch (_: Throwable) {
                        }
                    }
                }
            }
        }
    }

    private fun setRadioNotification(): Notification {
        val mediaSession = MediaSessionCompat(this, "Radyjo Maryia session")
        val name = getString(R.string.padie_maryia_s)
        mediaSession.setMetadata(MediaMetadataCompat.Builder().putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(resources, R.drawable.maria)).putString(MediaMetadataCompat.METADATA_KEY_TITLE, name).putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, titleRadyjoMaryia).build())
        mediaSession.isActive = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Settings.NOTIFICATION_CHANNEL_ID_RADIO_MARYIA, name, NotificationManager.IMPORTANCE_LOW)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.description = name
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        val notifi = NotificationCompat.Builder(this, Settings.NOTIFICATION_CHANNEL_ID_RADIO_MARYIA)
        notifi.setShowWhen(false)
        notifi.setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1))
        notifi.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.maria))
        notifi.setSmallIcon(R.drawable.krest)
        notifi.setContentTitle(getString(R.string.padie_maryia_s))
        notifi.setContentText(titleRadyjoMaryia)
        notifi.setOngoing(true)
        if (isPlaying) notifi.addAction(R.drawable.pause3, "pause", retreivePlaybackAction(PLAY_PAUSE))
        else notifi.addAction(R.drawable.play3, "play", retreivePlaybackAction(PLAY_PAUSE))
        notifi.addAction(R.drawable.stop3, "stop", retreivePlaybackAction(STOP))
        mediaSession.release()
        return notifi.build()
    }

    private fun retreivePlaybackAction(which: Int): PendingIntent? {
        val action = Intent()
        val pendingIntent: PendingIntent
        val serviceName = ComponentName(this, ServiceRadyjoMaryia::class.java)
        when (which) {
            PLAY_PAUSE -> {
                action.putExtra("action", PLAY_PAUSE)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, PLAY_PAUSE, action, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                return pendingIntent
            }

            STOP -> {
                action.putExtra("action", STOP)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, STOP, action, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                return pendingIntent
            }
        }
        return null
    }

    override fun onBind(intent: Intent) = ServiceRadyjoMaryiaBinder()
}