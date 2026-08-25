package by.carkva_gazeta.malitounik

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class Malitounik : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: Malitounik? = null
        private val storage: FirebaseStorage
            get() {
                val firebase = Firebase.storage
                firebase.maxOperationRetryTimeMillis = 300000
                return firebase
            }
        val referens: StorageReference
            get() = storage.reference

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}