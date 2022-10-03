package com.sun.net.ssl;

import java.security.cert.X509Certificate;

@Deprecated
public interface X509TrustManager extends TrustManager
{
    boolean isClientTrusted(final X509Certificate[] p0);
    
    boolean isServerTrusted(final X509Certificate[] p0);
    
    X509Certificate[] getAcceptedIssuers();
}
