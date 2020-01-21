/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package ui.cells.tellCell

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup

abstract class BaseCell(context: Context?) : ViewGroup(context) {
    private inner class CheckForTap : Runnable {
        override fun run() {
            if (pendingCheckForLongPress == null) {
                pendingCheckForLongPress = CheckForLongPress()
            }
            pendingCheckForLongPress!!.currentPressCount = ++pressCount
            postDelayed(pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout().toLong())
        }
    }

    internal inner class CheckForLongPress : Runnable {
        var currentPressCount = 0
        override fun run() {
            if (checkingForLongPress && parent != null && currentPressCount == pressCount) {
                checkingForLongPress = false
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onLongPress()
                val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
                onTouchEvent(event)
                event.recycle()
            }
        }
    }

    private var checkingForLongPress = false
    private var pendingCheckForLongPress: CheckForLongPress? = null
    private var pressCount = 0
    private var pendingCheckForTap: CheckForTap? = null
    protected fun setDrawableBounds(drawable: Drawable, x: Int, y: Int) {
        setDrawableBounds(drawable, x, y, drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    protected fun setDrawableBounds(drawable: Drawable, x: Float, y: Float) {
        setDrawableBounds(drawable, x.toInt(), y.toInt(), drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    protected fun setDrawableBounds(drawable: Drawable?, x: Int, y: Int, w: Int, h: Int) {
        drawable?.setBounds(x, y, x + w, y + h)
    }

    protected fun startCheckLongPress() {
        if (checkingForLongPress) {
            return
        }
        checkingForLongPress = true
        if (pendingCheckForTap == null) {
            pendingCheckForTap = CheckForTap()
        }
        postDelayed(pendingCheckForTap, ViewConfiguration.getTapTimeout().toLong())
    }

    protected fun cancelCheckLongPress() {
        checkingForLongPress = false
        if (pendingCheckForLongPress != null) {
            removeCallbacks(pendingCheckForLongPress)
        }
        if (pendingCheckForTap != null) {
            removeCallbacks(pendingCheckForTap)
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    protected fun onLongPress() {}

    init {
        setWillNotDraw(false)
    }
}