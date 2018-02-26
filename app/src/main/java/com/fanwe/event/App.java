package com.fanwe.event;

import android.app.Application;

import com.fanwe.lib.eventbus.EventBus;

/**
 * Created by Administrator on 2018/2/1.
 */

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        EventBus.getDefault().setDebug(true);
    }
}
