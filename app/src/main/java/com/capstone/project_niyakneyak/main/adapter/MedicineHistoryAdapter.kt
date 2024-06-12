package com.capstone.project_niyakneyak.main.adapter

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.data.medication_model.MedicineHistoryData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerHistoryBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Locale

open class MedicineHistoryAdapter(query: Query) :
    FireStoreAdapter<MedicineHistoryAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

    inner class ViewHolder(val binding: ItemRecyclerHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(snapshot: DocumentSnapshot){
            val historyData = snapshot.toObject<MedicineHistoryData>() ?: return

            // Setting Time Value
            val timeData = Calendar.getInstance()
            timeData.time = historyData.timeStamp
            if(timeData.get(Calendar.HOUR_OF_DAY) < 12)
                binding.contentHistoryTime.text = String.format("오전 ${timeData.get(Calendar.HOUR)}:%02d", timeData.get(Calendar.MINUTE))
            else binding.contentHistoryTime.text = String.format("오후 ${timeData.get(Calendar.HOUR)}:%02d", timeData.get(Calendar.MINUTE))

            // Setting Item Name
            binding.contentHistoryItemName.text = historyData.itemName

            // Setting Item Amount
            binding.contentHistoryItemAmount.text = String.format("${historyData.dailyAmount}정")
        }
    }
}