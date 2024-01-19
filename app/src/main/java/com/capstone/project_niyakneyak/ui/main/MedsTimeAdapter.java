package com.capstone.project_niyakneyak.ui.main;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.ui.fragment.OnChangedTimeListener;
import com.capstone.project_niyakneyak.data.model.TimeData;
import com.capstone.project_niyakneyak.ui.fragment.OnDeleteTimeListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MedsTimeAdapter extends RecyclerView.Adapter<MedsTimeAdapter.ViewHolder> {
    private Context context;
    private List<TimeData> timeData;
    private OnChangedTimeListener changed;
    private OnDeleteTimeListener deleted;

    public MedsTimeAdapter(Context context, List<TimeData> timeData, OnChangedTimeListener changed, OnDeleteTimeListener deleted){
        this.context = context; this.timeData = timeData; this.changed = changed; this.deleted = deleted;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rcv_time;
        private OnChangedTimeListener mCallback_m;
        private OnDeleteTimeListener mCallback_d;

        public ViewHolder(@NonNull View view, OnChangedTimeListener changed, OnDeleteTimeListener deleted) {
            super(view);
            rcv_time = view.findViewById(R.id.item_clock);
            mCallback_m = changed;
            mCallback_d = deleted;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds_time, parent, false);
        return new ViewHolder(view, changed, deleted);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rcv_time.setText(timeData.get(position).getTime());
        holder.rcv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = 0, minute = 0;
                TimeData data = timeData.get(holder.getAdapterPosition());
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                try {
                    Date cur_time = parser.parse(data.getTime());
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(cur_time);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                final TimePickerDialog timeDlg = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        holder.mCallback_m.onChangedTime(
                                String.format("%02d:%02d", hourOfDay, minute),
                                holder.getAdapterPosition());
                    }
                }, hour, minute, true);
                timeDlg.show();
            }
        });

        holder.rcv_time.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder notification = new AlertDialog.Builder(context);
                notification.setTitle(R.string.dialog_meds_time_delete_notification_title);
                notification.setMessage(R.string.dialog_meds_time_delete_notification_msg);

                notification.setPositiveButton(R.string.dialog_meds_time_delete_notification_pos_btn, (dialog, which) -> {
                    holder.mCallback_d.onDeleteTime(holder.getAdapterPosition());
                });
                notification.setNegativeButton(R.string.dialog_meds_time_delete_notificatino_neg_btn, (dialog, which) -> {});
                notification.show();
                return true;
            }
        });

    }

    public void addItem(int position){
        notifyItemInserted(position);
    }
    public void changeItem(int position){
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {return timeData.size();}
}
