package support.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.database.ContentObserver
import android.graphics.*
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import com.yaya.map.R
import ui.activities.LaunchActivity
import java.lang.reflect.Field
import java.util.*

class AndroidUtilities {
    companion object {
        private val typefaceCache = Hashtable<String, Typeface?>()
        private var prevOrientation = -10

        @JvmStatic
        public var statusBarHeight = 0
        @JvmStatic
        public var density = 1f
        @JvmStatic
        public var displaySize = Point()
        @JvmStatic
        public var roundMessageSize = 0
        var incorrectDisplaySizeFix = false
        var photoSize: Int? = null
        var displayMetrics = DisplayMetrics()
        var leftBaseline = 0
        var usingHardwareInput = false
        var isInMultiwindow = false

        var decelerateInterpolator = DecelerateInterpolator()
        var overshootInterpolator = OvershootInterpolator()

        @JvmStatic
        public var isTablet: Boolean? = null
        @JvmStatic
        private var adjustOwnerClassGuid = 0

        @JvmStatic
        private val roundPaint: Paint? = null
        @JvmStatic
        private val bitmapRect: RectF? = null

        init {
            leftBaseline = if (isTablet()) 80 else 72
            checkDisplaySize(LaunchActivity.applicationContext, null)
        }


        @JvmStatic
        fun requestAdjustResize(activity: Activity?, classGuid: Int) {
            if (activity == null || isTablet()) {
                return
            }
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            adjustOwnerClassGuid = classGuid
        }

        @JvmStatic
        fun removeAdjustResize(activity: Activity?, classGuid: Int) {
            if (activity == null || isTablet()) {
                return
            }
            if (adjustOwnerClassGuid == classGuid) {
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            }
        }

        @JvmStatic
        fun lockOrientation(activity: Activity?) {
            if (activity == null || prevOrientation != -10) {
                return
            }
            try { //            prevOrientation = activity.getRequestedOrientation();
                val manager = activity.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
                if (manager != null && manager.defaultDisplay != null) {
                    val rotation = manager.defaultDisplay.rotation
                    val orientation = activity.resources.configuration.orientation
                    if (rotation == Surface.ROTATION_270) {
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        }
                    } else if (rotation == Surface.ROTATION_90) {
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        } else {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    } else if (rotation == Surface.ROTATION_0) {
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    } else {
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        } else {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }

        @JvmStatic
        fun unlockOrientation(activity: Activity?) {
            if (activity == null) {
                return
            }
            try {
                if (prevOrientation != -10) { //                activity.setRequestedOrientation(prevOrientation);
                    prevOrientation = -10
                }
            } catch (e: Exception) {
            }
        }

        @JvmStatic
        fun getTypeface(assetPath: String): Typeface? {
            synchronized(typefaceCache) {
                if (!typefaceCache.containsKey(assetPath)) {
                    try {
                        val t = Typeface.createFromAsset(LaunchActivity.applicationContext.assets, assetPath)
                        typefaceCache[assetPath] = t
                    } catch (e: Exception) {
                        return null
                    }
                }
                return typefaceCache[assetPath]
            }
        }


        @JvmStatic
        fun showKeyboard(view: View?) {
            if (view == null) {
                return
            }
            try {
                val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            } catch (e: Exception) {
            }
        }

        @JvmStatic
        fun isKeyboardShowed(view: View?): Boolean {
            if (view == null) {
                return false
            }
            try {
                val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                return inputManager.isActive(view)
            } catch (e: Exception) {
            }
            return false
        }

        @JvmStatic
        fun hideKeyboard(view: View?) {
            if (view == null) {
                return
            }
            try {
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (!imm.isActive) {
                    return
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
            }
        }

        @JvmStatic
        fun dp(value: Float): Int {
            return if (value == 0f) {
                0
            } else Math.ceil(density * value.toDouble()).toInt()
        }

        @JvmStatic
        fun dp2(value: Float): Int {
            return if (value == 0f) {
                0
            } else Math.floor(density * value.toDouble()).toInt()
        }

        @SuppressLint("SoonBlockedPrivateApi")
        fun getViewInset(view: View?): Int {
            if (view == null || Build.VERSION.SDK_INT < 21 || view.height == displaySize.y || view.height == displaySize.y - statusBarHeight) {
                return 0
            }
            try {
                if (mAttachInfoField == null) {
                    mAttachInfoField = View::class.java.getDeclaredField("mAttachInfo")
                    mAttachInfoField!!.isAccessible = true
                }
                val mAttachInfo = mAttachInfoField!![view]
                if (mAttachInfo != null) {
                    if (mStableInsetsField == null) {
                        mStableInsetsField = mAttachInfo.javaClass.getDeclaredField("mStableInsets")
                        mStableInsetsField!!.setAccessible(true)
                    }
                    val insets = mStableInsetsField!![mAttachInfo] as Rect
                    return insets.bottom
                }
            } catch (e: java.lang.Exception) {
            }
            return 0
        }

        fun checkDisplaySize(context: Context, newConfiguration: Configuration?) {
            try {
                density = context.resources.displayMetrics.density
                var configuration = newConfiguration
                if (configuration == null) {
                    configuration = context.resources.configuration
                }
                usingHardwareInput = configuration!!.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO
                val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                if (manager != null) {
                    val display = manager.defaultDisplay
                    if (display != null) {
                        display.getMetrics(displayMetrics)
                        display.getSize(displaySize)
                    }
                }
                if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                    val newSize = Math.ceil(configuration.screenWidthDp * density.toDouble()).toInt()
                    if (Math.abs(displaySize.x - newSize) > 3) {
                        displaySize.x = newSize
                    }
                }
                if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                    val newSize = Math.ceil(configuration.screenHeightDp * density.toDouble()).toInt()
                    if (Math.abs(displaySize.y - newSize) > 3) {
                        displaySize.y = newSize
                    }
                }
                if (roundMessageSize == 0) {
                    roundMessageSize = if (isTablet()) {
                        (getMinTabletSide() * 0.6f).toInt()
                    } else {
                        (Math.min(displaySize.x, displaySize.y) * 0.6f).toInt()
                    }
                }
            } catch (e: Exception) {
            }
        }

        @JvmStatic
        fun getPixelsInCM(cm: Float, isX: Boolean): Float {
            return cm / 2.54f * if (isX) displayMetrics.xdpi else displayMetrics.ydpi
        }

        @JvmStatic
        fun makeBroadcastId(id: Int): Long {
            return 0x0000000100000000L or (id.toLong() and 0x00000000FFFFFFFFL)
        }

        @JvmStatic
        fun getMyLayerVersion(layer: Int): Int {
            return layer and 0xffff
        }

        @JvmStatic
        fun getPeerLayerVersion(layer: Int): Int {
            return layer shr 16 and 0xffff
        }

        fun setMyLayerVersion(layer: Int, version: Int): Int {
            return layer and -0x10000 or version
        }

        fun setPeerLayerVersion(layer: Int, version: Int): Int {
            return layer and 0x0000ffff or (version shl 16)
        }

        fun runOnUIThread(runnable: Runnable) {
            runOnUIThread(runnable, 0)
        }

        fun runOnUIThread(runnable: Runnable, delay: Long) {
            if (delay == 0L) {
                LaunchActivity.applicationHandler.post(runnable)
            } else {
                LaunchActivity.applicationHandler.postDelayed(runnable, delay)
            }
        }

        fun cancelRunOnUIThread(runnable: Runnable) {
            LaunchActivity.applicationHandler.removeCallbacks(runnable)
        }

        fun isTablet(): Boolean {
            if (isTablet == null) {
                isTablet = LaunchActivity.applicationContext.resources.getBoolean(R.bool.isTablet)
            }
            return isTablet as Boolean
        }

        fun isSmallTablet(): Boolean {
            val minSide = Math.min(displaySize.x, displaySize.y) / density
            return minSide <= 700
        }

        fun getMinTabletSide(): Int {
            return if (!isSmallTablet()) {
                val smallSide = Math.min(displaySize.x, displaySize.y)
                var leftSide = smallSide * 35 / 100
                if (leftSide < dp(320f)) {
                    leftSide = dp(320f)
                }
                smallSide - leftSide
            } else {
                val smallSide = Math.min(displaySize.x, displaySize.y)
                val maxSide = Math.max(displaySize.x, displaySize.y)
                var leftSide = maxSide * 35 / 100
                if (leftSide < dp(320f)) {
                    leftSide = dp(320f)
                }
                Math.min(smallSide, maxSide - leftSide)
            }
        }

        fun getPhotoSize(): Int {
            if (photoSize == null) {
                photoSize = 1280
            }
            return photoSize as Int
        }

        private val callLogContentObserver: ContentObserver? = null
        private val unregisterRunnable: Runnable? = null
        private val hasCallPermissions = Build.VERSION.SDK_INT >= 23


        private var mAttachInfoField: Field? = null
        private var mStableInsetsField: Field? = null

        fun getRealScreenSize(): Point? {
            val size = Point()
            try {
                val windowManager = LaunchActivity.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    windowManager.defaultDisplay.getRealSize(size)
                } else {
                    try {
                        val mGetRawW = Display::class.java.getMethod("getRawWidth")
                        val mGetRawH = Display::class.java.getMethod("getRawHeight")
                        size[(mGetRawW.invoke(windowManager.defaultDisplay) as Int)] = (mGetRawH.invoke(windowManager.defaultDisplay) as Int)
                    } catch (e: Exception) {
                        size[windowManager.defaultDisplay.width] = windowManager.defaultDisplay.height
                    }
                }
            } catch (e: Exception) {
            }
            return size
        }


        fun shakeView(view: View, x: Float, num: Int) {
            if (num == 6) {
                view.translationX = 0f
                return
            }
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(ObjectAnimator.ofFloat(view, "translationX", dp(x).toFloat()))
            animatorSet.duration = 50
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    shakeView(view, if (num == 5) 0f else -x, num + 1)
                }
            })
            animatorSet.start()
        }
    }

}
