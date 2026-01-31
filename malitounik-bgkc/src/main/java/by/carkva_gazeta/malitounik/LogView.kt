package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LogView : ViewModel() {
    private var log = ArrayList<String>()
    private var isDistroy = false
    var logViewText by mutableStateOf("")
    var isLogJob by mutableStateOf(false)

    fun upDateLog(context: Context) {
        if (isLogJob) return
        isDistroy = false
        log.clear()
        viewModelScope.launch {
            isLogJob = true
            val localFile = File("${context.filesDir}/cache/cache.txt")
            Malitounik.referens.child("/admin/log.txt").getFile(localFile).await()
            var log = ""
            if (localFile.exists()) log = localFile.readText()
            if (log.isNotEmpty()) {
                getLogFile(context)
            } else {
                logViewText = context.getString(R.string.admin_upload_log_contine)
            }
            isLogJob = false
        }
    }

    fun onDismiss() {
        isDistroy = true
        isLogJob = false
    }

    fun checkFiles(context: Context) {
        if (isLogJob) return
        viewModelScope.launch {
            isLogJob = true
            getLogFile(context)
            isLogJob = false
        }
    }

    private suspend fun getLogFile(context: Context): ArrayList<String> {
        val list = Malitounik.referens.child("/admin").list(1000).await()
        runPrefixes(list)
        var pathReference = Malitounik.referens.child("/calendarsviatyia.txt")
        addItems(pathReference.path, pathReference.name)
        for (year in Settings.GET_CALIANDAR_YEAR_MIN..Settings.GET_CALIANDAR_YEAR_MAX) {
            try {
                pathReference = Malitounik.referens.child("/calendar-cytanne_$year.txt")
                addItems(pathReference.path, pathReference.name)
            } catch (_: Throwable) {
            }
        }
        if (log.isEmpty()) {
            logViewText = context.getString(R.string.admin_upload_contine)
            val zip = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MainActivityResource.zip")
            sendAndClearLogFile(context, zip, isSendLogFile = false, isClearLogFile = true)
        } else {
            val strB = StringBuilder()
            log.forEach {
                strB.append(it)
                strB.append("\n")
            }
            logViewText = strB.toString()
        }
        return log
    }

    private suspend fun runPrefixes(list: ListResult) {
        list.prefixes.forEach {
            if (isDistroy) return@forEach
            if (it.name != "piasochnica") {
                val list2 = it.list(1000).await()
                runPrefixes(list2)
                runItems(list2)
            }
        }
    }

    private suspend fun runItems(list: ListResult) {
        list.items.forEach { storageReference ->
            if (isDistroy) return@forEach
            addItems(storageReference.path, storageReference.name)
        }
    }

    private suspend fun addItems(path: String, name: String, count: Int = 0) {
        val pathReference = Malitounik.referens.child(path)
        var error = false
        val meta = pathReference.metadata.addOnFailureListener {
            error = true
        }.await()
        if (error && count < 3) {
            addItems(path, name, count + 1)
            return
        }
        val t1 = path.lastIndexOf("/")
        val localPach = when {
            path.contains("bogashlugbovya/") -> "bogashlugbovya/" + path.substring(t1 + 1)
            path.contains("prynagodnyia/") -> "prynagodnyia/" + path.substring(t1 + 1)
            path.contains("parafii_bgkc/") -> "parafii_bgkc/" + path.substring(t1 + 1)
            path.contains("pesny/") -> "pesny/" + path.substring(t1 + 1)
            path.contains("other_files/") -> path.substring(t1 + 1)
            path.contains("calendar") -> path.substring(t1 + 1)
            else -> "bogashlugbovya/" + path.substring(t1 + 1)
        }
        val md5Sum = meta.md5Hash ?: "0"
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(openAssetsResources(Malitounik.applicationContext(), localPach).toByteArray())
        val digest = messageDigest.digest()
        val md5sumLocalFile = Base64.encodeToString(digest, Base64.DEFAULT).trim()
        if (md5Sum != md5sumLocalFile) {
            log.add(path)
        }
        logViewText = path
    }

    fun createAndSentFile(context: Context) {
        val zip = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MalitounikResource.zip")
        if (log.isNotEmpty() && Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(zip)))
                    val localFile = File("${context.filesDir}/cache/cache.txt")
                    val logFile = File("${context.filesDir}/cache/log.txt")
                    val strB = StringBuilder()
                    val buffer = ByteArray(1024)
                    for (index in 0 until log.size) {
                        val file = log[index]
                        var filePath = file.replace("//", "/")
                        val t1 = filePath.indexOf("(")
                        if (t1 != -1) filePath = filePath.take(t1)
                        var error = false
                        try {
                            Malitounik.referens.child(filePath).getFile(localFile).addOnFailureListener {
                                error = true
                            }.await()
                        } catch (_: Throwable) {
                            error = true
                        }
                        if (error) continue
                        val fi = FileInputStream(localFile)
                        val origin = BufferedInputStream(fi)
                        try {
                            val entry = ZipEntry(file.substring(file.lastIndexOf("/")))
                            out.putNextEntry(entry)
                            while (true) {
                                val len = fi.read(buffer)
                                if (len <= 0) break
                                out.write(buffer, 0, len)
                            }
                        } catch (_: Throwable) {
                        } finally {
                            origin.close()
                        }
                        strB.append(file)
                        strB.append("\n")
                    }
                    strB.append("\n")
                    logFile.writer().use {
                        it.write(strB.toString())
                    }
                    val fi = FileInputStream(logFile)
                    val origin = BufferedInputStream(fi)
                    try {
                        val entry = ZipEntry("log.txt")
                        out.putNextEntry(entry)
                        while (true) {
                            val len = fi.read(buffer)
                            if (len <= 0) break
                            out.write(buffer, 0, len)
                        }
                    } catch (_: Throwable) {
                    } finally {
                        origin.close()
                    }
                    out.closeEntry()
                    out.close()
                }
                sendAndClearLogFile(context, zip)
            }
        } else {
            sendAndClearLogFile(context, zip)
        }
    }

    private fun sendAndClearLogFile(context: Context, zip: File, isClearLogFile: Boolean = true, isSendLogFile: Boolean = true) {
        if (isSendLogFile) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "by.carkva_gazeta.malitounik.fileprovider", zip))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.set_log_file))
            sendIntent.type = "application/zip"
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.set_log_file)))
        }
        if (isClearLogFile && Settings.isNetworkAvailable(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val logFile = File("${context.filesDir}/cache/log.txt")
                logFile.writer().use {
                    it.write("")
                }
                Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()
            }
        }
    }
}