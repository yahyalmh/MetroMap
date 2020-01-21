/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package support.ActionBar

import android.animation.AnimatorSet
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import support.Theme

open class BaseFragment {
    private var isFinished = false
    var visibleDialog: Dialog? = null
    var fragmentView: View? = null
    var parentLayoute: ActionBarLayout? = null
    var actionBar: ActionBar? = null
        protected set
    protected var classGuid = 0
    var arguments: Bundle? = null
        protected set
     var swipeBackEnabled = true
     var hasOwnBackground = false

    constructor() {}
    constructor(args: Bundle?) {
        arguments = args
    }

    open fun createView(context: Context?): View? {
        return null
    }

    fun clearViews() {
        if (fragmentView != null) {
            if (fragmentView!!.parent  != null) {
                val parent = fragmentView!!.parent  as ViewGroup
                try {
                    onRemoveFromParent()
                    parent.removeView(fragmentView)
                } catch (e: Exception) {
                }
            }
            fragmentView = null
        }
        if (actionBar != null) {
            if (actionBar!!.parent != null) {
                val parent = actionBar!!.parent as ViewGroup
                try {
                    parent.removeView(actionBar)
                } catch (e: Exception) {
                }
            }
            actionBar = null
        }
        parentLayoute = null
    }

    fun onRemoveFromParent() {}
    fun setParentLayout(layout: ActionBarLayout?) {
        if (parentLayoute != layout) {
            parentLayoute = layout
            if (fragmentView != null) {
                val parent = fragmentView!!.parent as ViewGroup
                if (parent != null) {
                    try {
                        onRemoveFromParent()
                        parent.removeView(fragmentView)
                    } catch (e: Exception) {
                    }
                }
                if (parentLayoute != null && parentLayoute!!.context !== fragmentView!!.context) {
                    fragmentView = null
                }
            }
            if (actionBar != null) {
                val differentParent = parentLayoute != null && parentLayoute!!.context !== actionBar!!.context
                if (actionBar!!.addToContainer || differentParent) {
                    val parent = actionBar!!.parent as ViewGroup
                    if (parent != null) {
                        try {
                            parent.removeView(actionBar)
                        } catch (e: Exception) {
                        }
                    }
                }
                if (differentParent) {
                    actionBar = null
                }
            }
            if (parentLayoute != null && actionBar == null) {
                actionBar = createActionBar(parentLayoute!!.context)
                actionBar!!.parentFragment = this
            }
        }
    }

    protected fun createActionBar(context: Context?): ActionBar {
        val actionBar = ActionBar(context)
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault))
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false)
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true)
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false)
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true)
        return actionBar
    }

    @JvmOverloads
    fun finishFragment(animated: Boolean = true) {
        if (isFinished || parentLayoute == null) {
            return
        }
        parentLayoute!!.closeLastFragment(animated)
    }

    fun removeSelfFromStack() {
        if (isFinished || parentLayoute == null) {
            return
        }
        parentLayoute!!.removeFragmentFromStack(this)
    }

    open fun onFragmentCreate(): Boolean {
        return true
    }

    open fun onFragmentDestroy() {
        isFinished = true
        if (actionBar != null) {
            actionBar!!.isEnabled = false
        }
    }

    fun needDelayOpenAnimation(): Boolean {
        return false
    }

    open fun onResume() {}
    open fun onPause() {
        if (actionBar != null) {
            actionBar!!.onPause()
        }
        try {
            if (visibleDialog != null && visibleDialog!!.isShowing && dismissDialogOnPause(visibleDialog)) {
                visibleDialog!!.dismiss()
                visibleDialog = null
            }
        } catch (e: Exception) {
        }
    }

    fun getFragmentForAlert(offset: Int): BaseFragment {
        return if (parentLayoute == null || parentLayoute!!.fragmentsStack!!.size <= 1 + offset) {
            this
        } else parentLayoute!!.fragmentsStack!![parentLayoute!!.fragmentsStack!!.size - 2 - offset]
    }

    fun onConfigurationChanged(newConfig: Configuration?) {}
    open fun onBackPressed(): Boolean {
        return true
    }

    fun onActivityResultFragment(requestCode: Int, resultCode: Int, data: Intent?) {}
    open fun onRequestPermissionsResultFragment(requestCode: Int, permissions: Array<String?>?, grantResults: IntArray?) {}
    fun saveSelfArgs(args: Bundle?) {}
    fun restoreSelfArgs(args: Bundle?) {}
    fun presentFragment(fragment: BaseFragment?): Boolean {
        return parentLayoute != null && parentLayoute!!.presentFragment(fragment!!)
    }

    fun presentFragment(fragment: BaseFragment?, removeLast: Boolean): Boolean {
        return parentLayoute != null && parentLayoute!!.presentFragment(fragment!!, removeLast)
    }

    fun presentFragment(fragment: BaseFragment?, removeLast: Boolean, forceWithoutAnimation: Boolean): Boolean {
        return parentLayoute != null && parentLayoute!!.presentFragment(fragment!!, removeLast, forceWithoutAnimation, true)
    }

    val parentActivity: Activity?
        get() = if (parentLayoute != null) {
            parentLayoute!!.parentActivity
        } else null

    fun startActivityForResult(intent: Intent?, requestCode: Int) {
        if (parentLayoute != null) {
            parentLayoute!!.startActivityForResult(intent, requestCode)
        }
    }

    fun dismissCurrentDialig() {
        if (visibleDialog == null) {
            return
        }
        try {
            visibleDialog!!.dismiss()
            visibleDialog = null
        } catch (e: Exception) {
        }
    }

    fun dismissDialogOnPause(dialog: Dialog?): Boolean {
        return true
    }

    fun onBeginSlide() {
        try {
            if (visibleDialog != null && visibleDialog!!.isShowing) {
                visibleDialog!!.dismiss()
                visibleDialog = null
            }
        } catch (e: Exception) {
        }
        if (actionBar != null) {
            actionBar!!.onPause()
        }
    }

     fun onTransitionAnimationStart(isOpen: Boolean, backward: Boolean) {}
     fun onTransitionAnimationEnd(isOpen: Boolean, backward: Boolean) {}
    fun onBecomeFullyVisible() {}
     fun onCustomTransitionAnimation(isOpen: Boolean, callback: Runnable?): AnimatorSet? {
        return null
    }

    fun onLowMemory() {}
    fun showDialog(dialog: Dialog?, onDismissListener: DialogInterface.OnDismissListener?): Dialog? {
        return showDialog(dialog, false, onDismissListener)
    }

    @JvmOverloads
    fun showDialog(dialog: Dialog?, allowInTransition: Boolean = false, onDismissListener: DialogInterface.OnDismissListener? = null): Dialog? {
        if (dialog == null || parentLayoute == null || parentLayoute!!.animationInProgress || parentLayoute!!.startedTracking || !allowInTransition && parentLayoute!!.checkTransitionAnimation()) {
            return null
        }
        try {
            if (visibleDialog != null) {
                visibleDialog!!.dismiss()
                visibleDialog = null
            }
        } catch (e: Exception) {
        }
        try {
            visibleDialog = dialog
            visibleDialog!!.setCanceledOnTouchOutside(true)
            visibleDialog!!.setOnDismissListener { dialog ->
                onDismissListener?.onDismiss(dialog)
                onDialogDismiss(visibleDialog)
                visibleDialog = null
            }
            visibleDialog!!.show()
            return visibleDialog
        } catch (e: Exception) {
        }
        return null
    }

    protected fun onDialogDismiss(dialog: Dialog?) {}

    fun extendActionMode(menu: Menu?): Boolean {
        return false
    }
}