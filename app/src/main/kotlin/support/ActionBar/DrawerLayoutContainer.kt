/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.ActionBar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ListView
import com.yaya.map.R
import support.component.AndroidUtilities

class DrawerLayoutContainer(context: Context?) : FrameLayout(context) {
    private var drawerLayout: ViewGroup? = null
    private var parentActionBarLayout: ActionBarLayout? = null
    private var maybeStartTracking = false
    private var startedTracking = false
    private var startedTrackingX = 0
    private var startedTrackingY = 0
    private var startedTrackingPointerId = 0
    private var velocityTracker: VelocityTracker? = null
    private var beginTrackingSent = false
    private var currentAnimation: AnimatorSet? = null
    private val paddingTope = 0
    private val scrimPaint = Paint()
    private var lastInsets: Any? = null
    private var inLayout = false
    private val minDrawerMargin: Int
    private var scrimOpacity = 0f
    private val shadowLeft: Drawable?
    private var allowOpenDrawer = false
    private var drawerPosition = 0f
    var isDrawerOpened = false
        private set
    private var allowDrawContent = true
    @SuppressLint("NewApi")
    private fun dispatchChildInsets(child: View, insets: Any?, drawerGravity: Int) {
        var wi = insets as WindowInsets?
        if (drawerGravity == Gravity.LEFT) {
            wi = wi!!.replaceSystemWindowInsets(wi.systemWindowInsetLeft, wi.systemWindowInsetTop, 0, wi.systemWindowInsetBottom)
        } else if (drawerGravity == Gravity.RIGHT) {
            wi = wi!!.replaceSystemWindowInsets(0, wi.systemWindowInsetTop, wi.systemWindowInsetRight, wi.systemWindowInsetBottom)
        }
        child.dispatchApplyWindowInsets(wi)
    }

    @SuppressLint("NewApi")
    private fun applyMarginInsets(lp: MarginLayoutParams, insets: Any?, drawerGravity: Int, topOnly: Boolean) {
        var wi = insets as WindowInsets?
        if (drawerGravity == Gravity.LEFT) {
            wi = wi!!.replaceSystemWindowInsets(wi.systemWindowInsetLeft, wi.systemWindowInsetTop, 0, wi.systemWindowInsetBottom)
        } else if (drawerGravity == Gravity.RIGHT) {
            wi = wi!!.replaceSystemWindowInsets(0, wi.systemWindowInsetTop, wi.systemWindowInsetRight, wi.systemWindowInsetBottom)
        }
        lp.leftMargin = wi!!.systemWindowInsetLeft
        lp.topMargin = if (topOnly) 0 else wi.systemWindowInsetTop
        lp.rightMargin = wi.systemWindowInsetRight
        lp.bottomMargin = wi.systemWindowInsetBottom
    }

    private fun getTopInset(insets: Any?): Int {
        return if (Build.VERSION.SDK_INT >= 21) {
            if (insets != null) (insets as WindowInsets).systemWindowInsetTop else 0
        } else 0
    }

    fun setDrawerLayout(layout: ViewGroup?) {
        drawerLayout = layout
        addView(drawerLayout)
        if (Build.VERSION.SDK_INT >= 21) {
            drawerLayout!!.fitsSystemWindows = true
        }
    }

    fun moveDrawerByX(dx: Float) {
        setDrawerPosition(drawerPosition + dx)
    }

    fun setDrawerPosition(value: Float) {
        drawerPosition = value
        if (drawerPosition > drawerLayout!!.measuredWidth) {
            drawerPosition = drawerLayout!!.measuredWidth.toFloat()
        } else if (drawerPosition < 0) {
            drawerPosition = 0f
        }
        drawerLayout!!.translationX = drawerPosition
        val newVisibility = if (drawerPosition > 0) View.VISIBLE else View.GONE
        if (drawerLayout!!.visibility != newVisibility) {
            drawerLayout!!.visibility = newVisibility
        }
        setScrimOpacity(drawerPosition / drawerLayout!!.measuredWidth.toFloat())
    }

