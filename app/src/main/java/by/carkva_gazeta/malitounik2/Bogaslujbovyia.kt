package by.carkva_gazeta.malitounik2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import by.carkva_gazeta.malitounik2.views.HtmlText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bogaslujbovyia() {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "title",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { } },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = { },
                                text = { Text(stringResource(R.string.tools_item)) }
                            )
                            DropdownMenuItem(
                                onClick = { },
                                text = { Text(stringResource(R.string.sabytie)) }
                            )
                            DropdownMenuItem(
                                onClick = { },
                                text = { Text(stringResource(R.string.search_svityia)) }
                            )
                            DropdownMenuItem(
                                onClick = { },
                                text = { Text(stringResource(R.string.pra_nas)) }
                            )
                            DropdownMenuItem(
                                onClick = { },
                                text = { Text(stringResource(R.string.help)) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiary)
            )
        }, modifier = Modifier
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())) {
            val inputStream =
                Malitounik.applicationContext().resources.openRawResource(R.raw.piesni_bahar_karaleva_supakoju)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val citataList = reader.readText()
            HtmlText(
                modifier = Modifier.padding(10.dp),
                text = citataList
            )
        }
    }
}