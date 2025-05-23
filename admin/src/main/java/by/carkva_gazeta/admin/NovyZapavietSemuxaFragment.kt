package by.carkva_gazeta.admin

import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import by.carkva_gazeta.admin.databinding.AdminBiblePageFragmentBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class NovyZapavietSemuxaFragment : BaseFragment() {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var _binding: AdminBiblePageFragmentBinding? = null
    private val binding get() = _binding!!
    private var urlJob: Job? = null
    private var mLastClickTime: Long = 0
    private var bibleJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        urlJob?.cancel()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        bibleJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kniga = arguments?.getInt("kniga") ?: 0
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pazicia") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AdminBiblePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_save) {
            sendPostRequest(kniga + 1, binding.textView.text.toString(), page + 1)
            return true
        }
        return false
    }

    private suspend fun saveLogFile(count: Int = 0) {
        activity?.let { activity ->
            val logFile = File("${activity.filesDir}/cache/log.txt")
            var error = false
            logFile.writer().use {
                it.write(getString(by.carkva_gazeta.malitounik.R.string.check_update_resourse))
            }
            MainActivity.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
                Toast.makeText(activity, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                error = true
            }.await()
            if (error && count < 3) {
                saveLogFile(count + 1)
            }
        }
    }

    private fun sendPostRequest(id: Int, spaw: String, sv: Int) {
        activity?.let { activity ->
            if (Settings.isNetworkAvailable(activity)) {
                bibleJob = CoroutineScope(Dispatchers.Main).launch {
                    _binding?.progressBar2?.visibility = View.VISIBLE
                    val localFile = File("${activity.filesDir}/cache/cache.txt")
                    val zag = "Разьдзел"
                    MainActivity.referens.child("/chytanne/Semucha/biblian$id.txt").getFile(localFile).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val file = localFile.readText()
                            val file2 = file.split("===")
                            val fileNew = StringBuilder()
                            for ((count, element) in file2.withIndex()) {
                                val fil = element.trim()
                                var stringraz = ""
                                if (fil != "") {
                                    if (count != 0) {
                                        stringraz = "===\n"
                                    }
                                    if (count == sv) {
                                        fileNew.append(stringraz + spaw.trim() + "\n\n// " + zag + " " + (sv + 1) + "\n")
                                    } else {
                                        fileNew.append(stringraz + fil + "\n")
                                    }
                                }
                            }
                            localFile.writer().use {
                                it.write(fileNew.toString())
                            }
                        } else {
                            Toast.makeText(activity, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                    saveLogFile()
                    MainActivity.referens.child("/chytanne/Semucha/biblian$id.txt").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, getString(by.carkva_gazeta.malitounik.R.string.save), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }.await()
                    _binding?.progressBar2?.visibility = View.GONE
                }
            } else {
                Toast.makeText(activity, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Settings.isNetworkAvailable(requireActivity())) {
            binding.progressBar2.visibility = View.VISIBLE
            var url = "/chytanne/Semucha/biblian1.txt"
            when (kniga) {
                0 -> url = "/chytanne/Semucha/biblian1.txt"
                1 -> url = "/chytanne/Semucha/biblian2.txt"
                2 -> url = "/chytanne/Semucha/biblian3.txt"
                3 -> url = "/chytanne/Semucha/biblian4.txt"
                4 -> url = "/chytanne/Semucha/biblian5.txt"
                5 -> url = "/chytanne/Semucha/biblian6.txt"
                6 -> url = "/chytanne/Semucha/biblian7.txt"
                7 -> url = "/chytanne/Semucha/biblian8.txt"
                8 -> url = "/chytanne/Semucha/biblian9.txt"
                9 -> url = "/chytanne/Semucha/biblian10.txt"
                10 -> url = "/chytanne/Semucha/biblian11.txt"
                11 -> url = "/chytanne/Semucha/biblian12.txt"
                12 -> url = "/chytanne/Semucha/biblian13.txt"
                13 -> url = "/chytanne/Semucha/biblian14.txt"
                14 -> url = "/chytanne/Semucha/biblian15.txt"
                15 -> url = "/chytanne/Semucha/biblian16.txt"
                16 -> url = "/chytanne/Semucha/biblian17.txt"
                17 -> url = "/chytanne/Semucha/biblian18.txt"
                18 -> url = "/chytanne/Semucha/biblian19.txt"
                19 -> url = "/chytanne/Semucha/biblian20.txt"
                20 -> url = "/chytanne/Semucha/biblian21.txt"
                21 -> url = "/chytanne/Semucha/biblian22.txt"
                22 -> url = "/chytanne/Semucha/biblian23.txt"
                23 -> url = "/chytanne/Semucha/biblian24.txt"
                24 -> url = "/chytanne/Semucha/biblian25.txt"
                25 -> url = "/chytanne/Semucha/biblian26.txt"
                26 -> url = "/chytanne/Semucha/biblian27.txt"
            }
            urlJob = CoroutineScope(Dispatchers.Main).launch {
                activity?.let { fragmentActivity ->
                    val sb = StringBuilder()
                    try {
                        val localFile = File("${fragmentActivity.filesDir}/cache/cache.txt")
                        MainActivity.referens.child(url).getFile(localFile).addOnCompleteListener { it ->
                            if (it.isSuccessful) {
                                if (localFile.length() != 0L) {
                                    val text = localFile.readText()
                                    val split = text.split("===")
                                    val knig = split[page + 1]
                                    val split2 = knig.split("\n")
                                    split2.forEach {
                                        val t1 = it.indexOf("//")
                                        if (t1 != -1) {
                                            sb.append(it.substring(0, t1)).append("\n")
                                        } else {
                                            sb.append(it).append("\n")
                                        }
                                    }
                                } else {
                                    context?.let {
                                        Toast.makeText(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                context?.let {
                                    Toast.makeText(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.await()
                    } catch (_: Throwable) {
                        context?.let {
                            Toast.makeText(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error_ch2), Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.textView.setText(sb.toString().trim())
                    binding.progressBar2.visibility = View.GONE
                }
            }
        } else {
            activity?.let {
                Toast.makeText(it, getString(by.carkva_gazeta.malitounik.R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(page: Int, kniga: Int, pazicia: Int): NovyZapavietSemuxaFragment {
            val fragmentFirst = NovyZapavietSemuxaFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}