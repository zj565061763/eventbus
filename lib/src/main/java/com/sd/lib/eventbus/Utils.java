package com.sd.lib.eventbus;


import android.os.Handler;
import android.os.Looper;

class Utils
{
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private Utils()
    {
    }

    public static void runOnMainThread(final Runnable runnable)
    {
        if (runnable == null)
            return;

        if (Looper.myLooper() == Looper.getMainLooper())
        {
            runnable.run();
        } else
        {
            MAIN_HANDLER.post(new Runnable()
            {
                @Override
                public void run()
                {
                    runnable.run();
                }
            });
        }
    }
}
