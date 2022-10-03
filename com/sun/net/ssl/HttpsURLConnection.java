package com.sun.net.ssl;

import java.security.Permission;
import javax.security.cert.X509Certificate;
import java.io.IOException;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;

@Deprecated
public abstract class HttpsURLConnection extends HttpURLConnection
{
    private static HostnameVerifier defaultHostnameVerifier;
    protected HostnameVerifier hostnameVerifier;
    private static SSLSocketFactory defaultSSLSocketFactory;
    private SSLSocketFactory sslSocketFactory;
    
    public HttpsURLConnection(final URL url) throws IOException {
        super(url);
        this.hostnameVerifier = HttpsURLConnection.defaultHostnameVerifier;
        this.sslSocketFactory = getDefaultSSLSocketFactory();
    }
    
    public abstract String getCipherSuite();
    
    public abstract X509Certificate[] getServerCertificateChain();
    
    public static void setDefaultHostnameVerifier(final HostnameVerifier defaultHostnameVerifier) {
        if (defaultHostnameVerifier == null) {
            throw new IllegalArgumentException("no default HostnameVerifier specified");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SSLPermission("setHostnameVerifier"));
        }
        HttpsURLConnection.defaultHostnameVerifier = defaultHostnameVerifier;
    }
    
    public static HostnameVerifier getDefaultHostnameVerifier() {
        return HttpsURLConnection.defaultHostnameVerifier;
    }
    
    public void setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        if (hostnameVerifier == null) {
            throw new IllegalArgumentException("no HostnameVerifier specified");
        }
        this.hostnameVerifier = hostnameVerifier;
    }
    
    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public static void setDefaultSSLSocketFactory(final SSLSocketFactory defaultSSLSocketFactory) {
        if (defaultSSLSocketFactory == null) {
            throw new IllegalArgumentException("no default SSLSocketFactory specified");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        HttpsURLConnection.defaultSSLSocketFactory = defaultSSLSocketFactory;
    }
    
    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        if (HttpsURLConnection.defaultSSLSocketFactory == null) {
            HttpsURLConnection.defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        return HttpsURLConnection.defaultSSLSocketFactory;
    }
    
    public void setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
        if (sslSocketFactory == null) {
            throw new IllegalArgumentException("no SSLSocketFactory specified");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        this.sslSocketFactory = sslSocketFactory;
    }
    
    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }
    
    static {
        HttpsURLConnection.defaultHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(final String s, final String s2) {
                return false;
            }
        };
        HttpsURLConnection.defaultSSLSocketFactory = null;
    }
}
