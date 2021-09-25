package ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yaya.map.R
import support.LocaleController
import support.Theme
import support.component.AndroidUtilities
import ui.cells.tellCell.DividerCell
import ui.cells.tellCell.DrawerActionCell
import ui.cells.tellCell.DrawerProfileCell
import ui.cells.tellCell.EmptyCell
import java.util.*

class DrawerLayoutAdapter(private val mContext: Context) : RecyclerView.Adapter<DrawerLayoutAdapter.Holder>() {
    private val items = ArrayList<Item?>(11)
    val itemCount: Int? =null


    fun isEnabled(holder: Holder): Boolean {
        return holder.getItemViewType() === 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View
        when (viewType) {
            0 -> view = DrawerProfileCell(mContext)
            1 -> view = EmptyCell(mContext, AndroidUtilities.dp(8f))
            2 -> view = DividerCell(mContext)
            3 -> view = DrawerActionCell(mContext)
            else -> view = EmptyCell(mContext, AndroidUtilities.dp(8f))
        }
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (holder.getItemViewType()) {
            0 -> {
                (holder.itemView as DrawerProfileCell).setUser(LocaleController.getString("app_name", R.string.app_name))
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue))
            }
            3 -> {
                val actionCell = holder.itemView as DrawerActionCell
                items[position]!!.bind(actionCell)
            }
        }
    }

    override fun getItemViewType(i: Int): Int {
        if (i == 0) {
            return 0
        } else if (i == 1) {
            return 1
        } else if (i == 5) {
            return 2
        }
        return 3
    }

    override fun getItemCount(): Int {
        return items.size
    }

    public fun resetItems() {
        items.clear()
        items.add(null) // profile
        items.add(null) // padding
//        items.add(Item(2, LocaleController.getString("action_settings", R.string.settings), R.drawable.ic_setting))
        items.add(Item(2, LocaleController.getString("language", R.string.language), R.drawable.ic_language))
        items.add(Item(3, LocaleController.getString("map", R.string.map), R.drawable.ic_map))
    }

    fun getId(position: Int): Int {
        if (position < 0 || position >= items.size) {
            return -1
        }
        val item = items[position]
        return item?.id ?: -1
    }

    private inner class Item(var id: Int, var text: String, var icon: Int) {
        fun bind(actionCell: DrawerActionCell) {
            actionCell.setTextAndIcon(text, icon)
        }

    }

    init {
//        Theme.createDialogsResources(mContext)
        resetItems()
    }
    class Holder(settingBaseCell: View) : RecyclerView.ViewHolder(settingBaseCell)

}