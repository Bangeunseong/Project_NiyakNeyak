package com.capstone.project_niyakneyak.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<MedsData> medsData;

    public RecyclerAdapter(List<MedsData> medsData){this.medsData = medsData;}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView rcv_title;
        private TextView rcv_detail;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageView = itemView.findViewById(R.id.img_rcv_photo);
            rcv_title = itemView.findViewById(R.id.item_title);
            rcv_detail = itemView.findViewById(R.id.item_detail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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
        holder.imageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {return medsData.size();}
}
