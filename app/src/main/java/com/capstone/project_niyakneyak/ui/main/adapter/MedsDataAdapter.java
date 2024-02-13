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
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.ui.fragment.ModifyDialogFragment;
import com.capstone.project_niyakneyak.ui.listener.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.listener.OnDeleteDataListener;

import java.util.List;


public class MedsDataAdapter extends RecyclerView.Adapter<MedsDataAdapter.ViewHolder> {
    private final List<MedsData> medsData;
    private final FragmentManager fragmentManager;
    private final OnDeleteDataListener onDeleteDataListener;
    private final OnChangedDataListener onChangedDataListener;

    public MedsDataAdapter(FragmentManager fragmentManager, List<MedsData> medsData, OnChangedDataListener onChangedDataListener, OnDeleteDataListener onDeleteDataListener){
        this.fragmentManager = fragmentManager; this.medsData = medsData; this.onDeleteDataListener = onDeleteDataListener; this.onChangedDataListener = onChangedDataListener;
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
        else holder.rcv_detail.setText("Detail: ");
        if(medsData.get(position).getMeds_start_date() != null && medsData.get(position).getMeds_end_date() != null)
            holder.rcv_duration.setText(String.format("Duration: %s~%s",
                    medsData.get(position).getMeds_start_date(),
                    medsData.get(position).getMeds_end_date()));
        else holder.rcv_duration.setText("Duration: ");
        holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable("BeforeModify", medsData.get(holder.getAdapterPosition()));
                DialogFragment newModifyForm = new ModifyDialogFragment(onChangedDataListener);
                newModifyForm.setArguments(data);
                newModifyForm.show(fragmentManager, "dialog");
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedsData data = medsData.get(holder.getAdapterPosition());
                holder.onDeleteDataListener.onDeletedData(data);
            }
        });
    }

    @Override
    public int getItemCount() {return medsData.size();}

    public void addItem(int position){
        notifyItemInserted(position);
    }
    public void changeItem(int position){
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        notifyItemRemoved(position);
    }
}
