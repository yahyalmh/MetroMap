/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package ui.cells.tellCell

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import support.LayoutHelper
import support.Theme
import support.component.AndroidUtilities.Companion.dp
import support.component.AndroidUtilities.Companion.getTypeface

class DrawerActionCell(context: Context?) : FrameLayout(context) {
    private val textView: TextView
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dp(48f), MeasureSpec.EXACTLY))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText))
    }

    fun setTextAndIcon(text: String?, resId: Int) {
        try {
            textView.text = text
            val drawable = resources.getDrawable(resId)
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon), PorterDuff.Mode.MULTIPLY)
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        } catch (e: Throwable) {
        }
    }

    init {
        textView = TextView(context)
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        textView.typeface = getTypeface("fonts/rmedium.ttf")
        textView.setLines(1)
        textView.maxLines = 1
        textView.setSingleLine(true)
        textView.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        textView.compoundDrawablePadding = dp(34f)
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), Gravity.LEFT or Gravity.TOP, 14f, 0f, 16f, 0f))
    }
}