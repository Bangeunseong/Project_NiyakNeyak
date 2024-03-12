package com.capstone.project_niyakneyak.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

/**
 * This activity is used for showing fragments which are linked by [MainActivity.navHostFragment].
 * Linked fragments will be controlled by [MainActivity.navController] with
 * BottomNavigationBar(NavigationBar listener is created as ItemSelectionListener)
 */
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null
    private var intent: Intent? = null

    internal inner class ItemSelectionListener : NavigationBarView.OnItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val currentFragmentId = navController!!.currentDestination!!.id
            if (item.itemId == R.id.menu_main) {
                binding!!.toolbar.setTitle(R.string.toolbar_main_title)
                if (currentFragmentId == R.id.alarmListFragment) navController!!.navigate(R.id.action_alarmListFragment_to_mainPageFragment)
                else if (currentFragmentId == R.id.checkListFragment) navController!!.navigate(R.id.action_checkListFragment_to_mainPageFragment)
            } else if (item.itemId == R.id.menu_time) {
                binding!!.toolbar.setTitle(R.string.toolbar_main_timer)
                if (currentFragmentId == R.id.mainPageFragment) navController!!.navigate(R.id.action_mainPageFragment_to_alarmListFragment)
                else if (currentFragmentId == R.id.checkListFragment) navController!!.navigate(R.id.action_checkListFragment_to_alarmListFragment)
            } else if (item.itemId == R.id.menu_time_check) {
                binding!!.toolbar.setTitle(R.string.toolbar_main_checklist)
                if (currentFragmentId == R.id.alarmListFragment) navController!!.navigate(R.id.action_alarmListFragment_to_checkListFragment)
                else if (currentFragmentId == R.id.mainPageFragment) navController!!.navigate(R.id.action_mainPageFragment_to_checkListFragment)
            } else if (item.itemId == R.id.menu_setting) {

            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent = getIntent()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.toolbar.setTitle(R.string.toolbar_main_title)
        setSupportActionBar(binding!!.toolbar)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        assert(navHostFragment != null)
        navController = navHostFragment!!.navController
        binding!!.menuBottomNavigation.setOnItemSelectedListener(ItemSelectionListener())
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(RESULT_OK, intent)
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, R.string.action_menu_logout)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}