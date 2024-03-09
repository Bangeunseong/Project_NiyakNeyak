package com.capstone.project_niyakneyak.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener
import java.util.Calendar

/**
 * This adapter is used for showing Medication info. which should be consumed in current date.
 * It needs [OnCheckedAlarmListener] to deliver which alarm is checked
 * When alarm is checked, [com.capstone.project_niyakneyak.ui.main.fragment.CheckListFragment]
 * will handle the process of recording
 */
class CheckDataAdapter(private val onCheckedAlarmListener: OnCheckedAlarmListener) :
    RecyclerView.Adapter<CheckDataAdapter.ViewHolder>() {
    private var medsList: List<MedsData>
    private var alarms: List<Alarm>

    init {
        medsList = ArrayList()
        alarms = ArrayList()
    }

    class ViewHolder(view: View, onCheckedAlarmListener: OnCheckedAlarmListener) :
        RecyclerView.ViewHolder(view) {
        private val title: TextView
        private val detail: TextView
        private val duration: TextView
        private val alarmList: RecyclerView
        private val checkItemLayout: ConstraintLayout
        private val adapter: CheckAlarmAdapter
        private var visibility = false

        init {
            checkItemLayout = view.findViewById(R.id.check_list_item_layout)
            title = view.findViewById(R.id.check_title_text)
            detail = view.findViewById(R.id.check_detail_text)
            duration = view.findViewById(R.id.check_title_time)
            alarmList = view.findViewById(R.id.alarm_check_list)
            adapter = CheckAlarmAdapter(onCheckedAlarmListener)
        }

        fun bind(data: MedsData, alarms: List<Alarm>) {
            title.text = data.medsName
            if (data.medsDetail != null) detail.text =
                String.format("Detail: %s", data.medsDetail) else detail.text =
                String.format("Detail: %s", "None")
            if (data.medsStartDate != null) duration.text = String.format(
                "Duration: %s~%s",
                data.medsStartDate,
                data.medsEndDate
            ) else duration.text = String.format("Duration: %s", "None")
            checkItemLayout.setOnClickListener {
                if (!visibility) alarmList.visibility = View.VISIBLE
                else alarmList.visibility = View.GONE
                visibility = !visibility
            }
            alarmList.setHasFixedSize(false)
            alarmList.layoutManager = LinearLayoutManager(itemView.context)
            alarmList.adapter = adapter
            alarmList.addItemDecoration(VerticalItemDecorator(10))
            adapter.setAlarms(data.id, getAlarms(data.alarms, alarms))
        }

        private fun getAlarms(includedAlarms: List<Int>, alarms: List<Alarm>): List<Alarm> {
            val today = Calendar.getInstance()
            today.timeInMillis = System.currentTimeMillis()
            val included: MutableList<Alarm> = ArrayList()
            for (alarm in alarms) {
                if (includedAlarms.contains(alarm.alarmCode) && alarm.isStarted) {
                    if (alarm.isRecurring) {
                        when (today[Calendar.DAY_OF_WEEK]) {
                            Calendar.SUNDAY -> {
                                if (alarm.isSun) included.add(alarm)
                            }
                            Calendar.MONDAY -> {
                                if (alarm.isMon) included.add(alarm)
                            }
                            Calendar.TUESDAY -> {
                                if (alarm.isTue) included.add(alarm)
                            }
                            Calendar.WEDNESDAY -> {
                                if (alarm.isWed) included.add(alarm)
                            }
                            Calendar.THURSDAY -> {
                                if (alarm.isThu) included.add(alarm)
                            }
                            Calendar.FRIDAY -> {
                                if (alarm.isFri) included.add(alarm)
                            }
                            Calendar.SATURDAY -> {
                                if (alarm.isSat) included.add(alarm)
                            }
                        }
                    } else {
                        val alarmClock = Calendar.getInstance()
                        alarmClock.timeInMillis = System.currentTimeMillis()
                        alarmClock[Calendar.HOUR_OF_DAY] = alarm.hour
                        alarmClock[Calendar.MINUTE] = alarm.min
                        alarmClock[Calendar.SECOND] = 0
                        alarmClock[Calendar.MILLISECOND] = 0
                        if (alarmClock > today) included.add(alarm)
                    }
                }
            }
            return included
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_check, parent, false)
        return ViewHolder(view, onCheckedAlarmListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = medsList[position]
        holder.bind(data, alarms)
    }

    override fun getItemCount(): Int {
        return medsList.size
    }

    fun setDataSet(medsList: List<MedsData>, alarms: List<Alarm>) {
        this.medsList = medsList
        this.alarms = alarms
        notifyDataSetChanged()
    }
}