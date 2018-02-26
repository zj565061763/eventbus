package com.fanwe.lib.eventbus;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by zhengjun on 2018/2/26.
 */
final class LifecycleHolder
{
    private Callback mCallback;

    public void setCallback(Callback callback)
    {
        mCallback = callback;
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
            if (mCallback != null)
            {
                mCallback.onViewAttachedToWindow(v);
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            if (mCallback != null)
            {
                mCallback.onViewDetachedFromWindow(v);
            }
        }
    };

    //---------- View end ----------

    public interface Callback
    {
        void onViewAttachedToWindow(View v);

        void onViewDetachedFromWindow(View v);
    }
}
