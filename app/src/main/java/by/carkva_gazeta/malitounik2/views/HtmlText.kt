package by.carkva_gazeta.malitounik2.views

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik2.MainActivity

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.secondary,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    fontSize: TextUnit = 18.sp
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
    val context = LocalActivity.current
    val dzenHoch by remember { mutableStateOf((context as? MainActivity)?.dzenNoch) }
    val newText = if (dzenHoch == true) text.replace("#d00505", "#ff6666", true)
    else text
    Text(
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        text = AnnotatedString.fromHtml(newText),
        fontSize = fontSize,
        lineHeight = fontSize * 1.15f,
        textAlign = textAlign
    )
}