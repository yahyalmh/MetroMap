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
class KolahdoozSubLine(context: Context) : MetroLine(context) {

    init {
        startStation = LocaleController.getString("bimeh", R.string.bimeh)
        endStation = LocaleController.getString("mehrabad_4and6", R.string.mehrabad_4and6)
        lineName = LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub)

        firstStation = true
        stationColor = Theme.getColor(Theme.key_kolahdooz_line)
        lineNumber = 9

        crossRoadsName = mapOf(LocaleController.getString("bimeh", R.string.bimeh) to LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))
        stationsNames = listOf(
                LocaleController.getString("bimeh", R.string.bimeh),
                LocaleController.getString("mehrabad_1and2", R.string.mehrabad_1and2),
                LocaleController.getString("mehrabad_4and6", R.string.mehrabad_4and6))
        stationDistancePoint = Point(0, 0)
        addStations()
        setWillNotDraw(false)
    }
    override fun addStations(){
        super.addStations()
        stationsList.forEach { station->
            station.stationNamePosition = when(stationsList.indexOf(station)){
                0 -> StationCell.NamePosition.Up
                else -> StationCell.NamePosition.Left
            }
        }
    }

    override fun changeDirection(stationName: String) {
        when(stationName){
            LocaleController.getString("mehrabad_4and6", R.string.mehrabad_4and6) -> stationDistancePoint= Point(0, stationDistancePoint.y + AndroidUtilities.dp(10f))
        }
    }


    override fun setStartEndPoint() {
        var cell =  MetroUtil.getCell(LocaleController.getString("bimeh", R.string.bimeh), LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))!!
        startPoint = Point(cell.left + ((cell.right - cell.left) / 2), cell.top + ((cell.bottom - cell.top) / 2))

        cell = MetroUtil.getCell(LocaleController.getString("ekbatan", R.string.ekbatan), LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))!!
        val cell1 = MetroUtil.getCell(LocaleController.getString("meydane_azadi", R.string.meydane_azadi), LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))!!
        endPoint = Point(cell.left + ((cell.right - cell.left) / 2), cell1.top + ((cell1.bottom - cell1.top) / 2))

    }

    override fun setStationDistancePoint(subList: List<String>) {
        val size =if(subList.size - 1 == 0 ) 1 else  subList.size
        stationDistancePoint.x = - Math.abs(startPoint.x - endPoint.x)
        stationDistancePoint.y = Math.abs(startPoint.y - endPoint.y)/size
    }
}