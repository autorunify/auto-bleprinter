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
        var char: Char = '0'
        var state = 0
        var readChar = sr.read()

        while (readChar != -1) {
            when (state) {
                0 -> {
                    char = readChar.toChar()
                    state = 1
                }

                1 -> {
                    if (readChar.toChar() == '!') {
                        lsb.clear()
                        state = 2
                    } else {
                        sb.append(char)
                        char = readChar.toChar()
                    }
                }

                2 -> {
                    if (readChar.toChar() == '!') {
                        sb.append(char.toString().repeat(lsb.toString().toInt(16)))
                        state = 0
                    } else {
                        lsb.append(readChar.toChar())
                    }
                }
            }

            readChar = sr.read()
        }

        return sb.toString()
    }
}