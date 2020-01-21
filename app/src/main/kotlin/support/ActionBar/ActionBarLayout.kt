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
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.yaya.map.R
import support.LayoutHelper
import support.Theme
import support.component.AndroidUtilities
import support.component.AndroidUtilities.Companion.dp
import support.component.AndroidUtilities.Companion.hideKeyboard
import support.component.AndroidUtilities.Companion.isTablet
import java.util.*

class ActionBarLayout(context: Context?) : FrameLayout(context) {
    interface ActionBarLayoutDelegate {
        fun onPreIme(): Boolean
        fun needPresentFragment(fragment: BaseFragment?, removeLast: Boolean, forceWithoutAnimation: Boolean, layout: ActionBarLayout?): Boolean
        fun needAddFragmentToStack(fragment: BaseFragment?, layout: ActionBarLayout?): Boolean
        fun needCloseLastFragment(layout: ActionBarLayout?): Boolean
        fun onRebuildAllFragments(layout: ActionBarLayout?)
    }

    inner class LinearLayoutContainer(context: Context?) : LinearLayout(context) {
        private val rect = Rect()
        var isKeyboardVisible = false
        override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
            return if (child is ActionBar) {
                super.drawChild(canvas, child, drawingTime)
            } else {
                var actionBarHeight = 0
                val childCount = childCount
                for (a in 0 until childCount) {
                    val view = getChildAt(a)
                    if (view === child) {
                        continue
                    }
                    if (view is ActionBar && view.getVisibility() == View.VISIBLE) {
                        if (view.castShadows) {
                            actionBarHeight = view.getMeasuredHeight()
                        }
                        break
                    }
                }
                val result = super.drawChild(canvas, child, drawingTime)
                if (actionBarHeight != 0 && headerShadowDrawable != null) {
                    headerShadowDrawable!!.setBounds(0, actionBarHeight, measuredWidth, actionBarHeight + headerShadowDrawable!!.getIntrinsicHeight())
                    headerShadowDrawable!!.draw(canvas)
                }
                result
            }
        }

        override fun hasOverlappingRendering(): Boolean {
            return false
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            super.onLayout(changed, l, t, r, b)
            val rootView = rootView
            getWindowVisibleDisplayFrame(rect)
            val usableViewHeight: Int = rootView.height - (if (rect.top != 0) AndroidUtilities.statusBarHeight else 0) - AndroidUtilities.getViewInset(rootView)
            isKeyboardVisible = usableViewHeight - (rect.bottom - rect.top) > 0
            if (waitingForKeyboardCloseRunnable != null && !containerView!!.isKeyboardVisible && !containerViewBack!!.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(waitingForKeyboardCloseRunnable)
                waitingForKeyboardCloseRunnable!!.run()
                waitingForKeyboardCloseRunnable = null
            }
        }

