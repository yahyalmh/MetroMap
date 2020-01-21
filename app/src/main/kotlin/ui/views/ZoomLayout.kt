package ui.views

/**
 * Created by yaya-mh on 24/07/2018 11:58 AM
 */
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import ui.cells.sataionCells.StationCell
import ui.lines.MetroLine

open class ZoomLayout : FrameLayout, ScaleGestureDetector.OnScaleGestureListener{

    private var mode = Mode.NONE
    private var scale = 1.0f
    private var lastScaleFactor = 0f

    // Where the finger first  touches the screen
    private var startX = 0f
    private var startY = 0f

    // How much to translate the canvas
    private var dx = 0f
    private var dy = 0f
    private var prevDx = 0f
    private var prevDy = 0f

    private enum class Mode {
        NONE,
        DRAG,
        ZOOM
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        print("sdfasdfasdf")
        return true
    }

    private fun init(context: Context) {
        val scaleDetector = ScaleGestureDetector(context, this)
        this.setOnTouchListener {

            view, motionEvent ->

            when (motionEvent.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    if (scale > MIN_ZOOM) {
                        mode = Mode.DRAG
                        startX = motionEvent.x - prevDx
                        startY = motionEvent.y - prevDy
                    }
                }

                MotionEvent.ACTION_MOVE ->
                    if (mode == Mode.DRAG) {
                        dx = motionEvent.x - startX
                        dy = motionEvent.y - startY
                    }

                MotionEvent.ACTION_POINTER_DOWN ->
                    mode = Mode.ZOOM

                MotionEvent.ACTION_POINTER_UP ->
                    mode = Mode.NONE

                MotionEvent.ACTION_UP -> {
                    mode = Mode.NONE
                    prevDx = dx
                    prevDy = dy
                }
            }
            scaleDetector.onTouchEvent(motionEvent)

            if (mode == Mode.DRAG && scale >= MIN_ZOOM || mode == Mode.ZOOM) {
                parent.requestDisallowInterceptTouchEvent(true)
                val maxDx = (child(0).width - child(0).width / scale) / 2 * scale
                val maxDy = (child(0).height - child(0).height / scale) / 2 * scale
                dx = Math.min(Math.max(dx, -maxDx), maxDx)
                dy = Math.min(Math.max(dy, -maxDy), maxDy)
                applyScaleAndTranslation()
            }else{
                kotlin.io.print("sdfsdfdf")
//                parent.requestDisallowInterceptTouchEvent(false)
            }
            true
        }

    }

    override fun performClick(): Boolean {
        print("sdfsdfasd")
        return super.performClick()
    }

    override fun onScaleBegin(scaleDetector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
        if (lastScaleFactor == 0f || Math.signum(scaleFactor) == Math.signum(lastScaleFactor)) {
            scale *= scaleFactor
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM))
            lastScaleFactor = scaleFactor
        } else {
            lastScaleFactor = 0f
        }
        return true
    }

    override fun onScaleEnd(scaleDetector: ScaleGestureDetector) {
    }

    fun applyScaleAndTranslation() {
        (0 until childCount)
                .map { child(it) }
                .forEach {
                    if (it is MetroLine){
                        it.scaleX = scale
                        it.scaleY = scale
                        it.translationX = dx
                        it.translationY = dy
                    }else if ((it is StationCell && it.stationName == "yourLocation")){
//                        it.scaleX = scale
//                        it.scaleY = scale
                    }
                }
    }

    fun applyScaleAndTranslation(lineName:String) {
        for (i in 0 until childCount ) {
            val child = child(i) as? MetroLine ?: continue
            if (child.lineName == lineName) {
                val animatorSet = AnimatorSet()
                val objAnimatorScaleUp = ObjectAnimator.ofFloat(child,"ScaleY", scale, scale + 0.1f, scale + 0.2f ).setDuration(1000)
                val objAnimatorScaleDown = ObjectAnimator.ofFloat(child,"ScaleY", scale + 0.2f, scale + 0.1f, scale).setDuration(1000)
//                animatorSet.setInterpolator {  }
                animatorSet.playSequentially(objAnimatorScaleUp, objAnimatorScaleDown)
                animatorSet.start()
//                break
            }
        }
    }

    private fun child(index : Int): View {
        return getChildAt(index)
    }

    companion object {
        private val MIN_ZOOM = 1.0f
        private val MAX_ZOOM = 6.0f
    }
}