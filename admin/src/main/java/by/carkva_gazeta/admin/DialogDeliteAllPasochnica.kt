package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.DialogTextviewDisplayBinding

class DialogDeliteAllPasochnica : DialogFragment() {
    private lateinit var mListener: DialogDeliteAllPasochnicaListener
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogDeliteAllPasochnicaListener {
        fun deliteAllPasochnica()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteAllPasochnicaListener
            } catch (_: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteAllPasochnicaListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.remove)
            binding.content.text = getString(by.carkva_gazeta.malitounik.R.string.del_all_pasochnica)
            binding.content.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int -> mListener.deliteAllPasochnica() }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }
}