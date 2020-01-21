/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.ActionBar

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import support.component.AndroidUtilities

class MenuDrawable : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var reverseAngle = false
    private var lastFrameTime: Long = 0
    private val animationInProgress = false
    private var finalRotation = 0f
    private var currentRotation = 0f
    private var currentAnimationTime = 0
    private val interpolator = DecelerateInterpolator()
    fun setRotation(rotation: Float, animated: Boolean) {
        lastFrameTime = 0
        if (currentRotation == 1f) {
            reverseAngle = true
        } else if (currentRotation == 0f) {
            reverseAngle = false
        }
        lastFrameTime = 0
        if (animated) {
            currentAnimationTime = if (currentRotation < rotation) {
                (currentRotation * 300).toInt()
            } else {
                ((1.0f - currentRotation) * 300).toInt()
            }
            lastFrameTime = System.currentTimeMillis()
            finalRotation = rotation
        } else {
            currentRotation = rotation
            finalRotation = currentRotation
        }
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (currentRotation != finalRotation) {
            if (lastFrameTime != 0L) {
                val dt = System.currentTimeMillis() - lastFrameTime
                currentAnimationTime += dt.toInt()
                currentRotation = if (currentAnimationTime >= 300) {
                    finalRotation
                } else {
                    if (currentRotation < finalRotation) {
                        interpolator.getInterpolation(currentAnimationTime / 300.0f) * finalRotation
                    } else {
                        1.0f - interpolator.getInterpolation(currentAnimationTime / 300.0f)
                    }
                }
            }
            lastFrameTime = System.currentTimeMillis()
            invalidateSelf()
        }
        canvas.save()
        canvas.translate(intrinsicWidth / 2.toFloat(), intrinsicHeight / 2.toFloat())
        canvas.rotate(currentRotation * if (reverseAngle) -180 else 180)
        canvas.drawLine((-AndroidUtilities.dp(9f)).toFloat(), 0f, AndroidUtilities.dp(9f) - AndroidUtilities.dp(3.0f) * currentRotation, 0f, paint)
        val endYDiff: Float = AndroidUtilities.dp(5f) * (1 - Math.abs(currentRotation)) - AndroidUtilities.dp(0.5f) * Math.abs(currentRotation)
        val endXDiff: Float = AndroidUtilities.dp(9f) - AndroidUtilities.dp(2.5f) * Math.abs(currentRotation)
        val startYDiff: Float = AndroidUtilities.dp(5f) + AndroidUtilities.dp(2.0f) * Math.abs(currentRotation)
        val startXDiff: Float = -AndroidUtilities.dp(9f) + AndroidUtilities.dp(7.5f) * Math.abs(currentRotation)
        canvas.drawLine(startXDiff, -startYDiff, endXDiff, -endYDiff, paint)
        canvas.drawLine(startXDiff, startYDiff, endXDiff, endYDiff, paint)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun getIntrinsicWidth(): Int {
        return AndroidUtilities.dp(24f)
    }

    override fun getIntrinsicHeight(): Int {
        return AndroidUtilities.dp(24f)
    }

    init {
        paint.color = -0x1
        paint.strokeWidth = AndroidUtilities.dp(2f).toFloat()
    }
}