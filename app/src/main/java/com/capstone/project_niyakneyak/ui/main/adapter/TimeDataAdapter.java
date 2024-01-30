package com.capstone.project_niyakneyak.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.ui.listener.OnChangedTimeListener;
import com.capstone.project_niyakneyak.data.model.TimeData;
import com.google.android.material.timepicker.*;

import java.util.List;

public class TimeDataAdapter extends RecyclerView.Adapter<TimeDataAdapter.ViewHolder> {
    private final Context context;
    private final FragmentManager fragmentManager;
    private final OnChangedTimeListener changed;
    private final List<TimeData> timeData;

    public TimeDataAdapter(Context context, FragmentManager fragmentManager, List<TimeData> timeData, OnChangedTimeListener changed){
        this.context = context; this.fragmentManager = fragmentManager;
        this.timeData = timeData; this.changed = changed;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rcv_time_text;
        private TextView rcv_time;
        private OnChangedTimeListener mCallback_m;

        public ViewHolder(@NonNull View view, OnChangedTimeListener changed) {
            super(view);
            rcv_time_text = view.findViewById(R.id.item_time_text);
            rcv_time = view.findViewById(R.id.item_clock);
            mCallback_m = changed;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds_time, parent, false);
        return new ViewHolder(view, changed);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rcv_time.setText(timeData.get(position).getTime());
        switch (position){
            case 0-> {holder.rcv_time_text.setText(R.string.dialog_meds_time_sw_morning_title);}
            case 1->{holder.rcv_time_text.setText(R.string.dialog_meds_time_sw_afternoon_title);}
            case 2-> {holder.rcv_time_text.setText(R.string.dialog_meds_time_sw_evening_title);}
            case 3->{holder.rcv_time_text.setText(R.string.dialog_meds_time_sw_midnight_title);}
        }

        holder.rcv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeData data = timeData.get(holder.getAdapterPosition());
                String[] times = data.getTime().split(":");
                int hour = Integer.parseInt(times[0]), min = Integer.parseInt(times[1]);

                final MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTitleText(R.string.dialog_meds_time_picker_title)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                        .setHour(hour).setMinute(min)
                        .setPositiveButtonText(R.string.dialog_add_form_submit)
                        .setNegativeButtonText(R.string.dialog_add_form_cancel).build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int minHour = 0, maxHour = 0;
                        switch (holder.getAdapterPosition()){
                            case 0-> {minHour = 0; maxHour = 8;}
                            case 1->{minHour = 9; maxHour = 14;}
                            case 2-> {minHour = 15; maxHour = 19;}
                            case 3->{minHour = 20; maxHour = 23;}
                        }
                        if(timePicker.getHour() > maxHour){
                            holder.mCallback_m.onChangedTime(data,
                                    new TimeData(String.format("%02d:%02d",maxHour, timePicker.getMinute()),
                                            MedsData.ConsumeTime.values()[holder.getAdapterPosition()]),
                                    holder.getAdapterPosition());
                            Toast.makeText(context, R.string.dialog_meds_time_picker_min_max_error, Toast.LENGTH_SHORT).show();
                        }
                        else if(timePicker.getHour() < minHour){
                            holder.mCallback_m.onChangedTime(data,
                                    new TimeData(String.format("%02d:%02d",minHour, timePicker.getMinute()),
                                    MedsData.ConsumeTime.values()[holder.getAdapterPosition()]),
                                    holder.getAdapterPosition());
                            Toast.makeText(context, R.string.dialog_meds_time_picker_min_max_error, Toast.LENGTH_SHORT).show();
                        }
                        else
                            holder.mCallback_m.onChangedTime(data,
                                    new TimeData(String.format("%02d:%02d",timePicker.getHour(), timePicker.getMinute()),
                                    MedsData.ConsumeTime.values()[holder.getAdapterPosition()]),
                                    holder.getAdapterPosition());
                    }
                });
                timePicker.show(fragmentManager, "MATERIAL_TIMEPICKER");
            }
        });
    }

    public void changeItem(int position){
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {return timeData.size();}
}
