package com.capstone.project_niyakneyak.main.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    private var isPressed = false
    private var isIgnoringOptimization = false

    private val batteryOptimizationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            isIgnoringOptimization = false
        }
    }

    // BackPressed Callback
    private val callback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                when (navController.currentDestination!!.id){
                    R.id.mainPageFragment -> {
                        if(!isPressed){
                            Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다!", Toast.LENGTH_LONG).show()
                            CoroutineScope(Dispatchers.Default).launch {
                                isPressed = true
                                delay(2000)
                                isPressed = false
                            }
                        } else{
                            finish()
                        }
                    }
                    R.id.alarmListFragment -> navController.navigate(R.id.action_alarmListFragment_to_mainPageFragment)
                    R.id.checkListFragment -> navController.navigate(R.id.action_checkListFragment_to_mainPageFragment)
                    R.id.settingFragment -> navController.navigate(R.id.action_settingFragment_to_mainPageFragment)
                }
            }
        }
    }

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
                R.id.mainPageFragment -> {
                    binding.toolbar.setLogo(R.drawable.ic_drug_entities_white)
                    binding.toolbar.setTitle(R.string.toolbar_main_title)
                    binding.menuBottomNavigation.menu.findItem(R.id.menu_main).isChecked = true
                    binding.toolbar.menu.findItem(R.id.item_history).isVisible = false
                }
                R.id.alarmListFragment -> {
                    binding.toolbar.setLogo(R.drawable.ic_alarm_white)
                    binding.toolbar.setTitle(R.string.toolbar_main_timer)
                    binding.menuBottomNavigation.menu.findItem(R.id.menu_time).isChecked = true
                    binding.toolbar.menu.findItem(R.id.item_history).isVisible = false
                }
                R.id.checkListFragment -> {
                    binding.toolbar.setLogo(R.drawable.baseline_checklist_rtl_24_white)
                    binding.toolbar.setTitle(R.string.toolbar_main_checklist)
                    binding.menuBottomNavigation.menu.findItem(R.id.menu_time_check).isChecked = true
                    binding.toolbar.menu.findItem(R.id.item_history).isVisible = true
                }
                R.id.settingFragment -> {
                    binding.toolbar.setLogo(R.drawable.baseline_account_circle_24_white)
                    binding.toolbar.setTitle(R.string.toolbar_main_settings)
                    binding.menuBottomNavigation.menu.findItem(R.id.menu_account).isChecked = true
                    binding.toolbar.menu.findItem(R.id.item_history).isVisible = false
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isIgnoringOptimization = (getSystemService(POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(getString(R.string.app_name))

        binding.toolbar.setTitle(R.string.toolbar_main_title)
        binding.toolbar.setTitleTextAppearance(this@MainActivity, R.style.ToolbarTextAppearance)
        setSupportActionBar(binding.toolbar)
        _navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        _navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(DestinationChangedListener())
        binding.menuBottomNavigation.setOnItemSelectedListener(ItemSelectionListener())
    }

    override fun onStart() {
        super.onStart()

        if(!isIgnoringOptimization){
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:${getString(R.string.app_name)}")
            batteryOptimizationLauncher.launch(intent)
        }

        if(checkSelfPermission(NOTIFICATION_SERVICE) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(ALARM_SERVICE) == PackageManager.PERMISSION_DENIED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                requestPermissions(arrayOf(
                    Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.USE_FULL_SCREEN_INTENT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SCHEDULE_EXACT_ALARM),101)
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                requestPermissions(arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.USE_FULL_SCREEN_INTENT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SCHEDULE_EXACT_ALARM),101)
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                requestPermissions(arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.USE_FULL_SCREEN_INTENT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SCHEDULE_EXACT_ALARM),101)
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                requestPermissions(arrayOf(
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.USE_FULL_SCREEN_INTENT), 101)
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE), 101)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_top, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}