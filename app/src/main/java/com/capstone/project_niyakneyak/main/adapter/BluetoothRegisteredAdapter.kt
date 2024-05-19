package com.capstone.project_niyakneyak.main.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.databinding.ItemRecyclerBluetoothBinding

class BluetoothRegisteredAdapter(private var devices: MutableList<BluetoothDevice>): RecyclerView.Adapter<BluetoothRegisteredAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerBluetoothBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(device: BluetoothDevice){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerBluetoothBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet Implemented!")
    }

    fun setBluetoothDeviceData(devices: MutableList<BluetoothDevice>){
        this.devices = devices
        notifyDataSetChanged()
    }

    fun clear(){
        this.devices.clear()
    }

    fun add(device: BluetoothDevice){
        this.devices.add(device)
        notifyItemChanged(devices.size - 1)
    }
}