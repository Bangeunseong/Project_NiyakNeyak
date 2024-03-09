package com.capstone.project_niyakneyak.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.FragmentCheckListBinding
import com.capstone.project_niyakneyak.ui.main.adapter.CheckDataAdapter
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.CheckListViewModel
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.CheckListViewModelFactory
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * This Fragment is used for showing daily Medication list by using [CheckListFragment.adapter].
 * [CheckListFragment.adapter] will be set by using [CheckDataAdapter]
 */
class CheckListFragment : Fragment(), OnCheckedAlarmListener {
    private var binding: FragmentCheckListBinding? = null
    private var checkListViewModel: CheckListViewModel? = null
    private var adapter: CheckDataAdapter? = null
    private var alarms: List<Alarm> = ArrayList()
    private var medsList: List<MedsData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkListViewModel =
            ViewModelProvider(this, CheckListViewModelFactory(requireActivity().application))[CheckListViewModel::class.java]
        adapter = CheckDataAdapter(this)
        checkListViewModel!!.getAlarmsLiveData()?.observe(this) { alarms: List<Alarm> ->
            this.alarms = alarms
            val temp = getCertainMedsData(medsList, alarms)
            if (temp.isEmpty()) binding!!.contentChecklistDescriptionText.visibility =
                View.VISIBLE else binding!!.contentChecklistDescriptionText.visibility = View.GONE
            adapter!!.setDataSet(temp, alarms)
        }
        checkListViewModel!!.getLiveActionResult().observe(this) { actionResult: ActionResult? ->
            if (actionResult == null) return@observe
            if (actionResult.success != null) {
                medsList = checkListViewModel!!.getMedsDataList()!!
                val temp = getCertainMedsData(medsList, alarms)
                if (temp.isEmpty()) binding!!.contentChecklistDescriptionText.visibility =
                    View.VISIBLE else binding!!.contentChecklistDescriptionText.visibility =
                    View.GONE
                adapter!!.setDataSet(temp, alarms)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckListBinding.inflate(
            layoutInflater
        )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.contentChecklist.setHasFixedSize(false)
        binding!!.contentChecklist.layoutManager = LinearLayoutManager(context)
        binding!!.contentChecklist.adapter = adapter
        binding!!.contentChecklist.addItemDecoration(HorizontalItemDecorator(10))
        binding!!.contentChecklist.addItemDecoration(VerticalItemDecorator(20))
    }

    private fun getCertainMedsData(medsList: List<MedsData>, alarms: List<Alarm>): List<MedsData> {
        val today = Calendar.getInstance()
        today.timeInMillis = System.currentTimeMillis()
        val format: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        var start: Date?
        var end: Date?
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        val certainMeds: MutableList<MedsData> = ArrayList()
        for (data in medsList) {
            if (data.medsStartDate != null) {
                try {
                    start = format.parse(data.medsStartDate)
                    end = format.parse(data.medsEndDate)
                    startCal.time = start
                    endCal.time = end
                    if (startCal > today || endCal < today) continue
                } catch (e: ParseException) {
                    Log.d("CheckListFragment", "Parsing Failed!")
                }
            }
            if (isConsumeDate(alarms, data.alarms)) certainMeds.add(data)
        }
        return certainMeds
    }

    private fun isConsumeDate(alarms: List<Alarm?>?, includedAlarms: List<Int>): Boolean {
        val today = Calendar.getInstance()
        today.timeInMillis = System.currentTimeMillis()
        var flag = false
        for (alarm in alarms!!) {
            if (includedAlarms.contains(alarm!!.alarmCode) && alarm.isStarted) {
                if (alarm.isRecurring) {
                    when (today[Calendar.DAY_OF_WEEK]) {
                        Calendar.SUNDAY -> {
                            if (alarm.isSun) flag = true
                        }

                        Calendar.MONDAY -> {
                            if (alarm.isMon) flag = true
                        }

                        Calendar.TUESDAY -> {
                            if (alarm.isTue) flag = true
                        }

                        Calendar.WEDNESDAY -> {
                            if (alarm.isWed) flag = true
                        }

                        Calendar.THURSDAY -> {
                            if (alarm.isThu) flag = true
                        }

                        Calendar.FRIDAY -> {
                            if (alarm.isFri) flag = true
                        }

                        Calendar.SATURDAY -> {
                            if (alarm.isSat) flag = true
                        }
                    }
                } else {
                    val alarmClock = Calendar.getInstance()
                    alarmClock.timeInMillis = System.currentTimeMillis()
                    alarmClock[Calendar.HOUR_OF_DAY] = alarm.hour
                    alarmClock[Calendar.MINUTE] = alarm.min
                    alarmClock[Calendar.SECOND] = 0
                    alarmClock[Calendar.MILLISECOND] = 0
                    if (alarmClock > today) flag = true
                }
            }
        }
        return flag
    }

    @Deprecated("")
    override fun onItemClicked(alarm: Alarm) {
    }

    //TODO: Need to add action when checkbox is checked
    override fun onItemClicked(medsID: Long, alarm: Alarm, isChecked: Boolean) {}

    companion object {
        fun newInstance(): CheckListFragment {
            return CheckListFragment()
        }
    }
}