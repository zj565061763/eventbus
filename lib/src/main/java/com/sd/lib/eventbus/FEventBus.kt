package com.sd.lib.eventbus

import android.util.Log
import com.sd.lib.eventbus.Utils.runOnUiThread
import java.util.concurrent.ConcurrentHashMap

object FEventBus {
    private val _observerHolder: MutableMap<Class<*>, MutableMap<FEventObserver<*>, String>> = HashMap()

    var isDebug = false

    @JvmStatic
    val default = FEventBus

    /**
     * 注册观察者
     */
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

    /**
     * 取消注册观察者
     */
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
    /**
     * 发送事件
     */
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

    /**
     * 通知观察者
     */
    private fun notifyObserver(observer: FEventObserver<Any>, event: Any) {
        runOnUiThread {
            if (isDebug) {
                Log.i(
                    FEventBus::class.java.simpleName, "notifyObserver"
                            + " event:" + event
                            + " observer:" + observer
                )
            }
            observer.onEvent(event)
        }
    }
}

internal inline fun logMsg(block: () -> String) {
    if (FEventBus.isDebug) {
        Log.i("FEventBus", block())
    }
}