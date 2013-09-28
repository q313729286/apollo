package seker.common;

import seker.common.utils.LogUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {


    public static final String TAG = BaseApplication.TAG;

    public static final boolean LOG = BaseApplication.GLOBAL_LOG & true;

    protected BaseApplication mApp;

    protected BaseActivity mActivity;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
        mApp = (BaseApplication) mActivity.getApplication();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
    }
    
    @Override
    public void onPause() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onPause();
    }
    
    @Override
    public void onStop() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onStop();
    }
    
    @Override
    public void onDestroyView() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onDestroyView();
    }
    
    @Override
    public void onDestroy() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        super.onDestroy();
    }
    
    @Override
    public void onDetach() {
        if (LOG) {
            Log.d(TAG, LogUtils.getClassFileLineMethod(getClass().getSimpleName()));
        }
        mActivity = null;
        mApp = null;
        super.onDetach();
    }
}
