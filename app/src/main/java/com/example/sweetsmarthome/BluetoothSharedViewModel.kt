package com.example.sweetsmarthome

import androidx.lifecycle.ViewModel
import com.example.bt_def.bluetooth.BluetoothController

class BluetoothSharedViewModel : ViewModel() {
    var bluetoothController: BluetoothController? = null
}