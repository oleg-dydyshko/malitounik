package by.carkva_gazeta.malitounik.admin

import by.carkva_gazeta.malitounik.Settings
import java.util.Calendar

class MenuCaliandar {
    companion object {

        init {
            Settings.dataCaliandar()
        }

        fun getPositionCaliandar(position: Int): ArrayList<String> {
            return Settings.data[position]
        }

        fun getPositionCaliandarMun(position: Int): ArrayList<String> {
            var pos = 0
            Settings.data.forEach {
                if (it[23].toInt() == position) {
                    pos = it[25].toInt()
                    return@forEach
                }
            }
            return Settings.data[pos]
        }

        fun getPositionCaliandarNiadzel(day: Int, mun: Int, year: Int): Int {
            var position = 0
            Settings.data.forEach { arrayList ->
                if (day == arrayList[1].toInt() && mun == arrayList[2].toInt() && year == arrayList[3].toInt()) {
                    position = arrayList[26].toInt()
                    return@forEach
                }
            }
            return position
        }

        fun getFirstPositionNiadzel(position: Int): ArrayList<String> {
            var pos = 0
            Settings.data.forEach {
                if (it[26].toInt() == position && it[0].toInt() == Calendar.SUNDAY) {
                    pos = it[25].toInt()
                    return@forEach
                }
            }
            return Settings.data[pos]
        }

        fun getDataCalaindar(dayOfMun: Int = -1, mun: Int = -1, year: Int = -1, dayOfYear: Int = -1): ArrayList<ArrayList<String>> {
            when {
                dayOfMun != -1 && mun != -1 && year != -1 -> {
                    val niadzeliaList = ArrayList<ArrayList<String>>()
                    var count = 0
                    Settings.data.forEach { arrayList ->
                        if (dayOfMun == arrayList[1].toInt() && mun == arrayList[2].toInt() && year == arrayList[3].toInt()) {
                            count++
                            if (arrayList[26].toInt() == 0) count = arrayList[0].toInt()
                        }
                        if (count in 1..7) {
                            niadzeliaList.add(arrayList)
                            count++
                        }
                        if (count == 8) return@forEach
                    }
                    return niadzeliaList
                }

                mun != -1 && year != -1 -> {
                    val munList = ArrayList<ArrayList<String>>()
                    Settings.data.forEach { arrayList ->
                        if (mun == arrayList[2].toInt() && year == arrayList[3].toInt()) {
                            munList.add(arrayList)
                        }
                    }
                    return munList
                }

                dayOfYear != -1 && year != -1 -> {
                    val dayList = ArrayList<ArrayList<String>>()
                    Settings.data.forEach { arrayList ->
                        if (dayOfYear == arrayList[24].toInt() && year == arrayList[3].toInt()) {
                            dayList.add(arrayList)
                            return@forEach
                        }
                    }
                    return dayList
                }

                year != -1 -> {
                    val yearList = ArrayList<ArrayList<String>>()
                    Settings.data.forEach { arrayList ->
                        if (year == arrayList[3].toInt()) {
                            yearList.add(arrayList)
                        }
                    }
                    return yearList
                }

                dayOfMun != -1 -> {
                    val dayList = ArrayList<ArrayList<String>>()
                    val g = Calendar.getInstance()
                    Settings.data.forEach { arrayList ->
                        if (dayOfMun == arrayList[1].toInt() && g[Calendar.MONTH] == arrayList[2].toInt() && g[Calendar.YEAR] == arrayList[3].toInt()) {
                            dayList.add(arrayList)
                            return@forEach
                        }
                    }
                    return dayList
                }

                else -> return Settings.data
            }
        }
    }
}