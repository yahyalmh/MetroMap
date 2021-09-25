/*
 * Copyright Nikolai Kudashov, 2013-2018.
 */
package ui.cells

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.yaya.map.R
import support.LayoutHelper
import support.LocaleController
import support.LocaleController.LocaleInfo
import support.Theme
import support.component.AndroidUtilities

class LanguageCell(context: Context?) : FrameLayout(context!!) {
    private val textView: TextView
    private val textView2: TextView
    private val checkImage: ImageView
    private var needDivider = false
    private var currentLocale: LocaleInfo? = null
    private val isDialog: Boolean
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(if (isDialog) 50f else 54f) + if (needDivider) 1 else 0, MeasureSpec.EXACTLY))
    }

    fun setLanguage(language: LocaleInfo, desc: String?, divider: Boolean) {
        textView.text = desc ?: language.name
        textView2.setText(language.nameEnglish)
        currentLocale = language
        needDivider = divider
    }

    fun setValue(name: String?, nameEnglish: String?) {
        textView.text = name
        textView2.text = nameEnglish
        checkImage.visibility = View.INVISIBLE
        currentLocale = null
        needDivider = false
    }

    fun getCurrentLocale(): LocaleInfo? {
        return currentLocale
    }

    fun setLanguageSelected(value: Boolean) {
        checkImage.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

    override fun onDraw(canvas: Canvas) {
        if (needDivider) {
            canvas.drawLine(if (LocaleController.isRTL) 0f else AndroidUtilities.dp(20f).toFloat(), measuredHeight - 1.toFloat(), measuredWidth - (if (LocaleController.isRTL) AndroidUtilities.dp(20f) else 0).toFloat(), measuredHeight - 1.toFloat(), Theme.dividerPaint)
        }
    }

    init {
        setWillNotDraw(false)
        isDialog = false
        val dialog = isDialog
        textView = TextView(context)
        textView.setTextColor(Theme.getColor(if (dialog) Theme.key_dialogTextBlack else Theme.key_windowBackgroundWhiteBlackText))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textView.setLines(1)
        textView.maxLines = 1
        textView.setSingleLine(true)
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.gravity = (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT) or Gravity.TOP
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT) or Gravity.TOP, if (LocaleController.isRTL) 23 + 48f else 23f, if (isDialog) 4f else 7f, if (LocaleController.isRTL) 23f else 23 + 48f, 0f))
        textView2 = TextView(context)
        textView2.setTextColor(Theme.getColor(if (dialog) Theme.key_dialogTextGray3 else Theme.key_windowBackgroundWhiteGrayText3))
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        textView2.setLines(1)
        textView2.maxLines = 1
        textView2.setSingleLine(true)
        textView2.ellipsize = TextUtils.TruncateAt.END
        textView2.gravity = (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT) or Gravity.TOP
        addView(textView2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT) or Gravity.TOP, if (LocaleController.isRTL) 23 + 48f else 23f, if (isDialog) 25f else 29f, if (LocaleController.isRTL) 23f else 23 + 48f, 0f))
        checkImage = ImageView(context)
        checkImage.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY)
        checkImage.setImageResource(R.drawable.sticker_added)
        addView(checkImage, LayoutHelper.createFrame(19, 14f, (if (LocaleController.isRTL) Gravity.LEFT else Gravity.RIGHT) or Gravity.CENTER_VERTICAL, 23f, 0f, 23f, 0f))
    }
}