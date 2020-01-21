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
* Created by yaya-mh on 18/07/2018 09:01 AM 02:23 PM.
*/

class FarhangsaraLine(context: Context) : MetroLine(context) {

    init {
        startStation = LocaleController.getString("farhangsara", R.string.farhangsara)
        endStation = LocaleController.getString("sadegheye", R.string.sadegheye)
        lineName = LocaleController.getString("Farhangsara", R.string.farhangsara)
        stationColor = Theme.getColor(Theme.key_Farhangsara_line)
        lineNumber = 2
        firstStation = true

        crossRoadsName = mapOf(
                LocaleController.getString("emam_hossein", R.string.emam_hossein) to LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("shemiran", R.string.shemiran)  to LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz) ,
                LocaleController.getString("shemiran", R.string.emam_khomeini)  to LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("navab_safavi", R.string.navab_safavi)  to LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),
                LocaleController.getString("shademan", R.string.shademan)  to LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz) ,
                LocaleController.getString("sadegheye", R.string.sadegheye)  to LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))

        stationsNames = listOf(
                LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("teharnpars", R.string.teharnpars),
                LocaleController.getString("shahid_bagheri", R.string.shahid_bagheri),
                LocaleController.getString("elmo_snaat", R.string.elmo_snaat),
                LocaleController.getString("sarsabz", R.string.sarsabz),
                LocaleController.getString("janbazan", R.string.janbazan),
                LocaleController.getString("fadak", R.string.fadak),
                LocaleController.getString("sabalan", R.string.sabalan),
                LocaleController.getString("shahid_madani", R.string.shahid_madani),
                LocaleController.getString("emam_hossein", R.string.emam_hossein),
                LocaleController.getString("shemiran", R.string.shemiran),
                LocaleController.getString("baharestan", R.string.baharestan),
                LocaleController.getString("mellat", R.string.mellat),
                LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                LocaleController.getString("hassan_abad", R.string.hassan_abad),
                LocaleController.getString("emam_ali", R.string.emam_ali),
                LocaleController.getString("meydan_hor", R.string.meydan_hor),
                LocaleController.getString("navab_safavi", R.string.navab_safavi),
                LocaleController.getString("shademan", R.string.shademan),
                LocaleController.getString("sharif", R.string.sharif),
                LocaleController.getString("tarasht", R.string.tarasht),
                LocaleController.getString("sadegheye", R.string.sadegheye))
        addStations()
        setWillNotDraw(false)
    }

    override fun addStations(){
        var stationNamePos  = StationCell.NamePosition.Down

        super.addStations()
        stationsList.forEach { station->
            station.stationNamePosition = when(stationsList.indexOf(station)){
                in 0..2, in 14..16 -> when (stationNamePos) {
                    StationCell.NamePosition.Down -> StationCell.NamePosition.Up
                    StationCell.NamePosition.Up -> StationCell.NamePosition.Down
                    else -> {
                        StationCell.NamePosition.Up
                    }
                }
                in 3..13 -> StationCell.NamePosition.Right
                17,19 -> StationCell.NamePosition.Left
                18,20 -> StationCell.NamePosition.Up
                else -> StationCell.NamePosition.Up
            }
            stationNamePos = station.stationNamePosition
        }
    }

    override fun changeDirection(stationName: String) {
        when (stationName) {
            LocaleController.getString("emam_ali", R.string.emam_ali) -> stationDistancePoint.x += AndroidUtilities.dp(-8F)
            LocaleController.getString("meydan_hor", R.string.meydan_hor) -> stationDistancePoint.x += AndroidUtilities.dp(8F)

            LocaleController.getString("teharnpars", R.string.teharnpars),
            LocaleController.getString("hassan_abad", R.string.hassan_abad),
            LocaleController.getString("sadegheye", R.string.sadegheye) -> stationDistancePoint.y = 0

            LocaleController.getString("elmo_snaat", R.string.elmo_snaat)  ->{
                val point = goToCell(LocaleController.getString("shahid_bagheri", R.string.shahid_bagheri),
                        LocaleController.getString("emam_hossein", R.string.emam_hossein) ,
                        LocaleController.getString("haftome_tir", R.string.haftome_tir), -4)
                stationDistancePoint = Point(-point.x, point.y)
            }
            LocaleController.getString("navab_safavi", R.string.navab_safavi)  ->{
                stationDistancePoint.x = - goToCell(LocaleController.getString("shademan", R.string.shademan) , LocaleController.getString("meydan_hor", R.string.meydan_hor) , LocaleController.getString("mofatteh", R.string.mofatteh) , 2).x
                stationDistancePoint.y = - goToCell(LocaleController.getString("shademan", R.string.shademan) , LocaleController.getString("meydan_hor", R.string.meydan_hor) , LocaleController.getString("dowlat", R.string.dowlat) ).y
            }
            LocaleController.getString("sharif",R.string.sharif)  ->{
                stationDistancePoint.y = - goToCell(LocaleController.getString("tarasht",R.string.tarasht) , LocaleController.getString("shademan",R.string.shademan) , LocaleController.getString("haftome_tir",R.string.haftome_tir) ).y
            }
            LocaleController.getString("shemiran",R.string.shemiran)  -> {
                val point = goToCell(LocaleController.getString("shemiran",R.string.shemiran) , LocaleController.getString("emam_hossein",R.string.emam_hossein) , LocaleController.getString("dowlat",R.string.dowlat) , -2)
                stationDistancePoint = Point(- point.x , point.y)
            }
            LocaleController.getString("baharestan",R.string.baharestan) ->{
                val point = goToCell(LocaleController.getString("emam_khomeini",R.string.emam_khomeini) , LocaleController.getString("shemiran",R.string.shemiran) , LocaleController.getString("emam_khomeini",R.string.emam_khomeini) )
                stationDistancePoint = Point(- point.x, point.y)
            }
        }
    }

    override fun setStartEndPoint() {
        var cell = MetroUtil.getCell(LocaleController.getString("sadr",R.string.sadr) , LocaleController.getString("tajrish", R.string.tajrish))!!
        startPoint = Point(viewWidth - AndroidUtilities.dp(15F), cell.bottom + AndroidUtilities.dp(3f))

        cell = MetroUtil.getCell(LocaleController.getString("mofatteh",R.string.mofatteh) , LocaleController.getString("tajrish", R.string.tajrish))!!
        endPoint = Point(AndroidUtilities.dp(120F) ,cell.bottom )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        stationsList.forEach { station->
            station.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30f), MeasureSpec.EXACTLY))
        }

    }

}



