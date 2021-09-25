package ui.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.view.View
import support.component.AndroidUtilities
import kotlin.math.ceil

class GuideBox(context: Context, resId: Int) : View(context) {
    var checkDrawables: Drawable
    private var drawBitmap: Bitmap? = null
    private lateinit var checkBitmap: Bitmap
    private var bitmapCanvas: Canvas? = null
    private var checkCanvas: Canvas? = null
    private var drawBackground = false
    private var hasBorder = false
    private var progress = 0f
    private var checkAnimator: ObjectAnimator? = null
    private var isCheckAnimation = true
    private var attachedToWindow = false
    var isChecked = false
        private set
    private var size = 15
    private var checkOffset = 0
    private var color = 0
    private var checkedText: String? = null
    fun setCheckDrawable(checkDrawable: Drawable) {
        this.checkDrawables = checkDrawable
    }

    fun getBackgroundColor(): Int {
        return color
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE && drawBitmap == null) {
            drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(size.toFloat()), AndroidUtilities.dp(size.toFloat()), Bitmap.Config.ARGB_4444)
            bitmapCanvas = Canvas(drawBitmap!!)
            checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp(size.toFloat()), AndroidUtilities.dp(size.toFloat()), Bitmap.Config.ARGB_4444)
            checkCanvas = Canvas(checkBitmap)
        }
    }

    fun setProgress(value: Float) {
        if (progress == value) {
            return
        }
        progress = value
        invalidate()
    }

    fun setDrawBackground(value: Boolean) {
        drawBackground = value
    }

    fun setHasBorder(value: Boolean) {
        hasBorder = value
    }

    fun setCheckOffset(value: Int) {
        checkOffset = value
    }

    fun setSize(size: Int) {
        this.size = size
    }

    fun getProgress(): Float {
        return progress
    }

    fun setColor(backgroundColor: Int, checkColor: Int) {
        color = backgroundColor
        checkDrawables.colorFilter = PorterDuffColorFilter(checkColor, PorterDuff.Mode.MULTIPLY)
        textPaint!!.color = checkColor
        invalidate()
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        color = backgroundColor
        invalidate()
    }

    fun setCheckColor(checkColor: Int) {
        checkDrawables.colorFilter = PorterDuffColorFilter(checkColor, PorterDuff.Mode.MULTIPLY)
        textPaint!!.color = checkColor
        invalidate()
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
            checkAnimator = null
        }
    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        isCheckAnimation = newCheckedState
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1f else 0.toFloat())
        checkAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (animation == checkAnimator) {
                    checkAnimator = null
                }
                if (!isChecked) {
                    checkedText = null
                }
            }
        })
        checkAnimator!!.duration = 300
        checkAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        setChecked(-1, checked, animated)
    }

    fun setNum(num: Int) {
        if (num >= 0) {
            checkedText = "" + (num + 1)
        } else if (checkAnimator == null) {
            checkedText = null
        }
        invalidate()
    }

    fun setChecked(num: Int, checked: Boolean, animated: Boolean) {
        if (num >= 0) {
            checkedText = "" + (num + 1)
        }
        if (checked == isChecked) {
            return
        }
        isChecked = checked
        if (attachedToWindow && animated) {
            animateToCheckedState(checked)
        } else {
            cancelCheckAnimator()
            setProgress(if (checked) 1.0f else 0.0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (visibility != VISIBLE) {
            return
        }
        if (drawBackground || progress != 0f) {
            eraser2!!.strokeWidth = AndroidUtilities.dp((size + 6).toFloat()).toFloat()
            drawBitmap!!.eraseColor(0)
            var rad = measuredWidth / 2.toFloat()
            val roundProgress = if (progress >= 0.5f) 1.0f else progress / 0.5f
            val checkProgress = if (progress < 0.5f) 0.0f else (progress - 0.5f) / 0.5f
            val roundProgressCheckState = if (isCheckAnimation) progress else 1.0f - progress
            if (roundProgressCheckState < progressBounceDiff) {
                rad -= AndroidUtilities.dp(2f) * roundProgressCheckState / progressBounceDiff
            } else if (roundProgressCheckState < progressBounceDiff * 2) {
                rad -= AndroidUtilities.dp(2f) - AndroidUtilities.dp(2f) * (roundProgressCheckState - progressBounceDiff) / progressBounceDiff
            }
            if (drawBackground) {
                paint!!.color = 0x44000000
                canvas.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), rad - AndroidUtilities.dp(1f),
                    paint!!
                )
                backgroundPaint?.let {
                    canvas.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), rad - AndroidUtilities.dp(1f),
                        it
                    )
                }
            }
            paint!!.color = color
            if (hasBorder) {
                rad -= AndroidUtilities.dp(2f)
            }
            bitmapCanvas!!.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), rad,
                paint!!
            )
            eraser?.let {
                bitmapCanvas!!.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), rad * (1 - roundProgress),
                    it
                )
            }
            canvas.drawBitmap(drawBitmap!!, 0f, 0f, null)
            checkBitmap.eraseColor(0)
            if (checkedText != null) {
                val w = ceil(textPaint!!.measureText(checkedText).toDouble()).toInt()
                checkCanvas!!.drawText(
                    checkedText!!, (measuredWidth - w) / 2.toFloat(), AndroidUtilities.dp(21f).toFloat(),
                    textPaint!!
                )
            } else {
                val w = checkDrawables.intrinsicWidth
                val h = checkDrawables.intrinsicHeight
                val x = (measuredWidth - w) / 2
                val y = (measuredHeight - h) / 2
                checkDrawables.setBounds(x, y + checkOffset, x + w, y + h + checkOffset)
                checkCanvas?.let { checkDrawables.draw(it) }
            }
            checkCanvas!!.drawCircle((measuredWidth / 2 - AndroidUtilities.dp(2.5f)).toFloat(), (measuredHeight / 2 + AndroidUtilities.dp(4f)).toFloat(), (measuredWidth + AndroidUtilities.dp(6f)) / 2 * (1 - checkProgress),
                eraser2!!
            )
            canvas.drawBitmap(checkBitmap, 0f, 0f, null)
        }
    }

    companion object {
        private var paint: Paint? = null
        private var eraser: Paint? = null
        private var eraser2: Paint? = null
        private val checkPaint: Paint? = null
        private var backgroundPaint: Paint? = null
        private var textPaint: TextPaint? = null
        private const val progressBounceDiff = 0.2f
    }

    init {
        if (paint == null) {
            paint = Paint(Paint.ANTI_ALIAS_FLAG)
            eraser = Paint(Paint.ANTI_ALIAS_FLAG)
            eraser!!.color = 0
            eraser!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            eraser2 = Paint(Paint.ANTI_ALIAS_FLAG)
            eraser2!!.color = 0
            eraser2!!.style = Paint.Style.STROKE
            eraser2!!.strokeWidth = AndroidUtilities.dp(28f).toFloat()
            eraser2!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint!!.color = -0x1
            backgroundPaint!!.style = Paint.Style.STROKE
            backgroundPaint!!.strokeWidth = AndroidUtilities.dp(2f).toFloat()
            textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            textPaint!!.textSize = AndroidUtilities.dp(18f).toFloat()
            //            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }
        checkDrawables = context.resources.getDrawable(resId).mutate()
    }
}