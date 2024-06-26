package com.capstone.project_niyakneyak.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ItemRecyclerCheckBinding
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import com.capstone.project_niyakneyak.main.listener.OnCheckedChecklistListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * This adapter is used for showing Medication info. which should be consumed in current date.
 * It needs [OnCheckedAlarmListener] to deliver which alarm is checked
 * When alarm is checked, [com.capstone.project_niyakneyak.main.fragment.CheckFragment]
 * will handle the process of recording
 */
open class CheckMedicineAdapter(query: Query, private val listener: OnCheckedChecklistListener) :
    FireStoreAdapter<CheckMedicineAdapter.ViewHolder>(query) {
    private var secondQuery: Query? = null
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snapshot = getSnapshot(position)
        val medicineData = snapshot.toObject<MedicineData>() ?: return

        FirebaseFirestore.setLoggingEnabled(true)
        if (_firestore == null) _firestore = Firebase.firestore
        if (_firebaseAuth == null) _firebaseAuth = Firebase.auth

        if (firebaseAuth.currentUser != null) {
            secondQuery = getCurrentDateQuery(medicineData)
        }

        holder.bind(snapshot, secondQuery, listener)
    }

    inner class ViewHolder(val binding: ItemRecyclerCheckBinding): RecyclerView.ViewHolder(binding.root){
        private var adapter: CheckAlarmAdapter? = null
        fun bind(snapshot: DocumentSnapshot, query: Query?, listener: OnCheckedChecklistListener){
            val medicineData = snapshot.toObject<MedicineData>() ?: return

            binding.checkItemImage.contentDescription = String.format("${medicineData.itemName}")
            Glide.with(itemView).load(medicineData.bigPrdtImgUrl).placeholder(R.drawable.baseline_medication_liquid_24).into(binding.checkItemImage)
            binding.checkItemName.text = String.format("이름: ${medicineData.itemName}")
            binding.checkItemAmount.text = String.format("매 섭취량: %d알", medicineData.dailyAmount)
            binding.checkItemDetail.text = String.format("세부 사항: ${medicineData.medsDetail}")
            if(medicineData.medsStartDate != null && medicineData.medsEndDate != null){
                binding.checkItemDuration.text = String.format("섭취 기간: " + SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN).format(medicineData.medsStartDate!!) + "~" +
                        SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN).format(medicineData.medsEndDate!!))
            } else{
                binding.checkItemDuration.text = "None"
            }

            query?.let {
                adapter = object: CheckAlarmAdapter(it, medicineData, listener){
                    override fun onDataChanged() {
                        if(itemCount == 0){
                            binding.checkVisibilityBtn.isEnabled = false
                            if(binding.alarmCheckList.isVisible)
                                binding.alarmCheckList.visibility = View.GONE
                        }
                        else{
                            binding.checkVisibilityBtn.isEnabled = true
                        }
                    }

                    override fun onError(e: FirebaseFirestoreException) {
                        Log.w(TAG, "Terrible Error Occurred!: $it")
                    }
                }
                binding.alarmCheckList.adapter = adapter
            }
            binding.alarmCheckList.layoutManager = LinearLayoutManager(binding.root.context)

            binding.checkVisibilityBtn.setOnClickListener {
                if(binding.alarmCheckList.isVisible) {
                    adapter?.stopListening()
                    binding.alarmCheckList.visibility = View.GONE
                }
                else {
                    adapter?.startListening()
                    binding.alarmCheckList.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getCurrentDateQuery(medicineData: MedicineData): Query?{
        val currentDate = Calendar.getInstance()
        currentDate.timeInMillis = System.currentTimeMillis()
        return when(currentDate.get(Calendar.DAY_OF_WEEK)){
            Calendar.SUNDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_SUNDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.MONDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_MONDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.TUESDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_TUESDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.WEDNESDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_WEDNESDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.THURSDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_THURSDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.FRIDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_FRIDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.SATURDAY -> {
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID)
                    .where(Filter.and(Filter.arrayContains(Alarm.FIELD_MEDICATION_LIST, medicineData.medsID), Filter.equalTo(Alarm.FIELD_IS_STARTED, true), Filter.equalTo(Alarm.FIELD_IS_SATURDAY, true)))
                    .orderBy(Alarm.FIELD_HOUR)
                    .orderBy(Alarm.FIELD_MINUTE)
            }
            else -> {null}
        }
    }

    companion object{
        private const val TAG = "CHECK_ALARM_ADAPTER"
    }
}