package com.capstone.project_niyakneyak.ui.main.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.databinding.FragmentDataSettingDialogBinding;
import com.capstone.project_niyakneyak.ui.main.adapter.MainAlarmDataAdapter;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.etc.SubmitFormState;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataSettingViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataSettingViewModelFactory;
import com.capstone.project_niyakneyak.ui.main.listener.OnAddedDataListener;
import com.capstone.project_niyakneyak.ui.main.listener.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This DialogFragment is used for setting {@link MedsData}(which is Medication Info.).
 *
 */
public class DataSettingDialog extends DialogFragment implements OnCheckedAlarmListener {
    MutableLiveData<SubmitFormState> submitFormState = new MutableLiveData<>();
    private FragmentDataSettingDialogBinding binding;
    private final OnAddedDataListener onAddedDataListener;
    private final OnChangedDataListener onChangedDataListener;
    private DataSettingViewModel dataSettingViewModel;
    private MainAlarmDataAdapter adapter;
    private MedsData data;
    private List<Alarm> alarms = new ArrayList<>();
    private ArrayList<Integer> includedAlarms = new ArrayList<>();

    public DataSettingDialog(OnAddedDataListener onAddedDataListener, OnChangedDataListener onChangedDataListener){
        this.onAddedDataListener = onAddedDataListener; this.onChangedDataListener = onChangedDataListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Accessible Data
        Bundle bundle = getArguments();
        assert bundle != null;
        data = bundle.getParcelable("BeforeModify");
        if(data != null) includedAlarms = data.getAlarms();

        adapter = new MainAlarmDataAdapter(this);
        dataSettingViewModel = new ViewModelProvider(this, new DataSettingViewModelFactory(getActivity().getApplication()))
                .get(DataSettingViewModel.class);
        dataSettingViewModel.getAlarmsLiveData().observe(this, alarms -> {
            this.alarms = alarms;
            adapter.setAlarms(alarms, includedAlarms);
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Main Dialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogBackground);
        binding = FragmentDataSettingDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        if(data == null)
            builder.setCustomTitle(getLayoutInflater().inflate(R.layout.item_add_dialog_title, null));
        else builder.setCustomTitle(getLayoutInflater().inflate(R.layout.item_modify_dialog_title, null));
        builder.setView(view);

        // Dialog Component Field
        final TextInputLayout meds_name_layout = binding.dialogMedsNameLayout;

        final TextInputEditText meds_name = binding.medsNameText;
        final TextInputEditText meds_detail = binding.medsDetailText;
        final TextInputEditText meds_date = binding.medsDateText;
        final RecyclerView rcv_meds_timer = binding.dialogMedsTimerList;
        final Button meds_timer = binding.dialogMedsTimerAddBtn;

        final Button submit = binding.submit;
        final Button cancel = binding.cancel;

        // Setting RecyclerView about timer list
        rcv_meds_timer.setHasFixedSize(false);
        rcv_meds_timer.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_meds_timer.setAdapter(adapter);
        rcv_meds_timer.addItemDecoration(new VerticalItemDecorator(10));

        // If Accessible Data is not null, preset input form
        final Long startDate, endDate;
        if(data != null){
            meds_name.setText(data.getMeds_name());
            if(data.getMeds_detail() != null)
                meds_detail.setText(data.getMeds_detail());
            if(data.getMeds_start_date() != null && data.getMeds_end_date() != null){
                meds_date.setText(String.format("%s~%s",data.getMeds_start_date(),data.getMeds_end_date()));
            }
            DateFormat parser = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN);

            try {
                if(data.getMeds_start_date() != null && data.getMeds_end_date() != null){
                    startDate = parser.parse(data.getMeds_start_date()).getTime();
                    endDate = parser.parse(data.getMeds_end_date()).getTime();
                }
                else startDate = endDate = null;
            } catch (ParseException e) {
                Log.d("DataSettingDialog", "Meds_date parsing error");
                throw new RuntimeException();
            }

            submit.setEnabled(true);
        }
        else startDate = endDate = null;

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for(int i = start; i < end; i++)
                if(Character.isWhitespace(source.charAt(i))) return "";
            return null;
        };

