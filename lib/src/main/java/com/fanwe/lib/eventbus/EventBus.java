package com.fanwe.lib.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengjun on 2018/1/31.
 */
public class EventBus
{
    private static EventBus sInstance;
    private final Map<Class, List<EventObserver>> MAP_OBSERVER = new HashMap<>();
    private Handler mHandler;

    private boolean mIsDebug;

    private EventBus()
    {
    }

    public static EventBus getDefault()
    {
        if (sInstance == null)
        {
            synchronized (EventBus.class)
            {
                if (sInstance == null)
                {
                    sInstance = new EventBus();
                }
            }
        }
        return sInstance;
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    private Handler getHandler()
    {
        if (mHandler == null)
        {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 发送事件
     *
     * @param event
     */
    public synchronized void post(final Object event)
    {
        if (event == null)
        {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper())
        {
            getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    EventBus.this.post(event);
                }
            });
            return;
        }

        final Class clazz = event.getClass();
        final List<EventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            return;
        }

        if (mIsDebug)
        {
            Log.i(EventBus.class.getSimpleName(), "post----->" + event + " " + holder.size());
        }
        int count = 0;
        for (EventObserver item : holder)
        {
            item.onEvent(event);

            if (mIsDebug)
            {
                count++;
                Log.i(EventBus.class.getSimpleName(), "notify " + count + " " + item);
            }
        }
    }

    synchronized void register(final EventObserver<?> observer)
    {
        final Class clazz = observer.mEventClass;
        List<EventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            holder = new ArrayList<>();
            MAP_OBSERVER.put(clazz, holder);
        }
        if (holder.contains(observer))
        {
            return;
        }

        holder.add(observer);
        if (mIsDebug)
        {
            Log.i(EventBus.class.getSimpleName(), "register:" + observer + " (" + clazz.getName() + ") " + (holder.size()));
        }
    }

    synchronized void unregister(final EventObserver<?> observer)
    {
        final Class clazz = observer.mEventClass;
        final List<EventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            return;
        }

        if (holder.remove(observer))
        {
            if (mIsDebug)
            {
                Log.e(EventBus.class.getSimpleName(), "unregister:" + observer + " (" + clazz.getName() + ") " + (holder.size()));
            }
        }
        if (holder.isEmpty())
        {
            MAP_OBSERVER.remove(clazz);
        }
    }
}
