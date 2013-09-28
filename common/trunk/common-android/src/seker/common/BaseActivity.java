package seker.common;

import seker.common.utils.LogUtils;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and navigation/system bar) with
 * user interaction.
 * 
 * @see SystemUiHider
 */
public abstract class BaseActivity extends FragmentActivity {
    
    public static final String TAG = BaseApplication.TAG;

    public static final boolean LOG = BaseApplication.GLOBAL_LOG & true;

    protected BaseApplication mApp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (BaseApplication) getApplication();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    public boolean onSearchRequested() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        return super.onSearchRequested();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }

    @Override
    public void onBackPressed() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        mApp = null;
        super.onDestroy();
    }
}
