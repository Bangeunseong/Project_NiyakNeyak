package com.capstone.project_niyakneyak.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.ui.main.listener.OnToggleAlarmListener
import java.util.Calendar
import java.util.Locale

/**
 * This Adapter is used for showing currently registered alarms.
 * It needs single attribute to create adapter which is abstract interface [OnToggleAlarmListener]
 */
class AlarmDataAdapter(private val onToggleAlarmListener: OnToggleAlarmListener) :
    RecyclerView.Adapter<AlarmDataAdapter.ViewHolder>() {
    private var alarms: List<Alarm> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val rcv_alarm_time: TextView
        private val rcv_alarm_title: TextView
        private val rcv_weekly_date_display: TextView
        private val rcv_alarm_layout: ConstraintLayout
        private val rcv_alarm_swt: SwitchCompat

        init {
            rcv_alarm_time = view.findViewById(R.id.item_clock)
            rcv_alarm_title = view.findViewById(R.id.alarm_title)
            rcv_alarm_layout = view.findViewById(R.id.textview_layout)
            rcv_weekly_date_display = view.findViewById(R.id.weekly_date_display)
            rcv_alarm_swt = view.findViewById(R.id.alarm_switch)
        }

        fun bind(alarm: Alarm, listener: OnToggleAlarmListener) {
            rcv_alarm_time.text = String.format(Locale.KOREAN, "%02d:%02d", alarm.hour, alarm.min)
            rcv_alarm_swt.isChecked = alarm.isStarted
            if (alarm.isRecurring) {
                rcv_weekly_date_display.text = alarm.daysText
            } else {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                if (alarm.hour < calendar[Calendar.HOUR_OF_DAY]) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    rcv_weekly_date_display.text = String.format(
                        Locale.KOREAN,
                        "Next Day %02d/%02d",
                        calendar[Calendar.MONTH] + 1,
                        calendar[Calendar.DAY_OF_MONTH]
                    )
                } else if (alarm.hour == calendar[Calendar.HOUR_OF_DAY]) {
                    if (alarm.min <= calendar[Calendar.MINUTE]) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                        rcv_weekly_date_display.text = String.format(
                            Locale.KOREAN,
                            "Next Day %02d/%02d",
                            calendar[Calendar.MONTH] + 1,
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                    } else rcv_weekly_date_display.text = String.format(
                        Locale.KOREAN,
                        "Today %02d/%02d",
                        calendar[Calendar.MONTH] + 1,
                        calendar[Calendar.DAY_OF_MONTH]
                    )
                } else {
                    rcv_weekly_date_display.text = String.format(
                        Locale.KOREAN,
                        "Today %02d/%02d",
                        calendar[Calendar.MONTH] + 1,
                        calendar[Calendar.DAY_OF_MONTH]
                    )
                }
            }
            if (alarm.title!!.isNotEmpty()) rcv_alarm_title.text =
                alarm.title else rcv_alarm_title.text = "My Alarm"
            rcv_alarm_swt.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                if (buttonView.isShown || buttonView.isPressed) listener.onToggle(alarm)
            }
            rcv_alarm_layout.setOnClickListener { listener.onItemClick(alarm) }
            rcv_alarm_layout.setOnLongClickListener {
                listener.onDelete(alarm)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_time, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm, onToggleAlarmListener)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun setAlarms(alarms: List<Alarm>) {
        this.alarms = alarms
        notifyDataSetChanged()
    }
}