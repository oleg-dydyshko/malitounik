package by.carkva_gazeta.malitounik.views

import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun AppDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    if (expanded) {
        Dialog(onDismissRequest = { onDismissRequest() }, DialogProperties(usePlatformDefaultWidth = false)) {
            (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
                val params = window.attributes
                params.gravity = Gravity.TOP or Gravity.END
                window.attributes = params
                window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                window.setDimAmount(0f)
            }
            Surface(
                modifier = Modifier.padding(5.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    content()
                }
            }
        }
    }
}