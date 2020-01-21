package ui.cells.settingCell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import support.LayoutHelper
import support.component.AndroidUtilities

class SettingCell(context: Context) : SettingBaseCell(context) {
    var imageView = ImageView(context)
    var textView = TextView(context)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    init {
        paint.color = Color.GREEN
        paint.strokeWidth = 5f
        val imageWidth = AndroidUtilities.dp(20f)
        addView(imageView, LayoutHelper.createFrame(imageWidth, AndroidUtilities.dp(10f).toFloat(), Gravity.CENTER_VERTICAL, 10f, 0f,0f,0f))
        textView.gravity = Gravity.CENTER
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), Gravity.CENTER_VERTICAL, imageWidth + 15f,0f,0f,0f))
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48f), MeasureSpec.AT_MOST))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawLine(AndroidUtilities.dp(20f).toFloat(), height.toFloat(), width.toFloat(), height.toFloat(), paint)
    }
}