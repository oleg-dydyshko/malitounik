package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.DialogEditviewDisplayBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class DialogPasochnicaMkDir : DialogFragment() {
    private var mListener: DialogPasochnicaMkDirListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var dir = ""
    private var oldName = ""
    private var newName = ""
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogPasochnicaMkDirListener {
        fun setDir(dir: String, oldName: String, newName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPasochnicaMkDirListener
            } catch (_: ClassCastException) {
                throw ClassCastException("$context must implement DialogPasochnicaMkDirListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", binding.content.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(layoutInflater)
            builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.set_file_dir)
            if (savedInstanceState != null) {
                binding.content.setText(savedInstanceState.getString("fileName"))
            }
            dir = arguments?.getString("dir", "") ?: ""
            oldName = arguments?.getString("oldName", "") ?: ""
            newName = arguments?.getString("newName", "") ?: ""
            binding.content.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendMkDirPostRequest()
                    dialog?.cancel()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int ->
                sendMkDirPostRequest()
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun sendMkDirPostRequest() {
        val dirName = binding.content.text.toString()
        if (dirName != "") {
            activity?.let { fragmentActivity ->
                if (Settings.isNetworkAvailable(fragmentActivity)) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val localFile = File("${fragmentActivity.filesDir}/cache/cache.txt")
                            MainActivity.referens.child("/admin/piasochnica/$oldName").getFile(localFile).addOnFailureListener {
                                Toast.makeText(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                            }.await()
                            MainActivity.referens.child("/$dir/$dirName/$newName").putFile(Uri.fromFile(localFile)).await()
                        } catch (_: Throwable) {
                            activity?.let {
                                Toast.makeText(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                            }
                        }
                        mListener?.setDir("/$dir/$dirName/", oldName, newName)
                    }
                } else {
                    Toast.makeText(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun getInstance(dir: String, oldName: String, newName: String): DialogPasochnicaMkDir {
            val instance = DialogPasochnicaMkDir()
            val args = Bundle()
            args.putString("dir", dir)
            args.putString("oldName", oldName)
            args.putString("newName", newName)
            instance.arguments = args
            return instance
        }
    }
}