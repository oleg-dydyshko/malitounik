package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.HtmlText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSviatyia(navController: NavHostController) {
    val lazyRowState = rememberLazyListState()
    val res = remember { mutableStateListOf<Prazdniki>() }
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldLoaded by remember { mutableStateOf(false) }
    var searshString by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(searshString) {
        if (searshString.trim().length >= 3 && res.isEmpty()) {
            if (searchJob?.isActive == true) {
                searchJob?.cancel()
            }
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                res.clear()
                val list = withContext(Dispatchers.IO) {
                    return@withContext rawAsset(context, searshString.trim())
                }
                res.addAll(list)
            }
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
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
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
                            res.clear()
                            var edit = newText
                            edit = edit.replace("и", "і")
                            edit = edit.replace("щ", "ў")
                            edit = edit.replace("И", "І")
                            edit = edit.replace("Щ", "Ў")
                            edit = edit.replace("ъ", "'")
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
                                IconButton(onClick = {
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
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.searh_sviatyia_result, res.size),
                fontStyle = FontStyle.Italic,
                fontSize = Settings.fontInterface.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            LazyColumn(
                Modifier.nestedScroll(nestedScrollConnection),
                state = lazyRowState
            ) {
                items(res.size) { index ->
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                val calendar = Calendar.getInstance()
                                calendar[Calendar.DAY_OF_YEAR] = res[index].dayOfYear
                                for (e in Settings.data.indices) {
                                    if (calendar[Calendar.DATE] == Settings.data[e][1].toInt() && calendar[Calendar.MONTH] == Settings.data[e][2].toInt() && calendar[Calendar.YEAR] == Settings.data[e][3].toInt()) {
                                        Settings.caliandarPosition = e
                                        break
                                    }
                                }
                                if (k.getBoolean(
                                        "caliandarList",
                                        false
                                    )
                                ) navigationActions.navigateToKaliandarYear()
                                else navigationActions.navigateToKaliandar()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = res[index].opisanieData,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = Settings.fontInterface.sp
                            )
                            when (res[index].typeSviat) {
                                0 -> {
                                    HtmlText(
                                        modifier = Modifier
                                            .padding(10.dp),
                                        text = res[index].opisanie,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                }

                                1 -> {
                                    Text(
                                        modifier = Modifier
                                            .padding(10.dp),
                                        text = res[index].opisanie,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                }

                                2 -> {
                                    Text(
                                        modifier = Modifier
                                            .padding(10.dp),
                                        text = res[index].opisanie,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                }

                                3 -> {
                                    Text(
                                        modifier = Modifier
                                            .padding(10.dp),
                                        text = res[index].opisanie,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                }

                                else -> {
                                    val t1 = res[index].opisanie.indexOf(":")
                                    val annotatedString = if (t1 != -1) {
                                        buildAnnotatedString {
                                            append(res[index].opisanie)
                                            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t1 + 1)
                                        }
                                    } else {
                                        AnnotatedString(res[index].opisanie)
                                    }
                                    Text(
                                        modifier = Modifier
                                            .padding(10.dp),
                                        text = annotatedString,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = Settings.fontInterface.sp
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider()
                }
                item {
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

private fun rawAsset(context: Context, poshukString: String, secondRun: Boolean = false): ArrayList<Prazdniki> {
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