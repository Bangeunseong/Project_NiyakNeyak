package com.capstone.project_niyakneyak.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * This Adapter is used for showing currently registered and selectable alarms.
 * When setting Medication Data, this adapter is used to describe which alarm is available.
 * Adapter needs {@link OnCheckedAlarmListener} to deliver checked alarm info to
 * {@link com.capstone.project_niyakneyak.ui.main.fragment.DataSettingDialog}
 */
public class MainAlarmDataAdapter extends RecyclerView.Adapter<MainAlarmDataAdapter.ViewHolder>{
    private final OnCheckedAlarmListener onCheckedAlarmListener;
    private List<Alarm> alarms;
    private List<Integer> includedAlarms;

    public MainAlarmDataAdapter(OnCheckedAlarmListener onCheckedAlarmListener){
        this.onCheckedAlarmListener = onCheckedAlarmListener;
        alarms = new ArrayList<>();
        includedAlarms = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout clockSelectionLayout;
        private final TextView clockTime;
        private final TextView clockRecursion;
        private final CheckBox clockCheckBox;
        public ViewHolder(@NonNull View view) {
            super(view);
            clockSelectionLayout = view.findViewById(R.id.clock_selection_single);
            clockTime = view.findViewById(R.id.clock_time);
            clockRecursion = view.findViewById(R.id.clock_recursion);
            clockCheckBox = view.findViewById(R.id.clock_checkbox);
        }

        public void bind(Alarm alarm, List<Integer> includedAlarms, OnCheckedAlarmListener onCheckedAlarmListener){
            clockTime.setText(String.format(Locale.KOREAN,"%02d:%02d",alarm.getHour(),alarm.getMin()));

            if (alarm.isRecurring()) {
                clockRecursion.setText(alarm.getRecurringDaysText());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                if(alarm.getHour() < calendar.get(Calendar.HOUR_OF_DAY)){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    clockRecursion.setText(String.format(Locale.KOREAN,"Next Day %02d/%02d",calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                else if(alarm.getHour() == calendar.get(Calendar.HOUR_OF_DAY)){
                    if(alarm.getMin() <= calendar.get(Calendar.MINUTE)){
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        clockRecursion.setText(String.format(Locale.KOREAN, "Next Day %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                    }
                    else clockRecursion.setText(String.format(Locale.KOREAN,"Today %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                else{
                    clockRecursion.setText(String.format(Locale.KOREAN,"Today %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
            }

            clockCheckBox.setEnabled(alarm.isStarted());
            clockCheckBox.setChecked(includedAlarms.contains(alarm.getAlarmCode()));
            clockCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedAlarmListener.OnItemClicked(alarm));
            if(!alarm.isStarted())
                clockSelectionLayout.setBackgroundResource(R.drawable.item_recycler_bg_disabled);
            else clockSelectionLayout.setBackgroundResource(R.drawable.item_recycler_bg_secondary);
        }
    }

    @NonNull
    @Override
    public MainAlarmDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds_time_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAlarmDataAdapter.ViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.bind(alarm, includedAlarms, onCheckedAlarmListener);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<Alarm> alarms, List<Integer> includedAlarms){
        this.alarms = alarms;
        this.includedAlarms = includedAlarms;
        notifyDataSetChanged();
    }
}
