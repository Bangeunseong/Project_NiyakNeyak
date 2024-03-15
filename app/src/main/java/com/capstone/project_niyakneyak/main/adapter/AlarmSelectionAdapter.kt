package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedsTimeSelectionBinding
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Locale

/**
 * This Adapter is used for showing currently registered and selectable alarms.
 * When setting Medication Data, this adapter is used to describe which alarm is available.
 * Adapter needs [OnCheckedAlarmListener] to deliver checked alarm info to
 * [com.capstone.project_niyakneyak.main.activity.DataSettingActivity]
 */
open class AlarmSelectionAdapter(query: Query, private val medsID: String?, private val onCheckedAlarmListener: OnCheckedAlarmListener) :
    FireStoreAdapter<AlarmSelectionAdapter.ViewHolder>(query) {

    class ViewHolder(val binding: ItemRecyclerMedsTimeSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(snapshot: DocumentSnapshot, medsID: String?, onCheckedAlarmListener: OnCheckedAlarmListener) {
            val alarm = snapshot.toObject<Alarm>() ?: return

            binding.clockTime.text = String.format(Locale.KOREAN, "%02d:%02d", alarm.hour, alarm.min)
            binding.clockRecursion.text = alarm.daysText
            binding.clockCheckbox.isEnabled = alarm.isStarted
            if(medsID != null)
                binding.clockCheckbox.isChecked = alarm.medsList.contains(medsID.toLong())
            binding.clockCheckbox.setOnClickListener {
                onCheckedAlarmListener.onItemClicked(alarm)
            }
            if (!alarm.isStarted) binding.clockSelectionSingle.setBackgroundResource(R.drawable.item_recycler_bg_disabled)
            else binding.clockSelectionSingle.setBackgroundResource(R.drawable.item_recycler_bg_secondary)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerMedsTimeSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), medsID, onCheckedAlarmListener)
    }
}