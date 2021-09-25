package ui.lines

import ui.activities.LaunchActivity
import android.content.Context
import android.graphics.Color
import ui.cells.sataionCells.StationCell
import ui.cells.StationLine
import support.LayoutHelper
import ui.cells.sataionCells.SingleStationCell
import utils.MetroUtil

/**
* Created by yaya-mh on 12/08/2018 02:55 PM
*/
class GuideLine: MetroLine {
    companion object {
        // this sentence create a new JVM signature for default getter
        @get:JvmName("getInst") private var Instance :GuideLine ?= null
        fun  getInstance(): GuideLine{
            if (Instance ==  null){
                synchronized(GuideLine::class){
                    if (Instance == null){
                        Instance = GuideLine(LaunchActivity.applicationContext)
                    }
                }
            }
            return Instance as GuideLine
        }
    }
    init {
        stationColor = Color.BLACK
        firstStation = true
        lineName = "GuideLine"

        setWillNotDraw(false)
    }
    private constructor(context: Context):super(context)
    constructor(context: Context, cellList: List<StationCell>, lineList:List<StationLine>):this(context){
        init(cellList, lineList)
    }
    fun init(cellList: List<StationCell>, lines:List<StationLine>){
        for (i in 0..childCount-1){
            removeViewAt(i)
        }

        for (line in lines) {
            val tempLine = line.clone()
            tempLine.paint.color = stationColor
            linesList.add(tempLine)
        }
        stationsList.addAll(cellList)
        for (cell in stationsList) {
            val tempCell = SingleStationCell(cell)
            if (tempCell.cx == 0f && tempCell.cy == 0f){
                val stationCell = MetroUtil.getCell(tempCell.stationName, tempCell.lineName)!!
                tempCell.cx = stationCell.cx
                tempCell.cy = stationCell.cy
            }
            tempCell.circlePaint.color = stationColor

            addView(tempCell, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat()))

        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

    }
}