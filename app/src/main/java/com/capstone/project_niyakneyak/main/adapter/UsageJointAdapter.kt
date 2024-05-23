package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.inspect_model.UsageJointData
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerUsageJointBinding
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

open class UsageJointAdapter(query: Query): FireStoreAdapter<UsageJointAdapter.ViewHolder>(query) {
    private var _openApiFunctions: OpenApiFunctions? = null
    private val openApiFunctions get() = _openApiFunctions!!
    private val job = Job()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerUsageJointBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Api Function Access
        if(_openApiFunctions == null) _openApiFunctions = OpenApiFunctions()
        holder.bind(getSnapshot(position))
    }

    inner class ViewHolder(val binding: ItemRecyclerUsageJointBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot){
            val usageJointData = snapshot.toObject<UsageJointData>() ?: return

            binding.subjectMedicineText.text = usageJointData.itemName
            binding.warningMedicineText.text = usageJointData.mixtureItemName

            var subjectJsonObj: JSONObject? = null
            var targetJsonObj: JSONObject? = null
            CoroutineScope(Dispatchers.Main + job).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    subjectJsonObj = performDataFetchTaskAsync(usageJointData.itemName!!, 1, 1)
                    targetJsonObj = performDataFetchTaskAsync(usageJointData.mixtureItemName!!, 1, 1)
                }.invokeOnCompletion {
                    if(it == null){
                        Glide.with(itemView).load(subjectJsonObj?.getJSONObject("body")?.getJSONArray("items")?.getJSONObject(0)?.get(MedicineData.FIELD_BIG_PRDT_IMG_URL).toString()).placeholder(
                            R.drawable.baseline_medication_liquid_24).into(binding.subjectMedicineImg)
                        Glide.with(itemView).load(targetJsonObj?.getJSONObject("body")?.getJSONArray("items")?.getJSONObject(0)?.get(MedicineData.FIELD_BIG_PRDT_IMG_URL).toString()).placeholder(
                            R.drawable.baseline_medication_liquid_24).into(binding.warningMedicineImg)
                    }
                }
            }
        }
    }

    private suspend fun performDataFetchTaskAsync(query: String, pageNo: Int?, numOfRows: Int?): JSONObject? =
        withContext(Dispatchers.IO) {
            return@withContext openApiFunctions.getPrdtMtrDetails(query, pageNo, numOfRows)
        }

    fun startCoroutineJob(){
        if(job.isCancelled) job.start()
    }

    fun stopCoroutineJob(){
        if(!job.isCompleted) job.cancel()
    }
}