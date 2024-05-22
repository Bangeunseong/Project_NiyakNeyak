package com.capstone.project_niyakneyak.main.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ItemRecyclerBluetoothBinding
import com.capstone.project_niyakneyak.main.listener.OnBTConnChangedListener
import java.lang.StringBuilder

class BluetoothDeviceListAdapter(private val devices: MutableList<BluetoothDevice>, private val isConnected: MutableList<Boolean>, private val onBTConnChangedListener: OnBTConnChangedListener): RecyclerView.Adapter<BluetoothDeviceListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecyclerBluetoothBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("MissingPermission")
        fun bind(device: BluetoothDevice, isConnected: Boolean, onBTConnChangedListener: OnBTConnChangedListener){
            binding.bluetoothDeviceName.text = device.name
            if(device.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO){
                if(device.bluetoothClass.deviceClass == BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX)
                    binding.bluetoothTypeIcon.setImageResource(R.drawable.icon_bluetooth_screen)
                else binding.bluetoothTypeIcon.setImageResource(R.drawable.icon_bluetooth_headset)
            }
            else binding.bluetoothTypeIcon.setImageResource(R.drawable.icon_bluetooth_screen)
            if(isConnected){
                binding.bluetoothDeviceConnType.visibility = View.VISIBLE
                binding.bluetoothDeviceConnType.text = serviceTypes(device)
            }
            else binding.bluetoothDeviceConnType.visibility = View.GONE
            binding.bluetoothDeviceSettingBtn.setImageResource(if(isConnected) R.drawable.icon_disconnect else R.drawable.icon_connect)
            binding.bluetoothDeviceSettingBtn.setOnClickListener {
                if(isConnected){
                    onBTConnChangedListener.requestDisconnection(device)
                } else{
                    onBTConnChangedListener.requestConnection(device)
                }
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
        holder.bind(devices[position], isConnected[position], onBTConnChangedListener)
    }

    fun clear(){
        this.devices.clear()
        this.isConnected.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("MissingPermission")
    fun addDevice(device: BluetoothDevice, isConnected: Boolean){
        if(!this.devices.contains(device) && device.name != null && device.address != null){
            this.devices.add(device)
            this.isConnected.add(isConnected)
            notifyItemInserted(devices.size - 1)
        }
    }

    fun changeConnectionState(device: BluetoothDevice, isConnected: Boolean){
        for(target in devices){
            Log.w("Bluetooth", "${target.address}, ${device.address}")
            if(target.address == device.address){
                this.isConnected[devices.indexOf(target)] = isConnected
                notifyItemChanged(devices.indexOf(target))
                break
            }
        }
    }
}