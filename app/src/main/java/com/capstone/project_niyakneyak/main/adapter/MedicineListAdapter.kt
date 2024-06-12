package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerMedicineBinding
import com.capstone.project_niyakneyak.main.listener.OnCheckedSearchItemListener
import org.json.JSONArray
import org.json.JSONObject

/**
 * This [MedicineListAdapter] used for showing results of API Function([com.capstone.project_niyakneyak.main.etc.OpenApiFunctions.getPrdtMtrDetails])
 * By using this function, this adapter will show API results using [RecyclerView]
 * This adapter needs [JSONArray] and [OnCheckedSearchItemListener] as parameters, [JSONArray] will be used as data input
 * and [OnCheckedSearchItemListener] will be used for transferring selected [MedicineData]
 */

open class MedicineListAdapter(private var jsonArray: JSONArray, private val onCheckedSearchItemListener: OnCheckedSearchItemListener): RecyclerView.Adapter<MedicineListAdapter.ViewHolder>() {
    private var positionList = Array(jsonArray.length()) { false }
    private var selectedPos = -1
    class ViewHolder(val binding: ItemRecyclerMedicineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jsonObject: JSONObject){
            binding.medicineName.text = jsonObject.get(MedicineData.FIELD_ITEM_NAME).toString()
            binding.medicineCategory.text = jsonObject.get(MedicineData.FIELD_PRDUCT_TYPE).toString()
            binding.medicineEntp.text = jsonObject.get(MedicineData.FIELD_ENPT_NAME).toString()
            binding.medicineMtr.text = jsonObject.get(MedicineData.FIELD_ITEM_INGR_NAME).toString()
            binding.medicineImg.contentDescription = jsonObject.get(MedicineData.FIELD_ITEM_NAME).toString()
            Glide.with(itemView).load(jsonObject.get(MedicineData.FIELD_BIG_PRDT_IMG_URL).toString()).placeholder(R.drawable.baseline_medication_liquid_24).into(binding.medicineImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jsonArray.getJSONObject(position))
        holder.binding.radioButton.isChecked = positionList[holder.absoluteAdapterPosition]
        holder.binding.radioButton.setOnClickListener {
            val prevPos = selectedPos
            if(prevPos != -1) positionList[prevPos] = false
            selectedPos = holder.absoluteAdapterPosition
            positionList[selectedPos] = true
            onCheckedSearchItemListener.onItemClicked(prevPos, selectedPos)
        }
    }

    fun setJSONArray(jsonArray: JSONArray){
        this.jsonArray = jsonArray
        positionList = Array(jsonArray.length()) { false }
        notifyDataSetChanged()
    }
}