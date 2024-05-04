package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCheckItemBinding
import com.capstone.project_niyakneyak.main.listener.OnCheckedChecklistListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

/**
 *
 */
open class CheckAlarmAdapter(query: Query, private val medicineData: MedicineData, private val onCheckedChecklistListener: OnCheckedChecklistListener) :
    FireStoreAdapter<CheckAlarmAdapter.ViewHolder>(query){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerCheckItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), medicineData, onCheckedChecklistListener)
    }

    inner class ViewHolder(val binding: ItemRecyclerCheckItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot, medicineData: MedicineData, listener: OnCheckedChecklistListener){
            val alarm = snapshot.toObject<Alarm>() ?: return

            binding.clockTimeText.text = String.format("%02d:%02d",alarm.hour, alarm.min)
            if(alarm.isRecurring) {
                binding.clockRecursionIcon.contentDescription = "isRecurring"
                binding.clockRecursionIcon.setImageResource(R.drawable.ic_repeat_icon)
            } else {
                binding.clockRecursionIcon.contentDescription = "notRecurring"
                binding.clockRecursionIcon.setImageResource(R.drawable.ic_repeat_once_icon)
            }
            if(alarm.isVibrate) {
                binding.clockVibrationIcon.contentDescription = "isVibrating"
                binding.clockVibrationIcon.setImageResource(R.drawable.ic_sound_on_icon)
            }
            else {
                binding.clockVibrationIcon.contentDescription = "notVibrating"
                binding.clockVibrationIcon.setImageResource(R.drawable.ic_sound_off_icon)
            }
            binding.clockCheckBox.setOnClickListener { listener.onItemClicked(medicineData, alarm) }
        }
    }
}