package by.carkva_gazeta.malitounik.views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltip(title: String, content: @Composable () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip(containerColor = Divider) { Text(text = title, fontSize = Settings.fontInterface.sp, lineHeight = (Settings.fontInterface * 1.15f).sp, color = PrimaryText) }
        },
        state = rememberTooltipState(isPersistent = true)
    ) {
        content()
    }
}