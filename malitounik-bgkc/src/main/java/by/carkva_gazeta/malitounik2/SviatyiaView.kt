package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SviatyiaView(navController: NavHostController, svity: Boolean, year: Int, mun: Int, day: Int) {
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    val lazyListState = rememberLazyListState()
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val context = LocalContext.current
    val sviatyiaList = remember { mutableStateListOf<OpisanieData>() }
    val dirList = remember { mutableStateListOf<DirList>() }
    if (svity) {
        loadOpisanieSviat(context, sviatyiaList, mun, day)
    } else {
        loadOpisanieSviatyia(context, sviatyiaList, year, mun, day)
    }
    var dialoNoIntent by remember { mutableStateOf(false) }
    var dialoNoWIFI by remember { mutableStateOf(false) }
    var isProgressVisable by remember { mutableStateOf(false) }
    if (dialoNoIntent) {
        DialogNoInternet {
            dialoNoIntent = false
        }
    }
    if (dialoNoWIFI) {
        DialogNoWiFI(onDismissRequest = {
            dialoNoWIFI = false
        }) {
            coroutineScope.launch {
                if (svity) {
                    getOpisanieSviat(context, sviatyiaList, mun, day)
                } else {
                    getSviatyia(context, sviatyiaList, year, mun, day)
                }
                getIcons(context, dirList, sviatyiaList, svity)
                getPiarliny(context)
            }
            dialoNoWIFI = false
        }
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isProgressVisable = true
            val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
            val fileOpisanie13 = File("${context.filesDir}/sviatyja/opisanie13.json")
            if (!Settings.isNetworkAvailable(context)) {
                if (!svity && (fileOpisanie.exists() || fileOpisanie13.exists())) {
                    loadOpisanieSviatyia(context, sviatyiaList, year, mun, day)
                } else {
                    dialoNoIntent = true
                }
            } else {
                try {
                    if (Settings.isNetworkAvailable(context, Settings.TRANSPORT_WIFI)) {
                        if (svity) {
                            getOpisanieSviat(context, sviatyiaList, mun, day)
                        } else {
                            getSviatyia(context, sviatyiaList, year, mun, day)
                        }
                        getIcons(context, dirList, sviatyiaList, svity)
                        getPiarliny(context)
                    } else {
                        dialoNoWIFI = true
                    }
                } catch (_: Throwable) {
                }
            }
            isProgressVisable = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            modifier = Modifier.clickable {
                                maxLine.intValue = Int.MAX_VALUE
                                coroutineScope.launch {
                                    delay(5000L)
                                    maxLine.intValue = 1
                                }
                            },
                            text = stringResource(R.string.sabytie),
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = maxLine.intValue,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
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
                    /*IconButton({
                        addPadzeia = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            tint = PrimaryTextBlack,
                            contentDescription = ""
                        )
                    }
                    IconButton({
                        deliteAll = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            tint = PrimaryTextBlack,
                            contentDescription = ""
                        )
                    }*/
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
                .fillMaxSize(),
            state = lazyListState
        ) {
            items(sviatyiaList.size) { index ->
            }

            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
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

suspend fun getOpisanieSviat(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, mun: Int, day: Int, count: Int = 0) {
    val pathReference = MainActivity.referens.child("/opisanie_sviat.json")
    var update = 0L
    var error = false
    pathReference.metadata.addOnCompleteListener { storageMetadata ->
        if (storageMetadata.isSuccessful) {
            update = storageMetadata.result.updatedTimeMillis
        } else {
            error = true
        }
    }.await()
    if (update == 0L) error = true
    if (error && count < 3) {
        getOpisanieSviat(context, sviatyiaList, mun, day, count + 1)
        return
    }
    saveOpisanieSviat(context, sviatyiaList, mun, day, update)
}

suspend fun saveOpisanieSviat(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, mun: Int, day: Int, update: Long, count: Int = 0) {
    val pathReference = MainActivity.referens.child("/opisanie_sviat.json")
    val file = File("${context.filesDir}/opisanie_sviat.json")
    var error = false
    val time = file.lastModified()
    if (!file.exists() || time < update) {
        pathReference.getFile(file).addOnCompleteListener {
            if (!it.isSuccessful) {
                error = true
            }
        }.await()
    }
    var read = ""
    if (file.exists()) read = file.readText()
    if (read == "") error = true
    if (error && count < 3) {
        saveOpisanieSviat(context, sviatyiaList, mun, day, update, count + 1)
        return
    }
    loadOpisanieSviat(context, sviatyiaList, mun, day)
}

suspend fun getSviatyia(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, year: Int, mun: Int, day: Int, count: Int = 0) {
    val dir = File("${context.filesDir}/sviatyja/")
    if (!dir.exists()) dir.mkdir()
    var error = false
    val pathReference = MainActivity.referens.child("/chytanne/sviatyja/opisanie$mun.json")
    var update = 0L
    pathReference.metadata.addOnCompleteListener { storageMetadata ->
        if (storageMetadata.isSuccessful) {
            update = storageMetadata.result.updatedTimeMillis
        } else {
            error = true
        }
    }.await()
    if (update == 0L) error = true
    if (error && count < 3) {
        getSviatyia(context, sviatyiaList, year, mun, day, count + 1)
        return
    }
    val pathReference2 = MainActivity.referens.child("/chytanne/sviatyja/opisanie13.json")
    var update13 = 0L
    pathReference2.metadata.addOnCompleteListener { storageMetadata ->
        if (storageMetadata.isSuccessful) {
            update13 = storageMetadata.result.updatedTimeMillis
        } else {
            error = true
        }
    }.await()
    if (update13 == 0L) error = true
    if (error && count < 3) {
        getSviatyia(context, sviatyiaList, year, mun, day, count + 1)
        return
    }
    saveOpisanieSviatyia(context, sviatyiaList, update, update13, year, mun, day)
}

suspend fun saveOpisanieSviatyia(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, update: Long, update13: Long, year: Int, mun: Int, day: Int, count: Int = 0) {
    val pathReference = MainActivity.referens.child("/chytanne/sviatyja/opisanie$mun.json")
    val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    val time = fileOpisanie.lastModified()
    var error = false
    if (!fileOpisanie.exists() || time < update) {
        pathReference.getFile(fileOpisanie).addOnCompleteListener {
            if (!it.isSuccessful) {
                error = true
            }
        }.await()
    }
    var read = ""
    if (fileOpisanie.exists()) read = fileOpisanie.readText()
    if (read == "") error = true
    val pathReference13 = MainActivity.referens.child("/chytanne/sviatyja/opisanie13.json")
    val fileOpisanie13 = File("${context.filesDir}/sviatyja/opisanie13.json")
    val time13 = fileOpisanie13.lastModified()
    if (!fileOpisanie13.exists() || time13 < update) {
        pathReference13.getFile(fileOpisanie13).addOnCompleteListener {
            if (!it.isSuccessful) {
                error = true
            }
        }.await()
    }
    var read13 = ""
    if (fileOpisanie13.exists()) read13 = fileOpisanie13.readText()
    if (read13 == "") error = true
    if (error && count < 3) {
        saveOpisanieSviatyia(context, sviatyiaList, update, update13, year, mun, day, count + 1)
        return
    }
    loadOpisanieSviatyia(context, sviatyiaList, year, mun, day)
}

fun loadOpisanieSviatyia(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, year: Int, mun: Int, day: Int) {
    val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    if (fileOpisanie.exists()) {
        val builder = fileOpisanie.readText()
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        var res = ""
        val arrayList = ArrayList<String>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            res = arrayList[day - 1]
        }
        if ((context as MainActivity).dzenNoch) res = res.replace("#d00505", "#ff6666")
        val title = ArrayList<String>()
        val listRes = res.split("<strong>")
        var sb = ""
        for (i in listRes.size - 1 downTo 0) {
            val text = listRes[i].replace("<!--image-->", "")
            if (text.trim() != "") {
                if (text.contains("Трапар") || text.contains("Кандак")) {
                    sb = "<strong>$text$sb"
                    continue
                } else {
                    sb = "<strong>$text$sb"
                    title.add(0, sb)
                    sb = ""
                }
            }
        }
        title.forEachIndexed { index, text ->
            val t1 = text.indexOf("</strong>")
            var textTitle = ""
            var fulText = ""
            if (t1 != -1) {
                textTitle = text.substring(0, t1 + 9)
                fulText = text.substring(t1 + 9)
            }
            val spannedtitle = AnnotatedString.fromHtml(textTitle)
            val spanned = AnnotatedString.fromHtml(fulText)
            sviatyiaList.add(OpisanieData(index + 1, day, mun, spannedtitle, spanned, "", ""))
        }
    }
    val fileOpisanie13 = File("${context.filesDir}/sviatyja/opisanie13.json")
    if (fileOpisanie13.exists()) {
        val builder = fileOpisanie13.readText()
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        val arrayList = ArrayList<ArrayList<String>>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            val pasha = GregorianCalendar(year, Calendar.DECEMBER, 25)
            val pastvoW = pasha[Calendar.DAY_OF_WEEK]
            for (i in 26..31) {
                val pastvo = GregorianCalendar(year, Calendar.DECEMBER, i)
                val iazepW = pastvo[Calendar.DAY_OF_WEEK]
                for (e in 0 until arrayList.size) {
                    if (pastvoW != Calendar.SUNDAY) {
                        if (arrayList[e][1].toInt() == 0 && mun - 1 == Calendar.DECEMBER && day == i && Calendar.SUNDAY == iazepW) {
                            val t1 = arrayList[e][2].indexOf("</strong>")
                            var textTitle = ""
                            var fulText = ""
                            if (t1 != -1) {
                                textTitle = arrayList[e][2].substring(0, t1 + 9)
                                fulText = arrayList[e][2].substring(t1 + 9)
                            }
                            val spannedtitle = AnnotatedString.fromHtml(textTitle)
                            val spanned = AnnotatedString.fromHtml(fulText)
                            sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                        }
                    } else {
                        if (arrayList[e][1].toInt() == 0 && mun - 1 == Calendar.DECEMBER && day == i && Calendar.MONDAY == iazepW) {
                            val t1 = arrayList[e][2].indexOf("</strong>")
                            var textTitle = ""
                            var fulText = ""
                            if (t1 != -1) {
                                textTitle = arrayList[e][2].substring(0, t1 + 9)
                                fulText = arrayList[e][2].substring(t1 + 9)
                            }
                            val spannedtitle = AnnotatedString.fromHtml(textTitle)
                            val spanned = AnnotatedString.fromHtml(fulText)
                            sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                        }
                    }
                }
            }
            val gc = GregorianCalendar()
            val dayF = if (gc.isLeapYear(year)) 29
            else 28
            for (e in 0 until arrayList.size) {
                if (arrayList[e][1].toInt() == 1 && mun - 1 == Calendar.FEBRUARY && day == dayF) {
                    val t1 = arrayList[e][2].indexOf("</strong>")
                    var textTitle = ""
                    var fulText = ""
                    if (t1 != -1) {
                        textTitle = arrayList[e][2].substring(0, t1 + 9)
                        fulText = arrayList[e][2].substring(t1 + 9)
                    }
                    val spannedtitle = AnnotatedString.fromHtml(textTitle)
                    val spanned = AnnotatedString.fromHtml(fulText)
                    sviatyiaList.add(OpisanieData(sviatyiaList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                }
            }
        }
    }
    loadIconsOnImageView(context, sviatyiaList, false)
}

fun loadOpisanieSviat(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, mun: Int, day: Int) {
    val fileOpisanieSviat = File("${context.filesDir}/opisanie_sviat.json")
    if (fileOpisanieSviat.exists()) {
        val builder = fileOpisanieSviat.readText()
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
        val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
        arrayList?.forEach { strings ->
            if (day == strings[0].toInt() && mun == strings[1].toInt()) {
                var res = strings[2]
                if ((context as MainActivity).dzenNoch) res = res.replace("#d00505", "#ff6666")
                val t1 = res.indexOf("</strong>")
                var textTitle = ""
                var fulText = ""
                if (t1 != -1) {
                    textTitle = res.substring(0, t1 + 9)
                    fulText = res.substring(t1 + 9)
                }
                val spannedtitle = AnnotatedString.fromHtml(textTitle)
                val spanned = AnnotatedString.fromHtml(fulText)
                sviatyiaList.add(OpisanieData(1, day, mun, spannedtitle, spanned, "", ""))
                loadIconsOnImageView(context, sviatyiaList, true)
            }
        }
    }
}

suspend fun getIcons(context: Context, dirList: SnapshotStateList<DirList>, sviatyiaList: SnapshotStateList<OpisanieData>, svity: Boolean, count: Int = 0) {
    val dir = File("${context.filesDir}/icons/")
    if (!dir.exists()) dir.mkdir()
    val dir2 = File("${context.filesDir}/iconsApisanne")
    if (!dir2.exists()) dir2.mkdir()
    if (count < 3) {
        getIcons(context, dirList, sviatyiaList, svity, count + 1)
        return
    }
    dirList.clear()
    var size = 0L
    val sb = StringBuilder()
    val fileIconMataData = File("${context.filesDir}/iconsMataData.txt")
    val pathReferenceMataData = MainActivity.referens.child("/chytanne/iconsMataData.txt")
    pathReferenceMataData.getFile(fileIconMataData).await()
    val list = fileIconMataData.readText().split("\n")
    for (i in 0 until sviatyiaList.size) {
        list.forEach {
            val t1 = it.indexOf("<-->")
            if (t1 != -1) {
                val t2 = it.indexOf("<-->", t1 + 4)
                val name = it.substring(0, t1)
                val pref = if (svity) "v"
                else "s"
                sb.append(name)
                if (name.contains("${pref}_${sviatyiaList[i].date}_${sviatyiaList[i].mun}")) {
                    val t3 = name.lastIndexOf(".")
                    val fileNameT = name.substring(0, t3) + ".txt"
                    val file = File("${context.filesDir}/iconsApisanne/$fileNameT")
                    try {
                        MainActivity.referens.child("/chytanne/iconsApisanne/$fileNameT").getFile(file).addOnFailureListener {
                            if (file.exists()) file.delete()
                        }.await()
                    } catch (_: Throwable) {
                    }
                    val fileIcon = File("${context.filesDir}/icons/${name}")
                    val time = fileIcon.lastModified()
                    val update = it.substring(t2 + 4).toLong()
                    if (!fileIcon.exists() || time < update) {
                        val updateFile = it.substring(t1 + 4, t2).toLong()
                        dirList.add(DirList(name, updateFile))
                        size += updateFile
                    }
                }
            }
        }
    }
    val fileList = File("${context.filesDir}/icons").list()
    fileList?.forEach {
        if (!sb.toString().contains(it)) {
            val file = File("${context.filesDir}/icons/$it")
            if (file.exists()) file.delete()
            val t3 = file.name.lastIndexOf(".")
            val fileNameT = file.name.substring(0, t3) + ".txt"
            val fileOpis = File("${context.filesDir}/iconsApisanne/$fileNameT")
            if (fileOpis.exists()) fileOpis.delete()
        }

    }
    //if (!loadIcons && Settings.isNetworkAvailable(context, Settings.TRANSPORT_CELLULAR)) {
    /*if (dirList.isNotEmpty()) {
        val dialog = DialogOpisanieWIFI.getInstance(size.toFloat())
        dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
    } else {
        binding.progressBar2.visibility = View.INVISIBLE
    }*/
    //} else {
    /*binding.progressBar2.isIndeterminate = false
    binding.progressBar2.progress = 0
    binding.progressBar2.max = size.toInt()*/
    //var progress = 0
    for (i in 0 until dirList.size) {
        try {
            val fileIcon = File("${context.filesDir}/icons/" + dirList[i].name)
            val pathReference = MainActivity.referens.child("/chytanne/icons/" + dirList[i].name)
            pathReference.getFile(fileIcon).await()
            //progress += dirList[i].sizeBytes.toInt()
            //binding.progressBar2.progress = progress
        } catch (_: Throwable) {
        }
    }
    loadIconsOnImageView(context, sviatyiaList, svity)
    //}
}

fun loadIconsOnImageView(context: Context, sviatyiaList: SnapshotStateList<OpisanieData>, svity: Boolean) {
    val pref = if (svity) "v"
    else "s"
    val fileList = File("${context.filesDir}/icons").list()
    for (i in 0 until sviatyiaList.size) {
        val indexImg = if (sviatyiaList[i].date == -1) 1
        else sviatyiaList[i].index
        fileList?.forEach {
            if (it.contains("${pref}_${sviatyiaList[i].date}_${sviatyiaList[i].mun}_${indexImg}")) {
                val t1 = it.lastIndexOf(".")
                val fileNameT = it.substring(0, t1) + ".txt"
                val file = File("${context.filesDir}/iconsApisanne/$fileNameT")
                if (file.exists()) {
                    sviatyiaList[i].textApisanne = file.readText()
                } else {
                    sviatyiaList[i].textApisanne = ""
                }
                val file2 = File("${context.filesDir}/icons/$it")
                if (file2.exists()) {
                    sviatyiaList[i].image = file2.absolutePath
                    return@forEach
                }
            }
        }
    }
    //binding.progressBar2.visibility = View.INVISIBLE
}

suspend fun getPiarliny(context: Context) {
    val pathReference = MainActivity.referens.child("/chytanne/piarliny.json")
    val localFile = File("${context.filesDir}/piarliny.json")
    pathReference.getFile(localFile).await()
}

fun checkParliny(context: Context, mun: Int, day: Int): Boolean {
    val piarliny = ArrayList<ArrayList<String>>()
    val fileOpisanieSviat = File("${context.filesDir}/piarliny.json")
    if (fileOpisanieSviat.exists()) {
        try {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            piarliny.addAll(gson.fromJson(builder, type))
        } catch (t: Throwable) {
            fileOpisanieSviat.delete()
        }
        val cal = GregorianCalendar()
        piarliny.forEach {
            cal.timeInMillis = it[0].toLong() * 1000
            if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                return true
            }
        }
    }
    return false
}

data class DirList(val name: String?, val sizeBytes: Long)

data class OpisanieData(val index: Int, val date: Int, val mun: Int, val title: AnnotatedString, val text: AnnotatedString, var image: String, var textApisanne: String)