package seker.common.speed;

/**
 * 时间点信息
 * 
 * @author Liu Xinjian
 */
class TimeInfo {
    /** 模块TAG */
    final String tag;

    /** 运行的系统时间点 */
    final long time;

    /** 运行的线程ID */
    final long thread;

    /**
     * 时间点信息
     * 
     * @param ta
     *            模块TAG
     * @param ti
     *            运行的系统时间点
     * @param th
     *            运行的线程ID
     */
    public TimeInfo(String ta, long ti, long th) {
        tag = ta;
        time = ti;
        thread = th;
    }
}
