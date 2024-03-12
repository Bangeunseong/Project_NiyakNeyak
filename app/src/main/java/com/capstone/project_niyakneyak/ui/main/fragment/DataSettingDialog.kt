package com.capstone.project_niyakneyak.ui.main.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import androidx.core.util.Pair
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.FragmentDataSettingDialogBinding
import com.capstone.project_niyakneyak.ui.main.adapter.MainAlarmDataAdapter
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.ui.main.etc.SubmitFormState
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataSettingViewModel
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataSettingViewModelFactory
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener
import com.capstone.project_niyakneyak.ui.main.listener.OnDialogActionListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This DialogFragment is used for setting [MedsData](which is Medication Info.).
 *
 */
class DataSettingDialog(private val onDialogActionListener: OnDialogActionListener) : DialogFragment(), OnCheckedAlarmListener {
    private var submitFormState = MutableLiveData<SubmitFormState>()
    private lateinit var binding: FragmentDataSettingDialogBinding
    private lateinit var dataSettingViewModel: DataSettingViewModel
    private var adapter: MainAlarmDataAdapter? = null
    private var snapshotId: String? = null
    private var data: MedsData? = null
    private var alarms: List<Alarm>? = ArrayList()
    private val includedAlarms = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Accessible Data
        val bundle = requireArguments()
        snapshotId = bundle.getString("snapshot_id")
        data = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(getString(R.string.arg_origin_data), MedsData::class.java)
        } else bundle.getParcelable(getString(R.string.arg_origin_data))
        if (data != null) { includedAlarms.addAll(data!!.alarms) }
        adapter = MainAlarmDataAdapter(this)
        dataSettingViewModel = ViewModelProvider(this, DataSettingViewModelFactory(requireActivity().application))[DataSettingViewModel::class.java]
        dataSettingViewModel.getAlarmsLiveData().observe(this) { alarms: List<Alarm> ->
            this.alarms = alarms
            adapter!!.setAlarms(alarms, includedAlarms)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Main Dialog Builder
        val builder = AlertDialog.Builder(context, R.style.DialogBackground)
        binding = FragmentDataSettingDialogBinding.inflate(layoutInflater)
        if (data == null) builder.setCustomTitle(layoutInflater.inflate(R.layout.item_add_dialog_title, null))
        else builder.setCustomTitle(layoutInflater.inflate(R.layout.item_modify_dialog_title, null))
        builder.setView(binding.root)

        // Setting RecyclerView about timer list
        binding.dialogMedsTimerList.setHasFixedSize(false)
        binding.dialogMedsTimerList.layoutManager = LinearLayoutManager(activity)
        binding.dialogMedsTimerList.adapter = adapter
        binding.dialogMedsTimerList.addItemDecoration(VerticalItemDecorator(10))

        // Set Dialog Components
        setDialog(data, binding)
        return builder.create()
    }

    private fun setDialog(data: MedsData?, binding: FragmentDataSettingDialogBinding){
        var startDate: Long? = null; var endDate: Long? = null

        if(data != null){
            binding.medsNameText.setText(data.medsName)
            if (data.medsDetail != null) binding.medsDetailText.setText(data.medsDetail)
            if (data.medsStartDate != null && data.medsEndDate != null) {
                binding.medsDateText.setText(String.format("%s~%s", data.medsStartDate, data.medsEndDate))
            }
            val parser: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN)
            try {
                if (data.medsStartDate != null && data.medsEndDate != null) {
                    startDate = parser.parse(data.medsStartDate.toString())?.time
                    endDate = parser.parse(data.medsEndDate.toString())?.time
                } else {
                    endDate = null
                    startDate = endDate
                }
            } catch (e: ParseException) {
                Log.d("DataSettingDialog", "Meds_date parsing error")
                throw RuntimeException()
            }
            binding.submit.isEnabled = true
        }

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
            datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER_RANGE")
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
        binding.dialogMedsTimerAddBtn.setOnClickListener { showAlarmSettingDialog() }
        binding.submit.setOnClickListener {
            val data1: MedsData
            val medsNameText = binding.medsNameText.text.toString()
            val medsDetailText =
                if (binding.medsDetailText.text.toString() == "null") null else binding.medsDetailText.text.toString()
            val medsDateText =
                if (binding.medsDateText.text.toString() == "null") null else binding.medsDateText.text.toString()
            data1 = if (medsDateText!!.isNotEmpty()) {
                val dates = medsDateText.split("~".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                MedsData(medsNameText, medsDetailText, dates[0], dates[1])
            } else {
                MedsData(medsNameText, medsDetailText, null, null)
            }
            data1.alarms = includedAlarms
            if (data == null) onDialogActionListener.onAddedMedicationData(data1)
            else onDialogActionListener.onModifiedMedicationData(snapshotId!!, data1)
            dismiss()
        }
        binding.cancel.setOnClickListener { dismiss() }
    }

    private fun showAlarmSettingDialog() {
        val alarmSettingDialog: DialogFragment = AlarmSettingDialog()
        val bundle = Bundle()
        bundle.putParcelable(getString(R.string.arg_alarm_obj), null)
        alarmSettingDialog.arguments = bundle
        alarmSettingDialog.show(requireActivity().supportFragmentManager, "ALARM_DIALOG_FRAGMENT")
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
        if (includedAlarms.contains(alarm.alarmCode)) includedAlarms.remove(alarm.alarmCode as Any)
        else includedAlarms.add(alarm.alarmCode)
    }

    override fun onItemClicked(medsID: Long, alarm: Alarm, isChecked: Boolean) {}
}