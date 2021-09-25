/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.actionbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.*
import android.view.Gravity
import android.view.View
import support.component.AndroidUtilities

class SimpleTextView(context: Context?) : View(context), Drawable.Callback {
    private var layout: Layout? = null
    private val textPaint: TextPaint
    private var gravity = Gravity.LEFT or Gravity.TOP
    var texte: CharSequence? = null
    private val spannableStringBuilder: SpannableStringBuilder? = null
    private var leftDrawable: Drawable? = null
    private var rightDrawable: Drawable? = null
    private var drawablePadding: Int = AndroidUtilities.dp(4f)
    private var leftDrawableTopPadding = 0
    private var rightDrawableTopPadding = 0
    private var offsetX = 0
    var textWidth = 0
        private set
    var textHeight = 0
        private set
    private var wasLayout = false
    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }

    fun setLinkTextColor(color: Int) {
        textPaint.linkColor = color
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        wasLayout = false
    }

    fun setTextSize(size: Int) {
        val newSize: Int = AndroidUtilities.dp(size.toFloat())
        if (newSize.toFloat() == textPaint.textSize) {
            return
        }
        textPaint.textSize = newSize.toFloat()
        if (!recreateLayoutMaybe()) {
            invalidate()
        }
    }

    fun setGravity(value: Int) {
        gravity = value
    }

    fun setTypeface(typeface: Typeface?) {
        textPaint.typeface = typeface
    }

    val sideDrawablesSize: Int
        get() {
            var size = 0
            if (leftDrawable != null) {
                size += leftDrawable!!.intrinsicWidth + drawablePadding
            }
            if (rightDrawable != null) {
                size += rightDrawable!!.intrinsicWidth + drawablePadding
            }
            return size
        }

    val paint: Paint
        get() = textPaint

    private fun calcOffset(width: Int) {
        if (layout!!.lineCount > 0) {
            textWidth = Math.ceil(layout!!.getLineWidth(0).toDouble()).toInt()
            textHeight = layout!!.getLineBottom(0)
            offsetX = if (gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.LEFT) {
                (-layout!!.getLineLeft(0)).toInt()
            } else if (layout!!.getLineLeft(0) == 0f) {
                width - textWidth
            } else {
                -AndroidUtilities.dp(8f)
            }
            offsetX += paddingLeft
        }
    }

    private fun createLayout(width: Int): Boolean {
        var width = width
        if (texte != null) {
            try {
                if (leftDrawable != null) {
                    width -= leftDrawable!!.intrinsicWidth
                    width -= drawablePadding
                }
                if (rightDrawable != null) {
                    width -= rightDrawable!!.intrinsicWidth
                    width -= drawablePadding
                }
                val string = TextUtils.ellipsize(texte, textPaint, width.toFloat(), TextUtils.TruncateAt.END)
                /*if (layout != null && TextUtils.equals(layout.getText(), string)) {
                    calcOffset(width);
                    return false;
                }*/layout = StaticLayout(string, 0, string.length, textPaint, width + AndroidUtilities.dp(8f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
                calcOffset(width)
            } catch (ignore: Exception) {
            }
        } else {
            layout = null
            textWidth = 0
            textHeight = 0
        }
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        createLayout(width - paddingLeft - paddingRight)
        val finalHeight: Int
        finalHeight = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height
        } else {
            textHeight
        }
        setMeasuredDimension(width, finalHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        wasLayout = true
    }

    fun setLeftDrawableTopPadding(value: Int) {
        leftDrawableTopPadding = value
    }

    fun setRightDrawableTopPadding(value: Int) {
        rightDrawableTopPadding = value
    }

    fun setLeftDrawable(resId: Int) {
        setLeftDrawable(if (resId == 0) null else context.resources.getDrawable(resId))
    }

    fun setRightDrawable(resId: Int) {
        setRightDrawable(if (resId == 0) null else context.resources.getDrawable(resId))
    }

    fun setLeftDrawable(drawable: Drawable?) {
        if (leftDrawable === drawable) {
            return
        }
        if (leftDrawable != null) {
            leftDrawable!!.callback = null
        }
        leftDrawable = drawable
        if (drawable != null) {
            drawable.callback = this
        }
        if (!recreateLayoutMaybe()) {
            invalidate()
        }
    }

    fun setRightDrawable(drawable: Drawable?) {
        if (rightDrawable === drawable) {
            return
        }
        if (rightDrawable != null) {
            rightDrawable!!.callback = null
        }
        rightDrawable = drawable
        if (drawable != null) {
            drawable.callback = this
        }
        if (!recreateLayoutMaybe()) {
            invalidate()
        }
    }

    fun setText(value: CharSequence?) {
        setText(value, false)
    }

    fun setText(value: CharSequence?, force: Boolean) {
        if (texte == null && value == null || !force && texte != null && value != null && texte == value) {
            return
        }
        texte = value
        recreateLayoutMaybe()
    }

    fun setDrawablePadding(value: Int) {
        if (drawablePadding == value) {
            return
        }
        drawablePadding = value
        if (!recreateLayoutMaybe()) {
            invalidate()
        }
    }

    private fun recreateLayoutMaybe(): Boolean {
        if (wasLayout) {
            return createLayout(measuredWidth)
        } else {
            requestLayout()
        }
        return true
    }

    fun getText(): CharSequence {
        return if (texte == null) {
            ""
        } else texte!!
    }

    override fun onDraw(canvas: Canvas) {
        var textOffsetX = 0
        if (leftDrawable != null) {
            val y = (textHeight - leftDrawable!!.intrinsicHeight) / 2 + leftDrawableTopPadding
            leftDrawable!!.setBounds(0, y, leftDrawable!!.intrinsicWidth, y + leftDrawable!!.intrinsicHeight)
            leftDrawable!!.draw(canvas)
            if (gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.LEFT) {
                textOffsetX += drawablePadding + leftDrawable!!.intrinsicWidth
            }
        }
        if (rightDrawable != null) {
            val x = textOffsetX + textWidth + drawablePadding
            val y = (textHeight - rightDrawable!!.intrinsicHeight) / 2 + rightDrawableTopPadding
            rightDrawable!!.setBounds(x, y, x + rightDrawable!!.intrinsicWidth, y + rightDrawable!!.intrinsicHeight)
            rightDrawable!!.draw(canvas)
        }
        if (layout != null) {
            if (offsetX + textOffsetX != 0) {
                canvas.save()
                canvas.translate(offsetX + textOffsetX.toFloat(), 0f)
            }
            layout!!.draw(canvas)
            if (offsetX + textOffsetX != 0) {
                canvas.restore()
            }
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        if (who === leftDrawable) {
            invalidate(leftDrawable!!.bounds)
        } else if (who === rightDrawable) {
            invalidate(rightDrawable!!.bounds)
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    init {
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    }
}