/*
 * Copyright (C) 2013 Seker. All rights reserved.
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
    
    /** 功能 */
    final String feature;
    
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
     * @param fe
     *            功能
     * @param md
     *            模块名称
     * @param ta
     *            模块TAG
     */
    public TimeInfo(long ti, long th, String fe, String md, String ta) {
        time = ti;
        thread = th;
        feature = fe;
        model = md;
        tag = ta;
    }
}
