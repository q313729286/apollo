/*
 * Copyright (C) 2011 Baidu Inc. All rights reserved.
 */
package seker.common.net;

/**
 * 网络HTTP请求结果的解析接口
 * 
 * 在发起HTTP请求时，必须要传入一个该接口的实现。各个不同的HTTP请求的返回结果，应该对应各自的Response
 * Parser，这样将网络请求和返回结果的解析解耦合。
 * 
 * @see com.baidu.searchbox.net.common.HttpRequester<R>.requestAsync()
 * @author liuxinjian
 * @since 2012-7-22
 * 
 * @param <T>
 *      HTTP请求返回的结果内容类型（如String类型）
 * @param <R>
 *      HTTP请求返回的结果解析后的数据Model类型
 */
public interface IResponseParser<T, R> {

    /**
     * 网络HTTP请求结果的处理回调
     * 
     * @param result
     *            网络HTTP请求的返回。
     * 
     * @return 网络HTTP请求解析数据的Model实例。如果解析失败，请返回null。
     */
    R parseResponse(T result);
}
