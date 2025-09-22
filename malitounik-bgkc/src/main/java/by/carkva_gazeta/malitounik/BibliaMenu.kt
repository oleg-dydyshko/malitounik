package by.carkva_gazeta.malitounik

import android.content.Context
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.TitleCalendarMounth
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import by.carkva_gazeta.malitounik.views.HtmlText
import by.carkva_gazeta.malitounik.views.openAssetsResources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliaMenu(
    navController: NavHostController,
    perevod: String,
    innerPadding: PaddingValues,
    searchText: Boolean,
    searchBibleState: LazyListState,
    sorted: Int,
    isIconSortVisibility: (Boolean) -> Unit,
    navigateToCytanniList: (String, Int, String, Int) -> Unit,
    navigateToBogaslujbovyia: (String, String) -> Unit
) {
    val context = LocalContext.current
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    var bibleTime by remember { mutableStateOf(false) }
    if (bibleTime) {
        val prevodName = when (perevod) {
            Settings.PEREVODSEMUXI -> {
                "biblia"
            }

            Settings.PEREVODBOKUNA -> {
                "bokuna"
            }

            Settings.PEREVODNADSAN -> {
                "nadsan"
            }

            Settings.PEREVODCARNIAUSKI -> {
                "carniauski"
            }

            Settings.PEREVODCATOLIK -> {
                "catolik"
            }

            Settings.PEREVODSINOIDAL -> {
                "sinaidal"
            }

            else -> "biblia"
        }
        val knigaText = k.getString("bible_time_${prevodName}_kniga", "Быц") ?: "Быц"
        val kniga = knigaBiblii(knigaText)
        Settings.bibleTime = true
        bibleTime = false
        navigationActions.navigateToBibliaList(kniga >= 50, perevod)
    }
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
    var dialogImageView by rememberSaveable { mutableStateOf(false) }
    var searchSettings by remember { mutableStateOf(false) }
    var isRegistr by remember { mutableStateOf(k.getBoolean("pegistrbukv", true)) }
    var isDakladnaeSupadzenne by remember { mutableIntStateOf(k.getInt("slovocalkam", 0)) }
    var isProgressVisable by remember { mutableStateOf(false) }
    var isDeliteVybranaeAll by remember { mutableStateOf(false) }
    if (isDeliteVybranaeAll) {
        val titlePerevod = when (perevod) {
            Settings.PEREVODSEMUXI -> stringResource(R.string.title_biblia2)
            Settings.PEREVODSINOIDAL -> stringResource(R.string.bsinaidal2)
            Settings.PEREVODNADSAN -> stringResource(R.string.title_psalter)
            Settings.PEREVODBOKUNA -> stringResource(R.string.title_biblia_bokun2)
            Settings.PEREVODCARNIAUSKI -> stringResource(R.string.title_biblia_charniauski2)
            Settings.PEREVODCATOLIK -> stringResource(R.string.title_biblia_catolik2)
            else -> stringResource(R.string.title_biblia2)
        }
        DialogDelite(
            title = stringResource(R.string.vybranoe_biblia_delite, titlePerevod),
            onDismiss = {
                isDeliteVybranaeAll = false
            },
            onConfirmation = {
                val prevodName = when (perevod) {
                    Settings.PEREVODSEMUXI -> "biblia"
                    Settings.PEREVODBOKUNA -> "bokuna"
                    Settings.PEREVODCARNIAUSKI -> "carniauski"
                    Settings.PEREVODNADSAN -> "nadsan"
                    Settings.PEREVODCATOLIK -> "catolik"
                    Settings.PEREVODSINOIDAL -> "sinaidal"
                    else -> "biblia"
                }
                val file = File("${context.filesDir}/vybranoe_${prevodName}.json")
                if (file.exists()) {
                    file.delete()
                }
                isDeliteVybranaeAll = false
            }
        )
    }
    LaunchedEffect(searchSettings, Settings.textFieldValueState.value) {
        if (searchSettings) {
            searchList.clear()
            searchSettings = false
        }
        if (Settings.textFieldValueState.value.trim().length >= 3) {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                isProgressVisable = true
                Settings.textFieldValueLatest.value = Settings.textFieldValueState.value.trim()
                searchList.clear()
                val list = withContext(Dispatchers.IO) {
                    return@withContext doInBackground(context, Settings.textFieldValueState.value.trim(), perevod, false)
                }
                searchList.addAll(list)
                isProgressVisable = false
            }
        } else {
            searchJob?.cancel()
            isProgressVisable = false
        }
    }
    if (searchText) {
        if (AppNavGraphState.searchSettings) {
            ModalBottomSheet(
                scrimColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                properties = ModalBottomSheetProperties(isAppearanceLightStatusBars = false, isAppearanceLightNavigationBars = false),
                onDismissRequest = {
                    AppNavGraphState.searchSettings = false
                }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (perevod != Settings.PEREVODNADSAN) {
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
                                navigateToCytanniList(
                                    searchList[index].title + " " + searchList[index].glava.toString(),
                                    searchList[index].styx - 1,
                                    perevod,
                                    Settings.CHYTANNI_BIBLIA
                                )
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
        if (isProgressVisable) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (perevod != Settings.PEREVODCATOLIK) {
                TextButton(
                    onClick = {
                        navigationActions.navigateToBibliaList(false, perevod)
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
                        navigationActions.navigateToBibliaList(true, perevod)
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
                    bibleTime = true
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
            val prevodName = when (perevod) {
                Settings.PEREVODSEMUXI -> "biblia"
                Settings.PEREVODBOKUNA -> "bokuna"
                Settings.PEREVODCARNIAUSKI -> "carniauski"
                Settings.PEREVODNADSAN -> "nadsan"
                Settings.PEREVODCATOLIK -> "catolik"
                Settings.PEREVODSINOIDAL -> "sinaidal"
                else -> "biblia"
            }
            val titlePerevod = stringResource(R.string.str_short_label1)
            val file = File("${LocalContext.current.filesDir}/vybranoe_${prevodName}.json")
            if (file.exists()) {
                val gson = Gson()
                val type =
                    TypeToken.getParameterized(
                        ArrayList::class.java,
                        VybranaeData::class.java
                    ).type
                val list: ArrayList<VybranaeData> = gson.fromJson(file.readText(), type)
                var removeItem by remember { mutableIntStateOf(-1) }
                if (removeItem != -1) {
                    val titleVybrenae = stringResource(
                        R.string.vybranoe_biblia_delite, list[removeItem].title + " " + (list[removeItem].glava + 1)
                    )
                    DialogDelite(
                        title = titleVybrenae,
                        onDismiss = {
                            removeItem = -1
                        },
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
                    )
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
                                    AppNavGraphState.setItemsValue(titlePerevod)
                                    collapsedState = !collapsedState
                                },
                                onLongClick = {
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
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp),
                            color = PrimaryText,
                            fontSize = Settings.fontInterface.sp,
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            painter = if (!collapsedState)
                                painterResource(R.drawable.keyboard_arrow_down)
                            else
                                painterResource(R.drawable.keyboard_arrow_up),
                            contentDescription = "",
                            tint = PrimaryText,
                        )
                    }
                }
                if (sorted == Settings.SORT_BY_ABC) {
                    list.sortWith(
                        compareBy({
                            it.knigaText
                        }, {
                            it.glava
                        })
                    )
                } else {
                    list.sortByDescending { it.id }
                }
                isIconSortVisibility(!collapsedState)
                AnimatedVisibility(
                    !collapsedState, enter = fadeIn(
                        tween(
                            durationMillis = 700, easing = LinearOutSlowInEasing
                        )
                    ), exit = fadeOut(tween(durationMillis = 700, easing = LinearOutSlowInEasing))
                ) {
                    Column {
                        for (index in list.indices) {
                            Row(
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = {
                                            val newList = StringBuilder()
                                            for (r in 0 until list.size) {
                                                val char = if (r == list.size - 1) ""
                                                else ";"
                                                newList.append(list[r].knigaText + " " + (list[r].glava + 1) + char)
                                            }
                                            navigateToCytanniList(
                                                newList.toString(),
                                                index,
                                                list[index].perevod,
                                                Settings.CHYTANNI_VYBRANAE
                                            )
                                        },
                                        onLongClick = {
                                            removeItem = index
                                        }
                                    )
                                    .padding(start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(5.dp, 5.dp),
                                    painter = painterResource(R.drawable.poiter),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                                Text(
                                    list[index].title + " " + (list[index].glava + 1),
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
                TextButton(
                    onClick = {
                        navigateToBogaslujbovyia(context.getString(R.string.malitva_pered), "nadsan_pered.html")
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
                TextButton(
                    onClick = {
                        navigateToBogaslujbovyia(context.getString(R.string.malitva_posle), "nadsan_posle.html")
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
                TextButton(
                    onClick = {
                        pesnyView = !pesnyView
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
                    Text(stringResource(R.string.pesni), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
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
                            Row(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clickable {
                                        navigationActions.navigateToBogaslujbovyia(
                                            context.getString(R.string.pesnia, i),
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
                                    modifier = Modifier.size(5.dp, 5.dp),
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
                TextButton(
                    onClick = {
                        dialogImageView = !dialogImageView
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
                    Text(stringResource(R.string.title_psalter_privila), fontSize = Settings.fontInterface.sp, color = PrimaryText, textAlign = TextAlign.Center)
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
                    painter = painterResource(R.drawable.pravily_chytannia_psaltyria), contentDescription = "", modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(), contentScale = ContentScale.FillWidth
                )
            }
            if (perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA || perevod == Settings.PEREVODCATOLIK) {
                TextButton(
                    onClick = {
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

@OptIn(ExperimentalLayoutApi::class)
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
                    painter = painter, contentDescription = "", Modifier
                        .fillMaxWidth(), contentScale = ContentScale.FillWidth
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
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