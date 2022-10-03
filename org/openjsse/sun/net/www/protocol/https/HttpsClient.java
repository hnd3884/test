package org.openjsse.sun.net.www.protocol.https;

import java.net.InetSocketAddress;
import javax.net.ssl.HandshakeCompletedEvent;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import sun.security.util.HostnameChecker;
import java.net.UnknownHostException;
import javax.net.ssl.SSLParameters;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import org.openjsse.sun.security.ssl.SSLSocketImpl;
import javax.net.ssl.SSLSocket;
import java.net.SocketException;
import java.net.Socket;
import java.net.InetAddress;
import sun.util.logging.PlatformLogger;
import sun.net.www.protocol.http.HttpURLConnection;
import java.net.Proxy;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HandshakeCompletedListener;
import sun.net.www.http.HttpClient;

final class HttpsClient extends HttpClient implements HandshakeCompletedListener
{
    private static final int httpsPortNumber = 443;
    private static final String defaultHVCanonicalName = "javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
    private HostnameVerifier hv;
    private SSLSocketFactory sslSocketFactory;
    private SSLSession session;
    
    @Override
    protected int getDefaultPort() {
        return 443;
    }
    
    private String[] getCipherSuites() {
        final String cipherString = GetPropertyAction.privilegedGetProperty("https.cipherSuites");
        String[] ciphers;
        if (cipherString == null || "".equals(cipherString)) {
            ciphers = null;
        }
        else {
            final Vector<String> v = new Vector<String>();
            final StringTokenizer tokenizer = new StringTokenizer(cipherString, ",");
            while (tokenizer.hasMoreTokens()) {
                v.addElement(tokenizer.nextToken());
            }
            ciphers = new String[v.size()];
            for (int i = 0; i < ciphers.length; ++i) {
                ciphers[i] = v.elementAt(i);
            }
        }
        return ciphers;
    }
    
    private String[] getProtocols() {
        final String protocolString = GetPropertyAction.privilegedGetProperty("https.protocols");
        String[] protocols;
        if (protocolString == null || "".equals(protocolString)) {
            protocols = null;
        }
        else {
            final Vector<String> v = new Vector<String>();
            final StringTokenizer tokenizer = new StringTokenizer(protocolString, ",");
            while (tokenizer.hasMoreTokens()) {
                v.addElement(tokenizer.nextToken());
            }
            protocols = new String[v.size()];
            for (int i = 0; i < protocols.length; ++i) {
                protocols[i] = v.elementAt(i);
            }
        }
        return protocols;
    }
    
    private String getUserAgent() {
        String userAgent = GetPropertyAction.privilegedGetProperty("https.agent");
        if (userAgent == null || userAgent.length() == 0) {
            userAgent = "JSSE";
        }
        return userAgent;
    }
    
    private HttpsClient(final SSLSocketFactory sf, final URL url) throws IOException {
        this(sf, url, (String)null, -1);
    }
    
    HttpsClient(final SSLSocketFactory sf, final URL url, final String proxyHost, final int proxyPort) throws IOException {
        this(sf, url, proxyHost, proxyPort, -1);
    }
    
    HttpsClient(final SSLSocketFactory sf, final URL url, final String proxyHost, final int proxyPort, final int connectTimeout) throws IOException {
        this(sf, url, (proxyHost == null) ? null : HttpClient.newHttpProxy(proxyHost, proxyPort, "https"), connectTimeout);
    }
    
