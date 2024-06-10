package com.capstone.project_niyakneyak.main.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineHistoryData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityHistoryBinding
import com.capstone.project_niyakneyak.main.decorator.EventDecorator
import com.capstone.project_niyakneyak.main.decorator.TodayDecorator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar
import java.util.Date

class HistoryActivity: AppCompatActivity() {
    // Params for view binding
    private var _binding: ActivityHistoryBinding? = null
    private val binding get() = _binding!!

    // Params for data fetch from firebase
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!

    // Params for recycler view
    private var query: Query? = null

    // Params for date validation
    private var firstOfMonth: Calendar? = null
    private var lastOfMonth: Calendar? = null

    // BackPressed Callback
    private var callback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        _binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting Toolbar layout
        binding.toolbar7.setTitle(R.string.toolbar_history_activity)
        binding.toolbar7.setTitleTextAppearance(this@HistoryActivity, R.style.ToolbarTextAppearance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar7.navigationIcon?.mutate().let {
            it?.setTint(Color.WHITE)
            binding.toolbar7.navigationIcon = it
        }
        setSupportActionBar(binding.toolbar7)

        // Initialize Firestore and FirebaseAuth
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        // Fetching Data from firestore and Setting Calendar Layout Invalidation Process
        setTimeSection(binding.calendar.currentDate.date)
        binding.calendar.addDecorator(TodayDecorator())
        binding.calendar.setOnMonthChangedListener { widget, date ->
            setTimeSection(date.date)
        }
        binding.calendar.setOnDateChangedListener { widget, date, selected ->

        }
    }

    private fun setTimeSection(targetDate: Date){
        firstOfMonth = Calendar.getInstance()
        firstOfMonth!!.time = targetDate
        firstOfMonth!!.set(Calendar.DAY_OF_MONTH, 1)
        firstOfMonth!!.set(Calendar.HOUR_OF_DAY, 0)
        firstOfMonth!!.set(Calendar.MINUTE, 0)
        firstOfMonth!!.set(Calendar.SECOND, 0)

        lastOfMonth = Calendar.getInstance()
        lastOfMonth!!.time = targetDate
        val lastDay = lastOfMonth!!.getActualMaximum(Calendar.DAY_OF_MONTH)
        lastOfMonth!!.set(Calendar.DAY_OF_MONTH, lastDay)
        lastOfMonth!!.set(Calendar.HOUR_OF_DAY, 23)
        lastOfMonth!!.set(Calendar.MINUTE, 59)
        lastOfMonth!!.set(Calendar.SECOND, 59)

        firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineHistoryData.COLLECTION_ID)
            .where(Filter.and(
                Filter.greaterThanOrEqualTo(MedicineHistoryData.FIELD_TIME_STAMP, Date(firstOfMonth!!.timeInMillis)),
                Filter.lessThanOrEqualTo(MedicineHistoryData.FIELD_TIME_STAMP, Date(lastOfMonth!!.timeInMillis)))
            ).get()
            .addOnSuccessListener {
                val dateList = mutableSetOf<CalendarDay>()
                if(it.documents.isNotEmpty()){
                    for(document in it.documents){
                        val history = document.toObject<MedicineHistoryData>() ?: continue
                        dateList.add(CalendarDay.from(history.timeStamp))
                    }
                }
                binding.calendar.addDecorator(EventDecorator(dateList))
            }.addOnFailureListener {
                Log.w(TAG, "Error Occurred!: $it")
            }
    }

    companion object{
        private const val TAG = "History_Activity"
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        callback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback as OnBackPressedCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            // 다른 메뉴 아이템 처리
            else -> super.onOptionsItemSelected(item)
        }
    }
}