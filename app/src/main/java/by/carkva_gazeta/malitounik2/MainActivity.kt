package by.carkva_gazeta.malitounik2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import by.carkva_gazeta.malitounik2.ui.theme.MalitounikTheme
import by.carkva_gazeta.malitounik2.views.AllDestinations
import by.carkva_gazeta.malitounik2.views.AppNavGraph
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

object Settings {
    const val TRANSPORT_ALL = 100
    const val TRANSPORT_WIFI = 101
    const val TRANSPORT_CELLULAR = 102
    const val GET_CALIANDAR_YEAR_MIN = 2023
    const val GET_CALIANDAR_YEAR_MAX = 2026
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
    const val MENU_TRYEDZ_BIALIKAGA_TYDNIA = 114
    const val MENU_TRYEDZ_SVETLAGA_TYDNIA = 115
    const val MENU_TRYEDZ_KVETNAIA = 116
    var bibleTime = false
    var bibleTimeList = false
    var destinations = AllDestinations.KALIANDAR
    var caliandarPosition = -1
    var initCaliandarPosition = 0
    var data = ArrayList<ArrayList<String>>()
}

class MainActivity : ComponentActivity(), SensorEventListener {
    private var backPressed: Long = 0
    private var mLastClickTime: Long = 0
    private var myTimer: Job? = null
    private var ferstStart = false
    var dzenNoch = false
    var checkDzenNoch = false
    private var startTimeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) {
            setlightSensor()
        }
        setContent {
            if (savedInstanceState != null) {
                dzenNoch = savedInstanceState.getBoolean("dzenNoch", false)
            } else {
                val modeNight = k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM)
                dzenNoch = isSystemInDarkTheme()
                if (modeNight == Settings.MODE_NIGHT_NO) dzenNoch = false
                if (modeNight == Settings.MODE_NIGHT_YES) dzenNoch = true
            }
            checkDzenNoch = dzenNoch
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            MalitounikTheme(darkTheme = dzenNoch) {
                AppNavGraph()
                onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        onBack()
                    }
                })
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (isNetworkAvailable()) {//k.getBoolean("admin", false) &&
                val dir = File("$filesDir/cache")
                if (!dir.exists()) dir.mkdir()
                val localFile = File("$filesDir/cache/cache.txt")
                referens.child("/admin/log.txt").getFile(localFile)
                    .addOnFailureListener {
                        val mes = Toast.makeText(
                            this@MainActivity,
                            getString(R.string.error),
                            Toast.LENGTH_SHORT
                        )
                        mes.show()
                    }.await()
                val log = localFile.readText()
                if (log != "") {
                    withContext(Dispatchers.Main) {
                        val mes = Toast.makeText(
                            this@MainActivity,
                            getString(R.string.check_update_resourse),
                            Toast.LENGTH_SHORT
                        )
                        mes.show()
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(typeTransport: Int = Settings.TRANSPORT_ALL): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            when (typeTransport) {
                Settings.TRANSPORT_CELLULAR -> {
                    if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
                }

                Settings.TRANSPORT_WIFI -> {
                    if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
                }

                Settings.TRANSPORT_ALL -> {
                    return when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
            if (activeNetwork.isConnectedOrConnecting) {
                when (typeTransport) {
                    Settings.TRANSPORT_CELLULAR -> {
                        if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return true
                    }

                    Settings.TRANSPORT_WIFI -> {
                        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return true
                    }

                    Settings.TRANSPORT_ALL -> {
                        return when (activeNetwork.type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_VPN -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }
                    }
                }
            }
        }
        return false
    }

    override fun attachBaseContext(context: Context) {
        /*Configuration(context.resources.configuration).apply {
            if (this.fontScale != 1.0f) {
                this.fontScale = 1.0f
            }
            applyOverrideConfiguration(this)
        }*/
        super.attachBaseContext(context)
        FirebaseApp.initializeApp(context)
        //Firebase.appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
    }

    /*fun getBaseDzenNoch(): Boolean {
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val modeNight = k.getInt("mode_night", SettingsActivity.MODE_NIGHT_SYSTEM)
        when (modeNight) {
            SettingsActivity.MODE_NIGHT_SYSTEM -> {
                val configuration = Resources.getSystem().configuration
                dzenNoch = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            }

            SettingsActivity.MODE_NIGHT_YES -> {
                dzenNoch = true
            }

            SettingsActivity.MODE_NIGHT_NO -> {
                dzenNoch = false
            }

            SettingsActivity.MODE_NIGHT_AUTO -> {
                dzenNoch = k.getBoolean("dzen_noch", false)
            }
        }
        return dzenNoch
    }

    fun getCheckDzenNoch() = checkDzenNoch*/

    private fun timeJob(isDzenNoch: Boolean) {
        if (startTimeJob?.isActive != true) {
            startTimeJob = CoroutineScope(Dispatchers.Main).launch {
                dzenNoch = isDzenNoch
                /*val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                prefEditors.putBoolean("dzen_noch", isDzenNoch)
                prefEditors.apply()*/
                recreate()
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            sensorChangeDzenNoch(sensorEvent.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun sensorChangeDzenNoch(sensorValue: Float) {
        if (!ferstStart) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 6000) {
                return
            }
        }
        if (myTimer?.isActive != true) {
            myTimer = CoroutineScope(Dispatchers.Main).launch {
                if (!ferstStart) delay(1000)
                when {
                    sensorValue <= 4f -> {
                        if (!dzenNoch && !checkDzenNoch) {
                            timeJob(true)
                        }
                    }

                    sensorValue >= 21f -> {
                        if (dzenNoch && checkDzenNoch) {
                            timeJob(false)
                        }
                    }

                    else -> {
                        if (dzenNoch != checkDzenNoch) {
                            timeJob(!dzenNoch)
                        }
                    }
                }
            }
        }
        ferstStart = false
    }

    override fun onPause() {
        super.onPause()
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM) == Settings.MODE_NIGHT_AUTO) {
            setlightSensor()
        }
    }

    private fun onBack() {
        /*if (snackbar?.isShown == true) {
            snackbar?.dismiss()
        }*/
        //if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true)
            finish()
            /*for ((key) in k.all) {
                if (key.contains("Scroll") || key.contains("position")) {
                    prefEditors.remove(key)
                }
            }
            prefEditors.remove("search_svityx_string")
            prefEditors.remove("search_string")
            prefEditors.remove("search_array")
            prefEditors.remove("search_bogashugbovya_string")
            prefEditors.remove("search_bogashugbovya_array")
            prefEditors.remove("search_bible_fierstPosition")
            prefEditors.remove("search_position")
            prefEditors.putBoolean("BibliotekaUpdate", false)
            prefEditors.putBoolean("autoscroll", false)
            prefEditors.putBoolean("setAlarm", true)
            prefEditors.apply()
            val dir = File("$filesDir/cache")
            val list = dir.listFiles()
            list?.forEach {
                it.delete()
            }*/
            //super.onBack()
        } else {
            backPressed = System.currentTimeMillis()
            val mes = Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT)
            mes.show()
        }
        /*} else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }*/
    }

    private fun setlightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
    }

    private fun removelightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.unregisterListener(this, lightSensor)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("dzenNoch", dzenNoch)
    }

    companion object {
        private val storage: FirebaseStorage
            get() = Firebase.storage
        val referens: StorageReference
            get() = storage.reference
    }
}
/*@Composable
fun Greeting(name : String, modifier : Modifier = Modifier) {
val expanded = remember { mutableStateOf(false)
}
val extraPadding = if (expanded.value) 48.dp else 0.dp
Surface(
color = MaterialTheme.colorScheme.primary,
modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
) {
Row(modifier = Modifier.padding(24.dp)) {
    Column(
        modifier = modifier.weight(1f)
            .fillMaxWidth()
            .padding(extraPadding)
    ) {
        Text(text = "Hello ")
        Text(text = name)
    }
    ElevatedButton(
        onClick = { expanded.value = !expanded.value }
    ) {
        Text(if (expanded.value) "Show less" else "Show more")
    }
}
}
}

@Composable
fun MyApp(
modifier : Modifier = Modifier,
names : List<String> = listOf("World", "Compose")
) {
Surface(
modifier = modifier,
color = MaterialTheme.colorScheme.background
) {
Column(modifier.padding(vertical = 4.dp)) {
    for (name in names) {
        Greeting(name = name)
    }
}
}
}*/