package com.fanwe.lib.eventbus;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;

import java.lang.ref.WeakReference;

/**
 * Created by zhengjun on 2018/2/26.
 */
final class LifecycleHolder
{
    private Callback mCallback;

    public LifecycleHolder(Callback callback)
    {
        mCallback = callback;
        if (callback == null)
        {
            throw new NullPointerException("callback is null");
        }
    }

    public final void setActivity(final Activity activity)
    {
        if (activity == null)
        {
            setView(null);
            return;
        }

        final Window window = activity.getWindow();
        if (window != null)
        {
            setView(window.getDecorView());
        } else
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        setView(activity.getWindow().getDecorView());
                    } catch (Exception e)
                    {
                        throw new RuntimeException("bind lifecycle view failed with " + activity, e);
                    }
                }
            });
        }
    }

    public final void setDialog(final Dialog dialog)
    {
        if (dialog == null)
        {
            setView(null);
            return;
        }

        final Window window = dialog.getWindow();
        if (window != null)
        {
            setView(window.getDecorView());
        } else
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        setView(dialog.getWindow().getDecorView());
                    } catch (Exception e)
                    {
                        throw new RuntimeException("bind lifecycle view failed with " + dialog, e);
                    }
                }
            });
        }
    }

    //---------- View start ----------

    private WeakReference<View> mView;

    private View getView()
    {
        return mView == null ? null : mView.get();
    }

    public final void setView(View view)
    {
        View old = getView();
        if (old != view)
        {
            if (old != null)
            {
                old.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
            }

            if (view != null)
            {
                mView = new WeakReference<>(view);

                view.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
                if (isAttachedToWindow(view))
                {
                    mCallback.onLifecycleStateChanged(true);
                }
            } else
            {
                mView = null;
            }
        }
    }

    private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener()
    {
        @Override
        public void onViewAttachedToWindow(View v)
        {
            mCallback.onLifecycleStateChanged(true);
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            mCallback.onLifecycleStateChanged(false);
        }
    };

    //---------- View end ----------

    private static boolean isAttachedToWindow(View view)
    {
        if (Build.VERSION.SDK_INT >= 19)
        {
            return view.isAttachedToWindow();
        } else
        {
            return view.getWindowToken() != null;
        }
    }

    public interface Callback
    {
        void onLifecycleStateChanged(boolean enable);
    }
}
