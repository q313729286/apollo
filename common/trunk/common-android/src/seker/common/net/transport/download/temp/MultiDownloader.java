
package seker.common.net.transport.download.temp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * @author Lifeix
 */
public class MultiDownloader {
    
    /**
     * 线程池
     */
    private SingleDownloader[]                  mDownloaders;
    
    /**
     * 需要下载的远程资源URL路径
     */
    private String                              mUrl;
    
    /**
     * DAO操作一个数据库，记录每一个远程Url下载的断点情况：
     * 
     * 它的数据表结果为：id, url, threadId, downedSize
     */
    private final DownloadDBHelper              mDBHelper;
    
    /**
     * 远程资源文件的总长度（bytes）
     */
    private int                                 mTotalSize;
    
    /**
     * 总线程数
     */
    private int                                 mThreadNum;
    
    /**
     * 记录每一个线程下载的情况： key：线程Id，其实是线程所负责下载的数据块的index value：已下载的数据长度（bytes）
     */
    private ConcurrentHashMap<Integer, Integer> mCache;
    
    private Thread                              mDaemon;
    
    private DownloadListener                    mListener;
    
    private DownloadHandler                     mHandler = new DownloadHandler();
    
    private volatile boolean                    mIsLoop  = true;
    
    private int block;
    
    /**
     * UI线程里面invoke DownloadListener的回调
     */
    class DownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (null != mListener) {
                switch (msg.what) {
                    case DownloadListener.STATE_START: {
                        int curr = getCurrSize();
                        mListener.onDownloadStart(mTotalSize, curr);
                        break;
                    }
                    case DownloadListener.STATE_PROCESSING: {
                        int curr = getCurrSize();
                        mListener.onDownloadProgress(mTotalSize, curr);
                        break;
                    }
                    case DownloadListener.STATE_END: {
                        int curr = getCurrSize();
                        mListener.onDownloadEnd(mTotalSize, curr);
                        break;
                    }
                    case DownloadListener.STATE_ERROR: {
                        mListener.onDownloadError(mTotalSize, 0);
                        break;
                    }
                    case DownloadListener.STATE_CANCEL: {
                        int curr = getCurrSize();
                        mListener.onDownloadCancel(mTotalSize, curr);
                        break;
                    }
                }
            }
        }
    }
    
    public MultiDownloader(Context context) {
        mDBHelper = new DownloadDBHelper(context);
    }
    
    public void cancelDownload() {
        if (null != mDaemon && mDaemon.isAlive()) {
            mIsLoop = false;
            try {
                mDaemon.join(500);
            } catch (InterruptedException e) {
                Log.w("l99", e);
            }
            mDaemon = null;
        }
        
        if (null != mDownloaders) {
            for (int i = 0, n = mDownloaders.length; i < n; i++) {
                SingleDownloader downloader = mDownloaders[i];
                if (null != downloader) {
                    downloader.cancelDownload();
                }
                mDownloaders[i] = null;
            }
        }
    }
    
    public void startDownload(DownloadListener listener, final String url, final String dir, final int threadNum) {
        cancelDownload();
        if (null != mCache) {
            mCache.clear();
            mCache = null;
        }
        
        mIsLoop = true;
        
        mListener = listener;
        mThreadNum = threadNum;
        mUrl = url;
        
        mDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                download(dir);
            }
        });
        mDaemon.start();
    }
    
    private void download(String dir) {
        mTotalSize = connect(mUrl);// 远程资源文件的总长度（bytes）
        if (mTotalSize <= 0) {
            mHandler.sendEmptyMessage(DownloadListener.STATE_ERROR);
            return;
        }
        // 分配给每一个线程的数据块的长度（bytes）
        block = (int) Math.ceil((double) mTotalSize / mThreadNum);
        Log.i("l99", String.format("total=%d, block=%d", mTotalSize, block));
        
        recoverCache();
        
        if (getCurrSize() == mTotalSize) {
            mHandler.sendEmptyMessage(DownloadListener.STATE_END);
            return;
        }
        
        File root = new File(dir);
        if (!root.exists()) {
            root.mkdirs();
        }
        String filename = mUrl.substring(mUrl.lastIndexOf('/') + 1);
        File file = new File(dir, filename);
        
        mHandler.sendEmptyMessage(DownloadListener.STATE_START);
        
        mDownloaders = new SingleDownloader[mThreadNum];
        boolean finishs[] = new boolean[mThreadNum];        // 为每一个子线程做一个finish的flag.
        do {
            for (int i = 0, n = mDownloaders.length; i < n; i++) {
                if (finishs[i]) {
                    
                } else {        // 只为尚未结束的线程做finish更新检测
                    int start = mCache.get(i);
                    mDBHelper.update(mUrl, i, start);
                    
                    int end = (i + 1) * block - 1;
                    if (i + 1 == n) {
                        end = mTotalSize;
                    }
                    if (start < end) { // 大于和等于都算下载完成
                        if (null == mDownloaders[i] || !mDownloaders[i].isAlive()) {
                            mDownloaders[i] = new SingleDownloader(start, end, i, mUrl, file, mCache, mDBHelper);
                            mDownloaders[i].setPriority(Thread.MAX_PRIORITY);
                            mDownloaders[i].start();
                        }
                    } else {
                        finishs[i] = true;
                    }
                }
            }
            
            mHandler.sendEmptyMessage(DownloadListener.STATE_PROCESSING);
            
            boolean finish = true;
            for (boolean flag : finishs) {
                finish = finish && flag;
            }
            if (!finish) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        } while (mIsLoop);
        
        if (mIsLoop) {
            mHandler.sendEmptyMessage(DownloadListener.STATE_END);
        } else {
            mHandler.sendEmptyMessage(DownloadListener.STATE_CANCEL);
        }
    }
    
    public static int connect(String url) {
        int size = 0;
        HttpURLConnection conn = null;
        try {
            conn = getConnection(new URL(url));
            conn.connect();
            if (200 == conn.getResponseCode()) {
                size = conn.getContentLength();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return size;
    }
    
    private void recoverCache() {
        mCache = new ConcurrentHashMap<Integer, Integer>(mThreadNum);
        
        // 检查数据库中是否有断点
        SparseIntArray points = mDBHelper.query(mUrl);
        if (0 == points.size()) {   // 数据库中尚没有历史记录 
            for (int i = 0; i < mThreadNum; i++) { 
                mCache.put(i, i * block);
            }
            mDBHelper.add(mUrl, mCache);
        } else {                    // 数据库有历史记录 
            for (int i = 0, n = points.size(); i < n; i ++) {
                mCache.put(i, points.get(i, i * block));
            }
        }
    }
    
    /**
     * 统计出：上次已下载的数据的长度（bytes）
     */
    private int getCurrSize() {
        int result = 0;
        // StringBuffer buf = new StringBuffer();
        for (int i = 0; i < mThreadNum; i++) {
            int download = mCache.get(i) - block * i;
            // buf.append("Thread[").append(i).append("].download=").append(download).append("; ");
            result += download;
        }
        // Log.d("l99", buf.toString());
        return result;
    }
    
    public static HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Referer", url.toString());
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Connection", "Keep-Alive");
        return conn;
    }
    
    public static void delete(Context context, String url) {
        new DownloadDBHelper(context).delete(url);
    }
}

