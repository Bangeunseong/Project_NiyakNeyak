package com.capstone.project_niyakneyak.util.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.ParcelUuid
import android.util.Log
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectThread(device: BluetoothDevice): Thread(){
    // Params for Firebase
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    // Params for Bluetooth Connection
    private val device: BluetoothDevice? by lazy { device }
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private val mSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val uuids = device.uuids
        if(uuids.contains(ParcelUuid.fromString(BluetoothUuids.A2DP_UUID))){
            device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BluetoothUuids.A2DP_UUID))
        } else if(uuids.contains(ParcelUuid.fromString(BluetoothUuids.HFP_UUID))) {
            device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BluetoothUuids.HFP_UUID))
        } else if(uuids.contains(ParcelUuid.fromString(BluetoothUuids.AVRCP_UUID))) {
            device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BluetoothUuids.AVRCP_UUID))
        } else if(uuids.contains(ParcelUuid.fromString(BluetoothUuids.SPP_UUID))){
            device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BluetoothUuids.SPP_UUID))
        } else if(uuids.contains(ParcelUuid.fromString(BluetoothUuids.PRIVATE_UUID))){
            device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BluetoothUuids.PRIVATE_UUID))
        } else {
            null
        }
    }

    init {
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
    }

    override fun run() {
        mSocket?.let { socket ->
            try{
                socket.connect()
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        if(it.exists())
                            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid).update("address", device?.address)
                    }
            } catch (e: IOException){
                Log.w("BluetoothSocket", "Error Occurred!: $e")
            }
        }
    }

    fun read(): String?{
        val buffer = ByteArray(1024)
        var bytes: Int
        mSocket?.let { socket ->
            try {
                if(inputStream == null) inputStream = socket.inputStream
                while(true){
                    try {
                        // 데이터 받기(읽기)
                        bytes = inputStream!!.read(buffer)
                        return bytes.toString()
                    } catch (e: Exception) { // 기기와의 연결이 끊기면 호출
                        Log.w("InputStream", "Error Occurred!: $e")
                        break
                    }
                }
            } catch (e: IOException){
                Log.w("BluetoothSocket", "Error Occurred!: $e")
                null
            }
        }
        return null
    }

    fun write(bytes: ByteArray): Boolean{
        mSocket?.let { socket ->
            try {
                if(outputStream == null) outputStream = socket.outputStream
                try {
                    // 데이터 전송
                    outputStream!!.write(bytes)
                    return true
                } catch (e: IOException) {
                    Log.w("OutputStream", "Error Occurred!: $e")
                }
            } catch (e: IOException){
                Log.w("BluetoothSocket", "Error Occurred!: $e")
            }
        }
        return false
    }

    fun disconnectSocket(): Boolean{
        return try {
            if(inputStream != null) inputStream?.close()
            if(outputStream != null) outputStream?.close()
            mSocket?.close()
            true
        } catch (e: IOException){
            Log.w("Bluetooth", "Error Occurred!: $e")
            false
        }
    }
}