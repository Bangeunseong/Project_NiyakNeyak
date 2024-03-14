package com.capstone.project_niyakneyak.main.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.databinding.FragmentAlarmSettingDialogBinding
import com.capstone.project_niyakneyak.main.etc.ActionResult
import com.capstone.project_niyakneyak.main.etc.AlarmDataView
import com.capstone.project_niyakneyak.main.viewmodel.AlarmSettingViewModel
import java.util.Random

/**
 * This DialogFragment is used to set [Alarm] getting information
 * by user interaction.
 */
//TODO: Modify Activity
class AlarmSettingActivity : DialogFragment() {
    private var alarmSettingViewModel: AlarmSettingViewModel? = null
    private lateinit var binding: FragmentAlarmSettingDialogBinding
    private lateinit var tone: String
    private val actionResult = MutableLiveData<ActionResult?>()
    private var alarm: Alarm? = null
    private var ringtone: Ringtone? = null
    private var isRecurring = false
    private var alarms: List<Alarm?>? = null
    private val mStartForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { o: ActivityResult ->
            if (o.resultCode == Activity.RESULT_OK) {
                val uri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        o.data!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
                    else o.data!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

                ringtone = RingtoneManager.getRingtone(context, uri)
                val title = ringtone!!.getTitle(context)
                if (uri != null) {
                    tone = uri.toString()
                    if (title != null && title.isNotEmpty()) binding.alarmRingtoneText.text = title
                } else binding.alarmRingtoneText.text = ""
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Accessible data
        val data = arguments
        alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data?.getParcelable(getString(R.string.arg_alarm_obj), Alarm::class.java)
        } else data?.getParcelable(getString(R.string.arg_alarm_obj))

        // Setting ViewModel
        alarmSettingViewModel = ViewModelProvider(this)[AlarmSettingViewModel::class.java]
        alarmSettingViewModel!!.getAlarmsLiveData()
            ?.observe(this) { alarms: List<Alarm?>? -> this.alarms = alarms }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Main Dialog Builder
        val builder = AlertDialog.Builder(context, R.style.DialogBackground)
        binding = FragmentAlarmSettingDialogBinding.inflate(layoutInflater)
        val view: View = binding.root
        tone = RingtoneManager.getActualDefaultRingtoneUri(requireContext(), RingtoneManager.TYPE_ALARM).toString()
        ringtone = RingtoneManager.getRingtone(context, Uri.parse(tone))
        builder.setView(view)
        if (alarm != null) updateAlarmInfo(alarm!!) else {
            binding.timePicker.hour = 6
            binding.timePicker.minute = 0
            binding.alarmRingtoneText.text = ringtone!!.getTitle(context)
        }

        // Setting TimePickerView in 24hourView
        binding.timePicker.setIs24HourView(true)

        // Toggle Button Animation
        binding.toggleSunday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleTuesday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleFriday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isSunday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isSunday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }
        binding.toggleMonday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleSunday.isChecked || binding.toggleTuesday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleFriday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isMonday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isMonday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }
        binding.toggleTuesday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleSunday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleFriday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isTuesday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isTuesday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }
        binding.toggleWednesday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleTuesday.isChecked || binding.toggleSunday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleFriday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isWednesday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isWednesday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }
        binding.toggleThursday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleTuesday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleSunday.isChecked || binding.toggleFriday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isThursday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isThursday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }
        binding.toggleFriday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleTuesday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleSunday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isFriday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isFriday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }
        binding.toggleSaturday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleTuesday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleFriday.isChecked || binding.toggleSunday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                buttonView.alpha = 0.5f
            }
            if (actionResult.value == null) {
                val date = AlarmDataView()
                date.isSaturday = isChecked
                actionResult.setValue(ActionResult(date))
            } else {
                val date = actionResult.value!!.alarmDateView
                date!!.isSaturday = isChecked
                actionResult.setValue(ActionResult(date))
            }
        }

        // Ringtone Setting
        binding.alarmRingtoneLayout.setOnClickListener { v: View? ->
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone))
            mStartForResult.launch(intent)
        }

        // Observe LiveData of date toggle button
        actionResult.observe(this) { actionResult: ActionResult? ->
            if (actionResult == null) return@observe
            if (actionResult.alarmDateView != null)
                binding.weeklyDate.text = String.format("Weekly: %s", actionResult.alarmDateView!!.displayData)
        }
        binding.alarmSubmit.setOnClickListener { v: View? ->
            if (alarm != null) updateAlarm() else scheduleAlarm()
            dismiss()
        }
        binding.alarmCancel.setOnClickListener { v: View? ->
            if (alarm != null) alarm!!.scheduleAlarm(requireContext())
            dismiss()
        }
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmSettingViewModel = null
    }

    private fun scheduleAlarm() {
        var alarmTitle = getString(R.string.alarm_title)
        val alarmCode = codeGenerator()
        if (!binding.dialogAlarmTitleText.text.toString().isEmpty()) alarmTitle =
            binding.dialogAlarmTitleText.text.toString()
        val alarm = Alarm(
            alarmCode,
            binding.timePicker.hour,
            binding.timePicker.minute,
            true,
            isRecurring,
            binding.toggleMonday.isChecked,
            binding.toggleTuesday.isChecked,
            binding.toggleWednesday.isChecked,
            binding.toggleThursday.isChecked,
            binding.toggleFriday.isChecked,
            binding.toggleSaturday.isChecked,
            binding.toggleSunday.isChecked,
            alarmTitle,
            tone,
            binding.alarmVibSwt.isChecked
        )

        // Validate alarm's existence
        if (isAlreadyExistsAlarm(alarm)) {
            Toast.makeText(
                context,
                String.format(
                    "Alarm already exists at %02d:%02d!",
                    binding.timePicker.hour,
                    binding.timePicker.minute
                ),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        //TODO: Add data in firestore and scheduleAlarm when selected in medication data addition process
        alarmSettingViewModel!!.insert(alarm)
        alarm.scheduleAlarm(requireContext())
    }

    private fun updateAlarm() {
        var alarmTitle = getString(R.string.alarm_title)
        //int alarmCode = codeGenerator();
        if (!binding.dialogAlarmTitleText.text.toString().isEmpty()) alarmTitle =
            binding.dialogAlarmTitleText.text.toString()
        val updatedAlarm = Alarm(
            alarm!!.alarmCode,
            binding.timePicker.hour,
            binding.timePicker.minute,
            true,
            isRecurring,
            binding.toggleMonday.isChecked,
            binding.toggleTuesday.isChecked,
            binding.toggleWednesday.isChecked,
            binding.toggleThursday.isChecked,
            binding.toggleFriday.isChecked,
            binding.toggleSaturday.isChecked,
            binding.toggleSunday.isChecked,
            alarmTitle,
            tone,
            binding.alarmVibSwt.isChecked
        )

        //TODO: Update data in firestroe and scheduleAlarm in medication data modification process
        alarmSettingViewModel!!.update(updatedAlarm)
        updatedAlarm.scheduleAlarm(requireContext())
    }

    private fun updateAlarmInfo(alarm: Alarm) {
        binding.dialogAlarmTitleText.setText(alarm.title)
        binding.timePicker.hour = alarm.hour
        binding.timePicker.minute = alarm.min
        if (alarm.isMon) {
            binding.toggleMonday.isChecked = true
            binding.toggleMonday.alpha = 1f
            isRecurring = true
        }
        if (alarm.isTue) {
            binding.toggleTuesday.isChecked = true
            binding.toggleTuesday.alpha = 1f
            isRecurring = true
        }
        if (alarm.isWed) {
            binding.toggleWednesday.isChecked = true
            binding.toggleWednesday.alpha = 1f
            isRecurring = true
        }
        if (alarm.isThu) {
            binding.toggleThursday.isChecked = true
            binding.toggleThursday.alpha = 1f
            isRecurring = true
        }
        if (alarm.isFri) {
            binding.toggleFriday.isChecked = true
            binding.toggleFriday.alpha = 1f
            isRecurring = true
        }
        if (alarm.isSat) {
            binding.toggleSaturday.isChecked = true
            binding.toggleSaturday.alpha = 1f
            isRecurring = true
        }
        if (alarm.isSun) {
            binding.toggleSunday.isChecked = true
            binding.toggleSunday.alpha = 1f
            isRecurring = true
        }
        actionResult.value = ActionResult(
            AlarmDataView(
                alarm.isSun, alarm.isMon, alarm.isTue,
                alarm.isWed, alarm.isThu, alarm.isFri, alarm.isSat
            )
        )
        tone = alarm.tone.toString()
        ringtone = RingtoneManager.getRingtone(requireContext(), Uri.parse(tone))
        binding.alarmRingtoneText.text = ringtone!!.getTitle(context)
        if (alarm.isVibrate) binding.alarmVibSwt.isChecked = true
    }

    private fun isAlreadyExistsAlarm(data: Alarm): Boolean {
        for (alarm in alarms!!) if (alarm!!.hour == data.hour && alarm.min == data.min) {
            return alarm.daysText == data.daysText
        }
        return false
    }

    private fun codeGenerator(): Int {
        return Random().nextInt(Int.MAX_VALUE)
    }
}