/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */
package ui.cells.tellCell

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.yaya.map.R
import support.LayoutHelper
import support.Theme
import support.component.AndroidUtilities

class DrawerProfileCell(context: Context?) : FrameLayout(context) {
    private val nameTextView: TextView
    private val phoneTextView: TextView
    private val shadowView: ImageView
    //private CloudView cloudView;
    private val srcRect = Rect()
    private val destRect = Rect()
    private val paint = Paint()
    private var currentColor: Int? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148f) + AndroidUtilities.statusBarHeight, MeasureSpec.EXACTLY))
        } else {
            try {
                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148f), MeasureSpec.EXACTLY))
            } catch (e: Exception) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148f))
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
//        val backgroundDrawable: Drawable = Theme.getCachedWallpaper()
        val color: Int
        color = Theme.getColor(Theme.key_chats_menuTopShadow)
        if (currentColor == null || currentColor != color) {
            currentColor = color
            shadowView.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
        nameTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName))
        /* if (Theme.isCustomTheme() && backgroundDrawable != null) {
             phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhone))
             shadowView.visibility = View.VISIBLE
             if (backgroundDrawable is ColorDrawable) {
                 backgroundDrawable.setBounds(0, 0, measuredWidth, measuredHeight)
                 backgroundDrawable.draw(canvas)
             } else if (backgroundDrawable is BitmapDrawable) {
                 val bitmap = backgroundDrawable.bitmap
                 val scaleX = measuredWidth.toFloat() / bitmap.width.toFloat()
                 val scaleY = measuredHeight.toFloat() / bitmap.height.toFloat()
                 val scale = if (scaleX < scaleY) scaleY else scaleX
                 val width = (measuredWidth / scale).toInt()
                 val height = (measuredHeight / scale).toInt()
                 val x = (bitmap.width - width) / 2
                 val y = (bitmap.height - height) / 2
                 srcRect[x, y, x + width] = y + height
                 destRect[0, 0, measuredWidth] = measuredHeight
                 try {
                     canvas.drawBitmap(bitmap, srcRect, destRect, paint)
                 } catch (e: Throwable) {
                 }
             }
         } else {*/
        shadowView.visibility = View.INVISIBLE
        phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhoneCats))
        super.onDraw(canvas)
//        }
    }

    /* fun getCachedWallpaper(): Drawable? {
         synchronized(wallpaperSync) {
             return if (themedWallpaper != null) {
                 themedWallpaper
             } else {
                 wallpaper
             }
         }
     }*/
    fun setUser(name: String?) {
        nameTextView.text = name
    }

    //private Drawable cloudDrawable;
//private int lastCloudColor;
/*private class CloudView extends View {

        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public CloudView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (Theme.isCustomTheme() && Theme.getCachedWallpaper() != null) {
                paint.setColor(Theme.getServiceMessageColor());
            } else {
                paint.setColor(Theme.getColor(Theme.key_chats_menuCloudBackgroundCats));
            }
            int newColor = Theme.getColor(Theme.key_chats_menuCloud);
            if (lastCloudColor != newColor) {
                cloudDrawable.setColorFilter(new PorterDuffColorFilter(lastCloudColor = Theme.getColor(Theme.key_chats_menuCloud), PorterDuff.Mode.MULTIPLY));
            }
            canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, support.AndroidUtilities.dp(34) / 2.0f, paint);
            int l = (getMeasuredWidth() - support.AndroidUtilities.dp(24)) / 2;
            int t = (getMeasuredHeight() - support.AndroidUtilities.dp(24)) / 2 + support.AndroidUtilities.dp(0.5f);
            cloudDrawable.setBounds(l, t, l + support.AndroidUtilities.dp(24), t + support.AndroidUtilities.dp(24));
            cloudDrawable.draw(canvas);
        }
    }*/
    init {
        //cloudDrawable = context.getResources().getDrawable(R.drawable.bookmark_filled);
//cloudDrawable.setColorFilter(new PorterDuffColorFilter(lastCloudColor = Theme.getColor(Theme.key_chats_menuCloud), PorterDuff.Mode.MULTIPLY));
        shadowView = ImageView(context)
        shadowView.visibility = View.INVISIBLE
        shadowView.scaleType = ImageView.ScaleType.FIT_XY
        shadowView.setImageResource(R.drawable.bottom_shadow)
        addView(shadowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 70, Gravity.LEFT or Gravity.BOTTOM))
        nameTextView = TextView(context)
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        nameTextView.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")
        nameTextView.setLines(1)
        nameTextView.maxLines = 1
        nameTextView.setSingleLine(true)
        nameTextView.gravity = Gravity.LEFT
        nameTextView.ellipsize = TextUtils.TruncateAt.END
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.LEFT or Gravity.BOTTOM, 16f, 0f, 76f, 28f))
        phoneTextView = TextView(context)
        phoneTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        phoneTextView.setLines(1)
        phoneTextView.maxLines = 1
        phoneTextView.setSingleLine(true)
        phoneTextView.gravity = Gravity.LEFT
        addView(phoneTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.LEFT or Gravity.BOTTOM, 16f, 0f, 76f, 9f))
    }
}