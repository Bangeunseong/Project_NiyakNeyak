package com.capstone.project_niyakneyak.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedsBinding
import com.capstone.project_niyakneyak.ui.main.listener.OnMedicationChangedListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

open class MedicationAdapter(query: Query, private val listener: OnMedicationChangedListener) :
    FireStoreAdapter<MedicationAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerMedsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(val binding: ItemRecyclerMedsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot, listener: OnMedicationChangedListener?){
            val medsData = snapshot.toObject<MedsData>() ?: return

            binding.itemTitle.text = medsData.medsName
            if (medsData.medsDetail != null) binding.itemDetail.text =
                String.format("Detail: %s", medsData.medsDetail)
            else binding.itemDetail.text = String.format("Detail: %s", "None")
            if (medsData.medsStartDate != null && medsData.medsEndDate != null)
                binding.itemDuration.text = String.format("Duration: %s~%s", medsData.medsStartDate, medsData.medsEndDate)
            else binding.itemDuration.text = String.format("Duration: %s", "None")
            binding.itemModifyButton.setOnClickListener { listener?.onModifyBtnClicked(snapshot) }
            binding.itemDeleteButton.setOnClickListener { listener?.onDeleteBtnClicked(snapshot) }
        }
    }
}