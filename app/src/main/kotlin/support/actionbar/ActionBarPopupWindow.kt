/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
//Thanks to https://github.com/JakeWharton/ActionBarSherlock/
package support.actionbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import com.yaya.map.R
import support.LayoutHelper
import support.Theme
import support.component.AndroidUtilities.Companion.dp
import java.lang.reflect.Field
import java.util.*

class ActionBarPopupWindow : PopupWindow {
    private var windowAnimatorSet: AnimatorSet? = null
    private var animationEnabled = allowAnimation

    companion object {
        private var superListenerField: Field? = null
        private val allowAnimation = Build.VERSION.SDK_INT >= 18
        private val decelerateInterpolator = DecelerateInterpolator()
        private val NOP = OnScrollChangedListener { /* do nothing */ }

        init {
            var f: Field? = null
            try {
                f = PopupWindow::class.java.getDeclaredField("mOnScrollChangedListener")
                f.setAccessible(true)
            } catch (e: NoSuchFieldException) { /* ignored */
            }
            superListenerField = f
        }
    }

    private var mSuperScrollListener: OnScrollChangedListener? = null
    private var mViewTreeObserver: ViewTreeObserver? = null

    interface OnDispatchKeyEventListener {
        fun onDispatchKeyEvent(keyEvent: KeyEvent?)
    }

    class ActionBarPopupWindowLayout(context: Context?) : FrameLayout(context!!) {
        private var mOnDispatchKeyEventListener: OnDispatchKeyEventListener? = null
        private var backScaleX = 1f
        private var backScaleY = 1f
        var backAlpha = 255
        var lastStartedChild = 0
        var showedFromBoton = false
        private var animationEnabled = allowAnimation
        val positions = HashMap<View, Int>()
        private var scrollView: ScrollView? = null
         var linearLayout: LinearLayout
        var backgroundDrawabled: Drawable? = null
        fun setShowedFromBotton(value: Boolean) {
            showedFromBoton = value
        }

        fun setDispatchKeyEventListener(listener: OnDispatchKeyEventListener?) {
            mOnDispatchKeyEventListener = listener
        }

        fun setBackScaleX(value: Float) {
            backScaleX = value
            invalidate()
        }

        fun setBackScaleY(value: Float) {
            backScaleY = value
            if (animationEnabled) {
                val count = itemsCount
                var visibleCount = 0
                for (a in 0 until count) {
                    visibleCount += if (getItemAt(a).visibility == View.VISIBLE) 1 else 0
                }
                val height = measuredHeight - dp(16f)
                if (showedFromBoton) {
                    for (a in lastStartedChild downTo 0) {
                        val child = getItemAt(a)
                        if (child.visibility != View.VISIBLE) {
                            continue
                        }
                        val position = positions[child]
                        if (position != null && height - (position * dp(48f) + dp(32f)) > value * height) {
                            break
                        }
                        lastStartedChild = a - 1
                        startChildAnimation(child)
                    }
                } else {
                    for (a in lastStartedChild until count) {
                        val child = getItemAt(a)
                        if (child.visibility != View.VISIBLE) {
                            continue
                        }
                        val position = positions[child]
                        if (position != null && (position + 1) * dp(48f) - dp(24f) > value * height) {
                            break
                        }
                        lastStartedChild = a + 1
                        startChildAnimation(child)
                    }
                }
            }
            invalidate()
        }

        override fun setBackgroundDrawable(drawable: Drawable) {
            backgroundDrawabled = drawable
        }

        private fun startChildAnimation(child: View) {
            if (animationEnabled) {
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(child, "alpha", 0.0f, 1.0f),
                        ObjectAnimator.ofFloat(child, "translationY", dp(if (showedFromBoton) 6f else -6.toFloat()).toFloat(), 0f))
                animatorSet.duration = 180
                animatorSet.interpolator = decelerateInterpolator
                animatorSet.start()
            }
        }

        fun setAnimationEnabled(value: Boolean) {
            animationEnabled = value
        }

        override fun addView(child: View) {
            linearLayout.addView(child)
        }

        fun removeInnerViews() {
            linearLayout.removeAllViews()
        }

        fun getBackScaleX(): Float {
            return backScaleX
        }

