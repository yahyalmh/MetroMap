package ui.cells.sataionCells

import android.content.Context
import android.graphics.Canvas
import support.component.AndroidUtilities
import ui.views.GuideBox

/**
 * Created by yaya-mh on 01/08/2018 09:15 AM 09:15 AM.
 */

class SingleStationCell : StationCell {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isGuideViewed) {
            (0 until childCount)
                    .map { getChildAt(it) }
                    /* .filterIsInstance<GuideBox>()*/
                    .forEach { it.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(15f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(15f), MeasureSpec.EXACTLY)) }
        }
        setMeasuredDimension(AndroidUtilities.dp(120f), AndroidUtilities.dp(120f))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isGuideViewed) {
            for (i in 0 until childCount) {
                val childAt = getChildAt(i)
                val x = (right - left - paddingLeft - paddingRight) / 2
                val y = (bottom - top - paddingBottom - paddingTop) / 2
                if (childAt is GuideBox) {
                    when (i) {
                        0 -> {
                            val xChild = x - radius - childAt.measuredWidth - AndroidUtilities.dp(2f)
                            val yChild = y - childAt.measuredHeight / 2
                            childAt.layout(xChild.toInt(), yChild, (xChild + childAt.measuredWidth).toInt(), yChild + childAt.measuredHeight)
                        }
                        1 -> {
                            val xChild = x - childAt.measuredWidth / 2
                            val yChild = y - radius - childAt.measuredHeight - AndroidUtilities.dp(2f)
                            childAt.layout(xChild, yChild.toInt(), xChild + childAt.measuredWidth, (yChild + childAt.measuredHeight).toInt())
                        }
                        2 -> {
                            val xChild = x + radius + AndroidUtilities.dp(2f)
                            val yChild = y - childAt.measuredHeight / 2
                            childAt.layout(xChild.toInt(), yChild, (xChild + childAt.measuredWidth).toInt(), yChild + childAt.measuredHeight)
                        }
                    }
                } else {
                    val xChild = x - childAt.measuredWidth / 2
                    val yChild = y - childAt.measuredHeight
                    childAt.layout(xChild, yChild, (xChild + childAt.measuredWidth), yChild + childAt.measuredHeight)
                }
            }
        }
    }

    constructor(context: Context, stationName: String) : super(context, stationName)
    constructor(context: Context, stationName: String, color: Int) : super(context, stationName, color)
    constructor(stationCell: StationCell) : super(stationCell)
    constructor(context: Context, stationName: String, cx: Float, cy: Float, color: Int) : super(context, stationName, cx, cy, color)
    constructor(context: Context, stationName: String, cx: Float, cy: Float, color: Int, lineNumber: Int) : super(context, stationName, cx, cy, color, lineNumber)

    override fun onDraw(canvas: Canvas?) {

        centerPoint.x = (right - left.toFloat()) / 2
        centerPoint.y = (bottom - top.toFloat()) / 2
        stationNamePoint = stationNamePointCal()
        canvas?.drawText(stationName, stationNamePoint.x, stationNamePoint.y, textPaint)
        canvas?.drawCircle((right - left.toFloat()) / 2, (bottom - top.toFloat()) / 2, radius.toFloat(), circlePaint)
    }

}