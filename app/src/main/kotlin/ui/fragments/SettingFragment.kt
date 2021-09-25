package ui.fragments

import RecyclerItemClickListener
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yaya.map.R
import support.actionbar.ActionBar
import support.actionbar.BaseFragment
import support.LayoutHelper
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import ui.activities.LaunchActivity
import ui.cells.settingCell.SettingBaseCell
import ui.cells.settingCell.SubSettingCell


class SettingFragment : BaseFragment(), RecyclerItemClickListener.OnItemClickListener {
    private var settingAdapter = SettingAdapter()
    private lateinit var listView: RecyclerView
    private lateinit var mContext: Context
    var rowCount = 0
    val metroMapRow = rowCount++
    val brtMapRow = rowCount++

    override fun onFragmentCreate(): Boolean {
         super.onFragmentCreate()
        return true
    }
    override fun createView(context: Context?): View? {

        actionBar!!.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue))
        actionBar!!.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false)
        actionBar!!.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false)
        actionBar!!.setBackButtonImage(R.drawable.ic_ab_back)
        actionBar!!.title = LocaleController.getString("action_settings", R.string.settings)
        actionBar!!.setAllowOverlayTitle(true)

        val extraHeight = 88
        if (AndroidUtilities.isTablet()) {
            actionBar!!.setOccupyStatusBar(false)
        }
        actionBar!!.actionBarMenuOnItemClick = (object : ActionBar.Companion.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
//                    presentFragment(MapFragment())
                    finishFragment()
                }
            }
        })

        mContext = context!!
        val contentView = FrameLayout(LaunchActivity.applicationContext)


//        contentView.addView(actionBar)
        listView = RecyclerView(context)
        listView.adapter = settingAdapter
        listView.layoutManager = LinearLayoutManager(context)
        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(),Gravity.START,0f,0f,0f,0f))
        settingAdapter.notifyDataSetChanged()
        listView.addOnItemTouchListener(RecyclerItemClickListener(context, listView, this@SettingFragment))
        fragmentView = contentView
        return fragmentView
    }

    override fun onItemClick(view: View?, position: Int) {
        when (position) {

            metroMapRow -> {
                val metroCell = view as SubSettingCell
                metroCell.setChecked(checked = true, animated = true)
                val brtCell = listView.findViewHolderForAdapterPosition(brtMapRow)!!.itemView as SubSettingCell
                brtCell.setChecked(checked = false, animated = true)
                LaunchActivity.sharedPreferences!!.edit().putString("mapType", "metro").apply()
            }
            brtMapRow -> {
                val subCell = view as SubSettingCell
                val subSettingCell = listView.findViewHolderForAdapterPosition(metroMapRow)!!.itemView as SubSettingCell
//                subCell.setChecked(checked = true, animated = true)
//                subSettingCell.setChecked(checked = false, animated = true)
//                LaunchActivity.sharedPreferences!!.edit().putString("mapType", "brt").apply()
                Toast.makeText(mContext, "Sorry BRT map no implemented.", Toast.LENGTH_SHORT).show()
            }
        }
        finishFragment(true)
    }

    override fun onLongItemClick(view: View?, position: Int) {
    }

    inner class SettingAdapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            lateinit var settingCell: SettingBaseCell
            when (viewType) {
                2 -> settingCell = SubSettingCell(LaunchActivity.applicationContext)
            }
            return Holder(settingCell)

        }

        override fun getItemCount(): Int {
            return rowCount
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            when (position) {

                metroMapRow -> {
                    val settingSubCell = holder.itemView as SubSettingCell
                    settingSubCell.checkBox.setChecked(checked = true, animated = true)
                    settingSubCell.textView.text = LocaleController.getString("metro", R.string.metro)
                }
                brtMapRow -> {
                    val settingSubCell = holder.itemView as SubSettingCell
                    settingSubCell.textView.text =LocaleController.getString("brt", R.string.brt)
                }

            }
        }

        override fun getItemViewType(position: Int): Int {
            when (position) {
                 metroMapRow, brtMapRow -> return 2
            }
            return 0
        }
    }


    class Holder(settingBaseCell: SettingBaseCell) : RecyclerView.ViewHolder(settingBaseCell)
}
