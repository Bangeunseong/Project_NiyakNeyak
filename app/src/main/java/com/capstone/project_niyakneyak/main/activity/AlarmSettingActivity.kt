package com.capstone.project_niyakneyak.main.activity

import android.app.Activity
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityAlarmSettingBinding
import com.capstone.project_niyakneyak.main.etc.ActionResult
import com.capstone.project_niyakneyak.main.etc.AlarmDataView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Random

/**
 * This DialogFragment is used to set [Alarm] getting information
 * by user interaction.
 */
class AlarmSettingActivity : AppCompatActivity() {
    private var _binding: ActivityAlarmSettingBinding? = null
    private val binding get() = _binding!!
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!
    private lateinit var tone: String

    private var snapshotId: String? = null
    private val actionResult = MutableLiveData<ActionResult?>()
    private var alarm: Alarm? = null
    private var ringtone: Ringtone? = null
    private var isRecurring = false

    private val mStartForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { o: ActivityResult ->
            if (o.resultCode == Activity.RESULT_OK) {
                val uri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        o.data!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
                    else {
                        @Suppress("DEPRECATION")
                        o.data!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    }

                ringtone = RingtoneManager.getRingtone(applicationContext, uri)
                val title = ringtone!!.getTitle(applicationContext)
                if (uri != null) {
                    tone = uri.toString()
                    if (title != null && title.isNotEmpty()) binding.alarmRingtoneText.text = title
                } else binding.alarmRingtoneText.text = ""
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setting firestore and firebaseAuth
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        // Set View Binding
        _binding = ActivityAlarmSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get Accessible data if needed
        snapshotId = intent.getStringExtra("snapshot_id")

        if(snapshotId != null){
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID).document(snapshotId!!).get()
                .addOnSuccessListener {
                    alarm = it.toObject(Alarm::class.java)
                    setActivity(alarm)
                }.addOnFailureListener {
                    alarm = null
                    setActivity(alarm)
                }
        } else setActivity(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _firestore = null
        _firebaseAuth = null
    }

    private fun setActivity(alarm: Alarm?){
        // Setting Base Tone
        tone = RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_ALARM).toString()
        ringtone = RingtoneManager.getRingtone(applicationContext, Uri.parse(tone))

        // Get bundle data if needed
        if (alarm != null) updateAlarmInfo(alarm) else {
            binding.timePicker.hour = 6
            binding.timePicker.minute = 0
            binding.alarmRingtoneText.text = ringtone!!.getTitle(applicationContext)
        }

        // Setting TimePickerView in 24hourView
        binding.timePicker.setIs24HourView(true)

        // Toggle Button Animation
        binding.toggleSunday.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            isRecurring =
                (isChecked || binding.toggleMonday.isChecked || binding.toggleTuesday.isChecked || binding.toggleWednesday.isChecked
                        || binding.toggleThursday.isChecked || binding.toggleFriday.isChecked || binding.toggleSaturday.isChecked)
            if (isChecked) {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                buttonView.alpha = 1f
            } else {
                buttonView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
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
        binding.alarmRingtoneLayout.setOnClickListener {
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
        binding.alarmSubmit.setOnClickListener {
            if (alarm != null) {
                if(alarm.isStarted)
                    alarm.cancelAlarm(applicationContext)
                updateAlarm()
            } else scheduleAlarm()
        }
        binding.alarmCancel.setOnClickListener {
            finish()
        }
    }

    private fun scheduleAlarm() {
        var alarmTitle = getString(R.string.alarm_title)
        val alarmCode = codeGenerator()
        if (binding.dialogAlarmTitleText.text.toString().isNotEmpty()) alarmTitle =
            binding.dialogAlarmTitleText.text.toString()
        val alarm = Alarm(
            alarmCode,
            binding.timePicker.hour,
            binding.timePicker.minute,
            false,
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
            Toast.makeText(applicationContext, String.format("Alarm already exists at %02d:%02d!", binding.timePicker.hour, binding.timePicker.minute), Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID).document(alarm.alarmCode.toString()).set(alarm)
            .addOnSuccessListener {
                setResult(RESULT_OK)
                finish()
            }.addOnFailureListener {
                Log.w(TAG,"Alarm registration Failed")
                setResult(RESULT_CANCELED)
                finish()
            }
    }

    private fun updateAlarm() {
        var alarmTitle = getString(R.string.alarm_title)
        //int alarmCode = codeGenerator();
        if (binding.dialogAlarmTitleText.text.toString().isNotEmpty()) alarmTitle =
            binding.dialogAlarmTitleText.text.toString()
        val updatedAlarm = Alarm(
            alarm!!.alarmCode,
            binding.timePicker.hour,
            binding.timePicker.minute,
            alarm!!.medsList.isNotEmpty(),
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
            binding.alarmVibSwt.isChecked,
            alarm!!.medsList
        )

        // Validate alarm's existence
        if (isAlreadyExistsAlarm(updatedAlarm)) {
            Toast.makeText(applicationContext, String.format("Alarm already exists at %02d:%02d!", binding.timePicker.hour, binding.timePicker.minute), Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID).document(updatedAlarm.alarmCode.toString()).set(updatedAlarm)
            .addOnSuccessListener {
                if(updatedAlarm.isStarted)
                    updatedAlarm.scheduleAlarm(applicationContext)
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                Log.w(TAG,"Terrible Error Occurred: Alarm Modification Failed, Alarm Canceled!")
                setResult(RESULT_CANCELED)
                finish()
            }
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
        ringtone = RingtoneManager.getRingtone(applicationContext, Uri.parse(tone))
        binding.alarmRingtoneText.text = ringtone!!.getTitle(applicationContext)
        if (alarm.isVibrate) binding.alarmVibSwt.isChecked = true
    }

    private fun isAlreadyExistsAlarm(alarm: Alarm): Boolean {
        var flag = false
        firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID).where(Filter.and(
                Filter.notEqualTo(Alarm.FIELD_ALARM_CODE, alarm.alarmCode),
                Filter.equalTo(Alarm.FIELD_HOUR, alarm.hour),
                Filter.equalTo(Alarm.FIELD_MINUTE, alarm.min),
                Filter.equalTo(Alarm.FIELD_IS_SUNDAY, alarm.isSun),
                Filter.equalTo(Alarm.FIELD_IS_MONDAY, alarm.isMon),
                Filter.equalTo(Alarm.FIELD_IS_TUESDAY, alarm.isTue),
                Filter.equalTo(Alarm.FIELD_IS_WEDNESDAY, alarm.isWed),
                Filter.equalTo(Alarm.FIELD_IS_THURSDAY, alarm.isThu),
                Filter.equalTo(Alarm.FIELD_IS_FRIDAY, alarm.isFri),
                Filter.equalTo(Alarm.FIELD_IS_SATURDAY, alarm.isSat))).get()
            .addOnSuccessListener {
                flag = !it.isEmpty
            }.addOnFailureListener {

            }
        return flag
    }

    private fun codeGenerator(): Int {
        return Random().nextInt(Int.MAX_VALUE)
    }

    companion object{
        private const val TAG = "ALARM_SETTING_ACTIVITY"
    }
}