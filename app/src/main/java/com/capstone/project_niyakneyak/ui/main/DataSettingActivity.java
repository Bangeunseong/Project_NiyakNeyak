package com.capstone.project_niyakneyak.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.databinding.ActivityDataSettingBinding;
import com.capstone.project_niyakneyak.ui.main.etc.SubmitFormState;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.MainViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.MainViewModelFactory;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataSettingActivity extends AppCompatActivity {
    private final MutableLiveData<SubmitFormState> submitFormState = new MutableLiveData<>();
    private MainViewModel mainViewModel;
    private ActivityDataSettingBinding binding;

    @NonNull
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory())
                .get(MainViewModel.class);

        Intent intent = getIntent();

        // TextWatcher to evaluate that medication name is not empty
        TextWatcher afterTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                submitDataChanged(binding.medsNameText.getText().toString());
            }
        };

        // Observe the medication name exception
        submitFormState.observe(this, submitFormState -> {
            if (submitFormState == null) return;
            binding.submit.setEnabled(submitFormState.isDataValid());
            if (submitFormState.getMedsNameError() != null) {
                binding.dialogMedsNameLayout.setErrorEnabled(true);
                binding.dialogMedsNameLayout.setError(getString(submitFormState.getMedsNameError()));
            } else binding.dialogMedsNameLayout.setErrorEnabled(false);
        });


        binding.medsNameText.addTextChangedListener(afterTextChanged);
        binding.medsDateText.setOnClickListener(v -> {
            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setStart(MaterialDatePicker.thisMonthInUtcMilliseconds())
                    .setValidator(DateValidatorPointForward.now())
                    .build();
            MaterialDatePicker.Builder<Pair<Long, Long>> datePickerBuilder = MaterialDatePicker
                    .Builder
                    .dateRangePicker()
                    .setTitleText("Select Medication End Date")
                    .setCalendarConstraints(constraints);
            final MaterialDatePicker<Pair<Long, Long>> datePicker = datePickerBuilder.build();
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_RANGE");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Date startdate = new Date(), enddate = new Date();
                startdate.setTime(selection.first);
                enddate.setTime(selection.second);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                binding.medsDateText.setText(String.format("%s~%s",dateFormat.format(startdate),dateFormat.format(enddate)));
            });
            datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            });
        });

        if(mainViewModel == null) throw new RuntimeException("Runtime Exception Occurred, MainViewModel Not Found");

        // Setting the type of Activity(Add or modify)
        if(intent.getBundleExtra("DataBundle") == null){
            binding.submit.setOnClickListener(v -> {
                String meds_name_text = String.valueOf(binding.medsNameText.getText());
                String meds_detail_text = String.valueOf(binding.medsDetailText.getText()).equals("null") ? null : String.valueOf(binding.medsDetailText.getText());
                String meds_date_text = String.valueOf(binding.medsDateText.getText()).equals("null") ? null : String.valueOf(binding.medsDateText.getText());
                MedsData data1;
                if(meds_date_text != null && !meds_date_text.isEmpty()){
                    String[] dates = meds_date_text.split("~");
                    data1 = new MedsData(meds_name_text.hashCode(), meds_name_text,
                            meds_detail_text, dates[0], dates[1]);
                }
                else{
                    data1 = new MedsData(meds_name_text.hashCode(), meds_name_text,
                            meds_detail_text, null, null);
                }
                mainViewModel.add_MedsData(data1);
                setResult(RESULT_OK);
                finish();
            });
        }
        else {
            Bundle bundle = intent.getBundleExtra("DataBundle");
            assert bundle != null;
            MedsData data = (MedsData) bundle.get(getString(R.string.arg_origin_data));

            assert data != null;
            binding.medsNameText.setText(data.getMeds_name());
            binding.medsDetailText.setText(data.getMeds_detail());
            if(data.getMeds_start_date() != null && data.getMeds_end_date() != null){
                binding.medsDateText.setText(String.format("%s~%s",data.getMeds_start_date(), data.getMeds_end_date()));
            }

            binding.submit.setOnClickListener(v -> {
                String meds_name_text = String.valueOf(binding.medsNameText.getText());
                String meds_detail_text = String.valueOf(binding.medsDetailText.getText()).equals("null") ? null : String.valueOf(binding.medsDetailText.getText());
                String meds_date_text = String.valueOf(binding.medsDateText.getText()).equals("null") ? null : String.valueOf(binding.medsDateText.getText());
                MedsData data1;
                if(meds_date_text != null && !meds_date_text.isEmpty()){
                    String[] dates = meds_date_text.split("~");
                    data1 = new MedsData(meds_name_text.hashCode(), meds_name_text,
                            meds_detail_text, dates[0], dates[1]);
                }
                else{
                    data1 = new MedsData(meds_name_text.hashCode(), meds_name_text,
                            meds_detail_text, null, null);
                }
                mainViewModel.modify_MedsData(data, data1);
                setResult(RESULT_OK);
                finish();
            });
        }

        // When Click cancel button, any changes will be disposed!
        binding.cancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        /*
        sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) sunday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else sunday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });
        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) monday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else monday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });
        tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) tuesday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else tuesday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });
        wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) wednesday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else wednesday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });
        thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) thursday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else thursday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });
        friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) friday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else friday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });
        saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) saturday.setBackgroundResource(R.drawable.item_dialog_time_tgbtn);
                else saturday.setBackgroundResource(R.drawable.item_dialog_time_configurator_transparent);
            }
        });*/
    }

    private void submitDataChanged(String meds_name){
        if(!isMedsNameValid(meds_name)){
            submitFormState.setValue(new SubmitFormState(R.string.dialog_add_form_meds_name_error));
            binding.medsNameText.setFocusable(true);
        }
        else submitFormState.setValue(new SubmitFormState(true));
    }
    private boolean isMedsNameValid(String meds_name){
        if(meds_name == null || meds_name.length() < 1) return false;
        return true;
    }
}
