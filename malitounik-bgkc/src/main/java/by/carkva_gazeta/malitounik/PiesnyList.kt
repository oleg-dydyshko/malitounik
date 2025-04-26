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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.views.AllDestinations
import by.carkva_gazeta.malitounik.views.AppNavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.Collator
import java.util.Locale

class FilterPiesnyListModel : ViewModel() {
    private val items = SnapshotStateList<PiesnyListItem>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: MutableStateFlow<SnapshotStateList<PiesnyListItem>> = _filteredItems

    fun clear() {
        items.clear()
    }

    fun sortWith() {
        items.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }

    fun addAllItemList(item: SnapshotStateList<PiesnyListItem>) {
        items.addAll(item)
    }

    fun filterItem(search: String) {
        if (search.isNotEmpty()) {
            _filteredItems.value = items.filter {
                it.title.contains(search, ignoreCase = true)
            } as SnapshotStateList<PiesnyListItem>
        }
    }
}

@Composable
fun PiesnyList(navController: NavHostController, piesny: String, innerPadding: PaddingValues, searchText: Boolean, search: String) {
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    val viewModel: FilterPiesnyListModel = viewModel()
    val piesnyBagarList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyBelarusList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyKaliadyList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyPraslList = remember { SnapshotStateList<PiesnyListItem>() }
    val piesnyTaizeList = remember { SnapshotStateList<PiesnyListItem>() }
    LaunchedEffect(Unit) {
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_aniol_ad_boha_habryel, "Анёл ад Бога Габрыэль"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_z_dalokaj_facimy, "З далёкай Фацімы"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_lurdauskaja_piesnia, "Люрдаўская песьня"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_mataczka_bozaja, "Матачка Божая"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_maci_bozaja_budslauskaja, "Маці Божая Будслаўская"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_maci_bozaja_u_zyrovicach, "Маці Божая ў Жыровіцах"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_maci_z_facimy, "Маці з Фацімы"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_maci_maja_bozaja, "Маці мая Божая"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_mnie_adnojczy, "Мне аднойчы"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_o_maryja_maci_boha, "О Марыя, Маці Бога (1)"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_o_maryja_maci_boha_2, "О Марыя, Маці Бога (2)"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_pamalisia_maryja, "Памаліся, Марыя"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_da_maci_bozaj_niastomnaj_dapamohi, "Песьня да Маці Божай Нястомнай Дапамогі"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_radujsia_maryja, "Радуйся, Марыя!"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_tabie_maryja_daviaraju_ja, "Табе, Марыя, давяраю я"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_cichaja_pakornaja, "Ціхая, пакорная"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_baharodz_zastupnica_duchounaja, "Заступніца духоўная"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_lubic_maryja_mianie, "Любіць Марыя мяне"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_radujsia_dzieva_maci, "Радуйся, Дзева Маці"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_maci_bozaja_u_barunach, "Маці Божая ў Барунах"
            )
        )
        piesnyBagarList.add(PiesnyListItem(2, R.raw.piesni_bahar_maci_bozaja, "Маці Божая"))
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_himn_da_imia_maryi, "Гімн да імя Марыі"
            )
        )
        piesnyBagarList.add(
            PiesnyListItem(
                2, R.raw.piesni_bahar_karaleva_supakoju, "Каралева супакою"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_ave_maria_pazniak, "Ave Maria"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_boza_szto_kalis_narody, "Божа, што калісь народы"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_boza_ja_malusia_za_bielarus, "Божа, я малюся за Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_vieczna_zyvi_maja_bielarus, "Вечна жывi, мая Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_k_tabie_bielarus, "К табе, Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_mahutny_boza, "Магутны Божа"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_malusia_za_ciabie_bielarus, "Малюся за цябе, Беларусь"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_malitva_piesniary, "Малітва («Песьняры»)"
            )
        )
        piesnyBelarusList.add(PiesnyListItem(1, R.raw.piesni_belarus_maja_kraina, "Мая краіна"))
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_my_choczam_boha, "Мы хочам Бога"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_malitva_buraukin, "Малітва (Г. Бураўкін)"
            )
        )
        piesnyBelarusList.add(
            PiesnyListItem(
                1, R.raw.piesni_belarus_viarszynia_bielarusi_kryz, "Вяршыня Беларусі – крыж"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_zorka_zazziala_avemaria, "Ave Maria (Зорка зазьзяла)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_a_szto_heta_za_spievy, "А што гэта за сьпевы"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_a_u_sviecie_nam_navina_byla, "А ў сьвеце нам навіна была"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_a_uczora_z_viaczora, "А ўчора з вячора"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_viasiolych_kaladnych_sviatau, "Вясёлых калядных сьвятаў"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_zazziala_zoraczka_nad_betlejemam, "Зазьзяла зорачка над Бэтлеемам"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_zvon_zvinic, "Звон зьвініць"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_na_szlachu_u_betlejem, "На шляху ў Бэтлеем"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_nieba_i_ziamla, "Неба і зямля"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_nova_radasc_stala, "Нова радасьць стала"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_noczka_cichaja_zarysta, "Ночка цiхая, зарыста"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_noczczu_sviatoj, "Ноччу сьвятой"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_pakazalas_z_nieba_jasnasc, "Паказалась з неба яснасьць"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_pryjdzicie_da_zbaucy, "Прыйдзіце да Збаўцы"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_radasnaja_viestka, "Радасная вестка"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_u_nacznuju_cisz, "У начную ціш"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_uczora_zviaczora_zasviacila_zora, "Учора зьвячора — засьвяціла зора"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_cichaja_nocz_arsiennieva, "Ціхая ноч (пер.Н.Арсеньневай)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_cichaja_nocz_dziunaja_nocz, "Ціхая ноч, дзіўная ноч"
            )
        )
        piesnyKaliadyList.add(PiesnyListItem(3, R.raw.piesni_kalady_cichaja_nocz, "Ціхая ноч"))
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_pryjdzi_pryjdzi_emanuel_19st, "Прыйдзі, прыйдзі, Эмануэль (ХІХ ст.)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_pryjdzi_pryjdzi_emanuel, "Прыйдзі, прыйдзі, Эмануэль (XII–ХVIII стст.)"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_pierszaje_rastvo, "Першае Раство"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_zorka_betlejemska, "Зорка Бэтлеемска"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_piesnia_pra_bozaje_naradzennie_boh_prychodzic, "Песьня пра Божае нараджэньне"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_promien_zorki_z_nieba_liusia, "Промень зоркі з неба ліўся"
            )
        )
        piesnyKaliadyList.add(PiesnyListItem(3, R.raw.piesni_kalady_u_betlejem, "У Бэтлеем"))
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_o_zyvatvorny_tvorca_zor, "O, жыватворны Творца зор"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_u_horadzie_cara_davida, "У горадзе цара Давіда"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_zbauca_svietu, "Збаўца сьвету"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_ci_znala_ty_maryja, "Ці знала Ты, Марыя?"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_najpiersz_pra_rastvo_first_noel, "Найперш пра Раство"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_maryja_ci_ty_viedala, "Марыя, ці Ты ведала?"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_pryjdzicie_paklanicca_adeste_fideles, "Прыйдзіце пакланіцца"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_nam_isus_naradziusia, "Нам Ісус нарадзіўся"
            )
        )
        piesnyKaliadyList.add(PiesnyListItem(3, R.raw.piesni_kalady_na_rastvo, "На Раство"))
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_dobry_vieczar, "Добры вечар"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_dzin_dzilin, "Дзінь-дзілінь"
            )
        )
        piesnyKaliadyList.add(
            PiesnyListItem(
                3, R.raw.piesni_kalady_szto_heta_za_dziva, "Што гэта за дзіва"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_0, "Ён паўсюль"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_1, "Ісус вызваліў мяне"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_2, "Ісус нам дае збаўленьне"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_3, "Айцец наш і наш Валадар"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_aliluja_chvalecie_z_nieba, "Алілуя! (Хвалеце з неба...)"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_5, "Бог блаславіў гэты дзень"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_6, "Бог ёсьць любоў"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_7, "Богу сьпявай, уся зямля!"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_8, "Божа мой"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_9, "Браце мой, мы знайшлі Месію"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_10, "Весяліся і пляскай у далоні"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_11, "Вольная воля"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_12, "Вось маё сэрца"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_13, "Вядзі мяне, Божа"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_14, "Вялікім і цудоўным"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_15, "Госпад мой заўсёды па маёй правіцы"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_16, "Госпаду дзякуйце, бо добры Ён"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_17, "Дай Духа любові"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_18, "Дай уславіць Цябе"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_19, "Дай, добры Божа"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_20, "Дакраніся да маіх вачэй"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_21, "Дзякуй за ўсё, што Ты стварыў"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_22, "Дзякуй !"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_23, "З намі — Пятро і Андрэй"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_24, "Знайдзі мяне"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_25, "Зоркі далёка"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_26, "Кадош (Сьвяты)"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_27, "Клічаш ты"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_28, "Любоў Твая"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_29, "Любіць — гэта ахвяраваць"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_30, "Майго жыцьця — мой Бог крыніца"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_31, "Маё сэрца"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_32, "Маё шчасьце ў Iсуса"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_33, "На псалтыры і на арфе"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_34, "Настане дзень"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_35, "Невычэрпныя ласкі ў Бога"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_36, "О, калі б ты паслухаў Мяне"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_37, "Ойча мой, к Табе іду"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_38, "Ойча, мяне Ты любіш"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_39, "Пакліканьне (Човен)"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_40, "Пачуй мой кліч, чулы Ойча"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_41, "Песьню славы засьпявайма"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_42, "Песьня Давіда"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_43, "Песьня вячэрняя"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_44, "Песьня пілігрыма"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_45, "Песьня ранішняя"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_46, "Пяцёра пакутнікаў"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_47, "Пілігрым"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_48, "Руах"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_49, "Сьвятло жыцьця"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_50, "Сьпявайма добраму Богу"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_51, "Сьпявайце Цару"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_52, "Так, як імкнецца сарна"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_53, "Твая любоў"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_54, "Твая прысутнасьць"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_55, "Толькі Ісус"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_56, "Толькі Бог, толькі ты"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_57, "Толькі Бог"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_58, "Ты ведаеш сэрца маё"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_59, "Ты ведаеш..."))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_60, "Ты — Госпад мой"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_chvala_tabie_vialiki_boh, "Хвала Табе, вялікі Бог"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_62, "Хвалім Цябе, Божа!"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_63, "Хрыстос уваскрос! (Resucito)"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_65, "Шалом алэхем (Мір вам)"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.pesny_prasl_66, "Я люблю Цябе, Ойча міласэрны"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_ja_ustanu_rana_kab_spiavac, "Я ўстану рана, каб сьпяваць"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_68, "Як гэта хораша й міла"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_jamu_za_usio_slava, "Яму за ўсё слава"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_70, "Цябе, Бога, хвалім"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_71, "Мой Госпад, мой Збаўца"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_72, "Крыжовы шлях"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_73, "Аднаві маю надзею"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_74, "Праслаўляйце Пана Бога"))
        piesnyPraslList.add(PiesnyListItem(0, R.raw.pesny_prasl_75, "Радуйся і слаў Яго"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_aliluja_leanard_koen_zanna_hauryczenkava, "Алілуя (Я чуў таемны той акорд...)"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.piesni_prasl_bo_lublu, "Бо люблю"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_prad_taboju_moj_boh, "Прад табою, мой Бог"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_chrystos_uvaskros, "Хрыстос уваскрос"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_aliluja_leanard_koen_maryja_trapaszka, "Алілуя (О Божа, памажы нам жыць...)"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_aliluja_leanard_koen_eduard_akulin, "Алілуя (Сярод нягод і сумных кроз...)"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_pachvala_sviatomu_jazafatu, "Пахвала сьвятому Язафату"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_nie_cviki_ciabie_trymali, "Не цьвікі Цябе трымалі, а мой грэх"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_kryzu_chrystovy, "Крыжу Хрыстовы"
            )
        )
        piesnyPraslList.add(PiesnyListItem(0, R.raw.piesni_prasl_tvoj_chram, "Твой хpам"))
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_za_usio_tabie_ja_dziakuju, "За ўсё Табе я дзякую"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_himn_na_uszanavannie_sviatych, "Гімн на ўшанаваньне сьвятых"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_addac_z_lubovi_usio, "Аддаць з любові ўсё"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_sionniaszni_dzien, "Сёньняшні дзень"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_ci_ty_byu_na_halhofie, "Ці ты быў на Галгофе"
            )
        )
        piesnyPraslList.add(
            PiesnyListItem(
                0, R.raw.piesni_prasl_dzien_za_dniom, "Дзень за днём"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_0, "Magnifikat"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_1, "Ostende nobis"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_2, "Ubi caritas"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_3, "Блаславёны Бог"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, R.raw.pesny_taize_4, "Бог мой, Iсус, сьвяцi нам у цемры"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_5, "Будзь са Мной"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, R.raw.pesny_taize_6, "Дай нам, Божа, моц ласкi Сваёй"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_7, "Дзякуем Табе, Божа наш"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_8, "Дзякуем Табе, Хрысьце"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, R.raw.pesny_taize_9, "Кожны дзень Бог дае мне сiлы"
            )
        )
        piesnyTaizeList.add(
            PiesnyListItem(
                4, R.raw.pesny_taize_10, "Мая душа ў Богу мае спакой"
            )
        )
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_11, "О, Iсусе"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_12, "О, Госпадзе мой"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_13, "Прыйдзi, Дух Сьвяты"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_14, "У цемры iдзём"))
        piesnyTaizeList.add(PiesnyListItem(4, R.raw.pesny_taize_15, "У цемры нашых дзён"))
        piesnyTaizeList.add(
            PiesnyListItem(
                4, R.raw.pesny_taize_16, "Хай тваё сэрца больш не журыцца"
            )
        )
        piesnyPraslList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyBelarusList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyBagarList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyKaliadyList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
        piesnyTaizeList.sortWith(compareBy(Collator.getInstance(Locale("be", "BE"))) { it.title })
    }
    if (searchText) {
        viewModel.clear()
        viewModel.addAllItemList(piesnyPraslList)
        viewModel.addAllItemList(piesnyBelarusList)
        viewModel.addAllItemList(piesnyBagarList)
        viewModel.addAllItemList(piesnyKaliadyList)
        viewModel.addAllItemList(piesnyTaizeList)
        viewModel.sortWith()
    }
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    viewModel.filterItem(search)
    Column {
        if (searchText) {
            PiesnyList(filteredItems, navigationActions, innerPadding)
        } else {
            val piesnyList = when (piesny) {
                AllDestinations.PIESNY_PRASLAULENNIA -> piesnyPraslList
                AllDestinations.PIESNY_ZA_BELARUS -> piesnyBelarusList
                AllDestinations.PIESNY_DA_BAGARODZICY -> piesnyBagarList
                AllDestinations.PIESNY_KALIADNYIA -> piesnyKaliadyList
                AllDestinations.PIESNY_TAIZE -> piesnyTaizeList
                else -> piesnyPraslList
            }
            PiesnyList(piesnyList, navigationActions, innerPadding)
        }
    }
}

@Composable
fun PiesnyList(piesnyList: SnapshotStateList<PiesnyListItem>, navigationActions: AppNavigationActions, innerPadding: PaddingValues) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset, source: NestedScrollSource
            ): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }
    LazyColumn(Modifier.nestedScroll(nestedScrollConnection)) {
        items(
            piesnyList.size, key = { index -> piesnyList[index].title + index }) { index ->
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            navigationActions.navigateToBogaslujbovyia(
                                piesnyList[index].title, piesnyList[index].resurs
                            )
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(5.dp, 5.dp), painter = painterResource(R.drawable.poiter), tint = MaterialTheme.colorScheme.primary, contentDescription = null
                    )
                    Text(
                        text = piesnyList[index].title, modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), color = MaterialTheme.colorScheme.secondary, fontSize = Settings.fontInterface.sp
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            Spacer(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
        }
    }
}

data class PiesnyListItem(val rubrika: Int, val resurs: Int, val title: String)
