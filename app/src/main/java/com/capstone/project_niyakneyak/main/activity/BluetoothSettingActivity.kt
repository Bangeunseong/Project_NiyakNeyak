package com.capstone.project_niyakneyak.main.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothClass.Device
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.databinding.ActivityBluetoothSettingBinding
import com.capstone.project_niyakneyak.main.adapter.BluetoothRegisteredAdapter

class BluetoothSettingActivity: AppCompatActivity() {
    // View Binding
    private var _binding: ActivityBluetoothSettingBinding? = null
    private val binding get() = _binding!!

    // Params for bluetooth connection
    private val bluetoothManager: BluetoothManager by lazy { getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val stateChangedReceiver: BroadcastReceiver by lazy {
        object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent?.action){
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        when(bluetoothAdapter?.state){
                            STATE_ON -> {
                                binding.bluetoothEnableBtn.text = "사용 중"
                                binding.bluetoothEnableBtn.isEnabled = false
                                binding.bluetoothEnableBtn.invalidate()
                                getPairedDevices()
                                binding.bluetoothMainLayout.visibility = View.VISIBLE
                            }
                            BluetoothAdapter.STATE_OFF -> {
                                binding.bluetoothEnableBtn.text = "사용 안함"
                                binding.bluetoothEnableBtn.isEnabled = true
                                binding.bluetoothEnableBtn.invalidate()
                                binding.bluetoothMainLayout.visibility = View.INVISIBLE
                            }
                            BluetoothAdapter.STATE_TURNING_OFF -> {

                            }
                            BluetoothAdapter.STATE_TURNING_ON -> {

                            }
                        }
                    }
                }
            }
        }
    }
    private val deviceConnectionReceiver: BroadcastReceiver by lazy{
        object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                TODO("Not yet implemented")
            }
        }
    }

    private var connectedDevice: Device? = null

    private val bluetoothActiveLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            Toast.makeText(this, "블루투스가 활성화 되었습니다!", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "블루투스가 활성화 되지 않았습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    // Adapters
    private var registeredAdapter: BluetoothRegisteredAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBluetoothSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar6.setTitle("블루투스")
        setSupportActionBar(binding.toolbar6)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(bluetoothAdapter == null){
            setResult(RESULT_CANCELED)
            finish()
        }

        registeredAdapter = BluetoothRegisteredAdapter(mutableListOf(), mutableListOf())

        if(bluetoothAdapter?.isEnabled == true){
            binding.bluetoothEnableBtn.text = "사용 중"
            binding.bluetoothEnableBtn.isEnabled = false
            getPairedDevices()
            binding.bluetoothMainLayout.visibility = View.VISIBLE
        } else{
            binding.bluetoothEnableBtn.text = "사용 안함"
            binding.bluetoothEnableBtn.isEnabled = true
            binding.bluetoothMainLayout.visibility = View.INVISIBLE
        }

        binding.bluetoothRegisteredDeviceView.setHasFixedSize(false)
        binding.bluetoothRegisteredDeviceView.layoutManager = LinearLayoutManager(this)
        binding.bluetoothRegisteredDeviceView.adapter = registeredAdapter
        binding.bluetoothEnableBtn.setOnClickListener {
            setActive()
        }

        registerReceiver(stateChangedReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun setActive(){
        bluetoothAdapter?.let {
            if(!it.isEnabled){
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothActiveLauncher.launch(intent)
            } else{
                Toast.makeText(this, "이미 활성화 되어 있습니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getPairedDevices(){
        bluetoothAdapter?.let {
            if(it.isEnabled){
                val pairedDevices: Set<BluetoothDevice> = it.bondedDevices
                if(pairedDevices.isNotEmpty()){
                    pairedDevices.forEach { device ->
                        Log.w("Bluetooth", "${device.name}, ${device.address}")
                        registeredAdapter?.addDevice(device, false)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                if(connectedDevice == null) setResult(RESULT_CANCELED)
                else setResult(RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(stateChangedReceiver)
    }
}