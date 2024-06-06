package com.capstone.project_niyakneyak.main.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.medication_model.MedicineHistoryData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCheckItemBinding
import com.capstone.project_niyakneyak.main.fragment.AlarmFragment
import com.capstone.project_niyakneyak.main.listener.OnCheckedChecklistListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 *
 */
open class CheckAlarmAdapter(query: Query, private val medicineData: MedicineData, private val onCheckedChecklistListener: OnCheckedChecklistListener) :
    FireStoreAdapter<CheckAlarmAdapter.ViewHolder>(query){
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!
    private var documents: MutableList<MedicineHistoryData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
        documents = mutableListOf()

        val start = Calendar.getInstance(Locale.KOREA)
        val end = Calendar.getInstance(Locale.KOREA)
        start.timeInMillis =  System.currentTimeMillis()
        end.timeInMillis = System.currentTimeMillis()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        end.set(Calendar.HOUR_OF_DAY, 23)
        end.set(Calendar.MINUTE, 59)
        end.set(Calendar.SECOND, 59)

        firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineHistoryData.COLLECTION_ID)
            .where(
                Filter.and(
                    Filter.greaterThanOrEqualTo(MedicineHistoryData.FIELD_TIME_STAMP, Date(start.timeInMillis)),
                    Filter.lessThanOrEqualTo(MedicineHistoryData.FIELD_TIME_STAMP, Date(end.timeInMillis)),
                    Filter.equalTo(MedicineHistoryData.FIELD_MEDICINE_ID, medicineData.medsID))
            ).get().addOnSuccessListener {
                if(!it.isEmpty){
                    documents?.clear()
                    for(document in it.documents){
                        val medicineHistoryData = document.toObject<MedicineHistoryData>() ?: continue
                        documents?.add(medicineHistoryData)
                    }
                    notifyDataSetChanged()
                }
            }

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

            if(documents != null){
                for(document in documents!!){
                    if(document.alarmCode == alarm.alarmCode){
                        Log.w("CheckAlarmAdapter", "Found Medicine: ${medicineData.medsID}, ${alarm.alarmCode}")
                        binding.clockCheckBox.isChecked = true
                        binding.clockCheckBox.isEnabled = false
                    }
                }
            }

            binding.clockCheckBox.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("주의!")
                builder.setMessage(String.format("${medicineData.itemName}을 %02d:%02d에 맞춰 드셨습니까?\n이를 기록하시면 취소할 수 없습니다!", alarm.hour, alarm.min))
                builder.setPositiveButton("네") { _: DialogInterface?, _: Int ->
                    listener.onItemClicked(medicineData, alarm)
                    binding.clockCheckBox.isChecked = true
                    binding.clockCheckBox.isEnabled = false
                }
                builder.setNegativeButton("아니요") { _: DialogInterface?, _: Int ->
                    binding.clockCheckBox.isChecked = false
                }
                builder.create().show()
            }
        }
    }
}