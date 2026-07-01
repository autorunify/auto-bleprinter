package com.autorunify.capacitor.bleprinter

import com.getcapacitor.JSObject
import java.io.StringReader

class FormatManager {
    fun toJSON(device: BleDevice): JSObject {
        return JSObject().apply {
            put("address", device.address)
            put("name", device.name)
            put("rssi", device.rssi)
        }
    }

    fun RLE(value: String): String {
        val sb = StringBuilder()
        val lsb = StringBuilder()
        val sr = StringReader(value)
        var nextChar: Int
        var fisrtChar = sr.read()

        while (fisrtChar != -1) {
            nextChar = sr.read()
            if (nextChar.toChar() == '!') {
                nextChar = sr.read()
                while (nextChar.toChar() != '!') {
                    lsb.append(nextChar.toChar())
                    nextChar = sr.read()
                }
                sb.append(fisrtChar.toChar().toString().repeat(lsb.toString().toInt(16)))
                lsb.clear()
                fisrtChar = sr.read()
            } else {
                sb.append(fisrtChar.toChar())
                fisrtChar = nextChar
            }
        }

        return sb.toString()
    }
}