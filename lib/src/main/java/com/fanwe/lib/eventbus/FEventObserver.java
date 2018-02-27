package com.fanwe.lib.eventbus;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 事件观察者
 */
public abstract class FEventObserver<T>
{
    final Class<T> mEventClass;

    public FEventObserver()
    {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        final Type[] types = parameterizedType.getActualTypeArguments();
        if (types != null && types.length == 1)
        {
            mEventClass = (Class<T>) types[0];
        } else
        {
            throw new RuntimeException("generic type length must be 1");
        }
    }

    /**
     * 收到事件通知
     *
     * @param event
     */
    public abstract void onEvent(T event);

    /**
     * 注册当前对象
     *
     * @return
     */
    public final FEventObserver<T> register()
    {
        FEventBus.getDefault().register(this);
        return this;
    }

    /**
     * 取消注册当前对象
     *
     * @return
     */
    public final FEventObserver<T> unregister()
    {
        FEventBus.getDefault().unregister(this);
        return this;
    }

    //---------- Lifecycle start ----------

    private LifecycleHolder mLifecycleHolder;

    private LifecycleHolder getLifecycleHolder()
    {
        if (mLifecycleHolder == null)
        {
            mLifecycleHolder = new LifecycleHolder(new LifecycleHolder.Callback()
            {
                @Override
                public void onStateChanged(boolean enable)
                {
                    if (enable)
                    {
                        register();
                    } else
                    {
                        unregister();
                    }
                }
            });
        }
        return mLifecycleHolder;
    }

    /**
     * {@link #setLifecycle(View)}
     *
     * @param activity
     * @return
     */
    public final FEventObserver<T> setLifecycle(final Activity activity)
    {
        if (activity == null)
        {
            setLifecycle((View) null);
        } else
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    setLifecycle(activity.getWindow().getDecorView());
                }
            });
        }
        return this;
    }

    /**
     * {@link #setLifecycle(View)}
     *
     * @param dialog
     * @return
     */
    public final FEventObserver<T> setLifecycle(final Dialog dialog)
    {
        if (dialog == null)
        {
            setLifecycle((View) null);
        } else
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    setLifecycle(dialog.getWindow().getDecorView());
                }
            });
        }
        return this;
    }

    /**
     * 设置View对象
     * <br>
     * 当该View对象Attached的时候会注册当前观察者对象
     * 当该View对象Detached的时候会取消注册当前观察者对象
     *
     * @param view
     * @return
     */
    public final FEventObserver<T> setLifecycle(View view)
    {
        getLifecycleHolder().setView(view);
        return this;
    }

    //---------- Lifecycle end ----------
}
