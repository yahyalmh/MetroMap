package ui.cells.settingCell

import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.yaya.map.R
import support.LayoutHelper
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import support.component.CheckBox


class SubSettingCell(context: Context) : SettingBaseCell(context) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var textView: TextView
    val checkBox: CheckBox

    init {
        paint.strokeWidth = AndroidUtilities.dp(1.5f).toFloat()
        paint.color = Color.DKGRAY

        textView = TextView(context)
        textView.gravity = Gravity.START

        checkBox = CheckBox(context, R.drawable.round_check2)
        checkBox.visibility = View.VISIBLE
        checkBox.setColor(Theme.getColor(Theme.key_featuredStickers_addedIcon), Color.WHITE)

        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT) or Gravity.TOP, if (LocaleController.isRTL) 23 + 48f else 23f,  7f, if (LocaleController.isRTL) 23f else 23 + 48f, 0f))
        addView(checkBox, LayoutHelper.createFrame(22, 22f, (if (LocaleController.isRTL) Gravity.LEFT else Gravity.RIGHT) or Gravity.CENTER_VERTICAL, 23f, 0f, 23f, 0f))
        setWillNotDraw(false)
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        checkBox.setChecked(-1, checked, animated)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(45f), MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawLine(if (LocaleController.isRTL) 0f else AndroidUtilities.dp(20f).toFloat(), measuredHeight - 1.toFloat(), measuredWidth - (if (LocaleController.isRTL) AndroidUtilities.dp(20f) else 0).toFloat(), measuredHeight - 1.toFloat(), Theme.dividerPaint)

    }
}