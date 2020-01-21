package support

import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import support.component.AndroidUtilities
import kotlin.math.roundToInt

class LayoutHelper {
    public companion object {
        public val MATCH_PARENT = -1
        public val WRAP_CONTENT = -2

        private fun getSize(size: Float): Int {
            return (if (size < 0) size.roundToInt() else AndroidUtilities.dp(size))
        }

        fun createScroll(width: Int, height: Int, gravity: Int): FrameLayout.LayoutParams? {
            return FrameLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()), gravity)
        }

        fun createFrame(width: Int, height: Float, gravity: Int, leftMargin: Float, topMargin: Float, rightMargin: Float, bottomMargin: Float): FrameLayout.LayoutParams? {
            val layoutParams = FrameLayout.LayoutParams(getSize(width.toFloat()), getSize(height), gravity)
            layoutParams.setMargins(AndroidUtilities.dp(leftMargin), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(rightMargin), AndroidUtilities.dp(bottomMargin))
            return layoutParams
        }

        fun createFrame(width: Int, height: Int, gravity: Int): FrameLayout.LayoutParams? {
            return FrameLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()), gravity)
        }

        fun createFrame(width: Int, height: Float): FrameLayout.LayoutParams? {
            return FrameLayout.LayoutParams(getSize(width.toFloat()), getSize(height))
        }

        fun createRelative(width: Float, height: Float, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int, alignParent: Int, alignRelative: Int, anchorRelative: Int): RelativeLayout.LayoutParams? {
            val layoutParams = RelativeLayout.LayoutParams(getSize(width), getSize(height))
            if (alignParent >= 0) {
                layoutParams.addRule(alignParent)
            }
            if (alignRelative >= 0 && anchorRelative >= 0) {
                layoutParams.addRule(alignRelative, anchorRelative)
            }
            layoutParams.leftMargin = AndroidUtilities.dp(leftMargin.toFloat())
            layoutParams.topMargin = AndroidUtilities.dp(topMargin.toFloat())
            layoutParams.rightMargin = AndroidUtilities.dp(rightMargin.toFloat())
            layoutParams.bottomMargin = AndroidUtilities.dp(bottomMargin.toFloat())
            return layoutParams
        }

        fun createRelative(width: Int, height: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): RelativeLayout.LayoutParams? {
            return createRelative(width.toFloat(), height.toFloat(), leftMargin, topMargin, rightMargin, bottomMargin, -1, -1, -1)
        }

        fun createRelative(width: Int, height: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int, alignParent: Int): RelativeLayout.LayoutParams? {
            return createRelative(width.toFloat(), height.toFloat(), leftMargin, topMargin, rightMargin, bottomMargin, alignParent, -1, -1)
        }

        fun createRelative(width: Float, height: Float, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int, alignRelative: Int, anchorRelative: Int): RelativeLayout.LayoutParams? {
            return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, alignRelative, anchorRelative)
        }

        fun createRelative(width: Int, height: Int, alignParent: Int, alignRelative: Int, anchorRelative: Int): RelativeLayout.LayoutParams? {
            return createRelative(width.toFloat(), height.toFloat(), 0, 0, 0, 0, alignParent, alignRelative, anchorRelative)
        }

        fun createRelative(width: Int, height: Int): RelativeLayout.LayoutParams? {
            return createRelative(width.toFloat(), height.toFloat(), 0, 0, 0, 0, -1, -1, -1)
        }

        fun createRelative(width: Int, height: Int, alignParent: Int): RelativeLayout.LayoutParams? {
            return createRelative(width.toFloat(), height.toFloat(), 0, 0, 0, 0, alignParent, -1, -1)
        }

        fun createRelative(width: Int, height: Int, alignRelative: Int, anchorRelative: Int): RelativeLayout.LayoutParams? {
            return createRelative(width.toFloat(), height.toFloat(), 0, 0, 0, 0, -1, alignRelative, anchorRelative)
        }

        fun createLinear(width: Int, height: Int, weight: Float, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): LinearLayout.LayoutParams? {
            val layoutParams = LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()), weight)
            layoutParams.setMargins(AndroidUtilities.dp(leftMargin.toFloat()), AndroidUtilities.dp(topMargin.toFloat()), AndroidUtilities.dp(rightMargin.toFloat()), AndroidUtilities.dp(bottomMargin.toFloat()))
            layoutParams.gravity = gravity
            return layoutParams
        }

        fun createLinear(width: Int, height: Int, weight: Float, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): LinearLayout.LayoutParams? {
            val layoutParams = LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()), weight)
            layoutParams.setMargins(AndroidUtilities.dp(leftMargin.toFloat()), AndroidUtilities.dp(topMargin.toFloat()), AndroidUtilities.dp(rightMargin.toFloat()), AndroidUtilities.dp(bottomMargin.toFloat()))
            return layoutParams
        }

        fun createLinear(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): LinearLayout.LayoutParams? {
            val layoutParams = LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()))
            layoutParams.setMargins(AndroidUtilities.dp(leftMargin.toFloat()), AndroidUtilities.dp(topMargin.toFloat()), AndroidUtilities.dp(rightMargin.toFloat()), AndroidUtilities.dp(bottomMargin.toFloat()))
            layoutParams.gravity = gravity
            return layoutParams
        }

        fun createLinear(width: Int, height: Int, leftMargin: Float, topMargin: Float, rightMargin: Float, bottomMargin: Float): LinearLayout.LayoutParams? {
            val layoutParams = LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()))
            layoutParams.setMargins(AndroidUtilities.dp(leftMargin), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(rightMargin), AndroidUtilities.dp(bottomMargin))
            return layoutParams
        }

        fun createLinear(width: Int, height: Int, weight: Float, gravity: Int): LinearLayout.LayoutParams? {
            val layoutParams = LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()), weight)
            layoutParams.gravity = gravity
            return layoutParams
        }

        fun createLinear(width: Int, height: Int, gravity: Int): LinearLayout.LayoutParams? {
            val layoutParams = LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()))
            layoutParams.gravity = gravity
            return layoutParams
        }

        fun createLinear(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams? {
            return LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()), weight)
        }

        fun createLinear(width: Int, height: Int): LinearLayout.LayoutParams? {
            return LinearLayout.LayoutParams(getSize(width.toFloat()), getSize(height.toFloat()))
        }
    }
}
