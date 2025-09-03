package by.carkva_gazeta.malitounik

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar

class SlugbovyiaTextu {
    private val datMinALL = ArrayList<SlugbovyiaTextuData>()
    private val piarliny = ArrayList<ArrayList<String>>()
    private var loadPiarlinyJob: Job? = null

    companion object {
        const val HADZINA6 = 1
        const val VIACZERNIA = 2
        const val JUTRAN = 3
        const val VIALHADZINY = 4
        const val LITURHIJA = 5
        const val VIACZERNIA_UVIECZARY = 6
        const val VIACZERNIA_Z_LITURHIJA = 7
        const val VELIKODNYIAHADZINY = 8
        const val ABIEDNICA = 9
        const val PAVIACHERNICA = 10
        const val PAUNOCHNICA = 11
        const val AICOU_VII_SUSVETNAGA_SABORY = 1000
        const val NIADZELIA_PRA_AICOU = 1001
        const val NIADZELIA_AICOU_VI_SABORY = 1002
        const val NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU = 1003
        const val SUBOTA_PERAD_RASTVOM = 1004
        const val NIADZELIA_PERAD_BOHAZJAULENNEM = 1005
        const val NIADZELIA_PASLIA_BOHAZJAULENIA = 1006
        const val MINEIA_KVETNAIA = 100
        const val MINEIA_VIALIKI_POST_1 = 101
        const val MINEIA_VIALIKI_POST_2 = 102
        const val MINEIA_VIALIKI_POST_3 = 103
        const val MINEIA_VIALIKI_POST_4 = 104
        const val MINEIA_VIALIKI_POST_5 = 105
        const val MINEIA_VIALIKI_POST_6 = 106
        const val MINEIA_VIALIKI_TYDZEN = 107
        const val MINEIA_SVITLY_TYDZEN = 108
        const val MINEIA_MESIACHNAIA = 109
    }

