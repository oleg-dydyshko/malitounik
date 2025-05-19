package by.carkva_gazeta.malitounik

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.BezPosta
import by.carkva_gazeta.malitounik.ui.theme.Divider
import by.carkva_gazeta.malitounik.ui.theme.Post
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaliandarScreenInfo(navController: NavHostController) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val maxLine = remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = !(context as MainActivity).dzenNoch
        }
    }
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
                        text = stringResource(R.string.symbols),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = Settings.fontInterface.sp,
                        maxLines = maxLine.intValue,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(10.dp)
            .verticalScroll(rememberScrollState())) {
            Text(
                text = stringResource(R.string.Znaki_cviat),
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.znaki_krest_v_kruge), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                val text = stringResource(R.string.dvuna_i_vial)
                val t1 = text.indexOf("\n")
                val annotatedString =
                    buildAnnotatedString {
                        append(text)
                        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, t1)
                    }
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = annotatedString,
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.znaki_krest_v_polukruge), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(R.string.Z_Lic_na_ve),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.znaki_krest), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(R.string.Z_v_v_v_u_n_u),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.znaki_ttk), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(R.string.Z_sh_v_v_u_u),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.znaki_ttk_black), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(R.string.Z_sh_v_m_u_u),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.tipicon_fon),
                fontSize = (Settings.fontInterface - 2).sp,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Primary)
                    .padding(10.dp),
                text = stringResource(R.string.niadzeli_i_sviaty),
                fontSize = (Settings.fontInterface - 2).sp,
                color = PrimaryTextBlack
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(Divider)
                    .padding(10.dp),
                text = stringResource(R.string.zvychaynye_dny),
                fontSize = (Settings.fontInterface - 2).sp,
                color = PrimaryText
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(BezPosta)
                    .padding(10.dp),
                text = stringResource(R.string.No_post_n),
                fontSize = (Settings.fontInterface - 2).sp,
                color = PrimaryText
            )
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.fishe), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(Post)
                        .padding(10.dp),
                    text = stringResource(R.string.Post),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = PrimaryText
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(22.dp, 22.dp), painter = painterResource(R.drawable.fishe), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(StrogiPost)
                        .padding(10.dp),
                    text = stringResource(R.string.Strogi_post_n),
                    fontSize = (Settings.fontInterface - 2).sp,
                    color = PrimaryTextBlack
                )
            }
        }
    }
}