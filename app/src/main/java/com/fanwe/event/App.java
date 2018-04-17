package com.fanwe.event;

import android.app.Application;

import com.fanwe.lib.eventbus.FEventBus;

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        FEventBus.getDefault().setDebug(true);
    }
}
