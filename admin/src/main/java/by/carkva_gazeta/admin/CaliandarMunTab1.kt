package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.admin.databinding.CalendarTab1Binding
import by.carkva_gazeta.malitounik.Settings
import java.util.Calendar

class CaliandarMunTab1 : BaseFragment() {
    private val names get() = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac2)
    private var day = 0
    private var posMun = 0
    private var yearG = 0
    private var _binding: CalendarTab1Binding? = null
    private val binding get() = _binding!!
    private var munListener: CaliandarMunTab1Listener? = null

    interface CaliandarMunTab1Listener {
        fun setDayAndMun1(day: Int, mun: Int, year: Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            munListener = try {
                context as CaliandarMunTab1Listener
            } catch (_: ClassCastException) {
                throw ClassCastException("$activity must implement CaliandarMunTab1Listener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        day = arguments?.getInt("day") ?: 0
        posMun = arguments?.getInt("posMun") ?: 0
        yearG = arguments?.getInt("yearG") ?: 0
    }

    fun setDataCalendar(dataCalendar: Int) {
        val c = Calendar.getInstance()
        if (dataCalendar >= Settings.GET_CALIANDAR_YEAR_MIN) {
            yearG = dataCalendar
            if (yearG == c[Calendar.YEAR]) {
                binding.year.typeface = createFont(Typeface.BOLD)
            } else {
                binding.year.typeface = createFont(Typeface.NORMAL)
            }
            binding.year.text = yearG.toString()
        } else {
            posMun = dataCalendar
            if (posMun == c[Calendar.MONTH] && yearG == c[Calendar.YEAR]) {
                binding.mun.typeface = createFont(Typeface.BOLD)
            } else {
                binding.mun.typeface = createFont(Typeface.NORMAL)
            }
            binding.mun.text = names[posMun]
        }
        val son1 = (yearG - Settings.GET_CALIANDAR_YEAR_MIN) * 12 + posMun
        binding.pager.setCurrentItem(son1, false)
        munListener?.setDayAndMun1(day, posMun, yearG)
    }

    fun createFont(style: Int): Typeface? {
        return when (style) {
            Typeface.BOLD -> ResourcesCompat.getFont(requireActivity(), by.carkva_gazeta.malitounik.R.font.robotocondensedbold)
            Typeface.ITALIC -> ResourcesCompat.getFont(requireActivity(), by.carkva_gazeta.malitounik.R.font.robotocondenseditalic)
            Typeface.BOLD_ITALIC -> ResourcesCompat.getFont(requireActivity(), by.carkva_gazeta.malitounik.R.font.robotocondensedbolditalic)
            else -> ResourcesCompat.getFont(requireActivity(), by.carkva_gazeta.malitounik.R.font.robotocondensed)
        }
    }

    private fun showDialog(data: Int) {
        val dialogCaliandarMunDate = DialogCaliandarMunDate.getInstance(data)
        dialogCaliandarMunDate.show(childFragmentManager, "dialogCaliandarMunDate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalendarTab1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val c = Calendar.getInstance()
        if (posMun == c[Calendar.MONTH] && yearG == c[Calendar.YEAR]) {
            binding.mun.typeface = createFont(Typeface.BOLD)
        }
        if (yearG == c[Calendar.YEAR]) {
            binding.year.typeface = createFont(Typeface.BOLD)
        }
        binding.mun.text = names[posMun]
        binding.year.text = yearG.toString()
        binding.mun.setOnClickListener {
            showDialog(posMun)
        }
        binding.year.setOnClickListener {
            showDialog(yearG)
        }
        binding.pager.offscreenPageLimit = 1
        val adapterViewPager = MyPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.pager.adapter = adapterViewPager

        val son = (yearG - Settings.GET_CALIANDAR_YEAR_MIN) * 12 + posMun
        binding.pager.setCurrentItem(son, false)
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val caliandarMun = MenuCaliandar.getPositionCaliandarMun(position)
                yearG = caliandarMun[3].toInt()
                posMun = caliandarMun[2].toInt()
                if (posMun == c[Calendar.MONTH] && yearG == c[Calendar.YEAR]) {
                    binding.mun.typeface = createFont(Typeface.BOLD)
                } else {
                    binding.mun.typeface = createFont(Typeface.NORMAL)
                }
                if (yearG == c[Calendar.YEAR]) {
                    binding.year.typeface = createFont(Typeface.BOLD)
                } else {
                    binding.year.typeface = createFont(Typeface.NORMAL)
                }
                binding.mun.text = names[posMun]
                binding.year.text = yearG.toString()
                munListener?.setDayAndMun1(day, posMun, yearG)
            }
        })
    }

    private class MyPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount() = (Settings.GET_CALIANDAR_YEAR_MAX - Settings.GET_CALIANDAR_YEAR_MIN + 1) * 12

        override fun createFragment(position: Int): PageFragmentMonth {
            val caliandarMun = MenuCaliandar.getPositionCaliandarMun(position)
            return PageFragmentMonth.newInstance(caliandarMun[1].toInt(), caliandarMun[2].toInt(), caliandarMun[3].toInt())
        }
    }

    companion object {
        fun getInstance(posMun: Int, yearG: Int, day: Int): CaliandarMunTab1 {
            val frag = CaliandarMunTab1()
            val bundle = Bundle()
            bundle.putInt("posMun", posMun)
            bundle.putInt("yearG", yearG)
            bundle.putInt("day", day)
            frag.arguments = bundle
            return frag
        }
    }
}