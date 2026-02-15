package by.carkva_gazeta.malitounik

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryBlack
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.PlainTooltip
import by.carkva_gazeta.malitounik.views.openAssetsResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cytaty(navController: NavHostController) {
    val context = LocalContext.current
    val view = LocalView.current
    val inputStream = openAssetsResources(context, "citata.txt")
    val listState = remember { inputStream.split("\n") }
    val lazyListState = rememberLazyListState()
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window, view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    LaunchedEffect(Unit) {
        lazyListState.scrollToItem(AppNavGraphState.getScrollValuePosition("Cytaty"), AppNavGraphState.getScrollValueOffset("Cytaty"))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.cytaty), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis, fontSize = Settings.fontInterface.sp
                        )
                    }
                }, navigationIcon = {
                    PlainTooltip(stringResource(R.string.exit_page), TooltipAnchorPosition.Below) {
                        IconButton(onClick = {
                            Settings.vibrate()
                            navController.popBackStack()
                        }, content = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back), tint = MaterialTheme.colorScheme.onSecondary, contentDescription = ""
                            )
                        })
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onTertiary)
            )
        }
    ) { innerPadding ->
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset, source: NestedScrollSource
                ): Offset {
                    AppNavGraphState.setScrollValuePosition("Cytaty", lazyListState.firstVisibleItemIndex, lazyListState.firstVisibleItemScrollOffset)
                    return super.onPreScroll(available, source)
                }

                override suspend fun onPostFling(
                    consumed: Velocity, available: Velocity
                ): Velocity {
                    return super.onPostFling(consumed, available)
                }
            }
        }
        Box(
            modifier = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr), innerPadding.calculateTopPadding(), innerPadding.calculateEndPadding(LayoutDirection.Rtl), 0.dp
            )
        ) {
            LazyColumn(state = lazyListState, modifier = Modifier.nestedScroll(nestedScrollConnection)) {
                items(listState.size) { item ->
                    val t1 = listState[item].indexOf("(")
                    val cytata = AnnotatedString.Builder(listState[item]).apply {
                        addStyle(
                            SpanStyle(
                                fontFamily = FontFamily(Font(R.font.andantinoscript)), fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = if (Settings.dzenNoch) PrimaryBlack else Primary, fontSize = (Settings.fontInterface + 4).sp
                            ), 0, 1
                        )
                        addStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.comici))), 1, this.length)
                        if (t1 != -1) {
                            addStyle(ParagraphStyle(textAlign = TextAlign.Right), t1, this.length)
                        }
                    }.toAnnotatedString()
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = ""
                        )
                        Text(
                            text = cytata,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding().plus(10.dp)))
                }
            }
        }
    }
}