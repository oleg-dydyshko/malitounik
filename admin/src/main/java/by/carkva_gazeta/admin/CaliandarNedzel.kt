package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import by.carkva_gazeta.admin.databinding.CaliandarNedelBinding
import by.carkva_gazeta.admin.databinding.CaliandarNedzeliaBinding
import by.carkva_gazeta.malitounik.Settings
import java.util.Calendar
import java.util.GregorianCalendar

class CaliandarNedzel : BaseFragment(), AdapterView.OnItemClickListener {
    private var year = 0
    private var mun = 0
    private var dateInt = 0
    private val niadzelia = ArrayList<ArrayList<String>>()
    private var _binding: CaliandarNedzeliaBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        year = arguments?.getInt("year") ?: Settings.GET_CALIANDAR_YEAR_MIN
        mun = arguments?.getInt("mun") ?: 0
        dateInt = arguments?.getInt("date") ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CaliandarNedzeliaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        niadzelia.addAll(MenuCaliandar.getDataCalaindar(dateInt, mun, year))
        activity?.let {
            binding.listView.adapter = CaliandarNedzelListAdapter(it, niadzelia)
            binding.listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_default)
        }
        val c = GregorianCalendar(year, mun, dateInt)
        val cReal = Calendar.getInstance()
        cReal[Calendar.DATE] = dateInt
        if (c[Calendar.YEAR] == cReal[Calendar.YEAR] && c[Calendar.DAY_OF_YEAR] == cReal[Calendar.DAY_OF_YEAR]) {
            binding.listView.setSelection(c[Calendar.DAY_OF_WEEK] - 1)
        }
        binding.listView.isVerticalScrollBarEnabled = false
        binding.listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent()
        intent.putExtra("position", niadzelia[position][25].toInt())
        activity?.let {
            it.setResult(Activity.RESULT_OK, intent)
            it.finish()
        }
    }

    private class CaliandarNedzelListAdapter(private val mContext: Activity, private val niadzelia: ArrayList<ArrayList<String>>) : ArrayAdapter<ArrayList<String>>(mContext, R.layout.caliandar_nedel, niadzelia) {
        private val c = Calendar.getInstance()
        private val munName = mContext.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
        private val nedelName = mContext.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.dni_nedeli)

        override fun getView(position: Int, rootView: View?, parent: ViewGroup): View {
            val view: View
            val viewHolder: ViewHolder
            if (rootView == null) {
                val binding = CaliandarNedelBinding.inflate(mContext.layoutInflater, parent, false)
                view = binding.root
                viewHolder = ViewHolder(binding.textCalendar, binding.textCviatyGlavnyia, binding.textSviatyia, binding.textPost, binding.linearView, binding.textCviatyGosud, binding.textCviatyRKC)
                view.tag = viewHolder
            } else {
                view = rootView
                viewHolder = view.tag as ViewHolder
            }
            viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDivider))
            viewHolder.textSviatyia.visibility = View.VISIBLE
            viewHolder.textCviatyGlavnyia.visibility = View.GONE
            viewHolder.textPost.visibility = View.GONE
            viewHolder.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            viewHolder.textCviatyGlavnyia.typeface = createFont(Typeface.BOLD)
            viewHolder.textSviatyGosud.visibility = View.GONE
            viewHolder.textSviatyRKC.visibility = View.GONE
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getInt("pkc", 0) == 1 && niadzelia[position][19].isNotEmpty()) {
                viewHolder.textSviatyRKC.text = niadzelia[position][19]
                viewHolder.textSviatyRKC.visibility = View.VISIBLE
            }
            if (k.getInt("gosud", 0) == 1 && niadzelia[position][15].isNotEmpty()) {
                viewHolder.textSviatyGosud.text = niadzelia[position][15]
                viewHolder.textSviatyGosud.visibility = View.VISIBLE
            }
            if (c[Calendar.YEAR] == niadzelia[position][3].toInt() && c[Calendar.DATE] == niadzelia[position][1].toInt() && c[Calendar.MONTH] == niadzelia[position][2].toInt()) {
                viewHolder.linearView.setBackgroundResource(R.drawable.calendar_nedel_today)
            } else {
                viewHolder.linearView.setBackgroundResource(R.drawable.selector_default)
            }
            if (niadzelia[position][3].toInt() != c[Calendar.YEAR]) viewHolder.textCalendar.text = mContext.getString(by.carkva_gazeta.malitounik.R.string.tydzen_name3, nedelName[niadzelia[position][0].toInt()], niadzelia[position][1], munName[niadzelia[position][2].toInt()], niadzelia[position][3])
            else viewHolder.textCalendar.text = mContext.getString(by.carkva_gazeta.malitounik.R.string.tydzen_name2, nedelName[niadzelia[position][0].toInt()], niadzelia[position][1], munName[niadzelia[position][2].toInt()])
            val sviatyia = niadzelia[position][4]
            viewHolder.textSviatyia.text = HtmlCompat.fromHtml(sviatyia, HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (niadzelia[position][4].contains("no_sviatyia")) viewHolder.textSviatyia.visibility = View.GONE
            viewHolder.textCviatyGlavnyia.text = niadzelia[position][6]
            if (!niadzelia[position][6].contains("no_sviaty")) viewHolder.textCviatyGlavnyia.visibility = View.VISIBLE // убот = субота
            if (niadzelia[position][6].contains("Пачатак") || niadzelia[position][6].contains("Вялікі") || niadzelia[position][6].contains("Вялікая") || niadzelia[position][6].contains("убот") || niadzelia[position][6].contains("ВЕЧАР") || niadzelia[position][6].contains("Палова")) {
                viewHolder.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textCviatyGlavnyia.typeface = createFont(Typeface.NORMAL)
            }
            when (niadzelia[position][7].toInt()) {
                1 -> {
                    viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
                    viewHolder.textPost.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textPost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
                    viewHolder.textPost.text = mContext.resources.getString(by.carkva_gazeta.malitounik.R.string.No_post_n)
                }

                2 -> {
                    viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
                    viewHolder.textPost.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textPost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
                    viewHolder.textPost.text = mContext.resources.getString(by.carkva_gazeta.malitounik.R.string.Post)
                }

                3 -> {
                    viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                    viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
                }
            }
            if (niadzelia[position][5].contains("1") || niadzelia[position][5].contains("2") || niadzelia[position][5].contains("3")) {
                viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            }
            if (niadzelia[position][5].contains("2")) {
                viewHolder.textCviatyGlavnyia.typeface = createFont(Typeface.NORMAL)
            }
            if (niadzelia[position][7].contains("3")) {
                viewHolder.textPost.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                viewHolder.textPost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
                viewHolder.textPost.text = mContext.resources.getString(by.carkva_gazeta.malitounik.R.string.Strogi_post_n)
                viewHolder.textPost.visibility = View.VISIBLE
            } else if (niadzelia[position][0].contains("6") && !(niadzelia[position][5].contains("1") || niadzelia[position][5].contains("2"))) { // Пятница
                viewHolder.textPost.visibility = View.VISIBLE
            }
            return view
        }

        fun createFont(style: Int): Typeface? {
            return when (style) {
                Typeface.BOLD -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik.R.font.roboto_condensed_bold)
                Typeface.ITALIC -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik.R.font.roboto_condensed_italic)
                Typeface.BOLD_ITALIC -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik.R.font.roboto_condensed_bold_italic)
                else -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik.R.font.roboto_condensed_regular)
            }
        }
    }

    private class ViewHolder(var textCalendar: TextView, var textCviatyGlavnyia: TextView, var textSviatyia: TextView, var textPost: TextView, var linearView: LinearLayout, var textSviatyGosud: TextView, var textSviatyRKC: TextView)

    companion object {
        fun newInstance(year: Int, mun: Int, date: Int): CaliandarNedzel {
            val fragment = CaliandarNedzel()
            val args = Bundle()
            args.putInt("year", year)
            args.putInt("mun", mun)
            args.putInt("date", date)
            fragment.arguments = args
            return fragment
        }
    }
}