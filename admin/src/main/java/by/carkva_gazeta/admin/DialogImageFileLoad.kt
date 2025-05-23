package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.scale
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminDialogImageLoadBinding
import by.carkva_gazeta.admin.databinding.SimpleListItem1Binding
import java.io.File

class DialogImageFileLoad : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var _binding: AdminDialogImageLoadBinding? = null
    private val binding get() = _binding!!
    private var arrayList = ArrayList<String>()
    private var mListener: DialogFileExplorerListener? = null

    internal interface DialogFileExplorerListener {
        fun onDialogFile(absolutePath: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogFileExplorerListener
            } catch (_: ClassCastException) {
                throw ClassCastException("$context must implement DialogFileExplorerListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = AdminDialogImageLoadBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.setView(binding.root)
            arrayList.add("Дадаць ікону па чарзе")
            arrayList.add("Перазапісаць 1 ікону")
            arrayList.add("Перазапісаць 2 ікону")
            arrayList.add("Перазапісаць 3 ікону")
            arrayList.add("Перазапісаць 4 ікону")
            binding.title.text = "ЗАХАВАЦЬ ІКОНУ"
            val arrayAdapter = ListAdapter(it)
            binding.content.adapter = arrayAdapter
            val path = arguments?.getString("path") ?: ""
            val file = File(path)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.icon.setImageBitmap(bitmap.scale(600, 1000, false))
            //Picasso.get().load(file).resize(600, 1000).onlyScaleDown().centerInside().into(binding.icon)
            builder.setNegativeButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.save_sabytie)) { _: DialogInterface?, _: Int ->
                mListener?.onDialogFile(path)
            }
            alert = builder.create()
        }
        return alert
    }

    private inner class ListAdapter(mContext: Activity) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_1, arrayList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem1Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = arrayList[position]
            viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val text = v as TextView
            text.text = arrayList[position]
            text.setBackgroundResource(R.drawable.selector_default)
            return v
        }
    }

    companion object {
        fun getInstance(path: String): DialogImageFileLoad {
            val dialogImageFileLoad = DialogImageFileLoad()
            val bundle = Bundle()
            bundle.putString("path", path)
            dialogImageFileLoad.arguments = bundle
            return dialogImageFileLoad
        }
    }

    private class ViewHolder(var text: TextView)
}