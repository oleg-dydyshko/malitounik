package by.carkva_gazeta.malitounik.admin

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.core.view.size
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Job
import java.io.File

abstract class BaseActivity : AppCompatActivity(), MenuProvider {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var checkDzenNoch = false
    private var mLastClickTime: Long = 0
    private var startTimeJob: Job? = null
    private var myTimer: Job? = null
    private var ferstStart = false

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        for (i in 0 until menu.size) {
            val item = menu[i]
            val spanString = SpannableString(menu[i].title.toString())
            val end = spanString.length
            var itemFontSize = setFontInterface()
            if (itemFontSize > 22f) itemFontSize = 18f
            spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onMenuItemSelected(item: MenuItem) = false

    open fun onBack() {
        finish()
    }

    override fun attachBaseContext(context: Context) {
        Configuration(context.resources.configuration).apply {
            if (this.fontScale != 1.0f) {
                this.fontScale = 1.0f
            }
            applyOverrideConfiguration(this)
        }
        super.attachBaseContext(context)
        FirebaseApp.initializeApp(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        theme.applyStyle(R.style.OptOutEdgeToEdgeEnforcement, false)
        addMenuProvider(this)
        setTheme(R.style.AppTheme)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
        ferstStart = true
        mLastClickTime = SystemClock.elapsedRealtime()
        k = getSharedPreferences("biblia", MODE_PRIVATE)
        dzenNoch = savedInstanceState?.getBoolean("dzenNoch", false) ?: getBaseDzenNoch()
        checkDzenNoch = dzenNoch
        val file1 = File("$filesDir/BookCache")
        if (file1.exists()) file1.deleteRecursively()
        val file2 = File("$filesDir/Book")
        if (file2.exists()) file2.deleteRecursively()
        val list = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()).listFiles()
        list?.forEach {
            if (it.exists() && it.name.contains("pdf", ignoreCase = true)) it.delete()
        }
    }

    fun getBaseDzenNoch(): Boolean {
        val modeNight = k.getInt("mode_night", Settings.MODE_NIGHT_SYSTEM)
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

            Settings.MODE_NIGHT_AUTO -> {
                dzenNoch = k.getBoolean("dzen_noch", false)
            }
        }
        return dzenNoch
    }

    fun setFontInterface(): Float {
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        return k.getFloat("fontSizeInterface", 20f)
    }

    override fun onPause() {
        super.onPause()
        startTimeJob?.cancel()
        myTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        dzenNoch = getBaseDzenNoch()
        if (checkDzenNoch != getBaseDzenNoch()) {
            recreate()
        }
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.alphain, R.anim.alphaout)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.alphain, R.anim.alphaout)
        } else {
            @Suppress("DEPRECATION") overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("dzenNoch", dzenNoch)
    }
}