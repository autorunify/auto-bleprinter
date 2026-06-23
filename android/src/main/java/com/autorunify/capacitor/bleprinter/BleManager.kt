package com.autorunify.capacitor.bleprinter

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import com.autorunify.capacitor.bleprinter.SystemAsyncCall.Companion.PRINTER_STATE_CONNECTED
import com.autorunify.capacitor.bleprinter.SystemAsyncCall.Companion.PRINTER_STATE_DISCONNECTED
import com.getcapacitor.JSObject
import com.getcapacitor.PluginCall
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings


class BleManager {
    private class StateReceiver : BroadcastReceiver {
        private val ble: BleManager

        constructor(bleManager: BleManager) {
            this.ble = bleManager
        }

        override fun onReceive(context: Context, intent: Intent) {
            val eventName = "state"
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    ble.notify.notifyListeners(eventName, JSObject().apply {
                        ble.isConnected.postValue(false)
                        put("action", "enabled")
                        put("state", true)
                    })
                }

                BluetoothAdapter.STATE_OFF -> {
                    ble.notify.notifyListeners(eventName, JSObject().apply {
                        ble.isConnected.postValue(false)
                        put("action", "enabled")
                        put("state", false)
                    })
                }
            }

            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    ble.isConnected.postValue(true)
                    ble.notify.notifyListeners(eventName, JSObject().apply {
                        put("action", "connected")
                        put("state", true)
                    })

                    ble.async.emit(JSObject(), PRINTER_STATE_CONNECTED)
                }

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    ble.isConnected.postValue(false)
                    ble.notify.notifyListeners(eventName, JSObject().apply {
                        put("action", "connected")
                        put("state", false)
                    })
                    ble.async.emit(JSObject(), PRINTER_STATE_DISCONNECTED)
                }
            }
        }
    }

    private class ScanReceiver : ScanCallback {
        private val ble: BleManager

        constructor(bleManager: BleManager) {
            this.ble = bleManager
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = BleDevice(result)
            synchronized(ble.devices) {
                if (!ble.devices.contains(device)) {
                    ble.devices.add(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            ble.scan(false)
        }
    }

    val notify: BlePrinterPlugin
    val async: AsyncManager

    private lateinit var context: Context
    private lateinit var adapter: BluetoothAdapter
    private lateinit var stateReceiver: StateReceiver
    private lateinit var scanReceiver: ScanReceiver

    private var isSupported: Boolean = false
    private var isScanning: Boolean = false

    var filters: MutableList<ScanFilter> = mutableListOf()
    val devices: MutableList<BleDevice> = mutableListOf()
    val isConnected = MutableLiveData<Boolean>(false)

    constructor(notify: BlePrinterPlugin, asyncManager: AsyncManager) {
        this.notify = notify
        this.async = asyncManager
    }

    fun load(activity: Activity, context: Context) {
        this.isSupported =
            activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        if (!this.isSupported) return

        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        this.adapter = manager.adapter
        this.context = context

        this.stateReceiver = StateReceiver(this)
        this.scanReceiver = ScanReceiver(this)
    }

    fun init() {
        isConnected.postValue(false)
        try {
            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            context.registerReceiver(this.stateReceiver, filter)
        } catch (ex: Exception) {
        }
    }

    fun kill() {
        isConnected.postValue(false)
        try {
            context.unregisterReceiver(this.stateReceiver)
        } catch (ex: Exception) {
        }
    }

    fun assertFeature(call: PluginCall): Boolean {
        if (!isSupported) {
            call.reject("BLE is not supported.")
            return false
        }

        return true
    }

    fun assertAdapter(call: PluginCall): Boolean {
        if (adapter == null) {
            call.reject("BLE is not available.")
            return false
        }

        return true
    }

    fun assertEnabled(call: PluginCall): Boolean {
        if (adapter != null && adapter.isEnabled) {
            return true
        }

        call.reject("BLE is not enabled")
        return false
    }

    fun assertConnected(call: PluginCall): Boolean {
        if (isConnected.value == true) return true

        call.reject("BLE is not connected")
        return false
    }

    fun isEnabled(): Boolean {
        if (adapter != null) return adapter.isEnabled
        return false
    }

    fun scan(startScan: Boolean) {
        if (startScan) {
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setUseHardwareFilteringIfSupported(false)
                .setReportDelay(0)
                .build()

            synchronized(this) {
                if (isScanning) return
                isScanning = true

                val scanner = BluetoothLeScannerCompat.getScanner()
                scanner.startScan(filters, settings, scanReceiver)
            }
        } else {
            synchronized(this) {
                if (!isScanning) return
                isScanning = false

                val scanner = BluetoothLeScannerCompat.getScanner()
                scanner.stopScan(scanReceiver)
            }
        }
    }
}