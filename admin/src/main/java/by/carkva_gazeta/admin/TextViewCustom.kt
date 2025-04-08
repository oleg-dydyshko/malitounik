package by.carkva_gazeta.admin

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView

class TextViewCustom : AppCompatTextView {

    constructor(context: Context) : super(context) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        setFontInterface(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFontInterface(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setFontInterface(context)
    }

    private fun setFontInterface(context: Context) {
        val sp = (context as? BaseActivity)?.setFontInterface() ?: 20f
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
    }
}