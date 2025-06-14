package by.carkva_gazeta.malitounik

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import by.carkva_gazeta.malitounik.ui.theme.BackgroundTolBarDark
import by.carkva_gazeta.malitounik.ui.theme.Primary
import by.carkva_gazeta.malitounik.ui.theme.PrimaryText
import by.carkva_gazeta.malitounik.ui.theme.PrimaryTextBlack
import by.carkva_gazeta.malitounik.ui.theme.SecondaryText
import by.carkva_gazeta.malitounik.ui.theme.StrogiPost
import java.util.Calendar

@Composable
fun KaliandarKnigaView(
    colorBlackboard: Color,
    navigateToBogaslujbovyia: (title: String, resurs: String) -> Unit,
    navigateToSvityiaView: (svity: Boolean, position: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val interactionSourse = remember { MutableInteractionSource() }
    var dialogKnigaView by remember { mutableStateOf(false) }
    val slujbaList = remember { mutableStateListOf<SlugbovyiaTextuData>() }
    var slujva by remember { mutableIntStateOf(SlugbovyiaTextu.LITURHIJA) }
    if (dialogKnigaView) {
        DialogKniga(
            slujva, slujbaList,
            navigateToBogaslujbovyia = { title, resourse ->
                navigateToBogaslujbovyia(title, resourse)
                dialogKnigaView = false
            }) {
            dialogKnigaView = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0f, 0f, 0f, 0.5f))
            .clickable(
                interactionSource = interactionSourse,
                indication = null
            ) {
                onDismiss()
            }
    ) {
        val tint = if (colorBlackboard == Primary || colorBlackboard == StrogiPost || colorBlackboard == BackgroundTolBarDark) PrimaryTextBlack
        else PrimaryText
        val data = Settings.data[Settings.caliandarPosition]
        val dayOfYear = data[24].toInt()
        val year = data[3].toInt()
        val modifier = Modifier
            .weight(1f)
            .padding(horizontal = 5.dp)
            .align(Alignment.CenterVertically)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(PrimaryTextBlack)
            .padding(2.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(colorBlackboard)
        val slujba = SlugbovyiaTextu()
        slujba.loadPiarliny()
        var viewPiarliny by remember { mutableStateOf(false) }
        if (viewPiarliny) {
            DialogPairlinyView(data[1].toInt(), data[2].toInt() + 1) {
                viewPiarliny = false
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    val listSlujbaViach = slujba.loadSluzbaDayList(SlugbovyiaTextu.VIACZERNIA, dayOfYear, year)
                    Column(modifier = modifier
                        .clickable(listSlujbaViach.isNotEmpty()) {
                            if (listSlujbaViach.size == 1) {
                                navigateToBogaslujbovyia(listSlujbaViach[0].title, listSlujbaViach[0].resource)
                            } else {
                                slujbaList.clear()
                                slujbaList.addAll(listSlujbaViach)
                                slujva = 1
                                dialogKnigaView = true
                            }
                        }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (listSlujbaViach.isEmpty()) SecondaryText else tint
                        Icon(painterResource(R.drawable.moon2_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.viachernia), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                    val listSlujbaPavia = slujba.loadSluzbaDayList(SlugbovyiaTextu.PAVIACHERNICA, dayOfYear, year)
                    listSlujbaPavia.add(SlugbovyiaTextuData(0, "Павячэрніца малая", "bogashlugbovya/paviaczernica_malaja.html", SlugbovyiaTextu.PAVIACHERNICA))
                    Column(modifier = modifier.clickable {
                        if (listSlujbaPavia.size == 1) {
                            navigateToBogaslujbovyia(listSlujbaPavia[0].title, listSlujbaPavia[0].resource)
                        } else {
                            slujbaList.clear()
                            slujbaList.addAll(listSlujbaPavia)
                            slujva = 2
                            dialogKnigaView = true
                        }
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (listSlujbaPavia.isEmpty()) SecondaryText else tint
                        Icon(painterResource(R.drawable.moon_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.raviachernica), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                    val listSlujbaPaunoch = slujba.loadSluzbaDayList(SlugbovyiaTextu.PAUNOCHNICA, dayOfYear, year)
                    Column(modifier = modifier.clickable(listSlujbaPaunoch.isNotEmpty()) {
                        if (listSlujbaPaunoch.size == 1) {
                            navigateToBogaslujbovyia(listSlujbaPaunoch[0].title, listSlujbaPaunoch[0].resource)
                        } else {
                            slujbaList.clear()
                            slujbaList.addAll(listSlujbaPaunoch)
                            slujva = 3
                            dialogKnigaView = true
                        }
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (listSlujbaPaunoch.isEmpty()) SecondaryText else tint
                        Icon(painterResource(R.drawable.sun2_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.paunochnica), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                }
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    val listSlujbaJutran = slujba.loadSluzbaDayList(SlugbovyiaTextu.JUTRAN, dayOfYear, year)
                    if (data[0].toInt() == Calendar.SUNDAY) {
                        listSlujbaJutran.add(SlugbovyiaTextuData(0, "Ютрань нядзельная (у скароце)", "bogashlugbovya/jutran_niadzelnaja.html", SlugbovyiaTextu.JUTRAN))
                    }
                    Column(modifier = modifier.clickable(listSlujbaJutran.isNotEmpty()) {
                        if (listSlujbaJutran.size == 1) {
                            navigateToBogaslujbovyia(listSlujbaJutran[0].title, listSlujbaJutran[0].resource)
                        } else {
                            slujbaList.clear()
                            slujbaList.addAll(listSlujbaJutran)
                            slujva = 4
                            dialogKnigaView = true
                        }
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (listSlujbaJutran.isEmpty()) SecondaryText else tint
                        Icon(painterResource(R.drawable.sun_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.utran), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                    val listSlujbaVilHadz = slujba.loadSluzbaDayList(SlugbovyiaTextu.VIALHADZINY, dayOfYear, year)
                    Column(modifier = modifier.clickable(listSlujbaVilHadz.isNotEmpty()) {
                        if (listSlujbaVilHadz.size == 1) {
                            navigateToBogaslujbovyia(listSlujbaVilHadz[0].title, listSlujbaVilHadz[0].resource)
                        } else {
                            slujbaList.clear()
                            slujbaList.addAll(listSlujbaVilHadz)
                            slujva = 5
                            dialogKnigaView = true
                        }
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (listSlujbaVilHadz.isEmpty()) SecondaryText else tint
                        Icon(painterResource(R.drawable.clock_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.gadziny), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                    val listSlujbaLitur = slujba.loadSluzbaDayList(SlugbovyiaTextu.LITURHIJA, dayOfYear, year)
                    Column(modifier = modifier.clickable(listSlujbaLitur.isNotEmpty()) {
                        if (listSlujbaLitur.size == 1) {
                            navigateToBogaslujbovyia(listSlujbaLitur[0].title, listSlujbaLitur[0].resource)
                        } else {
                            slujbaList.clear()
                            slujbaList.addAll(listSlujbaLitur)
                            slujva = 6
                            dialogKnigaView = true
                        }
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (listSlujbaLitur.isEmpty()) SecondaryText else tint
                        Icon(painterResource(R.drawable.carkva_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.liturgia), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                }
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Column(modifier = modifier.padding(vertical = 10.dp)) {
                        val newTint = SecondaryText
                        Icon(painterResource(R.drawable.kanon_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.ustau), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                    val svityia = data[4]
                    Column(modifier = modifier.clickable(svityia != "no_sviatyia") {
                        navigateToSvityiaView(false, Settings.caliandarPosition)
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (svityia == "no_sviatyia") SecondaryText else tint
                        Icon(painterResource(R.drawable.man_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.jyci), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                    val parliny = slujba.checkParliny(dayOfYear)
                    Column(modifier = modifier.clickable(parliny) {
                        viewPiarliny = true
                    }
                        .padding(vertical = 10.dp)
                    ) {
                        val newTint = if (!parliny) SecondaryText else tint
                        Icon(painterResource(R.drawable.book_white), contentDescription = "", modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(40.dp, 40.dp), tint = newTint)
                        Text(
                            text = stringResource(R.string.piarliny), modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp), fontSize = 18.sp, color = newTint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogKniga(
    slujba: Int,
    list: SnapshotStateList<SlugbovyiaTextuData>,
    navigateToBogaslujbovyia: (title: String, resurs: String) -> Unit,
    onDismiss: () -> Unit
) {
    val slujvaTitle = when (slujba) {
        1 -> stringResource(R.string.viachernia)
        2 -> stringResource(R.string.raviachernica)
        3 -> stringResource(R.string.paunochnica)
        4 -> stringResource(R.string.utran)
        5 -> stringResource(R.string.gadziny)
        6 -> stringResource(R.string.liturgia)
        else -> ""
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = slujvaTitle.uppercase(), modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onTertiary)
                        .padding(10.dp), fontSize = Settings.fontInterface.sp, color = MaterialTheme.colorScheme.onSecondary
                )
                HorizontalDivider()
                for (i in list.indices) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clickable {
                                navigateToBogaslujbovyia(list[i].title, list[i].resource)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(5.dp, 5.dp),
                            painter = painterResource(R.drawable.poiter),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            text = list[i].title,
                            modifier = Modifier
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = Settings.fontInterface.sp
                        )
                    }
                    HorizontalDivider()
                }
                TextButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.End),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(modifier = Modifier.padding(end = 5.dp), painter = painterResource(R.drawable.close), contentDescription = "")
                    Text(stringResource(R.string.cansel), fontSize = 18.sp)
                }
            }
        }
    }
}