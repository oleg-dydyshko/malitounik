package by.carkva_gazeta.malitounik2.views

import android.content.SharedPreferences
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.cytanniListItemData
import by.carkva_gazeta.malitounik2.views.AllDestinations.AKAFIST_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIJATEKA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIJATEKA_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.CYTANNI_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.HELP
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR_YEAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.MAE_NATATKI_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.MALITVY_LIST_ALL
import by.carkva_gazeta.malitounik2.views.AllDestinations.MALITVY_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.PADRYXTOUKA
import by.carkva_gazeta.malitounik2.views.AllDestinations.PAMIATKA
import by.carkva_gazeta.malitounik2.views.AllDestinations.PARAFII_BGKC
import by.carkva_gazeta.malitounik2.views.AllDestinations.PASHALIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.PIESNY_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.PRANAS
import by.carkva_gazeta.malitounik2.views.AllDestinations.RUJANEC_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.SEARCH_BIBLIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.SEARCH_SVITYIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.SETTINGS_VIEW
import by.carkva_gazeta.malitounik2.views.AllDestinations.SVAITY_MUNU
import by.carkva_gazeta.malitounik2.views.AllDestinations.VYBRANAE_LIST

object AllDestinations {
    const val KALIANDAR = "Kaliandar"
    const val BOGASLUJBOVYIA_MENU = "Bogaslujbovyia_Menu"
    const val BOGASLUJBOVYIA = "Bogaslujbovyia"
    const val MALITVY_MENU = "Malitvy_Menu"
    const val MALITVY_LIST_ALL = "Malitvy_List_All"
    const val KALIANDAR_YEAR = "Kaliandar_Year"
    const val CYTANNI_LIST = "Cytanni_List"
    const val BIBLIA = "Biblia"
    const val BIBLIA_LIST = "Biblia_List"
    const val VYBRANAE_LIST = "Bybranae_List"
    const val SEARCH_BIBLIA = "Search_Biblia"
    const val AKAFIST_MENU = "Akafist_Menu"
    const val RUJANEC_MENU = "Rujanec_Menu"
    const val MAE_NATATKI_MENU = "Mae_Natatki_Menu"
    const val BIBLIJATEKA_LIST = "Biblijateka_List"
    const val BIBLIJATEKA = "Biblijateka"
    const val PIESNY_LIST = "Piesny_List"
    const val PAMIATKA = "Pamiatka"
    const val PADRYXTOUKA = "Padryxtouka"
    const val SVAITY_MUNU = "Svity_Menu"
    const val PARAFII_BGKC = "Parafii_Bgkc"
    const val PASHALIA = "Pashalia"
    const val PRANAS = "PraNas"
    const val HELP = "Help"
    const val SEARCH_SVITYIA = "Search_Svityia"
    const val SETTINGS_VIEW = "Settings_View"
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

    fun navigateToRujanecMenu() {
        navController.navigate(RUJANEC_MENU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: RUJANEC_MENU) {
                inclusive = true
            }
        }
        edit.putString("navigate", RUJANEC_MENU)
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

    fun navigateToBiblia() {
        navController.navigate(BIBLIA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIA)
        edit.apply()
    }

    fun navigateToBiblijatekaList() {
        navController.navigate(BIBLIJATEKA_LIST) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIJATEKA_LIST) {
                inclusive = true
            }
        }
        edit.putString("navigate", BIBLIJATEKA_LIST)
        edit.apply()
    }

    fun navigateToPiesnyList() {
        navController.navigate(PIESNY_LIST) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: PIESNY_LIST) {
                inclusive = true
            }
        }
        edit.putString("navigate", PIESNY_LIST)
        edit.apply()
    }

    fun navigateToBiblijateka(title: String, fileName: String) {
        navController.navigate("$BIBLIJATEKA/$title/$fileName") {
            BIBLIJATEKA
        }
    }

    fun navigateToPamiatka() {
        navController.navigate(PAMIATKA) {
            PAMIATKA
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

    fun navigateToSearchSvityia() {
        navController.navigate(SEARCH_SVITYIA) {
            SEARCH_SVITYIA
        }
    }

    fun navigateToPadryxtouka() {
        navController.navigate(PADRYXTOUKA) {
            PADRYXTOUKA
        }
    }

    fun navigateToSettingsView() {
        navController.navigate(SETTINGS_VIEW) {
            SETTINGS_VIEW
        }
    }

    fun navigateToSviaty() {
        navController.navigate(SVAITY_MUNU) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: SVAITY_MUNU) {
                inclusive = true
            }
        }
        edit.putString("navigate", SVAITY_MUNU)
        edit.apply()
    }

    fun navigateToParafiiBgkc() {
        navController.navigate(PARAFII_BGKC) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: PARAFII_BGKC) {
                inclusive = true
            }
        }
        edit.putString("navigate", PARAFII_BGKC)
        edit.apply()
    }

    fun navigateToPashalia() {
        navController.navigate(PASHALIA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: PASHALIA) {
                inclusive = true
            }
        }
        edit.putString("navigate", PASHALIA)
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

    fun navigateToSearchBiblia(perevod: String) {
        navController.navigate("$SEARCH_BIBLIA/$perevod") {
            SEARCH_BIBLIA
        }
    }

    fun navigateToMalitvyListAll(title: String, menuItem: Int, subTitle: String = "") {
        navController.navigate("$MALITVY_LIST_ALL/$title/$menuItem/$subTitle") {
            MALITVY_LIST_ALL
        }
    }

    fun navigateToBogaslujbovyia(title: String, resurs: Int) {
        navController.navigate("$BOGASLUJBOVYIA/$title/$resurs") {
            BOGASLUJBOVYIA
        }
    }
}
