/*
 * Copyright (C) 2011 Baidu Inc. All rights reserved.
 */
package seker.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.protocol.HTTP;

import seker.common.BaseApplication;
import seker.common.Utility;
import seker.common.utils.StreamUtils;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 封装ProxyHttpClient类，该类的核心接口requestAsync()的主要功能为：
 * 1、异步化网络请求：requestAsync()方法体内，将启动一个子线程来发起网络请求。 
 * 2、接口参数化，以实现不同HTTP请求的扩展性。 
 * 3、解除
 *      a.网络请求 
 *      b.网络请求结果的解析 
 *      c.网络请求结果的处理（如：数据逻辑的处理、UI的处理）
 *  这三个子模块的耦合度。以实现网络请求功能的稳定性和可重用性。
 * 
 * @author liuxinjian
 * @since 2012-7-22
 * @param <R>
 *            HTTP请求结果所对应的数据类型(Response)，它将由IResponseParser解析，生成实例，
 *            然后传递给IResponseHandler处理。
 */
public class HttpRequester<R> {
    
    /** Log的TAG */
    private static final String TAG = "HttpRequester";
    
    /** Log开关 */
    public static final boolean DEBUG = BaseApplication.GLOBAL_LOG & true;
    
    /** File buffer stream size. */
    public static final int FILE_STREAM_BUFFER_SIZE = 8192;
    
    /** 运行上下文 */
    protected Context mContext;

    /**
     * 构造方法
     * 
     * @param context
     *            程序运行上下文
     */
    public HttpRequester(Context context) {
        mContext = context;
    }

