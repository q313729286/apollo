package seker.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

public final class Utility {
    private Utility() {

    }
    
    /**
     * 缓存文件
     * 
     * @param context   Context Object
     * @param file      本地文件名
     * @param data      要保存的数据
     * @param mode      打开文件的方式
     * @return          是否保存成功
     */
    public static boolean cache(Context context, String file, String data, int mode) {
        return cache(context, file, data.getBytes(), mode);
    }
    
    /**
     * 缓存文件
     * 
     * @param context   Context Object
     * @param file      本地文件名
     * @param data      要保存的数据
     * @param mode      打开文件的方式
     * @return          是否保存成功
     */
    public static boolean cache(Context context, String file, byte[] data, int mode) {
        boolean bResult = false;
        if (null != data && data.length > 0) {
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(file, mode);
                fos.write(data);
                fos.flush();
                bResult = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return bResult;
    }
    
    /** 根据系统时间生成文件名的格式 */
    private static SimpleDateFormat sDateFormat = null;
    
    /**
     * 根据系统时间生成文件名
     * @param suffix
     *                  文件后缀名 
     * @return
     *                  文件名
     */
    public static synchronized String createFileName(String suffix) {
        if (null == sDateFormat) {
            sDateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss-SSS");
        }
        Date date = new Date();
        return String.format("%s.%s", sDateFormat.format(date), suffix);
    }
    
    /**
     * 为了防止大家不给线程起名字，写此静态函数.目的是当发生线程泄漏后能够快速定位问题.
     * @param r Runnable
     * @param name 线程名
     * @return Thread
     */
    public static Thread newThread(Runnable r, String name) {
        if (TextUtils.isEmpty(name)) {
            throw new RuntimeException("thread name should not be empty");
        }
        return new Thread(r, getStandardThreadName(name));
    }
    
    /**
     * 获取标准线程名
     * @param name 线程名
     * @return 处理过的线程名
     */
    public static String getStandardThreadName(String name) {
        if (!TextUtils.isEmpty(name)) {
            final String PREFIX = "seker_";
            if (!name.startsWith(PREFIX)) {
                return PREFIX + name;
            }
        }
        return name;
    }
}
