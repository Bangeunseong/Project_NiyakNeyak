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
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
        private final AlarmCheckAdapter adapter;
        private boolean visibility = false;

        public ViewHolder(@NonNull View view, OnCheckedAlarmListener onCheckedAlarmListener) {
            super(view);
            checkItemLayout = view.findViewById(R.id.check_list_item_layout);
            title = view.findViewById(R.id.check_title_text);
            detail = view.findViewById(R.id.check_detail_text);
            duration = view.findViewById(R.id.check_title_time);
            alarmList = view.findViewById(R.id.alarm_check_list);
            adapter = new AlarmCheckAdapter(onCheckedAlarmListener);
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
            adapter.setAlarms(getAlarms(data.getAlarms(), alarms));
        }

        private List<Alarm> getAlarms(List<Integer> includedAlarms, List<Alarm> alarms){
            List<Alarm> included = new ArrayList<>();
            for(Alarm alarm : alarms){
                if(includedAlarms.contains(alarm.getAlarmCode()))
                    included.add(alarm);
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
