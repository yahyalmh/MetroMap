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
* Created by yaya-mh on 25/07/2018 09:01 AM 05:44 PM.
*/
class AzadeganLine(context: Context) : MetroLine(context) {

    init {
        startStation = LocaleController.getString("azadegan", R.string.azadegan)
        endStation =  LocaleController.getString("ghaem", R.string.ghaem)
        lineName =  LocaleController.getString("azadegan", R.string.azadegan)
        stationColor = Theme.getColor(Theme.key_Azadegan_line)
        lineNumber = 3
        firstStation = true

        crossRoadsName = mapOf(
                LocaleController.getString("beheshti", R.string.beheshti)   to LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("teatre_shahr", R.string.teatre_shahr)   to  LocaleController.getString("shemiran", R.string.shahid_kolahdooz)  ,
                LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr)   to LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("mahdiye", R.string.mahdiye)   to LocaleController.getString("varzeshagahe_takhtai", R.string.varzeshagahe_takhti),
                LocaleController.getString("nobonyad", R.string.nobonyad)   to LocaleController.getString("azadegan_sub", R.string.azadegan_sub))

        stationsNames = listOf(
                LocaleController.getString("ghaem", R.string.ghaem),
                LocaleController.getString("shahid_mahallati", R.string.shahid_mahallati),
                LocaleController.getString("aghdasiyeh", R.string.aghdasiyeh),
                LocaleController.getString("nobonyad", R.string.nobonyad),
                LocaleController.getString("hassan_abad", R.string.hassan_abad),
                LocaleController.getString("heravi", R.string.heravi),
                LocaleController.getString("zeynedin", R.string.zeynedin),
                LocaleController.getString("abdollah_ansari", R.string.abdollah_ansari),
                LocaleController.getString("sayad_shrirazi", R.string.sayad_shrirazi),
                LocaleController.getString("shahid_ghodoosi", R.string.shahid_ghodoosi),
                LocaleController.getString("sohravardi", R.string.sohravardi),
                LocaleController.getString("beheshti", R.string.beheshti),
                LocaleController.getString("mirzaye_shirazi", R.string.mirzaye_shirazi),
                LocaleController.getString("meydane_jahad", R.string.meydane_jahad),
                LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                LocaleController.getString("moniriyeh", R.string.moniriyeh),
                LocaleController.getString("mahdiye", R.string.mahdiye),
                LocaleController.getString("rahahan", R.string.rahahan),
                LocaleController.getString("javadiyeh", R.string.javadiyeh),
                LocaleController.getString("zamzam", R.string.zamzam),
                LocaleController.getString("shahrake_shariati", R.string.shahrake_shariati),
                LocaleController.getString("abdolabad", R.string.abdolabad),
                LocaleController.getString("nemat_abad", R.string.nemat_abad),
                LocaleController.getString("azadegan", R.string.azadegan))
        addStations()
        setWillNotDraw(false)
    }

    override fun addStations(){
        var stationNamePos = StationCell.NamePosition.Left
        super.addStations()
        stationsList.forEach { station->
            station.stationNamePosition = when(stationsList.indexOf(station)){
                in 0..2, 10, 15 -> when (stationNamePos) {
                    StationCell.NamePosition.Down -> StationCell.NamePosition.Up
                    StationCell.NamePosition.Up -> StationCell.NamePosition.Down
                    else -> {
                        StationCell.NamePosition.Down
                    }
                }
                8,9, 17 -> StationCell.NamePosition.Right
                else -> StationCell.NamePosition.Left
            }
            stationNamePos = station.stationNamePosition
        }
    }

    override fun changeDirection(stationName: String) {
        when (stationName) {
            LocaleController.getString("sohravardi",R.string.sohravardi) ->stationDistancePoint.y =  0
            LocaleController.getString("abdolabad",R.string.abdolabad) -> stationDistancePoint.x = 0
            LocaleController.getString("meydane_jahad",R.string.meydane_jahad) -> stationDistancePoint.y += AndroidUtilities.dp(15F)
            LocaleController.getString("javadiyeh",R.string.javadiyeh) -> stationDistancePoint.x += AndroidUtilities.dp(-10F)
            LocaleController.getString("azadegan",R.string.azadegan) -> stationDistancePoint.x += AndroidUtilities.dp(-10F)
            LocaleController.getString("moniriyeh",R.string.moniriyeh) ->stationDistancePoint.y = goToCell(LocaleController.getString("teatre_shahr",R.string.teatre_shahr),LocaleController.getString("moniriyeh",R.string.moniriyeh) , LocaleController.getString("panezdahe_khordad",R.string.panezdahe_khordad)).y

            LocaleController.getString("mahdiye",R.string.mahdiye)  -> stationDistancePoint.y = goToCell(LocaleController.getString("azadegan",R.string.azadegan), LocaleController.getString("moniriyeh",R.string.moniriyeh),LocaleController.getString("shahed",R.string.shahed)).y
            LocaleController.getString("teatre_shahr",R.string.teatre_shahr) -> stationDistancePoint.y = goToCell(LocaleController.getString("teatre_shahr",R.string.teatre_shahr),LocaleController.getString("meydane_valiasr",R.string.meydane_valiasr), LocaleController.getString("dowlat",R.string.dowlat) ).y
            LocaleController.getString("hassan_abad",R.string.hassan_abad) ->{
                val point = goToCell(LocaleController.getString("nobonyad",R.string.nobonyad) , LocaleController.getString("shahid_ghodoosi",R.string.shahid_ghodoosi), LocaleController.getString("beheshti",R.string.beheshti), -2)
                stationDistancePoint = Point( - point.x, point.y)
            }
            LocaleController.getString("meydane_valiasr",R.string.meydane_valiasr) -> {
                val point = goToCell(LocaleController.getString("meydane_jahad",R.string.meydane_jahad) , LocaleController.getString("meydane_valiasr",R.string.meydane_valiasr), LocaleController.getString(".haftome_tir",R.string.haftome_tir))
                stationDistancePoint = Point(0, point.y)
            }
        }
    }

    override fun setStartEndPoint() {
        stationDistancePoint = Point(0,0)

        var cell = MetroUtil.getCell(LocaleController.getString("sadr",R.string.sadr) , LocaleController.getString("tajrish", R.string.tajrish))!!
        startPoint = Point(viewWidth - AndroidUtilities.dp(15F) ,cell.top + ((cell.bottom - cell.top) / 2))

        cell = MetroUtil.getCell(LocaleController.getString("shahed",R.string.shahed) , LocaleController.getString("tajrish", R.string.tajrish))!!
        endPoint = Point(AndroidUtilities.dp(30f), cell.top + ((cell.bottom - cell.top) / 2))
    }

}