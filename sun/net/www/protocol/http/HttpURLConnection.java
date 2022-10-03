package sun.net.www.protocol.http;

import java.nio.ByteBuffer;
import java.io.FilterOutputStream;
import java.io.FilterInputStream;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.action.GetIntegerAction;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import sun.misc.JavaNetHttpCookieAccess;
import java.net.HttpCookie;
import sun.misc.SharedSecrets;
import sun.net.www.MeteredStream;
import sun.net.www.http.ChunkedInputStream;
import java.net.CacheRequest;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import sun.net.www.HeaderParser;
import java.net.HttpRetryException;
import sun.net.ProgressMonitor;
import sun.net.www.http.ChunkedOutputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.net.URI;
import java.net.ProxySelector;
import java.net.SecureCacheResponse;
import sun.net.www.ParseUtil;
import java.net.URLPermission;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.net.UnknownHostException;
import java.net.URLConnection;
import sun.net.ApplicationProxy;
import sun.net.util.IPAddressUtil;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.ProtocolException;
import java.security.PrivilegedAction;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.URL;
import java.net.InetAddress;
import java.util.Locale;
import java.util.HashSet;
import java.util.Collections;
import java.security.AccessController;
import sun.net.NetProperties;
import java.util.List;
import java.util.Map;
import sun.util.logging.PlatformLogger;
import java.net.SocketPermission;
import sun.net.www.http.PosterOutputStream;
import sun.net.ProgressSource;
import java.io.PrintStream;
import java.io.InputStream;
import sun.net.www.MessageHeader;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.CookieHandler;
import java.net.Proxy;
import sun.net.www.http.HttpClient;
import java.util.Set;

public class HttpURLConnection extends java.net.HttpURLConnection
{
    static String HTTP_CONNECT;
    static final String version;
    public static final String userAgent;
    static final int defaultmaxRedirects = 20;
    static final int maxRedirects;
    static final boolean validateProxy;
    static final boolean validateServer;
    static final Set<String> disabledProxyingSchemes;
    static final Set<String> disabledTunnelingSchemes;
    private StreamingOutputStream strOutputStream;
    private static final String RETRY_MSG1 = "cannot retry due to proxy authentication, in streaming mode";
    private static final String RETRY_MSG2 = "cannot retry due to server authentication, in streaming mode";
    private static final String RETRY_MSG3 = "cannot retry due to redirection, in streaming mode";
    private static boolean enableESBuffer;
    private static int timeout4ESBuffer;
    private static int bufSize4ES;
    private static final boolean allowRestrictedHeaders;
    private static final Set<String> restrictedHeaderSet;
    private static final String[] restrictedHeaders;
    static final String httpVersion = "HTTP/1.1";
    static final String acceptString = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
    private static final String[] EXCLUDE_HEADERS;
    private static final String[] EXCLUDE_HEADERS2;
    protected HttpClient http;
    protected Handler handler;
    protected Proxy instProxy;
    private CookieHandler cookieHandler;
    private final ResponseCache cacheHandler;
    protected CacheResponse cachedResponse;
    private MessageHeader cachedHeaders;
    private InputStream cachedInputStream;
    protected PrintStream ps;
    private InputStream errorStream;
    private boolean setUserCookies;
    private String userCookies;
    private String userCookies2;
    @Deprecated
    private static HttpAuthenticator defaultAuth;
    private MessageHeader requests;
    private MessageHeader userHeaders;
    private boolean connecting;
    String domain;
    DigestAuthentication.Parameters digestparams;
    AuthenticationInfo currentProxyCredentials;
    AuthenticationInfo currentServerCredentials;
    boolean needToCheck;
    private boolean doingNTLM2ndStage;
    private boolean doingNTLMp2ndStage;
    private boolean tryTransparentNTLMServer;
    private boolean tryTransparentNTLMProxy;
    private boolean useProxyResponseCode;
    private Object authObj;
    boolean isUserServerAuth;
    boolean isUserProxyAuth;
    String serverAuthKey;
    String proxyAuthKey;
    protected ProgressSource pi;
    private MessageHeader responses;
    private InputStream inputStream;
    private PosterOutputStream poster;
    private boolean setRequests;
    private boolean failedOnce;
    private Exception rememberedException;
    private HttpClient reuseClient;
    private TunnelState tunnelState;
    private int connectTimeout;
    private int readTimeout;
    private SocketPermission socketPermission;
    private static final PlatformLogger logger;
    String requestURI;
    byte[] cdata;
    private static final String SET_COOKIE = "set-cookie";
    private static final String SET_COOKIE2 = "set-cookie2";
    private Map<String, List<String>> filteredHeaders;
    
    private static String getNetProperty(final String s) {
        return AccessController.doPrivileged(() -> NetProperties.get(s2));
    }
    
    private static Set<String> schemesListToSet(final String s) {
        if (s == null || s.isEmpty()) {
            return Collections.emptySet();
        }
        final HashSet set = new HashSet();
        final String[] split = s.split("\\s*,\\s*");
        for (int length = split.length, i = 0; i < length; ++i) {
            set.add(split[i].toLowerCase(Locale.ROOT));
        }
        return set;
    }
    
