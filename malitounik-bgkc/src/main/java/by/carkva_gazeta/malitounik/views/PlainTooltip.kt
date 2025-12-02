package by.carkva_gazeta.malitounik.views

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltip(title: String, positioning: TooltipAnchorPosition = TooltipAnchorPosition.Above, content: @Composable () -> Unit) {
    val state = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            coroutineScope.launch {
                delay(7000L)
                state.dismiss()
            }
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
                Text(modifier = Modifier.padding(10.dp), text = title, fontSize = Settings.fontInterface.sp, lineHeight = (Settings.fontInterface * 1.15f).sp, color = MaterialTheme.colorScheme.secondary)
            }
        },
        state = state
    ) {
        content()
    }
}