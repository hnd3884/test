package sun.net.www.protocol.https;

import java.security.Principal;
import javax.security.cert.X509Certificate;
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
    
    protected AbstractDelegateHttpsURLConnection(final URL url, final Proxy proxy, final Handler handler) throws IOException {
        super(url, proxy, handler);
    }
    
    protected abstract SSLSocketFactory getSSLSocketFactory();
    
    protected abstract HostnameVerifier getHostnameVerifier();
    
    public void setNewClient(final URL url) throws IOException {
        this.setNewClient(url, false);
    }
    
    public void setNewClient(final URL url, final boolean b) throws IOException {
        this.http = HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), b, this);
        ((HttpsClient)this.http).afterConnect();
    }
    
    public void setProxiedClient(final URL url, final String s, final int n) throws IOException {
        this.setProxiedClient(url, s, n, false);
    }
    
    public void setProxiedClient(final URL url, final String s, final int n, final boolean b) throws IOException {
        this.proxiedConnect(url, s, n, b);
        if (!this.http.isCachedConnection()) {
            this.doTunneling();
        }
        ((HttpsClient)this.http).afterConnect();
    }
    
    @Override
    protected void proxiedConnect(final URL url, final String s, final int n, final boolean b) throws IOException {
        if (this.connected) {
            return;
        }
        this.http = HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), s, n, b, this);
        this.connected = true;
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void setConnected(final boolean connected) {
        this.connected = connected;
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
    protected HttpClient getNewHttpClient(final URL url, final Proxy proxy, final int n) throws IOException {
        return HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), proxy, true, n, this);
    }
    
    @Override
    protected HttpClient getNewHttpClient(final URL url, final Proxy proxy, final int n, final boolean b) throws IOException {
        return HttpsClient.New(this.getSSLSocketFactory(), url, this.getHostnameVerifier(), proxy, b, n, this);
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
            final List<Certificate> localCertificateChain = ((SecureCacheResponse)this.cachedResponse).getLocalCertificateChain();
            if (localCertificateChain == null) {
                return null;
            }
            return localCertificateChain.toArray(new Certificate[0]);
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
            final List<Certificate> serverCertificateChain = ((SecureCacheResponse)this.cachedResponse).getServerCertificateChain();
            if (serverCertificateChain == null) {
                return null;
            }
            return serverCertificateChain.toArray(new Certificate[0]);
        }
        else {
            if (this.http == null) {
                throw new IllegalStateException("connection not yet open");
            }
            return ((HttpsClient)this.http).getServerCertificates();
        }
    }
    
    public X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
        if (this.cachedResponse != null) {
            throw new UnsupportedOperationException("this method is not supported when using cache");
        }
        if (this.http == null) {
            throw new IllegalStateException("connection not yet open");
        }
        return ((HttpsClient)this.http).getServerCertificateChain();
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
