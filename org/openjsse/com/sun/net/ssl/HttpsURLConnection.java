package org.openjsse.com.sun.net.ssl;

import java.security.Permission;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
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
    
    public abstract Certificate[] getServerCertificates() throws SSLPeerUnverifiedException;
    
    public static void setDefaultHostnameVerifier(final HostnameVerifier v) {
        if (v == null) {
            throw new IllegalArgumentException("no default HostnameVerifier specified");
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new SSLPermission("setHostnameVerifier"));
        }
        HttpsURLConnection.defaultHostnameVerifier = v;
    }
    
    public static HostnameVerifier getDefaultHostnameVerifier() {
        return HttpsURLConnection.defaultHostnameVerifier;
    }
    
    public void setHostnameVerifier(final HostnameVerifier v) {
        if (v == null) {
            throw new IllegalArgumentException("no HostnameVerifier specified");
        }
        this.hostnameVerifier = v;
    }
    
    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public static void setDefaultSSLSocketFactory(final SSLSocketFactory sf) {
        if (sf == null) {
            throw new IllegalArgumentException("no default SSLSocketFactory specified");
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkSetFactory();
        }
        HttpsURLConnection.defaultSSLSocketFactory = sf;
    }
    
    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        if (HttpsURLConnection.defaultSSLSocketFactory == null) {
            HttpsURLConnection.defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        return HttpsURLConnection.defaultSSLSocketFactory;
    }
    
    public void setSSLSocketFactory(final SSLSocketFactory sf) {
        if (sf == null) {
            throw new IllegalArgumentException("no SSLSocketFactory specified");
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkSetFactory();
        }
        this.sslSocketFactory = sf;
    }
    
    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }
    
    static {
        HttpsURLConnection.defaultHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(final String urlHostname, final String certHostname) {
                return false;
            }
        };
        HttpsURLConnection.defaultSSLSocketFactory = null;
    }
}
