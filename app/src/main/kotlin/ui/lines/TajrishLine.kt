package ui.lines

import Constants
import android.content.Context
import android.graphics.Point
import com.yaya.map.R
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import ui.cells.sataionCells.StationCell
import ui.fragments.MapFragment

/**
 * Created by yaya-mh on 24/07/2018 09:01 AM.
 */
class TajrishLine(context: Context) : MetroLine(context) {

    init {

        startStation = LocaleController.getString("tajrish", R.string.tajrish)
        endStation = LocaleController.getString("kahrizak", R.string.kahrizak)
        lineName = LocaleController.getString("tajrish", R.string.tajrish)
        stationColor = Theme.getColor(Theme.key_Tajrish_line)
        lineNumber = 1

        firstStation = true
        crossRoadsName = hashMapOf(
                LocaleController.getString("beheshti", R.string.beheshti) to
                        LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("haftome_tir", R.string.haftome_tir) to
                        LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh) to
                        LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),
                LocaleController.getString("dowlat", R.string.dowlat) to
                        LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz),
                LocaleController.getString("emam_khomeini", R.string.emam_khomeini) to
                        LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("shahed", R.string.shahed) to
                        LocaleController.getString("tajrish_sub", R.string.tajrish_sub))

        stationsNames = listOf(
                LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("gheytarihe", R.string.gheytarihe),
                LocaleController.getString("sadr", R.string.sadr),
                LocaleController.getString("gholhak", R.string.gholhak),
                LocaleController.getString("shariati", R.string.shariati),
                LocaleController.getString("mirdamad", R.string.mirdamad),
                LocaleController.getString("haghani", R.string.haghani),
                LocaleController.getString("hemmat", R.string.hemmat),
                LocaleController.getString("mosalla", R.string.mosalla),
                LocaleController.getString("beheshti", R.string.beheshti),
                LocaleController.getString("mofatteh", R.string.mofatteh),
                LocaleController.getString("haftome_tir", R.string.haftome_tir),
                LocaleController.getString("taleghani", R.string.taleghani),
                LocaleController.getString("dowlat", R.string.dowlat),
                LocaleController.getString("sadi", R.string.sadi),
                LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                LocaleController.getString("panezdahe_khordad", R.string.panezdahe_khordad),
                LocaleController.getString("khayyam", R.string.khayyam),
                LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                LocaleController.getString("shoosh", R.string.shoosh),
                LocaleController.getString("payane_jonoob", R.string.payane_jonoob),
                LocaleController.getString("bokharai", R.string.bokharai),
                LocaleController.getString("aliabad", R.string.aliabad),
                LocaleController.getString("shahre_rey", R.string.shahre_rey),
                LocaleController.getString("bagher_shahr", R.string.bagher_shahr),
                LocaleController.getString("shahed", R.string.shahed),
                LocaleController.getString("harame_emam", R.string.harame_emam),
                LocaleController.getString("kahrizak", R.string.kahrizak))
        addStations()
        setWillNotDraw(false)
    }

    var x = 5
    var y = 5
    override fun addStations() {

        super.addStations()

        stationsList.forEach { station ->
            station.lat = x
            station.long = y
            x += 10
            y += 10
            station.cellProperty.addAll(listOf(Constants.BUS_PROPERTY, Constants.ELEVATOR_PROPERTY))
            station.stationNamePosition = when (stationsList.indexOf(station)) {
                9 , 13 -> StationCell.NamePosition.Up
                11, 18 -> StationCell.NamePosition.Down
                15 -> StationCell.NamePosition.Right
                in 0..17 -> {
                    StationCell.NamePosition.Left
                }
                else -> StationCell.NamePosition.Right
            }
        }
    }

    override fun changeDirection(stationName: String) {
        when (stationName) {
            LocaleController.getString("shariati", R.string.shariati) -> stationDistancePoint.x += (-15f * AndroidUtilities.density).toInt()
            LocaleController.getString("haghani", R.string.haghani) -> stationDistancePoint.x = 0
        }
    }

    override fun setStartEndPoint() {
        startPoint = Point(viewWidth * 3 / 4, AndroidUtilities.dp(25F))
        endPoint = Point(startPoint.x - (-15f * AndroidUtilities.density).toInt(), viewHeight - MapFragment.infoHeight.toInt())
    }

    override fun setStationDistancePoint(subList: List<String>) {
        stationDistancePoint = Point(0, (viewHeight - MapFragment.infoHeight.toInt()) / subList.size)
    }

}