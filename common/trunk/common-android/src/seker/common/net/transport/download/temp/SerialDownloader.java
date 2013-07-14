/**
 * 
 */

package seker.common.net.transport.download.temp;

import java.util.ArrayList;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 游戏部分动态资源加载
 * 
 * @author Lifeix
 * 
 */
public class SerialDownloader {
    // 下载超时时间
    private final int          TIME_OUT   = 15 * 1000;
    
    // 最少等待时间（进度dailog显示的最短时间）
    private final int          TIME_LIMIT = 2 * 1000;
    
    // 需要下载的url列表
    private ArrayList<String>  mUrls;
    
    private final DownloadHandler    mDownloadHandler;
    
    private DownloadListener   mListener;
    
    private int                mTotal;
    
    /**
     * UI线程里面invoke DownloadListener的回调
     */
    class DownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int curr = mTotal - mUrls.size();
            if (null != mListener) {
                switch (msg.what) {
                    case DownloadListener.STATE_START: {
                        mListener.onDownloadStart(mTotal, curr);
                        break;
                    }
                    case DownloadListener.STATE_PROCESSING: {
                        mListener.onDownloadProgress(mTotal, curr);
                        break;
                    }
                    case DownloadListener.STATE_END: {
                        mListener.onDownloadEnd(mTotal, curr);
                        break;
                    }
                    case DownloadListener.STATE_ERROR: {
                        mListener.onDownloadError(mTotal, curr);
                        break;
                    }
                    case DownloadListener.STATE_CANCEL: {
                        mListener.onDownloadCancel(mTotal, curr);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param listener
     *            监听器
     * @param min
     *            指定最少完成所需时间，以供前台显示进度条
     */
    public SerialDownloader() {
        mDownloadHandler = new DownloadHandler();
    }
    
    public void startDownload(DownloadListener listener, final String dir, final String... urls) {
        mListener = listener;
        mTotal = urls.length;
        
        if (null != mUrls) {
            mUrls.clear();
            mUrls = null;
        }
        mUrls = new ArrayList<String>(urls.length);
        for (String url : urls) {
            mUrls.add(url);
        }
        
        Thread deamon = new Thread(new Runnable() {
            @Override
            public void run() {
                mDownloadHandler.sendEmptyMessage(DownloadListener.STATE_START);
                
                final long startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(TIME_LIMIT);
                } catch (InterruptedException e) {
                    Log.w("l99", e);
                }
                
                long passed = 0;
                while (true) {  // 检查下载是否完成
                    passed = System.currentTimeMillis() - startTime;
                    if (mUrls.isEmpty()) {
                        mDownloadHandler.sendEmptyMessage(DownloadListener.STATE_END);
                        break;
                    } else if (passed > TIME_OUT) {
                        mDownloadHandler.sendEmptyMessage(DownloadListener.STATE_ERROR);
                        break;
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Log.w("l99", e);
                        }
                    }
                }
            }
        });
        deamon.setDaemon(true);
        deamon.start();
    }
}
