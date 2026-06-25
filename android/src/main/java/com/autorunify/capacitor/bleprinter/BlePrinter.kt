package com.autorunify.capacitor.bleprinter

import android.graphics.Bitmap

abstract class BlePrinter {
    protected val scaner: BleManager

    constructor(scanner: BleManager) {
        this.scaner = scanner
    }

    protected fun px2mm(px: Double, dpi: Int): Double {
        return (px * 25.4) / dpi
    }

    protected fun mm2px(mm: Double, dpi: Int): Double {
        return (mm * dpi) / 25.4
    }

    abstract fun init()
    abstract fun kill()

    abstract suspend fun devices(max: Int, timeout: Int): MutableList<BleDevice>

    abstract fun connect(address: String)
    abstract fun disconnect()

    abstract fun printImage(bitmap: Bitmap)
}