package com.sd.lib.eventbus

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

object FEventBus {
    private val _observerHolder: MutableMap<Class<*>, MutableMap<FEventObserver<*>, String>> = HashMap()

    var isDebug = false

    @JvmStatic
    val default = FEventBus

    @Synchronized
    fun register(observer: FEventObserver<*>) {
        val clazz = observer.eventClass
        val holder = _observerHolder[clazz] ?: ConcurrentHashMap<FEventObserver<*>, String>().also {
            _observerHolder[clazz] = it
        }

        val put = holder.put(observer, "")
        if (put == null) {
            logMsg { "+++++ ${clazz.name} (${holder.size}) ($observer) eventTypeSize:${_observerHolder.size}" }
        }
    }

    @Synchronized
    fun unregister(observer: FEventObserver<*>) {
        val clazz = observer.eventClass
        val holder = _observerHolder[clazz] ?: return

        val remove = holder.remove(observer)
        if (remove != null) {
            if (holder.isEmpty()) _observerHolder.remove(clazz)
            logMsg { "----- ${clazz.name} (${holder.size}) ($observer) eventTypeSize:${_observerHolder.size}" }
        }
    }

    @Synchronized
    fun post(event: Any) {
        val clazz = event.javaClass
        val holder = _observerHolder[clazz] ?: return

        if (holder.isEmpty()) {
            _observerHolder.remove(clazz)
            return
        }

        logMsg { "post -----> $event (${holder.size})" }

        for (item in holder.keys) {
            notifyObserver(item as FEventObserver<Any>, event)
        }
    }

    private fun notifyObserver(observer: FEventObserver<Any>, event: Any) {
        runOnUiThread {
            logMsg { "notify $event $observer" }
            observer.onEvent(event)
        }
    }
}

private inline fun logMsg(block: () -> String) {
    if (FEventBus.isDebug) {
        Log.i("FEventBus", block())
    }
}

private fun runOnUiThread(runnable: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        runnable.run()
    } else {
        Handler(Looper.getMainLooper()).post { runnable.run() }
    }
}