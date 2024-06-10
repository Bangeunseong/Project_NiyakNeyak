package com.capstone.project_niyakneyak.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedsBinding
import com.google.firebase.firestore.Query

class MedicineHistoryAdapter(query: Query) :
    FireStoreAdapter<MedicationAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MedicationAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(val binding: ItemRecyclerMedsBinding): RecyclerView.ViewHolder(binding.root) {

    }
}