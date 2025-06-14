package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
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

var searchJob: Job? = null
val searchList = SnapshotStateList<SearchBibleItem>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBible(
    navController: NavHostController,
    searchBibleState: LazyListState,
    perevod: String,
    isBogaslujbovyiaSearch: Boolean,
    navigateToCytanniList: (String, Int, String) -> Unit,
    navigateToBogaslujbovyia: (title: String, resurs: String) -> Unit
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
                available: Offset,
                source: NestedScrollSource
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
            window,
            view
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
                            },
                        value = searshString,
                        onValueChange = { newText ->
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
                            if (searshString.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searshString = ""
                                    }) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
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
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!backPressHandled) {
                                backPressHandled = true
                                navController.popBackStack()
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                actions = {
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary),
            )
        }, modifier = Modifier
    ) { innerPadding ->
        Box(
            Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            if (showDropdown) {
                ModalBottomSheet(
                    scrimColor = Color.Transparent,
                    properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                    containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = {
                        showDropdown = false
                    }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (!(perevod == Settings.PEREVODNADSAN || isBogaslujbovyiaSearch)) {
                            DropdownMenuBox(onSearchStart = { searchSettings = true })
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                isRegistr = !isRegistr
                                k.edit {
                                    putBoolean("pegistrbukv", isRegistr)
                                }
                                searchSettings = true
                            }) {
                            Checkbox(
                                checked = !isRegistr,
                                onCheckedChange = {
                                    isRegistr = !isRegistr
                                    k.edit {
                                        putBoolean("pegistrbukv", isRegistr)
                                    }
                                    searchSettings = true
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
                                isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                                else 0
                                k.edit {
                                    putInt("slovocalkam", isDakladnaeSupadzenne)
                                }
                                searchSettings = true
                            }) {
                            Checkbox(
                                checked = isDakladnaeSupadzenne == 1,
                                onCheckedChange = {
                                    isDakladnaeSupadzenne = if (isDakladnaeSupadzenne == 0) 1
                                    else 0
                                    k.edit {
                                        putInt("slovocalkam", isDakladnaeSupadzenne)
                                    }
                                    searchSettings = true
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
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    text = stringResource(R.string.searh_sviatyia_result, searchList.size),
                    fontStyle = FontStyle.Italic,
                    fontSize = Settings.fontInterface.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                LazyColumn(
                    Modifier.nestedScroll(nestedScrollConnection),
                    state = searchBibleState
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
                                            searchList[index].subTitle + " " + searchList[index].glava.toString(),
                                            searchList[index].styx - 1,
                                            perevod
                                        )
                                    }
                                },
                            text = searchList[index].text.toAnnotatedString(),
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
        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            state = textFieldNotificstionState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(focusedTextColor = PrimaryText, unfocusedTextColor = PrimaryText, focusedContainerColor = Divider, unfocusedContainerColor = Divider, focusedTrailingIconColor = PrimaryText, unfocusedTrailingIconColor = PrimaryText),
        )
        ExposedDropdownMenu(
            containerColor = Divider,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = Settings.fontInterface.sp) },
                    onClick = {
                        textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option)
                        expanded = false
                        k.edit {
                            putInt("biblia_seash", index)
                        }
                        onSearchStart()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    colors = MenuDefaults.itemColors(textColor = PrimaryText)
                )
            }
        }
    }
}

fun doInBackground(
    context: Context,
    searche: String,
    perevod: String,
    isBogaslujbovyiaSearch: Boolean
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
        val bibleline = openAssetsResources(context, bogaslugbovyiaListAll[i].resurs)
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
        seashpost.add(SearchBibleItem(nazva, 0, 0, bogaslugbovyiaListAll[i].resurs, span))
    }
    return seashpost
}

fun biblia(
    context: Context,
    poshuk: String,
    perevod: String,
    secondRun: Boolean = false
): ArrayList<SearchBibleItem> {
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var poshuk1 = poshuk
    val seashpost = ArrayList<SearchBibleItem>()
    val registr = k.getBoolean("pegistrbukv", true)
    if (secondRun) {
        val m = if (perevod == Settings.PEREVODSINOIDAL) charArrayOf(
            'у',
            'е',
            'а',
            'о',
            'э',
            'я',
            'и',
            'ю',
            'ь',
            'ы'
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
                    var aSviatyia =
                        HtmlCompat.fromHtml(bibleline[r], HtmlCompat.FROM_HTML_MODE_LEGACY)
                            .toString()
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
                    val isInt =
                        if (t6 != -1) {
                            val item = aSviatyia.substring(t5, t6)
                            item.isNotEmpty() && item.isDigitsOnly()
                        } else false
                    val padd = if (isInt) {
                        val color = if ((context as MainActivity).dzenNoch) PrimaryBlack
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
                            SpanStyle(background = BezPosta, color = PrimaryText),
                            t1 - t2,
                            t1
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
                                    strSub1Pos,
                                    strSub1Pos + 1
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
                    val startString =
                        if (strSub1Pos > 0) textSearch.substring(strSub1Pos - 1, strSub1Pos)
                        else " "
                    val endString =
                        if (strSub1Pos + stringBuilder.length + 1 <= textSearch.length) textSearch.substring(
                            strSub1Pos + stringBuilder.length,
                            strSub1Pos + stringBuilder.length + 1
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

data class FindString(val str: String, val position: Int)

data class SearchBibleItem(
    val subTitle: String,
    val glava: Int,
    val styx: Int,
    val resource: String,
    val text: AnnotatedString.Builder
)