package seker.common.utils;

import java.io.File;
import java.io.IOException;

import seker.common.BaseApplication;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public final class StorageUtils {
    
    public static final boolean LOG = BaseApplication.GLOBAL_LOG & true;

    public static final String TAG = "StorageUtils";

    private StorageUtils() {
        
    }
    
    /**
     * 判断外部存储是否可写
     * 
     * 此方法内采用文件读写操作来检测，所以相对比较耗时，请谨慎使用。
     * 
     * @return  true:可写; false 不存在/没有mounted/不可写
     */
    public static boolean isExternalStorageWriteable() {
        boolean writealbe = false;
        long start = System.currentTimeMillis();
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            File esd = Environment.getExternalStorageDirectory();
            if (esd.exists() && esd.canWrite()) {
                File file = new File(esd, ".696E5309-E4A7-27C0-A787-0B2CEBF1F1AB");
                if (file.exists()) {
                    writealbe = true;
                } else {
                    try {
                        writealbe = file.createNewFile();
                    } catch (IOException e) {
                        if (LOG) {
                            Log.w(TAG, "isExternalStorageWriteable() can't create test file.");
                        }
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        if (LOG) {
            Log.i(TAG, "Utility.isExternalStorageWriteable(" + writealbe + ") cost " + (end - start) + "ms.");
        }
        return writealbe;
    }
}
