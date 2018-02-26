package com.fanwe.lib.eventbus;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by zhengjun on 2018/2/26.
 */
final class LifecycleHolder
{
    private WeakReference<Activity> mActivity;

    private Callback mCallback;

    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    private Activity getActivity()
    {
        return mActivity == null ? null : mActivity.get();
    }

    public void setActivity(Activity activity)
    {
        Activity old = getActivity();
        if (old != activity)
        {
            if (old != null)
            {
                old.getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }

            if (activity != null)
            {
                mActivity = new WeakReference<>(activity);
                activity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            } else
            {
                mActivity = null;
            }
        }
    }

    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks()
    {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState)
        {
        }

        @Override
        public void onActivityStarted(Activity activity)
        {
        }

        @Override
        public void onActivityResumed(Activity activity)
        {
        }

        @Override
        public void onActivityPaused(Activity activity)
        {
        }

        @Override
        public void onActivityStopped(Activity activity)
        {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState)
        {
        }

        @Override
        public void onActivityDestroyed(Activity activity)
        {
            if (activity == getActivity())
            {
                if (mCallback != null)
                {
                    mCallback.onActivityDestroyed(activity);
                }
                setActivity(null);
            }
        }
    };

    public interface Callback
    {
        void onActivityDestroyed(Activity activity);
    }
}
