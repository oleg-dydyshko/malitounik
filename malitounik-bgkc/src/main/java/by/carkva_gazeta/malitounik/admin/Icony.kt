package by.carkva_gazeta.malitounik.admin

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.scale
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.DialogDelite
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Icony(navController: NavHostController) {
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
    val context = LocalContext.current
    val iconList = remember { mutableStateListOf<DataImages>() }
    val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
    val coroutineScope = rememberCoroutineScope()
    var isProgressVisable by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }
    var isDialodApisanne by rememberSaveable { mutableStateOf(false) }
    var isDialodDeliteIcon by rememberSaveable { mutableStateOf(false) }
    var isDialodDeliteApisanne by rememberSaveable { mutableStateOf(false) }
    val mActivityResultFile = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val imageUri = it.data?.data
            imageUri?.let { image ->
                val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(context.contentResolver, image)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                }
                fileUpload(context, position, bitmap, isLoad = { isload ->
                    isProgressVisable = isload
                }) { file ->
                    iconList[position] = DataImages(iconList[position].title, file.length(), file, iconList[position].position, iconList[position].iconApisanne)
                    isDialodApisanne = true
                }
            }
        }
    }
    if (isDialodDeliteIcon) {
        DialogDelite(title = stringResource(R.string.del_icon_apis), onConfirmation = {
            if (Settings.isNetworkAvailable(context)) {
                CoroutineScope(Dispatchers.Main).launch {
                    isProgressVisable = true
                    try {
                        val day = Settings.data[Settings.caliandarPosition][1].toInt()
                        val mun = Settings.data[Settings.caliandarPosition][2].toInt() + 1
                        val fileName = "s_${day}_${mun}_${position + 1}.jpg"
                        val t1 = fileName.lastIndexOf(".")
                        val fileNameT = fileName.take(t1) + ".txt"
                        val file = iconList[position].file
                        Malitounik.referens.child("/chytanne/icons/" + file.name).delete().await()
                        Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").delete().await()
                        val imageFile = File("${context.filesDir}/icons/" + file.name)
                        if (imageFile.exists()) imageFile.delete()
                        val localFile = File("${context.filesDir}/iconsApisanne/$fileNameT")
                        if (localFile.exists()) localFile.delete()
                        iconList[position] = DataImages(iconList[position].title, 0, File(""), iconList[position].position, "")
                    } catch (_: Throwable) {
                    }
                }
                isProgressVisable = false
                isDialodDeliteIcon = false
            }
        }) {
            isDialodDeliteIcon = false
        }
    }
    if (isDialodDeliteApisanne) {
        DialogDelite(title = stringResource(R.string.del_apis), onConfirmation = {
            if (Settings.isNetworkAvailable(context)) {
                CoroutineScope(Dispatchers.Main).launch {
                    isProgressVisable = true
                    try {
                        val day = Settings.data[Settings.caliandarPosition][1].toInt()
                        val mun = Settings.data[Settings.caliandarPosition][2].toInt() + 1
                        val fileName = "s_${day}_${mun}_${position + 1}.jpg"
                        val t1 = fileName.lastIndexOf(".")
                        val fileNameT = fileName.take(t1) + ".txt"
                        Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").delete().await()
                        val localFile = File("${context.filesDir}/iconsApisanne/$fileNameT")
                        if (localFile.exists()) localFile.delete()
                        iconList[position] = DataImages(iconList[position].title, iconList[position].size, iconList[position].file, iconList[position].position, "")
                    } catch (_: Throwable) {
                    }
                }
                isProgressVisable = false
                isDialodDeliteApisanne = false
            }
        }) {
            isDialodDeliteApisanne = false
        }
    }
    if (isDialodApisanne) {
        DialogApisanneIcony(iconList[position].iconApisanne, saveApisanne = { iconApisanne ->
            if (Settings.isNetworkAvailable(context)) {
                CoroutineScope(Dispatchers.Main).launch {
                    isProgressVisable = true
                    try {
                        val dir = File("${context.filesDir}/iconsApisanne")
                        if (!dir.exists()) dir.mkdir()
                        val day = Settings.data[Settings.caliandarPosition][1].toInt()
                        val mun = Settings.data[Settings.caliandarPosition][2].toInt() + 1
                        val fileName = "s_${day}_${mun}_${position + 1}.jpg"
                        val t1 = fileName.lastIndexOf(".")
                        val fileNameT = fileName.take(t1) + ".txt"
                        val localFile = File("${context.filesDir}/iconsApisanne/$fileNameT")
                        if (iconApisanne != "") {
                            localFile.writer().use {
                                it.write(iconApisanne)
                            }
                            Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").putFile(Uri.fromFile(localFile)).await()
                            iconList[position] = DataImages(iconList[position].title, iconList[position].size, iconList[position].file, iconList[position].position, iconApisanne)
                        } else {
                            if (localFile.exists()) localFile.delete()
                            Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").delete().await()
                            iconList[position] = DataImages(iconList[position].title, iconList[position].size, iconList[position].file, iconList[position].position, "")
                        }
                    } catch (_: Throwable) {
                    }
                    isProgressVisable = false
                    isDialodApisanne = false
                }
            } else {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                isDialodApisanne = false
            }
        }) {
            isDialodApisanne = false
        }
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isProgressVisable = true
            getIcons(context) {
                iconList.addAll(it)
            }
            isProgressVisable = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_img_sviat),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = Settings.fontInterface.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    0.dp
                )
        ) {
            if (isProgressVisable) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(count = iconList.size, key = { it }) { newPosition ->
                    val imgFile = iconList[newPosition].file
                    val myBitmap = resizeImage(BitmapFactory.decodeFile(imgFile.absolutePath))
                    Column(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .border(
                                width = 1.dp,
                                color = SecondaryText,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (myBitmap != null) {
                            Image(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .combinedClickable(
                                        onClick = {
                                            position = newPosition
                                            val intent = Intent()
                                            intent.type = "*/*"
                                            intent.action = Intent.ACTION_GET_CONTENT
                                            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                                            mActivityResultFile.launch(Intent.createChooser(intent, context.getString(R.string.vybrac_file)))
                                        },
                                        onLongClick = {
                                            if (iconList[position].size > 0) {
                                                position = newPosition
                                                isDialodDeliteIcon = true
                                            }
                                        }
                                    )
                                , bitmap = myBitmap.asImageBitmap(), contentDescription = ""
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                text = iconList[newPosition].title, fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center
                            )
                            Text(
                                text = iconList[newPosition].iconApisanne.ifEmpty { stringResource(R.string.niama_apisannia) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .combinedClickable(
                                        onClick = {
                                            position = newPosition
                                            isDialodApisanne = true
                                        },
                                        onLongClick = {
                                            if (iconList[newPosition].iconApisanne.isNotEmpty()) {
                                                position = newPosition
                                                isDialodDeliteApisanne = true
                                            }
                                        }
                                    ),
                                fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center, fontStyle = FontStyle.Italic
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .clickable {
                                        position = newPosition
                                        val intent = Intent()
                                        intent.type = "*/*"
                                        intent.action = Intent.ACTION_GET_CONTENT
                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                                        mActivityResultFile.launch(Intent.createChooser(intent, context.getString(R.string.vybrac_file)))
                                    },
                                text = stringResource(R.string.niama_icony), fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = SecondaryText, textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .padding(bottom = innerPadding.calculateBottomPadding())
                            .imePadding()
                    )
                }
            }
        }
    }
}

@Composable
fun DialogApisanneIcony(apisanneIcony: String, saveApisanne: (String) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val focusRequester = remember { FocusRequester() }
        var textFieldLoaded by remember { mutableStateOf(false) }
        var textFieldValueStateTitle by rememberSaveable { mutableStateOf(apisanneIcony) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.admin_opisanne_icon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                TextField(
                    textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                    placeholder = { Text(stringResource(R.string.admin_opisanne_icon), fontSize = Settings.fontInterface.sp) },
                    value = textFieldValueStateTitle,
                    onValueChange = {
                        textFieldValueStateTitle = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!textFieldLoaded) {
                                focusRequester.requestFocus()
                                textFieldLoaded = true
                            }
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                    })
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            saveApisanne(textFieldValueStateTitle)
                        }, shape = MaterialTheme.shapes.small
                    ) {
                        Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.save), contentDescription = "")
                        Text(stringResource(R.string.save_sabytie), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

fun resizeImage(bitmap: Bitmap?): Bitmap? {
    bitmap?.let {
        var newHeight = it.height.toFloat()
        var newWidth = it.width.toFloat()
        val widthLinear = 500f
        val resoluton = newWidth / newHeight
        newWidth = 500f * resoluton
        newHeight = 500f
        if (newWidth > widthLinear) {
            newWidth = widthLinear
            newHeight = newWidth / resoluton
        }
        return it.scale(newWidth.toInt(), newHeight.toInt(), false)
    }
    return null
}

suspend fun getIcons(context: Context, resultList: (ArrayList<DataImages>) -> Unit) {
    if (Settings.isNetworkAvailable(context)) {
        val dir = File("${context.filesDir}/icons/")
        if (!dir.exists()) dir.mkdir()
        val dir2 = File("${context.filesDir}/iconsApisanne")
        if (!dir2.exists()) dir2.mkdir()
        val images = ArrayList<DataImages>()
        val itPos = StringBuilder()
        val list = Malitounik.referens.child("/chytanne/icons").list(1000).await()
        val day = Settings.data[Settings.caliandarPosition][1].toInt()
        val mun = Settings.data[Settings.caliandarPosition][2].toInt() + 1
        list.items.forEach {
            if (it.name.contains("s_${day}_${mun}_")) {
                val fileIcon = File("${context.filesDir}/icons/" + it.name)
                it.getFile(fileIcon).addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                }.await()
                var iconApisanne = ""
                try {
                    val t1 = it.name.lastIndexOf(".")
                    val fileNameT = it.name.substring(0, t1) + ".txt"
                    val file = File("${context.filesDir}/iconsApisanne/$fileNameT")
                    Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").getFile(file).await()
                    iconApisanne = file.readText()
                } catch (_: Throwable) {
                }
                var e = 1
                val s1 = "s_${day}_${mun}".length
                val s3 = it.name.substring(s1 + 1, s1 + 2)
                if (s3.isDigitsOnly()) e = s3.toInt()
                images.add(DataImages(getSviatyia(context, e - 1), fileIcon.length(), fileIcon, e.toLong(), iconApisanne))
                itPos.append(e)
            }
        }
        for (i in 1..4) {
            if (!itPos.contains(i.toString())) images.add(DataImages(getSviatyia(context, i - 1), 0, File(""), i.toLong(), ""))
        }
        images.sortBy { it.position }
        resultList(images)
    } else {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}

suspend fun getSviatyia(context: Context, position: Int): String {
    var title = ""
    val day = Settings.data[Settings.caliandarPosition][1].toInt()
    val mun = Settings.data[Settings.caliandarPosition][2].toInt() + 1
    val dir = File("${context.filesDir}/sviatyja/")
    if (!dir.exists()) dir.mkdir()
    val fileOpisanie = File("${context.filesDir}/sviatyja/opisanie$mun.json")
    val storageReference = Malitounik.referens.child("/chytanne/sviatyja/opisanie$mun.json")
    var update = 0L
    storageReference.metadata.addOnSuccessListener { storageMetadata ->
        update = storageMetadata.updatedTimeMillis
    }.await()
    val time = fileOpisanie.lastModified()
    if (!fileOpisanie.exists() || time < update) {
        storageReference.getFile(fileOpisanie).addOnFailureListener {
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
        }.await()
    }
    val gson = Gson()
    val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
    var res = ""
    val arrayList = ArrayList<String>()
    if (fileOpisanie.exists() && fileOpisanie.readText().isNotEmpty()) {
        arrayList.addAll(gson.fromJson(fileOpisanie.readText(), type))
        res = arrayList[day - 1]
    }
    val titleArray = ArrayList<String>()
    val listRes = res.split("<strong>")
    var sb: String
    for (i in listRes.size - 1 downTo 0) {
        val text = listRes[i].replace("<!--image-->", "")
        if (text.trim() != "") {
            if (text.contains("Трапар", ignoreCase = true) || text.contains("Кандак", ignoreCase = true)) {
                continue
            } else {
                val t1 = text.indexOf("</strong>")
                if (t1 != -1) {
                    sb = text.take(t1)
                    titleArray.add(0, sb)
                }
            }
        }
    }
    titleArray.forEachIndexed { index, text ->
        if (position == index) {
            title = text
            return@forEachIndexed
        }
    }
    return title
}

fun fileUpload(context: Context, position: Int, bitmap: Bitmap?, isLoad: (Boolean) -> Unit, result: (File) -> Unit) {
    if (Settings.isNetworkAvailable(context)) {
        CoroutineScope(Dispatchers.Main).launch {
            isLoad(true)
            val day = Settings.data[Settings.caliandarPosition][1].toInt()
            val mun = Settings.data[Settings.caliandarPosition][2].toInt() + 1
            val localFile = File("${context.filesDir}/icons/s_${day}_${mun}_${position + 1}.jpg")
            withContext(Dispatchers.IO) {
                bitmap?.let {
                    val out = FileOutputStream(localFile)
                    it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.flush()
                    out.close()
                }
            }
            bitmap?.let {
                Malitounik.referens.child("/chytanne/icons/s_${day}_${mun}_${position + 1}.jpg").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                    result(localFile)
                }.await()
            }
            withContext(Dispatchers.IO) {
                loadFilesMetaData(context)
            }
            isLoad(false)
        }
    } else {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}

suspend fun loadFilesMetaData(context: Context) {
    val sb = StringBuilder()
    val list = Malitounik.referens.child("/chytanne/icons").list(1000).await()
    list.items.forEach {
        val meta = it.metadata.await()
        sb.append(it.name).append("<-->").append(meta.sizeBytes).append("<-->").append(meta.updatedTimeMillis).append("\n")
    }
    val fileIcon = File("${context.filesDir}/iconsMataData.txt")
    fileIcon.writer().use {
        it.write(sb.toString())
    }
    Malitounik.referens.child("/chytanne/iconsMataData.txt").putFile(Uri.fromFile(fileIcon)).await()
}

data class DataImages(var title: String, var size: Long, var file: File, val position: Long, var iconApisanne: String)