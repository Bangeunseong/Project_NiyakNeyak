package com.capstone.project_niyakneyak.main.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
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
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null

    internal inner class ItemSelectionListener : NavigationBarView.OnItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val currentFragmentId = navController!!.currentDestination!!.id
            if (item.itemId == R.id.menu_main) {
                binding.toolbar.setTitle(R.string.toolbar_main_title)
                when (currentFragmentId){
                    R.id.alarmListFragment -> navController!!.navigate(R.id.action_alarmListFragment_to_mainPageFragment)
                    R.id.checkListFragment -> navController!!.navigate(R.id.action_checkListFragment_to_mainPageFragment)
                    R.id.settingFragment -> navController!!.navigate(R.id.action_settingFragment_to_mainPageFragment)
                }
            } else if (item.itemId == R.id.menu_time) {
                binding.toolbar.setTitle(R.string.toolbar_main_timer)
                when (currentFragmentId){
                    R.id.mainPageFragment -> navController!!.navigate(R.id.action_mainPageFragment_to_alarmListFragment)
                    R.id.checkListFragment -> navController!!.navigate(R.id.action_checkListFragment_to_alarmListFragment)
                    R.id.settingFragment -> navController!!.navigate(R.id.action_settingFragment_to_alarmListFragment)
                }
            } else if (item.itemId == R.id.menu_time_check) {
                binding.toolbar.setTitle(R.string.toolbar_main_checklist)
                when (currentFragmentId){
                    R.id.mainPageFragment -> navController!!.navigate(R.id.action_mainPageFragment_to_checkListFragment)
                    R.id.alarmListFragment -> navController!!.navigate(R.id.action_alarmListFragment_to_checkListFragment)
                    R.id.settingFragment -> navController!!.navigate(R.id.action_settingFragment_to_checkListFragment)
                }
            } else if (item.itemId == R.id.menu_setting) {
                binding.toolbar.setTitle(R.string.toolbar_main_settings)
                when (currentFragmentId){
                    R.id.mainPageFragment -> navController!!.navigate(R.id.action_mainPageFragment_to_settingFragment)
                    R.id.alarmListFragment -> navController!!.navigate(R.id.action_alarmListFragment_to_settingFragment)
                    R.id.checkListFragment -> navController!!.navigate(R.id.action_checkListFragment_to_settingFragment)
                }
            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth

        binding.toolbar.setTitle(R.string.toolbar_main_title)
        setSupportActionBar(binding.toolbar)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        assert(navHostFragment != null)
        navController = navHostFragment!!.navController
        binding.menuBottomNavigation.setOnItemSelectedListener(ItemSelectionListener())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tool, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_search -> { // 수정사항
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Warning!")
                    .setMessage("Do you really want to sign out?")
                    .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                        // When SignOut option clicked signs out
                        firebaseAuth.signOut()

                        // Return to SignIn Activity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("Cancel"){ _: DialogInterface?, _: Int -> }
                alertDialog.create().show()
            }
            R.id.nav_setting -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}