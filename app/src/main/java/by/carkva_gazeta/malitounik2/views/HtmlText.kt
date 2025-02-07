package by.carkva_gazeta.malitounik2.views

import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.secondary,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    mode: Int = HtmlCompat.FROM_HTML_MODE_LEGACY
) {
    val annotatedString = buildAnnotatedString {
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
    }

    Text(
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        text = annotatedString,
        fontSize = fontSize,
        lineHeight = lineHeight,
        textAlign = textAlign
    )
}