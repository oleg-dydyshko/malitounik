package by.carkva_gazeta.malitounik2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik2.ui.theme.MalitounikTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class WidgetRadyjoMaryiaProgram : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MalitounikTheme {
                val checkInternet = intent.extras?.getBoolean("checkInternet", false) == true
                if (checkInternet) {
                    DialogNoInternet(
                        onDismissRequest = { finish() })
                } else {
                    DialogProgramRadoiMaryia(
                        onDismissRequest = { finish() }
                    )
                    val intent = Intent(this, WidgetRadyjoMaryia::class.java)
                    intent.putExtra(
                        "action",
                        ServiceRadyjoMaryia.WIDGET_RADYJO_MARYIA_PROGRAM_EXIT
                    )
                    sendBroadcast(intent)
                }
            }
        }
    }
}

@Composable
fun DialogProgramRadoiMaryia(
    onDismissRequest: () -> Unit
) {
    var sendTitlePadioMaryiaJob: Job? = null
    var progress by remember { mutableStateOf(false) }
    var program by remember { mutableStateOf(AnnotatedString("")) }
    LaunchedEffect(Unit) {
        sendTitlePadioMaryiaJob = CoroutineScope(Dispatchers.Main).launch {
            progress = true
            runCatching {
                withContext(Dispatchers.IO) {
                    try {
                        var efir: String
                        val mURL1 = URL("https://radiomaria.by/player/hintbackend.php")
                        with(mURL1.openConnection() as HttpURLConnection) {
                            val sb = StringBuilder()
                            BufferedReader(InputStreamReader(inputStream)).use {
                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    sb.append(inputLine)
                                    inputLine = it.readLine()
                                }
                            }
                            var text =
                                AnnotatedString.fromHtml(sb.toString()).toString().trim()
                            val t1 = text.indexOf(":", ignoreCase = true)
                            if (t1 != -1) {
                                text = text.substring(t1 + 1)
                            }
                            val t2 = text.indexOf(">", ignoreCase = true)
                            if (t2 != -1) {
                                text = text.substring(t2 + 1)
                            }
                            efir =
                                "<strong>Цяпер у эфіры:</strong><br><em>" + text.trim() + "</em>"
                        }
                        val mURL = URL("https://radiomaria.by/program")
                        with(mURL.openConnection() as HttpURLConnection) {
                            val sb = StringBuilder()
                            BufferedReader(InputStreamReader(inputStream)).use {
                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    sb.append(inputLine)
                                    inputLine = it.readLine()
                                }
                            }
                            withContext(Dispatchers.Main) {
                                var text = sb.toString()
                                text = text.replace("<h1>Праграма</h1>", "")
                                text = text.replace("<div class=\"dayhdr\">", "<strong>")
                                text = text.replace("</div><ul>", "</strong><p>")
                                text = text.replace("</ul>", "")
                                text = text.replace("<li>", "")
                                text = text.replace("</div>", "")
                                text = text.replace("</li>", "<p>")
                                text = text.replace("<span class=\"pstarttime\">", "– ")
                                text = text.replace("</span>", "")
                                text = text.replace("<div class=\"programday\">", "")
                                text = text.replace("<span class=\"ptitle\">", " ")
                                val t1 = text.indexOf(
                                    "<div class=\"program\">",
                                    ignoreCase = true
                                )
                                if (t1 != -1) {
                                    val t2 = text.indexOf(
                                        "<div id=\"sidebar-2\"",
                                        t1,
                                        ignoreCase = true
                                    )
                                    if (t2 != -1) {
                                        text = text.substring(t1, t2)
                                    }
                                }
                                val t2 = text.lastIndexOf("<div style=\"clear: both;\">")
                                if (t2 != -1) {
                                    text = text.substring(0, t2)
                                }
                                text = text.replace("<div style=\"clear: both;\">", "")
                                text = "$efir$text"
                                program = AnnotatedString.fromHtml(text.trim())
                            }
                        }
                    } catch (_: Throwable) {
                    }
                }
            }
            progress = false
        }
    }
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.description), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.program_radio_maryia))
        },
        text = {
            if (progress) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                Text(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    text = program,
                    fontSize = Settings.fontInterface.sp
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    sendTitlePadioMaryiaJob?.cancel()
                    sendTitlePadioMaryiaJob = null
                }
            ) {
                Text(stringResource(R.string.close), fontSize = Settings.fontInterface.sp)
            }
        }
    )
}

@Composable
fun DialogNoInternet(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.cloud_off), contentDescription = "")
        },
        title = {
            Text(text = stringResource(R.string.no_internet))
        },
        text = {
            Text(text = stringResource(R.string.check_internet), fontSize = Settings.fontInterface.sp)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.ok), fontSize = Settings.fontInterface.sp)
            }
        }
    )
}