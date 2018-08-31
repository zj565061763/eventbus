package com.sd.lib.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class FEventBus
{
    private static FEventBus sInstance;
    private final Map<Class, List<FEventObserver>> MAP_OBSERVER = new LinkedHashMap<>();
    private final Map<Class, Object> MAP_STICKY = new HashMap<>();
    private Handler mHandler;

    private boolean mIsDebug;

    public FEventBus()
    {
        // 保持Public，支持创建新的对象
    }

    public static FEventBus getDefault()
    {
        if (sInstance == null)
        {
            synchronized (FEventBus.class)
            {
                if (sInstance == null)
                    sInstance = new FEventBus();
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
            mHandler = new Handler(Looper.getMainLooper());
        return mHandler;
    }

    /**
     * 发送粘性事件
     *
     * @param event
     */
    public synchronized void postSticky(Object event)
    {
        if (event == null)
            return;

        final Class clazz = event.getClass();
        MAP_STICKY.put(clazz, event);

        if (mIsDebug)
            Log.i(FEventBus.class.getSimpleName(), "postSticky:" + event);

        post(event);
    }

    /**
     * 移除某个粘性事件
     *
     * @param clazz
     */
    public synchronized void removeSticky(Class clazz)
    {
        MAP_STICKY.remove(clazz);
    }

    /**
     * 移除所有粘性事件
     */
    public synchronized void removeAllSticky()
    {
        MAP_STICKY.clear();
    }

    /**
     * 发送事件
     *
     * @param event
     */
    public synchronized void post(final Object event)
    {
        if (event == null)
            return;

        final List<FEventObserver> holder = MAP_OBSERVER.get(event.getClass());
        if (holder == null)
            return;

        if (Looper.myLooper() == Looper.getMainLooper())
        {
            if (mIsDebug)
                Log.i(FEventBus.class.getSimpleName(), "post----->" + event + " " + holder.size());

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
        } else
        {
            getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    post(event);
                }
            });
        }
    }

    /**
     * 注册观察者
     *
     * @param observer
     */
    public synchronized void register(final FEventObserver observer)
    {
        if (observer == null)
            return;

        final Class clazz = observer.mEventClass;
        if (clazz == null)
            throw new NullPointerException("observer's event class is null");

        List<FEventObserver> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            holder = new CopyOnWriteArrayList<>();
            MAP_OBSERVER.put(clazz, holder);
        }

        if (holder.contains(observer))
            return;

        holder.add(observer);

        if (mIsDebug)
            Log.i(FEventBus.class.getSimpleName(), "register:" + observer + " (" + clazz.getName() + " " + holder.size() + ")");

        final Object sticky = MAP_STICKY.get(clazz);
        if (sticky != null)
        {
            notifyObserver(observer, sticky);
            if (mIsDebug)
                Log.i(FEventBus.class.getSimpleName(), "notify sticky when register:" + sticky);
        }
    }

    /**
     * 取消注册观察者
     *
     * @param observer
     */
    public synchronized void unregister(final FEventObserver observer)
    {
        if (observer == null)
            return;

        final Class clazz = observer.mEventClass;
        if (clazz == null)
            throw new NullPointerException("observer's event class is null");

        final List<FEventObserver> holder = MAP_OBSERVER.get(clazz);

        if (holder == null)
            return;

        if (holder.remove(observer))
        {
            if (mIsDebug)
                Log.e(FEventBus.class.getSimpleName(), "unregister:" + observer + " (" + clazz.getName() + " " + holder.size() + ")");
        }

        if (holder.isEmpty())
            MAP_OBSERVER.remove(clazz);
    }

    private void notifyObserver(final FEventObserver observer, final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            observer.onEvent(event);
        } else
        {
            getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    observer.onEvent(event);
                }
            });
        }
    }
}
