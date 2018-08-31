package com.sd.event;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.sd.lib.eventbus.FEventObserver;

public class TestDialog extends Dialog
{
    public static final String TAG = TestDialog.class.getSimpleName();

    public TestDialog(Context context)
    {
        super(context);
        setContentView(R.layout.dialog_test);
    }

    private FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            // 在主线程回调
            Log.i(TAG, "onEvent dialog:" + event);
        }
    }.setLifecycle(this);
}
