package com.capstone.project_niyakneyak.ui.main;

import android.os.Bundle;
import android.util.Log;
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
import com.capstone.project_niyakneyak.ui.fragment.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.fragment.OnDeleteDataListener;

import java.util.List;


public class MedsInfoAdapter extends RecyclerView.Adapter<MedsInfoAdapter.ViewHolder> {
    private List<MedsData> medsData;
    private FragmentManager fragmentManager;
    private OnDeleteDataListener communicator;
    private OnChangedDataListener changed_communicator;

    public MedsInfoAdapter(FragmentManager fragmentManager, List<MedsData> medsData, OnChangedDataListener changed_communicator, OnDeleteDataListener communicator){
        this.fragmentManager = fragmentManager; this.medsData = medsData; this.communicator = communicator; this.changed_communicator = changed_communicator;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rcv_title;
        private TextView rcv_detail;
        private ImageButton modifyBtn;
        private ImageButton deleteBtn;
        private OnDeleteDataListener mCallback;
        public ViewHolder(View view, OnDeleteDataListener communicator) {
            super(view);
            // Define click listener for the ViewHolder's View
            rcv_title = itemView.findViewById(R.id.item_title);
            rcv_detail = itemView.findViewById(R.id.item_detail);
            modifyBtn = itemView.findViewById(R.id.item_modify_button);
            deleteBtn = itemView.findViewById(R.id.item_delete_button);
            mCallback = communicator;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds,parent,false);
        return new ViewHolder(view, communicator);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rcv_title.setText(medsData.get(position).getMeds_name());
        holder.rcv_detail.setText(medsData.get(position).getMeds_detail());
        holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable("BeforeModify", medsData.get(holder.getAdapterPosition()));
                DialogFragment newModifyForm = new ModifyDialogFragment(changed_communicator);
                newModifyForm.setArguments(data);
                newModifyForm.show(fragmentManager, "dialog");
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedsData data = medsData.get(holder.getAdapterPosition());
                holder.mCallback.onDeletedData(data);
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
