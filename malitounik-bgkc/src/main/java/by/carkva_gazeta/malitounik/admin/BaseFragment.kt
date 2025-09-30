package by.carkva_gazeta.malitounik.admin

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

abstract class BaseFragment : Fragment(), MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        (activity as? BaseActivity)?.let {
            for (i in 0 until menu.size) {
                val item = menu[i]
                val spanString = SpannableString(menu[i].title.toString())
                val end = spanString.length
                var itemFontSize = it.setFontInterface()
                if (itemFontSize > 22f) itemFontSize = 18f
                spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
        }
    }

    override fun onMenuItemSelected(item: MenuItem) = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}