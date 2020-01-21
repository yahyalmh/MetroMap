package ui.cells

import android.graphics.Canvas
import android.graphics.Paint
import support.component.AndroidUtilities
import support.Theme

/**
* Created by yaya-mh on 23/07/2018 09:02 AM.
*/
open class StationLine : Cloneable{

    protected var color : Int = Theme.getColor(Theme.key_avatar_backgroundGreen)
    var paint  = Paint(Paint.ANTI_ALIAS_FLAG)
    var left: Int = 0
    var top: Int = 0
    var right: Int = 0
    var bottom: Int = 0
    var radius : Int = AndroidUtilities.dp(3f)
    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = AndroidUtilities.dp(2F).toFloat()
    }
    constructor(color: Int)  {
        this.color = color
        paint.color = color
    }
    constructor(stationLine: StationLine){
        left = stationLine.left
        top = stationLine.top
        right = stationLine.right
        bottom = stationLine.bottom
//        paint = stationLine.paint
    }
    /*constructor(context:Context, left : Int, top: Int, right: Int,  bottom : Int,  color: Int):this(context, color){
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }*/

    fun draw(canvas: Canvas?){
        canvas?.drawLine(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
    }

    public override fun clone(): StationLine{
        val copyLine = super.clone() as StationLine
        copyLine.paint = Paint(Paint.ANTI_ALIAS_FLAG)
        copyLine.paint.style = Paint.Style.STROKE
        copyLine.paint.strokeWidth = AndroidUtilities.dp(2F).toFloat()
        return copyLine
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StationLine

        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false

        return true
    }


    override fun toString(): String {
        return "StationLine(left=$left, top=$top, right=$right, bottom=$bottom)"
    }

    override fun hashCode(): Int {
        var result = color
        result = 31 * result + paint.hashCode()
        result = 31 * result + left
        result = 31 * result + top
        result = 31 * result + right
        result = 31 * result + bottom
        return result
    }


}