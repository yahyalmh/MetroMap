package ui.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.yaya.map.R
import support.actionbar.ActionBar
import support.actionbar.BaseFragment
import support.actionbar.MenuDrawable
import support.LayoutHelper
import support.LocaleController
import support.NotificationCenter
import support.Theme
import support.component.AndroidUtilities
import support.component.HintEditText
import ui.activities.LaunchActivity
import ui.activities.LocationController
import ui.activities.StationFinder
import ui.adapters.DrawerLayoutAdapter
import ui.cells.sataionCells.SingleStationCell
import ui.cells.sataionCells.StationCell
import ui.lines.*
import ui.views.GuideView
import ui.views.ZoomLayout
import utils.MetroUtil
import java.util.*

/**
 * Created by yaya-mh on 7/15/2018 09:02 AM
 */
class MapFragment : BaseFragment(), NotificationCenter.NotificationCenterDelegate {
    companion object {
        var infoHeight: Float = 0.0f
//        var database = SQLiteDatabase(File(Constants.DB_PATH + File.separator + Constants.DB_NAME).absolutePath)
    }

    interface MapSearchListener {
        fun onTextChange(editText: EditText) {
        }
    }


    private var isRoadViewed: Boolean = false
    private lateinit var goTextView: HintEditText
    private var isRoatingStarted = false
    private lateinit var directionButtonContainer: FrameLayout
    private lateinit var sideMenu: RecyclerView
    private var checkPermission = true
    private lateinit var guideView: GuideView
    private lateinit var goImageView: ImageView
    private lateinit var stationFinder: StationFinder
    private lateinit var latTextView: TextView
    private lateinit var longTextView: TextView
    private lateinit var bottomView: LinearLayout
    private var clickedCell: StationCell? = null
    var sourceCell: StationCell? = null
    var destCell: StationCell? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFragmentCreate(): Boolean {
//        MetroUtil.copyDatabasesFromAssets()
        NotificationCenter.getInstance()!!.setAllowedNotificationsDutingAnimation(intArrayOf(NotificationCenter.cellClicked, NotificationCenter.liveLocationsChanged))
        NotificationCenter.getInstance()!!.addObserver(this, NotificationCenter.liveLocationsChanged)
        NotificationCenter.getInstance()!!.addObserver(this, NotificationCenter.cellClicked)

        if (Build.VERSION.SDK_INT > 23 && (LaunchActivity.applicationContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || LaunchActivity.applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            askForPermissions()
        } else {
            LocationController.getInstance()
        }
        return true
    }

    override fun createView(context: Context?): View? {
        MetroUtil.lines.clear()

        actionBar!!.setBackButtonDrawable(MenuDrawable())
        actionBar!!.title = "smart map"
        actionBar!!.actionBarMenuOnItemClick = object : ActionBar.Companion.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                parentLayoute!!.drawerLayoutContainer!!.openDrawer(false)
            }
        }
        actionBar!!.setAllowOverlayTitle(true)

        fragmentView = ZoomLayout(LaunchActivity.applicationContext)
        fragmentView!!.setBackgroundColor(Theme.getColor(Theme.key_metro_background))

        (sideMenu.adapter as DrawerLayoutAdapter).resetItems()
        sideMenu.adapter!!.notifyDataSetChanged()

        addLines(fragmentView as ZoomLayout)
        bottomView = addBottomView(LaunchActivity.applicationContext, fragmentView as ZoomLayout)
        addGuideView(fragmentView as ZoomLayout, LaunchActivity.applicationContext)


        addDirectionButton(fragmentView as ZoomLayout)
        stationFinder = StationFinder(fragmentView as ZoomLayout)

