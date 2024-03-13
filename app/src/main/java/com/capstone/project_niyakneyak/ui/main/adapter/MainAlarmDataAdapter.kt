package com.capstone.project_niyakneyak.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener
import java.util.Locale

/**
 * This Adapter is used for showing currently registered and selectable alarms.
 * When setting Medication Data, this adapter is used to describe which alarm is available.
 * Adapter needs [OnCheckedAlarmListener] to deliver checked alarm info to
 * [com.capstone.project_niyakneyak.ui.main.fragment.DataSettingActivity]
 */
class MainAlarmDataAdapter(private val onCheckedAlarmListener: OnCheckedAlarmListener) :
    RecyclerView.Adapter<MainAlarmDataAdapter.ViewHolder>() {
    private var alarms: List<Alarm>
    private var includedAlarms: List<Int>

    init {
        alarms = ArrayList()
        includedAlarms = ArrayList()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val clockSelectionLayout: ConstraintLayout
        private val clockTime: TextView
        private val clockRecursion: TextView
        private val clockCheckBox: CheckBox

        init {
            clockSelectionLayout = view.findViewById(R.id.clock_selection_single)
            clockTime = view.findViewById(R.id.clock_time)
            clockRecursion = view.findViewById(R.id.clock_recursion)
            clockCheckBox = view.findViewById(R.id.clock_checkbox)
        }

        fun bind(alarm: Alarm, includedAlarms: List<Int?>, onCheckedAlarmListener: OnCheckedAlarmListener) {
            clockTime.text = String.format(Locale.KOREAN, "%02d:%02d", alarm.hour, alarm.min)
            clockRecursion.text = alarm.daysText
            clockCheckBox.isEnabled = alarm.isStarted
            clockCheckBox.isChecked = includedAlarms.contains(alarm.alarmCode)
            clockCheckBox.setOnClickListener {
                onCheckedAlarmListener.onItemClicked(alarm)
            }
            if (!alarm.isStarted) clockSelectionLayout.setBackgroundResource(R.drawable.item_recycler_bg_disabled)
            else clockSelectionLayout.setBackgroundResource(R.drawable.item_recycler_bg_secondary)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_meds_time_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm, includedAlarms, onCheckedAlarmListener)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun setAlarms(alarms: List<Alarm>, includedAlarms: List<Int>) {
        this.alarms = alarms
        this.includedAlarms = includedAlarms
        notifyDataSetChanged()
    }
}