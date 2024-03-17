package com.capstone.project_niyakneyak.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedsData
import com.capstone.project_niyakneyak.databinding.FragmentCheckListBinding
import com.capstone.project_niyakneyak.main.adapter.CheckDataAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.viewmodel.CheckViewModel
import com.capstone.project_niyakneyak.main.listener.OnCheckedAlarmListener
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * This Fragment is used for showing daily Medication list by using [CheckFragment.adapter].
 * [CheckFragment.adapter] will be set by using [CheckDataAdapter]
 */
class CheckFragment : Fragment(), OnCheckedAlarmListener {
    private lateinit var binding: FragmentCheckListBinding
    private var checkViewModel: CheckViewModel? = null
    private var adapter: CheckDataAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCheckListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkViewModel = ViewModelProvider(this)[CheckViewModel::class.java]


        binding.contentChecklist.setHasFixedSize(false)
        binding.contentChecklist.layoutManager = LinearLayoutManager(context)
        binding.contentChecklist.addItemDecoration(HorizontalItemDecorator(10))
        binding.contentChecklist.addItemDecoration(VerticalItemDecorator(20))
    }

    @Deprecated("This Function is for Alarm adapter")
    override fun onItemClicked(alarm: Alarm) {}

    //TODO: Need to add action when checkbox is checked
    override fun onItemClicked(medsID: Long, alarm: Alarm, isChecked: Boolean) {}

    companion object {
        fun newInstance(): CheckFragment {
            return CheckFragment()
        }
    }
}