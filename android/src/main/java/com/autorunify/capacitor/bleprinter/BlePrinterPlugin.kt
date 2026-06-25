package com.autorunify.capacitor.bleprinter

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import com.autorunify.capacitor.bleprinter.SystemAsyncCall.Companion.PRINTER_PRINT_FINISHED
import com.autorunify.capacitor.bleprinter.SystemAsyncCall.Companion.PRINTER_STATE_CONNECTED
import com.autorunify.capacitor.bleprinter.SystemAsyncCall.Companion.PRINTER_STATE_DISCONNECTED
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.PermissionState
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.getcapacitor.annotation.PermissionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@CapacitorPlugin(
    name = "BlePrinter",
    permissions = [
        Permission(
            strings = [
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
            ], alias = "ANDROID_R"
        ),
        Permission(
            strings = [
                "android.permission.BLUETOOTH_SCAN",
                "android.permission.BLUETOOTH_CONNECT",
            ], alias = "ANDROID_S"
        )]
)
class BlePrinterPlugin : Plugin {
    private var alias: String = "ANDROID_R"
    private val defaultTimeout = 20
    private var printer: BlePrinter? = null
    private val formatter = FormatManager()
    private val async = AsyncManager()
    private val ble = BleManager(this, async)


    constructor() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.R) {
            this.alias = "ANDROID_S"
        }
    }

    @PermissionCallback
    @PluginMethod
    override fun checkPermissions(call: PluginCall) {

        val permissions = super.handle.pluginAnnotation.permissions

        call.resolve(JSObject().apply {
            permissions.forEach { permission ->
                if (permission.alias == alias) {
                    permission.strings.forEach { key ->
                        val status = ActivityCompat.checkSelfPermission(context, key)
                        val state = if (status == PackageManager.PERMISSION_DENIED) {
                            PermissionState.DENIED
                        } else {
                            PermissionState.GRANTED
                        }
                        put(key, state)
                    }
                }
            }
        })
    }

    @PluginMethod
    override fun requestPermissions(call: PluginCall) {
        super.requestPermissionForAlias(this.alias, call, "checkPermissions")
    }

    @PluginMethod
    fun isBluetoothEnabled(call: PluginCall) {
        call.resolve(JSObject().apply {
            put("enabled", ble.isEnabled())
        })
    }

    public override fun notifyListeners(eventName: String?, data: JSObject?) {
        super.notifyListeners(eventName, data)
    }

    override fun load() {
        super.load()
        ble.load(activity, context)
    }

    fun assertPrinter(call: PluginCall): Boolean {
        if (printer == null) {
            call.reject("printer is not init.")
            return false
        }

        return true
    }

    @PluginMethod
    fun init(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return

        if (printer != null) return call.resolve()

        val manufacturer = call.getString("manufacturer")
            ?: return call.reject("manufacturer is required")

        try {
            when (manufacturer) {
                "DotHanTech" -> printer = DotHanTechPrinter(ble)
                else -> return call.reject("manufacturer is not supported $manufacturer")
            }

            ble.init()
            printer!!.init()

            call.resolve()
        } catch (ex: Exception) {
            call.reject(ex.message)
        }
    }

    @PluginMethod
    fun kill(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return

        if (printer == null) return call.resolve()

        ble.kill()
        printer!!.kill()
        printer = null
    }

    @PluginMethod
    fun devices(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return
        if (!this.assertPrinter(call)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timeout = call.getInt("timeout", 5)!!
                val max = call.getInt("max", 0)!!
                val devices = printer!!.devices(
                    max,
                    if (timeout <= 0) 1000 else timeout * 1000
                )

                call.resolve(JSObject().apply {
                    put("devices", JSArray().apply {
                        devices.forEach { device ->
                            put(formatter.toJSON(device))
                        }
                    })
                })
            } catch (ex: Exception) {
                call.reject(ex.message)
            }
        }
    }

    @PluginMethod
    fun isConnected(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return
        if (!this.assertPrinter(call)) return

        call.resolve(JSObject().apply {
            put("connected", ble.isConnected.value)
        })
    }

    @PluginMethod
    fun connect(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return
        if (!this.assertPrinter(call)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timeout = call.getInt("timeout", defaultTimeout)
                val address = call.getString("address")
                    ?: return@launch call.reject("address is required")


                async.on(call, PRINTER_STATE_CONNECTED).timeout(timeout!!)
                printer?.connect(address)
            } catch (ex: Exception) {
                call.reject(ex.message)
            }
        }
    }

    @PluginMethod
    fun disconnect(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return
        if (!this.assertPrinter(call)) return
        if (!this.ble.assertConnected(call)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timeout = call.getInt("timeout", defaultTimeout)

                async.on(call, PRINTER_STATE_DISCONNECTED).timeout(timeout!!)
                printer?.disconnect()
            } catch (ex: Exception) {
                call.reject(ex.message)
            }
        }
    }

    @PluginMethod
    fun printImage(call: PluginCall) {
        if (!this.ble.assertFeature(call)) return
        if (!this.ble.assertAdapter(call)) return
        if (!this.ble.assertEnabled(call)) return
        if (!this.assertPrinter(call)) return
        if (!this.ble.assertConnected(call)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val width = call.getInt("width")
                    ?: return@launch call.reject("width is required")
                val height = call.getInt("height")
                    ?: return@launch call.reject("height is required")
                val imageData = call.getObject("imageData")
                    ?: return@launch call.reject("imageData is required")

                val colors = IntArray(width * height)
                var colorConfig = Bitmap.Config.ARGB_8888

                val colorsType = imageData.length() / (width * height)
                if (colorsType != 4) return@launch call.reject("imageData type not Uint8ClampedArray")

                for (i in 0..<colors.size) {
                    val R = imageData.getInt((i * 4 + 0).toString())
                    val G = imageData.getInt((i * 4 + 1).toString())
                    val B = imageData.getInt((i * 4 + 2).toString())
                    val A = imageData.getInt((i * 4 + 3).toString())

                    var color = 0
                    color += ((A and 0xff) shl 24)
                    color += ((G and 0xff) shl 16)
                    color += ((B and 0xff) shl 8)
                    color += ((R and 0xff) shl 0)

                    colors[i] = color
                }

                async.on(call, PRINTER_PRINT_FINISHED)
                printer!!.printImage(
                    Bitmap.createBitmap(colors, width, height, colorConfig),
                    width,
                    height
                )
            } catch (ex: Exception) {
                call.reject(ex.message)
            }
        }
    }
}