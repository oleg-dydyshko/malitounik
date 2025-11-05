package by.carkva_gazeta.malitounik.views

import android.content.SharedPreferences
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik.cytanniListItemData
import by.carkva_gazeta.malitounik.views.AllDestinations.AKAFIST_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_BOKUNA
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_CATOLIK
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_CHARNIAUSKI
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_LIST
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_NADSAN
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_SEMUXA
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIA_SINODAL
import by.carkva_gazeta.malitounik.views.AllDestinations.BIBLIJATEKA
import by.carkva_gazeta.malitounik.views.AllDestinations.BOGASLUJBOVYIA
import by.carkva_gazeta.malitounik.views.AllDestinations.BOGASLUJBOVYIA_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.CHASASLOU_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.CYTANNI_LIST
import by.carkva_gazeta.malitounik.views.AllDestinations.CYTATY_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.HELP
import by.carkva_gazeta.malitounik.views.AllDestinations.KALIANDAR
import by.carkva_gazeta.malitounik.views.AllDestinations.KALIANDAR_YEAR
import by.carkva_gazeta.malitounik.views.AllDestinations.LITURGIKON_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.MAE_NATATKI_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.MALITVY_LIST_ALL
import by.carkva_gazeta.malitounik.views.AllDestinations.MALITVY_MENU
import by.carkva_gazeta.malitounik.views.AllDestinations.PADZEI_VIEW
import by.carkva_gazeta.malitounik.views.AllDestinations.PIASOCHNICA
import by.carkva_gazeta.malitounik.views.AllDestinations.PIASOCHNICA_LIST
import by.carkva_gazeta.malitounik.views.AllDestinations.PRANAS
import by.carkva_gazeta.malitounik.views.AllDestinations.SEARCH_BIBLIA
import by.carkva_gazeta.malitounik.views.AllDestinations.SETTINGS_VIEW
import by.carkva_gazeta.malitounik.views.AllDestinations.SVITYIA_VIEW
import by.carkva_gazeta.malitounik.views.AllDestinations.UMOUNIA_ZNACHENNI
import by.carkva_gazeta.malitounik.views.AllDestinations.UNDER_PADRYXTOUKA
import by.carkva_gazeta.malitounik.views.AllDestinations.UNDER_PAMIATKA
import by.carkva_gazeta.malitounik.views.AllDestinations.UNDER_PARAFII_BGKC
import by.carkva_gazeta.malitounik.views.AllDestinations.UNDER_PASHALIA
import by.carkva_gazeta.malitounik.views.AllDestinations.UNDER_SVAITY_MUNU
import by.carkva_gazeta.malitounik.views.AllDestinations.VYBRANAE_LIST
import java.net.URLEncoder

object AllDestinations {
    const val KALIANDAR = "Kaliandar"
    const val BOGASLUJBOVYIA_MENU = "Bogaslujbovyia_Menu"
    const val BOGASLUJBOVYIA = "Bogaslujbovyia"
    const val MALITVY_MENU = "Malitvy_Menu"
    const val MALITVY_LIST_ALL = "Malitvy_List_All"
    const val KALIANDAR_YEAR = "Kaliandar_Year"
    const val CYTANNI_LIST = "Cytanni_List"
    const val BIBLIA_SEMUXA = "Biblia_Semuxa"
    const val BIBLIA_BOKUNA = "Biblia_Bokuna"
    const val BIBLIA_NADSAN = "Biblia_Nadsan"
    const val BIBLIA_CHARNIAUSKI = "Biblia_Charniauski"
    const val BIBLIA_SINODAL = "Biblia_Sinodal"
    const val BIBLIA_CATOLIK = "Biblia_Catolik"
    const val BIBLIA_LIST = "Biblia_List"
    const val VYBRANAE_LIST = "Bybranae_List"
    const val SEARCH_BIBLIA = "Search_Biblia"
    const val AKAFIST_MENU = "Akafist_Menu"
    const val CHASASLOU_MENU = "Chasaslou_Menu"
    const val MAE_NATATKI_MENU = "Mae_Natatki_Menu"
    const val BIBLIJATEKA = "Biblijateka"
    const val BIBLIJATEKA_NIADAUNIA = "Biblijateka_Naidaunia"
    const val BIBLIJATEKA_GISTORYIA = "Biblijateka_Gistoryia"
    const val BIBLIJATEKA_MALITOUNIKI = "Biblijateka_Malitouniki"
    const val BIBLIJATEKA_SPEUNIKI = "Biblijateka_Speuniki"
    const val BIBLIJATEKA_RELIGIJNAIA_LITARATURA = "Biblijateka_Peligijnaia_Litaratura"
    const val BIBLIJATEKA_ARXIU_NUMAROU = "Biblijateka_Arxiu_Numarou"
    const val PIESNY_PRASLAULENNIA = "Piesny_Raslaulennia"
    const val PIESNY_ZA_BELARUS = "Piesny_Za_Belarus"
    const val PIESNY_DA_BAGARODZICY = "Piesny_Da_Bagarodzicy"
    const val PIESNY_KALIADNYIA = "Piesny_Kaliadnyia"
    const val PIESNY_TAIZE = "Piesny_Taize"
    const val UNDER_PAMIATKA = "Under_Pamiatka"
    const val UNDER_PADRYXTOUKA = "Under_Padryxtouka"
    const val UNDER_SVAITY_MUNU = "Under_Svity_Menu"
    const val UNDER_PARAFII_BGKC = "Under_Parafii_Bgkc"
    const val UNDER_PASHALIA = "Under_Pashalia"
    const val UMOUNIA_ZNACHENNI = "Shto_Novaga"
    const val PRANAS = "PraNas"
    const val HELP = "Help"
    const val SETTINGS_VIEW = "Settings_View"
    const val PADZEI_VIEW = "Padzei_View"
    const val SVITYIA_VIEW = "Svityia_View"
    const val LITURGIKON_MENU = "Liturgikon_Menu"
    const val CYTATY_MENU = "cytaty"
    const val PIASOCHNICA_LIST = "Piasochnica_List"
    const val PIASOCHNICA = "Piasochnica"
}

