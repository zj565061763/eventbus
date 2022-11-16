package com.sd.demo.eventbus;

import android.app.Application;

import com.sd.lib.eventbus.FEventBus;

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        // 设置调试模式，输入日志
        FEventBus.getDefault().setDebug(true);
    }
}
