package com.capstone.project_niyakneyak.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This adapter is used for showing Medication info. which should be consumed in current date.
 * It needs {@link OnCheckedAlarmListener} to deliver which alarm is checked
 * When alarm is checked, {@link com.capstone.project_niyakneyak.ui.main.fragment.CheckListFragment}
 * will handle the process of recording
 */
public class CheckDataAdapter extends RecyclerView.Adapter<CheckDataAdapter.ViewHolder> {
    private OnCheckedAlarmListener onCheckedAlarmListener;
    private List<MedsData> medsList = new ArrayList<>();
    private List<Alarm> alarms = new ArrayList<>();

    public CheckDataAdapter(OnCheckedAlarmListener onCheckedAlarmListener){
        this.onCheckedAlarmListener = onCheckedAlarmListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView detail;
        private final TextView duration;
        private final RecyclerView alarmList;
        private final ConstraintLayout checkItemLayout;
        private final CheckAlarmAdapter adapter;
        private boolean visibility = false;

        public ViewHolder(@NonNull View view, OnCheckedAlarmListener onCheckedAlarmListener) {
            super(view);
            checkItemLayout = view.findViewById(R.id.check_list_item_layout);
            title = view.findViewById(R.id.check_title_text);
            detail = view.findViewById(R.id.check_detail_text);
            duration = view.findViewById(R.id.check_title_time);
            alarmList = view.findViewById(R.id.alarm_check_list);
            adapter = new CheckAlarmAdapter(onCheckedAlarmListener);
        }

        public void bind(MedsData data, List<Alarm> alarms){
            title.setText(data.getMeds_name());
            if(data.getMeds_detail() != null)
                detail.setText(String.format("Detail: %s",data.getMeds_detail()));
            else detail.setText(String.format("Detail: %s", "None"));
            if(data.getMeds_start_date() != null)
                duration.setText(String.format("Duration: %s~%s", data.getMeds_start_date(), data.getMeds_end_date()));
            else duration.setText(String.format("Duration: %s", "None"));
            checkItemLayout.setOnClickListener(v -> {
                if(!visibility) alarmList.setVisibility(View.VISIBLE);
                else alarmList.setVisibility(View.GONE);
                visibility = !visibility;
            });

            alarmList.setHasFixedSize(false);
            alarmList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            alarmList.setAdapter(adapter);
            alarmList.addItemDecoration(new VerticalItemDecorator(10));
            adapter.setAlarms(data.getID(), getAlarms(data.getAlarms(), alarms));
        }

        private List<Alarm> getAlarms(List<Integer> includedAlarms, List<Alarm> alarms){
            Calendar today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());
            List<Alarm> included = new ArrayList<>();
            for(Alarm alarm : alarms){
                if(includedAlarms.contains(alarm.getAlarmCode()) && alarm.isStarted()){
                    if(alarm.isRecurring()){
                        switch(today.get(Calendar.DAY_OF_WEEK)){
                            case Calendar.SUNDAY -> {if (alarm.isSun()) included.add(alarm);}
                            case Calendar.MONDAY -> {if (alarm.isMon()) included.add(alarm);}
                            case Calendar.TUESDAY -> {if (alarm.isTue()) included.add(alarm);}
                            case Calendar.WEDNESDAY -> {if (alarm.isWed()) included.add(alarm);}
                            case Calendar.THURSDAY -> {if (alarm.isThu()) included.add(alarm);}
                            case Calendar.FRIDAY -> {if (alarm.isFri()) included.add(alarm);}
                            case Calendar.SATURDAY -> {if (alarm.isSat()) included.add(alarm);}
                        }
                    }
                    else{
                        Calendar alarmClock = Calendar.getInstance();
                        alarmClock.setTimeInMillis(System.currentTimeMillis());
                        alarmClock.set(Calendar.HOUR_OF_DAY, alarm.getHour()); alarmClock.set(Calendar.MINUTE, alarm.getMin());
                        alarmClock.set(Calendar.SECOND, 0); alarmClock.set(Calendar.MILLISECOND, 0);
                        if(alarmClock.compareTo(today) > 0) included.add(alarm);
                    }
                }
            }
            return included;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_check, parent, false);
        return new ViewHolder(view, onCheckedAlarmListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedsData data = medsList.get(position);
        holder.bind(data, alarms);
    }

    @Override
    public int getItemCount() {
        return medsList.size();
    }

    public void setDataSet(List<MedsData> medsList, List<Alarm> alarms) {
        this.medsList = medsList; this.alarms = alarms;
        notifyDataSetChanged();
    }
}