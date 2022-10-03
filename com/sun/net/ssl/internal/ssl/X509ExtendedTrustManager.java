package com.sun.net.ssl.internal.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public abstract class X509ExtendedTrustManager implements X509TrustManager
{
    protected X509ExtendedTrustManager() {
    }
    
    public abstract void checkClientTrusted(final X509Certificate[] p0, final String p1, final String p2, final String p3) throws CertificateException;
    
    public abstract void checkServerTrusted(final X509Certificate[] p0, final String p1, final String p2, final String p3) throws CertificateException;
}
