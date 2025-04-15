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

class DialogIsHtml : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogIsHtmlListener? = null

    interface DialogIsHtmlListener {
        fun pasochnica(isHtml: Boolean, saveAs: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogIsHtmlListener
            } catch (_: ClassCastException) {
                throw ClassCastException("$activity must implement DialogIsHtmlListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.is_html_title)
            binding.content.setText(by.carkva_gazeta.malitounik.R.string.is_html)
            binding.content.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            builder.setView(binding.root)
            val saveAs = arguments?.getBoolean("saveAs") != false
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.sabytie_yes)) { _: DialogInterface, _: Int ->
                mListener?.pasochnica(true, saveAs)
            }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.sabytie_no)) { _: DialogInterface, _: Int ->
                mListener?.pasochnica(false, saveAs)
            }
            builder.setNeutralButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(saveAs: Boolean): DialogIsHtml {
            val bundle = Bundle()
            bundle.putBoolean("saveAs", saveAs)
            val dialog = DialogIsHtml()
            dialog.arguments = bundle
            return dialog
        }
    }
}