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
        FEventBus.getDefault().init(this);
        FEventBus.getDefault().setDebug(true);
    }
}
