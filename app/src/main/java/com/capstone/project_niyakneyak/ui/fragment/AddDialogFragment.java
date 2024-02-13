package com.capstone.project_niyakneyak.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.ui.listener.OnAddedDataListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDialogFragment extends DialogFragment {
    MutableLiveData<SubmitFormState> submitFormState = new MutableLiveData<>();
    private final OnAddedDataListener added_communicator;
    public AddDialogFragment(OnAddedDataListener added_communicator){this.added_communicator = added_communicator;}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.item_dialog_form, null);
        builder.setCustomTitle(getLayoutInflater().inflate(R.layout.item_add_dialog_title,null));
        builder.setView(view);

        final TextInputLayout meds_name_layout = view.findViewById(R.id.dialog_meds_name_layout);

        final TextInputEditText meds_name = view.findViewById(R.id.dialog_meds_name_text);
        final TextInputEditText meds_detail = view.findViewById(R.id.dialog_meds_detail_text);
        final TextInputEditText meds_date = view.findViewById(R.id.dialog_meds_date_text);

        final ToggleButton sunday = view.findViewById(R.id.dialog_meds_time_tgl_sun);
        final ToggleButton monday = view.findViewById(R.id.dialog_meds_time_tgl_mon);
        final ToggleButton tuesday = view.findViewById(R.id.dialog_meds_time_tgl_tue);
        final ToggleButton wednesday = view.findViewById(R.id.dialog_meds_time_tgl_wed);
        final ToggleButton thursday = view.findViewById(R.id.dialog_meds_time_tgl_thu);
        final ToggleButton friday = view.findViewById(R.id.dialog_meds_time_tgl_fri);
        final ToggleButton saturday = view.findViewById(R.id.dialog_meds_time_tgl_sat);

        final SwitchCompat morning = view.findViewById(R.id.dialog_meds_time_sw_morning);
        final SwitchCompat afternoon = view.findViewById(R.id.dialog_meds_time_sw_afternoon);
        final SwitchCompat evening = view.findViewById(R.id.dialog_meds_time_sw_evening);
        final SwitchCompat midnight = view.findViewById(R.id.dialog_meds_time_sw_midnight);

        final Button submit = view.findViewById(R.id.dialog_submit);
        final Button cancel = view.findViewById(R.id.dialog_cancel);

        TextWatcher afterTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                submitDataChanged(meds_name.getText().toString());
            }
        };
        meds_name.addTextChangedListener(afterTextChanged);

        meds_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarConstraints constraints = new CalendarConstraints.Builder()
                        .setStart(MaterialDatePicker.thisMonthInUtcMilliseconds())
                        .setValidator(DateValidatorPointForward.now())
                        .build();
                MaterialDatePicker.Builder<Pair<Long,Long>> datePickerBuilder = MaterialDatePicker
                        .Builder
                        .dateRangePicker()
                        .setTitleText("Select Medication End Date")
                        .setCalendarConstraints(constraints);
                final MaterialDatePicker<Pair<Long, Long>> datePicker = datePickerBuilder.build();
                datePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER_RANGE");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Date startdate = new Date(), enddate = new Date();
                        startdate.setTime(selection.first);
                        enddate.setTime(selection.second);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        meds_date.setText(String.format("%s~%s",dateFormat.format(startdate),dateFormat.format(enddate)));
                    }
                });

                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
            }
        });

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
        });

        //TODO: Add MutableLiveData to check submit form is correct



        submitFormState.observe(this, new Observer<SubmitFormState>() {
            @Override
            public void onChanged(SubmitFormState submitFormState) {
                if(submitFormState == null) return;
                submit.setEnabled(submitFormState.isDataValid());
                if(submitFormState.getMedsNameError() != null){
                    meds_name_layout.setErrorEnabled(true);
                    meds_name_layout.setError(getString(submitFormState.getMedsNameError()));
                }
                else meds_name_layout.setErrorEnabled(false);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meds_name_text = String.valueOf(meds_name.getText());
                String meds_detail_text = String.valueOf(meds_detail.getText()).equals("null") ? null : String.valueOf(meds_detail.getText());
                String meds_date_text = String.valueOf(meds_date.getText()).equals("null") ? null : String.valueOf(meds_date.getText());

                MedsData data;
                Log.d("DialogFragment",meds_date_text);
                if(meds_date_text != null && !meds_date_text.isEmpty()){
                    String[] dates = meds_date_text.split("~");
                    data = new MedsData(meds_name_text.hashCode(), meds_name_text,
                            meds_detail_text, dates[0], dates[1]);
                }
                else{
                    data = new MedsData(meds_name_text.hashCode(), meds_name_text,
                            meds_detail_text, null, null);
                }

                boolean[] toggled = new boolean[]{
                        sunday.isChecked(),monday.isChecked(), tuesday.isChecked(),
                        wednesday.isChecked(),thursday.isChecked(), friday.isChecked(),
                        saturday.isChecked()
                };
                boolean[] checked = new boolean[]{
                        morning.isChecked(), afternoon.isChecked(),
                        evening.isChecked(), midnight.isChecked()
                };
                int i = 0;
                for(MedsData.ConsumeDate date : MedsData.ConsumeDate.values())
                    data.setDailyConsume(date, toggled[i++]);
                i = 0;
                for(MedsData.ConsumeTime time : MedsData.ConsumeTime.values())
                    data.setActivation(time, checked[i++]);
                added_communicator.onAddedData(data);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void submitDataChanged(String meds_name){
        if(!isMedsNameValid(meds_name)){
            submitFormState.setValue(new SubmitFormState(R.string.dialog_add_form_meds_name_error));
        }
        else submitFormState.setValue(new SubmitFormState(true));
    }
    private boolean isMedsNameValid(String meds_name){
        if(meds_name == null || meds_name.length() < 1) return false;
        return true;
    }
}
