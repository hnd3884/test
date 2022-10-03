package sun.net.www.http;

import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.io.InputStream;
import java.net.URI;
import java.net.CookieHandler;
import sun.net.www.MeteredStream;
import java.net.SocketException;
import sun.net.www.HeaderParser;
import java.util.Locale;
import sun.net.www.ParseUtil;
import sun.net.ProgressSource;
import java.net.MalformedURLException;
import sun.net.www.URLConnection;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.net.UnknownHostException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import java.net.SocketTimeoutException;
import java.io.BufferedInputStream;
import java.net.InetAddress;
import sun.net.www.protocol.http.HttpURLConnection;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.io.IOException;
import sun.util.logging.PlatformLogger;
import java.net.URL;
import java.net.CacheRequest;
import sun.net.www.MessageHeader;
import sun.net.NetworkClient;

public class HttpClient extends NetworkClient
{
    protected boolean cachedHttpClient;
    protected boolean inCache;
    MessageHeader requests;
    PosterOutputStream poster;
    boolean streaming;
    boolean failedOnce;
    private boolean ignoreContinue;
    private static final int HTTP_CONTINUE = 100;
    static final int httpPortNumber = 80;
    protected boolean proxyDisabled;
    public boolean usingProxy;
    protected String host;
    protected int port;
    protected static KeepAliveCache kac;
    private static boolean keepAliveProp;
    private static boolean retryPostProp;
    private static final boolean cacheNTLMProp;
    private static final boolean cacheSPNEGOProp;
    volatile boolean keepingAlive;
    volatile boolean disableKeepAlive;
    int keepAliveConnections;
    int keepAliveTimeout;
    private CacheRequest cacheRequest;
    protected URL url;
    public boolean reuse;
    private HttpCapture capture;
    private static final PlatformLogger logger;
    
    protected int getDefaultPort() {
        return 80;
    }
    
    private static int getDefaultPort(final String s) {
        if ("http".equalsIgnoreCase(s)) {
            return 80;
        }
        if ("https".equalsIgnoreCase(s)) {
            return 443;
        }
        return -1;
    }
    
    private static void logFinest(final String s) {
        if (HttpClient.logger.isLoggable(PlatformLogger.Level.FINEST)) {
            HttpClient.logger.finest(s);
        }
    }
    
    @Deprecated
    public static synchronized void resetProperties() {
    }
    
