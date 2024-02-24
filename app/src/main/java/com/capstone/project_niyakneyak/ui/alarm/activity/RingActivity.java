package com.capstone.project_niyakneyak.ui.alarm.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.databinding.ActivityRingBinding;
import com.capstone.project_niyakneyak.ui.alarm.service.AlarmService;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class RingActivity extends AppCompatActivity {
    private final ArrayList<String> snoozeTime = new ArrayList<>(Arrays.asList("Five Minutes","Ten Minutes","Fifteen Minutes"));
    private int snoozeVal;
    private AlarmListViewModel alarmListViewModel;
    private ActivityRingBinding binding;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        alarmListViewModel = new ViewModelProvider(this, new AlarmListViewModelFactory(getApplication()))
                .get(AlarmListViewModel.class);
        Bundle bundle = getIntent().getBundleExtra(getString(R.string.arg_alarm_bundle_obj));
        if(bundle != null) alarm = bundle.getParcelable(getString(R.string.arg_alarm_obj));
        binding.alarmRingSnoozeTime.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, snoozeTime));

        binding.alarmRingSnoozeTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                snoozeVal = (position + 1) * 5;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
                snoozeVal = 5;
            }
        });
        binding.alarmRingReschedule.setOnClickListener(v -> rescheduleAlarm());
        binding.alarmRingOff.setOnClickListener(v -> dismissAlarm());

        animateClock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false);
            setTurnScreenOn(false);
        } else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
    }

    private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(binding.alarmRingClock, "rotation", 0f, 30f, 0f, -30f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }

    private void rescheduleAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, snoozeVal);

        if(alarm!=null){
            alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            alarm.setMin(calendar.get(Calendar.MINUTE));
            alarm.setTitle("Snooze "+ alarm.getTitle());
        }
        else {
            alarm = new Alarm(
                    new Random().nextInt(Integer.MAX_VALUE),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    "Snooze",
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    RingtoneManager.getActualDefaultRingtoneUri(getBaseContext(), RingtoneManager.TYPE_ALARM).toString(),
                    false
            );
        }
        alarm.scheduleAlarm(getApplicationContext());

        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    private void dismissAlarm(){
        if(alarm != null){
            if(!alarm.isRecurring()){
                alarm.setStarted(false);
                alarm.cancelAlarm(getBaseContext());
                alarmListViewModel.update(alarm);
            }
        }
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }
}