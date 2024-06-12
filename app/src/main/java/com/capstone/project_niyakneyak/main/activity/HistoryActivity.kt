package com.capstone.project_niyakneyak.main.activity

import android.animation.ObjectAnimator
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineHistoryData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityHistoryBinding
import com.capstone.project_niyakneyak.main.adapter.MedicineHistoryAdapter
import com.capstone.project_niyakneyak.main.decorator.EventDecorator
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.TodayDecorator
import com.capstone.project_niyakneyak.main.viewmodel.HistoryActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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

    // Params for view model
    private var _viewModel: HistoryActivityViewModel? = null
    private val viewModel get() = _viewModel!!

    // Params for data fetch from firebase
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!

    // Params for recycler view
    private var query: Query? = null
    private var adapter: MedicineHistoryAdapter? = null

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

        // View Model
        _viewModel = ViewModelProvider(this)[HistoryActivityViewModel::class.java]

        // Setting Toolbar layout
        binding.toolbar7.setTitle(R.string.toolbar_history_activity)
        binding.toolbar7.setTitleTextAppearance(this@HistoryActivity, R.style.ToolbarTextAppearance)
        setSupportActionBar(binding.toolbar7)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar7.navigationIcon?.mutate().let {
            it?.setTint(Color.WHITE)
            binding.toolbar7.navigationIcon = it
        }

        // Initialize Firestore and FirebaseAuth
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        // Fetching Data from firestore and Setting Calendar Layout Invalidation Process
        setTimeSection(binding.calendar.currentDate.date)
        binding.calendar.addDecorator(TodayDecorator())
        binding.calendar.setDateSelected(CalendarDay.today(), true)
        viewModel.isOpened = true
        binding.calendar.setOnMonthChangedListener { _, date ->
            binding.calendar.setDateSelected(binding.calendar.selectedDate, false)
            setTimeSection(date.date)
            binding.calendar.setDateSelected(date, true)
            query = setQuery(date.date)
            adapter?.setQuery(query!!)
        }
        binding.calendar.setOnDateChangedListener { _, date, _ ->
            query = setQuery(date.date)
            adapter?.setQuery(query!!)
            if(!viewModel.isOpened){
                binding.contentHistoryViewShowHideBtn.setImageResource(R.drawable.ic_arrow_down)
                ObjectAnimator.ofFloat(binding.contentHistoryViewLayout, "translationY", (4f * Resources.getSystem().displayMetrics.density + 0.5f)).apply {
                    duration = 1000
                    start()
                }
                viewModel.isOpened = true
            }
        }

        // Setting Adapter
        query = setQuery(Date(System.currentTimeMillis()))
        query?.let {
            adapter = object: MedicineHistoryAdapter(it){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentHistoryNotFoundTxt.visibility = View.VISIBLE
                        binding.contentHistory.visibility = View.GONE
                    } else {
                        binding.contentHistoryNotFoundTxt.visibility = View.GONE
                        binding.contentHistory.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Toast.makeText(this@HistoryActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
            binding.contentHistory.adapter = adapter
        }
        binding.contentHistory.layoutManager = LinearLayoutManager(this)
        binding.contentHistory.addItemDecoration(HorizontalItemDecorator(10))

        // Adapter Layout Show, Hide Button Action
        binding.contentHistoryViewShowHideBtn.setOnClickListener {
            if(viewModel.isOpened){
                binding.contentHistoryViewShowHideBtn.setImageResource(R.drawable.ic_arrow_up)
                ObjectAnimator.ofFloat(binding.contentHistoryViewLayout, "translationY", (308f * Resources.getSystem().displayMetrics.density + 0.5f)).apply {
                    duration = 1000
                    start()
                }
            } else{
                binding.contentHistoryViewShowHideBtn.setImageResource(R.drawable.ic_arrow_down)
                ObjectAnimator.ofFloat(binding.contentHistoryViewLayout, "translationY", (4f * Resources.getSystem().displayMetrics.density + 0.5f)).apply {
                    duration = 1000
                    start()
                }
            }
            viewModel.isOpened = !viewModel.isOpened
        }
    }

    override fun onStart() {
        super.onStart()

        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        adapter?.stopListening()
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
            ).orderBy(MedicineHistoryData.FIELD_TIME_STAMP).get()
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

    private fun setQuery(targetDate: Date?): Query?{
        if(targetDate == null) return null

        val firstOfDay = Calendar.getInstance()
        firstOfDay.timeInMillis = targetDate.time
        firstOfDay.set(Calendar.HOUR_OF_DAY, 0)
        firstOfDay.set(Calendar.MINUTE, 0)
        firstOfDay.set(Calendar.SECOND, 0)

        val lastOfDay = Calendar.getInstance()
        lastOfDay.timeInMillis = targetDate.time
        lastOfDay.set(Calendar.HOUR_OF_DAY, 23)
        lastOfDay.set(Calendar.MINUTE, 59)
        lastOfDay.set(Calendar.SECOND, 59)

        return firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineHistoryData.COLLECTION_ID)
            .where(Filter.and(
                Filter.greaterThanOrEqualTo(MedicineHistoryData.FIELD_TIME_STAMP, Date(firstOfDay.timeInMillis)),
                Filter.lessThanOrEqualTo(MedicineHistoryData.FIELD_TIME_STAMP, Date(lastOfDay.timeInMillis)))
            ).orderBy(MedicineHistoryData.FIELD_TIME_STAMP)
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