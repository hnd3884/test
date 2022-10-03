package org.openjsse.sun.net.www.protocol.https;

import java.security.Principal;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.util.List;
import java.security.cert.Certificate;
import java.net.SecureCacheResponse;
import sun.net.www.http.HttpClient;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Proxy;
import sun.net.www.protocol.http.Handler;
import java.net.URL;
import sun.net.www.protocol.http.HttpURLConnection;

public abstract class AbstractDelegateHttpsURLConnection extends HttpURLConnection
{
    protected AbstractDelegateHttpsURLConnection(final URL url, final Handler handler) throws IOException {
        this(url, null, handler);
    }
    
    protected AbstractDelegateHttpsURLConnection(final URL url, final Proxy p, final Handler handler) throws IOException {
        super(url, p, handler);
    }
    
    protected abstract SSLSocketFactory getSSLSocketFactory();
    
    protected abstract HostnameVerifier getHostnameVerifier();
    
    public void setNewClient(final URL url) throws IOException {
        this.setNewClient(url, false);
    }
    
    public void setNewClient(final URL url, final boolean useCache) throws IOException {
        this.http = HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), useCache, this);
        ((HttpsClient)this.http).afterConnect();
    }
    
    public void setProxiedClient(final URL url, final String proxyHost, final int proxyPort) throws IOException {
        this.setProxiedClient(url, proxyHost, proxyPort, false);
    }
    
    public void setProxiedClient(final URL url, final String proxyHost, final int proxyPort, final boolean useCache) throws IOException {
        this.proxiedConnect(url, proxyHost, proxyPort, useCache);
        if (!this.http.isCachedConnection()) {
            this.doTunneling();
        }
        ((HttpsClient)this.http).afterConnect();
    }
    
    @Override
    protected void proxiedConnect(final URL url, final String proxyHost, final int proxyPort, final boolean useCache) throws IOException {
        if (this.connected) {
            return;
        }
        this.http = HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), proxyHost, proxyPort, useCache, this);
        this.connected = true;
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void setConnected(final boolean conn) {
        this.connected = conn;
    }
    
    @Override
    public void connect() throws IOException {
        if (this.connected) {
            return;
        }
        this.plainConnect();
        if (this.cachedResponse != null) {
            return;
        }
        if (!this.http.isCachedConnection() && this.http.needsTunneling()) {
            this.doTunneling();
        }
        ((HttpsClient)this.http).afterConnect();
    }
    
    @Override
    protected HttpClient getNewHttpClient(final URL url, final Proxy p, final int connectTimeout) throws IOException {
        return HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), p, true, connectTimeout, this);
    }
    
    @Override
    protected HttpClient getNewHttpClient(final URL url, final Proxy p, final int connectTimeout, final boolean useCache) throws IOException {
        return HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), p, useCache, connectTimeout, this);
    }
    
    public String getCipherSuite() {
        if (this.cachedResponse != null) {
            return ((SecureCacheResponse)this.cachedResponse).getCipherSuite();
        }
        if (this.http == null) {
            throw new IllegalStateException("connection not yet open");
        }
        return ((HttpsClient)this.http).getCipherSuite();
    }
    
    public Certificate[] getLocalCertificates() {
        if (this.cachedResponse != null) {
            final List<Certificate> l = ((SecureCacheResponse)this.cachedResponse).getLocalCertificateChain();
            if (l == null) {
                return null;
            }
            return l.toArray(new Certificate[0]);
        }
        else {
            if (this.http == null) {
                throw new IllegalStateException("connection not yet open");
            }
            return ((HttpsClient)this.http).getLocalCertificates();
        }
    }
    
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        if (this.cachedResponse != null) {
            final List<Certificate> l = ((SecureCacheResponse)this.cachedResponse).getServerCertificateChain();
            if (l == null) {
                return null;
            }
            return l.toArray(new Certificate[0]);
        }
        else {
            if (this.http == null) {
                throw new IllegalStateException("connection not yet open");
            }
            return ((HttpsClient)this.http).getServerCertificates();
        }
    }
    
    Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        if (this.cachedResponse != null) {
            return ((SecureCacheResponse)this.cachedResponse).getPeerPrincipal();
        }
        if (this.http == null) {
            throw new IllegalStateException("connection not yet open");
        }
        return ((HttpsClient)this.http).getPeerPrincipal();
    }
    
    Principal getLocalPrincipal() {
        if (this.cachedResponse != null) {
            return ((SecureCacheResponse)this.cachedResponse).getLocalPrincipal();
        }
        if (this.http == null) {
            throw new IllegalStateException("connection not yet open");
        }
        return ((HttpsClient)this.http).getLocalPrincipal();
    }
}
