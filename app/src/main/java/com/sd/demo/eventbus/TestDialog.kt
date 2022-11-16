package com.sd.demo.eventbus

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.sd.lib.eventbus.FEventBus
import com.sd.lib.eventbus.FEventObserver

class TestDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_test)
        findViewById<View>(R.id.btn_post).setOnClickListener {
            FEventBus.post(TestEvent())
        }

        _eventObserver.bindDialog(this)
    }

    private val _eventObserver = object : FEventObserver<TestEvent>() {
        override fun onEvent(event: TestEvent) {
            logMsg { "onEvent dialog $event" }
        }
    }

    companion object {
        val TAG = TestDialog::class.java.simpleName
    }
}