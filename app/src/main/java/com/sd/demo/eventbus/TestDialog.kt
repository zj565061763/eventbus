package com.sd.demo.eventbus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sd.lib.eventbus.FEventBus;
import com.sd.lib.eventbus.FEventObserver;

public class TestDialog extends Dialog {
    public static final String TAG = TestDialog.class.getSimpleName();

    public TestDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_test);
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送事件
                FEventBus.getDefault().post(new TestEvent());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventObserver.bindDialog(this);
    }

    private final FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>() {
        @Override
        public void onEvent(TestEvent event) {
            Log.i(TAG, "onEvent dialog:" + event);
        }
    };
}
