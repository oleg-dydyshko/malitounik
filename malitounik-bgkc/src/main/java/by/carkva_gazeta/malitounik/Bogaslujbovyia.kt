package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.SpannableStringBuilder
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.admin.PaisochnicaFileList
import by.carkva_gazeta.malitounik.admin.Piasochnica
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Button
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryBlack
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.displayFontFamily
import by.carkva_gazeta.malitounik.views.AppDropdownMenu
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.DialogListinner
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.PlainTooltip
import by.carkva_gazeta.malitounik.views.findCaliandarToDay
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder
import java.util.Calendar
import java.util.Locale

class BogaslujbovyiaViewModel : ViewModel() {
    private lateinit var ttsManager: TTSManager
    var isSpeaking by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
    var autoScroll by mutableStateOf(false)
    var autoScrollSensor by mutableStateOf(false)
    var autoScrollSpeed by mutableIntStateOf(60)
    var autoScrollTextVisable by mutableStateOf(false)
    val vybranoeList = mutableStateListOf<VybranaeDataAll>()
    var isVybranoe by mutableStateOf(false)
    val scrollState = ScrollState(0)
    val result = mutableStateListOf<ArrayList<Int>>()
    var resultPosition by mutableIntStateOf(0)
    var scrollToY = 0f
    var find by mutableStateOf(false)
    private var autoScrollJob: Job? = null
    private var autoScrollTextVisableJob: Job? = null
    private var searchJob: Job? = null
    var searchText by mutableStateOf(AppNavGraphState.searchBogaslujbovyia.isNotEmpty())
    var searshString by mutableStateOf(TextFieldValue(AppNavGraphState.searchBogaslujbovyia, TextRange(AppNavGraphState.searchBogaslujbovyia.length)))
    var searchTextResult by mutableStateOf(AnnotatedString(""))
    var htmlText by mutableStateOf("")
    val srcListTTS = ArrayList<TTS>()
    var dialodTTSHelp by mutableStateOf(false)
    var dialodTTSHelpError by mutableStateOf(false)
    var curentPosition = 0
    var editSearshString = ""
    private var findTTSPosition = 0
    private val gson = Gson()
    private val type = TypeToken.getParameterized(ArrayList::class.java, VybranaeDataAll::class.java).type

