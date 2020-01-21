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
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.view.View.MeasureSpec
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import com.yaya.map.R
import support.ActionBar.BottomSheet
import support.LayoutHelper
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import java.util.*

class BottomSheet(context: Context, needFocus: Boolean) : Dialog(context, R.style.TransparentDialog) {
    var sheetContainer: ViewGroup? = null
        protected set
    protected var container: ContainerView
    private var lastInsets: WindowInsets? = null
    private var startAnimationRunnable: Runnable? = null
    private var layoutCount = 0
    var isDismissed = false
        private set
    var tag = 0
        private set
    private var allowDrawContent = true
    private var useHardwareLayer = true
    private var onClickListener: DialogInterface.OnClickListener? = null
    private var items: Array<CharSequence?>? = null
    private var itemIcons: IntArray? = null
    private var customView: View? = null
    private var title: CharSequence? = null
    protected var fullWidth = false
    protected var backDrawable = ColorDrawable(-0x1000000)
    private var allowCustomAnimation = true
    private var showWithoutAnimation = false
    private val touchSlop: Int
    private var useFastDismiss = false
    private val focusable: Boolean
    private var allowNestedScroll = true
    private val shadowDrawable: Drawable
    private var applyTopPadding = true
    private var applyBottomPadding = true
    private val decelerateInterpolator = DecelerateInterpolator()
    private val accelerateInterpolator = AccelerateInterpolator()
    private val itemViews = ArrayList<BottomSheetCell>()
    private var delegate: BottomSheetDelegateInterface? = null
    protected var currentSheetAnimation: AnimatorSet? = null

    protected inner open class ContainerView(context: Context?) : FrameLayout(context), NestedScrollingParent {
        private var velocityTracker: VelocityTracker? = null
        private var startedTrackingX = 0
        private var startedTrackingY = 0
        private var startedTrackingPointerId = -1
        private var maybeStartTracking = false
        private var startedTracking = false
        private var currentAnimation: AnimatorSet? = null
        private val nestedScrollingParentHelper: NestedScrollingParentHelper
        override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
            return !isDismissed && allowNestedScroll && nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && !canDismissWithSwipe()
        }

