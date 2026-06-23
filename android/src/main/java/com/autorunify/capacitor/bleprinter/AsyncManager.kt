package com.autorunify.capacitor.bleprinter

import com.getcapacitor.JSObject
import com.getcapacitor.PluginCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class AsyncCall {
    protected val call: PluginCall
    var done: Boolean
        protected set

    constructor(call: PluginCall) {
        this.done = false
        this.call = call
    }

    abstract fun match(code: Int, address: Int): AsyncCall?

    open fun trigger(data: JSObject?, message: String?) {
        if (data != null) this.resolve(data)
        if (message != null) this.reject(message)
    }

    open fun timeout(seconds: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val milliseconds = (seconds * 1000)
            delay(milliseconds.toLong())
            if (!done) {
                val reply = JSObject()
                reply.put("method", call.methodName)
                reply.put("data", JSObject().apply {
                    call.data.keys().forEach { key ->
                        put(key, call.data.get(key))
                    }
                })
                call.reject("Operation timed out", reply)
                done = true
            }
        }
    }

    private fun resolve(result: JSObject) {
        if (done) return

        call.resolve(result)
        done = true
    }

    private fun reject(message: String) {
        if (done) return

        call.reject(message)
        done = true
    }
}

class SystemAsyncCall : AsyncCall {
    companion object {
        const val PRINTER_STATE_CONNECTED = 0x08000000;
        const val PRINTER_STATE_DISCONNECTED = 0x08000001;
        const val PRINTER_PRINT_FINISHED = 0x08000002;
    }

    private val code: Int
    private val address: Int

    constructor(call: PluginCall, code: Int, address: Int) : super(call) {
        this.code = code
        this.address = address
    }

    override fun match(code: Int, address: Int): AsyncCall? {
        if (this.code == code && this.address == address) return this

        return null
    }
}

class AsyncManager {
    private val calls: MutableList<AsyncCall> = mutableListOf<AsyncCall>()

    private fun removeDoneCalls() {
        val doneCalls = this.calls.filter {
            it.done
        }

        doneCalls.forEach {
            calls.remove(it)
        }
    }


    fun on(call: PluginCall, code: Int, address: Int = 0): AsyncCall {
        removeDoneCalls()
        val async = SystemAsyncCall(call, code, address)
        calls.add(async)
        return async
    }

    fun emit(data: JSObject, code: Int, address: Int = 0) {
        removeDoneCalls()
        calls.forEach { call ->
            call.match(code, address)?.trigger(data, null)
        }
        removeDoneCalls()
    }

    fun error(message: String, code: Int, address: Int) {
        removeDoneCalls()
        calls.forEach { call ->
            call.match(code, address)?.trigger(null, message)
        }
        removeDoneCalls()
    }
}