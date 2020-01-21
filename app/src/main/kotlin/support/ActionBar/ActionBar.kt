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
import android.content.Context
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import support.LayoutHelper
import support.Theme
import support.component.AndroidUtilities
import support.component.AndroidUtilities.Companion.dp
import support.component.AndroidUtilities.Companion.getTypeface
import support.component.AndroidUtilities.Companion.isTablet
import java.util.*

class ActionBar(context: Context?) : FrameLayout(context) {


    private var backButtonImageView: ImageView? = null
    var titleTextView: SimpleTextView? = null
    var subtitleTextView: SimpleTextView? = null
    private var actionModeTop: View? = null
    private var menu: ActionBarMenu? = null
    private var actionMode: ActionBarMenu? = null
    private var occupyStatusBar = Build.VERSION.SDK_INT >= 21
    private var actionModeVisible = false
    var addToContainer = true
    private var interceptTouches = true
    private var extraHeight = 0
    private var actionModeAnimation: AnimatorSet? = null
    private var titleRightMargin = 0
    private var allowOverlayTitle = false
    private var lastTitle: CharSequence? = null
    private var lastSubtitle: CharSequence? = null
    private var titleActionRunnable: Runnable? = null
    var castShadows = true
    var isSearchFieldVisible = false
    
    var itemsBackgroundColor = 0
    
    var itemsActionModeBackgroundColor = 0
    
    var itemsColor = 0
    
    var itemsActionModeColor = 0
    private val isBackOverlayVisible = false
    
    var parentFragment: BaseFragment? = null
    
    var actionBarMenuOnItemClick: ActionBarMenuOnItemClick? = null

    private fun createBackButtonImage() {
        if (backButtonImageView != null) {
            return
        }
        backButtonImageView = ImageView(context)
        backButtonImageView!!.scaleType = ImageView.ScaleType.CENTER
        backButtonImageView!!.setBackgroundDrawable(Theme.createSelectorDrawable(itemsBackgroundColor))
        if (itemsColor != 0) {
            backButtonImageView!!.colorFilter = PorterDuffColorFilter(itemsColor, PorterDuff.Mode.MULTIPLY)
        }
        backButtonImageView!!.setPadding(dp(1f), 0, 0, 0)
        addView(backButtonImageView, LayoutHelper.createFrame(54, 54, Gravity.LEFT or Gravity.TOP))
        backButtonImageView!!.setOnClickListener(OnClickListener {
            if (!actionModeVisible && isSearchFieldVisible) {
                closeSearchField()
                return@OnClickListener
            }
            if (actionBarMenuOnItemClick != null) {
                actionBarMenuOnItemClick!!.onItemClick(-1)
            }
        })
    }

    fun setBackButtonDrawable(drawable: Drawable?) {
        if (backButtonImageView == null) {
            createBackButtonImage()
        }
        backButtonImageView!!.visibility = if (drawable == null) View.GONE else View.VISIBLE
        backButtonImageView!!.setImageDrawable(drawable)
        if (drawable is BackDrawable) {
            val backDrawable = drawable
            backDrawable.setRotation(if (isActionModeShowed) 1f else 0.toFloat(), false)
            backDrawable.setRotatedColor(itemsActionModeColor)
            backDrawable.setColor(itemsColor)
        }
    }

    fun setBackButtonImage(resource: Int) {
        if (backButtonImageView == null) {
            createBackButtonImage()
        }
        backButtonImageView!!.visibility = if (resource == 0) View.GONE else View.VISIBLE
        backButtonImageView!!.setImageResource(resource)
    }

