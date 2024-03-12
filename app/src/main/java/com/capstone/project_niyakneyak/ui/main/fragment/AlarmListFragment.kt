package com.capstone.project_niyakneyak.ui.main.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.FragmentAlarmListBinding
import com.capstone.project_niyakneyak.ui.main.adapter.AlarmDataAdapter
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModel
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModelFactory
import com.capstone.project_niyakneyak.ui.main.listener.OnToggleAlarmListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.function.Consumer

/**
 * This Fragment is used for showing currently registered alarmList.
 * This Fragment implements [OnToggleAlarmListener] to update alarm data
 * by using [OnToggleAlarmListener.onToggle], [OnToggleAlarmListener.onItemClick],
 * [OnToggleAlarmListener.onDelete]
 */
class AlarmListFragment : Fragment(), OnToggleAlarmListener {
    private var binding: FragmentAlarmListBinding? = null
    private var adapter: AlarmDataAdapter? = null
    private var alarmListViewModel: AlarmListViewModel? = null
    private lateinit var firestore: FirebaseFirestore
    private var query: Query? = null
    private var alarms: List<Alarm>? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmListViewModel =
            ViewModelProvider(this, AlarmListViewModelFactory(requireActivity().application))[AlarmListViewModel::class.java]
        adapter = AlarmDataAdapter(this)
        alarmListViewModel!!.getAlarmsLiveData().observe(this) { alarms: List<Alarm>? ->
            if (alarms != null) {
                this.alarms = alarms
                adapter!!.setAlarms(alarms)
                showTime()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler()
        runnable = Runnable { showTime() }
        showTime()
        binding!!.contentTimeTable.setHasFixedSize(false)
        binding!!.contentTimeTable.layoutManager = LinearLayoutManager(activity)
        binding!!.contentTimeTable.adapter = adapter
        binding!!.contentTimeTable.addItemDecoration(HorizontalItemDecorator(10))
        binding!!.contentTimeTable.addItemDecoration(VerticalItemDecorator(20))
        binding!!.contentAlarmAdd.setOnClickListener { showAlarmSettingDialog(null) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler!!.removeCallbacks(runnable!!)
        runnable = null
        handler = null
        binding = null
    }

    // Override Methods for data manipulation
    override fun onToggle(alarm: Alarm) {
        if (alarm.isStarted) {
            alarm.cancelAlarm(requireContext())
            alarmListViewModel!!.update(alarm)
        } else {
            alarm.scheduleAlarm(requireContext())
            alarmListViewModel!!.update(alarm)
        }
    }

    override fun onDelete(alarm: Alarm) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Warning!")
        builder.setMessage("Do you want to delete this timer?")
        builder.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
            if (alarm.isStarted) alarm.cancelAlarm(requireContext())
            alarmListViewModel?.getPatientData()?.getMedsData()?.forEach(Consumer { medsData: MedsData ->
                medsData.alarms.remove(
                    alarm.alarmCode as Any
                )
            })
            alarmListViewModel!!.delete(alarm.alarmCode)
        }
        builder.setNegativeButton("CANCEL") { dialog: DialogInterface?, which: Int -> }
        builder.create().show()
    }

    override fun onItemClick(alarm: Alarm) {
        if (alarm.isStarted) alarm.cancelAlarm(requireContext())
        showAlarmSettingDialog(alarm)
    }

    private fun showAlarmSettingDialog(alarm: Alarm?) {
        val alarmSettingDialog: DialogFragment = AlarmSettingDialog()
        val bundle = Bundle()
        bundle.putParcelable(getString(R.string.arg_alarm_obj), alarm)
        alarmSettingDialog.arguments = bundle
        alarmSettingDialog.show(requireActivity().supportFragmentManager, "ALARM_DIALOG_FRAGMENT")
    }

    //Functions for getting time difference between next time and current time
    private fun showTime() {
        val timeDifference = timeDifference
        if (timeDifference != null) {
            val days = timeDifference / (24 * 60 * 60 * 1000)
            val hours = timeDifference % (24 * 60 * 60 * 1000) / (60 * 60 * 1000)
            val minutes = timeDifference % (24 * 60 * 60 * 1000) % (60 * 60 * 1000) / (60 * 1000)
            binding!!.contentTimeLeftBeforeAlarm.text = String.format(
                "%d Days %d hours %d minutes left for next timer rings",
                days,
                hours,
                minutes
            )
        } else binding!!.contentTimeLeftBeforeAlarm.setText(R.string.dialog_meds_time_timer_error)
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
    }

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
                0 -> {
                    if (alarm!!.isSun) return alarmTime
                }

                1 -> {
                    if (alarm!!.isMon) return alarmTime
                }

                2 -> {
                    if (alarm!!.isTue) return alarmTime
                }

                3 -> {
                    if (alarm!!.isWed) return alarmTime
                }

                4 -> {
                    if (alarm!!.isThu) return alarmTime
                }

                5 -> {
                    if (alarm!!.isFri) return alarmTime
                }

                6 -> {
                    if (alarm!!.isSat) return alarmTime
                }
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

    companion object {
        fun newInstance(): AlarmListFragment {
            return AlarmListFragment()
        }
    }
}