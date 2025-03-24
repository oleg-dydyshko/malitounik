package by.carkva_gazeta.malitounik2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.carkva_gazeta.malitounik2.ui.theme.BackgroundTolBarDark
import by.carkva_gazeta.malitounik2.ui.theme.Primary
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik2.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik2.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik2.ui.theme.StrogiPost

@Composable
fun KaliandarKnigaView(
    colorBlackboard: Color = Primary,
    close: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .background(colorBlackboard)
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        val tint = if(colorBlackboard == Primary || colorBlackboard == StrogiPost || colorBlackboard == BackgroundTolBarDark) PrimaryTextBlack
        else PrimaryText
        val data = Settings.data[Settings.caliandarPosition]
        val dayOfYear = data[24].toInt()
        val year = data[3].toInt()
        val modifier = Modifier.weight(1f)
            .padding(horizontal = 10.dp)
            .align(Alignment.CenterVertically)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(2.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(colorBlackboard)
            .padding(vertical = 10.dp)
        val slujba = SlugbovyiaTextu()
        slujba.loadPiarliny()
        Column(
            modifier = Modifier
                .align(Alignment.Top)
        ) {
            Column {
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Column(modifier = modifier) {
                        val listSlujba = slujba.loadSluzbaDayList(SlugbovyiaTextu.VIACZERNIA, dayOfYear, year)
                        val newTint = if (listSlujba.size == 0) SecondaryText else tint
                        Icon(painterResource(R.drawable.moon2_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.viachernia), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                    Column(modifier = modifier) {
                        val listSlujba = slujba.loadSluzbaDayList(SlugbovyiaTextu.PAVIACHERNICA, dayOfYear, year)
                        val newTint = if (listSlujba.size == 0) SecondaryText else tint
                        Icon(painterResource(R.drawable.moon_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.raviachernica), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                }
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Column(modifier = modifier) {
                        val listSlujba = slujba.loadSluzbaDayList(SlugbovyiaTextu.PAUNOCHNICA, dayOfYear, year)
                        val newTint = if (listSlujba.size == 0) SecondaryText else tint
                        Icon(painterResource(R.drawable.sun2_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.paunochnica), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                    Column(modifier = modifier) {
                        val listSlujba = slujba.loadSluzbaDayList(SlugbovyiaTextu.JUTRAN, dayOfYear, year)
                        val newTint = if (listSlujba.size == 0) SecondaryText else tint
                        Icon(painterResource(R.drawable.sun_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.utran), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                }
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Column(modifier = modifier) {
                        val listSlujba = slujba.loadSluzbaDayList(SlugbovyiaTextu.VIALHADZINY, dayOfYear, year)
                        val newTint = if (listSlujba.size == 0) SecondaryText else tint
                        Icon(painterResource(R.drawable.clock_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.gadziny), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                    Column(modifier = modifier) {
                        val listSlujba = slujba.loadSluzbaDayList(SlugbovyiaTextu.LITURHIJA, dayOfYear, year)
                        val newTint = if (listSlujba.size == 0) SecondaryText else tint
                        Icon(painterResource(R.drawable.carkva_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.liturgia), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                }
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Column(modifier = modifier) {
                        val svityia = data[4]
                        val newTint = if (svityia == "no_sviatyia") SecondaryText else tint
                        Icon(painterResource(R.drawable.man_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.jyci), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                    Column(modifier = modifier) {
                        val parliny = slujba.checkParliny(dayOfYear)
                        val newTint = if (!parliny) SecondaryText else tint
                        Icon(painterResource(R.drawable.book_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.piarliny), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                }
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Column(modifier = modifier) {
                        val newTint = SecondaryText
                        Icon(painterResource(R.drawable.kanon_white), contentDescription = "", modifier = Modifier.align(Alignment.CenterHorizontally), tint = newTint)
                        Text(text = stringResource(R.string.ustau), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), fontSize = Settings.fontInterface.sp, color = newTint)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().background(colorBlackboard).clickable {
                close()
            }) {
                Icon(modifier = Modifier.align(Alignment.End), painter = painterResource(R.drawable.keyboard_arrow_up), contentDescription = "", tint = tint)
            }
        }
    }
}

@Preview
@Composable
fun Test() {
    KaliandarKnigaView()
}