    private static PasswordAuthentication privilegedRequestPasswordAuthentication(final String s, final InetAddress inetAddress, final int n, final String s2, final String s3, final String s4, final URL url, final Authenticator.RequestorType requestorType) {
        return AccessController.doPrivileged((PrivilegedAction<PasswordAuthentication>)new PrivilegedAction<PasswordAuthentication>() {
            @Override
            public PasswordAuthentication run() {
                if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                    HttpURLConnection.logger.finest("Requesting Authentication: host =" + s + " url = " + url);
                }
                final PasswordAuthentication requestPasswordAuthentication = Authenticator.requestPasswordAuthentication(s, inetAddress, n, s2, s3, s4, url, requestorType);
                if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                    HttpURLConnection.logger.finest("Authentication returned: " + ((requestPasswordAuthentication != null) ? requestPasswordAuthentication.toString() : "null"));
                }
                return requestPasswordAuthentication;
            }
        });
    }
    
    private boolean isRestrictedHeader(String lowerCase, final String s) {
        if (HttpURLConnection.allowRestrictedHeaders) {
            return false;
        }
        lowerCase = lowerCase.toLowerCase();
        if (HttpURLConnection.restrictedHeaderSet.contains(lowerCase)) {
            return !lowerCase.equals("connection") || !s.equalsIgnoreCase("close");
        }
        return lowerCase.startsWith("sec-");
    }
    
    private boolean isExternalMessageHeaderAllowed(final String s, final String s2) {
        this.checkMessageHeader(s, s2);
        return !this.isRestrictedHeader(s, s2);
    }
    
    public static PlatformLogger getHttpLogger() {
        return HttpURLConnection.logger;
    }
    
    public Object authObj() {
        return this.authObj;
    }
    
    public void authObj(final Object authObj) {
        this.authObj = authObj;
    }
    
    private void checkMessageHeader(final String s, final String s2) {
        final int n = 10;
        final int index = s.indexOf(n);
        final int index2 = s.indexOf(58);
        if (index != -1 || index2 != -1) {
            throw new IllegalArgumentException("Illegal character(s) in message header field: " + s);
        }
        if (s2 == null) {
            return;
        }
        int i = s2.indexOf(n);
        while (i != -1) {
            if (++i < s2.length()) {
                final char char1 = s2.charAt(i);
                if (char1 == ' ' || char1 == '\t') {
                    i = s2.indexOf(n, i);
                    continue;
                }
            }
            throw new IllegalArgumentException("Illegal character(s) in message header value: " + s2);
        }
    }
    
    @Override
    public synchronized void setRequestMethod(final String requestMethod) throws ProtocolException {
        if (this.connecting) {
            throw new IllegalStateException("connect in progress");
        }
        super.setRequestMethod(requestMethod);
    }
    
    private void writeRequests() throws IOException {
        if (this.http.usingProxy && this.tunnelState() != TunnelState.TUNNELING) {
            this.setPreemptiveProxyAuthentication(this.requests);
        }
        if (!this.setRequests) {
            if (!this.failedOnce) {
                this.checkURLFile();
                this.requests.prepend(this.method + " " + this.getRequestURI() + " " + "HTTP/1.1", null);
            }
            if (!this.getUseCaches()) {
                this.requests.setIfNotSet("Cache-Control", "no-cache");
                this.requests.setIfNotSet("Pragma", "no-cache");
            }
            this.requests.setIfNotSet("User-Agent", HttpURLConnection.userAgent);
            final int port = this.url.getPort();
            String s = this.url.getHost();
            if (port != -1 && port != this.url.getDefaultPort()) {
                s = s + ":" + String.valueOf(port);
            }
            final String value = this.requests.findValue("Host");
            if (value == null || (!value.equalsIgnoreCase(s) && !this.checkSetHost())) {
                this.requests.set("Host", s);
            }
            this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            if (!this.failedOnce && this.http.getHttpKeepAliveSet()) {
                if (this.http.usingProxy && this.tunnelState() != TunnelState.TUNNELING) {
                    this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
                }
                else {
                    this.requests.setIfNotSet("Connection", "keep-alive");
                }
            }
            else {
                this.requests.setIfNotSet("Connection", "close");
            }
            final long ifModifiedSince = this.getIfModifiedSince();
            if (ifModifiedSince != 0L) {
                final Date date = new Date(ifModifiedSince);
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                this.requests.setIfNotSet("If-Modified-Since", simpleDateFormat.format(date));
            }
            final AuthenticationInfo serverAuth = AuthenticationInfo.getServerAuth(this.url);
            if (serverAuth != null && serverAuth.supportsPreemptiveAuthorization()) {
                this.requests.setIfNotSet(serverAuth.getHeaderName(), serverAuth.getHeaderValue(this.url, this.method));
                this.currentServerCredentials = serverAuth;
            }
            if (!this.method.equals("PUT") && (this.poster != null || this.streaming())) {
                this.requests.setIfNotSet("Content-type", "application/x-www-form-urlencoded");
            }
            boolean b = false;
            if (this.streaming()) {
                if (this.chunkLength != -1) {
                    this.requests.set("Transfer-Encoding", "chunked");
                    b = true;
                }
                else if (this.fixedContentLengthLong != -1L) {
                    this.requests.set("Content-Length", String.valueOf(this.fixedContentLengthLong));
                }
                else if (this.fixedContentLength != -1) {
                    this.requests.set("Content-Length", String.valueOf(this.fixedContentLength));
                }
            }
            else if (this.poster != null) {
                synchronized (this.poster) {
                    this.poster.close();
                    this.requests.set("Content-Length", String.valueOf(this.poster.size()));
                }
            }
            if (!b && this.requests.findValue("Transfer-Encoding") != null) {
                this.requests.remove("Transfer-Encoding");
                if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.WARNING)) {
                    HttpURLConnection.logger.warning("use streaming mode for chunked encoding");
                }
            }
            this.setCookieHeader();
            this.setRequests = true;
        }
        if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
            HttpURLConnection.logger.fine(this.requests.toString());
        }
        this.http.writeRequests(this.requests, this.poster, this.streaming());
        if (this.ps.checkError()) {
            final String proxyHostUsed = this.http.getProxyHostUsed();
            final int proxyPortUsed = this.http.getProxyPortUsed();
            this.disconnectInternal();
            if (this.failedOnce) {
                throw new IOException("Error writing to server");
            }
            this.failedOnce = true;
            if (proxyHostUsed != null) {
                this.setProxiedClient(this.url, proxyHostUsed, proxyPortUsed);
            }
            else {
                this.setNewClient(this.url);
            }
            this.ps = (PrintStream)this.http.getOutputStream();
            this.connected = true;
            this.responses = new MessageHeader();
            this.setRequests = false;
            this.writeRequests();
        }
    }
    
    private boolean checkSetHost() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final String name = securityManager.getClass().getName();
            if (name.equals("sun.plugin2.applet.AWTAppletSecurityManager") || name.equals("sun.plugin2.applet.FXAppletSecurityManager") || name.equals("com.sun.javaws.security.JavaWebStartSecurity") || name.equals("sun.plugin.security.ActivatorSecurityManager")) {
                final int n = -2;
                try {
                    securityManager.checkConnect(this.url.toExternalForm(), n);
                }
                catch (final SecurityException ex) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void checkURLFile() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final String name = securityManager.getClass().getName();
            if (name.equals("sun.plugin2.applet.AWTAppletSecurityManager") || name.equals("sun.plugin2.applet.FXAppletSecurityManager") || name.equals("com.sun.javaws.security.JavaWebStartSecurity") || name.equals("sun.plugin.security.ActivatorSecurityManager")) {
                final int n = -3;
                try {
                    securityManager.checkConnect(this.url.toExternalForm(), n);
                }
                catch (final SecurityException ex) {
                    throw new SecurityException("denied access outside a permitted URL subpath", ex);
                }
            }
        }
    }
    
    protected void setNewClient(final URL url) throws IOException {
        this.setNewClient(url, false);
    }
    
    protected void setNewClient(final URL url, final boolean b) throws IOException {
        (this.http = HttpClient.New(url, null, -1, b, this.connectTimeout, this)).setReadTimeout(this.readTimeout);
    }
    
    protected void setProxiedClient(final URL url, final String s, final int n) throws IOException {
        this.setProxiedClient(url, s, n, false);
    }
    
    protected void setProxiedClient(final URL url, final String s, final int n, final boolean b) throws IOException {
        this.proxiedConnect(url, s, n, b);
    }
    
    protected void proxiedConnect(final URL url, final String s, final int n, final boolean b) throws IOException {
        (this.http = HttpClient.New(url, s, n, b, this.connectTimeout, this)).setReadTimeout(this.readTimeout);
    }
    
    protected HttpURLConnection(final URL url, final Handler handler) throws IOException {
        this(url, null, handler);
    }
    
    private static String checkHost(final String s) throws IOException {
        if (s != null && s.indexOf(10) > -1) {
            throw new MalformedURLException("Illegal character in host");
        }
        return s;
    }
    
    public HttpURLConnection(final URL url, final String s, final int n) throws IOException {
        this(url, new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(checkHost(s), n)));
    }
    
    public HttpURLConnection(final URL url, final Proxy proxy) throws IOException {
        this(url, proxy, new Handler());
    }
    
    private static URL checkURL(final URL url) throws IOException {
        if (url != null && url.toExternalForm().indexOf(10) > -1) {
            throw new MalformedURLException("Illegal character in URL");
        }
        final String checkAuthority = IPAddressUtil.checkAuthority(url);
        if (checkAuthority != null) {
            throw new MalformedURLException(checkAuthority);
        }
        return url;
    }
    
    protected HttpURLConnection(final URL url, final Proxy instProxy, final Handler handler) throws IOException {
        super(checkURL(url));
        this.ps = null;
        this.errorStream = null;
        this.setUserCookies = true;
        this.userCookies = null;
        this.userCookies2 = null;
        this.connecting = false;
        this.currentProxyCredentials = null;
        this.currentServerCredentials = null;
        this.needToCheck = true;
        this.doingNTLM2ndStage = false;
        this.doingNTLMp2ndStage = false;
        this.tryTransparentNTLMServer = true;
        this.tryTransparentNTLMProxy = true;
        this.useProxyResponseCode = false;
        this.inputStream = null;
        this.poster = null;
        this.setRequests = false;
        this.failedOnce = false;
        this.rememberedException = null;
        this.reuseClient = null;
        this.tunnelState = TunnelState.NONE;
        this.connectTimeout = -1;
        this.readTimeout = -1;
        this.requestURI = null;
        this.cdata = new byte[128];
        this.requests = new MessageHeader();
        this.responses = new MessageHeader();
        this.userHeaders = new MessageHeader();
        this.handler = handler;
        this.instProxy = instProxy;
        if (this.instProxy instanceof ApplicationProxy) {
            try {
                this.cookieHandler = CookieHandler.getDefault();
            }
            catch (final SecurityException ex) {}
        }
        else {
            this.cookieHandler = AccessController.doPrivileged((PrivilegedAction<CookieHandler>)new PrivilegedAction<CookieHandler>() {
                @Override
                public CookieHandler run() {
                    return CookieHandler.getDefault();
                }
            });
        }
        this.cacheHandler = AccessController.doPrivileged((PrivilegedAction<ResponseCache>)new PrivilegedAction<ResponseCache>() {
            @Override
            public ResponseCache run() {
                return ResponseCache.getDefault();
            }
        });
    }
    
    @Deprecated
    public static void setDefaultAuthenticator(final HttpAuthenticator defaultAuth) {
        HttpURLConnection.defaultAuth = defaultAuth;
    }
    
    public static InputStream openConnectionCheckRedirects(URLConnection openConnection) throws IOException {
        int n = 0;
        boolean b;
        InputStream inputStream;
        do {
            if (openConnection instanceof HttpURLConnection) {
                ((HttpURLConnection)openConnection).setInstanceFollowRedirects(false);
            }
            inputStream = openConnection.getInputStream();
            b = false;
            if (openConnection instanceof HttpURLConnection) {
                final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
                final int responseCode = httpURLConnection.getResponseCode();
                if (responseCode < 300 || responseCode > 307 || responseCode == 306 || responseCode == 304) {
                    continue;
                }
                final URL url = httpURLConnection.getURL();
                final String headerField = httpURLConnection.getHeaderField("Location");
                URL url2 = null;
                if (headerField != null) {
                    url2 = new URL(url, headerField);
                }
                httpURLConnection.disconnect();
                if (url2 == null || !url.getProtocol().equals(url2.getProtocol()) || url.getPort() != url2.getPort() || !hostsEqual(url, url2) || n >= 5) {
                    throw new SecurityException("illegal URL redirect");
                }
                b = true;
                openConnection = url2.openConnection();
                ++n;
            }
        } while (b);
        return inputStream;
    }
    
    private static boolean hostsEqual(final URL url, final URL url2) {
        final String host = url.getHost();
        final String host2 = url2.getHost();
        if (host == null) {
            return host2 == null;
        }
        if (host2 == null) {
            return false;
        }
        if (host.equalsIgnoreCase(host2)) {
            return true;
        }
        final boolean[] array = { false };
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    array[0] = InetAddress.getByName(host).equals(InetAddress.getByName(host2));
                }
                catch (final UnknownHostException | SecurityException ex) {}
                return null;
            }
        });
        return array[0];
    }
    
    @Override
    public void connect() throws IOException {
        synchronized (this) {
            this.connecting = true;
        }
        this.plainConnect();
    }
    
    private boolean checkReuseConnection() {
        if (this.connected) {
            return true;
        }
        if (this.reuseClient != null) {
            (this.http = this.reuseClient).setReadTimeout(this.getReadTimeout());
            this.http.reuse = false;
            this.reuseClient = null;
            return this.connected = true;
        }
        return false;
    }
    
    private String getHostAndPort(final URL url) {
        final String host;
        String s = host = url.getHost();
        try {
            s = AccessController.doPrivileged((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws IOException {
                    return InetAddress.getByName(host).getHostAddress();
                }
            });
        }
        catch (final PrivilegedActionException ex) {}
        final int port = url.getPort();
        if (port != -1) {
            return s + ":" + Integer.toString(port);
        }
        if ("http".equals(url.getProtocol())) {
            return s + ":80";
        }
        return s + ":443";
    }
    
    protected void plainConnect() throws IOException {
        synchronized (this) {
            if (this.connected) {
                return;
            }
        }
        final SocketPermission urLtoSocketPermission = this.URLtoSocketPermission(this.url);
        if (urLtoSocketPermission != null) {
            try {
                AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws IOException {
                        HttpURLConnection.this.plainConnect0();
                        return null;
                    }
                }, null, urLtoSocketPermission);
                return;
            }
            catch (final PrivilegedActionException ex) {
                throw (IOException)ex.getException();
            }
        }
        this.plainConnect0();
    }
    
    SocketPermission URLtoSocketPermission(final URL url) throws IOException {
        if (this.socketPermission != null) {
            return this.socketPermission;
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return null;
        }
        final SocketPermission socketPermission = new SocketPermission(this.getHostAndPort(url), "connect");
        final URLPermission urlPermission = new URLPermission(url.getProtocol() + "://" + url.getAuthority() + url.getPath(), this.getRequestMethod() + ":" + this.getUserSetHeaders().getHeaderNamesInList());
        try {
            securityManager.checkPermission(urlPermission);
            return this.socketPermission = socketPermission;
        }
        catch (final SecurityException ex) {
            return null;
        }
    }
    
    protected void plainConnect0() throws IOException {
        if (this.cacheHandler != null && this.getUseCaches()) {
            try {
                final URI uri = ParseUtil.toURI(this.url);
                if (uri != null) {
                    this.cachedResponse = this.cacheHandler.get(uri, this.getRequestMethod(), this.getUserSetHeaders().getHeaders());
                    if ("https".equalsIgnoreCase(uri.getScheme()) && !(this.cachedResponse instanceof SecureCacheResponse)) {
                        this.cachedResponse = null;
                    }
                    if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                        HttpURLConnection.logger.finest("Cache Request for " + uri + " / " + this.getRequestMethod());
                        HttpURLConnection.logger.finest("From cache: " + ((this.cachedResponse != null) ? this.cachedResponse.toString() : "null"));
                    }
                    if (this.cachedResponse != null) {
                        this.cachedHeaders = this.mapToMessageHeader(this.cachedResponse.getHeaders());
                        this.cachedInputStream = this.cachedResponse.getBody();
                    }
                }
            }
            catch (final IOException ex) {}
            if (this.cachedHeaders != null && this.cachedInputStream != null) {
                this.connected = true;
                return;
            }
            this.cachedResponse = null;
        }
        try {
            if (this.instProxy == null) {
                final ProxySelector proxySelector = AccessController.doPrivileged((PrivilegedAction<ProxySelector>)new PrivilegedAction<ProxySelector>() {
                    @Override
                    public ProxySelector run() {
                        return ProxySelector.getDefault();
                    }
                });
                if (proxySelector != null) {
                    final URI uri2 = ParseUtil.toURI(this.url);
                    if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                        HttpURLConnection.logger.finest("ProxySelector Request for " + uri2);
                    }
                    final Iterator<Proxy> iterator = proxySelector.select(uri2).iterator();
                    while (iterator.hasNext()) {
                        final Proxy proxy = iterator.next();
                        try {
                            if (!this.failedOnce) {
                                (this.http = this.getNewHttpClient(this.url, proxy, this.connectTimeout)).setReadTimeout(this.readTimeout);
                            }
                            else {
                                (this.http = this.getNewHttpClient(this.url, proxy, this.connectTimeout, false)).setReadTimeout(this.readTimeout);
                            }
                            if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST) && proxy != null) {
                                HttpURLConnection.logger.finest("Proxy used: " + proxy.toString());
                            }
                        }
                        catch (final IOException ex2) {
                            if (proxy == Proxy.NO_PROXY) {
                                throw ex2;
                            }
                            proxySelector.connectFailed(uri2, proxy.address(), ex2);
                            if (iterator.hasNext()) {
                                continue;
                            }
                            (this.http = this.getNewHttpClient(this.url, null, this.connectTimeout, false)).setReadTimeout(this.readTimeout);
                        }
                        break;
                    }
                }
                else if (!this.failedOnce) {
                    (this.http = this.getNewHttpClient(this.url, null, this.connectTimeout)).setReadTimeout(this.readTimeout);
                }
                else {
                    (this.http = this.getNewHttpClient(this.url, null, this.connectTimeout, false)).setReadTimeout(this.readTimeout);
                }
            }
            else if (!this.failedOnce) {
                (this.http = this.getNewHttpClient(this.url, this.instProxy, this.connectTimeout)).setReadTimeout(this.readTimeout);
            }
            else {
                (this.http = this.getNewHttpClient(this.url, this.instProxy, this.connectTimeout, false)).setReadTimeout(this.readTimeout);
            }
            this.ps = (PrintStream)this.http.getOutputStream();
        }
        catch (final IOException ex3) {
            throw ex3;
        }
        this.connected = true;
    }
    
    protected HttpClient getNewHttpClient(final URL url, final Proxy proxy, final int n) throws IOException {
        return HttpClient.New(url, proxy, n, this);
    }
    
    protected HttpClient getNewHttpClient(final URL url, final Proxy proxy, final int n, final boolean b) throws IOException {
        return HttpClient.New(url, proxy, n, b, this);
    }
    
    private void expect100Continue() throws IOException {
        final int readTimeout = this.http.getReadTimeout();
        boolean b = false;
        boolean b2 = false;
        if (readTimeout <= 0) {
            this.http.setReadTimeout(5000);
            b = true;
        }
        try {
            this.http.parseHTTP(this.responses, this.pi, this);
        }
        catch (final SocketTimeoutException ex) {
            if (!b) {
                throw ex;
            }
            b2 = true;
            this.http.setIgnoreContinue(true);
        }
        if (!b2) {
            final String value = this.responses.getValue(0);
            if (value != null && value.startsWith("HTTP/")) {
                final String[] split = value.split("\\s+");
                this.responseCode = -1;
                try {
                    if (split.length > 1) {
                        this.responseCode = Integer.parseInt(split[1]);
                    }
                }
                catch (final NumberFormatException ex2) {}
            }
            if (this.responseCode != 100) {
                throw new ProtocolException("Server rejected operation");
            }
        }
        this.http.setReadTimeout(readTimeout);
        this.responseCode = -1;
        this.responses.reset();
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        this.connecting = true;
        final SocketPermission urLtoSocketPermission = this.URLtoSocketPermission(this.url);
        if (urLtoSocketPermission != null) {
            try {
                return AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction<OutputStream>)new PrivilegedExceptionAction<OutputStream>() {
                    @Override
                    public OutputStream run() throws IOException {
                        return HttpURLConnection.this.getOutputStream0();
                    }
                }, null, urLtoSocketPermission);
            }
            catch (final PrivilegedActionException ex) {
                throw (IOException)ex.getException();
            }
        }
        return this.getOutputStream0();
    }
    
    private synchronized OutputStream getOutputStream0() throws IOException {
        try {
            if (!this.doOutput) {
                throw new ProtocolException("cannot write to a URLConnection if doOutput=false - call setDoOutput(true)");
            }
            if (this.method.equals("GET")) {
                this.method = "POST";
            }
            if ("TRACE".equals(this.method) && "http".equals(this.url.getProtocol())) {
                throw new ProtocolException("HTTP method TRACE doesn't support output");
            }
            if (this.inputStream != null) {
                throw new ProtocolException("Cannot write output after reading input.");
            }
            if (!this.checkReuseConnection()) {
                this.connect();
            }
            boolean b = false;
            if ("100-Continue".equalsIgnoreCase(this.requests.findValue("Expect")) && this.streaming()) {
                this.http.setIgnoreContinue(false);
                b = true;
            }
            if (this.streaming() && this.strOutputStream == null) {
                this.writeRequests();
            }
            if (b) {
                this.expect100Continue();
            }
            this.ps = (PrintStream)this.http.getOutputStream();
            if (this.streaming()) {
                if (this.strOutputStream == null) {
                    if (this.chunkLength != -1) {
                        this.strOutputStream = new StreamingOutputStream(new ChunkedOutputStream(this.ps, this.chunkLength), -1L);
                    }
                    else {
                        long fixedContentLengthLong = 0L;
                        if (this.fixedContentLengthLong != -1L) {
                            fixedContentLengthLong = this.fixedContentLengthLong;
                        }
                        else if (this.fixedContentLength != -1) {
                            fixedContentLengthLong = this.fixedContentLength;
                        }
                        this.strOutputStream = new StreamingOutputStream(this.ps, fixedContentLengthLong);
                    }
                }
                return this.strOutputStream;
            }
            if (this.poster == null) {
                this.poster = new PosterOutputStream();
            }
            return this.poster;
        }
        catch (final RuntimeException ex) {
            this.disconnectInternal();
            throw ex;
        }
        catch (final ProtocolException ex2) {
            final int responseCode = this.responseCode;
            this.disconnectInternal();
            this.responseCode = responseCode;
            throw ex2;
        }
        catch (final IOException ex3) {
            this.disconnectInternal();
            throw ex3;
        }
    }
    
    public boolean streaming() {
        return this.fixedContentLength != -1 || this.fixedContentLengthLong != -1L || this.chunkLength != -1;
    }
    
    private void setCookieHeader() throws IOException {
        if (this.cookieHandler != null) {
            synchronized (this) {
                if (this.setUserCookies) {
                    final int key = this.requests.getKey("Cookie");
                    if (key != -1) {
                        this.userCookies = this.requests.getValue(key);
                    }
                    final int key2 = this.requests.getKey("Cookie2");
                    if (key2 != -1) {
                        this.userCookies2 = this.requests.getValue(key2);
                    }
                    this.setUserCookies = false;
                }
            }
            this.requests.remove("Cookie");
            this.requests.remove("Cookie2");
            final URI uri = ParseUtil.toURI(this.url);
            if (uri != null) {
                if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                    HttpURLConnection.logger.finest("CookieHandler request for " + uri);
                }
                final Map<String, List<String>> value = this.cookieHandler.get(uri, this.requests.getHeaders(HttpURLConnection.EXCLUDE_HEADERS));
                if (!value.isEmpty()) {
                    if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                        HttpURLConnection.logger.finest("Cookies retrieved: " + value.toString());
                    }
                    for (final Map.Entry entry : value.entrySet()) {
                        final String s = (String)entry.getKey();
                        if (!"Cookie".equalsIgnoreCase(s) && !"Cookie2".equalsIgnoreCase(s)) {
                            continue;
                        }
                        final List list = (List)entry.getValue();
                        if (list == null || list.isEmpty()) {
                            continue;
                        }
                        final StringBuilder sb = new StringBuilder();
                        final Iterator iterator2 = list.iterator();
                        while (iterator2.hasNext()) {
                            sb.append((String)iterator2.next()).append("; ");
                        }
                        try {
                            this.requests.add(s, sb.substring(0, sb.length() - 2));
                        }
                        catch (final StringIndexOutOfBoundsException ex) {}
                    }
                }
            }
            if (this.userCookies != null) {
                final int key3;
                if ((key3 = this.requests.getKey("Cookie")) != -1) {
                    this.requests.set("Cookie", this.requests.getValue(key3) + ";" + this.userCookies);
                }
                else {
                    this.requests.set("Cookie", this.userCookies);
                }
            }
            if (this.userCookies2 != null) {
                final int key4;
                if ((key4 = this.requests.getKey("Cookie2")) != -1) {
                    this.requests.set("Cookie2", this.requests.getValue(key4) + ";" + this.userCookies2);
                }
                else {
                    this.requests.set("Cookie2", this.userCookies2);
                }
            }
        }
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        this.connecting = true;
        final SocketPermission urLtoSocketPermission = this.URLtoSocketPermission(this.url);
        if (urLtoSocketPermission != null) {
            try {
                return AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                    @Override
                    public InputStream run() throws IOException {
                        return HttpURLConnection.this.getInputStream0();
                    }
                }, null, urLtoSocketPermission);
            }
            catch (final PrivilegedActionException ex) {
                throw (IOException)ex.getException();
            }
        }
        return this.getInputStream0();
    }
    
    private synchronized InputStream getInputStream0() throws IOException {
        if (!this.doInput) {
            throw new ProtocolException("Cannot read from URLConnection if doInput=false (call setDoInput(true))");
        }
        if (this.rememberedException != null) {
            if (this.rememberedException instanceof RuntimeException) {
                throw new RuntimeException(this.rememberedException);
            }
            throw this.getChainedException((IOException)this.rememberedException);
        }
        else {
            if (this.inputStream != null) {
                return this.inputStream;
            }
            if (this.streaming()) {
                if (this.strOutputStream == null) {
                    this.getOutputStream();
                }
                this.strOutputStream.close();
                if (!this.strOutputStream.writtenOK()) {
                    throw new IOException("Incomplete output stream");
                }
            }
            int i = 0;
            long long1 = -1L;
            AuthenticationInfo serverAuthentication = null;
            AuthenticationInfo resetProxyAuthentication = null;
            AuthenticationHeader authenticationHeader = null;
            int n = 0;
            int n2 = 0;
            this.isUserServerAuth = (this.requests.getKey("Authorization") != -1);
            this.isUserProxyAuth = (this.requests.getKey("Proxy-Authorization") != -1);
            try {
                do {
                    if (!this.checkReuseConnection()) {
                        this.connect();
                    }
                    if (this.cachedInputStream != null) {
                        return this.cachedInputStream;
                    }
                    if (ProgressMonitor.getDefault().shouldMeterInput(this.url, this.method)) {
                        (this.pi = new ProgressSource(this.url, this.method)).beginTracking();
                    }
                    this.ps = (PrintStream)this.http.getOutputStream();
                    if (!this.streaming()) {
                        this.writeRequests();
                    }
                    this.http.parseHTTP(this.responses, this.pi, this);
                    if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
                        HttpURLConnection.logger.fine(this.responses.toString());
                    }
                    final boolean filterNTLMResponses = this.responses.filterNTLMResponses("WWW-Authenticate");
                    final boolean filterNTLMResponses2 = this.responses.filterNTLMResponses("Proxy-Authenticate");
                    if ((filterNTLMResponses || filterNTLMResponses2) && HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
                        HttpURLConnection.logger.fine(">>>> Headers are filtered");
                        HttpURLConnection.logger.fine(this.responses.toString());
                    }
                    this.inputStream = this.http.getInputStream();
                    final int responseCode = this.getResponseCode();
                    if (responseCode == -1) {
                        this.disconnectInternal();
                        throw new IOException("Invalid Http response");
                    }
                    if (responseCode == 407) {
                        if (this.streaming()) {
                            this.disconnectInternal();
                            throw new HttpRetryException("cannot retry due to proxy authentication, in streaming mode", 407);
                        }
                        boolean b = false;
                        final Iterator<String> multiValueIterator = this.responses.multiValueIterator("Proxy-Authenticate");
                        while (multiValueIterator.hasNext()) {
                            final String trim = multiValueIterator.next().trim();
                            if (trim.equalsIgnoreCase("Negotiate") || trim.equalsIgnoreCase("Kerberos")) {
                                if (n2 == 0) {
                                    n2 = 1;
                                    break;
                                }
                                b = true;
                                this.doingNTLMp2ndStage = false;
                                resetProxyAuthentication = null;
                                break;
                            }
                        }
                        final AuthenticationHeader authenticationHeader2 = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), b, HttpURLConnection.disabledProxyingSchemes);
                        if (!this.doingNTLMp2ndStage) {
                            resetProxyAuthentication = this.resetProxyAuthentication(resetProxyAuthentication, authenticationHeader2);
                            if (resetProxyAuthentication != null) {
                                ++i;
                                this.disconnectInternal();
                                continue;
                            }
                        }
                        else {
                            final String value = this.responses.findValue("Proxy-Authenticate");
                            this.reset();
                            if (!resetProxyAuthentication.setHeaders(this, authenticationHeader2.headerParser(), value)) {
                                this.disconnectInternal();
                                throw new IOException("Authentication failure");
                            }
                            if (serverAuthentication != null && authenticationHeader != null && !serverAuthentication.setHeaders(this, authenticationHeader.headerParser(), value)) {
                                this.disconnectInternal();
                                throw new IOException("Authentication failure");
                            }
                            this.authObj = null;
                            this.doingNTLMp2ndStage = false;
                            continue;
                        }
                    }
                    else {
                        n2 = 0;
                        this.doingNTLMp2ndStage = false;
                        if (!this.isUserProxyAuth) {
                            this.requests.remove("Proxy-Authorization");
                        }
                    }
                    if (resetProxyAuthentication != null) {
                        resetProxyAuthentication.addToCache();
                    }
                    if (responseCode == 401) {
                        if (this.streaming()) {
                            this.disconnectInternal();
                            throw new HttpRetryException("cannot retry due to server authentication, in streaming mode", 401);
                        }
                        boolean b2 = false;
                        final Iterator<String> multiValueIterator2 = this.responses.multiValueIterator("WWW-Authenticate");
                        while (multiValueIterator2.hasNext()) {
                            final String trim2 = multiValueIterator2.next().trim();
                            if (trim2.equalsIgnoreCase("Negotiate") || trim2.equalsIgnoreCase("Kerberos")) {
                                if (n == 0) {
                                    n = 1;
                                    break;
                                }
                                b2 = true;
                                this.doingNTLM2ndStage = false;
                                serverAuthentication = null;
                                break;
                            }
                        }
                        authenticationHeader = new AuthenticationHeader("WWW-Authenticate", this.responses, new HttpCallerInfo(this.url), b2);
                        final String raw = authenticationHeader.raw();
                        if (!this.doingNTLM2ndStage) {
                            if (serverAuthentication != null && serverAuthentication.getAuthScheme() != AuthScheme.NTLM) {
                                if (serverAuthentication.isAuthorizationStale(raw)) {
                                    this.disconnectWeb();
                                    ++i;
                                    this.requests.set(serverAuthentication.getHeaderName(), serverAuthentication.getHeaderValue(this.url, this.method));
                                    this.currentServerCredentials = serverAuthentication;
                                    this.setCookieHeader();
                                    continue;
                                }
                                serverAuthentication.removeFromCache();
                            }
                            serverAuthentication = this.getServerAuthentication(authenticationHeader);
                            if ((this.currentServerCredentials = serverAuthentication) != null) {
                                this.disconnectWeb();
                                ++i;
                                this.setCookieHeader();
                                continue;
                            }
                        }
                        else {
                            this.reset();
                            if (!serverAuthentication.setHeaders(this, null, raw)) {
                                this.disconnectWeb();
                                throw new IOException("Authentication failure");
                            }
                            this.doingNTLM2ndStage = false;
                            this.authObj = null;
                            this.setCookieHeader();
                            continue;
                        }
                    }
                    if (serverAuthentication != null) {
                        if (!(serverAuthentication instanceof DigestAuthentication) || this.domain == null) {
                            if (serverAuthentication instanceof BasicAuthentication) {
                                String path = AuthenticationInfo.reducePath(this.url.getPath());
                                final String path2 = serverAuthentication.path;
                                if (!path2.startsWith(path) || path.length() >= path2.length()) {
                                    path = BasicAuthentication.getRootPath(path2, path);
                                }
                                final BasicAuthentication basicAuthentication = (BasicAuthentication)serverAuthentication.clone();
                                serverAuthentication.removeFromCache();
                                basicAuthentication.path = path;
                                serverAuthentication = basicAuthentication;
                            }
                            serverAuthentication.addToCache();
                        }
                        else {
                            final DigestAuthentication digestAuthentication = (DigestAuthentication)serverAuthentication;
                            final StringTokenizer stringTokenizer = new StringTokenizer(this.domain, " ");
                            final String realm = digestAuthentication.realm;
                            final PasswordAuthentication pw = digestAuthentication.pw;
                            this.digestparams = digestAuthentication.params;
                            while (stringTokenizer.hasMoreTokens()) {
                                final String nextToken = stringTokenizer.nextToken();
                                try {
                                    new DigestAuthentication(false, new URL(this.url, nextToken), realm, "Digest", pw, this.digestparams).addToCache();
                                }
                                catch (final Exception ex) {}
                            }
                        }
                    }
                    n = 0;
                    n2 = 0;
                    this.doingNTLMp2ndStage = false;
                    this.doingNTLM2ndStage = false;
                    if (!this.isUserServerAuth) {
                        this.requests.remove("Authorization");
                    }
                    if (!this.isUserProxyAuth) {
                        this.requests.remove("Proxy-Authorization");
                    }
                    if (responseCode == 200) {
                        this.checkResponseCredentials(false);
                    }
                    else {
                        this.needToCheck = false;
                    }
                    this.needToCheck = true;
                    if (this.followRedirect()) {
                        ++i;
                        this.setCookieHeader();
                    }
                    else {
                        try {
                            long1 = Long.parseLong(this.responses.findValue("content-length"));
                        }
                        catch (final Exception ex2) {}
                        if (this.method.equals("HEAD") || long1 == 0L || responseCode == 304 || responseCode == 204) {
                            if (this.pi != null) {
                                this.pi.finishTracking();
                                this.pi = null;
                            }
                            this.http.finished();
                            this.http = null;
                            this.inputStream = new EmptyInputStream();
                            this.connected = false;
                        }
                        if ((responseCode == 200 || responseCode == 203 || responseCode == 206 || responseCode == 300 || responseCode == 301 || responseCode == 410) && this.cacheHandler != null && this.getUseCaches()) {
                            final URI uri = ParseUtil.toURI(this.url);
                            if (uri != null) {
                                URLConnection urlConnection = this;
                                if ("https".equalsIgnoreCase(uri.getScheme())) {
                                    try {
                                        urlConnection = (URLConnection)this.getClass().getField("httpsURLConnection").get(this);
                                    }
                                    catch (final IllegalAccessException | NoSuchFieldException ex3) {}
                                }
                                final CacheRequest put = this.cacheHandler.put(uri, urlConnection);
                                if (put != null && this.http != null) {
                                    this.http.setCacheRequest(put);
                                    this.inputStream = new HttpInputStream(this.inputStream, put);
                                }
                            }
                        }
                        if (!(this.inputStream instanceof HttpInputStream)) {
                            this.inputStream = new HttpInputStream(this.inputStream);
                        }
                        if (responseCode < 400) {
                            this.poster = null;
                            this.strOutputStream = null;
                            return this.inputStream;
                        }
                        if (responseCode == 404 || responseCode == 410) {
                            throw new FileNotFoundException(this.url.toString());
                        }
                        throw new IOException("Server returned HTTP response code: " + responseCode + " for URL: " + this.url.toString());
                    }
                } while (i < HttpURLConnection.maxRedirects);
                throw new ProtocolException("Server redirected too many  times (" + i + ")");
            }
            catch (final RuntimeException rememberedException) {
                this.disconnectInternal();
                throw this.rememberedException = rememberedException;
            }
            catch (final IOException rememberedException2) {
                this.rememberedException = rememberedException2;
                final String value2 = this.responses.findValue("Transfer-Encoding");
                if (this.http != null && this.http.isKeepingAlive() && HttpURLConnection.enableESBuffer && (long1 > 0L || (value2 != null && value2.equalsIgnoreCase("chunked")))) {
                    this.errorStream = ErrorStream.getErrorStream(this.inputStream, long1, this.http);
                }
                throw rememberedException2;
            }
            finally {
                if (this.proxyAuthKey != null) {
                    AuthenticationInfo.endAuthRequest(this.proxyAuthKey);
                }
                if (this.serverAuthKey != null) {
                    AuthenticationInfo.endAuthRequest(this.serverAuthKey);
                }
            }
        }
    }
    
    private IOException getChainedException(final IOException ex) {
        try {
            final IOException ex2 = AccessController.doPrivileged((PrivilegedExceptionAction<IOException>)new PrivilegedExceptionAction<IOException>() {
                final /* synthetic */ Object[] val$args = { ex.getMessage() };
                
                @Override
                public IOException run() throws Exception {
                    return (IOException)ex.getClass().getConstructor(String.class).newInstance(this.val$args);
                }
            });
            ex2.initCause(ex);
            return ex2;
        }
        catch (final Exception ex3) {
            return ex;
        }
    }
    
    @Override
    public InputStream getErrorStream() {
        if (this.connected && this.responseCode >= 400) {
            if (this.errorStream != null) {
                return this.errorStream;
            }
            if (this.inputStream != null) {
                return this.inputStream;
            }
        }
        return null;
    }
    
    private AuthenticationInfo resetProxyAuthentication(AuthenticationInfo httpProxyAuthentication, final AuthenticationHeader authenticationHeader) throws IOException {
        if (httpProxyAuthentication != null && httpProxyAuthentication.getAuthScheme() != AuthScheme.NTLM) {
            if (httpProxyAuthentication.isAuthorizationStale(authenticationHeader.raw())) {
                String s;
                if (httpProxyAuthentication instanceof DigestAuthentication) {
                    final DigestAuthentication digestAuthentication = (DigestAuthentication)httpProxyAuthentication;
                    if (this.tunnelState() == TunnelState.SETUP) {
                        s = digestAuthentication.getHeaderValue(connectRequestURI(this.url), HttpURLConnection.HTTP_CONNECT);
                    }
                    else {
                        s = digestAuthentication.getHeaderValue(this.getRequestURI(), this.method);
                    }
                }
                else {
                    s = httpProxyAuthentication.getHeaderValue(this.url, this.method);
                }
                this.requests.set(httpProxyAuthentication.getHeaderName(), s);
                return this.currentProxyCredentials = httpProxyAuthentication;
            }
            httpProxyAuthentication.removeFromCache();
        }
        httpProxyAuthentication = this.getHttpProxyAuthentication(authenticationHeader);
        return this.currentProxyCredentials = httpProxyAuthentication;
    }
    
    TunnelState tunnelState() {
        return this.tunnelState;
    }
    
    public void setTunnelState(final TunnelState tunnelState) {
        this.tunnelState = tunnelState;
    }
    
    public synchronized void doTunneling() throws IOException {
        int i = 0;
        AuthenticationInfo resetProxyAuthentication = null;
        String proxyHostUsed = null;
        int proxyPortUsed = -1;
        final MessageHeader requests = this.requests;
        this.requests = new MessageHeader();
        int n = 0;
        try {
            this.setTunnelState(TunnelState.SETUP);
            String value;
            int int1;
            do {
                if (!this.checkReuseConnection()) {
                    this.proxiedConnect(this.url, proxyHostUsed, proxyPortUsed, false);
                }
                this.sendCONNECTRequest();
                this.responses.reset();
                this.http.parseHTTP(this.responses, null, this);
                if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
                    HttpURLConnection.logger.fine(this.responses.toString());
                }
                if (this.responses.filterNTLMResponses("Proxy-Authenticate") && HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
                    HttpURLConnection.logger.fine(">>>> Headers are filtered");
                    HttpURLConnection.logger.fine(this.responses.toString());
                }
                value = this.responses.getValue(0);
                final StringTokenizer stringTokenizer = new StringTokenizer(value);
                stringTokenizer.nextToken();
                int1 = Integer.parseInt(stringTokenizer.nextToken().trim());
                if (int1 == 407) {
                    boolean b = false;
                    final Iterator<String> multiValueIterator = this.responses.multiValueIterator("Proxy-Authenticate");
                    while (multiValueIterator.hasNext()) {
                        final String trim = multiValueIterator.next().trim();
                        if (trim.equalsIgnoreCase("Negotiate") || trim.equalsIgnoreCase("Kerberos")) {
                            if (n == 0) {
                                n = 1;
                                break;
                            }
                            b = true;
                            this.doingNTLMp2ndStage = false;
                            resetProxyAuthentication = null;
                            break;
                        }
                    }
                    final AuthenticationHeader authenticationHeader = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), b, HttpURLConnection.disabledTunnelingSchemes);
                    if (!this.doingNTLMp2ndStage) {
                        resetProxyAuthentication = this.resetProxyAuthentication(resetProxyAuthentication, authenticationHeader);
                        if (resetProxyAuthentication != null) {
                            proxyHostUsed = this.http.getProxyHostUsed();
                            proxyPortUsed = this.http.getProxyPortUsed();
                            this.disconnectInternal();
                            ++i;
                            continue;
                        }
                    }
                    else {
                        final String value2 = this.responses.findValue("Proxy-Authenticate");
                        this.reset();
                        if (!resetProxyAuthentication.setHeaders(this, authenticationHeader.headerParser(), value2)) {
                            this.disconnectInternal();
                            throw new IOException("Authentication failure");
                        }
                        this.authObj = null;
                        this.doingNTLMp2ndStage = false;
                        continue;
                    }
                }
                if (resetProxyAuthentication != null) {
                    resetProxyAuthentication.addToCache();
                }
                if (int1 == 200) {
                    this.setTunnelState(TunnelState.TUNNELING);
                    break;
                }
                this.disconnectInternal();
                this.setTunnelState(TunnelState.NONE);
                break;
            } while (i < HttpURLConnection.maxRedirects);
            if (i >= HttpURLConnection.maxRedirects || int1 != 200) {
                if (int1 != 407) {
                    this.responses.reset();
                }
                throw new IOException("Unable to tunnel through proxy. Proxy returns \"" + value + "\"");
            }
        }
        finally {
            if (this.proxyAuthKey != null) {
                AuthenticationInfo.endAuthRequest(this.proxyAuthKey);
            }
        }
        this.requests = requests;
        this.responses.reset();
    }
    
    static String connectRequestURI(final URL url) {
        final String host = url.getHost();
        final int port = url.getPort();
        return host + ":" + ((port != -1) ? port : url.getDefaultPort());
    }
    
    private void sendCONNECTRequest() throws IOException {
        final int port = this.url.getPort();
        this.requests.set(0, HttpURLConnection.HTTP_CONNECT + " " + connectRequestURI(this.url) + " " + "HTTP/1.1", null);
        this.requests.setIfNotSet("User-Agent", HttpURLConnection.userAgent);
        String s = this.url.getHost();
        if (port != -1 && port != this.url.getDefaultPort()) {
            s = s + ":" + String.valueOf(port);
        }
        this.requests.setIfNotSet("Host", s);
        this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
        if (this.http.getHttpKeepAliveSet()) {
            this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
        }
        this.setPreemptiveProxyAuthentication(this.requests);
        if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
            HttpURLConnection.logger.fine(this.requests.toString());
        }
        this.http.writeRequests(this.requests, null);
    }
    
    private void setPreemptiveProxyAuthentication(final MessageHeader messageHeader) throws IOException {
        final AuthenticationInfo proxyAuth = AuthenticationInfo.getProxyAuth(this.http.getProxyHostUsed(), this.http.getProxyPortUsed());
        if (proxyAuth != null && proxyAuth.supportsPreemptiveAuthorization()) {
            String s;
            if (proxyAuth instanceof DigestAuthentication) {
                final DigestAuthentication digestAuthentication = (DigestAuthentication)proxyAuth;
                if (this.tunnelState() == TunnelState.SETUP) {
                    s = digestAuthentication.getHeaderValue(connectRequestURI(this.url), HttpURLConnection.HTTP_CONNECT);
                }
                else {
                    s = digestAuthentication.getHeaderValue(this.getRequestURI(), this.method);
                }
            }
            else {
                s = proxyAuth.getHeaderValue(this.url, this.method);
            }
            messageHeader.set(proxyAuth.getHeaderName(), s);
            this.currentProxyCredentials = proxyAuth;
        }
    }
    
    private AuthenticationInfo getHttpProxyAuthentication(final AuthenticationHeader authenticationHeader) {
        AuthenticationInfo authenticationInfo = null;
        final String raw = authenticationHeader.raw();
        final String proxyHostUsed = this.http.getProxyHostUsed();
        final int proxyPortUsed = this.http.getProxyPortUsed();
        if (proxyHostUsed != null && authenticationHeader.isPresent()) {
            final HeaderParser headerParser = authenticationHeader.headerParser();
            String value = headerParser.findValue("realm");
            final String scheme = authenticationHeader.scheme();
            AuthScheme authScheme = AuthScheme.UNKNOWN;
            if ("basic".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.BASIC;
            }
            else if ("digest".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.DIGEST;
            }
            else if ("ntlm".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.NTLM;
                this.doingNTLMp2ndStage = true;
            }
            else if ("Kerberos".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.KERBEROS;
                this.doingNTLMp2ndStage = true;
            }
            else if ("Negotiate".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.NEGOTIATE;
                this.doingNTLMp2ndStage = true;
            }
            if (value == null) {
                value = "";
            }
            this.proxyAuthKey = AuthenticationInfo.getProxyAuthKey(proxyHostUsed, proxyPortUsed, value, authScheme);
            authenticationInfo = AuthenticationInfo.getProxyAuth(this.proxyAuthKey);
            Label_0632: {
                if (authenticationInfo == null) {
                    switch (authScheme) {
                        case BASIC: {
                            InetAddress inetAddress = null;
                            try {
                                inetAddress = AccessController.doPrivileged((PrivilegedExceptionAction<InetAddress>)new PrivilegedExceptionAction<InetAddress>() {
                                    @Override
                                    public InetAddress run() throws UnknownHostException {
                                        return InetAddress.getByName(proxyHostUsed);
                                    }
                                });
                            }
                            catch (final PrivilegedActionException ex) {}
                            final PasswordAuthentication privilegedRequestPasswordAuthentication = privilegedRequestPasswordAuthentication(proxyHostUsed, inetAddress, proxyPortUsed, "http", value, scheme, this.url, Authenticator.RequestorType.PROXY);
                            if (privilegedRequestPasswordAuthentication != null) {
                                authenticationInfo = new BasicAuthentication(true, proxyHostUsed, proxyPortUsed, value, privilegedRequestPasswordAuthentication);
                            }
                            break Label_0632;
                        }
                        case DIGEST: {
                            final PasswordAuthentication privilegedRequestPasswordAuthentication2 = privilegedRequestPasswordAuthentication(proxyHostUsed, null, proxyPortUsed, this.url.getProtocol(), value, scheme, this.url, Authenticator.RequestorType.PROXY);
                            if (privilegedRequestPasswordAuthentication2 != null) {
                                authenticationInfo = new DigestAuthentication(true, proxyHostUsed, proxyPortUsed, value, scheme, privilegedRequestPasswordAuthentication2, new DigestAuthentication.Parameters());
                            }
                            break Label_0632;
                        }
                        case NTLM: {
                            if (NTLMAuthenticationProxy.supported) {
                                if (this.tryTransparentNTLMProxy) {
                                    this.tryTransparentNTLMProxy = NTLMAuthenticationProxy.supportsTransparentAuth;
                                    if (this.tryTransparentNTLMProxy && this.useProxyResponseCode) {
                                        this.tryTransparentNTLMProxy = false;
                                    }
                                }
                                PasswordAuthentication privilegedRequestPasswordAuthentication3 = null;
                                if (this.tryTransparentNTLMProxy) {
                                    HttpURLConnection.logger.finest("Trying Transparent NTLM authentication");
                                }
                                else {
                                    privilegedRequestPasswordAuthentication3 = privilegedRequestPasswordAuthentication(proxyHostUsed, null, proxyPortUsed, this.url.getProtocol(), "", scheme, this.url, Authenticator.RequestorType.PROXY);
                                }
                                if (this.tryTransparentNTLMProxy || (!this.tryTransparentNTLMProxy && privilegedRequestPasswordAuthentication3 != null)) {
                                    authenticationInfo = NTLMAuthenticationProxy.proxy.create(true, proxyHostUsed, proxyPortUsed, privilegedRequestPasswordAuthentication3);
                                }
                                this.tryTransparentNTLMProxy = false;
                            }
                            break Label_0632;
                        }
                        case NEGOTIATE: {
                            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(authenticationHeader.getHttpCallerInfo(), "Negotiate"));
                            break Label_0632;
                        }
                        case KERBEROS: {
                            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(authenticationHeader.getHttpCallerInfo(), "Kerberos"));
                            break Label_0632;
                        }
                        case UNKNOWN: {
                            if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                                HttpURLConnection.logger.finest("Unknown/Unsupported authentication scheme: " + scheme);
                                break;
                            }
                            break;
                        }
                    }
                    throw new AssertionError((Object)"should not reach here");
                }
            }
            if (authenticationInfo == null && HttpURLConnection.defaultAuth != null && HttpURLConnection.defaultAuth.schemeSupported(scheme)) {
                try {
                    final String authString = HttpURLConnection.defaultAuth.authString(new URL("http", proxyHostUsed, proxyPortUsed, "/"), scheme, value);
                    if (authString != null) {
                        authenticationInfo = new BasicAuthentication(true, proxyHostUsed, proxyPortUsed, value, authString);
                    }
                }
                catch (final MalformedURLException ex2) {}
            }
            if (authenticationInfo != null && !authenticationInfo.setHeaders(this, headerParser, raw)) {
                authenticationInfo = null;
            }
        }
        if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINER)) {
            HttpURLConnection.logger.finer("Proxy Authentication for " + authenticationHeader.toString() + " returned " + ((authenticationInfo != null) ? authenticationInfo.toString() : "null"));
        }
        return authenticationInfo;
    }
    
    private AuthenticationInfo getServerAuthentication(final AuthenticationHeader authenticationHeader) {
        AuthenticationInfo authenticationInfo = null;
        final String raw = authenticationHeader.raw();
        if (authenticationHeader.isPresent()) {
            final HeaderParser headerParser = authenticationHeader.headerParser();
            String value = headerParser.findValue("realm");
            final String scheme = authenticationHeader.scheme();
            AuthScheme authScheme = AuthScheme.UNKNOWN;
            if ("basic".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.BASIC;
            }
            else if ("digest".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.DIGEST;
            }
            else if ("ntlm".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.NTLM;
                this.doingNTLM2ndStage = true;
            }
            else if ("Kerberos".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.KERBEROS;
                this.doingNTLM2ndStage = true;
            }
            else if ("Negotiate".equalsIgnoreCase(scheme)) {
                authScheme = AuthScheme.NEGOTIATE;
                this.doingNTLM2ndStage = true;
            }
            this.domain = headerParser.findValue("domain");
            if (value == null) {
                value = "";
            }
            this.serverAuthKey = AuthenticationInfo.getServerAuthKey(this.url, value, authScheme);
            authenticationInfo = AuthenticationInfo.getServerAuth(this.serverAuthKey);
            InetAddress byName = null;
            if (authenticationInfo == null) {
                try {
                    byName = InetAddress.getByName(this.url.getHost());
                }
                catch (final UnknownHostException ex) {}
            }
            int n = this.url.getPort();
            if (n == -1) {
                n = this.url.getDefaultPort();
            }
            Label_0688: {
                if (authenticationInfo == null) {
                    switch (authScheme) {
                        case KERBEROS: {
                            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(authenticationHeader.getHttpCallerInfo(), "Kerberos"));
                            break Label_0688;
                        }
                        case NEGOTIATE: {
                            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(authenticationHeader.getHttpCallerInfo(), "Negotiate"));
                            break Label_0688;
                        }
                        case BASIC: {
                            final PasswordAuthentication privilegedRequestPasswordAuthentication = privilegedRequestPasswordAuthentication(this.url.getHost(), byName, n, this.url.getProtocol(), value, scheme, this.url, Authenticator.RequestorType.SERVER);
                            if (privilegedRequestPasswordAuthentication != null) {
                                authenticationInfo = new BasicAuthentication(false, this.url, value, privilegedRequestPasswordAuthentication);
                            }
                            break Label_0688;
                        }
                        case DIGEST: {
                            final PasswordAuthentication privilegedRequestPasswordAuthentication2 = privilegedRequestPasswordAuthentication(this.url.getHost(), byName, n, this.url.getProtocol(), value, scheme, this.url, Authenticator.RequestorType.SERVER);
                            if (privilegedRequestPasswordAuthentication2 != null) {
                                this.digestparams = new DigestAuthentication.Parameters();
                                authenticationInfo = new DigestAuthentication(false, this.url, value, scheme, privilegedRequestPasswordAuthentication2, this.digestparams);
                            }
                            break Label_0688;
                        }
                        case NTLM: {
                            if (NTLMAuthenticationProxy.supported) {
                                URL url;
                                try {
                                    url = new URL(this.url, "/");
                                }
                                catch (final Exception ex2) {
                                    url = this.url;
                                }
                                if (this.tryTransparentNTLMServer) {
                                    this.tryTransparentNTLMServer = NTLMAuthenticationProxy.supportsTransparentAuth;
                                    if (this.tryTransparentNTLMServer) {
                                        this.tryTransparentNTLMServer = NTLMAuthenticationProxy.isTrustedSite(this.url);
                                    }
                                }
                                PasswordAuthentication privilegedRequestPasswordAuthentication3 = null;
                                if (this.tryTransparentNTLMServer) {
                                    HttpURLConnection.logger.finest("Trying Transparent NTLM authentication");
                                }
                                else {
                                    privilegedRequestPasswordAuthentication3 = privilegedRequestPasswordAuthentication(this.url.getHost(), byName, n, this.url.getProtocol(), "", scheme, this.url, Authenticator.RequestorType.SERVER);
                                }
                                if (this.tryTransparentNTLMServer || (!this.tryTransparentNTLMServer && privilegedRequestPasswordAuthentication3 != null)) {
                                    authenticationInfo = NTLMAuthenticationProxy.proxy.create(false, url, privilegedRequestPasswordAuthentication3);
                                }
                                this.tryTransparentNTLMServer = false;
                            }
                            break Label_0688;
                        }
                        case UNKNOWN: {
                            if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                                HttpURLConnection.logger.finest("Unknown/Unsupported authentication scheme: " + scheme);
                                break;
                            }
                            break;
                        }
                    }
                    throw new AssertionError((Object)"should not reach here");
                }
            }
            if (authenticationInfo == null && HttpURLConnection.defaultAuth != null && HttpURLConnection.defaultAuth.schemeSupported(scheme)) {
                final String authString = HttpURLConnection.defaultAuth.authString(this.url, scheme, value);
                if (authString != null) {
                    authenticationInfo = new BasicAuthentication(false, this.url, value, authString);
                }
            }
            if (authenticationInfo != null && !authenticationInfo.setHeaders(this, headerParser, raw)) {
                authenticationInfo = null;
            }
        }
        if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINER)) {
            HttpURLConnection.logger.finer("Server Authentication for " + authenticationHeader.toString() + " returned " + ((authenticationInfo != null) ? authenticationInfo.toString() : "null"));
        }
        return authenticationInfo;
    }
    
    private void checkResponseCredentials(final boolean b) throws IOException {
        try {
            if (!this.needToCheck) {
                return;
            }
            if (HttpURLConnection.validateProxy && this.currentProxyCredentials != null && this.currentProxyCredentials instanceof DigestAuthentication) {
                final String value = this.responses.findValue("Proxy-Authentication-Info");
                if (b || value != null) {
                    ((DigestAuthentication)this.currentProxyCredentials).checkResponse(value, this.method, this.getRequestURI());
                    this.currentProxyCredentials = null;
                }
            }
            if (HttpURLConnection.validateServer && this.currentServerCredentials != null && this.currentServerCredentials instanceof DigestAuthentication) {
                final String value2 = this.responses.findValue("Authentication-Info");
                if (b || value2 != null) {
                    ((DigestAuthentication)this.currentServerCredentials).checkResponse(value2, this.method, this.url);
                    this.currentServerCredentials = null;
                }
            }
            if (this.currentServerCredentials == null && this.currentProxyCredentials == null) {
                this.needToCheck = false;
            }
        }
        catch (final IOException ex) {
            this.disconnectInternal();
            this.connected = false;
            throw ex;
        }
    }
    
    String getRequestURI() throws IOException {
        if (this.requestURI == null) {
            this.requestURI = this.http.getURLFile();
        }
        return this.requestURI;
    }
    
    private boolean followRedirect() throws IOException {
        if (!this.getInstanceFollowRedirects()) {
            return false;
        }
        final int responseCode = this.getResponseCode();
        if (responseCode < 300 || responseCode > 307 || responseCode == 306 || responseCode == 304) {
            return false;
        }
        final String headerField = this.getHeaderField("Location");
        if (headerField == null) {
            return false;
        }
        URL url;
        try {
            url = new URL(headerField);
            if (!this.url.getProtocol().equalsIgnoreCase(url.getProtocol())) {
                return false;
            }
        }
        catch (final MalformedURLException ex) {
            url = new URL(this.url, headerField);
        }
        final URL url2 = url;
        this.socketPermission = null;
        final SocketPermission urLtoSocketPermission = this.URLtoSocketPermission(url);
        if (urLtoSocketPermission != null) {
            try {
                return AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction<Boolean>)new PrivilegedExceptionAction<Boolean>() {
                    @Override
                    public Boolean run() throws IOException {
                        return HttpURLConnection.this.followRedirect0(headerField, responseCode, url2);
                    }
                }, null, urLtoSocketPermission);
            }
            catch (final PrivilegedActionException ex2) {
                throw (IOException)ex2.getException();
            }
        }
        return this.followRedirect0(headerField, responseCode, url);
    }
    
    private boolean followRedirect0(final String s, final int n, final URL url) throws IOException {
        this.disconnectInternal();
        if (this.streaming()) {
            throw new HttpRetryException("cannot retry due to redirection, in streaming mode", n, s);
        }
        if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINE)) {
            HttpURLConnection.logger.fine("Redirected from " + this.url + " to " + url);
        }
        this.responses = new MessageHeader();
        if (n == 305) {
            final String host = url.getHost();
            final int port = url.getPort();
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkConnect(host, port);
            }
            this.setProxiedClient(this.url, host, port);
            this.requests.set(0, this.method + " " + this.getRequestURI() + " " + "HTTP/1.1", null);
            this.connected = true;
            this.useProxyResponseCode = true;
        }
        else {
            final URL url2 = this.url;
            this.url = url;
            this.requestURI = null;
            if (this.method.equals("POST") && !Boolean.getBoolean("http.strictPostRedirect") && n != 307) {
                this.requests = new MessageHeader();
                this.setRequests = false;
                super.setRequestMethod("GET");
                this.poster = null;
                if (!this.checkReuseConnection()) {
                    this.connect();
                }
                if (!sameDestination(url2, this.url)) {
                    this.userCookies = null;
                    this.userCookies2 = null;
                }
            }
            else {
                if (!this.checkReuseConnection()) {
                    this.connect();
                }
                if (this.http != null) {
                    this.requests.set(0, this.method + " " + this.getRequestURI() + " " + "HTTP/1.1", null);
                    final int port2 = this.url.getPort();
                    String s2 = this.url.getHost();
                    if (port2 != -1 && port2 != this.url.getDefaultPort()) {
                        s2 = s2 + ":" + String.valueOf(port2);
                    }
                    this.requests.set("Host", s2);
                }
                if (!sameDestination(url2, this.url)) {
                    this.userCookies = null;
                    this.userCookies2 = null;
                    this.requests.remove("Cookie");
                    this.requests.remove("Cookie2");
                    this.requests.remove("Authorization");
                    final AuthenticationInfo serverAuth = AuthenticationInfo.getServerAuth(this.url);
                    if (serverAuth != null && serverAuth.supportsPreemptiveAuthorization()) {
                        this.requests.setIfNotSet(serverAuth.getHeaderName(), serverAuth.getHeaderValue(this.url, this.method));
                        this.currentServerCredentials = serverAuth;
                    }
                }
            }
        }
        return true;
    }
    
    private static boolean sameDestination(final URL url, final URL url2) {
        assert url.getProtocol().equalsIgnoreCase(url2.getProtocol()) : "protocols not equal: " + url + " - " + url2;
        if (!url.getHost().equalsIgnoreCase(url2.getHost())) {
            return false;
        }
        int n = url.getPort();
        if (n == -1) {
            n = url.getDefaultPort();
        }
        int n2 = url2.getPort();
        if (n2 == -1) {
            n2 = url2.getDefaultPort();
        }
        return n == n2;
    }
    
    private void reset() throws IOException {
        this.http.reuse = true;
        this.reuseClient = this.http;
        final InputStream inputStream = this.http.getInputStream();
        if (!this.method.equals("HEAD")) {
            try {
                if (inputStream instanceof ChunkedInputStream || inputStream instanceof MeteredStream) {
                    while (inputStream.read(this.cdata) > 0) {}
                }
                else {
                    long long1 = 0L;
                    final String value = this.responses.findValue("Content-Length");
                    if (value != null) {
                        try {
                            long1 = Long.parseLong(value);
                        }
                        catch (final NumberFormatException ex) {
                            long1 = 0L;
                        }
                    }
                    int read;
                    for (long n = 0L; n < long1; n += read) {
                        if ((read = inputStream.read(this.cdata)) == -1) {
                            break;
                        }
                    }
                }
            }
            catch (final IOException ex2) {
                this.http.reuse = false;
                this.reuseClient = null;
                this.disconnectInternal();
                return;
            }
            try {
                if (inputStream instanceof MeteredStream) {
                    inputStream.close();
                }
            }
            catch (final IOException ex3) {}
        }
        this.responseCode = -1;
        this.responses = new MessageHeader();
        this.connected = false;
    }
    
    private void disconnectWeb() throws IOException {
        if (this.usingProxy() && this.http.isKeepingAlive()) {
            this.responseCode = -1;
            this.reset();
        }
        else {
            this.disconnectInternal();
        }
    }
    
    private void disconnectInternal() {
        this.responseCode = -1;
        this.inputStream = null;
        if (this.pi != null) {
            this.pi.finishTracking();
            this.pi = null;
        }
        if (this.http != null) {
            this.http.closeServer();
            this.http = null;
            this.connected = false;
        }
    }
    
    @Override
    public void disconnect() {
        this.responseCode = -1;
        if (this.pi != null) {
            this.pi.finishTracking();
            this.pi = null;
        }
        if (this.http != null) {
            if (this.inputStream != null) {
                final HttpClient http = this.http;
                final boolean keepingAlive = http.isKeepingAlive();
                try {
                    this.inputStream.close();
                }
                catch (final IOException ex) {}
                if (keepingAlive) {
                    http.closeIdleConnection();
                }
            }
            else {
                this.http.setDoNotRetry(true);
                this.http.closeServer();
            }
            this.http = null;
            this.connected = false;
        }
        this.cachedInputStream = null;
        if (this.cachedHeaders != null) {
            this.cachedHeaders.reset();
        }
    }
    
    @Override
    public boolean usingProxy() {
        return this.http != null && this.http.getProxyHostUsed() != null;
    }
    
    private String filterHeaderField(final String s, final String s2) {
        if (s2 == null) {
            return null;
        }
        if (!"set-cookie".equalsIgnoreCase(s) && !"set-cookie2".equalsIgnoreCase(s)) {
            return s2;
        }
        if (this.cookieHandler == null || s2.length() == 0) {
            return s2;
        }
        final JavaNetHttpCookieAccess javaNetHttpCookieAccess = SharedSecrets.getJavaNetHttpCookieAccess();
        final StringBuilder sb = new StringBuilder();
        final List<HttpCookie> parse = javaNetHttpCookieAccess.parse(s2);
        int n = 0;
        for (final HttpCookie httpCookie : parse) {
            if (httpCookie.isHttpOnly()) {
                continue;
            }
            if (n != 0) {
                sb.append(',');
            }
            sb.append(javaNetHttpCookieAccess.header(httpCookie));
            n = 1;
        }
        return (sb.length() == 0) ? "" : sb.toString();
    }
    
    private Map<String, List<String>> getFilteredHeaderFields() {
        if (this.filteredHeaders != null) {
            return this.filteredHeaders;
        }
        final HashMap hashMap = new HashMap();
        Map<String, List<String>> map;
        if (this.cachedHeaders != null) {
            map = this.cachedHeaders.getHeaders();
        }
        else {
            map = this.responses.getHeaders();
        }
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            final List list = (List)entry.getValue();
            final ArrayList list2 = new ArrayList();
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                final String filterHeaderField = this.filterHeaderField(s, (String)iterator2.next());
                if (filterHeaderField != null) {
                    list2.add(filterHeaderField);
                }
            }
            if (!list2.isEmpty()) {
                hashMap.put(s, Collections.unmodifiableList((List<?>)list2));
            }
        }
        return this.filteredHeaders = (Map<String, List<String>>)Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
    
    @Override
    public String getHeaderField(final String s) {
        try {
            this.getInputStream();
        }
        catch (final IOException ex) {}
        if (this.cachedHeaders != null) {
            return this.filterHeaderField(s, this.cachedHeaders.findValue(s));
        }
        return this.filterHeaderField(s, this.responses.findValue(s));
    }
    
    @Override
    public Map<String, List<String>> getHeaderFields() {
        try {
            this.getInputStream();
        }
        catch (final IOException ex) {}
        return this.getFilteredHeaderFields();
    }
    
    @Override
    public String getHeaderField(final int n) {
        try {
            this.getInputStream();
        }
        catch (final IOException ex) {}
        if (this.cachedHeaders != null) {
            return this.filterHeaderField(this.cachedHeaders.getKey(n), this.cachedHeaders.getValue(n));
        }
        return this.filterHeaderField(this.responses.getKey(n), this.responses.getValue(n));
    }
    
    @Override
    public String getHeaderFieldKey(final int n) {
        try {
            this.getInputStream();
        }
        catch (final IOException ex) {}
        if (this.cachedHeaders != null) {
            return this.cachedHeaders.getKey(n);
        }
        return this.responses.getKey(n);
    }
    
    @Override
    public synchronized void setRequestProperty(final String s, final String s2) {
        if (this.connected || this.connecting) {
            throw new IllegalStateException("Already connected");
        }
        if (s == null) {
            throw new NullPointerException("key is null");
        }
        if (this.isExternalMessageHeaderAllowed(s, s2)) {
            this.requests.set(s, s2);
            if (!s.equalsIgnoreCase("Content-Type")) {
                this.userHeaders.set(s, s2);
            }
        }
    }
    
    MessageHeader getUserSetHeaders() {
        return this.userHeaders;
    }
    
    @Override
    public synchronized void addRequestProperty(final String s, final String s2) {
        if (this.connected || this.connecting) {
            throw new IllegalStateException("Already connected");
        }
        if (s == null) {
            throw new NullPointerException("key is null");
        }
        if (this.isExternalMessageHeaderAllowed(s, s2)) {
            this.requests.add(s, s2);
            if (!s.equalsIgnoreCase("Content-Type")) {
                this.userHeaders.add(s, s2);
            }
        }
    }
    
    public void setAuthenticationProperty(final String s, final String s2) {
        this.checkMessageHeader(s, s2);
        this.requests.set(s, s2);
    }
    
    @Override
    public synchronized String getRequestProperty(final String s) {
        if (s == null) {
            return null;
        }
        for (int i = 0; i < HttpURLConnection.EXCLUDE_HEADERS.length; ++i) {
            if (s.equalsIgnoreCase(HttpURLConnection.EXCLUDE_HEADERS[i])) {
                return null;
            }
        }
        if (!this.setUserCookies) {
            if (s.equalsIgnoreCase("Cookie")) {
                return this.userCookies;
            }
            if (s.equalsIgnoreCase("Cookie2")) {
                return this.userCookies2;
            }
        }
        return this.requests.findValue(s);
    }
    
    @Override
    public synchronized Map<String, List<String>> getRequestProperties() {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        }
        if (this.setUserCookies) {
            return this.requests.getHeaders(HttpURLConnection.EXCLUDE_HEADERS);
        }
        Map map = null;
        if (this.userCookies != null || this.userCookies2 != null) {
            map = new HashMap();
            if (this.userCookies != null) {
                map.put("Cookie", Arrays.asList(this.userCookies));
            }
            if (this.userCookies2 != null) {
                map.put("Cookie2", Arrays.asList(this.userCookies2));
            }
        }
        return this.requests.filterAndAddHeaders(HttpURLConnection.EXCLUDE_HEADERS2, map);
    }
    
    @Override
    public void setConnectTimeout(final int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        this.connectTimeout = connectTimeout;
    }
    
    @Override
    public int getConnectTimeout() {
        return (this.connectTimeout < 0) ? 0 : this.connectTimeout;
    }
    
    @Override
    public void setReadTimeout(final int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        this.readTimeout = readTimeout;
    }
    
    @Override
    public int getReadTimeout() {
        return (this.readTimeout < 0) ? 0 : this.readTimeout;
    }
    
    public CookieHandler getCookieHandler() {
        return this.cookieHandler;
    }
    
    String getMethod() {
        return this.method;
    }
    
    private MessageHeader mapToMessageHeader(final Map<String, List<String>> map) {
        final MessageHeader messageHeader = new MessageHeader();
        if (map == null || map.isEmpty()) {
            return messageHeader;
        }
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            for (final String s2 : (List)entry.getValue()) {
                if (s == null) {
                    messageHeader.prepend(s, s2);
                }
                else {
                    messageHeader.add(s, s2);
                }
            }
        }
        return messageHeader;
    }
    
    static {
        HttpURLConnection.HTTP_CONNECT = "CONNECT";
        HttpURLConnection.enableESBuffer = false;
        HttpURLConnection.timeout4ESBuffer = 0;
        HttpURLConnection.bufSize4ES = 0;
        restrictedHeaders = new String[] { "Access-Control-Request-Headers", "Access-Control-Request-Method", "Connection", "Content-Length", "Content-Transfer-Encoding", "Host", "Keep-Alive", "Origin", "Trailer", "Transfer-Encoding", "Upgrade", "Via" };
        maxRedirects = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("http.maxRedirects", 20));
        version = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.version"));
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.agent"));
        String userAgent2;
        if (s == null) {
            userAgent2 = "Java/" + HttpURLConnection.version;
        }
        else {
            userAgent2 = s + " Java/" + HttpURLConnection.version;
        }
        userAgent = userAgent2;
        disabledTunnelingSchemes = schemesListToSet(getNetProperty("jdk.http.auth.tunneling.disabledSchemes"));
        disabledProxyingSchemes = schemesListToSet(getNetProperty("jdk.http.auth.proxying.disabledSchemes"));
        validateProxy = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("http.auth.digest.validateProxy"));
        validateServer = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("http.auth.digest.validateServer"));
        HttpURLConnection.enableESBuffer = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.net.http.errorstream.enableBuffering"));
        HttpURLConnection.timeout4ESBuffer = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.net.http.errorstream.timeout", 300));
        if (HttpURLConnection.timeout4ESBuffer <= 0) {
            HttpURLConnection.timeout4ESBuffer = 300;
        }
        HttpURLConnection.bufSize4ES = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.net.http.errorstream.bufferSize", 4096));
        if (HttpURLConnection.bufSize4ES <= 0) {
            HttpURLConnection.bufSize4ES = 4096;
        }
        if (!(allowRestrictedHeaders = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.net.http.allowRestrictedHeaders")))) {
            restrictedHeaderSet = new HashSet<String>(HttpURLConnection.restrictedHeaders.length);
            for (int i = 0; i < HttpURLConnection.restrictedHeaders.length; ++i) {
                HttpURLConnection.restrictedHeaderSet.add(HttpURLConnection.restrictedHeaders[i].toLowerCase());
            }
        }
        else {
            restrictedHeaderSet = null;
        }
        EXCLUDE_HEADERS = new String[] { "Proxy-Authorization", "Authorization" };
        EXCLUDE_HEADERS2 = new String[] { "Proxy-Authorization", "Authorization", "Cookie", "Cookie2" };
        logger = PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection");
    }
    
    public enum TunnelState
    {
        NONE, 
        SETUP, 
        TUNNELING;
    }
    
    class HttpInputStream extends FilterInputStream
    {
        private CacheRequest cacheRequest;
        private OutputStream outputStream;
        private boolean marked;
        private int inCache;
        private int markCount;
        private boolean closed;
        private byte[] skipBuffer;
        private static final int SKIP_BUFFER_SIZE = 8096;
        
        public HttpInputStream(final InputStream inputStream) {
            super(inputStream);
            this.marked = false;
            this.inCache = 0;
            this.markCount = 0;
            this.cacheRequest = null;
            this.outputStream = null;
        }
        
        public HttpInputStream(final InputStream inputStream, final CacheRequest cacheRequest) {
            super(inputStream);
            this.marked = false;
            this.inCache = 0;
            this.markCount = 0;
            this.cacheRequest = cacheRequest;
            try {
                this.outputStream = cacheRequest.getBody();
            }
            catch (final IOException ex) {
                this.cacheRequest.abort();
                this.cacheRequest = null;
                this.outputStream = null;
            }
        }
        
        @Override
        public synchronized void mark(final int n) {
            super.mark(n);
            if (this.cacheRequest != null) {
                this.marked = true;
                this.markCount = 0;
            }
        }
        
        @Override
        public synchronized void reset() throws IOException {
            super.reset();
            if (this.cacheRequest != null) {
                this.marked = false;
                this.inCache += this.markCount;
            }
        }
        
        private void ensureOpen() throws IOException {
            if (this.closed) {
                throw new IOException("stream is closed");
            }
        }
        
        @Override
        public int read() throws IOException {
            this.ensureOpen();
            try {
                final byte[] array = { 0 };
                final int read = this.read(array);
                return (read == -1) ? read : (array[0] & 0xFF);
            }
            catch (final IOException ex) {
                if (this.cacheRequest != null) {
                    this.cacheRequest.abort();
                }
                throw ex;
            }
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            this.ensureOpen();
            try {
                final int read = super.read(array, n, n2);
                int n3;
                if (this.inCache > 0) {
                    if (this.inCache >= read) {
                        this.inCache -= read;
                        n3 = 0;
                    }
                    else {
                        n3 = read - this.inCache;
                        this.inCache = 0;
                    }
                }
                else {
                    n3 = read;
                }
                if (n3 > 0 && this.outputStream != null) {
                    this.outputStream.write(array, n + (read - n3), n3);
                }
                if (this.marked) {
                    this.markCount += read;
                }
                return read;
            }
            catch (final IOException ex) {
                if (this.cacheRequest != null) {
                    this.cacheRequest.abort();
                }
                throw ex;
            }
        }
        
        @Override
        public long skip(final long n) throws IOException {
            this.ensureOpen();
            long n2 = n;
            if (this.skipBuffer == null) {
                this.skipBuffer = new byte[8096];
            }
            final byte[] skipBuffer = this.skipBuffer;
            if (n <= 0L) {
                return 0L;
            }
            while (n2 > 0L) {
                final int read = this.read(skipBuffer, 0, (int)Math.min(8096L, n2));
                if (read < 0) {
                    break;
                }
                n2 -= read;
            }
            return n - n2;
        }
        
        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            try {
                if (this.outputStream != null) {
                    if (this.read() != -1) {
                        this.cacheRequest.abort();
                    }
                    else {
                        this.outputStream.close();
                    }
                }
                super.close();
            }
            catch (final IOException ex) {
                if (this.cacheRequest != null) {
                    this.cacheRequest.abort();
                }
                throw ex;
            }
            finally {
                this.closed = true;
                HttpURLConnection.this.http = null;
                HttpURLConnection.this.checkResponseCredentials(true);
            }
        }
    }
    
    class StreamingOutputStream extends FilterOutputStream
    {
        long expected;
        long written;
        boolean closed;
        boolean error;
        IOException errorExcp;
        
        StreamingOutputStream(final OutputStream outputStream, final long expected) {
            super(outputStream);
            this.expected = expected;
            this.written = 0L;
            this.closed = false;
            this.error = false;
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.checkError();
            ++this.written;
            if (this.expected != -1L && this.written > this.expected) {
                throw new IOException("too many bytes written");
            }
            this.out.write(n);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.write(array, 0, array.length);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.checkError();
            this.written += n2;
            if (this.expected != -1L && this.written > this.expected) {
                this.out.close();
                throw new IOException("too many bytes written");
            }
            this.out.write(array, n, n2);
        }
        
        void checkError() throws IOException {
            if (this.closed) {
                throw new IOException("Stream is closed");
            }
            if (this.error) {
                throw this.errorExcp;
            }
            if (((PrintStream)this.out).checkError()) {
                throw new IOException("Error writing request body to server");
            }
        }
        
        boolean writtenOK() {
            return this.closed && !this.error;
        }
        
        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.expected != -1L) {
                if (this.written != this.expected) {
                    this.error = true;
                    this.errorExcp = new IOException("insufficient data written");
                    this.out.close();
                    throw this.errorExcp;
                }
                super.flush();
            }
            else {
                super.close();
                final OutputStream outputStream = HttpURLConnection.this.http.getOutputStream();
                outputStream.write(13);
                outputStream.write(10);
                outputStream.flush();
            }
        }
    }
    
    static class ErrorStream extends InputStream
    {
        ByteBuffer buffer;
        InputStream is;
        
        private ErrorStream(final ByteBuffer buffer) {
            this.buffer = buffer;
            this.is = null;
        }
        
        private ErrorStream(final ByteBuffer buffer, final InputStream is) {
            this.buffer = buffer;
            this.is = is;
        }
        
        public static InputStream getErrorStream(final InputStream inputStream, final long n, final HttpClient httpClient) {
            if (n == 0L) {
                return null;
            }
            try {
                final int readTimeout = httpClient.getReadTimeout();
                httpClient.setReadTimeout(HttpURLConnection.timeout4ESBuffer / 5);
                boolean b = false;
                long n2;
                if (n < 0L) {
                    n2 = HttpURLConnection.bufSize4ES;
                    b = true;
                }
                else {
                    n2 = n;
                }
                if (n2 > HttpURLConnection.bufSize4ES) {
                    return null;
                }
                final int n3 = (int)n2;
                final byte[] array = new byte[n3];
                int n4 = 0;
                int n5 = 0;
                int read = 0;
                do {
                    try {
                        read = inputStream.read(array, n4, array.length - n4);
                        if (read < 0) {
                            if (b) {
                                break;
                            }
                            throw new IOException("the server closes before sending " + n + " bytes of data");
                        }
                        else {
                            n4 += read;
                        }
                    }
                    catch (final SocketTimeoutException ex) {
                        n5 += HttpURLConnection.timeout4ESBuffer / 5;
                    }
                } while (n4 < n3 && n5 < HttpURLConnection.timeout4ESBuffer);
                httpClient.setReadTimeout(readTimeout);
                if (n4 == 0) {
                    return null;
                }
                if ((n4 == n2 && !b) || (b && read < 0)) {
                    inputStream.close();
                    return new ErrorStream(ByteBuffer.wrap(array, 0, n4));
                }
                return new ErrorStream(ByteBuffer.wrap(array, 0, n4), inputStream);
            }
            catch (final IOException ex2) {
                return null;
            }
        }
        
        @Override
        public int available() throws IOException {
            if (this.is == null) {
                return this.buffer.remaining();
            }
            return this.buffer.remaining() + this.is.available();
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            final int read = this.read(array);
            return (read == -1) ? read : (array[0] & 0xFF);
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            final int remaining = this.buffer.remaining();
            if (remaining > 0) {
                final int n3 = (remaining < n2) ? remaining : n2;
                this.buffer.get(array, n, n3);
                return n3;
            }
            if (this.is == null) {
                return -1;
            }
            return this.is.read(array, n, n2);
        }
        
        @Override
        public void close() throws IOException {
            this.buffer = null;
            if (this.is != null) {
                this.is.close();
            }
        }
    }
}
