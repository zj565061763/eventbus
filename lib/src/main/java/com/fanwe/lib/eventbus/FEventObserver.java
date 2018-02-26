package com.fanwe.lib.eventbus;

import android.app.Activity;
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
        if (types != null && types.length > 0)
        {
            mEventClass = (Class<T>) types[0];
        } else
        {
            throw new RuntimeException("generic type not found");
        }
        register();
    }

    FEventObserver(Class<T> clazz)
    {
        mEventClass = clazz;
    }

    /**
     * 收到事件通知
     *
     * @param event
     */
    public abstract void onEvent(T event);

    /**
     * 注册当前对象
     */
    public final void register()
    {
        FEventBus.getDefault().register(this);
    }

    /**
     * 取消注册当前对象
     */
    public final void unregister()
    {
        FEventBus.getDefault().unregister(this);
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        unregister();
    }

    private LifecycleHolder mLifecycleHolder;

    public LifecycleHolder getLifecycleHolder()
    {
        if (mLifecycleHolder == null)
        {
            mLifecycleHolder = new LifecycleHolder();
            mLifecycleHolder.setCallback(new LifecycleHolder.Callback()
            {
                @Override
                public void onActivityDestroyed(Activity activity)
                {
                    unregister();
                }

                @Override
                public void onViewAttachedToWindow(View v)
                {
                    register();
                }

                @Override
                public void onViewDetachedFromWindow(View v)
                {
                    unregister();
                }
            });
        }
        return mLifecycleHolder;
    }
}
