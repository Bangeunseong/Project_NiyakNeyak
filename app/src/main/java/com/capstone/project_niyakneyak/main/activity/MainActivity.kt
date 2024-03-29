package com.capstone.project_niyakneyak.main.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.BufferedReader
import java.io.FileReader

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

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth

        binding.toolbar.setTitle(R.string.toolbar_main_title)
        setSupportActionBar(binding.toolbar)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        assert(navHostFragment != null)
        navController = navHostFragment!!.navController
        binding.menuBottomNavigation.setOnItemSelectedListener(ItemSelectionListener())

        // Data Writing Procedure
        Log.w("Data Read", "Reading -> $filesDir/data.csv")
        val data = BufferedReader(FileReader("${filesDir}/data.csv"))

        data.readLine()
        for (i in 0 until 6){
            firestore.runTransaction {transaction ->
                for(j in 0 until 50000){
                    val string = data.readLine() ?: break
                    val params = string.split(",")
                    firestore.collection("medicines").add(
                        MedicineData(params[0],params[1],params[2],params[3],params[4],
                            params[5],params[6],params[7],params[8],params[9],
                            params[10],params[11],params[12],params[13],params[14],
                            params[15],params[16], params[17] == "Y",params[18],
                            params[19],params[20],params[21]))
                }
            }
        }
        data.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, R.string.action_menu_logout)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
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
        }
        return super.onOptionsItemSelected(item)
    }
}