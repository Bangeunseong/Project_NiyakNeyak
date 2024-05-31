package com.capstone.project_niyakneyak.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_BONDED
import android.bluetooth.BluetoothDevice.BOND_BONDING
import android.bluetooth.BluetoothDevice.BOND_NONE
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
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
import com.capstone.project_niyakneyak.util.bluetooth.ConnectThread
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.lang.IllegalStateException
import java.lang.reflect.Method

class BluetoothSettingActivity: AppCompatActivity(), OnBTConnChangedListener {
    // View Binding
    private var _binding: ActivityBluetoothSettingBinding? = null
    private val binding get() = _binding!!

    // Params for device registration
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    // Params for bluetooth connection
    private var isSearching = MutableLiveData(false)
    private val bluetoothManager: BluetoothManager by lazy { getSystemService(BLUETOOTH_SERVICE) as BluetoothManager }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val broadcastReceiver: BroadcastReceiver by lazy {
        object: BroadcastReceiver(){
            @SuppressLint("MissingPermission")
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
                                bluetoothAdapter?.cancelDiscovery()
                                registeredAdapter?.clear()
                                connectableAdapter?.clear()
                            }
                            BluetoothAdapter.STATE_TURNING_ON -> {

                            }
                        }
                    }
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                        val device =
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            }else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        if(device != null){
                            when(device.bondState) {
                                BOND_BONDED -> {
                                    connectableAdapter?.removeDevice(device)
                                    registeredAdapter?.addDevice(device, false)
                                }
                                BOND_BONDING -> {
                                    connectedDevices[device] = ConnectThread(device)
                                }
                                BOND_NONE -> {
                                    connectableAdapter?.clear()
                                    registeredAdapter?.removeDevice(device)
                                }
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
                            if(!connectedDevices.containsKey(device))
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
                        if(device != null) {
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
                    BluetoothDevice.ACTION_UUID -> {
                        val uuids =
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID, ParcelUuid::class.java)
                            }else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID) as Array<out ParcelUuid>
                            }
                        if (uuids != null) {
                            for(uuid in uuids){
                                Log.w("Bluetooth","${uuid.uuid}")
                            }
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


    private val connectedDevices: MutableMap<BluetoothDevice, ConnectThread> = mutableMapOf()

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

        if(_firestore == null) _firestore = Firebase.firestore
        if(_firebaseAuth == null) _firebaseAuth = Firebase.auth

        registeredAdapter = BluetoothDeviceListAdapter(mutableListOf(), mutableListOf(), this)
        connectableAdapter = BluetoothDeviceListAdapter(mutableListOf(), mutableListOf(), this)
        isSearching.observe(this){
            invalidateMenu()
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
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_UUID)

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
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PERMISSION_DENIED)
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            if(permissions.isNotEmpty())
                requestPermissions(permissions.toTypedArray(),101).let {
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
                }
            else{
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
            }
        } else{
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
                        connectedDevices[device] = ConnectThread(device)
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

        for(device in connectedDevices.toList()){
            device.second.disconnectSocket()
        }
        if(bluetoothAdapter?.isDiscovering == true) bluetoothAdapter?.cancelDiscovery()
        unregisterReceiver(broadcastReceiver)
    }

    @SuppressLint("MissingPermission")
    override fun requestConnection(device: BluetoothDevice) {
        bluetoothAdapter?.cancelDiscovery()
        if(device.name.contains("NiyakNeyak")){
            if(!connectedDevices.containsKey(device))
                connectedDevices[device] = ConnectThread(device)
            connectedDevices[device]?.start()
        } else{
            Toast.makeText(this, "이 기기와 앱은 서로 호환되지 않습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun requestDisconnection(device: BluetoothDevice) {
        if(device.name.contains("NiyakNeyak")){
            connectedDevices[device]?.disconnectSocket()
            connectedDevices.remove(device)
        } else{
            Toast.makeText(this, "이 기기와 연결을 해제할 수 없습니다!", Toast.LENGTH_SHORT).show()
        }
    }
}