package ui.lines

import android.content.Context
import android.graphics.Point
import com.yaya.map.R
import support.LocaleController
import ui.cells.sataionCells.StationCell
import support.component.AndroidUtilities
import support.Theme
import utils.MetroUtil

/**
* Created by yaya-mh on 07/08/2018 09:07 AM 09:42 AM.
*/
class TajrishSubLine(context: Context) :MetroLine(context) {

    init {
        startStation =  LocaleController.getString("shahed", R.string.shahed)
        endStation =  LocaleController.getString("emam_khomeini_airport", R.string.emam_khomeini_airport)
        lineName = LocaleController.getString("tajrish_sub", R.string.tajrish_sub)
        firstStation = true
        stationColor = Theme.getColor(Theme.key_Tajrish_line)
        lineNumber = 8

        crossRoadsName = mapOf( LocaleController.getString("shahed", R.string.shahed) to LocaleController.getString("tajrish", R.string.tajrish))
        stationsNames = listOf(
                LocaleController.getString("shahed", R.string.shahed),
                LocaleController.getString("share_aftab", R.string.share_aftab),
                LocaleController.getString("vavan", R.string.vavan),
                LocaleController.getString("emam_khomeini_airport", R.string.emam_khomeini_airport))
        stationDistancePoint = Point(0,0)
        addStations()
        setWillNotDraw(false)
    }
    override fun addStations(){
        super.addStations()
        stationsList.forEach { station->
            station.stationNamePosition = StationCell.NamePosition.Left
        }
    }
    override fun changeDirection(stationName: String) {
        when(stationName){
            LocaleController.getString("vavan", R.string.vavan) -> stationDistancePoint.y += AndroidUtilities.dp(10F)
//            "Shahed" -> stationDistancePoint.x += support.component.AndroidUtilities.dp(-10f)
        }
    }

    override fun setStartEndPoint() {
        val crossCell =  MetroUtil.getCell(LocaleController.getString("shahed", R.string.shahed), LocaleController.getString("tajrish", R.string.tajrish))!!
        startPoint = Point(crossCell.left + ((crossCell.right - crossCell.left) / 2),crossCell.top + ((crossCell.bottom - crossCell.top) / 2))

        val cell = MetroUtil.getCell(LocaleController.getString("hassan_abad", R.string.hassan_abad), LocaleController.getString("farhangsara", R.string.farhangsara))!!
        endPoint = Point(cell.left + ((cell.right - cell.left) / 2) + AndroidUtilities.dp(2f), 0)
    }

    override fun setStationDistancePoint(subList: List<String>) {
        stationDistancePoint.x = -(startPoint.x - endPoint.x)
        stationDistancePoint.y = 0
    }

}