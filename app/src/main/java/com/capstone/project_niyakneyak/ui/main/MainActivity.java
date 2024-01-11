package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle(R.string.toolbar_main_title);
        setSupportActionBar(binding.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,R.string.action_menu_settings);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                //TODO: Need to Modify!
                Toast.makeText(MainActivity.this, "Add custom settings", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}