    fun search(textLayout: TextLayoutResult?) {
        if (searshString.text.trim().length >= 3) {
            searchJob?.cancel()
            textLayout?.let { layout ->
                searchJob = viewModelScope.launch {
                    result.clear()
                    resultPosition = 0
                    var originalHtmlText = htmlText
                    if (Settings.dzenNoch) originalHtmlText = originalHtmlText.replace("#d00505", "#ff6666", true)
                    val originalText = AnnotatedString.fromHtml(originalHtmlText)
                    result.addAll(findAllAsanc(originalText.text, searshString.text))
                    if (result.isNotEmpty()) {
                        val annotatedString = buildAnnotatedString {
                            append(originalText)
                            for (i in result.indices) {
                                val size = result[i].size - 1
                                addStyle(SpanStyle(background = BezPosta, color = PrimaryText), result[i][0], result[i][size])
                            }
                        }
                        searchTextResult = annotatedString
                        val t1 = result[0][0]
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            scrollToY = layout.getLineTop(line)
                            find = true
                        }
                    } else {
                        searchTextResult = AnnotatedString("")
                    }
                }
            }
        } else {
            searchTextResult = AnnotatedString("")
        }
    }

    fun findForward(textLayout: TextLayoutResult?) {
        textLayout?.let { layout ->
            if (result.isNotEmpty()) {
                if (result.size - 1 > resultPosition) {
                    resultPosition += 1
                } else {
                    resultPosition = 0
                    val context = Malitounik.applicationContext()
                    Toast.makeText(context, context.getString(R.string.find_back), Toast.LENGTH_SHORT).show()
                }
                val t1 = result[resultPosition][0]
                if (t1 != -1) {
                    val line = layout.getLineForOffset(t1)
                    scrollToY = layout.getLineTop(line)
                    find = true
                }
            }
        }
    }

    fun findBack(textLayout: TextLayoutResult?) {
        textLayout?.let { layout ->
            if (result.isNotEmpty()) {
                if (resultPosition > 0) {
                    resultPosition -= 1
                } else {
                    resultPosition = result.size - 1
                    val context = Malitounik.applicationContext()
                    Toast.makeText(context, context.getString(R.string.find_up), Toast.LENGTH_SHORT).show()
                }
                val t1 = result[resultPosition][0]
                if (t1 != -1) {
                    val line = layout.getLineForOffset(t1)
                    scrollToY = layout.getLineTop(line)
                    find = true
                }
            }
        }
    }

    fun initVybranoe(context: Context, resurs: String) {
        if (htmlText.isEmpty()) {
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            autoScrollSpeed = k.getInt("autoscrollSpid", 60)
            htmlText = openAssetsResources(context, resurs)
        }
        val file = File("${context.filesDir}/vybranoe_all.json")
        if (file.exists() && vybranoeList.isEmpty()) {
            vybranoeList.addAll(gson.fromJson(file.readText(), type))
            if (vybranoeList.isNotEmpty()) {
                for (i in 0 until vybranoeList.size) {
                    if (resurs == vybranoeList[i].resource) {
                        isVybranoe = true
                        break
                    }
                }
            }
        }
    }

    fun saveVybranoe(context: Context, title: String, resurs: String) {
        if (isVybranoe) {
            var pos = 0
            for (i in 0 until vybranoeList.size) {
                if (resurs == vybranoeList[i].resource) {
                    pos = i
                    break
                }
            }
            vybranoeList.removeAt(pos)
            Toast.makeText(context, context.getString(R.string.removeVybranoe), Toast.LENGTH_SHORT).show()
        } else {
            vybranoeList.add(0, VybranaeDataAll(Calendar.getInstance().timeInMillis, title, resurs))
            Toast.makeText(context, context.getString(R.string.addVybranoe), Toast.LENGTH_SHORT).show()
        }
        isVybranoe = !isVybranoe
        val file = File("${context.filesDir}/vybranoe_all.json")
        if (vybranoeList.isEmpty() && file.exists()) {
            file.delete()
        } else {
            file.writer().use {
                it.write(gson.toJson(vybranoeList, type))
            }
        }
    }

    fun autoScroll(title: String, isPlay: Boolean) {
        if (isPlay) {
            if (autoScrollJob?.isActive != true) {
                autoScrollJob = viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        while (true) {
                            delay(autoScrollSpeed.toLong())
                            scrollState.scrollBy(2f)
                            AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                        }
                    }
                }
            }
        } else {
            autoScrollJob?.cancel()
        }
        autoScroll = autoScrollJob?.isActive == true
    }

    fun autoScrollSpeed(context: Context) {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        autoScrollTextVisable = true
        autoScrollTextVisableJob?.cancel()
        autoScrollTextVisableJob = viewModelScope.launch {
            delay(3000)
            autoScrollTextVisable = false
        }
        k.edit {
            putInt("autoscrollSpid", autoScrollSpeed)
        }
    }

    suspend fun findAllAsanc(text: String, search: String): ArrayList<ArrayList<Int>> {
        val result = withContext(Dispatchers.Main) {
            return@withContext findAll(text, search.trim())
        }
        return result
    }

    fun findChars(text: String, searchChars: String): ArrayList<FindString> {
        var strSub = 0
        val list = searchChars.toCharArray()
        val stringBuilder = StringBuilder()
        val result = ArrayList<FindString>()
        while (true) {
            val strSub1Pos = text.indexOf(list[0].toString(), strSub, true)
            if (strSub1Pos != -1) {
                stringBuilder.clear()
                strSub = strSub1Pos + 1
                val subChar2 = StringBuilder()
                for (i in 1 until list.size) {
                    if (text.length >= strSub + 1) {
                        if (list[i].isLetterOrDigit()) {
                            var subChar = text.substring(strSub, strSub + 1)
                            if (subChar == "́") {
                                stringBuilder.append(subChar)
                                strSub++
                                if (text.length >= strSub + 1) {
                                    subChar = text.substring(strSub, strSub + 1)
                                }
                            }
                            val strSub2Pos = subChar.indexOf(list[i], ignoreCase = true)
                            if (strSub2Pos != -1) {
                                if (stringBuilder.isEmpty()) stringBuilder.append(text.substring(strSub1Pos, strSub1Pos + 1))
                                if (subChar2.isNotEmpty()) stringBuilder.append(subChar2.toString())
                                stringBuilder.append(list[i].toString())
                                subChar2.clear()
                                strSub++
                            } else {
                                stringBuilder.clear()
                                break
                            }
                        } else {
                            while (true) {
                                if (text.length >= strSub + 1) {
                                    val subChar = text.substring(strSub, strSub + 1).toCharArray()
                                    if (!subChar[0].isLetterOrDigit()) {
                                        subChar2.append(subChar[0])
                                        strSub++
                                    } else {
                                        break
                                    }
                                } else {
                                    break
                                }
                            }
                            if (subChar2.isEmpty()) {
                                stringBuilder.clear()
                                break
                            }
                        }
                    } else {
                        stringBuilder.clear()
                        break
                    }
                }
                if (stringBuilder.toString().isNotEmpty()) {
                    result.add(FindString(stringBuilder.toString(), strSub))
                }
            } else {
                break
            }
        }
        return result
    }

    fun findAll(search: String, searchChars: String): ArrayList<ArrayList<Int>> {
        val findList = ArrayList<ArrayList<Int>>()
        val arraySearsh = ArrayList<FindString>()
        if (searchChars.length >= 3) {
            val findString = findChars(search, searchChars)
            if (findString.isNotEmpty()) arraySearsh.addAll(findString)
            for (i in 0 until arraySearsh.size) {
                val searchLig = arraySearsh[i].str.length
                val strPosition = arraySearsh[i].position - searchLig
                if (strPosition != -1) {
                    val list = ArrayList<Int>()
                    for (e in strPosition..strPosition + searchLig) {
                        list.add(e)
                    }
                    findList.add(list)
                }
            }
        }
        return findList
    }

    fun creteTTSList(textLayout: TextLayoutResult?, ttsPosition: Int = 0) {
        srcListTTS.clear()
        val list = htmlText.replace("\n", "").split("<br><br>")
        var isRed = false
        var positionTTS = 0
        for (i in list.indices) {
            try {
                var positionN = 0
                var positionK = 0
                var positionKold = -1
                val spikText = StringBuilder()
                while (true) {
                    val t1 = list[i].indexOf("<font color=\"#d00505\">", positionN)
                    if (t1 != -1 && t1 < positionKold) {
                        positionN += 22
                        continue
                    }
                    val t2 = list[i].indexOf("</font>", positionK)
                    if (isRed) {
                        if (t2 != -1) {
                            isRed = false
                            positionN = t2 + 7
                            positionK = t2 + 7
                            positionKold = t2
                            continue
                        } else {
                            break
                        }
                    } else {
                        if (t1 != -1) {
                            if (t2 != -1) {
                                spikText.append(
                                    if (positionN == 0) list[i].take(t1)
                                    else list[i].substring(positionKold + 7, t1)
                                )
                                positionN = t1 + 22
                                positionK = t2 + 7
                                positionKold = t2
                                continue
                            } else {
                                isRed = true
                                if (positionKold != -1) spikText.append(list[i].substring(positionKold + 7, t1))
                                else spikText.append(list[i].take(t1))
                                break
                            }
                        } else if (t2 != -1) {
                            spikText.append(list[i].substring(t2 + 7))
                            break
                        } else {
                            if (positionN == 0) spikText.append(list[i])
                            else if (positionK < list[i].length) spikText.append(list[i].substring(positionK))
                            break
                        }
                    }
                }
                var length = AnnotatedString.fromHtml(list[i]).length
                val br = if (i < list.size - 1) {
                    length += 2
                    "<br><br>"
                } else {
                    ""
                }
                srcListTTS.add(TTS(spikText.toString(), positionTTS, positionTTS + length, list[i] + br))
                positionTTS += length
            } catch (_: Throwable) {
                val length = AnnotatedString.fromHtml(list[i]).length + 2
                srcListTTS.add(TTS(list[i], positionTTS, positionTTS + length, list[i] + "<br><br>"))
                positionTTS += length
            }
        }
        val verticalPosition = scrollState.value.toFloat()
        var position = textLayout?.getLineForVerticalPosition(verticalPosition) ?: 0
        var firstLineStartIndex = textLayout?.getLineStart(position) ?: 0
        var firstLineEndIndex = textLayout?.getLineEnd(position, true) ?: 0
        if (firstLineStartIndex == firstLineEndIndex) {
            while (true) {
                position++
                firstLineStartIndex = textLayout?.getLineStart(position) ?: 0
                firstLineEndIndex = textLayout?.getLineEnd(position, true) ?: 0
                if (firstLineStartIndex != firstLineEndIndex) break
            }
        }
        val textH = AnnotatedString.fromHtml(htmlText).text
        val firstVisableString = textH.substring(firstLineStartIndex, firstLineEndIndex)
        if (ttsPosition == 0) {
            findTTSPosition = 0
            for (i in srcListTTS.indices) {
                if (srcListTTS[i].end > firstLineEndIndex) {
                    val t1 = AnnotatedString.fromHtml(srcListTTS[i].textFull).text.indexOf(firstVisableString)
                    if (t1 != -1) {
                        findTTSPosition = i
                        break
                    }
                }
            }
        } else {
            findTTSPosition = ttsPosition
        }
    }

    fun initTTS(context: Context, layout: TextLayoutResult?, isLiturgia: Boolean, navigateTo: (String) -> Unit, isDialogListinner: (String, Int) -> Unit) {
        ttsManager = TTSManager(context, langNotSupported = {
            dialodTTSHelpError = true
        }, speakText = {
            curentPosition = it
            var text = htmlText.replace("\n", "").replace(
                "<!--<VERSION></VERSION>-->",
                "<em>Версія праграмы: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})</em><br><br>"
            )
            if (Settings.dzenNoch) text = text.replace("#d00505", "#ff6666", true)
            val annotatedString = buildAnnotatedString {
                append(
                    AnnotatedString.fromHtml(
                        text,
                        TextLinkStyles(
                            SpanStyle(
                                color = if (Settings.dzenNoch) PrimaryBlack else Primary,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ) { link ->
                        val url = (link as LinkAnnotation.Url).url
                        if (url.contains("https://localhost/")) {
                            when (url) {
                                "https://localhost/pasliachytaeca/" -> {
                                    viewModelScope.launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                }

                                "https://localhost/qr.code/" -> {
                                    isDialogListinner(DialogListinner.DIALOGQRCODE.name, 0)
                                }

                                "https://localhost/shto.novaga/" -> {
                                    isDialogListinner(DialogListinner.DIALOGSZTOHOVAHA.name, 0)
                                }

                                "https://localhost/malitvypasliaprychastia/" -> {
                                    navigateTo("malitvypasliaprychastia")
                                }

                                "https://localhost/pershaiagadzina/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ПЕРШАЯ ГАДЗІНА")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/trecaiagadzina/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ТРЭЦЯЯ ГАДЗІНА")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/shostaiagadzina/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ШОСТАЯ ГАДЗІНА")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/dzeviataiagadzina/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ДЗЯВЯТАЯ ГАДЗІНА")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/zakanchennevialposty/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ЗАКАНЧЭНЬНЕ АБЕДНІЦЫ")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/litciaiblaslavennechl/" -> {
                                    navigateTo("litciaiblaslavennechl")
                                }

                                "https://localhost/zysimprapuskauca/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("10 песьняў")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/vybranyiavershyzpsalm/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 11)
                                }

                                "https://localhost/gltut/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 13)
                                }

                                "https://localhost/gospadzetabeklichu/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("Псалом 140")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/gladzinijai/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ЗАКАНЧЭНЬНЕ ВЯЧЭРНІ Ў ВЯЛІКІ ПОСТ")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/gladztut102/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 1)
                                }

                                "https://localhost/gladztut91/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 2)
                                }

                                "https://localhost/gladztut145/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 3)
                                }

                                "https://localhost/gladztut92/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 4)
                                }

                                "https://localhost/gladztut94/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 10)
                                }

                                "https://localhost/inshyantyfon/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 5)
                                }

                                "https://localhost/malitvazapamerlyx/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 6)
                                }

                                "https://localhost/malitvazapaclikanyx/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 7)
                                }

                                "https://localhost/uspaminpamerlyxijyvix/" -> {
                                    isDialogListinner(DialogListinner.DIALOGLITURGIA.name, 14)
                                }

                                "https://localhost/adzinarodnesyne/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("Адзінародны Сыне")
                                        val t2 = layout.layoutInput.text.indexOf("Адзінародны Сыне", t1 + 17)
                                        if (t2 != -1) {
                                            val line = layout.getLineForOffset(t2)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/gliadzitutdabraveshchane/" -> {
                                    navigateTo("gliadzitutdabraveshchane")
                                }

                                "https://localhost/autorakkanon/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("АЎТОРАК")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/seradakanon/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("СЕРАДА")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/chacverkanon/" -> {
                                    layout?.let { layout ->
                                        val t1 = layout.layoutInput.text.indexOf("ЧАЦЬВЕР")
                                        if (t1 != -1) {
                                            val line = layout.getLineForOffset(t1)
                                            val y = layout.getLineTop(line)
                                            viewModelScope.launch {
                                                scrollState.animateScrollTo(y.toInt())
                                            }
                                        }
                                    }
                                }

                                "https://localhost/cytanne/" -> {
                                    if (isLiturgia) {
                                        navigateTo("cytanne")
                                    } else {
                                        navigateTo("error")
                                    }
                                }

                                else -> {
                                    val error = context.getString(R.string.error_ch)
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(browserIntent)
                        }
                    }
                )
                addStyle(SpanStyle(textDecoration = TextDecoration.Underline), srcListTTS[it].start, srcListTTS[it].end)
            }
            searchTextResult = annotatedString
            layout?.let { textLayoutResult ->
                val line = textLayoutResult.getLineForOffset(srcListTTS[it].start)
                scrollToY = textLayoutResult.getLineTop(line)
                find = true
            }
        }) {
            isPaused = false
            isSpeaking = false
        }
        viewModelScope.launch {
            ttsManager.initialize()
        }
    }

    fun speak() {
        val list = ArrayList<String>()
        for (i in srcListTTS.indices) {
            val text = (AnnotatedString.fromHtml(srcListTTS[i].textSpik).text)
                .replace("*", "")
                .replace("а́", "а")
            list.add(text)
        }
        ttsManager.speakLongText(list, findTTSPosition)
    }

    fun pause() {
        ttsManager.pause()
    }

    fun resume() {
        ttsManager.resume()
    }

    fun stop() {
        ttsManager.stop()
    }

    fun shutdown() {
        ttsManager.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bogaslujbovyia(
    navController: NavHostController, title: String, resurs: String,
    navigateTo: (String, skipUtran: Boolean) -> Unit = { _, _ -> }, viewModel: BogaslujbovyiaViewModel = viewModel(), adminViewModel: Piasochnica
) {
    val resursEncode = URLDecoder.decode(resurs, "UTF8")
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    var showDropdown by remember { mutableStateOf(false) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var autoScrollText by remember { mutableStateOf("") }
    var autoScrollTextColor by remember { mutableStateOf(Primary) }
    var autoScrollTextColor2 by remember { mutableStateOf(PrimaryTextBlack) }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    var adminResourceEditPosition by remember { mutableIntStateOf(0) }
    val scrollStateDop = rememberScrollState()
    viewModel.initVybranoe(context, resursEncode)
    var isBottomBar by remember { mutableStateOf(k.getBoolean("bottomBar", false)) }
    var backPressHandled by remember { mutableStateOf(false) }
    var iskniga by rememberSaveable { mutableStateOf(false) }
    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var bottomSheetScaffoldIsVisible by rememberSaveable { mutableStateOf(AppNavGraphState.bottomSheetScaffoldIsVisible) }
    val actyvity = LocalActivity.current as MainActivity
    var isShare by rememberSaveable { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var dialogLiturgia by rememberSaveable { mutableStateOf(false) }
    var chast by rememberSaveable { mutableIntStateOf(0) }
    var dialogQrCode by rememberSaveable { mutableStateOf(false) }
    var dialogSztoHovahaVisable by remember { mutableStateOf(false) }
    val isViachernia = resursEncode == "bogashlugbovya/viaczernia_niadzelnaja.html" || resursEncode == "bogashlugbovya/viaczernia_na_kozny_dzen.html" || resursEncode == "bogashlugbovya/viaczernia_u_vialikim_poscie.html" || resursEncode == "bogashlugbovya/viaczerniaja_sluzba_sztodzionnaja_biez_sviatara.html" || resursEncode == "bogashlugbovya/viaczernia_svietly_tydzien.html"
    val isUtran = resursEncode == "bogashlugbovya/jutran_niadzelnaja.html"
    val isLiturgia = resursEncode == "bogashlugbovya/lit_jana_zalatavusnaha.html" || resursEncode == "bogashlugbovya/lit_jan_zalat_vielikodn.html" || resursEncode == "bogashlugbovya/lit_vasila_vialikaha.html" || resursEncode == "bogashlugbovya/abiednica.html" || resursEncode == "bogashlugbovya/vialikdzien_liturhija.html"
    val data = findCaliandarToDay()
    if (viewModel.autoScrollSensor) {
        actyvity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        SheetState(
            skipHiddenState = false,
            skipPartiallyExpanded = true,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            initialValue = SheetValue.Hidden
        )
    )
    LaunchedEffect(viewModel.find) {
        if (viewModel.find) {
                viewModel.scrollState.animateScrollTo(viewModel.scrollToY.toInt())
                AppNavGraphState.setScrollValuePosition(title, viewModel.scrollState.value)
                viewModel.find = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initTTS(
            context, textLayout, isLiturgia && isLiturgia(data), navigateTo = { navigate ->
                navigateTo(navigate, false)
            },
            isDialogListinner = { dialog, chastka ->
                when (dialog) {
                    DialogListinner.DIALOGQRCODE.name -> {
                        dialogQrCode = true
                    }

                    DialogListinner.DIALOGSZTOHOVAHA.name -> {
                        dialogSztoHovahaVisable = true
                    }

                    DialogListinner.DIALOGLITURGIA.name -> {
                        chast = chastka
                        dialogLiturgia = true
                    }
                }
            })
    }
    LaunchedEffect(Settings.dzenNoch) {
        if (viewModel.isPaused || viewModel.isSpeaking) {
            var text = viewModel.htmlText.replace("\n", "")
            if (Settings.dzenNoch) text = text.replace("#d00505", "#ff6666", true)
            val annotatedString = buildAnnotatedString {
                append(AnnotatedString.fromHtml(text))
                addStyle(SpanStyle(textDecoration = TextDecoration.Underline), viewModel.srcListTTS[viewModel.curentPosition].start, viewModel.srcListTTS[viewModel.curentPosition].end)
            }
            viewModel.searchTextResult = annotatedString
            textLayout?.let { textLayoutResult ->
                val line = textLayoutResult.getLineForOffset(viewModel.srcListTTS[viewModel.curentPosition].start)
                viewModel.scrollToY = textLayoutResult.getLineTop(line)
                viewModel.find = true
            }
        }
    }
    LifecycleResumeEffect(Unit) {
        if (AppNavGraphState.searchBogaslujbovyia.isEmpty()) {
            if (resursEncode.contains("akafist")) {
                AppNavGraphState.setScrollValuePosition(title, k.getInt(resursEncode, 0))
            }
            coroutineScope.launch {
                viewModel.scrollState.animateScrollTo(AppNavGraphState.getScrollValuePosition(title))
            }
        } else {
            coroutineScope.launch {
                viewModel.search(textLayout)
                withContext(Dispatchers.IO) {
                    viewModel.scrollState.animateScrollTo(viewModel.scrollToY.toInt())
                    AppNavGraphState.setScrollValuePosition(title, viewModel.scrollState.value)
                    viewModel.find = false
                }
            }
        }
        onPauseOrDispose {
            viewModel.shutdown()
            viewModel.isPaused = false
            viewModel.isSpeaking = false
            AppNavGraphState.setScrollValuePosition(title, viewModel.scrollState.value)
            if (resursEncode.contains("akafist")) {
                k.edit {
                    putInt(resursEncode, viewModel.scrollState.value)
                }
            }
        }
    }
    BackHandler(!backPressHandled || showDropdown || iskniga || viewModel.searchText || isShare) {
        when {
            isShare -> {
                isShare = false
                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            }

            viewModel.searchText -> {
                viewModel.searchText = false
                viewModel.searchTextResult = AnnotatedString("")
                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            }

            iskniga -> {
                if (bottomSheetScaffoldIsVisible) {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.show()
                    }
                }
                iskniga = false
            }

            showDropdown -> {
                showDropdown = false
                if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            }

            !backPressHandled -> {
                AppNavGraphState.searchBogaslujbovyia = ""
                fullscreen = false
                backPressHandled = true
                if (!k.getBoolean("power", false)) actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                viewModel.autoScroll(title, false)
                viewModel.autoScrollSensor = false
                navController.popBackStack()
            }
        }
    }
    var isUpList by remember { mutableStateOf(false) }
    if (isUpList) {
        LaunchedEffect(Unit) {
            isUpList = false
            viewModel.scrollState.animateScrollTo(0)
        }
    }
    val view = LocalView.current
    LaunchedEffect(fullscreen) {
        val controller =
            WindowCompat.getInsetsController((view.context as Activity).window, view)
        if (fullscreen) {
            bottomSheetScaffoldIsVisible = false
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.show(WindowInsetsCompat.Type.navigationBars())
        }
    }
    if (dialogLiturgia) {
        DialogLiturgia(chast) {
            dialogLiturgia = false
        }
    }
    if (dialogQrCode) {
        DialogImage(painter = painterResource(R.drawable.qr_code_google_play)) {
            dialogQrCode = false
        }
    }
    if (dialogSztoHovahaVisable) {
        DialogSztoHovaha {
            dialogSztoHovahaVisable = false
        }
    }
    if (viewModel.dialodTTSHelpError) {
        viewModel.dialodTTSHelp = false
        DialogHelpTTS(perevod = Settings.PEREVODSEMUXI, isError = true) {
            viewModel.isSpeaking = false
            viewModel.isPaused = false
            viewModel.dialodTTSHelpError = false
        }
    }
    if (viewModel.dialodTTSHelp) {
        DialogHelpTTS(isError = false) {
            k.edit {
                putBoolean("isTTSHelp", it)
            }
            viewModel.isSpeaking = true
            viewModel.creteTTSList(textLayout)
            viewModel.speak()
            viewModel.autoScroll(title, false)
            viewModel.autoScrollSensor = false
            viewModel.dialodTTSHelp = false
        }
    }
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
    var subTitle by rememberSaveable { mutableStateOf("") }
    var subText by rememberSaveable { mutableStateOf("") }
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDialogNoWIFIVisable by remember { mutableStateOf(false) }
    var printFile by remember { mutableStateOf("") }
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
    val cytanneVisable = (isLiturgia && isLiturgia(data)) || isViachernia || isUtran
    val listResource = ArrayList<SlugbovyiaTextuData>()
    when {
        isLiturgia -> {
            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.LITURHIJA, data[24].toInt(), data[3].toInt()))
        }

        isUtran -> {
            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.JUTRAN, data[24].toInt(), data[3].toInt()))
        }

        isViachernia -> {
            listResource.addAll(SlugbovyiaTextu().loadSluzbaDayList(SlugbovyiaTextu.VIACZERNIA, data[24].toInt(), data[3].toInt()))
        }
    }
    LaunchedEffect(bottomSheetScaffoldIsVisible) {
        if (listResource.isNotEmpty()) {
            if (!iskniga) {
                coroutineScope.launch {
                    if (bottomSheetScaffoldIsVisible) {
                        bottomSheetScaffoldState.bottomSheetState.show()
                    } else {
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }
        } else {
            AppNavGraphState.bottomSheetScaffoldIsVisible = false
        }
    }
    LaunchedEffect(viewModel.searshString) {
        if (viewModel.editSearshString != viewModel.searshString.text) {
            viewModel.editSearshString = viewModel.searshString.text
            viewModel.editSearshString = viewModel.editSearshString.replace("и", "і")
            viewModel.editSearshString = viewModel.editSearshString.replace("щ", "ў")
            viewModel.editSearshString = viewModel.editSearshString.replace("И", "І")
            viewModel.editSearshString = viewModel.editSearshString.replace("Щ", "Ў")
            viewModel.editSearshString = viewModel.editSearshString.replace("ъ", "'")
            viewModel.searchTextResult = AnnotatedString("")
            if (viewModel.editSearshString != viewModel.searshString.text) {
                val selection = TextRange(viewModel.editSearshString.length)
                viewModel.searshString = TextFieldValue(viewModel.editSearshString, selection)
            }
            AppNavGraphState.searchBogaslujbovyia = viewModel.searshString.text
            viewModel.search(textLayout)
        }
    }
    var paddingValues by remember { mutableStateOf(PaddingValues()) }
    BottomSheetScaffold(
        sheetDragHandle = { },
        sheetSwipeEnabled = false,
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Column(Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                Column {
                    for (i in listResource.indices) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    adminResourceEditPosition = i
                                    subTitle = listResource[i].title
                                    subText = openAssetsResources(context, listResource[i].resource)
                                    iskniga = true
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.hide()
                                    }
                                    viewModel.autoScroll(title, false)
                                    viewModel.autoScrollSensor = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = ""
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = listResource[i].title,
                                fontSize = Settings.fontInterface.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        HorizontalDivider()
                    }
                    if (cytanneVisable) {
                        var skipUtran = false
                        if (data[9].isNotEmpty()) {
                            var chtenie = data[9]
                            if (isLiturgia && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.substring(t1 + 1)
                                skipUtran = true
                            }
                            if (isUtran && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.take(t1)
                            }
                            listResource.add(SlugbovyiaTextuData(0, chtenie, "9", SlugbovyiaTextu.LITURHIJA))
                        }
                        if (data[10].isNotEmpty()) {
                            listResource.add(SlugbovyiaTextuData(0, data[10], "10", SlugbovyiaTextu.LITURHIJA))
                        }
                        if (data[11].isNotEmpty()) {
                            var chtenie = data[11]
                            if (isLiturgia && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.substring(t1 + 1)
                                skipUtran = true
                            }
                            if (isUtran && chtenie.contains("На ютрані", ignoreCase = true)) {
                                val t1 = chtenie.indexOf("\n")
                                if (t1 != -1) chtenie = chtenie.take(t1)
                            }
                            listResource.add(SlugbovyiaTextuData(0, chtenie, "11", SlugbovyiaTextu.LITURHIJA))
                        }
                        for (i in listResource.indices) {
                            if (!(listResource[i].resource == "9" || listResource[i].resource == "10" || listResource[i].resource == "11")) continue
                            val navigate = when (listResource[i].resource) {
                                "10" -> "cytannesvityx"
                                "11" -> "cytannedop"
                                else -> "cytanne"
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.hide()
                                        }
                                        viewModel.autoScroll(title, false)
                                        viewModel.autoScrollSensor = false
                                        navigateTo(navigate, skipUtran)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp),
                                    painter = painterResource(R.drawable.poiter),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = ""
                                )
                                Text(
                                    modifier = Modifier.padding(start = 10.dp),
                                    text = listResource[i].title,
                                    fontSize = Settings.fontInterface.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        paddingValues = innerPadding
        var shareIsLaunch by remember { mutableStateOf(false) }
        val launcherShare = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
            shareIsLaunch = false
            selectedText = ""
        }
        var expandedUp by remember { mutableStateOf(false) }
        LaunchedEffect(expandedUp) {
            if (viewModel.autoScrollSensor && !isShare && !viewModel.searchText) viewModel.autoScroll(title, !expandedUp)
        }
        val interactionSourse = remember { MutableInteractionSource() }
        LaunchedEffect(shareIsLaunch) {
            if (shareIsLaunch) {
                val sent = selectedText.ifEmpty { textLayout?.layoutInput?.text?.text }
                sent?.let { shareText ->
                    val sendIntent = Intent(Intent.ACTION_SEND)
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                    sendIntent.type = "text/plain"
                    launcherShare.launch(Intent.createChooser(sendIntent, title))
                }
            }
        }
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    !fullscreen, enter = fadeIn(
                        tween(
                            durationMillis = 500, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                ) {
                    TopAppBar(
                        title = {
                            if (!viewModel.searchText) {
                                Text(
                                    modifier = Modifier.clickable {
                                        maxLine.intValue = Int.MAX_VALUE
                                        coroutineScope.launch {
                                            delay(5000L)
                                            maxLine.intValue = 1
                                        }
                                    },
                                    text = if (iskniga) subTitle.replace("\n", " ").uppercase() else title.replace("\n", " ").uppercase(),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = maxLine.intValue,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = Settings.fontInterface.sp
                                )
                            } else {
                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester)
                                        .onGloballyPositioned {
                                            if (!textFieldLoaded) {
                                                focusRequester.requestFocus()
                                                textFieldLoaded = true
                                            }
                                        },
                                    value = viewModel.searshString,
                                    onValueChange = { newText ->
                                        viewModel.searshString = newText
                                    },
                                    singleLine = true,
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.search),
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            contentDescription = ""
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            viewModel.searshString = TextFieldValue("")
                                        }) {
                                            Icon(
                                                painter = if (viewModel.searshString.text.isNotEmpty()) painterResource(R.drawable.close) else painterResource(R.drawable.empty),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                                        focusedTextColor = PrimaryTextBlack,
                                        focusedIndicatorColor = PrimaryTextBlack,
                                        unfocusedTextColor = PrimaryTextBlack,
                                        unfocusedIndicatorColor = PrimaryTextBlack,
                                        cursorColor = PrimaryTextBlack
                                    ),
                                    textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                                )
                            }
                        },
                        navigationIcon = {
                            if (iskniga || viewModel.searchText) {
                                PlainTooltip(stringResource(R.string.close), TooltipAnchorPosition.Below) {
                                    IconButton(
                                        onClick = {
                                            if (iskniga) {
                                                if (bottomSheetScaffoldIsVisible) {
                                                    coroutineScope.launch {
                                                        bottomSheetScaffoldState.bottomSheetState.show()
                                                    }
                                                }
                                                iskniga = false
                                            } else {
                                                viewModel.searchText = false
                                                AppNavGraphState.searchBogaslujbovyia = ""
                                            }
                                            if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                        },
                                        content = {
                                            Icon(
                                                painter = painterResource(R.drawable.close),
                                                tint = MaterialTheme.colorScheme.onSecondary,
                                                contentDescription = ""
                                            )
                                        })
                                }
                            } else {
                                PlainTooltip(stringResource(R.string.exit_page), TooltipAnchorPosition.Below) {
                                    IconButton(
                                        onClick = {
                                            when {
                                                iskniga -> {
                                                    coroutineScope.launch {
                                                        bottomSheetScaffoldState.bottomSheetState.show()
                                                    }
                                                    iskniga = false
                                                    if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                                }

                                                showDropdown -> {
                                                    showDropdown = false
                                                    if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
                                                }

                                                else -> {
                                                    if (!backPressHandled) {
                                                        backPressHandled = true
                                                        viewModel.autoScroll(title, false)
                                                        viewModel.autoScrollSensor = false
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
                            }
                        },
                        actions = {
                            if (viewModel.searchText) {
                                PlainTooltip(stringResource(R.string.poshuk_back), TooltipAnchorPosition.Below) {
                                    IconButton(onClick = {
                                        viewModel.findBack(textLayout)
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_upward),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                                PlainTooltip(stringResource(R.string.poshuk_forvard), TooltipAnchorPosition.Below) {
                                    IconButton(onClick = {
                                        viewModel.findForward(textLayout)
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_downward),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            } else {
                                if (!isShare) {
                                    if (!iskniga && listResource.isNotEmpty()) {
                                        PlainTooltip(stringResource(R.string.zmennyia_chastki), TooltipAnchorPosition.Below) {
                                            IconButton(onClick = {
                                                showDropdown = false
                                                coroutineScope.launch {
                                                    bottomSheetScaffoldIsVisible = !bottomSheetScaffoldIsVisible
                                                    AppNavGraphState.bottomSheetScaffoldIsVisible = bottomSheetScaffoldIsVisible
                                                }
                                            }) {
                                                Icon(
                                                    modifier = Modifier.size(24.dp),
                                                    painter = painterResource(R.drawable.book_red),
                                                    contentDescription = "",
                                                    tint = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                        if (k.getBoolean("admin", false) && isBottomBar) {
                                            VerticalDivider()
                                        }
                                    }
                                    if (!iskniga && !isBottomBar) {
                                        if (!(viewModel.isSpeaking || viewModel.isPaused)) {
                                            if (viewModel.scrollState.canScrollForward) {
                                                val iconAutoScroll = if (viewModel.autoScrollSensor) painterResource(R.drawable.stop_circle)
                                                else painterResource(R.drawable.play_circle)
                                                PlainTooltip(stringResource(if (viewModel.autoScrollSensor) R.string.auto_stop else R.string.auto_play), TooltipAnchorPosition.Below) {
                                                    IconButton(onClick = {
                                                        viewModel.autoScrollSensor = !viewModel.autoScrollSensor
                                                        viewModel.autoScroll(title, viewModel.autoScrollSensor)
                                                        if (viewModel.autoScrollSensor) {
                                                            actyvity.window.addFlags(
                                                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                                            )
                                                        } else if (!k.getBoolean("power", false)) {
                                                            actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                                        }
                                                    }) {
                                                        Icon(
                                                            iconAutoScroll,
                                                            contentDescription = "",
                                                            tint = MaterialTheme.colorScheme.onSecondary
                                                        )
                                                    }
                                                }
                                            } else if (viewModel.scrollState.canScrollBackward) {
                                                PlainTooltip(stringResource(R.string.auto_up), TooltipAnchorPosition.Below) {
                                                    IconButton(onClick = {
                                                        isUpList = true
                                                    }) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.arrow_upward),
                                                            contentDescription = "",
                                                            tint = MaterialTheme.colorScheme.onSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (listResource.isEmpty()) {
                                            PlainTooltip(stringResource(if (viewModel.isVybranoe) R.string.vybranae_remove else R.string.vybranae_add), TooltipAnchorPosition.Below) {
                                                IconButton(onClick = {
                                                    viewModel.saveVybranoe(context, title, resursEncode)
                                                }) {
                                                    val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                                    else painterResource(R.drawable.star)
                                                    Icon(
                                                        painter = icon,
                                                        contentDescription = "",
                                                        tint = MaterialTheme.colorScheme.onSecondary
                                                    )
                                                }
                                            }
                                        }
                                        PlainTooltip(stringResource(R.string.more_items), TooltipAnchorPosition.Below) {
                                            IconButton(onClick = { expandedUp = true }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.more_vert), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                        AppDropdownMenu(
                                            expanded = expandedUp, onDismissRequest = { expandedUp = false }) {
                                            if (listResource.isNotEmpty()) {
                                                DropdownMenuItem(onClick = {
                                                    expandedUp = false
                                                    viewModel.saveVybranoe(context, title, resursEncode)
                                                }, text = { Text(stringResource(if (viewModel.isVybranoe) R.string.vybranae_remove else R.string.vybranae_add), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                    val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                                    else painterResource(R.drawable.star)
                                                    Icon(
                                                        painter = icon, contentDescription = ""
                                                    )
                                                })
                                            }
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                viewModel.autoScroll(title, false)
                                                isShare = true
                                            }, text = { Text(stringResource(R.string.share), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.share), contentDescription = ""
                                                )
                                            })
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                fullscreen = true
                                            }, text = { Text(stringResource(R.string.fullscreen), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.fullscreen), contentDescription = ""
                                                )
                                            })
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                viewModel.searchText = true
                                                viewModel.search(textLayout)
                                                viewModel.autoScroll(title, false)
                                            }, text = { Text(stringResource(R.string.searche_text), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.search), contentDescription = ""
                                                )
                                            })
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                showDropdown = !showDropdown
                                                viewModel.autoScroll(title, false)
                                            }, text = { Text(stringResource(R.string.menu_font_size_app), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.format_size), contentDescription = ""
                                                )
                                            })
                                            DropdownMenuItem(onClick = {
                                                expandedUp = false
                                                if (viewModel.isSpeaking || viewModel.isPaused) {
                                                    viewModel.isSpeaking = false
                                                    viewModel.isPaused = false
                                                    viewModel.stop()
                                                } else {
                                                    if (k.getBoolean("isTTSHelp", true)) {
                                                        viewModel.dialodTTSHelp = true
                                                    } else {
                                                        viewModel.isSpeaking = true
                                                        viewModel.creteTTSList(textLayout)
                                                        viewModel.speak()
                                                        viewModel.autoScroll(title, false)
                                                        viewModel.autoScrollSensor = false
                                                    }
                                                }
                                            }, text = { Text(stringResource(R.string.tts), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.text_to_speech), contentDescription = ""
                                                )
                                            })
                                            if (k.getBoolean("admin", false)) {
                                                HorizontalDivider()
                                                DropdownMenuItem(onClick = {
                                                    expandedUp = false
                                                    viewModel.autoScroll(title, false)
                                                    viewModel.viewModelScope.launch {
                                                        isProgressVisable = true
                                                        val fileList = SnapshotStateList<PaisochnicaFileList>()
                                                        fileList.addAll(adminViewModel.getPasochnicaFileList())
                                                        val dirToFile = if (iskniga) listResource[adminResourceEditPosition].resource
                                                        else resursEncode
                                                        adminViewModel.isHTML = dirToFile.contains(".html")
                                                        val t1 = dirToFile.lastIndexOf("/")
                                                        val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
                                                        else dirToFile
                                                        if (adminViewModel.isFilePiasochnicaExitst(fileName, fileList)) {
                                                            coroutineScope.launch {
                                                                isProgressVisable = true
                                                                adminViewModel.getPasochnicaFile(fileName, result = { text ->
                                                                    val html = if (adminViewModel.isHTML) {
                                                                        SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                                    } else {
                                                                        SpannableStringBuilder(text)
                                                                    }
                                                                    adminViewModel.htmlText = html
                                                                    adminViewModel.addHistory(0)
                                                                    navigationActions.navigateToPiasochnica(fileName)
                                                                })
                                                            }
                                                        } else {
                                                            if (adminViewModel.findDirAsSave.isEmpty()) {
                                                                adminViewModel.getFindFileListAsSave()
                                                            }
                                                            adminViewModel.getFileCopyPostRequest(dirToFile = adminViewModel.findResoursDir(fileName), isProgressVisable = {
                                                                isProgressVisable = it
                                                            }) { text, fileName ->
                                                                val html = if (adminViewModel.isHTML) {
                                                                    SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                                } else {
                                                                    SpannableStringBuilder(text)
                                                                }
                                                                adminViewModel.htmlText = html
                                                                adminViewModel.addHistory(0)
                                                                navigationActions.navigateToPiasochnica(fileName)
                                                                isProgressVisable = false
                                                            }
                                                        }
                                                        isProgressVisable = false
                                                    }
                                                }, text = { Text(stringResource(R.string.redagaktirovat), fontSize = (Settings.fontInterface - 2).sp) }, trailingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.edit), contentDescription = ""
                                                    )
                                                })
                                            }
                                        }
                                    }
                                    if (k.getBoolean("admin", false) && (isBottomBar || iskniga)) {
                                        PlainTooltip(stringResource(R.string.redagaktirovat)) {
                                            IconButton(onClick = {
                                                viewModel.autoScroll(title, false)
                                                viewModel.viewModelScope.launch {
                                                    isProgressVisable = true
                                                    val fileList = SnapshotStateList<PaisochnicaFileList>()
                                                    fileList.addAll(adminViewModel.getPasochnicaFileList())
                                                    val dirToFile = if (iskniga) listResource[adminResourceEditPosition].resource
                                                    else resursEncode
                                                    adminViewModel.isHTML = dirToFile.contains(".html")
                                                    val t1 = dirToFile.lastIndexOf("/")
                                                    val fileName = if (t1 != -1) dirToFile.substring(t1 + 1)
                                                    else dirToFile
                                                    if (adminViewModel.isFilePiasochnicaExitst(fileName, fileList)) {
                                                        coroutineScope.launch {
                                                            isProgressVisable = true
                                                            adminViewModel.getPasochnicaFile(fileName, result = { text ->
                                                                val html = if (adminViewModel.isHTML) {
                                                                    SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                                } else {
                                                                    SpannableStringBuilder(text)
                                                                }
                                                                adminViewModel.htmlText = html
                                                                adminViewModel.addHistory(0)
                                                                navigationActions.navigateToPiasochnica(fileName)
                                                            })
                                                        }
                                                    } else {
                                                        if (adminViewModel.findDirAsSave.isEmpty()) {
                                                            adminViewModel.getFindFileListAsSave()
                                                        }
                                                        adminViewModel.getFileCopyPostRequest(dirToFile = adminViewModel.findResoursDir(fileName), isProgressVisable = {
                                                            isProgressVisable = it
                                                        }) { text, fileName ->
                                                            val html = if (adminViewModel.isHTML) {
                                                                SpannableStringBuilder(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                            } else {
                                                                SpannableStringBuilder(text)
                                                            }
                                                            adminViewModel.addHistory(0)
                                                            adminViewModel.htmlText = html
                                                            navigationActions.navigateToPiasochnica(fileName)
                                                            isProgressVisable = false
                                                        }
                                                    }
                                                    isProgressVisable = false
                                                }
                                            }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.edit), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
                    )
                }
            },
            snackbarHost = {
                if (isShare) {
                    Surface(
                        modifier = Modifier.padding(5.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .weight(1f), text = stringResource(R.string.share_help), fontSize = Settings.fontInterface.sp, lineHeight = (Settings.fontInterface * 1.15f).sp, color = MaterialTheme.colorScheme.secondary
                            )
                            IconButton(onClick = {
                                showDropdown = false
                                viewModel.autoScroll(title, false)
                                shareIsLaunch = true
                                isShare = false
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (!viewModel.searchText && !isShare) {
                    AnimatedVisibility(
                        !fullscreen, enter = fadeIn(
                            tween(
                                durationMillis = 500, easing = LinearOutSlowInEasing
                            )
                        ), exit = fadeOut(tween(durationMillis = 500, easing = LinearOutSlowInEasing))
                    ) {
                        if (!iskniga) {
                            if (showDropdown) {
                                ModalBottomSheet(
                                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                                    scrimColor = Color.Transparent,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                                    onDismissRequest = {
                                        showDropdown = false
                                        if (viewModel.autoScrollSensor) viewModel.autoScroll(title, true)
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
                            if (isBottomBar) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(interactionSource = interactionSourse, indication = null) {}
                                        .padding(top = 10.dp)
                                        .background(MaterialTheme.colorScheme.onTertiary)
                                        .navigationBarsPadding(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    PlainTooltip(stringResource(R.string.tts)) {
                                        IconButton(onClick = {
                                            if (viewModel.isSpeaking || viewModel.isPaused) {
                                                viewModel.isSpeaking = false
                                                viewModel.isPaused = false
                                                viewModel.stop()
                                            } else {
                                                if (k.getBoolean("isTTSHelp", true)) {
                                                    viewModel.dialodTTSHelp = true
                                                } else {
                                                    viewModel.isSpeaking = true
                                                    viewModel.creteTTSList(textLayout)
                                                    viewModel.speak()
                                                    viewModel.autoScroll(title, false)
                                                    viewModel.autoScrollSensor = false
                                                }
                                            }
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.text_to_speech),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    PlainTooltip(stringResource(R.string.menu_font_size_app_info)) {
                                        IconButton(onClick = {
                                            showDropdown = !showDropdown
                                            viewModel.autoScroll(title, false)
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.format_size),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    PlainTooltip(stringResource(R.string.share)) {
                                        IconButton(onClick = {
                                            showDropdown = false
                                            viewModel.autoScroll(title, false)
                                            isShare = true
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.share),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    PlainTooltip(stringResource(R.string.searche_text)) {
                                        IconButton(onClick = {
                                            viewModel.searchText = true
                                            viewModel.autoScroll(title, false)
                                            viewModel.search(textLayout)
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.search),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    PlainTooltip(stringResource(R.string.fullscreen_apis)) {
                                        IconButton(onClick = {
                                            fullscreen = true
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.fullscreen),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    PlainTooltip(stringResource(if (viewModel.isVybranoe) R.string.vybranae_remove else R.string.vybranae_add)) {
                                        IconButton(onClick = {
                                            viewModel.saveVybranoe(context, title, resursEncode)
                                        }) {
                                            val icon = if (viewModel.isVybranoe) painterResource(R.drawable.stars)
                                            else painterResource(R.drawable.star)
                                            Icon(
                                                painter = icon,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onSecondary
                                            )
                                        }
                                    }
                                    if (!(viewModel.isSpeaking || viewModel.isPaused)) {
                                        if (viewModel.scrollState.canScrollForward) {
                                            val iconAutoScroll =
                                                if (viewModel.autoScrollSensor) painterResource(R.drawable.stop_circle)
                                                else painterResource(R.drawable.play_circle)
                                            PlainTooltip(stringResource(if (viewModel.autoScrollSensor) R.string.auto_stop else R.string.auto_play)) {
                                                IconButton(onClick = {
                                                    viewModel.autoScrollSensor = !viewModel.autoScrollSensor
                                                    viewModel.autoScroll(title, viewModel.autoScrollSensor)
                                                    if (viewModel.autoScrollSensor) {
                                                        actyvity.window.addFlags(
                                                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                                        )
                                                    } else if (!k.getBoolean("power", false)) {
                                                        actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                                                    }
                                                }) {
                                                    Icon(
                                                        iconAutoScroll,
                                                        contentDescription = "",
                                                        tint = MaterialTheme.colorScheme.onSecondary
                                                    )
                                                }
                                            }
                                        } else if (viewModel.scrollState.canScrollBackward) {
                                            PlainTooltip(stringResource(R.string.auto_up)) {
                                                IconButton(onClick = {
                                                    isUpList = true
                                                }) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.arrow_upward),
                                                        contentDescription = "",
                                                        tint = MaterialTheme.colorScheme.onSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(
                        innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        if (fullscreen) 0.dp else innerPadding.calculateTopPadding(),
                        innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                        0.dp
                    )
            ) {
                var isScrollRun by remember { mutableStateOf(false) }
                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            isScrollRun = true
                            return super.onPreScroll(available, source)
                        }

                        override suspend fun onPostFling(
                            consumed: Velocity,
                            available: Velocity
                        ): Velocity {
                            isScrollRun = false
                            if (viewModel.autoScrollSensor && !isShare && !viewModel.searchText) viewModel.autoScroll(title, true)
                            return super.onPostFling(consumed, available)
                        }

                        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                            if (viewModel.result.isNotEmpty()) {
                                textLayout?.let {
                                    val firstTextPosition = it.getLineStart(it.getLineForVerticalPosition(viewModel.scrollState.value.toFloat()))
                                    val lastTextPosition = it.getLineEnd(it.getLineForVerticalPosition(viewModel.scrollState.value.toFloat()))
                                    if (firstTextPosition < viewModel.result[0][0]) {
                                        viewModel.resultPosition = 0
                                    } else {
                                        for (i in viewModel.result.indices) {
                                            if (viewModel.result[i][0] in firstTextPosition..lastTextPosition || firstTextPosition < viewModel.result[i][0]) {
                                                viewModel.resultPosition = i
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                            return super.onPostScroll(consumed, available, source)
                        }
                    }
                }
                Column {
                    if (isProgressVisable) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    if (viewModel.searchText && viewModel.result.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = stringResource(R.string.searh_text_result, viewModel.resultPosition + 1, viewModel.result.size),
                            fontStyle = FontStyle.Italic,
                            fontSize = Settings.fontInterface.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    val padding = if (fullscreen) innerPadding.calculateTopPadding() else 0.dp
                    Column(
                        modifier = Modifier
                            .pointerInput(PointerEventType.Press) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Press) {
                                            viewModel.autoScroll(title, false)
                                        }
                                        if (viewModel.autoScrollSensor && !isShare && !viewModel.searchText && event.type == PointerEventType.Release && !isScrollRun) {
                                            viewModel.autoScroll(title, true)
                                        }
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        fullscreen = !fullscreen
                                    }
                                )
                            }
                            .nestedScroll(nestedScrollConnection)
                            .verticalScroll(viewModel.scrollState),
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (viewModel.autoScrollSensor || !isShare) {
                            HtmlText(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp, top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(if (isBottomBar) 0.dp else 10.dp))
                                    .pointerInput(Unit) {
                                        awaitEachGesture {
                                            awaitFirstDown()
                                            do {
                                                val event = awaitPointerEvent()
                                                if (event.changes.size == 2) {
                                                    fontSize *= event.calculateZoom()
                                                    fontSize = fontSize.coerceIn(18f, 58f)
                                                    k.edit {
                                                        putFloat("font_biblia", fontSize)
                                                    }
                                                    event.changes.forEach { pointerInputChange: PointerInputChange ->
                                                        pointerInputChange.consume()
                                                    }
                                                }
                                            } while (event.changes.any { it.pressed })
                                        }
                                    },
                                text = viewModel.htmlText,
                                title = title,
                                fontSize = fontSize.sp,
                                isLiturgia = isLiturgia && isLiturgia(data),
                                searchText = if (viewModel.searchText || viewModel.isPaused || viewModel.isSpeaking) viewModel.searchTextResult else AnnotatedString(""),
                                scrollState = viewModel.scrollState,
                                navigateTo = { navigate ->
                                    navigateTo(navigate, false)
                                },
                                textLayoutResult = { layout ->
                                    textLayout = layout
                                },
                                isDialogListinner = { dialog, chastka ->
                                    when (dialog) {
                                        DialogListinner.DIALOGQRCODE.name -> {
                                            dialogQrCode = true
                                        }

                                        DialogListinner.DIALOGSZTOHOVAHA.name -> {
                                            dialogSztoHovahaVisable = true
                                        }

                                        DialogListinner.DIALOGLITURGIA.name -> {
                                            chast = chastka
                                            dialogLiturgia = true
                                        }
                                    }
                                }
                            )
                        } else {
                            if (Settings.dzenNoch) {
                                var text by remember { mutableStateOf(TextFieldValue(AnnotatedString.fromHtml(viewModel.htmlText.replace("#d00505", "#ff6666", true)))) }
                                BasicTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp, top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(if (isBottomBar) 0.dp else 10.dp)),
                                    value = text,
                                    onValueChange = { newText ->
                                        text = newText
                                        if (text.getSelectedText().text.isNotEmpty()) {
                                            selectedText = text.getSelectedText().text
                                        }
                                    },
                                    readOnly = true,
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.secondary, fontSize = fontSize.sp, fontFamily = displayFontFamily, letterSpacing = TextUnit(0.5f, TextUnitType.Sp)),
                                )
                            } else {
                                var text by remember { mutableStateOf(TextFieldValue(AnnotatedString.fromHtml(viewModel.htmlText))) }
                                BasicTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp, top = padding.plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(if (isBottomBar) 0.dp else 10.dp)),
                                    value = text,
                                    onValueChange = { newText ->
                                        text = newText
                                        if (text.getSelectedText().text.isNotEmpty()) {
                                            selectedText = text.getSelectedText().text
                                        }
                                    },
                                    readOnly = true,
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.secondary, fontSize = fontSize.sp, fontFamily = displayFontFamily, letterSpacing = TextUnit(0.5f, TextUnitType.Sp)),
                                )
                            }
                        }
                        if (viewModel.scrollState.lastScrolledForward && !viewModel.scrollState.canScrollForward) {
                            viewModel.autoScroll(title, false)
                            viewModel.autoScrollSensor = false
                            if (!k.getBoolean("power", false)) {
                                actyvity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    AnimatedVisibility(
                        viewModel.autoScrollTextVisable, enter = fadeIn(
                            tween(
                                durationMillis = 700, easing = LinearOutSlowInEasing
                            )
                        ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp, end = 10.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = autoScrollText,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(autoScrollTextColor)
                                    .padding(5.dp)
                                    .align(Alignment.CenterVertically),
                                color = autoScrollTextColor2,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (!isBottomBar || fullscreen) 10.dp else 0.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AnimatedVisibility(
                            viewModel.isSpeaking || viewModel.isPaused, enter = fadeIn(
                                tween(
                                    durationMillis = 700, easing = LinearOutSlowInEasing
                                )
                            ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.tts_stop),
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.isSpeaking = false
                                        viewModel.isPaused = false
                                        viewModel.stop()
                                    }
                                    .background(Button)
                                    .size(40.dp)
                                    .padding(5.dp)
                            )
                        }
                        AnimatedVisibility(
                            viewModel.isSpeaking || viewModel.isPaused, enter = fadeIn(
                                tween(
                                    durationMillis = 700, easing = LinearOutSlowInEasing
                                )
                            ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                        ) {
                            Image(
                                painter = painterResource(if (viewModel.isPaused) R.drawable.tts_play else R.drawable.tts_pause),
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (viewModel.isPaused) {
                                            viewModel.resume()
                                        } else {
                                            viewModel.pause()
                                        }
                                        viewModel.isPaused = !viewModel.isPaused
                                    }
                                    .background(Button)
                                    .size(40.dp)
                                    .padding(5.dp)
                            )
                        }
                        AnimatedVisibility(
                            viewModel.autoScrollSensor, enter = fadeIn(
                                tween(
                                    durationMillis = 700, easing = LinearOutSlowInEasing
                                )
                            ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.minus_auto_scroll),
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (viewModel.autoScrollSpeed in 10..125) {
                                            viewModel.autoScrollSpeed += 5
                                            val proc = 100 - (viewModel.autoScrollSpeed - 15) * 100 / 115
                                            autoScrollTextColor = Post
                                            autoScrollTextColor2 = PrimaryText
                                            autoScrollText = "$proc%"
                                            viewModel.autoScrollTextVisable = true
                                            viewModel.autoScrollSpeed(context)
                                        }
                                    }
                                    .background(Button)
                                    .size(40.dp)
                                    .padding(5.dp)
                            )
                        }
                        AnimatedVisibility(
                            viewModel.autoScrollSensor, enter = fadeIn(
                                tween(
                                    durationMillis = 700, easing = LinearOutSlowInEasing
                                )
                            ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.plus_auto_scroll),
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (viewModel.autoScrollSpeed in 20..135) {
                                            viewModel.autoScrollSpeed -= 5
                                            val proc = 100 - (viewModel.autoScrollSpeed - 15) * 100 / 115
                                            autoScrollTextColor = Primary
                                            autoScrollTextColor2 = PrimaryTextBlack
                                            autoScrollText = "$proc%"
                                            viewModel.autoScrollTextVisable = true
                                            viewModel.autoScrollSpeed(context)
                                        }
                                    }
                                    .background(Button)
                                    .size(40.dp)
                                    .padding(5.dp)

                            )
                        }
                    }
                }
            }
            if (iskniga) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            0.dp,
                            innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                            0.dp
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollStateDop)
                ) {
                    HtmlText(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = innerPadding.calculateTopPadding().plus(10.dp), bottom = innerPadding.calculateBottomPadding().plus(10.dp)),
                        text = subText,
                        fontSize = fontSize.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DialogLiturgia(
    chast: Int,
    onDismiss: () -> Unit,
) {
    val context = LocalActivity.current as MainActivity
    var title by remember { mutableStateOf("") }
    var item by remember { mutableStateOf("") }
    var filename = "bogashlugbovya/bogashlugbovya1_1.html"
    when (chast) {
        1 -> {
            filename = "bogashlugbovya/bogashlugbovya1_1.html"
            title = stringResource(R.string.ps_102)
        }

        2 -> {
            filename = "bogashlugbovya/bogashlugbovya1_2.html"
            title = stringResource(R.string.ps_91)
        }

        3 -> {
            filename = "bogashlugbovya/bogashlugbovya1_3.html"
            title = stringResource(R.string.ps_145)
        }

        4 -> {
            filename = "bogashlugbovya/bogashlugbovya1_4.html"
            title = stringResource(R.string.ps_92)
        }

        5 -> {
            filename = "bogashlugbovya/bogashlugbovya1_5.html"
            title = stringResource(R.string.mc_5_3_12)
        }

        6 -> {
            filename = "bogashlugbovya/bogashlugbovya1_6.html"
            title = stringResource(R.string.malitva_za_pamerlyx)
        }

        7 -> {
            filename = "bogashlugbovya/bogashlugbovya1_7.html"
            title = stringResource(R.string.malitva_za_paclicanyx)
        }

        10 -> {
            filename = "bogashlugbovya/bogashlugbovya1_8.html"
            title = stringResource(R.string.ps_94)
        }

        11 -> {
            filename = "viaczernia_bierascie_1.html"
            title = stringResource(R.string.viaczernia_bierascie_1)
        }

        13 -> {
            filename = "viaczernia_bierascie_3.html"
            title = stringResource(R.string.viaczernia_bierascie_3)
        }

        14 -> {
            filename = "bogashlugbovya/bogashlugbovya1_9.html"
            title = stringResource(R.string.malitva_za_paclicanyx_i_jyvyx)
        }
    }
    item = openAssetsResources(context, filename)
    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = title.uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f, false)
                        .verticalScroll(rememberScrollState())
                ) {
                    HtmlText(text = item, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogHelpTTS(perevod: String = Settings.PEREVODSEMUXI, isError: Boolean, onDismiss: (Boolean) -> Unit) {
    val context = LocalContext.current
    var isCheck by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDismiss(isCheck) }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                val movaTitle = when (perevod) {
                    Settings.PEREVODSINOIDAL -> stringResource(R.string.tts_help_title_ru)
                    Settings.PEREVODNEWAMERICANBIBLE -> stringResource(R.string.tts_help_title_en)
                    else -> stringResource(R.string.tts_help_title_be)
                }
                val movaText = when (perevod) {
                    Settings.PEREVODSINOIDAL -> "расейскай мовы"
                    Settings.PEREVODNEWAMERICANBIBLE -> "ангельскай мовы"
                    else -> "беларускай мовы"
                }
                Text(
                    text = movaTitle, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f, false)
                ) {
                    HtmlText(text = openAssetsResources(context, "tts_help.html").replace("<MOVA/>", movaText), modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp)
                }
                if (!isError) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCheck, onCheckedChange = {
                            isCheck = !isCheck
                        })
                        Text(
                            text = stringResource(R.string.not_show), modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    isCheck = !isCheck
                                }, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss(isCheck) }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = "")
                        Text(stringResource(R.string.ok), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun isLiturgia(dataDayList: ArrayList<String>): Boolean {
    val dayIfYear = dataDayList[24].toInt()
    val dayInPasha = dataDayList[22].toInt()
    val dayOfNedel = dataDayList[0].toInt()
    return when {
        dayIfYear == 85 -> true
        dayInPasha == -53 -> false
        dayInPasha == -51 -> false
        dayInPasha == -2 -> false
        dayInPasha in -48..-4 -> dayOfNedel == Calendar.SATURDAY || dayOfNedel == Calendar.SUNDAY
        else -> true
    }
}

class TTSManager(val context: Context, val langNotSupported: () -> Unit, speakText: (Int) -> Unit, isDone: () -> Unit) {
    private var tts: TextToSpeech? = null
    private var textList = listOf<String>()
    private var currentSentenceIndex = 0
    private var isPaused = false
    private var isInitialized = false
    private var result: Int? = null

    @Suppress("DEPRECATION")
    suspend fun initialize(perevod: String = Settings.PEREVODSEMUXI): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                result = tts?.setLanguage(
                    when (perevod) {
                        Settings.PEREVODSINOIDAL -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                                Locale.of("rus", "RUS")
                            } else Locale("rus", "RUS")
                        }

                        Settings.PEREVODNEWAMERICANBIBLE -> Locale.US

                        else -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                                Locale.of("be", "BE")
                            } else Locale("be", "BE")
                        }
                    }
                )
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    continuation.resume(false) { _, _, _ ->
                    }
                } else {
                    tts?.setOnUtteranceProgressListener(utteranceProgressListener)
                    isInitialized = true
                    continuation.resume(true) { _, _, _ ->
                    }
                }
            } else {
                continuation.resume(false) { _, _, _ ->
                }
            }
        }
    }

    private val utteranceProgressListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            speakText(currentSentenceIndex)
        }

        override fun onDone(utteranceId: String?) {
            if (currentSentenceIndex == textList.size - 1) {
                isDone()
            }
            if (currentSentenceIndex < textList.size - 1 && !isPaused) {
                currentSentenceIndex++
                while (true) {
                    if (textList[currentSentenceIndex].contains("color=red")) currentSentenceIndex++
                    else break
                }
                speakSentence(textList[currentSentenceIndex])
            } else {
                currentSentenceIndex = 0
                isPaused = false
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
        }
    }

    fun speakLongText(list: List<String>, positionTTS: Int) {
        if (!isInitialized) {
            if (result == TextToSpeech.LANG_NOT_SUPPORTED) langNotSupported()
            Toast.makeText(context, context.getString(R.string.error_ch), Toast.LENGTH_SHORT).show()
            return
        }
        textList = list
        currentSentenceIndex = positionTTS
        isPaused = false
        while (true) {
            if (textList[currentSentenceIndex].contains("color=red")) currentSentenceIndex++
            else break
        }
        if (textList.isNotEmpty()) {
            speakSentence(textList[currentSentenceIndex])
        }
    }

    private fun speakSentence(sentence: String) {
        tts?.speak(sentence, TextToSpeech.QUEUE_ADD, null, currentSentenceIndex.toString())
    }

    fun pause() {
        if (isInitialized) {
            isPaused = true
            tts?.stop()
        }
    }

    fun resume() {
        while (true) {
            if (textList[currentSentenceIndex].contains("color=red")) currentSentenceIndex++
            else break
        }
        if (isInitialized && isPaused && currentSentenceIndex < textList.size) {
            isPaused = false
            speakSentence(textList[currentSentenceIndex])
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
            currentSentenceIndex = 0
            isPaused = false
        }
    }

    fun shutdown() {
        tts?.shutdown()
    }
}

data class VybranaeDataAll(
    val id: Long,
    val title: String,
    val resource: String,
)

data class TTS(val textSpik: String, val start: Int, val end: Int, val textFull: String)