        return fragmentView
    }

    private fun addDirectionButton(fragmentView: ZoomLayout) {
        directionButtonContainer = FrameLayout(LaunchActivity.applicationContext)
        directionButtonContainer.visibility = View.VISIBLE
        val floatingButton = ImageView(LaunchActivity.applicationContext)
        floatingButton.scaleType = ImageView.ScaleType.CENTER
        val drawable: Drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground))!!

        floatingButton.background = drawable
        floatingButton.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY)
        floatingButton.setImageResource(R.drawable.attach_send2)

        directionButtonContainer.addView(floatingButton, LayoutHelper.createFrame(if (Build.VERSION.SDK_INT >= 21) 56 else 60, if (Build.VERSION.SDK_INT >= 21) 56f else 60f, Gravity.LEFT or Gravity.TOP, 10f, 0f, 10F, 0f))
        fragmentView.addView(directionButtonContainer, LayoutHelper.createFrame((if (Build.VERSION.SDK_INT >= 21) 56 else 60) + 20, ((if (Build.VERSION.SDK_INT >= 21) 56 else 60) + 14).toFloat(), (/*if (LocaleController.isRTL) Gravity.LEFT else*/ Gravity.RIGHT) or Gravity.BOTTOM, if (LocaleController.isRTL) 4f else 0f, 0f, if (LocaleController.isRTL) 0f else 4f, 0f))
        directionButtonContainer.setOnClickListener { v: View ->
            if (isRoadViewed){
                isRoadViewed = false
                stationFinder.removeGuideLine()
                sourceCell!!.removeLocationView()
                destCell!!.removeLocationView()
                isRoatingStarted = false
            }else {
                v.visibility = View.GONE
                bottomView.visibility = View.VISIBLE
                isRoatingStarted = true
            }
        }
    }


    private fun addGuideView(frameLayout: ZoomLayout, context: Context) {
        guideView = GuideView(context)
        frameLayout.addView(guideView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.BOTTOM or Gravity.LEFT,
                10f, 15f, 20f,
                infoHeight / AndroidUtilities.density + AndroidUtilities.dp(5f)))
    }

    private fun addBottomView(context: Context?, frameLayout: ZoomLayout): LinearLayout {
        val linearLayout = LinearLayout(LaunchActivity.applicationContext)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutDirection = LinearLayout.LAYOUT_DIRECTION_LTR
        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground))

        goImageView = ImageView(LaunchActivity.applicationContext)
        goImageView.setImageResource(R.drawable.ic_send)
        goImageView.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY)
        goImageView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground))
        val goImageLayoutParams = LinearLayout.LayoutParams(0, AndroidUtilities.dp(30f), 1.0f)
        goImageLayoutParams.setMargins(0, AndroidUtilities.dp(7f), AndroidUtilities.dp(5f), 0)
        goImageView.layoutParams = goImageLayoutParams

        goTextView = HintEditText(context)
        goTextView.hint = LocaleController.getString("Source", R.string.source)
        goTextView.setAlphaNumericFilter()
