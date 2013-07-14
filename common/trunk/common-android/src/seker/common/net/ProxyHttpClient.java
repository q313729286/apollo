package seker.common.net;


import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 提供一个anroid平台自动填充apn proxy的 HttpClient.
 * 
 * 自动填充 proxy 设置，不需要使用者再次关心wap net情况。
 */
public class ProxyHttpClient extends DefaultHttpClient {
    
    /** log tag. */
    private static final String TAG = ProxyHttpClient.class.getSimpleName();
    
    /** log debug on/off .*/
    private static final boolean DEBUG = false;
    
    /** apn proxy. */
    private String mProxy;
    
    /** proxy port. */
    private String mPort;
    
    /** 当前网络是否使用wap。
     *  wifi 或者 cmnet等为不使用 wap.
     */
    private boolean mUseWap;
    
    /** http 超时。 */
    private static final int HTTP_TIMEOUT_MS = 30000;

    /** 用于检查是否发生没有释放(close)的情况. */
    private RuntimeException mLeakedException = new IllegalStateException(
                                                        "ProxyHttpClient created and never closed");
    
    /**
     * constructor.
     * @param context  context
     */
    public ProxyHttpClient(Context context) {
        this(context, null, null);
    }
    
    /**
     * 构造函数.
     * @param context application context
     * @param userAgent  useragent you want to set 
     */
    public ProxyHttpClient(Context context, String userAgent) {
        this(context, userAgent, null);
    }
    
    /**
     * 构造函数.
     * @param context application context
     * @param connectManager ConnectManager
     */
    public ProxyHttpClient(Context context, ConnectManager connectManager) {
        this(context, null, connectManager);
    }
    
    /**
     * 构造函数.
     * @param context application context
     * @param userAgent  useragent you want to set 
     * @param connectManager ConnectManager
     */
    public ProxyHttpClient(Context context, String userAgent, ConnectManager connectManager) {
        
        // 检查网络类型 wifi /mobile & apn proxy
        ConnectManager cm = connectManager;
        if (cm == null) {
            cm = new ConnectManager(context);
        }
        
        mUseWap = cm.isWapNetwork();
        mProxy = cm.getProxy();
        mPort = cm.getProxyPort();
        
        if (DEBUG) {
            Log.d(TAG, "wap :" + mUseWap + " " + mProxy + " " + mPort);
        }
        
        if (mProxy != null && mProxy.length() > 0) {
            
            HttpHost proxy = new HttpHost(mProxy, Integer.valueOf(mPort));
            getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
        }

        // Default connection and socket timeout of 30 seconds. 
        HttpConnectionParams.setConnectionTimeout(getParams(), HTTP_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(getParams(), HTTP_TIMEOUT_MS);
        /*
         * http://stackoverflow.com/questions/5358014/android-httpclient-oom-on-4g-lte-htc-thunderbolt
         * https://android-review.googlesource.com/#/c/22852/
         * 解决部分手机(android 2.x 以下) httpClient OOM 问题
         */
        HttpConnectionParams.setSocketBufferSize(getParams(), 8192); // SUPPRESS CHECKSTYLE
        
        if (!TextUtils.isEmpty(userAgent)) {
            HttpProtocolParams.setUserAgent(getParams(), userAgent);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mLeakedException != null) {
            Log.e(TAG, "Leak found", mLeakedException);
        }
    }
    
    /**
     * Release resources associated with this client.  You must call this,
     * or significant resources (sockets and memory) may be leaked.
     */
    public void close() {
        if (mLeakedException != null) {
            getConnectionManager().shutdown();
            mLeakedException = null;
        }
    }

    /**
     * 获取是否当前网络是wap网络。
     * 
     * @return true 表示是wap网络，false表示不是
     */
    public boolean isWap() {
        return mUseWap;
    }

    @Override
    protected HttpParams createHttpParams() {
        HttpParams params = super.createHttpParams();
        // 禁用 Expect: 100-continue。联通 uniwap网关不支持，post数据发生错误。
        HttpProtocolParams.setUseExpectContinue(params, false);
        return params;
    }
    
    /**
     * 替换{@link #execute(HttpUriRequest)} 可以解决2.3系统上访问网关时，崩溃的问题<br>
     * 要处理空指针异常的情况。
     * 注意：不能直接使用{@link #execute(HttpUriRequest)}
     * 参考： 关于HttpClient的NullPointer https://code.google.com/p/android/issues/detail?id=5255
     * @param request request
     * @throws ClientProtocolException ClientProtocolException
     * @throws IOException IOException
     * @return HttpResponse
     */
    public HttpResponse executeSafely(HttpUriRequest request)
            throws ClientProtocolException, IOException {
        try {
            return execute(request);
        } catch (NullPointerException e) {
            throw new ClientProtocolException(e);
        }
    }
}
