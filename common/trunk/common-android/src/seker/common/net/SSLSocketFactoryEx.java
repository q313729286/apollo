/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package seker.common.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * 为了解决新浪短网址服务的HTTPS请求，No peer certificate的问题， 客户端需要信任新浪服务端的证书。
 * 该类仅为解决No peer certificate的问题而做出的妥协。
 * 该类的源代码: http://blog.csdn.net/binyao02123202/article/details/7697462
 * 
 * @author liuxinjian
 * @since 2013-1-26
 */
class SSLSocketFactoryEx extends SSLSocketFactory {

    /** HTTPS请求的SSL上下文 */
    SSLContext sslContext = SSLContext.getInstance("TLS");

    /**
     * 构造方法
     * 
     * @param truststore                    信任一切证书
     * @throws NoSuchAlgorithmException     NoSuchAlgorithmException
     * @throws KeyManagementException       KeyManagementException
     * @throws KeyStoreException            KeyStoreException
     * @throws UnrecoverableKeyException    UnrecoverableKeyException
     */
    public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        TrustManager tm = new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
            }
        };
        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
            UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
