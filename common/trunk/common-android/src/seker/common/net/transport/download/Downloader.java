package seker.common.net.transport.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import seker.common.Utility;
import seker.common.net.HttpRequestInfo;
import seker.common.net.HttpRequester;
import seker.common.net.IResponseHandler.IResponseCallback;
import seker.common.net.ParamPair;
import seker.common.net.ResponseHandler;
import seker.common.net.transport.ITransport;
import seker.common.net.transport.ITransportCallback;
import seker.common.net.transport.TransportTaskInfo;
import seker.common.utils.StreamUtils;
import android.content.Context;
import android.text.TextUtils;

/**
 * Downloader类的对象，只能使用一次。
 * 
 * @author seker
 *
 */
public class Downloader implements ITransport {

    Context mContext;
    TransportTaskInfo mTransportTaskInfo;
    
    public Downloader(Context context, TransportTaskInfo info) {
        mContext = context.getApplicationContext();
        mTransportTaskInfo = info;
    }
    
    public void start(ITransportCallback tcallback) {
        switch (mTransportTaskInfo.getTransportMode()) {
        case STTMF:
            stdmf(tcallback);
            break;
        case MTTSF:
            mtdsf(tcallback);
            break;
        case MTTMF:
            mtdmf(tcallback);
            break;
        case STTSF:
        default:
            stdsf(tcallback);
            break;
        }
    }
    
    private void stdmf(final ITransportCallback tdcallback) {
    }
    
    private void mtdsf(final ITransportCallback tcallback) {
    }
    
    private void mtdmf(final ITransportCallback tcallback) {
    }
    
    private void stdsf(final ITransportCallback tcallback) {
        String url = mTransportTaskInfo.getUrls().get(0);
        String file = mTransportTaskInfo.getFiles().get(0);
        SingleDownloader downloader = new SingleDownloader(mContext, url, 0, 0, new File(file));
        downloader.start(tcallback);
    }
    
    public void pause() {
        throw new RuntimeException("Downloader.pause() is not implemented yet.");
    }
    
    public void resume() {
        throw new RuntimeException("Downloader.resume() is not implemented yet.");
    }
    
    public void cancel() {
        throw new RuntimeException("Downloader.cancel() is not implemented yet.");
    }
}

/**
 * SingleDownloader类的对象，只能使用一次。
 * 
 * @author seker
 *
 */
class SingleDownloader implements ITransport {
    /**
     * Stream buffer size.
     */
    public static final int STREAM_BUFFER_SIZE = 8192;
    
    long mStart;
    long mEnd;
    long mCurr;
    
    String mUrl;
    
    File mFile;
    
    Context mContext;
    
    public SingleDownloader(Context context, String url, long s, long e, File file) {
        this(context, url, s, s, e, file);
    }
    
    public SingleDownloader(Context context, String url, long s, long c, long e, File file) {
        mContext = context.getApplicationContext();
        
        mUrl = url;
        
        mStart = s;
        mEnd = e;
        mCurr = s;
        
        mFile = file;
    }
    
    public void start(final ITransportCallback tcallback) {
        Utility.newThread(new Runnable() {
            @Override
            public void run() {
                download(tcallback);
            }
        }, toString()).start();
    }
    
    public void download(final ITransportCallback tcallback) {
        HttpRequestInfo info = new HttpRequestInfo(mUrl, HttpRequestInfo.HTTP_GET);
        HttpRequester<InputStream> requester = new HttpRequester<InputStream>(mContext);
        List<ParamPair<?>> list = null;
        if (0 != mEnd) {
            list = new ArrayList<ParamPair<?>>(1);
            list.add(new ParamPair<String>("Range", "bytes=" + mCurr + "-" + mEnd));
        }
        IResponseCallback<InputStream> callback = new IResponseCallback<InputStream>() {
            @Override
            public void handleNetException() {
                tcallback.onError();
            }
            @Override
            public void handleNoResponse(List<ParamPair<String>> headers) {
                tcallback.onError();
            }
            @Override
            public void handleResponse(List<ParamPair<String>> headers, InputStream is) {
                if (0 == mEnd) {
                    if (null != headers && !headers.isEmpty()) {
                        for (ParamPair<String> header : headers) {
                            if (TextUtils.equals("content-length", header.getName())) {
                                mEnd = Long.parseLong(header.getValue());
                            }
                        }
                    }
                }
                if (mEnd < mCurr) {
                    handleNoResponse(headers);
                } else {
                    RandomAccessFile raf = null;
                    try {
                        raf = new RandomAccessFile(mFile, "rwd");
                        int offset = 0;
                        raf.seek(mCurr);
                        
                        byte[] buffer = new byte[STREAM_BUFFER_SIZE];
                        while ((offset = is.read(buffer, 0, STREAM_BUFFER_SIZE)) != -1) {
                            if (offset > 0) {
                                raf.write(buffer, 0, offset);
                                mCurr += offset;
                            }
                            tcallback.onProgress(mCurr - mStart, mEnd - mStart);
                        }
                        tcallback.onFinished();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        tcallback.onError();
                    } catch (IOException e) {
                        e.printStackTrace();
                        tcallback.onError();
                    } finally {
                        StreamUtils.closeSafely(raf);
                    }
                }
                StreamUtils.closeSafely(is);
            }
        };
        ResponseHandler<InputStream> handler = new ResponseHandler<InputStream>(info, callback);
        tcallback.onStart();
        requester.requestSync(info, list, null, handler);
    }
    
    public void pause() {
        throw new RuntimeException("SingleDownloader.pause() is not implemented yet.");
    }
    
    public void resume() {
        throw new RuntimeException("SingleDownloader.resume() is not implemented yet.");
    }
    
    public void cancel() {
        throw new RuntimeException("SingleDownloader.cancel() is not implemented yet.");
    }

    @Override
    public String toString() {
        return "SingleDownloader [mUrl=" + mUrl + ", mStart=" + mStart + ", mEnd=" + mEnd + ", mFile=" + mFile + "]";
    }
}
