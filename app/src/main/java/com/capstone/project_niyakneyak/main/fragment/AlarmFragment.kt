package com.capstone.project_niyakneyak.main.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentAlarmListBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.activity.AlarmSettingActivity
import com.capstone.project_niyakneyak.main.adapter.AlarmAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.viewmodel.AlarmViewModel
import com.capstone.project_niyakneyak.main.listener.OnAlarmChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.toObject
import java.util.Calendar

/**
 * This Fragment is used for showing currently registered alarmList.
 * This Fragment implements [OnAlarmChangedListener] to update alarm data
 * by using [OnAlarmChangedListener.onItemClick], [OnAlarmChangedListener.onDelete]
 */
class AlarmFragment : Fragment(), OnAlarmChangedListener {
    // Field
    private var _binding: FragmentAlarmListBinding? = null
    private val binding get() = _binding!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _viewModel: AlarmViewModel? = null
    private val viewModel get() = _viewModel!!

    private var adapter: AlarmAdapter? = null
    private var query: Query? = null

    private val loginProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            viewModel.isSignedIn = true
        }
    }
    private val alarmSettingProcessLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            Toast.makeText(context, "Modified Timer!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Modifying Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        FirebaseFirestore.setLoggingEnabled(true)
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        if(firebaseAuth.currentUser != null){
            query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID)
                .orderBy(Alarm.FIELD_HOUR, Query.Direction.ASCENDING)
                .orderBy(Alarm.FIELD_MINUTE, Query.Direction.ASCENDING)
        }

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
        if(shouldStartSignIn()){
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("request_token", 0)
            loginProcessLauncher.launch(intent)
            return
        }

        // Start Listening Data changes from firebase when activity starts
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop Listening Data changes from firebase when activity stops
        adapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
        _firestore = null
        _firebaseAuth = null
        adapter = null
    }

    override fun onDelete(snapshot: DocumentSnapshot) {
        val alarmRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID).document(snapshot.id)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Warning!")
        builder.setMessage("Do you want to delete this timer?")
        builder.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
            firestore.runTransaction {transaction ->
                val alarm = transaction.get(alarmRef).toObject<Alarm>()
                if (alarm!!.isStarted) alarm.cancelAlarm(requireContext())
                alarmRef.delete()
            }.addOnSuccessListener {
                Log.w(TAG, "Delete Alarm Success")
            }.addOnFailureListener { Log.w(TAG, "Delete Alarm Failed") }
        }
        builder.setNegativeButton("CANCEL") { _: DialogInterface?, _: Int -> }
        builder.create().show()
    }

    override fun onItemClick(snapshot: DocumentSnapshot) {
        showAlarmSettingDialog(snapshot)
    }

    private fun showAlarmSettingDialog(snapshot: DocumentSnapshot) {
        val intent = Intent(context, AlarmSettingActivity::class.java)
        intent.putExtra("snapshot_id",snapshot.id)
        alarmSettingProcessLauncher.launch(intent)
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSignedIn && firebaseAuth.currentUser == null
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