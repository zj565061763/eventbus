package com.fanwe.lib.eventbus;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 在这个类的实现类里面定义要接收的事件的方法即可接收事件，如：
 * <p>
 * public void onEvent(Event event)
 * {
 * <p>
 * }
 */
public abstract class EventObserverContainer
{
    private final List<EventObserver> mListObserver = new ArrayList<>();

    public EventObserverContainer()
    {
        createObserver();
        register();
    }

    public final void register()
    {
        for (EventObserver item : mListObserver)
        {
            item.register();
        }
    }

    public final void unregister()
    {
        for (EventObserver item : mListObserver)
        {
            item.unregister();
        }
    }

    public void onError(Exception e)
    {

    }

    private void createObserver()
    {
        List<Method> listMethod = getEventMethod();
        for (final Method item : listMethod)
        {
            final Class clazz = item.getParameterTypes()[0];
            final EventObserver observer = new EventObserver(clazz)
            {
                @Override
                public void onEvent(Object event)
                {
                    try
                    {
                        item.invoke(EventObserverContainer.this, event);
                    } catch (Exception e)
                    {
                        onError(e);
                    }
                }
            };
            mListObserver.add(observer);
        }
    }

    private List<Method> getEventMethod()
    {
        List<Method> listMethod = new ArrayList<>();

        Method[] methods = getClass().getDeclaredMethods();
        for (Method item : methods)
        {
            final int modifiers = item.getModifiers();
            if (Modifier.isStatic(modifiers))
            {
                throw new RuntimeException("method must not be static:" + item);
            }
            if (!Modifier.isPublic(modifiers))
            {
                throw new RuntimeException("method must be public:" + item);
            }
            if (!"void".equals(item.getReturnType().getSimpleName()))
            {
                throw new RuntimeException("method return type must be void:" + item);
            }
            Class<?>[] params = item.getParameterTypes();
            if (params.length != 1)
            {
                throw new RuntimeException("method params length must be 1:" + item);
            }
            if (params[0].isPrimitive())
            {
                throw new RuntimeException("method params must not be primitive:" + item);
            }
            item.setAccessible(true);
            listMethod.add(item);
        }

        return listMethod;
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
