package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedsData
import com.capstone.project_niyakneyak.databinding.ActivityDataSettingBinding
import com.capstone.project_niyakneyak.main.adapter.AlarmSelectionAdapter
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.etc.SubmitFormState
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

/**
 * This DialogFragment is used for setting [MedsData](which is Medication Info.).
 */
class DataSettingActivity : AppCompatActivity(), OnCheckedAlarmListener {
    private lateinit var binding: ActivityDataSettingBinding
    private var adapter: AlarmSelectionAdapter? = null
    private var submitFormState = MutableLiveData<SubmitFormState>()

    private var snapshotId: String? = null
    private var originData: MedsData? = null
    private var query: Query? = null
    private var originAlarmID = mutableListOf<String>()
    private var includedAlarmID = mutableListOf<String>()

    private lateinit var firestore: FirebaseFirestore

    companion object{
        private const val TAG = "DATA_SETTING_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set Activity View
        binding = ActivityDataSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Accessible Data
        firestore = Firebase.firestore
        snapshotId = intent.getStringExtra("snapshot_id")
        if(snapshotId != null){
            val medicationRef = firestore.collection("medications").document(snapshotId!!)
            medicationRef.get().addOnSuccessListener {
                originData = it.toObject(MedsData::class.java)

                // Setting toolbar title
                binding.toolbar2.setTitle(R.string.dialog_modify_form_title)

                // Setting Activity Components
                setActivity(originData, binding)
            }.addOnFailureListener {
                Log.w(TAG, "Terrible Error Occurred!: Data not exists!")
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        else {
            binding.toolbar2.setTitle(R.string.dialog_add_form_title)
            setActivity(null, binding)
        }

        // Setting RecyclerView Data Query
        query = firestore.collection("alarms")

        query?.let {
            adapter = object: AlarmSelectionAdapter(it, snapshotId, this@DataSettingActivity){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.dialogMedsTimerList.visibility = View.GONE
                    }
                    else {
                        binding.dialogMedsTimerList.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
            binding.dialogMedsTimerList.adapter = adapter
        }

        // Setting RecyclerView about timer list
        binding.dialogMedsTimerList.setHasFixedSize(false)
        binding.dialogMedsTimerList.layoutManager = LinearLayoutManager(this)
        binding.dialogMedsTimerList.addItemDecoration(VerticalItemDecorator(10))
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

    private fun setActivity(originData: MedsData?, binding: ActivityDataSettingBinding){
        var startDate: Long? = null; var endDate: Long? = null
        val data = MedsData()
        if(originData != null){
            data.medsID = originData.medsID
            binding.medsNameText.setText(originData.medsName)
            if (originData.medsDetail != null) binding.medsDetailText.setText(originData.medsDetail)
            if (originData.medsStartDate != null && originData.medsEndDate != null) {
                binding.medsDateText.setText(String.format("%s~%s", originData.medsStartDate, originData.medsEndDate))
            }
            val parser: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN)
            try {
                if (originData.medsStartDate != null && originData.medsEndDate != null) {
                    startDate = parser.parse(originData.medsStartDate.toString())?.time
                    endDate = parser.parse(originData.medsEndDate.toString())?.time
                } else {
                    endDate = null
                    startDate = endDate
                }
            } catch (e: ParseException) {
                Log.d("DataSettingDialog", "Meds_date parsing error")
                throw RuntimeException()
            }
            firestore.collection("alarms").whereArrayContains(Alarm.FIELD_MEDICATION_LIST, originData.medsID).get()
                .addOnSuccessListener {
                    for(document in it.documents){
                        originAlarmID.add(document.id)
                        includedAlarmID.add(document.id)
                    }
                }
                .addOnFailureListener {
                    Log.w(TAG, "Terrible Error Occurred: $it")
                }

            binding.submit.isEnabled = true
        } else data.medsID = codeGenerator()

        val filter =
            InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
                for (i in start until end)
                    if (Character.isWhitespace(source[i]))
                        return@InputFilter ""
                null
            }

        // TextInput Exception Control Methods
        val afterTextChanged: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                submitDataChanged(binding.medsNameText.text.toString())
            }
        }

        submitFormState.observe(this) { submitFormState: SubmitFormState? ->
            if (submitFormState == null) return@observe
            binding.submit.isEnabled = submitFormState.isDataValid
            if (submitFormState.medsNameError != null) {
                binding.dialogMedsNameLayout.isErrorEnabled = true
                binding.dialogMedsNameLayout.error = getString(submitFormState.medsNameError!!)
            } else binding.dialogMedsNameLayout.isErrorEnabled = false
        }

        // Main Body(Setting Functions for each Components)
        binding.medsNameText.addTextChangedListener(afterTextChanged)
        binding.medsNameText.filters = arrayOf(filter)
        binding.medsDateText.setOnClickListener {
            val calBuilder = CalendarConstraints.Builder()
                .setStart(MaterialDatePicker.thisMonthInUtcMilliseconds())
                .setValidator(DateValidatorPointForward.now())
            val constraints = calBuilder.build()
            val datePickerBuilder = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTitleText("Select Medication End Date")
                .setCalendarConstraints(constraints)
            if (startDate != null) datePickerBuilder.setSelection(Pair(startDate + 1000 * 60 * 60 * 24, endDate))

            val datePicker = datePickerBuilder.build()
            datePicker.show(supportFragmentManager, "DATE_PICKER_RANGE")
            datePicker.addOnPositiveButtonClickListener { selection: Pair<Long, Long?> ->
                val start = Date()
                val end = Date()
                start.time = selection.first
                end.time = selection.second!!
                val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
                binding.medsDateText.setText(String.format("%s~%s", dateFormat.format(start), dateFormat.format(end)))
            }
            datePicker.addOnNegativeButtonClickListener { }
        }
        binding.dialogMedsTimerAddBtn.setOnClickListener { showAlarmSettingActivity() }
        binding.submit.setOnClickListener {
            data.medsName = binding.medsNameText.text.toString()
            data.medsDetail =
                if (binding.medsDetailText.text.toString() == "null") null else binding.medsDetailText.text.toString()
            val medsDateText =
                if (binding.medsDateText.text.toString() == "null") null else binding.medsDateText.text.toString()
            if (medsDateText!!.isNotEmpty()) {
                val dates = medsDateText.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                data.medsStartDate = dates[0]
                data.medsEndDate = dates[1]
            }
            if(snapshotId == null) submitData(null, data)
            else submitData(snapshotId, data)

            setResult(RESULT_OK)
            finish()
        }
        binding.cancel.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun submitData(snapshotID: String?, data: MedsData){
        val medicationRef = firestore.collection("medications")
        val alarmRef = firestore.collection("alarms")
        if(snapshotID == null){
            firestore.runTransaction {transaction ->
                for(snapshotId in includedAlarmID){
                    val alarm = transaction.get(alarmRef.document(snapshotId)).toObject<Alarm>() ?: continue
                    if(!alarm.isStarted) {
                        transaction.update(alarmRef.document(snapshotId), Alarm.FIELD_IS_STARTED, true)
                        alarm.scheduleAlarm(applicationContext)
                    }
                    transaction.update(alarmRef.document(snapshotId), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayUnion(data.medsID))
                }
                transaction.set(medicationRef.document(data.medsID.toString()), data)
            }.addOnSuccessListener {
                Log.w(TAG, "Added Medication Data Successfully!")
            }.addOnFailureListener { Log.w(TAG, "Medication Data Addition Failed!") }
        }
        else{
            firestore.runTransaction {transaction ->
                for(snapshotId in includedAlarmID){
                    if(!originAlarmID.contains(snapshotId)){
                        val alarm = transaction.get(alarmRef.document(snapshotId)).toObject<Alarm>() ?: continue
                        if(!alarm.isStarted) {
                            transaction.update(alarmRef.document(snapshotId), Alarm.FIELD_IS_STARTED, true)
                            alarm.scheduleAlarm(applicationContext)
                        }
                        transaction.update(alarmRef.document(snapshotId), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayUnion(data.medsID))
                    }
                }
                for(snapshotId in originAlarmID){
                    if(!includedAlarmID.contains(snapshotId)){
                        val alarm = transaction.get(alarmRef.document(snapshotId)).toObject<Alarm>() ?: continue
                        if(alarm.isStarted && alarm.medsList.size <= 1) {
                            alarm.cancelAlarm(applicationContext)
                            transaction.update(alarmRef.document(snapshotId), Alarm.FIELD_IS_STARTED, false)
                        }
                        transaction.update(alarmRef.document(snapshotId), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayRemove(data.medsID))
                    }
                }
                transaction.set(medicationRef.document(data.medsID.toString()), data)
            }.addOnSuccessListener {
                Log.w(TAG, "Data Modification Success")
            }.addOnFailureListener { Log.w(TAG, "Data Modification Failed") }
        }
    }

    private fun showAlarmSettingActivity() {
        val intent = Intent(this, AlarmSettingActivity::class.java)
        startActivity(intent)
    }

    private fun submitDataChanged(meds: String) {
        if (!isMedsNameValid(meds)) {
            submitFormState.setValue(SubmitFormState(R.string.dialog_add_form_meds_name_error))
        } else submitFormState.setValue(SubmitFormState(true))
    }

    private fun isMedsNameValid(medsName: String?): Boolean {
        return medsName != null && medsName.matches("\\w{1,20}".toRegex())
    }

    override fun onItemClicked(alarm: Alarm) {
        if(includedAlarmID.contains(alarm.alarmCode.toString()))
            includedAlarmID.remove(alarm.alarmCode.toString())
        else includedAlarmID.add(alarm.alarmCode.toString())
    }

    override fun onItemClicked(medsID: Long, alarm: Alarm, isChecked: Boolean) {}

    private fun codeGenerator(): Long {
        return Random(System.currentTimeMillis()).nextLong()
    }
}