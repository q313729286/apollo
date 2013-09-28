/*
 * Copyright (C) 2013 Seker. All rights reserved.
 */
package seker.common.speed;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于计时的工具类
 * 
 * @author Liu Xinjian
 * 
 */
public final class TimeLogger {

    /** 时间监控的开关 */
    public static final boolean LOG = false;

    /**
     * 默认的输出文件路径
     */
    static final String DEFAULT_TIME_LOG_PATH = "timelogger_%s.csv";

    /** 单实例 */
    private static volatile TimeLogger sInstance = new TimeLogger();

    /** TimeInfo缓存*/
    private List<TimeInfo> mTimeInfos;

    /**
     * private constructor
     */
    private TimeLogger() {
        mTimeInfos = new ArrayList<TimeInfo>();
    }

    /**
     * 记录一条时间Log
     * @param feture    功能
     * @param model     模块名
     * @param tag       时间点Tag
     */
    public static void record(String feture, String model, String tag) {
        if (null != model && model.length() > 0 && null != tag && tag.length() > 0) {
            TimeInfo info = new TimeInfo(System.currentTimeMillis(), Thread.currentThread().getId(), feture, model, tag);
            sInstance.mTimeInfos.add(info);
        }
    }

    /**
     * 输出到默cvs文件(文件名为timelogger_yyyy-MM-dd HH:mm:ss.SSS.cvs)
     * @param dir
     *              目标文件路径
     * @throws IOException 如果目标目录没有写权限，会抛出IO异常
     */
    public static void export(String dir) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String file = String.format(DEFAULT_TIME_LOG_PATH, sdf.format(new Date()));
        export(dir, file);
    }
    
    /**
     * 输出到指定的csv文件
     * 
     * @param dir
     *              目标文件路径
     * @param file
     *              目标文件名
     * @throws IOException 如果目标目录没有写权限，会抛出IO异常
     */
    public static synchronized void export(String dir, String file) throws IOException {
        File path = new File(dir);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new IOException("Can't creat parent dirs:" + dir);
            }
        }

        
        CsvWriter writer = new CsvWriter(new File(path, file), true);
        String[] row = new String[] { "SystemTime", "Thread", "Feture", "Model", "TAG" };
        writer.writeRow(row);

        for (TimeInfo info : sInstance.mTimeInfos) {
            row[0] = String.valueOf(info.time); 
            row[1] = String.valueOf(info.thread);
            row[2] = info.feature; // SUPPRESS CHECKSTYLE: 这里的index数字是靠程序员来保证
            row[3] = info.model;   // SUPPRESS CHECKSTYLE: 同上
            row[4] = info.tag;     // SUPPRESS CHECKSTYLE: 同上
            writer.writeRow(row);
        }
        writer.close();
        
        sInstance.mTimeInfos.clear();
    }
}
