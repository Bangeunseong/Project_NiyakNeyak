package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle(R.string.toolbar_main_title);
        setSupportActionBar(binding.toolbar);

        binding.contentMainMeds.setLayoutManager(new LinearLayoutManager(this));
        binding.contentMainMeds.setAdapter(new RecyclerAdapter(
                Arrays.asList(
                        new MedsData("Asphirine","Painkiller"),
                        new MedsData("Insulin","Decreases BloodSugar"))
                )
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,R.string.action_menu_logout);
        menu.add(0,1,1,R.string.action_menu_settings);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                setResult(RESULT_OK, intent); finish();
                break;
            case 1:
                //TODO: Need to Modify!
                Toast.makeText(MainActivity.this, "Add custom settings", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}