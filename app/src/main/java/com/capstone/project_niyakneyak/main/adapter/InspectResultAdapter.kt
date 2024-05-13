package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.inspect_model.InspectData
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerOtherOptionsBinding
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

open class InspectResultAdapter(query: Query): FireStoreAdapter<InspectResultAdapter.ViewHolder>(query) {
    private var _openApiFunctions: OpenApiFunctions? = null
    private val openApiFunctions get() = _openApiFunctions!!
    private val job = Job()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectResultAdapter.ViewHolder {
        return ViewHolder(ItemRecyclerOtherOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(_openApiFunctions == null) _openApiFunctions = OpenApiFunctions()
        holder.bind(getSnapshot(position))
    }

    inner class ViewHolder(val binding: ItemRecyclerOtherOptionsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(snapshot: DocumentSnapshot){
            val inspectData = snapshot.toObject<InspectData>() ?: return

            binding.contentInspectNameTxt.text = String.format("약물 이름: ${inspectData.itemName}")
            binding.contentInspectMaterialTxt.text = String.format("약물 성분: ${inspectData.mainIngr}")
            binding.contentInspectItemImg.contentDescription = inspectData.itemName
            if(!inspectData.prohbtContent.equals("null"))
                binding.contentInspectPrhbtTxt.text = String.format("주의 사항: ${inspectData.prohbtContent}")
            else binding.contentInspectPrhbtTxt.text = buildString { append("문제점이 발견 되었습니다! \n약물 복용 시 주의점을 반드시 확인하고 복용하시길 바랍니다!") }
            if(!inspectData.remark.equals("null"))
                binding.contentInspectRemarkTxt.text = String.format("비고: ${inspectData.remark}")
            else binding.contentInspectRemarkTxt.visibility = View.GONE

            var targetJsonObj: JSONObject? = null

            CoroutineScope(Dispatchers.Main + job).launch {
                binding.progressBar3.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    targetJsonObj = performDataFetchTaskAsync(inspectData.itemName!!, 1, 1)
                }.join()
                CoroutineScope(Dispatchers.Main).launch {
                    Glide.with(itemView).load(targetJsonObj?.getJSONObject("body")?.getJSONArray("items")?.getJSONObject(0)?.get(
                        MedicineData.FIELD_BIG_PRDT_IMG_URL).toString()).placeholder(
                        R.drawable.baseline_medication_liquid_24).into(binding.contentInspectItemImg)
                }.invokeOnCompletion {
                    binding.contentInspectItemImg.visibility = View.VISIBLE
                    binding.progressBar3.visibility = View.GONE
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