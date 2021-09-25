package ui.cells.sataionCells

import Constants
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.yaya.map.R
import support.LocaleController
import support.NotificationCenter
import support.Theme
import support.component.AndroidUtilities
import ui.activities.LaunchActivity
import ui.views.GuideBox


/**
 * Created by yaya-mh on 18/07/2018 10:14 AM
 */
open class StationCell : FrameLayout, Cloneable {
    companion object {
        var clickedCellName: String? = null
    }

    class Builder {
        var color: Int = 0
        var left: Int = 0
        var top: Int = 0
        var right: Int = 0
        var bottom: Int = 0
        var radius: Float = 0f
        lateinit var context: Context

        fun build(): StationCell {
            return StationCell(this)
        }

        fun context(context: Context): Builder {
            this.context = context
            return this
        }

        fun color(color: Int): Builder {
            this.color = color
            return this
        }

        fun left(left: Int): Builder {
            this.left = left
            return this
        }

        fun top(top: Int): Builder {
            this.top = top
            return this
        }

        fun right(right: Int): Builder {
            this.right = right
            return this
        }

        fun bottom(bottom: Int): Builder {
            this.bottom = bottom
            return this
        }

        fun radius(radius: Float): Builder {
            this.radius = radius
            return this
        }
    }

    var cx: Float = 0f
    var cy: Float = 0f
    var lineNumber: Int = -1
    var stationNamePosition = NamePosition.Up
    var radius: Float = AndroidUtilities.dp(3f).toFloat()
    var stationName: String = ""
    var lineName: String = ""
    var startX = 0f
    var startY = 0f
    var endX = 0f
    var endY = 0f
    var lat = 0
    var long = 0
    var centerPoint = PointF(0f, 0f)

    var isClicked = false
    var firstStation = false
    var isGuideViewed = false


    var stationColor: Int = Theme.getColor(Theme.key_avatar_backgroundGreen)
    var circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var stationNamePoint: PointF
    var textPaint: Paint
    lateinit var stationNameTextView: TextView
    var cellProperty = mutableListOf<String>()
    var guideBoxList = mutableListOf<GuideBox>()

    init {

        circlePaint.color = stationColor
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = AndroidUtilities.dp(2F).toFloat()

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = AndroidUtilities.dp(4f).toFloat()
        textPaint.color = Color.BLACK

    }

    constructor(context: Context) : super(context)

    constructor(builder: Builder) : this(builder.context) {
        this.left = builder.left
        this.top = builder.top
        this.right = builder.right
        this.bottom = builder.bottom
        this.radius = builder.radius
    }

    constructor(context: Context, stationName: String) : this(context) {
        this.stationNameTextView = TextView(context)
        this.stationNameTextView.text = LocaleController.getStringResourceByName(stationName)
        this.stationName = stationName
        setWillNotDraw(false)
    }

    constructor(context: Context, stationName: String, color: Int) : this(context, stationName) {
        this.stationColor = color
        circlePaint.color = color
    }

    constructor(context: Context, stationName: String, cx: Float, cy: Float, color: Int) : this(context, stationName, color) {
        this.cx = cx
        this.cy = cy
    }

    constructor(context: Context, stationName: String, cx: Float, cy: Float, color: Int, lineNumber: Int) : this(context, stationName, cx, cy, color) {
        this.lineNumber = lineNumber
    }

    constructor(stationCell: StationCell) : this(stationCell.context) {
        this.stationName = stationCell.stationName
        this.radius = stationCell.radius
        this.cx = stationCell.cx
        this.cy = stationCell.cy
        this.firstStation = stationCell.firstStation
        this.startX = stationCell.startX
        this.startY = stationCell.startY
        this.endX = stationCell.endX
        this.endY = stationCell.endY
        this.cellProperty = stationCell.cellProperty
        this.lineName = stationCell.lineName
        this.left = stationCell.left
        this.right = stationCell.right
        this.top = stationCell.top
        this.bottom = stationCell.bottom
        this.centerPoint = stationCell.centerPoint
        setWillNotDraw(false)

    }

