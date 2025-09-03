package by.carkva_gazeta.malitounik

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LogView(val context: MainActivity) {
    private var log = ArrayList<String>()
    private var logJob: Job? = null
    private val sb = StringBuilder()
    private val checkSB = StringBuilder()
    private var oldCheckSB = ""
    private var logViewListinner: LogViewListinner? = null

    interface LogViewListinner {
        fun logView(log: String)
    }

    fun setLogViewListinner(listinner: LogViewListinner) {
        logViewListinner = listinner
    }

    fun upDateLog() {
        log.clear()
        sb.clear()
        logJob?.cancel()
        logJob = CoroutineScope(Dispatchers.Main).launch {
            val localFile = File("${context.filesDir}/cache/cache.txt")
            Malitounik.referens.child("/admin/log.txt").getFile(localFile).await()
            var log = ""
            if (localFile.exists()) log = localFile.readText()
            if (log.isNotEmpty()) {
                getLogFile()
            } else {
                logViewListinner?.logView(context.getString(R.string.admin_upload_log_contine))
            }
        }
    }

    fun checkFiles() {
        logJob?.cancel()
        logJob = CoroutineScope(Dispatchers.Main).launch {
            getLogFile()
        }
    }

    private suspend fun getLogFile(count: Int = 0): ArrayList<String> {
        val localFile = File("${context.filesDir}/cache/log.txt")
        var error = false
        Malitounik.referens.child("/admin/adminListFile.txt").getFile(localFile).addOnFailureListener {
            error = true
        }.await()
        if (error && count < 3) {
            getLogFile(count + 1)
            return log
        }
        oldCheckSB = localFile.readText()
        val list = Malitounik.referens.child("/admin").list(1000).await()
        runPrefixes(list, oldCheckSB)
        val list2 = Malitounik.referens.child("/chytanne/Semucha").list(1000).await()
        runItems(list2, oldCheckSB)
        var pathReference = Malitounik.referens.child("/calendarsviatyia.txt")
        addItems(pathReference.path, pathReference.name, oldCheckSB)
        for (year in Settings.GET_CALIANDAR_YEAR_MIN..Settings.GET_CALIANDAR_YEAR_MAX) {
            try {
                pathReference = Malitounik.referens.child("/calendar-cytanne_$year.php")
                addItems(pathReference.path, pathReference.name, oldCheckSB)
            } catch (_: Throwable) {
            }
        }
        checkResources()
        if (log.isEmpty()) {
            logViewListinner?.logView(context.getString(R.string.admin_upload_contine))
            val zip = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MainActivityResource.zip")
            sendAndClearLogFile(zip, isSendLogFile = false, isClearLogFile = true)
        } else {
            val strB = StringBuilder()
            log.forEach {
                strB.append(it)
                strB.append("\n")
            }
            logViewListinner?.logView(strB.toString())
        }
        return log
    }

    private fun checkResources() {
        val list = sb.toString().split("\n")
        val oldList = oldCheckSB.split("\n")
        for (element in oldList.indices) {
            val t11 = oldList[element].indexOf("<name>")
            val t22 = oldList[element].indexOf("</name>")
            if (t11 != -1 && t22 != -1) {
                val name2 = oldList[element].substring(t11 + 6, t22)
                val t33 = name2.indexOf(".")
                val nameRR = if (t33 != -1) name2.substring(0, t33)
                else name2
                var testR = false
                for (i in list.indices) {
                    val t1 = list[i].indexOf("<name>")
                    val t2 = list[i].indexOf("</name>")
                    if (t1 != -1 && t2 != -1) {
                        val name1 = list[i].substring(t1 + 6, t2)
                        val t3 = name1.indexOf(".")
                        val nameR = if (t3 != -1) name1.substring(0, t3)
                        else name1
                        if (nameR == nameRR) {
                            testR = true
                            break
                        }
                    }
                }
                if (!testR) {
                    checkSB.append("firebase: перайменавана альбо выдалена $nameRR\n")
                }
            }
        }
        logViewListinner?.logView(checkSB.toString())
    }

    private suspend fun runPrefixes(list: ListResult, checkList: String) {
        list.prefixes.forEach {
            if (logJob?.isActive != true) return@forEach
            if (it.name != "piasochnica") {
                val list2 = it.list(1000).await()
                runPrefixes(list2, checkList)
                runItems(list2, checkList)
            }
        }
    }

    private suspend fun runItems(list: ListResult, checkList: String) {
        list.items.forEach { storageReference ->
            if (logJob?.isActive != true) return@forEach
            addItems(storageReference.path, storageReference.name, checkList)
        }
    }

    private suspend fun addItems(path: String, name: String, checkList: String, count: Int = 0) {
        val pathReference = Malitounik.referens.child(path)
        var error = false
        val meta = pathReference.metadata.addOnFailureListener {
            error = true
        }.await()
        if (error && count < 3) {
            addItems(path, name, checkList, count + 1)
            return
        }
        val md5Sum = meta.md5Hash ?: 0
        sb.append("<name>")
        sb.append(name)
        sb.append("</name>")
        sb.append("<md5>")
        sb.append(md5Sum)
        sb.append("</md5>\n")
        if (checkList.contains("<name>$name</name>")) {
            val t1 = checkList.indexOf("<name>$name</name>")
            val t2 = checkList.indexOf("<md5>", t1)
            val t3 = checkList.indexOf("</md5>", t2)
            val filemd5Sum = checkList.substring(t2 + 5, t3)
            if (filemd5Sum != md5Sum) {
                log.add(path)
            }
        } else {
            log.add(path)
        }
        logViewListinner?.logView(path)
    }

    fun createAndSentFile() {
        val zip = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MalitounikResource.zip")
        if (log.isNotEmpty() && Settings.isNetworkAvailable(context)) {
            logJob = CoroutineScope(Dispatchers.Main).launch {
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
                        if (t1 != -1) filePath = filePath.substring(0, t1)
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
                    strB.append(checkSB.toString())
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
                sendAndClearLogFile(zip)
            }
        } else {
            sendAndClearLogFile(zip)
        }
    }

    private fun sendAndClearLogFile(zip: File, isClearLogFile: Boolean = true, isSendLogFile: Boolean = true) {
        if (isSendLogFile) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "by.carkva_gazeta.malitounik.fileprovider", zip))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.set_log_file))
            sendIntent.type = "application/zip"
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.set_log_file)))
        }
        if (isClearLogFile && Settings.isNetworkAvailable(context) && sb.toString().isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val logFile = File("${context.filesDir}/cache/log.txt")
                logFile.writer().use {
                    it.write("")
                }
                val localFile = File("${context.filesDir}/cache/cache.txt")
                localFile.writer().use {
                    it.write(sb.toString())
                }
                Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()
                Malitounik.referens.child("/admin/adminListFile.txt").putFile(Uri.fromFile(localFile)).await()
            }
        }
    }
}