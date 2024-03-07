package com.capstone.project_niyakneyak.ui.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

import kotlin.coroutines.CoroutineContext;

/**
 * This activity is used for showing fragments which are linked by {@link MainActivity#navHostFragment}.
 * Linked fragments will be controlled by {@link MainActivity#navController} with
 * BottomNavigationBar(NavigationBar listener is created as ItemSelectionListener)
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private Intent intent;

    class ItemSelectionListener implements NavigationBarView.OnItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int currentFragmentId = navController.getCurrentDestination().getId();
            if(item.getItemId() == R.id.menu_main){
                binding.toolbar.setTitle(R.string.toolbar_main_title);
                if(currentFragmentId == R.id.alarmListFragment) navController.navigate(R.id.action_alarmListFragment_to_mainPageFragment);
                else if(currentFragmentId == R.id.checkListFragment) navController.navigate(R.id.action_checkListFragment_to_mainPageFragment);
            }
            else if(item.getItemId() == R.id.menu_time){
                binding.toolbar.setTitle(R.string.toolbar_main_timer);
                if(currentFragmentId == R.id.mainPageFragment) navController.navigate(R.id.action_mainPageFragment_to_alarmListFragment);
                else if(currentFragmentId == R.id.checkListFragment) navController.navigate(R.id.action_checkListFragment_to_alarmListFragment);
            }
            else if(item.getItemId() == R.id.menu_time_check){
                binding.toolbar.setTitle(R.string.toolbar_main_checklist);
                if(currentFragmentId == R.id.alarmListFragment) navController.navigate(R.id.action_alarmListFragment_to_checkListFragment);
                else if(currentFragmentId == R.id.mainPageFragment) navController.navigate(R.id.action_mainPageFragment_to_checkListFragment);
            }
            else if(item.getItemId() == R.id.menu_setting){

            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle(R.string.toolbar_main_title);
        setSupportActionBar(binding.toolbar);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        binding.menuBottomNavigation.setOnItemSelectedListener(new ItemSelectionListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,R.string.action_menu_logout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0->{
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}