        fun getBackScaleY(): Float {
            return backScaleY
        }

        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            if (mOnDispatchKeyEventListener != null) {
                mOnDispatchKeyEventListener!!.onDispatchKeyEvent(event)
            }
            return super.dispatchKeyEvent(event)
        }

        override fun onDraw(canvas: Canvas) {
            if (backgroundDrawabled != null) {
                backgroundDrawabled!!.alpha = backAlpha
                val height = measuredHeight
                if (showedFromBoton) {
                    backgroundDrawabled!!.setBounds(0, (measuredHeight * (1.0f - backScaleY)).toInt(), (measuredWidth * backScaleX).toInt(), measuredHeight)
                } else {
                    backgroundDrawabled!!.setBounds(0, 0, (measuredWidth * backScaleX).toInt(), (measuredHeight * backScaleY).toInt())
                }
                backgroundDrawabled!!.draw(canvas)
            }
        }

        val itemsCount: Int
            get() = linearLayout.childCount

        fun getItemAt(index: Int): View {
            return linearLayout.getChildAt(index)
        }

        fun scrollToTop() {
            if (scrollView != null) {
                scrollView!!.scrollTo(0, 0)
            }
        }

        init {
            backgroundDrawabled = resources.getDrawable(R.drawable.popup_fixed).mutate()
            backgroundDrawabled!!.setColorFilter(PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY))
            setPadding(dp(8f), dp(8f), dp(8f), dp(8f))
            setWillNotDraw(false)
            try {
                scrollView = ScrollView(context)
                scrollView!!.setVerticalScrollBarEnabled(false)
                addView(scrollView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat()))
            } catch (e: Throwable) {
            }
            linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.VERTICAL
            if (scrollView != null) {
                scrollView!!.addView(linearLayout, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            } else {
                addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat()))
            }
        }
    }

    constructor() : super() {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(width: Int, height: Int) : super(width, height) {
        init()
    }

    constructor(contentView: View?) : super(contentView) {
        init()
    }

    constructor(contentView: View?, width: Int, height: Int, focusable: Boolean) : super(contentView, width, height, focusable) {
        init()
    }

    constructor(contentView: View?, width: Int, height: Int) : super(contentView, width, height) {
        init()
    }

    fun setAnimationEnabled(value: Boolean) {
        animationEnabled = value
    }

    private fun init() {
        if (superListenerField != null) {
            try {
                mSuperScrollListener = superListenerField!!.get(this) as OnScrollChangedListener
                superListenerField!!.set(this, NOP)
            } catch (e: Exception) {
                mSuperScrollListener = null
            }
        }
    }

    private fun unregisterListener() {
        if (mSuperScrollListener != null && mViewTreeObserver != null) {
            if (mViewTreeObserver!!.isAlive) {
                mViewTreeObserver!!.removeOnScrollChangedListener(mSuperScrollListener)
            }
            mViewTreeObserver = null
        }
    }

    private fun registerListener(anchor: View) {
        if (mSuperScrollListener != null) {
            val vto = if (anchor.windowToken != null) anchor.viewTreeObserver else null
            if (vto != mViewTreeObserver) {
                if (mViewTreeObserver != null && mViewTreeObserver!!.isAlive) {
                    mViewTreeObserver!!.removeOnScrollChangedListener(mSuperScrollListener)
                }
                if (vto.also { mViewTreeObserver = it } != null) {
                    vto!!.addOnScrollChangedListener(mSuperScrollListener)
                }
            }
        }
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int) {
        try {
            super.showAsDropDown(anchor, xoff, yoff)
            registerListener(anchor)
        } catch (e: Exception) {
        }
    }

    fun startAnimation() {
        if (animationEnabled) {
            if (windowAnimatorSet != null) {
                return
            }
            val content = contentView as ActionBarPopupWindowLayout
            content.translationY = 0f
            content.alpha = 1.0f
            content.pivotX = content.measuredWidth.toFloat()
            content.pivotY = 0f
            val count = content.itemsCount
            content.positions.clear()
            var visibleCount = 0
            for (a in 0 until count) {
                val child = content.getItemAt(a)
                if (child.visibility != View.VISIBLE) {
                    continue
                }
                content.positions[child] = visibleCount
                child.alpha = 0.0f
                visibleCount++
            }
            if (content.showedFromBoton) {
                content.lastStartedChild = count - 1
            } else {
                content.lastStartedChild = 0
            }
            windowAnimatorSet = AnimatorSet()
            windowAnimatorSet!!.playTogether(
                    ObjectAnimator.ofFloat(content, "backScaleY", 0.0f, 1.0f),
                    ObjectAnimator.ofInt(content, "backAlpha", 0, 255))
            windowAnimatorSet!!.duration = 150 + 16 * visibleCount.toLong()
            windowAnimatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    windowAnimatorSet = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    onAnimationEnd(animation)
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            windowAnimatorSet!!.start()
        }
    }

    override fun update(anchor: View, xoff: Int, yoff: Int, width: Int, height: Int) {
        super.update(anchor, xoff, yoff, width, height)
        registerListener(anchor)
    }

    override fun update(anchor: View, width: Int, height: Int) {
        super.update(anchor, width, height)
        registerListener(anchor)
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        unregisterListener()
    }

    override fun dismiss() {
        dismiss(true)
    }

    fun dismiss(animated: Boolean) {
        isFocusable = false
        if (animationEnabled && animated) {
            if (windowAnimatorSet != null) {
                windowAnimatorSet!!.cancel()
            }
            val content = contentView as ActionBarPopupWindowLayout
            windowAnimatorSet = AnimatorSet()
            windowAnimatorSet!!.playTogether(
                    ObjectAnimator.ofFloat(content, "translationY", dp(if (content.showedFromBoton) 5f else -5.toFloat()).toFloat()),
                    ObjectAnimator.ofFloat(content, "alpha", 0.0f))
            windowAnimatorSet!!.duration = 150
            windowAnimatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    windowAnimatorSet = null
                    isFocusable = false
                    try {
                        super@ActionBarPopupWindow.dismiss()
                    } catch (e: Exception) { //don't promt
                    }
                    unregisterListener()
                }

                override fun onAnimationCancel(animation: Animator) {
                    onAnimationEnd(animation)
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            windowAnimatorSet!!.start()
        } else {
            try {
                super.dismiss()
            } catch (e: Exception) { //don't promt
            }
            unregisterListener()
        }
    }
}