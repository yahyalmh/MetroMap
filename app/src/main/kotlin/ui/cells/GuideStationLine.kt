package ui.cells

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities

/**
 * Created by yaya-mh on 23/07/2018 09:02 AM.
 */
class GuideStationLine(context: Context) : View(context) {

    val bound = Rect()
    var lineNumber: String? = null
    private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var lineName: String
    var linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var color: Int = Theme.getColor(Theme.key_avatar_backgroundGreen)

    init {
        textPaint.textSize = AndroidUtilities.dp(7f).toFloat()
        textPaint.color = Color.BLACK
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeWidth = AndroidUtilities.dp(3F).toFloat()
    }

    constructor(context: Context, color: Int) : this(context) {
        this.color = color
        linePaint.color = color
    }

    constructor(context: Context, lineName: String, lineNumber: String, color: Int) : this(context, color) {
        this.lineName = lineName
        this.lineNumber = lineNumber
    }

    /*constructor(context: Context, left : Int, top: Int, right: Int,  bottom : Int,  color: Int, lineNumber : String):super(context, left, top, right, bottom, color){
        textPaint.color = color
        this.lineNumber = lineNumber
    }*/

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
//            MapFragment.fra.applyScaleAndTranslation(lineName)
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        textPaint.getTextBounds(lineNumber, 0, lineNumber!!.length, bound)
        if (LocaleController.isRTL) {
            canvas?.drawText(lineNumber, (right - left.toFloat()) / 2 + AndroidUtilities.dp(5F).toFloat(), (bottom - top.toFloat()) / 2 + (bound.height() / 2), textPaint)
            canvas?.drawLine(AndroidUtilities.dp(5F).toFloat(), (bottom - top.toFloat()) / 2, ((right - left) / 2f), (bottom - top.toFloat()) / 2f, linePaint)
        } else {
            canvas?.drawText(lineNumber, AndroidUtilities.dp(5F).toFloat(), (bottom - top.toFloat()) / 2 + (bound.height() / 2), textPaint)
            canvas?.drawLine(((right - left) / 2).toFloat(), (bottom - top.toFloat()) / 2, (right - left.toFloat()) -AndroidUtilities.dp(5f), (bottom - top.toFloat()) / 2, linePaint)
        }
    }

}