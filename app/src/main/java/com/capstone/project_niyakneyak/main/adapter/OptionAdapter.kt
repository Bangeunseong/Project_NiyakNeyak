package com.capstone.project_niyakneyak.main.adapter

import android.os.Build
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

open class OptionAdapter(
    private val options: List<String>?,
    private val medsData: List<MedicineData>,
    private val onClickedOptionListener: OnClickedOptionListener) :
    RecyclerView.Adapter<OptionAdapter.ViewHolder>() {

    private val jobControl = Job()

    inner class ViewHolder(val binding: ItemRecyclerOptionBinding): RecyclerView.ViewHolder(binding.root){
        private val openApiFunctions = OpenApiFunctions()
        private var jsonObject: JSONObject? = null
        private var resultObject: JSONObject? = null
        private val channel = Channel<String>()
        private var ioJob: Job? = null
        private var mainJob: Job? = null
        fun bind(option: String){
            binding.contentActiveApiFunctionBtn.text = option
            binding.contentActiveApiFunctionBtn.setOnClickListener {
                CoroutineScope(Dispatchers.Default + jobControl).launch {
                    if (ioJob == null || (ioJob!!.isCompleted && mainJob!!.isCompleted)){
                        ioJob = checkMedicineIsValid(option)
                        mainJob = changeIo(onClickedOptionListener)
                    }
                }
            }
        }

        private fun checkMedicineIsValid(option: String): Job?{
            var job: Job? = null
            when(option){
                OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST-> job = activateAPIFunction(0)
                OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST-> job = activateAPIFunction(1)
                OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST-> job = activateAPIFunction(2)
                OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST-> job = activateAPIFunction(3)
                OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST-> job = activateAPIFunction(4)
            }
            return job
        }

        private fun activateAPIFunction(type: Int): Job? {
            var job: Job? = null
            when (type) {
                0 -> job = CoroutineScope(Dispatchers.IO).launch {
                    var result: JSONObject? = null
                    for (data in medsData) {
                        try {
                            val mixedItem = JSONObject().put("itemSeq", data.itemSeq)
                            jsonObject = openApiFunctions.getUsageJointPrdtList(data.itemSeq, null, 1, 25)
                            Log.w("OptionAdapter", jsonObject.toString())
                            if (!jsonObject!!.getJSONObject("body").isNull("items")) {
                                for (pos in 0 until jsonObject!!.getJSONObject("body")
                                    .getJSONArray("items").length()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        mixedItem.append(
                                            "mixedItems",
                                            jsonObject!!.getJSONObject("body").getJSONArray("items")
                                                .getJSONObject(pos)
                                        )
                                    } else{
                                        if(mixedItem.isNull("mixedItems")) mixedItem.put("mixedItems", JSONArray())
                                        mixedItem.accumulate("mixedItems",
                                            jsonObject!!.getJSONObject("body").getJSONArray("items")
                                                .getJSONObject(pos))
                                    }
                                }
                            }
                            val totalCount = jsonObject!!.getJSONObject("body").getInt("totalCount")
                            for (i in 1 until (totalCount / 25 + 1)) {
                                jsonObject = openApiFunctions.getUsageJointPrdtList(
                                    data.itemSeq,
                                    null,
                                    i + 1,
                                    25
                                )
                                for (pos in 0 until jsonObject!!.getJSONObject("body")
                                    .getJSONArray("items").length()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        mixedItem.append(
                                            "mixedItems",
                                            jsonObject!!.getJSONObject("body").getJSONArray("items")
                                                .getJSONObject(pos)
                                        )
                                    } else{
                                        if(mixedItem.isNull("mixedItems")) mixedItem.put("mixedItems", JSONArray())
                                        mixedItem.accumulate("mixedItems",
                                            jsonObject!!.getJSONObject("body").getJSONArray("items")
                                                .getJSONObject(pos))
                                    }
                                }
                            }
                            if (result == null) result = JSONObject().put("typeName", OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                result!!.append("items", mixedItem)
                            } else {
                                if(result!!.isNull("items")) result.put("items", JSONArray())
                                result.accumulate("items", mixedItem)
                            }

                        } catch (exception: JSONException) {
                            Log.w("OptionAdapter", "Error Occurred: $exception")
                            channel.send("Failed")
                            return@launch
                        }
                    }

                    if (result == null) channel.send("Success_NotFound")
                    else if (!result.isNull("items")) {
                        Log.w("Option Adapter", result.toString())
                        var flag = false
                        for (pos in 0 until result.getJSONArray("items").length()) {
                            if (result.getJSONArray("items").getJSONObject(pos)
                                    .isNull("mixedItems")
                            ) continue
                            for (data in medsData) {
                                for (mixedPos in 0 until result.getJSONArray("items").getJSONObject(pos).getJSONArray("mixedItems").length()) {
                                    if (result.getJSONArray("items").getJSONObject(pos).getJSONArray("mixedItems").getJSONObject(mixedPos).getString("MIXTURE_ITEM_SEQ") == data.itemSeq) {
                                        if (resultObject == null) resultObject = JSONObject().put("typeName", OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST)
                                        else { if(!resultObject!!.isNull("items")) resultObject!!.remove("items") }
                                        val mixedObject =
                                            result.getJSONArray("items").getJSONObject(pos)
                                                .getJSONArray("mixedItems").getJSONObject(mixedPos)
                                        val inputObject = JSONObject().put("itemSeq", data.itemSeq)
                                            .put("mixedItem", mixedObject)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            resultObject!!.append("items", inputObject)
                                        } else {
                                            if(resultObject!!.isNull("items")) resultObject!!.put("items", JSONArray())
                                            resultObject!!.accumulate("items", inputObject)
                                        }
                                        flag = true
                                    }
                                }
                            }
                        }
                        if (!flag)
                            channel.send("Success_NotFound")
                        else channel.send("Success")
                    } else if (result.isNull("items")) channel.send("Success_NotFound")
                    else channel.send("Failed")

                    return@launch
                }

                1 -> job = CoroutineScope(Dispatchers.IO).launch {
                    for (data in medsData) {
                        try {
                            jsonObject = openApiFunctions.getElderlyAttentionPrdtList(data.itemSeq, null, 1, 25)
                            Log.w("OptionAdapter", jsonObject.toString())
                            if (!jsonObject!!.getJSONObject("body").isNull("items")) {
                                if (resultObject == null)
                                    resultObject = JSONObject().put("typeName", OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST)
                                else { if(!resultObject!!.isNull("items")) resultObject!!.remove("items") }
                                for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        resultObject!!.append("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    } else{
                                        if(resultObject!!.isNull("items")) resultObject!!.put("items", JSONArray())
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            }
                        } catch (exception: JSONException) {
                            Log.w("OptionAdapter", "Error Occurred: $exception")
                            channel.send("Failed")
                            return@launch
                        }
                    }

                    if (resultObject == null) channel.send("Success_NotFound")
                    else if (!resultObject!!.isNull("items")) channel.send("Success")
                    else if (resultObject!!.isNull("items")) channel.send("Success_NotFound")
                    else channel.send("Failed")

                    return@launch
                }

                2 -> job = CoroutineScope(Dispatchers.IO).launch {
                    for (data in medsData) {
                        try {
                            jsonObject =
                                openApiFunctions.getMdcCnsDatePrdtList(data.itemSeq, null, 1, 25)
                            Log.w("OptionAdapter", jsonObject.toString())
                            if (!jsonObject!!.getJSONObject("body").isNull("items")) {
                                if (resultObject == null)
                                    resultObject = JSONObject().put("typeName", OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST)
                                else { if(!resultObject!!.isNull("items")) resultObject!!.remove("items") }
                                for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        resultObject!!.append("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    } else{
                                        if(resultObject!!.isNull("items")) resultObject!!.put("items", JSONArray())
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            }
                        } catch (exception: JSONException) {
                            Log.w("OptionAdapter", "Error Occurred: $exception")
                            channel.send("Failed")
                            return@launch
                        }
                    }

                    if (resultObject == null) channel.send("Success_NotFound")
                    else if (!resultObject!!.isNull("items")) channel.send("Success")
                    else if (resultObject!!.isNull("items")) channel.send("Success_NotFound")
                    else channel.send("Failed")

                    return@launch
                }

                3 -> job = CoroutineScope(Dispatchers.IO).launch {
                    for (data in medsData) {
                        try {
                            jsonObject = openApiFunctions.getSpecificAgeRangePrdtList(data.itemSeq, null, 1, 25)
                            Log.w("OptionAdapter", jsonObject.toString())
                            if (!jsonObject!!.getJSONObject("body").isNull("items")) {
                                if (resultObject == null)
                                    resultObject = JSONObject().put("typeName", OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST)
                                else { if(!resultObject!!.isNull("items")) resultObject!!.remove("items") }
                                for (pos in 0 until jsonObject!!.getJSONObject("body")
                                    .getJSONArray("items").length()) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        resultObject!!.append("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    } else{
                                        if(resultObject!!.isNull("items")) resultObject!!.put("items", JSONArray())
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            }
                        } catch (exception: JSONException) {
                            Log.w("OptionAdapter", "Error Occurred: $exception")
                            channel.send("Failed")
                            return@launch
                        }
                    }

                    if (resultObject == null) channel.send("Success_NotFound")
                    else if (!resultObject!!.isNull("items")) channel.send("Success")
                    else if (resultObject!!.isNull("items")) channel.send("Success_NotFound")
                    else channel.send("Failed")

                    return@launch
                }

                4 -> job = CoroutineScope(Dispatchers.IO).launch {
                    for (data in medsData) {
                        try {
                            jsonObject = openApiFunctions.getPregWomPrdtList(data.itemSeq, null, 1, 25)
                            Log.w("OptionAdapter", jsonObject.toString())
                            if (!jsonObject!!.getJSONObject("body").isNull("items")) {
                                if (resultObject == null)
                                    resultObject = JSONObject().put("typeName", OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST)
                                else { if(!resultObject!!.isNull("items")) resultObject!!.remove("items") }
                                for (pos in 0 until jsonObject!!.getJSONObject("body").getJSONArray("items").length()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        resultObject!!.append("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    } else{
                                        if(resultObject!!.isNull("items")) resultObject!!.put("items", JSONArray())
                                        resultObject!!.accumulate("items", jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(pos))
                                    }
                                }
                            }
                        } catch (exception: JSONException) {
                            Log.w("OptionAdapter", "Error Occurred: $exception")
                            channel.send("Failed")
                            return@launch
                        }
                    }

                    if (resultObject == null) channel.send("Success_NotFound")
                    else if (!resultObject!!.isNull("items")) channel.send("Success")
                    else if (resultObject!!.isNull("items")) channel.send("Success_NotFound")
                    else channel.send("Failed")

                    return@launch
                }
            }
            return job
        }

        private fun changeIo(onClickedOptionListener: OnClickedOptionListener): Job{
            return CoroutineScope(Dispatchers.Main).launch {
                binding.contentResultDirectionImg.visibility = View.GONE
                binding.progressBar2.visibility = View.VISIBLE
                binding.contentActiveApiFunctionBtn.isClickable = false

                val msg = channel.receive()
                binding.progressBar2.visibility = View.GONE
                binding.contentResultDirectionImg.visibility = View.VISIBLE
                binding.contentActiveApiFunctionBtn.isClickable = true
                when (msg){
                    "Success"->{
                        Log.w("OptionAdapter", resultObject.toString())
                        binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_error_24)
                        onClickedOptionListener.onOptionClicked(resultObject!!.getString("typeName"), resultObject)
                    }
                    "Success_NotFound"->{
                        binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_check_circle_24)
                    }
                    else->{
                        binding.contentResultDirectionImg.setImageResource(R.drawable.baseline_running_with_errors_24)
                    }
                }

                return@launch
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

    fun cancelAllActiveCoroutines(){
        if(jobControl.isActive)
            jobControl.cancel()
    }
}