//        goTextView.setSingleLine(true)
        goTextView.setHintColor(Theme.getColor(Theme.key_Tajrish_line))
        goTextView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground))
        goTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText))
        goTextView.gravity = Gravity.CENTER
        goTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        goTextView.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")
        goTextView.setPadding(AndroidUtilities.dp(17F), AndroidUtilities.dp(9F), AndroidUtilities.dp(17F), AndroidUtilities.dp(9F))
        goTextView.visibility = View.VISIBLE
        infoHeight = (goTextView.lineHeight + AndroidUtilities.dp(goTextView.gravity.toFloat())).toFloat()
        val textViewLayoutParams = LinearLayout.LayoutParams(0, AndroidUtilities.dp(42f), 7.0f)
        goTextView.layoutParams = textViewLayoutParams

        linearLayout.addView(goTextView)
        linearLayout.addView(goImageView)

        goTextView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                stationFinder.onTextChange(goTextView)
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }


        })

        if (sourceCell != null) {
            sourceCell!!.removeLocationView()
        }
        if (destCell != null) {
            destCell!!.removeLocationView()
        }
        goImageView.setOnClickListener {

            if (goTextView.text.isBlank()) {
                Toast.makeText(LaunchActivity.applicationContext, "enter staion", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val stationName = goTextView.text.toString().trim()
            val cell = MetroUtil.getCell(stationName)
            if (cell != null) {

                routing(cell)
            } else {
                Toast.makeText(LaunchActivity.applicationContext, "false station", Toast.LENGTH_SHORT).show()

            }
        }

        frameLayout.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM))
        linearLayout.visibility = View.INVISIBLE
        return linearLayout
    }

    private fun routing(cell: StationCell) {
        if (goTextView.hint == LocaleController.getString("Source", R.string.source)) {
            goTextView.hint = LocaleController.getString("destentation", R.string.destentation)
            if (sourceCell != null) {
                sourceCell!!.removeLocationView()
            }
            sourceCell = cell
        } else {
            if (destCell != null) {
                destCell!!.removeLocationView()
            }
            destCell = cell
            bottomView.visibility = View.GONE
            directionButtonContainer.visibility = View.VISIBLE
            stationFinder.findRoad(sourceCell!!, destCell!!)
            goTextView.hint = LocaleController.getString("source", R.string.source)
            isRoadViewed = true
        }
        cell.addLocationView()
        goTextView.setText("")
    }

    private fun addLines(frameLayout: ZoomLayout) {
        addLine(frameLayout, TajrishLine(LaunchActivity.applicationContext))
        addLine(frameLayout, FarhangsaraLine(LaunchActivity.applicationContext))
        addLine(frameLayout, AzadeganLine(LaunchActivity.applicationContext))
        addLine(frameLayout, KolahdoozLine(LaunchActivity.applicationContext))
        addLine(frameLayout, FarhangsaraSubLine(LaunchActivity.applicationContext))
        addLine(frameLayout, AbdolazimLine(LaunchActivity.applicationContext))
        addLine(frameLayout, TakhtiLine(LaunchActivity.applicationContext))
        addLine(frameLayout, TajrishSubLine(LaunchActivity.applicationContext))
        addLine(frameLayout, KolahdoozSubLine(LaunchActivity.applicationContext))
        addLine(frameLayout, AzadeganSubLine(LaunchActivity.applicationContext))

    }

    private fun addLine(frameLayout: ZoomLayout, metroLine: MetroLine) {
        frameLayout.addView(metroLine, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER))
        MetroUtil.lines.add(metroLine)
    }

    var value = Pair(123, 122)
    var firstTime = true

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        MetroUtil.lines.clear()
        NotificationCenter.getInstance()!!.removeObserver(LaunchActivity.applicationContext, NotificationCenter.liveLocationsChanged)
    }

    private fun cellClicked(stationName: String) {
        val cell = MetroUtil.getCell(stationName)
        if (clickedCell != null) {
            if (clickedCell!!.isGuideViewed) {
                clickedCell!!.removeGuideProperty()
            }
        }

        if (isRoatingStarted && !isRoadViewed) {
            routing(cell!!)
        } else  {
            if (clickedCell != null && clickedCell!!.stationName == cell!!.stationName) return
            clickedCell = cell
            clickedCell!!.isGuideViewed = true
            clickedCell!!.addGuideProperty()
        }
    }

    override fun didReceivedNotification(id: Int, vararg args: Any?) {
        if (id == NotificationCenter.cellClicked) {
            val stationName = args[0] as String
            cellClicked(stationName)

        } else if (id == NotificationCenter.liveLocationsChanged) {
            val location = LocationController.getInstance()!!.lastKnownLocation
//            latTextView.text = location!!.latitude.toString()
//            longTextView.text = location!!.longitude.toString()
//            support.component.AndroidUtilities.shakeView(latTextView, 4f, 0)
//            support.component.AndroidUtilities.shakeView(longTextView, 4f, 0)
//            println("location: "+ value)
            val tajrishLineList = MetroUtil.getMetroLine(LocaleController.getString("tajrish", R.string.tajrish))?.stationsList
            for (i in 0 until tajrishLineList!!.size) {
                if (value.first > tajrishLineList[i].lat && value.first < tajrishLineList[i + 1].lat
                        && value.second > tajrishLineList[i].long && value.second < tajrishLineList[i + 1].long) {
                    val stationLine = MetroUtil.getMetroLine(LocaleController.getString("tajrish", R.string.tajrish))!!.linesList[i - 1]

                    Log.i("locationM", tajrishLineList[i].stationName)
                    Log.i("locationM", stationLine.toString())
                    val locationMapX = LocationController.getInstance()!!.locationMap(value.first.toDouble(), 0.toDouble(), 100.toDouble(), stationLine.left.toFloat(), stationLine.right.toFloat())
                    val locationMapY = LocationController.getInstance()!!.locationMap(value.second.toDouble(), 0.toDouble(), 100.toDouble(), stationLine.top.toFloat(), stationLine.bottom.toFloat())
                    Log.i("locationM", Point(locationMapX.toInt(), locationMapY.toInt()).toString())

                    if (firstTime) {
                        firstTime = false
//                        var locationCell = LocationCell(PointF(locationMapX.toFloat(), locationMapY.toFloat()))
//                        locationCell.draw(Canvas())
                        val cell = SingleStationCell(LaunchActivity.applicationContext, "temp", /*locationMapX.toInt()*/100f, /*locationMapY.toInt()*/500f, Color.rgb(100, 250, 60))
                        cell.stationName = "yourLocation"
                        (fragmentView as ZoomLayout).addView(cell, LayoutHelper.createFrame(50, 50f, Gravity.LEFT, (locationMapX.toFloat() - AndroidUtilities.dp(30f)) / AndroidUtilities.density, (locationMapY.toFloat() - AndroidUtilities.dp(30f)) / AndroidUtilities.density, 0f, 0f))

                        /*cell.layout(*//*locationMapX - support.component.AndroidUtilities.dp(2f)).toInt(),
                                (locationMapY - support.component.AndroidUtilities.dp(2f)).toInt(),
                                (locationMapX + support.component.AndroidUtilities.dp(2f)).toInt(),
                                (locationMapY + support.component.AndroidUtilities.dp(2f)).toInt(*//*300, 250, 400, 500)*/
                        (fragmentView as ZoomLayout).invalidate()
                    }
                }
            }
        }
    }

    fun setSideMenu(recyclerView: RecyclerView) {
        sideMenu = recyclerView
        sideMenu.setBackgroundColor(support.Theme.getColor(support.Theme.key_chats_menuBackground))
//         sideMenu.setGlowColor(support.Theme.getColor(support.Theme.key_chats_menuBackground))
    }

    private fun showPermissionAlert(byButton: Boolean) {
        val builder = AlertDialog.Builder(LaunchActivity.applicationContext)
        builder.setTitle(LaunchActivity.applicationContext.getString(R.string.AppName))
        if (byButton) {
            builder.setMessage(LaunchActivity.applicationContext.getString(R.string.PermissionNoLocationPosition))
        } else {
            builder.setMessage(LaunchActivity.applicationContext.getString(R.string.PermissionNoLocation))
        }
        builder.setNegativeButton(LaunchActivity.applicationContext.getString(R.string.PermissionOpenSettings), DialogInterface.OnClickListener { dialog, which ->
            if (LaunchActivity.applicationContext == null) {
                return@OnClickListener
            }
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + LaunchActivity.applicationContext.packageName)
                LaunchActivity.applicationContext.startActivity(intent)
            } catch (e: Exception) {

            }
        })
        builder.setPositiveButton(LaunchActivity.applicationContext.getString(R.string.OK), null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission && Build.VERSION.SDK_INT >= 23) {
            val activity = LaunchActivity.applicationContext
            if (activity != null) {
                checkPermission = false
                askForPermissions()
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun askForPermissions() {
        val activity = LaunchActivity.applicationContext
        val permission = ArrayList<String>()
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permission.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        val items = permission.toTypedArray()
        try {
//            activity!!.requestPermissions(items, 1)
        } catch (ignore: Exception) {
        }

    }

    override fun onRequestPermissionsResultFragment(requestCode: Int, permissions: Array<String?>?, grantResults: IntArray?) {
        if (requestCode == 1) {
            permissions!!.indices
                    .filter {
                        grantResults!!.size > it && grantResults[it] == PackageManager.PERMISSION_GRANTED && (permissions[it] == Manifest.permission.ACCESS_COARSE_LOCATION
                                || permissions[it] == Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .forEach { LocationController.getInstance() }
        }
    }

    private fun addLocationView(frameLayout: ZoomLayout) {
        val linearLayout = LinearLayout(LaunchActivity.applicationContext)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER
        linearLayout.layoutDirection = LinearLayout.LAYOUT_DIRECTION_LTR

        latTextView = TextView(LaunchActivity.applicationContext)
        latTextView.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground))
        latTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText))
        latTextView.gravity = Gravity.CENTER
        latTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        latTextView.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")

        longTextView = TextView(LaunchActivity.applicationContext)
        longTextView.setBackgroundColor(Theme.getColor(Theme.key_kolahdooz_line))
        longTextView.setTextColor(Theme.getColor(Theme.key_contacts_inviteText))
        longTextView.gravity = Gravity.CENTER
        longTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        longTextView.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")

        val textView = TextView(LaunchActivity.applicationContext)
        textView.text = "LAT"
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
        textView.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")
        textView.setPadding(AndroidUtilities.dp(17F), AndroidUtilities.dp(9F), AndroidUtilities.dp(17F), AndroidUtilities.dp(9F))
        linearLayout.addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(20f).toFloat()))
        linearLayout.addView(latTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(20f).toFloat()))

        val textView2 = TextView(LaunchActivity.applicationContext)
        textView2.text = "LONG"
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
        textView2.typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")
        textView2.setPadding(AndroidUtilities.dp(17F), AndroidUtilities.dp(9F), AndroidUtilities.dp(17F), AndroidUtilities.dp(9F))
        textView2.gravity = Gravity.CENTER
        linearLayout.addView(textView2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(20f).toFloat()))
        linearLayout.addView(longTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(20f).toFloat()))

        val stopButton = Button(LaunchActivity.applicationContext)
        val startButton = Button(LaunchActivity.applicationContext)
        startButton.text = "start"
        startButton.gravity = Gravity.CENTER
        startButton.setPadding(AndroidUtilities.dp(17F), AndroidUtilities.dp(9F), AndroidUtilities.dp(17F), AndroidUtilities.dp(9F))
        startButton.setOnClickListener {
            LocationController.getInstance()!!.start()
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
        val linearLayout1 = LinearLayout(LaunchActivity.applicationContext)
        linearLayout1.orientation = LinearLayout.HORIZONTAL
        linearLayout1.gravity = Gravity.CENTER
        linearLayout1.layoutDirection = LinearLayout.LAYOUT_DIRECTION_LTR
        linearLayout1.addView(startButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(20f).toFloat()))
        stopButton.text = "stop"
        stopButton.isEnabled = false
        stopButton.setPadding(AndroidUtilities.dp(17F), AndroidUtilities.dp(9F), AndroidUtilities.dp(17F), AndroidUtilities.dp(9F))
        stopButton.gravity = Gravity.CENTER
        stopButton.setOnClickListener {
            LocationController.getInstance()!!.stop(true)
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
        linearLayout1.addView(stopButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(20f).toFloat()))
        linearLayout.addView(linearLayout1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat()))

        (fragmentView as ZoomLayout).addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER))
    }

}