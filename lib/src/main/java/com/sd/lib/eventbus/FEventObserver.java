package com.sd.lib.eventbus;

import android.app.Activity;
import android.app.Dialog;
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
        final Class<?> clazz = findTargetClass();
        final ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        final Type[] types = parameterizedType.getActualTypeArguments();

        if (types != null && types.length > 0)
            mEventClass = (Class<T>) types[0];
        else
            throw new RuntimeException("generic type not found");
    }

    private Class<?> findTargetClass()
    {
        Class<?> clazz = getClass();
        while (true)
        {
            if (clazz.getSuperclass() == FEventObserver.class)
                break;
            else
                clazz = clazz.getSuperclass();
        }
        return clazz;
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
                public void onLifecycleStateChanged(boolean enable)
                {
                    if (enable)
                        register();
                    else
                        unregister();
                }
            });
        }
        return mLifecycleHolder;
    }

    /**
     * 生命周期对象：Activity.getWindow().getDecorView()
     * <br>
     * {@link #bindLifecycle(View)}
     *
     * @param activity
     * @return
     */
    public final boolean bindLifecycle(Activity activity)
    {
        if (activity == null || activity.isFinishing())
            return false;

        getLifecycleHolder().setActivity(activity);
        return true;
    }

    /**
     * 生命周期对象：Dialog.getWindow().getDecorView()
     * <br>
     * {@link #bindLifecycle(View)}
     *
     * @param dialog
     * @return
     */
    public final boolean bindLifecycle(Dialog dialog)
    {
        if (dialog == null)
            return false;

        getLifecycleHolder().setDialog(dialog);
        return true;
    }

    /**
     * 绑定生命周期对象
     * <br>
     * 当View对象Attached的时候会注册当前观察者
     * <br>
     * 当View对象Detached的时候会取消注册当前观察者
     *
     * @param view
     * @return true-绑定成功；false-绑定失败
     */
    public final boolean bindLifecycle(View view)
    {
        if (view == null)
            return false;

        getLifecycleHolder().setView(view);
        return true;
    }

    /**
     * 如果已经绑定生命周期，则解除生命周期绑定并取消注册
     */
    public final void unbindLifecycle()
    {
        getLifecycleHolder().setView(null);
    }

    //---------- Lifecycle end ----------

    @Deprecated
    public final FEventObserver<T> setLifecycle(Activity activity)
    {
        getLifecycleHolder().setActivity(activity);
        return this;
    }

    @Deprecated
    public final FEventObserver<T> setLifecycle(Dialog dialog)
    {
        getLifecycleHolder().setDialog(dialog);
        return this;
    }

    @Deprecated
    public final FEventObserver<T> setLifecycle(View view)
    {
        getLifecycleHolder().setView(view);
        return this;
    }
}
