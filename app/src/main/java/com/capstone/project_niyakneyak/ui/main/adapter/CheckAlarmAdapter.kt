package com.capstone.project_niyakneyak.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener

/**
 *
 */
class CheckAlarmAdapter(private val onCheckedAlarmListener: OnCheckedAlarmListener) :
    RecyclerView.Adapter<CheckAlarmAdapter.ViewHolder>() {
    private var alarms: List<Alarm>
    private var medsID: Long

    init {
        alarms = ArrayList()
        medsID = -1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val checkTitle: TextView
        private val checkRecursion: TextView
        private val checkBox: CheckBox

        init {
            checkTitle = view.findViewById(R.id.check_clock_time)
            checkRecursion = view.findViewById(R.id.check_clock_recursion)
            checkBox = view.findViewById(R.id.check_clock_checkbox)
        }

        fun bind(medsID: Long, alarm: Alarm, onCheckedAlarmListener: OnCheckedAlarmListener) {
            checkTitle.text = String.format("%02d:%02d", alarm.hour, alarm.min)
            checkRecursion.text = alarm.daysText
            checkBox.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                onCheckedAlarmListener.onItemClicked(medsID, alarm, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_check_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarms!![position]
        holder.bind(medsID, alarm, onCheckedAlarmListener)
    }

    override fun getItemCount(): Int {
        return alarms!!.size
    }

    fun setAlarms(medsID: Long, alarms: List<Alarm>) {
        this.alarms = alarms
        this.medsID = medsID
        notifyDataSetChanged()
    }
}