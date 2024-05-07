package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCurrentMedicineBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

open class CurrentMedicineAdapter(query: Query) : FireStoreAdapter<CurrentMedicineAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentMedicineAdapter.ViewHolder {
        return ViewHolder(ItemRecyclerCurrentMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

    inner class ViewHolder(val binding: ItemRecyclerCurrentMedicineBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot){
            val medsData = snapshot.toObject<MedicineData>() ?: return
            binding.medicineName.text = medsData.itemName
            binding.medicineCategory.text = medsData.pdtType
            binding.medicineEntp.text = medsData.entpName
            binding.medicineMtr.text = medsData.itemIngrName
            binding.medicineImg.contentDescription = medsData.itemName
            Glide.with(itemView).load(medsData.bigPrdtImgUrl).placeholder(R.drawable.baseline_medication_liquid_24).into(binding.medicineImg)
        }
    }
}