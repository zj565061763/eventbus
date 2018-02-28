package com.fanwe.lib.eventbus;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
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
    private final Map<Class, Object> MAP_STICKY = new HashMap<>();
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
                mTimer = new CountDownTimer(Long.MAX_VALUE, 20 * 1000)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        printRegisterInfo();
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

    private synchronized void printRegisterInfo()
    {
        if (mIsDebug)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("====================");
            sb.append("\n");

            if (!MAP_OBSERVER.isEmpty())
            {
                sb.append("---observer:");
                for (Map.Entry<Class, List<FEventObserver>> item : MAP_OBSERVER.entrySet())
                {
                    sb.append(item.getKey().getName()).append("=").append(item.getValue().toString())
                            .append(" ").append(item.getValue().size())
                            .append("\n");
                }
            }

            if (!MAP_STICKY.isEmpty())
            {
                sb.append("---sticky:");
                for (Map.Entry<Class, Object> item : MAP_STICKY.entrySet())
                {
                    sb.append(item.getValue().toString())
                            .append("\n");
                }
            }

            sb.append("====================");
            Log.i(FEventBus.class.getSimpleName(), sb.toString());
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
     * 发送粘性事件
     *
     * @param event
     */
    public synchronized void postSticky(Object event)
    {
        if (event == null)
        {
            return;
        }
        final Class clazz = event.getClass();
        MAP_STICKY.put(clazz, event);
        if (mIsDebug)
        {
            Log.i(FEventBus.class.getSimpleName(), "postSticky:" + event);
        }
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
        {
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
            notifyObserver(item, event);
            if (mIsDebug)
            {
                count++;
                Log.i(FEventBus.class.getSimpleName(), "notify " + count + " " + item);
            }
        }
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

    synchronized void register(final FEventObserver observer)
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

        Object sticky = MAP_STICKY.get(clazz);
        if (sticky != null)
        {
            notifyObserver(observer, sticky);
            if (mIsDebug)
            {
                Log.i(FEventBus.class.getSimpleName(), "notify sticky when register:" + sticky);
            }
        }
    }

    synchronized void unregister(final FEventObserver observer)
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
