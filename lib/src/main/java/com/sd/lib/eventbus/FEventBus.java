package com.sd.lib.eventbus;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FEventBus {
    private static FEventBus sInstance;

    private final Map<Class<?>, Map<FEventObserver, String>> mObserverHolder = new HashMap<>();
    private final Map<Class<?>, Object> mStickyHolder = new HashMap<>();

    private boolean mIsDebug;

    public FEventBus() {
        // 保持Public，支持创建新的对象
    }

    public static FEventBus getDefault() {
        if (sInstance == null) {
            synchronized (FEventBus.class) {
                if (sInstance == null)
                    sInstance = new FEventBus();
            }
        }
        return sInstance;
    }

    public void setDebug(boolean debug) {
        mIsDebug = debug;
    }

    /**
     * 注册观察者
     *
     * @param observer
     */
    public synchronized void register(final FEventObserver observer) {
        if (observer == null)
            return;

        final Class<?> clazz = observer.mEventClass;
        if (clazz == null)
            throw new NullPointerException("observer's event class is null");

        Map<FEventObserver, String> holder = mObserverHolder.get(clazz);
        if (holder == null) {
            holder = new ConcurrentHashMap<>();
            mObserverHolder.put(clazz, holder);
        }

        final String put = holder.put(observer, "");
        if (put == null) {
            if (mIsDebug) {
                Log.i(FEventBus.class.getSimpleName(), "register +++++"
                        + " class:" + clazz.getName()
                        + " observer:" + observer
                        + " size:" + holder.size()
                        + " sizeTotal:" + mObserverHolder.size());
            }

            final Object sticky = mStickyHolder.get(clazz);
            if (sticky != null) {
                if (mIsDebug) {
                    Log.i(FEventBus.class.getSimpleName(), "notify sticky when register"
                            + " observer:" + observer
                            + " sticky:" + sticky);
                }

                notifyObserver(observer, sticky);
            }
        }
    }

    /**
     * 取消注册观察者
     *
     * @param observer
     */
    public synchronized void unregister(final FEventObserver observer) {
        if (observer == null)
            return;

        final Class<?> clazz = observer.mEventClass;
        if (clazz == null)
            throw new NullPointerException("observer's event class is null");

        final Map<FEventObserver, String> holder = mObserverHolder.get(clazz);
        if (holder == null)
            return;

        final String remove = holder.remove(observer);
        if (remove != null) {
            if (holder.isEmpty())
                mObserverHolder.remove(clazz);

            if (mIsDebug) {
                Log.i(FEventBus.class.getSimpleName(), "unregister -----"
                        + " class:" + clazz.getName()
                        + " observer:" + observer
                        + " size:" + holder.size()
                        + " sizeTotal:" + mObserverHolder.size());
            }
        }
    }

    /**
     * 发送事件
     *
     * @param event
     */
    public synchronized void post(final Object event) {
        if (event == null)
            return;

        final Class<?> clazz = event.getClass();
        final Map<FEventObserver, String> holder = mObserverHolder.get(clazz);
        if (holder == null)
            return;

        if (holder.isEmpty()) {
            mObserverHolder.remove(clazz);
            return;
        }

        if (mIsDebug) {
            Log.i(FEventBus.class.getSimpleName(), "post----->"
                    + " event:" + event
                    + " size:" + holder.size());
        }

        for (FEventObserver item : holder.keySet()) {
            notifyObserver(item, event);
        }
    }

    //---------- sticky ----------

    /**
     * 发送粘性事件
     *
     * @param event
     */
    public synchronized void postSticky(Object event) {
        if (event == null)
            return;

        final Class<?> clazz = event.getClass();
        mStickyHolder.put(clazz, event);

        if (mIsDebug)
            Log.i(FEventBus.class.getSimpleName(), "postSticky:" + event);

        post(event);
    }

    /**
     * 移除某个粘性事件
     *
     * @param clazz
     */
    public synchronized void removeSticky(Class<?> clazz) {
        if (clazz == null)
            return;

        mStickyHolder.remove(clazz);
    }

    /**
     * 移除所有粘性事件
     */
    public synchronized void removeAllSticky() {
        mStickyHolder.clear();
    }

    /**
     * 通知观察者
     *
     * @param observer
     * @param event
     */
    private void notifyObserver(final FEventObserver observer, final Object event) {
        Utils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mIsDebug) {
                    Log.i(FEventBus.class.getSimpleName(), "notifyObserver"
                            + " event:" + event
                            + " observer:" + observer);
                }

                observer.onEvent(event);
            }
        });
    }
}
