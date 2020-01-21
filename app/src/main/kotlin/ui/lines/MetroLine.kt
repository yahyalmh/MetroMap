package ui.lines

import ui.activities.LaunchActivity
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.widget.FrameLayout
import ui.cells.*
import support.component.AndroidUtilities
import support.LayoutHelper
import ui.cells.sataionCells.DoubleStationCell
import ui.cells.sataionCells.SingleStationCell
import ui.cells.sataionCells.StationCell
import utils.MetroUtil

/**
* Created by yaya-mh on 23/07/2018 09:00 AM
*/
abstract class MetroLine: FrameLayout{

    var startStation: String = ""
    var endStation: String = ""
    var lineName :String = ""
    var stationColor : Int = 0
    var viewWidth : Int = 0
    var viewHeight : Int = 0
    var lineNumber : Int = -1

    val lat = 35.7368277

    var firstStation = true
    var isLineDrawFinished = false

    var stationDistancePoint = Point(0,0)
    var startPoint = Point(0,0)
    lateinit var endPoint : Point

    var crossRoadsName = mapOf<String, String>()
    var stationsNames = listOf<String>()
    var stationsList = mutableListOf<StationCell>()
    var linesList = mutableListOf<StationLine>()
    private val  crossroadsCells  = mutableListOf<StationCell>()

    constructor(context: Context):super(context){
//        loadDataFromDB()
    }

    private fun loadDataFromDB() {
//        android.os.Debug.waitForDebugger()
//
//        val cursor = MapActivity.database.queryFinalized("SELECT * FROM " + Constants.LINE_TABLE_NAME + " WHERE lineName = '" + LocaleController.getString("tajrish", R.string.tajrish) + "'")
//        cursor.next()
//        do {
//            lineNumber = cursor.intValue(1)
//            startStation = cursor.stringValue(2)
//            endStation = cursor.stringValue(3)
//            stationColor = cursor.intValue(4)
//        }while (cursor.next())
    }

    protected open fun addStations() {
        stationsNames.forEach{name->
            val stationCell = if(crossRoadsName.containsKey(name)){
                DoubleStationCell(context, name, lineName, crossRoadsName[name]!!)
            }else {
                SingleStationCell(context, name, stationColor)
            }
            stationCell.firstStation = when(stationsNames.indexOf(name)){0->true  else-> false}
            stationsList.add(stationCell)
            if(crossRoadsName.containsKey(name)){
                val metroLineName = crossRoadsName[name]!!
                val metroLine = MetroUtil.getMetroLine(metroLineName)
                if (metroLine == null || !(metroLine.stationsList.contains(stationCell))){
                    addView(stationCell, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat()))
                }
            }else{
                addView(stationCell, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat()))
            }

