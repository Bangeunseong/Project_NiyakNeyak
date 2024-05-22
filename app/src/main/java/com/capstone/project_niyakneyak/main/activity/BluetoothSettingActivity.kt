package com.capstone.project_niyakneyak.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_CONNECTED
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothClass.Device
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityBluetoothSettingBinding
import com.capstone.project_niyakneyak.main.adapter.BluetoothDeviceListAdapter
import com.capstone.project_niyakneyak.main.listener.OnBTConnChangedListener
import java.io.IOException
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.util.UUID

class BluetoothSettingActivity: AppCompatActivity(), OnBTConnChangedListener {
    // View Binding
    private var _binding: ActivityBluetoothSettingBinding? = null
    private val binding get() = _binding!!

    // Params for bluetooth connection
    private var isSearching = MutableLiveData(false)
    private val bluetoothManager: BluetoothManager by lazy { getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val broadcastReceiver: BroadcastReceiver by lazy {
        object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent?.action){
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        Log.w("Bluetooth", "State Changed")
                        when(bluetoothAdapter?.state){
                            STATE_ON -> {
                                binding.bluetoothEnableBtn.text = "사용 중"
                                binding.bluetoothEnableBtn.isEnabled = false
                                binding.bluetoothEnableBtn.invalidate()
                                getPairedDevices()
                                findDevice()
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
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        isSearching.value = true
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        isSearching.value = false
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        // BluetoothDevice 객체 획득
                        val device =
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            }else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        if(device != null){
                           connectableAdapter?.addDevice(device, false)
                        }
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        val device =
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            }else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        if(device != null){
                            registeredAdapter?.changeConnectionState(device, true)
                        }
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        val device =
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            }else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        if(device != null){
                            registeredAdapter?.changeConnectionState(device, false)
                        }
                    }
                }
            }
        }
    }

    private val bluetoothActiveLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            Toast.makeText(this, "블루투스가 활성화 되었습니다!", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "블루투스가 활성화 되지 않았습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice): Thread(){
        private val mSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.randomUUID())
        }

        public override fun run() {
            bluetoothAdapter?.cancelDiscovery()

            mSocket?.let { socket ->
                socket.connect()

            }
        }

        fun cancel(): Boolean{
            return try {
                mSocket?.close()
                true
            } catch (e: IOException){
                Log.w("Bluetooth","Couldn't close the client socket: $e")
                false
            }
        }
    }

    // Adapters
    private var registeredAdapter: BluetoothDeviceListAdapter? = null
    private var connectableAdapter: BluetoothDeviceListAdapter? = null

    // Intent Filters
    private val intentFilter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBluetoothSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar6.setTitle("블루투스")
        binding.toolbar6.setTitleTextAppearance(this, R.style.ToolbarTextAppearance)
        setSupportActionBar(binding.toolbar6)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar6.navigationIcon?.mutate().let {
            it?.setTint(Color.WHITE)
            binding.toolbar6.navigationIcon = it
        }

        if(bluetoothAdapter == null){
            setResult(RESULT_CANCELED)
            finish()
        }

        registeredAdapter = BluetoothDeviceListAdapter(mutableListOf(), mutableListOf(), this)
        connectableAdapter = BluetoothDeviceListAdapter(mutableListOf(), mutableListOf(), this)
        isSearching.observe(this){
            invalidateMenu()
        }

        if(bluetoothAdapter?.isEnabled == true){
            binding.bluetoothEnableBtn.text = "사용 중"
            binding.bluetoothEnableBtn.isEnabled = false
            getPairedDevices()
            findDevice()
            binding.bluetoothMainLayout.visibility = View.VISIBLE
        } else{
            binding.bluetoothEnableBtn.text = "사용 안함"
            binding.bluetoothEnableBtn.isEnabled = true
            binding.bluetoothMainLayout.visibility = View.INVISIBLE
        }

        binding.bluetoothRegisteredDeviceView.setHasFixedSize(false)
        binding.bluetoothRegisteredDeviceView.layoutManager = LinearLayoutManager(this)
        binding.bluetoothRegisteredDeviceView.adapter = registeredAdapter

        binding.bluetoothConnectableDeviceView.setHasFixedSize(false)
        binding.bluetoothConnectableDeviceView.layoutManager = LinearLayoutManager(this)
        binding.bluetoothConnectableDeviceView.adapter = connectableAdapter

        binding.bluetoothEnableBtn.setOnClickListener {
            setActive()
        }

        // Additional Options for searchFilter(IntentFilter)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)

        // RegisterReceiver
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = mutableListOf<String>()
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PERMISSION_DENIED)
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED)
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED)
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if(permissions.isNotEmpty())
                requestPermissions(permissions.toTypedArray(),101)
        }
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
    private fun getPairedDevices() {
        bluetoothAdapter?.let {
            if(it.isEnabled){
                registeredAdapter?.clear()
                val pairedDevices: Set<BluetoothDevice> = it.bondedDevices
                if(pairedDevices.isNotEmpty()){

                    pairedDevices.forEach { device ->
                        Log.w("Bluetooth", "${device.name}, ${device.address}")
                        var connected = false
                        try{
                            val m: Method = device.javaClass.getMethod("isConnected")
                            connected = m.invoke(device) as Boolean
                        } catch (e: IllegalStateException){
                            Log.w("Bluetooth", "Error Occurred!: $e")
                        }
                        registeredAdapter?.addDevice(device, connected)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun findDevice() {
        bluetoothAdapter?.let {
            if (it.isEnabled) {
                if (it.isDiscovering) {
                    it.cancelDiscovery()
                    return
                }
                connectableAdapter?.clear()
                it.startDiscovery()
            } else{
                Toast.makeText(this, "먼저 블루투스 기능을 활성화 해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bluetooth, menu)
        menu!!.findItem(R.id.bluetooth_find_device).title = if(isSearching.value == true) "중지" else "찾기"
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            R.id.bluetooth_find_device -> {
                findDevice()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()

        if(bluetoothAdapter?.isDiscovering == true) bluetoothAdapter?.cancelDiscovery()
        unregisterReceiver(broadcastReceiver)
    }

    override fun requestConnection(device: BluetoothDevice) {
        ConnectThread(device).start()
    }

    override fun requestDisconnection(device: BluetoothDevice) {
        ConnectThread(device).cancel()
    }
}