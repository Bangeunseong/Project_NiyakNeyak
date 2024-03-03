package com.capstone.project_niyakneyak.ui.main.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.ui.main.listener.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.main.listener.OnDeleteDataListener;
import com.capstone.project_niyakneyak.ui.main.fragment.DataSettingDialog;

import java.util.List;


public class MainDataAdapter extends RecyclerView.Adapter<MainDataAdapter.ViewHolder> {
    private final List<MedsData> medsData;
    private final FragmentManager fragmentManager;
    private final OnChangedDataListener onChangedDataListener;
    private final OnDeleteDataListener onDeleteDataListener;

    public MainDataAdapter(FragmentManager fragmentManager, List<MedsData> medsData, OnChangedDataListener onChangedDataListener, OnDeleteDataListener onDeleteDataListener){
        this.fragmentManager = fragmentManager; this.medsData = medsData; this.onChangedDataListener = onChangedDataListener; this.onDeleteDataListener = onDeleteDataListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rcv_title;
        private final TextView rcv_detail;
        private final TextView rcv_duration;
        private final ImageButton modifyBtn;
        private final ImageButton deleteBtn;
        private final OnDeleteDataListener onDeleteDataListener;
        public ViewHolder(View view, OnDeleteDataListener onDeleteDataListener) {
            super(view);
            // Define click listener for the ViewHolder's View
            rcv_title = itemView.findViewById(R.id.item_title);
            rcv_detail = itemView.findViewById(R.id.item_detail);
            rcv_duration = itemView.findViewById(R.id.item_duration);

            modifyBtn = itemView.findViewById(R.id.item_modify_button);
            deleteBtn = itemView.findViewById(R.id.item_delete_button);

            this.onDeleteDataListener = onDeleteDataListener;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds,parent,false);
        return new ViewHolder(view, onDeleteDataListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rcv_title.setText(medsData.get(position).getMeds_name());

        if(medsData.get(position).getMeds_detail() != null)
            holder.rcv_detail.setText(String.format("Detail: %s",medsData.get(position).getMeds_detail()));
        else holder.rcv_detail.setText(String.format("Detail: %s", "None"));
        if(medsData.get(position).getMeds_start_date() != null && medsData.get(position).getMeds_end_date() != null)
            holder.rcv_duration.setText(String.format("Duration: %s~%s",
                    medsData.get(position).getMeds_start_date(),
                    medsData.get(position).getMeds_end_date()));
        else holder.rcv_duration.setText(String.format("Duration: %s", "None"));

        holder.modifyBtn.setOnClickListener(v -> {
            DialogFragment changeDataDialog = new DataSettingDialog(null, onChangedDataListener);
            Bundle bundle = new Bundle();
            bundle.putParcelable("BeforeModify", medsData.get(position));
            changeDataDialog.setArguments(bundle);
            changeDataDialog.show(fragmentManager, "DIALOG_FRAGMENT");
        });

        holder.deleteBtn.setOnClickListener(v -> {
            MedsData data = medsData.get(holder.getAdapterPosition());
            holder.onDeleteDataListener.onDeletedData(data);
        });
    }

    @Override
    public int getItemCount() {return medsData.size();}

    public void addItem(int position) { notifyItemInserted(position); }
    public void modifyItem(int position) { notifyItemChanged(position); }
    public void removeItem(int position){
        notifyItemRemoved(position);
    }
}
