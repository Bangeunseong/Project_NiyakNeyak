package com.capstone.project_niyakneyak.main.listener

import android.bluetooth.BluetoothDevice

interface OnBTConnChangedListener {
    fun requestConnection(device: BluetoothDevice)
    fun requestDisconnection(device: BluetoothDevice)
}