package com.sd.lib.eventbus;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;

import java.lang.ref.WeakReference;

final class LifecycleHolder
{
    private Callback mCallback;

    public LifecycleHolder(Callback callback)
    {
        if (callback == null)
            throw new NullPointerException("callback is null");

        mCallback = callback;
    }

    public final void setActivity(final Activity activity)
    {
        if (activity == null)
        {
            setView(null);
            return;
        }

        Window window = activity.getWindow();
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
                    final Window delayWindow = activity.getWindow();
                    if (delayWindow != null)
                    {
                        setView(delayWindow.getDecorView());
                    } else
                    {
                        throw new RuntimeException("bind lifecycle view failed with " + activity);
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

        Window window = dialog.getWindow();
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
                    final Window delayWindow = dialog.getWindow();
                    if (delayWindow != null)
                    {
                        setView(delayWindow.getDecorView());
                    } else
                    {
                        throw new RuntimeException("bind lifecycle view failed with " + dialog);
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
        final View old = getView();
        if (old != view)
        {
            if (old != null)
                old.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);

            mView = view == null ? null : new WeakReference<>(view);

            if (view != null)
            {
                view.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
                if (isAttachedToWindow(view))
                    mCallback.onLifecycleStateChanged(true);
            } else
            {
                mCallback.onLifecycleStateChanged(false);
            }
        }
    }

    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener()
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
            return view.isAttachedToWindow();
        else
            return view.getWindowToken() != null;
    }

    public interface Callback
    {
        void onLifecycleStateChanged(boolean enable);
    }
}
