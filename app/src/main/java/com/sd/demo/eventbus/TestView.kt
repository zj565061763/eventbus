package com.sd.demo.eventbus

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.sd.lib.eventbus.FEventObserver

class TestView(
    context: Context,
    attr: AttributeSet,
) : FrameLayout(context, attr) {

    private val _eventObserver = object : FEventObserver<TestEvent>() {
        override fun onEvent(event: TestEvent) {
            logMsg { "onEvent view $event" }
        }
    }

    init {
        _eventObserver.bindView(this)
    }
}