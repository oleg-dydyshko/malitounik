package by.carkva_gazeta.malitounik.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.Settings
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    initValue: Int,
    menuList: Array<String>,
    onClickItem: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldNotificstionState = rememberTextFieldState(menuList[initValue])
    if (textFieldNotificstionState.text != menuList[initValue]) textFieldNotificstionState.setTextAndPlaceCursorAtEnd(menuList[initValue])
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Row(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .clip(MaterialTheme.shapes.small)
                .clickable {}
                .background(Divider)
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                text = textFieldNotificstionState.text.toString(),
                fontSize = (Settings.fontInterface - 2).sp,
                color = PrimaryText,
            )
            Icon(
                modifier = Modifier
                    .padding(start = 21.dp, end = 2.dp)
                    .size(22.dp, 22.dp),
                painter = painterResource(if (expanded) R.drawable.keyboard_arrow_up else R.drawable.keyboard_arrow_down),
                tint = PrimaryText,
                contentDescription = ""
            )
        }
        ExposedDropdownMenu(
            containerColor = Divider,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            menuList.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = Settings.fontInterface.sp) }, onClick = {
                        textFieldNotificstionState.setTextAndPlaceCursorAtEnd(option)
                        expanded = false
                        onClickItem(index)
                    }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding, colors = MenuDefaults.itemColors(textColor = PrimaryText)
                )
            }
        }
    }
}