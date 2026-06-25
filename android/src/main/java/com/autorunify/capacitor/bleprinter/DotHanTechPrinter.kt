package com.autorunify.capacitor.bleprinter

import android.graphics.Bitmap
import android.os.ParcelUuid
import android.util.Log
import com.autorunify.capacitor.bleprinter.SystemAsyncCall.Companion.PRINTER_PRINT_FINISHED
import com.dothantech.lpapi.LPAPI
import com.dothantech.printer.IDzPrinter
import com.getcapacitor.JSObject
import kotlinx.coroutines.delay
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import java.util.UUID

class DotHanTechPrinter : BlePrinter {
    private class DotHanTechReceiver : LPAPI.Callback {
        private val printer: DotHanTechPrinter

        constructor(blePrinter: DotHanTechPrinter) {
            this.printer = blePrinter
        }

        override fun onProgressInfo(p0: IDzPrinter.ProgressInfo?, p1: Any?) {
            Log.w("BlePrinter", "onProgressInfo: ${p0?.name}")
        }

        override fun onStateChange(p0: IDzPrinter.PrinterAddress?, p1: IDzPrinter.PrinterState?) {
            Log.w("BlePrinter", "onStateChange: ${p0?.shownName} ${p1?.name}")
            if (p1== IDzPrinter.PrinterState.Connected){
                this.printer.adapter.setPrintPageGapType(0)
            }
        }

        override fun onPrintProgress(
            p0: IDzPrinter.PrinterAddress?,
            p1: IDzPrinter.PrintData?,
            p2: IDzPrinter.PrintProgress?,
            p3: Any?
        ) {
            Log.w("BlePrinter", "onPrintProgress: ${p0?.shownName} ${p2?.name}")
            if (p2 == IDzPrinter.PrintProgress.Success) {
                printer.async.emit(JSObject().apply {
                    put("state", "success")
                }, PRINTER_PRINT_FINISHED)
            }

            if (p2 == IDzPrinter.PrintProgress.Failed) {
                printer.async.emit(JSObject().apply {
                    put("state", "failed")
                }, PRINTER_PRINT_FINISHED)
            }
        }

        override fun onPrinterDiscovery(p0: IDzPrinter.PrinterAddress?, p1: Any?) {
            Log.w("BlePrinter", "onPrinterDiscovery: ${p0?.shownName}")
        }
    }

    private val async: AsyncManager
    private val receiver: DotHanTechReceiver = DotHanTechReceiver(this)
    private lateinit var adapter: LPAPI
    private val DPI = 300

    constructor(scanner: BleManager) : super(scanner) {
        this.async = scanner.async
    }

    override fun init() {
        this.adapter = LPAPI.Factory.createInstance(receiver)
    }

    override fun kill() {
        this.adapter.closePrinter()
    }

    override suspend fun devices(max: Int, timeout: Int): MutableList<BleDevice> {
        this.scaner.scan(false)
        this.scaner.filters = mutableListOf<ScanFilter>()
        this.scaner.filters.add(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb")))
                .build()

        )

        this.scaner.devices.clear()
        this.scaner.scan(true);

        val timeMillis = 200L
        var timeCount = timeout.toLong() / timeMillis

        while (timeCount > 0) {
            delay(timeMillis)
            if (max > 0 && scaner.devices.size >= max) {
                break
            }

            timeCount--
        }

        this.scaner.scan(false)
        val _devices: MutableList<BleDevice> = mutableListOf()

        synchronized(scaner.devices) {
            scaner.devices.forEach { device ->
                _devices.add(device)
            }
        }

        return _devices
    }

    override fun connect(address: String) {
        val _device = scaner.devices.firstOrNull { device -> device.address == address }
        if (_device == null) return

        this.adapter.openPrinter(_device.device)
//        val address = IDzPrinter.PrinterAddress(_device.address, IDzPrinter.AddressType.BLE)
//        this.adapter.openPrinterByAddressSync(address)
//        this.adapter.setPrintPageGapType(0)
    }

    override fun disconnect() {
        if (this.adapter.isPrinterOpened) {
            this.adapter.closePrinter()
        }
    }

    override fun printImage(bitmap: Bitmap) {
        val widthMm = px2mm(bitmap.width.toDouble(), DPI)
        val heightMm = px2mm(bitmap.height.toDouble(), DPI) + 10

        if (!adapter.startJob(widthMm, heightMm, 0)) return
        if (!adapter.drawBitmapWithThreshold(bitmap, 0.0, 0.0, 0.0, 0.0, 256)) return
        adapter.commitJob()
    }
}