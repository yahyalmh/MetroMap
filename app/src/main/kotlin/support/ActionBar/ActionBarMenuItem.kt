/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.ActionBar

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.View.OnClickListener
import android.view.View.OnKeyListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import support.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout
import support.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
import support.LayoutHelper
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import support.component.AndroidUtilities.Companion.dp
import support.component.AndroidUtilities.Companion.hideKeyboard
import support.component.AndroidUtilities.Companion.showKeyboard
import support.component.CloseProgressDrawable2
import support.component.EditTextBoldCursor
import java.lang.reflect.Method

class ActionBarMenuItem(context: Context?, menu: ActionBarMenu?, backgroundColor: Int, iconColor: Int) : FrameLayout(context) {
    class ActionBarMenuItemSearchListener {
        fun onSearchExpand() {}
        fun canCollapseSearch(): Boolean {
            return true
        }

        fun onSearchCollapse() {}
        fun onTextChanged(editText: EditText?) {}
        fun onSearchPressed(editText: EditText?) {}
        fun onCaptionCleared() {}
    }

    interface ActionBarMenuItemDelegate {
        fun onItemClick(id: Int)
    }

    private var popupLayout: ActionBarPopupWindowLayout? = null
    private val parentMenu: ActionBarMenu?
    private var popupWindow: ActionBarPopupWindow? = null
    var searchField: EditTextBoldCursor? = null
        private set
    private var searchFieldCaption: TextView? = null
    private var clearButton: ImageView? = null
    lateinit var imageView: ImageView
    private var searchContainer: FrameLayout? = null
    private var isSearchField = false
    private var listener: ActionBarMenuItemSearchListener? = null
    private var rect: Rect? = null
    private var location: IntArray? = null
    private var selectedMenuView: View? = null
    private var showMenuRunnable: Runnable? = null
    private var menuHeight = dp(16f)
    private var subMenuOpenSide = 0
    private var delegate: ActionBarMenuItemDelegate? = null
    private var allowCloseAnimation = true
    var overrideMenuClick = false
    private var processedPopupClick = false
    private var layoutInScreen = false
    private var animationEnabled = true
    private var ignoreOnTextChange = false
    private var progressDrawable: CloseProgressDrawable2? = null
    private var additionalOffset = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            if (hasSubMenu() && (popupWindow == null || popupWindow != null && !popupWindow!!.isShowing)) {
                showMenuRunnable = Runnable {
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    toggleSubMenu()
                }
                AndroidUtilities.runOnUIThread(showMenuRunnable, 200)
            }
        } else if (event.actionMasked == MotionEvent.ACTION_MOVE) {
            if (hasSubMenu() && (popupWindow == null || popupWindow != null && !popupWindow!!.isShowing)) {
                if (event.y > height) {
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    toggleSubMenu()
                    return true
                }
            } else if (popupWindow != null && popupWindow!!.isShowing) {
                getLocationOnScreen(location)
                var x = event.x + location!![0]
                var y = event.y + location!![1]
                popupLayout!!.getLocationOnScreen(location)
                x -= location!![0]
                y -= location!![1]
                selectedMenuView = null
                for (a in 0 until popupLayout!!.itemsCount) {
                    val child = popupLayout!!.getItemAt(a)
                    child.getHitRect(rect)
                    if ((child.tag as Int) < 100) {
                        if (!rect!!.contains(x.toInt(), y.toInt())) {
                            child.isPressed = false
                            child.isSelected = false
                            if (Build.VERSION.SDK_INT == 21) {
                                child.background.setVisible(false, false)
                            }
                        } else {
                            child.isPressed = true
                            child.isSelected = true
                            if (Build.VERSION.SDK_INT >= 21) {
                                if (Build.VERSION.SDK_INT == 21) {
                                    child.background.setVisible(true, false)
                                }
                                child.drawableHotspotChanged(x, y - child.top)
                            }
                            selectedMenuView = child
                        }
                    }
                }
            }
        } else if (popupWindow != null && popupWindow!!.isShowing && event.actionMasked == MotionEvent.ACTION_UP) {
            if (selectedMenuView != null) {
                selectedMenuView!!.isSelected = false
                if (parentMenu != null) {
                    parentMenu.onItemClick((selectedMenuView!!.tag as Int))
                } else if (delegate != null) {
                    delegate!!.onItemClick(selectedMenuView!!.tag as Int)
                }
                popupWindow!!.dismiss(allowCloseAnimation)
            } else {
                popupWindow!!.dismiss()
            }
        } else {
            if (selectedMenuView != null) {
                selectedMenuView!!.isSelected = false
                selectedMenuView = null
            }
        }
        return super.onTouchEvent(event)
    }

    fun setDelegate(delegate: ActionBarMenuItemDelegate?) {
        this.delegate = delegate
    }

    fun setIconColor(color: Int) {
        imageView.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        if (clearButton != null) {
            clearButton!!.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    fun setSubMenuOpenSide(side: Int) {
        subMenuOpenSide = side
    }

    fun setLayoutInScreen(value: Boolean) {
        layoutInScreen = value
    }

    private fun createPopupLayout() {
        if (popupLayout != null) {
            return
        }
        rect = Rect()
        location = IntArray(2)
        popupLayout = ActionBarPopupWindowLayout(context)
        popupLayout!!.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                if (popupWindow != null && popupWindow!!.isShowing) {
                    v.getHitRect(rect)
                    if (!rect!!.contains(event.x.toInt(), event.y.toInt())) {
                        popupWindow!!.dismiss()
                    }
                }
            }
            false
        }
        popupLayout!!.setDispatchKeyEventListener(object : OnDispatchKeyEventListener {
            override fun onDispatchKeyEvent(keyEvent: KeyEvent?) {
                fun onDispatchKeyEvent(keyEvent: KeyEvent) {
                    if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.repeatCount == 0 && popupWindow != null && popupWindow!!.isShowing) {
                        popupWindow!!.dismiss()
                    }
                }
            }
        })
    }

    fun addSubItem(view: View?, width: Int, height: Int) {
        createPopupLayout()
        popupLayout!!.addView(view, LinearLayout.LayoutParams(width, height))
    }

    fun addSubItem(id: Int, text: String?): TextView {
        createPopupLayout()
        val textView = TextView(context)
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem))
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false))
        if (!LocaleController.isRTL) {
            textView.gravity = Gravity.CENTER_VERTICAL
        } else {
            textView.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
        textView.setPadding(dp(16f), 0, dp(16f), 0)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textView.minWidth = dp(196f)
        textView.tag = id
        textView.text = text
        popupLayout!!.addView(textView)
        val layoutParams = textView.layoutParams as LinearLayout.LayoutParams
        if (LocaleController.isRTL) {
            layoutParams.gravity = Gravity.RIGHT
        }
        layoutParams.width = LayoutHelper.MATCH_PARENT
        layoutParams.height = dp(48f)
        textView.layoutParams = layoutParams
        textView.setOnClickListener(OnClickListener { view ->
            if (popupWindow != null && popupWindow!!.isShowing) {
                if (processedPopupClick) {
                    return@OnClickListener
                }
                processedPopupClick = true
                popupWindow!!.dismiss(allowCloseAnimation)
            }
            if (parentMenu != null) {
                parentMenu.onItemClick((view.tag as Int))
            } else if (delegate != null) {
                delegate!!.onItemClick(view.tag as Int)
            }
        })
        menuHeight += layoutParams.height
        return textView
    }

    fun redrawPopup(color: Int) {
        if (popupLayout != null) {
            popupLayout!!.backgroundDrawabled!!.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
            popupLayout!!.invalidate()
        }
    }

    fun setPopupItemsColor(color: Int) {
        if (popupLayout == null) {
            return
        }
        val count = popupLayout!!.linearLayout.childCount
        for (a in 0 until count) {
            val child = popupLayout!!.linearLayout.getChildAt(a)
            if (child is TextView) {
                child.setTextColor(color)
            }
        }
    }

    fun hasSubMenu(): Boolean {
        return popupLayout != null
    }

    fun toggleSubMenu() {
        if (popupLayout == null) {
            return
        }
        if (showMenuRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(showMenuRunnable)
            showMenuRunnable = null
        }
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
            return
        }
        if (popupWindow == null) {
            popupWindow = ActionBarPopupWindow(popupLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT)
            if (animationEnabled && Build.VERSION.SDK_INT >= 19) {
                popupWindow!!.animationStyle = 0
            } else { //                popupWindow.setAnimationStyle(R.style.PopupAnimation);
            }
            if (!animationEnabled) {
                popupWindow!!.setAnimationEnabled(animationEnabled)
            }
            popupWindow!!.isOutsideTouchable = true
            popupWindow!!.isClippingEnabled = true
            if (layoutInScreen) {
                try {
                    if (layoutInScreenMethod == null) {
                        layoutInScreenMethod = PopupWindow::class.java.getDeclaredMethod("setLayoutInScreenEnabled", Boolean::class.javaPrimitiveType)
                        layoutInScreenMethod!!.setAccessible(true)
                    }
                    layoutInScreenMethod!!.invoke(popupWindow, true)
                } catch (e: Exception) {
                }
            }
//            popupWindow!!.inputMethodMode = ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED
            popupWindow!!.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
            popupLayout!!.measure(MeasureSpec.makeMeasureSpec(dp(1000f), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(dp(1000f), MeasureSpec.AT_MOST))
            popupWindow!!.contentView.isFocusableInTouchMode = true
            popupWindow!!.contentView.setOnKeyListener(OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_MENU && event.repeatCount == 0 && event.action == KeyEvent.ACTION_UP && popupWindow != null && popupWindow!!.isShowing) {
                    popupWindow!!.dismiss()
                    return@OnKeyListener true
                }
                false
            })
        }
        processedPopupClick = false
        popupWindow!!.isFocusable = true
        if (popupLayout!!.measuredWidth == 0) {
            updateOrShowPopup(true, true)
        } else {
            updateOrShowPopup(true, false)
        }
        popupWindow!!.startAnimation()
    }

    fun openSearch(openKeyboard: Boolean) {
        if (searchContainer == null || searchContainer!!.visibility == View.VISIBLE || parentMenu == null) {
            return
        }
        parentMenu.parentActionBar!!.onSearchFieldVisibilityChanged(toggleSearch(openKeyboard))
    }

    fun toggleSearch(openKeyboard: Boolean): Boolean {
        if (searchContainer == null) {
            return false
        }
        return if (searchContainer!!.visibility == View.VISIBLE) {
            if (listener == null || listener != null && listener!!.canCollapseSearch()) {
                searchContainer!!.visibility = View.GONE
                searchField!!.clearFocus()
                visibility = View.VISIBLE
                if (openKeyboard) {
                    hideKeyboard(searchField)
                }
                if (listener != null) {
                    listener!!.onSearchCollapse()
                }
            }
            false
        } else {
            searchContainer!!.visibility = View.VISIBLE
            visibility = View.GONE
            searchField!!.setText("")
            searchField!!.requestFocus()
            if (openKeyboard) {
                showKeyboard(searchField)
            }
            if (listener != null) {
                listener!!.onSearchExpand()
            }
            true
        }
    }

    fun closeSubMenu() {
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
        }
    }

    fun setIcon(resId: Int) {
        imageView.setImageResource(resId)
    }

    fun setIcon(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    fun setOverrideMenuClick(value: Boolean): ActionBarMenuItem {
        overrideMenuClick = value
        return this
    }

    fun setIsSearchField(value: Boolean): ActionBarMenuItem {
        if (parentMenu == null) {
            return this
        }
        if (value && searchContainer == null) {
            searchContainer = object : FrameLayout(context) {
                override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                    measureChildWithMargins(clearButton, widthMeasureSpec, 0, heightMeasureSpec, 0)
                    val width: Int
                    width = if (searchFieldCaption!!.visibility == View.VISIBLE) {
                        measureChildWithMargins(searchFieldCaption, widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec) / 2, heightMeasureSpec, 0)
                        searchFieldCaption!!.measuredWidth + dp(4f)
                    } else {
                        0
                    }
                    measureChildWithMargins(searchField, widthMeasureSpec, width, heightMeasureSpec, 0)
                    val w = MeasureSpec.getSize(widthMeasureSpec)
                    val h = MeasureSpec.getSize(heightMeasureSpec)
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
                }

                override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
                    super.onLayout(changed, left, top, right, bottom)
                    val x: Int
                    x = if (LocaleController.isRTL) {
                        0
                    } else {
                        if (searchFieldCaption!!.visibility == View.VISIBLE) {
                            searchFieldCaption!!.measuredWidth + dp(4f)
                        } else {
                            0
                        }
                    }
                    searchField!!.layout(x, searchField!!.top, x + searchField!!.measuredWidth, searchField!!.bottom)
                }
            }
            parentMenu.addView(searchContainer, 0, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f, 6, 0, 0, 0))
            searchContainer!!.setVisibility(View.GONE)
            searchFieldCaption = TextView(context)
            searchFieldCaption!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            searchFieldCaption!!.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSearch))
            searchFieldCaption!!.setSingleLine(true)
            searchFieldCaption!!.ellipsize = TextUtils.TruncateAt.END
            searchFieldCaption!!.visibility = View.GONE
            searchFieldCaption!!.gravity = if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT
            searchField = object : EditTextBoldCursor(context) {
                override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_DEL && searchField!!.length() == 0 && searchFieldCaption!!.visibility == View.VISIBLE && searchFieldCaption!!.length() > 0) {
                        clearButton!!.callOnClick()
                        return true
                    }
                    return super.onKeyDown(keyCode, event)
                }

                override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                    return super.dispatchKeyEvent(event)
                }
            }
            searchField!!.setCursorWidth(1.5f)
            searchField!!.setCursorColor(-0x1)
            searchField!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            searchField!!.setHintTextColor(Theme.getColor(Theme.key_actionBarDefaultSearchPlaceholder))
            searchField!!.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSearch))
            searchField!!.setSingleLine(true)
            searchField!!.setBackgroundResource(0)
            searchField!!.setPadding(0, 0, 0, 0)
            val inputType = searchField!!.getInputType() or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            searchField!!.setInputType(inputType)
            if (Build.VERSION.SDK_INT < 23) {
                searchField!!.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
                    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                        return false
                    }

                    override fun onDestroyActionMode(mode: ActionMode) {}
                    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                        return false
                    }

                    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                        return false
                    }
                })
            }
            searchField!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (event != null && (event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_SEARCH || event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(searchField)
                    if (listener != null) {
                        listener!!.onSearchPressed(searchField)
                    }
                }
                false
            })
            (searchField as EditTextBoldCursor).addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (ignoreOnTextChange) {
                        ignoreOnTextChange = false
                        return
                    }
                    if (listener != null) {
                        listener!!.onTextChanged(searchField)
                    }
                    if (clearButton != null) { //clearButton.setAlpha(TextUtils.isEmpty(s) && searchFieldCaption.getVisibility() != VISIBLE ? 0.6f : 1.0f);
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
            searchField!!.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_SEARCH)
            searchField!!.setTextIsSelectable(false)
            if (!LocaleController.isRTL) {
                searchContainer!!.addView(searchFieldCaption, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36f, Gravity.CENTER_VERTICAL or Gravity.LEFT, 0f, 5.5f, 0f, 0f))
                searchContainer!!.addView(searchField, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36f, Gravity.CENTER_VERTICAL, 0f, 0f, 48f, 0f))
            } else {
                searchContainer!!.addView(searchField, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36f, Gravity.CENTER_VERTICAL, 0f, 0f, 48f, 0f))
                searchContainer!!.addView(searchFieldCaption, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36f, Gravity.CENTER_VERTICAL or Gravity.RIGHT, 0f, 5.5f, 48f, 0f))
            }
            clearButton = ImageView(context)
            clearButton!!.setImageDrawable(CloseProgressDrawable2().also { progressDrawable = it })
            clearButton!!.colorFilter = PorterDuffColorFilter(parentMenu.parentActionBar!!.itemsColor, PorterDuff.Mode.MULTIPLY)
            clearButton!!.scaleType = ImageView.ScaleType.CENTER
            clearButton!!.setOnClickListener {
                if (searchField!!.length() != 0) {
                    searchField!!.setText("")
                } else if (searchFieldCaption != null && searchFieldCaption!!.visibility == View.VISIBLE) {
                    searchFieldCaption!!.visibility = View.GONE
                    //clearButton.setAlpha(searchField.length() == 0 && searchFieldCaption.getVisibility() != VISIBLE ? 0.6f : 1.0f);
                    if (listener != null) {
                        listener!!.onCaptionCleared()
                    }
                }
                searchField!!.requestFocus()
                showKeyboard(searchField)
            }
            searchContainer!!.addView(clearButton, LayoutHelper.createFrame(48, LayoutHelper.MATCH_PARENT, Gravity.CENTER_VERTICAL or Gravity.RIGHT))
        }
        isSearchField = value
        return this
    }

    fun setShowSearchProgress(show: Boolean) {
        if (progressDrawable == null) {
            return
        }
        if (show) {
            progressDrawable!!.startAnimation()
        } else {
            progressDrawable!!.stopAnimation()
        }
    }

    fun setSearchFieldCaption(caption: CharSequence?) {
        if (TextUtils.isEmpty(caption)) {
            searchFieldCaption!!.visibility = View.GONE
        } else {
            searchFieldCaption!!.visibility = View.VISIBLE
            searchFieldCaption!!.text = caption
        }
        if (clearButton != null) { //clearButton.setAlpha(searchField.length() == 0 && searchFieldCaption.getVisibility() != VISIBLE ? 0.6f : 1.0f);
        }
    }

    fun setIgnoreOnTextChange() {
        ignoreOnTextChange = true
    }

    fun isSearchField(): Boolean {
        return isSearchField
    }

    fun clearSearchText() {
        if (searchField == null) {
            return
        }
        searchField!!.setText("")
    }

    fun setActionBarMenuItemSearchListener(listener: ActionBarMenuItemSearchListener?): ActionBarMenuItem {
        this.listener = listener
        return this
    }

    fun setAllowCloseAnimation(value: Boolean): ActionBarMenuItem {
        allowCloseAnimation = value
        return this
    }

    fun setPopupAnimationEnabled(value: Boolean) {
        if (popupWindow != null) {
            popupWindow!!.setAnimationEnabled(value)
        }
        animationEnabled = value
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (popupWindow != null && popupWindow!!.isShowing) {
            updateOrShowPopup(false, true)
        }
    }

    fun setAdditionalOffset(value: Int) {
        additionalOffset = value
    }

    private fun updateOrShowPopup(show: Boolean, update: Boolean) {
        val offsetY: Int
        offsetY = if (parentMenu != null) {
            -parentMenu.parentActionBar!!.measuredHeight + parentMenu.top
        } else {
            val scaleY = scaleY
            (-(measuredHeight * scaleY - translationY / scaleY)).toInt() + additionalOffset
        }
        if (show) {
            popupLayout!!.scrollToTop()
        }
        if (parentMenu != null) {
            val parent: ActionBar = parentMenu!!.parentActionBar!!
            if (subMenuOpenSide == 0) {
                if (show) {
                    popupWindow!!.showAsDropDown(parent, left + parentMenu.left + measuredWidth - popupLayout!!.measuredWidth + translationX.toInt(), offsetY)
                }
                if (update) {
                    popupWindow!!.update(parent, left + parentMenu.left + measuredWidth - popupLayout!!.measuredWidth + translationX.toInt(), offsetY, -1, -1)
                }
            } else {
                if (show) {
                    popupWindow!!.showAsDropDown(parent, left - dp(8f) + translationX.toInt(), offsetY)
                }
                if (update) {
                    popupWindow!!.update(parent, left - dp(8f) + translationX.toInt(), offsetY, -1, -1)
                }
            }
        } else {
            if (subMenuOpenSide == 0) {
                if (parent != null) {
                    val parent = parent as View
                    if (show) {
                        popupWindow!!.showAsDropDown(parent, left + measuredWidth - popupLayout!!.measuredWidth, offsetY)
                    }
                    if (update) {
                        popupWindow!!.update(parent, left + measuredWidth - popupLayout!!.measuredWidth, offsetY, -1, -1)
                    }
                }
            } else {
                if (show) {
                    popupWindow!!.showAsDropDown(this, -dp(8f), offsetY)
                }
                if (update) {
                    popupWindow!!.update(this, -dp(8f), offsetY, -1, -1)
                }
            }
        }
    }

    fun hideSubItem(id: Int) {
        val view = popupLayout!!.findViewWithTag<View>(id)
        if (view != null) {
            view.visibility = View.GONE
        }
    }

    fun showSubItem(id: Int) {
        val view = popupLayout!!.findViewWithTag<View>(id)
        if (view != null) {
            view.visibility = View.VISIBLE
        }
    }

    companion object {
        private var layoutInScreenMethod: Method? = null
    }

    init {
        if (backgroundColor != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(backgroundColor))
        }
        parentMenu = menu
        imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER
        addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat()))
        if (iconColor != 0) {
            imageView.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY)
        }
    }
}