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
class AbdolazimLine(context: Context) : MetroLine(context) {

    init {
        startStation = LocaleController.getString("abdolazim", R.string.abdolazim)
        endStation = LocaleController.getString("sulghan", R.string.sulghan)
        lineName = LocaleController.getString("abdolazim", R.string.abdolazim)

        lineNumber = 6
        firstStation = true

        stationColor = Theme.getColor(Theme.key_Abdolazim_line)
        crossRoadsName = mapOf(
                LocaleController.getString("meydane_shohada", R.string.meydane_shohada) to
                        LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz),

                LocaleController.getString("emam_hossein", R.string.emam_hossein) to
                        LocaleController.getString("farhangsara", R.string.farhangsara),

                LocaleController.getString("haftome_tir", R.string.haftome_tir) to
                        LocaleController.getString("tajrish", R.string.tajrish),

                LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr) to
                        LocaleController.getString("azadegan", R.string.azadegan),

                LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar) to
                        LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),

                LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares) to
                        LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti))

        stationsNames = listOf(
                LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("ebne_babveh", R.string.ebne_babveh),
                LocaleController.getString("cheshme_ali", R.string.cheshme_ali),
                LocaleController.getString("dowlat_abad", R.string.dowlat_abad),
                LocaleController.getString("kiyan_shahr", R.string.kiyan_shahr),
                LocaleController.getString("besat", R.string.besat),
                LocaleController.getString("shahid_rezai", R.string.shahid_rezai),
                LocaleController.getString("meydane_khorasan", R.string.meydane_khorasan),
                LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar),
                LocaleController.getString("amir_kabir", R.string.amir_kabir),
                LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                LocaleController.getString("emam_hossein", R.string.emam_hossein),
                LocaleController.getString("sarbaz", R.string.sarbaz),
                LocaleController.getString("bahar_shiraz", R.string.bahar_shiraz),
                LocaleController.getString("haftome_tir", R.string.haftome_tir),
                LocaleController.getString("shahid_nejatollahi", R.string.shahid_nejatollahi),
                LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                LocaleController.getString("boostan_laleh", R.string.boostan_laleh),
                LocaleController.getString("kargar", R.string.kargar),
                LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares),
                LocaleController.getString("shahrake_azemayesh", R.string.shahrake_azemayesh),
                LocaleController.getString("mazdaran", R.string.mazdaran),
                LocaleController.getString("yadegare_emam", R.string.yadegare_emam),
                LocaleController.getString("ashrafi_esfahani", R.string.ashrafi_esfahani),
                LocaleController.getString("shahid_sattari", R.string.shahid_sattari),
                LocaleController.getString("ayatollah_kashani", R.string.ayatollah_kashani),
                LocaleController.getString("shahre_ziba", R.string.shahre_ziba),
                LocaleController.getString("shahran", R.string.shahran),
                LocaleController.getString("shahid_abshenasan", R.string.shahid_abshenasan),
                LocaleController.getString("sulghan", R.string.sulghan))
        addStations()
        setWillNotDraw(false)
    }

    override fun addStations() {
        var stationNamePos = StationCell.NamePosition.Down
        super.addStations()
        stationsList.forEach { station ->
            station.stationNamePosition = when (stationsList.indexOf(station)) {
                12 -> StationCell.NamePosition.Down
                8, 13, 15 -> StationCell.NamePosition.Up
                in 0..9 -> StationCell.NamePosition.Right
                in 16..19 -> StationCell.NamePosition.Left
                in 20..28 -> when (stationNamePos) {
                    StationCell.NamePosition.Up -> StationCell.NamePosition.Down
                    StationCell.NamePosition.Down -> StationCell.NamePosition.Up
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
        when (stationName) {
            LocaleController.getString("abdolazim", R.string.abdolazim) -> {
                stationDistancePoint.y -= AndroidUtilities.dp(20f)
                stationDistancePoint.x = -goToCell(LocaleController.getString("abdolazim", R.string.abdolazim), LocaleController.getString("dowlat_abad", R.string.dowlat_abad), LocaleController.getString("meydane_shohada", R.string.meydane_shohada)).x
            }
            LocaleController.getString("emam_hossein", R.string.emam_hossein), LocaleController.getString("haftome_tir", R.string.haftome_tir) -> stationDistancePoint.y = 0
            LocaleController.getString("kiyan_shahr", R.string.kiyan_shahr) -> stationDistancePoint.x = 0
            LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr) -> {
                stationDistancePoint.y -= (10f * AndroidUtilities.density).toInt()
                stationDistancePoint.x += (15f * AndroidUtilities.density).toInt()
            }
            LocaleController.getString("mazdaran", R.string.mazdaran) -> {
                stationDistancePoint.y = 0
                stationDistancePoint.x += (2f * AndroidUtilities.density).toInt()
            }
        }

    }

    override fun setStartEndPoint() {
        var cell = MetroUtil.getCell(LocaleController.getString("nabard", R.string.nabard), LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))!!
        val cell1 = MetroUtil.getCell(LocaleController.getString("harame_emam", R.string.harame_emam), LocaleController.getString("tajrish", R.string.tajrish))!!

        startPoint = Point(cell.left + ((cell.right - cell.left) / 2), cell1.top + ((cell1.bottom - cell1.top) / 2))

        cell = MetroUtil.getCell(LocaleController.getString("shahed", R.string.shahed), LocaleController.getString("tajrish", R.string.tajrish))!!
        endPoint = Point(AndroidUtilities.dp(15F), cell.top + ((cell.bottom - cell.top) / 2))
    }

}
