package by.carkva_gazeta.malitounik2.views

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import by.carkva_gazeta.malitounik2.BuildConfig
import by.carkva_gazeta.malitounik2.DialogLiturgia
import by.carkva_gazeta.malitounik2.DialogSztoHovaha
import by.carkva_gazeta.malitounik2.MainActivity
import kotlinx.coroutines.launch

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.secondary,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    fontSize: TextUnit = 18.sp,
    scrollState: ScrollState = rememberScrollState()
) {
    /*val annotatedString = buildAnnotatedString {
        val spanned = HtmlCompat.fromHtml(text, mode)
        append(spanned.toString())
        spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is StyleSpan -> when (span.style) {
                    Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                    Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    Typeface.BOLD_ITALIC -> addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                        start,
                        end
                    )
                }

                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    start,
                    end
                )

                is ForegroundColorSpan -> addStyle(
                    SpanStyle(color = MaterialTheme.colorScheme.primary),
                    start,
                    end
                )

                is URLSpan -> {
                    addLink(
                        LinkAnnotation.Url(
                            span.url,
                            TextLinkStyles(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        ), start, end
                    )
                }
            }
        }
    }*/
    val coroutineScope = rememberCoroutineScope()
    val context = LocalActivity.current
    val dzenHoch by remember { mutableStateOf((context as? MainActivity)?.dzenNoch) }
    var newText = if (dzenHoch == true) text.replace("#d00505", "#ff6666", true)
    else text
    newText = newText.replace(
        "<!--<VERSION></VERSION>-->",
        "<em>Версія праграмы: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})</em><br><br>"
    )
    val uriHandler = LocalUriHandler.current
    var dialogSztoHovahaVisable by remember { mutableStateOf(false) }
    if (dialogSztoHovahaVisable) {
        DialogSztoHovaha {
            dialogSztoHovahaVisable = false
        }
    }
    val textLayout = remember { mutableStateOf<TextLayoutResult?>(null) }
    var dialogLiturgia by rememberSaveable { mutableStateOf(false) }
    var chast by rememberSaveable { mutableIntStateOf(0) }
    if (dialogLiturgia) {
        DialogLiturgia(chast) {
            dialogLiturgia = false
        }
    }
    val annotatedString = AnnotatedString.fromHtml(
        newText,
        TextLinkStyles(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        )
    ) { link ->
        when(val url = (link as LinkAnnotation.Url).url) {
            "https://localhost/pasliachytaeca/" -> {
                coroutineScope.launch {
                    scrollState.animateScrollTo(0)
                }
            }
            "https://localhost/shto.novaga/" -> {
                dialogSztoHovahaVisable = true
            }
            "https://localhost/malitvypasliaprychastia/" -> {
                //Малітвы пасьля сьвятога прычасьця
            }
            "https://localhost/pershaiagadzina/" -> {
                textLayout.value?.let { layout ->
                    val t1 = layout.layoutInput.text.indexOf("ПЕРШАЯ ГАДЗІНА")
                    if (t1 != -1) {
                        val line = layout.getLineForOffset(t1)
                        val y = layout.getLineTop(line)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(y.toInt())
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
                        }
                    }
                }
            }
            "https://localhost/litciaiblaslavennechl/" -> {
                //ліцьця і блаславеньне хлябоў
            }
            "https://localhost/zysimprapuskauca/" -> {
                textLayout.value?.let { layout ->
                    val t1 = layout.layoutInput.text.indexOf("10 песьняў")
                    if (t1 != -1) {
                        val line = layout.getLineForOffset(t1)
                        val y = layout.getLineTop(line)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(y.toInt())
                        }
                    }
                }
            }
            "https://localhost/vybranyiavershyzpsalm/" -> {
                chast = 11
                dialogLiturgia = true
            }
            "https://localhost/gltut/" -> {
                chast = 13
                dialogLiturgia = true
            }
            "https://localhost/gospadzetabeklichu/" -> {
                textLayout.value?.let { layout ->
                    val t1 = layout.layoutInput.text.indexOf("Псалом 140")
                    if (t1 != -1) {
                        val line = layout.getLineForOffset(t1)
                        val y = layout.getLineTop(line)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(y.toInt())
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
                        }
                    }
                }
            }
            "https://localhost/gladztut102/" -> {
                chast = 1
                dialogLiturgia = true
            }
            "https://localhost/gladztut91/" -> {
                chast = 2
                dialogLiturgia = true
            }
            "https://localhost/gladztut145/" -> {
                chast = 3
                dialogLiturgia = true
            }
            "https://localhost/gladztut92/" -> {
                chast = 4
                dialogLiturgia = true
            }
            "https://localhost/gladztut94/" -> {
                chast = 10
                dialogLiturgia = true
            }
            "https://localhost/inshyantyfon/" -> {
                chast = 5
                dialogLiturgia = true
            }
            "https://localhost/malitvazapamerlyx/" -> {
                chast = 6
                dialogLiturgia = true
            }
            "https://localhost/malitvazapaclikanyx/" -> {
                chast = 7
                dialogLiturgia = true
            }
            "https://localhost/uspaminpamerlyxijyvix/" -> {
                chast = 14
                dialogLiturgia = true
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
                        }
                    }
                }
            }
            else -> uriHandler.openUri(url)
        }
    }
    Text(
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        text = annotatedString,
        fontSize = fontSize,
        lineHeight = fontSize * 1.15f,
        textAlign = textAlign,
        onTextLayout = { layout ->
            textLayout.value = layout
            //val line = layout.getLineForOffset(t1)
            //y = layout.getLineTop(line)

            //val lineForVertical = layout.getLineForVerticalPosition(scrollStateValue.toFloat() + innerPadding.calculateTopPadding().value)
            //val lineForOffset = layout.getLineForOffset(t1)
            //Log.d("Oleg", "$lineForVertical $y $line")
        }
    )
}