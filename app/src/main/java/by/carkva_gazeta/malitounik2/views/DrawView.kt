package by.carkva_gazeta.malitounik2.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik2.Malitounik
import by.carkva_gazeta.malitounik2.R
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

@Composable
fun DrawView(
    modifier: Modifier = Modifier,
    route: String,
    navigateToRazdel: (String) -> Unit = { }
) {
    ModalDrawerSheet(modifier = Modifier) {
        DrawerHeader(modifier)
        HorizontalDivider(
            modifier = modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.kaliandar2),
                )
            },
            selected = route.contains(AllDestinations.KALIANDAR),
            onClick = {
                navigateToRazdel(AllDestinations.KALIANDAR)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.liturgikon),
                )
            },
            selected = route == AllDestinations.BOGASLUJBOVYIA_MENU,
            onClick = {
                navigateToRazdel(AllDestinations.BOGASLUJBOVYIA_MENU)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.MenuVybranoe),
                )
            },
            selected = route == AllDestinations.VYBRANAE_LIST,
            onClick = {
                navigateToRazdel(AllDestinations.VYBRANAE_LIST)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
        HorizontalDivider(
            modifier = modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.bibliaAll),
                )
            },
            selected = route == AllDestinations.BIBLIA,
            onClick = {
                navigateToRazdel(AllDestinations.BIBLIA)
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(R.drawable.krest),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(horizontal = 5.dp)
        )
    }
}

@Composable
fun DrawerHeader(modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        val inputStream = Malitounik.applicationContext().resources.openRawResource(R.raw.citata)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val citataList = ArrayList<String>()
        reader.forEachLine {
            val line = StringBuilder()
            val t1 = it.indexOf("(")
            if (t1 != -1) {
                line.append(it.substring(0, t1).trim())
                line.append("\n")
                line.append(it.substring(t1))
                citataList.add(line.toString())
            }
        }
        val annotated = AnnotatedString.Builder(citataList[Random.nextInt(citataList.size)]).apply {
            addStyle(
                SpanStyle(
                    fontFamily = FontFamily(Font(R.font.andantinoscript)),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = TextUnit(30f, TextUnitType.Sp)
                ), 0, 1
            )
            addStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.comici))), 1, this.length)
        }.toAnnotatedString()

        Text(
            modifier = modifier.fillMaxWidth(),
            text = annotated,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.secondary,
        )

        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.malitounik_name),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            lineHeight = TextUnit(30f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.padding(5.dp))

        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.bgkc_resource),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}