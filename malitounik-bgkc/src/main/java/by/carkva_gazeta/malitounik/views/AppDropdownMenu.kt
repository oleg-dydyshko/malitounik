package by.carkva_gazeta.malitounik.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun AppDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    BackHandler(expanded) {
        onDismissRequest()
    }
    if (expanded) {
        val interactionSourse = remember { MutableInteractionSource() }
        Popup(onDismissRequest = { onDismissRequest() }) {
            Column(modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = interactionSourse, indication = null) {
                    onDismissRequest()
                }, horizontalAlignment = Alignment.End) {
                Surface(
                    modifier = Modifier.padding(5.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.wrapContentWidth(unbounded = true), verticalArrangement = Arrangement.Center) {
                        content()
                    }
                }
            }
        }
    }
}