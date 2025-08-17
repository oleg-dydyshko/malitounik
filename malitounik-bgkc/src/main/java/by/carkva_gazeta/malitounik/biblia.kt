package by.carkva_gazeta.malitounik

import android.content.Context
import by.carkva_gazeta.malitounik.views.openAssetsResources

fun biblia(
    context: Context, kniga: Int, glavaStart: Int, glavaEnd: Int, styxStart: Int, styxEnd: Int, perevod: String
): ArrayList<BibliaDataItem> {
    var perevodNew = perevod
    val result = ArrayList<BibliaDataItem>()
    if (glavaStart == 0) return result
    var knigaNew = getRealBook(kniga, perevodNew)
    if (knigaNew == -1) {
        perevodNew = Settings.PEREVODCARNIAUSKI
        knigaNew = getRealBook(kniga, perevodNew)
    }
    if (knigaNew == -1) {
        perevodNew = Settings.PEREVODSINOIDAL
        knigaNew = getRealBook(kniga, perevodNew)
    }
    val zavet = if (kniga >= 50) {
        "n"
    } else {
        "s"
    }
    var fileName: String
    if (perevod == Settings.PEREVODNADSAN) {
        fileName = "chytanne/psaltyr_nadsan.txt"
    } else {
        val prevodName = when (perevodNew) {
            Settings.PEREVODSEMUXI -> {
                "chytanne/Semucha/biblia"
            }

            Settings.PEREVODBOKUNA -> {
                "chytanne/Bokun/bokuna"
            }

            Settings.PEREVODCARNIAUSKI -> {
                "chytanne/Carniauski/carniauski"
            }

            Settings.PEREVODSINOIDAL -> {
                "chytanne/Sinodal/sinaidal"
            }

            else -> {
                "chytanne/Semucha/biblia"
            }
        }
        fileName = "$prevodName$zavet${knigaNew + 1}.txt"
    }
    val isPsaltyrGreek = perevodNew == Settings.PEREVODSEMUXI || perevodNew == Settings.PEREVODNADSAN || perevodNew == Settings.PEREVODSINOIDAL
    val listGlav = openAssetsResources(context, fileName).split("===")
    for (glava in glavaStart..glavaEnd) {
        val spisStyxov = listGlav[glava].trim().split("\n")
        if (glava == glavaStart) {
            for (styx in spisStyxov.indices) {
                if (styxStart != 0 && styxEnd != 0 && glavaEnd == glavaStart) {
                    if (styxStart - 1 <= styx && styxEnd > styx) result.add(
                        BibliaDataItem(
                            glava, spisStyxov[styx].replace(
                                "\\n", "<br>"
                            ), getParalel(kniga, glava, styx + 1, isPsaltyrGreek)
                        )
                    )
                } else {
                    if (styxStart != 0) {
                        if (styxStart - 1 <= styx) result.add(
                            BibliaDataItem(
                                glava, spisStyxov[styx].replace(
                                    "\\n", "<br>"
                                ), getParalel(kniga, glava, styx + 1, isPsaltyrGreek)
                            )
                        )
                    } else {
                        result.add(
                            BibliaDataItem(
                                glava, spisStyxov[styx].replace("\\n", "<br>"), getParalel(kniga, glava, styx + 1, isPsaltyrGreek)
                            )
                        )
                    }
                }
            }
        }
        if (glava != glavaStart && glava != glavaEnd) {
            for (styx in spisStyxov.indices) {
                result.add(
                    BibliaDataItem(
                        glava, spisStyxov[styx].replace("\\n", "<br>"), getParalel(kniga, glava, styx + 1, isPsaltyrGreek)
                    )
                )
            }
        }
        if (glava == glavaEnd && glavaEnd != glavaStart) {
            for (styx in spisStyxov.indices) {
                if (styxEnd != 0) {
                    if (styxEnd > styx) result.add(
                        BibliaDataItem(
                            glava, spisStyxov[styx].replace(
                                "\\n", "<br>"
                            ), getParalel(kniga, glava, styx + 1, isPsaltyrGreek)
                        )
                    )
                } else {
                    result.add(
                        BibliaDataItem(
                            glava, spisStyxov[styx].replace("\\n", "<br>"), getParalel(kniga, glava, styx + 1, isPsaltyrGreek)
                        )
                    )
                }
            }
        }
    }
    return result
}

