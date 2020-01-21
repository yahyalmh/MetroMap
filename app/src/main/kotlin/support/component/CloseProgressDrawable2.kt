/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.component

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import support.component.AndroidUtilities.Companion.dp

class CloseProgressDrawable2 : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var lastFrameTime: Long = 0
    private val interpolator = DecelerateInterpolator()
    private val rect = RectF()
    private var angle = 0f
    private var animating = false
    fun startAnimation() {
        animating = true
        lastFrameTime = System.currentTimeMillis()
        invalidateSelf()
    }

    fun stopAnimation() {
        animating = false
    }

    fun setColor(value: Int) {
        paint.color = value
    }

    override fun draw(canvas: Canvas) {
        val newTime = System.currentTimeMillis()
        val invalidate = false
        if (lastFrameTime != 0L) {
            val dt = newTime - lastFrameTime
            if (animating || angle != 0f) {
                angle += 360 * dt / 500.0f
                if (!animating && angle >= 720) {
                    angle = 0f
                } else {
                    angle -= (angle / 720).toInt() * 720.toFloat()
                }
                invalidateSelf()
            }
        }
        canvas.save()
        canvas.translate(intrinsicWidth / 2.toFloat(), intrinsicHeight / 2.toFloat())
        canvas.rotate(-45f)
        var progress1 = 1.0f
        var progress2 = 1.0f
        var progress3 = 1.0f
        var progress4 = 0.0f
        if (angle >= 0 && angle < 90) {
            progress1 = 1.0f - angle / 90.0f
        } else if (angle >= 90 && angle < 180) {
            progress1 = 0.0f
            progress2 = 1.0f - (angle - 90) / 90.0f
        } else if (angle >= 180 && angle < 270) {
            progress2 = 0f
            progress1 = progress2
            progress3 = 1.0f - (angle - 180) / 90.0f
        } else if (angle >= 270 && angle < 360) {
            progress3 = 0f
            progress2 = progress3
            progress1 = progress2
            progress4 = (angle - 270) / 90.0f
        } else if (angle >= 360 && angle < 450) {
            progress3 = 0f
            progress2 = progress3
            progress1 = progress2
            progress4 = 1.0f - (angle - 360) / 90.0f
        } else if (angle >= 450 && angle < 540) {
            progress3 = 0f
            progress2 = progress3
            progress1 = (angle - 450) / 90.0f
        } else if (angle >= 540 && angle < 630) {
            progress3 = 0f
            progress2 = (angle - 540) / 90.0f
        } else if (angle >= 630 && angle < 720) {
            progress3 = (angle - 630) / 90.0f
        }
        if (progress1 != 0f) {
            canvas.drawLine(0f, 0f, 0f, dp(8f) * progress1, paint)
        }
        if (progress2 != 0f) {
            canvas.drawLine(-dp(8f) * progress2, 0f, 0f, 0f, paint)
        }
        if (progress3 != 0f) {
            canvas.drawLine(0f, -dp(8f) * progress3, 0f, 0f, paint)
        }
        if (progress4 != 1f) {
            canvas.drawLine(dp(8f) * progress4, 0f, dp(8f).toFloat(), 0f, paint)
        }
        canvas.restore()
        val cx = bounds.centerX()
        val cy = bounds.centerY()
        rect[cx - dp(8f).toFloat(), cy - dp(8f).toFloat(), cx + dp(8f).toFloat()] = cy + dp(8f).toFloat()
        canvas.drawArc(rect, (if (angle < 360) 0f else angle - 360) - 45, if (angle < 360) angle else 720 - angle, false, paint)
        lastFrameTime = newTime
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun getIntrinsicWidth(): Int {
        return dp(24f)
    }

    override fun getIntrinsicHeight(): Int {
        return dp(24f)
    }

    init {
        paint.color = -0x1
        paint.strokeWidth = dp(2f).toFloat()
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
    }
}