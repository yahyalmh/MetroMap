/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.actionbar

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import support.component.AndroidUtilities

class BackDrawable(close: Boolean) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var reverseAngle = false
    private var lastFrameTime: Long = 0
    private val animationInProgress = false
    private var finalRotation = 0f
    private var currentRotation = 0f
    private var currentAnimationTime = 0
    private val alwaysClose: Boolean
    private val interpolator = DecelerateInterpolator()
    private var color = -0x1
    private var rotatedColor = -0x8a8a8b
    private var animationTime = 300.0f
    private var rotated = true
    fun setColor(value: Int) {
        color = value
        invalidateSelf()
    }

    fun setRotatedColor(value: Int) {
        rotatedColor = value
        invalidateSelf()
    }

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
                (currentRotation * animationTime).toInt()
            } else {
                ((1.0f - currentRotation) * animationTime).toInt()
            }
            lastFrameTime = System.currentTimeMillis()
            finalRotation = rotation
        } else {
            currentRotation = rotation
            finalRotation = currentRotation
        }
        invalidateSelf()
    }

    fun setAnimationTime(value: Float) {
        animationTime = value
    }

    fun setRotated(value: Boolean) {
        rotated = value
    }

    override fun draw(canvas: Canvas) {
        if (currentRotation != finalRotation) {
            if (lastFrameTime != 0L) {
                val dt = System.currentTimeMillis() - lastFrameTime
                currentAnimationTime += dt.toInt()
                currentRotation = if (currentAnimationTime >= animationTime) {
                    finalRotation
                } else {
                    if (currentRotation < finalRotation) {
                        interpolator.getInterpolation(currentAnimationTime / animationTime) * finalRotation
                    } else {
                        1.0f - interpolator.getInterpolation(currentAnimationTime / animationTime)
                    }
                }
            }
            lastFrameTime = System.currentTimeMillis()
            invalidateSelf()
        }
        val rD = if (rotated) ((Color.red(rotatedColor) - Color.red(color)) * currentRotation).toInt() else 0
        val rG = if (rotated) ((Color.green(rotatedColor) - Color.green(color)) * currentRotation).toInt() else 0
        val rB = if (rotated) ((Color.blue(rotatedColor) - Color.blue(color)) * currentRotation).toInt() else 0
        val c = Color.rgb(Color.red(color) + rD, Color.green(color) + rG, Color.blue(color) + rB)
        paint.color = c
        canvas.save()
        canvas.translate(intrinsicWidth / 2.toFloat(), intrinsicHeight / 2.toFloat())
        var rotation = currentRotation
        if (!alwaysClose) {
            canvas.rotate(currentRotation * if (reverseAngle) -225 else 135)
        } else {
            canvas.rotate(135 + currentRotation * if (reverseAngle) -180 else 180)
            rotation = 1.0f
        }
        canvas.drawLine(-AndroidUtilities.dp(7f) - AndroidUtilities.dp(1f) * rotation, 0f, AndroidUtilities.dp(8f).toFloat(), 0f, paint)
        val startYDiff: Float = -AndroidUtilities.dp(0.5f).toFloat()
        val endYDiff: Float = AndroidUtilities.dp(7f) + AndroidUtilities.dp(1f) * rotation
        val startXDiff: Float = -AndroidUtilities.dp(7.0f) + AndroidUtilities.dp(7.0f) * rotation
        val endXDiff: Float = AndroidUtilities.dp(0.5f) - AndroidUtilities.dp(0.5f) * rotation
        canvas.drawLine(startXDiff, -startYDiff, endXDiff, -endYDiff, paint)
        canvas.drawLine(startXDiff, startYDiff, endXDiff, endYDiff, paint)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
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
        paint.strokeWidth = AndroidUtilities.dp(2f).toFloat()
        alwaysClose = close
    }
}