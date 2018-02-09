package com.fanwe.lib.eventbus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 收到事件通知
     *
     * @param event
     * @return true-停止继续分发事件
     */
    public abstract boolean onEvent(T event);

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

    /**
     * 注册object对象的所有属性观察者
     *
     * @param object
     */
    public static void registerAll(Object object)
    {
        List<FEventObserver> listObserver = getAllObserver(object);
        for (FEventObserver item : listObserver)
        {
            item.register();
        }
    }

    /**
     * 取消注册object对象的所有属性观察者
     *
     * @param object
     */
    public static void unregisterAll(Object object)
    {
        List<FEventObserver> listObserver = getAllObserver(object);
        for (FEventObserver item : listObserver)
        {
            item.unregister();
        }
    }

    private static List<FEventObserver> getAllObserver(Object object)
    {
        final List<FEventObserver> listObserver = new ArrayList<>();
        Class clazz = object.getClass();
        try
        {
            while (true)
            {
                if (clazz.getName().startsWith("android.") || clazz == Object.class)
                {
                    break;
                }

                Field[] fields = clazz.getDeclaredFields();
                if (fields != null)
                {
                    for (Field item : fields)
                    {
                        if (Modifier.isStatic(item.getModifiers()))
                        {
                            continue;
                        }
                        item.setAccessible(true);
                        Object itemValue = item.get(object);
                        if (itemValue instanceof FEventObserver)
                        {
                            listObserver.add((FEventObserver) itemValue);
                        }
                    }
                }

                clazz = clazz.getSuperclass();
            }
        } catch (Exception e)
        {
        }
        return listObserver;
    }
}
