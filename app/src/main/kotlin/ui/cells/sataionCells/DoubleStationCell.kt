package ui.cells.sataionCells

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.yaya.map.R
import support.component.AndroidUtilities
import utils.MetroUtil

/**
* Created by yaya-mh on 01/08/2018 09:15 AM 09:15 AM.
*/
class DoubleStationCell : StationCell {
    private lateinit var firstLineName :String
    private lateinit var secondLineName :String
    val bound = Rect()

    private var size = 5
    private var checkDrawable: Drawable = context.resources.getDrawable(R.drawable.round_check2).mutate()
    private var checkBitmap: Bitmap?

    constructor(context: Context, stationName : String, cx : Float, cy : Float, color : Int) : super(context, stationName, cx, cy, color)
    constructor(context: Context, stationName : String) :super(context, stationName)
    constructor(context: Context, stationName : String, cx : Float, cy : Float) : this(context, stationName){
        this.cx = cx
        this.cy = cy
    }
    constructor(context: Context, stationName : String, cx : Float, cy : Float, firstLineName :String, secondLineName :String) : this(context, stationName, cx, cy){
        this.firstLineName = firstLineName
        this.secondLineName = secondLineName
        this.radius = AndroidUtilities.dp(2f).toFloat()

        stationColor = Color.GREEN

    }
    constructor(context: Context, stationName : String, firstLineName :String, secondLineName :String) : this(context, stationName){
        this.firstLineName = firstLineName
        this.secondLineName = secondLineName
        this.radius = AndroidUtilities.dp(2f).toFloat()
        this.lineName = firstLineName
        stationColor = Color.GREEN

    }

   /* override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(stationName, 0, stationName.length, bound)

       *//* when(stationNamePosition){
            NamePosition.Up, NamePosition.Down->
        }*//*
        var height = paddingTop + bound.height() + (radius * 4)*//* +((radius+radius)*2)*//*
        if (isClicked) height += (radius * 2)
        setMeasuredDimension(support.component.AndroidUtilities.dp(30f), height)
    }*/
   override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
       setMeasuredDimension(AndroidUtilities.dp(60f), AndroidUtilities.dp(60f))
   }


    override fun onDraw(canvas: Canvas?) {
        stationNamePoint = stationNamePointCal()
        circlePaint.color = MetroUtil.getMetroLine(firstLineName)!!.stationColor

        centerPoint.x = (right - left.toFloat()) / 2
        centerPoint.y = (bottom - top.toFloat()) / 2
        canvas?.drawCircle(centerPoint.x, centerPoint.y , radius.toFloat(), circlePaint)

        circlePaint.color = MetroUtil.getMetroLine(secondLineName)!!.stationColor
        canvas?.drawCircle(centerPoint.x, centerPoint.y, (radius + radius).toFloat(), circlePaint)

        canvas?.drawText(stationName, stationNamePoint.x.toFloat(), stationNamePoint.y.toFloat()  , textPaint)

        if (isClicked ){
            if (!isGuideViewed) {
                /*checkBitmap!!.eraseColor(0)
                val w = checkDrawable.intrinsicWidth
                val h = checkDrawable.intrinsicHeight
                val x = centerPoint.x.toInt() - (radius / 2)
                val y = (centerPoint.y - (4 * radius)).toInt() - (radius / 2)

                checkDrawable.setBounds(x, y, x + radius, y + radius)
                checkDrawable.draw(canvas)

                canvas!!.drawBitmap(checkBitmap, 0f, 0f, null)

                circlePaint.style = Paint.Style.STROKE
                circlePaint.strokeWidth = support.component.AndroidUtilities.dp(0.2F).toFloat()
                canvas.drawCircle(centerPoint.x, centerPoint.y - (4 * radius), radius.toFloat(), circlePaint)*/
//                addGuideProperty()
                isGuideViewed = true
                isClicked = false
            }else{
                isClicked = false
                isGuideViewed = false
            }
        }
    }

    init {
        checkDrawable.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp(size.toFloat()), AndroidUtilities.dp(size.toFloat()), Bitmap.Config.ARGB_4444)
    }



}