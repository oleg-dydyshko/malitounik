package by.carkva_gazeta.malitounik2.views

import android.content.SharedPreferences
import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.CYTANNI_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR_YEAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.MALITVY_LIST_ALL
import by.carkva_gazeta.malitounik2.views.AllDestinations.MALITVY_MENU
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