    private fun createSubtitleTextView() {
        if (subtitleTextView != null) {
            return
        }
        subtitleTextView = SimpleTextView(context)
        subtitleTextView!!.setGravity(Gravity.LEFT)
        subtitleTextView!!.visibility = View.GONE
        subtitleTextView!!.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle))
        addView(subtitleTextView, 0, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT or Gravity.TOP))
    }

    private fun createTitleTextView() {
        if (titleTextView != null) {
            return
        }
        titleTextView = SimpleTextView(context)
        titleTextView!!.setGravity(Gravity.LEFT)
        titleTextView!!.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle))
        titleTextView!!.setTypeface(getTypeface("fonts/rmedium.ttf"))
        addView(titleTextView, 0, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT or Gravity.TOP))
    }

    fun setTitleRightMargin(value: Int) {
        titleRightMargin = value
    }

    fun setTitleColor(color: Int) {
        if (titleTextView == null) {
            createTitleTextView()
        }
        titleTextView!!.setTextColor(color)
    }

    fun setSubtitleColor(color: Int) {
        if (subtitleTextView == null) {
            createSubtitleTextView()
        }
        subtitleTextView!!.setTextColor(color)
    }

    fun setPopupItemsColor(color: Int) {
        if (menu != null) {
            menu!!.setPopupItemsColor(color)
        }
    }

    fun setPopupBackgroundColor(color: Int) {
        if (menu != null) {
            menu!!.redrawPopup(color)
        }
    }

    var title: String?
        get() = if (titleTextView == null) {
            null
        } else titleTextView!!.texte.toString()
        set(value) {
            if (value != null && titleTextView == null) {
                createTitleTextView()
            }
            if (titleTextView != null) {
                lastTitle = value
                titleTextView!!.visibility = if (value != null && !isSearchFieldVisible) View.VISIBLE else View.INVISIBLE
                titleTextView!!.texte = value
            }
        }

    var subtitle: String?
        get() = if (subtitleTextView == null) {
            null
        } else subtitleTextView!!.texte.toString()
        set(value) {
            if (value != null && subtitleTextView == null) {
                createSubtitleTextView()
            }
            if (subtitleTextView != null) {
                lastSubtitle = value
                subtitleTextView!!.visibility = if (!TextUtils.isEmpty(value) && !isSearchFieldVisible) View.VISIBLE else View.GONE
                subtitleTextView!!.texte = value
            }
        }

    fun createMenu(): ActionBarMenu {
        if (menu != null) {
            return menu as ActionBarMenu
        }
        menu = ActionBarMenu(context, this)
        addView(menu, 0, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT))
        return menu!!
    }

    val backButton: View?
        get() = backButtonImageView

    fun createActionMode(): ActionBarMenu {
        if (actionMode != null) {
            return actionMode!!
        }
        actionMode = ActionBarMenu(context, this)
        actionMode!!.isActionMode = true
        actionMode!!.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefault))
        addView(actionMode, indexOfChild(backButtonImageView))
        actionMode!!.setPadding(0, if (occupyStatusBar) AndroidUtilities.statusBarHeight else 0, 0, 0)
        var layoutParams = actionMode!!.layoutParams as LayoutParams
        layoutParams.height = LayoutHelper.MATCH_PARENT
        layoutParams.width = LayoutHelper.MATCH_PARENT
        layoutParams.gravity = Gravity.RIGHT
        actionMode!!.layoutParams = layoutParams
        actionMode!!.visibility = View.INVISIBLE
        if (occupyStatusBar && actionModeTop == null) {
            actionModeTop = View(context)
            actionModeTop!!.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultTop))
            addView(actionModeTop)
            layoutParams = actionModeTop!!.layoutParams as LayoutParams
            layoutParams.height = AndroidUtilities.statusBarHeight
            layoutParams.width = LayoutHelper.MATCH_PARENT
            layoutParams.gravity = Gravity.TOP or Gravity.LEFT
            actionModeTop!!.layoutParams = layoutParams
            actionModeTop!!.visibility = View.INVISIBLE
        }
        return actionMode!!
    }

    fun showActionMode() {
        if (actionMode == null || actionModeVisible) {
            return
        }
        actionModeVisible = true
        val animators = ArrayList<Animator>()
        animators.add(ObjectAnimator.ofFloat(actionMode!!, "alpha", 0.0f, 1.0f))
        if (occupyStatusBar && actionModeTop != null) {
            animators.add(ObjectAnimator.ofFloat(actionModeTop!!, "alpha", 0.0f, 1.0f))
        }
        if (actionModeAnimation != null) {
            actionModeAnimation!!.cancel()
        }
        actionModeAnimation = AnimatorSet()
        actionModeAnimation!!.playTogether(animators)
        actionModeAnimation!!.duration = 200
        actionModeAnimation!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                actionMode!!.visibility = View.VISIBLE
                if (occupyStatusBar && actionModeTop != null) {
                    actionModeTop!!.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (actionModeAnimation != null && actionModeAnimation == animation) {
                    actionModeAnimation = null
                    if (titleTextView != null) {
                        titleTextView!!.visibility = View.INVISIBLE
                    }
                    if (subtitleTextView != null) {
                        subtitleTextView!!.visibility = View.INVISIBLE
                    }
                    if (menu != null) {
                        menu!!.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                if (actionModeAnimation != null && actionModeAnimation == animation) {
                    actionModeAnimation = null
                }
            }
        })
        actionModeAnimation!!.start()
        if (backButtonImageView != null) {
            val drawable = backButtonImageView!!.drawable
            if (drawable is BackDrawable) {
                drawable.setRotation(1f, true)
            }
            backButtonImageView!!.setBackgroundDrawable(Theme.createSelectorDrawable(itemsActionModeBackgroundColor))
        }
    }

    fun hideActionMode() {
        if (actionMode == null || !actionModeVisible) {
            return
        }
        actionModeVisible = false
        val animators = ArrayList<Animator>()
        animators.add(ObjectAnimator.ofFloat(actionMode!!, "alpha", 0.0f))
        if (occupyStatusBar && actionModeTop != null) {
            animators.add(ObjectAnimator.ofFloat(actionModeTop!!, "alpha", 0.0f))
        }
        if (actionModeAnimation != null) {
            actionModeAnimation!!.cancel()
        }
        actionModeAnimation = AnimatorSet()
        actionModeAnimation!!.playTogether(animators)
        actionModeAnimation!!.duration = 200
        actionModeAnimation!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (actionModeAnimation != null && actionModeAnimation == animation) {
                    actionModeAnimation = null
                    actionMode!!.visibility = View.INVISIBLE
                    if (occupyStatusBar && actionModeTop != null) {
                        actionModeTop!!.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                if (actionModeAnimation != null && actionModeAnimation == animation) {
                    actionModeAnimation = null
                }
            }
        })
        actionModeAnimation!!.start()
        if (titleTextView != null) {
            titleTextView!!.visibility = View.VISIBLE
        }
        if (subtitleTextView != null) {
            subtitleTextView!!.visibility = View.VISIBLE
        }
        if (menu != null) {
            menu!!.visibility = View.VISIBLE
        }
        if (backButtonImageView != null) {
            val drawable = backButtonImageView!!.drawable
            if (drawable is BackDrawable) {
                drawable.setRotation(0f, true)
            }
            backButtonImageView!!.setBackgroundDrawable(Theme.createSelectorDrawable(itemsBackgroundColor))
        }
    }

    fun showActionModeTop() {
        if (occupyStatusBar && actionModeTop == null) {
            actionModeTop = View(context)
            actionModeTop!!.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultTop))
            addView(actionModeTop)
            val layoutParams = actionModeTop!!.layoutParams as LayoutParams
            layoutParams.height = AndroidUtilities.statusBarHeight
            layoutParams.width = LayoutHelper.MATCH_PARENT
            layoutParams.gravity = Gravity.TOP or Gravity.LEFT
            actionModeTop!!.layoutParams = layoutParams
        }
    }

    fun setActionModeTopColor(color: Int) {
        if (actionModeTop != null) {
            actionModeTop!!.setBackgroundColor(color)
        }
    }

    fun setSearchTextColor(color: Int, placeholder: Boolean) {
        if (menu != null) {
            menu!!.setSearchTextColor(color, placeholder)
        }
    }

    fun setActionModeColor(color: Int) {
        if (actionMode != null) {
            actionMode!!.setBackgroundColor(color)
        }
    }

    val isActionModeShowed: Boolean
        get() = actionMode != null && actionModeVisible

    fun onSearchFieldVisibilityChanged(visible: Boolean) {
        isSearchFieldVisible = visible
        if (titleTextView != null) {
            titleTextView!!.visibility = if (visible) View.INVISIBLE else View.VISIBLE
        }
        if (subtitleTextView != null) {
            subtitleTextView!!.visibility = if (visible) View.INVISIBLE else View.VISIBLE
        }
        val drawable = backButtonImageView!!.drawable
        if (drawable != null && drawable is MenuDrawable) {
            drawable.setRotation(if (visible) 1f else 0.toFloat(), true)
        }
    }

    fun setInterceptTouches(value: Boolean) {
        interceptTouches = value
    }

    fun setExtraHeight(value: Int) {
        extraHeight = value
    }

    @JvmOverloads
    fun closeSearchField(closeKeyboard: Boolean = true) {
        if (!isSearchFieldVisible || menu == null) {
            return
        }
        menu!!.closeSearchField(closeKeyboard)
    }

    fun openSearchField(text: String?) {
        if (menu == null || text == null) {
            return
        }
        menu!!.openSearchField(!isSearchFieldVisible, text)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val actionBarHeight = currentActionBarHeight
        val actionBarHeightSpec = MeasureSpec.makeMeasureSpec(actionBarHeight, MeasureSpec.EXACTLY)
        setMeasuredDimension(width, actionBarHeight + (if (occupyStatusBar) AndroidUtilities.statusBarHeight else 0) + extraHeight)
        val textLeft: Int
        textLeft = if (backButtonImageView != null && backButtonImageView!!.visibility != View.GONE) {
            backButtonImageView!!.measure(MeasureSpec.makeMeasureSpec(dp(54f), MeasureSpec.EXACTLY), actionBarHeightSpec)
            dp(if (isTablet!!) 80f else 72.toFloat())
        } else {
            dp(if (isTablet!!) 26f else 18.toFloat())
        }
        if (menu != null && menu!!.visibility != View.GONE) {
            val menuWidth: Int
            menuWidth = if (isSearchFieldVisible) {
                MeasureSpec.makeMeasureSpec(width - dp(if (isTablet!!) 74f else 66.toFloat()), MeasureSpec.EXACTLY)
            } else {
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST)
            }
            menu!!.measure(menuWidth, actionBarHeightSpec)
        }
        if (titleTextView != null && titleTextView!!.visibility != View.GONE || subtitleTextView != null && subtitleTextView!!.visibility != View.GONE) {
            val availableWidth = width - (if (menu != null) menu!!.measuredWidth else 0) - dp(16f) - textLeft - titleRightMargin
            if (titleTextView != null && titleTextView!!.visibility != View.GONE) {
                titleTextView!!.setTextSize(if (!isTablet!! && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 18 else 20)
                titleTextView!!.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(dp(24f), MeasureSpec.AT_MOST))
            }
            if (subtitleTextView != null && subtitleTextView!!.visibility != View.GONE) {
                subtitleTextView!!.setTextSize(if (!isTablet!! && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 14 else 16)
                subtitleTextView!!.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(dp(20f), MeasureSpec.AT_MOST))
            }
        }
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE || child === titleTextView || child === subtitleTextView || child === menu || child === backButtonImageView) {
                continue
            }
            measureChildWithMargins(child, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY), 0)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val additionalTop = if (occupyStatusBar) AndroidUtilities.statusBarHeight else 0
        val textLeft: Int
        textLeft = if (backButtonImageView != null && backButtonImageView!!.visibility != View.GONE) {
            backButtonImageView!!.layout(0, additionalTop, backButtonImageView!!.measuredWidth, additionalTop + backButtonImageView!!.measuredHeight)
            dp(if (isTablet!!) 80f else 72.toFloat())
        } else {
            dp(if (isTablet!!) 26f else 18.toFloat())
        }
        if (menu != null && menu!!.visibility != View.GONE) {
            val menuLeft = if (isSearchFieldVisible) dp(if (isTablet!!) 74f else 66.toFloat()) else right - left - menu!!.measuredWidth
            menu!!.layout(menuLeft, additionalTop, menuLeft + menu!!.measuredWidth, additionalTop + menu!!.measuredHeight)
        }
        if (titleTextView != null && titleTextView!!.visibility != View.GONE) {
            val textTop: Int
            textTop = if (subtitleTextView != null && subtitleTextView!!.visibility != View.GONE) {
                (currentActionBarHeight / 2 - titleTextView!!.textHeight) / 2 + dp(if (!isTablet!! && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2f else 3.toFloat())
            } else {
                (currentActionBarHeight - titleTextView!!.textHeight) / 2
            }
            titleTextView!!.layout(textLeft, additionalTop + textTop, textLeft + titleTextView!!.measuredWidth, additionalTop + textTop + titleTextView!!.textHeight)
        }
        if (subtitleTextView != null && subtitleTextView!!.visibility != View.GONE) {
            val textTop = currentActionBarHeight / 2 + (currentActionBarHeight / 2 - subtitleTextView!!.textHeight) / 2 - dp(if (!isTablet!! && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 1f else 1.toFloat())
            subtitleTextView!!.layout(textLeft, additionalTop + textTop, textLeft + subtitleTextView!!.measuredWidth, additionalTop + textTop + subtitleTextView!!.textHeight)
        }
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE || child === titleTextView || child === subtitleTextView || child === menu || child === backButtonImageView) {
                continue
            }
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
            child.layout(childLeft, childTop, childLeft + width, childTop + height)
        }
    }

    fun onMenuButtonPressed() {
        if (menu != null) {
            menu!!.onMenuButtonPressed()
        }
    }

    fun onPause() {
        if (menu != null) {
            menu!!.hideAllPopupMenus()
        }
    }

    fun setAllowOverlayTitle(value: Boolean) {
        allowOverlayTitle = value
    }

    fun setTitleOverlayText(title: String?, subtitle: String?, action: Runnable?) {
        if (!allowOverlayTitle || parentFragment!!.parentLayoute == null) {
            return
        }
        var textToSet = title ?: lastTitle
        if (textToSet != null && titleTextView == null) {
            createTitleTextView()
        }
        if (titleTextView != null) {
            titleTextView!!.visibility = if (textToSet != null && !isSearchFieldVisible) View.VISIBLE else View.INVISIBLE
            titleTextView!!.texte = textToSet
        }
        textToSet = subtitle ?: lastSubtitle
        if (textToSet != null && subtitleTextView == null) {
            createSubtitleTextView()
        }
        if (subtitleTextView != null) {
            subtitleTextView!!.visibility = if (!TextUtils.isEmpty(textToSet) && !isSearchFieldVisible) View.VISIBLE else View.GONE
            subtitleTextView!!.texte = textToSet
        }
        titleActionRunnable = action
    }

    fun setOccupyStatusBar(value: Boolean) {
        occupyStatusBar = value
        if (actionMode != null) {
            actionMode!!.setPadding(0, if (occupyStatusBar) AndroidUtilities.statusBarHeight else 0, 0, 0)
        }
    }

    fun getOccupyStatusBar(): Boolean {
        return occupyStatusBar
    }

    fun setItemsBackgroundColor(color: Int, isActionMode: Boolean) {
        if (isActionMode) {
            itemsActionModeBackgroundColor = color
            if (actionModeVisible) {
                if (backButtonImageView != null) {
                    backButtonImageView!!.setBackgroundDrawable(Theme.createSelectorDrawable(itemsActionModeBackgroundColor))
                }
            }
            if (actionMode != null) {
                actionMode!!.updateItemsBackgroundColor()
            }
        } else {
            itemsBackgroundColor = color
            if (backButtonImageView != null) {
                backButtonImageView!!.setBackgroundDrawable(Theme.createSelectorDrawable(itemsBackgroundColor))
            }
            if (menu != null) {
                menu!!.updateItemsBackgroundColor()
            }
        }
    }

    fun setItemsColor(color: Int, isActionMode: Boolean) {
        if (isActionMode) {
            itemsActionModeColor = color
            if (actionMode != null) {
                actionMode!!.updateItemsColor()
            }
            if (backButtonImageView != null) {
                val drawable = backButtonImageView!!.drawable
                if (drawable is BackDrawable) {
                    drawable.setRotatedColor(color)
                }
            }
        } else {
            itemsColor = color
            if (backButtonImageView != null) {
                if (itemsColor != 0) {
                    backButtonImageView!!.colorFilter = PorterDuffColorFilter(itemsColor, PorterDuff.Mode.MULTIPLY)
                    val drawable = backButtonImageView!!.drawable
                    if (drawable is BackDrawable) {
                        drawable.setColor(color)
                    }
                }
            }
            if (menu != null) {
                menu!!.updateItemsColor()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event) || interceptTouches
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

   /* fun setActionBarMenuOnItemClick(actionBarMenuOnItemClick: ActionBar.Companion.ActionBarMenuOnItemClick) {
        this.actionBarMenuOnItemClick = actionBarMenuOnItemClick
    }*/

    companion object {
        public open class ActionBarMenuOnItemClick {
            open fun onItemClick(id: Int) {}
            fun canOpenMenu(): Boolean {
                return true
            }
        }

        val currentActionBarHeight: Int
            get() = if (isTablet!!) {
                dp(64f)
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                dp(48f)
            } else {
                dp(56f)
            }
    }

    init {
        setOnClickListener {
            if (titleActionRunnable != null) {
                titleActionRunnable!!.run()
            }
        }
    }
}