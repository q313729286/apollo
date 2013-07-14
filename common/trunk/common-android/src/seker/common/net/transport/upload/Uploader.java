package seker.common.net.transport.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

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

/**
 * Uploader类的对象，只能使用一次。
 * 
 * @author seker
 * 
 */
public class Uploader implements ITransport {

    Context mContext;

    TransportTaskInfo mTransportTaskInfo;

    public Uploader(Context context, TransportTaskInfo info) {
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

    private void stdmf(final ITransportCallback tcallback) {
    }

    private void mtdsf(final ITransportCallback tcallback) {
    }

    private void mtdmf(final ITransportCallback tcallback) {
    }

    private void stdsf(final ITransportCallback tcallback) {
        String key = mTransportTaskInfo.getKeys().get(0);
        String url = mTransportTaskInfo.getUrls().get(0);
        String file = mTransportTaskInfo.getFiles().get(0);
        SingleUploader uploader = new SingleUploader(mContext, url, 0, 0, key, new File(file));
        uploader.start(tcallback);
    }

    public void pause() {
        throw new RuntimeException("Uploader.pause() is not implemented yet.");
    }

    public void resume() {
        throw new RuntimeException("Uploader.resume() is not implemented yet.");
    }

    public void cancel() {
        throw new RuntimeException("Uploader.cancel() is not implemented yet.");
    }
}

/**
 * SingleUploader类的对象，只能使用一次。
 * 
 * @author seker
 * 
 */
class SingleUploader implements ITransport {
    /**
     * Stream buffer size.
     */
    public static final int STREAM_BUFFER_SIZE = 8192;

    long mStart;
    long mEnd;

    String mUrl;

    String mKey;
    File mFile;

    Context mContext;

    public SingleUploader(Context context, String url, long s, long e, String key, File file) {
        mContext = context.getApplicationContext();

        mUrl = url;

        mStart = s;
        mEnd = e;
        if (mEnd < mStart) {
            throw new RuntimeException("SingleUploader(): mEnd < mStart");
        }

        mKey = key;
        mFile = file;
    }

    public void start(final ITransportCallback tcallback) {
        Utility.newThread(new Runnable() {
            @Override
            public void run() {
                upload(tcallback);
            }
        }, toString()).start();
    }

    public void upload(final ITransportCallback tcallback) {
        HttpRequestInfo info = new HttpRequestInfo(mUrl, HttpRequestInfo.HTTP_PUT);
        HttpRequester<InputStream> requester = new HttpRequester<InputStream>(mContext) {
            @Override
            protected HttpEntity processHttpPutParams(List<ParamPair<?>> params) {
                MultipartEntity entity = null;

                RandomAccessFile raf = null;
                byte[] data = null;
                try {
                    raf = new RandomAccessFile(mFile, "rwd");
                    if (0 == mEnd) {
                        mEnd = raf.length();
                    }
                    
                    raf.seek(mStart);
                    data = new byte[(int) (mEnd - mStart)];
                    raf.read(data);

                    entity = new MultipartEntity();
                    ByteArrayBody bytesBody = new ByteArrayBody(data, mFile.getAbsolutePath());
                    entity.addPart(mKey, bytesBody);
                    if (null != params && !params.isEmpty()) {
                        for (ParamPair<?> param : params) {
                            try {
                                StringBody body = new StringBody(param.value.toString(), Charset.forName(HTTP.UTF_8));
                                entity.addPart(param.name, body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    tcallback.onError();
                } catch (IOException e) {
                    e.printStackTrace();
                    tcallback.onError();
                } finally {
                    StreamUtils.closeSafely(raf);
                }
                return entity;
            }
        };

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
                StreamUtils.closeSafely(is);
                tcallback.onFinished();
            }
        };
        ResponseHandler<InputStream> handler = new ResponseHandler<InputStream>(info, callback);
        tcallback.onStart();
        requester.requestSync(info, null, null, handler);
    }

    public void pause() {
        throw new RuntimeException("SingleUploader.pause() is not implemented yet.");
    }

    public void resume() {
        throw new RuntimeException("SingleUploader.resume() is not implemented yet.");
    }

    public void cancel() {
        throw new RuntimeException("SingleUploader.cancel() is not implemented yet.");
    }

    @Override
    public String toString() {
        return "SingleUploader [mUrl=" + mUrl + ", mStart=" + mStart + ", mEnd=" + mEnd + ", mFile=" + mFile + "]";
    }
}
