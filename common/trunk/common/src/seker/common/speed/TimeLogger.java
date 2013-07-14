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

    private static TimeLogger sInstance;

    static {
        sInstance = new TimeLogger();
    }

    private List<TimeInfo> mTimeInfos;

    /**
     * private constructor
     */
    private TimeLogger() {
        mTimeInfos = new ArrayList<TimeInfo>();
    }

    public static void LogTimeInfo(String tag) {
        TimeInfo info = new TimeInfo(tag, System.currentTimeMillis(), Thread.currentThread().getId());
        sInstance.mTimeInfos.add(info);
    }

    /**
     * 输出到csv文件
     * 
     * @param dir
     *            dir
     * @throws IOException
     */
    public static void exportToCsvFile(String dir) throws IOException {
        File path = new File(dir);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new IOException("Can't creat parent dirs:" + dir);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String file = String.format(DEFAULT_TIME_LOG_PATH, sdf.format(new Date()));
        CsvWriter writer = new CsvWriter(new File(path, file), true);

        String[] row = new String[] { "Time", "TAG", "Thread" };
        writer.writeRow(row);

        for (TimeInfo info : sInstance.mTimeInfos) {
            row[0] = String.valueOf(info.time);
            row[1] = info.tag;
            row[2] = String.valueOf(info.thread);
            writer.writeRow(row);
        }

        writer.close();
    }
}