            if (stationsNames.indexOf(name)!=0 ){
                val stationLine = StationLine(stationColor)
                linesList.add(stationLine)
            }

        }
    }

    open fun changeDirection(stationName: String){}
    fun drawLine(subList: List<String>) {
        val builder = StationCell.Builder()
        builder.context(LaunchActivity.applicationContext).radius(AndroidUtilities.dp(3f).toFloat()).left(12)
        val stationCell = builder.build()
        val d : String = ""

        for (element in subList) {
            drawStation(element)
        }
    }
    open fun setStartEndPoint(){}

    open fun setStationDistancePoint(subList: List<String>){
        val size = if(subList.size - 1 == 0 ) 1 else  subList.size
        stationDistancePoint.x = -(startPoint.x - endPoint.x) / size
    }

    private fun drawStation(stationName: String) {
        val stationCell : StationCell?
        val stationLine : StationLine?
        changeDirection(stationName)
        when {
            crossRoadsName.keys.contains(stationName)  -> {
                val metroLine = MetroUtil.getMetroLine(crossRoadsName[stationName]!!)
                if (metroLine == null  || metroLine.lineNumber > lineNumber){

                    stationLine = linesList[stationsList.indexOf(SingleStationCell(context, stationName))-1]
                    stationLine.left = startPoint.x
                    stationLine.top = startPoint.y
                    stationLine.right = startPoint.x + stationDistancePoint.x
                    stationLine.bottom = startPoint.y + stationDistancePoint.y

                    startPoint.x += stationDistancePoint.x
                    startPoint.y += stationDistancePoint.y

                    stationCell = stationsList[stationsList.indexOf(DoubleStationCell(context, stationName))]
                    stationCell.lineName = lineName
                    val l = startPoint.x - stationCell.measuredWidth/2
                    val t = startPoint.y - stationCell.measuredHeight/2
                    stationCell.layout(l,t, l+stationCell.measuredWidth, t+stationCell.measuredHeight)


                }else {
                    val crossCell = metroLine.stationsList[metroLine.stationsList.indexOf(DoubleStationCell(context, stationName))]
                    val x = crossCell.left + ((crossCell.right - crossCell.left) / 2)
                    val y = crossCell.top + ((crossCell.bottom - crossCell.top) / 2)

                    val indexOfCurrentCell = stationsList.indexOf(DoubleStationCell(context, stationName))
                    stationCell = stationsList[indexOfCurrentCell]
                    stationCell.layout(crossCell.left, crossCell.top, crossCell.right, crossCell.bottom)
                    if(!stationCell.firstStation){
                        //first station don't need change start point and dist point value
                        stationLine = linesList[indexOfCurrentCell -1]
                        if (stationLine.left == 0 && stationLine.top ==0){
                            stationLine.left = startPoint.x
                            stationLine.top = startPoint.y
                            stationLine.right = x
                            stationLine.bottom = y
                        }
                        startPoint.x = x
                        startPoint.y = y
                    }
                }
                if (!crossroadsCells.contains(stationCell)) crossroadsCells.add(stationCell)
                firstStation = false
            }
            firstStation -> {
                stationCell = stationsList[stationsList.indexOf(SingleStationCell(context, stationName))]
                stationCell.lineName = lineName
                val l = startPoint.x - stationCell.measuredWidth/2
                val t = startPoint.y - stationCell.measuredHeight/2
                stationCell.layout(l,t, l+stationCell.measuredWidth, t+stationCell.measuredHeight)
                firstStation = false
            }
            else -> {
                stationLine = linesList[stationsList.indexOf(SingleStationCell(context, stationName))-1]
                stationLine.left = startPoint.x
                stationLine.top = startPoint.y
                stationLine.right = startPoint.x + stationDistancePoint.x
                stationLine.bottom = startPoint.y + stationDistancePoint.y
//                stationLine.layout(startPoint.x, startPoint.y, startPoint.x+stationDistancePoint.x, startPoint.y + stationDistancePoint.y)

                startPoint.x += stationDistancePoint.x
                startPoint.y += stationDistancePoint.y

                stationCell =stationsList[stationsList.indexOf(SingleStationCell(context, stationName))]
                stationCell.lineName = lineName
                val l = startPoint.x - stationCell.measuredWidth/2
                val t = startPoint.y - stationCell.measuredHeight/2
                stationCell.layout(l,t, l+stationCell.measuredWidth, t+stationCell.measuredHeight)

            }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        viewWidth = right - left
        viewHeight = bottom - top - AndroidUtilities.dp(60F)

        firstStation = true
        stationDistancePoint = Point(0, 0)
        setStartEndPoint()

        var subList : List<String>
        var firstIndex = 0

        for (crossroadName in crossRoadsName){
            val metroLine = MetroUtil.getMetroLine(crossroadName.value)
            if (metroLine!=null && metroLine.isLineDrawFinished && metroLine.lineNumber < lineNumber) {
                subList = stationsNames.subList(firstIndex, stationsNames.indexOf(crossroadName.key) + 1)
                val size = if(subList.size - 1 == 0 ) 1 else  subList.size-1
                val cell = MetroUtil.getCell(crossroadName.key, crossroadName.value)!!
                stationDistancePoint.x = -(startPoint.x - (cell.left + ((cell.right - cell.left)/2))) / size
                firstIndex = stationsNames.indexOf(crossroadName.key)
                drawLine( subList)
            }
        }

        if (firstIndex != stationsNames.size){
            if (firstStation)firstIndex  = -1  //this line haven't crossCell like tajrish line
            subList = stationsNames.subList(firstIndex + 1, stationsNames.size)

            setStationDistancePoint(subList)
            drawLine( subList)
        }
        isLineDrawFinished = true
    }

    override fun onDraw(canvas: Canvas?) {
        linesList.forEach { line->
            line.draw(canvas)
        }
    }
    fun goToCell(firstCellName: String, secondCellName: String, targetCellName:String, xExtraCell: Int, yExtraCell: Int): Point {
        val targetCell = MetroUtil.getCell(targetCellName)!!
        val point = Point(0,0)
        val stationNumber = Math.abs(stationsNames.indexOf(firstCellName) - stationsNames.indexOf(secondCellName))

        val cx = Math.abs(targetCell.left + ((targetCell.right - targetCell.left) / 2))
        val cy = Math.abs(targetCell.top + ((targetCell.bottom - targetCell.top) / 2))

        point.x = Math.abs(startPoint.x - cx - ((xExtraCell) * stationDistancePoint.x)) / stationNumber
        point.y = Math.abs(startPoint.y - cy - ((yExtraCell) * stationDistancePoint.y)) / stationNumber
        return point
    }
    fun goToCell(firstCellName: String, secondCellName: String, targetCellName:String, xExtraCell: Int): Point {
        return goToCell(firstCellName, secondCellName, targetCellName, xExtraCell, 0)
    }
    fun goToCell(firstCellName: String, secondCellName: String, targetCellName:String): Point {
        return goToCell(firstCellName, secondCellName, targetCellName, 0, 0)
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MetroLine) return false

        if (startStation != other.startStation) return false
        if (endStation != other.endStation) return false
        if (lineNumber != other.lineNumber) return false
        if (startPoint != other.startPoint) return false
        if (crossroadsCells != other.crossroadsCells) return false
        if (linesList != other.linesList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startStation.hashCode()
        result = 31 * result + endStation.hashCode()
        result = 31 * result + lineNumber
        result = 31 * result + startPoint.hashCode()
        return result
    }

    override fun toString(): String {
        return "MetroLine( lineNumber=$lineNumber, crossRoadsName=$crossRoadsName, stationsList=$stationsList, crossroadsCells=$crossroadsCells, linesList=$linesList)"
    }

}