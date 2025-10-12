package by.carkva_gazeta.malitounik.views

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.BuildConfig
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import kotlinx.coroutines.launch

enum class DialogListinner {
    DIALOGQRCODE,
    DIALOGSZTOHOVAHA,
    DIALOGLITURGIA
}

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    title: String = "",
    color: Color = MaterialTheme.colorScheme.secondary,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    fontSize: TextUnit = 22.sp,
    isNoLiturgia: Boolean = true,
    scrollState: ScrollState = rememberScrollState(),
    searchText: AnnotatedString = AnnotatedString(""),
    navigateTo: (String) -> Unit = {},
    textLayoutResult: (TextLayoutResult?) -> Unit = {},
    isDialogListinner: (String, Int) -> Unit = { _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var newText = if (Settings.dzenNoch.value) text.replace("#d00505", "#ff6666", true)
    else text
    newText = newText.replace(
        "<!--<VERSION></VERSION>-->",
        "<em>Версія праграмы: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})</em><br><br>"
    )
    val uriHandler = LocalUriHandler.current
    val textLayout = remember { mutableStateOf<TextLayoutResult?>(null) }
    val annotatedString = AnnotatedString.fromHtml(
        newText,
        TextLinkStyles(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        )
    ) { link ->
        val url = (link as LinkAnnotation.Url).url
        if (url.contains("https://localhost/")) {
            when (url) {
                "https://localhost/pasliachytaeca/" -> {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(0)
                        if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
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
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ПЕРШАЯ ГАДЗІНА")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/trecaiagadzina/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ТРЭЦЯЯ ГАДЗІНА")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/shostaiagadzina/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ШОСТАЯ ГАДЗІНА")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/dzeviataiagadzina/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ДЗЯВЯТАЯ ГАДЗІНА")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/zakanchennevialposty/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ЗАКАНЧЭНЬНЕ АБЕДНІЦЫ")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/litciaiblaslavennechl/" -> {
                    navigateTo("litciaiblaslavennechl")
                }

                "https://localhost/zysimprapuskauca/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("10 песьняў")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
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
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("Псалом 140")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/gladzinijai/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ЗАКАНЧЭНЬНЕ ВЯЧЭРНІ Ў ВЯЛІКІ ПОСТ")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
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
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("Адзінародны Сыне")
                        val t2 = layout.layoutInput.text.indexOf("Адзінародны Сыне", t1 + 17)
                        if (t2 != -1) {
                            val line = layout.getLineForOffset(t2)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/gliadzitutdabraveshchane/" -> {
                    navigateTo("gliadzitutdabraveshchane")
                }

                "https://localhost/autorakkanon/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("АЎТОРАК")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/seradakanon/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("СЕРАДА")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/chacverkanon/" -> {
                    textLayout.value?.let { layout ->
                        val t1 = layout.layoutInput.text.indexOf("ЧАЦЬВЕР")
                        if (t1 != -1) {
                            val line = layout.getLineForOffset(t1)
                            val y = layout.getLineTop(line)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(y.toInt())
                                if (title.isNotEmpty()) AppNavGraphState.setScrollValuePosition(title, scrollState.value)
                            }
                        }
                    }
                }

                "https://localhost/cytanne/" -> {
                    if (isNoLiturgia) {
                        navigateTo("cytanne")
                    } else {
                        navigateTo("error")
                    }
                }

                else -> Toast.makeText(context, context.getString(R.string.error_ch), Toast.LENGTH_SHORT).show()
            }
        } else {
            uriHandler.openUri(url)
        }
    }
    Text(
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        text = searchText.ifEmpty { annotatedString },
        fontSize = fontSize,
        lineHeight = fontSize * 1.15f,
        textAlign = textAlign,
        onTextLayout = { layout ->
            textLayout.value = layout
            textLayoutResult(layout)
        }
    )
}