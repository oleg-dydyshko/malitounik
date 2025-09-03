package by.carkva_gazeta.malitounik

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
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
            get() = Firebase.storage
        val referens: StorageReference
            get() = storage.reference

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}