    init {
        datMinALL.add(
            SlugbovyiaTextuData(
                6, "Субота перад Нядзеляй Тамаша (Антыпасха) увечары", "bogashlugbovya/ndz_tamasza_viaczernia_subota.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                7, "Нядзеля Тамаша (Антыпасха)", "bogashlugbovya/ndz_tamasza_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                7, "Нядзеля Тамаша (Антыпасха)", "bogashlugbovya/ndz_tamasza_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                7, "Нядзеля Тамаша (Антыпасха)", "bogashlugbovya/ndz_tamasza_viaczernia_uvieczary.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                8, "Панядзелак пасьля нядзелі Тамаша", "bogashlugbovya/ndz_tamasza_01paniadzielak_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                9, "Аўторак пасьля нядзелі Тамаша", "bogashlugbovya/ndz_tamasza_02autorak_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                10, "Серада пасьля нядзелі Тамаша", "bogashlugbovya/ndz_tamasza_03sierada_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                11, "Чацьвер пасьля нядзелі Тамаша", "bogashlugbovya/ndz_tamasza_04czacvier_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                12, "Пятніца пасьля нядзелі Тамаша", "bogashlugbovya/ndz_tamasza_05piatnica_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                13, "Субота перад Нядзеляй міраносіцаў увечары", "bogashlugbovya/ndz_miranosicau_viaczernia_u_subotu.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                14, "Нядзеля міраносіцаў", "bogashlugbovya/ndz_miranosicau_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                14, "Нядзеля міраносіцаў", "bogashlugbovya/ndz_miranosic_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                14, "Нядзеля міраносіцаў", "bogashlugbovya/ndz_miranosicau_viaczernia_u_niadzielu.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                15, "Панядзелак пасьля Нядзелі міраносіцаў увечары", "bogashlugbovya/ndz_miranosicau_01_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                16, "Аўторак пасьля Нядзелі міраносіцаў увечары", "bogashlugbovya/ndz_miranosicau_02_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                17, "Серада пасьля Нядзелі міраносіцаў увечары", "bogashlugbovya/ndz_miranosicau_03_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                18, "Чацьвер пасьля Нядзелі міраносіцаў увечары", "bogashlugbovya/ndz_miranosicau_04_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                19, "Пятніца пасьля Нядзелі міраносіцаў увечары", "bogashlugbovya/ndz_miranosicau_05_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                20, "Субота перад Нядзеляй расслабленага увечары", "bogashlugbovya/ndz_rasslablenaha_viaczernia_u_subotu_vieczaram.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                21, "Нядзеля расслабленага", "bogashlugbovya/ndz_rasslablenaha_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                21, "Нядзеля расслабленага", "bogashlugbovya/ndz_rasslablenaha_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                21, "Нядзеля расслабленага", "bogashlugbovya/ndz_rasslablenaha_uvieczary_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                22, "Панядзелак пасьля Нядзелі расслабленага ўвечары", "bogashlugbovya/ndz_rasslablenaha_01_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                23, "Аўторак перад Паловай сьвята Пяцідзясятніцы увечары", "bogashlugbovya/palova_sviata_piacidziasiatnicy_viaczernia_u_autorak.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                24, "Палова сьвята Пяцідзясятніцы", "bogashlugbovya/palova_sviata_piacidziasiatnicy_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                24, "Палова сьвята Пяцідзясятніцы", "bogashlugbovya/palova_sviata_piacidziasiatnicy_viaczernia_u_sieradu.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                25, "Чацьвер Паловы сьвята Пяцідзясятніцы ўвечары", "bogashlugbovya/palova_sviata_piacidziasiatnicy_viaczernia_u_czacvier.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                26, "Пятніца Паловы сьвята Пяцідзясятніцы ўвечары", "bogashlugbovya/palova_sviata_piacidziasiatnicy_viaczernia_u_piatnicu.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                28, "Нядзеля самаранкі", "bogashlugbovya/ndz_samaranki_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                29, "Панядзелак пасьля Нядзелі самаранкі ўвечары", "bogashlugbovya/ndz_samaranki_01paniadzielak_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                32, "Чацьвер пасьля Нядзелі самаранкі ўвечары", "bogashlugbovya/ndz_samaranki_04czacvier_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                33, "Пятніца пасьля Нядзелі самаранкі ўвечары", "bogashlugbovya/ndz_samaranki_05piatnica_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                35, "Нядзеля сьлепанароджанага", "bogashlugbovya/ndz_slepanarodz_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                38, "Адданьне Вялікадня", "bogashlugbovya/ndz_slepanarodz_addannie_vialikadnia_viaczernia_autorak_uvieczary.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                38, "Адданьне Вялікадня", "bogashlugbovya/ndz_slepanarodz_addannie_vialikadnia_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                38, "Адданьне Вялікадня", "bogashlugbovya/ndz_slepanarodz_addannie_vialikadnia_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "bogashlugbovya/uzniasienne_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "bogashlugbovya/uzniasienne_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "bogashlugbovya/uzniasienne_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                42, "Нядзеля сьвятых айцоў I-га Сусьветнага Сабору", "bogashlugbovya/ndz_ajcou_1susviet_saboru_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                42, "Нядзеля сьвятых айцоў I-га Сусьветнага Сабору", "bogashlugbovya/ndz_ajcou_1susvietnaha_saboru_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                48, "Субота перад Зыходам Сьвятога Духа (Сёмуха) увечары", "bogashlugbovya/zychod_sv_ducha_viaczernia_u_subotu.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                49, "Зыход Сьвятога Духа (Сёмуха)", "bogashlugbovya/zychod_sv_ducha_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                49, "Зыход Сьвятога Духа (Сёмуха)", "bogashlugbovya/zychod_sv_ducha_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                49, "Зыход Сьвятога Духа (Сёмуха)", "bogashlugbovya/paniadzielak_sv_ducha_niadziela_uvieczary_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                50, "Панядзелак Сьвятога Духа", "bogashlugbovya/paniadzielak_sv_ducha_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                50, "Панядзелак Сьвятога Духа", "bogashlugbovya/paniadzielak_sv_ducha_uvieczary_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                51, "Аўторак пасьля Сёмухі ўвечары", "bogashlugbovya/siomucha_02autorak_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                52, "Серада пасьля Сёмухі ўвечары", "bogashlugbovya/siomucha_03sierada_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                53, "Чацьвер пасьля Сёмухі ўвечары", "bogashlugbovya/siomucha_04czacvier_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                54, "Пятніца пасьля Сёмухі ўвечары – Адданьне Сёмухі", "bogashlugbovya/siomucha_05piatnica_addannie_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                55, "Субота перад Нядзеляй ўсіх сьвятых ўвечары", "bogashlugbovya/ndz_usich_sviatych_viaczernia_u_subotu_uvieczary.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                56, "Нядзеля ўсіх сьвятых", "bogashlugbovya/ndz_usich_sviatych_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                56, "Нядзеля ўсіх сьвятых", "bogashlugbovya/ndz_usich_sviatych_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                63, "Нядзеля ўсіх сьвятых беларускага народу", "bogashlugbovya/ndz_usich_sv_biel_narodu_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_KVETNAIA
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                -49, "Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya/bogashlugbovya12_1.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -48, "Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya/bogashlugbovya12_2.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -47, "Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya/bogashlugbovya12_3.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -46, "Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya/bogashlugbovya12_4.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -45, "Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya/bogashlugbovya12_5.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -44, "Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya/bogashlugbovya12_6.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -43, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya/bogashlugbovya12_7.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya/bogashlugbovya12_8.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Літургія сьвятoга Васіля Вялiкага", "bogashlugbovya/bogashlugbovya12_9.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                -42, "1-ая нядзеля посту ўвечары", "bogashlugbovya/bogashlugbovya13_1.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -41, "Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya13_2.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -40, "Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya13_3.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -39, "Серада 2-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya13_4.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -38, "Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya13_5.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -37, "Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya13_6.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -35, "2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya/bogashlugbovya13_7.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -35, "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya/bogashlugbovya13_8.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                -35, "2-ая нядзеля посту ўвечары", "bogashlugbovya/bogashlugbovya14_1.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -34, "Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya14_2.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -33, "Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya14_3.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -32, "Серада 3-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya14_4.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -31, "Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya14_5.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -30, "Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya14_6.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -29, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya/bogashlugbovya14_7.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya/bogashlugbovya14_8.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya/bogashlugbovya14_9.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                -28, "3-яя нядзеля посту ўвечары", "bogashlugbovya/bogashlugbovya15_1.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -27, "Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya15_2.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -27, "Панядзeлак 4-га тыдня вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_4_v_post_01paniadzielak_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -26, "Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya15_3.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -26, "Аўторак 4-га тыдня вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_4_v_post_02autorak_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -25, "Серада 4-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya15_4.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -25, "Серада 4-га тыдня вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_4_v_post_03sierada_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -24, "Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya15_5.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -24, "Чацьвер 4-га тыдня вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_4_v_post_04czacvier_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -23, "Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya15_6.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -23, "Пятніца 4-га тыдня вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_4_v_post_05piatnica_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -22, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya/bogashlugbovya15_7.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya/bogashlugbovya15_8.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya/bogashlugbovya15_9.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                -21, "4-ая нядзеля посту ўвечары", "bogashlugbovya/bogashlugbovya16_1.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -20, "Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya16_2.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -20, "Панядзелак 5-га тыдня Вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_5_v_post_01paniadzielak_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -19, "Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya16_3.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -19, "Аўторак 5-га тыдня Вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_5_v_post_02autorak_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -18, "Серада 5-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya16_4.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -18, "Серада 5-га тыдня Вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_5_v_post_03sierada_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -17, "Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya16_5.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -17, "Чацьвер 5-га тыдня Вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_5_v_post_04_czacvier_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -16, "Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya16_6.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -16, "Пятніца 5-га тыдня Вялікага посту. Шостая гадзіна", "bogashlugbovya/tydzien_5_v_post_05_piatnica_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -15, "Субота Акафісту. Ютрань", "bogashlugbovya/subota_akafistu_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -15, "Літургія ў суботу Акафісту", "bogashlugbovya/subota_akafistu_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -15, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya/bogashlugbovya16_9.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya/bogashlugbovya16_10.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya/bogashlugbovya16_11.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                -14, "5-ая нядзеля посту ўвечары", "bogashlugbovya/bogashlugbovya17_1.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -13, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya17_2.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -12, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya17_3.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -11, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya17_4.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -10, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya17_5.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -9, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya/bogashlugbovya17_6.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -8, "Субота Лазара. Ютрань", "bogashlugbovya/bogashlugbovya17_7.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -8, "Субота Лазара. Літургія", "bogashlugbovya/bogashlugbovya17_8.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -8, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца)", "bogashlugbovya/vierbnica_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -7, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца)", "bogashlugbovya/vierbnica_viaczernia_niadziela_uvieczary.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -7, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца)", "bogashlugbovya/vierbnica_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -7, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца)", "bogashlugbovya/vierbnica_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -6, "Вялікі панядзелак", "bogashlugbovya/vialiki_paniadzielak_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -6, "Вялікі панядзелак", "bogashlugbovya/vialiki_paniadzielak_liturhija_raniej_asviacz_darou.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -6, "Вялікі панядзелак", "bogashlugbovya/vialiki_paniadzielak_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -5, "Вялікі аўторак", "bogashlugbovya/vialiki_autorak_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -5, "Вялікі аўторак", "bogashlugbovya/vialiki_autorak_liturhija_raniej_asviaczanych_darou.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -5, "Вялікі аўторак", "bogashlugbovya/vialiki_autorak_hadzina_6.html", HADZINA6, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -4, "Вялікая серада", "bogashlugbovya/vialikaja_sierada_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -4, "Вялікая серада", "bogashlugbovya/vialikaja_sierada_liturhija_raniej_asviacz_darou.html", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -3, "Вялікі чацьвер", "bogashlugbovya/vialiki_czacvier_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -3, "Вялікі чацьвер", "bogashlugbovya/vialiki_czacvier_viaczernia_z_liturhijaj.html", VIACZERNIA_Z_LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -2, "Вялікая пятніца", "bogashlugbovya/vialikaja_piatnica_viaczernia.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -2, "Вячэрня з вынасам плашчаніцы (без сьвятара)", "bogashlugbovya/vialikaja_piatnica_viaczernia_biez_sviatara.html", VIACZERNIA_UVIECZARY, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -2, "Вялікая пятніца", "bogashlugbovya/vialikaja_piatnica_jutran_12jevanhellau.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -2, "Вялікая пятніца", "bogashlugbovya/vialikaja_piatnica_vialikija_hadziny.html", VIALHADZINY, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -2, "Вялікая пятніца", "bogashlugbovya/vialikaja_piatnica_mal_paviaczernica.html", PAVIACHERNICA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -1, "Вялікая субота", "bogashlugbovya/vialikaja_subota_paunocznica.html", PAUNOCHNICA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -1, "Вялікая субота", "bogashlugbovya/vialikaja_subota_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -1, "Вялікая субота", "bogashlugbovya/vialikaja_subota_viaczernia_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "bogashlugbovya/vialikdzien_jutran.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "bogashlugbovya/vialikdzien_liturhija.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "bogashlugbovya/vialikdzien_viaczernia.html", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Сьветлы панядзелак", "bogashlugbovya/u_svietly_paniadzielak.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Сьветлы панядзелак", "bogashlugbovya/l_svietly_paniadzielak.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Сьветлы панядзелак", "bogashlugbovya/v_svietly_paniadzielak.html", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Сьветлы панядзелак", "bogashlugbovya/vielikodnyja_hadziny.html", VELIKODNYIAHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Сьветлы аўторак", "bogashlugbovya/u_svietly_autorak.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Сьветлы аўторак", "bogashlugbovya/l_svietly_autorak.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Сьветлы аўторак", "bogashlugbovya/v_svietly_autorak.html", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Сьветлы аўторак", "bogashlugbovya/vielikodnyja_hadziny.html", VELIKODNYIAHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Сьветлая серада", "bogashlugbovya/u_svietlaja_sierada.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Сьветлая серада", "bogashlugbovya/l_svietlaja_sierada.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Сьветлая серада", "bogashlugbovya/v_svietlaja_sierada.html", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Сьветлая серада", "bogashlugbovya/vielikodnyja_hadziny.html", VELIKODNYIAHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Сьветлы чацьвер", "bogashlugbovya/u_svietly_czacvier.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Сьветлы чацьвер", "bogashlugbovya/l_svietly_czacvier.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Сьветлы чацьвер", "bogashlugbovya/v_svietly_czacvier.html", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Сьветлы чацьвер", "bogashlugbovya/vielikodnyja_hadziny.html", VELIKODNYIAHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Сьветлая пятніца", "bogashlugbovya/u_svietlaja_piatnica.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Сьветлая пятніца", "bogashlugbovya/l_svietlaja_piatnica.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Сьветлая пятніца", "bogashlugbovya/v_svietlaja_piatnica.html", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Сьветлая пятніца", "bogashlugbovya/vielikodnyja_hadziny.html", VELIKODNYIAHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                6, "Сьветлая субота", "bogashlugbovya/u_svietlaja_subota.html", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                6, "Сьветлая субота", "bogashlugbovya/l_svietlaja_subota.html", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                6, "Сьветлая субота", "bogashlugbovya/vielikodnyja_hadziny.html", VELIKODNYIAHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "bogashlugbovya/mm_08_11_sabor_archaniola_michaila_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "bogashlugbovya/mm_08_11_sabor_archaniola_michaila_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "bogashlugbovya/mm_08_11_sabor_archaniola_michaila_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                318, "Між сьвятымі айца нашага Яна Залатавуснага", "bogashlugbovya/mm_13_11_jana_zalatavusnaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                318, "Між сьвятымі айца нашага Яна Залатавуснага", "bogashlugbovya/mm_13_11_jana_zalatavusnaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Абрэзаньне Гасподняе. Сьвятаначальніка Васіля Вялікага, архібіскупа Кесарыі Кападакійскай", "bogashlugbovya/mm_01_01_abrezannie_vasila_vialikaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Малебен на Новы год", "bogashlugbovya/mm_01_01_malebien_novy_hod.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Перадсьвяцьце Богазьяўленьня; сьвятаначальніка Сільвестра, папы Рымскага", "bogashlugbovya/mm_02_01_pieradsv_bohazjaulennia_silviestra_papy_rymskaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Перадсьвяцьце Богазьяўленьня; сьвятаначальніка Сільвестра, папы Рымскага", "bogashlugbovya/mm_02_01_pieradsviaccie_bohazjaulennia_silviestra_papy_rymskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                128, "Жырoвiцкaй iкoны Maцi Бoжae", "bogashlugbovya/mm_07_05_zyrovickaj_ikony_maci_bozaj_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                142, "Сьвятых роўнаапостальных Канстанціна і Алены", "bogashlugbovya/mm_21_05_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                142, "Сьвятых роўнаапостальных Канстанціна і Алены", "bogashlugbovya/mm_21_05_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "bogashlugbovya/mm_23_05_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "bogashlugbovya/mm_23_05_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "bogashlugbovya/mm_23_05_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                181, "Сьвятых слаўных i годных пахвалы апосталаў Пятра i Паўла", "bogashlugbovya/mm_29_06_piatra_i_paula_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                182, "Сабор сьвятых 12-ці апосталаў", "bogashlugbovya/mm_30_06_sabor_12_apostalau_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                245, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "bogashlugbovya/mm_01_09_paczatak_cark_hodu_siamiona_stoupnika_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                245, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "bogashlugbovya/mm_01_09_paczatak_cark_hodu_siamiona_stoupnika_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                249, "Сьвятога прарока Захара, бацькі Яна Хрысьціцеля, і праведнае Альжбеты, ягонае маці", "bogashlugbovya/mm_05_09_praroka_zachara_prav_alzbiety_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                249, "Сьвятога прарока Захара, бацькі Яна Хрысьціцеля, і праведнае Альжбеты, ягонае маці", "bogashlugbovya/mm_05_09_praroka_zachara_prav_alzbiety_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                251, "Перадсьвяцьце Нараджэньня Багародзіцы і сьвятога мучаніка Сазонта", "bogashlugbovya/mm_07_09_pieradsv_naradzennia_baharodzicy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                251, "Перадсьвяцьце Нараджэньня Багародзіцы і сьвятога мучаніка Сазонта", "bogashlugbovya/mm_07_09_pieradsv_naradzennia_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                252, "Нараджэньне Найсьвяцейшае Багародзіцы", "bogashlugbovya/mm_08_09_naradzennie_baharodzicy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                253, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых Якіма і Ганны", "bogashlugbovya/mm_09_09_pasviaccie_naradzennia_baharodzicy_jakima_i_hanny_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                253, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых Якіма і Ганны", "bogashlugbovya/mm_09_09_pasviaccie_naradzennia_baharodzicy_jakima_i_hanny_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                254, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых мучаніц Мінадоры, Мітрадоры і Німфадоры", "bogashlugbovya/mm_10_09_pasviaccie_naradzennia_baharodzicy_muczanic_minadory_mitradory_nimfadory_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                255, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятое маці нашае Тадоры", "bogashlugbovya/mm_11_09_pasviaccie_naradzennia_baharodzicy_maci_tadory_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                256, "Адданьне сьвята Нараджэньня Багародзіцы", "bogashlugbovya/mm_12_09_addannie_naradzennia_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                257, "Перадсьвяцьце Ўзвышэньня, памяць аднаўленьня царквы Ўваскрасеньня і сьвятога мучаніка Карніла", "bogashlugbovya/mm_13_09_pieradsv_uzvyszennia_adnaul_carkvy_uvaskr_mucz_karnila_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "bogashlugbovya/mm_14_09_uzvyszennie_kryza_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "bogashlugbovya/mm_14_09_uzvyszennie_kryza_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "bogashlugbovya/mm_14_09_uzvyszennie_kryza_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                259, "Пасьвяцьце Ўзвышэньня і сьвятога вялікамучаніка Мікіты", "bogashlugbovya/mm_15_09_pasviaccie_uzvyszennia_vialikamuczanika_mikity_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "bogashlugbovya/mm_11_17_10_ndz_ajcou_7susvietnaha_saboru_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "bogashlugbovya/mm_11_17_10_ndz_ajcou_7susvietnaha_saboru_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "bogashlugbovya/mm_11_17_10_ndz_sv_ajcou_7susvietnaha_saboru_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PRA_AICOU, "Нядзеля праайцоў", "bogashlugbovya/mm_11_17_12_ndz_praajcou_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PRA_AICOU, "Нядзеля праайцоў", "bogashlugbovya/mm_11_17_12_ndz_praajcou_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_AICOU_VI_SABORY, "Нядзеля сьвятых Айцоў першых шасьці Сабораў", "bogashlugbovya/mm_13_19_ndz_ajcou_pierszych_szasci_saborau_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_AICOU_VI_SABORY, "Нядзеля сьвятых Айцоў першых шасьці Сабораў", "bogashlugbovya/mm_13_19_07_ndz_ajcou_pierszych_szasci_saborau_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                218, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "bogashlugbovya/mm_05_08_pieradsv_pieramianiennia_muczanika_jausihnieja_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                218, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "bogashlugbovya/mm_05_08_pieradsv_pieramianiennia_muczanika_jausihnieja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                219, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "bogashlugbovya/mm_06_08_pieramianiennie_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                219, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "bogashlugbovya/mm_06_08_pieramianiennie_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                222, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "bogashlugbovya/mm_09_08_pasviaccie_pieramianiennia_apostala_macieja_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                222, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "bogashlugbovya/mm_09_08_pasviaccie_pieramianennia_apostala_macieja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                228, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "bogashlugbovya/mm_15_08_uspiennie_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                228, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "bogashlugbovya/mm_15_08_uspiennie_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                223, "Пасьвяцьце Перамяненьня і сьв. мучаніка Лаўрына", "bogashlugbovya/mm_10_08_pasviaccie_pieramianiennia_muczanika_laurena_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                227, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "bogashlugbovya/mm_14_08_pieradsv_uspiennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                227, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "bogashlugbovya/mm_14_08_pieradsv_uspiennia_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PERAD_BOHAZJAULENNEM, "Нядзеля перад Богазьяўленьнем", "bogashlugbovya/mm_ndz_pierad_bohazjaulenniem_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PERAD_BOHAZJAULENNEM, "Нядзеля перад Богазьяўленьнем", "bogashlugbovya/mm_ndz_pierad_bohazjaulenniem_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PASLIA_BOHAZJAULENIA, "Нядзеля пасьля Богазьяўленьня", "bogashlugbovya/mm_ndz_pasla_bohazjaulennia_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                1, "Абрэзаньне Гасподняе; сьвятаначальніка Васіля Вялікага, архібіск. Кесарыі Кападакійскай", "bogashlugbovya/mm_01_01_abrezannie_vasila_vialikaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                2, "Перадсьвяцьце Богазьяўленьня", "bogashlugbovya/mm_02_04_01_pieradsv_bohazjaulennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Перадсьвяцьце Богазьяўленьня", "bogashlugbovya/mm_02_04_01_pieradsv_bohazjaulennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Перадсьвяцьце Богазьяўленьня", "bogashlugbovya/mm_02_04_01_pieradsv_bohazjaulennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Перадсьвяцьце Богазьяўленьня. Прарока Малахіі, мучаніка Гардзея", "bogashlugbovya/mm_03_01_pieradsv_bohazjaulennia_praroka_malachii_muczanika_hardzieja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                3, "Перадсьвяцьце Богазьяўленьня. Прарока Малахіі, мучаніка Гардзея", "bogashlugbovya/mm_03_01_pieradsv_bohazjaulennia_praroka_malachii_muczanika_hardzieja_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Перадсьвяцьце Богазьяўленьня. Сабор 70-ці апосталаў, пачэснага Тэактыста", "bogashlugbovya/mm_04_01_pieradsviaccie_bohazjaulennia_sabor_70apostalau_paczesnaha_teaktysta_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                4, "Перадсьвяцьце Богазьяўленьня; Сабор 70-ці апосталаў, пачэснага Тэактыста", "bogashlugbovya/mm_04_01_pieradsviaccie_bohazjaulennia_sabor_70apostalau_paczesnaha_teaktysta_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "bogashlugbovya/mm_05_01_czakannie_bohazjauliennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Чаканьне Богазьяўленьня. Мучанікаў Тэапэмпта і Тэоны; пачэснае Сынклітыкі Александрыйскай", "bogashlugbovya/mm_05_01_czakannie_bohazjaulennia_muczanikau_teapempta_teony_sinklityki_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "bogashlugbovya/mm_07_01_pasviaccie_bohazjaulennia_sabor_jana_chrysciciela_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "bogashlugbovya/mm_07_01_pasviaccie_bohazjaulennia_sabor_jana_chrysciciela_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                16, "Пакланеньне кайданам апостала Пятра", "bogashlugbovya/mm_16_01_paklaniennie_kajdanam_apostala_piatra_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                16, "Пакланеньне кайданам апостала Пятра", "bogashlugbovya/mm_16_01_paklaniennie_kajdanam_apostala_piatra_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "bogashlugbovya/mm_30_01_troch_sviatanaczalnikau_vasila_vialikaha_ryhora_bahaslova_i_jana_zalatavusnaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "bogashlugbovya/mm_30_01_troch_sviatanaczalnikau_vasila_vialikaha_ryhora_bahaslova_i_jana_zalatavusnaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "bogashlugbovya/mm_30_01_troch_sviatanaczalnikau_vasila_vialikaha_ryhora_bahaslova_i_jana_zalatavusnaha_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                275, "Покрыва Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_01_10_pokryva_baharodzicy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                326, "Уваход у храм Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_21_11_uvachod_u_sviatyniu_baharodzicy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                326, "Уваход у храм Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_21_11_uvachod_u_sviatyniu_baharodzicy_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                326, "Уваход у храм Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_21_11_uvachod_u_sviatyniu_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                328, "Пасьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы і між сьвятымі айцоў нашых Амфілёха і Рыгора", "bogashlugbovya/mm_23_11_pasviaccie_uvachodu_baharodzicy_amfilocha_ryhora_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                328, "Пасьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы і між сьвятымі айцоў нашых Амфілёха і Рыгора", "bogashlugbovya/mm_23_11_pasviaccie_uvachodu_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                329, "Пасьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы, сьвятой вялікамучаніцы Кацярыны і сьвятога мучаніка Мяркура", "bogashlugbovya/mm_24_11_pasviaccie_uvachodu_baharodzicy_vialikamuczanicy_kaciaryny_vialikamuczanika_miarkura_miarkura_smalenskaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                329, "Пасьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы, сьвятой вялікамучаніцы Кацярыны і сьвятога мучаніка Мяркура", "bogashlugbovya/mm_24_11_pasviaccie_uvachodu_baharodzicy_vialikamuczanicy_kaciaryny_vialikamuczanika_miarkura_miarkura_smalenskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "bogashlugbovya/mm_06_01_bohazjaulennie_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "bogashlugbovya/mm_05_01_sv_vieczar_bohazjaulennia_vial_hadziny.html", VIALHADZINY
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "bogashlugbovya/mm_05_01_sv_vieczar_bohazjaulennia_abiednica.html", ABIEDNICA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "bogashlugbovya/mm_06_12_mikoly_cudatvorcy_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "bogashlugbovya/mm_06_12_mikoly_cudatvorcy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "bogashlugbovya/mm_06_12_mikoly_cudatvorcy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "bogashlugbovya/mm_12_11_sviatamuczanika_jazafata_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                355, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "bogashlugbovya/mm_20_12_peradsviaccie_rastva_sviatamucz_ihnata_bahanosca_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                355, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "bogashlugbovya/mm_20_12_peradsviaccie_rastva_sviatamucz_ihnata_bahanosca_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "bogashlugbovya/mm_24_12_rastvo_sv_vieczar_abednica.html", ABIEDNICA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "bogashlugbovya/mm_24_12_rastvo_sv_vieczar_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "bogashlugbovya/mm_24_12_rastvo_sv_vieczar_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "bogashlugbovya/mm_24_12_rastvo_sv_vieczar_vial_hadziny.html", VIALHADZINY
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                360, "Нараджэньне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "bogashlugbovya/mm_25_12_naradzennie_chrystova_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                361, "Сабор Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_26_12_sabor_baharodzicy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -70, "Нядзеля мытніка і фарысэя", "bogashlugbovya/ndz_mytnika_i_faryseja_liturhija.html", LITURHIJA, pasxa = true
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                32, "Перадсьвяцьце Сустрэчы Госпада, Бога і Збаўцы нашага Ісуса Хрыста і сьвятога мучаніка Трыфана", "bogashlugbovya/mm_01_02_pieradsviaccie_sustreczy_hospada_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                32, "Перадсьвяцьце Сустрэчы Госпада, Бога і Збаўцы нашага Ісуса Хрыста і сьвятога мучаніка Трыфана", "bogashlugbovya/mm_01_02_pieradsviaccie_sustreczy_hospada_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ)", "bogashlugbovya/mm_02_02_sustrecza_hospada_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ)", "bogashlugbovya/mm_02_02_sustrecza_hospada_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -56, "Нядзеля мясапусная, пра Страшны суд", "bogashlugbovya/ndz_miasapusnaja_liturhija.html", LITURHIJA, pasxa = true
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -63, "Нядзеля блуднага сына", "bogashlugbovya/ndz_bludnaha_syna_liturhija.html", LITURHIJA, pasxa = true
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                -49, "Нядзеля сырная", "bogashlugbovya/ndz_syrnaja_liturhija.html", LITURHIJA, pasxa = true
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                97, "Сьвятога і роўнага апосталам Мятода, настаўніка славянскага і архібіскупа Мараўскага", "bogashlugbovya/mm_06_04_miatoda_marauskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                74, "Пачэснага айца нашага Бенядыкта", "bogashlugbovya/mm_14_03_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                77, "Аляксея, чалавека Божага", "bogashlugbovya/mm_17_03_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                78, "Між сьвятымі айца нашага Кірылы, біскупа Ерусалімскага", "bogashlugbovya/mm_18_03_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                114, "Сьвятога вялікамучаніка Юрыя", "bogashlugbovya/mm_23_04_juryja_pieramozcy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                114, "Сьвятога вялікамучаніка Юрыя", "bogashlugbovya/mm_23_04_juryja_pieramozcy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                84, "Перадсьвяцьце Дабравешчаньня", "bogashlugbovya/mm_24_03_pieradsv_dabravieszczannia_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                84, "Перадсьвяцьце Дабравешчаньня", "bogashlugbovya/mm_24_03_pieradsv_dabravieszczannia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_25_03_dabravieszczannie_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_25_03_dabravieszczannie_liturhija_subota_niadziela.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_25_03_dabravieszczannie_viaczernia_z_liturhijaj.html", VIACZERNIA_Z_LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                105, "Між сьвятымі айца нашага Марціна Вызнаўцы, папы Рымскага", "bogashlugbovya/mm_14_04_marcina_papy_rymskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                114, "Сьвятога вялікамучаніка Юрыя", "bogashlugbovya/mm_23_04_juryja_pieramozcy_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                116, "Сьвятога апостала і евангеліста Марка", "bogashlugbovya/mm_25_04_apostala_marka_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                116, "Сьвятога апостала і евангеліста Марка", "bogashlugbovya/mm_25_04_apostala_marka_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                118, "Сьвятога сьвятамучаніка Сямёна, сваяка Гасподняга", "bogashlugbovya/mm_27_04_siamiona_svajaka_haspodniaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                119, "Між сьвятымі айца нашага Кірылы, біскупа Тураўскага", "bogashlugbovya/mm_28_04_kiryly_turauskaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                119, "Між сьвятымі айца нашага Кірылы, біскупа Тураўскага", "bogashlugbovya/mm_28_04_kiryly_turauskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                121, "Сьвятога апостала Якуба, брата Яна Багаслова", "bogashlugbovya/mm_30_04_apostala_jakuba_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                121, "Сьвятога апостала Якуба, брата Яна Багаслова", "bogashlugbovya/mm_30_04_apostala_jakuba_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                123, "Між сьвятымі айца нашага Апанаса, архібіскупа Александрыйскага", "bogashlugbovya/mm_02_05_apanasa_aleksandryjskaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                123, "Між сьвятымі айца нашага Апанаса, архібіскупа Александрыйскага", "bogashlugbovya/mm_02_05_apanasa_aleksandryjskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "bogashlugbovya/mm_11_05_nastaunikau_slavianau_kiryly_i_miatoda_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "bogashlugbovya/mm_11_05_nastaunikau_slavianau_kiryly_i_miatoda_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "bogashlugbovya/mm_11_05_nastaunikau_slavianau_kiryly_i_miatoda_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                127, "Ёва Шматпакутнага", "bogashlugbovya/mm_06_05_jova_szmatpakutnaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                131, "Сьвятога апостала Сымана Зілота", "bogashlugbovya/mm_10_05_apostala_symana_zilota_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                161, "Кірылы, архібіскупа Александрыйскага", "bogashlugbovya/mm_09_06_kiryly_aleksandryjskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                163, "Сьвятых апосталаў Баўтрамея і Варнавы", "bogashlugbovya/mm_11_06_apostalau_bautramieja_i_varnavy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                163, "Сьвятых апосталаў Баўтрамея і Варнавы", "bogashlugbovya/mm_11_06_apostalau_bautramieja_i_varnavy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                164, "Сьвятога Анупрэя Вялікага", "bogashlugbovya/mm_12_06_anupreja_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                164, "Сьвятога Анупрэя Вялікага", "bogashlugbovya/mm_12_06_anupreja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                171, "Сьвятога апостала Юды, сваяка Гасподняга", "bogashlugbovya/mm_19_06_apostala_judy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                171, "Сьвятога апостала Юды, сваяка Гасподняга", "bogashlugbovya/mm_19_06_apostala_judy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                176, "Нараджэньне сьвятога прарока Прадвесьніка і Хрысьціцеля", "bogashlugbovya/mm_24_06_jana_chrysciciela_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                176, "Нараджэньне сьвятога прарока Прадвесьніка і Хрысьціцеля", "bogashlugbovya/mm_24_06_naradzennie_jana_chrysciciela_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                181, "Сьвятых слаўных i годных пахвалы апосталаў Пятра i Паўла", "bogashlugbovya/mm_29_06_apostalau_piatra_i_paula_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                182, "Сабор сьвятых 12-ці апосталаў", "bogashlugbovya/mm_30_06_sabor_12_apostalau_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                192, "Сьвятога Антона Кіевапячорскага", "bogashlugbovya/mm_10_07_antona_kijevapiaczorskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                193, "Сьвятой мучаніцы Аўхіміі ўсяхвальнай", "bogashlugbovya/mm_11_07_auchimii_usiachvalnaj_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                201, "Сьвятое маці нашае Макрыны, сястры сьв. Васіля Вялікага", "bogashlugbovya/mm_19_07_maci_makryny_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                202, "Сьвятога прарока Ільлі", "bogashlugbovya/mm_20_07_praroka_illi_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                202, "Сьвятога прарока Ільлі", "bogashlugbovya/mm_20_07_praroka_illi_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                202, "Сьвятога прарока Ільлі", "bogashlugbovya/mm_20_07_praroka_illi_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                204, "Сьвятой і роўнай апосталам Марыі Магдалены", "bogashlugbovya/mm_22_07_maryi_mahdaleny_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                207, "Усьпеньне сьвятой Ганны, маці Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_25_07_uspiennie_sviatoj_hanny_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                207, "Усьпеньне сьвятой Ганны, маці Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_25_07_uspiennie_sviatoj_hanny_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                209, "Сьвятога вялікамучаніка і лекара Панцялеймана", "bogashlugbovya/mm_27_07_vialikamuczanika_pancialejmana_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                217, "7-мі юнакоў Эфэскіх", "bogashlugbovya/mm_04_08_7junakou_efeskich_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                224, "Пасьвяцьце Перамяненьня і сьвятога мучаніка Еўпла", "bogashlugbovya/mm_11_08_pasviaccie_pieramianinnie_muczanika_jeupla_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                231, "Пасьвяцьце Ўсьпеньня і сьвятых мучанікаў Флёра і Лаўра", "bogashlugbovya/mm_18_08_pasviaccie_uspiennia_mucz_flora_laura_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                232, "Пасьвяцьце Ўсьпеньня і сьвятога мучаніка Андрэя Сотніка, а з ім 2593 мучанікаў", "bogashlugbovya/mm_19_08_pasviaccie_uspiennia_mucz_andreja_sotnika_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                233, "Пасьвяцьце Ўсьпеньня і сьвятога прарока Самуіла", "bogashlugbovya/mm_20_08_pasviaccie_uspiennia_praroka_samuila_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                234, "Пасьвяцьце Ўсьпеньня, сьв. апостала Тадэя і сьвятога айца нашага Аўрама Смаленскага і вучня ягонага Ахрэма", "bogashlugbovya/mm_21_08_pasviaccie_uspiennia_apostala_tadeja_aurama_smalenskaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                234, "Пасьвяцьце Ўсьпеньня, сьв. апостала Тадэя і сьвятога айца нашага Аўрама Смаленскага і вучня ягонага Ахрэма", "bogashlugbovya/mm_21_08_pasviaccie_uspiennia_apostala_tadeja_aurama_smalenskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                235, "Пасьвяцьце Ўсьпеньня, сьв. мучаніка Агатоніка і сьв. мучаніка Люпа", "bogashlugbovya/mm_22_08_pasviaccie_uspiennia_muczanikau_ahatonika_lupa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                236, "Адданьне сьвята Ўсьпеньня Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "bogashlugbovya/mm_23_08_addannie_sviata_uspiennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                236, "Адданьне сьвята Ўсьпеньня Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "bogashlugbovya/mm_23_08_addannie_sviata_uspiennia_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                237, "Сьвятамучаніка Яўціха, вучня сьвятога Яна Багаслова", "bogashlugbovya/mm_24_08_sviatamuczanika_jaucicha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                238, "Перанясеньне мошчаў апостала Баўтрамея, апостала Ціта, вучня сьвятога Паўла", "bogashlugbovya/mm_25_08_pieranias_moszczau_apostala_bautramieja_apostala_cita_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                242, "Адсячэньне галавы сьвятога Яна Хрысьціцеля", "bogashlugbovya/mm_29_08_adsiaczennie_halavy_jana_chrysciciela_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                242, "Адсячэньне галавы сьвятога Яна Хрысьціцеля", "bogashlugbovya/mm_29_08_adsiaczennie_halavy_jana_chrysciciela_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                242, "Адсячэньне галавы сьвятога Яна Хрысьціцеля", "bogashlugbovya/mm_29_08_adsiaczennie_halavy_jana_chrysciciela_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                252, "Нараджэньне Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_08_09_naradzennie_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                257, "Перадсьвяцьце Ўзвышэньня, памяць аднаўленьня царквы Ўваскрасеньня і сьвятога мучаніка Карніла", "bogashlugbovya/mm_13_09_pieradsv_uzvyszennia_adnaul_carkvy_uvaskr_mucz_karnila_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                262, "Пасьвяцьце Ўзвышэньня і сьвятога Яўмена, біскупа Гартынскага, цудатворцы", "bogashlugbovya/mm_18_09_pasviaccie_uzvyszennia_jaumiena_cudatvorcy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                263, "Пасьвяцьце Ўзвышэньня і сьвятых мучанікаў Трахіма, Савацея і Дарымедонта", "bogashlugbovya/mm_19_09_pasviaccie_uzvyszennia_muczanikau_trachima_savacieja_darymiedonta_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                267, "Зачацьце сьвятога Яна Хрысьціцеля", "bogashlugbovya/mm_23_09_zaczaccie_jana_chrysciciela_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                267, "Зачацьце сьвятога Яна Хрысьціцеля", "bogashlugbovya/mm_23_09_zaczaccie_jana_chrysciciela_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                270, "Перастаўленьне сьвятога апостала і евангеліста Яна Багаслова", "bogashlugbovya/mm_26_09_pierastaulennie_apostala_jana_bahaslova_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                270, "Перастаўленьне сьвятога апостала і евангеліста Яна Багаслова", "bogashlugbovya/mm_26_09_pierastaulennie_apostala_jana_bahaslova_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                270, "Перастаўленьне сьвятога апостала і евангеліста Яна Багаслова", "bogashlugbovya/mm_26_09_pierastaulennie_apostala_jana_bahaslova_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                275, "Покрыва Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_01_10_pokryva_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                276, "Сьвятамучаніка Кіпрыяна і сьв. мучаніцы Юстыны", "bogashlugbovya/mm_02_10_sviatamuczanika_kipryjana_muczanicy_justyny_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                277, "Сьвятога Дзяніса Арэапагіта", "bogashlugbovya/mm_03_10_dzianisa_aerapahita_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                278, "Сьвятамучаніка Ератэя, біскупа Атэнскага", "bogashlugbovya/mm_04_10_sviatamuczanika_jerateja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                280, "Сьвятога апостала Тамаша", "bogashlugbovya/mm_06_10_apostala_tamasza_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                280, "Сьвятога апостала Тамаша", "bogashlugbovya/mm_06_10_apostala_tamasza_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                283, "Сьвятога апостала Якуба Алфеевага", "bogashlugbovya/mm_09_10_apostala_jakuba_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                283, "Сьвятога апостала Якуба Алфеевага", "bogashlugbovya/mm_09_10_apostala_jakuba_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                285, "Сьвятога апостала Піліпа, аднаго з сямі дыяканаў", "bogashlugbovya/mm_11_10_apostala_pilipa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                275, "Покрыва Найсьвяцейшай Багародзіцы", "bogashlugbovya/mm_01_10_pokryva_baharodzicy_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                297, "Сьвятога апостала Якуба, сваяка Гасподняга", "bogashlugbovya/mm_23_10_apostala_jakuba_svajaka_haspodniaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                297, "Сьвятога апостала Якуба, сваяка Гасподняга", "bogashlugbovya/mm_23_10_apostala_jakuba_svajaka_haspodniaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                300, "Сьвятога вялікамучаніка Зьмітра і ўспамін землятрусу", "bogashlugbovya/mm_26_10_vialikamuczanika_zmitra_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                300, "Сьвятога вялікамучаніка Зьмітра і ўспамін землятрусу", "bogashlugbovya/mm_26_10_vialikamuczanika_zmitra_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                316, "Пачэснага айца нашага Тодара Студыта", "bogashlugbovya/mm_11_11_todara_studyta_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                319, "Сьвятога апостала Піліпа", "bogashlugbovya/mm_14_11_apostala_pilipa_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                319, "Сьвятога апостала Піліпа", "bogashlugbovya/mm_14_11_apostala_pilipa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                321, "Сьвятога апостала і евангеліста Мацьвея", "bogashlugbovya/mm_16_11_apostala_macvieja_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                321, "Сьвятога апостала і евангеліста Мацьвея", "bogashlugbovya/mm_16_11_apostala_macvieja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "bogashlugbovya/mm_12_11_sviatamuczanika_jazafata_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "bogashlugbovya/mm_12_11_sviatamuczanika_jazafata_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                330, "Адданьне сьвята Ўводзінаў Багародзіцы, сьв. Клімента, папы Рымскага і сьв. Пятра, архібіскупа Александрыйскага", "bogashlugbovya/mm_25_11_addannie_uvachodu_baharodzicy_klimenta_papy_piatra_aleksandryjskaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                335, "Сьвятога апостала Андрэя Першапакліканага", "bogashlugbovya/mm_30_11_apostala_andreja_pierszapaklikanaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                336, "Сьвятога прарока Навума", "bogashlugbovya/mm_01_12_praroka_navuma_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                337, "Сьвятога прарока Абакума", "bogashlugbovya/mm_02_12_praroka_abakuma_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                327, "Пасьвяцьце Ўводзінаў Найсьвяцейшай Багародзіцы і сьвятога апостала Халімона і інш.", "bogashlugbovya/mm_22_11_pasviaccie_uvachodu_baharodzicy_apostala_chalimona_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                327, "Пасьвяцьце Ўводзінаў Найсьвяцейшай Багародзіцы і сьвятога апостала Халімона і інш.", "bogashlugbovya/mm_22_11_pasviaccie_uvachodu_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                335, "Сьвятога апостала Андрэя Першапакліканага", "bogashlugbovya/mm_30_11_apostala_andreja_pierszapaklikanaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                338, "Сьвятога прарока Сафоніі", "bogashlugbovya/mm_03_12_praroka_safonii_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                339, "Сьв. мучаніцы Барбары і пачэснага айца нашага Яна Дамаскіна", "bogashlugbovya/mm_04_12_muczanicy_barbary_paczesnaha_jana_damaskina_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                344, "Зачацьце сьв. Ганны, калі яна зачала Найсьвяцейшую Багародзіцу", "bogashlugbovya/mm_09_12_zaczaccie_baharodzicy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                344, "Зачацьце сьв. Ганны, калі яна зачала Найсьвяцейшую Багародзіцу", "bogashlugbovya/mm_09_12_zaczaccie_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                344, "Зачацьце сьв. Ганны, калі яна зачала Найсьвяцейшую Багародзіцу", "bogashlugbovya/mm_09_12_zaczaccie_baharodzicy_jutran.html", JUTRAN
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                358, "Перадсьвяцьце нараджэньня Госпада нашага Ісуса Хрыста і сьвятых 10-ці мучанікаў Крыцкіх", "bogashlugbovya/mm_23_12_pieradsviaccie_rastva_10muczanikau_kryckich_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU, "Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста (Нядзеля айцоў)", "bogashlugbovya/mm_18_24_12_ndz_pierad_rastvom_sviatych_ajcou_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU, "Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 18-19 сьнежня", "bogashlugbovya/mm_ndz_pierad_rastvom_sviatych_ajcou_18_19_12_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU, "Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 20-23 сьнежня", "bogashlugbovya/mm_ndz_pierad_rastvom_sviatych_ajcou_20_23_12_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU, "Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 24 сьнежня", "bogashlugbovya/mm_ndz_pierad_rastvom_sviatych_ajcou_24_12_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                363, "20 тысячаў мучанікаў Нікамедыйскіх", "bogashlugbovya/mm_28_12_pasviaccie_rastva_20000_muczanikau_nikamiedyjskich_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                306, "Сьвятых бескарысьлівых лекараў і цудатворцаў Кузьмы і Дзям’яна", "bogashlugbovya/mm_01_11_bieskaryslivych_lekarau_kuzmy_dziamjana_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                292, "Сьвятога апостала і евангеліста Лукі", "bogashlugbovya/mm_18_10_apostala_luki_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                292, "Сьвятога апостала і евангеліста Лукі", "bogashlugbovya/mm_18_10_apostala_luki_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                264, "Пасьвяцьце Ўзвышэньня і сьвятога мучаніка Астапа, жонкі яго Тэапістыі і сыноў іхніх Агапа і Тэапіста", "bogashlugbovya/mm_20_09_pasviaccie_uzvyszennia_muczanika_astapa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "bogashlugbovya/mm_06_01_bohazjauliennie_viaczernia_liturhija_asviaczennie_vady.html", VIACZERNIA_Z_LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                8, "Пасьвяцьце Богазьяўленьня. Пачэснага Юрыя Хазэвіта; пачэснае Дамінікі", "bogashlugbovya/mm_08_01_pasviaccie_bohazjaulennia_juryja_chazevita_daminiki_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                11, "Пасьвяцьце Богазьяўленьня. Пачэснага Тэадосія Вялікага", "bogashlugbovya/mm_11_01_pasviaccie_bohazjaulennia_teadosija_vialikaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                12, "Пасьвяцьце Богазьяўленьня. Мучаніцы Тацяны", "bogashlugbovya/mm_12_01_pasviaccie_bohazjaulennia_muczanicy_taciany_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                13, "Пасьвяцьце Богазьяўленьня. Мучанікаў Ярміла і Стратоніка", "bogashlugbovya/mm_13_01_pasviaccie_bohazjaulennia_mucz_jarmila_stratonika_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                14, "Адданьне сьвята Богазьяўленьня. Пачэсных айцоў, у Сінаі і Раіце забітых", "bogashlugbovya/mm_14_01_addannie_bohazjaulennia_ajcou_u_sinai_raicie_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                14, "Адданьне сьвята Богазьяўленьня. Пачэсных айцоў, у Сінаі і Раіце забітых", "bogashlugbovya/mm_14_01_addannie_bohazjaulennia_ajcou_u_sinai_raicie_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                17, "Пачэснага Антона Вялікага", "bogashlugbovya/mm_17_01_paczesnaha_antona_vialikaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                17, "Пачэснага Антона Вялікага", "bogashlugbovya/mm_17_01_paczesnaha_antona_vialikaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                21, "Пачэснага Максіма вызнаўцы, мучаніка Неафіта", "bogashlugbovya/mm_21_01_paczesnaha_maksima_vyznaucy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                25, "Сьвятаначальніка Рыгора Багаслова, архібіскупа Канстанцінопальскага", "bogashlugbovya/mm_25_01_sviatanaczalnika_ryhora_bahaslova_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                25, "Сьвятаначальніка Рыгора Багаслова, архібіскупа Канстанцінопальскага", "bogashlugbovya/mm_25_01_sviatanaczalnika_ryhora_bahaslova_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                27, "Перанясеньне мошчаў сьвятаначальніка Яна Залатавуснага", "bogashlugbovya/mm_27_01_pieranias_moszczau_jana_zalatavusnaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                27, "Перанясеньне мошчаў сьвятаначальніка Яна Залатавуснага", "bogashlugbovya/mm_27_01_pieranias_moszczau_jana_zalatavusnaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                28, "Пачэснага Ахрэма Сірыйца", "bogashlugbovya/mm_28_01_paczesnaha_achrema_siryjca_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                39, "Пасьвяцьце Сустрэчы Госпада; сьвятога вялікамучаніка Тодара Страцілата і прарока Захара", "bogashlugbovya/mm_08_02_pasviaccie_sustreczy_vialikamucz_todara_praroka_zachara_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                34, "Пасьвяцьце Сустрэчы Госпада, Сымона Богапрыемцы і Ганны прарочыцы", "bogashlugbovya/mm_03_02_pasviaccie_sustreczy_symona_hanny_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                41, "Сьвятамучаніка Харалампа", "bogashlugbovya/mm_10_02_sviatamuczanika_charlampa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                129, "Cьвятога апостала і евангеліста Яна Багаслова", "bogashlugbovya/mm_08_05_apostala_jevanhielista_jana_bahaslova_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                129, "Cьвятога апостала і евангеліста Яна Багаслова", "bogashlugbovya/mm_08_05_apostala_jevanhielista_jana_bahaslova_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                130, "Перанясеньне мошчаў сьвятога Мікалая Цудатворцы ў Бары", "bogashlugbovya/mm_09_05_pieraniasiennie_moszczau_mikalaja_cudatvorcy_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                130, "Перанясеньне мошчаў сьвятога Мікалая Цудатворцы ў Бары", "bogashlugbovya/mm_09_05_pieraniasiennie_moszczau_mikalaja_cudatvorcy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                195, "Сабор арханёла Габрыэля", "bogashlugbovya/mm_13_07_sabor_archaniola_habryela_viaczernia.html", VIACZERNIA
            )
        )

        datMinALL.add(
            SlugbovyiaTextuData(
                214, "Працэсія сьвятога Крыжа і памяці сямі мучанікаў Макабэяў", "bogashlugbovya/mm_01_08_pracesija_kryza_7muczanikau_makabejau_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                214, "Працэсія сьвятога Крыжа і памяці сямі мучанікаў Макабэяў", "bogashlugbovya/mm_01_08_pracesija_kryza_7muczanikau_makabejau_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                215, "Перанясеньне мошчаў сьвятога першамучаніка і дыякана Сьцяпана", "bogashlugbovya/mm_02_08_pieraniasiennie_moszczau_pierszamucz_sciapana_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                220, "Пасьвяцьце Перамяненьня і сьв. мучаніка Дамэція", "bogashlugbovya/mm_07_08_pasviaccie_pieramianiennia_mucz_damecija_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                221, "Пасьвяцьце Перамяненьня і сьв. Амільяна, біскупа Кізіцкага", "bogashlugbovya/mm_08_08_pasviaccie_pieramianiennia_amilana_bisk_kizickaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                225, "Пасьвяцьце Перамяненьня і сьв. мучанікаў Фоція і Анікіты", "bogashlugbovya/mm_12_08_pasviaccie_pieramianiennia_mucz_focija_anikity_maksima_vyzn_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                226, "Адданьне сьвята Перамяненьня", "bogashlugbovya/mm_13_08_addannie_pieramianiennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                229, "Пасьвяцьце Ўсьпеньня і Пакланеньне нерукатворнаму вобразу Госпада нашага Ісуса Хрыста", "bogashlugbovya/mm_16_08_pasviaccie_uspiennia_nierukatvorny_vobraz_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                229, "Пасьвяцьце Ўсьпеньня і Пакланеньне нерукатворнаму вобразу Госпада нашага Ісуса Хрыста", "bogashlugbovya/mm_16_08_pasviaccie_uspiennia_nierukatvorny_vobraz_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                230, "Пасьвяцьце Ўсьпеньня і сьвятога мучаніка Мірона", "bogashlugbovya/mm_17_08_pasviaccie_uspiennia_muczanika_mirona_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                243, "Пасьвяцьце Адсячэньня галавы Яна Хрысьціцеля; сьвятых біскупаў Аляксандра, Яна і Паўла Новага", "bogashlugbovya/mm_30_08_pasviaccie_adsiaczennia_bisk_alaksandra_jana_paula_novaha_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                243, "Пасьвяцьце Адсячэньня галавы Яна Хрысьціцеля; сьвятых біскупаў Аляксандра, Яна і Паўла Новага", "bogashlugbovya/mm_30_08_pasviaccie_adsiaczennia_bisk_alaksandra_jana_paula_novaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                250, "Успамін цуду сьвятога арханёла Міхаіла ў Калосах", "bogashlugbovya/mm_06_09_cud_archaniola_michala_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                265, "Адданьне сьвята Ўзвышэньня Пачэснага Крыжа", "bogashlugbovya/mm_21_09_addannie_kryzauzvyszennia_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                325, "Перадсьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы і сьвятых Рыгора і Прокла", "bogashlugbovya/mm_20_11_pieradsviaccie_uvachodu_u_sviatyniu_baharodzicy_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                325, "Перадсьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы і сьвятых Рыгора і Прокла", "bogashlugbovya/mm_20_11_pieradsviaccie_uvachodu_ryhora_prokla_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                340, "Сьвятога і баганоснага айца нашага Савы Асьвячанага", "bogashlugbovya/mm_05_12_savy_asviaczanaha.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                342, "Сьвятога Амбражэя, біскупа Міланскага", "bogashlugbovya/mm_07_12_ambrazeja_biskupa_milanskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                343, "Пачэснага айца нашага Патапа", "bogashlugbovya/mm_08_12_paczesnaha_patapa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                347, "Сьвятога айца нашага Сьпірыдона Трымітунцкага", "bogashlugbovya/mm_12_12_spirydona_trymitunckaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                351, "Сьвятога прарока Агея", "bogashlugbovya/mm_16_12_praroka_ahieja_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                356, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьвятой мучаніцы Юльяны", "bogashlugbovya/mm_21_12_pieradsviaccia_rastva_muczanicy_juljany_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                352, "Сьвятога прарока Данілы і трох юнакоў Ананіі, Азарыі і Місаіла", "bogashlugbovya/mm_17_12_praroka_danily_troch_junakou_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                352, "Сьвятога прарока Данілы і трох юнакоў Ананіі, Азарыі і Місаіла", "bogashlugbovya/mm_17_12_praroka_danily_troch_junakou_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                362, "Пасьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста, памяць сьвятога першамучаніка і дыякана Сьцяпана", "bogashlugbovya/mm_27_12_pasviaccie_rastva_pierszamuczanika_sciapana_ajca_todara_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                362, "Пасьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста, памяць сьвятога першамучаніка і дыякана Сьцяпана", "bogashlugbovya/mm_27_12_pasviaccie_rastva_pierszamuczanika_sciapana_ajca_todara_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                SUBOTA_PERAD_RASTVOM, "Субота перад Раством Хрыстовым", "bogashlugbovya/mm_18_24_12_subota_pierad_rastvom_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                35, "Пасьвяцьце Сустрэчы і сьвятога айца нашага Сідара Пелусійскага", "bogashlugbovya/mm_04_02_pasviaccie_sustreczy_sviatoha_sidara_pielusijskaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                47, "Між сьвятымі айца нашага Льва Вялікага, папы Рымскага", "bogashlugbovya/mm_16_02_lva_vialikaha_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                36, "Пасьвяцьце Сустрэчы Госпада. Сьвятой мучаніцы Агаты", "bogashlugbovya/mm_05_02_pasviaccie_sustreczy_muczanicy_ahaty_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                40, "Адданьне сьвята Сустрэчы Госпада, Бога і Збаўцы нашага Ісуса Хрыста. Мучаніка Нічыпара", "bogashlugbovya/mm_09_02_addanie_sustreczy_hospada_muczanika_niczypara_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                40, "Адданьне сьвята Сустрэчы Госпада, Бога і Збаўцы нашага Ісуса Хрыста. Мучаніка Нічыпара", "bogashlugbovya/mm_09_02_addanie_sustreczy_hospada_muczanika_niczypara_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                45, "Сьвятога і роўнага апосталам Кірылы Філосафа, настаўніка славянаў", "bogashlugbovya/mm_14_02_kiryly_filosafa_viaczernia.html", VIACZERNIA
            )
        )
        datMinALL.add(
            SlugbovyiaTextuData(
                45, "Сьвятога і роўнага апосталам Кірылы Філосафа, настаўніка славянаў", "bogashlugbovya/mm_14_02_kiryly_filosafa_liturhija.html", LITURHIJA
            )
        )
        datMinALL.add(SlugbovyiaTextuData(69, "40 мучанікаў Себастыйскіх", "bogashlugbovya/mm_09_03_40_muczanikau_siebastyjskich_viaczernia.html", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(177, "Сьвятой мучаніцы Хаўроньні", "bogashlugbovya/mm_25_06_muczanicy_chauronni_viaczernia.html", VIACZERNIA))
    }

    fun getNazouSluzby(sluzba: Int): String {
        return when (sluzba) {
            JUTRAN -> "Ютрань"
            LITURHIJA -> "Літургія"
            VIACZERNIA -> "Вячэрня"
            VIACZERNIA_UVIECZARY -> "Вячэрня"
            VIACZERNIA_Z_LITURHIJA -> "Вячэрня з Літургіяй"
            VELIKODNYIAHADZINY -> "Велікодныя гадзіны"
            VIALHADZINY -> "Вялікія гадзіны"
            ABIEDNICA -> "Абедніца"
            HADZINA6 -> "Шостая гадзіна"
            PAVIACHERNICA -> "Малая павячэрніца"
            PAUNOCHNICA -> "Паўночніца"
            else -> ""
        }
    }

    fun getAllSlugbovyiaTextu() = datMinALL

    fun getTydzen1(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_1
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getTydzen2(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_2
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getTydzen3(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_3
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getTydzen4(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_4
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getTydzen5(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_5
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getTydzen6(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_6
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getVilikiTydzen(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_TYDZEN
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getSvetlyTydzen(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_SVITLY_TYDZEN
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getMineiaMesiachnaia(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_MESIACHNAIA
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun getMineiaKvetnaia(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_KVETNAIA
        }
        list.sortedBy {
            it.day
        }
        return list
    }

    fun loadSluzbaDayList(
        slugbaType: Int, dayOfYear: Int, year: Int
    ): ArrayList<SlugbovyiaTextuData> {
        var dayOfYearReal: Int
        var day = 0
        val resultSlugba = datMinALL.filter {
            when (slugbaType) {
                VIACZERNIA, VIACZERNIA_UVIECZARY -> it.sluzba == VIACZERNIA || it.sluzba == VIACZERNIA_UVIECZARY || it.sluzba == VIACZERNIA_Z_LITURHIJA
                VIALHADZINY -> it.sluzba == VIALHADZINY || it.sluzba == VELIKODNYIAHADZINY || it.sluzba == HADZINA6
                else -> it.sluzba == slugbaType
            }
        }
        val resultDay = ArrayList<SlugbovyiaTextuData>()
        for (i in resultSlugba.indices) {
            day = resultSlugba[i].day
            dayOfYearReal = getRealDay(day, dayOfYear, year, resultSlugba[i].pasxa)
            val calendar = GregorianCalendar()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.DAY_OF_YEAR] = dayOfYearReal
            var addDay = 0
            if (!calendar.isLeapYear(calendar.get(Calendar.YEAR)) && calendar[Calendar.MONTH] > Calendar.FEBRUARY) addDay = 1
            if (dayOfYearReal + addDay == dayOfYear) {
                resultDay.add(resultSlugba[i])
            }
        }
        if (slugbaType == LITURHIJA) {
            val data = Settings.data[Settings.caliandarPosition]
            if (data[0].toInt() == Calendar.SUNDAY && data[20] != "0") {
                val list = getTraparyKandakiNiadzelnyia()
                val ton = data[20].toInt() - 1
                resultDay.add(SlugbovyiaTextuData(day, list[ton].title, list[ton].resource, LITURHIJA))
            }
            if (data[0].toInt() != Calendar.SUNDAY) {
                val list = getTraparyKandakiShtodzennyia()
                val dzenNedeli = data[0].toInt() - 2
                resultDay.add(SlugbovyiaTextuData(day, list[dzenNedeli].title, list[dzenNedeli].resource, LITURHIJA))
            }
        }
        return resultDay
    }

    fun loadPiarliny() {
        if (piarliny.isEmpty() && loadPiarlinyJob?.isActive != true) {
            val filePiarliny = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
            if (!filePiarliny.exists()) {
                if (Settings.isNetworkAvailable(Malitounik.applicationContext())) {
                    loadPiarlinyJob = CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val builder = getPiarliny()
                            if (builder != "") {
                                val gson = Gson()
                                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                                piarliny.addAll(gson.fromJson(builder, type))
                            }
                        } catch (_: Throwable) {
                            filePiarliny.delete()
                        }
                    }
                }
            } else {
                try {
                    val builder = filePiarliny.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                    piarliny.addAll(gson.fromJson(builder, type))
                } catch (_: Throwable) {
                    filePiarliny.delete()
                }
            }
        }
    }

    private suspend fun getPiarliny(): String {
        val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
        var text = ""
        val localFile = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
        pathReference.getFile(localFile).addOnSuccessListener {
            text = localFile.readText()
        }.await()
        return text
    }

    fun checkParliny(dayOfYear: Int): Boolean {
        val cal = Calendar.getInstance()
        piarliny.forEach {
            cal.timeInMillis = it[0].toLong() * 1000
            if (dayOfYear == cal.get(Calendar.DAY_OF_YEAR)) {
                return true
            }
        }
        return false
    }

    fun getRealDay(day: Int, dayOfYear: Int, year: Int, isPasxa: Boolean): Int {
        var realDay = day
        val calendar = GregorianCalendar()
        calendar[Calendar.YEAR] = year
        when {
            day >= 1000 -> {
                when (day) {
                    AICOU_VII_SUSVETNAGA_SABORY -> {
                        //Айцоў VII Сусьветнага Сабору
                        for (i in 11..17) {
                            calendar.set(calendar.get(Calendar.YEAR), Calendar.OCTOBER, i)
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SUNDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                break
                            }
                        }
                    }

                    NIADZELIA_PRA_AICOU -> {
                        //Нядзеля праайцоў
                        for (i in 11..17) {
                            calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SUNDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                break
                            }
                        }
                    }

                    NIADZELIA_AICOU_VI_SABORY -> {
                        //Нядзеля сьвятых Айцоў першых шасьці Сабораў
                        for (i in 13..19) {
                            calendar.set(calendar.get(Calendar.YEAR), Calendar.JULY, i)
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SUNDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                break
                            }
                        }
                    }

                    SUBOTA_PERAD_RASTVOM -> {
                        //Субота прерад Раством
                        for (i in 18..24) {
                            calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SATURDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                break
                            }
                        }
                    }

                    NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU -> {
                        //Нядзеля прерад Раством, сьвятых Айцоў
                        for (i in 18..24) {
                            calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SUNDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                break
                            }
                        }
                    }

                    NIADZELIA_PERAD_BOHAZJAULENNEM -> {
                        //Нядзеля прерад Богаз'яўленнем
                        if (dayOfYear > 5) calendar.set(
                            calendar.get(Calendar.YEAR), Calendar.DECEMBER, 30
                        )
                        else calendar.set(calendar.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 30)
                        (1..7).forEach { _ ->
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SUNDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                return@forEach
                            }
                            calendar.add(Calendar.DATE, 1)
                        }
                    }

                    NIADZELIA_PASLIA_BOHAZJAULENIA -> {
                        //Нядзеля пасля Богаз'яўлення
                        for (i in 7..13) {
                            calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, i)
                            val wik = calendar.get(Calendar.DAY_OF_WEEK)
                            if (wik == Calendar.SUNDAY) {
                                realDay = calendar.get(Calendar.DAY_OF_YEAR)
                                break
                            }
                        }
                    }
                }
            }

            isPasxa -> {
                realDay = pasha(day)
            }

            else -> {
                var newDay = day
                if (!calendar.isLeapYear(calendar[Calendar.YEAR]) && day > 59) newDay -= 1
                realDay = newDay
            }
        }
        return realDay
    }

    private fun pasha(day: Int): Int {
        val year = Calendar.getInstance()[Calendar.YEAR]
        var dataP: Int
        val monthP: Int
        val a = year % 19
        val b = year % 4
        val cx = year % 7
        val k = year / 100
        val p = (13 + 8 * k) / 25
        val q = k / 4
        val m = (15 - p + k - q) % 30
        val n = (4 + k - q) % 7
        val d = (19 * a + m) % 30
        val ex = (2 * b + 4 * cx + 6 * d + n) % 7
        if (d + ex <= 9) {
            dataP = d + ex + 22
            monthP = Calendar.MARCH
        } else {
            dataP = d + ex - 9
            if (d == 29 && ex == 6) dataP = 19
            if (d == 28 && ex == 6) dataP = 18
            monthP = Calendar.APRIL
        }
        val gCalendar = GregorianCalendar(year, monthP, dataP)
        gCalendar.add(Calendar.DATE, day)
        return gCalendar[Calendar.DAY_OF_YEAR]
    }
}

data class SlugbovyiaTextuData(
    val day: Int, val title: String, val resource: String, val sluzba: Int, val pasxa: Boolean = false, val mineia: Int = SlugbovyiaTextu.MINEIA_MESIACHNAIA
)