    HttpsClient(final SSLSocketFactory sf, final URL url, final Proxy proxy, final int connectTimeout) throws IOException {
        final PlatformLogger logger = HttpURLConnection.getHttpLogger();
        if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
            logger.finest("Creating new HttpsClient with url:" + url + " and proxy:" + proxy + " with connect timeout:" + connectTimeout);
        }
        this.proxy = proxy;
        this.setSSLSocketFactory(sf);
        this.proxyDisabled = true;
        this.host = url.getHost();
        this.url = url;
        this.port = url.getPort();
        if (this.port == -1) {
            this.port = this.getDefaultPort();
        }
        this.setConnectTimeout(connectTimeout);
        this.openServer();
    }
    
    static HttpClient New(final SSLSocketFactory sf, final URL url, final HostnameVerifier hv, final HttpURLConnection httpuc) throws IOException {
        return New(sf, url, hv, true, httpuc);
    }
    
    static HttpClient New(final SSLSocketFactory sf, final URL url, final HostnameVerifier hv, final boolean useCache, final HttpURLConnection httpuc) throws IOException {
        return New(sf, url, hv, null, -1, useCache, httpuc);
    }
    
    static HttpClient New(final SSLSocketFactory sf, final URL url, final HostnameVerifier hv, final String proxyHost, final int proxyPort, final HttpURLConnection httpuc) throws IOException {
        return New(sf, url, hv, proxyHost, proxyPort, true, httpuc);
    }
    
    static HttpClient New(final SSLSocketFactory sf, final URL url, final HostnameVerifier hv, final String proxyHost, final int proxyPort, final boolean useCache, final HttpURLConnection httpuc) throws IOException {
        return New(sf, url, hv, proxyHost, proxyPort, useCache, -1, httpuc);
    }
    
    static HttpClient New(final SSLSocketFactory sf, final URL url, final HostnameVerifier hv, final String proxyHost, final int proxyPort, final boolean useCache, final int connectTimeout, final HttpURLConnection httpuc) throws IOException {
        return New(sf, url, hv, (proxyHost == null) ? null : HttpClient.newHttpProxy(proxyHost, proxyPort, "https"), useCache, connectTimeout, httpuc);
    }
    
    static HttpClient New(final SSLSocketFactory sf, final URL url, final HostnameVerifier hv, Proxy p, final boolean useCache, final int connectTimeout, final HttpURLConnection httpuc) throws IOException {
        if (p == null) {
            p = Proxy.NO_PROXY;
        }
        final PlatformLogger logger = HttpURLConnection.getHttpLogger();
        if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
            logger.finest("Looking for HttpClient for URL " + url + " and proxy value of " + p);
        }
        HttpsClient ret = null;
        if (useCache) {
            ret = (HttpsClient)HttpsClient.kac.get(url, sf);
            if (ret != null && httpuc != null && httpuc.streaming() && httpuc.getRequestMethod() == "POST" && !ret.available()) {
                ret = null;
            }
            if (ret != null) {
                final boolean compatible = (ret.proxy != null && ret.proxy.equals(p)) || (ret.proxy == null && p == Proxy.NO_PROXY);
                if (compatible) {
                    synchronized (ret) {
                        ret.cachedHttpClient = true;
                        assert ret.inCache;
                        ret.inCache = false;
                        if (httpuc != null && ret.needsTunneling()) {
                            httpuc.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
                        }
                        if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                            logger.finest("KeepAlive stream retrieved from the cache, " + ret);
                        }
                    }
                }
                else {
                    synchronized (ret) {
                        if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                            logger.finest("Not returning this connection to cache: " + ret);
                        }
                        ret.inCache = false;
                        ret.closeServer();
                    }
                    ret = null;
                }
            }
        }
        if (ret == null) {
            ret = new HttpsClient(sf, url, p, connectTimeout);
        }
        else {
            final SecurityManager security = System.getSecurityManager();
            if (security != null) {
                if (ret.proxy == Proxy.NO_PROXY || ret.proxy == null) {
                    security.checkConnect(InetAddress.getByName(url.getHost()).getHostAddress(), url.getPort());
                }
                else {
                    security.checkConnect(url.getHost(), url.getPort());
                }
            }
            ret.url = url;
        }
        ret.setHostnameVerifier(hv);
        return ret;
    }
    
    void setHostnameVerifier(final HostnameVerifier hv) {
        this.hv = hv;
    }
    
    void setSSLSocketFactory(final SSLSocketFactory sf) {
        this.sslSocketFactory = sf;
    }
    
    SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }
    
    @Override
    protected Socket createSocket() throws IOException {
        try {
            return this.sslSocketFactory.createSocket();
        }
        catch (final SocketException se) {
            final Throwable t = se.getCause();
            if (t != null && t instanceof UnsupportedOperationException) {
                return super.createSocket();
            }
            throw se;
        }
    }
    
    @Override
    public boolean needsTunneling() {
        return this.proxy != null && this.proxy.type() != Proxy.Type.DIRECT && this.proxy.type() != Proxy.Type.SOCKS;
    }
    
    @Override
    public void afterConnect() throws IOException, UnknownHostException {
        if (!this.isCachedConnection()) {
            SSLSocket s = null;
            final SSLSocketFactory factory = this.sslSocketFactory;
            try {
                if (!(this.serverSocket instanceof SSLSocket)) {
                    s = (SSLSocket)factory.createSocket(this.serverSocket, this.host, this.port, true);
                }
                else {
                    s = (SSLSocket)this.serverSocket;
                    if (s instanceof SSLSocketImpl) {
                        ((SSLSocketImpl)s).setHost(this.host);
                    }
                }
            }
            catch (final IOException ex) {
                try {
                    s = (SSLSocket)factory.createSocket(this.host, this.port);
                }
                catch (final IOException ignored) {
                    throw ex;
                }
            }
            final String[] protocols = this.getProtocols();
            final String[] ciphers = this.getCipherSuites();
            if (protocols != null) {
                s.setEnabledProtocols(protocols);
            }
            if (ciphers != null) {
                s.setEnabledCipherSuites(ciphers);
            }
            s.addHandshakeCompletedListener(this);
            boolean needToCheckSpoofing = true;
            final String identification = s.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identification != null && identification.length() != 0) {
                if (identification.equalsIgnoreCase("HTTPS")) {
                    needToCheckSpoofing = false;
                }
            }
            else {
                boolean isDefaultHostnameVerifier = false;
                if (this.hv != null) {
                    final String canonicalName = this.hv.getClass().getCanonicalName();
                    if (canonicalName != null && canonicalName.equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier")) {
                        isDefaultHostnameVerifier = true;
                    }
                }
                else {
                    isDefaultHostnameVerifier = true;
                }
                if (isDefaultHostnameVerifier) {
                    final SSLParameters paramaters = s.getSSLParameters();
                    paramaters.setEndpointIdentificationAlgorithm("HTTPS");
                    s.setSSLParameters(paramaters);
                    needToCheckSpoofing = false;
                }
            }
            s.startHandshake();
            this.session = s.getSession();
            this.serverSocket = s;
            try {
                this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), false, HttpsClient.encoding);
            }
            catch (final UnsupportedEncodingException e) {
                throw new InternalError(HttpsClient.encoding + " encoding not found");
            }
            if (needToCheckSpoofing) {
                this.checkURLSpoofing(this.hv);
            }
        }
        else {
            this.session = ((SSLSocket)this.serverSocket).getSession();
        }
    }
    
    private void checkURLSpoofing(final HostnameVerifier hostnameVerifier) throws IOException {
        String host = this.url.getHost();
        if (host != null && host.startsWith("[") && host.endsWith("]")) {
            host = host.substring(1, host.length() - 1);
        }
        Certificate[] peerCerts = null;
        final String cipher = this.session.getCipherSuite();
        try {
            final HostnameChecker checker = HostnameChecker.getInstance((byte)1);
            peerCerts = this.session.getPeerCertificates();
            if (peerCerts[0] instanceof X509Certificate) {
                final X509Certificate peerCert = (X509Certificate)peerCerts[0];
                checker.match(host, peerCert);
                return;
            }
            throw new SSLPeerUnverifiedException("");
        }
        catch (final SSLPeerUnverifiedException ex) {}
        catch (final CertificateException ex2) {}
        if (cipher != null && cipher.indexOf("_anon_") != -1) {
            return;
        }
        if (hostnameVerifier != null && hostnameVerifier.verify(host, this.session)) {
            return;
        }
        this.serverSocket.close();
        this.session.invalidate();
        throw new IOException("HTTPS hostname wrong:  should be <" + this.url.getHost() + ">");
    }
    
    @Override
    protected void putInKeepAliveCache() {
        if (!this.inCache) {
            this.inCache = true;
            HttpsClient.kac.put(this.url, this.sslSocketFactory, this);
            return;
        }
        assert false : "Duplicate put to keep alive cache";
    }
    
    @Override
    public void closeIdleConnection() {
        final HttpClient http = HttpsClient.kac.get(this.url, this.sslSocketFactory);
        if (http != null) {
            http.closeServer();
        }
    }
    
    String getCipherSuite() {
        return this.session.getCipherSuite();
    }
    
    public Certificate[] getLocalCertificates() {
        return this.session.getLocalCertificates();
    }
    
    Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        return this.session.getPeerCertificates();
    }
    
    Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        Principal principal;
        try {
            principal = this.session.getPeerPrincipal();
        }
        catch (final AbstractMethodError e) {
            final Certificate[] certs = this.session.getPeerCertificates();
            principal = ((X509Certificate)certs[0]).getSubjectX500Principal();
        }
        return principal;
    }
    
    Principal getLocalPrincipal() {
        Principal principal;
        try {
            principal = this.session.getLocalPrincipal();
        }
        catch (final AbstractMethodError e) {
            principal = null;
            final Certificate[] certs = this.session.getLocalCertificates();
            if (certs != null) {
                principal = ((X509Certificate)certs[0]).getSubjectX500Principal();
            }
        }
        return principal;
    }
    
    @Override
    public void handshakeCompleted(final HandshakeCompletedEvent event) {
        this.session = event.getSession();
    }
    
    @Override
    public String getProxyHostUsed() {
        if (!this.needsTunneling()) {
            return null;
        }
        return super.getProxyHostUsed();
    }
    
    @Override
    public int getProxyPortUsed() {
        return (this.proxy == null || this.proxy.type() == Proxy.Type.DIRECT || this.proxy.type() == Proxy.Type.SOCKS) ? -1 : ((InetSocketAddress)this.proxy.address()).getPort();
    }
}
