package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

/**
 * This activity is used for showing fragments which are linked by [MainActivity.navHostFragment].
 * Linked fragments will be controlled by [MainActivity.navController] with
 * BottomNavigationBar(NavigationBar listener is created as ItemSelectionListener)
 */
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var _navHostFragment: NavHostFragment? = null
    private val navHostFragment get() = _navHostFragment!!
    private var _navController: NavController? = null
    private val navController get() = _navController!!

    internal inner class ItemSelectionListener : NavigationBarView.OnItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val currentFragmentId = navController.currentDestination!!.id
            if (item.itemId == R.id.menu_main) {
                binding.toolbar.setTitle(R.string.toolbar_main_title)
                when (currentFragmentId){
                    R.id.alarmListFragment -> navController.navigate(R.id.action_alarmListFragment_to_mainPageFragment)
                    R.id.checkListFragment -> navController.navigate(R.id.action_checkListFragment_to_mainPageFragment)
                    R.id.settingFragment -> navController.navigate(R.id.action_settingFragment_to_mainPageFragment)
                }
            } else if (item.itemId == R.id.menu_time) {
                binding.toolbar.setTitle(R.string.toolbar_main_timer)
                when (currentFragmentId){
                    R.id.mainPageFragment -> navController.navigate(R.id.action_mainPageFragment_to_alarmListFragment)
                    R.id.checkListFragment -> navController.navigate(R.id.action_checkListFragment_to_alarmListFragment)
                    R.id.settingFragment -> navController.navigate(R.id.action_settingFragment_to_alarmListFragment)
                }
            } else if (item.itemId == R.id.menu_time_check) {
                binding.toolbar.setTitle(R.string.toolbar_main_checklist)
                when (currentFragmentId){
                    R.id.mainPageFragment -> navController.navigate(R.id.action_mainPageFragment_to_checkListFragment)
                    R.id.alarmListFragment -> navController.navigate(R.id.action_alarmListFragment_to_checkListFragment)
                    R.id.settingFragment -> navController.navigate(R.id.action_settingFragment_to_checkListFragment)
                }
            } else if (item.itemId == R.id.menu_account) {
                binding.toolbar.setTitle(R.string.toolbar_main_settings)
                when (currentFragmentId){
                    R.id.mainPageFragment -> navController.navigate(R.id.action_mainPageFragment_to_settingFragment)
                    R.id.alarmListFragment -> navController.navigate(R.id.action_alarmListFragment_to_settingFragment)
                    R.id.checkListFragment -> navController.navigate(R.id.action_checkListFragment_to_settingFragment)
                }
            }
            return true
        }
    }

    internal inner class DestinationChangedListener : NavController.OnDestinationChangedListener{
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            when(destination.id){
                R.id.mainPageFragment -> binding.menuBottomNavigation.menu.findItem(R.id.menu_main).isChecked = true
                R.id.alarmListFragment -> binding.menuBottomNavigation.menu.findItem(R.id.menu_time).isChecked = true
                R.id.checkListFragment -> binding.menuBottomNavigation.menu.findItem(R.id.menu_time_check).isChecked = true
                R.id.settingFragment -> binding.menuBottomNavigation.menu.findItem(R.id.menu_account).isChecked = true
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setTitle(R.string.toolbar_main_title)
        setSupportActionBar(binding.toolbar)
        _navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        _navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(DestinationChangedListener())
        binding.menuBottomNavigation.setOnItemSelectedListener(ItemSelectionListener())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}