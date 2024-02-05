package com.capstone.project_niyakneyak.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.ui.fragment.SettingFragment;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;
import com.capstone.project_niyakneyak.ui.fragment.MainPageFragment;
import com.capstone.project_niyakneyak.ui.fragment.TimeSettingFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private ActivityMainBinding binding;
    private PatientViewModel patientViewModel;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private MainPageFragment mainPageFragment;
    private TimeSettingFragment timeSettingFragment;

    private SettingFragment settingFragment;

    class ItemSelectionListener implements NavigationBarView.OnItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(item.getItemId() == R.id.menu_main){
                binding.toolbar.setTitle(R.string.toolbar_main_title);
                transaction.replace(R.id.content_fragment_layout, mainPageFragment).commitAllowingStateLoss();
            }
            else if(item.getItemId() == R.id.menu_time){
                binding.toolbar.setTitle(R.string.toolbar_main_timer);
                transaction.replace(R.id.content_fragment_layout, timeSettingFragment).commitAllowingStateLoss();
            }
            else if(item.getItemId() == R.id.menu_setting){
                binding.toolbar.setTitle("Setting");
                transaction.replace(R.id.content_fragment_layout,settingFragment).commitAllowingStateLoss();
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

        patientViewModel = new ViewModelProvider(this, new PatientViewModelFactory())
                .get(PatientViewModel.class);
        mainPageFragment = MainPageFragment.newInstance(patientViewModel);
        timeSettingFragment = TimeSettingFragment.newInstance(patientViewModel);

        settingFragment = SettingFragment.newInstance("blank","blank");

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_fragment_layout, mainPageFragment).commitAllowingStateLoss();
        binding.menuBottomNavigation.setOnItemSelectedListener(new ItemSelectionListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,R.string.action_menu_logout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK, intent);
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