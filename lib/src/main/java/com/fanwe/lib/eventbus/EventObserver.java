package com.fanwe.lib.eventbus;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 事件观察者
 */
public abstract class EventObserver<T>
{
    final Class<T> mEventClass;

    public EventObserver()
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

    EventObserver(Class<T> clazz)
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
        EventBus.getDefault().register(this);
    }

    /**
     * 取消注册当前对象
     */
    public final void unregister()
    {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        unregister();
    }

    //---------- Lifecycle start ----------

    private LifecycleHolder mLifecycleHolder;

    private LifecycleHolder getLifecycleHolder()
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

    public final void setActivity(Activity activity)
    {
        getLifecycleHolder().setActivity(activity);
    }

    public final void setView(View view)
    {
        getLifecycleHolder().setView(view);
    }

    //---------- Lifecycle end ----------
}
