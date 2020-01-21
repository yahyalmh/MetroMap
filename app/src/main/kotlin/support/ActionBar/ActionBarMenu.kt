/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.ActionBar

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import support.Theme
import support.component.AndroidUtilities.Companion.dp

class ActionBarMenu : LinearLayout {
    var parentActionBar: ActionBar? = null
    var isActionMode = false

    constructor(context: Context?, layer: ActionBar?) : super(context) {
        orientation = HORIZONTAL
        parentActionBar = layer
    }

    constructor(context: Context?) : super(context) {}

    fun updateItemsBackgroundColor() {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            (view as? ActionBarMenuItem)?.setBackgroundDrawable(Theme.createSelectorDrawable(if (isActionMode) parentActionBar!!.itemsActionModeBackgroundColor else parentActionBar!!.itemsBackgroundColor))
        }
    }

    fun updateItemsColor() {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                view.setIconColor(if (isActionMode) parentActionBar!!.itemsActionModeColor else parentActionBar!!.itemsColor)
            }
        }
    }

    fun addItem(id: Int, drawable: Drawable?): ActionBarMenuItem {
        return addItem(id, 0, if (isActionMode) parentActionBar!!.itemsActionModeBackgroundColor else parentActionBar!!.itemsBackgroundColor, drawable, dp(48f))
    }

    fun addItemWithWidth(id: Int, icon: Int, width: Int): ActionBarMenuItem {
        return addItem(id, icon, if (isActionMode) parentActionBar!!.itemsActionModeBackgroundColor else parentActionBar!!.itemsBackgroundColor, null, width)
    }

    @JvmOverloads
    fun addItem(id: Int, icon: Int, backgroundColor: Int = if (isActionMode) parentActionBar!!.itemsActionModeBackgroundColor else parentActionBar!!.itemsBackgroundColor, drawable: Drawable? = null, width: Int = dp(48f)): ActionBarMenuItem {
        val menuItem = ActionBarMenuItem(context, this, backgroundColor, if (isActionMode) parentActionBar!!.itemsActionModeColor else parentActionBar!!.itemsColor)
        menuItem.tag = id
        if (drawable != null) {
            menuItem.imageView.setImageDrawable(drawable)
        } else if (icon != 0) {
            menuItem.imageView.setImageResource(icon)
        }
        addView(menuItem, LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT))
        menuItem.setOnClickListener { view ->
            val item = view as ActionBarMenuItem
            if (item.hasSubMenu()) {
                if (parentActionBar!!.actionBarMenuOnItemClick!!.canOpenMenu()) {
                    item.toggleSubMenu()
                }
            } else if (item.isSearchField()) {
                parentActionBar!!.onSearchFieldVisibilityChanged(item.toggleSearch(true))
            } else {
                onItemClick(view.getTag() as Int)
            }
        }
        return menuItem
    }

    fun hideAllPopupMenus() {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                view.closeSubMenu()
            }
        }
    }

    fun setPopupItemsColor(color: Int) {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                view.setPopupItemsColor(color)
            }
        }
    }

    fun redrawPopup(color: Int) {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                view.redrawPopup(color)
            }
        }
    }

    fun onItemClick(id: Int) {
        if (parentActionBar!!.actionBarMenuOnItemClick != null) {
            parentActionBar!!.actionBarMenuOnItemClick!!.onItemClick(id)
        }
    }

    fun clearItems() {
        removeAllViews()
    }

    fun onMenuButtonPressed() {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                val item = view
                if (item.visibility != View.VISIBLE) {
                    continue
                }
                if (item.hasSubMenu()) {
                    item.toggleSubMenu()
                    break
                } else if (item.overrideMenuClick) {
                    onItemClick(item.tag as Int)
                    break
                }
            }
        }
    }

    fun closeSearchField(closeKeyboard: Boolean) {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                val item = view
                if (item.isSearchField()) {
                    parentActionBar!!.onSearchFieldVisibilityChanged(false)
                    item.toggleSearch(closeKeyboard)
                    break
                }
            }
        }
    }

    fun setSearchTextColor(color: Int, placeholder: Boolean) {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                val item = view
                if (item.isSearchField()) {
                    if (placeholder) {
                        item.searchField!!.setHintTextColor(color)
                    } else {
                        item.searchField!!.setTextColor(color)
                    }
                    break
                }
            }
        }
    }

    fun openSearchField(toggle: Boolean, text: String) {
        val count = childCount
        for (a in 0 until count) {
            val view = getChildAt(a)
            if (view is ActionBarMenuItem) {
                val item = view
                if (item.isSearchField()) {
                    if (toggle) {
                        parentActionBar!!.onSearchFieldVisibilityChanged(item.toggleSearch(true))
                    }
                    item.searchField!!.setText(text)
                    item.searchField!!.setSelection(text.length)
                    break
                }
            }
        }
    }

    fun getItem(id: Int): ActionBarMenuItem? {
        val v = findViewWithTag<View>(id)
        return if (v is ActionBarMenuItem) {
            v
        } else null
    }
}