fun getRealBook(kniga: Int, perevod: String): Int {
    var knigaNew = kniga
    if (kniga >= 50) {
        knigaNew = kniga - 50
    }
    if (perevod == Settings.PEREVODCARNIAUSKI && kniga >= 50) {
        knigaNew = when (knigaNew) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            12 -> 5
            13 -> 6
            14 -> 7
            15 -> 8
            16 -> 9
            17 -> 10
            18 -> 11
            19 -> 12
            20 -> 13
            21 -> 14
            22 -> 15
            23 -> 16
            24 -> 17
            25 -> 18
            5 -> 19
            6 -> 20
            7 -> 21
            8 -> 22
            9 -> 23
            10 -> 24
            11 -> 25
            26 -> 26
            else -> -1
        }
    }
    if ((perevod == Settings.PEREVODSEMUXI || perevod == Settings.PEREVODBOKUNA) && kniga < 50) {
        knigaNew = when (knigaNew) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            5 -> 5
            6 -> 6
            7 -> 7
            8 -> 8
            9 -> 9
            10 -> 10
            11 -> 11
            12 -> 12
            13 -> 13
            14 -> 14
            15 -> 15
            19 -> 16
            20 -> 17
            21 -> 18
            22 -> 19
            23 -> 20
            24 -> 21
            27 -> 22
            28 -> 23
            29 -> 24
            32 -> 25
            33 -> 26
            34 -> 27
            35 -> 28
            36 -> 29
            37 -> 30
            38 -> 31
            39 -> 32
            40 -> 33
            41 -> 34
            42 -> 35
            43 -> 36
            44 -> 37
            45 -> 38
            else -> -1
        }
    }
    if (perevod == Settings.PEREVODCARNIAUSKI && kniga < 50) {
        knigaNew = when (knigaNew) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            5 -> 5
            6 -> 6
            7 -> 7
            8 -> 8
            9 -> 9
            10 -> 10
            11 -> 11
            12 -> 12
            13 -> 13
            14 -> 14
            15 -> 15
            19 -> 16
            20 -> 17
            21 -> 18
            22 -> 19
            23 -> 20
            24 -> 21
            27 -> 22
            28 -> 23
            29 -> 24
            32 -> 25
            33 -> 26
            34 -> 27
            35 -> 28
            36 -> 29
            37 -> 30
            38 -> 31
            39 -> 32
            40 -> 33
            41 -> 34
            42 -> 35
            43 -> 36
            44 -> 37
            45 -> 38
            17 -> 39
            18 -> 40
            25 -> 41
            26 -> 42
            31 -> 43
            46 -> 44
            47 -> 45
            else -> -1
        }
    }
    return knigaNew
}

fun getNameBook(context: Context, kniga: Int, perevod: String, novyZapavet: Boolean): String {
    if (perevod == Settings.PEREVODSINOIDAL) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.sinoidaln)
        } else context.resources.getStringArray(R.array.sinoidals)
        return bookList[kniga]
    }
    if (perevod == Settings.PEREVODSEMUXI) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.semuxan)
        } else {
            context.resources.getStringArray(R.array.semuxas)
        }
        return bookList[kniga]
    }
    if (perevod == Settings.PEREVODBOKUNA) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.bokunan)
        } else {
            context.resources.getStringArray(R.array.bokunas)
        }
        return bookList[kniga]
    }
    if (perevod == Settings.PEREVODCARNIAUSKI) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.charniauskin)
        } else {
            context.resources.getStringArray(R.array.charniauskis)
        }
        return bookList[kniga]
    }
    if (perevod == Settings.PEREVODNADSAN) {
        return context.resources.getStringArray(R.array.psalter_list)[0]
    }
    return ""
}

fun getNameBook(context: Context, perevod: String, novyZapavet: Boolean): Array<String> {
    if (perevod == Settings.PEREVODSINOIDAL) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.sinoidaln)
        } else context.resources.getStringArray(R.array.sinoidals)
        return bookList
    }
    if (perevod == Settings.PEREVODSEMUXI) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.semuxan)
        } else {
            context.resources.getStringArray(R.array.semuxas)
        }
        return bookList
    }
    if (perevod == Settings.PEREVODBOKUNA) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.bokunan)
        } else {
            context.resources.getStringArray(R.array.bokunas)
        }
        return bookList
    }
    if (perevod == Settings.PEREVODCARNIAUSKI) {
        val bookList = if (novyZapavet) {
            context.resources.getStringArray(R.array.charniauskin)
        } else {
            context.resources.getStringArray(R.array.charniauskis)
        }
        return bookList
    }
    if (perevod == Settings.PEREVODNADSAN) {
        return context.resources.getStringArray(R.array.psalter_list)
    }
    return arrayOf("")
}

data class BibliaDataItem(val glava: Int, val styx: String, val paralelStyx: String)