    int getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }
    
    public boolean getHttpKeepAliveSet() {
        return HttpClient.keepAliveProp;
    }
    
    protected HttpClient() {
        this.cachedHttpClient = false;
        this.poster = null;
        this.failedOnce = false;
        this.ignoreContinue = true;
        this.usingProxy = false;
        this.keepingAlive = false;
        this.keepAliveConnections = -1;
        this.keepAliveTimeout = 0;
        this.cacheRequest = null;
        this.reuse = false;
        this.capture = null;
    }
    
    private HttpClient(final URL url) throws IOException {
        this(url, null, -1, false);
    }
    
    protected HttpClient(final URL url, final boolean b) throws IOException {
        this(url, null, -1, b);
    }
    
    public HttpClient(final URL url, final String s, final int n) throws IOException {
        this(url, s, n, false);
    }
    
    protected HttpClient(final URL url, final Proxy proxy, final int connectTimeout) throws IOException {
        this.cachedHttpClient = false;
        this.poster = null;
        this.failedOnce = false;
        this.ignoreContinue = true;
        this.usingProxy = false;
        this.keepingAlive = false;
        this.keepAliveConnections = -1;
        this.keepAliveTimeout = 0;
        this.cacheRequest = null;
        this.reuse = false;
        this.capture = null;
        this.proxy = ((proxy == null) ? Proxy.NO_PROXY : proxy);
        this.host = url.getHost();
        this.url = url;
        this.port = url.getPort();
        if (this.port == -1) {
            this.port = this.getDefaultPort();
        }
        this.setConnectTimeout(connectTimeout);
        this.capture = HttpCapture.getCapture(url);
        this.openServer();
    }
    
    protected static Proxy newHttpProxy(final String s, final int n, final String s2) {
        if (s == null || s2 == null) {
            return Proxy.NO_PROXY;
        }
        return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(s, (n < 0) ? getDefaultPort(s2) : n));
    }
    
    private HttpClient(final URL url, final String s, final int n, final boolean b) throws IOException {
        this(url, b ? Proxy.NO_PROXY : newHttpProxy(s, n, "http"), -1);
    }
    
    public HttpClient(final URL url, final String s, final int n, final boolean b, final int n2) throws IOException {
        this(url, b ? Proxy.NO_PROXY : newHttpProxy(s, n, "http"), n2);
    }
    
    public static HttpClient New(final URL url) throws IOException {
        return New(url, Proxy.NO_PROXY, -1, true, null);
    }
    
    public static HttpClient New(final URL url, final boolean b) throws IOException {
        return New(url, Proxy.NO_PROXY, -1, b, null);
    }
    
    public static HttpClient New(final URL url, Proxy no_PROXY, final int n, final boolean b, final HttpURLConnection httpURLConnection) throws IOException {
        if (no_PROXY == null) {
            no_PROXY = Proxy.NO_PROXY;
        }
        HttpClient value = null;
        if (b) {
            value = HttpClient.kac.get(url, null);
            if (value != null && httpURLConnection != null && httpURLConnection.streaming() && httpURLConnection.getRequestMethod() == "POST" && !value.available()) {
                value.inCache = false;
                value.closeServer();
                value = null;
            }
            if (value != null) {
                if ((value.proxy != null && value.proxy.equals(no_PROXY)) || (value.proxy == null && no_PROXY == null)) {
                    synchronized (value) {
                        value.cachedHttpClient = true;
                        assert value.inCache;
                        value.inCache = false;
                        if (httpURLConnection != null && value.needsTunneling()) {
                            httpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
                        }
                        logFinest("KeepAlive stream retrieved from the cache, " + value);
                    }
                }
                else {
                    synchronized (value) {
                        value.inCache = false;
                        value.closeServer();
                    }
                    value = null;
                }
            }
        }
        if (value == null) {
            value = new HttpClient(url, no_PROXY, n);
        }
        else {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                if (value.proxy == Proxy.NO_PROXY || value.proxy == null) {
                    securityManager.checkConnect(InetAddress.getByName(url.getHost()).getHostAddress(), url.getPort());
                }
                else {
                    securityManager.checkConnect(url.getHost(), url.getPort());
                }
            }
            value.url = url;
        }
        return value;
    }
    
    public static HttpClient New(final URL url, final Proxy proxy, final int n, final HttpURLConnection httpURLConnection) throws IOException {
        return New(url, proxy, n, true, httpURLConnection);
    }
    
    public static HttpClient New(final URL url, final String s, final int n, final boolean b) throws IOException {
        return New(url, newHttpProxy(s, n, "http"), -1, b, null);
    }
    
    public static HttpClient New(final URL url, final String s, final int n, final boolean b, final int n2, final HttpURLConnection httpURLConnection) throws IOException {
        return New(url, newHttpProxy(s, n, "http"), n2, b, httpURLConnection);
    }
    
    public void finished() {
        if (this.reuse) {
            return;
        }
        --this.keepAliveConnections;
        this.poster = null;
        if (this.keepAliveConnections > 0 && this.isKeepingAlive() && !this.serverOutput.checkError()) {
            this.putInKeepAliveCache();
        }
        else {
            this.closeServer();
        }
    }
    
    protected synchronized boolean available() {
        boolean b = true;
        int soTimeout = -1;
        try {
            try {
                soTimeout = this.serverSocket.getSoTimeout();
                this.serverSocket.setSoTimeout(1);
                if (new BufferedInputStream(this.serverSocket.getInputStream()).read() == -1) {
                    logFinest("HttpClient.available(): read returned -1: not available");
                    b = false;
                }
            }
            catch (final SocketTimeoutException ex) {
                logFinest("HttpClient.available(): SocketTimeout: its available");
            }
            finally {
                if (soTimeout != -1) {
                    this.serverSocket.setSoTimeout(soTimeout);
                }
            }
        }
        catch (final IOException ex2) {
            logFinest("HttpClient.available(): SocketException: not available");
            b = false;
        }
        return b;
    }
    
    protected synchronized void putInKeepAliveCache() {
        if (!this.inCache) {
            this.inCache = true;
            HttpClient.kac.put(this.url, null, this);
            return;
        }
        assert false : "Duplicate put to keep alive cache";
    }
    
    protected synchronized boolean isInKeepAliveCache() {
        return this.inCache;
    }
    
    public void closeIdleConnection() {
        final HttpClient value = HttpClient.kac.get(this.url, null);
        if (value != null) {
            value.closeServer();
        }
    }
    
    @Override
    public void openServer(final String s, final int n) throws IOException {
        this.serverSocket = this.doConnect(s, n);
        try {
            OutputStream outputStream = this.serverSocket.getOutputStream();
            if (this.capture != null) {
                outputStream = new HttpCaptureOutputStream(outputStream, this.capture);
            }
            this.serverOutput = new PrintStream(new BufferedOutputStream(outputStream), false, HttpClient.encoding);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError(HttpClient.encoding + " encoding not found", ex);
        }
        this.serverSocket.setTcpNoDelay(true);
    }
    
    public boolean needsTunneling() {
        return false;
    }
    
    public synchronized boolean isCachedConnection() {
        return this.cachedHttpClient;
    }
    
    public void afterConnect() throws IOException, UnknownHostException {
    }
    
    private synchronized void privilegedOpenServer(final InetSocketAddress inetSocketAddress) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    HttpClient.this.openServer(inetSocketAddress.getHostString(), inetSocketAddress.getPort());
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private void superOpenServer(final String s, final int n) throws IOException, UnknownHostException {
        super.openServer(s, n);
    }
    
    protected synchronized void openServer() throws IOException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkConnect(this.host, this.port);
        }
        if (this.keepingAlive) {
            return;
        }
        if (this.url.getProtocol().equals("http") || this.url.getProtocol().equals("https")) {
            if (this.proxy != null && this.proxy.type() == Proxy.Type.HTTP) {
                URLConnection.setProxiedHost(this.host);
                this.privilegedOpenServer((InetSocketAddress)this.proxy.address());
                this.usingProxy = true;
                return;
            }
            this.openServer(this.host, this.port);
            this.usingProxy = false;
        }
        else {
            if (this.proxy != null && this.proxy.type() == Proxy.Type.HTTP) {
                URLConnection.setProxiedHost(this.host);
                this.privilegedOpenServer((InetSocketAddress)this.proxy.address());
                this.usingProxy = true;
                return;
            }
            super.openServer(this.host, this.port);
            this.usingProxy = false;
        }
    }
    
    public String getURLFile() throws IOException {
        String s;
        if (this.usingProxy && !this.proxyDisabled) {
            final StringBuffer sb = new StringBuffer(128);
            sb.append(this.url.getProtocol());
            sb.append(":");
            if (this.url.getAuthority() != null && this.url.getAuthority().length() > 0) {
                sb.append("//");
                sb.append(this.url.getAuthority());
            }
            if (this.url.getPath() != null) {
                sb.append(this.url.getPath());
            }
            if (this.url.getQuery() != null) {
                sb.append('?');
                sb.append(this.url.getQuery());
            }
            s = sb.toString();
        }
        else {
            s = this.url.getFile();
            if (s == null || s.length() == 0) {
                s = "/";
            }
            else if (s.charAt(0) == '?') {
                s = "/" + s;
            }
        }
        if (s.indexOf(10) == -1) {
            return s;
        }
        throw new MalformedURLException("Illegal character in URL");
    }
    
    @Deprecated
    public void writeRequests(final MessageHeader requests) {
        (this.requests = requests).print(this.serverOutput);
        this.serverOutput.flush();
    }
    
    public void writeRequests(final MessageHeader requests, final PosterOutputStream poster) throws IOException {
        (this.requests = requests).print(this.serverOutput);
        this.poster = poster;
        if (this.poster != null) {
            this.poster.writeTo(this.serverOutput);
        }
        this.serverOutput.flush();
    }
    
    public void writeRequests(final MessageHeader messageHeader, final PosterOutputStream posterOutputStream, final boolean streaming) throws IOException {
        this.streaming = streaming;
        this.writeRequests(messageHeader, posterOutputStream);
    }
    
    public boolean parseHTTP(final MessageHeader messageHeader, final ProgressSource progressSource, final HttpURLConnection httpURLConnection) throws IOException {
        try {
            this.serverInput = this.serverSocket.getInputStream();
            if (this.capture != null) {
                this.serverInput = new HttpCaptureInputStream(this.serverInput, this.capture);
            }
            this.serverInput = new BufferedInputStream(this.serverInput);
            return this.parseHTTPHeader(messageHeader, progressSource, httpURLConnection);
        }
        catch (final SocketTimeoutException ex) {
            if (this.ignoreContinue) {
                this.closeServer();
            }
            throw ex;
        }
        catch (final IOException ex2) {
            this.closeServer();
            this.cachedHttpClient = false;
            if (!this.failedOnce && this.requests != null) {
                this.failedOnce = true;
                if (!this.getRequestMethod().equals("CONNECT") && !this.streaming) {
                    if (!httpURLConnection.getRequestMethod().equals("POST") || HttpClient.retryPostProp) {
                        this.openServer();
                        this.checkTunneling(httpURLConnection);
                        this.afterConnect();
                        this.writeRequests(this.requests, this.poster);
                        return this.parseHTTP(messageHeader, progressSource, httpURLConnection);
                    }
                }
            }
            throw ex2;
        }
    }
    
    private void checkTunneling(final HttpURLConnection httpURLConnection) throws IOException {
        if (this.needsTunneling()) {
            final MessageHeader requests = this.requests;
            final PosterOutputStream poster = this.poster;
            httpURLConnection.doTunneling();
            this.requests = requests;
            this.poster = poster;
        }
    }
    
    private boolean parseHTTPHeader(final MessageHeader messageHeader, final ProgressSource progressSource, final HttpURLConnection httpURLConnection) throws IOException {
        this.keepAliveConnections = -1;
        this.keepAliveTimeout = 0;
        final byte[] array = new byte[8];
        boolean b;
        try {
            int i = 0;
            this.serverInput.mark(10);
            while (i < 8) {
                final int read = this.serverInput.read(array, i, 8 - i);
                if (read < 0) {
                    break;
                }
                i += read;
            }
            String s = null;
            String s2 = null;
            b = (array[0] == 72 && array[1] == 84 && array[2] == 84 && array[3] == 80 && array[4] == 47 && array[5] == 49 && array[6] == 46);
            this.serverInput.reset();
            if (b) {
                messageHeader.parseHeader(this.serverInput);
                final CookieHandler cookieHandler = httpURLConnection.getCookieHandler();
                if (cookieHandler != null) {
                    final URI uri = ParseUtil.toURI(this.url);
                    if (uri != null) {
                        cookieHandler.put(uri, messageHeader.getHeaders());
                    }
                }
                if (this.usingProxy) {
                    s = messageHeader.findValue("Proxy-Connection");
                    s2 = messageHeader.findValue("Proxy-Authenticate");
                }
                if (s == null) {
                    s = messageHeader.findValue("Connection");
                    s2 = messageHeader.findValue("WWW-Authenticate");
                }
                boolean b2 = !this.disableKeepAlive;
                if (b2 && (!HttpClient.cacheNTLMProp || !HttpClient.cacheSPNEGOProp) && s2 != null) {
                    final String lowerCase = s2.toLowerCase(Locale.US);
                    if (!HttpClient.cacheNTLMProp) {
                        b2 &= !lowerCase.startsWith("ntlm ");
                    }
                    if (!HttpClient.cacheSPNEGOProp) {
                        b2 = (b2 & !lowerCase.startsWith("negotiate ") & !lowerCase.startsWith("kerberos "));
                    }
                }
                this.disableKeepAlive |= !b2;
                if (s != null && s.toLowerCase(Locale.US).equals("keep-alive")) {
                    if (this.disableKeepAlive) {
                        this.keepAliveConnections = 1;
                    }
                    else {
                        final HeaderParser headerParser = new HeaderParser(messageHeader.findValue("Keep-Alive"));
                        this.keepAliveConnections = headerParser.findInt("max", this.usingProxy ? 50 : 5);
                        this.keepAliveTimeout = headerParser.findInt("timeout", this.usingProxy ? 60 : 5);
                    }
                }
                else if (array[7] != 48) {
                    if (s != null || this.disableKeepAlive) {
                        this.keepAliveConnections = 1;
                    }
                    else {
                        this.keepAliveConnections = 5;
                    }
                }
            }
            else {
                if (i != 8) {
                    if (!this.failedOnce && this.requests != null) {
                        this.failedOnce = true;
                        if (!this.getRequestMethod().equals("CONNECT") && !this.streaming) {
                            if (!httpURLConnection.getRequestMethod().equals("POST") || HttpClient.retryPostProp) {
                                this.closeServer();
                                this.cachedHttpClient = false;
                                this.openServer();
                                this.checkTunneling(httpURLConnection);
                                this.afterConnect();
                                this.writeRequests(this.requests, this.poster);
                                return this.parseHTTP(messageHeader, progressSource, httpURLConnection);
                            }
                        }
                    }
                    throw new SocketException("Unexpected end of file from server");
                }
                messageHeader.set("Content-type", "unknown/unknown");
            }
        }
        catch (final IOException ex) {
            throw ex;
        }
        int int1 = -1;
        try {
            String value;
            int index;
            for (value = messageHeader.getValue(0), index = value.indexOf(32); value.charAt(index) == ' '; ++index) {}
            int1 = Integer.parseInt(value.substring(index, index + 3));
        }
        catch (final Exception ex2) {}
        if (int1 == 100 && this.ignoreContinue) {
            messageHeader.reset();
            return this.parseHTTPHeader(messageHeader, progressSource, httpURLConnection);
        }
        long long1 = -1L;
        final String value2 = messageHeader.findValue("Transfer-Encoding");
        if (value2 != null && value2.equalsIgnoreCase("chunked")) {
            this.serverInput = new ChunkedInputStream(this.serverInput, this, messageHeader);
            if (this.keepAliveConnections <= 1) {
                this.keepAliveConnections = 1;
                this.keepingAlive = false;
            }
            else {
                this.keepingAlive = !this.disableKeepAlive;
            }
            this.failedOnce = false;
        }
        else {
            final String value3 = messageHeader.findValue("content-length");
            if (value3 != null) {
                try {
                    long1 = Long.parseLong(value3);
                }
                catch (final NumberFormatException ex3) {
                    long1 = -1L;
                }
            }
            final String key = this.requests.getKey(0);
            if ((key != null && key.startsWith("HEAD")) || int1 == 304 || int1 == 204) {
                long1 = 0L;
            }
            if (this.keepAliveConnections > 1 && (long1 >= 0L || int1 == 304 || int1 == 204)) {
                this.keepingAlive = !this.disableKeepAlive;
                this.failedOnce = false;
            }
            else if (this.keepingAlive) {
                this.keepingAlive = false;
            }
        }
        if (long1 > 0L) {
            if (progressSource != null) {
                progressSource.setContentType(messageHeader.findValue("content-type"));
            }
            if (this.isKeepingAlive() || this.disableKeepAlive) {
                logFinest("KeepAlive stream used: " + this.url);
                this.serverInput = new KeepAliveStream(this.serverInput, progressSource, long1, this);
                this.failedOnce = false;
            }
            else {
                this.serverInput = new MeteredStream(this.serverInput, progressSource, long1);
            }
        }
        else if (long1 == -1L) {
            if (progressSource != null) {
                progressSource.setContentType(messageHeader.findValue("content-type"));
                this.serverInput = new MeteredStream(this.serverInput, progressSource, long1);
            }
        }
        else if (progressSource != null) {
            progressSource.finishTracking();
        }
        return b;
    }
    
    public synchronized InputStream getInputStream() {
        return this.serverInput;
    }
    
    public OutputStream getOutputStream() {
        return this.serverOutput;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.url + ")";
    }
    
    public final boolean isKeepingAlive() {
        return this.getHttpKeepAliveSet() && this.keepingAlive;
    }
    
    public void setCacheRequest(final CacheRequest cacheRequest) {
        this.cacheRequest = cacheRequest;
    }
    
    CacheRequest getCacheRequest() {
        return this.cacheRequest;
    }
    
    String getRequestMethod() {
        if (this.requests != null) {
            final String key = this.requests.getKey(0);
            if (key != null) {
                return key.split("\\s+")[0];
            }
        }
        return "";
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
    
    public void setDoNotRetry(final boolean failedOnce) {
        this.failedOnce = failedOnce;
    }
    
    public void setIgnoreContinue(final boolean ignoreContinue) {
        this.ignoreContinue = ignoreContinue;
    }
    
    @Override
    public void closeServer() {
        try {
            this.keepingAlive = false;
            this.serverSocket.close();
        }
        catch (final Exception ex) {}
    }
    
    public String getProxyHostUsed() {
        if (!this.usingProxy) {
            return null;
        }
        return ((InetSocketAddress)this.proxy.address()).getHostString();
    }
    
    public int getProxyPortUsed() {
        if (this.usingProxy) {
            return ((InetSocketAddress)this.proxy.address()).getPort();
        }
        return -1;
    }
    
    static {
        HttpClient.kac = new KeepAliveCache();
        HttpClient.keepAliveProp = true;
        HttpClient.retryPostProp = true;
        logger = HttpURLConnection.getHttpLogger();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.keepAlive"));
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.net.http.retryPost"));
        final String s3 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.ntlm.cache"));
        final String s4 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.spnego.cache"));
        if (s != null) {
            HttpClient.keepAliveProp = Boolean.valueOf(s);
        }
        else {
            HttpClient.keepAliveProp = true;
        }
        if (s2 != null) {
            HttpClient.retryPostProp = Boolean.valueOf(s2);
        }
        else {
            HttpClient.retryPostProp = true;
        }
        if (s3 != null) {
            cacheNTLMProp = Boolean.parseBoolean(s3);
        }
        else {
            cacheNTLMProp = true;
        }
        if (s4 != null) {
            cacheSPNEGOProp = Boolean.parseBoolean(s4);
        }
        else {
            cacheSPNEGOProp = true;
        }
    }
}
