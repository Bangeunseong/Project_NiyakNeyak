package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.databinding.ActivityMainBinding;
import com.capstone.project_niyakneyak.ui.fragment.AddDialogFragment;
import com.capstone.project_niyakneyak.ui.fragment.OnAddedDataListener;
import com.capstone.project_niyakneyak.ui.fragment.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.fragment.OnDeleteDataListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private ActivityMainBinding binding;
    private MedsViewModel medsViewModel;
    private MedsInfoAdapter adapter;


    private OnAddedDataListener added_communicator = new OnAddedDataListener() {
        @Override
        public void onAddedData(MedsData target) {
            Log.d("MainActivity","Data Addition called");
            if(!target.getMeds_name().isEmpty()){
                Result<List<MedsData>> result = medsViewModel.getDatas();
                if(result instanceof Result.Success){
                    medsViewModel.addData(target);
                    adapter.addItem(((Result.Success<List<MedsData>>) result).getData().size() - 1);
                }

                medsViewModel.getActionResult().observe(MainActivity.this, new Observer<ActionResult>() {
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
                Toast toast = Toast.makeText(MainActivity.this, "Addition Failed!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };
    private OnChangedDataListener changed_communicator = new OnChangedDataListener() {
        @Override
        public void onChangedData(MedsData origin, MedsData changed) {
            Log.d("MainActivity","Data Modification called");
            if(!changed.getMeds_name().isEmpty()){
                Result<List<MedsData>> result = medsViewModel.getDatas();
                if(result instanceof Result.Success){
                    medsViewModel.modifyData(origin, changed);
                    adapter.changeItem(((Result.Success<List<MedsData>>) result).getData().indexOf(changed));
                }

                medsViewModel.getActionResult().observe(MainActivity.this, new Observer<ActionResult>() {
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
                Toast toast = Toast.makeText(MainActivity.this, "Modification Canceled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };
    private OnDeleteDataListener communicator = new OnDeleteDataListener() {
        @Override
        public void onDeletedData(MedsData target) {
            Log.d("MainActivity","Data Deletion called");

            Result<List<MedsData>> result = medsViewModel.getDatas();
            if(result instanceof Result.Success){
                int position = ((Result.Success<List<MedsData>>) result).getData().indexOf(target);
                medsViewModel.deleteData(target); adapter.removeItem(position);
            }
            medsViewModel.getActionResult().observe(MainActivity.this, new Observer<ActionResult>() {
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle(R.string.toolbar_main_title);
        setSupportActionBar(binding.toolbar);

        //View Control Unit
        medsViewModel = new ViewModelProvider(this, new MedsViewModelFactory(intent.getStringExtra("USER_ID")))
                .get(MedsViewModel.class);

        //RecyclerAdapter for Medication Info.
        Result<List<MedsData>> result = medsViewModel.getDatas();
        if(result instanceof Result.Success)
            adapter = new MedsInfoAdapter(getSupportFragmentManager(), ((Result.Success<List<MedsData>>) result).getData(), changed_communicator, communicator);
        else adapter = new MedsInfoAdapter(getSupportFragmentManager(), new ArrayList<>(), changed_communicator, communicator);

        binding.contentMainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newAddForm = new AddDialogFragment(added_communicator);
                newAddForm.show(getSupportFragmentManager(), "dialog");
            }
        });

        binding.contentMainMeds.setHasFixedSize(false);
        binding.contentMainMeds.setLayoutManager(new LinearLayoutManager(this));
        binding.contentMainMeds.setAdapter(adapter);
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
        switch (item.getItemId()) {
            case 0:
                setResult(RESULT_OK, intent);
                finish();
                break;
            case 1:
                //TODO: Need to Modify!
                Toast.makeText(MainActivity.this, "Add custom settings", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}