class AppNavigationActions(private val navController: NavHostController, k: SharedPreferences) {
    private val edit = k.edit()
    fun navigateToKaliandar() {
        navController.navigate(KALIANDAR) {
            val navigate = if (navController.currentBackStackEntry?.destination?.route == "Search_Svityia" || navController.currentBackStackEntry?.destination?.route == "Settings_View") KALIANDAR
            else navController.currentBackStackEntry?.destination?.route
            popUpTo(navigate ?: KALIANDAR) {
                inclusive = true
            }
        }
        edit.putString("navigate", KALIANDAR)
        edit.apply()
    }

    fun navigateToKaliandarYear() {
        navController.navigate(KALIANDAR_YEAR) {
            val navigate = if (navController.currentBackStackEntry?.destination?.route == "Search_Svityia" || navController.currentBackStackEntry?.destination?.route == "Settings_View") KALIANDAR
            else navController.currentBackStackEntry?.destination?.route
            popUpTo(navigate ?: KALIANDAR_YEAR) {
                inclusive = true
            }
        }
        edit.putString("navigate", KALIANDAR_YEAR)
        edit.apply()
    }

    fun navigateToAkafistMenu() {
        navController.navigate(AKAFIST_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: AKAFIST_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", AKAFIST_MENU)
        edit.apply()
    }

    fun navigateToChasaslouMenu() {
        navController.navigate(CHASASLOU_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: CHASASLOU_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", CHASASLOU_MENU)
        edit.apply()
    }

    fun navigateToLiturgikonMenu() {
        navController.navigate(LITURGIKON_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: LITURGIKON_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", LITURGIKON_MENU)
        edit.apply()
    }

    fun navigateToMaeNatatkiMenu() {
        navController.navigate(MAE_NATATKI_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: MAE_NATATKI_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", MAE_NATATKI_MENU)
        edit.apply()
    }

    fun navigateToBogaslujbovyiaMenu() {
        navController.navigate(BOGASLUJBOVYIA_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BOGASLUJBOVYIA_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", BOGASLUJBOVYIA_MENU)
        edit.apply()
    }

    fun navigateToMalitvyMenu() {
        navController.navigate(MALITVY_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: MALITVY_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", MALITVY_MENU)
        edit.apply()
    }

    fun navigateToBibliaSemuxa() {
        navController.navigate(BIBLIA_SEMUXA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_SEMUXA) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA_SEMUXA)
        edit.apply()
    }

    fun navigateToBibliaBokuna() {
        navController.navigate(BIBLIA_BOKUNA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_BOKUNA) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA_BOKUNA)
        edit.apply()
    }

    fun navigateToBibliaNadsan() {
        navController.navigate(BIBLIA_NADSAN) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_NADSAN) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA_NADSAN)
        edit.apply()
    }

    fun navigateToBibliaCharniauski() {
        navController.navigate(BIBLIA_CHARNIAUSKI) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_CHARNIAUSKI) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA_CHARNIAUSKI)
        edit.apply()
    }

