package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryBlack
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.TitleCalendarMounth
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.DropdownMenuBox
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.openAssetsResources
import by.carkva_gazeta.malitounik.views.openBibleResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.Collator
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.zip.ZipFile

class SearchBibleViewModel : CytanniListViewModel() {
    var searchJob: Job? = null
    var textFieldValueState by mutableStateOf(TextFieldValue(AppNavGraphState.searchBogaslujbovyia, TextRange(AppNavGraphState.searchBogaslujbovyia.length)))
    val searchList = mutableStateListOf<SearchBibleItem>()
    var searchSettings by mutableStateOf(false)
    val searchListSvityia = mutableStateListOf<Prazdniki>()
    var isProgressVisable by mutableStateOf(false)
    var oldTextFieldValueState by mutableStateOf("")
    var searchText by mutableStateOf(false)
    var searchFullText by mutableStateOf(false)
    var textFieldValueNatatkaContent by mutableStateOf(TextFieldValue(""))
    var addFileNatatki by mutableStateOf(false)
    var saveFileNatatki by mutableStateOf(false)
    var natatkaPosition by mutableIntStateOf(0)
    var fileList = mutableStateListOf<MaeNatatkiItem>()
    var natatkaVisable by mutableStateOf(false)
    var isEditMode by mutableStateOf(false)
    var isDeliteNatatka by mutableStateOf(false)
    var isIconSort by mutableStateOf(false)
    var dialogKniga by mutableStateOf(false)
    val slujbaList = mutableStateListOf<SlugbovyiaTextuData>()
    var dialogKnigaView by mutableStateOf(false)
    var slujva by mutableIntStateOf(SlugbovyiaTextu.LITURHIJA)
    var perevodBiblii = Settings.PEREVODSEMUXI

    fun doInBackground(
        context: Context, searche: String, perevod: String, isBogaslujbovyiaSearch: Boolean
    ): ArrayList<SearchBibleItem> {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        var list = if (isBogaslujbovyiaSearch) {
            bogashlugbovya(context, searche)
        } else {
            biblia(context, searche, perevod)
        }
        if (!isBogaslujbovyiaSearch) {
            if (list.isEmpty() && k.getInt("slovocalkam", 0) == 0) {
                list = biblia(context, searche, perevod, true)
            }
        }
        return list
    }

