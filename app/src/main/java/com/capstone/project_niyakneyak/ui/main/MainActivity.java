package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;
import com.capstone.project_niyakneyak.ui.fragment.AddDialogFragment;

public class MainActivity extends AppCompatActivity implements AddDialogFragment.OnCompleteListener {
    private Intent intent;
    private ActivityMainBinding binding;
    private MedsViewModel medsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle(R.string.toolbar_main_title);
        setSupportActionBar(binding.toolbar);

        medsViewModel = new ViewModelProvider(this, new MedsViewModelFactory(intent.getStringExtra("USER_ID")))
                .get(MedsViewModel.class);

        binding.contentMainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddForm();
            }
        });

        binding.contentMainMeds.setHasFixedSize(true);
        binding.contentMainMeds.setLayoutManager(new LinearLayoutManager(this));
        binding.contentMainMeds.setAdapter(new RecyclerAdapter(medsViewModel.getDatas()));
        binding.contentMainMeds.addItemDecoration(new VerticalItemDecorator(20));
        binding.contentMainMeds.addItemDecoration(new HorizontalItemDecorator(10));
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



    private void showAddForm(){
        DialogFragment newAddForm = new AddDialogFragment();
        newAddForm.show(getSupportFragmentManager(), "dialog");

    }

    @Override
    public void onInputedData(@NonNull String meds_name, String meds_detail, String meds_duration, @Nullable String meds_time_morning, @Nullable String meds_time_afternoon, @Nullable String meds_time_evening, @Nullable String meds_time_latenight) {
        if(!meds_name.isEmpty()){
            medsViewModel.addData(new MedsData(intent.getStringExtra("USER_ID"), meds_name, meds_detail));
            binding.contentMainMeds.getAdapter().notifyDataSetChanged();
            medsViewModel.getActionResult().observe(this, new Observer<ActionResult>() {
                @Override
                public void onChanged(ActionResult actionResult) {
                    if(actionResult == null) return;
                    if(actionResult.getSuccess() != null)
                        Toast.makeText(getApplicationContext(), actionResult.getSuccess().getDisplayData(), Toast.LENGTH_SHORT).show();
                    if(actionResult.getError() != null)
                        Toast.makeText(getApplicationContext(), actionResult.getError(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        else{
            Toast toast = Toast.makeText(MainActivity.this, "Addition Canceled!", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}