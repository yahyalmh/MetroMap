/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package ui.cells.tellCell

import android.content.Context
import android.graphics.Canvas
import android.view.View
import support.Theme
import support.component.AndroidUtilities.Companion.dp

class DividerCell(context: Context?) : View(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), dp(16f) + 1)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawLine(paddingLeft.toFloat(), dp(8f).toFloat(), width - paddingRight.toFloat(), dp(8f).toFloat(), Theme.dividerPaint)
    }
}