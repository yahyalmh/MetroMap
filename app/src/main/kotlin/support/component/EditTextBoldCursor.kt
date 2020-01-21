
package support.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.text.Layout
import android.text.StaticLayout
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.yaya.map.R
import java.lang.reflect.Field
import java.lang.reflect.Method

open class EditTextBoldCursor(context: Context?) : EditText(context) {
    private var editor: Any? = null
    private var mCursorDrawable: Array<Drawable?>? = null
    private var gradientDrawable: GradientDrawable? = null
    private var cursorSize: Int
    private var ignoreTopCount = 0
    private var ignoreBottomCount = 0
    private var scrollYe = 0
    private var lineSpacingExtras = 0f
    private val rect = Rect()
    private var hintLayout: StaticLayout? = null
    private var hintColor = 0
    private var hintVisible = true
    private var hintAlpha = 1.0f
    private var lastUpdateTime: Long = 0
    private var allowDrawCursor = true
    private var cursorWidth = 2.0f
    fun setAllowDrawCursor(value: Boolean) {
        allowDrawCursor = value
    }

    fun setCursorWidth(width: Float) {
        cursorWidth = width
    }

    fun setCursorColor(color: Int) {
        gradientDrawable!!.setColor(color)
        invalidate()
    }

    fun setCursorSize(value: Int) {
        cursorSize = value
    }

    fun setHintVisible(value: Boolean) {
        if (hintVisible == value) {
            return
        }
        lastUpdateTime = System.currentTimeMillis()
        hintVisible = value
        invalidate()
    }

    fun setHintColor(value: Int) {
        hintColor = value
        invalidate()
    }

    open fun setHintText(value: String) {
        hintLayout = StaticLayout(value, paint, AndroidUtilities.dp(1000f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        lineSpacingExtras = add
    }

    override fun getExtendedPaddingTop(): Int {
        if (ignoreTopCount != 0) {
            ignoreTopCount--
            return 0
        }
        return super.getExtendedPaddingTop()
    }

    override fun getExtendedPaddingBottom(): Int {
        if (ignoreBottomCount != 0) {
            ignoreBottomCount--
            return if (scrollYe != Int.MAX_VALUE) -scrollYe else 0
        }
        return super.getExtendedPaddingBottom()
    }

    override fun onDraw(canvas: Canvas) {
        val topPadding = extendedPaddingTop
        scrollYe = Int.MAX_VALUE
        try {
            scrollYe = mScrollYField!!.getInt(this)
            mScrollYField!![this] = 0
        } catch (e: Exception) { //
        }
        ignoreTopCount = 1
        ignoreBottomCount = 1
        canvas.save()
        canvas.translate(0f, topPadding.toFloat())
        try {
            super.onDraw(canvas)
        } catch (e: Exception) { //
        }
        if (scrollYe != Int.MAX_VALUE) {
            try {
                mScrollYField!![this] = scrollYe
            } catch (e: Exception) { //
            }
        }
        canvas.restore()
        if (length() == 0 && hintLayout != null && (hintVisible || hintAlpha != 0f)) {
            if (hintVisible && hintAlpha != 1.0f || !hintVisible && hintAlpha != 0.0f) {
                val newTime = System.currentTimeMillis()
                var dt = newTime - lastUpdateTime
                if (dt < 0 || dt > 17) {
                    dt = 17
                }
                lastUpdateTime = newTime
                if (hintVisible) {
                    hintAlpha += dt / 150.0f
                    if (hintAlpha > 1.0f) {
                        hintAlpha = 1.0f
                    }
                } else {
                    hintAlpha -= dt / 150.0f
                    if (hintAlpha < 0.0f) {
                        hintAlpha = 0.0f
                    }
                }
                invalidate()
            }
            val oldColor = paint.color
            paint.color = hintColor
            paint.alpha = (255 * hintAlpha).toInt()
            canvas.save()
            var left = 0
            val lineLeft = hintLayout!!.getLineLeft(0)
            if (lineLeft != 0f) {
                left -= lineLeft.toInt()
            }
            canvas.translate(left.toFloat(), (measuredHeight - hintLayout!!.height) / 2.0f)
            hintLayout!!.draw(canvas)
            paint.color = oldColor
            canvas.restore()
        }
        try {
            if (allowDrawCursor && mShowCursorField != null && mCursorDrawable != null && mCursorDrawable!![0] != null) {
                val mShowCursor = mShowCursorField!!.getLong(editor)
                val showCursor = (SystemClock.uptimeMillis() - mShowCursor) % (2 * 500) < 500 && isFocused
                if (showCursor) {
                    canvas.save()
                    var voffsetCursor = 0
                    if (gravity and Gravity.VERTICAL_GRAVITY_MASK != Gravity.TOP) {
                        voffsetCursor = getVerticalOffsetMethod!!.invoke(this, true) as Int
                    }
                    canvas.translate(paddingLeft.toFloat(), extendedPaddingTop + voffsetCursor.toFloat())
                    val layout = layout
                    val line = layout.getLineForOffset(selectionStart)
                    val lineCount = layout.lineCount
                    val bounds = mCursorDrawable!![0]!!.bounds
                    rect.left = bounds.left
                    rect.right = bounds.left + AndroidUtilities.dp(cursorWidth)
                    rect.bottom = bounds.bottom
                    rect.top = bounds.top
                    if (lineSpacingExtras != 0f && line < lineCount - 1) {
                        rect.bottom -= lineSpacingExtras.toInt()
                    }
                    rect.top = rect.centerY() - cursorSize / 2
                    rect.bottom = rect.top + cursorSize
                    gradientDrawable!!.bounds = rect
                    gradientDrawable!!.draw(canvas)
                    canvas.restore()
                }
            }
        } catch (e: Throwable) { //ignore
        }
    }

    companion object {
        private var mEditor: Field? = null
        private var mShowCursorField: Field? = null
        private var mCursorDrawableField: Field? = null
        private var mScrollYField: Field? = null
        private var getVerticalOffsetMethod: Method? = null
        private var mCursorDrawableResField: Field? = null
    }

    init {
        if (mCursorDrawableField == null) {
            try {
                mScrollYField = View::class.java.getDeclaredField("mScrollY")
                mScrollYField!!.setAccessible(true)
                mCursorDrawableResField = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                mCursorDrawableResField!!.setAccessible(true)
                mEditor = TextView::class.java.getDeclaredField("mEditor")
                mEditor!!.setAccessible(true)
                val editorClass = Class.forName("android.widget.Editor")
                mShowCursorField = editorClass.getDeclaredField("mShowCursor")
                mShowCursorField!!.setAccessible(true)
                mCursorDrawableField = editorClass.getDeclaredField("mCursorDrawable")
                mCursorDrawableField!!.setAccessible(true)
                getVerticalOffsetMethod = TextView::class.java.getDeclaredMethod("getVerticalOffset", Boolean::class.javaPrimitiveType)
                getVerticalOffsetMethod!!.setAccessible(true)
            } catch (e: Throwable) { //
            }
        }
        try {
            gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(-0xab5e25, -0xab5e25))
            editor = mEditor!![this]
            mCursorDrawable = mCursorDrawableField!![editor] as Array<Drawable?>
            mCursorDrawableResField!![this] = R.drawable.field_carret_empty
        } catch (e: Exception) {
        }
        cursorSize = AndroidUtilities.dp(24f)
    }
}