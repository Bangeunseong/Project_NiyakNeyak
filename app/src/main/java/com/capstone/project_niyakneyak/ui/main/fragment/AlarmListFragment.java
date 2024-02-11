package com.capstone.project_niyakneyak.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class AlarmListFragment extends Fragment {
    private FragmentAlarmListBinding binding;
    private AlarmDataAdapter adapter;
    private AlarmListViewModel alarmListViewModel;
    private Handler handler;
    private Runnable runnable;

    public static AlarmListFragment newInstance(AlarmListViewModel alarmListViewModel) {
        AlarmListFragment fragment = new AlarmListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("AlarmListViewModel", alarmListViewModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        alarmListViewModel = (AlarmListViewModel) bundle.getSerializable("AlarmListViewModel");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        runnable = this::showTime;
        binding = FragmentAlarmListBinding.inflate(inflater, container, false);
        showTime();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new AlarmDataAdapter(getContext(), requireActivity().getSupportFragmentManager(), alarmListViewModel.getAlarmsLiveData().getValue());

        binding.contentTimeTable.setHasFixedSize(false);
        binding.contentTimeTable.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.contentTimeTable.setAdapter(adapter);
        binding.contentTimeTable.addItemDecoration(new HorizontalItemDecorator(10));
        binding.contentTimeTable.addItemDecoration(new VerticalItemDecorator(20));

        alarmListViewModel.getAlarmsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                adapter.notifyItemRangeChanged(0, alarms.size());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        runnable = null; handler = null;
        binding = null;
    }

    //Functions for getting time difference between next time and current time
    private void showTime() {
        Long timediff = getTimeDifference();
        if(timediff != null){
            long days = timediff/(24*60*60*1000), hours = (timediff % (24*60*60*1000)) / (60*60*1000), minutes = ((timediff % (24*60*60*1000)) % (60*60*1000)) / (60*1000);
            binding.contentTimeLeftBeforeAlarm.setText(String.format("%d Days %d hours %d minutes left for next timer rings", days, hours, minutes));
        }
        else binding.contentTimeLeftBeforeAlarm.setText(R.string.dialog_meds_time_timer_error);
        handler.postDelayed(runnable, 60000);
    }

    private boolean compareTo(Calendar target, Calendar subject) throws ParseException {
        return target.getTimeInMillis() > subject.getTimeInMillis();
    }

    private Long getTimeDifference(){
        return null;
    }
}