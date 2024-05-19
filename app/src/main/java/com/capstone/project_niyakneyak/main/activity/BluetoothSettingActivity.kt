package com.capstone.project_niyakneyak.main.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class BluetoothSettingActivity: AppCompatActivity() {
    private val bluetoothManager: BluetoothManager by lazy { getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }

    private val bluetoothActiveLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(bluetoothAdapter == null){
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    fun setActive(){
        bluetoothAdapter?.let {
            if(!it.isEnabled){
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothActiveLauncher.launch(intent)
            } else{
                Toast.makeText(this, "이미 활성화 되어 있습니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}