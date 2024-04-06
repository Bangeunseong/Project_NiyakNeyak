package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCheckItemBinding
import com.capstone.project_niyakneyak.main.listener.OnCheckedMedicationListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

/**
 *
 */
open class CheckMedicationAdapter(query: Query, private val onCheckedMedicationListener: OnCheckedMedicationListener) :
    FireStoreAdapter<CheckMedicationAdapter.ViewHolder>(query){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerCheckItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), onCheckedMedicationListener)
    }

    inner class ViewHolder(val binding: ItemRecyclerCheckItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot, listener: OnCheckedMedicationListener){
            val medicationData = snapshot.toObject<MedicineData>() ?: return
            binding.medicationName.text = medicationData.itemName
            binding.medicationAmount.text = String.format("%d pills",medicationData.dailyAmount)
            binding.medicationDetail.text = medicationData.medsDetail
            binding.medicationCheckbox.setOnClickListener { listener.onItemClicked(medicationData) }
        }
    }
}