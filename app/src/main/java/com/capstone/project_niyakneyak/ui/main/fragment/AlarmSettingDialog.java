package com.capstone.project_niyakneyak.ui.main.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.databinding.FragmentAlarmSettingDialogBinding;
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult;
import com.capstone.project_niyakneyak.ui.main.etc.AlarmDataView;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmSettingViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmSettingViewModelFactory;

import java.util.List;
import java.util.Random;

public class AlarmSettingDialog extends DialogFragment {
    private final MutableLiveData<ActionResult> actionResult = new MutableLiveData<>();
    private AlarmSettingViewModel alarmSettingViewModel;
    private FragmentAlarmSettingDialogBinding binding;
    private String tone;
    private Alarm alarm;
    private Ringtone ringtone;
    private boolean isRecurring = false;
    private List<Alarm> alarms = null;

    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
        if(o.getResultCode() == RESULT_OK){
            Uri uri = o.getData().getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ringtone = RingtoneManager.getRingtone(getContext(), uri);
            String title = ringtone.getTitle(getContext());
            if (uri != null) {
                tone = uri.toString();
                if(title!=null && !title.isEmpty())
                    binding.alarmRingtoneText.setText(title);
            } else binding.alarmRingtoneText.setText("");
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Accessible data
        Bundle data = getArguments();
        if(data != null)
            alarm = data.getParcelable(getString(R.string.arg_alarm_obj));
        else alarm = null;

        // Setting ViewModel
        alarmSettingViewModel = new ViewModelProvider(this, new AlarmSettingViewModelFactory(requireActivity().getApplication()))
                .get(AlarmSettingViewModel.class);
        alarmSettingViewModel.getAlarmsLiveData().observe(this, alarms -> {
            this.alarms = alarms;
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Main Dialog Builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogBackground);
        binding = FragmentAlarmSettingDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        tone = RingtoneManager.getActualDefaultRingtoneUri(requireContext(), RingtoneManager.TYPE_ALARM).toString();
        ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(tone));
        builder.setView(view);

        if(alarm != null) updateAlarmInfo(alarm);
        else {
            binding.timePicker.setHour(6);
            binding.timePicker.setMinute(0);
            binding.alarmRingtoneText.setText(ringtone.getTitle(getContext()));
        }

        // Setting TimePickerView in 24hourView
        binding.timePicker.setIs24HourView(true);

        // Toggle Button Animation
        binding.toggleSunday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleMonday.isChecked() || binding.toggleTuesday.isChecked() || binding.toggleWednesday.isChecked()
                    || binding.toggleThursday.isChecked() || binding.toggleFriday.isChecked() || binding.toggleSaturday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setSunday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setSunday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });
        binding.toggleMonday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleSunday.isChecked() || binding.toggleTuesday.isChecked() || binding.toggleWednesday.isChecked()
                    || binding.toggleThursday.isChecked() || binding.toggleFriday.isChecked() || binding.toggleSaturday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setMonday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setMonday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });
        binding.toggleTuesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleMonday.isChecked() || binding.toggleSunday.isChecked() || binding.toggleWednesday.isChecked()
                    || binding.toggleThursday.isChecked() || binding.toggleFriday.isChecked() || binding.toggleSaturday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setTuesday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setTuesday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });
        binding.toggleWednesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleMonday.isChecked() || binding.toggleTuesday.isChecked() || binding.toggleSunday.isChecked()
                    || binding.toggleThursday.isChecked() || binding.toggleFriday.isChecked() || binding.toggleSaturday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setWednesday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setWednesday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });
        binding.toggleThursday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleMonday.isChecked() || binding.toggleTuesday.isChecked() || binding.toggleWednesday.isChecked()
                    || binding.toggleSunday.isChecked() || binding.toggleFriday.isChecked() || binding.toggleSaturday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setThursday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setThursday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });
        binding.toggleFriday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleMonday.isChecked() || binding.toggleTuesday.isChecked() || binding.toggleWednesday.isChecked()
                    || binding.toggleThursday.isChecked() || binding.toggleSunday.isChecked() || binding.toggleSaturday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setFriday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setFriday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });
        binding.toggleSaturday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRecurring = isChecked || binding.toggleMonday.isChecked() || binding.toggleTuesday.isChecked() || binding.toggleWednesday.isChecked()
                    || binding.toggleThursday.isChecked() || binding.toggleFriday.isChecked() || binding.toggleSunday.isChecked();

            if(isChecked) {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); buttonView.setAlpha(1);}
            else {buttonView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out)); buttonView.setAlpha(0.5f);}
            if(actionResult.getValue() == null){
                AlarmDataView date = new AlarmDataView();
                date.setSaturday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
            else {
                AlarmDataView date = actionResult.getValue().getAlarmDateView();
                date.setSaturday(isChecked);
                actionResult.setValue(new ActionResult(date));
            }
        });

        // Ringtone Setting
        binding.alarmRingtoneLayout.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone));
            mStartForResult.launch(intent);
        });

        // Observe LiveData of date toggle button
        actionResult.observe(this, actionResult -> {
            if(actionResult == null) return;
            if(actionResult.getAlarmDateView() != null)
                binding.weeklyDate.setText(String.format("Weekly: %s", actionResult.getAlarmDateView().getDisplayData()));
        });

        binding.alarmSubmit.setOnClickListener(v -> {
            if(alarm != null) updateAlarm();
            else scheduleAlarm();
            dismiss();
        });

        binding.alarmCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null; alarmSettingViewModel = null;
    }

    private void scheduleAlarm(){
        String alarmTitle = getString(R.string.alarm_title);
        int alarmCode = codeGenerator();
        if(!binding.dialogAlarmTitleText.getText().toString().isEmpty())
            alarmTitle = binding.dialogAlarmTitleText.getText().toString();

        Alarm alarm = new Alarm(
                alarmCode,
                binding.timePicker.getHour(),
                binding.timePicker.getMinute(),
                alarmTitle,
                true,
                isRecurring,
                binding.toggleMonday.isChecked(),
                binding.toggleTuesday.isChecked(),
                binding.toggleWednesday.isChecked(),
                binding.toggleThursday.isChecked(),
                binding.toggleFriday.isChecked(),
                binding.toggleSaturday.isChecked(),
                binding.toggleSunday.isChecked(),
                tone,
                binding.alarmVibSwt.isChecked()
        );

        // Validate alarm's existence
        if(isAlreadyExistsAlarm(alarm)){
            Toast.makeText(getContext(), String.format("Alarm already exists at %02d:%02d!", binding.timePicker.getHour(), binding.timePicker.getMinute()), Toast.LENGTH_SHORT).show();
            return;
        }

        alarmSettingViewModel.insert(alarm);
        alarm.scheduleAlarm(getContext());
    }

    private void updateAlarm(){
        String alarmTitle = getString(R.string.alarm_title);
        //int alarmCode = codeGenerator();
        if(!binding.dialogAlarmTitleText.getText().toString().isEmpty())
            alarmTitle = binding.dialogAlarmTitleText.getText().toString();
        Alarm updatedAlarm = new Alarm(
                alarm.getAlarmCode(),
                binding.timePicker.getHour(),
                binding.timePicker.getMinute(),
                alarmTitle,
                true,
                isRecurring,
                binding.toggleMonday.isChecked(),
                binding.toggleTuesday.isChecked(),
                binding.toggleWednesday.isChecked(),
                binding.toggleThursday.isChecked(),
                binding.toggleFriday.isChecked(),
                binding.toggleSaturday.isChecked(),
                binding.toggleSunday.isChecked(),
                tone,
                binding.alarmVibSwt.isChecked()
        );
        alarmSettingViewModel.update(updatedAlarm);
        updatedAlarm.scheduleAlarm(getContext());
    }

    private void updateAlarmInfo(Alarm alarm){
        binding.dialogAlarmTitleText.setText(alarm.getTitle());
        binding.timePicker.setHour(alarm.getHour());
        binding.timePicker.setMinute(alarm.getMin());
        if(alarm.isMon()){
            binding.toggleMonday.setChecked(true);
            binding.toggleMonday.setAlpha(1);
            isRecurring = true;
        }
        if(alarm.isTue()){
            binding.toggleTuesday.setChecked(true);
            binding.toggleTuesday.setAlpha(1);
            isRecurring = true;
        }
        if(alarm.isWed()){
            binding.toggleWednesday.setChecked(true);
            binding.toggleWednesday.setAlpha(1);
            isRecurring = true;
        }
        if(alarm.isThu()){
            binding.toggleThursday.setChecked(true);
            binding.toggleThursday.setAlpha(1);
            isRecurring = true;
        }
        if(alarm.isFri()){
            binding.toggleFriday.setChecked(true);
            binding.toggleFriday.setAlpha(1);
            isRecurring = true;
        }
        if(alarm.isSat()){
            binding.toggleSaturday.setChecked(true);
            binding.toggleSaturday.setAlpha(1);
            isRecurring = true;
        }
        if(alarm.isSun()){
            binding.toggleSunday.setChecked(true);
            binding.toggleSunday.setAlpha(1);
            isRecurring = true;
        }

        actionResult.setValue(new ActionResult(new AlarmDataView(alarm.isSun(),alarm.isMon(),alarm.isTue(),
                alarm.isWed(),alarm.isThu(),alarm.isFri(),alarm.isSat())));

        tone = alarm.getTone();
        ringtone = RingtoneManager.getRingtone(requireContext(), Uri.parse(tone));
        binding.alarmRingtoneText.setText(ringtone.getTitle(getContext()));
        if(alarm.isVibrate())
            binding.alarmVibSwt.setChecked(true);
    }

    private boolean isAlreadyExistsAlarm(Alarm data){
        for(Alarm alarm : alarms)
            if(alarm.getHour() == data.getHour() && alarm.getMin() == data.getMin()){
                if(alarm.getRecurringDaysText() != null)
                    return alarm.getRecurringDaysText().equals(data.getRecurringDaysText());
                else return data.getRecurringDaysText() == null;
            }
        return false;
    }

    private int codeGenerator(){
        return new Random().nextInt(Integer.MAX_VALUE);
    }
}