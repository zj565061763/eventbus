package com.fanwe.lib.eventbus;

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
public abstract class FEventObserverGroup
{
    private final List<FEventObserver> mListObserver = new ArrayList<>();

    public FEventObserverGroup()
    {
        createObserver();
        register();
    }

    public final void register()
    {
        for (FEventObserver item : mListObserver)
        {
            item.register();
        }
    }

    public final void unregister()
    {
        for (FEventObserver item : mListObserver)
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
            final FEventObserver observer = new FEventObserver(clazz)
            {
                @Override
                public void onEvent(Object event)
                {
                    try
                    {
                        item.invoke(FEventObserverGroup.this, event);
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
}
