package com.fanwe.lib.eventbus;

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

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        unregister();
    }
}
