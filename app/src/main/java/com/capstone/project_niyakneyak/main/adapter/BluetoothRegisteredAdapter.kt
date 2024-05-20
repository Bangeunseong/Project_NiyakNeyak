package com.capstone.project_niyakneyak.main.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ItemRecyclerBluetoothBinding
import java.lang.StringBuilder

class BluetoothRegisteredAdapter(private val devices: MutableList<BluetoothDevice>, private val isConnected: MutableList<Boolean>): RecyclerView.Adapter<BluetoothRegisteredAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerBluetoothBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("MissingPermission")
        fun bind(device: BluetoothDevice, isConnected: Boolean){
            binding.bluetoothDeviceName.text = device.name
            if(device.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO)
                binding.bluetoothTypeIcon.setImageResource(R.drawable.icon_bluetooth_headset)
            else binding.bluetoothTypeIcon.setImageResource(R.drawable.icon_bluetooth_screen)
            if(isConnected){
                binding.bluetoothDeviceConnType.visibility = View.VISIBLE
                binding.bluetoothDeviceConnType.text = serviceTypes(device)
            }
            else binding.bluetoothDeviceConnType.visibility = View.GONE
            binding.bluetoothDeviceSettingBtn.setOnClickListener {
                TODO("Not yet Implemented")
            }
        }

        @SuppressLint("MissingPermission")
        private fun serviceTypes(device: BluetoothDevice): String{
            val serviceType = StringBuilder()
            val services = arrayOf(BluetoothClass.Service.TELEPHONY, BluetoothClass.Service.AUDIO, BluetoothClass.Service.CAPTURE,
                BluetoothClass.Service.INFORMATION, BluetoothClass.Service.NETWORKING, BluetoothClass.Service.POSITIONING, BluetoothClass.Service.OBJECT_TRANSFER)

            for(i in services.indices){
                if(device.bluetoothClass.hasService(services[i])){
                    if(serviceType.isNotEmpty())
                        serviceType.append(", ")
                    when(i){
                        0 -> serviceType.append("통화")
                        1 -> serviceType.append("오디오")
                        2 -> serviceType.append("캡쳐")
                        3 -> serviceType.append("정보")
                        4 -> serviceType.append("네트워킹")
                        5 -> serviceType.append("위치")
                        6 -> serviceType.append("전달")
                    }
                }
            }

            if(serviceType.isEmpty()) serviceType.append("기타 작업")
            serviceType.append("를 위해 연결됨.")
            return serviceType.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecyclerBluetoothBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position], isConnected[position])
    }

    fun clear(){
        this.devices.clear()
        this.isConnected.clear()
        notifyDataSetChanged()
    }

    fun addDevice(device: BluetoothDevice, isConnected: Boolean){
        this.devices.add(device)
        this.isConnected.add(isConnected)
        notifyItemInserted(devices.size - 1)
    }

    fun changeConnectionState(device: BluetoothDevice, isConnected: Boolean){
        this.isConnected[devices.indexOf(device)] = isConnected
        notifyItemChanged(devices.indexOf(device))
    }
}