        init {
            orientation = VERTICAL
        }
    }

    private var waitingForKeyboardCloseRunnable: Runnable? = null
    private var delayedOpenAnimationRunnable: Runnable? = null
    private var containerView: LinearLayoutContainer? = null
    private var containerViewBack: LinearLayoutContainer? = null
    var drawerLayoutContainer: DrawerLayoutContainer? = null
    private var currentActionBar: ActionBar? = null
    private var currentAnimation: AnimatorSet? = null
    private val decelerateInterpolator = DecelerateInterpolator(1.5f)
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
    var innerTranslationX = 0f
    private var maybeStartTracking = false
    var startedTracking = false
    private var startedTrackingX = 0
    private var startedTrackingY = 0
    var animationInProgress = false
    private var velocityTracker: VelocityTracker? = null
    private var beginTrackingSent = false
    private var transitionAnimationInProgress = false
    private var rebuildAfterAnimation = false
    private var rebuildLastAfterAnimation = false
    private var showLastAfterAnimation = false
    private var transitionAnimationStartTime: Long = 0
    private var inActionMode = false
    private var startedTrackingPointerId = 0
    private var onCloseAnimationEndRunnable: Runnable? = null
    private var onOpenAnimationEndRunnable: Runnable? = null
    private var useAlphaAnimations = false
    private var backgroundView: View? = null
    private var removeActionBarExtraHeight = false
    private var animationRunnable: Runnable? = null
    private var animationProgress = 0.0f
    private var lastFrameTime: Long = 0
    private var titleOverlayText: String? = null
    private var subtitleOverlayText: String? = null
    private var overlayAction: Runnable? = null
    private var delegate: ActionBarLayoutDelegate? = null
    var parentActivity: Activity? = null
    var fragmentsStack: ArrayList<BaseFragment>? = null
    fun init(stack: ArrayList<BaseFragment>?) {
        fragmentsStack = stack
        containerViewBack = LinearLayoutContainer(parentActivity)
        addView(containerViewBack)
        var layoutParams = containerViewBack!!.layoutParams as LayoutParams
        layoutParams.width = LayoutHelper.MATCH_PARENT
        layoutParams.height = LayoutHelper.MATCH_PARENT
        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        containerViewBack!!.layoutParams = layoutParams
        containerView = LinearLayoutContainer(parentActivity)
        addView(containerView)
        layoutParams = containerView!!.layoutParams as LayoutParams
        layoutParams.width = LayoutHelper.MATCH_PARENT
        layoutParams.height = LayoutHelper.MATCH_PARENT
        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        containerView!!.layoutParams = layoutParams
        for (fragment in fragmentsStack!!) {
            fragment.setParentLayout(this)
        }
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!fragmentsStack!!.isEmpty()) {
            val lastFragment = fragmentsStack!![fragmentsStack!!.size - 1] as BaseFragment
            lastFragment.onConfigurationChanged(newConfig)
            if (lastFragment.visibleDialog is BottomSheet) {
                (lastFragment.visibleDialog as BottomSheet).onConfigurationChanged(newConfig)
            }
        }
    }

    fun drawHeaderShadow(canvas: Canvas?, y: Int) {
        if (headerShadowDrawable != null) {
            headerShadowDrawable!!.setBounds(0, y, measuredWidth, y + headerShadowDrawable!!.getIntrinsicHeight())
            headerShadowDrawable!!.draw(canvas)
        }
    }

    fun setInnerTranslationsX(value: Float) {
        innerTranslationX = value
        invalidate()
    }

    fun getInnerTranslationsX(): Float {
        return innerTranslationX
    }

    fun dismissDialogs() {
        if (!fragmentsStack!!.isEmpty()) {
            val lastFragment = fragmentsStack!![fragmentsStack!!.size - 1]
            lastFragment.dismissCurrentDialig()
        }
    }

    fun onResume() {
        if (transitionAnimationInProgress) {
            if (currentAnimation != null) {
                currentAnimation!!.cancel()
                currentAnimation = null
            }
            if (onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd()
            } else if (onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd()
            }
        }
        if (!fragmentsStack!!.isEmpty()) {
            val lastFragment = fragmentsStack!![fragmentsStack!!.size - 1]
            lastFragment.onResume()
        }
    }

    fun onPause() {
        if (!fragmentsStack!!.isEmpty()) {
            val lastFragment = fragmentsStack!![fragmentsStack!!.size - 1]
            lastFragment.onPause()
        }
    }


    public override fun onInterceptTouchEvent(ev: MotionEvent ) : Boolean{
        return !(!animationInProgress && !checkTransitionAnimation()) || onTouchEvent(ev)
    }

    public override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        onTouchEvent(null)
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }
    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        return if (event != null && event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            delegate != null && delegate!!.onPreIme() || super.dispatchKeyEventPreIme(event)
        } else super.dispatchKeyEventPreIme(event)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val width = width - paddingLeft - paddingRight
        val translationX = innerTranslationX.toInt() + paddingRight
        var clipLeft = paddingLeft
        var clipRight = width + paddingLeft
        if (child === containerViewBack) {
            clipRight = translationX
        } else if (child === containerView) {
            clipLeft = translationX
        }
        val restoreCount = canvas.save()
        if (!transitionAnimationInProgress) {
            canvas.clipRect(clipLeft, 0, clipRight, height)
        }
        val result = super.drawChild(canvas, child, drawingTime)
        canvas.restoreToCount(restoreCount)
        if (translationX != 0) {
            if (child === containerView) {
                val alpha = Math.max(0f, Math.min((width - translationX) / dp(20f).toFloat(), 1.0f))
                layerShadowDrawable!!.setBounds(translationX - layerShadowDrawable!!.intrinsicWidth, child.getTop(), translationX, child.getBottom())
                layerShadowDrawable!!.alpha = (0xff * alpha).toInt()
                layerShadowDrawable!!.draw(canvas)
            } else if (child === containerViewBack) {
                var opacity = Math.min(0.8f, (width - translationX) / width.toFloat())
                if (opacity < 0) {
                    opacity = 0f
                }
                scrimPaint!!.color = ((-0x67000000 and -0x1000000 ushr 24) * opacity).toInt() shl 24
                canvas.drawRect(clipLeft.toFloat(), 0f, clipRight.toFloat(), height.toFloat(), scrimPaint)
            }
        }
        return result
    }

    fun setDelegate(delegate: ActionBarLayoutDelegate?) {
        this.delegate = delegate
    }

    private fun onSlideAnimationEnd(backAnimation: Boolean) {
        if (!backAnimation) {
            var lastFragment = fragmentsStack!![fragmentsStack!!.size - 1]
            lastFragment.onPause()
            lastFragment.onFragmentDestroy()
            lastFragment.setParentLayout(null)
            fragmentsStack!!.removeAt(fragmentsStack!!.size - 1)
            val temp = containerView
            containerView = containerViewBack
            containerViewBack = temp
            bringChildToFront(containerView)
            lastFragment = fragmentsStack!![fragmentsStack!!.size - 1]
            currentActionBar = lastFragment.actionBar!!
            lastFragment.onResume()
            lastFragment.onBecomeFullyVisible()
        } else {
            val lastFragment = fragmentsStack!![fragmentsStack!!.size - 2]
            lastFragment.onPause()
            if (lastFragment.fragmentView != null) {
                val parent = lastFragment.fragmentView!!.parent as ViewGroup
                if (parent != null) {
                    lastFragment.onRemoveFromParent()
                    parent.removeView(lastFragment.fragmentView)
                }
            }
            if (lastFragment.actionBar!! != null && lastFragment.actionBar!!!!.addToContainer) {
                val parent = lastFragment.actionBar!!!!.parent as ViewGroup
                parent?.removeView(lastFragment.actionBar!!)
            }
        }
        containerViewBack!!.visibility = View.GONE
        startedTracking = false
        animationInProgress = false
        containerView!!.translationX = 0f
        containerViewBack!!.translationX = 0f
        setInnerTranslationsX(0f)
    }

    private fun prepareForMoving(ev: MotionEvent) {
        maybeStartTracking = false
        startedTracking = true
        startedTrackingX = ev.x.toInt()
        containerViewBack!!.visibility = View.VISIBLE
        beginTrackingSent = false
        val lastFragment = fragmentsStack!![fragmentsStack!!.size - 2]
        var fragmentView = lastFragment.fragmentView
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(parentActivity)
        }
        var parent = fragmentView!!.parent as ViewGroup
        if (parent != null) {
            lastFragment.onRemoveFromParent()
            parent.removeView(fragmentView)
        }
        if (lastFragment.actionBar!! != null && lastFragment.actionBar!!!!.addToContainer) {
            parent = lastFragment.actionBar!!.parent as ViewGroup
            parent?.removeView(lastFragment.actionBar!!)
            if (removeActionBarExtraHeight) {
                lastFragment.actionBar!!.setOccupyStatusBar(false)
            }
            containerViewBack!!.addView(lastFragment.actionBar!!)
            lastFragment.actionBar!!.setTitleOverlayText(titleOverlayText, subtitleOverlayText, overlayAction)
        }
        containerViewBack!!.addView(fragmentView)
        val layoutParams = fragmentView.layoutParams
        layoutParams.width = LayoutHelper.MATCH_PARENT
        layoutParams.height = LayoutHelper.MATCH_PARENT
        fragmentView.layoutParams = layoutParams
        if (!lastFragment.hasOwnBackground && fragmentView.background == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
        }
        lastFragment.onResume()
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!checkTransitionAnimation() && !inActionMode && !animationInProgress) {
            if (fragmentsStack!!.size > 1) {
                if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN && !startedTracking && !maybeStartTracking) {
                    val currentFragment = fragmentsStack!![fragmentsStack!!.size - 1]
                    if (!currentFragment.swipeBackEnabled) {
                        return false
                    }
                    startedTrackingPointerId = ev.getPointerId(0)
                    maybeStartTracking = true
                    startedTrackingX = ev.x.toInt()
                    startedTrackingY = ev.y.toInt()
                    if (velocityTracker != null) {
                        velocityTracker!!.clear()
                    }
                } else if (ev != null && ev.action == MotionEvent.ACTION_MOVE && ev.getPointerId(0) == startedTrackingPointerId) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    }
                    val dx = Math.max(0, (ev.getX() - startedTrackingX).toInt())
                    val dy = Math.abs(ev.getY() - startedTrackingY)
                    velocityTracker!!.addMovement(ev)
                    if (maybeStartTracking && !startedTracking && dx >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                        prepareForMoving(ev)
                    } else if (startedTracking) {
                        if (!beginTrackingSent) {
                            if (parentActivity!!.getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(parentActivity!!.getCurrentFocus())
                            }
                            val currentFragment = fragmentsStack!!.get(fragmentsStack!!.size - 1)
                            currentFragment.onBeginSlide()
                            beginTrackingSent = true
                        }
                        containerView!!.setTranslationX(dx.toFloat())
                        innerTranslationX = dx.toFloat()
                    }
                } else if (ev != null && ev.getPointerId(0) == startedTrackingPointerId && (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_POINTER_UP)) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    }
                    velocityTracker!!.computeCurrentVelocity(1000)
                    if (!startedTracking && fragmentsStack!!.get(fragmentsStack!!.size - 1).swipeBackEnabled) {
                        val velX = velocityTracker!!.getXVelocity()
                        val velY = velocityTracker!!.getYVelocity()
                        if (velX >= 3500 && velX > Math.abs(velY)) {
                            prepareForMoving(ev);
                            if (!beginTrackingSent) {
                                if ((getContext() as Activity).getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard((getContext() as Activity).getCurrentFocus())
                                }
                                beginTrackingSent = true;
                            }
                        }
                    }
                    if (startedTracking) {
                        val x = containerView!!.getX()
                        val animatorSet = AnimatorSet()
                        val velX = velocityTracker!!.getXVelocity()
                        val velY = velocityTracker!!.getYVelocity()
                        val backAnimation = x < containerView!!.getMeasuredWidth() / 3.0f && (velX < 3500 || velX < velY);
                        var distToMove: Float
                        if (!backAnimation) {
                            distToMove = containerView!!.getMeasuredWidth() - x
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(containerView!!, "translationX", containerView!!.measuredWidth.toFloat()),
                                    ObjectAnimator.ofFloat(this, "innerTranslationX", containerView!!.measuredWidth.toFloat())
                            );
                        } else {
                            distToMove = x
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(containerView!!, "translationX", 0f),
                                    ObjectAnimator.ofFloat(this, "innerTranslationX", 0.0f)
                            )
                        }

                        animatorSet.setDuration(Math.max((200.0f / containerView!!.getMeasuredWidth() * distToMove).toInt(), 50).toLong())
                        animatorSet.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animator: Animator) {
                                onSlideAnimationEnd(backAnimation)
                            }
                        })
                        animatorSet.start()
                        animationInProgress = true
                    } else {
                        maybeStartTracking = false
                        startedTracking = false
                    }
                    if (velocityTracker != null) {
                        velocityTracker!!.recycle()
                        velocityTracker = null
                    }
                } else if (ev == null) {
                    maybeStartTracking = false
                    startedTracking = false
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

    fun onBackPressed() {
        if (startedTracking || checkTransitionAnimation() || fragmentsStack!!.isEmpty()) {
            return
        }
        if (currentActionBar != null && currentActionBar!!.isSearchFieldVisible) {
            currentActionBar!!.closeSearchField()
            return
        }
        val lastFragment = fragmentsStack!![fragmentsStack!!.size - 1]
        if (lastFragment.onBackPressed()) {
            if (!fragmentsStack!!.isEmpty()) {
                closeLastFragment(true)
            }
        }
    }

    fun onLowMemory() {
        for (fragment in fragmentsStack!!) {
            fragment.onLowMemory()
        }
    }

    private fun onAnimationEndCheck(byCheck: Boolean) {
        onCloseAnimationEnd()
        onOpenAnimationEnd()
        if (waitingForKeyboardCloseRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(waitingForKeyboardCloseRunnable)
            waitingForKeyboardCloseRunnable = null
        }
        if (currentAnimation != null) {
            if (byCheck) {
                currentAnimation!!.cancel()
            }
            currentAnimation = null
        }
        if (animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(animationRunnable)
            animationRunnable = null
        }
        alpha = 1.0f
        containerView!!.alpha = 1.0f
        containerView!!.scaleX = 1.0f
        containerView!!.scaleY = 1.0f
        containerViewBack!!.alpha = 1.0f
        containerViewBack!!.scaleX = 1.0f
        containerViewBack!!.scaleY = 1.0f
    }

    fun checkTransitionAnimation(): Boolean {
        if (transitionAnimationInProgress && transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true)
        }
        return transitionAnimationInProgress
    }

    private fun presentFragmentInternalRemoveOld(removeLast: Boolean, fragment: BaseFragment?) {
        if (fragment == null) {
            return
        }
        fragment.onPause()
        if (removeLast) {
            fragment.onFragmentDestroy()
            fragment.setParentLayout(null)
            fragmentsStack!!.remove(fragment)
        } else {
            if (fragment.fragmentView != null) {
                val parent = fragment.fragmentView!!.parent as ViewGroup
                if (parent != null) {
                    fragment.onRemoveFromParent()
                    parent.removeView(fragment.fragmentView)
                }
            }
            if (fragment.actionBar != null && fragment.actionBar!!.addToContainer) {
                if (fragment.actionBar!!.parent != null) {
                    val parent = fragment.actionBar!!.parent as ViewGroup
                    parent?.removeView(fragment.actionBar!!)
                }
            }
        }
        containerViewBack!!.visibility = View.GONE
    }

    private fun startLayoutAnimation(open: Boolean, first: Boolean) {
        if (first) {
            animationProgress = 0.0f
            lastFrameTime = System.nanoTime() / 1000000
        }
        AndroidUtilities.runOnUIThread(object : Runnable {
            override fun run() {
                if (animationRunnable !== this) {
                    return
                }
                animationRunnable = null
                if (first) {
                    transitionAnimationStartTime = System.currentTimeMillis()
                }
                val newTime = System.nanoTime() / 1000000
                var dt = newTime - lastFrameTime
                if (dt > 18) {
                    dt = 18
                }
                lastFrameTime = newTime
                animationProgress += dt / 150.0f
                if (animationProgress > 1.0f) {
                    animationProgress = 1.0f
                }
                val interpolated = decelerateInterpolator.getInterpolation(animationProgress)
                if (open) {
                    containerView!!.alpha = interpolated
                    containerView!!.translationX = dp(48f) * (1.0f - interpolated)
                } else {
                    containerViewBack!!.alpha = 1.0f - interpolated
                    containerViewBack!!.translationX = dp(48f) * interpolated
                }
                if (animationProgress < 1) {
                    startLayoutAnimation(open, false)
                } else {
                    onAnimationEndCheck(false)
                }
            }
        }.also { animationRunnable = it })
    }

    fun resumeDelayedFragmentAnimation() {
        if (delayedOpenAnimationRunnable == null) {
            return
        }
        AndroidUtilities.cancelRunOnUIThread(delayedOpenAnimationRunnable)
        delayedOpenAnimationRunnable!!.run()
        delayedOpenAnimationRunnable = null
    }

    @JvmOverloads
    fun presentFragment(fragment: BaseFragment, removeLast: Boolean = false, forceWithoutAnimation: Boolean = false, check: Boolean = true): Boolean {
        if (checkTransitionAnimation() || delegate != null && check && !delegate!!.needPresentFragment(fragment, removeLast, forceWithoutAnimation, this) || !fragment.onFragmentCreate()) {
            return false
        }
        if (parentActivity!!.currentFocus != null) {
            hideKeyboard(parentActivity!!.currentFocus)
        }
        val needAnimation = !forceWithoutAnimation && parentActivity!!.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).getBoolean("view_animations", true)
        val currentFragment = if (!fragmentsStack!!.isEmpty()) fragmentsStack!![fragmentsStack!!.size - 1] else null
        fragment.setParentLayout(this)
        var fragmentView = fragment.fragmentView
        if (fragmentView == null) {
            fragmentView = fragment.createView(parentActivity)
        } else {
            val parent = fragmentView.parent as ViewGroup
            if (parent != null) {
                fragment.onRemoveFromParent()
                parent.removeView(fragmentView)
            }
        }
        if (fragment.actionBar != null && fragment.actionBar!!.addToContainer) {
            if (removeActionBarExtraHeight) {
                fragment.actionBar!!.setOccupyStatusBar(false)
            }
            if (fragment.actionBar!!.parent != null) {
                val parent = fragment.actionBar!!.parent as ViewGroup
                parent.removeView(fragment.actionBar!!)
            }
            containerViewBack!!.addView(fragment.actionBar!!)
            fragment.actionBar!!.setTitleOverlayText(titleOverlayText, subtitleOverlayText, overlayAction)
        }
        containerViewBack!!.addView(fragmentView)
        val layoutParams = fragmentView!!.layoutParams
        layoutParams.width = LayoutHelper.MATCH_PARENT
        layoutParams.height = LayoutHelper.MATCH_PARENT
        fragmentView.layoutParams = layoutParams
        fragmentsStack!!.add(fragment)
        fragment.onResume()
        currentActionBar = fragment.actionBar!!
        if (!fragment.hasOwnBackground && fragmentView.background == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
        }
        val temp = containerView
        containerView = containerViewBack
        containerViewBack = temp
        containerView!!.visibility = View.VISIBLE
        setInnerTranslationsX(0f)
        bringChildToFront(containerView)
        if (!needAnimation) {
            presentFragmentInternalRemoveOld(removeLast, currentFragment)
            if (backgroundView != null) {
                backgroundView!!.visibility = View.VISIBLE
            }
        }
        if (needAnimation) {
            if (useAlphaAnimations && fragmentsStack!!.size == 1) {
                presentFragmentInternalRemoveOld(removeLast, currentFragment)
                transitionAnimationStartTime = System.currentTimeMillis()
                transitionAnimationInProgress = true
                onOpenAnimationEndRunnable = Runnable {
                    fragment.onTransitionAnimationEnd(true, false)
                    fragment.onBecomeFullyVisible()
                }
                val animators = ArrayList<Animator>()
                animators.add(ObjectAnimator.ofFloat(this, "alpha", 0.0f, 1.0f))
                if (backgroundView != null) {
                    backgroundView!!.visibility = View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(backgroundView!!, "alpha", 0.0f, 1.0f))
                }
                fragment.onTransitionAnimationStart(true, false)
                currentAnimation = AnimatorSet()
                currentAnimation!!.playTogether(animators)
                currentAnimation!!.interpolator = accelerateDecelerateInterpolator
                currentAnimation!!.duration = 200
                currentAnimation!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        onAnimationEndCheck(false)
                    }
                })
                currentAnimation!!.start()
            } else {
                transitionAnimationStartTime = System.currentTimeMillis()
                transitionAnimationInProgress = true
                onOpenAnimationEndRunnable = Runnable {
                    presentFragmentInternalRemoveOld(removeLast, currentFragment)
                    fragment.onTransitionAnimationEnd(true, false)
                    fragment.onBecomeFullyVisible()
                    containerView!!.translationX = 0f
                }
                fragment.onTransitionAnimationStart(true, false)
                val animation = fragment.onCustomTransitionAnimation(true, Runnable { onAnimationEndCheck(false) })
                if (animation == null) {
                    containerView!!.alpha = 0.0f
                    containerView!!.translationX = 48.0f
                    if (containerView!!.isKeyboardVisible || containerViewBack!!.isKeyboardVisible) {
                        waitingForKeyboardCloseRunnable = object : Runnable {
                            override fun run() {
                                if (waitingForKeyboardCloseRunnable !== this) {
                                    return
                                }
                                waitingForKeyboardCloseRunnable = null
                                startLayoutAnimation(true, true)
                            }
                        }
                        AndroidUtilities.runOnUIThread(waitingForKeyboardCloseRunnable, 200)
                    } else if (fragment.needDelayOpenAnimation()) {
                        delayedOpenAnimationRunnable = object : Runnable {
                            override fun run() {
                                if (delayedOpenAnimationRunnable !== this) {
                                    return
                                }
                                delayedOpenAnimationRunnable = null
                                startLayoutAnimation(true, true)
                            }
                        }
                        AndroidUtilities.runOnUIThread(delayedOpenAnimationRunnable, 200)
                    } else {
                        startLayoutAnimation(true, true)
                    }
                } else {
                    containerView!!.alpha = 1.0f
                    containerView!!.translationX = 0.0f
                    currentAnimation = animation
                }
            }
        } else {
            if (backgroundView != null) {
                backgroundView!!.alpha = 1.0f
                backgroundView!!.visibility = View.VISIBLE
            }
            fragment.onTransitionAnimationStart(true, false)
            fragment.onTransitionAnimationEnd(true, false)
            fragment.onBecomeFullyVisible()
        }
        return true
    }

    @JvmOverloads
    fun addFragmentToStack(fragment: BaseFragment, position: Int = -1): Boolean {
        if (delegate != null && !delegate!!.needAddFragmentToStack(fragment, this) || !fragment.onFragmentCreate()) {
            return false
        }
        fragment.setParentLayout(this)
        if (position == -1) {
            if (!fragmentsStack!!.isEmpty()) {
                val previousFragment = fragmentsStack!![fragmentsStack!!.size - 1]
                previousFragment.onPause()
                if (previousFragment.actionBar!! != null && previousFragment.actionBar!!.addToContainer) {
                    val parent = previousFragment.actionBar!!.parent as ViewGroup
                    parent?.removeView(previousFragment.actionBar!!)
                }
                if (previousFragment.fragmentView != null) {
                    val parent = previousFragment.fragmentView!!.parent as ViewGroup
                    if (parent != null) {
                        previousFragment.onRemoveFromParent()
                        parent.removeView(previousFragment.fragmentView)
                    }
                }
            }
            fragmentsStack!!.add(fragment)
        } else {
            fragmentsStack!!.add(position, fragment)
        }
        return true
    }

    private fun closeLastFragmentInternalRemoveOld(fragment: BaseFragment) {
        fragment.onPause()
        fragment.onFragmentDestroy()
        fragment.setParentLayout(null)
        fragmentsStack!!.remove(fragment)
        containerViewBack!!.visibility = View.GONE
        bringChildToFront(containerView)
    }

    fun closeLastFragment(animated: Boolean) {
        if (delegate != null && !delegate!!.needCloseLastFragment(this) || checkTransitionAnimation() || fragmentsStack!!.isEmpty()) {
            return
        }
        if (parentActivity!!.currentFocus != null) {
            hideKeyboard(parentActivity!!.currentFocus)
        }
        setInnerTranslationsX(0f)
        val needAnimation = animated && parentActivity!!.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).getBoolean("view_animations", true)
        val currentFragment = fragmentsStack!![fragmentsStack!!.size - 1]
        var previousFragment: BaseFragment? = null
        if (fragmentsStack!!.size > 1) {
            previousFragment = fragmentsStack!![fragmentsStack!!.size - 2]
        }
        if (previousFragment != null) {
            val temp = containerView
            containerView = containerViewBack
            containerViewBack = temp
            containerView!!.visibility = View.VISIBLE
            previousFragment.setParentLayout(this)
            var fragmentView = previousFragment.fragmentView
            if (fragmentView == null) {
                fragmentView = previousFragment.createView(parentActivity)
            } else {
                if (fragmentView.parent != null) {
                    val parent = fragmentView.parent as ViewGroup
                    previousFragment.onRemoveFromParent()
                    parent.removeView(fragmentView)
                }
            }
            if (previousFragment.actionBar != null && previousFragment.actionBar!!.addToContainer) {
                if (removeActionBarExtraHeight) {
                    previousFragment.actionBar!!.setOccupyStatusBar(false)
                }
                if (previousFragment.actionBar!!.parent != null) {
                    val parent = previousFragment.actionBar!!.parent as ViewGroup
                    parent.removeView(previousFragment.actionBar!!)
                }
                containerView!!.addView(previousFragment.actionBar!!)
                previousFragment.actionBar!!.setTitleOverlayText(titleOverlayText, subtitleOverlayText, overlayAction)
            }
            containerView!!.addView(fragmentView)
            val layoutParams = fragmentView!!.layoutParams
            layoutParams.width = LayoutHelper.MATCH_PARENT
            layoutParams.height = LayoutHelper.MATCH_PARENT
            fragmentView.layoutParams = layoutParams
            previousFragment.onTransitionAnimationStart(true, true)
            currentFragment.onTransitionAnimationStart(false, false)
            previousFragment.onResume()
            currentActionBar = previousFragment.actionBar!!
            if (!previousFragment.hasOwnBackground && fragmentView.background == null) {
                fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
            }
            if (!needAnimation) {
                closeLastFragmentInternalRemoveOld(currentFragment)
            }
            if (needAnimation) {
                transitionAnimationStartTime = System.currentTimeMillis()
                transitionAnimationInProgress = true
                val previousFragmentFinal: BaseFragment = previousFragment
                onCloseAnimationEndRunnable = Runnable {
                    closeLastFragmentInternalRemoveOld(currentFragment)
                    containerViewBack!!.translationX = 0f
                    currentFragment.onTransitionAnimationEnd(false, false)
                    previousFragmentFinal.onTransitionAnimationEnd(true, true)
                    previousFragmentFinal.onBecomeFullyVisible()
                }
                val animation = currentFragment.onCustomTransitionAnimation(false, Runnable { onAnimationEndCheck(false) })
                if (animation == null) {
                    if (containerView!!.isKeyboardVisible || containerViewBack!!.isKeyboardVisible) {
                        waitingForKeyboardCloseRunnable = object : Runnable {
                            override fun run() {
                                if (waitingForKeyboardCloseRunnable !== this) {
                                    return
                                }
                                waitingForKeyboardCloseRunnable = null
                                startLayoutAnimation(false, true)
                            }
                        }
                        AndroidUtilities.runOnUIThread(waitingForKeyboardCloseRunnable, 200)
                    } else {
                        startLayoutAnimation(false, true)
                    }
                } else {
                    currentAnimation = animation
                }
            } else {
                currentFragment.onTransitionAnimationEnd(false, false)
                previousFragment.onTransitionAnimationEnd(true, true)
                previousFragment.onBecomeFullyVisible()
            }
        } else {
            if (useAlphaAnimations) {
                transitionAnimationStartTime = System.currentTimeMillis()
                transitionAnimationInProgress = true
                onCloseAnimationEndRunnable = Runnable {
                    removeFragmentFromStackInternal(currentFragment)
                    visibility = View.GONE
                    if (backgroundView != null) {
                        backgroundView!!.visibility = View.GONE
                    }
                    if (drawerLayoutContainer != null) {
                        drawerLayoutContainer!!.setAllowOpenDrawer(true, false)
                    }
                }
                val animators = ArrayList<Animator>()
                animators.add(ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f))
                if (backgroundView != null) {
                    animators.add(ObjectAnimator.ofFloat(backgroundView!!, "alpha", 1.0f, 0.0f))
                }
                currentAnimation = AnimatorSet()
                currentAnimation!!.playTogether(animators)
                currentAnimation!!.interpolator = accelerateDecelerateInterpolator
                currentAnimation!!.duration = 200
                currentAnimation!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        transitionAnimationStartTime = System.currentTimeMillis()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        onAnimationEndCheck(false)
                    }
                })
                currentAnimation!!.start()
            } else {
                removeFragmentFromStackInternal(currentFragment)
                visibility = View.GONE
                if (backgroundView != null) {
                    backgroundView!!.visibility = View.GONE
                }
            }
        }
    }

    fun showLastFragment() {
        if (fragmentsStack!!.isEmpty()) {
            return
        }
        for (a in 0 until fragmentsStack!!.size - 1) {
            val previousFragment = fragmentsStack!![a]
            if (previousFragment.actionBar != null && previousFragment.actionBar!!.addToContainer) {
                if (previousFragment.actionBar!!.parent != null) {
                    val parent = previousFragment.actionBar!!.parent as ViewGroup
                    parent.removeView(previousFragment.actionBar!!)
                }
            }
            if (previousFragment.fragmentView != null) {
                val parent = previousFragment.fragmentView!!.parent as ViewGroup
                if (parent != null) {
                    previousFragment.onPause()
                    previousFragment.onRemoveFromParent()
                    parent.removeView(previousFragment.fragmentView)
                }
            }
        }
        val previousFragment = fragmentsStack!![fragmentsStack!!.size - 1]
        previousFragment.setParentLayout(this)
        var fragmentView = previousFragment.fragmentView
        if (fragmentView == null) {
            fragmentView = previousFragment.createView(parentActivity)
        } else {
            val parent = fragmentView.parent as ViewGroup
            if (parent != null) {
                previousFragment.onRemoveFromParent()
                parent.removeView(fragmentView)
            }
        }
        if (previousFragment.actionBar != null && previousFragment.actionBar!!.addToContainer) {
            if (removeActionBarExtraHeight) {
                previousFragment.actionBar!!.setOccupyStatusBar(false)
            }
            if (previousFragment.actionBar!!.parent != null) {
                val parent = previousFragment.actionBar!!.parent as ViewGroup
                parent.removeView(previousFragment.actionBar)
            }

            containerView!!.addView(previousFragment.actionBar!!)
            previousFragment.actionBar!!.setTitleOverlayText(titleOverlayText, subtitleOverlayText, overlayAction)
        }
        containerView!!.addView(fragmentView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT))
        previousFragment.onResume()
        currentActionBar = previousFragment.actionBar!!
        if (!previousFragment.hasOwnBackground && fragmentView!!.background == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
        }
    }

    private fun removeFragmentFromStackInternal(fragment: BaseFragment) {
        fragment.onPause()
        fragment.onFragmentDestroy()
        fragment.setParentLayout(null)
        fragmentsStack!!.remove(fragment)
    }

    fun removeFragmentFromStack(fragment: BaseFragment) {
        if (useAlphaAnimations && fragmentsStack!!.size == 1 && isTablet!!) {
            closeLastFragment(true)
        } else {
            removeFragmentFromStackInternal(fragment)
        }
    }

    fun removeAllFragments() {
        var a = 0
        while (a < fragmentsStack!!.size) {
            removeFragmentFromStackInternal(fragmentsStack!![a])
            a--
            a++
        }
    }

    fun rebuildAllFragmentViews(last: Boolean, showLastAfter: Boolean) {
        if (transitionAnimationInProgress) {
            rebuildAfterAnimation = true
            rebuildLastAfterAnimation = last
            showLastAfterAnimation = showLastAfter
            return
        }
        for (a in 0 until fragmentsStack!!.size - if (last) 0 else 1) {
            fragmentsStack!![a].clearViews()
            fragmentsStack!![a].setParentLayout(this)
        }
        if (delegate != null) {
            delegate!!.onRebuildAllFragments(this)
        }
        if (showLastAfter) {
            showLastFragment()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU && !checkTransitionAnimation() && !startedTracking && currentActionBar != null) {
            currentActionBar!!.onMenuButtonPressed()
        }
        return super.onKeyUp(keyCode, event)
    }

    fun onActionModeStarted(mode: Any?) {
        if (currentActionBar != null) {
            currentActionBar!!.visibility = View.GONE
        }
        inActionMode = true
    }

    fun onActionModeFinished(mode: Any?) {
        if (currentActionBar != null) {
            currentActionBar!!.visibility = View.VISIBLE
        }
        inActionMode = false
    }

    private fun onCloseAnimationEnd() {
        if (transitionAnimationInProgress && onCloseAnimationEndRunnable != null) {
            transitionAnimationInProgress = false
            transitionAnimationStartTime = 0
            onCloseAnimationEndRunnable!!.run()
            onCloseAnimationEndRunnable = null
            checkNeedRebuild()
        }
    }

    private fun checkNeedRebuild() {
        if (rebuildAfterAnimation) {
            rebuildAllFragmentViews(rebuildLastAfterAnimation, showLastAfterAnimation)
            rebuildAfterAnimation = false
        }
    }

    private fun onOpenAnimationEnd() {
        if (transitionAnimationInProgress && onOpenAnimationEndRunnable != null) {
            transitionAnimationInProgress = false
            transitionAnimationStartTime = 0
            onOpenAnimationEndRunnable!!.run()
            onOpenAnimationEndRunnable = null
            checkNeedRebuild()
        }
    }

    fun startActivityForResult(intent: Intent?, requestCode: Int) {
        if (parentActivity == null) {
            return
        }
        if (transitionAnimationInProgress) {
            if (currentAnimation != null) {
                currentAnimation!!.cancel()
                currentAnimation = null
            }
            if (onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd()
            } else if (onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd()
            }
            containerView!!.invalidate()
            if (intent != null) {
                parentActivity!!.startActivityForResult(intent, requestCode)
            }
        } else {
            if (intent != null) {
                parentActivity!!.startActivityForResult(intent, requestCode)
            }
        }
    }

    fun setUseAlphaAnimations(value: Boolean) {
        useAlphaAnimations = value
    }

    fun setBackgroundView(view: View?) {
        backgroundView = view
    }

    fun setRemoveActionBarExtraHeight(value: Boolean) {
        removeActionBarExtraHeight = value
    }

    fun setTitleOverlayText(title: String?, subtitle: String?, action: Runnable?) {
        titleOverlayText = title
        subtitleOverlayText = subtitle
        overlayAction = action
        for (a in fragmentsStack!!.indices) {
            val fragment = fragmentsStack!![a]
            if (fragment.actionBar!! != null) {
                fragment.actionBar!!.setTitleOverlayText(titleOverlayText, subtitleOverlayText, action)
            }
        }
    }

    fun extendActionMode(menu: Menu?): Boolean {
        return !fragmentsStack!!.isEmpty() && fragmentsStack!![fragmentsStack!!.size - 1].extendActionMode(menu)
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    companion object {
        private var headerShadowDrawable: Drawable? = null
        private var layerShadowDrawable: Drawable? = null
        private var scrimPaint: Paint? = null
    }

    init {
        parentActivity = context as Activity?
        if (layerShadowDrawable == null) {
            layerShadowDrawable = resources.getDrawable(R.drawable.layer_shadow)
            headerShadowDrawable = resources.getDrawable(R.drawable.header_shadow).mutate()
            scrimPaint = Paint()
        }
    }
}