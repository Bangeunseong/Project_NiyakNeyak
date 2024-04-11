package com.capstone.project_niyakneyak.main.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.DialogFragmentFilterBinding
import com.capstone.project_niyakneyak.main.etc.Filters
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FilterDialogFragment: DialogFragment() {
    // Interface for data transfer
    interface FilterListener{
        fun onFilter(filters: Filters)
    }

    // Field for initializing view and listener
    private var filterListener: FilterListener? = null
    private var _binding: DialogFragmentFilterBinding? = null
    private val binding get() = _binding!!

    // Field for filter parameters
    private val selectedSortBy: String? get() {
        val selected = binding.spinner.selectedItem as String
        return if(getString(R.string.all_category) == selected) null else selected
    }
    private val selectedSortDirection: Query.Direction get() {
        val selected = binding.spinnerSortDirection.selectedItem as String
        return if("Ascending" == selected) return Query.Direction.ASCENDING
        else Query.Direction.DESCENDING
    }
    private val selectedStartDate: String? get() {
        return if(TextUtils.isEmpty(binding.startDateTime.text.toString())) null
        else binding.startDateTime.text.toString()
    }
    private val selectedEndDate: String? get() {
        return if(TextUtils.isEmpty(binding.endDateTime.text.toString())) null
        else binding.endDateTime.text.toString()
    }

    // Filters object
    private val filters: Filters get() {
        val filters = Filters()
        filters.sortBy = selectedSortBy
        filters.sortDirection = selectedSortDirection
        filters.startDate = selectedStartDate
        filters.endDate = selectedEndDate
        return filters
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentFilterBinding.inflate(inflater, container, false)
        binding.ok.setOnClickListener { filterListener?.onFilter(filters) }
        binding.cancel.setOnClickListener { dismiss() }

        binding.changeStartDateBtn.setOnClickListener {
            val datePickerBuilder = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select Start Date")
            val datePicker = datePickerBuilder.build()
            datePicker.show(parentFragmentManager, "FILTER_DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener {
                val date = Date(it)
                binding.startDateTime.setText(SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN).format(date))
            }
            datePicker.addOnNegativeButtonClickListener {  }
        }
        binding.changeEndDateBtn.setOnClickListener {
            val datePickerBuilder = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select End Date")
            val datePicker = datePickerBuilder.build()
            datePicker.show(parentFragmentManager, "FILTER_DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener {
                val date = Date(it)
                binding.endDateTime.setText(SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN).format(date))
            }
            datePicker.addOnNegativeButtonClickListener {  }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(parentFragment is FilterListener)
            filterListener = parentFragment as FilterListener
    }
}