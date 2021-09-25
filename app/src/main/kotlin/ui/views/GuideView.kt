package ui.views

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ui.cells.sataionCells.GuideStationCell
import ui.cells.GuideStationLine
import com.yaya.map.R
import support.component.AndroidUtilities
import support.LayoutHelper
import support.LocaleController
import support.Theme

/**
* Created by yaya-mh on 07/08/2018 12:23 PM 01:32 PM 03:35 PM.
*/
class GuideView(context: Context) : FrameLayout(context!!) {

    private val ghotbNama = ImageView(context)
    private val linesList = mutableListOf<GuideStationLine>()
    private val stationsList = mutableListOf<GuideStationCell>()


    init {
        addLines()
        addStations()
        addGhotbNama()

        setWillNotDraw(false)
    }

    private fun addGhotbNama() {
        ghotbNama.setImageResource(R.drawable.ghotbnama)
        addView(ghotbNama, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT or Gravity.BOTTOM))
        ghotbNama.visibility = View.VISIBLE
    }

    private fun addLines(){
        linesList.add(GuideStationLine(context,LocaleController.getString("tajrish", R.string.tajrish), LocaleController.getString("line_1",R.string.line_1),  Theme.getColor(Theme.key_Tajrish_line)))
        linesList.add(GuideStationLine(context,LocaleController.getString("farhangsara", R.string.farhangsara), LocaleController.getString("line_2",R.string.line_2),  Theme.getColor(Theme.key_Farhangsara_line)))
        linesList.add(GuideStationLine(context,LocaleController.getString("azadegan", R.string.azadegan), LocaleController.getString("line_3",R.string.line_3),  Theme.getColor(Theme.key_Azadegan_line)))
        linesList.add(GuideStationLine(context,LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz), LocaleController.getString("line_4",R.string.line_4),  Theme.getColor(Theme.key_kolahdooz_line)))
        linesList.add(GuideStationLine(context,LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub), LocaleController.getString("line_5",R.string.line_5),  Theme.getColor(Theme.key_FarhangsaraSubLine_line)))
        linesList.add(GuideStationLine(context,LocaleController.getString("abdolazim", R.string.abdolazim), LocaleController.getString("line_6",R.string.line_6),  Theme.getColor(Theme.key_Abdolazim_line)))
        linesList.add(GuideStationLine(context,LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti), LocaleController.getString("line_7",R.string.line_7),  Theme.getColor(Theme.key_Takhti_line)))

        linesList.forEach{line->
            addView(line, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT))
        }
    }

    private fun addStations() {
        stationsList.add(GuideStationCell(context, LocaleController.getString("Station_1", R.string.Station_1), Theme.getColor(Theme.key_Tajrish_line)))
        stationsList.add(GuideStationCell(context, LocaleController.getString("Station_2", R.string.Station_2), Theme.getColor(Theme.key_Tajrish_line)))
        stationsList.add(GuideStationCell(context, LocaleController.getString("Station_3", R.string.Station_3), Theme.getColor(Theme.key_Tajrish_line)))
        stationsList.add(GuideStationCell(context, LocaleController.getString("Station_4", R.string.Station_4), Theme.getColor(Theme.key_Tajrish_line)))

        stationsList.forEach { station->
            addView(station, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT))
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var l = Math.abs(left - right)/2
        var t = 0

        linesList.forEach { line->
            line.layout(l ,t, l + line.measuredWidth,  t + line.measuredHeight)
            t += line.measuredHeight
        }

        l = 0
        t = 0
        stationsList.forEach { station->
            station.layout(l ,t, l + station.measuredWidth,  t + station.measuredHeight)
            t += station.measuredHeight
        }
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(AndroidUtilities.dp(115f), AndroidUtilities.dp(160f))
        val availableWidth = measuredWidth - paddingLeft - paddingRight
        val availableHeight = measuredHeight - paddingTop- paddingEnd
        val width = availableWidth/2

        ghotbNama.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(measuredHeight / 3, MeasureSpec.EXACTLY))

        linesList.forEach { line->
            line.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(availableHeight/linesList.size, MeasureSpec.EXACTLY))
        }

        stationsList.forEach { station->
            station.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec((availableHeight - ghotbNama.measuredHeight) / stationsList.size, MeasureSpec.EXACTLY))
        }

    }
}