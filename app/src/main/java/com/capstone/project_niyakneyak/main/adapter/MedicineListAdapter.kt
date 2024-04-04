package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedicineBinding

//TODO: Modify Adapter
open class MedicineListAdapter: RecyclerView.Adapter<MedicineListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerMedicineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medicineData: MedicineData){
            binding.medicineName.text = medicineData.itemName
            binding.medicineCategory.text = medicineData.pdtType
            binding.medicineEntp.text = medicineData.entpName
            binding.medicineMtr.text = medicineData.itemIngrName
            Glide.with(itemView).load(medicineData.bigPrdtImgUrl).into(binding.medicineImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}