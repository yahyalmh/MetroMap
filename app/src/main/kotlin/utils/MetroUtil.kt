package utils

import ui.activities.LaunchActivity
import ui.cells.sataionCells.SingleStationCell
import ui.cells.sataionCells.StationCell
import ui.cells.StationLine
import ui.lines.MetroLine

/**
 * Created by yaya-mh on 12/08/2018 08:52 AM
 */
class MetroUtil {

    companion object {
        var lines = mutableListOf<MetroLine>()

        fun getMetroLine(lineName: String): MetroLine? {
            var metroLine: MetroLine? = null

            lines.forEach { line ->
                if (line.lineName == lineName) {
                    metroLine = line
                }
            }
            return metroLine
        }

        fun getCell(stationName: String, lineName: String): StationCell? {
            var cell: StationCell? = null
            val stationCell = SingleStationCell(LaunchActivity.applicationContext, stationName, 0)
            val line = getMetroLine(lineName)
            if (line != null && line.stationsList.contains(stationCell)) {
                cell = line.stationsList[line.stationsList.indexOf(stationCell)]
                cell.lineName = lineName
            }
            return cell
        }

        fun getCell(stationName: String): StationCell? {
            var cell: StationCell? = null
            val stationCell = StationCell(LaunchActivity.applicationContext, stationName)

            for (i in 0 until lines.size) {
                if (lines[i].stationsList.contains(stationCell)) {
                    cell = lines[i].stationsList[lines[i].stationsList.indexOf(stationCell)]
                    cell.lineNumber = lines[i].lineNumber
                    break
                }
            }
            return cell
        }


        fun getStationCount(firstStationName: String, secondStationName: String, lineName: String): Int {
            val metroLine = getMetroLine(lineName)!!
            val firstStationCell = SingleStationCell(LaunchActivity.applicationContext, firstStationName)
            val secondStationCell = SingleStationCell(LaunchActivity.applicationContext, secondStationName)
            return Math.abs(metroLine.stationsList.indexOf(firstStationCell) - metroLine.stationsList.indexOf(secondStationCell))
        }

        fun getStations(firstStation: StationCell, secondStation: StationCell, lineName: String): MutableList<StationCell> {
            val metroLine = getMetroLine(lineName)!!
            val firstIndex = Math.min(metroLine.stationsList.indexOf(firstStation), metroLine.stationsList.indexOf(secondStation))
            val lastIndex = Math.max(metroLine.stationsList.indexOf(firstStation), metroLine.stationsList.indexOf(secondStation))

            return metroLine.stationsList.subList(firstIndex, lastIndex + 1)
        }

        fun getLines(firstStation: StationCell, secondStation: StationCell, lineName: String): MutableList<StationLine> {
            val metroLine = MetroUtil.getMetroLine(lineName)!!
            val firstIndex = Math.min(metroLine.stationsList.indexOf(firstStation), metroLine.stationsList.indexOf(secondStation))
            val lastIndex = Math.max(metroLine.stationsList.indexOf(firstStation), metroLine.stationsList.indexOf(secondStation))

            return metroLine.linesList.subList(firstIndex, lastIndex)
        }
        /* fun copyDatabasesFromAssets() {
             val dbFile = File(Constants.DB_PATH)
             if (!dbFile.exists()){
                 dbFile.mkdir()
             }

             val inputDB = LaunchActivity.applicationContext.assets.open(Constants.DB_NAME)

             val newDBFile = File(Constants.DB_PATH + File.separator + Constants.DB_NAME)
             if(!newDBFile.exists()){
                 newDBFile.createNewFile()
             }
             val outputDB = FileOutputStream(newDBFile)
             val mBuffer = ByteArray(1024)
             var mLength: Int

             mLength = inputDB.read(mBuffer)
             while (mLength > 0) {
                 outputDB.write(mBuffer, 0, mLength)
                 mLength = inputDB.read(mBuffer)
             }
             outputDB.flush()
             outputDB.close()
             inputDB.close()

         }*/
    }

}