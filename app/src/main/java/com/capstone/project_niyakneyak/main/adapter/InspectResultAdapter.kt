package com.capstone.project_niyakneyak.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.databinding.ItemRecyclerOtherOptionsBinding
import com.google.firebase.firestore.Query

open class InspectResultAdapter(query: Query): FireStoreAdapter<InspectResultAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectResultAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(val binding: ItemRecyclerOtherOptionsBinding): RecyclerView.ViewHolder(binding.root){

    }
}