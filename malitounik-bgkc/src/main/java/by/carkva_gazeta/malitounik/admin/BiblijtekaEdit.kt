package by.carkva_gazeta.malitounik.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.views.DropdownMenuBox
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@Composable
fun DialogEditBiblijteka(list: ArrayList<String>, onSave: (title: String, rubrika: Int, apisanne: String, pdfFile: String) -> Unit, onDismiss: (String) -> Unit) {
    Dialog(onDismissRequest = { onDismiss(list[5]) }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            val context = LocalContext.current
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            var fontSize by remember { mutableFloatStateOf(k.getFloat("font_biblia", 22F)) }
            var textFieldValueStateTitle by rememberSaveable { mutableStateOf(list[0]) }
            var textFieldValueStateApisanne by rememberSaveable { mutableStateOf(list[1]) }
            var rubryka by rememberSaveable { mutableIntStateOf(list[4].toInt() - 1) }
            var resourceId by remember { mutableIntStateOf(1) }
            var bitmap: Bitmap? = remember(resourceId) {
                val t1 = list[5].lastIndexOf("/")
                val imageNewFile = if (list[5].isEmpty()) "cacheNew.png"
                else list[5].substring(t1 + 1)
                val file = File("${context.filesDir}/bibliatekaImage/$imageNewFile")
                if (file.exists()) {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    BitmapFactory.decodeFile("${context.filesDir}/bibliatekaImage/$imageNewFile", options)
                } else {
                    null
                }
            }
            var pdfFileName by remember { mutableStateOf(list[2]) }
            val mActivityResultImageFile = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val imageUri = activityResult.data?.data
                    imageUri?.let { image ->
                        bitmap = if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(context.contentResolver, image)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                        }
                        bitmap?.let {
                            val t1 = list[5].lastIndexOf("/")
                            val imageNewFile = if (list[5].isEmpty()) "cacheNew.png"
                            else list[5].substring(t1 + 1)
                            val image = File("${context.filesDir}/bibliatekaImage/$imageNewFile")
                            if (list[5].isNotEmpty()) {
                                val cacheImage = File("${context.filesDir}/cache/cache.png")
                                image.copyTo(cacheImage, overwrite = true)
                            }
                            val out = FileOutputStream(image)
                            it.compress(Bitmap.CompressFormat.PNG, 90, out)
                            out.flush()
                            out.close()
                            resourceId++
                        }
                    }
                }
            }
            val mActivityResultFile = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val fileUri = activityResult.data?.data
                    fileUri?.let { uri ->
                        var result: String? = null
                        if (uri.scheme.equals("content")) {
                            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
                            try {
                                if (cursor != null && cursor.moveToFirst()) {
                                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    if (nameIndex != -1) {
                                        result = cursor.getString(nameIndex)
                                    }
                                }
                            } finally {
                                cursor?.close()
                            }
                        }
                        if (result == null) {
                            result = uri.path
                            val cut = result?.lastIndexOf('/') ?: -1
                            result = result?.substring(cut + 1)
                        }
                        pdfFileName = result ?: "pdfFile.pdf"
                        val file = File("${context.filesDir}/bibliatekaPdf/$pdfFileName")
                        val output = FileOutputStream(file)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        inputStream?.let { inputStream ->
                            val buffer = ByteArray(1024)
                            var size: Int
                            while ((inputStream.read(buffer).also { size = it }) != -1) {
                                output.write(buffer, 0, size)
                            }
                        }
                        inputStream?.close()
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.title_biblijateka),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    item {
                        TextField(
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                            placeholder = { Text(stringResource(R.string.admin_title), fontSize = Settings.fontInterface.sp) },
                            value = textFieldValueStateTitle,
                            onValueChange = {
                                textFieldValueStateTitle = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Default)
                        )
                        DropdownMenuBox(
                            initValue = rubryka,
                            menuList = stringArrayResource(R.array.amin_title_book)
                        ) {
                            rubryka = it
                        }
                        if (bitmap == null) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                    .padding(bottom = 10.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .border(
                                        width = 1.dp,
                                        color = SecondaryText,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(vertical = 10.dp)
                                    .clickable {
                                        val intent = Intent()
                                        intent.type = "*/*"
                                        intent.action = Intent.ACTION_GET_CONTENT
                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                                        mActivityResultImageFile.launch(Intent.createChooser(intent, context.getString(R.string.vybrac_file)))
                                    },
                                text = stringResource(R.string.niama_malunka), fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center
                            )
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                                Image(
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .size(width = 200.dp, height = (200 / aspectRatio).dp)
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = SecondaryText,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            val intent = Intent()
                                            intent.type = "*/*"
                                            intent.action = Intent.ACTION_GET_CONTENT
                                            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                                            mActivityResultImageFile.launch(Intent.createChooser(intent, context.getString(R.string.vybrac_file)))
                                        }, bitmap = bitmap.asImageBitmap(), contentDescription = ""
                                )
                            }
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .padding(bottom = 10.dp)
                                .clickable {
                                    val intent = Intent()
                                    intent.type = "application/pdf"
                                    intent.action = Intent.ACTION_GET_CONTENT
                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
                                    mActivityResultFile.launch(Intent.createChooser(intent, context.getString(R.string.vybrac_file)))
                                },
                            text = pdfFileName, fontSize = fontSize.sp, lineHeight = (fontSize * 1.15).sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center
                        )
                        TextField(
                            textStyle = TextStyle(fontSize = Settings.fontInterface.sp),
                            placeholder = { Text(stringResource(R.string.opisanie), fontSize = Settings.fontInterface.sp) },
                            value = textFieldValueStateApisanne,
                            onValueChange = {
                                textFieldValueStateApisanne = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Default)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.End)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                onClick = { onDismiss(list[5]) }, shape = MaterialTheme.shapes.small
                            ) {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                                Text(stringResource(R.string.cansel), fontSize = 18.sp)
                            }
                            TextButton(
                                onClick = {
                                    onSave(textFieldValueStateTitle, rubryka + 1, textFieldValueStateApisanne, pdfFileName)
                                }, shape = MaterialTheme.shapes.small
                            ) {
                                Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.save), contentDescription = "")
                                Text(stringResource(R.string.save_sabytie), fontSize = 18.sp)
                            }
                        }
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .imePadding()
                                .padding(bottom = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

fun saveBibliateka(context: Context, arrayList: SnapshotStateList<ArrayList<String>>, position: Int, title: String, rubrika: Int, apisanne: String, pdfFile: String, isLoad: (Boolean) -> Unit) {
    if (Settings.isNetworkAvailable(context)) {
        CoroutineScope(Dispatchers.Main).launch {
            isLoad(true)
            withContext(Dispatchers.IO) {
                val t1 = pdfFile.lastIndexOf(".")
                val pdfName = if (t1 != -1) pdfFile.take(t1)
                else pdfFile
                val imageLocal = "$pdfName.png"
                val localPdfFile = File("${context.filesDir}/bibliatekaPdf/$pdfFile")
                val image = File("${context.filesDir}/cache/cache.png")
                if (position == -1) {
                    val mySqlList = ArrayList<String>()
                    mySqlList.add(title)
                    mySqlList.add(apisanne)
                    mySqlList.add(pdfFile)
                    mySqlList.add(localPdfFile.length().toString())
                    mySqlList.add(rubrika.toString())
                    mySqlList.add(imageLocal)
                    arrayList.add(0, mySqlList)
                } else {
                    arrayList[position][0] = title
                    arrayList[position][1] = apisanne
                    arrayList[position][2] = pdfFile
                    arrayList[position][4] = rubrika.toString()
                    arrayList[position][5] = imageLocal
                }
                val dir = File("${context.filesDir}/bibliatekaImage")
                if (!dir.exists()) dir.mkdir()
                val newFile = File("${context.filesDir}/bibliatekaImage/cacheNew.png")
                val file = File("${context.filesDir}/bibliatekaImage/$imageLocal")
                if (newFile.exists()) {
                    newFile.copyTo(file, overwrite = true)
                    newFile.delete()
                }
                if (image.exists()) image.delete()
                Malitounik.referens.child("/data/bibliateka/$pdfFile").putFile(Uri.fromFile(localPdfFile)).await()
                Malitounik.referens.child("/images/bibliateka/" + file.name).putFile(Uri.fromFile(file)).await()
                saveBibliatekaJson(context, arrayList)
                localPdfFile.delete()
            }
            Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
            isLoad(false)
        }
    } else {
        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}

suspend fun saveBibliatekaJson(context: Context, arrayList: SnapshotStateList<ArrayList<String>>) {
    val localFile = File("${context.filesDir}/cache/cache.txt")
    val gson = Gson()
    val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
    localFile.writer().use {
        it.write(gson.toJson(arrayList, type))
    }
    Malitounik.referens.child("/bibliateka.json").putFile(Uri.fromFile(localFile)).await()
}