package by.carkva_gazeta.malitounik.admin

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.Base64
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toHtml
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.DialogNoWiFI
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.PdfDocumentAdapter
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.views.AppDropdownMenu
import by.carkva_gazeta.malitounik.views.PlainTooltip
import by.carkva_gazeta.malitounik.writeFile
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.security.MessageDigest

class Piasochnica : ViewModel() {
    var history = ArrayList<History>()
    var isHTML by mutableStateOf(true)
    var isDialogSaveFileExplorer by mutableStateOf(false)
    val findDirAsSave = ArrayList<String>()
    var isProgressVisable by mutableStateOf(false)
    var isBackPressVisable by mutableStateOf(false)
    var htmlText by mutableStateOf(SpannableStringBuilder())
    val fileList = mutableStateListOf<MyNetFile>()
    var md5sumLocalFile = "0"
    val backCopy = mutableStateListOf<String>()

    fun getDirListRequest(dir: String) {
        val context = Malitounik.applicationContext()
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    isProgressVisable = true
                    fileList.clear()
                    val temp = ArrayList<MyNetFile>()
                    val list = Malitounik.referens.child("/$dir").list(1000).await()
                    if (dir != "") {
                        val t1 = dir.lastIndexOf("/")
                        temp.add(MyNetFile(R.drawable.directory_up, dir.substring(t1 + 1)))
                    }
                    list.prefixes.forEach {
                        temp.add(MyNetFile(R.drawable.directory_icon, it.name))
                    }
                    list.items.forEach {
                        if (it.name.contains(".htm")) {
                            temp.add(MyNetFile(R.drawable.file_html_icon, it.name))
                        } else if (it.name.contains(".json")) {
                            temp.add(MyNetFile(R.drawable.file_json_icon, it.name))
                        } else if (it.name.contains(".php")) {
                            temp.add(MyNetFile(R.drawable.file_php_icon, it.name))
                        } else {
                            temp.add(MyNetFile(R.drawable.file_txt_icon, it.name))
                        }
                    }
                    fileList.addAll(temp)
                } catch (_: Throwable) {
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                isProgressVisable = false
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getPasochnicaFileList(count: Int = 0): ArrayList<PaisochnicaFileList> {
        if (findDirAsSave.isEmpty()) {
            getFindFileListAsSave()
        }
        var error = false
        val fileList = ArrayList<PaisochnicaFileList>()
        try {
            val list = Malitounik.referens.child("/admin/piasochnica").list(500).addOnFailureListener {
                error = true
            }.await()
            list.items.forEach {
                val metadata = it.metadata.await()
                val isFileExists = findDirAsSave(it.name)
                fileList.add(PaisochnicaFileList(it.name, metadata.updatedTimeMillis, isFileExists))
            }
        } catch (_: Throwable) {
            error = true
        }
        if (count == 3) Toast.makeText(Malitounik.applicationContext(), Malitounik.applicationContext().getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
        if (error && count < 3) {
            getPasochnicaFileList(count + 1)
            return ArrayList()
        }
        return fileList
    }

    fun isFilePiasochnicaExitst(resours: String, fileList: SnapshotStateList<PaisochnicaFileList>): Boolean {
        for (i in 0 until fileList.size) {
            if (fileList[i].fileName.contains(resours)) {
                return true
            }
        }
        return false
    }

    fun getFileCopyPostRequest(dirToFile: String, isProgressVisable: (Boolean) -> Unit, result: (String, String) -> Unit) {
        val context = Malitounik.applicationContext()
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                isProgressVisable(true)
                val t5 = dirToFile.lastIndexOf("/")
                val fileName = dirToFile.substring(t5 + 1)
                val localFile = File("${context.filesDir}/cache/cache.txt")
                var error = false
                try {
                    Malitounik.referens.child("/$dirToFile").getFile(localFile).addOnFailureListener {
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                        error = true
                    }.await()
                } catch (_: Throwable) {
                    error = true
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                val newFileName = fileName.replace("\n", " ")
                try {
                    Malitounik.referens.child("/admin/piasochnica/$newFileName").putFile(Uri.fromFile(localFile)).addOnFailureListener {
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                        error = true
                    }.await()
                } catch (_: Throwable) {
                    error = true
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                var text = if (error) "" else localFile.readText()
                text = if (Settings.dzenNoch) text.replace("#d00505", "#ff6666", true)
                else text
                result(text, newFileName)
                isProgressVisable(false)
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    fun getFileRenamePostRequest(oldFileName: String, fileName: String, isSite: Boolean, update: () -> Unit = {}) {
        val context = Malitounik.applicationContext()
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    val localFile = File("${context.filesDir}/cache/cache.txt")
                    if (isSite) {
                        Malitounik.referens.child("/$oldFileName").getFile(localFile).addOnFailureListener {
                            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }.await()
                        Malitounik.referens.child("/$oldFileName").delete().await()
                        Malitounik.referens.child("/$fileName").putFile(Uri.fromFile(localFile)).await()
                    } else {
                        Malitounik.referens.child("/admin/piasochnica/$oldFileName").getFile(localFile).addOnFailureListener {
                            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }.await()
                        Malitounik.referens.child("/admin/piasochnica/$oldFileName").delete().await()
                        Malitounik.referens.child("/admin/piasochnica/$fileName").putFile(Uri.fromFile(localFile)).await()
                    }
                    update()
                } catch (_: Throwable) {
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                if (isSite) saveLogFile()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getPasochnicaFile(resours: String, result: (SpannableStringBuilder, String) -> Unit, count: Int = 0) {
        val context = Malitounik.applicationContext()
        val localFile = File("${context.filesDir}/cache/cache.txt")
        var error = false
        try {
            Malitounik.referens.child("/admin/piasochnica/$resours").getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) {
                    var text = localFile.readText()
                    text = if (Settings.dzenNoch) text.replace("#d00505", "#ff6666", true)
                    else text
                    val htmlText = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT) as SpannableStringBuilder
                    result(htmlText, text)
                } else {
                    error = true
                }
            }.await()
        } catch (_: Throwable) {
            error = true
        }
        if (error && count < 3) {
            getPasochnicaFile(resours = resours, result = { _, _ -> }, count = count + 1)
        }
    }

    private suspend fun findFile(list: ListResult? = null) {
        val nawList = list ?: Malitounik.referens.child("/admin").list(1000).await()
        nawList.items.forEach {
            findDirAsSave.add(it.path)
        }
        nawList.prefixes.forEach {
            if (it.name != "piasochnica") {
                val rList = Malitounik.referens.child(it.path).list(1000).await()
                findFile(rList)
            }
        }
    }

    suspend fun getFindFileListAsSave(count: Int = 0) {
        try {
            findFile()
        } catch (_: Throwable) {
            if (count == 3) Toast.makeText(Malitounik.applicationContext(), Malitounik.applicationContext().getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
            if (count < 3) {
                findDirAsSave.clear()
                getFindFileListAsSave(count + 1)
            }
        }
    }

    fun addHistory(s: Editable?, editPosition: Int) {
        s?.let {
            if (it.toString() != "") {
                if (history.size == 51) history.removeAt(0)
                history.add(History(it.toSpannable(), editPosition))
            }
            isBackPressVisable = history.size > 1
        }
    }

    fun getFileUnlinkPostRequest(fileName: String, isSite: Boolean) {
        val context = Malitounik.applicationContext()
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    if (isSite) {
                        Malitounik.referens.child("/admin/piasochnica/$fileName").delete().await()
                    } else {
                        Malitounik.referens.child("/$fileName").delete().await()
                    }
                } catch (_: Throwable) {
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                if (!isSite) saveLogFile()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    fun findResoursDir(fileName: String): String {
        var resours = "bogashlugbovya_error.html"
        for (i in 0 until findDirAsSave.size) {
            if (findDirAsSave[i].contains(fileName)) {
                resours = findDirAsSave[i]
                break
            }
        }
        return resours
    }

    suspend fun saveLogFile(count: Int = 0) {
        val context = Malitounik.applicationContext()
        val logFile = File("${context.filesDir}/cache/log.txt")
        var error = false
        logFile.writer().use {
            it.write(context.getString(R.string.check_update_resourse))
        }
        Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
            error = true
        }.await()
        if (error && count < 3) {
            saveLogFile(count + 1)
        }
    }

    fun crateNewFilePiasochnica(newFile: String) {
        val context = Malitounik.applicationContext()
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                val localFile = File("${context.filesDir}/cache/cache.txt")
                localFile.writer().use {
                    it.write("")
                }
                Malitounik.referens.child("/admin/piasochnica/$newFile").putFile(Uri.fromFile(localFile)).await()
            }
        }
    }

    fun sendSaveAsPostRequest(dirToFile: String, fileName: String, count: Int = 0) {
        val context = Malitounik.applicationContext()
        var error = false
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                isProgressVisable = true
                try {
                    val localFile = File("${context.filesDir}/cache/cache.txt")
                    Malitounik.referens.child("/admin/piasochnica/$fileName").getFile(localFile).addOnFailureListener {
                        error = true
                    }.await()
                    val t3 = dirToFile.lastIndexOf("/")
                    val newFile = dirToFile.substring(t3 + 1)
                    val newDir = dirToFile.take(t3 + 1)
                    Malitounik.referens.child("/$newDir$newFile").putFile(Uri.fromFile(localFile)).addOnFailureListener {
                        error = true
                    }.await()
                    Malitounik.referens.child("/admin/piasochnica/$fileName").delete().addOnFailureListener {
                        error = true
                    }.await()
                    Malitounik.referens.child("/admin/piasochnica/$newFile").putFile(Uri.fromFile(localFile)).addOnFailureListener {
                        error = true
                    }.await()
                    if (!error) {
                        findDirAsSave.add("/$newDir$newFile")
                        val metadata = Malitounik.referens.child("/$newDir$newFile").metadata.await()
                        if (md5sumLocalFile.trim().compareTo(metadata.md5Hash?.trim() ?: "0") == 0) {
                            File(context.getExternalFilesDir("PiasochnicaBackCopy"), fileName).delete()
                            var remove = -1
                            for (i in backCopy.indices) {
                                if (backCopy[i] == fileName) {
                                    remove = i
                                    break
                                }
                            }
                            if (remove != -1) backCopy.removeAt(remove)
                        }
                    }
                } catch (_: Throwable) {
                    error = true
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
                if (error && count < 3) {
                    sendSaveAsPostRequest(dirToFile, fileName, count + 1)
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                    saveLogFile()
                }
                isProgressVisable = false
            }
        }
    }

    private fun getOrSendFilePostRequest(resours: String, content: String, saveAs: Boolean) {
        val context = Malitounik.applicationContext()
        val dir = context.getExternalFilesDir("PiasochnicaBackCopy")
        dir?.let { dir ->
            if (!dir.exists()) dir.mkdir()
        }
        val file = File(context.getExternalFilesDir("PiasochnicaBackCopy"), resours)
        file.writer().use {
            it.write(content)
        }
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(file.readText().toByteArray())
        val digest = messageDigest.digest()
        md5sumLocalFile = Base64.encodeToString(digest, Base64.DEFAULT)
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                isProgressVisable = true
                if (findDirAsSave.isEmpty()) {
                    getFindFileListAsSave()
                }
                try {
                    val referens = Malitounik.referens.child("/admin/piasochnica/$resours")
                    referens.putFile(Uri.fromFile(file)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (saveAs) {
                                if (findDirAsSave(resours)) {
                                    sendSaveAsPostRequest(getDirAsSave(resours), resours)
                                } else {
                                    isDialogSaveFileExplorer = true
                                }
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                    if (!saveAs) {
                        val metadata = Malitounik.referens.child("/admin/piasochnica/$resours").metadata.await()
                        if (md5sumLocalFile.trim().compareTo(metadata.md5Hash?.trim() ?: "0") == 0) {
                            file.delete()
                            var remove = -1
                            for (i in backCopy.indices) {
                                if (backCopy[i] == resours) {
                                    remove = i
                                    break
                                }
                            }
                            if (remove != -1) backCopy.removeAt(remove)
                        }
                    }
                } catch (_: Throwable) {
                    Toast.makeText(context, context.getString(R.string.error_ch2), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    fun findDirAsSave(resours: String): Boolean {
        if (resours != "") {
            for (i in 0 until findDirAsSave.size) {
                val t1 = findDirAsSave[i].lastIndexOf("/")
                if (t1 != -1 && findDirAsSave[i].substring(t1 + 1) == resours) {
                    return true
                }
            }
        }
        return false
    }

    private fun getDirAsSave(resours: String): String {
        var result = ""
        if (resours != "") {
            for (i in 0 until findDirAsSave.size) {
                if (findDirAsSave[i].contains(resours)) {
                    result = findDirAsSave[i]
                    break
                }
            }
        }
        return result
    }

    private fun clearColor(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<font color=\"#d00505\">", position)
            val t2 = result.indexOf("</font>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 22, t2)
                val oldSubText = result.substring(t1, t2 + 7)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</font>", position)
            val t2 = result.indexOf("<font color=\"#d00505\">", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 7, t2)
                val oldSubText = result.substring(t1, t2 + 22)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearBold(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<strong>", position)
            val t2 = result.indexOf("</strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 8, t2)
                val oldSubText = result.substring(t1, t2 + 9)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</strong>", position)
            val t2 = result.indexOf("<strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 9, t2)
                val oldSubText = result.substring(t1, t2 + 8)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearEm(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<em>", position)
            val t2 = result.indexOf("</em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 4, t2)
                val oldSubText = result.substring(t1, t2 + 5)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</em>", position)
            val t2 = result.indexOf("<em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 5, t2)
                val oldSubText = result.substring(t1, t2 + 4)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearHtml(text: String): String {
        var result = text
        val t1 = result.indexOf("<p")
        if (t1 != -1) {
            val t2 = result.indexOf(">")
            val subString = result.substring(t1, t2 + 1)
            var stringres = result.replace(subString, "")
            stringres = stringres.replace("</p>", "<br>")
            stringres = stringres.replace("<span", "<font")
            stringres = stringres.replace("</span>", "</font>")
            stringres = stringres.replace("style=\"color:#D00505;\"", "color=\"#d00505\"")
            stringres = stringres.replace("style=\"color:#FF6666;\"", "color=\"#d00505\"")
            stringres = stringres.replace("<i>", "<em>")
            stringres = stringres.replace("</i>", "</em>")
            stringres = stringres.replace("<b>", "<strong>")
            stringres = stringres.replace("</b>", "</strong>")
            stringres = stringres.replace("<u>", "")
            stringres = stringres.replace("</u>", "")
            val t3 = stringres.lastIndexOf("<br>")
            result = stringres.take(t3)
        }
        return result
    }

    fun saveResult(resours: String, saveAs: Boolean) {
        if (isHTML) {
            var result = htmlText.toHtml(HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
            result = StringEscapeUtils.unescapeHtml4(result)
            result = clearHtml(result)
            result = clearColor(result)
            result = clearEm(result)
            result = clearBold(result)
            result = clearEm(result)
            result = clearColor(result)
            result = clearBold(result)
            result = clearEm(result)
            result = result.replace(" ", "")
            if (!result.contains("<!DOCTYPE HTML>")) result = "<!DOCTYPE HTML>$result"
            getOrSendFilePostRequest(resours, result, saveAs)
        } else {
            getOrSendFilePostRequest(resours, htmlText.toString(), saveAs)
        }
    }

    fun sendPostRequest(svityia: String, chtenieSvaitomu: String, style: Int, tipicon: String, titleCytanne: String, cytanne: String, dayOfPascha: Int, isLoad: (Boolean) -> Unit) {
        val context = Malitounik.applicationContext()
        if (Settings.isNetworkAvailable(context)) {
            viewModelScope.launch {
                isLoad(true)
                var myTipicon = tipicon
                if (myTipicon == "0") myTipicon = ""
                if (svityia != "") {
                    var myStyle = 8
                    when (style) {
                        0 -> myStyle = 6
                        1 -> myStyle = 7
                        2 -> myStyle = 8
                    }
                    val localFile2 = File("${context.filesDir}/cache/cache.txt")
                    val sviatyiaNewList = ArrayList<ArrayList<String>>()
                    Malitounik.referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val sviatyiaNew = localFile2.readLines()
                            for (element in sviatyiaNew) {
                                val re1 = element.split("<>")
                                val list = ArrayList<String>()
                                for (element2 in re1) {
                                    list.add(element2)
                                }
                                sviatyiaNewList.add(list)
                            }
                            if (sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][0] != svityia || sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][1] != chtenieSvaitomu || sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][2] != myStyle.toString() || sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][3] != myTipicon) {
                                sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][0] = svityia
                                sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][1] = chtenieSvaitomu
                                sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][2] = myStyle.toString()
                                sviatyiaNewList[Settings.data[Settings.caliandarPosition][24].toInt() - 1][3] = myTipicon
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                    val sb = StringBuilder()
                    for (i in 0 until 366) {
                        sb.append(sviatyiaNewList[i][0] + "<>" + sviatyiaNewList[i][1] + "<>" + sviatyiaNewList[i][2] + "<>" + sviatyiaNewList[i][3] + "\n")
                    }
                    val localFile3 = File("${context.filesDir}/cache/cache2.txt")
                    if (sviatyiaNewList.isNotEmpty()) {
                        localFile3.writer().use {
                            it.write(sb.toString())
                        }
                    }
                    sb.clear()
                    if (sviatyiaNewList.isNotEmpty()) {
                        Malitounik.referens.child("/calendarsviatyia.txt").putFile(Uri.fromFile(localFile3)).await()
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
                val localFile1 = File("${context.filesDir}/cache/cache3.txt")
                val localFile3 = File("${context.filesDir}/cache/cache4.txt")
                val year = Settings.data[Settings.caliandarPosition][3].toInt()
                val sb = StringBuilder()
                val preList = "<?php\n" +
                        "/***********************************************************************\n" +
                        "*                      Літургічны каляндар                             *\n" +
                        "* ==================================================================== *\n" +
                        "*                                                                      *\n" +
                        "* Copyright (c) 2014 by Oleg Dydyshko                                  *\n" +
                        "* http://carkva-gazeta.by                                              *\n" +
                        "*                                                                      *\n" +
                        "* This program is free software. You can redistribute it and/or modify *\n" +
                        "* it under the terms of the GNU General Public License as published by *\n" +
                        "* the Free Software Foundation; either version 2 of the License.       *\n" +
                        "*                                                                      *\n" +
                        "***********************************************************************/\n" +
                        "\n" +
                        "//Здесь Очередные чтения, Святые и праздники привязаные к Пасхе\n" +
                        "//\n" +
                        "/*******************************************************************************\n" +
                        "* Основной формат: Лк 10.38-11.2, Лк 10.38-42                                  *\n" +
                        "* Допустимые значения: Лк 10.38, 10.38-11.2, 10.38-42, 10.38, 38               *\n" +
                        "* Недостающие элементы чтений автоматически добавляются из предыдущего чтения  *\n" +
                        "* Расположение других элементов - произвольно                                  *\n" +
                        "* Тэг <br> - перенос строки                                                    *\n" +
                        "* BAG(ошибка): чтение Дз 6.8-7.5, 47-60 выведет Дз 6.8-7.5, Дз 6.47-60         *\n" +
                        "* BAG 2: Ян 6.35б-39 выведет Ян 6.35-39                                        *\n" +
                        "********************************************************************************/\n"
                sb.append(preList)
                Malitounik.referens.child("/calendar-cytanne_$year.php").getFile(localFile1).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var countDay = 0
                        var countDayNovyGog = 0
                        var calPos = 0
                        var calPosNovyGod = -1
                        Settings.data.forEachIndexed { index, strings ->
                            if (strings[3].toInt() == year && calPosNovyGod == -1) {
                                calPosNovyGod = index
                            }
                            if (strings[22].toInt() == 0 && strings[3].toInt() == year) {
                                calPos = index
                                return@forEachIndexed
                            }
                        }
                        localFile1.forEachLine { fw ->
                            if (fw.contains($$"$calendar[]")) {
                                var c = Settings.data[calPos + countDay]
                                var myDayOfPasha = c[22].toInt()
                                if (c[3].toInt() != year) {
                                    c = Settings.data[calPosNovyGod + countDayNovyGog]
                                    myDayOfPasha = c[22].toInt()
                                    countDayNovyGog++
                                }
                                countDay++
                                if (dayOfPascha == myDayOfPasha) {
                                    sb.append($$"$calendar[]=array(\"cviaty\"=>\"$${titleCytanne}\", \"cytanne\"=>\"\".$ahref.\"$${cytanne}</a>\");\n")
                                } else {
                                    sb.append("$fw\n")
                                }
                            }
                        }
                        sb.append("?>")
                        localFile3.writer().use {
                            it.write(sb.toString())
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                }.await()
                Malitounik.referens.child("/calendar-cytanne_$year.php").putFile(Uri.fromFile(localFile3)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                }.await()
                saveLogFile()
                isLoad(false)
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Piasochnica(
    navController: NavHostController, resours: String, viewModel: Piasochnica
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    var showDropdown by remember { mutableStateOf(false) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var isProgressVisable by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val editText = remember { EditText(context) }
    var startEditPosition by remember { mutableIntStateOf(0) }
    var endEditPosition by remember { mutableIntStateOf(0) }
    val textWatcher = remember {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                startEditPosition = 0
                endEditPosition = start + count
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.addHistory(s, endEditPosition)
                editText.removeTextChangedListener(this)
                s?.let {
                    viewModel.htmlText = it as SpannableStringBuilder
                }
                editText.addTextChangedListener(this)
                viewModel.isBackPressVisable = viewModel.history.size > 1
            }
        }
    }
    LifecycleResumeEffect(Unit) {
        onPauseOrDispose {
            viewModel.htmlText = editText.text as SpannableStringBuilder
            viewModel.saveResult(resours, false)
        }
    }
    var backPressHandled by remember { mutableStateOf(false) }
    BackHandler(!backPressHandled) {
        if (!backPressHandled) {
            navController.popBackStack()
            backPressHandled = true
        }
    }
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    val maxLine = remember { mutableIntStateOf(1) }
    var isDialogNoWIFIVisable by remember { mutableStateOf(false) }
    var printFile by remember { mutableStateOf("") }
    if (viewModel.isDialogSaveFileExplorer) {
        DialogNetFileExplorer(viewModel = viewModel, fileName = resours, setFile = {
            viewModel.sendSaveAsPostRequest(it, resours)
            viewModel.isDialogSaveFileExplorer = false
        }) {
            viewModel.isDialogSaveFileExplorer = false
        }
    }
    if (isDialogNoWIFIVisable) {
        DialogNoWiFI(
            onDismiss = {
                isDialogNoWIFIVisable = false
            },
            onConfirmation = {
                writeFile(
                    context, printFile, loadComplete = {
                        val printAdapter = PdfDocumentAdapter(context, printFile)
                        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                        val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                        printManager.print(printFile, printAdapter, printAttributes)
                    },
                    inProcess = {
                    })
                isDialogNoWIFIVisable = false
            }
        )
    }
    var expandedUp by remember { mutableStateOf(false) }
    val interactionSourse = remember { MutableInteractionSource() }
    var dialogCrateUrl by remember { mutableStateOf(false) }
    var dialogUrl by remember { mutableStateOf("") }
    if (dialogCrateUrl) {
        DialogCrateURL(setURL = {
            startEditPosition = editText.selectionStart
            endEditPosition = editText.selectionEnd
            if (viewModel.isHTML) {
                val text = editText.text
                val subtext = text.getSpans(startEditPosition, endEditPosition, URLSpan::class.java)
                subtext.forEach {
                    if (it.url.contains(dialogUrl)) {
                        text.removeSpan(it)
                    }
                }
                text.setSpan(URLSpan(dialogUrl), startEditPosition, endEditPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                val text = editText.text.toString()
                val build = with(StringBuilder()) {
                    append(text.take(startEditPosition))
                    append("<a href=\"$dialogUrl\">")
                    append(text.substring(startEditPosition, endEditPosition))
                    append("</a>")
                    append(text.substring(endEditPosition))
                    toString()
                }
                editText.removeTextChangedListener(textWatcher)
                editText.setText(build)
                endEditPosition += 29
                editText.addTextChangedListener(textWatcher)
            }
            viewModel.htmlText = editText.text as SpannableStringBuilder
            viewModel.addHistory(editText.text, editText.selectionEnd)
            dialogCrateUrl = false
        }) {
            dialogCrateUrl = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.clickable {
                            maxLine.intValue = Int.MAX_VALUE
                            coroutineScope.launch {
                                delay(5000L)
                                maxLine.intValue = 1
                            }
                        },
                        text = resours.replace("\n", " "),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = maxLine.intValue,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = Settings.fontInterface.sp
                    )
                },
                navigationIcon = {
                    PlainTooltip(stringResource(R.string.exit_page), TooltipAnchorPosition.Below) {
                        IconButton(
                            onClick = {
                                when {
                                    showDropdown -> {
                                        showDropdown = false
                                    }

                                    else -> {
                                        if (!backPressHandled) {
                                            backPressHandled = true
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back),
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    contentDescription = ""
                                )
                            })
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.htmlText = editText.text as SpannableStringBuilder
                        viewModel.saveResult(resours, true)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.save_as), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    IconButton(onClick = { expandedUp = true }) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    AppDropdownMenu(
                        expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                        DropdownMenuItem(onClick = {
                            expandedUp = false
                            showDropdown = !showDropdown
                        }, text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.format_size), contentDescription = ""
                            )
                        })
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        },
        bottomBar = {
            if (showDropdown) {
                ModalBottomSheet(
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    scrimColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                    onDismissRequest = {
                        showDropdown = false
                    }
                ) {
                    Column {
                        Text(
                            stringResource(R.string.menu_font_size_app),
                            modifier = Modifier.padding(start = 10.dp),
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                        Slider(
                            modifier = Modifier.padding(10.dp),
                            valueRange = 18f..58f,
                            value = fontSize,
                            onValueChange = {
                                k.edit {
                                    putFloat("font_biblia", it)
                                }
                                fontSize = it
                            }, colors = SliderDefaults.colors(inactiveTrackColor = Divider)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .clickable(interactionSource = interactionSourse, indication = null) {}
                    .padding(top = 10.dp)
                    .background(MaterialTheme.colorScheme.onTertiary)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (viewModel.isBackPressVisable) {
                    IconButton(onClick = {
                        editText.removeTextChangedListener(textWatcher)
                        if (viewModel.history.size > 1) {
                            editText.setText(viewModel.history[viewModel.history.size - 2].spannable)
                            val editPosition = if (viewModel.history[viewModel.history.size - 2].editPosition == 0) endEditPosition
                            else viewModel.history[viewModel.history.size - 2].editPosition
                            endEditPosition = editPosition
                            editText.setSelection(endEditPosition)
                            viewModel.history.removeAt(viewModel.history.size - 1)
                        }
                        viewModel.isBackPressVisable = viewModel.history.size > 1
                        editText.addTextChangedListener(textWatcher)
                    }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.back),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                IconButton(onClick = {
                    startEditPosition = editText.selectionStart
                    endEditPosition = editText.selectionEnd
                    if (viewModel.isHTML) {
                        val text = editText.text
                        text?.let { editable ->
                            val subtext = editable.getSpans(startEditPosition, endEditPosition, StyleSpan(Typeface.BOLD)::class.java)
                            var check = false
                            subtext.forEach {
                                if (it.style == Typeface.BOLD) {
                                    check = true
                                    editable.removeSpan(it)
                                }
                            }
                            if (!check) editable.setSpan(StyleSpan(Typeface.BOLD), startEditPosition, endEditPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } else {
                        val text = editText.text.toString()
                        val build = with(StringBuilder()) {
                            append(text.take(startEditPosition))
                            append("<strong>")
                            append(text.substring(startEditPosition, endEditPosition))
                            append("</strong>")
                            append(text.substring(endEditPosition))
                            toString()
                        }
                        editText.removeTextChangedListener(textWatcher)
                        editText.setText(build)
                        endEditPosition += 17
                        editText.addTextChangedListener(textWatcher)
                    }
                    viewModel.htmlText = editText.text as SpannableStringBuilder
                    viewModel.addHistory(editText.text, editText.selectionEnd)
                }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.bold_menu),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
                IconButton(onClick = {
                    startEditPosition = editText.selectionStart
                    endEditPosition = editText.selectionEnd
                    if (viewModel.isHTML) {
                        val text = editText.text
                        text?.let { editable ->
                            val subtext = editable.getSpans(startEditPosition, endEditPosition, StyleSpan(Typeface.ITALIC)::class.java)
                            var check = false
                            subtext.forEach {
                                if (it.style == Typeface.ITALIC) {
                                    check = true
                                    editable.removeSpan(it)
                                }
                            }
                            if (!check) editable.setSpan(StyleSpan(Typeface.ITALIC), startEditPosition, endEditPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } else {
                        val text = editText.text.toString()
                        val build = with(StringBuilder()) {
                            append(text.take(startEditPosition))
                            append("<em>")
                            append(text.substring(startEditPosition, endEditPosition))
                            append("</em>")
                            append(text.substring(endEditPosition))
                            toString()
                        }
                        editText.removeTextChangedListener(textWatcher)
                        editText.setText(build)
                        endEditPosition += 9
                        editText.addTextChangedListener(textWatcher)
                    }
                    viewModel.htmlText = editText.text as SpannableStringBuilder
                    viewModel.addHistory(editText.text, editText.selectionEnd)
                }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.italic),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
                IconButton(onClick = {
                    startEditPosition = editText.selectionStart
                    endEditPosition = editText.selectionEnd
                    if (viewModel.isHTML) {
                        val text = editText.text
                        text?.let { editable ->
                            val subtext = editable.getSpans(startEditPosition, endEditPosition, ForegroundColorSpan::class.java)
                            var check = false
                            subtext.forEach {
                                if (it.foregroundColor == ContextCompat.getColor(context, if (Settings.dzenNoch) R.color.colorPrimary_black else R.color.colorPrimary)) {
                                    check = true
                                    editable.removeSpan(it)
                                }
                            }
                            if (!check) editable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, if (Settings.dzenNoch) R.color.colorPrimary_black else R.color.colorPrimary)), startEditPosition, endEditPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } else {
                        val text = editText.text.toString()
                        val build = with(StringBuilder()) {
                            append(text.take(startEditPosition))
                            append("<font color=\"#d00505\">")
                            append(text.substring(startEditPosition, endEditPosition))
                            append("</font>")
                            append(text.substring(endEditPosition))
                            toString()
                        }
                        editText.removeTextChangedListener(textWatcher)
                        editText.setText(build)
                        endEditPosition += 29
                        editText.addTextChangedListener(textWatcher)
                    }
                    viewModel.htmlText = editText.text as SpannableStringBuilder
                    viewModel.addHistory(editText.text, editText.selectionEnd)
                }) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.red_menu),
                        contentDescription = ""
                    )
                }
                if (!viewModel.isHTML) {
                    IconButton(onClick = {
                        endEditPosition = editText.selectionEnd
                        val text = editText.text.toString()
                        val build = with(StringBuilder()) {
                            append(text.take(endEditPosition))
                            append("<br>")
                            append(text.substring(endEditPosition))
                            toString()
                        }
                        editText.removeTextChangedListener(textWatcher)
                        editText.setText(build)
                        viewModel.htmlText = editText.text as SpannableStringBuilder
                        startEditPosition = 0
                        endEditPosition += 4
                        editText.addTextChangedListener(textWatcher)
                        viewModel.addHistory(editText.text, editText.selectionEnd)
                    }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.br_menu),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                IconButton(onClick = {
                    val startSelect = editText.selectionStart
                    val endSelect = editText.selectionEnd
                    if (startSelect == endSelect) {
                        Toast.makeText(context, "Памылка. Абярыце тэкст", Toast.LENGTH_LONG).show()
                    } else {
                        val text = editText.text
                        val urlSpan = text?.getSpans(startSelect, endSelect, URLSpan::class.java)
                        urlSpan?.forEach {
                            dialogUrl = it.url
                        }
                        dialogCrateUrl = true
                    }
                }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.a_menu),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Top
            ) {
                if (isProgressVisable) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = innerPadding.calculateBottomPadding())
                        .imePadding(),
                    factory = {
                        editText.apply {
                            setText(viewModel.htmlText)
                            setTextColor(ContextCompat.getColor(context, if (Settings.dzenNoch) R.color.colorWhite else R.color.colorPrimary_text))
                            setLinkTextColor(ContextCompat.getColor(context, if (Settings.dzenNoch) R.color.colorPrimary_black else R.color.colorPrimary))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                setTextCursorDrawable(ContextCompat.getDrawable(context, if (Settings.dzenNoch) R.color.colorPrimary_black else R.color.colorPrimary))
                            }
                            setTypeface(ResourcesCompat.getFont(context, R.font.roboto_condensed_regular))
                            textSize = fontSize
                            addTextChangedListener(textWatcher)
                        }
                    },
                    update = { editText ->
                        editText.removeTextChangedListener(textWatcher)
                        editText.setText(viewModel.htmlText)
                        if (startEditPosition == 0) editText.setSelection(endEditPosition)
                        else editText.setSelection(startEditPosition, endEditPosition)
                        editText.setTextColor(ContextCompat.getColor(context, if (Settings.dzenNoch) R.color.colorWhite else R.color.colorPrimary_text))
                        editText.textSize = fontSize
                        editText.addTextChangedListener(textWatcher)
                    },
                    onRelease = {
                        keyboardController?.hide()
                    }
                )
            }
        }
    }
}

@Composable
fun DialogCrateURL(
    setURL: (String) -> Unit, onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var textFieldValueState by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.admin_set_name_link),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                TextField(
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                    placeholder = { Text(stringResource(R.string.set_url), fontSize = Settings.fontInterface.sp) },
                    value = textFieldValueState,
                    onValueChange = {
                        textFieldValueState = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onDone = {
                        setURL(textFieldValueState)
                        onDismiss()
                    })
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.cansel), fontSize = 18.sp)
                    }
                    TextButton(
                        onClick = {
                            setURL(textFieldValueState)
                            onDismiss()
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

data class History(val spannable: Spannable, val editPosition: Int)