package ui.fragments

import RecyclerItemClickListener
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import ui.cells.LanguageCell


class SelectLanguageFragment : BaseFragment(), RecyclerItemClickListener.OnItemClickListener {
    private var settingAdapter = SettingAdapter()
    private lateinit var listView: RecyclerView
    private lateinit var mContext: Context
    private val languageList = ArrayList<LocaleController.LocaleInfo>(LocaleController.getInstance().languages)


    override fun onFragmentCreate(): Boolean {
         super.onFragmentCreate()
        return true
    }
    override fun createView(context: Context?): View? {

        actionBar!!.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue))
        actionBar!!.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false)
        actionBar!!.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false)
        actionBar!!.setBackButtonImage(R.drawable.ic_ab_back)
        actionBar!!.title = LocaleController.getString("language", R.string.language)
        actionBar!!.setAllowOverlayTitle(true)

        if (AndroidUtilities.isTablet()) {
            actionBar!!.setOccupyStatusBar(false)
        }
        actionBar!!.actionBarMenuOnItemClick = (object : ActionBar.Companion.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    finishFragment()
                }
            }
        })

        mContext = context!!
        val contentView = FrameLayout(LaunchActivity.applicationContext)

        listView = RecyclerView(context)
        listView.adapter = settingAdapter
        listView.layoutManager = LinearLayoutManager(context)
        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(),Gravity.START,0f,0f,0f,0f))
        settingAdapter.notifyDataSetChanged()
        listView.addOnItemTouchListener(RecyclerItemClickListener(context, listView, this@SelectLanguageFragment))
        fragmentView = contentView
        return fragmentView
    }

    override fun onItemClick(view: View?, position: Int) {
        if (parentActivity == null || parentLayoute == null || view !is LanguageCell) {
            return
        }
        val localeInfo: LocaleController.LocaleInfo? = view.getCurrentLocale()
        if (localeInfo != null) {
            LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true)
            parentLayoute!!.rebuildAllFragmentViews(false, false)
        }
        finishFragment()
    }

    override fun onLongItemClick(view: View?, position: Int) {
    }
 /*   override fun onBackPressed(): Boolean {
//        finishFragment()
        return false
    }*/
    inner class SettingAdapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            lateinit var languageCell: LanguageCell
            when (viewType) {
                1 -> languageCell = LanguageCell(LaunchActivity.applicationContext)
            }
            return Holder(languageCell)

        }

        override fun getItemCount(): Int {
            return languageList.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val languageCell = holder.itemView as LanguageCell
            val localeInfo = languageList[position]
            languageCell.setLanguage(localeInfo,null, true )
            languageCell.setLanguageSelected(localeInfo == LocaleController.getInstance().getCurrentLocaleInfo())
        }

        override fun getItemViewType(position: Int): Int {
            return 1
        }
    }

    class Holder(languageCell: LanguageCell) : RecyclerView.ViewHolder(languageCell)
}