    @Suppress("DEPRECATION")
    fun bogashlugbovya(context: Context, poshuk: String, secondRun: Boolean = false): ArrayList<SearchBibleItem> {
        perevodBiblii = Settings.PEREVODSEMUXI
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        var poshuk1 = poshuk
        val seashpost = ArrayList<SearchBibleItem>()
        val registr = k.getBoolean("pegistrbukv", true)
        poshuk1 = zamena(poshuk1, registr)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1[r] == aM && r >= 3) {
                    poshuk1 = poshuk1.replace(poshuk1, poshuk1.take(r), registr)
                }
            }
        }
        val bogaslugbovyiaListAll = getAllBogaslujbovyia(context)
        for (i in 0 until bogaslugbovyiaListAll.size) {
            if (searchJob?.isActive == false) break
            var nazva = context.getString(R.string.error_ch)
            val bibleline = openAssetsResources(context, bogaslugbovyiaListAll[i].resource)
            val t1 = bibleline.indexOf("<strong>")
            if (t1 != -1) {
                val t2 = bibleline.indexOf("</strong>", t1 + 8)
                nazva = bibleline.substring(t1 + 8, t2)
                nazva = AnnotatedString.fromHtml(nazva).text
                nazva = nazva.replace("\n", " ")
            }
            val prepinanie = AnnotatedString.fromHtml(bibleline).text
            val poshuk2 = findChars(context, poshuk1, prepinanie)
            if (poshuk2.isEmpty()) continue
            val span = AnnotatedString.Builder()
            span.append(nazva)
            seashpost.add(SearchBibleItem(nazva, 0, 0, bogaslugbovyiaListAll[i].resource, span))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            seashpost.sortWith(compareBy(Collator.getInstance(Locale.of("be", "BE"))) { it.title })
        } else {
            seashpost.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        }
        return seashpost
    }

    fun biblia(
        context: Context, poshuk: String, perevod: String, secondRun: Boolean = false
    ): ArrayList<SearchBibleItem> {
        perevodBiblii = perevod
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        var poshuk1 = poshuk
        val seashpost = ArrayList<SearchBibleItem>()
        val registr = k.getBoolean("pegistrbukv", true)
        if (secondRun) {
            val m = if (perevod == Settings.PEREVODSINOIDAL) charArrayOf(
                'у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы'
            )
            else charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1.length >= 3) {
                    if (poshuk1[r] == aM && r >= 3) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.take(r), registr)
                    }
                }
            }
        }
        val rangeBibile = when (perevod) {
            Settings.PEREVODNADSAN -> 0..0
            Settings.PEREVODCATOLIK -> 1..1
            else -> 0..1
        }
        for (novyZapaviet in rangeBibile) {
            val list = if (novyZapaviet == 0) getNameBook(context, perevod, false)
            else getNameBook(context, perevod, true)
            val subTitleListName = if (novyZapaviet == 0) setStaryZapavet(list, perevod)
            else setNovyZapavet(list, perevod)
            val range = if (perevod == Settings.PEREVODCATOLIK) {
                if (k.getInt("biblia_seash_novy_zapavet", 0) == 0) {
                    0 until getNameBook(context, perevod, true).size
                } else {
                    0 until 4
                }
            } else {
                when (k.getInt("biblia_seash", 0)) {
                    1 -> {
                        if (novyZapaviet == 0) continue
                        0 until 4
                    }

                    2 -> {
                        if (novyZapaviet == 0) continue
                        0 until getNameBook(context, perevod, true).size
                    }

                    3 -> {
                        if (novyZapaviet == 1) continue
                        0 until 4
                    }

                    4 -> {
                        if (novyZapaviet == 1) continue
                        0 until getNameBook(context, perevod, false).size
                    }

                    else -> {
                        if (novyZapaviet == 0) 0 until getNameBook(context, perevod, false).size
                        else 0 until getNameBook(context, perevod, true).size
                    }
                }
            }
            for (i in range) {
                if (searchJob?.isActive == false) break
                val nazva = list[i]
                val subTitle = subTitleListName[i].subTitle
                val zavet = if (novyZapaviet == 1) "n"
                else "s"
                val perevodFilePach = when (perevod) {
                    Settings.PEREVODSEMUXI -> "chytanne/Semucha/biblia"
                    Settings.PEREVODBOKUNA -> "chytanne/Bokun/bokuna"
                    Settings.PEREVODCARNIAUSKI -> "chytanne/Carniauski/carniauski"
                    Settings.PEREVODCATOLIK -> "/Catolik/catolik"
                    Settings.PEREVODNADSAN -> "chytanne/psaltyr_nadsan.txt"
                    Settings.PEREVODSINOIDAL -> "/Sinodal/sinaidal"
                    Settings.PEREVODNEWAMERICANBIBLE -> "/NewAmericanBible/english"
                    else -> "chytanne/Semucha/biblia"
                }
                val fileName = if (perevod == Settings.PEREVODNADSAN) perevodFilePach
                else "$perevodFilePach$zavet${i + 1}.txt"
                var glava = 0
                val split = if (perevod == Settings.PEREVODSINOIDAL || perevod == Settings.PEREVODCATOLIK || perevod == Settings.PEREVODNEWAMERICANBIBLE) {
                    openBibleResources(context, fileName).split("===")
                } else {
                    openAssetsResources(context, fileName).split("===")
                }
                for (e in 1 until split.size) {
                    glava++
                    val bibleline = split[e].split("\n")
                    var stix = 0
                    for (r in 1 until bibleline.size) {
                        stix++
                        var aSviatyia = AnnotatedString.fromHtml(bibleline[r]).text
                        val title = "$nazva Гл. $glava\n"
                        val t3 = title.length
                        val span = AnnotatedString.Builder()
                        val poshuk2 = findChars(context, poshuk1, aSviatyia)
                        if (poshuk2.isEmpty()) continue
                        span.append(title)
                        span.addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, title.length)
                        var t5 = aSviatyia.indexOf("<br>")
                        if (t5 == -1) t5 = 0
                        else t5 += 4
                        val t6 = aSviatyia.indexOf(" ", t5)
                        val isInt = if (t6 != -1) {
                            val item = aSviatyia.substring(t5, t6)
                            item.isNotEmpty() && item.isDigitsOnly()
                        } else false
                        val padd = if (isInt) {
                            val color = if (Settings.dzenNoch) PrimaryBlack
                            else Primary
                            val sub1 = aSviatyia.substring(t5, t6)
                            aSviatyia = aSviatyia.replace(sub1, "$sub1.")
                            span.append(aSviatyia)
                            span.addStyle(SpanStyle(color = color), t5 + t3, t6 + t3 + 1)
                            1
                        } else {
                            span.append(aSviatyia)
                            0
                        }
                        for (w in 0 until poshuk2.size) {
                            val t2 = poshuk2[w].str.length
                            val t1 = poshuk2[w].position + t3 + padd
                            span.addStyle(
                                SpanStyle(background = BezPosta, color = PrimaryText), t1 - t2, t1
                            )
                        }
                        seashpost.add(SearchBibleItem(subTitle, glava, stix, "", span))
                    }
                }
            }
        }
        return seashpost
    }

    fun findChars(context: Context, search: String, textSearch: String): ArrayList<FindString> {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val registr = k.getBoolean("pegistrbukv", true)
        val stringBuilder = StringBuilder()
        var strSub = 0
        val list = search.toCharArray()
        val result = ArrayList<FindString>()
        while (true) {
            val strSub1Pos = textSearch.indexOf(list[0], strSub, registr)
            if (strSub1Pos != -1) {
                strSub = strSub1Pos + 1
                val subChar2 = StringBuilder()
                for (i in 1 until list.size) {
                    if (textSearch.length >= strSub + 1) {
                        if (list[i].isLetterOrDigit()) {
                            var subChar = textSearch.substring(strSub, strSub + 1)
                            if (subChar == "́") {
                                stringBuilder.append(list[i])
                                strSub++
                                if (textSearch.length >= strSub + 1) {
                                    subChar = textSearch.substring(strSub, strSub + 1)
                                }
                            }
                            val strSub2Pos = subChar.indexOf(list[i], ignoreCase = registr)
                            if (strSub2Pos != -1) {
                                if (stringBuilder.isEmpty()) stringBuilder.append(
                                    textSearch.substring(
                                        strSub1Pos, strSub1Pos + 1
                                    )
                                )
                                if (subChar2.isNotEmpty()) stringBuilder.append(subChar2.toString())
                                stringBuilder.append(list[i])
                                subChar2.clear()
                                strSub++
                            } else {
                                stringBuilder.clear()
                                break
                            }
                        } else {
                            while (true) {
                                if (textSearch.length >= strSub + 1) {
                                    val subChar = textSearch.substring(strSub, strSub + 1).toCharArray()
                                    if (!subChar[0].isLetterOrDigit()) {
                                        subChar2.append(subChar[0])
                                        strSub++
                                    } else {
                                        if (list.size - 1 == i) {
                                            stringBuilder.append(list[i])
                                        }
                                        break
                                    }
                                } else {
                                    break
                                }
                            }
                            if (subChar2.isEmpty()) {
                                strSub++
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
                    if (k.getInt("slovocalkam", 0) == 1) {
                        val startString = if (strSub1Pos > 0) textSearch.substring(strSub1Pos - 1, strSub1Pos)
                        else " "
                        val endString = if (strSub1Pos + stringBuilder.length + 1 <= textSearch.length) textSearch.substring(
                            strSub1Pos + stringBuilder.length, strSub1Pos + stringBuilder.length + 1
                        )
                        else " "
                        if (!startString.toCharArray()[0].isLetterOrDigit() && !endString.toCharArray()[0].isLetterOrDigit()) {
                            result.add(FindString(stringBuilder.toString(), strSub))
                            stringBuilder.clear()
                        }
                    } else {
                        result.add(FindString(stringBuilder.toString(), strSub))
                        stringBuilder.clear()
                    }
                }
            } else {
                break
            }
        }
        return result
    }

    fun rawAsset(context: Context, poshukString: String, secondRun: Boolean = false): ArrayList<Prazdniki> {
        val year = Calendar.getInstance()[Calendar.YEAR]
        val yearList = ArrayList<ArrayList<String>>()
        Settings.data.forEach { arrayList ->
            if (year == arrayList[3].toInt()) {
                yearList.add(arrayList)
            }
        }
        val arrayLists = ArrayList<ArrayList<String>>()
        arrayLists.addAll(yearList)
        val munName = context.resources.getStringArray(R.array.meciac_smoll)
        var poshuk = poshukString
        poshuk = zamena(poshuk)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk.length - 1
                if (r >= 3) {
                    if (poshuk[r] == aM) {
                        poshuk = poshuk.replace(poshuk, poshuk.take(r), true)
                    }
                }
            }
        }
        val result = ArrayList<Prazdniki>()
        val nedelName = context.resources.getStringArray(R.array.dni_nedeli)
        for (e in arrayLists.indices) {
            val sviatyia = arrayLists[e][4].split("<br>")
            for (aSviatyia in sviatyia) {
                if (aSviatyia.replace("ё", "е", true).contains(poshuk, true)) {
                    val g = GregorianCalendar(arrayLists[e][3].toInt(), arrayLists[e][2].toInt(), arrayLists[e][1].toInt())
                    result.add(Prazdniki(g[Calendar.DAY_OF_YEAR], aSviatyia, g[Calendar.DATE].toString() + " " + munName[g[Calendar.MONTH]] + ", " + nedelName[g[Calendar.DAY_OF_WEEK]], 0))
                }
            }
        }
        val data = getPrazdnik(context, 1)
        data.addAll(getPrazdnik(context, 2))
        data.addAll(getPrazdnik(context, 3))
        data.addAll(getPrazdnik(context, 4))
        data.addAll(getPrazdnik(context, 5))
        data.addAll(getPrazdnik(context, 6))
        for (e in data.indices) {
            val sviatya = data[e].opisanie.replace("ё", "е", true)
            if (sviatya.contains(poshuk, true)) {
                result.add(data[e])
            }
        }
        return result
    }

    fun search(context: Context, perevod: String = Settings.PEREVODSEMUXI, isBogaslujbovyiaSearch: Boolean = false) {
        if (oldTextFieldValueState != textFieldValueState.text || AppNavGraphState.searchSettings) {
            viewModelScope.launch {
                if (searchSettings) {
                    searchList.clear()
                }
                if (textFieldValueState.text.trim().length >= 3) {
                    if (textFieldValueState.text != AppNavGraphState.searchBogaslujbovyia) {
                        searchJob?.cancel()
                        searchJob = CoroutineScope(Dispatchers.Main).launch {
                            isProgressVisable = true
                            searchList.clear()
                            val list = withContext(Dispatchers.IO) {
                                return@withContext doInBackground(context, textFieldValueState.text.trim(), perevod, isBogaslujbovyiaSearch)
                            }
                            searchList.addAll(list)
                            isProgressVisable = false
                        }
                    }
                } else {
                    searchJob?.cancel()
                    isProgressVisable = false
                }
            }
        }
    }

    fun searchSvityia(context: Context) {
        if (textFieldValueState.text.trim().length >= 3 && textFieldValueState.text.trim() != Settings.textFieldValueLatest.trim()) {
            Settings.textFieldValueLatest = textFieldValueState.text.trim()
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                searchListSvityia.clear()
                val list = withContext(Dispatchers.IO) {
                    return@withContext rawAsset(context, textFieldValueState.text.trim())
                }
                searchListSvityia.addAll(list)
            }
        } else {
            searchJob?.cancel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliaMenu(
    navController: NavHostController,
    perevod: String,
    innerPadding: PaddingValues,
    searchBibleState: LazyListState,
    sort: Int,
    navigateToCytanniList: (String, Int, String, Int) -> Unit,
    navigateToBogaslujbovyia: (String, String) -> Unit,
    viewModel: SearchBibleViewModel
) {
    viewModel.perevodBiblii = perevod
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    LaunchedEffect(viewModel.bibleTime) {
        if (viewModel.bibleTime) {
            var defKniga = "Быц"
            val prevodName = when (perevod) {
                Settings.PEREVODSEMUXI -> {
                    "biblia"
                }

                Settings.PEREVODBOKUNA -> {
                    "bokuna"
                }

                Settings.PEREVODNADSAN -> {
                    defKniga = "Пс"
                    "nadsan"
                }

                Settings.PEREVODCARNIAUSKI -> {
                    "carniauski"
                }

                Settings.PEREVODCATOLIK -> {
                    defKniga = "Мц"
                    "catolik"
                }

                Settings.PEREVODSINOIDAL -> {
                    "sinaidal"
                }

                Settings.PEREVODNEWAMERICANBIBLE -> {
                    "english"
                }

                else -> "biblia"
            }
            val knigaText = k.getString("bible_time_${prevodName}_kniga", defKniga) ?: defKniga
            val kniga = knigaBiblii(knigaText)
            navigationActions.navigateToBibliaList(kniga >= 50, perevod)
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    var isRegistr by remember { mutableStateOf(k.getBoolean("pegistrbukv", true)) }
    var isDakladnaeSupadzenne by remember { mutableIntStateOf(k.getInt("slovocalkam", 0)) }
    LaunchedEffect(viewModel.textFieldValueState.text) {
        viewModel.search(context, perevod)
    }
    LaunchedEffect(AppNavGraphState.searchSettings) {
        if (AppNavGraphState.searchSettings) {
            viewModel.search(context, perevod)
            AppNavGraphState.searchSettings = false
        }
    }
    if (viewModel.searchText || viewModel.searchFullText) {
        if (viewModel.searchSettings) {
            ModalBottomSheet(
                scrimColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                onDismissRequest = {
                    viewModel.searchSettings = false
                }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (perevod != Settings.PEREVODNADSAN) {
                        DropdownMenuBox(
                            initValue = if (perevod == Settings.PEREVODCATOLIK) k.getInt("biblia_seash_novy_zapavet", 0)
                            else k.getInt("biblia_seash", 0),
                            menuList = stringArrayResource(if (perevod == Settings.PEREVODCATOLIK) R.array.serche_bible_novy_zapavet else R.array.serche_bible)
                        ) { index ->
                            k.edit {
                                if (perevod == Settings.PEREVODCATOLIK) putInt("biblia_seash_novy_zapavet", index)
                                else putInt("biblia_seash", index)
                            }
                            AppNavGraphState.searchSettings = true
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            Settings.vibrate()
                            isRegistr = !isRegistr
                            k.edit {
                                putBoolean("pegistrbukv", isRegistr)
                            }
                            AppNavGraphState.searchSettings = true
                        }) {
                        Checkbox(
                            checked = !isRegistr,
                            onCheckedChange = {
                                Settings.vibrate()
                                isRegistr = !isRegistr
                                k.edit {
                                    putBoolean("pegistrbukv", isRegistr)
                                }
                                AppNavGraphState.searchSettings = true
                            }
                        )
                        Text(
                            stringResource(R.string.registr),
                            fontSize = Settings.fontInterface.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            Settings.vibrate()
                            isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                            else 0
                            k.edit {
                                putInt("slovocalkam", isDakladnaeSupadzenne)
                            }
                            AppNavGraphState.searchSettings = true
                        }) {
                        Checkbox(
                            checked = isDakladnaeSupadzenne == 1,
                            onCheckedChange = {
                                Settings.vibrate()
                                isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                                else 0
                                k.edit {
                                    putInt("slovocalkam", isDakladnaeSupadzenne)
                                }
                                AppNavGraphState.searchSettings = true
                            }
                        )
                        Text(
                            stringResource(R.string.dakladnae_supadzenne),
                            fontSize = Settings.fontInterface.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        Column {
            if (viewModel.isProgressVisable) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                text = stringResource(R.string.searh_sviatyia_result, viewModel.searchList.size),
                fontStyle = FontStyle.Italic,
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            LazyColumn(
                Modifier.nestedScroll(nestedScrollConnection),
                state = searchBibleState
            ) {
                items(viewModel.searchList.size) { index ->
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                Settings.vibrate()
                                viewModel.setPerevod(context, Settings.CHYTANNI_BIBLIA, viewModel.searchList[index].title + " " + viewModel.searchList[index].glava.toString(), perevod)
                                viewModel.oldTextFieldValueState = viewModel.textFieldValueState.text
                                navigateToCytanniList(
                                    viewModel.searchList[index].title + " " + viewModel.searchList[index].glava.toString(),
                                    viewModel.searchList[index].styx - 1,
                                    perevod,
                                    Settings.CHYTANNI_BIBLIA
                                )
                            },
                        text = viewModel.searchList[index].text.toAnnotatedString(),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = Settings.fontInterface.sp
                    )
                    HorizontalDivider()
                }
                item {
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                }
            }
        }
    } else {
        BibliaMenuList(perevod, sort, innerPadding, viewModel, navigationActions, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
            navigateToCytanniList(chytanne, position, perevod2, biblia)
        }, navigateToBogaslujbovyia = { title, resurs ->
            navigateToBogaslujbovyia(title, resurs)
        })
    }
}

@Composable
fun BibliaMenuList(
    perevod: String, sort: Int, innerPadding: PaddingValues, viewModel: SearchBibleViewModel, navigationActions: AppNavigationActions, navigateToCytanniList: (String, Int, String, Int) -> Unit,
    navigateToBogaslujbovyia: (String, String) -> Unit
) {
    BoxWithConstraints {
        BibliaMenuList(perevod, sort, innerPadding, viewModel, navigationActions, navigateToCytanniList = { chytanne, position, perevod2, biblia ->
            navigateToCytanniList(chytanne, position, perevod2, biblia)
        }, navigateToBogaslujbovyia = { title, resurs ->
            navigateToBogaslujbovyia(title, resurs)
        })
    }
}

@Composable
fun BoxWithConstraintsScope.BibliaMenuList(
    perevod: String, sort: Int, innerPadding: PaddingValues, viewModel: SearchBibleViewModel, navigationActions: AppNavigationActions, navigateToCytanniList: (String, Int, String, Int) -> Unit,
    navigateToBogaslujbovyia: (String, String) -> Unit
) {
    val parentConstraints = this.constraints
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var dialogImageView by rememberSaveable { mutableStateOf(false) }
    var dialogVisable by remember { mutableStateOf(false) }
    if (dialogVisable) {
        DialogSemuxa(perevod) {
            dialogVisable = false
        }
    }
    var pesnyView by rememberSaveable { mutableStateOf(false) }
    var dialogPeryiadyView by rememberSaveable { mutableStateOf(false) }
    if (dialogPeryiadyView) {
        DialogPeryaidy {
            dialogPeryiadyView = false
        }
    }
    val perevodName = when (perevod) {
        Settings.PEREVODSEMUXI -> "biblia"
        Settings.PEREVODBOKUNA -> "bokuna"
        Settings.PEREVODCARNIAUSKI -> "carniauski"
        Settings.PEREVODNADSAN -> "nadsan"
        Settings.PEREVODCATOLIK -> "catolik"
        Settings.PEREVODSINOIDAL -> "sinaidal"
        Settings.PEREVODNEWAMERICANBIBLE -> "english"
        else -> "biblia"
    }
    var isDeliteVybranaeAll by remember { mutableStateOf(false) }
    if (isDeliteVybranaeAll) {
        val titlePerevod = when (perevod) {
            Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia2)
            Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal2)
            Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
            Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun2)
            Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski2)
            Settings.PEREVODCATOLIK -> stringResource(R.string.title_biblia_catolik2)
            Settings.PEREVODNEWAMERICANBIBLE -> stringResource(R.string.perevod_new_american_bible_2)
            else -> stringResource(R.string.title_biblia2)
        }
        DialogDelite(
            title = stringResource(R.string.vybranoe_biblia_delite, titlePerevod),
            onConfirmation = {
                val file = File("${context.filesDir}/vybranoe_${perevodName}.json")
                if (file.exists()) {
                    file.delete()
                }
                isDeliteVybranaeAll = false
            }
        ) {
            isDeliteVybranaeAll = false
        }
    }
    var dialogDownLoad by remember { mutableStateOf(false) }
    var novyZapavet by remember { mutableStateOf(false) }
    var isBibleTime by remember { mutableStateOf(false) }
    if (dialogDownLoad) {
        DialogDownLoadBible(viewModel, perevod, onConfirmation = {
            if (isBibleTime) {
                viewModel.bibleTime = true
                isBibleTime = false
            } else {
                navigationActions.navigateToBibliaList(novyZapavet, perevod)
            }
            dialogDownLoad = false
        }) {
            dialogDownLoad = false
        }
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        if (perevod != Settings.PEREVODCATOLIK) {
            TextButton(
                onClick = {
                    Settings.vibrate()
                    novyZapavet = false
                    when (perevod) {
                        Settings.PEREVODSINOIDAL -> {
                            val dir = File("${context.filesDir}/Sinodal")
                            if (!dir.exists()) {
                                dialogDownLoad = true
                            } else {
                                navigationActions.navigateToBibliaList(false, perevod)
                            }
                        }

                        Settings.PEREVODNEWAMERICANBIBLE -> {
                            val dir = File("${context.filesDir}/NewAmericanBible")
                            if (!dir.exists()) {
                                dialogDownLoad = true
                            } else {
                                navigationActions.navigateToBibliaList(false, perevod)
                            }
                        }

                        else -> navigationActions.navigateToBibliaList(false, perevod)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Primary,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    if (perevod == Settings.PEREVODNADSAN) stringResource(R.string.psalter)
                    else stringResource(R.string.stary_zapaviet),
                    fontSize = Settings.fontInterface.sp,
                    color = PrimaryTextBlack,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (perevod != Settings.PEREVODNADSAN) {
            TextButton(
                onClick = {
                    Settings.vibrate()
                    novyZapavet = true
                    when (perevod) {
                        Settings.PEREVODCATOLIK -> {
                            val dir = File("${context.filesDir}/Catolik")
                            if (!dir.exists()) {
                                dialogDownLoad = true
                            } else {
                                navigationActions.navigateToBibliaList(true, perevod)
                            }
                        }

                        Settings.PEREVODSINOIDAL -> {
                            val dir = File("${context.filesDir}/Sinodal")
                            if (!dir.exists()) {
                                dialogDownLoad = true
                            } else {
                                navigationActions.navigateToBibliaList(true, perevod)
                            }
                        }

                        Settings.PEREVODNEWAMERICANBIBLE -> {
                            val dir = File("${context.filesDir}/NewAmericanBible")
                            if (!dir.exists()) {
                                dialogDownLoad = true
                            } else {
                                navigationActions.navigateToBibliaList(true, perevod)
                            }
                        }

                        else -> navigationActions.navigateToBibliaList(true, perevod)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Primary,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    stringResource(R.string.novy_zapaviet),
                    fontSize = Settings.fontInterface.sp,
                    color = PrimaryTextBlack,
                    textAlign = TextAlign.Center
                )
            }
        }
        TextButton(
            onClick = {
                Settings.vibrate()
                when (perevod) {
                    Settings.PEREVODCATOLIK -> {
                        val dir = File("${context.filesDir}/Catolik")
                        if (!dir.exists()) {
                            dialogDownLoad = true
                            isBibleTime = true
                        } else {
                            viewModel.bibleTime = true
                        }
                    }

                    Settings.PEREVODSINOIDAL -> {
                        val dir = File("${context.filesDir}/Sinodal")
                        if (!dir.exists()) {
                            dialogDownLoad = true
                            isBibleTime = true
                        } else {
                            viewModel.bibleTime = true
                        }
                    }

                    Settings.PEREVODNEWAMERICANBIBLE -> {
                        val dir = File("${context.filesDir}/NewAmericanBible")
                        if (!dir.exists()) {
                            dialogDownLoad = true
                            isBibleTime = true
                        } else {
                            viewModel.bibleTime = true
                        }
                    }

                    else -> viewModel.bibleTime = true
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
                .size(width = 200.dp, height = Dp.Unspecified),
            colors = ButtonColors(
                Divider,
                Color.Unspecified,
                Color.Unspecified,
                Color.Unspecified
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.bible_time), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
        }
        val titlePerevod = stringResource(R.string.str_short_label1)
        val file = File("${LocalContext.current.filesDir}/vybranoe_${perevodName}.json")
        if (file.exists()) {
            val gson = Gson()
            val type =
                TypeToken.getParameterized(
                    ArrayList::class.java,
                    VybranaeData::class.java
                ).type
            val list = remember { mutableStateListOf<VybranaeData>() }
            if (list.isEmpty()) list.addAll(gson.fromJson(file.readText(), type))
            var removeItem by remember { mutableIntStateOf(-1) }
            if (removeItem != -1) {
                val titleVybrenae = stringResource(
                    R.string.vybranoe_biblia_delite, list[removeItem].title + " " + (list[removeItem].glava + 1)
                )
                DialogDelite(
                    title = titleVybrenae,
                    onConfirmation = {
                        list.removeAt(removeItem)
                        if (list.isEmpty() && file.exists()) {
                            file.delete()
                        } else {
                            file.writer().use {
                                it.write(gson.toJson(list, type))
                            }
                        }
                        removeItem = -1
                    }
                ) {
                    removeItem = -1
                }
            }
            var collapsedState by remember { mutableStateOf(AppNavGraphState.setItemsValue(titlePerevod, true)) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                Settings.vibrate()
                                AppNavGraphState.setItemsValue(titlePerevod)
                                collapsedState = !collapsedState
                            },
                            onLongClick = {
                                Settings.vibrate(true)
                                isDeliteVybranaeAll = true
                            }
                        )
                        .clip(MaterialTheme.shapes.small)
                        .background(Divider)
                        .align(Alignment.CenterHorizontally)
                        .size(width = 200.dp, height = Dp.Unspecified)
                ) {
                    Text(
                        text = titlePerevod,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f),
                        color = PrimaryText,
                        fontSize = Settings.fontInterface.sp,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        painter = if (!collapsedState)
                            painterResource(R.drawable.keyboard_arrow_down)
                        else
                            painterResource(R.drawable.keyboard_arrow_up),
                        contentDescription = null,
                        tint = PrimaryText,
                    )
                }
            }
            LaunchedEffect(sort) {
                when (sort) {
                    Settings.SORT_BY_ABC -> {
                        list.sortWith(
                            compareBy({
                                it.knigaText
                            }, {
                                it.glava
                            })
                        )
                    }

                    Settings.SORT_BY_TIME -> {
                        list.sortByDescending { it.id }
                    }

                    Settings.SORT_BY_CUSTOM -> {
                        list.clear()
                        list.addAll(gson.fromJson(file.readText(), type))
                    }
                }
            }
            viewModel.isIconSort = !collapsedState
            val typeBibleList = TypeToken.getParameterized(
                ArrayList::class.java, VybranaeData::class.java
            ).type
            val lazyColumnState = rememberLazyListState()
            val dragDropState = rememberDragDropState(lazyColumnState) { fromIndex, toIndex ->
                if (fromIndex < list.size && toIndex < list.size) {
                    list.apply { add(toIndex, removeAt(fromIndex)) }
                    val file = File("${context.filesDir}/vybranoe_${perevodName}.json")
                    file.writer().use {
                        it.write(gson.toJson(list, typeBibleList))
                    }
                    k.edit {
                        putInt("sortedVybranae", Settings.SORT_BY_CUSTOM)
                    }
                }
            }
            AnimatedVisibility(
                !collapsedState, enter = fadeIn(
                    tween(
                        durationMillis = 700, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .dragContainer(dragDropState)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(
                                constraints.copy(maxHeight = parentConstraints.maxHeight)
                            )

                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(0, 0)
                            }
                        },
                    state = lazyColumnState
                ) {
                    itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
                        DraggableItem(dragDropState, index) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        Settings.vibrate()
                                        val newList = StringBuilder()
                                        for (r in 0 until list.size) {
                                            val char = if (r == list.size - 1) ""
                                            else ";"
                                            newList.append(list[r].knigaText + " " + (list[r].glava + 1) + char)
                                        }
                                        navigateToCytanniList(
                                            newList.toString(),
                                            index,
                                            item.perevod,
                                            Settings.CHYTANNI_VYBRANAE
                                        )
                                    }
                                    .padding(start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp),
                                    painter = painterResource(R.drawable.poiter),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                                Text(
                                    item.title + " " + (item.glava + 1),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = Settings.fontInterface.sp
                                )
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .clickable {
                                            Settings.vibrate()
                                            removeItem = index
                                        }, painter = painterResource(R.drawable.delete), tint = MaterialTheme.colorScheme.secondary, contentDescription = stringResource(R.string.delite)
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        } else {
            viewModel.isIconSort = false
        }
        if (perevod == Settings.PEREVODNADSAN) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .align(Alignment.CenterHorizontally)
                    .size(width = 400.dp, height = Dp.Unspecified)
            ) {
                Row {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .weight(1f)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(TitleCalendarMounth)
                            .padding(10.dp),
                        text = stringResource(R.string.kafizma),
                        fontSize = Settings.fontInterface.sp,
                        textAlign = TextAlign.Center,
                        color = PrimaryTextBlack
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        Text(
                            modifier = Modifier
                                .padding(5.dp)
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Divider)
                                .padding(vertical = 5.dp)
                                .clickable {
                                    Settings.vibrate()
                                    val index = when (i) {
                                        1 -> "1"
                                        2 -> "9"
                                        3 -> "17"
                                        4 -> "24"
                                        5 -> "32"
                                        else -> "1"
                                    }
                                    navigateToCytanniList(
                                        "Пс $index",
                                        -1,
                                        perevod,
                                        Settings.CHYTANNI_BIBLIA
                                    )
                                },
                            text = i.toString(),
                            fontSize = Settings.fontInterface.sp,
                            textAlign = TextAlign.Center,
                            color = PrimaryText
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 6..10) {
                        Text(
                            modifier = Modifier
                                .padding(5.dp)
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Divider)
                                .padding(vertical = 5.dp)
                                .clickable {
                                    Settings.vibrate()
                                    val index = when (i) {
                                        6 -> "37"
                                        7 -> "46"
                                        8 -> "55"
                                        9 -> "64"
                                        10 -> "70"
                                        else -> "37"
                                    }
                                    navigateToCytanniList(
                                        "Пс $index",
                                        -1,
                                        perevod,
                                        Settings.CHYTANNI_BIBLIA
                                    )
                                },
                            text = i.toString(),
                            fontSize = Settings.fontInterface.sp,
                            textAlign = TextAlign.Center,
                            color = PrimaryText
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 11..15) {
                        Text(
                            modifier = Modifier
                                .padding(5.dp)
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Divider)
                                .padding(vertical = 5.dp)
                                .clickable {
                                    Settings.vibrate()
                                    val index = when (i) {
                                        11 -> "77"
                                        12 -> "85"
                                        13 -> "91"
                                        14 -> "101"
                                        15 -> "105"
                                        else -> "77"
                                    }
                                    navigateToCytanniList(
                                        "Пс $index",
                                        -1,
                                        perevod,
                                        Settings.CHYTANNI_BIBLIA
                                    )
                                },
                            text = i.toString(),
                            fontSize = Settings.fontInterface.sp,
                            textAlign = TextAlign.Center,
                            color = PrimaryText
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 16..20) {
                        Text(
                            modifier = Modifier
                                .padding(5.dp)
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(Divider)
                                .padding(vertical = 5.dp)
                                .clickable {
                                    Settings.vibrate()
                                    val index = when (i) {
                                        16 -> "109"
                                        17 -> "118"
                                        18 -> "119"
                                        19 -> "134"
                                        20 -> "143"
                                        else -> "109"
                                    }
                                    navigateToCytanniList(
                                        "Пс $index",
                                        -1,
                                        perevod,
                                        Settings.CHYTANNI_BIBLIA
                                    )
                                },
                            text = i.toString(),
                            fontSize = Settings.fontInterface.sp,
                            textAlign = TextAlign.Center,
                            color = PrimaryText
                        )
                    }
                }
            }
            val malitvaPered = stringResource(R.string.malitva_pered)
            TextButton(
                onClick = {
                    Settings.vibrate()
                    navigateToBogaslujbovyia(malitvaPered, "nadsan_pered.html")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Divider,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.malitva_pered), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
            }
            val malitvaPosle = stringResource(R.string.malitva_posle)
            TextButton(
                onClick = {
                    Settings.vibrate()
                    navigateToBogaslujbovyia(malitvaPosle, "nadsan_posle.html")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Divider,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.malitva_posle), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable {
                            Settings.vibrate()
                            pesnyView = !pesnyView
                        }
                        .clip(MaterialTheme.shapes.small)
                        .background(Divider)
                        .align(Alignment.CenterHorizontally)
                        .size(width = 200.dp, height = Dp.Unspecified)
                ) {
                    Text(
                        text = stringResource(R.string.pesni),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f),
                        color = PrimaryText,
                        fontSize = Settings.fontInterface.sp,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        painter = if (pesnyView)
                            painterResource(R.drawable.keyboard_arrow_down)
                        else
                            painterResource(R.drawable.keyboard_arrow_up),
                        contentDescription = null,
                        tint = PrimaryText,
                    )
                }
            }
            AnimatedVisibility(
                pesnyView, enter = fadeIn(
                    tween(
                        durationMillis = 700, easing = LinearOutSlowInEasing
                    )
                ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
            ) {
                Column {
                    for (i in 1..9) {
                        val pesnia = stringResource(R.string.pesnia, i)
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clickable {
                                    Settings.vibrate()
                                    navigateToBogaslujbovyia(
                                        pesnia,
                                        when (i) {
                                            1 -> "nadsan_pesni_1.html"
                                            2 -> "nadsan_pesni_2.html"
                                            3 -> "nadsan_pesni_3.html"
                                            4 -> "nadsan_pesni_4.html"
                                            5 -> "nadsan_pesni_5.html"
                                            6 -> "nadsan_pesni_6.html"
                                            7 -> "nadsan_pesni_7.html"
                                            8 -> "nadsan_pesni_8.html"
                                            9 -> "nadsan_pesni_9.html"
                                            else -> "nadsan_pesni_1.html"
                                        }
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(5.dp),
                                painter = painterResource(R.drawable.poiter),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                            Text(
                                stringResource(R.string.pesnia, i),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = Settings.fontInterface.sp
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
            TextButton(
                onClick = {
                    Settings.vibrate()
                    dialogPeryiadyView = true
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Divider,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.peryiady), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable {
                            Settings.vibrate()
                            dialogImageView = !dialogImageView
                        }
                        .clip(MaterialTheme.shapes.small)
                        .background(Divider)
                        .align(Alignment.CenterHorizontally)
                        .size(width = 200.dp, height = Dp.Unspecified)
                ) {
                    Text(
                        text = stringResource(R.string.title_psalter_privila),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f),
                        color = PrimaryText,
                        fontSize = Settings.fontInterface.sp,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        painter = if (dialogImageView)
                            painterResource(R.drawable.keyboard_arrow_down)
                        else
                            painterResource(R.drawable.keyboard_arrow_up),
                        contentDescription = null,
                        tint = PrimaryText,
                    )
                }
            }
        }
        AnimatedVisibility(
            dialogImageView, enter = fadeIn(
                tween(
                    durationMillis = 700, easing = LinearOutSlowInEasing
                )
            ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
        ) {
            Image(
                painter = painterResource(R.drawable.pravily_chytannia_psaltyria), contentDescription = null, modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(), contentScale = ContentScale.FillWidth
            )
        }
        if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODCATOLIK) {
            TextButton(
                onClick = {
                    Settings.vibrate()
                    dialogVisable = true
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(width = 200.dp, height = Dp.Unspecified),
                colors = ButtonColors(
                    Divider,
                    Color.Unspecified,
                    Color.Unspecified,
                    Color.Unspecified
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    stringResource(R.string.alesyaSemukha2),
                    fontSize = Settings.fontInterface.sp,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
    }
}

@Composable
fun DialogSemuxa(
    perevod: String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.alesyaSemukha).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                val context = LocalContext.current
                val text = when (perevod) {
                    Settings.PEREVODBOKUNA -> openAssetsResources(context, "all_rights_reserved_bokun.html")
                    Settings.PEREVODCATOLIK -> openAssetsResources(context, "all_rights_reserved_catolik.html")
                    else -> openAssetsResources(context, "all_rights_reserved_semuxa.html")
                }
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    HtmlText(text = text, modifier = Modifier.padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDownLoadBible(
    viewModel: SearchBibleViewModel,
    perevod: String,
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit
) {
    val titleBible = when (perevod) {
        Settings.PEREVODCATOLIK -> stringResource(R.string.title_biblia_catolik2)
        Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal2)
        Settings.PEREVODNEWAMERICANBIBLE -> stringResource(R.string.perevod_new_american_bible_2)
        else -> stringResource(R.string.bsinaidal2)
    }
    val context = LocalActivity.current as MainActivity
    var download by remember { mutableStateOf(false) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var dirCount by remember { mutableLongStateOf(0) }
    var izm by remember { mutableStateOf("O Кб") }
    LaunchedEffect(Unit) {
        if (Settings.isNetworkAvailable(context)) {
            val result = when (perevod) {
                Settings.PEREVODCATOLIK -> Malitounik.referens.child("/chytanne/Catolik/catolik.zip").metadata.await()
                Settings.PEREVODSINOIDAL -> Malitounik.referens.child("/chytanne/Sinodal/sinaidal.zip").metadata.await()
                Settings.PEREVODNEWAMERICANBIBLE -> Malitounik.referens.child("/chytanne/NewAmericanBible/NewAmericanBible.zip").metadata.await()
                else -> Malitounik.referens.child("/chytanne/Sinodal/sinaidal.zip").metadata.await()
            }
            dirCount = result.sizeBytes
            izm = if (dirCount / 1024 > 1000) {
                formatFigureTwoPlaces(
                    BigDecimal
                        .valueOf(dirCount.toFloat() / 1024 / 1024.toDouble())
                        .setScale(2, RoundingMode.HALF_UP)
                        .toFloat()
                ) + " Мб"
            } else {
                formatFigureTwoPlaces(
                    BigDecimal
                        .valueOf(dirCount.toFloat() / 1024.toDouble())
                        .setScale(2, RoundingMode.HALF_UP)
                        .toFloat()
                ) + " Кб"
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(download) {
        if (download) {
            if (Settings.isNetworkAvailable(context)) {
                isProgressVisable = true
                viewModel.downLoadBibile(context, perevod)
                viewModel.downLoadVersionBibile(context, perevod)
                viewModel.saveVersionFile(context, perevod)
                val destinationDir = when (perevod) {
                    Settings.PEREVODCATOLIK -> "${context.filesDir}/Catolik"

                    Settings.PEREVODSINOIDAL -> "${context.filesDir}/Sinodal"

                    Settings.PEREVODNEWAMERICANBIBLE -> "${context.filesDir}/NewAmericanBible"

                    else -> "${context.filesDir}/Sinodal"
                }
                val dir = File(destinationDir)
                if (!dir.exists()) dir.mkdir()
                val zipFile = File("${context.filesDir}/cache/cache.zip")
                ZipFile(zipFile).use { zip ->
                    zip.entries().asSequence().forEach { entry ->
                        val entryFile = File(destinationDir, entry.name)
                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(entryFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
                isProgressVisable = false
                zipFile.delete()
                onConfirmation()
            } else {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(if (viewModel.fireBaseVersionUpdate) R.string.title_down_load_update else R.string.title_down_load), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                if (isProgressVisable) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = stringResource(R.string.content_down_load, titleBible, izm), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (!download) {
                        TextButton(
                            onClick = {
                                Settings.vibrate()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                            Text(stringResource(R.string.cansel), fontSize = 18.sp)
                        }

                        TextButton(
                            onClick = {
                                Settings.vibrate()
                                download = true
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                            Text(stringResource(R.string.ok), fontSize = 18.sp)
                        }
                    } else {
                        TextButton(
                            onClick = {
                                Settings.vibrate()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.check), contentDescription = null)
                            Text(stringResource(R.string.close), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogPeryaidy(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = stringResource(R.string.peryiady).uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                Row(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    HtmlText(
                        modifier = Modifier.padding(10.dp),
                        text = openAssetsResources(LocalContext.current, "nadsan_periody.txt"),
                        fontSize = Settings.fontInterface.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogImage(
    painter: Painter,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painter, contentDescription = null, Modifier
                        .fillMaxWidth(), contentScale = ContentScale.FillWidth
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            Settings.vibrate()
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = null)
                        Text(stringResource(R.string.close), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun zamena(replase: String, ignoreCase: Boolean = true): String {
    var replase1 = replase
    replase1 = replase1.replace("и", "і", ignoreCase)
    replase1 = replase1.replace("щ", "ў", ignoreCase)
    replase1 = replase1.replace("ъ", "’", ignoreCase)
    replase1 = replase1.replace("'", "’", ignoreCase)
    replase1 = replase1.replace("све", "сьве", ignoreCase)
    replase1 = replase1.replace("сві", "сьві", ignoreCase)
    replase1 = replase1.replace("свя", "сьвя", ignoreCase)
    replase1 = replase1.replace("зве", "зьве", ignoreCase)
    replase1 = replase1.replace("зві", "зьві", ignoreCase)
    replase1 = replase1.replace("звя", "зьвя", ignoreCase)
    replase1 = replase1.replace("зме", "зьме", ignoreCase)
    replase1 = replase1.replace("змі", "зьмі", ignoreCase)
    replase1 = replase1.replace("змя", "зьмя", ignoreCase)
    replase1 = replase1.replace("зня", "зьня", ignoreCase)
    replase1 = replase1.replace("сле", "сьле", ignoreCase)
    replase1 = replase1.replace("слі", "сьлі", ignoreCase)
    replase1 = replase1.replace("сль", "сьль", ignoreCase)
    replase1 = replase1.replace("слю", "сьлю", ignoreCase)
    replase1 = replase1.replace("сля", "сьля", ignoreCase)
    replase1 = replase1.replace("сне", "сьне", ignoreCase)
    replase1 = replase1.replace("сні", "сьні", ignoreCase)
    replase1 = replase1.replace("сню", "сьню", ignoreCase)
    replase1 = replase1.replace("сня", "сьня", ignoreCase)
    replase1 = replase1.replace("спе", "сьпе", ignoreCase)
    replase1 = replase1.replace("спі", "сьпі", ignoreCase)
    replase1 = replase1.replace("спя", "сьпя", ignoreCase)
    replase1 = replase1.replace("сце", "сьце", ignoreCase)
    replase1 = replase1.replace("сці", "сьці", ignoreCase)
    replase1 = replase1.replace("сць", "сьць", ignoreCase)
    replase1 = replase1.replace("сцю", "сьцю", ignoreCase)
    replase1 = replase1.replace("сця", "сьця", ignoreCase)
    replase1 = replase1.replace("ццё", "цьцё", ignoreCase)
    replase1 = replase1.replace("цці", "цьці", ignoreCase)
    replase1 = replase1.replace("ццю", "цьцю", ignoreCase)
    replase1 = replase1.replace("ззе", "зьзе", ignoreCase)
    replase1 = replase1.replace("ззі", "зьзі", ignoreCase)
    replase1 = replase1.replace("ззю", "зьзю", ignoreCase)
    replase1 = replase1.replace("ззя", "зьзя", ignoreCase)
    replase1 = replase1.replace("зле", "зьле", ignoreCase)
    replase1 = replase1.replace("злі", "зьлі", ignoreCase)
    replase1 = replase1.replace("злю", "зьлю", ignoreCase)
    replase1 = replase1.replace("зля", "зьля", ignoreCase)
    replase1 = replase1.replace("збе", "зьбе", ignoreCase)
    replase1 = replase1.replace("збі", "зьбі", ignoreCase)
    replase1 = replase1.replace("збя", "зьбя", ignoreCase)
    replase1 = replase1.replace("нне", "ньне", ignoreCase)
    replase1 = replase1.replace("нні", "ньні", ignoreCase)
    replase1 = replase1.replace("нню", "ньню", ignoreCase)
    replase1 = replase1.replace("ння", "ньня", ignoreCase)
    replase1 = replase1.replace("лле", "льле", ignoreCase)
    replase1 = replase1.replace("ллі", "льлі", ignoreCase)
    replase1 = replase1.replace("ллю", "льлю", ignoreCase)
    replase1 = replase1.replace("лля", "льля", ignoreCase)
    replase1 = replase1.replace("дск", "дзк", ignoreCase)
    replase1 = replase1.replace("дств", "дзтв", ignoreCase)
    replase1 = replase1.replace("з'е", "зье", ignoreCase)
    replase1 = replase1.replace("з'я", "зья", ignoreCase)
    return replase1
}

fun getPrazdnik(
    context: Context, razdel: Int, yearG: Int = Calendar.getInstance().get(Calendar.YEAR)
): ArrayList<Prazdniki> {
    val a = yearG % 19
    val b = yearG % 4
    val cx = yearG % 7
    val ks = yearG / 100
    val p = (13 + 8 * ks) / 25
    val q = ks / 4
    val m = (15 - p + ks - q) % 30
    val n = (4 + ks - q) % 7
    val d = (19 * a + m) % 30
    val ex = (2 * b + 4 * cx + 6 * d + n) % 7
    val monthP: Int
    var dataP: Int
    if (d + ex <= 9) {
        dataP = d + ex + 22
        monthP = 3
    } else {
        dataP = d + ex - 9
        if (d == 29 && ex == 6) dataP = 19
        if (d == 28 && ex == 6) dataP = 18
        monthP = 4
    }
    val monthName = context.resources.getStringArray(R.array.meciac_smoll)
    val nedelName = context.resources.getStringArray(R.array.dni_nedeli)
    val prazdnikiAll = ArrayList<Prazdniki>()
    val c = GregorianCalendar(yearG, monthP - 1, dataP)
    when (razdel) {
        1 -> {
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S1),
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + " " + yearG + " году, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    1,
                )
            )
        }

        2 -> {
            c.set(yearG, Calendar.JANUARY, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S2),
                    "6 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, 1, 2)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S3),
                    "2 лютага, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.MARCH, 25)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S4),
                    "25 сакавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            c.add(Calendar.DATE, -7)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S5),
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.add(Calendar.DATE, 46)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S6),
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.add(Calendar.DATE, 10)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S7),
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.AUGUST, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S8),
                    "6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.AUGUST, 15)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S9),
                    "15 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 8)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S10),
                    "8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 14)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S11),
                    "14 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.NOVEMBER, 21)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S12),
                    "21 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
            c.set(yearG, Calendar.DECEMBER, 25)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S13),
                    "25 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    2,
                )
            )
        }

        3 -> {
            c.set(yearG, Calendar.JANUARY, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S14),
                    "1 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.JUNE, 24)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S15),
                    "24 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.JUNE, 29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S16),
                    "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.AUGUST, 29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S17),
                    "29 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
            c.set(yearG, Calendar.OCTOBER, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    context.resources.getString(R.string.S18),
                    "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    3,
                )
            )
        }

        4 -> {
            c.add(Calendar.DATE, -57)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Мясапусная задушная субота",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -50)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін усіх сьвятых айцоў, манахаў і посьнікаў",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Субота 3-га тыдня Вялікага посту",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -22)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Субота 4-га тыдня Вялікага посту",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 9)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Радаўніца",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 48)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Траецкая бацькоўская субота",
                    c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
            for (i in 19..25) {
                c.set(yearG, Calendar.OCTOBER, i)
                val dayofweek = c[Calendar.DAY_OF_WEEK]
                if (7 == dayofweek) {
                    prazdnikiAll.add(
                        Prazdniki(
                            c[Calendar.DAY_OF_YEAR],
                            "Зьмітраўская бацькоўская субота",
                            c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                            4,
                        )
                    )
                }
            }
            c.set(yearG, Calendar.NOVEMBER, 2)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Дзяды, дзень успаміну памёрлых",
                    "2 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    4,
                )
            )
        }

        5 -> {
            c.set(yearG, Calendar.JULY, 11)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін мучаніцкай сьмерці ў катэдры сьв. Сафіі ў Полацку 5 манахаў-базыльянаў",
                    "11 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    5,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 15)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін Бабровіцкай трагедыі (зьнішчэньне ў 1942 г. жыхароў уніяцкай парафіі в. Бабровічы Івацэвіцкага р-ну)",
                    "15 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    5,
                )
            )
            c.set(yearG, Calendar.OCTOBER, 18)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Успамін Берасьцейскай царкоўнай Уніі 1596 году",
                    "18(8) кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    5,
                )
            )
        }

        6 -> {
            c.set(yearG, Calendar.JANUARY, 30)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Гомель: Трох Сьвяціцеляў",
                    "30 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Антвэрпан: Уваскрасеньня Хрыстовага",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Віцебск: Уваскрасеньня Хрыстовага",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Маладэчна: Хрыста Чалавекалюбцы",
                    "1 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 7)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Івацэвічы: Маці Божай Жыровіцкай",
                    "7 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 11)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Баранавічы: сьвятых роўнаапостальных Кірылы і Мятода",
                    "11 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.MAY, 13)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Горадня: Маці Божай Фацімскай",
                    "13 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.add(Calendar.DATE, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: Сьвятога Духа",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.JUNE, 27)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: Маці Божай Нястомнай Дапамогі",
                    "27 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.JUNE, 29)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Лондан: сьвятых апосталаў Пятра і Паўла",
                    "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            val chisla = intArrayOf(24, 25, 26, 27, 28, 29, 30)
            var brest = 24
            for (aChisla in chisla) {
                val cal = GregorianCalendar(yearG, 5, aChisla)
                val deyNed = cal[Calendar.DAY_OF_WEEK]
                if (deyNed == Calendar.SATURDAY) brest = aChisla
            }
            c.set(yearG, Calendar.JUNE, brest)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Берасьце: сьвятых братоў-апосталаў Пятра і Андрэя",
                    c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Полацак: манастыр сьв. Барыса і Глеба",
                    "24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 8)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Магілёў: Бялыніцкай іконы Маці Божай",
                    "8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.SEPTEMBER, 16)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Ліда: сьвятамучаніка Язафата Полацкага",
                    "16 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.OCTOBER, 1)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Ворша: Покрыва Найсьвяцейшай Багародзіцы",
                    "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.NOVEMBER, 12)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Полацак: сьвятамучаніка Язафата",
                    "12 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            c.set(yearG, Calendar.DECEMBER, 6)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: сьвятога Мікалая Цудатворцы",
                    "6 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
            val dayofweekrastvo = GregorianCalendar(yearG, Calendar.DECEMBER, 25)[Calendar.DAY_OF_WEEK]
            val chislaJiazep = intArrayOf(26, 27, 28, 29, 30, 31)
            var minsk = 26
            for (aChisla in chislaJiazep) {
                val deyNed = GregorianCalendar(yearG, Calendar.DECEMBER, aChisla)[Calendar.DAY_OF_WEEK]
                if (dayofweekrastvo != Calendar.SUNDAY) {
                    if (deyNed == Calendar.SUNDAY) minsk = aChisla
                } else {
                    if (deyNed == Calendar.MONDAY) minsk = aChisla
                }
            }
            c.set(yearG, Calendar.DECEMBER, minsk)
            prazdnikiAll.add(
                Prazdniki(
                    c[Calendar.DAY_OF_YEAR],
                    "Менск: праведнага Язэпа",
                    c[Calendar.DATE].toString() + " сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]],
                    6,
                )
            )
        }
    }
    prazdnikiAll.sortBy { it.dayOfYear }
    return prazdnikiAll
}

data class Prazdniki(val dayOfYear: Int, val opisanie: String, val opisanieData: String, val typeSviat: Int)

data class FindString(val str: String, val position: Int)

data class SearchBibleItem(
    val title: String, val glava: Int, val styx: Int, val resource: String, val text: AnnotatedString.Builder
)