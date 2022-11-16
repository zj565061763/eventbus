package com.sd.demo.eventbus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.eventbus.FEventBus;
import com.sd.lib.eventbus.FEventObserver;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    static {
        // 设置调试模式，输入日志
        FEventBus.getDefault().setDebug(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送事件
                FEventBus.getDefault().post(new TestEvent());
            }
        });

        // 绑定生命周期对象(会自动注册和取消注册观察者)，支持Activity，Dialog，View
        mEventObserver.bindActivity(this);

        new TestDialog(this).show();
    }

    /**
     * 事件观察者
     */
    private final FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>() {
        @Override
        public void onEvent(TestEvent event) {
            // 在主线程回调
            Log.i(TAG, "onEvent activity:" + event);
        }
    };
}