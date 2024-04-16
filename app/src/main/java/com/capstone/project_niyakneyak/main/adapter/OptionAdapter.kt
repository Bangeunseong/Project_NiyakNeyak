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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

open class OptionAdapter(
    private val options: List<String>?,
    private val medsData: List<MedicineData>,
    private val onClickedOptionListener: OnClickedOptionListener) :
    RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerOptionBinding): RecyclerView.ViewHolder(binding.root){
        private val ioScope = CoroutineScope(Dispatchers.IO)
        private val mainScope = CoroutineScope(Dispatchers.Main)
        private val openApiFunctions = OpenApiFunctions()
        private var jsonObject: JSONObject? = null
        private var resultObject: JSONObject? = null
        private val channel = Channel<String>()
        fun bind(option: String){
            binding.contentActiveApiFunctionBtn.text = option
            binding.contentActiveApiFunctionBtn.setOnClickListener {
                binding.contentResultDirectionImg.visibility = View.GONE
                binding.progressBar2.visibility = View.VISIBLE
                checkMedicineIsValid(option)
                changeIo()
            }
        }

        private fun checkMedicineIsValid(option: String){
            when(option){
                OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST-> activateAPIFunction(0)
                OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST-> activateAPIFunction(1)
                OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST-> activateAPIFunction(2)
                OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST-> activateAPIFunction(3)
                OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST-> activateAPIFunction(4)
            }
        }

        private fun activateAPIFunction(type: Int) {
            when (type) {
                0 -> ioScope.launch {
                        for (data in medsData) {
                            try {
                                jsonObject = openApiFunctions.getUsageJointPrdtList(data.itemSeq, null, 1, 25)
                                Log.w("OptionAdapter", jsonObject.toString())
                                if(!jsonObject!!.getJSONObject("body").isNull("items")){
                                    for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                        if(resultObject == null) resultObject = JSONObject()
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                                val totalCount = jsonObject!!.getJSONObject("body").getInt("totalCount")
                                for (i in 1 until (totalCount / 25 + 1)) {
                                    jsonObject = openApiFunctions.getUsageJointPrdtList(data.itemSeq, null, i + 1, 25)
                                    for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            } catch (exception: JSONException) {
                                Log.w("OptionAdapter", "Error Occurred: $exception")
                                channel.send("Failed")
                                return@launch
                            }
                        }

                        if(resultObject == null) channel.send("Success_NotFound")
                        else if(!resultObject!!.isNull("items")){
                            val mixedMeds = mutableMapOf<String, JSONObject>()
                            for(data in medsData){
                                val mixedList = JSONObject()
                                for(pos in 0 until resultObject!!.getJSONArray("items").length()){
                                    if(data.itemSeq == resultObject!!.getJSONArray("items").getJSONObject(pos).getString("MIXTURE_ITEM_SEQ")){
                                        mixedList.accumulate("items", resultObject!!.getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                                if(!mixedList.isNull("items"))
                                    mixedMeds[data.itemSeq!!] = mixedList
                            }
                            if(mixedMeds.isEmpty())
                                channel.send("Success_NotFound")
                            else channel.send("Success")
                        }
                        else if(resultObject!!.isNull("items")) channel.send("Success_NotFound")
                        else channel.send("Failed")
                    }
                1 -> ioScope.launch {
                        for (data in medsData) {
                            try {
                                jsonObject = openApiFunctions.getElderlyAttentionPrdtList(data.itemSeq, null, 1, 25)
                                Log.w("OptionAdapter", jsonObject.toString())
                                if(!jsonObject!!.getJSONObject("body").isNull("items")){
                                    for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                        if(resultObject == null) resultObject = JSONObject()
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            } catch (exception: JSONException) {
                                Log.w("OptionAdapter", "Error Occurred: $exception")
                                channel.send("Failed")
                                return@launch
                            }
                        }

                        if(resultObject == null) channel.send("Success_NotFound")
                        else if(!resultObject!!.isNull("items")) {
                            channel.send("Success")
                        }
                        else if(resultObject!!.isNull("items")) channel.send("Success_NotFound")
                        else channel.send("Failed")
                    }
                2 -> ioScope.launch {
                        for (data in medsData) {
                            try {
                                jsonObject = openApiFunctions.getMdcCnsDatePrdtList(data.itemSeq, null, 1, 25)
                                Log.w("OptionAdapter", jsonObject.toString())
                                if(!jsonObject!!.getJSONObject("body").isNull("items")){
                                    for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                        if(resultObject == null) resultObject = JSONObject()
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            } catch (exception: JSONException) {
                                Log.w("OptionAdapter", "Error Occurred: $exception")
                                channel.send("Failed")
                                return@launch
                            }
                        }

                        if(resultObject == null) channel.send("Success_NotFound")
                        else if(!resultObject!!.isNull("items")) {
                            channel.send("Success")
                        }
                        else if(resultObject!!.isNull("items")) channel.send("Success_NotFound")
                        else channel.send("Failed")
                    }
                3 -> ioScope.launch {
                        for (data in medsData) {
                            try {
                                jsonObject = openApiFunctions.getSpecificAgeRangePrdtList(data.itemSeq, null, 1, 25)
                                Log.w("OptionAdapter", jsonObject.toString())
                                if(!jsonObject!!.getJSONObject("body").isNull("items")){
                                    for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                        if(resultObject == null) resultObject = JSONObject()
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            } catch (exception: JSONException) {
                                Log.w("OptionAdapter", "Error Occurred: $exception")
                                channel.send("Failed")
                                return@launch
                            }
                        }

                        if(resultObject == null) channel.send("Success_NotFound")
                        else if(!resultObject!!.isNull("items")) {
                            channel.send("Success")
                        }
                        else if(resultObject!!.isNull("items")) channel.send("Success_NotFound")
                        else channel.send("Failed")
                    }
                4 -> ioScope.launch {
                        for (data in medsData) {
                            try {
                                jsonObject = openApiFunctions.getPregWomPrdtList(data.itemSeq, null, 1, 25)
                                Log.w("OptionAdapter", jsonObject.toString())
                                if(jsonObject != null && !jsonObject!!.getJSONObject("body").isNull("items")) {
                                    for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                        if(resultObject == null) resultObject = JSONObject()
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            } catch (exception: JSONException) {
                                Log.w("OptionAdapter", "Error Occurred: $exception")
                                channel.send("Failed")
                                return@launch
                            }
                        }

                        if(resultObject == null) channel.send("Success_NotFound")
                        else if(!resultObject!!.isNull("items")){
                            channel.send("Success")
                        }
                        else if(resultObject!!.isNull("items")) channel.send("Success_NotFound")
                        else channel.send("Failed")
                    }
            }
        }

        private fun changeIo(){
            mainScope.launch {
                val msg = channel.receive()
                binding.progressBar2.visibility = View.GONE
                binding.contentResultDirectionImg.visibility = View.VISIBLE
                when (msg){
                    "Success"->{
                        Log.w("OptionAdapter", resultObject.toString())
                        binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_error_24)
                    }
                    "Success_NotFound"->{
                        binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_check_circle_24)
                    }
                    else->{
                        binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_running_with_errors_24)
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
            holder.bind(option)
        else return
    }
}