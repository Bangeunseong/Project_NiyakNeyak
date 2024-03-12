package com.capstone.project_niyakneyak.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedsBinding
import com.capstone.project_niyakneyak.ui.main.listener.OnMedicationChangedListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

open class MedicationAdapter(query: Query, private val listener: OnMedicationChangedListener) :
    FireStoreAdapter<MedicationAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_meds, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val itemTitle: TextView = view.findViewById(R.id.item_title)
        private val itemDetail: TextView = view.findViewById(R.id.item_detail)
        private val itemDuration: TextView = view.findViewById(R.id.item_duration)
        private val modifyBtn: ImageButton = view.findViewById(R.id.item_modify_button)
        private val deleteBtn: ImageButton = view.findViewById(R.id.item_delete_button)
        fun bind(snapshot: DocumentSnapshot, listener: OnMedicationChangedListener?){
            val medsData = snapshot.toObject<MedsData>() ?: return

            itemTitle.text = medsData.medsName
            if (medsData.medsDetail != null) itemDetail.text =
                String.format("Detail: %s", medsData.medsDetail)
            else itemDetail.text = String.format("Detail: %s", "None")
            if (medsData.medsStartDate != null && medsData.medsEndDate != null) itemDuration.text =
                String.format("Duration: %s~%s", medsData.medsStartDate, medsData.medsEndDate)
            else itemDuration.text = String.format("Duration: %s", "None")
            modifyBtn.setOnClickListener { listener?.onModifyBtnClicked(snapshot) }
            deleteBtn.setOnClickListener { listener?.onDeleteBtnClicked(snapshot) }
        }
    }
}