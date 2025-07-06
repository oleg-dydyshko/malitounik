package by.carkva_gazeta.malitounik

data class SviatyDataM(var data: Int, var mun: Int, var dataCaliandar: Int, var opisanie: String) {
    companion object {
        const val CALAINDAR = 0
        const val PASHA = 1
        const val UNDER = 2
    }
}