class SingleDownloader extends Thread {
    /**
     * 缓冲区的大小
     */
    private static final int                          BUFFER_SIZE = 1024;
    
    /**
     * 控制该线程的循环的结束
     */
    private volatile boolean                          mIsLoop     = true;
    
    /**
     * 总共需要下载的size，相当于MultiDownLoader中的BlockSize
     */
    private final int                                 mPosEnd;
    
    /**
     * 当前数据块已下载的size
     */
    private int                                       mPosStart;
    
    /**
     * 线程的id：其实是当前数据块在所有的数据块中的index
     */
    private final int                                 mId;
    
    private final ConcurrentHashMap<Integer, Integer> mCache;
    
    private final String                              mUrl;
    
    private final File                                mFile;
    
    public SingleDownloader(int start, int end, int id, String url, File file,
            ConcurrentHashMap<Integer, Integer> cache, DownloadDBHelper helper) {
        mPosStart = start;
        mPosEnd = end;
        mId = id;
        mCache = cache;
        mUrl = url;
        mFile = file;
    }
    
    public void cancelDownload() {
        if (isAlive()) {
            mIsLoop = false;
            try {
                join(200);
            } catch (InterruptedException e) {
                Log.w("l99", e);
            }
        }
    }
    
    @Override
    public void run() {
        if (mPosStart < mPosEnd) {
            HttpURLConnection conn = null;
            InputStream is = null;
            RandomAccessFile raf = null;
            try {
                Log.i("l99", String.format("Thread[%d] start. start=%d, end=%d", mId, mPosStart, mPosEnd));
                
                conn = MultiDownloader.getConnection(new URL(mUrl));
                conn.setRequestProperty("Range", "bytes=" + mPosStart + "-" + mPosEnd);
                conn.connect();
                is = conn.getInputStream();
                
                byte[] buffer = new byte[BUFFER_SIZE];
                int offset = 0;
                
                raf = new RandomAccessFile(mFile, "rwd");
                raf.seek(mPosStart);
                
                while (mIsLoop && (offset = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    if (offset > 0) {
                        raf.write(buffer, 0, offset);
                        mPosStart += offset;
                        mCache.put(mId, mPosStart);
                    }
                }
                Log.i("l99", String.format("Thread[%d] end. start=%d, end=%d", mId, mPosStart, mPosEnd));
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("l99", e);
                mPosStart = -1;
            } finally {
                if (null != raf) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        Log.w("l99", e);
                    }
                }
                
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.w("l99", e);
                    }
                }
                
                if (null != conn) {
                    conn.disconnect();
                }
            }
        }
    }
}
