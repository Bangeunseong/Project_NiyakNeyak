package com.capstone.project_niyakneyak.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class CheckAlarmAdapter extends RecyclerView.Adapter<CheckAlarmAdapter.ViewHolder> {
    private final OnCheckedAlarmListener onCheckedAlarmListener;
    private List<Alarm> alarms;

    public CheckAlarmAdapter(OnCheckedAlarmListener onCheckedAlarmListener){
        this.onCheckedAlarmListener = onCheckedAlarmListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView checkTitle;
        private final TextView checkRecursion;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View view) {
            super(view);
            checkTitle = view.findViewById(R.id.check_clock_time);
            checkRecursion = view.findViewById(R.id.check_clock_recursion);
            checkBox = view.findViewById(R.id.check_clock_checkbox);
        }

        public void bind(Alarm alarm, OnCheckedAlarmListener onCheckedAlarmListener){
            checkTitle.setText(String.format("%02d:%02d",alarm.getHour(),alarm.getMin()));
            if (alarm.isRecurring()) {
                checkRecursion.setText(alarm.getRecurringDaysText());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                if(alarm.getHour() < calendar.get(Calendar.HOUR_OF_DAY)){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    checkRecursion.setText(String.format(Locale.KOREAN,"Next Day %02d/%02d",calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                else if(alarm.getHour() == calendar.get(Calendar.HOUR_OF_DAY)){
                    if(alarm.getMin() <= calendar.get(Calendar.MINUTE)){
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        checkRecursion.setText(String.format(Locale.KOREAN, "Next Day %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                    }
                    else checkRecursion.setText(String.format(Locale.KOREAN,"Today %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
                else{
                    checkRecursion.setText(String.format(Locale.KOREAN,"Today %02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                }
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedAlarmListener.OnItemClicked(alarm, isChecked));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_check_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.bind(alarm, onCheckedAlarmListener);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<Alarm> alarms){
        this.alarms = alarms;
        notifyDataSetChanged();
    }
}
