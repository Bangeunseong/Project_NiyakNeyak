package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModelFactory;
import com.capstone.project_niyakneyak.ui.main.fragment.MainPageFragment;
import com.capstone.project_niyakneyak.ui.main.fragment.AlarmListFragment;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.MainViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.MainViewModelFactory;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private AlarmListViewModel alarmListViewModel;
    private MainPageFragment mainPageFragment;
    private AlarmListFragment alarmListFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    class ItemSelectionListener implements NavigationBarView.OnItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(item.getItemId() == R.id.menu_main){
                binding.toolbar.setTitle(R.string.toolbar_main_title);
                binding.contentMainAdd.setImageResource(android.R.drawable.ic_input_add);
                binding.contentMainAdd.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, DataSettingActivity.class);
                    startActivityIfNeeded(intent, 0);
                });
                transaction.replace(R.id.content_fragment_layout, mainPageFragment).commitAllowingStateLoss();
            }
            else if(item.getItemId() == R.id.menu_time){
                binding.toolbar.setTitle(R.string.toolbar_main_timer);
                binding.contentMainAdd.setImageResource(android.R.drawable.ic_lock_idle_alarm);
                binding.contentMainAdd.setOnClickListener(null);
                transaction.replace(R.id.content_fragment_layout, alarmListFragment).commitAllowingStateLoss();
            }
            else if(item.getItemId() == R.id.menu_time_check){

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

        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MainViewModel.class);
        alarmListViewModel = new ViewModelProvider(this, new AlarmListViewModelFactory(getApplication()))
                .get(AlarmListViewModel.class);

        mainPageFragment = MainPageFragment.newInstance(mainViewModel);
        alarmListFragment = AlarmListFragment.newInstance(alarmListViewModel);
        binding.menuBottomNavigation.setOnItemSelectedListener(new ItemSelectionListener());

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_fragment_layout, mainPageFragment).commitAllowingStateLoss();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            mainPageFragment.notifyDataChanged();
        }
    }
}