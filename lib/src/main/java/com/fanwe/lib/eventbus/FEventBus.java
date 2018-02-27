package com.fanwe.lib.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhengjun on 2018/1/31.
 */
public class FEventBus
{
    private static FEventBus sInstance;
    private final Map<Class, List<FEventObserver>> MAP_OBSERVER = new HashMap<>();
    private Handler mHandler;
    private int mRegisterCount;

    private boolean mIsDebug;

    private FEventBus()
    {
    }

    public static FEventBus getDefault()
    {
        if (sInstance == null)
        {
            synchronized (FEventBus.class)
            {
                if (sInstance == null)
                {
                    sInstance = new FEventBus();
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
                    FEventBus.this.post(event);
                }
            });
            return;
        }

        final Class clazz = event.getClass();
        final List<FEventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            return;
        }

        if (mIsDebug)
        {
            Log.i(FEventBus.class.getSimpleName(), "post----->" + event + " " + holder.size());
        }
        int count = 0;
        for (FEventObserver item : holder)
        {
            item.onEvent(event);

            if (mIsDebug)
            {
                count++;
                Log.i(FEventBus.class.getSimpleName(), "notify " + count + " " + item);
            }
        }
    }

    synchronized void register(final FEventObserver<?> observer)
    {
        final Class clazz = observer.mEventClass;
        List<FEventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            holder = new CopyOnWriteArrayList<>();
            MAP_OBSERVER.put(clazz, holder);
        }
        if (holder.contains(observer))
        {
            return;
        }

        holder.add(observer);
        mRegisterCount++;
        if (mIsDebug)
        {
            Log.i(FEventBus.class.getSimpleName(), "register:" + observer + " (" + clazz.getName() + " " + holder.size() + ") " + mRegisterCount);
        }
    }

    synchronized void unregister(final FEventObserver<?> observer)
    {
        final Class clazz = observer.mEventClass;
        final List<FEventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            return;
        }

        if (holder.remove(observer))
        {
            mRegisterCount--;
            if (mIsDebug)
            {
                Log.e(FEventBus.class.getSimpleName(), "unregister:" + observer + " (" + clazz.getName() + " " + holder.size() + ") " + mRegisterCount);
            }
        }
        if (holder.isEmpty())
        {
            MAP_OBSERVER.remove(clazz);
        }
    }
}
