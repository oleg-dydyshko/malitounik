package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.DialogListviewDisplayBinding
import by.carkva_gazeta.admin.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik2.Settings
import java.util.Calendar

class DialogCaliandarMunDate : DialogFragment() {
    private var mListener: DialogCaliandarMunDateListener? = null
    private var data = 0
    private lateinit var alert: AlertDialog
    private var _binding: DialogListviewDisplayBinding? = null
    private val binding get() = _binding!!

    interface DialogCaliandarMunDateListener {
        fun setDataCalendar(dataCalendar: Int)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getInt("data") ?: 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogCaliandarMunDateListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogCaliandarMunDateListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogListviewDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.content.selector = ContextCompat.getDrawable(it, R.drawable.selector_default)
            val arrayList = ArrayList<String>()
            if (data >= Settings.GET_CALIANDAR_YEAR_MIN) {
                binding.title.text = resources.getString(by.carkva_gazeta.malitounik2.R.string.vybor_year)
                for (i in Settings.GET_CALIANDAR_YEAR_MIN..Settings.GET_CALIANDAR_YEAR_MAX) {
                    arrayList.add(i.toString())
                }
            } else {
                binding.title.text = resources.getString(by.carkva_gazeta.malitounik2.R.string.vybor_mun)
                arrayList.addAll(it.resources.getStringArray(by.carkva_gazeta.malitounik2.R.array.meciac2))
            }
            binding.content.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                if (data >= Settings.GET_CALIANDAR_YEAR_MIN) mListener?.setDataCalendar(i + Settings.GET_CALIANDAR_YEAR_MIN)
                else mListener?.setDataCalendar(i)
                alert.cancel()
            }
            binding.content.adapter = DataListAdaprer(it, arrayList, data)
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik2.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    private class DataListAdaprer(private val mContext: Activity, private val arrayList: ArrayList<String>, private val data: Int) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_2, R.id.label, arrayList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(mContext.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = arrayList[position]
            val c = Calendar.getInstance()
            if (data >= Settings.GET_CALIANDAR_YEAR_MIN) {
                if (c[Calendar.YEAR] == position + Settings.GET_CALIANDAR_YEAR_MIN) viewHolder.text.typeface = createFont(Typeface.BOLD)
                else viewHolder.text.typeface = createFont(Typeface.NORMAL)
            } else {
                if (c[Calendar.MONTH] == position) viewHolder.text.typeface = createFont(Typeface.BOLD)
                else viewHolder.text.typeface = createFont(Typeface.NORMAL)
            }
            return rootView
        }

        fun createFont(style: Int): Typeface? {
            return when (style) {
                Typeface.BOLD -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik2.R.font.robotocondensedbold)
                Typeface.ITALIC -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik2.R.font.robotocondenseditalic)
                Typeface.BOLD_ITALIC -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik2.R.font.robotocondensedbolditalic)
                else -> ResourcesCompat.getFont(mContext, by.carkva_gazeta.malitounik2.R.font.robotocondensed)
            }
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        fun getInstance(data: Int): DialogCaliandarMunDate {
            val dialogDelite = DialogCaliandarMunDate()
            val bundle = Bundle()
            bundle.putInt("data", data)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}