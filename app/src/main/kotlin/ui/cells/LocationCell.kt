package ui.cells

import android.graphics.*
import support.component.AndroidUtilities

/**
 * Created by yaya-mh on 29/09/2018 09:44 AM
 */
class LocationCell {
    var radius = AndroidUtilities.dp(3f).toFloat()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var point = PointF(0f,0f)
    init {
        paint.strokeWidth = AndroidUtilities.dp(2f).toFloat()
        paint.color = Color.rgb(50,200,60)
    }
    constructor(point: PointF) {
        this.point = point
    }
    fun draw(canvas: Canvas){
        canvas.drawCircle(point.x, point.y, radius, paint)
    }

}