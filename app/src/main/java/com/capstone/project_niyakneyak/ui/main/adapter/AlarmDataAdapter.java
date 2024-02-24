package com.capstone.project_niyakneyak.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.ui.main.listener.OnToggleAlarmListener;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmDataAdapter extends RecyclerView.Adapter<AlarmDataAdapter.ViewHolder> {
    private final OnToggleAlarmListener onToggleAlarmListener;
    private List<Alarm> alarms;

    public AlarmDataAdapter(OnToggleAlarmListener onToggleAlarmListener){
        this.onToggleAlarmListener = onToggleAlarmListener;
        this.alarms = new ArrayList<>();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rcv_alarm_time;
        private final TextView rcv_alarm_title;
        private final TextView rcv_weekly_date_display;
        private final ConstraintLayout rcv_alarm_layout;
        private final SwitchCompat rcv_alarm_swt;

        public ViewHolder(@NonNull View view) {
            super(view);
            rcv_alarm_time = view.findViewById(R.id.item_clock);
            rcv_alarm_title = view.findViewById(R.id.alarm_title);
            rcv_alarm_layout = view.findViewById(R.id.textview_layout);
            rcv_weekly_date_display = view.findViewById(R.id.weekly_date_display);
            rcv_alarm_swt = view.findViewById(R.id.alarm_switch);
        }

        public void bind(Alarm alarm, OnToggleAlarmListener listener){
            rcv_alarm_time.setText(String.format(Locale.KOREAN,"%02d:%02d",alarm.getHour(),alarm.getMin()));
            rcv_alarm_swt.setChecked(alarm.isStarted());

            if (alarm.isRecurring()) {
                rcv_weekly_date_display.setText(alarm.getRecurringDaysText());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                if(alarm.getHour() < calendar.get(Calendar.HOUR_OF_DAY)){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    rcv_weekly_date_display.setText(String.format(Locale.KOREAN,"Next Day, %02d/%02d",calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                else if(alarm.getHour() == calendar.get(Calendar.HOUR_OF_DAY)){
                    if(alarm.getMin() <= calendar.get(Calendar.MINUTE)){
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        rcv_weekly_date_display.setText(String.format(Locale.KOREAN, "Next Day: %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                    }
                    else rcv_weekly_date_display.setText(String.format(Locale.KOREAN,"Today, %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                else{
                    rcv_weekly_date_display.setText(String.format(Locale.KOREAN,"Today, %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
            }

            if(!alarm.getTitle().isEmpty()) rcv_alarm_title.setText(alarm.getTitle());
            else rcv_alarm_title.setText("My Alarm");

            rcv_alarm_swt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(buttonView.isShown() || buttonView.isPressed())
                    listener.onToggle(alarm);
            });
            rcv_alarm_layout.setOnClickListener(v -> listener.onItemClick(alarm));
            rcv_alarm_layout.setOnLongClickListener(v -> {
                listener.onDelete(alarm);
                return true;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.bind(alarm, onToggleAlarmListener);
    }

    @Override
    public int getItemCount() {return alarms.size();}

    public void setAlarms(List<Alarm> alarms){
        this.alarms = alarms;
        notifyDataSetChanged();
    }
}
