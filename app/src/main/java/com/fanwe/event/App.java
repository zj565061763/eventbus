package com.fanwe.event;

import android.app.Application;

import com.fanwe.lib.eventbus.FEventBus;

/**
 * Created by Administrator on 2018/2/1.
 */

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        /**
         * 设置调试模式
         */
        FEventBus.getDefault().setDebug(true);

        /**
         * 发送一个粘性事件，当有对应事件的观察者注册的时候，会在注册的时候立即通知此事件
         */
        FEventBus.getDefault().postSticky(new TestEvent());

        /**
         * 移除粘性事件
         */
//        FEventBus.getDefault().removeSticky(TestEvent.class);
    }
}
