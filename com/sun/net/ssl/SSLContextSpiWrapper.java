package com.sun.net.ssl;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.SSLContext;

final class SSLContextSpiWrapper extends SSLContextSpi
{
    private SSLContext theSSLContext;
    
    SSLContextSpiWrapper(final String s, final Provider provider) throws NoSuchAlgorithmException {
        this.theSSLContext = SSLContext.getInstance(s, provider);
    }
    
    @Override
    protected void engineInit(final KeyManager[] array, final TrustManager[] array2, final SecureRandom secureRandom) throws KeyManagementException {
        javax.net.ssl.KeyManager[] array3;
        if (array != null) {
            array3 = new javax.net.ssl.KeyManager[array.length];
            int i = 0;
            int n = 0;
            while (i < array.length) {
                if (!(array[i] instanceof javax.net.ssl.KeyManager)) {
                    if (array[i] instanceof X509KeyManager) {
                        array3[n] = new X509KeyManagerJavaxWrapper((X509KeyManager)array[i]);
                        ++n;
                    }
                }
                else {
                    array3[n] = (javax.net.ssl.KeyManager)array[i];
                    ++n;
                }
                ++i;
            }
            if (n != i) {
                array3 = (javax.net.ssl.KeyManager[])SSLSecurity.truncateArray(array3, new javax.net.ssl.KeyManager[n]);
            }
        }
        else {
            array3 = null;
        }
        javax.net.ssl.TrustManager[] array4;
        if (array2 != null) {
            array4 = new javax.net.ssl.TrustManager[array2.length];
            int j = 0;
            int n2 = 0;
            while (j < array2.length) {
                if (!(array2[j] instanceof javax.net.ssl.TrustManager)) {
                    if (array2[j] instanceof X509TrustManager) {
                        array4[n2] = new X509TrustManagerJavaxWrapper((X509TrustManager)array2[j]);
                        ++n2;
                    }
                }
                else {
                    array4[n2] = (javax.net.ssl.TrustManager)array2[j];
                    ++n2;
                }
                ++j;
            }
            if (n2 != j) {
                array4 = (javax.net.ssl.TrustManager[])SSLSecurity.truncateArray(array4, new javax.net.ssl.TrustManager[n2]);
            }
        }
        else {
            array4 = null;
        }
        this.theSSLContext.init(array3, array4, secureRandom);
    }
    
    @Override
    protected SSLSocketFactory engineGetSocketFactory() {
        return this.theSSLContext.getSocketFactory();
    }
    
    @Override
    protected SSLServerSocketFactory engineGetServerSocketFactory() {
        return this.theSSLContext.getServerSocketFactory();
    }
}
