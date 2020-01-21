package ui.lines

import android.content.Context
import android.graphics.Point
import com.yaya.map.R
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import ui.cells.sataionCells.StationCell
import utils.MetroUtil

/**
 * Created by yaya-mh on 05/08/2018 01:28 PM 03:48 PM.
 */
class FarhangsaraSubLine(context: Context) : MetroLine(context) {

    init {
        startStation = LocaleController.getString("sadegheye", R.string.sadegheye)
        endStation = LocaleController.getString("golshahr", R.string.golshahr)
        lineName = LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub)
        firstStation = true
        stationColor = Theme.getColor(Theme.key_FarhangsaraSubLine_line)
        lineNumber = 5

        crossRoadsName = mapOf(
                LocaleController.getString("sadegheye", R.string.sadegheye) to
                        LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("erame_sabz", R.string.erame_sabz) to
                        LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))
        stationsNames = listOf(
                LocaleController.getString("sadegheye", R.string.sadegheye),
                LocaleController.getString("erame_sabz", R.string.erame_sabz),
                LocaleController.getString("varzeshgah_azadi", R.string.varzeshgah_azadi),
                LocaleController.getString("chitgar", R.string.chitgar),
                LocaleController.getString("iran_khodro", R.string.iran_khodro),
                LocaleController.getString("vardavard", R.string.vardavard),
                LocaleController.getString("garmdarreh", R.string.garmdarreh),
                LocaleController.getString("alborz", R.string.alborz),
                LocaleController.getString("karaj", R.string.karaj),
                LocaleController.getString("mohammad_shahr", R.string.mohammad_shahr),
                LocaleController.getString("golshahr", R.string.golshahr))

        stationDistancePoint = Point(0, 0)
        addStations()
        setWillNotDraw(false)
    }

    override fun addStations() {
        var stationNamePos = StationCell.NamePosition.Down
        super.addStations()
        stationsList.forEach { station ->
            station.stationNamePosition = when (stationsList.indexOf(station)) {
                in 2..10 -> when (stationNamePos) {
                    StationCell.NamePosition.Down -> StationCell.NamePosition.Up
                    StationCell.NamePosition.Up -> StationCell.NamePosition.Down
                    else -> {
                        StationCell.NamePosition.Up
                    }
                }
                else -> StationCell.NamePosition.Up
            }
            stationNamePos = station.stationNamePosition
        }
    }

    override fun changeDirection(stationName: String) {
    }


    override fun setStartEndPoint() {
        val crossCell = MetroUtil.getCell(LocaleController.getString("sadegheye", R.string.sadegheye), LocaleController.getString("farhangsara", R.string.farhangsara))!!
        startPoint = Point(crossCell.left + ((crossCell.right - crossCell.left) / 2), crossCell.top + ((crossCell.bottom - crossCell.top) / 2))
        endPoint = Point(AndroidUtilities.dp(10f), 0)
    }

    override fun setStationDistancePoint(subList: List<String>) {
        val size = if (subList.size - 1 == 0) 1 else subList.size
        stationDistancePoint.x = -(startPoint.x - endPoint.x) / size
        stationDistancePoint.y = 0
    }
}