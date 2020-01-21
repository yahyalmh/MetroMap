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
* Created by yaya-mh on 04/08/2018 01:52 PM 09:58 AM.
*/
class KolahdoozLine(context : Context) : MetroLine(context) {

    init {
        startStation =  LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz)
        endStation =  LocaleController.getString("erame_sabz", R.string.erame_sabz)
        lineName = LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz)
        stationColor = Theme.getColor(Theme.key_kolahdooz_line)
        lineNumber = 4
        firstStation = true

        crossRoadsName = mapOf(
                LocaleController.getString("shemiran", R.string.shemiran)  to LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("dowlat", R.string.dowlat) to LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("teatre_shahr", R.string.teatre_shahr) to LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("shademan", R.string.shademan)  to LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("towhid", R.string.towhid)  to LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),
                LocaleController.getString("bimeh", R.string.bimeh)  to LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub),
                LocaleController.getString("erame_sabz", R.string.erame_sabz)  to LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        stationsNames = listOf(
                LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz),
                LocaleController.getString("niro_havaei", R.string.niro_havaei),
                LocaleController.getString("nabard", R.string.nabard),
                LocaleController.getString("piroozi", R.string.piroozi),
                LocaleController.getString("ebne_sina", R.string.ebne_sina),
                LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                LocaleController.getString("shemiran", R.string.shemiran),
                LocaleController.getString("dowlat", R.string.dowlat),
                LocaleController.getString("ferdowsi", R.string.ferdowsi),
                LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                LocaleController.getString("meydane_enghelab", R.string.meydane_enghelab),
                LocaleController.getString("towhid", R.string.towhid),
                LocaleController.getString("shademan", R.string.shademan),
                LocaleController.getString("dr_habibollah", R.string.dr_habibollah),
                LocaleController.getString("ostad_moein", R.string.ostad_moein),
                LocaleController.getString("meydane_azadi", R.string.meydane_azadi),
                LocaleController.getString("bimeh", R.string.bimeh),
                LocaleController.getString("ekbatan", R.string.ekbatan),
                LocaleController.getString("erame_sabz", R.string.erame_sabz))
        addStations()
        setWillNotDraw(false)
    }

    override fun addStations(){
        var stationNamePos  = StationCell.NamePosition.Down

        super.addStations()
        stationsList.forEach { station->
            station.stationNamePosition = when(stationsList.indexOf(station)){
                17 -> StationCell.NamePosition.Left
                in 0..16 -> when (stationNamePos) {
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
        when(stationName){
            LocaleController.getString("niro_havaei", R.string.niro_havaei)->{
                val point = goToCell(LocaleController.getString("meydane_shohada", R.string.meydane_shohada), LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz), LocaleController.getString("emam_hossein", R.string.emam_hossein))
                stationDistancePoint = Point( - point.x, 0)
            }

            LocaleController.getString("bimeh", R.string.bimeh)-> {
                stationDistancePoint.y -= AndroidUtilities.dp(8F)
            }
            LocaleController.getString("erame_sabz", R.string.erame_sabz)-> {
                val point = goToCell(LocaleController.getString("ekbatan", R.string.ekbatan), LocaleController.getString("erame_sabz", R.string.erame_sabz), LocaleController.getString("sadegheye", R.string.sadegheye))
                stationDistancePoint = Point(0, - point.y)
            }
            LocaleController.getString("towhid", R.string.towhid)-> stationDistancePoint.x = -goToCell(LocaleController.getString("meydane_enghelab", R.string.meydane_enghelab), LocaleController.getString("towhid", R.string.towhid), LocaleController.getString("navab_safavi", R.string.navab_safavi), 0).x
            LocaleController.getString("dr_habibollah", R.string.dr_habibollah)-> stationDistancePoint.x = - goToCell(LocaleController.getString("shademan", R.string.shademan), LocaleController.getString("meydane_azadi", R.string.meydane_azadi), LocaleController.getString("sadegheye", R.string.sadegheye)).x
        }
    }

    override fun setStartEndPoint() {
        var cell = MetroUtil.getCell(LocaleController.getString("baharestan", R.string.baharestan), LocaleController.getString("farhangsara", R.string.farhangsara))!!
        startPoint = Point(viewWidth - AndroidUtilities.dp(15F), cell.top + ((cell.bottom - cell.top) / 2) + AndroidUtilities.dp(5F))

        cell = MetroUtil.getCell(LocaleController.getString("sadegheye", R.string.sadegheye), LocaleController.getString("farhangsara", R.string.farhangsara))!!
        endPoint = Point( AndroidUtilities.dp(90F),cell.top + ((cell.bottom - cell.top) / 2))
    }

}