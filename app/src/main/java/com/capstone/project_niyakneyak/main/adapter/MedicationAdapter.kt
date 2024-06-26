package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedsBinding
import com.capstone.project_niyakneyak.main.listener.OnMedicationChangedListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class MedicationAdapter(query: Query, private val listener: OnMedicationChangedListener) :
    FireStoreAdapter<MedicationAdapter.ViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerMedsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    inner class ViewHolder(val binding: ItemRecyclerMedsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot, listener: OnMedicationChangedListener?){
            val medsData = snapshot.toObject<MedicineData>() ?: return

            binding.itemTitle.text = medsData.itemName
            if (medsData.medsDetail != null) binding.itemDetail.text =
                String.format("Detail: %s", medsData.medsDetail)
            else binding.itemDetail.text = String.format("Detail: %s", "None")
            if (medsData.medsStartDate != null && medsData.medsEndDate != null){
                binding.itemDuration.text = String.format("Duration: %s~%s",
                    SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN).format(medsData.medsStartDate!!),
                    SimpleDateFormat("yyyy/MM/dd", Locale.KOREAN).format(medsData.medsEndDate!!))
                if(medsData.medsStartDate!!.after(Date(System.currentTimeMillis())) || medsData.medsEndDate!!.before(Date(System.currentTimeMillis())))
                    binding.itemLayout.setBackgroundResource(R.drawable.item_recycler_bg_disabled)
            }
            else binding.itemDuration.text = String.format("Duration: %s", "None")
            binding.itemModifyButton.setOnClickListener { listener?.onModifyBtnClicked(snapshot) }
            binding.itemDeleteButton.setOnClickListener { listener?.onDeleteBtnClicked(snapshot) }
        }
    }
}