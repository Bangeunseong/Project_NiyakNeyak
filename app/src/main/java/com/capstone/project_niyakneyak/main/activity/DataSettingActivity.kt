package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.Container
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityDataSettingBinding
import com.capstone.project_niyakneyak.main.adapter.AlarmSelectionAdapter
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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
    private var _binding: ActivityDataSettingBinding? = null
    private val binding get() = _binding!!
    private var adapter: AlarmSelectionAdapter? = null

    private var snapshotId: String? = null
    private var originData: MedicineData? = null
    private var fetchedData: MedicineData = MedicineData()
    private var query: Query? = null
    private var originAlarmID = mutableListOf<String>()
    private var includedAlarmID = mutableListOf<String>()

    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!

    private val searchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val intent = it.data ?: return@registerForActivityResult
            val bundle = intent.getBundleExtra(Container.CONTAINER_BUNDLE_KEY)
            val container = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle!!.getParcelable(Container.CONTAINER_KEY, Container::class.java)
            else {
                @Suppress("DEPRECATION")
                bundle!!.getParcelable(Container.CONTAINER_KEY)
            }

            fetchedData.itemSeq = container!!.itemSeq; fetchedData.itemName = container.itemName
            fetchedData.itemEngName = container.itemEngName; fetchedData.entpName = container.entpName
            fetchedData.entpEngName = container.entpEngName; fetchedData.entpSeq = container.entpSeq
            fetchedData.entpNo = container.entpNo
            if(!container.cancelDate.equals("null"))
                fetchedData.itemPermDate = fetchedData.convertStrToDate(container.itemPermDate)
            fetchedData.inDuty = container.inDuty; fetchedData.prdlstStrdCode = container.prdlstStrdCode
            fetchedData.spcltyPblc = container.spcltyPblc; fetchedData.pdtType = container.pdtType
            fetchedData.pdtPermNo = container.pdtPermNo; fetchedData.itemIngrName = container.itemIngrName
            fetchedData.itemIngrCnt = container.itemIngrCnt; fetchedData.bigPrdtImgUrl = container.bigPrdtImgUrl
            fetchedData.permKindCode= container.permKindCode
            if(!container.cancelDate.equals("null"))
                fetchedData.cancelDate = fetchedData.convertStrToDate(container.cancelDate)
            fetchedData.cancelName = container.cancelName; fetchedData.ediCode = container.ediCode; fetchedData.bizrNo = container.bizrNo

            binding.medsNameText.setText(fetchedData.itemName)
            binding.submit.isEnabled = true
        }
    }

    companion object{
        private const val TAG = "DATA_SETTING_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set Activity View
        _binding = ActivityDataSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Accessible Data
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        snapshotId = intent.getStringExtra("snapshot_id")
        if(snapshotId != null){
            val medicationRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(MedicineData.COLLECTION_ID).document(snapshotId!!)

            medicationRef.get().addOnSuccessListener {
                originData = it.toObject(MedicineData::class.java)

                // Setting toolbar title
                binding.toolbar2.setTitle(R.string.dialog_modify_form_title)

                // Setting Activity Components
                setActivity(originData, binding)
            }.addOnFailureListener {
                Log.w(TAG, "Terrible Error Occurred!: $it")
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        else {
            binding.toolbar2.setTitle(R.string.dialog_add_form_title)
            setActivity(null, binding)
        }

        // Setting RecyclerView Data Query
        query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID)

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _firebaseAuth = null
        _firestore = null
    }

    private fun setActivity(originData: MedicineData?, binding: ActivityDataSettingBinding){
        var startDate: Long? = null; var endDate: Long? = null
        if(originData != null){
            fetchedData.medsID = originData.medsID
            binding.medsNameText.setText(originData.itemName)
            if(originData.dailyAmount > 0){
                binding.medsDailyAmountText.setText(originData.dailyAmount.toString())
            }
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
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID).whereArrayContains(Alarm.FIELD_MEDICATION_LIST, originData.medsID).get()
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
        } else fetchedData.medsID = codeGenerator()

        val amountFilter =
            InputFilter{ source: CharSequence, start: Int, end: Int, _: Spanned?, _: Int, _: Int ->
                for(i in start until end)
                    if(!Character.isDigit(source[i]))
                        return@InputFilter ""
                null
            }

        // Main Body(Setting Functions for each Components)
        binding.medsNameText.isFocusable = false
        binding.medsNameSearchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchLauncher.launch(intent)
        }
        binding.medsDailyAmountText.filters = arrayOf(amountFilter)
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
                val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd",Locale.KOREAN)
                binding.medsDateText.setText(String.format("%s~%s", dateFormat.format(start), dateFormat.format(end)))
            }
            datePicker.addOnNegativeButtonClickListener { }
        }
        binding.dialogMedsTimerAddBtn.setOnClickListener { showAlarmSettingActivity() }
        binding.submit.setOnClickListener {
            fetchedData.dailyAmount = binding.medsDailyAmountText.text.toString().toInt()
            fetchedData.medsDetail =
                if (binding.medsDetailText.text.toString() == "null") null else binding.medsDetailText.text.toString()
            val medsDateText =
                if (binding.medsDateText.text.toString() == "null") null else binding.medsDateText.text.toString()
            if (medsDateText!!.isNotEmpty()) {
                val dates = medsDateText.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                fetchedData.medsStartDate = SimpleDateFormat("yyyy/MM/dd",Locale.KOREAN).parse(dates[0])
                fetchedData.medsEndDate = SimpleDateFormat("yyyy/MM/dd",Locale.KOREAN).parse(dates[1])
            }

            if(snapshotId == null) submitData(null, fetchedData)
            else submitData(snapshotId, fetchedData)

            setResult(RESULT_OK)
            finish()
        }
        binding.cancel.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun submitData(snapshotID: String?, data: MedicineData){
        val medicationRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineData.COLLECTION_ID)
        val alarmRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID)
        if(snapshotID == null){
            firestore.runTransaction {transaction ->
                // Data Read -> Need to be executed before Write Process
                val alarms = mutableListOf<Alarm>()
                for(snapshotId in includedAlarmID){
                    val alarm = transaction.get(alarmRef.document(snapshotId)).toObject<Alarm>() ?: continue
                    alarms.add(alarm)
                }

                // Data Write
                for(alarm in alarms){
                    if(!alarm.isStarted) {
                        transaction.update(alarmRef.document(alarm.alarmCode.toString()), Alarm.FIELD_IS_STARTED, true)
                        alarm.scheduleAlarm(applicationContext)
                    }
                    transaction.update(alarmRef.document(alarm.alarmCode.toString()), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayUnion(data.medsID))
                }
                transaction.set(medicationRef.document(data.medsID.toString()), data)
            }.addOnSuccessListener {
                Log.w(TAG, "Added Medication Data Successfully!")
            }.addOnFailureListener { Log.w(TAG, "Medication Data Addition Failed!: $it") }
        }
        else{
            firestore.runTransaction {transaction ->
                // Data Read -> Need to be executed before Write Process
                val includedAlarms = mutableListOf<Alarm>()
                val deletedAlarms = mutableListOf<Alarm>()
                for(snapshotId in includedAlarmID){
                    if(!originAlarmID.contains(snapshotId)) {
                        val alarm = transaction.get(alarmRef.document(snapshotId)).toObject<Alarm>() ?: continue
                        if(!alarm.isStarted)
                            alarm.scheduleAlarm(applicationContext)
                        includedAlarms.add(alarm)
                    }
                }
                for(snapshotId in originAlarmID){
                    if(!includedAlarmID.contains(snapshotId)) {
                        val alarm = transaction.get(alarmRef.document(snapshotId)).toObject<Alarm>() ?: continue
                        if(alarm.medsList.size <= 1)
                            alarm.cancelAlarm(applicationContext)
                        deletedAlarms.add(alarm)
                    }
                }

                // Data Write
                val maxPos =
                    if(includedAlarms.size > deletedAlarms.size) includedAlarms.size
                    else deletedAlarms.size
                for(pos in 0..maxPos){
                    if(includedAlarms.size > pos){
                        transaction.update(alarmRef.document(includedAlarms[pos].alarmCode.toString()), Alarm.FIELD_IS_STARTED, true)
                        transaction.update(alarmRef.document(includedAlarms[pos].alarmCode.toString()), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayUnion(data.medsID))
                    }
                    if(deletedAlarms.size > pos){
                        transaction.update(alarmRef.document(deletedAlarms[pos].alarmCode.toString()), Alarm.FIELD_IS_STARTED, false)
                        transaction.update(alarmRef.document(deletedAlarms[pos].alarmCode.toString()), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayRemove(data.medsID))
                    }
                }
                transaction.set(medicationRef.document(data.medsID.toString()), data)
            }.addOnSuccessListener {
                Log.w(TAG, "Data Modification Success")
            }.addOnFailureListener { Log.w(TAG, "Data Modification Failed: $it") }
        }
    }

    private fun showAlarmSettingActivity() {
        val intent = Intent(this, AlarmSettingActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClicked(alarm: Alarm) {
        if(includedAlarmID.contains(alarm.alarmCode.toString()))
            includedAlarmID.remove(alarm.alarmCode.toString())
        else includedAlarmID.add(alarm.alarmCode.toString())
    }

    private fun codeGenerator(): Int {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(Int.MAX_VALUE)
    }
}