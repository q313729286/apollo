/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package seker.common.net;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 通用的Response类型的处理类
 * 
 * @author liuxinjian
 * @since 2013-4-26
 * @param <R> Response类型的返回结果
 */
public class ResponseHandler<R> implements IResponseHandler<R> {
    
    /** HTTP请求的基本信息，如：url、http请求类型、超时时长等。 */
    protected final HttpRequestInfo mInfo;

    /** 所有Command的回调器：使用action来映射 */
    private HashSet<IResponseCallback<R>> mCallbacks = new HashSet<IResponseCallback<R>>();

    /**
     * 构造方法
     * 
     * @param info     HTTP请求的基本信息，如：url、http请求类型、超时时长等。
     * @param callback  默认的监听回调(不能为空)
     */
    public ResponseHandler(HttpRequestInfo info, IResponseCallback<R> callback) {
        mInfo = info;
        if (null != callback) {
            register(callback);
        }
    }

    /**
     * 注册新的监听器
     * 
     * @param callback  监听回调
     * @return          是否注册成功
     */
    public boolean register(IResponseCallback<R> callback) {
        return mCallbacks.add(callback);
    }

    /**
     * 注消新的监听器
     * 
     * @param callback  监听回调
     * @return          是否注消成功
     */
    public boolean unregister(IResponseCallback<R> callback) {
        return mCallbacks.remove(callback);
    }

    @Override
    public void onResult(HttpRequestInfo info, byte status, List<ParamPair<String>> headers, R response) {
        if (mInfo.equals(info)) {
            switch (status) {
            case STATUS_SUCCESS:
                if (null != response) {
                    handleResponse(headers, response);
                } else {
                    handleNoResponse(headers);
                }
                break;
            case STATUS_EXCEPTION:
            default:
                handleNetException();
                break;
            }
        }
    }

    /**
     * 处理“网络异常”的结果
     */
    protected void handleNetException() {
        Iterator<IResponseCallback<R>> iterator = mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().handleNetException();
        }
    }

    /**
     * 处理“没有返回Response”的结果
     * 
     * @param headers
     *            HTTP返回头信息
     */
    protected void handleNoResponse(List<ParamPair<String>> headers) {
        Iterator<IResponseCallback<R>> iterator = mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().handleNoResponse(headers);
        }
    }

    /**
     * 处理“有返回Response”的结果
     * 
     * @param headers
     *            HTTP返回头信息
     *            
     * @param response  Response
     */
    protected void handleResponse(List<ParamPair<String>> headers, R response) {
        Iterator<IResponseCallback<R>> iterator = mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().handleResponse(headers, response);
        }
    }
}
