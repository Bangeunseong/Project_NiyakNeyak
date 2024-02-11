package com.capstone.project_niyakneyak.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

import java.util.List;

public class AlarmDataAdapter extends RecyclerView.Adapter<AlarmDataAdapter.ViewHolder> {
    private final Context context;
    private final FragmentManager fragmentManager;
    private final List<Alarm> timeData;

    public AlarmDataAdapter(Context context, FragmentManager fragmentManager, List<Alarm> timeData){
        this.context = context; this.fragmentManager = fragmentManager;
        this.timeData = timeData;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rcv_time_text;
        private final TextView rcv_time;

        public ViewHolder(@NonNull View view) {
            super(view);
            rcv_time_text = view.findViewById(R.id.item_time_text);
            rcv_time = view.findViewById(R.id.item_clock);
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

    }

    @Override
    public int getItemCount() {return timeData.size();}
}
