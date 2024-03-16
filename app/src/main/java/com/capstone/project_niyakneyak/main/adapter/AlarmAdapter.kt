package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.databinding.ItemRecyclerTimeBinding
import com.capstone.project_niyakneyak.main.listener.OnAlarmChangedListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Locale

open class AlarmAdapter(query: Query, private val onAlarmChangedListener: OnAlarmChangedListener) :
    FireStoreAdapter<AlarmAdapter.ViewHolder>(query){

    class ViewHolder(val binding: ItemRecyclerTimeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot, listener: OnAlarmChangedListener){
            val alarm = snapshot.toObject<Alarm>() ?: return

            binding.itemClock.text = String.format(Locale.KOREAN, "%02d:%02d", alarm.hour, alarm.min)
            if(alarm.isStarted)
                binding.itemAlarmLayout.setBackgroundResource(R.drawable.item_recycler_bg_primary)
            else binding.itemAlarmLayout.setBackgroundResource(R.drawable.item_recycler_bg_disabled)
            binding.weeklyDateDisplay.text = alarm.daysText
            if(alarm.title!!.isNotEmpty()) binding.alarmTitle.text = alarm.title
            else binding.alarmTitle.text = "My Alarm"
            binding.itemEditBtn.setOnClickListener { listener.onItemClick(snapshot, alarm) }
            binding.itemDeleteBtn.setOnClickListener { listener.onDelete(snapshot, alarm) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), onAlarmChangedListener)
    }
}