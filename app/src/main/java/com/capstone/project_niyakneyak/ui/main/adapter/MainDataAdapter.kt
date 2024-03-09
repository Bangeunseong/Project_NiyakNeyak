package com.capstone.project_niyakneyak.ui.main.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.ui.main.fragment.DataSettingDialog
import com.capstone.project_niyakneyak.ui.main.listener.OnChangedDataListener
import com.capstone.project_niyakneyak.ui.main.listener.OnDeleteDataListener

/**
 * This adapter is used for showing registered Medication info. in
 * [com.capstone.project_niyakneyak.ui.main.fragment.DataListFragment]
 */
class MainDataAdapter(
    private val fragmentManager: FragmentManager,
    private val onChangedDataListener: OnChangedDataListener,
    private val onDeleteDataListener: OnDeleteDataListener
) : RecyclerView.Adapter<MainDataAdapter.ViewHolder>() {

    private val medsData: List<MedsData>

    init {
        medsData = ArrayList()
    }

    class ViewHolder(
        view: View,
        private val fragmentManager: FragmentManager,
        private val onChangedDataListener: OnChangedDataListener,
        private val onDeleteDataListener: OnDeleteDataListener) : RecyclerView.ViewHolder(view) {

        private val rcvTitle: TextView
        private val rcvDetail: TextView
        private val rcvDuration: TextView
        private val modifyBtn: ImageButton
        private val deleteBtn: ImageButton

        init {
            // Define click listener for the ViewHolder's View
            rcvTitle = view.findViewById(R.id.item_title)
            rcvDetail = view.findViewById(R.id.item_detail)
            rcvDuration = view.findViewById(R.id.item_duration)
            modifyBtn = view.findViewById(R.id.item_modify_button)
            deleteBtn = view.findViewById(R.id.item_delete_button)
        }

        fun bind(medsData: MedsData){
            rcvTitle.text = medsData.medsName
            if (medsData.medsDetail != null) rcvDetail.text =
                String.format("Detail: %s", medsData.medsDetail)
            else rcvDetail.text = String.format("Detail: %s", "None")
            if (medsData.medsStartDate != null && medsData.medsEndDate != null) rcvDuration.text =
                String.format("Duration: %s~%s", medsData.medsStartDate, medsData.medsEndDate)
            else rcvDuration.text = String.format("Duration: %s", "None")
            modifyBtn.setOnClickListener {
                val changeDataDialog: DialogFragment = DataSettingDialog(null, onChangedDataListener)
                val bundle = Bundle()
                bundle.putParcelable("BeforeModify", medsData)
                changeDataDialog.arguments = bundle
                changeDataDialog.show(fragmentManager, "DIALOG_FRAGMENT")
            }
            deleteBtn.setOnClickListener {
                onDeleteDataListener.onDeletedData(medsData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_meds, parent, false)
        return ViewHolder(view, fragmentManager, onChangedDataListener, onDeleteDataListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(medsData[position])
    }

    override fun getItemCount(): Int {
        return medsData.size
    }

    fun addItem(position: Int) {
        notifyItemInserted(position)
    }

    fun modifyItem(position: Int) {
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }
}