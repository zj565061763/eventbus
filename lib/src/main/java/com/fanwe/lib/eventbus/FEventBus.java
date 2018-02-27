package com.fanwe.lib.eventbus;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhengjun on 2018/1/31.
 */
public class FEventBus
{
    private static FEventBus sInstance;
    private final Map<Class, List<FEventObserver>> MAP_OBSERVER = new LinkedHashMap<>();
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

    public synchronized void setDebug(boolean debug)
    {
        mIsDebug = debug;
        startTimer(debug);
    }

    private CountDownTimer mTimer;

    private void startTimer(boolean start)
    {
        if (start)
        {
            if (mTimer == null)
            {
                mTimer = new CountDownTimer(Long.MAX_VALUE, 10 * 1000)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        synchronized (FEventBus.this)
                        {
                            if (mIsDebug)
                            {
                                StringBuilder sb = new StringBuilder("register observer:\n");
                                for (Map.Entry<Class, List<FEventObserver>> item : MAP_OBSERVER.entrySet())
                                {
                                    sb.append(item.getKey().getName()).append("=").append(item.getValue().toString())
                                            .append(" ").append(item.getValue().size())
                                            .append("\n");
                                }
                                Log.i(FEventBus.class.getSimpleName(), sb.toString());
                            }
                        }
                    }

                    @Override
                    public void onFinish()
                    {
                    }
                };
                mTimer.start();
            }
        } else
        {
            if (mTimer != null)
            {
                mTimer.cancel();
                mTimer = null;
            }
        }
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
        if (mIsDebug)
        {
            Log.i(FEventBus.class.getSimpleName(), "register:" + observer + " (" + clazz.getName() + " " + holder.size() + ")");
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
            if (mIsDebug)
            {
                Log.e(FEventBus.class.getSimpleName(), "unregister:" + observer + " (" + clazz.getName() + " " + holder.size() + ")");
            }
        }
        if (holder.isEmpty())
        {
            MAP_OBSERVER.remove(clazz);
        }
    }
}
