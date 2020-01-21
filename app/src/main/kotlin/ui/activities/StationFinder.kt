package ui.activities

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.yaya.map.R
import ui.cells.sataionCells.SingleStationCell
import ui.cells.sataionCells.StationCell
import ui.cells.StationLine
import ui.lines.*
import org.json.JSONException
import org.json.JSONObject
import support.component.AndroidUtilities
import support.LayoutHelper
import support.LocaleController
import support.Theme
import ui.fragments.MapFragment
import utils.MetroUtil
import ui.views.ZoomLayout
import java.io.IOException
import java.nio.charset.Charset

/**
* Created by yaya-mh on 12/08/2018 09:57 AM
*/
class StationFinder(frameLayout: ZoomLayout) : MapFragment.MapSearchListener{

    private lateinit var guideLine : GuideLine
    private var frameLayout = frameLayout
    private var listView  = ListView(LaunchActivity.applicationContext)
    private val GuideTextview =TextView(LaunchActivity.applicationContext)

    private val cellListKey = "station"
    private val cellLineKey = "line"
    private val otherViewCount = 2
    private val guideLineIndex = frameLayout.childCount - otherViewCount

    init {
        /*guideLine = GuideLine.getInstance()
        addToLayout(frameLayout)*/
        listView.isVerticalScrollBarEnabled = true
//        listView.itemAnimator = null
//        listView.setInstantClick(true)
        listView.layoutAnimation = null
        listView.tag = 4
       /* val layoutManager = object : LinearLayoutManager(LaunchActivity.applicationContext) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }*/
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        listView.layoutManager = layoutManager
//        listView.adapter = MapSearchAdapter(LaunchActivity.applicationContext)
//        listView.verticalScrollbarPosition = if (isRTL) RecyclerListView.SCROLLBAR_POSITION_LEFT else RecyclerListView.SCROLLBAR_POSITION_RIGHT
//        listView.setBackgroundColor(Color.BLACK)
        /* frameLayout.addView(listView, support.LayoutHelper.createFrame(support.LayoutHelper.WRAP_CONTENT, support.component.AndroidUtilities.dp(30f).toFloat(), Gravity.BOTTOM,
                 0f, 0f, 0f,
                 MapActivity.infoHeight / support.component.AndroidUtilities.density + support.component.AndroidUtilities.dp(5f).toFloat()))*/

        GuideTextview.setBackgroundColor(Theme.getColor(Theme.key_contacts_inviteBackground))
        GuideTextview.setTextColor(Color.WHITE)
        GuideTextview.setSingleLine()
        GuideTextview.gravity =Gravity.CENTER
        GuideTextview.visibility = View.GONE
        frameLayout.addView(GuideTextview, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.BOTTOM or Gravity.CENTER,
                0f, 0f, 0f,
                MapFragment.infoHeight / AndroidUtilities.density + AndroidUtilities.dp(7f).toFloat()))
    }

    fun findRoad(sourceCell: StationCell, destCell: StationCell){

       if (sourceCell == destCell){
            Toast.makeText(LaunchActivity.applicationContext, "you are here", Toast.LENGTH_SHORT).show()
        }

        val startMetroLine = MetroUtil.getMetroLine(sourceCell.lineName)!!
        val endMetroLine = MetroUtil.getMetroLine(destCell.lineName)!!
        when (startMetroLine) {
            endMetroLine -> {
                val cellList = MetroUtil.getStations(sourceCell, destCell, startMetroLine.lineName)
                val lineList = MetroUtil.getLines(sourceCell, destCell, startMetroLine.lineName)
                guideLine = GuideLine(LaunchActivity.applicationContext, cellList, lineList)
                addToLayout(frameLayout)
            }
            else -> {
                val shortestPathMap = getShortestPath(startMetroLine, sourceCell, destCell, endMetroLine)
                guideLine = GuideLine(LaunchActivity.applicationContext, shortestPathMap[cellListKey] as List<StationCell>, shortestPathMap[cellLineKey] as List<StationLine>)
                addToLayout(frameLayout)
            }
        }
    }

    private fun getShortestPath(startMetroLine: MetroLine, startStationCell: StationCell, endStationCell: StationCell, endMetroLine: MetroLine?): HashMap<String, List<*>> {
        var minLength = Int.MAX_VALUE
//        val preDefPathList = loadPathsFromAsset(startMetroLine.lineName, endMetroLine!!.lineName)
        val preDefPathList = PrePaths.getInstance().getPrePath(startMetroLine.lineName, endMetroLine!!.lineName)
        val finalMap = hashMapOf<String, List<*>>()
        var resultListCells = mutableListOf<StationCell>()
        var resultListLines = mutableListOf<StationLine>()

        preDefPathList.forEach{path->

            var stationCount = 0
            resultListCells = mutableListOf()
            resultListLines = mutableListOf()
            var tempStationCell = startStationCell
            var tempMetroLine = startMetroLine


            path.forEach{stationName->

                getLineStations(tempStationCell,MetroUtil.getCell(stationName, tempStationCell.lineName)!!, resultListCells, resultListLines)
                stationCount += MetroUtil.getStationCount(tempStationCell.stationName, stationName, tempMetroLine.lineName)

                tempStationCell = SingleStationCell(LaunchActivity.applicationContext, stationName)

                if (tempMetroLine.crossRoadsName.containsKey(stationName)) {
                    tempStationCell.lineName = tempMetroLine.crossRoadsName[stationName]!!
                } else {
                    tempStationCell.lineName = MetroUtil.getCell(stationName)!!.lineName
                }
                tempMetroLine = MetroUtil.getMetroLine(tempStationCell.lineName)!!
            }

            stationCount += MetroUtil.getStationCount(tempStationCell.stationName, endStationCell.stationName, endMetroLine.lineName)

            if (stationCount < minLength) {
                minLength = stationCount
                getLineStations(MetroUtil.getCell(endStationCell.stationName, endMetroLine.lineName)!!, tempStationCell, resultListCells, resultListLines)

                finalMap[cellListKey] = resultListCells.toList()
                finalMap[cellLineKey] = resultListLines.toList()
            }
        }
        return finalMap
    }


    private fun getLineStations(firstStationCell: StationCell, secondStationCell: StationCell, resultListCells: MutableList<StationCell>, resultListLines: MutableList<StationLine>)  {

        MetroUtil.getStations(firstStationCell, secondStationCell, firstStationCell.lineName).forEach { station ->
            if (!resultListCells.contains(station)) resultListCells.add(SingleStationCell(station)/*station.clone()*/)
        }
        MetroUtil.getLines(firstStationCell, secondStationCell, firstStationCell.lineName).forEach { line ->
            if (!resultListLines.contains(line)) resultListLines.add(/*StationLine(line)*/line.clone())
        }
    }

    private fun addToLayout(frameLayout: ZoomLayout) {
        if(frameLayout.getChildAt(guideLineIndex) is GuideLine) {
            frameLayout.removeViewAt(guideLineIndex)
        }
        frameLayout.addView(guideLine, guideLineIndex, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER))
        frameLayout.applyScaleAndTranslation()
        AndroidUtilities.hideKeyboard(frameLayout)
        frameLayout.invalidate()
    }

    fun removeGuideLine(){
        if(frameLayout.getChildAt(guideLineIndex) is GuideLine) {
            frameLayout.removeViewAt(guideLineIndex)
        }
    }

    private fun loadPathsFromAsset(startMetroLineName : String, endMetroLineName : String): MutableList<MutableList<String>> {
        var json: String? = null
        val jsonObject: JSONObject
        var needReverse = false

        try {
            val inputStream = LaunchActivity.applicationContext.assets.open("{\n\n  \"Tajrish_Farhangsara\":{\n    \"path_1\":[\"Emam khomeini\"],\n    \"path_2\":[\"Shemiran\", \"Dowlat\"],\n    \"path_3\":[\"Emam hosein\", \"Haftom tir\"],\n    \"path_4\":[\"Shademan\", \"Teatr shahr\",\"Beheshti\"],\n    \"path_5\":[\"Shademan\", \"Dowlat\"]\n  },\n\n  \"Tajrish_Azadegan\":{\n    \"path_1\":[\"Beheshti\"],\n    \"path_2\":[\"Dowlat\", \"Teatr shahr\"],\n    \"path_3\":[\"Mohammadie\", \"Mahdie\"]\n  },\n\n  \"Tajrish_Kolahdooz\":{\n    \"path_1\":[\"Dowlat\"],\n    \"path_2\":[\"Beheshti\", \"Teatr shahr\"],\n    \"path_3\":[\"Emam khomeini\", \"Shademan\"]\n  },\n\n  \"Tajrish_Abdolazim\":{\n    \"path_1\":[\"Haftom tir\"],\n    \"path_2\":[\"Dowlat\", \"Shemiran\", \"Emam hosein\"],\n    \"path_3\":[\"Mohammadie\", \"Hefdahe shahrivr\"],\n    \"path_4\":[\"Beheshti\", \"Vali asr\"],\n    \"path_5\":[\"Emam khomeini\", \"Emam hosein\"]\n  },\n\n  \"Tajrish_Takhti\":{\n    \"path_1\":[\"Mohammadie\"],\n    \"path_2\":[\"Haftom tir\", \"Tarbiat modares\"],\n    \"path_3\":[\"Dowlat\", \"Tohid\"],\n    \"path_4\":[\"Emam khomeini\", \"Safavi\"]\n  },\n\n  \"Tajrish_TajrishSub\":{\n    \"path_1\":[ \"Shahed\"]\n  },\n\n  \"Tajrish_FarhangsaraSub\":{\n    \"path_1\":[ \"Emam khomeini\", \"Sadeghye\"]\n  },\n\n  \"Tajrish_AzadeganSub\":{\n    \"path_1\":[ \"Beheshti\", \"No bonyad\"]\n  },\n\n  \"Tajrish_KolahdoozSub\":{\n    \"path_1\":[ \"Dowlat\", \"Bime\"]\n  },\n\n  \"Farhangsara_Azadegan\":{\n    \"path_1\":[\"Emam khomeini\", \"Beheshti\"],\n    \"path_2\":[\"Shemiran\", \"Teatr shahr\"],\n    \"path_3\":[\"Shademan\", \"Teatr shahr\"],\n    \"path_4\":[\"Emam hosein\", \"Vali asr\"],\n    \"path_5\":[\"Shemiran\", \"Dowlat\", \"Beheshti\"],\n    \"path_6\":[\"Emam khomeini\", \"Dowlat\", \"Teatr shahr\"],\n    \"path_7\":[\"Emam hosein\",  \"Haftom tir\",\"Beheshti\" ]\n  },\n\n  \"Farhangsara_Kolahdooz\": {\n    \"path_1\":[\"Shemiran\"],\n    \"path_2\":[\"Shademan\"],\n    \"path_3\":[\"Emam khomeini\", \"Dowlat\"],\n    \"path_4\":[\"Safavi\", \"Tohid\"],\n    \"path_5\":[\"Sadeghye\", \"Eram sabze\"],\n    \"path_6\":[\"Emam hosein\", \"Meydan shohada\"]\n  },\n\n  \"Farhangsara_FarhangsaraSub\": {\n    \"path_1\":[\"Sadeghye\"]\n  },\n\n  \"Farhangsara_TajrishSub\": {\n    \"path_1\":[\"Emam khomeini\", \"Shahed\"]\n  },\n\n  \"Farhangsara_AzadeganSub\": {\n    \"path_1\":[\"Emam khomeini\", \"Beheshti\", \"No bonyad\"],\n    \"path_2\":[\"Shademan\", \"Teatr shahr\", \"No bonyad\"],\n    \"path_3\":[\"Shemiran\", \"Teatr shahr\", \"No bonyad\"],\n    \"path_4\":[\"Emam hosein\", \"Vali asr\", \"No bonyad\"]\n  },\n\n  \"Farhangsara_KolahdoozSub\":{\n    \"path_1\":[\"Shademan\", \"Bime\"],\n    \"path_2\":[\"Shemiran\", \"Bime\"],\n    \"path_3\":[\"Sadeghye\", \"Eram sabze\", \"Bime\"],\n    \"path_4\":[\"Emam khomeini\", \"Dowlat\", \"Bime\"],\n    \"path_5\":[\"Safavi\", \"Tohid\", \"Bime\"]\n  },\n  \"Farhangsara_Takhti\":{\n    \"path_1\":[\"Safavi\"],\n    \"path_2\":[\"Emam hosein\", \"Hefdahe shahrivr\"],\n    \"path_3\":[\"Emam khomeini\", \"Mohammadie\"]\n  },\n\n  \"Farhangsara_Abdolazim\":{\n    \"path_1\":[\"Emam hosein\"],\n    \"path_2\":[\"Emam khomeini\", \"Haftom tir\"],\n    \"path_3\":[\"Safavi\", \"Tarbiat modares\"]\n  },\n\n\n  \"Azadegan_Kolahdooz\":{\n    \"path_1\":[\"Teatr shahr\"],\n    \"path_2\":[\"Teatr shahr\"],\n    \"path_3\":[\"Beheshti\", \"Dowlat\"]\n  },\n\n  \"Azadegan_Abdolazim\":{\n    \"path_1\":[\"Vali asr\"],\n    \"path_2\":[\"Beheshti\", \"Haftom tir\"],\n    \"path_3\":[\"Mahdie\", \"Hefdahe shahrivr\"],\n    \"path_4\":[\"Teatr shahr\", \"Meydan shohada\"]\n  },\n\n  \"Azadegan_Takhti\":{\n    \"path_1\":[\"Mahdie\"],\n    \"path_2\":[\"Teatr shahr\", \"Tohid\"],\n    \"path_3\":[\"Vali asr\", \"Tarbiat modares\"]\n  },\n\n  \"Azadegan_AzadeganSub\":{\n    \"path_1\":[\"No bonyad\"]\n  },\n\n  \"Azadegan_TajrishSub\":{\n    \"path_1\":[\"Beheshti\", \"Shahed\"]\n  },\n\n  \"Azadegan_FarhangsaraSub\":{\n    \"path_1\":[\"Teatr shahr\", \"Shademan\", \"Sadeghye\"],\n    \"path_2\":[\"Mahdie\", \"Safavi\", \"Sadeghye\"]\n  },\n\n  \"Azadegan_KolahdoozSub\":{\n    \"path_1\":[\"Teatr shahr\", \"Bime\"]\n  },\n\n  \"Abdolazim_FarhangsaraSub\":{\n    \"path_1\":[\"Meydan shohada\", \"Shemiran\", \"Sadeghye\"],\n    \"path_2\":[\"Meydan shohada\", \"Emam hosein\", \"Sadeghye\"]\n  },\n\n  \"Abdolazim_Takhti\":{\n    \"path_1\":[\"Hefdahe shahrivr\"],\n    \"path_2\":[\"Tarbiat modares\"],\n    \"path_3\":[\"Emam hosein\", \"Safavi\"],\n    \"path_4\":[\"Shemiran\", \"Tohid\"],\n    \"path_5\":[\"Shohada\", \"Tohid\"],\n    \"path_6\":[\"Vali ast\", \"Mahdie\"],\n    \"path_7\":[\"Haftom tir\", \"Mohammadie\"]\n  },\n\n  \"Abdolazim_TajrishSub\":{\n    \"path_1\":[\"Haftom tir\",\"Shahed\"],\n    \"path_3\":[\"Hefdahe shahrivr\", \"Mohammadie\", \"Shahed\"],\n    \"path_4\":[\"Vali asr\", \"Mahdie\",\"Mohammadie\", \"Shahed\"]\n  },\n\n  \"Abdolazim_AzadeganSub\":{\n    \"path_1\":[\"Emam hosein\",\"Haftom tir\", \"Beheshti\", \"No bonyad\"],\n    \"path_2\":[\"Meydan shohada\",\"Dowlat\", \"Beheshti\", \"No bonyad\"],\n    \"path_3\":[\"Vali asr\", \"No bonyad\"]\n  },\n  \"Abdolazim_KolahdoozSub\":{\n    \"path_1\":[\"Meydan shohada\",\"Bime\"],\n    \"path_2\":[\"Tarbiat modares\",\"Tohid\", \"Bime\"],\n    \"path_3\":[\"Haftom tir\", \"Dowlat\", \"Bime\"],\n    \"path_4\":[\"Bime\", \"Shademan\", \"Bime\"],\n    \"path_5\":[\"Vali asr\", \"Teatr shahr\", \"Bime\"]\n  },\n\n  \"Takhti_TajrishSub\":{\n    \"path_1\":[\"Mohammadie\",\"Shahed\"],\n    \"path_2\":[\"Safavi\",\"Emam khomeini\", \"Shahed\"]\n  },\n\n  \"Takhti_FarhangsaraSub\":{\n    \"path_1\":[\"Safavi\", \"Sadeghye\"],\n    \"path_2\":[\"Shohada\",\"Shemiran\", \"Sadeghye\"],\n    \"path_3\":[\"Tarbiat modares\",\"Safavi\",\"Sadeghye\"],\n    \"path_4\":[\"Hefdahe shahrivr\",\"Emam hosein\", \"Safavi\",\"Sadeghye\"],\n    \"path_5\":[\"Mahdie\",\"Teatr shahr\", \"Shademan\",\"Sadeghye\"]\n  },\n\n  \"Takhti_AzadeganSub\":{\n    \"path_1\":[\"Tarbiat modares\",\"Vali asr\",\"Beheshti\", \"No bonyad\"],\n    \"path_2\":[\"Mohammadie\",\"Beheshti\", \"No bonyad\"],\n    \"path_3\":[\"Hefdahe shahrivr\", \"Haftom tir\", \"Beheshti\", \"No bonyad\"]\n  },\n  \"TajrishSub_FarhangsaraSub\":{\n    \"path_1\":[\"Shahed\",\"Emam khomeini\",\"Sadeghye\"],\n    \"path_2\":[\"Shahed\", \"Mohammadie\",\"Safavi\", \"Sadeghye\"]\n  },\n\n  \"TajrishSub_AzadeganSub\":{\n    \"path_1\":[\"Shahed\",\"Beheshti\",\"No bonyad\"]\n  },\n\n  \"TajrishSub_KolahdoozSub\":{\n    \"path_1\":[\"Shahed\",\"Dowlat\",\"Bime\"],\n    \"path_2\":[\"Shahed\", \"Mohammadie\",\"Tohid\",\"Bime\"]\n  },\n\n  \"FarhangsaraSub_KolahdoozSub\":{\n    \"path_1\":[\"Eram sabze\", \"Bime\"]\n  },\n\n  \"FarhangsaraSub_AzadeganSub\":{\n    \"path_1\":[\"Sadeghye\", \"Shademan\", \"No bonyad\"]\n  },\n\n  \"kolahdoozSub_AzadeganSub\":{\n    \"path_1\":[\"Bime\", \"Teatr shahr\", \"No bonyad\"]\n  }\n\n}\n\n\n")
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            print("Json file not exist")
        }

        jsonObject = try {
            JSONObject(json).getJSONObject(startMetroLineName + "_" + endMetroLineName)
        }catch (ex: JSONException){
            needReverse = true
            JSONObject(json).getJSONObject(endMetroLineName + "_" + startMetroLineName)
        }

        val resultList = mutableListOf<MutableList<String>>()
        for (i in 0 until jsonObject.names().length()){
            var temp = mutableListOf<String>()
            val array = jsonObject.getJSONArray(jsonObject.names()[i].toString())
            (0 until array.length()).mapTo(temp){array[it].toString()}
            if (needReverse) temp = temp.asReversed()
            resultList.add(temp)
        }
        return resultList
    }

    override fun onTextChange(editText: EditText) {
        var temp =""
        if (editText.text.toString() == ""){
            GuideTextview.visibility = View.GONE
            return
        }

        MetroUtil.lines.forEach{line->
            line.stationsNames.forEach{name->
                if (name.toLowerCase().contains(editText.text.toString().toLowerCase())){
                    temp = line.stationsNames[line.stationsNames.indexOf(name)]
                }
            }

        }
        if (temp != ""){
            GuideTextview.text = temp
            GuideTextview.animate().translationY(AndroidUtilities.dp(2.5f).toFloat())
            GuideTextview.visibility = View.VISIBLE
        }else{
            GuideTextview.visibility = View.GONE
        }
        GuideTextview.setOnClickListener{
            editText.setText(GuideTextview.text.toString())
            GuideTextview.visibility = View.GONE
            return@setOnClickListener
        }


    }

}
