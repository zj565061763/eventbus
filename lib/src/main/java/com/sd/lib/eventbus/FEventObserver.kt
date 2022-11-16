package com.sd.lib.eventbus

import android.app.Activity
import android.app.Dialog
import android.view.View
import java.lang.reflect.ParameterizedType

abstract class FEventObserver<T> {
    internal val eventClass: Class<*>

    init {
        val clazz = findTargetClass()
        val parameterizedType = clazz.genericSuperclass as ParameterizedType
        val types = parameterizedType.actualTypeArguments

        eventClass = if (types.isNotEmpty()) {
            types[0] as Class<*>
        } else {
            throw RuntimeException("Generic type not found.")
        }
    }

    private fun findTargetClass(): Class<*> {
        var clazz: Class<*> = javaClass
        while (true) {
            if (clazz.superclass == FEventObserver::class.java) break
            clazz = clazz.superclass
        }
        return clazz
    }

    fun register() {
        FEventBus.default.register(this)
    }

    fun unregister() {
        FEventBus.default.unregister(this)
    }

    fun bindActivity(activity: Activity?) {
        _lifecycleHolder.setActivity(activity)
    }

    fun bindDialog(dialog: Dialog?) {
        _lifecycleHolder.setDialog(dialog)
    }

    fun bindView(view: View?) {
        _lifecycleHolder.setView(view)
    }

    fun unbind() {
        _lifecycleHolder.setView(null)
    }

    private val _lifecycleHolder by lazy {
        object : LifecycleHolder() {
            override fun onLifecycleChanged(enable: Boolean) {
                if (enable) register() else unregister()
            }
        }
    }

    abstract fun onEvent(event: T)
}