        // TextInput Exception Control Methods
        TextWatcher afterTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                submitDataChanged(meds_name.getText().toString());
            }
        };

        submitFormState.observe(this, submitFormState -> {
            if(submitFormState == null) return;
            submit.setEnabled(submitFormState.isDataValid());
            if(submitFormState.getMedsNameError() != null){
                meds_name_layout.setErrorEnabled(true);
                meds_name_layout.setError(getString(submitFormState.getMedsNameError()));
            }
            else meds_name_layout.setErrorEnabled(false);
        });

        // Main Body(Setting Functions for each Components)
        meds_name.addTextChangedListener(afterTextChanged);
        meds_name.setFilters(new InputFilter[]{filter});
        meds_date.setOnClickListener(v -> {
            CalendarConstraints.Builder cal_builder = new CalendarConstraints.Builder()
                    .setStart(MaterialDatePicker.thisMonthInUtcMilliseconds());
            if(startDate != null)
                cal_builder.setValidator(DateValidatorPointForward.from(startDate));
            else cal_builder.setValidator(DateValidatorPointForward.now());
            CalendarConstraints constraints = cal_builder.build();

            MaterialDatePicker.Builder<Pair<Long, Long>> datePickerBuilder = MaterialDatePicker
                    .Builder
                    .dateRangePicker()
                    .setTitleText("Select Medication End Date")
                    .setCalendarConstraints(constraints);
            if(startDate != null)
                datePickerBuilder.setSelection(new Pair<>(startDate + 1000*60*60*24,endDate));

            final MaterialDatePicker<Pair<Long, Long>> datePicker = datePickerBuilder.build();
            datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER_RANGE");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Date startdate = new Date(), enddate = new Date();
                startdate.setTime(selection.first);
                enddate.setTime(selection.second);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                meds_date.setText(String.format("%s~%s", dateFormat.format(startdate), dateFormat.format(enddate)));
            });

            datePicker.addOnNegativeButtonClickListener(v1 -> {});
        });

        meds_timer.setOnClickListener(v -> showAlarmSettingDialog(null));

        submit.setOnClickListener(v -> {
            String meds_name_text = String.valueOf(meds_name.getText());
            String meds_detail_text = String.valueOf(meds_detail.getText()).equals("null") ? null : String.valueOf(meds_detail.getText());
            String meds_date_text = String.valueOf(meds_date.getText()).equals("null") ? null : String.valueOf(meds_date.getText());

            MedsData data1;
            if(!meds_date_text.isEmpty()){
                String[] dates = meds_date_text.split("~");
                data1 = new MedsData(meds_name_text.hashCode(), meds_name_text,
                        meds_detail_text, dates[0], dates[1]);
            }
            else{
                data1 = new MedsData(meds_name_text.hashCode(), meds_name_text,
                        meds_detail_text, null, null);
            }
            data1.setAlarms(includedAlarms);
            if(data == null)
                onAddedDataListener.onAddedData(data1);
            else onChangedDataListener.onChangedData(data, data1);
            dismiss();
        });

        cancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null; data = null;
    }

    void showAlarmSettingDialog(@Nullable Alarm alarm){
        DialogFragment alarmSettingDialog = new AlarmSettingDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.arg_alarm_obj), alarm);
        alarmSettingDialog.setArguments(bundle);
        alarmSettingDialog.show(requireActivity().getSupportFragmentManager(), "ALARM_DIALOG_FRAGMENT");
    }

    private void submitDataChanged(String meds){
        if(!isMedsNameValid(meds)){
            submitFormState.setValue(new SubmitFormState(R.string.dialog_add_form_meds_name_error));
        }
        else submitFormState.setValue(new SubmitFormState(true));
    }
    private boolean isMedsNameValid(String meds_name){
        if(meds_name == null || !meds_name.matches("\\w{1,20}")) return false;
        return true;
    }

    @Override
    public void OnItemClicked(Alarm alarm) {
        if(includedAlarms.contains(alarm.getAlarmCode()))
            includedAlarms.remove((Object)alarm.getAlarmCode());
        else includedAlarms.add(alarm.getAlarmCode());
    }

    @Override
    public void OnItemClicked(Alarm alarm, boolean isChecked) {}
}
