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
import android.view.View
import android.widget.Button
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
    private var binding: FragmentDataSettingDialogBinding? = null
    private var dataSettingViewModel: DataSettingViewModel? = null
    private var adapter: MainAlarmDataAdapter? = null
    private var snapshot_id: String? = null
    private var data: MedsData? = null
    private var alarms: List<Alarm>? = ArrayList()
    private val includedAlarms = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Accessible Data
        val bundle = requireArguments()
        snapshot_id = bundle.getString("snapshot_id")
        data = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable("BeforeModify", MedsData::class.java)
        } else bundle.getParcelable("BeforeModify")
        if (data != null) {
            includedAlarms.addAll(data!!.alarms)
        }
        adapter = MainAlarmDataAdapter(this)
        dataSettingViewModel =
            ViewModelProvider(this, DataSettingViewModelFactory(requireActivity().application))[DataSettingViewModel::class.java]
        dataSettingViewModel!!.getAlarmsLiveData().observe(this) { alarms: List<Alarm> ->
            this.alarms = alarms
            adapter!!.setAlarms(alarms, includedAlarms)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Main Dialog Builder
        val builder = AlertDialog.Builder(context, R.style.DialogBackground)
        binding = FragmentDataSettingDialogBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        if (data == null) builder.setCustomTitle(layoutInflater.inflate(R.layout.item_add_dialog_title, null))
        else builder.setCustomTitle(layoutInflater.inflate(R.layout.item_modify_dialog_title, null))
        builder.setView(view)

        // Dialog Component Field
        val medsNameLayout = binding!!.dialogMedsNameLayout
        val medsName = binding!!.medsNameText
        val medsDetail = binding!!.medsDetailText
        val medsDate = binding!!.medsDateText
        val rcvMedsTimer = binding!!.dialogMedsTimerList
        val medsTimer: Button = binding!!.dialogMedsTimerAddBtn
        val submit = binding!!.submit
        val cancel = binding!!.cancel

        // Setting RecyclerView about timer list
        rcvMedsTimer.setHasFixedSize(false)
        rcvMedsTimer.layoutManager = LinearLayoutManager(activity)
        rcvMedsTimer.adapter = adapter
        rcvMedsTimer.addItemDecoration(VerticalItemDecorator(10))

        // If Accessible Data is not null, preset input form
        var startDate: Long? = null
        var endDate: Long? = null
        if (data != null) {
            medsName.setText(data!!.medsName)
            if (data!!.medsDetail != null) medsDetail.setText(data!!.medsDetail)
            if (data!!.medsStartDate != null && data!!.medsEndDate != null) {
                medsDate.setText(String.format("%s~%s", data!!.medsStartDate, data!!.medsEndDate))
            }
            val parser: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN)
            try {
                if (data!!.medsStartDate != null && data!!.medsEndDate != null) {
                    startDate = parser.parse(data!!.medsStartDate.toString())?.time
                    endDate = parser.parse(data!!.medsEndDate.toString())?.time
                } else {
                    endDate = null
                    startDate = endDate
                }
            } catch (e: ParseException) {
                Log.d("DataSettingDialog", "Meds_date parsing error")
                throw RuntimeException()
            }
            submit.isEnabled = true
        }
        val filter =
            InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
                for (i in start until end) if (Character.isWhitespace(
                        source[i]
                    )
                ) return@InputFilter ""
                null
            }

        // TextInput Exception Control Methods
        val afterTextChanged: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                submitDataChanged(medsName.text.toString())
            }
        }

        submitFormState.observe(this) { submitFormState: SubmitFormState? ->
            if (submitFormState == null) return@observe
            submit.isEnabled = submitFormState.isDataValid
            if (submitFormState.medsNameError != null) {
                medsNameLayout.isErrorEnabled = true
                medsNameLayout.error = getString(submitFormState.medsNameError!!)
            } else medsNameLayout.isErrorEnabled = false
        }

        // Main Body(Setting Functions for each Components)
        medsName.addTextChangedListener(afterTextChanged)
        medsName.filters = arrayOf(filter)
        medsDate.setOnClickListener {
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
                medsDate.setText(String.format("%s~%s", dateFormat.format(start), dateFormat.format(end)))
            }
            datePicker.addOnNegativeButtonClickListener { }
        }
        medsTimer.setOnClickListener { showAlarmSettingDialog() }
        submit.setOnClickListener {
            val data1: MedsData
            val medsNameText = medsName.text.toString()
            val medsDetailText =
                if (medsDetail.text.toString() == "null") null else medsDetail.text.toString()
            val medsDateText =
                if (medsDate.text.toString() == "null") null else medsDate.text.toString()
            data1 = if (medsDateText!!.isNotEmpty()) {
                    val dates = medsDateText.split("~".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    MedsData(medsNameText, medsDetailText, dates[0], dates[1])
                } else {
                    MedsData(medsNameText, medsDetailText, null, null)
                }
            data1.alarms = includedAlarms
            if (data == null) onDialogActionListener.onAddedMedicationData(data1)
            else onDialogActionListener.onModifiedMedicationData(snapshot_id!!, data1)
            dismiss()
        }
        cancel.setOnClickListener { dismiss() }
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        data = null
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