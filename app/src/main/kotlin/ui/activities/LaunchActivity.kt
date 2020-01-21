package ui.activities

import Constants
import RecyclerItemClickListener
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import support.ActionBar.ActionBarLayout
import support.ActionBar.BaseFragment
import support.ActionBar.DrawerLayoutContainer
import support.LocaleController
import support.component.AndroidUtilities
import ui.adapters.DrawerLayoutAdapter
import ui.fragments.MapFragment
import ui.fragments.SelectLanguageFragment
import ui.fragments.SettingFragment
import java.util.*


class LaunchActivity : Activity() {
    private lateinit var drawerLayoutContainer: DrawerLayoutContainer
    private lateinit var actionBarLayout: ActionBarLayout
    private lateinit var sideMenu: RecyclerView

    companion object {
        lateinit var applicationContext: Context
        lateinit var applicationHandler: Handler
        var sharedPreferences: SharedPreferences? = null
    }

    private val mainFragmentsStack: ArrayList<BaseFragment>? = ArrayList()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationHandler = Handler(Looper.getMainLooper())
        LaunchActivity.applicationContext = applicationContext
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_FILENAME, Context.MODE_PRIVATE)
        LocaleController.getInstance()
        PrePaths.getInstance()

        actionBarLayout = ActionBarLayout(this)
        actionBarLayout.init(mainFragmentsStack)

        drawerLayoutContainer = DrawerLayoutContainer(this)
        drawerLayoutContainer.setParentActionBarLayout(actionBarLayout)
        actionBarLayout.drawerLayoutContainer = drawerLayoutContainer
        drawerLayoutContainer.addView(actionBarLayout, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        setContentView(drawerLayoutContainer, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))


        sideMenu = RecyclerView(this)
        sideMenu.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        sideMenu.setBackgroundColor(support.Theme.getColor(support.Theme.key_chats_menuBackground))


        val drawerLayoutAdapter = DrawerLayoutAdapter(this)
        sideMenu.adapter = drawerLayoutAdapter
        drawerLayoutContainer.setDrawerLayout(sideMenu)

        val layoutParams = sideMenu.layoutParams as FrameLayout.LayoutParams
        val screenSize = AndroidUtilities.getRealScreenSize()
        layoutParams.width = if (AndroidUtilities.isTablet()) AndroidUtilities.dp(320F) else AndroidUtilities.dp(320F).coerceAtMost(Math.min(screenSize!!.x, screenSize.y) - AndroidUtilities.dp(56F))
        layoutParams.height = support.LayoutHelper.MATCH_PARENT
        sideMenu.layoutParams = layoutParams

        setSideMenuClickListener(drawerLayoutAdapter)
        val mapFragment = MapFragment()
        mapFragment.setSideMenu(sideMenu)
        actionBarLayout.addFragmentToStack(mapFragment)
//        actionBarLayout.presentFragment(mapFragment)
        drawerLayoutContainer.setAllowOpenDrawer(value = true, animated = false)
        actionBarLayout.showLastFragment()
    }

    private fun setSideMenuClickListener(drawerLayoutAdapter: DrawerLayoutAdapter) {
        sideMenu.addOnItemTouchListener(RecyclerItemClickListener(this, sideMenu, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                when (drawerLayoutAdapter.getId(position)) {
                    2 -> {
                        drawerLayoutContainer.closeDrawer(false)
                        actionBarLayout.presentFragment(SelectLanguageFragment())
                    }
                    3 -> {
                        drawerLayoutContainer.closeDrawer(false)
                        actionBarLayout.presentFragment(SettingFragment())
                    }
                }
            }
            override fun onLongItemClick(view: View?, position: Int) {
            }

        }))
    }

    override fun onPause() {
        super.onPause()
        actionBarLayout.onPause()
    }

    override fun onResume() {
        super.onResume()
        actionBarLayout.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        AndroidUtilities.checkDisplaySize(this@LaunchActivity, newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        when {
            drawerLayoutContainer.isDrawerOpened -> {
                drawerLayoutContainer.closeDrawer(false)
            }
            actionBarLayout.fragmentsStack!!.size > 1 -> {
                actionBarLayout.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        actionBarLayout.onLowMemory()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
        }
    }
}