        override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
            nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes)
            if (isDismissed || !allowNestedScroll) {
                return
            }
            cancelCurrentAnimation()
        }

        override fun onStopNestedScroll(target: View) {
            nestedScrollingParentHelper.onStopNestedScroll(target)
            if (isDismissed || !allowNestedScroll) {
                return
            }
            val currentTranslation = sheetContainer!!.translationY
            checkDismiss(0f, 0f)
        }

        override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
            if (isDismissed || !allowNestedScroll) {
                return
            }
            cancelCurrentAnimation()
            if (dyUnconsumed != 0) {
                var currentTranslation = sheetContainer!!.translationY
                currentTranslation -= dyUnconsumed.toFloat()
                if (currentTranslation < 0) {
                    currentTranslation = 0f
                }
                sheetContainer!!.translationY = currentTranslation
            }
        }

        override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
            if (isDismissed || !allowNestedScroll) {
                return
            }
            cancelCurrentAnimation()
            var currentTranslation = sheetContainer!!.translationY
            if (currentTranslation > 0 && dy > 0) {
                currentTranslation -= dy.toFloat()
                consumed[1] = dy
                if (currentTranslation < 0) {
                    currentTranslation = 0f
                    consumed[1] += currentTranslation.toInt()
                }
                sheetContainer!!.translationY = currentTranslation
            }
        }

        override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
            return false
        }

        override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
            return false
        }

        override fun getNestedScrollAxes(): Int {
            return nestedScrollingParentHelper.nestedScrollAxes
        }

        private fun checkDismiss(velX: Float, velY: Float) {
            val translationY = sheetContainer!!.translationY
            val backAnimation = translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500 || Math.abs(velY) < Math.abs(velX)) || velY < 0 && Math.abs(velY) >= 3500
            if (!backAnimation) {
                val allowOld = allowCustomAnimation
                allowCustomAnimation = false
                useFastDismiss = true
                dismiss()
                allowCustomAnimation = allowOld
            } else {
                currentAnimation = AnimatorSet()
                currentAnimation!!.playTogether(ObjectAnimator.ofFloat(sheetContainer, "translationY", 0f))
                currentAnimation!!.setDuration((150 * (translationY / AndroidUtilities.getPixelsInCM(0.8f, false))) as Long)
                currentAnimation!!.interpolator = DecelerateInterpolator()
                currentAnimation!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (currentAnimation != null && currentAnimation == animation) {
                            currentAnimation = null
                        }
                    }
                })
                currentAnimation!!.start()
            }
        }

        private fun cancelCurrentAnimation() {
            if (currentAnimation != null) {
                currentAnimation!!.cancel()
                currentAnimation = null
            }
        }

        override fun onTouchEvent(ev: MotionEvent): Boolean {
            if (isDismissed) {
                return false
            }
            if (onContainerTouchEvent(ev)) {
                return true
            }
            if (canDismissWithTouchOutside() && ev != null && (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE) && (!startedTracking && !maybeStartTracking || ev.pointerCount == 1)) {
                startedTrackingX = ev.x.toInt()
                startedTrackingY = ev.y.toInt()
                if (startedTrackingY < sheetContainer!!.top || startedTrackingX < sheetContainer!!.left || startedTrackingX > sheetContainer!!.right) {
                    dismiss()
                    return true
                }
                startedTrackingPointerId = ev.getPointerId(0)
                maybeStartTracking = true
                cancelCurrentAnimation()
                if (velocityTracker != null) {
                    velocityTracker!!.clear()
                }
            } else if (ev != null && ev.action == MotionEvent.ACTION_MOVE && ev.getPointerId(0) == startedTrackingPointerId) {
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                }
                val dx = Math.abs((ev.x - startedTrackingX).toInt()).toFloat()
                val dy = ev.y.toInt() - startedTrackingY.toFloat()
                velocityTracker!!.addMovement(ev)
                if (maybeStartTracking && !startedTracking && dy > 0 && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= touchSlop) {
                    startedTrackingY = ev.y.toInt()
                    maybeStartTracking = false
                    startedTracking = true
                    requestDisallowInterceptTouchEvent(true)
                } else if (startedTracking) {
                    var translationY = sheetContainer!!.translationY
                    translationY += dy
                    if (translationY < 0) {
                        translationY = 0f
                    }
                    sheetContainer!!.translationY = translationY
                    startedTrackingY = ev.y.toInt()
                }
            } else if (ev == null || ev != null && ev.getPointerId(0) == startedTrackingPointerId && (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_POINTER_UP)) {
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                }
                velocityTracker!!.computeCurrentVelocity(1000)
                val translationY = sheetContainer!!.translationY
                if (startedTracking || translationY != 0f) {
                    checkDismiss(velocityTracker!!.xVelocity, velocityTracker!!.yVelocity)
                    startedTracking = false
                } else {
                    maybeStartTracking = false
                    startedTracking = false
                }
                if (velocityTracker != null) {
                    velocityTracker!!.recycle()
                    velocityTracker = null
                }
                startedTrackingPointerId = -1
            }
            return startedTracking || !canDismissWithSwipe()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var width = MeasureSpec.getSize(widthMeasureSpec)
            var height = MeasureSpec.getSize(heightMeasureSpec)
            if (lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                height -= lastInsets!!.getSystemWindowInsetBottom()
            }
            setMeasuredDimension(width, height)
            if (lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                width -= lastInsets!!.getSystemWindowInsetRight() + lastInsets!!.getSystemWindowInsetLeft()
            }
            val isPortrait = width < height
            if (sheetContainer != null) {
                if (!fullWidth) {
                    val widthSpec: Int
                    widthSpec = if (AndroidUtilities.isTablet()) {
                        MeasureSpec.makeMeasureSpec((Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f) as Int + backgroundPaddingLeft!! * 2, MeasureSpec.EXACTLY)
                    } else {
                        MeasureSpec.makeMeasureSpec(if (isPortrait) width + backgroundPaddingLeft!! * 2 else Math.max((width * 0.8f) as Int, Math.min(AndroidUtilities.dp(480f), width)) as Int + backgroundPaddingLeft!! * 2, MeasureSpec.EXACTLY)
                    }
                    sheetContainer!!.measure(widthSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST))
                } else {
                    sheetContainer!!.measure(MeasureSpec.makeMeasureSpec(width + backgroundPaddingLeft!! * 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST))
                }
            }
            val childCount = childCount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.visibility == View.GONE || child === sheetContainer) {
                    continue
                }
                if (!onCustomMeasure(child, width, height)) {
                    measureChildWithMargins(child, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 0, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), 0)
                }
            }
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            var left = left
            var right = right
            layoutCount--
            if (sheetContainer != null) {
                if (lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                    left += lastInsets!!.getSystemWindowInsetLeft()
                    right -= lastInsets!!.getSystemWindowInsetRight()
                }
                val t = bottom - top - sheetContainer!!.measuredHeight
                var l = (right - left - sheetContainer!!.measuredWidth) / 2
                if (lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                    l += lastInsets!!.getSystemWindowInsetLeft()
                }
                sheetContainer!!.layout(l, t, l + sheetContainer!!.measuredWidth, t + sheetContainer!!.measuredHeight)
            }
            val count = childCount
            for (i in 0 until count) {
                val child = getChildAt(i)
                if (child.visibility == View.GONE || child === sheetContainer) {
                    continue
                }
                if (!onCustomLayout(child, left, top, right, bottom)) {
                    val lp = child.layoutParams as LayoutParams
                    val width = child.measuredWidth
                    val height = child.measuredHeight
                    var childLeft: Int
                    var childTop: Int
                    var gravity = lp.gravity
                    if (gravity == -1) {
                        gravity = Gravity.TOP or Gravity.LEFT
                    }
                    val absoluteGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
                    val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
                    childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                        Gravity.CENTER_HORIZONTAL -> (right - left - width) / 2 + lp.leftMargin - lp.rightMargin
                        Gravity.RIGHT -> right - width - lp.rightMargin
                        Gravity.LEFT -> lp.leftMargin
                        else -> lp.leftMargin
                    }
                    childTop = when (verticalGravity) {
                        Gravity.TOP -> lp.topMargin
                        Gravity.CENTER_VERTICAL -> (bottom - top - height) / 2 + lp.topMargin - lp.bottomMargin
                        Gravity.BOTTOM -> bottom - top - height - lp.bottomMargin
                        else -> lp.topMargin
                    }
                    if (lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                        childLeft += lastInsets!!.getSystemWindowInsetLeft()
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height)
                }
            }
            if (layoutCount == 0 && startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(startAnimationRunnable)
                startAnimationRunnable!!.run()
                startAnimationRunnable = null
            }
        }

        override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
            return if (canDismissWithSwipe()) {
                onTouchEvent(event)
            } else super.onInterceptTouchEvent(event)
        }

        override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            if (maybeStartTracking && !startedTracking) {
                onTouchEvent(null)
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
        }

        override fun hasOverlappingRendering(): Boolean {
            return false
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            onContainerDraw(canvas)
        }

        init {
            nestedScrollingParentHelper = NestedScrollingParentHelper(this)
        }
    }

    interface BottomSheetDelegateInterface {
        fun onOpenAnimationStart()
        fun onOpenAnimationEnd()
        fun canDismiss(): Boolean
    }

    class BottomSheetDelegate : BottomSheetDelegateInterface {
        override fun onOpenAnimationStart() {}
        override fun onOpenAnimationEnd() {}
        override fun canDismiss(): Boolean {
            return true
        }
    }

    class BottomSheetCell(context: Context?, type: Int) : FrameLayout(context) {
        val textView: TextView
        private val imageView: ImageView
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48f), MeasureSpec.EXACTLY))
        }

        fun setTextColor(color: Int) {
            textView.setTextColor(color)
        }

        fun setGravity(gravity: Int) {
            textView.gravity = gravity
        }

        fun setTextAndIcon(text: CharSequence?, icon: Int) {
            textView.text = text
            if (icon != 0) {
                imageView.setImageResource(icon)
                imageView.visibility = View.VISIBLE
                textView.setPadding(if (LocaleController.isRTL) 0 else AndroidUtilities.dp(56f), 0, if (LocaleController.isRTL) AndroidUtilities.dp(56f) else 0, 0)
            } else {
                imageView.visibility = View.INVISIBLE
                textView.setPadding(0, 0, 0, 0)
            }
        }

        init {
            setBackgroundDrawable(Theme.getSelectorDrawable(false))
            setPadding(AndroidUtilities.dp(16f), 0, AndroidUtilities.dp(16f), 0)
            imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER
            imageView.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY)
            addView(imageView, LayoutHelper.createFrame(24, 24, Gravity.CENTER_VERTICAL or if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT))
            textView = TextView(context)
            textView.setLines(1)
            textView.setSingleLine(true)
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView.ellipsize = TextUtils.TruncateAt.END
            if (type == 0) {
                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack))
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT) or Gravity.CENTER_VERTICAL))
            } else if (type == 1) {
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack))
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                textView.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")
                addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat()))
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun setAllowNestedScroll(value: Boolean) {
        allowNestedScroll = value
        if (!allowNestedScroll) {
            sheetContainer!!.translationY = 0f
        }
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val window = window
        window.setWindowAnimations(R.style.DialogNoAnimation)
        setContentView(container, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        if (sheetContainer == null) {
            sheetContainer = object : FrameLayout(context) {
                override fun hasOverlappingRendering(): Boolean {
                    return false
                }

                override fun setTranslationY(translationY: Float) {
                    super.setTranslationY(translationY)
                    onContainerTranslationYChanged(translationY)
                }
            }
            sheetContainer!!.setBackgroundDrawable(shadowDrawable)
            sheetContainer!!.setPadding(backgroundPaddingLeft!!, (if (applyTopPadding) AndroidUtilities.dp(8f) else 0) + backgroundPaddingTop!! - 1, backgroundPaddingLeft!!, if (applyBottomPadding) AndroidUtilities.dp(8f) else 0)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            sheetContainer!!.fitsSystemWindows = true
        }
        sheetContainer!!.visibility = View.INVISIBLE
        container.addView(sheetContainer, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM))
        var topOffset = 0
        if (title != null) {
            val titleView = TextView(context)
            titleView.setLines(1)
            titleView.setSingleLine(true)
            titleView.text = title
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2))
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            titleView.ellipsize = TextUtils.TruncateAt.MIDDLE
            titleView.setPadding(AndroidUtilities.dp(16f), 0, AndroidUtilities.dp(16f), AndroidUtilities.dp(8f))
            titleView.gravity = Gravity.CENTER_VERTICAL
            sheetContainer!!.addView(titleView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48f))
            titleView.setOnTouchListener { v, event -> true }
            topOffset += 48
        }
        if (customView != null) {
            if (customView!!.parent != null) {
                val viewGroup = customView!!.parent as ViewGroup
                viewGroup.removeView(customView)
            }
            sheetContainer!!.addView(customView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.LEFT or Gravity.TOP, 0f, topOffset.toFloat(), 0f, 0f))
        } else {
            if (items != null) {
                val rowLayout: FrameLayout? = null
                val lastRowLayoutNum = 0
                for (a in items!!.indices) {
                    if (items!![a] == null) {
                        continue
                    }
                    val cell = BottomSheetCell(context, 0)
                    cell.setTextAndIcon(items!![a], if (itemIcons != null) itemIcons!![a] else 0)
                    sheetContainer!!.addView(cell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48f, Gravity.LEFT or Gravity.TOP, 0f, topOffset.toFloat(), 0f, 0f))
                    topOffset += 48
                    cell.tag = a
                    cell.setOnClickListener { v -> dismissWithButtonClick(v.tag as Int) }
                    itemViews.add(cell)
                }
            }
        }
        val params = window.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.dimAmount = 0f
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        if (!focusable) {
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        }
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = params
    }

    fun setShowWithoutAnimation(value: Boolean) {
        showWithoutAnimation = value
    }

    fun setBackgroundColor(color: Int) {
        shadowDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    override fun show() {
        super.show()
        if (focusable) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        isDismissed = false
        cancelSheetAnimation()
        sheetContainer!!.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + backgroundPaddingLeft!! * 2, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, MeasureSpec.AT_MOST))
        if (showWithoutAnimation) {
            backDrawable.alpha = 51
            sheetContainer!!.translationY = 0f
            return
        }
        backDrawable.alpha = 0
        if (Build.VERSION.SDK_INT >= 18) {
            layoutCount = 2
            sheetContainer!!.translationY = sheetContainer!!.measuredHeight.toFloat()
            AndroidUtilities.runOnUIThread(object : Runnable {
                override fun run() {
                    if (startAnimationRunnable !== this || isDismissed) {
                        return
                    }
                    startAnimationRunnable = null
                    startOpenAnimation()
                }
            }.also { startAnimationRunnable = it }, 150)
        } else {
            startOpenAnimation()
        }
    }

    fun setAllowDrawContent(value: Boolean) {
        if (allowDrawContent != value) {
            allowDrawContent = value
            container.setBackgroundDrawable(if (allowDrawContent) backDrawable else null)
            container.invalidate()
        }
    }

    protected fun canDismissWithSwipe(): Boolean {
        return true
    }

    protected fun onContainerTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    fun setCustomView(view: View?) {
        customView = view
    }

    override fun setTitle(value: CharSequence) {
        title = value
    }

    fun setApplyTopPadding(value: Boolean) {
        applyTopPadding = value
    }

    fun setApplyBottomPadding(value: Boolean) {
        applyBottomPadding = value
    }

    protected fun onCustomMeasure(view: View?, width: Int, height: Int): Boolean {
        return false
    }

    protected fun onCustomLayout(view: View?, left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return false
    }

    protected fun canDismissWithTouchOutside(): Boolean {
        return true
    }

    protected fun onContainerTranslationYChanged(translationY: Float) {}
    private fun cancelSheetAnimation() {
        if (currentSheetAnimation != null) {
            currentSheetAnimation!!.cancel()
            currentSheetAnimation = null
        }
    }

    private fun startOpenAnimation() {
        if (isDismissed) {
            return
        }
        sheetContainer!!.visibility = View.VISIBLE
        if (!onCustomOpenAnimation()) {
            if (Build.VERSION.SDK_INT >= 20 && useHardwareLayer) {
                container.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
            sheetContainer!!.translationY = sheetContainer!!.measuredHeight.toFloat()
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(sheetContainer, "translationY", 0f),
                    ObjectAnimator.ofInt(backDrawable, "alpha", 51))
            animatorSet.duration = 200
            animatorSet.startDelay = 20
            animatorSet.interpolator = DecelerateInterpolator()
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (currentSheetAnimation != null && currentSheetAnimation == animation) {
                        currentSheetAnimation = null
                        if (delegate != null) {
                            delegate!!.onOpenAnimationEnd()
                        }
                        if (useHardwareLayer) {
                            container.setLayerType(View.LAYER_TYPE_NONE, null)
                        }
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                    if (currentSheetAnimation != null && currentSheetAnimation == animation) {
                        currentSheetAnimation = null
                    }
                }
            })
            animatorSet.start()
            currentSheetAnimation = animatorSet
        }
    }

    fun setDelegate(bottomSheetDelegate: BottomSheetDelegateInterface?) {
        delegate = bottomSheetDelegate
    }

    fun getContainer(): FrameLayout {
        return container
    }

    fun setItemText(item: Int, text: CharSequence?) {
        if (item < 0 || item >= itemViews.size) {
            return
        }
        val cell = itemViews[item]
        cell.textView.text = text
    }

    fun dismissWithButtonClick(item: Int) {
        if (isDismissed) {
            return
        }
        isDismissed = true
        cancelSheetAnimation()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(sheetContainer, "translationY", sheetContainer!!.measuredHeight + AndroidUtilities.dp(10f).toFloat()),
                ObjectAnimator.ofInt(backDrawable, "alpha", 0)
        )
        animatorSet.duration = 180
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (currentSheetAnimation != null && currentSheetAnimation == animation) {
                    currentSheetAnimation = null
                    if (onClickListener != null) {
                        onClickListener!!.onClick(this@BottomSheet, item)
                    }
                    AndroidUtilities.runOnUIThread(Runnable {
                        try {
                            super@BottomSheet.dismiss()
                        } catch (e: Exception) {
                        }
                    })
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                if (currentSheetAnimation != null && currentSheetAnimation == animation) {
                    currentSheetAnimation = null
                }
            }
        })
        animatorSet.start()
        currentSheetAnimation = animatorSet
    }

    override fun dismiss() {
        if (delegate != null && !delegate!!.canDismiss()) {
            return
        }
        if (isDismissed) {
            return
        }
        isDismissed = true
        cancelSheetAnimation()
        if (!allowCustomAnimation || !onCustomCloseAnimation()) {
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(sheetContainer, "translationY", sheetContainer!!.measuredHeight + AndroidUtilities.dp(10f).toFloat()),
                    ObjectAnimator.ofInt(backDrawable, "alpha", 0)
            )
            if (useFastDismiss) {
                val height = sheetContainer!!.measuredHeight
                animatorSet.duration = Math.max(60, (180 * (height - sheetContainer!!.translationY) / height.toFloat()).toInt()).toLong()
                useFastDismiss = false
            } else {
                animatorSet.duration = 180
            }
            animatorSet.interpolator = AccelerateInterpolator()
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (currentSheetAnimation != null && currentSheetAnimation == animation) {
                        currentSheetAnimation = null
                        AndroidUtilities.runOnUIThread(Runnable {
                            try {
                                dismissInternal()
                            } catch (e: Exception) {
                            }
                        })
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                    if (currentSheetAnimation != null && currentSheetAnimation == animation) {
                        currentSheetAnimation = null
                    }
                }
            })
            animatorSet.start()
            currentSheetAnimation = animatorSet
        }
    }

    fun dismissInternal() {
        try {
            super.dismiss()
        } catch (e: Exception) {
        }
    }

    protected fun onCustomCloseAnimation(): Boolean {
        return false
    }

    protected fun onCustomOpenAnimation(): Boolean {
        return false
    }

    class Builder {
        private var bottomSheet: BottomSheet

        constructor(context: Context) {
            bottomSheet = BottomSheet(context, false)
        }

        constructor(context: Context, needFocus: Boolean) {
            bottomSheet = BottomSheet(context, needFocus)
        }

        fun setItems(items: Array<CharSequence?>?, onClickListener: DialogInterface.OnClickListener?): Builder {
            bottomSheet.items = items
            bottomSheet.onClickListener = onClickListener
            return this
        }

        fun setItems(items: Array<CharSequence?>?, icons: IntArray?, onClickListener: DialogInterface.OnClickListener?): Builder {
            bottomSheet.items = items
            bottomSheet.itemIcons = icons
            bottomSheet.onClickListener = onClickListener
            return this
        }

        fun setCustomView(view: View?): Builder {
            bottomSheet.customView = view
            return this
        }

        fun setTitle(title: CharSequence?): Builder {
            bottomSheet.title = title
            return this
        }

        fun create(): BottomSheet {
            return bottomSheet
        }

        fun show(): BottomSheet {
            bottomSheet.show()
            return bottomSheet
        }

        fun setTag(tag: Int): Builder {
            bottomSheet.tag = tag
            return this
        }

        fun setUseHardwareLayer(value: Boolean): Builder {
            bottomSheet.useHardwareLayer = value
            return this
        }

        fun setDelegate(delegate: BottomSheetDelegate?): Builder {
            bottomSheet.setDelegate(delegate)
            return this
        }

        fun setApplyTopPadding(value: Boolean): Builder {
            bottomSheet.applyTopPadding = value
            return this
        }

        fun setApplyBottomPadding(value: Boolean): Builder {
            bottomSheet.applyBottomPadding = value
            return this
        }

        fun setUseFullWidth(value: Boolean): BottomSheet {
            bottomSheet.fullWidth = value
            return bottomSheet
        }
    }

    protected val leftInset: Int
        protected get() = if (lastInsets != null && Build.VERSION.SDK_INT >= 21) {
            lastInsets!!.getSystemWindowInsetLeft()
        } else 0

    fun onConfigurationChanged(newConfig: Configuration?) {}
    fun onContainerDraw(canvas: Canvas?) {}

    companion object {
        protected var backgroundPaddingTop: Int? =null
        protected var backgroundPaddingLeft: Int? =null
    }

    init {
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        val vc = ViewConfiguration.get(context)
        touchSlop = vc.scaledTouchSlop
        val padding = Rect()
        shadowDrawable = context.resources.getDrawable(R.drawable.sheet_shadow).mutate()
        shadowDrawable.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY)
        shadowDrawable.getPadding(padding)
        backgroundPaddingLeft = padding.left
        backgroundPaddingTop = padding.top
        container = object : ContainerView(getContext()) {
            public override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
                try {
                    return allowDrawContent && super.drawChild(canvas, child, drawingTime)
                } catch (e: Exception) {
                }
                return true
            }
        }
        container.setBackgroundDrawable(backDrawable)
        focusable = needFocus
        if (Build.VERSION.SDK_INT >= 21) {
            container.fitsSystemWindows = true
            container.setOnApplyWindowInsetsListener { v, insets ->
                lastInsets = insets
                v.requestLayout()
                insets.consumeSystemWindowInsets()
            }
            container.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        backDrawable.alpha = 0
    }
}