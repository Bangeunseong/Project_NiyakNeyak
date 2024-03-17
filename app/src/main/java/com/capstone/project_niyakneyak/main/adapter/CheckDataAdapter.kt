package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedsData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCheckBinding
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedsBinding
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import com.capstone.project_niyakneyak.main.listener.OnMedicationChangedListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Calendar

/**
 * This adapter is used for showing Medication info. which should be consumed in current date.
 * It needs [OnCheckedAlarmListener] to deliver which alarm is checked
 * When alarm is checked, [com.capstone.project_niyakneyak.main.fragment.CheckFragment]
 * will handle the process of recording
 */
open class CheckDataAdapter(query: Query, private val listener: OnCheckedAlarmListener) :
    FireStoreAdapter<CheckDataAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(val binding: ItemRecyclerCheckBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot, listener: OnCheckedAlarmListener){
            val alarm = snapshot.toObject<Alarm>() ?: return


        }
    }
}