package by.carkva_gazeta.malitounik.views

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.Settings
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltip(title: String, positioning: TooltipAnchorPosition = TooltipAnchorPosition.Above, content: @Composable () -> Unit) {
    var delay = 4000L
    val t1 = title.indexOf("\n")
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val newTitle = if (!k.getBoolean("gestures", true) && t1 != -1) title.take(t1)
    else title
    val anotatedString = AnnotatedString.Builder(newTitle).apply {
        val t2 = newTitle.indexOf("\n")
        if (t2 != -1) {
            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t2)
            delay = 9000L
        }
    }
    val state = rememberTooltipState(isPersistent = true)
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            Settings.vibrate(true)
            delay(delay.milliseconds)
            state.dismiss()
        }
    }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(positioning = positioning),
        tooltip = {
            Surface(
                modifier = Modifier.padding(5.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                Text(modifier = Modifier.padding(10.dp), text = anotatedString.toAnnotatedString(), fontSize = Settings.fontInterface.sp, lineHeight = (Settings.fontInterface * 1.15f).sp, color = MaterialTheme.colorScheme.secondary)
            }
        },
        state = state
    ) {
        content()
    }
}