package com.capstone.project_niyakneyak.ui.main.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.databinding.FragmentAlarmListBinding;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModelFactory;
import com.capstone.project_niyakneyak.ui.main.adapter.AlarmDataAdapter;
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.listener.OnToggleAlarmListener;

import java.util.Calendar;
import java.util.List;

/**
 * This Fragment is used for showing currently registered alarmList.
 * This Fragment implements {@link OnToggleAlarmListener} to update alarm data
 * by using {@link OnToggleAlarmListener#onToggle(Alarm)}, {@link OnToggleAlarmListener#onItemClick(Alarm)},
 * {@link OnToggleAlarmListener#onDelete(Alarm)}
 */
public class AlarmListFragment extends Fragment implements OnToggleAlarmListener {
    private FragmentAlarmListBinding binding;
    private AlarmDataAdapter adapter;
    private AlarmListViewModel alarmListViewModel;
    private List<Alarm> alarms = null;
    private Handler handler;
    private Runnable runnable;

    public static AlarmListFragment newInstance() {
        return new AlarmListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmListViewModel = new ViewModelProvider(this, new AlarmListViewModelFactory(requireActivity().getApplication()))
                .get(AlarmListViewModel.class);
        adapter = new AlarmDataAdapter(this);
        alarmListViewModel.getAlarmsLiveData().observe(this, alarms -> {
            if(alarms != null) {
                adapter.setAlarms(alarms); this.alarms = alarms;
                showTime();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAlarmListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();
        runnable = this::showTime;
        showTime();

        binding.contentTimeTable.setHasFixedSize(false);
        binding.contentTimeTable.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.contentTimeTable.setAdapter(adapter);
        binding.contentTimeTable.addItemDecoration(new HorizontalItemDecorator(10));
        binding.contentTimeTable.addItemDecoration(new VerticalItemDecorator(20));

        binding.contentAlarmAdd.setOnClickListener(v -> {
            showAlarmSettingDialog(null);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        runnable = null; handler = null;
        binding = null;
    }


    // Override Methods for data manipulation
    @Override
    public void onToggle(Alarm alarm) {
        if(alarm.isStarted()){
            alarm.cancelAlarm(getContext());
            alarmListViewModel.update(alarm);
        }
        else{
            alarm.scheduleAlarm(getContext());
            alarmListViewModel.update(alarm);
        }
    }

    @Override
    public void onDelete(Alarm alarm) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning!");
        builder.setMessage("Do you want to delete this timer?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            if(alarm.isStarted())
                alarm.cancelAlarm(getContext());
            alarmListViewModel.getPatientData().getMedsData().forEach(medsData -> {
                medsData.getAlarms().remove((Object)alarm.getAlarmCode());
            });
            alarmListViewModel.delete(alarm.getAlarmCode());
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> {});
        builder.create().show();
    }

    @Override
    public void onItemClick(Alarm alarm) {
        if(alarm.isStarted())
            alarm.cancelAlarm(getContext());
        showAlarmSettingDialog(alarm);
    }

    void showAlarmSettingDialog(@Nullable Alarm alarm){
        DialogFragment alarmSettingDialog = new AlarmSettingDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.arg_alarm_obj), alarm);
        alarmSettingDialog.setArguments(bundle);
        alarmSettingDialog.show(requireActivity().getSupportFragmentManager(), "ALARM_DIALOG_FRAGMENT");
    }

    //Functions for getting time difference between next time and current time
    private void showTime() {
        Long timeDifference = getTimeDifference();
        if(timeDifference != null){
            long days = timeDifference/(24*60*60*1000), hours = (timeDifference % (24*60*60*1000)) / (60*60*1000), minutes = ((timeDifference % (24*60*60*1000)) % (60*60*1000)) / (60*1000);
            binding.contentTimeLeftBeforeAlarm.setText(String.format("%d Days %d hours %d minutes left for next timer rings", days, hours, minutes));
        }
        else binding.contentTimeLeftBeforeAlarm.setText(R.string.dialog_meds_time_timer_error);
        handler.postDelayed(runnable, 60000);
    }

    private Long getTimeDifference(){
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        Long timeDifference = null;

        if(alarms == null) return null;
        for(Alarm alarm : alarms){
            if(!alarm.isStarted()) continue;
            timeDifference = getMinTimeDifference(alarm, today, timeDifference);
        }
        return timeDifference;
    }
    private Long getMinTimeDifference(Alarm alarm, Calendar today, Long timeDifference){
        Calendar alarmTime;
        if(!alarm.isRecurring()){alarmTime = getAlarmTime(alarm, today);}
        else{alarmTime = getRecurringAlarmTime(alarm, today);}
        timeDifference = minTimeDifference(timeDifference, alarmTime.getTimeInMillis() - today.getTimeInMillis());
        return timeDifference;
    }
    private boolean compareTime(Calendar target, Calendar subject) {
        return target.getTimeInMillis() > subject.getTimeInMillis();
    }
    private Calendar getAlarmTime(Alarm alarm, Calendar today){
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(System.currentTimeMillis());
        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        alarmTime.set(Calendar.MINUTE, alarm.getMin());
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);
        if(!compareTime(alarmTime, today)) alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        return alarmTime;
    }
    private Calendar getRecurringAlarmTime(Alarm alarm, Calendar today){
        Calendar alarmTime = getAlarmTime(alarm, today);
        int flag = 0;
        for(int i = alarmTime.get(Calendar.DAY_OF_WEEK) - 1; flag < 7; i = (i + 1) % 7, flag++){
            switch(i){
                case 0->{if(alarm.isSun()) return alarmTime;}
                case 1->{if(alarm.isMon()) return alarmTime;}
                case 2->{if(alarm.isTue()) return alarmTime;}
                case 3->{if(alarm.isWed()) return alarmTime;}
                case 4->{if(alarm.isThu()) return alarmTime;}
                case 5->{if(alarm.isFri()) return alarmTime;}
                case 6->{if(alarm.isSat()) return alarmTime;}
            }
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        return alarmTime;
    }
    private Long minTimeDifference(Long target, Long subject){
        if(target == null) return subject;
        return target < subject ? target : subject ;
    }
}