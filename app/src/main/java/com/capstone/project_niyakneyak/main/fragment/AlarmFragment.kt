package com.capstone.project_niyakneyak.main.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.databinding.FragmentAlarmListBinding
import com.capstone.project_niyakneyak.main.activity.AlarmSettingActivity
import com.capstone.project_niyakneyak.main.adapter.AlarmAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.viewmodel.AlarmViewModel
import com.capstone.project_niyakneyak.main.listener.OnAlarmChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

/**
 * This Fragment is used for showing currently registered alarmList.
 * This Fragment implements [OnAlarmChangedListener] to update alarm data
 * by using [OnAlarmChangedListener.onToggle], [OnAlarmChangedListener.onItemClick],
 * [OnAlarmChangedListener.onDelete]
 */
class AlarmFragment : Fragment(), OnAlarmChangedListener {
    // Field
    private lateinit var binding: FragmentAlarmListBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var viewModel: AlarmViewModel

    private var adapter: AlarmAdapter? = null
    private var query: Query? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        FirebaseFirestore.setLoggingEnabled(true)
        firestore = Firebase.firestore
        query = firestore.collection("alarms")
            .orderBy(Alarm.FIELD_ALARM_CODE, Query.Direction.ASCENDING)

        query?.let {
            adapter = object: AlarmAdapter(it, this@AlarmFragment){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentTimeLeftBeforeAlarm.setText(R.string.dialog_meds_time_timer_error)
                        binding.contentTimeTable.visibility = View.GONE
                    }
                    else {
                        binding.contentTimeTable.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
            binding.contentTimeTable.adapter = adapter
        }

        binding.contentTimeTable.setHasFixedSize(false)
        binding.contentTimeTable.layoutManager = LinearLayoutManager(activity)
        binding.contentTimeTable.addItemDecoration(HorizontalItemDecorator(10))
        binding.contentTimeTable.addItemDecoration(VerticalItemDecorator(20))
    }

    override fun onStart() {
        super.onStart()

        // Start Listening Data changes from firebase when activity starts
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop Listening Data changes from firebase when activity stops
        adapter?.stopListening()
    }

    override fun onDelete(snapshot: DocumentSnapshot, alarm: Alarm) {
        val alarmRef = firestore.collection("alarms").document(snapshot.id)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Warning!")
        builder.setMessage("Do you want to delete this timer?")
        builder.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
            if (alarm.isStarted) alarm.cancelAlarm(requireContext())
            alarmRef.delete().addOnSuccessListener {
                Log.w(TAG, "Delete Alarm Success")
            }.addOnFailureListener { Log.w(TAG, "Delete Alarm Failed") }
        }
        builder.setNegativeButton("CANCEL") { dialog: DialogInterface?, which: Int -> }
        builder.create().show()
    }

    override fun onItemClick(snapshot: DocumentSnapshot, alarm: Alarm) {
        showAlarmSettingDialog(snapshot, alarm)
    }

    private fun showAlarmSettingDialog(snapshot: DocumentSnapshot, alarm: Alarm) {
        val intent = Intent(context, AlarmSettingActivity::class.java)
        intent.putExtra("snapshot_id",snapshot.id)
        intent.putExtra(getString(R.string.arg_alarm_obj),alarm)
        startActivity(intent)
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSignedIn && Firebase.auth.currentUser == null
    }

    //Functions for getting time difference between next time and current time
    /*private fun showTime() {

        val timeDifference = timeDifference
        if (timeDifference != null) {
            val days = timeDifference / (24 * 60 * 60 * 1000)
            val hours = timeDifference % (24 * 60 * 60 * 1000) / (60 * 60 * 1000)
            val minutes = timeDifference % (24 * 60 * 60 * 1000) % (60 * 60 * 1000) / (60 * 1000)
            binding.contentTimeLeftBeforeAlarm.text = String.format(
                "%d Days %d hours %d minutes left for next timer rings",
                days,
                hours,
                minutes
            )
        } else binding.contentTimeLeftBeforeAlarm.setText(R.string.dialog_meds_time_timer_error)
        handler!!.postDelayed(runnable!!, 60000)
    }

    private val timeDifference: Long?
        get() {
            val today = Calendar.getInstance()
            today.timeInMillis = System.currentTimeMillis()
            var timeDifference: Long? = null
            if (alarms == null) return null
            for (alarm in alarms!!) {
                if (!alarm.isStarted) continue
                timeDifference = getMinTimeDifference(alarm, today, timeDifference)
            }
            return timeDifference
        }

    private fun getMinTimeDifference(alarm: Alarm?, today: Calendar, timeDifference: Long?): Long {
        var timeDifference = timeDifference
        val alarmTime: Calendar = if (!alarm!!.isRecurring) {
            getAlarmTime(alarm, today)
        } else {
            getRecurringAlarmTime(alarm, today)
        }
        timeDifference =
            minTimeDifference(timeDifference, alarmTime.timeInMillis - today.timeInMillis)
        return timeDifference
    }*/

    private fun compareTime(target: Calendar, subject: Calendar): Boolean {
        return target.timeInMillis > subject.timeInMillis
    }

    private fun getAlarmTime(alarm: Alarm?, today: Calendar): Calendar {
        val alarmTime = Calendar.getInstance()
        alarmTime.timeInMillis = System.currentTimeMillis()
        alarmTime[Calendar.HOUR_OF_DAY] = alarm!!.hour
        alarmTime[Calendar.MINUTE] = alarm.min
        alarmTime[Calendar.SECOND] = 0
        alarmTime[Calendar.MILLISECOND] = 0
        if (!compareTime(alarmTime, today)) alarmTime.add(Calendar.DAY_OF_MONTH, 1)
        return alarmTime
    }

    private fun getRecurringAlarmTime(alarm: Alarm?, today: Calendar): Calendar {
        val alarmTime = getAlarmTime(alarm, today)
        var flag = 0
        var i = alarmTime[Calendar.DAY_OF_WEEK] - 1
        while (flag < 7) {
            when (i) {
                0 -> { if (alarm!!.isSun) return alarmTime }
                1 -> { if (alarm!!.isMon) return alarmTime }
                2 -> { if (alarm!!.isTue) return alarmTime }
                3 -> { if (alarm!!.isWed) return alarmTime }
                4 -> { if (alarm!!.isThu) return alarmTime }
                5 -> { if (alarm!!.isFri) return alarmTime }
                6 -> { if (alarm!!.isSat) return alarmTime }
            }
            alarmTime.add(Calendar.DAY_OF_MONTH, 1)
            i = (i + 1) % 7
            flag++
        }
        return alarmTime
    }

    private fun minTimeDifference(target: Long?, subject: Long): Long {
        if (target == null) return subject
        return if (target < subject) target else subject
    }

    companion object{
        private const val TAG = "ALARM_FRAGMENT"
        private const val DIALOG_TAG = "ALARM_DIALOG"
    }
}