package com.capstone.project_niyakneyak.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.List;


public class MedsInfoAdapter extends RecyclerView.Adapter<MedsInfoAdapter.ViewHolder> {
    private List<MedsData> medsData;

    public MedsInfoAdapter(List<MedsData> medsData){this.medsData = medsData;}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rcv_title;
        private TextView rcv_detail;
        private ImageButton modifyBtn;
        private ImageButton deleteBtn;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            rcv_title = itemView.findViewById(R.id.item_title);
            rcv_detail = itemView.findViewById(R.id.item_detail);
            modifyBtn = itemView.findViewById(R.id.item_modify_button);
            deleteBtn = itemView.findViewById(R.id.item_delete_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_meds,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rcv_title.setText(medsData.get(position).getMeds_name());
        holder.rcv_detail.setText(medsData.get(position).getMeds_detail());
        holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {return medsData.size();}

    public void setItems(List<MedsData> medsData){
        this.medsData = medsData;
        notifyDataSetChanged();
    }
}
