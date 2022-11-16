package com.sd.demo.eventbus

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.eventbus.FEventBus
import com.sd.lib.eventbus.FEventObserver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_post).setOnClickListener {
            FEventBus.post(TestEvent())
        }

        _eventObserver.bindActivity(this)
        TestDialog(this).show()
    }

    private val _eventObserver = object : FEventObserver<TestEvent>() {
        override fun onEvent(event: TestEvent) {
            logMsg { "onEvent activity $event" }
        }
    }

    companion object {
        init {
            FEventBus.isDebug = true
        }
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("FEventBus-demo", block())
}