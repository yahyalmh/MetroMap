
package support.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.InputFilter
import support.Theme

class HintEditText(context: Context?) : EditTextBoldCursor(context) {
    var hintText: String? = null
        private set
    private var textOffset = 0f
    private var spaceSize = 0f
    private var numberSize = 0f
    private val firstTime = true
    private val paint = Paint()
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = Rect()
    var rectf = RectF()

    override fun setHintText(value: String) {
        hintText = value
        onTextChange()
        text = text
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        onTextChange()
    }

    fun onTextChange() {
        textOffset = if (length() > 0) getPaint().measureText(text, 0, length()) else 0f
        spaceSize = getPaint().measureText(" ")
        numberSize = getPaint().measureText("1")
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hintText != null && length() < hintText!!.length) {
            val top = measuredHeight / 2
            var offsetX = textOffset
            for (a in length() until hintText!!.length) {
                if (hintText!![a] == ' ') {
                    offsetX += spaceSize
                } else {
                    rect[offsetX.toInt() + AndroidUtilities.dp(1f), top, (offsetX + numberSize).toInt() - AndroidUtilities.dp(1f)] = top + AndroidUtilities.dp(2f)
                    canvas.drawRect(rect, paint)
                    offsetX += numberSize
                }
            }
        }
        rectf[AndroidUtilities.dp2(15f).toFloat(), AndroidUtilities.dp(6f).toFloat(), measuredWidth - AndroidUtilities.dp(5f).toFloat()] = measuredHeight - AndroidUtilities.dp(6f).toFloat()
        canvas.drawRoundRect(rectf, AndroidUtilities.dp(1f).toFloat(), AndroidUtilities.dp(1f).toFloat(), rectPaint)
    }

    fun setAlphaNumericFilter() {
        this.filters = arrayOf(
                InputFilter { source, start, end, dest, dstart, dend ->
                    for (i in start until end) {
                        if (!Character.isLetter(source[i]) && !Character.isSpaceChar(source[i])) {
                            return@InputFilter ""
                        }
                    }
                    null
                }
        )
    }

    init {
        paint.color = Theme.getColor(Theme.key_windowBackgroundWhiteHintText)
        rectPaint.alpha = 255
        rectPaint.color = Theme.getColor(Theme.key_contacts_inviteText)
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = AndroidUtilities.dp(2.5f).toFloat()
    }
}