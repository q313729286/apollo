/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package seker.common.net;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * HTTP请求信息，如：url、http请求类型、超时时长等
 * 
 * @author liuxinjian
 * @since 2013-5-8
 */
public class HttpRequestInfo {

    /** 
     * HTTPS协议的URL前缀
     */
    public static final String PREFIX_HTTPS = "https";
    
    /** 
     * HTTP协议的URL前缀 
     */
    public static final String PREFIX_HTTP = "http";
    
    /**
     * HTTPS协议的默认端口
     */
    public static final int PORT_HTTPS = 443;

    /**
     * HTTP协议的默认端口
     */
    public static final int PORT_HTTP = 80;

    /**
     * HTTP请求认证方式：未知认证，将导致程序抛出RuntimeException
     */
    public static final byte AUTH_UNKNOWN = 0;

    /**
     * HTTP请求认证方式：不认证
     */
    public static final byte AUTH_NONE = 1;

    /**
     * HTTP请求认证方式：basic认证
     */
    public static final byte AUTH_BASIC = 2;

    /**
     * HTTP请求类型：未知的请求类型，将导致程序抛出RuntimeException
     */
    public static final byte HTTP_UNKNOWN = 0;

    /**
     * HTTP请求类型：Get请求
     */
    public static final byte HTTP_GET = 1;

    /**
     * HTTP请求类型：Post请求
     */
    public static final byte HTTP_POST = 2;

    /**
     * HTTP请求类型：Put请求
     */
    public static final byte HTTP_PUT = 3;
    /**
     * 默认的HTTP链接时长
     */
    public static final int TIME_OUT = 30 * 1000;

    /** URL */
    final String url;

    /** HTTP请求类型{@link HTTP_GET}或{@link HTTP_POST} */
    final byte type;

    /** HTTP请求时长 */
    final int time;

    /** HTTP请求验证(暂未使用到){@link AUTH_BASIC}或{@link AUTH_NONE} */
    final byte auth;
    
    /** HTTP请求请求头:请求头是预先规定的协议，而不是运行时规定的，所以放到了HttpRequestInfo中 */
    private List<ParamPair<?>> headers;

    /**
     * 构造方法
     * 
     * @param u   URL
     * @param ty  HTTP请求类型{@link HTTP_GET}或{@link HTTP_POST}
     */
    public HttpRequestInfo(String u, byte ty) {
        this(u, ty, TIME_OUT);
    }

    /**
     * 构造方法
     * 
     * @param u   URL
     * @param ty  HTTP请求类型{@link HTTP_GET}或{@link HTTP_POST}
     * @param ti  HTTP请求时长
     */
    public HttpRequestInfo(String u, byte ty, int ti) {
        this(u, ty, ti, AUTH_NONE);
    }

    /**
     * 构造方法
     * 
     * @param u   URL
     * @param ty  HTTP请求类型{@link HTTP_GET}或{@link HTTP_POST}
     * @param ti  HTTP请求时长
     * @param a   认证方式（暂时未使用）
     */
    public HttpRequestInfo(String u, byte ty, int ti, byte a) {
        this.url = u;
        this.type = ty;
        this.time = ti;
        this.auth = AUTH_NONE;
    }

    /**
     * 根据HTTP请求url
     * 
     * @return url字符串
     */
    public String url() {
        return url;
    }

    /**
     * HTTP请求类型
     * 
     * @return 该HTTP的请求类型 {@link HTTP_GET}, {@link HTTP_POST}
     */
    public byte type() {
        return type;
    }

    /**
     * 根据HTTP请求认证类型{@link AUTH_BASIC}
     * 
     * @return 该HTTP的认证类型
     */
    public byte auth() {
        return auth;
    }

    /**
     * HTTP链接的时长
     * 
     * @return HTTP链接的时长
     */
    public int time() {
        return time;
    }
    
    /**
     * 得到该Http Request请求的请求头
     * @return  Http Request请求的请求头
     */
    public List<ParamPair<?>> getHeaders() {
        return headers;
    }
    
    /**
     * 添加Http Request请求的请求头
     * @param <T>       请求头的Value类型也不确定，暂时写成泛型类型
     * @param key       请求头的Key
     * @param value     请求头的Value
     */
    public <T> void addHeader(String key, T value) {
        if (null == headers) {
            headers = new ArrayList<ParamPair<?>>();
        }
        for (ParamPair<?> header : headers) {
            if (TextUtils.equals(header.getName(), key)) {
                headers.remove(header);
                break;
            }
        }
        headers.add(new ParamPair<T>(key, value));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof HttpRequestInfo) {
            HttpRequestInfo info = (HttpRequestInfo) obj;
            return this == info 
                    || (url.equals(info.url()) && type == info.type() && time == info.time() && auth == info.auth());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
    	return url.hashCode() + type + time + auth;
    }
    
    @Override
    public String toString() {
        return "{url=" + url + ", type=" + type + ", time=" + time + ", auth=" + auth + "}";
    }
}
