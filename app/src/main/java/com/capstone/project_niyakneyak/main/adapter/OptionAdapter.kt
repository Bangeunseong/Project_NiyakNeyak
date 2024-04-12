package com.capstone.project_niyakneyak.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.databinding.ItemRecyclerOptionBinding
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import com.capstone.project_niyakneyak.main.listener.OnClickedOptionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

open class OptionAdapter(private val options: List<String>?): RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerOptionBinding): RecyclerView.ViewHolder(binding.root){
        private var _ioScope: CoroutineScope? = null
        private val ioScope get() = _ioScope!!
        private var _mainScope: CoroutineScope? = null
        private val mainScope get() = _mainScope!!

        fun bind(option: String){
            _ioScope = CoroutineScope(Dispatchers.IO)
            _mainScope = CoroutineScope(Dispatchers.Main)
            binding.contentActiveApiFunctionBtn.text = option
            binding.contentActiveApiFunctionBtn.setOnClickListener {
                when (option){
                    OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST->{

                    }
                    OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST->{

                    }
                    OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST->{

                    }
                    OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST->{

                    }
                    OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST->{

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