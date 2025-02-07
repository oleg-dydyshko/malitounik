package by.carkva_gazeta.malitounik2

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

fun biblia(
    context: Context,
    kniga: Int,
    glavaStart: Int,
    glavaEnd: Int,
    styxStart: Int,
    styxEnd: Int,
    perevod: String
): ArrayList<String> {
    var perevodNew = perevod
    val result = ArrayList<String>()
    if (glavaStart == 0) return result
    val fields = R.raw::class.java.fields
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
    var fileName = 0
    if (perevod == Settings.PEREVODNADSAN) {
        fileName = R.raw.psaltyr_nadsan
    } else {
        val prevodName = when (perevodNew) {
            Settings.PEREVODSEMUXI -> "biblia"
            Settings.PEREVODBOKUNA -> "bokuna"
            Settings.PEREVODCARNIAUSKI -> "carniauski"
            Settings.PEREVODSINOIDAL -> "sinaidal"
            else -> "biblia"
        }
        for (element in fields) {
            val name = element.name
            if (name == "$prevodName$zavet${knigaNew + 1}") {
                fileName = element.getInt(name)
                break
            }
        }
    }
    if (fileName != 0) {
        val inputStream =
            context.resources.openRawResource(fileName)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val sb = StringBuilder()
        reader.forEachLine {
            line = it
            if (line.contains("//")) {
                val t1 = line.indexOf("//")
                line = line.substring(0, t1).trim()
                if (line != "") sb.append(line).append("\n")
            } else {
                sb.append(line).append("\n")
            }
        }
        val listGlav = sb.toString().split("===")
        for (e in glavaStart..glavaEnd) {
            val spisStyxov = listGlav[e].trim().split("\n")
            if (e == glavaStart) {
                for (i in spisStyxov.indices) {
                    if (styxStart != 0 && styxEnd != 0 && glavaEnd == glavaStart) {
                        if (styxStart - 1 <= i && styxEnd > i) result.add(
                            spisStyxov[i].replace(
                                "\\n",
                                "<br>"
                            )
                        )
                    } else {
                        if (styxStart != 0) {
                            if (styxStart - 1 <= i) result.add(spisStyxov[i].replace("\\n", "<br>"))
                        } else {
                            result.add(spisStyxov[i].replace("\\n", "<br>"))
                        }
                    }
                }
            }
            if (e != glavaStart && e != glavaEnd) {
                for (i in spisStyxov.indices) {
                    result.add(spisStyxov[i].replace("\\n", "<br>"))
                }
            }
            if (e == glavaEnd && glavaEnd != glavaStart) {
                for (i in spisStyxov.indices) {
                    if (styxEnd != 0) {
                        if (styxEnd > i) result.add(spisStyxov[i].replace("\\n", "<br>"))
                    } else {
                        result.add(spisStyxov[i].replace("\\n", "<br>"))
                    }
                }
            }
        }
    } else {
        val inputStream =
            context.resources.openRawResource(R.raw.biblia_error)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        result.add(reader.readText())
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
    return ""
}