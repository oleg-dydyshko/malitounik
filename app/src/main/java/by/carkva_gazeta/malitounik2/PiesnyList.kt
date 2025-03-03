package by.carkva_gazeta.malitounik2

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AppNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilterPiesnyListModel : ViewModel() {
    private val items = ArrayList<PiesnyListItem>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: StateFlow<ArrayList<PiesnyListItem>> = _filteredItems

    fun clear() {
        items.clear()
    }

    fun addItemList(item: PiesnyListItem) {
        items.add(item)
    }

    fun filterItem(rubrika: Int) {
        _filteredItems.value = items.filter { it.rubrika == rubrika } as ArrayList<PiesnyListItem>
        _filteredItems.value.sortBy {
            it.title
        }
    }
}

@Composable
fun PiesnyList(navController: NavHostController, innerPadding: PaddingValues) {
    val view = LocalView.current
    val k = LocalContext.current.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    val navigationActions = remember(navController) {
        AppNavigationActions(navController, k)
    }
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars = false
    }
    val viewModel: FilterPiesnyListModel = viewModel()
    var isInit by remember { mutableStateOf(true) }
    var rubrika by remember {
        mutableIntStateOf(
            k.getInt(
                "pubrikaPesnyMenu",
                0
            )
        )
    }
    if (isInit) {
        LaunchedEffect(Unit) {
            isInit = false
            viewModel.clear()
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_aniol_ad_boha_habryel,
                    "Анёл ад Бога Габрыэль"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_z_dalokaj_facimy,
                    "З далёкай Фацімы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_lurdauskaja_piesnia,
                    "Люрдаўская песьня"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_mataczka_bozaja,
                    "Матачка Божая"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_maci_bozaja_budslauskaja,
                    "Маці Божая Будслаўская"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_maci_bozaja_u_zyrovicach,
                    "Маці Божая ў Жыровіцах"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_maci_z_facimy,
                    "Маці з Фацімы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_maci_maja_bozaja,
                    "Маці мая Божая"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_mnie_adnojczy,
                    "Мне аднойчы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_o_maryja_maci_boha,
                    "О Марыя, Маці Бога (1)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_o_maryja_maci_boha_2,
                    "О Марыя, Маці Бога (2)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_pamalisia_maryja,
                    "Памаліся, Марыя"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_da_maci_bozaj_niastomnaj_dapamohi,
                    "Песьня да Маці Божай Нястомнай Дапамогі"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_radujsia_maryja,
                    "Радуйся, Марыя!"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_tabie_maryja_daviaraju_ja,
                    "Табе, Марыя, давяраю я"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_cichaja_pakornaja,
                    "Ціхая, пакорная"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_baharodz_zastupnica_duchounaja,
                    "Заступніца духоўная"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_lubic_maryja_mianie,
                    "Любіць Марыя мяне"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_radujsia_dzieva_maci,
                    "Радуйся, Дзева Маці"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_maci_bozaja_u_barunach,
                    "Маці Божая ў Барунах"
                )
            )
            viewModel.addItemList(PiesnyListItem(2, R.raw.piesni_bahar_maci_bozaja, "Маці Божая"))
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_himn_da_imia_maryi,
                    "Гімн да імя Марыі"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    2,
                    R.raw.piesni_bahar_karaleva_supakoju,
                    "Каралева супакою"
                )
            )

            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_ave_maria_pazniak,
                    "Ave Maria"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_boza_szto_kalis_narody,
                    "Божа, што калісь народы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_boza_ja_malusia_za_bielarus,
                    "Божа, я малюся за Беларусь"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_vieczna_zyvi_maja_bielarus,
                    "Вечна жывi, мая Беларусь"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_k_tabie_bielarus,
                    "К табе, Беларусь"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_mahutny_boza,
                    "Магутны Божа"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_malusia_za_ciabie_bielarus,
                    "Малюся за цябе, Беларусь"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_malitva_piesniary,
                    "Малітва («Песьняры»)"
                )
            )
            viewModel.addItemList(PiesnyListItem(1, R.raw.piesni_belarus_maja_kraina, "Мая краіна"))
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_my_choczam_boha,
                    "Мы хочам Бога"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_malitva_buraukin,
                    "Малітва (Г. Бураўкін)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    1,
                    R.raw.piesni_belarus_viarszynia_bielarusi_kryz,
                    "Вяршыня Беларусі – крыж"
                )
            )

            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_zorka_zazziala_avemaria,
                    "Ave Maria (Зорка зазьзяла)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_a_szto_heta_za_spievy,
                    "А што гэта за сьпевы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_a_u_sviecie_nam_navina_byla,
                    "А ў сьвеце нам навіна была"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_a_uczora_z_viaczora,
                    "А ўчора з вячора"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_viasiolych_kaladnych_sviatau,
                    "Вясёлых калядных сьвятаў"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_zazziala_zoraczka_nad_betlejemam,
                    "Зазьзяла зорачка над Бэтлеемам"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_zvon_zvinic,
                    "Звон зьвініць"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_na_szlachu_u_betlejem,
                    "На шляху ў Бэтлеем"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_nieba_i_ziamla,
                    "Неба і зямля"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_nova_radasc_stala,
                    "Нова радасьць стала"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_noczka_cichaja_zarysta,
                    "Ночка цiхая, зарыста"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_noczczu_sviatoj,
                    "Ноччу сьвятой"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_pakazalas_z_nieba_jasnasc,
                    "Паказалась з неба яснасьць"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_pryjdzicie_da_zbaucy,
                    "Прыйдзіце да Збаўцы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_radasnaja_viestka,
                    "Радасная вестка"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_u_nacznuju_cisz,
                    "У начную ціш"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_uczora_zviaczora_zasviacila_zora,
                    "Учора зьвячора — засьвяціла зора"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_cichaja_nocz_arsiennieva,
                    "Ціхая ноч (пер.Н.Арсеньневай)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_cichaja_nocz_dziunaja_nocz,
                    "Ціхая ноч, дзіўная ноч"
                )
            )
            viewModel.addItemList(PiesnyListItem(3, R.raw.piesni_kalady_cichaja_nocz, "Ціхая ноч"))
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_pryjdzi_pryjdzi_emanuel_19st,
                    "Прыйдзі, прыйдзі, Эмануэль (ХІХ ст.)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_pryjdzi_pryjdzi_emanuel,
                    "Прыйдзі, прыйдзі, Эмануэль (XII–ХVIII стст.)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_pierszaje_rastvo,
                    "Першае Раство"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_zorka_betlejemska,
                    "Зорка Бэтлеемска"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_piesnia_pra_bozaje_naradzennie_boh_prychodzic,
                    "Песьня пра Божае нараджэньне"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_promien_zorki_z_nieba_liusia,
                    "Промень зоркі з неба ліўся"
                )
            )
            viewModel.addItemList(PiesnyListItem(3, R.raw.piesni_kalady_u_betlejem, "У Бэтлеем"))
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_o_zyvatvorny_tvorca_zor,
                    "O, жыватворны Творца зор"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_u_horadzie_cara_davida,
                    "У горадзе цара Давіда"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_zbauca_svietu,
                    "Збаўца сьвету"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_ci_znala_ty_maryja,
                    "Ці знала Ты, Марыя?"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_najpiersz_pra_rastvo_first_noel,
                    "Найперш пра Раство"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_maryja_ci_ty_viedala,
                    "Марыя, ці Ты ведала?"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_pryjdzicie_paklanicca_adeste_fideles,
                    "Прыйдзіце пакланіцца"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_nam_isus_naradziusia,
                    "Нам Ісус нарадзіўся"
                )
            )
            viewModel.addItemList(PiesnyListItem(3, R.raw.piesni_kalady_na_rastvo, "На Раство"))
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_dobry_vieczar,
                    "Добры вечар"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_dzin_dzilin,
                    "Дзінь-дзілінь"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    3,
                    R.raw.piesni_kalady_szto_heta_za_dziva,
                    "Што гэта за дзіва"
                )
            )

            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_0, "Ён паўсюль"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_1, "Ісус вызваліў мяне"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_2, "Ісус нам дае збаўленьне"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_3, "Айцец наш і наш Валадар"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_aliluja_chvalecie_z_nieba,
                    "Алілуя! (Хвалеце з неба...)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_5,
                    "Бог блаславіў гэты дзень"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_6, "Бог ёсьць любоў"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_7,
                    "Богу сьпявай, уся зямля!"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_8, "Божа мой"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_9,
                    "Браце мой, мы знайшлі Месію"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_10,
                    "Весяліся і пляскай у далоні"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_11, "Вольная воля"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_12, "Вось маё сэрца"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_13, "Вядзі мяне, Божа"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_14, "Вялікім і цудоўным"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_15,
                    "Госпад мой заўсёды па маёй правіцы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_16,
                    "Госпаду дзякуйце, бо добры Ён"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_17, "Дай Духа любові"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_18, "Дай уславіць Цябе"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_19, "Дай, добры Божа"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_20,
                    "Дакраніся да маіх вачэй"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_21,
                    "Дзякуй за ўсё, што Ты стварыў"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_22, "Дзякуй !"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_23,
                    "З намі — Пятро і Андрэй"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_24, "Знайдзі мяне"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_25, "Зоркі далёка"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_26, "Кадош (Сьвяты)"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_27, "Клічаш ты"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_28, "Любоў Твая"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_29,
                    "Любіць — гэта ахвяраваць"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_30,
                    "Майго жыцьця — мой Бог крыніца"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_31, "Маё сэрца"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_32, "Маё шчасьце ў Iсуса"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_33, "На псалтыры і на арфе"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_34, "Настане дзень"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_35,
                    "Невычэрпныя ласкі ў Бога"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_36,
                    "О, калі б ты паслухаў Мяне"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_37, "Ойча мой, к Табе іду"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_38, "Ойча, мяне Ты любіш"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_39, "Пакліканьне (Човен)"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_40,
                    "Пачуй мой кліч, чулы Ойча"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_41,
                    "Песьню славы засьпявайма"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_42, "Песьня Давіда"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_43, "Песьня вячэрняя"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_44, "Песьня пілігрыма"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_45, "Песьня ранішняя"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_46, "Пяцёра пакутнікаў"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_47, "Пілігрым"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_48, "Руах"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_49, "Сьвятло жыцьця"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_50, "Сьпявайма добраму Богу"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_51, "Сьпявайце Цару"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_52, "Так, як імкнецца сарна"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_53, "Твая любоў"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_54, "Твая прысутнасьць"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_55, "Толькі Ісус"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_56, "Толькі Бог, толькі ты"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_57, "Толькі Бог"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_58, "Ты ведаеш сэрца маё"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_59, "Ты ведаеш..."))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_60, "Ты — Госпад мой"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_chvala_tabie_vialiki_boh,
                    "Хвала Табе, вялікі Бог"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_62, "Хвалім Цябе, Божа!"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_63,
                    "Хрыстос уваскрос! (Resucito)"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_65, "Шалом алэхем (Мір вам)"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.pesny_prasl_66,
                    "Я люблю Цябе, Ойча міласэрны"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_ja_ustanu_rana_kab_spiavac,
                    "Я ўстану рана, каб сьпяваць"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_68, "Як гэта хораша й міла"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_jamu_za_usio_slava,
                    "Яму за ўсё слава"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_70, "Цябе, Бога, хвалім"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_71, "Мой Госпад, мой Збаўца"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_72, "Крыжовы шлях"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_73, "Аднаві маю надзею"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_74, "Праслаўляйце Пана Бога"))
            viewModel.addItemList(PiesnyListItem(0, R.raw.pesny_prasl_75, "Радуйся і слаў Яго"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_aliluja_leanard_koen_zanna_hauryczenkava,
                    "Алілуя (Я чуў таемны той акорд...)"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.piesni_prasl_bo_lublu, "Бо люблю"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_prad_taboju_moj_boh,
                    "Прад табою, мой Бог"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_chrystos_uvaskros,
                    "Хрыстос уваскрос"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_aliluja_leanard_koen_maryja_trapaszka,
                    "Алілуя (О Божа, памажы нам жыць...)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_aliluja_leanard_koen_eduard_akulin,
                    "Алілуя (Сярод нягод і сумных кроз...)"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_pachvala_sviatomu_jazafatu,
                    "Пахвала сьвятому Язафату"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_nie_cviki_ciabie_trymali,
                    "Не цьвікі Цябе трымалі, а мой грэх"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_kryzu_chrystovy,
                    "Крыжу Хрыстовы"
                )
            )
            viewModel.addItemList(PiesnyListItem(0, R.raw.piesni_prasl_tvoj_chram, "Твой хpам"))
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_za_usio_tabie_ja_dziakuju,
                    "За ўсё Табе я дзякую"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_himn_na_uszanavannie_sviatych,
                    "Гімн на ўшанаваньне сьвятых"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_addac_z_lubovi_usio,
                    "Аддаць з любові ўсё"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_sionniaszni_dzien,
                    "Сёньняшні дзень"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_ci_ty_byu_na_halhofie,
                    "Ці ты быў на Галгофе"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    0,
                    R.raw.piesni_prasl_dzien_za_dniom,
                    "Дзень за днём"
                )
            )

            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_0, "Magnifikat"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_1, "Ostende nobis"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_2, "Ubi caritas"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_3, "Блаславёны Бог"))
            viewModel.addItemList(
                PiesnyListItem(
                    4,
                    R.raw.pesny_taize_4,
                    "Бог мой, Iсус, сьвяцi нам у цемры"
                )
            )
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_5, "Будзь са Мной"))
            viewModel.addItemList(
                PiesnyListItem(
                    4,
                    R.raw.pesny_taize_6,
                    "Дай нам, Божа, моц ласкi Сваёй"
                )
            )
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_7, "Дзякуем Табе, Божа наш"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_8, "Дзякуем Табе, Хрысьце"))
            viewModel.addItemList(
                PiesnyListItem(
                    4,
                    R.raw.pesny_taize_9,
                    "Кожны дзень Бог дае мне сiлы"
                )
            )
            viewModel.addItemList(
                PiesnyListItem(
                    4,
                    R.raw.pesny_taize_10,
                    "Мая душа ў Богу мае спакой"
                )
            )
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_11, "О, Iсусе"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_12, "О, Госпадзе мой"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_13, "Прыйдзi, Дух Сьвяты"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_14, "У цемры iдзём"))
            viewModel.addItemList(PiesnyListItem(4, R.raw.pesny_taize_15, "У цемры нашых дзён"))
            viewModel.addItemList(
                PiesnyListItem(
                    4,
                    R.raw.pesny_taize_16,
                    "Хай тваё сэрца больш не журыцца"
                )
            )
        }
    }
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    val lazyRowState = rememberLazyListState()
    val list = listOf(
        0,
        1,
        2,
        3,
        4
    )
    val selectState = remember(list) { list.map { false }.toMutableStateList() }
    LaunchedEffect(rubrika) {
        CoroutineScope(Dispatchers.Main).launch {
            selectState[rubrika] = true
            lazyRowState.scrollToItem(rubrika)
            viewModel.filterItem(rubrika)
        }
    }
    Column {
        LazyRow(state = lazyRowState, modifier = Modifier.fillMaxWidth()) {
            items(list.size) { index ->
                val title = when (index) {
                    0 -> stringResource(R.string.pesny1)
                    1 -> stringResource(R.string.pesny2)
                    2 -> stringResource(R.string.pesny3)
                    3 -> stringResource(R.string.pesny4)
                    4 -> stringResource(R.string.pesny5)
                    else -> stringResource(R.string.pesny1)
                }
                val modifier = if (index == 0) Modifier.padding(horizontal = 10.dp)
                else Modifier.padding(end = 10.dp)
                FilterChip(
                    modifier = modifier,
                    onClick = {
                        for (i in 0..4)
                            selectState[i] = false
                        selectState[index] = !selectState[index]
                        val edit = k.edit()
                        edit.putInt("pubrikaPesnyMenu", index)
                        rubrika = index
                        CoroutineScope(Dispatchers.Main).launch {
                            lazyRowState.scrollToItem(index)
                        }
                        edit.apply()
                    },
                    label = {
                        Text(title, fontSize = 18.sp)
                    },
                    selected = selectState[index],
                    leadingIcon = if (selectState[index]) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = "",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
        LazyColumn {
            items(
                filteredItems.size,
                key = { index -> filteredItems[index].title + filteredItems[index].rubrika }) { index ->
                Column {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                navigationActions.navigateToBogaslujbovyia(
                                    filteredItems[index].title,
                                    filteredItems[index].resurs
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp, 12.dp),
                            painter = painterResource(R.drawable.krest),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            text = filteredItems[index].title,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.secondary
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
}

data class PiesnyListItem(val rubrika: Int, val resurs: Int, val title: String)
