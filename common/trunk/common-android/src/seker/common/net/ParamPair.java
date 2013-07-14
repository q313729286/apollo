/*
 * Copyright (C) 2011 Baidu Inc. All rights reserved.
 */
package seker.common.net;

import org.apache.http.NameValuePair;

/**
 * 网络请求{@link HttpRequester}参数，以键/值对的形式
 * 
 * @author liuxinjian
 * @since 2012-7-20
 * 
 * @param <V>
 *            "值"的数据类型
 */
public final class ParamPair<V> implements NameValuePair {

    /** 键 */
    public final String name;

    /** 值 */
    public final V value;

    /**
     * 构造方法
     * 
     * @param k
     *            键
     * @param v
     *            值
     */
    public ParamPair(String k, V v) {
        name = k;
        value = v;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    @Override
    public String toString() {
        return new StringBuilder(getName()).append('=').append(getValue()).toString();
    }
}
