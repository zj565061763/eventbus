package com.fanwe.lib.eventbus;

import android.os.Build;
import android.view.View;

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

    //---------- View start ----------

    private WeakReference<View> mView;

    private View getView()
    {
        return mView == null ? null : mView.get();
    }

    public void setView(View view)
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
                    mCallback.onViewAttachedToWindow(view);
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
            mCallback.onViewAttachedToWindow(v);
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            mCallback.onViewDetachedFromWindow(v);
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
        void onViewAttachedToWindow(View v);

        void onViewDetachedFromWindow(View v);
    }
}
