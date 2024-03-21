package com.capstone.project_niyakneyak.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedsData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCheckBinding
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import com.capstone.project_niyakneyak.main.listener.OnCheckedMedicationListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

/**
 * This adapter is used for showing Medication info. which should be consumed in current date.
 * It needs [OnCheckedAlarmListener] to deliver which alarm is checked
 * When alarm is checked, [com.capstone.project_niyakneyak.main.fragment.CheckFragment]
 * will handle the process of recording
 */
open class CheckAlarmAdapter(query: Query, private val listener: OnCheckedMedicationListener) :
    FireStoreAdapter<CheckAlarmAdapter.ViewHolder>(query) {
    private var secondQuery: Query? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snapshot = getSnapshot(position)
        val alarm = snapshot.toObject<Alarm>() ?: return

        FirebaseFirestore.setLoggingEnabled(true)
        firestore = Firebase.firestore

        //TODO: Modify Query to filter medication info. by valid date
        secondQuery = firestore.collection("medications")
            .whereIn(MedsData.FIELD_ID, alarm.medsList)

        holder.bind(snapshot, secondQuery, listener)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.startListening()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.stopListening()
    }

    class ViewHolder(val binding: ItemRecyclerCheckBinding): RecyclerView.ViewHolder(binding.root){
        private lateinit var adapter: CheckMedicationAdapter
        fun bind(snapshot: DocumentSnapshot, query: Query?, listener: OnCheckedMedicationListener){
            val alarm = snapshot.toObject<Alarm>() ?: return
            binding.checkTimeText.text = String.format("%02d:%02d",alarm.hour, alarm.min)
            binding.checkTitleText.text = alarm.title

            query?.let {
                adapter = object: CheckMedicationAdapter(it, listener){
                    override fun onDataChanged() {
                        if(itemCount == 0){
                            binding.imageView.isEnabled = false
                            if(binding.alarmCheckList.isVisible)
                                binding.alarmCheckList.visibility = View.GONE
                        }
                        else{
                            binding.imageView.isEnabled = true
                        }
                    }

                    override fun onError(e: FirebaseFirestoreException) {
                        Log.w(TAG, "Terrible Error Occurred!: $it")
                    }
                }
                binding.alarmCheckList.adapter = adapter
            }
            binding.alarmCheckList.layoutManager = LinearLayoutManager(binding.root.context)

            binding.imageView.setOnClickListener {
                if(binding.alarmCheckList.isVisible) binding.alarmCheckList.visibility = View.GONE
                else binding.alarmCheckList.visibility = View.VISIBLE
            }
        }

        fun startListening(){
            adapter.startListening()
        }
        fun stopListening(){
            adapter.stopListening()
        }
    }

    companion object{
        private const val TAG = "CHECK_ALARM_ADAPTER"
    }
}