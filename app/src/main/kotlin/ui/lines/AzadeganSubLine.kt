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
* Created by yaya-mh on 05/08/2018 01:28 PM 03:48 PM.
*/
class AzadeganSubLine(context: Context): MetroLine(context){


    init {
        startStation = LocaleController.getString("nobonyad", R.string.nobonyad)
        endStation = LocaleController.getString("shahid_bahonar", R.string.shahid_bahonar)
        firstStation = true
        lineName = LocaleController.getString("azadegan_sub", R.string.azadegan_sub)
        stationColor = Theme.getColor(Theme.key_Azadegan_line)
        lineNumber = 10

        crossRoadsName = mapOf(LocaleController.getString("nobonyad", R.string.nobonyad) to     LocaleController.getString("azadegan", R.string.azadegan) )
        stationsNames = listOf(
                LocaleController.getString("nobonyad", R.string.nobonyad),
                LocaleController.getString("niyavaran", R.string.niyavaran),
                LocaleController.getString("shahid_bahonar", R.string.shahid_bahonar))
        stationDistancePoint = Point(0,0)
        addStations()
        setWillNotDraw(false)
    }
    override fun addStations(){
        super.addStations()
        stationsList.forEach { station->
            station.stationNamePosition = when(stationsList.indexOf(station)){
                0 -> StationCell.NamePosition.Left
                1 -> StationCell.NamePosition.Right
                else -> StationCell.NamePosition.Up
            }
        }
    }
    override fun changeDirection(stationName: String) {
        when(stationName){
            LocaleController.getString("shahid_bahonar", R.string.shahid_bahonar) -> stationDistancePoint.y = 0
            LocaleController.getString("niyavaran",R.string.niyavaran)  -> {
                val point = goToCell(LocaleController.getString("nobonyad",R.string.nobonyad) , LocaleController.getString("niyavaran",R.string.niyavaran)  , LocaleController.getString("tajrish", R.string.tajrish))
                stationDistancePoint = Point(AndroidUtilities.dp(-10f), - point.y)

            }
        }
    }



    override fun setStartEndPoint() {
        val crossCell =  MetroUtil.getCell(LocaleController.getString("nobonyad",R.string.nobonyad) , LocaleController.getString("azadegan", R.string.azadegan))!!
        startPoint = Point(crossCell.left + ((crossCell.right - crossCell.left) / 2), crossCell.top + ((crossCell.bottom - crossCell.top) / 2))

        val cell = MetroUtil.getCell(LocaleController.getString("tajrish", R.string.tajrish), LocaleController.getString("tajrish", R.string.tajrish))!!
        endPoint = Point((cell.left + ((cell.right - cell.left) / 2)) + AndroidUtilities.dp(10f),cell.top + ((cell.bottom - cell.top) / 2))
    }

    override fun setStationDistancePoint(subList: List<String>) {
        val size =if(subList.size - 1 == 0 ) 1 else  subList.size
        stationDistancePoint.x = -(startPoint.x - endPoint.x) / size
        stationDistancePoint.y = -(startPoint.y - endPoint.y)
    }
}