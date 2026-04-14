@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.views.AllDestinations
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import java.text.Collator
import java.util.Locale

@Composable
fun PiesnyList(navController: NavHostController, piesny: String, innerPadding: PaddingValues, viewModel: SearchBibleViewModel) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val filteredItems = remember { mutableStateListOf<PiesnyListItem>() }
    val piesnyAll = remember { mutableStateListOf<PiesnyListItem>() }
    LaunchedEffect(Unit) {
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_aniol_ad_boha_habryel.html", "Анёл ад Бога Габрыэль"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_z_dalokaj_facimy.html", "З далёкай Фацімы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_lurdauskaja_piesnia.html", "Люрдаўская песьня"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_mataczka_bozaja.html", "Матачка Божая"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_maci_bozaja_budslauskaja.html", "Маці Божая Будслаўская"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_maci_bozaja_u_zyrovicach.html", "Маці Божая ў Жыравічах", "https://youtu.be/pTeCTDXQ2aI?si=LuhGfZBBf6if58Cm"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_maci_z_facimy.html", "Маці з Фацімы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_maci_maja_bozaja.html", "Маці мая Божая"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_mnie_adnojczy.html", "Мне аднойчы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_o_maryja_maci_boha.html", "О Марыя, Маці Бога (1)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_o_maryja_maci_boha_2.html", "О Марыя, Маці Бога (2)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_pamalisia_maryja.html", "Памаліся, Марыя"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_da_maci_bozaj_niastomnaj_dapamohi.html", "Песьня да Маці Божай Нястомнай Дапамогі"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_radujsia_maryja.html", "Радуйся, Марыя!"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_tabie_maryja_daviaraju_ja.html", "Табе, Марыя, давяраю я", "https://youtu.be/S-3IF8iRNe8?si=Gyslwe8N0uVa2KS1"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_cichaja_pakornaja.html", "Ціхая, пакорная", "https://music.youtube.com/watch?v=Bz3HIVbpGaA&si=Jx1oqNw1QnNO9LZp"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_baharodz_zastupnica_duchounaja.html", "Заступніца духоўная"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_lubic_maryja_mianie.html", "Любіць Марыя мяне"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_radujsia_dzieva_maci.html", "Радуйся, Дзева Маці", "https://youtu.be/vNm2klNHRSA"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_maci_bozaja_u_barunach.html", "Маці Божая ў Барунах", "https://youtu.be/D7oDCyxukkg"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_maci_bozaja.html", "Маці Божая", "https://holychords.pro/14849"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_himn_da_imia_maryi.html", "Гімн да Імя Марыі", "https://youtu.be/WARyZXOYd9E"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_DA_BAGARODZICY, "pesny/piesni_bahar_karaleva_supakoju.html", "Каралева супакою", "https://youtu.be/PGF5R7b2W4I?si=_N-RTIwitassHtzu"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_ave_maria_pazniak.html", "Ave Maria"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_boza_szto_kalis_narody.html", "Божа, што калісь народы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_boza_ja_malusia_za_bielarus.html", "Божа, я малюся за Беларусь"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_vieczna_zyvi_maja_bielarus.html", "Вечна жывi, мая Беларусь"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_k_tabie_bielarus.html", "К табе, Беларусь"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_mahutny_boza.html", "Магутны Божа"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_malusia_za_ciabie_bielarus.html", "Малюся за цябе, Беларусь"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_malitva_piesniary.html", "Малітва («Песьняры»)"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_maja_kraina.html", "Мая краіна"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_my_choczam_boha.html", "Мы хочам Бога"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_malitva_buraukin.html", "Малітва (Г. Бураўкін)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_ZA_BELARUS, "pesny/piesni_belarus_viarszynia_bielarusi_kryz.html", "Вяршыня Беларусі – крыж", "https://youtu.be/uQOxxziVuI8"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_zorka_zazziala_avemaria.html", "Ave Maria (Зорка зазьзяла)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_a_szto_heta_za_spievy.html", "А што гэта за сьпевы", "https://youtu.be/v629_KFOA3k"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_a_u_sviecie_nam_navina_byla.html", "А ў сьвеце нам навіна была"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_a_uczora_z_viaczora.html", "А ўчора з вячора"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_viasiolych_kaladnych_sviatau.html", "Вясёлых калядных сьвятаў"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_zazziala_zoraczka_nad_betlejemam.html", "Зазьзяла зорачка над Бэтлеемам"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_zvon_zvinic.html", "Звон зьвініць"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_na_szlachu_u_betlejem.html", "На шляху ў Бэтлеем"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_nieba_i_ziamla.html", "Неба і зямля"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_nova_radasc_stala.html", "Нова радасьць стала", "https://youtu.be/hD2jZv6mA-s?si=W6GueJ_kVMD2mTuj"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_noczka_cichaja_zarysta.html", "Ночка цiхая, зарыста"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_noczczu_sviatoj.html", "Ноччу сьвятой"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_pakazalas_z_nieba_jasnasc.html", "Паказалась з неба яснасьць"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_pryjdzicie_da_zbaucy.html", "Прыйдзіце да Збаўцы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_radasnaja_viestka.html", "Радасная вестка", "https://music.youtube.com/watch?v=o3l1QAr86VU&si=x1xwT9d2Mr9Hrrdr"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_u_nacznuju_cisz.html", "У начную ціш"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_uczora_zviaczora_zasviacila_zora.html", "Учора зьвячора — засьвяціла зора"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_cichaja_nocz_arsiennieva.html", "Ціхая ноч (пер.Н.Арсеньневай)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_cichaja_nocz_dziunaja_nocz.html", "Ціхая ноч, дзіўная ноч"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_cichaja_nocz.html", "Ціхая ноч"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_pryjdzi_pryjdzi_emanuel_19st.html", "Прыйдзі, прыйдзі, Эмануэль (ХІХ ст.)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_pryjdzi_pryjdzi_emanuel.html", "Прыйдзі, прыйдзі, Эмануэль (XII–ХVIII стст.)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_pierszaje_rastvo.html", "Першае Раство"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_zorka_betlejemska.html", "Зорка Бэтлеемска", "https://youtu.be/tT1LjXxyegY"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_piesnia_pra_bozaje_naradzennie_boh_prychodzic.html", "Песьня пра Божае нараджэньне", "https://youtu.be/hhwG9cj0BMk"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_promien_zorki_z_nieba_liusia.html", "Промень зоркі з неба ліўся"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_u_betlejem.html", "У Бэтлеем"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_o_zyvatvorny_tvorca_zor.html", "О, жыватворны Творца зор"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_u_horadzie_cara_davida.html", "У горадзе цара Давіда", "https://youtu.be/c6hgVc1aIsY?si=OyPxN3bEI7xqv9_2"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_zbauca_svietu.html", "Збаўца сьвету"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_ci_znala_ty_maryja.html", "Ці знала Ты, Марыя?", "https://youtu.be/w175jYTxw9w"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_najpiersz_pra_rastvo_first_noel.html", "Найперш пра Раство"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_maryja_ci_ty_viedala.html", "Марыя, ці Ты ведала?", "https://youtu.be/Lt3c9nEBwAE"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_pryjdzicie_paklanicca_adeste_fideles.html", "Прыйдзіце пакланіцца"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_nam_isus_naradziusia.html", "Нам Ісус нарадзіўся", "https://youtu.be/w0X6FFXgLX"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_na_rastvo.html", "На Раство", "https://holychords.pro/44077"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_dobry_vieczar.html", "Добры вечар", "https://youtu.be/LBB3ifI_qwM"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_dzin_dzilin.html", "Дзінь-дзілінь", "https://youtu.be/_CuIYfvWdT8"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_szto_heta_za_dziva.html", "Што гэта за дзіва", "https://www.facebook.com/share/v/19kpsDyNrq/"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_ziamla_i_nieba.html", "Зямля і неба"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_na_kalady.html", "На Каляды"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_u_betlejem_spiaszajma.html", "У Бэтлеем сьпяшайма", "https://youtu.be/uedBFlQIwUA?si=J2l8uQUINkFwsx-O"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_oj_u_betlejeme_viasiola_navina.html", "Ой, у Бэтлееме Вясёла навіна", "https://youtu.be/BrWCHu6M3UE?si=-GNPmcy7phr3lKVE"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_KALIADNYIA, "pesny/piesni_kalady_sviataja_nocz.html", "Сьвятая ноч", "https://holychords.pro/78687"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_0.html", "Ён паўсюль"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_2.html", "Ісус нам дае збаўленьне"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_3.html", "Айцец наш і наш Валадар"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_aliluja_chvalecie_z_nieba.html", "Алілуя! (Хвалеце з неба...)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_5.html", "Бог блаславіў гэты дзень"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_6.html", "Бог ёсьць любоў"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_7.html", "Богу сьпявай, уся зямля!"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_8.html", "Божа мой"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_9.html", "Браце мой, мы знайшлі Месію"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_10.html", "Весяліся і пляскай у далоні"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_11.html", "Вольная воля"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_12.html", "Вось маё сэрца"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_13.html", "Вядзі мяне, Божа"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_14.html", "Вялікім і цудоўным"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_15.html", "Госпад мой заўсёды па маёй правіцы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_16.html", "Госпаду дзякуйце, бо добры Ён"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_17.html", "Дай Духа любові"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_18.html", "Дай уславіць Цябе"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_19.html", "Дай, добры Божа"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_20.html", "Дакраніся да маіх вачэй"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_21.html", "Дзякуй за ўсё, што Ты стварыў"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_22.html", "Дзякуй !"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_23.html", "З намі — Пятро і Андрэй"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_24.html", "Знайдзі мяне"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_25.html", "Зоркі далёка"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_26.html", "Кадош (Сьвяты)"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_27.html", "Клічаш ты"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_28.html", "Любоў Твая"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_29.html", "Любіць — гэта ахвяраваць"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_30.html", "Майго жыцьця — мой Бог крыніца"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_31.html", "Маё сэрца"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_32.html", "Маё шчасьце ў Iсуса"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_33.html", "На псалтыры і на арфе"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_34.html", "Настане дзень"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_35.html", "Невычэрпныя ласкі ў Бога"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_36.html", "О, калі б ты паслухаў Мяне"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_37.html", "Ойча мой, к Табе іду"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_38.html", "Ойча, мяне Ты любіш"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_39.html", "Пакліканьне (Човен)"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_40.html", "Пачуй мой кліч, чулы Ойча"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_41.html", "Песьню славы засьпявайма"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_42.html", "Песьня Давіда"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_43.html", "Песьня вячэрняя"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_44.html", "Песьня пілігрыма"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_45.html", "Песьня ранішняя"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_46.html", "Пяцёра пакутнікаў"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_47.html", "Пілігрым"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_48.html", "Руах"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_49.html", "Сьвятло жыцьця"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_50.html", "Сьпявайма добраму Богу"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_51.html", "Сьпявайце Цару"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_52.html", "Так, як імкнецца сарна"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_53.html", "Твая любоў"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_54.html", "Твая прысутнасьць", "https://holychords.pro/32034"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_55.html", "Толькі Ісус"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_56.html", "Толькі Бог, толькі ты"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_57.html", "Толькі Бог"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_58.html", "Ты ведаеш сэрца маё"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_59.html", "Ты ведаеш..."))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_60.html", "Ты — Госпад мой"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_chvala_tabie_vialiki_boh.html", "Хвала Табе, вялікі Бог", "https://youtu.be/yn6fGE5CoXA"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_62.html", "Хвалім Цябе, Божа!"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_63.html", "Хрыстос уваскрос! (Resucito)"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_65.html", "Шалом алэхем (Мір вам)"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_66.html", "Я люблю Цябе, Ойча міласэрны"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_ja_ustanu_rana_kab_spiavac.html", "Я ўстану рана, каб сьпяваць"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_68.html", "Як гэта хораша й міла"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_jamu_za_usio_slava.html", "Яму за ўсё слава"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_70.html", "Цябе, Бога, хвалім"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_71.html", "Мой Госпад, мой Збаўца"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_72.html", "Крыжовы шлях"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_73.html", "Аднаві маю надзею"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_74.html", "Праслаўляйце Пана Бога"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/pesny_prasl_75.html", "Радуйся і слаў Яго"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_aliluja_leanard_koen_zanna_hauryczenkava.html", "Алілуя (Я чуў таемны той акорд...)"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_bo_lublu.html", "Бо люблю", "https://youtu.be/MfsOcV72oCg?si=AsZDHiMLhNlQH_cc"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_prad_taboju_moj_boh.html", "Прад табою, мой Бог"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_chrystos_uvaskros.html", "Хрыстос уваскрос"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_aliluja_leanard_koen_maryja_trapaszka.html", "Алілуя (О Божа, памажы нам жыць...)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_aliluja_leanard_koen_eduard_akulin.html", "Алілуя (Сярод нягод і сумных кроз...)"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_pachvala_sviatomu_jazafatu.html", "Пахвала сьвятому Язафату", "https://youtu.be/RLaMmJ9idGE"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_nie_cviki_ciabie_trymali.html", "Не цьвікі Цябе трымалі, а мой грэх", "https://youtu.be/WoW7VGY-JTA"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_kryzu_chrystovy.html", "Крыжу Хрыстовы", "https://youtu.be/GfD6ga5iknM"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_tvoj_chram.html", "Твой хpам"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_za_usio_tabie_ja_dziakuju.html", "За ўсё Табе я дзякую", "https://youtu.be/Z6K1hCmVyNw"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_himn_na_uszanavannie_sviatych.html", "Гімн на ўшанаваньне сьвятых", "https://youtu.be/5FqOdrJwn64"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_addac_z_lubovi_usio.html", "Аддаць з любові ўсё", "https://youtu.be/n0AlgDH-fhc"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_sionniaszni_dzien.html", "Сёньняшні дзень", "https://youtu.be/WCtSAd5IySs"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_ci_ty_byu_na_halhofie.html", "Ці ты быў на Галгофе", "https://youtu.be/DQm5pfmY9IY"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_dzien_za_dniom.html", "Дзень за днём", "https://holychords.pro/21451"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_chaj_zychodzic_duch_tvoj.html", "Хай зыходзіць Дух Твой"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_ziamla_biazvodnaja_pustaja.html", "Зямля бязводная, пустая", "https://www.instagram.com/reel/DDEIC8OM0hC/?igsh=dzBhOWhyaWRha25s"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_kali_duch_haspodni_napauniaje_mianie.html", "Калі Дух Гасподні напаўняе мяне", "https://youtu.be/rzOHHVdcE98?sі=T4GEwk4boLAwvcPW"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_PRASLAULENNIA, "pesny/piesni_prasl_praslaulajcie_sluhi.html", "Праслаўляйце, слугі", "https://youtu.be/jAhVX1Y8gpE?si=UYWKNeRpaCYwsHs"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_VIALIKODNYIA, "pesny/piesni_vlkdzn_chrystos_uvaskros_u_radasci_ziamla.html", "Хрыстос уваскрос – у радасьці зямля", "https://youtu.be/xTwiYbIpcEc?si=YQPNbFylcO1JICQE"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_VIALIKODNYIA, "pesny/piesni_vlkdzn_chrystos_uvaskros_z_pamierlych.html", "Хрыстос уваскрос з памерлых", "https://youtu.be/o27K0rvZR70"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_VIALIKODNYIA, "pesny/piesni_vlkdzn_isus_vyzvaliu_mianie.html", "Ісус вызваліў мяне", "https://youtu.be/zP9toA8PYlc?si=wz3BIRdVStHDI-O7"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_VIALIKODNYIA, "pesny/piesni_vlkdzn_pryjszou_da_nas_vialikdzien.html", "Прыйшоў да нас Вялікдзень", "https://youtu.be/l1Tt5nGEHbs?si=qYMuu1JN1vouTEUE"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_VIALIKODNYIA, "pesny/piesni_vlkdzn_radujmasia_chryscijanie.html", "Радуймася, хрысьціяне", "https://youtu.be/zgHAN7-3Aj0"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_VIALIKODNYIA, "pesny/piesni_vlkdzn_vialikdzien.html", "Вялікдзень", "https://youtu.be/IL1xCy5rScc?si=WXfxqZ1XCSiSvT7B"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_0.html", "Magnifikat"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_1.html", "Ostende nobis"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_2.html", "Ubi caritas"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_3.html", "Блаславёны Бог"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_TAIZE, "pesny/pesny_taize_4.html", "Бог мой, Iсус, сьвяцi нам у цемры"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_5.html", "Будзь са Мной"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_TAIZE, "pesny/pesny_taize_6.html", "Дай нам, Божа, моц ласкi Сваёй"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_7.html", "Дзякуем Табе, Божа наш"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_8.html", "Дзякуем Табе, Хрысьце"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_TAIZE, "pesny/pesny_taize_9.html", "Кожны дзень Бог дае мне сiлы"
            )
        )
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_TAIZE, "pesny/pesny_taize_10.html", "Мая душа ў Богу мае спакой"
            )
        )
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_11.html", "О, Iсусе"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_12.html", "О, Госпадзе мой"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_13.html", "Прыйдзi, Дух Сьвяты"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_14.html", "У цемры iдзём"))
        piesnyAll.add(PiesnyListItem(Settings.PIESNY_TAIZE, "pesny/pesny_taize_15.html", "У цемры нашых дзён"))
        piesnyAll.add(
            PiesnyListItem(
                Settings.PIESNY_TAIZE, "pesny/pesny_taize_16.html", "Хай тваё сэрца больш не журыцца"
            )
        )
        piesnyAll.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    LaunchedEffect(viewModel.textFieldValueState.text, viewModel.searchText) {
        if (viewModel.searchText) {
            filteredItems.clear()
            val filter = piesnyAll.filter {
                it.title.contains(viewModel.textFieldValueState.text, ignoreCase = true)
            }
            filteredItems.addAll(filter)
            filteredItems.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        } else {
            filteredItems.clear()
            filteredItems.addAll(
                when (piesny) {
                    AllDestinations.PIESNY_PRASLAULENNIA -> piesnyAll.filter { it.rubrika == Settings.PIESNY_PRASLAULENNIA }
                    AllDestinations.PIESNY_ZA_BELARUS -> piesnyAll.filter { it.rubrika == Settings.PIESNY_ZA_BELARUS }
                    AllDestinations.PIESNY_DA_BAGARODZICY -> piesnyAll.filter { it.rubrika == Settings.PIESNY_DA_BAGARODZICY }
                    AllDestinations.PIESNY_KALIADNYIA -> piesnyAll.filter { it.rubrika == Settings.PIESNY_KALIADNYIA }
                    AllDestinations.PIESNY_TAIZE -> piesnyAll.filter { it.rubrika == Settings.PIESNY_TAIZE }
                    AllDestinations.PIESNY_VIALIKODNYIA -> piesnyAll.filter { it.rubrika == Settings.PIESNY_VIALIKODNYIA }
                    else -> piesnyAll.filter { it.rubrika == Settings.PIESNY_PRASLAULENNIA }
                }
            )
        }
    }
    val piesnyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset, source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                AppNavGraphState.setScrollValuePosition(piesny, piesnyListState.firstVisibleItemIndex, piesnyListState.firstVisibleItemScrollOffset)
                return super.onPreScroll(available, source)
            }
        }
    }
    LaunchedEffect(Unit) {
        piesnyListState.scrollToItem(AppNavGraphState.getScrollValuePosition(piesny), AppNavGraphState.getScrollValueOffset(piesny))
    }
    LazyColumn(Modifier.nestedScroll(nestedScrollConnection), state = piesnyListState) {
        items(
            filteredItems.size, key = { index -> filteredItems[index].title + index }) { index ->
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            Settings.vibrate()
                            navigationActions.navigateToBogaslujbovyia(
                                filteredItems[index].title, filteredItems[index].resurs
                            )
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                    )
                    Text(
                        text = filteredItems[index].title, modifier = Modifier
                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                    if (filteredItems[index].url.isNotEmpty()) {
                        val context = LocalContext.current
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 20.dp)
                                    .align(Alignment.End)
                                    .clickable {
                                        Settings.vibrate()
                                        val uri = filteredItems[index].url.toUri()
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(intent)
                                    }, painter = painterResource(R.drawable.play_arrow), contentDescription = stringResource(R.string.play_pesny), tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}

data class PiesnyListItem(val rubrika: Int, val resurs: String, val title: String, val url: String = "")
