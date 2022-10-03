package sun.net.www.protocol.https;

import java.net.InetSocketAddress;
import javax.net.ssl.HandshakeCompletedEvent;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import sun.security.util.HostnameChecker;
import java.net.UnknownHostException;
import javax.net.ssl.SSLParameters;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import sun.security.ssl.SSLSocketImpl;
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
import java.security.PrivilegedAction;
import java.security.AccessController;
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
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("https.cipherSuites"));
        String[] array;
        if (s == null || "".equals(s)) {
            array = null;
        }
        else {
            final Vector vector = new Vector();
            final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
            while (stringTokenizer.hasMoreTokens()) {
                vector.addElement(stringTokenizer.nextToken());
            }
            array = new String[vector.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (String)vector.elementAt(i);
            }
        }
        return array;
    }
    
    private String[] getProtocols() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("https.protocols"));
        String[] array;
        if (s == null || "".equals(s)) {
            array = null;
        }
        else {
            final Vector vector = new Vector();
            final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
            while (stringTokenizer.hasMoreTokens()) {
                vector.addElement(stringTokenizer.nextToken());
            }
            array = new String[vector.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (String)vector.elementAt(i);
            }
        }
        return array;
    }
    
    private String getUserAgent() {
        String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("https.agent"));
        if (s == null || s.length() == 0) {
            s = "JSSE";
        }
        return s;
    }
    
    private HttpsClient(final SSLSocketFactory sslSocketFactory, final URL url) throws IOException {
        this(sslSocketFactory, url, (String)null, -1);
    }
    
    HttpsClient(final SSLSocketFactory sslSocketFactory, final URL url, final String s, final int n) throws IOException {
        this(sslSocketFactory, url, s, n, -1);
    }
    
    HttpsClient(final SSLSocketFactory sslSocketFactory, final URL url, final String s, final int n, final int n2) throws IOException {
        this(sslSocketFactory, url, (s == null) ? null : HttpClient.newHttpProxy(s, n, "https"), n2);
    }
    
    HttpsClient(final SSLSocketFactory sslSocketFactory, final URL url, final Proxy proxy, final int connectTimeout) throws IOException {
        final PlatformLogger httpLogger = HttpURLConnection.getHttpLogger();
        if (httpLogger.isLoggable(PlatformLogger.Level.FINEST)) {
            httpLogger.finest("Creating new HttpsClient with url:" + url + " and proxy:" + proxy + " with connect timeout:" + connectTimeout);
        }
        this.proxy = proxy;
        this.setSSLSocketFactory(sslSocketFactory);
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
    
    static HttpClient New(final SSLSocketFactory sslSocketFactory, final URL url, final HostnameVerifier hostnameVerifier, final HttpURLConnection httpURLConnection) throws IOException {
        return New(sslSocketFactory, url, hostnameVerifier, true, httpURLConnection);
    }
    
    static HttpClient New(final SSLSocketFactory sslSocketFactory, final URL url, final HostnameVerifier hostnameVerifier, final boolean b, final HttpURLConnection httpURLConnection) throws IOException {
        return New(sslSocketFactory, url, hostnameVerifier, null, -1, b, httpURLConnection);
    }
    
    static HttpClient New(final SSLSocketFactory sslSocketFactory, final URL url, final HostnameVerifier hostnameVerifier, final String s, final int n, final HttpURLConnection httpURLConnection) throws IOException {
        return New(sslSocketFactory, url, hostnameVerifier, s, n, true, httpURLConnection);
    }
    
    static HttpClient New(final SSLSocketFactory sslSocketFactory, final URL url, final HostnameVerifier hostnameVerifier, final String s, final int n, final boolean b, final HttpURLConnection httpURLConnection) throws IOException {
        return New(sslSocketFactory, url, hostnameVerifier, s, n, b, -1, httpURLConnection);
    }
    
    static HttpClient New(final SSLSocketFactory sslSocketFactory, final URL url, final HostnameVerifier hostnameVerifier, final String s, final int n, final boolean b, final int n2, final HttpURLConnection httpURLConnection) throws IOException {
        return New(sslSocketFactory, url, hostnameVerifier, (s == null) ? null : HttpClient.newHttpProxy(s, n, "https"), b, n2, httpURLConnection);
    }
    
    static HttpClient New(final SSLSocketFactory sslSocketFactory, final URL url, final HostnameVerifier hostnameVerifier, Proxy no_PROXY, final boolean b, final int n, final HttpURLConnection httpURLConnection) throws IOException {
        if (no_PROXY == null) {
            no_PROXY = Proxy.NO_PROXY;
        }
        final PlatformLogger httpLogger = HttpURLConnection.getHttpLogger();
        if (httpLogger.isLoggable(PlatformLogger.Level.FINEST)) {
            httpLogger.finest("Looking for HttpClient for URL " + url + " and proxy value of " + no_PROXY);
        }
        HttpClient httpClient = null;
        if (b) {
            httpClient = HttpsClient.kac.get(url, sslSocketFactory);
            if (httpClient != null && httpURLConnection != null && httpURLConnection.streaming() && httpURLConnection.getRequestMethod() == "POST" && !httpClient.available()) {
                httpClient = null;
            }
            if (httpClient != null) {
                if ((((HttpsClient)httpClient).proxy != null && ((HttpsClient)httpClient).proxy.equals(no_PROXY)) || (((HttpsClient)httpClient).proxy == null && no_PROXY == Proxy.NO_PROXY)) {
                    synchronized (httpClient) {
                        ((HttpsClient)httpClient).cachedHttpClient = true;
                        assert ((HttpsClient)httpClient).inCache;
                        ((HttpsClient)httpClient).inCache = false;
                        if (httpURLConnection != null && ((HttpsClient)httpClient).needsTunneling()) {
                            httpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
                        }
                        if (httpLogger.isLoggable(PlatformLogger.Level.FINEST)) {
                            httpLogger.finest("KeepAlive stream retrieved from the cache, " + httpClient);
                        }
                    }
                }
                else {
                    synchronized (httpClient) {
                        if (httpLogger.isLoggable(PlatformLogger.Level.FINEST)) {
                            httpLogger.finest("Not returning this connection to cache: " + httpClient);
                        }
                        ((HttpsClient)httpClient).inCache = false;
                        ((HttpsClient)httpClient).closeServer();
                    }
                    httpClient = null;
                }
            }
        }
        if (httpClient == null) {
            httpClient = new HttpsClient(sslSocketFactory, url, no_PROXY, n);
        }
        else {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                if (((HttpsClient)httpClient).proxy == Proxy.NO_PROXY || ((HttpsClient)httpClient).proxy == null) {
                    securityManager.checkConnect(InetAddress.getByName(url.getHost()).getHostAddress(), url.getPort());
                }
                else {
                    securityManager.checkConnect(url.getHost(), url.getPort());
                }
            }
            ((HttpsClient)httpClient).url = url;
        }
        ((HttpsClient)httpClient).setHostnameVerifier(hostnameVerifier);
        return httpClient;
    }
    
    void setHostnameVerifier(final HostnameVerifier hv) {
        this.hv = hv;
    }
    
    void setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
    
    SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }
    
    @Override
    protected Socket createSocket() throws IOException {
        try {
            return this.sslSocketFactory.createSocket();
        }
        catch (final SocketException ex) {
            final Throwable cause = ex.getCause();
            if (cause != null && cause instanceof UnsupportedOperationException) {
                return super.createSocket();
            }
            throw ex;
        }
    }
    
    @Override
    public boolean needsTunneling() {
        return this.proxy != null && this.proxy.type() != Proxy.Type.DIRECT && this.proxy.type() != Proxy.Type.SOCKS;
    }
    
    @Override
    public void afterConnect() throws IOException, UnknownHostException {
        if (!this.isCachedConnection()) {
            final SSLSocketFactory sslSocketFactory = this.sslSocketFactory;
            SSLSocket serverSocket;
            try {
                if (!(this.serverSocket instanceof SSLSocket)) {
                    serverSocket = (SSLSocket)sslSocketFactory.createSocket(this.serverSocket, this.host, this.port, true);
                }
                else {
                    serverSocket = (SSLSocket)this.serverSocket;
                    if (serverSocket instanceof SSLSocketImpl) {
                        ((SSLSocketImpl)serverSocket).setHost(this.host);
                    }
                }
            }
            catch (final IOException ex) {
                try {
                    serverSocket = (SSLSocket)sslSocketFactory.createSocket(this.host, this.port);
                }
                catch (final IOException ex2) {
                    throw ex;
                }
            }
            final String[] protocols = this.getProtocols();
            final String[] cipherSuites = this.getCipherSuites();
            if (protocols != null) {
                serverSocket.setEnabledProtocols(protocols);
            }
            if (cipherSuites != null) {
                serverSocket.setEnabledCipherSuites(cipherSuites);
            }
            serverSocket.addHandshakeCompletedListener(this);
            boolean b = true;
            final String endpointIdentificationAlgorithm = serverSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (endpointIdentificationAlgorithm != null && endpointIdentificationAlgorithm.length() != 0) {
                if (endpointIdentificationAlgorithm.equalsIgnoreCase("HTTPS")) {
                    b = false;
                }
            }
            else {
                boolean b2 = false;
                if (this.hv != null) {
                    final String canonicalName = this.hv.getClass().getCanonicalName();
                    if (canonicalName != null && canonicalName.equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier")) {
                        b2 = true;
                    }
                }
                else {
                    b2 = true;
                }
                if (b2) {
                    final SSLParameters sslParameters = serverSocket.getSSLParameters();
                    sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
                    serverSocket.setSSLParameters(sslParameters);
                    b = false;
                }
            }
            serverSocket.startHandshake();
            this.session = serverSocket.getSession();
            this.serverSocket = serverSocket;
            try {
                this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), false, HttpsClient.encoding);
            }
            catch (final UnsupportedEncodingException ex3) {
                throw new InternalError(HttpsClient.encoding + " encoding not found");
            }
            if (b) {
                this.checkURLSpoofing(this.hv);
            }
        }
        else {
            this.session = ((SSLSocket)this.serverSocket).getSession();
        }
    }
    
    private void checkURLSpoofing(final HostnameVerifier hostnameVerifier) throws IOException {
        String s = this.url.getHost();
        if (s != null && s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
        }
        final String cipherSuite = this.session.getCipherSuite();
        try {
            final HostnameChecker instance = HostnameChecker.getInstance((byte)1);
            if (cipherSuite.startsWith("TLS_KRB5")) {
                if (!HostnameChecker.match(s, this.getPeerPrincipal())) {
                    throw new SSLPeerUnverifiedException("Hostname checker failed for Kerberos");
                }
            }
            else {
                final Certificate[] peerCertificates = this.session.getPeerCertificates();
                if (!(peerCertificates[0] instanceof X509Certificate)) {
                    throw new SSLPeerUnverifiedException("");
                }
                instance.match(s, (X509Certificate)peerCertificates[0]);
            }
            return;
        }
        catch (final SSLPeerUnverifiedException ex) {}
        catch (final CertificateException ex2) {}
        if (cipherSuite != null && cipherSuite.indexOf("_anon_") != -1) {
            return;
        }
        if (hostnameVerifier != null && hostnameVerifier.verify(s, this.session)) {
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
        final HttpClient value = HttpsClient.kac.get(this.url, this.sslSocketFactory);
        if (value != null) {
            value.closeServer();
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
    
    javax.security.cert.X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
        return this.session.getPeerCertificateChain();
    }
    
    Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        Principal principal;
        try {
            principal = this.session.getPeerPrincipal();
        }
        catch (final AbstractMethodError abstractMethodError) {
            principal = ((X509Certificate)this.session.getPeerCertificates()[0]).getSubjectX500Principal();
        }
        return principal;
    }
    
    Principal getLocalPrincipal() {
        Principal principal;
        try {
            principal = this.session.getLocalPrincipal();
        }
        catch (final AbstractMethodError abstractMethodError) {
            principal = null;
            final Certificate[] localCertificates = this.session.getLocalCertificates();
            if (localCertificates != null) {
                principal = ((X509Certificate)localCertificates[0]).getSubjectX500Principal();
            }
        }
        return principal;
    }
    
    @Override
    public void handshakeCompleted(final HandshakeCompletedEvent handshakeCompletedEvent) {
        this.session = handshakeCompletedEvent.getSession();
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
