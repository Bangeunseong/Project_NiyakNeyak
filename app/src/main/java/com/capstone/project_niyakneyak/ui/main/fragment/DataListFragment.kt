package com.capstone.project_niyakneyak.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.data.patient_model.PatientData
import com.capstone.project_niyakneyak.databinding.FragmentDataListBinding
import com.capstone.project_niyakneyak.ui.main.adapter.MainDataAdapter
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataListViewModel
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataListViewModelFactory
import com.capstone.project_niyakneyak.ui.main.listener.OnAddedDataListener
import com.capstone.project_niyakneyak.ui.main.listener.OnChangedDataListener
import com.capstone.project_niyakneyak.ui.main.listener.OnDeleteDataListener

/**
 * This Fragment is used for showing Medication info. by using [DataListFragment.adapter].
 * [DataListFragment.adapter] will be set by using [MainDataAdapter]
 */
class DataListFragment : Fragment() {
    private var binding: FragmentDataListBinding? = null
    private var adapter: MainDataAdapter? = null
    private var dataListViewModel: DataListViewModel? = null
    private val onAddedDataListener = object : OnAddedDataListener {
        override fun onAddedData(target: MedsData) {
            Log.d("MainActivity", "Data Addition called")
            val result = dataListViewModel?.patientData
            if (result is Result.Success<*>) {
                dataListViewModel!!.addMedsdata(target)
                val position = (result as Result.Success<PatientData?>).data!!.medsData!!.size - 1
                adapter!!.addItem(position)
                binding!!.contentMainGuide.visibility = View.GONE
            }
        }
    }
    private val onChangedDataListener = object : OnChangedDataListener {
        override fun onChangedData(origin: MedsData, changed: MedsData) {
            Log.d("MainActivity", "Data Modification called")
            val result = dataListViewModel?.patientData
            if (result is Result.Success<*>) {
                dataListViewModel!!.modifyMedsData(origin, changed)
                val position =
                    (result as Result.Success<PatientData?>).data!!.medsData!!.indexOf(changed)
                adapter!!.modifyItem(position)
                binding!!.contentMainGuide.visibility = View.GONE
            }
        }
    }
    private val onDeleteDataListener = object : OnDeleteDataListener {
        override fun onDeletedData(target: MedsData) {
            Log.d("MainActivity", "Data Deletion called")
            val result = dataListViewModel?.patientData
            if (result is Result.Success<*>) {
                val position =
                    (result as Result.Success<PatientData?>).data!!.medsData!!.indexOf(target)
                dataListViewModel!!.deleteMedsData(target)
                adapter!!.removeItem(position)
                if (adapter!!.itemCount < 1) binding!!.contentMainGuide.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataListViewModel = ViewModelProvider(this, DataListViewModelFactory()).get(
            DataListViewModel::class.java
        )
        dataListViewModel!!.getActionResult().observe(this) { actionResult: ActionResult? ->
            if (actionResult == null) return@observe
            if (actionResult.success != null) {
                Toast.makeText(context, actionResult.success!!.displayData, Toast.LENGTH_SHORT)
                    .show()
            }
            if (actionResult.error != null) {
                Toast.makeText(context, actionResult.error!!, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerAdapter for Medication Info.
        val result = dataListViewModel?.patientData
        adapter =
            if (result is Result.Success<*>) MainDataAdapter(
                requireActivity().supportFragmentManager,
                onChangedDataListener,
                onDeleteDataListener)
            else MainDataAdapter(
            requireActivity().supportFragmentManager,
            onChangedDataListener,
            onDeleteDataListener
        )
        binding!!.contentMainMeds.setHasFixedSize(false)
        binding!!.contentMainMeds.layoutManager = LinearLayoutManager(context)
        binding!!.contentMainMeds.adapter = adapter
        binding!!.contentMainMeds.addItemDecoration(VerticalItemDecorator(20))
        binding!!.contentMainMeds.addItemDecoration(HorizontalItemDecorator(10))
        binding!!.contentMainAdd.setOnClickListener { v: View? ->
            val addDataDialog: DialogFragment = DataSettingDialog(onAddedDataListener, null)
            val bundle = Bundle()
            bundle.putParcelable("BeforeModify", null)
            addDataDialog.arguments = bundle
            addDataDialog.show(requireActivity().supportFragmentManager, "DATA_DIALOG_FRAGMENT")
        }
        if (adapter!!.itemCount > 0) binding!!.contentMainGuide.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
        dataListViewModel = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment MainPage.
         */
        fun newInstance(): DataListFragment {
            return DataListFragment()
        }
    }
}