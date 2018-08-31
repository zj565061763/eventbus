package com.sd.event;

import android.app.Application;

import com.sd.lib.eventbus.FEventBus;

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        FEventBus.getDefault().setDebug(true);
    }
}
