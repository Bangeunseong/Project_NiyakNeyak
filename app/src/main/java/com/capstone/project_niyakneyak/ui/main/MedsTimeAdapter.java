package com.capstone.project_niyakneyak.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.TimeData;

import java.util.List;

public class MedsTimeAdapter extends RecyclerView.Adapter<MedsTimeAdapter.ViewHolder> {
    private List<TimeData> timeData;
    public MedsTimeAdapter(List<TimeData> timeData){this.timeData = timeData;}
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rcv_time;
        private Switch rcv_time_switch;
        public ViewHolder(@NonNull View view) {
            super(view);
            rcv_time = view.findViewById(R.id.item_clock);
            rcv_time_switch = view.findViewById(R.id.item_switch);
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
        holder.rcv_time.setText(timeData.get(position).getTime());
        holder.rcv_time_switch.setChecked(timeData.get(position).getState());
        holder.rcv_time_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timeData.get(holder.getAdapterPosition()).setState(isChecked);
            }
        });
    }

    public void setItems(List<TimeData> meds_time){
        timeData = meds_time;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {return timeData.size();}
}
