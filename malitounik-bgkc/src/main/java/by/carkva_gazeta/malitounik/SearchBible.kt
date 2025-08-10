package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryBlack
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.openAssetsResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.GregorianCalendar

var searchJob: Job? = null
val searchList = SnapshotStateList<SearchBibleItem>()
val searchListSvityia = SnapshotStateList<Prazdniki>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBible(
    navController: NavHostController, searchBibleState: LazyListState, perevod: String, isBogaslujbovyiaSearch: Boolean, navigateToCytanniList: (String, Int, String) -> Unit, navigateToBogaslujbovyia: (title: String, resurs: String) -> Unit
) {
    var searchSettings by remember { mutableStateOf(false) }
    var isProgressVisable by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldLoaded by remember { mutableStateOf(false) }
    var searshString by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(searchSettings, searshString) {
        if (searchSettings) {
            searchList.clear()
            searchSettings = false
        }
        if (searshString.trim().length >= 3 && searchList.isEmpty()) {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                isProgressVisable = true
                searchList.clear()
                val list = withContext(Dispatchers.IO) {
                    return@withContext doInBackground(context, searshString.trim(), perevod, isBogaslujbovyiaSearch)
                }
                searchList.addAll(list)
                isProgressVisable = false
            }
        } else {
            searchJob?.cancel()
            isProgressVisable = false
        }
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset, source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var showDropdown by remember { mutableStateOf(false) }
    var isRegistr by remember { mutableStateOf(k.getBoolean("pegistrbukv", true)) }
    var isDakladnaeSupadzenne by remember { mutableIntStateOf(k.getInt("slovocalkam", 0)) }
    var backPressHandled by remember { mutableStateOf(false) }
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window, view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                if (!textFieldLoaded) {
                                    focusRequester.requestFocus()
                                    textFieldLoaded = true
                                }
                            }, value = searshString, onValueChange = { newText ->
                            searchList.clear()
                            var edit = newText
                            if (perevod == Settings.PEREVODSINOIDAL) {
                                edit = edit.replace("і", "и")
                                edit = edit.replace("ў", "щ")
                                edit = edit.replace("І", "И")
                                edit = edit.replace("Ў", "Щ")
                                edit = edit.replace("'", "ъ")
                            } else {
                                edit = edit.replace("и", "і")
                                edit = edit.replace("щ", "ў")
                                edit = edit.replace("И", "І")
                                edit = edit.replace("Щ", "Ў")
                                edit = edit.replace("ъ", "'")
                            }
                            searshString = edit
                        }, singleLine = true, leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.search), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                            )
                        }, trailingIcon = {
                            if (searshString.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searshString = ""
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.close), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }, colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onTertiary, unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary, focusedTextColor = PrimaryTextBlack, focusedIndicatorColor = PrimaryTextBlack, unfocusedTextColor = PrimaryTextBlack, unfocusedIndicatorColor = PrimaryTextBlack, cursorColor = PrimaryTextBlack
                        ), textStyle = TextStyle(fontSize = TextUnit(Settings.fontInterface, TextUnitType.Sp))
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!backPressHandled) {
                            backPressHandled = true
                            navController.popBackStack()
                        }
                    }, content = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                        )
                    })
                },
                actions = {
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(
                            painter = painterResource(R.drawable.settings), contentDescription = "", tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary),
            )
        }, modifier = Modifier
    ) { innerPadding ->
        Box(
            Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr), innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
            )
        ) {
            if (showDropdown) {
                ModalBottomSheet(
                    scrimColor = Color.Transparent, properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false), containerColor = MaterialTheme.colorScheme.surfaceContainer, onDismissRequest = {
                        showDropdown = false
                    }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                isRegistr = !isRegistr
                                k.edit {
                                    putBoolean("pegistrbukv", isRegistr)
                                }
                                searchSettings = true
                            }) {
                            Checkbox(
                                checked = !isRegistr, onCheckedChange = {
                                    isRegistr = !isRegistr
                                    k.edit {
                                        putBoolean("pegistrbukv", isRegistr)
                                    }
                                    searchSettings = true
                                })
                            Text(
                                stringResource(R.string.registr), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                                else 0
                                k.edit {
                                    putInt("slovocalkam", isDakladnaeSupadzenne)
                                }
                                searchSettings = true
                            }) {
                            Checkbox(
                                checked = isDakladnaeSupadzenne == 1, onCheckedChange = {
                                    isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                                    else 0
                                    k.edit {
                                        putInt("slovocalkam", isDakladnaeSupadzenne)
                                    }
                                    searchSettings = true
                                })
                            Text(
                                stringResource(R.string.dakladnae_supadzenne), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp), text = stringResource(R.string.searh_sviatyia_result, searchList.size), fontStyle = FontStyle.Italic, fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.secondary
                )
                LazyColumn(
                    Modifier.nestedScroll(nestedScrollConnection), state = searchBibleState
                ) {
                    items(searchList.size) { index ->
                        Text(
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    if (isBogaslujbovyiaSearch) {
                                        navigateToBogaslujbovyia(searchList[index].subTitle, searchList[index].resource)
                                    } else {
                                        navigateToCytanniList(
                                            searchList[index].subTitle + " " + searchList[index].glava.toString(), searchList[index].styx - 1, perevod
                                        )
                                    }
                                }, text = searchList[index].text.toAnnotatedString(), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                        )
                        HorizontalDivider()
                    }
                    item {
                        Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                    }
                }
            }
        }
        if (isProgressVisable) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    onSearchStart: () -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val options = stringArrayResource(R.array.serche_bible)
    var expanded by remember { mutableStateOf(false) }
    val textFieldNotificstionState = rememberTextFieldState(options[k.getInt("biblia_seash", 0)])
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Row(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .clip(MaterialTheme.shapes.small)
                .clickable {}
                .background(Divider)
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                text = textFieldNotificstionState.text.toString(),
                fontSize = (Settings.fontInterface - 2).sp,
                color = PrimaryText,
            )
            Icon(
                modifier = Modifier
                    .padding(start = 21.dp, end = 2.dp)
                    .size(22.dp, 22.dp),
                painter = painterResource(if (expanded) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                tint = PrimaryText,
                contentDescription = ""
            )
        }
        ExposedDropdownMenu(
            containerColor = Divider,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = Settings.fontInterface.sp) }, onClick = {
                        textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option)
                        expanded = false
                        k.edit {
                            putInt("biblia_seash", index)
                        }
                        onSearchStart()
                    }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding, colors = MenuDefaults.itemColors(textColor = PrimaryText)
                )
            }
        }
    }
}

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