    fun navigateToBibliaCatolik() {
        navController.navigate(BIBLIA_CATOLIK) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_CATOLIK) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA_CATOLIK)
        edit.apply()
    }

    fun navigateToBibliaSinodal() {
        navController.navigate(BIBLIA_SINODAL) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_SINODAL) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA_SINODAL)
        edit.apply()
    }

    fun navigateToBiblijatekaList(biblijateka: String) {
        navController.navigate(biblijateka) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: biblijateka) {
                inclusive = true
            }
        }
        edit.putString("navigate", biblijateka)
        edit.apply()
    }

    fun navigateToPiesnyList(piesny: String) {
        navController.navigate(piesny) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: piesny) {
                inclusive = true
            }
        }
        edit.putString("navigate", piesny)
        edit.apply()
    }

    fun navigateToBiblijateka(title: String, fileName: String) {
        navController.navigate("$BIBLIJATEKA/$title/$fileName") {
            BIBLIJATEKA
        }
    }

    fun navigateToUmouniaZnachenni() {
        navController.navigate(UMOUNIA_ZNACHENNI) {
            UMOUNIA_ZNACHENNI
        }
    }

    fun navigateToPamiatka() {
        navController.navigate(UNDER_PAMIATKA) {
            UNDER_PAMIATKA
        }
    }

    fun navigateToPraNas() {
        navController.navigate(PRANAS) {
            PRANAS
        }
    }

    fun navigateToHelp() {
        navController.navigate(HELP) {
            HELP
        }
    }

    fun navigateToPadryxtouka() {
        navController.navigate(UNDER_PADRYXTOUKA) {
            UNDER_PADRYXTOUKA
        }
    }

    fun navigateToSettingsView() {
        navController.navigate(SETTINGS_VIEW) {
            SETTINGS_VIEW
        }
    }

    fun navigateToPadzeiView() {
        navController.navigate(PADZEI_VIEW) {
            PADZEI_VIEW
        }
    }
    fun navigateToSvityiaView(svity: Boolean, position: Int) {
        navController.navigate("$SVITYIA_VIEW/$svity/$position") {
            SVITYIA_VIEW
        }
    }

    fun navigateToSviaty() {
        navController.navigate(UNDER_SVAITY_MUNU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: UNDER_SVAITY_MUNU) {
                inclusive = true
            }
        }
        edit.putString("navigate", UNDER_SVAITY_MUNU)
        edit.apply()
    }

    fun navigateToParafiiBgkc() {
        navController.navigate(UNDER_PARAFII_BGKC) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: UNDER_PARAFII_BGKC) {
                inclusive = true
            }
        }
        edit.putString("navigate", UNDER_PARAFII_BGKC)
        edit.apply()
    }

    fun navigateToPashalia() {
        navController.navigate(UNDER_PASHALIA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: UNDER_PASHALIA) {
                inclusive = true
            }
        }
        edit.putString("navigate", UNDER_PASHALIA)
        edit.apply()
    }

    fun navigateToCytanniList(title: String, cytanne: String, biblia: Int, perevod: String, position: Int) {
        cytanniListItemData.value.clear()
        navController.navigate("$CYTANNI_LIST/$cytanne/$title/$biblia/$perevod/$position") {
            CYTANNI_LIST
        }
    }

    fun navigateToBibliaList(novyZapavet: Boolean, perevod: String) {
        navController.navigate("$BIBLIA_LIST/$novyZapavet/$perevod") {
            BIBLIA_LIST
        }
    }

    fun navigateToVybranaeList() {
        navController.navigate(VYBRANAE_LIST) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: VYBRANAE_LIST) {
                inclusive = true
            }
        }
        edit.putString("navigate", VYBRANAE_LIST)
        edit.apply()
    }

    fun navigateToSearchBiblia(perevod: String, searchBogaslujbovyia: Boolean) {
        navController.navigate("$SEARCH_BIBLIA/$perevod/$searchBogaslujbovyia") {
            SEARCH_BIBLIA
        }
    }

    fun navigateToMalitvyListAll(title: String, menuItem: Int, subTitle: String = "") {
        navController.navigate("$MALITVY_LIST_ALL/$title/$menuItem/$subTitle") {
            MALITVY_LIST_ALL
        }
    }

    fun navigateToBogaslujbovyia(title: String, resurs: String) {
        navController.navigate("$BOGASLUJBOVYIA/$title/" + URLEncoder.encode(resurs, "UTF8")) {
            BOGASLUJBOVYIA
        }
    }

    fun navigateToCytaty() {
        navController.navigate(CYTATY_MENU) {
            CYTATY_MENU
        }
    }

    fun navigateToPiasochnicaList(dirToFile: String = "") {
        navController.navigate("PIASOCHNICA_LIST/" + URLEncoder.encode(dirToFile, "UTF8")) {
            PIASOCHNICA_LIST
        }
    }

    fun navigateToPiasochnica(resurs: String) {
        navController.navigate("$PIASOCHNICA/" + URLEncoder.encode(resurs, "UTF8")) {
            PIASOCHNICA
        }
    }
}
