package com.fanwe.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.eventbus.FEventBus;
import com.fanwe.lib.eventbus.FEventObserver;
import com.fanwe.lib.eventbus.FEventObserverContainer;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FEventBus.getDefault().post(new TestEvent()); //发送事件
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FEventBus.getDefault().post(new OnResumeEvent()); //发送事件
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FEventBus.getDefault().post(new OnStopEvent()); //发送事件
    }

    /**
     * 接收事件方式一
     */
    private FEventObserverContainer mEventObserverContainer = new FEventObserverContainer()
    {
        /**
         * 定义public权限，无返回值，参数长度为1个的方法
         * @param event
         */
        public void onEvent(OnResumeEvent event)
        {
            Log.i(TAG, "group:" + String.valueOf(event));
        }

        /**
         * 定义public权限，无返回值，参数长度为1个的方法
         * @param event
         */
        public void onEvent(OnStopEvent event)
        {
            Log.i(TAG, "group:" + String.valueOf(event));
        }
    };

    /**
     * 接收事件方式二
     */
    private FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            Log.i(TAG, String.valueOf(event));
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /**
         * 取消注册观察者，否则会造成内存泄漏
         */
        mEventObserver.unregister();
        mEventObserverContainer.unregister();
    }
}