fun bogashlugbovya(context: Context, poshuk: String, secondRun: Boolean = false): ArrayList<SearchBibleItem> {
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
                poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
            }
        }
    }
    val bogaslugbovyiaListAll = ArrayList<BogaslujbovyiaListData>()
    bogaslugbovyiaListAll.addAll(getBogaslujbovyia())
    bogaslugbovyiaListAll.addAll(getMalitvy())
    bogaslugbovyiaListAll.addAll(getAkafist())
    bogaslugbovyiaListAll.addAll(getRujanec())
    bogaslugbovyiaListAll.addAll(getAktoix())
    bogaslugbovyiaListAll.addAll(getViachernia())
    bogaslugbovyiaListAll.addAll(getTraparyKandakiShtodzennyia())
    bogaslugbovyiaListAll.addAll(getTraparyKandakiNiadzelnyia())
    bogaslugbovyiaListAll.addAll(getMalitvyPasliaPrychascia())
    bogaslugbovyiaListAll.addAll(getTrebnik())
    bogaslugbovyiaListAll.addAll(getMineiaAgulnaia())
    val slugbovyiaTextu = SlugbovyiaTextu()
    val listPast = slugbovyiaTextu.getAllSlugbovyiaTextu()
    listPast.forEach { slugbovyiaTextuData ->
        bogaslugbovyiaListAll.add(BogaslujbovyiaListData(slugbovyiaTextuData.title + ". " + slugbovyiaTextu.getNazouSluzby(slugbovyiaTextuData.sluzba), slugbovyiaTextuData.resource))
    }
    for (i in 0 until bogaslugbovyiaListAll.size) {
        if (searchJob?.isActive == false) break
        var nazva = context.getString(R.string.error_ch)
        val bibleline = openAssetsResources(context, bogaslugbovyiaListAll[i].resource)
        val t1 = bibleline.indexOf("<strong>")
        if (t1 != -1) {
            val t2 = bibleline.indexOf("</strong>", t1 + 8)
            nazva = bibleline.substring(t1 + 8, t2)
            nazva = AnnotatedString.fromHtml(nazva).text
        }
        val prepinanie = AnnotatedString.fromHtml(bibleline).text
        val poshuk2 = findChars(context, poshuk1, prepinanie)
        if (poshuk2.isEmpty()) continue
        val span = AnnotatedString.Builder()
        span.append(nazva)
        seashpost.add(SearchBibleItem(nazva, 0, 0, bogaslugbovyiaListAll[i].resource, span))
    }
    return seashpost
}

fun biblia(
    context: Context, poshuk: String, perevod: String, secondRun: Boolean = false
): ArrayList<SearchBibleItem> {
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
                    poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                }
            }
        }
    }
    val rangeBibile = if (perevod == Settings.PEREVODNADSAN) 0..0
    else 0..1
    for (novyZapaviet in rangeBibile) {
        val list = if (novyZapaviet == 0) getNameBook(context, perevod, false)
        else getNameBook(context, perevod, true)
        val subTitleListName = if (novyZapaviet == 0) setStaryZapavet(list, perevod)
        else setNovyZapavet(list, perevod)
        val range = when (k.getInt("biblia_seash", 0)) {
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
        for (i in range) {
            if (searchJob?.isActive == false) break
            val nazva = list[i]
            val subTitle = subTitleListName[i].subTitle
            val zavet = if (novyZapaviet == 1) "n"
            else "s"
            val prevodName = when (perevod) {
                Settings.PEREVODSEMUXI -> "chytanne/Semucha/biblia"
                Settings.PEREVODBOKUNA -> "chytanne/Bokun/bokuna"
                Settings.PEREVODCARNIAUSKI -> "chytanne/Carniauski/carniauski"
                Settings.PEREVODNADSAN -> "chytanne/psaltyr_nadsan.txt"
                Settings.PEREVODSINOIDAL -> "chytanne/Sinodal/sinaidal"
                else -> "chytanne/Semucha/biblia"
            }
            var glava = 0
            val split = if (perevod == Settings.PEREVODNADSAN) openAssetsResources(context, prevodName).split("===")
            else openAssetsResources(context, "$prevodName$zavet${i + 1}.txt").split("===")
            for (e in 1 until split.size) {
                glava++
                val bibleline = split[e].split("\n")
                var stix = 0
                for (r in 1 until bibleline.size) {
                    stix++
                    var aSviatyia = HtmlCompat.fromHtml(bibleline[r], HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
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
                        val color = if (Settings.dzenNoch.value) PrimaryBlack
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
                    poshuk = poshuk.replace(poshuk, poshuk.substring(0, r), true)
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
    val subTitle: String, val glava: Int, val styx: Int, val resource: String, val text: AnnotatedString.Builder
)