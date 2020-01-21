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
 * Created by yaya-mh on 06/08/2018 01:03 PM 01:03 PM.
 */
class TakhtiLine(context: Context) : MetroLine(context) {

    init {
        startStation = LocaleController.getString("varzeshagahe_takhtai", R.string.varzeshagahe_takhti)
        endStation = LocaleController.getString("yadegare_emam", R.string.yadegare_emam)
        lineName = LocaleController.getString("varzeshagahe_takhtai", R.string.varzeshagahe_takhti)
        lineNumber = 7
        firstStation = true

        stationColor = Theme.getColor(Theme.key_Takhti_line)
        crossRoadsName = mapOf(
                LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar) to
                        LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh) to
                        LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("mahdiye", R.string.mahdiye) to
                        LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("navab_safavi", R.string.navab_safavi) to
                        LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("towhid", R.string.towhid) to
                        LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz),
                LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares) to
                        LocaleController.getString("abdolazim", R.string.abdolazim))
        stationsNames = listOf(
                LocaleController.getString("varzeshagahe_takhtai", R.string.varzeshagahe_takhti),
                LocaleController.getString("basig", R.string.basig),
                LocaleController.getString("golha", R.string.golha),
                LocaleController.getString("ahang", R.string.ahang),
                LocaleController.getString("shahid_mahallati", R.string.shahid_mahallati),
                LocaleController.getString("mokhber", R.string.mokhber),
                LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar),
                LocaleController.getString("meydan_ghiam", R.string.meydan_ghiam),
                LocaleController.getString("molavi", R.string.molavi),
                LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                LocaleController.getString("vahdat_eslami", R.string.vahdat_eslami),
                LocaleController.getString("mahdiye", R.string.mahdiye),
                LocaleController.getString("halal_ahmar", R.string.halal_ahmar),
                LocaleController.getString("beryanak", R.string.beryanak),
                LocaleController.getString("komeyl", R.string.komeyl),
                LocaleController.getString("roodaki", R.string.roodaki),
                LocaleController.getString("navab_safavi", R.string.navab_safavi),
                LocaleController.getString("towhid", R.string.towhid),
                LocaleController.getString("bagher_khan", R.string.bagher_khan),
                LocaleController.getString("towhid", R.string.tarbiat_modares),
                LocaleController.getString("shahid_dadman", R.string.boostane_goftegoo),
                LocaleController.getString("shahid_dadman", R.string.borje_milad),
                LocaleController.getString("shahid_dadman", R.string.meydan_sanat),
                LocaleController.getString("shahid_dadman", R.string.shahid_dadman),
                LocaleController.getString("meydane_ketab", R.string.meydane_ketab),
                LocaleController.getString("yadegare_emam", R.string.yadegare_emam))
        addStations()
        setWillNotDraw(false)
    }

    override fun addStations() {
        var stationNamePos  = StationCell.NamePosition.Down

        super.addStations()
        stationsList.forEach { station ->
            station.stationNamePosition = when (stationsList.indexOf(station)) {
                in 0..12 -> when (stationNamePos) {
                    StationCell.NamePosition.Down -> StationCell.NamePosition.Up
                    StationCell.NamePosition.Up -> StationCell.NamePosition.Down
                    else -> {
                        StationCell.NamePosition.Up
                    }
                }
                in 13..15 -> StationCell.NamePosition.Left
                else -> StationCell.NamePosition.Right
            }
            stationNamePos = station.stationNamePosition
        }
    }

    override fun changeDirection(stationName: String) {
        when (stationName) {
            LocaleController.getString("beryanak", R.string.beryanak), LocaleController.getString("molavi", R.string.molavi), LocaleController.getString("vahdat_eslami", R.string.vahdat_eslami) -> stationDistancePoint.y = 0
            LocaleController.getString("komeyl", R.string.komeyl) -> {
                val point = goToCell(LocaleController.getString("beryanak", R.string.beryanak), LocaleController.getString("navab_safavi", R.string.navab_safavi), LocaleController.getString("navab_safavi", R.string.navab_safavi), 0, -2)
                stationDistancePoint = Point(-point.x, -point.y)
            }
            LocaleController.getString("meydan_ghiam", R.string.meydan_ghiam) -> stationDistancePoint.y = goToCell(LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar), LocaleController.getString("meydan_ghiam", R.string.meydan_ghiam), LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh), -1).y
            LocaleController.getString("halal_ahmar", R.string.halal_ahmar) -> {
                stationDistancePoint.y = 0
                val point = goToCell(LocaleController.getString("mahdiye", R.string.mahdiye), LocaleController.getString("halal_ahmar", R.string.halal_ahmar), LocaleController.getString("meydane_enghelab", R.string.meydane_enghelab))
                stationDistancePoint.x = -point.x
            }
            LocaleController.getString("bagher_khan", R.string.bagher_khan) -> {
                stationDistancePoint.y -= AndroidUtilities.dp(8f)
                stationDistancePoint.x -= AndroidUtilities.dp(5f)
            }
        }
    }

    override fun setStartEndPoint() {
        var cell = MetroUtil.getCell(LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar), LocaleController.getString("abdolazim", R.string.abdolazim))!!
        startPoint = Point(viewWidth - AndroidUtilities.dp(15F), cell.top + ((cell.bottom - cell.top) / 2))

        cell = MetroUtil.getCell(LocaleController.getString("gheytarihe", R.string.gheytarihe), LocaleController.getString("tajrish", R.string.tajrish))!!
        endPoint = Point(AndroidUtilities.dp(30f), cell.top + ((cell.bottom - cell.top) / 2))
    }

    override fun setStationDistancePoint(subList: List<String>) {
        val size = if (subList.size - 1 == 0) 1 else subList.size
        stationDistancePoint.y = -Math.abs(startPoint.y - endPoint.y) / size
        stationDistancePoint.x = 0
    }
}