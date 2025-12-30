@file:Suppress("DEPRECATION")

package by.carkva_gazeta.malitounik

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.views.AllDestinations
import by.carkva_gazeta.malitounik.views.AppNavGraphState
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.launch
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
    val piesnyBagarList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyBelarusList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyKaliadyList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyPraslList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyTaizeList = remember { SnapshotStateList<PiesnyListItem>() }
    LaunchedEffect(Unit) {
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_aniol_ad_boha_habryel.html", "Анёл ад Бога Габрыэль"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_z_dalokaj_facimy.html", "З далёкай Фацімы"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_lurdauskaja_piesnia.html", "Люрдаўская песьня"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_mataczka_bozaja.html", "Матачка Божая"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_maci_bozaja_budslauskaja.html", "Маці Божая Будслаўская"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_maci_bozaja_u_zyrovicach.html", "Маці Божая ў Жыровіцах"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_maci_z_facimy.html", "Маці з Фацімы"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_maci_maja_bozaja.html", "Маці мая Божая"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_mnie_adnojczy.html", "Мне аднойчы"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_o_maryja_maci_boha.html", "О Марыя, Маці Бога (1)"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_o_maryja_maci_boha_2.html", "О Марыя, Маці Бога (2)"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_pamalisia_maryja.html", "Памаліся, Марыя"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_da_maci_bozaj_niastomnaj_dapamohi.html", "Песьня да Маці Божай Нястомнай Дапамогі"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_radujsia_maryja.html", "Радуйся, Марыя!"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_tabie_maryja_daviaraju_ja.html", "Табе, Марыя, давяраю я"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_cichaja_pakornaja.html", "Ціхая, пакорная"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_baharodz_zastupnica_duchounaja.html", "Заступніца духоўная"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_lubic_maryja_mianie.html", "Любіць Марыя мяне"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_radujsia_dzieva_maci.html", "Радуйся, Дзева Маці"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_maci_bozaja_u_barunach.html", "Маці Божая ў Барунах"
            )
        )
        piesnyBagarList.add(PiesnyListItem(2, "pesny/piesni_bahar_maci_bozaja.html", "Маці Божая"))
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_himn_da_imia_maryi.html", "Гімн да імя Марыі"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, "pesny/piesni_bahar_karaleva_supakoju.html", "Каралева супакою"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_ave_maria_pazniak.html", "Ave Maria"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_boza_szto_kalis_narody.html", "Божа, што калісь народы"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_boza_ja_malusia_za_bielarus.html", "Божа, я малюся за Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_vieczna_zyvi_maja_bielarus.html", "Вечна жывi, мая Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_k_tabie_bielarus.html", "К табе, Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_mahutny_boza.html", "Магутны Божа"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_malusia_za_ciabie_bielarus.html", "Малюся за цябе, Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_malitva_piesniary.html", "Малітва («Песьняры»)"
            )
        )
        piesnyBelarusList.add(PiesnyListItem(1, "pesny/piesni_belarus_maja_kraina.html", "Мая краіна"))
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_my_choczam_boha.html", "Мы хочам Бога"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_malitva_buraukin.html", "Малітва (Г. Бураўкін)"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, "pesny/piesni_belarus_viarszynia_bielarusi_kryz.html", "Вяршыня Беларусі – крыж"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_zorka_zazziala_avemaria.html", "Ave Maria (Зорка зазьзяла)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_a_szto_heta_za_spievy.html", "А што гэта за сьпевы"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_a_u_sviecie_nam_navina_byla.html", "А ў сьвеце нам навіна была"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_a_uczora_z_viaczora.html", "А ўчора з вячора"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_viasiolych_kaladnych_sviatau.html", "Вясёлых калядных сьвятаў"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_zazziala_zoraczka_nad_betlejemam.html", "Зазьзяла зорачка над Бэтлеемам"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_zvon_zvinic.html", "Звон зьвініць"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_na_szlachu_u_betlejem.html", "На шляху ў Бэтлеем"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_nieba_i_ziamla.html", "Неба і зямля"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_nova_radasc_stala.html", "Нова радасьць стала"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_noczka_cichaja_zarysta.html", "Ночка цiхая, зарыста"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_noczczu_sviatoj.html", "Ноччу сьвятой"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_pakazalas_z_nieba_jasnasc.html", "Паказалась з неба яснасьць"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_pryjdzicie_da_zbaucy.html", "Прыйдзіце да Збаўцы"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_radasnaja_viestka.html", "Радасная вестка"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_u_nacznuju_cisz.html", "У начную ціш"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_uczora_zviaczora_zasviacila_zora.html", "Учора зьвячора — засьвяціла зора"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_cichaja_nocz_arsiennieva.html", "Ціхая ноч (пер.Н.Арсеньневай)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_cichaja_nocz_dziunaja_nocz.html", "Ціхая ноч, дзіўная ноч"
            )
        )
        piesnyKaliadyList.add(PiesnyListItem(3, "pesny/piesni_kalady_cichaja_nocz.html", "Ціхая ноч"))
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_pryjdzi_pryjdzi_emanuel_19st.html", "Прыйдзі, прыйдзі, Эмануэль (ХІХ ст.)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_pryjdzi_pryjdzi_emanuel.html", "Прыйдзі, прыйдзі, Эмануэль (XII–ХVIII стст.)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_pierszaje_rastvo.html", "Першае Раство"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_zorka_betlejemska.html", "Зорка Бэтлеемска"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_piesnia_pra_bozaje_naradzennie_boh_prychodzic.html", "Песьня пра Божае нараджэньне"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_promien_zorki_z_nieba_liusia.html", "Промень зоркі з неба ліўся"
            )
        )
        piesnyKaliadyList.add(PiesnyListItem(3, "pesny/piesni_kalady_u_betlejem.html", "У Бэтлеем"))
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_o_zyvatvorny_tvorca_zor.html", "О, жыватворны Творца зор"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_u_horadzie_cara_davida.html", "У горадзе цара Давіда"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_zbauca_svietu.html", "Збаўца сьвету"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_ci_znala_ty_maryja.html", "Ці знала Ты, Марыя?"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_najpiersz_pra_rastvo_first_noel.html", "Найперш пра Раство"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_maryja_ci_ty_viedala.html", "Марыя, ці Ты ведала?"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_pryjdzicie_paklanicca_adeste_fideles.html", "Прыйдзіце пакланіцца"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_nam_isus_naradziusia.html", "Нам Ісус нарадзіўся"
            )
        )
        piesnyKaliadyList.add(PiesnyListItem(3, "pesny/piesni_kalady_na_rastvo.html", "На Раство"))
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_dobry_vieczar.html", "Добры вечар"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_dzin_dzilin.html", "Дзінь-дзілінь"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_szto_heta_za_dziva.html", "Што гэта за дзіва"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_ziamla_i_nieba.html", "Зямля і неба"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, "pesny/piesni_kalady_na_kalady.html", "На Каляды"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_0.html", "Ён паўсюль"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_1.html", "Ісус вызваліў мяне"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_2.html", "Ісус нам дае збаўленьне"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_3.html", "Айцец наш і наш Валадар"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_aliluja_chvalecie_z_nieba.html", "Алілуя! (Хвалеце з неба...)"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_5.html", "Бог блаславіў гэты дзень"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_6.html", "Бог ёсьць любоў"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_7.html", "Богу сьпявай, уся зямля!"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_8.html", "Божа мой"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_9.html", "Браце мой, мы знайшлі Месію"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_10.html", "Весяліся і пляскай у далоні"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_11.html", "Вольная воля"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_12.html", "Вось маё сэрца"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_13.html", "Вядзі мяне, Божа"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_14.html", "Вялікім і цудоўным"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_15.html", "Госпад мой заўсёды па маёй правіцы"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_16.html", "Госпаду дзякуйце, бо добры Ён"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_17.html", "Дай Духа любові"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_18.html", "Дай уславіць Цябе"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_19.html", "Дай, добры Божа"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_20.html", "Дакраніся да маіх вачэй"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_21.html", "Дзякуй за ўсё, што Ты стварыў"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_22.html", "Дзякуй !"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_23.html", "З намі — Пятро і Андрэй"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_24.html", "Знайдзі мяне"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_25.html", "Зоркі далёка"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_26.html", "Кадош (Сьвяты)"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_27.html", "Клічаш ты"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_28.html", "Любоў Твая"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_29.html", "Любіць — гэта ахвяраваць"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_30.html", "Майго жыцьця — мой Бог крыніца"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_31.html", "Маё сэрца"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_32.html", "Маё шчасьце ў Iсуса"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_33.html", "На псалтыры і на арфе"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_34.html", "Настане дзень"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_35.html", "Невычэрпныя ласкі ў Бога"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_36.html", "О, калі б ты паслухаў Мяне"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_37.html", "Ойча мой, к Табе іду"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_38.html", "Ойча, мяне Ты любіш"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_39.html", "Пакліканьне (Човен)"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_40.html", "Пачуй мой кліч, чулы Ойча"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_41.html", "Песьню славы засьпявайма"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_42.html", "Песьня Давіда"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_43.html", "Песьня вячэрняя"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_44.html", "Песьня пілігрыма"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_45.html", "Песьня ранішняя"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_46.html", "Пяцёра пакутнікаў"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_47.html", "Пілігрым"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_48.html", "Руах"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_49.html", "Сьвятло жыцьця"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_50.html", "Сьпявайма добраму Богу"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_51.html", "Сьпявайце Цару"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_52.html", "Так, як імкнецца сарна"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_53.html", "Твая любоў"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_54.html", "Твая прысутнасьць"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_55.html", "Толькі Ісус"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_56.html", "Толькі Бог, толькі ты"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_57.html", "Толькі Бог"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_58.html", "Ты ведаеш сэрца маё"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_59.html", "Ты ведаеш..."))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_60.html", "Ты — Госпад мой"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_chvala_tabie_vialiki_boh.html", "Хвала Табе, вялікі Бог"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_62.html", "Хвалім Цябе, Божа!"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_63.html", "Хрыстос уваскрос! (Resucito)"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_65.html", "Шалом алэхем (Мір вам)"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/pesny_prasl_66.html", "Я люблю Цябе, Ойча міласэрны"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_ja_ustanu_rana_kab_spiavac.html", "Я ўстану рана, каб сьпяваць"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_68.html", "Як гэта хораша й міла"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_jamu_za_usio_slava.html", "Яму за ўсё слава"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_70.html", "Цябе, Бога, хвалім"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_71.html", "Мой Госпад, мой Збаўца"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_72.html", "Крыжовы шлях"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_73.html", "Аднаві маю надзею"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_74.html", "Праслаўляйце Пана Бога"))
        piesnyPraslList.add(PiesnyListItem(0, "pesny/pesny_prasl_75.html", "Радуйся і слаў Яго"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_aliluja_leanard_koen_zanna_hauryczenkava.html", "Алілуя (Я чуў таемны той акорд...)"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/piesni_prasl_bo_lublu.html", "Бо люблю"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_prad_taboju_moj_boh.html", "Прад табою, мой Бог"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_chrystos_uvaskros.html", "Хрыстос уваскрос"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_aliluja_leanard_koen_maryja_trapaszka.html", "Алілуя (О Божа, памажы нам жыць...)"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_aliluja_leanard_koen_eduard_akulin.html", "Алілуя (Сярод нягод і сумных кроз...)"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_pachvala_sviatomu_jazafatu.html", "Пахвала сьвятому Язафату"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_nie_cviki_ciabie_trymali.html", "Не цьвікі Цябе трымалі, а мой грэх"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_kryzu_chrystovy.html", "Крыжу Хрыстовы"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, "pesny/piesni_prasl_tvoj_chram.html", "Твой хpам"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_za_usio_tabie_ja_dziakuju.html", "За ўсё Табе я дзякую"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_himn_na_uszanavannie_sviatych.html", "Гімн на ўшанаваньне сьвятых"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_addac_z_lubovi_usio.html", "Аддаць з любові ўсё"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_sionniaszni_dzien.html", "Сёньняшні дзень"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_ci_ty_byu_na_halhofie.html", "Ці ты быў на Галгофе"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_dzien_za_dniom.html", "Дзень за днём"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_prasl_chaj_zychodzic_duch_tvoj.html", "Хай зыходзіць Дух Твой"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_vialik_chrystos_uvaskros_z_pamierlych.html", "Хрыстос уваскрос з памерлых"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, "pesny/piesni_vialik_radujmasia_chryscijanie.html", "Радуймася, хрысьціяне"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_0.html", "Magnifikat"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_1.html", "Ostende nobis"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_2.html", "Ubi caritas"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_3.html", "Блаславёны Бог"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, "pesny/pesny_taize_4.html", "Бог мой, Iсус, сьвяцi нам у цемры"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_5.html", "Будзь са Мной"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, "pesny/pesny_taize_6.html", "Дай нам, Божа, моц ласкi Сваёй"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_7.html", "Дзякуем Табе, Божа наш"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_8.html", "Дзякуем Табе, Хрысьце"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, "pesny/pesny_taize_9.html", "Кожны дзень Бог дае мне сiлы"
            )
        )
        piesnyTaizeList.add(
            PiesnyListItem(
                4, "pesny/pesny_taize_10.html", "Мая душа ў Богу мае спакой"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_11.html", "О, Iсусе"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_12.html", "О, Госпадзе мой"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_13.html", "Прыйдзi, Дух Сьвяты"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_14.html", "У цемры iдзём"))
        piesnyTaizeList.add(PiesnyListItem(4, "pesny/pesny_taize_15.html", "У цемры нашых дзён"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, "pesny/pesny_taize_16.html", "Хай тваё сэрца больш не журыцца"
            )
        )
        piesnyPraslList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyBelarusList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyBagarList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyKaliadyList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyTaizeList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyAll.addAll(piesnyPraslList)
        piesnyAll.addAll(piesnyBelarusList)
        piesnyAll.addAll(piesnyBagarList)
        piesnyAll.addAll(piesnyKaliadyList)
        piesnyAll.addAll(piesnyTaizeList)
    }
    LaunchedEffect(viewModel.searchText) {
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
                    AllDestinations.PIESNY_PRASLAULENNIA -> piesnyPraslList
                    AllDestinations.PIESNY_ZA_BELARUS -> piesnyBelarusList
                    AllDestinations.PIESNY_DA_BAGARODZICY -> piesnyBagarList
                    AllDestinations.PIESNY_KALIADNYIA -> piesnyKaliadyList
                    AllDestinations.PIESNY_TAIZE -> piesnyTaizeList
                    else -> piesnyPraslList
                }
            )
        }
    }
    val coroutineScope = rememberCoroutineScope()
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
        coroutineScope.launch {
            piesnyListState.scrollToItem(AppNavGraphState.getScrollValuePosition(piesny), AppNavGraphState.getScrollValueOffset(piesny))
        }
    }
    LazyColumn(Modifier.nestedScroll(nestedScrollConnection), state = piesnyListState) {
        items(
            filteredItems.size, key = { index -> filteredItems[index].title + index }) { index ->
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            navigationActions.navigateToBogaslujbovyia(
                                filteredItems[index].title, filteredItems[index].resurs
                            )
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = ""
                    )
                    Text(
                        text = filteredItems[index].title, modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding() + if (k.getBoolean("isInstallApp", false)) 60.dp else 0.dp))
        }
    }
}

data class PiesnyListItem(val rubrika: Int, val resurs: String, val title: String)
