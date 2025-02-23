package by.carkva_gazeta.malitounik2

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaeNatatki(navController: NavHostController) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val maxLine = remember { mutableIntStateOf(1) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.clickable {
                            maxLine.intValue = Int.MAX_VALUE
                            coroutineScope.launch {
                                delay(5000L)
                                maxLine.intValue = 1
                            }
                        },
                        text = stringResource(R.string.maje_natatki),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = maxLine.intValue,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                tint = MaterialTheme.colorScheme.onSecondary,
                                contentDescription = ""
                            )
                        })
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }, modifier = Modifier
    ) { innerPadding ->
        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                0.dp
            )
        ) {
            File(context.filesDir.toString().plus("/Malitva")).walk().forEach {
                if (it.isFile) {
                    //val name = it.name
                    //val t1 = name.lastIndexOf("_")
                    //val index = name.substring(t1 + 1).toLong()
                    val inputStream = FileReader(it)
                    val reader = BufferedReader(inputStream)
                    val res = reader.readText().split("<MEMA></MEMA>")
                    inputStream.close()
                    /*var lRTE: Long = 1
                    if (res[1].contains("<RTE></RTE>")) {
                        val start = res[1].indexOf("<RTE></RTE>")
                        val end = res[1].length
                        lRTE = res[1].substring(start + 11, end).toLong()
                    }
                    if (lRTE <= 1) {
                        lRTE = it.lastModified()
                    }*/
                    item {
                        Text(res[0])
                    }
                }
            }
            item {
                Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
            }
        }
    }
}