    public fun addGuideProperty() {

        cellProperty.forEach { cellPro ->
            var guideBox: GuideBox? = null
            when (cellPro) {
                Constants.BUS_PROPERTY -> guideBox = GuideBox(context, R.drawable.ic_bus)
                Constants.BABY_CHANGING -> guideBox = GuideBox(context, R.drawable.ic_baby_changing)
                Constants.ELEVATOR_PROPERTY -> guideBox = GuideBox(context, R.drawable.ic_elevator)
            }
            guideBox!!.visibility = View.VISIBLE
            guideBox.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox), Theme.getColor(Theme.key_dialogRoundCheckBoxCheck))
            addView(guideBox)
            guideBox.setChecked(true, true)
            guideBoxList.add(guideBox)
        }
    }

    fun addLocationView() {
        isGuideViewed = true

        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.bot_location)
        imageView.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
        imageView.visibility = View.VISIBLE
        addView(imageView)
        invalidate()
        requestLayout()

    }


    fun removeLocationView() {
        (0 until childCount)
                .map { getChildAt(it) }
                .filterIsInstance<ImageView>()
                .forEach { removeView(it) }
        isGuideViewed = false
    }

    fun removeGuideProperty() {
        (0 until childCount)
                .map { getChildAt(it) }
                .filterIsInstance<GuideBox>()
                .forEach { removeView(it) }
        isGuideViewed = false
    }

    fun stationNamePointCal(): PointF {
        cx = (right - left.toFloat()) / 2
        cy = (bottom - top.toFloat()) / 2
        val bound = Rect()
        textPaint.getTextBounds(stationName, 0, stationName.length, bound)
        val space = AndroidUtilities.dp(2F)

        return when (stationNamePosition) {
            NamePosition.Up -> {
                PointF(cx - (bound.width() / 2), cy - bound.height() - radius)
            }
            NamePosition.Left -> {
                PointF(cx - bound.width() - radius - space, (cy + space))
            }
            NamePosition.Down -> {
                PointF(cx - (bound.width() / 2), cy + bound.height() + radius + space)
            }
            NamePosition.Right -> {
                PointF(cx + radius + space, cy + space)
            }
        }
    }

    private fun onClick() {
        Toast.makeText(LaunchActivity.applicationContext, "click on $stationName", Toast.LENGTH_SHORT).show()
        NotificationCenter.getInstance()!!.postNotificationName(NotificationCenter.cellClicked, stationName)
       /* if (!isGuideViewed) {
            if (null != clickedCellName) {
                val cell = MetroUtil.getCell(clickedCellName!!)
                clickedCellName = null
                cell!!.removeGuideProperty()
            }
            clickedCellName = stationName
            isGuideViewed = true
            addGuideProperty()
        } else {
            clickedCellName = null
            removeGuideProperty()
        }*/
        requestLayout()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event?.action == MotionEvent.ACTION_DOWN) {
            startX = event.x
            startY = event.y
            if (!isCircleClicked(startX, startY)) return false
        } else if (event?.action == MotionEvent.ACTION_UP) {
            endX = event.x
            endY = event.y
            return if (!isCircleClicked(endX, endY) || !isCircleClicked(startX, startY))
                false
            else {
                onClick()
                true
            }
        }
        return true

    }

    private fun isCircleClicked(startX: Float, startY: Float): Boolean {
        return Math.pow((startX - centerPoint.x).toDouble(), 2.0) + Math.pow((startY - centerPoint.y).toDouble(), 2.0) <= Math.pow(radius * 2.toDouble(), 2.0)

    }

    /**
     * this method create a deep copy from this class but they have same parent
     */
    public override fun clone(): StationCell {
        val copyCell = super.clone() as StationCell
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = AndroidUtilities.dp(2F).toFloat()
        copyCell.centerPoint = PointF(centerPoint.x, centerPoint.y)
        copyCell.circlePaint = paint
        return copyCell

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StationCell) return false

        if (stationName != other.stationName) return false
        /*if (cx != other.cx) return false
        if (cy != other.cy) return false*/

        return true
    }

    override fun hashCode(): Int {
        var result = lineName.hashCode()
        result = (31 * result + cx).toInt()
        result = (31 * result + cy).toInt()
        return result
    }

    override fun toString(): String {
        return "StationCell(stationName=$stationName)"
    }


    enum class NamePosition {
        Up, Down, Left, Right
    }

}