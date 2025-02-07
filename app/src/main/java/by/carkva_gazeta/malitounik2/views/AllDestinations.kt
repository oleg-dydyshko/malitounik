package by.carkva_gazeta.malitounik2.views

import androidx.navigation.NavHostController
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_BOKUNA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_CEMUXA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_CHARNIAUSKI
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_NADSAN
import by.carkva_gazeta.malitounik2.views.AllDestinations.BIBLIA_SINODAL
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA
import by.carkva_gazeta.malitounik2.views.AllDestinations.BOGASLUJBOVYIA_MENU
import by.carkva_gazeta.malitounik2.views.AllDestinations.CYTANNI_LIST
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.KALIANDAR_YEAR
import by.carkva_gazeta.malitounik2.views.AllDestinations.VYBRANAE_LIST

object AllDestinations {
    const val KALIANDAR = "Kaliandar"
    const val BOGASLUJBOVYIA_MENU = "Bogaslujbovyia_Menu"
    const val BOGASLUJBOVYIA = "Bogaslujbovyia"
    const val KALIANDAR_YEAR = "Kaliandar_Year"
    const val CYTANNI_LIST = "Cytanni_List"
    const val BIBLIA_CEMUXA = "Biblia_Cemuxa"
    const val BIBLIA_BOKUNA = "Biblia_Bokuna"
    const val BIBLIA_CHARNIAUSKI = "Biblia_Charniauski"
    const val BIBLIA_NADSAN = "Biblia_Nadsan"
    const val BIBLIA_SINODAL = "Biblia_Sinodal"
    const val BIBLIA_LIST = "Biblia_List"
    const val VYBRANAE_LIST = "Bybranae_List"
}

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateToKaliandar() {
        navController.navigate(KALIANDAR) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: KALIANDAR) {
                inclusive = true
            }
        }
    }

    fun navigateToBogaslujbovyia() {
        navController.navigate(BOGASLUJBOVYIA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BOGASLUJBOVYIA) {
                inclusive = true
            }
        }
    }

    fun navigateToKaliandarYear() {
        navController.navigate(KALIANDAR_YEAR) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: KALIANDAR_YEAR) {
                inclusive = true
            }
        }
    }

    fun navigateToBibliaCemuxa() {
        navController.navigate(BIBLIA_CEMUXA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_CEMUXA) {
                inclusive = true
            }
        }
    }

    fun navigateToBibliaBokuna() {
        navController.navigate(BIBLIA_BOKUNA) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_BOKUNA) {
                inclusive = true
            }
        }
    }

    fun navigateToBibliaCharniauski() {
        navController.navigate(BIBLIA_CHARNIAUSKI) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_CHARNIAUSKI) {
                inclusive = true
            }
        }
    }

    fun navigateToBibliaNadsan() {
        navController.navigate(BIBLIA_NADSAN) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_NADSAN) {
                inclusive = true
            }
        }
    }

    fun navigateToBibliaSinodal() {
        navController.navigate(BIBLIA_SINODAL) {
            popUpTo(navController.currentBackStackEntry?.destination?.route ?: BIBLIA_SINODAL) {
                inclusive = true
            }
        }
    }

    fun navigateToCytanniList(title: String, cytanne: String, biblia: Int, perevod: String, count: Int) {
        navController.navigate("$CYTANNI_LIST/$cytanne/$title/$biblia/$perevod/$count") {
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
    }

    fun navigateToBogaslujbovyiaMenu() {
        navController.navigate(BOGASLUJBOVYIA_MENU) {
            BOGASLUJBOVYIA_MENU
        }
    }
}
