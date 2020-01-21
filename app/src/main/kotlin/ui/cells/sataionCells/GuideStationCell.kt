package ui.cells.sataionCells

import ui.activities.LaunchActivity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.widget.Toast
import support.LocaleController
import support.component.AndroidUtilities
import ui.activities.LocationController
import ui.cells.sataionCells.StationCell

/**
* Created by yaya-mh on 03/09/2018 02:19 PM
*/
class GuideStationCell(context: Context, stationName: String, color: Int) : StationCell(context, stationName, color) {

    val bound = Rect()

    init {
        radius = AndroidUtilities.dp(7f).toFloat()
        textPaint.textSize = AndroidUtilities.dp(8f).toFloat()
        stationNamePosition = StationCell.NamePosition.Left
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            Toast.makeText(LaunchActivity.applicationContext, "click on $stationName", Toast.LENGTH_SHORT).show()
        }
        return  true
    }

    override fun onDraw(canvas: Canvas?){
        textPaint.getTextBounds(stationName, 0, stationName.length, bound)
        if (LocaleController.isRTL){
            canvas?.drawText(stationName, (right-left)/2f , (bottom - top.toFloat()) / 2 + (bound.height() / 2), textPaint)
            canvas?.drawCircle(left + (radius + AndroidUtilities.dp(5f).toFloat()), (bottom - top.toFloat()) / 2, radius.toFloat(), circlePaint)
        }else {
            canvas?.drawText(stationName, AndroidUtilities.dp(4F).toFloat(), (bottom - top.toFloat()) / 2 + (bound.height() / 2), textPaint)
            canvas?.drawCircle(right - (radius + AndroidUtilities.dp(5f).toFloat()), (bottom - top.toFloat()) / 2, radius.toFloat(), circlePaint)
        }
    }
}