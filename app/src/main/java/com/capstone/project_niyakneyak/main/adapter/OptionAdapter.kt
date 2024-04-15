package com.capstone.project_niyakneyak.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ItemRecyclerOptionBinding
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import com.capstone.project_niyakneyak.main.listener.OnClickedOptionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

open class OptionAdapter(private val options: List<String>?, private val medsData: List<MedicineData>, private val onClickedOptionListener: OnClickedOptionListener): RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerOptionBinding): RecyclerView.ViewHolder(binding.root){
        private val ioScope = CoroutineScope(Dispatchers.IO)
        private val mainScope = CoroutineScope(Dispatchers.Main)
        private val openApiFunctions = OpenApiFunctions()
        private var jsonObject: JSONObject? = null
        private val resultObject: JSONObject = JSONObject()
        private val channel = Channel<String>()
        fun bind(option: String, medsData: List<MedicineData>, onClickedOptionListener: OnClickedOptionListener){
            binding.contentActiveApiFunctionBtn.text = option
            binding.contentActiveApiFunctionBtn.setOnClickListener {
                binding.contentResultDirectionImg.visibility = View.GONE
                binding.progressBar2.visibility = View.VISIBLE
                getJSONObject(option, medsData)
            }
        }

        private fun getJSONObject(option: String, medsData: List<MedicineData>){
            when(option){
                OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST->{
                    ioScope.launch {
                        for(data in medsData){
                            try{
                                jsonObject = openApiFunctions.getUsageJointPrdtList(data.itemSeq, null, 1, 25)
                                resultObject.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items"))
                                val totalCount = jsonObject!!.getJSONObject("body").getInt("totalCount")
                                for(i in 1 until (totalCount / 25 + 1)){
                                    jsonObject = openApiFunctions.getUsageJointPrdtList(data.itemSeq, null, i + 1, 25)
                                    resultObject.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items"))
                                }
                            } catch (exception: JSONException){
                                Log.w("OptionAdapter", "Error Occurred: $exception")
                            }
                        }

                        if(!resultObject.isNull("items")) channel.send("Success")
                        else channel.send("Failed")
                    }
                    mainScope.launch {
                        val msg = channel.receive()
                        binding.progressBar2.visibility = View.GONE
                        binding.contentResultDirectionImg.visibility = View.VISIBLE
                        if(msg == "Success"){
                            if(resultObject.isNull("items")) binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_check_circle_24)
                            else {
                                Log.w("OptionAdapter", resultObject.toString())
                                binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_error_24)
                            }
                        }else{
                            binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_running_with_errors_24)
                        }
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerOptionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return options?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options?.get(position)
        if (option != null)
            holder.bind(option, medsData, onClickedOptionListener)
        else return
    }
}