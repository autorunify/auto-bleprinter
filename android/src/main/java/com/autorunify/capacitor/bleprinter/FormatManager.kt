package com.autorunify.capacitor.bleprinter

import com.getcapacitor.JSObject

class FormatManager {
    fun toJSON(device: BleDevice): JSObject {
        return JSObject().apply {
            put("address", device.address)
            put("name", device.name)
            put("rssi", device.rssi)
        }
    }
}