package com.sd.lib.eventbus

import android.os.Handler
import android.os.Looper

internal object Utils {
    private val MainHandler = Handler(Looper.getMainLooper())

    @JvmStatic
    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            MainHandler.post { runnable.run() }
        }
    }
}