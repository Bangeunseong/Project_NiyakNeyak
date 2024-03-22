package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

/**
 * This activity is used for showing fragments which are linked by [MainActivity.navHostFragment].
 * Linked fragments will be controlled by [MainActivity.navController] with
 * BottomNavigationBar(NavigationBar listener is created as ItemSelectionListener)
 */
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null

    internal inner class ItemSelectionListener : NavigationBarView.OnItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val currentFragmentId = navController!!.currentDestination!!.id
            if (item.itemId == R.id.menu_main) {
                binding.toolbar.setTitle(R.string.toolbar_main_title)
                if (currentFragmentId == R.id.alarmListFragment) navController!!.navigate(R.id.action_alarmListFragment_to_mainPageFragment)
                else if (currentFragmentId == R.id.checkListFragment) navController!!.navigate(R.id.action_checkListFragment_to_mainPageFragment)
            } else if (item.itemId == R.id.menu_time) {
                binding.toolbar.setTitle(R.string.toolbar_main_timer)
                if (currentFragmentId == R.id.mainPageFragment) navController!!.navigate(R.id.action_mainPageFragment_to_alarmListFragment)
                else if (currentFragmentId == R.id.checkListFragment) navController!!.navigate(R.id.action_checkListFragment_to_alarmListFragment)
            } else if (item.itemId == R.id.menu_time_check) {
                binding.toolbar.setTitle(R.string.toolbar_main_checklist)
                if (currentFragmentId == R.id.alarmListFragment) navController!!.navigate(R.id.action_alarmListFragment_to_checkListFragment)
                else if (currentFragmentId == R.id.mainPageFragment) navController!!.navigate(R.id.action_mainPageFragment_to_checkListFragment)
            } else if (item.itemId == R.id.menu_setting) {

            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth

        binding.toolbar.setTitle(R.string.toolbar_main_title)
        setSupportActionBar(binding.toolbar)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        assert(navHostFragment != null)
        navController = navHostFragment!!.navController
        binding.menuBottomNavigation.setOnItemSelectedListener(ItemSelectionListener())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, R.string.action_menu_logout)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                //TODO: When Signing Out Send Fragment that user is currently loggedOut
                firebaseAuth.signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}