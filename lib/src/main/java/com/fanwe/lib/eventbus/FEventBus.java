package com.fanwe.lib.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by zhengjun on 2018/1/31.
 */
public class FEventBus
{
    private static FEventBus sInstance;
    private final Map<Class, Map<FEventObserver, Object>> MAP_OBSERVER = new HashMap<>();
    private Handler mHandler;

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
        final Map<FEventObserver, Object> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            return;
        }

        if (holder.isEmpty())
        {
            MAP_OBSERVER.remove(clazz);
        } else
        {
            if (mIsDebug)
            {
                Log.i(FEventBus.class.getSimpleName(), "post----->" + event + " " + holder.size());
            }

            int count = 0;
            final Object[] observers = holder.keySet().toArray();
            for (Object item : observers)
            {
                if (mIsDebug)
                {
                    count++;
                    Log.i(FEventBus.class.getSimpleName(), "notify " + count + " " + item);
                }

                final FEventObserver observer = (FEventObserver) item;
                observer.onEvent(event);
            }
        }
    }

    synchronized void register(final FEventObserver<?> observer)
    {
        final Class clazz = observer.mEventClass;
        Map<FEventObserver, Object> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            holder = new WeakHashMap<>();
            MAP_OBSERVER.put(clazz, holder);
        }

        if (mIsDebug)
        {
            if (!holder.containsKey(observer))
            {
                Log.i(FEventBus.class.getSimpleName(), "register:" + observer + " (" + clazz.getName() + ") " + (holder.size() + 1));
            }
        }
        holder.put(observer, 0);
    }

    synchronized void unregister(final FEventObserver<?> observer)
    {
        final Class clazz = observer.mEventClass;
        final Map<FEventObserver, Object> holder = MAP_OBSERVER.get(clazz);
        if (holder == null)
        {
            return;
        }

        if (mIsDebug)
        {
            if (holder.containsKey(observer))
            {
                Log.e(FEventBus.class.getSimpleName(), "unregister:" + observer + " (" + clazz.getName() + ") " + (holder.size() - 1));
            }
        }

        holder.remove(observer);
        if (holder.isEmpty())
        {
            MAP_OBSERVER.remove(clazz);
        }
    }
}
