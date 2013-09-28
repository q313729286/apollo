package seker.common;

import seker.common.utils.LogUtils;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class BaseApplication extends android.app.Application {

    public static boolean GLOBAL_LOG = true;

    public static final boolean LOG = GLOBAL_LOG & true;

    public static final String TAG = "common";

    @Override
    public void onCreate() {
        super.onCreate();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    public void onLowMemory() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onTrimMemory(level);
    }
    
    @Override
    public void onTerminate() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onTerminate();
    }
}

