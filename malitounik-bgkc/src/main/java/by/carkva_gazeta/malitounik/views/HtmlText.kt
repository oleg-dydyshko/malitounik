package by.carkva_gazeta.malitounik.views

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
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.goToLink

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
    isLiturgia: Boolean = true,
    scrollState: ScrollState = rememberScrollState(),
    searchText: AnnotatedString = AnnotatedString(""),
    navigateTo: (String) -> Unit = {},
    textLayoutResult: (TextLayoutResult?) -> Unit = {},
    isDialogListinner: (String, Int) -> Unit = { _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var newText = if (Settings.dzenNoch) text.replace("#d00505", "#ff6666", true)
    else text
    newText = newText.replace(
        "<!--<VERSION></VERSION>-->",
        "<em>Версія праграмы: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})</em><br><br>"
    )
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
        goToLink(context, url, title, textLayout.value, isLiturgia, scrollState, coroutineScope, isDialogListinner = { title, position ->
            isDialogListinner(title, position)
        }, navigateTo = {
            navigateTo(it)
        })
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