    /**
     * 同步的发起HTTP请求。耗时操作， 不要在UI线程上调用它。
     * 
     * @param info
     *            HTTP请求信息，如：url、http请求类型、超时时长等
     * 
     * @param params
     *            该HTTP请求的参数
     *            
     * @param parser
     *            网络请求结果的解析器
     *            
     * @param handler
     *            网络请求结果的处理器
     *           
     * @param threadHanlder
     *            发起网络请求的原始线程的Handler
     */
    private void request(HttpRequestInfo info, List<ParamPair<?>> params, IResponseParser<InputStream, R> parser, 
            IResponseHandler<R> handler, RequestThreadHanlder<R> threadHanlder) {
        byte status = IResponseHandler.STATUS_EXCEPTION;
        InputStream result = null;
        List<ParamPair<String>> responseHeaders = null;
        ProxyHttpClient client = null;
        try {
            // 定义HTTP请求
            HttpUriRequest request = null;
            String url = info.url();
            if (DEBUG) {
                Log.d(TAG, "Org url:" + url);
            }
            switch (info.type()) {
            case HttpRequestInfo.HTTP_PUT:
                HttpPut httpput = new HttpPut(url);
                httpput.setEntity(processHttpPutParams(params));
                request = httpput;
                break;
            case HttpRequestInfo.HTTP_POST: 
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(processHttpPostParams(params));
                request = httppost;
                break;
            case HttpRequestInfo.HTTP_GET:
                url = processHttpGetParams(url, params);
                HttpGet httpget = new HttpGet(url);
                request = httpget;
                break;
            default:
                throw new RuntimeException("Only support the HTTP_POST & HTTP_GET & HTTP_PUT request now.");
            }

            if (DEBUG) {
                Log.d(TAG, "Des url:" + url);
                StringBuilder builder = new StringBuilder("params: ");
                if (null != params && !params.isEmpty()) {
                    for (ParamPair<?> param : params) {
                        builder.append(param.toString()).append(", ");
                    }
                    builder.delete(builder.length() - 2, builder.length());
                } else {
                    builder.append("null");
                }
                Log.d(TAG, builder.toString());
            }

            // 客户端不要请求"gzip"压缩，因为服务端不一定支持gzip
            // request.setHeader("Accept-Encoding", "gzip");

            List<ParamPair<?>> headers = info.getHeaders();
            if (null != headers && !headers.isEmpty()) {
                if (DEBUG) {
                    StringBuilder builder = new StringBuilder("headers: ");
                    for (ParamPair<?> header : headers) {
                        builder.append(header.toString()).append(", ");
                    }
                    builder.delete(builder.length() - 2, builder.length());
                    Log.d(TAG, builder.toString());
                }
                
                for (ParamPair<?> header : headers) {
                    request.addHeader(header.getName(), header.getValue());
                }
            }
            
            // 执行请求
            client = Utility.createHttpClient(mContext);

            ConnManagerParams.setTimeout(client.getParams(), info.time());
        
            /**
             * 为了解决新浪短网址服务的HTTPS请求，No peer certificate的问题， 客户端需要信任新浪服务端的证书。
             * 该处仅为解决No peer certificate的问题而做出的妥协。
             */
            if (url.startsWith(HttpRequestInfo.PREFIX_HTTPS)) {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);  
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                Scheme https = new Scheme(HttpRequestInfo.PREFIX_HTTPS, sf, HttpRequestInfo.PORT_HTTPS);
                client.getConnectionManager().getSchemeRegistry().register(https);
            }
            
            HttpResponse response = client.executeSafely(request);
            if (null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                status = IResponseHandler.STATUS_SUCCESS;
                
                Header[] hs = response.getAllHeaders();
                if (null != hs && hs.length > 0) {
                    responseHeaders = new ArrayList<ParamPair<String>>(hs.length);
                    for (Header header : hs) {
                        responseHeaders.add(new ParamPair<String>(header.getName(), header.getValue()));
                    }
                }
                
                HttpEntity resEntity = response.getEntity();
                result = getInputStream(resEntity);
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } finally {
            // 这里可能存在异常，为了保证HttpClient一定能够关闭，所以要catch异常。
            try {
                processHttpResponse(info, parser, handler, threadHanlder, status, responseHeaders, result);
            } catch (Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
            
            if (null != client) {
                client.close();
            }
        }
    }
    
    /**
     * 拼接HttpGet请求的参数
     * 
     * @param url       IHttpRequestInfo提供的url
     * @param params    参数
     * @return          处理后的url
     */
    protected String processHttpGetParams(String url, List<ParamPair<?>> params) {
        if (null != params && !params.isEmpty()) {
            StringBuilder builder = new StringBuilder(url);
            if (!url.contains("?")) {
                ParamPair<?> param = params.remove(0);
                try {
                    builder
                    .append('?')
                    .append(URLEncoder.encode(param.getName(), HTTP.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(param.getValue(), HTTP.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            for (ParamPair<?> param : params) {
                try {
                    builder
                    .append('&')
                    .append(URLEncoder.encode(param.getName(), HTTP.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(param.getValue(), HTTP.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            url = builder.toString();
        }
        return url;
    }
    
    /**
     * 处理HttPost请求的参数
     * 
     * @param params    参数
     * @return          处理后的UrlEncodedFormEntity
     */
    protected HttpEntity processHttpPostParams(List<ParamPair<?>> params) {
        HttpEntity entity = null;
        if (null != params && !params.isEmpty()) {
            try {
                entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }
    
    /**
     * 处理HttPut请求的参数
     * 
     * @param params    参数
     * @return          处理后的UrlEncodedFormEntity
     */
    protected HttpEntity processHttpPutParams(List<ParamPair<?>> params) {
        HttpEntity entity = null;
        if (null != params && !params.isEmpty()) {
            try {
                entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }
    
    /**
     * 同步的发起HTTP请求。耗时操作，建议不要在UI线程上调用它。
     * 
     * @param info
     *            HTTP请求信息，如：url、http请求类型、超时时长等
     * 
     * @param parser
     *            网络请求结果的解析器
     *            
     * @param handler
     *            网络请求结果的处理器
     *            
     * @param threadHanlder
     *            发起网络请求的原始线程的Handler
     *
     * @param status 
     *            返回结果状态：STATUS_SUCCESS或者STATUS_EXCEPTION
     *            
     * @param headers
     *            HTTP返回头信息
     *          
     * @param is 
     *            返回结果:Stream类型
     */
    @SuppressWarnings("unchecked")
    protected void processHttpResponse(HttpRequestInfo info, IResponseParser<InputStream, R> parser, 
            IResponseHandler<R> handler, RequestThreadHanlder<R> threadHanlder, byte status, 
            List<ParamPair<String>> headers, InputStream is) {
        // 解析返回结果
        R response = null;
        if (null == is) {
            if (DEBUG) {
                Log.d(TAG, "(0) result=null");
            }
        } else {
            if (DEBUG && !info.url.endsWith(".zip")) {
                String s = StreamUtils.streamToString(is);
                Log.d(TAG, "(1) result=" + s);
                is = new java.io.ByteArrayInputStream(s.getBytes());
            }
            if (null == parser) {
                if (DEBUG) {
                    Log.d(TAG, "parser=null");
                }
                try {
                    response = (R) is;
                } catch (Exception e) {
                    if (DEBUG) {
                        Log.e(TAG, "InputStream can't cast class to R");
                        e.printStackTrace();
                    }
                }
            } else {
                response = parser.parseResponse(is);
                if (DEBUG) {
                    Log.d(TAG, "(response = mParser.parseResponse(result)) = "
                            + ((null == response) ? "null" : response.toString()));
                }
            }
        }

        if (null != threadHanlder) {    // 将结果发送给父进程处理
            Message msg = Message.obtain(threadHanlder, 0, new ResponseData<R>(status, headers, response, is));
            threadHanlder.sendMessage(msg);
        } else if (null != handler) { // 处理返回结果
            if (DEBUG) {
                Log.d(TAG, "handleMessage(info=" + info + ", status=" + status + ", response=" + response + ")");
            }
            handler.onResult(info, status, headers, response);
            StreamUtils.closeSafely(is);
        } else {
            StreamUtils.closeSafely(is);
        }
    }

    /**
     * server如果下发为gip，则获取gzip inputstream .
     * 
     * @param resEntity
     *            {@link HttpEntity}
     * @return InputStream or GZIPInputStream or null
     * @throws IllegalStateException 
     *             {@link IllegalStateException}
     * @throws IOException
     *             {@link IOException}
     */
    protected InputStream getInputStream(HttpEntity resEntity) throws IllegalStateException, IOException {
        InputStream result = null;
        
        Header header = resEntity.getContentEncoding();
        if (header != null) {
            String contentEncoding = header.getValue();
            if (contentEncoding.toLowerCase().indexOf("gzip") != -1) {
                try {
                    result = new GZIPInputStream(resEntity.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (null == result) {
            result = resEntity.getContent();
        }
        
        return result;
    }
    
    /**
     * 网络HTTP请求异步化，这个函数调用后会立即返回，它会启动一个子线程去执行耗时的网络操作。
     * 注意：这个方法内会创建{@link android.os.Hanlder},因此调用线程必须保证本身已经初始化了消息队列(Loop.prepare())
     * 
     * @param info
     *            HTTP请求信息，如：url、http请求类型、超时时长等
     * 
     * @param list
     *            该HTTP请求的参数
     * 
     * @param parser
     *            该HTTP请求结果的解析器，返回结果回调给调用者自行解析
     * 
     * @param handler
     *            该HTTP请求结果的处理器，返回结果回调给调用者处理
     */
    public void requestAsync(final HttpRequestInfo info, final List<ParamPair<?>> list,
            final IResponseParser<InputStream, R> parser, final IResponseHandler<R> handler) {
        final RequestThreadHanlder<R> threadHanlder = new RequestThreadHanlder<R>(info, handler);
        Utility.newThread(new Runnable() {
            @Override
            public void run() {
                request(info, list, parser, null, threadHanlder);
            }
        }, "HttpRequester: " + info.url()).start();
    }
    
    /**
     * 网络HTTP请求同步化
     * @param info
     *            HTTP请求信息，如：url、http请求类型、超时时长等
     * 
     * @param list
     *            该HTTP请求的参数
     * 
     * @param parser
     *            该HTTP请求结果的解析器，返回结果回调给调用者自行解析
     * 
     * @param handler
     *            该HTTP请求结果的处理器，返回结果回调给调用者处理
     */
    public void requestSync(final HttpRequestInfo info, final List<ParamPair<?>> list,
            final IResponseParser<InputStream, R> parser, final IResponseHandler<R> handler) {
        request(info, list, parser, handler, null);
    }
    
    /**
     * 由于使用AsyncTask，在网络不佳的情况下会阻塞浏览器模块的AsyncTask的执行，所以改用Thread + Handler的方式来替代AsyncTask
     * 
     * @author liuxinjian
     * @since 2012-8-20
     */
    static class RequestThreadHanlder<R> extends Handler {
        /**
         * 该HTTP请求结果的处理器，返回结果回调给调用者处理（它会在UI线程上被invoke）
         */
        private final IResponseHandler<R> mHandler;
        
        /**
         * HTTP请求信息，如：url、http请求类型、超时时长等
         */
        private final HttpRequestInfo mInfo;
        
        /**
         * 构造方法
         * 
         * @param info      HTTP请求信息，如：url、http请求类型、超时时长等
         * @param handler   该HTTP请求结果的处理器，返回结果回调给调用者处理
         */
        public RequestThreadHanlder(HttpRequestInfo info, IResponseHandler<R> handler) {
            mHandler = handler;
            mInfo = info;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // 处理返回结果
            if (null != mHandler) {
                ResponseData<R> data = (ResponseData<R>) msg.obj;
                if (null != data) {
                    if (DEBUG) {
                        Log.d(TAG, "handleMessage(info=" + mInfo + "data=" + data.toString() + ")");
                    }
                    mHandler.onResult(mInfo, data.status, data.headers, data.response);
                    StreamUtils.closeSafely(data.is);
                } else {
                    if (DEBUG) {
                        Log.e(TAG, "handleMessage(ResponseData<R> data == null)");
                    }
                }
            } else {
                if (DEBUG) {
                    Log.e(TAG, "handleMessage(mHandler == null)");
                }
            }
        }
    }
    
    /**
     * HTTP请求的返回数据：状态；返回Header头；Response
     * 
     * @author liuxinjian
     * @since 2013-6-3
     * @param <R> HTTP请求结果所对应的数据类型(Response)
     */
    static class ResponseData<R> {
        /** 状态:STATUS_SUCCESS或者STATUS_EXCEPTION */
        byte status;
        
        /** 返回Header头 */
        List<ParamPair<String>> headers;
        
        /** HTTP请求结果所对应的数据类型(Response) */
        R response;
        
        /** HTTP请求结果数据流 */
        InputStream is;

        /**
         * 构造方法
         * @param sta       状态:STATUS_SUCCESS或者STATUS_EXCEPTION
         * @param hds       返回Header头
         * @param r         HTTP请求结果所对应的数据类型(Response)
         */
        public ResponseData(byte sta, List<ParamPair<String>> hds, R r, InputStream s) {
            status = sta;
            headers = hds;
            response = r;
            is = s;
        }

        @Override
        public String toString() {
            StringBuilder strHeaders = null;
            if (null != headers && !headers.isEmpty()) {
                strHeaders = new StringBuilder("{");
                for (ParamPair<String> header : headers) {
                    strHeaders.append("[").append(header).append("],");
                }
                strHeaders.deleteCharAt(strHeaders.length() - 1);
                strHeaders.append("}");
            }
            return "ResponseData [status=" + status + ", headers=" + strHeaders + ", response=" + response + "]";
        }
    }
}
