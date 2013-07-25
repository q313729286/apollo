/*
 * Copyright (C) 2012 Baidu Inc. All rights reserved.
 */
package seker.common.speed;

/**
 * 时间点信息
 * 
 * @author Liu Xinjian
 */
class TimeInfo {
    
    /** 运行的系统时间点 */
    final long time;
    
    /** 运行的线程ID */
    final long thread;
    
    /** 模块TAG */
    final String model;
    
    /** 模块TAG */
    final String tag;

    /**
     * 时间点信息
     * 
     * @param ti
     *            运行的系统时间点
     * @param th
     *            运行的线程ID
     * @param md
     *            模块名称
     * @param ta
     *            模块TAG
     */
    public TimeInfo(long ti, long th, String md, String ta) {
        time = ti;
        thread = th;
        model = md;
        tag = ta;
    }
}
