package by.carkva_gazeta.malitounik2.views

import android.content.SharedPreferences
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AllDestinations.AKAFIST_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIJATEKA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIJATEKA_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.CYTANNI_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR_YEAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.MAE_NATATKI_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.MALITVY_LIST_ALL
import by.carkva_gazeta.malitounik2.views.AllDestinations.MALITVY_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.RUJANEC_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.SEARCH_BIBLIA
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
}

class AppNavigationActions(private val navController: NavHostController, k: SharedPreferences) {
    private val edit = k.edit()
    fun navigateToKaliandar() {
        navController.navigate(KALIANDAR) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: KALIANDAR) {
                inclusive = true
            }
        }
        edit.putString("navigate", KALIANDAR)
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

    fun navigateToKaliandarYear() {
        navController.navigate(KALIANDAR_YEAR) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: KALIANDAR_YEAR) {
                inclusive = true
            }
        }
        edit.putString("navigate", KALIANDAR_YEAR)
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

    fun navigateToBiblijateka(title: String, fileName: String) {
        navController.navigate("$BIBLIJATEKA/$title/$fileName") {
            BIBLIJATEKA
        }
    }

    fun navigateToCytanniList(title: String, cytanne: String, biblia: Int, perevod: String, position: Int) {
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