    fun getDrawerPosition(): Float {
        return drawerPosition
    }

    fun cancelCurrentAnimation() {
        if (currentAnimation != null) {
            currentAnimation!!.cancel()
            currentAnimation = null
        }
    }

    fun openDrawer(fast: Boolean) {
        if (!allowOpenDrawer) {
            return
        }
        if (AndroidUtilities.isTablet() && parentActionBarLayout != null && parentActionBarLayout!!.parentActivity != null) {
            AndroidUtilities.hideKeyboard(parentActionBarLayout!!.parentActivity!!.currentFocus)
        }
        cancelCurrentAnimation()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "drawerPosition", drawerLayout!!.measuredWidth.toFloat()))
        animatorSet.interpolator = DecelerateInterpolator()
        if (fast) {
            animatorSet.duration = Math.max((200.0f / drawerLayout!!.measuredWidth * (drawerLayout!!.measuredWidth - drawerPosition)).toInt(), 50).toLong()
        } else {
            animatorSet.duration = 300
        }
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                onDrawerAnimationEnd(true)
            }
        })
        animatorSet.start()
        currentAnimation = animatorSet
    }

    fun closeDrawer(fast: Boolean) {
        cancelCurrentAnimation()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "drawerPosition", 0f)
        )
        animatorSet.interpolator = DecelerateInterpolator()
        if (fast) {
            animatorSet.duration = Math.max((200.0f / drawerLayout!!.measuredWidth * drawerPosition).toInt(), 50).toLong()
        } else {
            animatorSet.duration = 300
        }
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                onDrawerAnimationEnd(false)
            }
        })
        animatorSet.start()
    }

    private fun onDrawerAnimationEnd(opened: Boolean) {
        startedTracking = false
        currentAnimation = null
        isDrawerOpened = opened
        if (!opened) {
            if (drawerLayout is ListView) {
                (drawerLayout as ListView).setSelectionFromTop(0, 0)
            }
        }
    }

    private fun setScrimOpacity(value: Float) {
        scrimOpacity = value
        invalidate()
    }

    private fun getScrimOpacity(): Float {
        return scrimOpacity
    }

    fun getDrawerLayout(): View? {
        return drawerLayout
    }

    fun setParentActionBarLayout(layout: ActionBarLayout?) {
        parentActionBarLayout = layout
    }

    fun setAllowOpenDrawer(value: Boolean, animated: Boolean) {
        allowOpenDrawer = value
        if (!allowOpenDrawer && drawerPosition != 0f) {
            if (!animated) {
                setDrawerPosition(0f)
                onDrawerAnimationEnd(false)
            } else {
                closeDrawer(true)
            }
        }
    }

    private fun prepareForDrawerOpen(ev: MotionEvent?) {
        maybeStartTracking = false
        startedTracking = true
        if (ev != null) {
            startedTrackingX = ev.x.toInt()
        }
        beginTrackingSent = false
    }

    fun setAllowDrawContent(value: Boolean) {
        if (allowDrawContent != value) {
            allowDrawContent = value
            invalidate()
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (isDrawerOpened && ev != null && ev.x > drawerPosition && !startedTracking) {
            if (ev.action == MotionEvent.ACTION_UP) {
                closeDrawer(false)
            }
            return true
        }
        if ( /*!parentActionBarLayout.checkTransitionAnimation()*/false) {
            if (isDrawerOpened && ev != null && ev.x > drawerPosition && !startedTracking) {
                if (ev.action == MotionEvent.ACTION_UP) {
                    closeDrawer(false)
                }
                return true
            }
            if (allowOpenDrawer && parentActionBarLayout!!.fragmentsStack!!.size == 1) {
                if (ev != null && (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE) && !startedTracking && !maybeStartTracking) {
                    startedTrackingPointerId = ev.getPointerId(0)
                    maybeStartTracking = true
                    startedTrackingX = ev.x.toInt()
                    startedTrackingY = ev.y.toInt()
                    cancelCurrentAnimation()
                    if (velocityTracker != null) {
                        velocityTracker!!.clear()
                    }
                } else if (ev != null && ev.action == MotionEvent.ACTION_MOVE && ev.getPointerId(0) == startedTrackingPointerId) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    }
                    val dx: Float = (ev.x - startedTrackingX)
                    val dy = Math.abs(ev.y.toInt() - startedTrackingY).toFloat()
                    velocityTracker!!.addMovement(ev)
                    if (maybeStartTracking && !startedTracking && (dx > 0 && dx / 3.0f > Math.abs(dy) && Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.2f, true) || dx < 0 && Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.4f, true))) {
                        prepareForDrawerOpen(ev)
                        startedTrackingX = ev.x.toInt()
                        requestDisallowInterceptTouchEvent(true)
                    } else if (startedTracking) {
                        if (!beginTrackingSent) {
                            if ((context as Activity).currentFocus != null) {
                                AndroidUtilities.hideKeyboard((context as Activity).currentFocus)
                            }
                            beginTrackingSent = true
                        }
                        moveDrawerByX(dx)
                        startedTrackingX = ev.x.toInt()
                    }
                } else if (ev == null || ev != null && ev.getPointerId(0) == startedTrackingPointerId && (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_POINTER_UP)) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    }
                    velocityTracker!!.computeCurrentVelocity(1000)
                    /*if (!startedTracking) {
                        float velX = velocityTracker.getXVelocity();
                        float velY = velocityTracker.getYVelocity();
                        if (Math.abs(velX) >= 3500 && Math.abs(velX) > Math.abs(velY)) {
                            prepareForDrawerOpen(ev);
                            if (!beginTrackingSent) {
                                if (((Activity)getContext()).getCurrentFocus() != null) {
                                    support.AndroidUtilities.hideKeyboard(((Activity)getContext()).getCurrentFocus());
                                }
                                beginTrackingSent = true;
                            }
                        }
                    }*/if (startedTracking || drawerPosition != 0f && drawerPosition != drawerLayout!!.measuredWidth.toFloat()) {
                        val velX = velocityTracker!!.xVelocity
                        val velY = velocityTracker!!.yVelocity
                        val backAnimation = drawerPosition < drawerLayout!!.measuredWidth / 2.0f && (velX < 3500 || Math.abs(velX) < Math.abs(velY)) || velX < 0 && Math.abs(velX) >= 3500
                        if (!backAnimation) {
                            openDrawer(!isDrawerOpened && Math.abs(velX) >= 3500)
                        } else {
                            closeDrawer(isDrawerOpened && Math.abs(velX) >= 3500)
                        }
                    }
                    startedTracking = false
                    maybeStartTracking = false
                    if (velocityTracker != null) {
                        velocityTracker!!.recycle()
                        velocityTracker = null
                    }
                }
            }
            return startedTracking
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return parentActionBarLayout!!.checkTransitionAnimation() || onTouchEvent(ev)
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (maybeStartTracking && !startedTracking) {
            onTouchEvent(null)
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        inLayout = true
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            val lp = child.layoutParams as LayoutParams
            try {
                if (drawerLayout !== child) {
                    child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.measuredWidth, lp.topMargin + child.measuredHeight + getPaddingTop())
                } else {
                    child.layout(-child.measuredWidth, lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.measuredHeight + +getPaddingTop())
                }
            } catch (e: Exception) {
            }
        }
        inLayout = false
    }

    override fun requestLayout() {
        if (!inLayout) { /*StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            for (int a = 0; a < elements.length; a++) {
                FileLog.d("on " + elements[a]);
            }*/
            super.requestLayout()
        }
    }

    @SuppressLint("NewApi")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        if (Build.VERSION.SDK_INT < 21) {
            inLayout = true
            if (heightSize == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (layoutParams is MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0)
                }
                heightSize = AndroidUtilities.displaySize.y
            } else {
                if (layoutParams is MarginLayoutParams) {
                    setPadding(0, 0, 0, 0)
                }
            }
            inLayout = false
        }
        val applyInsets = lastInsets != null && Build.VERSION.SDK_INT >= 21
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            val lp = child.layoutParams as LayoutParams
            if (applyInsets) {
                if (child.fitsSystemWindows) {
                    dispatchChildInsets(child, lastInsets, lp.gravity)
                } else if (child.tag == null) {
                    applyMarginInsets(lp, lastInsets, lp.gravity, Build.VERSION.SDK_INT >= 21)
                }
            }
            if (drawerLayout !== child) {
                val contentWidthSpec = MeasureSpec.makeMeasureSpec(widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY)
                val contentHeightSpec = MeasureSpec.makeMeasureSpec(heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY)
                child.measure(contentWidthSpec, contentHeightSpec)
            } else {
                child.setPadding(0, 0, 0, 0)
                val drawerWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, minDrawerMargin + lp.leftMargin + lp.rightMargin, lp.width)
                val drawerHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height)
                child.measure(drawerWidthSpec, drawerHeightSpec)
            }
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        if (!allowDrawContent) {
            return false
        }
        val height = height
        val drawingContent = child !== drawerLayout
        var lastVisibleChild = 0
        var clipLeft = 0
        val clipRight = width
        val restoreCount = canvas.save()
        if (drawingContent) {
            val childCount = childCount
            for (i in 0 until childCount) {
                val v = getChildAt(i)
                if (v.visibility == View.VISIBLE && v !== drawerLayout) {
                    lastVisibleChild = i
                }
                if (v === child || v.visibility != View.VISIBLE || v !== drawerLayout || v.height < height) {
                    continue
                }
                val vright = v.right
                if (vright > clipLeft) {
                    clipLeft = vright
                }
            }
            if (clipLeft != 0) {
                canvas.clipRect(clipLeft, 0, clipRight, getHeight())
            }
        }
        val result = super.drawChild(canvas, child, drawingTime)
        canvas.restoreToCount(restoreCount)
        if (scrimOpacity > 0 && drawingContent) {
            if (indexOfChild(child) == lastVisibleChild) {
                scrimPaint.color = ((-0x67000000 and -0x1000000 ushr 24) * scrimOpacity).toInt() shl 24
                canvas.drawRect(clipLeft.toFloat(), 0f, clipRight.toFloat(), getHeight().toFloat(), scrimPaint)
            }
        } else if (shadowLeft != null) {
            val alpha: Float = Math.max(0f, Math.min(drawerPosition / AndroidUtilities.dp(20f), 1.0f))
            if (alpha != 0f) {
                shadowLeft.setBounds(drawerPosition.toInt(), child.top, drawerPosition.toInt() + shadowLeft.intrinsicWidth, child.bottom)
                shadowLeft.alpha = (0xff * alpha).toInt()
                shadowLeft.draw(canvas)
            }
        }
        return result
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    companion object {
        private const val MIN_DRAWER_MARGIN = 64
    }

    init {
        minDrawerMargin = ((MIN_DRAWER_MARGIN * AndroidUtilities.density + 0.5f).toInt())
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        isFocusableInTouchMode = true
        if (Build.VERSION.SDK_INT >= 21) {
            fitsSystemWindows = true
            setOnApplyWindowInsetsListener { v, insets ->
                val drawerLayout = v as DrawerLayoutContainer
                AndroidUtilities.statusBarHeight = insets.systemWindowInsetTop
                lastInsets = insets
                drawerLayout.setWillNotDraw(insets.systemWindowInsetTop <= 0 && background == null)
                drawerLayout.requestLayout()
                insets.consumeSystemWindowInsets()
            }
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        shadowLeft = resources.getDrawable(R.drawable.menu_shadow)
    }
}