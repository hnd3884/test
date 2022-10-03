package org.apache.tomcat.util.net;

import java.security.cert.X509Certificate;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;

public interface SSLContext
{
    void init(final KeyManager[] p0, final TrustManager[] p1, final SecureRandom p2) throws KeyManagementException;
    
    void destroy();
    
    SSLSessionContext getServerSessionContext();
    
    SSLEngine createSSLEngine();
    
    SSLServerSocketFactory getServerSocketFactory();
    
    SSLParameters getSupportedSSLParameters();
    
    X509Certificate[] getCertificateChain(final String p0);
    
    X509Certificate[] getAcceptedIssuers();
}
