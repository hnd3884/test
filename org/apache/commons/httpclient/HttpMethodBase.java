package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.io.InterruptedIOException;
import org.apache.commons.httpclient.util.ExceptionUtil;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.cookie.CookieVersionSupport;
import java.util.Collection;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.params.HttpMethodParams;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public abstract class HttpMethodBase implements HttpMethod
{
    private static final Log LOG;
    private HeaderGroup requestHeaders;
    protected StatusLine statusLine;
    private HeaderGroup responseHeaders;
    private HeaderGroup responseTrailerHeaders;
    private String path;
    private String queryString;
    private InputStream responseStream;
    private HttpConnection responseConnection;
    private byte[] responseBody;
    private boolean followRedirects;
    private boolean doAuthentication;
    private HttpMethodParams params;
    private AuthState hostAuthState;
    private AuthState proxyAuthState;
    private boolean used;
    private int recoverableExceptionCount;
    private HttpHost httphost;
    private MethodRetryHandler methodRetryHandler;
    private boolean connectionCloseForced;
    private static final int RESPONSE_WAIT_TIME_MS = 3000;
    protected HttpVersion effectiveVersion;
    private volatile boolean aborted;
    private boolean requestSent;
    private CookieSpec cookiespec;
    private static final int DEFAULT_INITIAL_BUFFER_SIZE = 4096;
    
    public HttpMethodBase() {
        this.requestHeaders = new HeaderGroup();
        this.statusLine = null;
        this.responseHeaders = new HeaderGroup();
        this.responseTrailerHeaders = new HeaderGroup();
        this.path = null;
        this.queryString = null;
        this.responseStream = null;
        this.responseConnection = null;
        this.responseBody = null;
        this.followRedirects = false;
        this.doAuthentication = true;
        this.params = new HttpMethodParams();
        this.hostAuthState = new AuthState();
        this.proxyAuthState = new AuthState();
        this.used = false;
        this.recoverableExceptionCount = 0;
        this.httphost = null;
        this.connectionCloseForced = false;
        this.effectiveVersion = null;
        this.aborted = false;
        this.requestSent = false;
        this.cookiespec = null;
    }
    
    public HttpMethodBase(String uri) throws IllegalArgumentException, IllegalStateException {
        this.requestHeaders = new HeaderGroup();
        this.statusLine = null;
        this.responseHeaders = new HeaderGroup();
        this.responseTrailerHeaders = new HeaderGroup();
        this.path = null;
        this.queryString = null;
        this.responseStream = null;
        this.responseConnection = null;
        this.responseBody = null;
        this.followRedirects = false;
        this.doAuthentication = true;
        this.params = new HttpMethodParams();
        this.hostAuthState = new AuthState();
        this.proxyAuthState = new AuthState();
        this.used = false;
        this.recoverableExceptionCount = 0;
        this.httphost = null;
        this.connectionCloseForced = false;
        this.effectiveVersion = null;
        this.aborted = false;
        this.requestSent = false;
        this.cookiespec = null;
        try {
            if (uri == null || uri.equals("")) {
                uri = "/";
            }
            final String charset = this.getParams().getUriCharset();
            this.setURI(new URI(uri, true, charset));
        }
        catch (final URIException e) {
            throw new IllegalArgumentException("Invalid uri '" + uri + "': " + e.getMessage());
        }
    }
    
    public abstract String getName();
    
    public URI getURI() throws URIException {
        final StringBuffer buffer = new StringBuffer();
        if (this.httphost != null) {
            buffer.append(this.httphost.getProtocol().getScheme());
            buffer.append("://");
            buffer.append(this.httphost.getHostName());
            final int port = this.httphost.getPort();
            if (port != -1 && port != this.httphost.getProtocol().getDefaultPort()) {
                buffer.append(":");
                buffer.append(port);
            }
        }
        buffer.append(this.path);
        if (this.queryString != null) {
            buffer.append('?');
            buffer.append(this.queryString);
        }
        final String charset = this.getParams().getUriCharset();
        return new URI(buffer.toString(), true, charset);
    }
    
    public void setURI(final URI uri) throws URIException {
        if (uri.isAbsoluteURI()) {
            this.httphost = new HttpHost(uri);
        }
        this.setPath((uri.getPath() == null) ? "/" : uri.getEscapedPath());
        this.setQueryString(uri.getEscapedQuery());
    }
    
    public void setFollowRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
    }
    
    public boolean getFollowRedirects() {
        return this.followRedirects;
    }
    
    public void setHttp11(final boolean http11) {
        if (http11) {
            this.params.setVersion(HttpVersion.HTTP_1_1);
        }
        else {
            this.params.setVersion(HttpVersion.HTTP_1_0);
        }
    }
    
    public boolean getDoAuthentication() {
        return this.doAuthentication;
    }
    
    public void setDoAuthentication(final boolean doAuthentication) {
        this.doAuthentication = doAuthentication;
    }
    
    public boolean isHttp11() {
        return this.params.getVersion().equals(HttpVersion.HTTP_1_1);
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public void addRequestHeader(final Header header) {
        HttpMethodBase.LOG.trace((Object)"HttpMethodBase.addRequestHeader(Header)");
        if (header == null) {
            HttpMethodBase.LOG.debug((Object)"null header value ignored");
        }
        else {
            this.getRequestHeaderGroup().addHeader(header);
        }
    }
    
    public void addResponseFooter(final Header footer) {
        this.getResponseTrailerHeaderGroup().addHeader(footer);
    }
    
    public String getPath() {
        return (this.path == null || this.path.equals("")) ? "/" : this.path;
    }
    
    public void setQueryString(final String queryString) {
        this.queryString = queryString;
    }
    
    public void setQueryString(final NameValuePair[] params) {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.setQueryString(NameValuePair[])");
        this.queryString = EncodingUtil.formUrlEncode(params, "UTF-8");
    }
    
    public String getQueryString() {
        return this.queryString;
    }
    
    public void setRequestHeader(final String headerName, final String headerValue) {
        final Header header = new Header(headerName, headerValue);
        this.setRequestHeader(header);
    }
    
    public void setRequestHeader(final Header header) {
        final Header[] headers = this.getRequestHeaderGroup().getHeaders(header.getName());
        for (int i = 0; i < headers.length; ++i) {
            this.getRequestHeaderGroup().removeHeader(headers[i]);
        }
        this.getRequestHeaderGroup().addHeader(header);
    }
    
    public Header getRequestHeader(final String headerName) {
        if (headerName == null) {
            return null;
        }
        return this.getRequestHeaderGroup().getCondensedHeader(headerName);
    }
    
    public Header[] getRequestHeaders() {
        return this.getRequestHeaderGroup().getAllHeaders();
    }
    
    public Header[] getRequestHeaders(final String headerName) {
        return this.getRequestHeaderGroup().getHeaders(headerName);
    }
    
    protected HeaderGroup getRequestHeaderGroup() {
        return this.requestHeaders;
    }
    
    protected HeaderGroup getResponseTrailerHeaderGroup() {
        return this.responseTrailerHeaders;
    }
    
    protected HeaderGroup getResponseHeaderGroup() {
        return this.responseHeaders;
    }
    
    public Header[] getResponseHeaders(final String headerName) {
        return this.getResponseHeaderGroup().getHeaders(headerName);
    }
    
    public int getStatusCode() {
        return this.statusLine.getStatusCode();
    }
    
    public StatusLine getStatusLine() {
        return this.statusLine;
    }
    
    private boolean responseAvailable() {
        return this.responseBody != null || this.responseStream != null;
    }
    
    public Header[] getResponseHeaders() {
        return this.getResponseHeaderGroup().getAllHeaders();
    }
    
    public Header getResponseHeader(final String headerName) {
        if (headerName == null) {
            return null;
        }
        return this.getResponseHeaderGroup().getCondensedHeader(headerName);
    }
    
    public long getResponseContentLength() {
        final Header[] headers = this.getResponseHeaderGroup().getHeaders("Content-Length");
        if (headers.length == 0) {
            return -1L;
        }
        if (headers.length > 1) {
            HttpMethodBase.LOG.warn((Object)"Multiple content-length headers detected");
        }
        int i = headers.length - 1;
        while (i >= 0) {
            final Header header = headers[i];
            try {
                return Long.parseLong(header.getValue());
            }
            catch (final NumberFormatException e) {
                if (HttpMethodBase.LOG.isWarnEnabled()) {
                    HttpMethodBase.LOG.warn((Object)("Invalid content-length value: " + e.getMessage()));
                }
                --i;
                continue;
            }
            break;
        }
        return -1L;
    }
    
    public byte[] getResponseBody() throws IOException {
        if (this.responseBody == null) {
            final InputStream instream = this.getResponseBodyAsStream();
            if (instream != null) {
                final long contentLength = this.getResponseContentLength();
                if (contentLength > 2147483647L) {
                    throw new IOException("Content too large to be buffered: " + contentLength + " bytes");
                }
                final int limit = this.getParams().getIntParameter("http.method.response.buffer.warnlimit", 1048576);
                if (contentLength == -1L || contentLength > limit) {
                    HttpMethodBase.LOG.warn((Object)"Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.");
                }
                HttpMethodBase.LOG.debug((Object)"Buffering response body");
                final ByteArrayOutputStream outstream = new ByteArrayOutputStream((contentLength > 0L) ? ((int)contentLength) : 4096);
                final byte[] buffer = new byte[4096];
                int len;
                while ((len = instream.read(buffer)) > 0) {
                    outstream.write(buffer, 0, len);
                }
                outstream.close();
                this.setResponseStream(null);
                this.responseBody = outstream.toByteArray();
            }
        }
        return this.responseBody;
    }
    
    public byte[] getResponseBody(final int maxlen) throws IOException {
        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen must be positive");
        }
        if (this.responseBody == null) {
            final InputStream instream = this.getResponseBodyAsStream();
            if (instream != null) {
                final long contentLength = this.getResponseContentLength();
                if (contentLength != -1L && contentLength > maxlen) {
                    throw new HttpContentTooLargeException("Content-Length is " + contentLength, maxlen);
                }
                HttpMethodBase.LOG.debug((Object)"Buffering response body");
                final ByteArrayOutputStream rawdata = new ByteArrayOutputStream((contentLength > 0L) ? ((int)contentLength) : 4096);
                final byte[] buffer = new byte[2048];
                int pos = 0;
                do {
                    final int len = instream.read(buffer, 0, Math.min(buffer.length, maxlen - pos));
                    if (len == -1) {
                        break;
                    }
                    rawdata.write(buffer, 0, len);
                    pos += len;
                } while (pos < maxlen);
                this.setResponseStream(null);
                if (pos == maxlen && instream.read() != -1) {
                    throw new HttpContentTooLargeException("Content-Length not known but larger than " + maxlen, maxlen);
                }
                this.responseBody = rawdata.toByteArray();
            }
        }
        return this.responseBody;
    }
    
    public InputStream getResponseBodyAsStream() throws IOException {
        if (this.responseStream != null) {
            return this.responseStream;
        }
        if (this.responseBody != null) {
            final InputStream byteResponseStream = new ByteArrayInputStream(this.responseBody);
            HttpMethodBase.LOG.debug((Object)"re-creating response stream from byte array");
            return byteResponseStream;
        }
        return null;
    }
    
    public String getResponseBodyAsString() throws IOException {
        byte[] rawdata = null;
        if (this.responseAvailable()) {
            rawdata = this.getResponseBody();
        }
        if (rawdata != null) {
            return EncodingUtil.getString(rawdata, this.getResponseCharSet());
        }
        return null;
    }
    
    public String getResponseBodyAsString(final int maxlen) throws IOException {
        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen must be positive");
        }
        byte[] rawdata = null;
        if (this.responseAvailable()) {
            rawdata = this.getResponseBody(maxlen);
        }
        if (rawdata != null) {
            return EncodingUtil.getString(rawdata, this.getResponseCharSet());
        }
        return null;
    }
    
    public Header[] getResponseFooters() {
        return this.getResponseTrailerHeaderGroup().getAllHeaders();
    }
    
    public Header getResponseFooter(final String footerName) {
        if (footerName == null) {
            return null;
        }
        return this.getResponseTrailerHeaderGroup().getCondensedHeader(footerName);
    }
    
    protected void setResponseStream(final InputStream responseStream) {
        this.responseStream = responseStream;
    }
    
    protected InputStream getResponseStream() {
        return this.responseStream;
    }
    
    public String getStatusText() {
        return this.statusLine.getReasonPhrase();
    }
    
    public void setStrictMode(final boolean strictMode) {
        if (strictMode) {
            this.params.makeStrict();
        }
        else {
            this.params.makeLenient();
        }
    }
    
    public boolean isStrictMode() {
        return false;
    }
    
    public void addRequestHeader(final String headerName, final String headerValue) {
        this.addRequestHeader(new Header(headerName, headerValue));
    }
    
    protected boolean isConnectionCloseForced() {
        return this.connectionCloseForced;
    }
    
    protected void setConnectionCloseForced(final boolean b) {
        if (HttpMethodBase.LOG.isDebugEnabled()) {
            HttpMethodBase.LOG.debug((Object)("Force-close connection: " + b));
        }
        this.connectionCloseForced = b;
    }
    
    protected boolean shouldCloseConnection(final HttpConnection conn) {
        if (this.isConnectionCloseForced()) {
            HttpMethodBase.LOG.debug((Object)"Should force-close connection.");
            return true;
        }
        Header connectionHeader = null;
        if (!conn.isTransparent()) {
            connectionHeader = this.responseHeaders.getFirstHeader("proxy-connection");
        }
        if (connectionHeader == null) {
            connectionHeader = this.responseHeaders.getFirstHeader("connection");
        }
        if (connectionHeader == null) {
            connectionHeader = this.requestHeaders.getFirstHeader("connection");
        }
        if (connectionHeader != null) {
            if (connectionHeader.getValue().equalsIgnoreCase("close")) {
                if (HttpMethodBase.LOG.isDebugEnabled()) {
                    HttpMethodBase.LOG.debug((Object)("Should close connection in response to directive: " + connectionHeader.getValue()));
                }
                return true;
            }
            if (connectionHeader.getValue().equalsIgnoreCase("keep-alive")) {
                if (HttpMethodBase.LOG.isDebugEnabled()) {
                    HttpMethodBase.LOG.debug((Object)("Should NOT close connection in response to directive: " + connectionHeader.getValue()));
                }
                return false;
            }
            if (HttpMethodBase.LOG.isDebugEnabled()) {
                HttpMethodBase.LOG.debug((Object)("Unknown directive: " + connectionHeader.toExternalForm()));
            }
        }
        HttpMethodBase.LOG.debug((Object)"Resorting to protocol version default close connection policy");
        if (this.effectiveVersion.greaterEquals(HttpVersion.HTTP_1_1)) {
            if (HttpMethodBase.LOG.isDebugEnabled()) {
                HttpMethodBase.LOG.debug((Object)("Should NOT close connection, using " + this.effectiveVersion.toString()));
            }
        }
        else if (HttpMethodBase.LOG.isDebugEnabled()) {
            HttpMethodBase.LOG.debug((Object)("Should close connection, using " + this.effectiveVersion.toString()));
        }
        return this.effectiveVersion.lessEquals(HttpVersion.HTTP_1_0);
    }
    
    private void checkExecuteConditions(final HttpState state, final HttpConnection conn) throws HttpException {
        if (state == null) {
            throw new IllegalArgumentException("HttpState parameter may not be null");
        }
        if (conn == null) {
            throw new IllegalArgumentException("HttpConnection parameter may not be null");
        }
        if (this.aborted) {
            throw new IllegalStateException("Method has been aborted");
        }
        if (!this.validate()) {
            throw new ProtocolException("HttpMethodBase object not valid");
        }
    }
    
    public int execute(final HttpState state, final HttpConnection conn) throws HttpException, IOException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.execute(HttpState, HttpConnection)");
        this.checkExecuteConditions(state, this.responseConnection = conn);
        this.statusLine = null;
        this.connectionCloseForced = false;
        conn.setLastResponseInputStream(null);
        if (this.effectiveVersion == null) {
            this.effectiveVersion = this.params.getVersion();
        }
        this.writeRequest(state, conn);
        this.requestSent = true;
        this.readResponse(state, conn);
        this.used = true;
        return this.statusLine.getStatusCode();
    }
    
    public void abort() {
        if (this.aborted) {
            return;
        }
        this.aborted = true;
        final HttpConnection conn = this.responseConnection;
        if (conn != null) {
            conn.close();
        }
    }
    
    public boolean hasBeenUsed() {
        return this.used;
    }
    
    public void recycle() {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.recycle()");
        this.releaseConnection();
        this.path = null;
        this.followRedirects = false;
        this.doAuthentication = true;
        this.queryString = null;
        this.getRequestHeaderGroup().clear();
        this.getResponseHeaderGroup().clear();
        this.getResponseTrailerHeaderGroup().clear();
        this.statusLine = null;
        this.effectiveVersion = null;
        this.aborted = false;
        this.used = false;
        this.params = new HttpMethodParams();
        this.responseBody = null;
        this.recoverableExceptionCount = 0;
        this.connectionCloseForced = false;
        this.hostAuthState.invalidate();
        this.proxyAuthState.invalidate();
        this.cookiespec = null;
        this.requestSent = false;
    }
    
    public void releaseConnection() {
        try {
            if (this.responseStream != null) {
                try {
                    this.responseStream.close();
                }
                catch (final IOException ex) {}
            }
        }
        finally {
            this.ensureConnectionRelease();
        }
    }
    
    public void removeRequestHeader(final String headerName) {
        final Header[] headers = this.getRequestHeaderGroup().getHeaders(headerName);
        for (int i = 0; i < headers.length; ++i) {
            this.getRequestHeaderGroup().removeHeader(headers[i]);
        }
    }
    
    public void removeRequestHeader(final Header header) {
        if (header == null) {
            return;
        }
        this.getRequestHeaderGroup().removeHeader(header);
    }
    
    public boolean validate() {
        return true;
    }
    
    private CookieSpec getCookieSpec(final HttpState state) {
        if (this.cookiespec == null) {
            final int i = state.getCookiePolicy();
            if (i == -1) {
                this.cookiespec = CookiePolicy.getCookieSpec(this.params.getCookiePolicy());
            }
            else {
                this.cookiespec = CookiePolicy.getSpecByPolicy(i);
            }
            this.cookiespec.setValidDateFormats((Collection)this.params.getParameter("http.dateparser.patterns"));
        }
        return this.cookiespec;
    }
    
    protected void addCookieRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.addCookieRequestHeader(HttpState, HttpConnection)");
        final Header[] cookieheaders = this.getRequestHeaderGroup().getHeaders("Cookie");
        for (int i = 0; i < cookieheaders.length; ++i) {
            final Header cookieheader = cookieheaders[i];
            if (cookieheader.isAutogenerated()) {
                this.getRequestHeaderGroup().removeHeader(cookieheader);
            }
        }
        final CookieSpec matcher = this.getCookieSpec(state);
        String host = this.params.getVirtualHost();
        if (host == null) {
            host = conn.getHost();
        }
        final Cookie[] cookies = matcher.match(host, conn.getPort(), this.getPath(), conn.isSecure(), state.getCookies());
        if (cookies != null && cookies.length > 0) {
            if (this.getParams().isParameterTrue("http.protocol.single-cookie-header")) {
                final String s = matcher.formatCookies(cookies);
                this.getRequestHeaderGroup().addHeader(new Header("Cookie", s, true));
            }
            else {
                for (int j = 0; j < cookies.length; ++j) {
                    final String s2 = matcher.formatCookie(cookies[j]);
                    this.getRequestHeaderGroup().addHeader(new Header("Cookie", s2, true));
                }
            }
            if (matcher instanceof CookieVersionSupport) {
                final CookieVersionSupport versupport = (CookieVersionSupport)matcher;
                final int ver = versupport.getVersion();
                boolean needVersionHeader = false;
                for (int k = 0; k < cookies.length; ++k) {
                    if (ver != cookies[k].getVersion()) {
                        needVersionHeader = true;
                    }
                }
                if (needVersionHeader) {
                    this.getRequestHeaderGroup().addHeader(versupport.getVersionHeader());
                }
            }
        }
    }
    
    protected void addHostRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.addHostRequestHeader(HttpState, HttpConnection)");
        String host = this.params.getVirtualHost();
        if (host != null) {
            HttpMethodBase.LOG.debug((Object)("Using virtual host name: " + host));
        }
        else {
            host = conn.getHost();
        }
        final int port = conn.getPort();
        if (HttpMethodBase.LOG.isDebugEnabled()) {
            HttpMethodBase.LOG.debug((Object)"Adding Host request header");
        }
        if (conn.getProtocol().getDefaultPort() != port) {
            host = host + ":" + port;
        }
        this.setRequestHeader("Host", host);
    }
    
    protected void addProxyConnectionHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.addProxyConnectionHeader(HttpState, HttpConnection)");
        if (!conn.isTransparent() && this.getRequestHeader("Proxy-Connection") == null) {
            this.addRequestHeader("Proxy-Connection", "Keep-Alive");
        }
    }
    
    protected void addRequestHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.addRequestHeaders(HttpState, HttpConnection)");
        this.addUserAgentRequestHeader(state, conn);
        this.addHostRequestHeader(state, conn);
        this.addCookieRequestHeader(state, conn);
        this.addProxyConnectionHeader(state, conn);
    }
    
    protected void addUserAgentRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.addUserAgentRequestHeaders(HttpState, HttpConnection)");
        if (this.getRequestHeader("User-Agent") == null) {
            String agent = (String)this.getParams().getParameter("http.useragent");
            if (agent == null) {
                agent = "Jakarta Commons-HttpClient";
            }
            this.setRequestHeader("User-Agent", agent);
        }
    }
    
    protected void checkNotUsed() throws IllegalStateException {
        if (this.used) {
            throw new IllegalStateException("Already used.");
        }
    }
    
    protected void checkUsed() throws IllegalStateException {
        if (!this.used) {
            throw new IllegalStateException("Not Used.");
        }
    }
    
    protected static String generateRequestLine(final HttpConnection connection, final String name, final String requestPath, final String query, final String version) {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.generateRequestLine(HttpConnection, String, String, String, String)");
        final StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append(" ");
        if (!connection.isTransparent()) {
            final Protocol protocol = connection.getProtocol();
            buf.append(protocol.getScheme().toLowerCase());
            buf.append("://");
            buf.append(connection.getHost());
            if (connection.getPort() != -1 && connection.getPort() != protocol.getDefaultPort()) {
                buf.append(":");
                buf.append(connection.getPort());
            }
        }
        if (requestPath == null) {
            buf.append("/");
        }
        else {
            if (!connection.isTransparent() && !requestPath.startsWith("/")) {
                buf.append("/");
            }
            buf.append(requestPath);
        }
        if (query != null) {
            if (query.indexOf("?") != 0) {
                buf.append("?");
            }
            buf.append(query);
        }
        buf.append(" ");
        buf.append(version);
        buf.append("\r\n");
        return buf.toString();
    }
    
    protected void processResponseBody(final HttpState state, final HttpConnection conn) {
    }
    
    protected void processResponseHeaders(final HttpState state, final HttpConnection conn) {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.processResponseHeaders(HttpState, HttpConnection)");
        final CookieSpec parser = this.getCookieSpec(state);
        Header[] headers = this.getResponseHeaderGroup().getHeaders("set-cookie");
        this.processCookieHeaders(parser, headers, state, conn);
        if (parser instanceof CookieVersionSupport) {
            final CookieVersionSupport versupport = (CookieVersionSupport)parser;
            if (versupport.getVersion() > 0) {
                headers = this.getResponseHeaderGroup().getHeaders("set-cookie2");
                this.processCookieHeaders(parser, headers, state, conn);
            }
        }
    }
    
    protected void processCookieHeaders(final CookieSpec parser, final Header[] headers, final HttpState state, final HttpConnection conn) {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.processCookieHeaders(Header[], HttpState, HttpConnection)");
        String host = this.params.getVirtualHost();
        if (host == null) {
            host = conn.getHost();
        }
        for (int i = 0; i < headers.length; ++i) {
            final Header header = headers[i];
            Cookie[] cookies = null;
            try {
                cookies = parser.parse(host, conn.getPort(), this.getPath(), conn.isSecure(), header);
            }
            catch (final MalformedCookieException e) {
                if (HttpMethodBase.LOG.isWarnEnabled()) {
                    HttpMethodBase.LOG.warn((Object)("Invalid cookie header: \"" + header.getValue() + "\". " + e.getMessage()));
                }
            }
            if (cookies != null) {
                for (int j = 0; j < cookies.length; ++j) {
                    final Cookie cookie = cookies[j];
                    try {
                        parser.validate(host, conn.getPort(), this.getPath(), conn.isSecure(), cookie);
                        state.addCookie(cookie);
                        if (HttpMethodBase.LOG.isDebugEnabled()) {
                            HttpMethodBase.LOG.debug((Object)("Cookie accepted: \"" + parser.formatCookie(cookie) + "\""));
                        }
                    }
                    catch (final MalformedCookieException e2) {
                        if (HttpMethodBase.LOG.isWarnEnabled()) {
                            HttpMethodBase.LOG.warn((Object)("Cookie rejected: \"" + parser.formatCookie(cookie) + "\". " + e2.getMessage()));
                        }
                    }
                }
            }
        }
    }
    
    protected void processStatusLine(final HttpState state, final HttpConnection conn) {
    }
    
    protected void readResponse(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.readResponse(HttpState, HttpConnection)");
        while (this.statusLine == null) {
            this.readStatusLine(state, conn);
            this.processStatusLine(state, conn);
            this.readResponseHeaders(state, conn);
            this.processResponseHeaders(state, conn);
            final int status = this.statusLine.getStatusCode();
            if (status >= 100 && status < 200) {
                if (HttpMethodBase.LOG.isInfoEnabled()) {
                    HttpMethodBase.LOG.info((Object)("Discarding unexpected response: " + this.statusLine.toString()));
                }
                this.statusLine = null;
            }
        }
        this.readResponseBody(state, conn);
        this.processResponseBody(state, conn);
    }
    
    protected void readResponseBody(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.readResponseBody(HttpState, HttpConnection)");
        final InputStream stream = this.readResponseBody(conn);
        if (stream == null) {
            this.responseBodyConsumed();
        }
        else {
            conn.setLastResponseInputStream(stream);
            this.setResponseStream(stream);
        }
    }
    
    private InputStream readResponseBody(final HttpConnection conn) throws HttpException, IOException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.readResponseBody(HttpConnection)");
        this.responseBody = null;
        InputStream is = conn.getResponseInputStream();
        if (Wire.CONTENT_WIRE.enabled()) {
            is = new WireLogInputStream(is, Wire.CONTENT_WIRE);
        }
        final boolean canHaveBody = canResponseHaveBody(this.statusLine.getStatusCode());
        InputStream result = null;
        final Header transferEncodingHeader = this.responseHeaders.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            final String transferEncoding = transferEncodingHeader.getValue();
            if (!"chunked".equalsIgnoreCase(transferEncoding) && !"identity".equalsIgnoreCase(transferEncoding) && HttpMethodBase.LOG.isWarnEnabled()) {
                HttpMethodBase.LOG.warn((Object)("Unsupported transfer encoding: " + transferEncoding));
            }
            final HeaderElement[] encodings = transferEncodingHeader.getElements();
            final int len = encodings.length;
            if (len > 0 && "chunked".equalsIgnoreCase(encodings[len - 1].getName())) {
                if (conn.isResponseAvailable(conn.getParams().getSoTimeout())) {
                    result = new ChunkedInputStream(is, this);
                }
                else {
                    if (this.getParams().isParameterTrue("http.protocol.strict-transfer-encoding")) {
                        throw new ProtocolException("Chunk-encoded body declared but not sent");
                    }
                    HttpMethodBase.LOG.warn((Object)"Chunk-encoded body missing");
                }
            }
            else {
                HttpMethodBase.LOG.info((Object)"Response content is not chunk-encoded");
                this.setConnectionCloseForced(true);
                result = is;
            }
        }
        else {
            final long expectedLength = this.getResponseContentLength();
            if (expectedLength == -1L) {
                if (canHaveBody && this.effectiveVersion.greaterEquals(HttpVersion.HTTP_1_1)) {
                    final Header connectionHeader = this.responseHeaders.getFirstHeader("Connection");
                    String connectionDirective = null;
                    if (connectionHeader != null) {
                        connectionDirective = connectionHeader.getValue();
                    }
                    if (!"close".equalsIgnoreCase(connectionDirective)) {
                        HttpMethodBase.LOG.info((Object)"Response content length is not known");
                        this.setConnectionCloseForced(true);
                    }
                }
                result = is;
            }
            else {
                result = new ContentLengthInputStream(is, expectedLength);
            }
        }
        if (!canHaveBody) {
            result = null;
        }
        if (result != null) {
            result = new AutoCloseInputStream(result, new ResponseConsumedWatcher() {
                public void responseConsumed() {
                    HttpMethodBase.this.responseBodyConsumed();
                }
            });
        }
        return result;
    }
    
    protected void readResponseHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.readResponseHeaders(HttpState,HttpConnection)");
        this.getResponseHeaderGroup().clear();
        final Header[] headers = HttpParser.parseHeaders(conn.getResponseInputStream(), this.getParams().getHttpElementCharset());
        this.getResponseHeaderGroup().setHeaders(headers);
    }
    
    protected void readStatusLine(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.readStatusLine(HttpState, HttpConnection)");
        final int maxGarbageLines = this.getParams().getIntParameter("http.protocol.status-line-garbage-limit", Integer.MAX_VALUE);
        int count = 0;
        while (true) {
            final String s = conn.readLine(this.getParams().getHttpElementCharset());
            if (s == null && count == 0) {
                throw new NoHttpResponseException("The server " + conn.getHost() + " failed to respond");
            }
            if (Wire.HEADER_WIRE.enabled()) {
                Wire.HEADER_WIRE.input(s + "\r\n");
            }
            if (s != null && StatusLine.startsWithHTTP(s)) {
                this.statusLine = new StatusLine(s);
                final String versionStr = this.statusLine.getHttpVersion();
                if (this.getParams().isParameterFalse("http.protocol.unambiguous-statusline") && versionStr.equals("HTTP")) {
                    this.getParams().setVersion(HttpVersion.HTTP_1_0);
                    if (HttpMethodBase.LOG.isWarnEnabled()) {
                        HttpMethodBase.LOG.warn((Object)("Ambiguous status line (HTTP protocol version missing):" + this.statusLine.toString()));
                    }
                }
                else {
                    this.effectiveVersion = HttpVersion.parse(versionStr);
                }
                return;
            }
            if (s == null || count >= maxGarbageLines) {
                throw new ProtocolException("The server " + conn.getHost() + " failed to respond with a valid HTTP response");
            }
            ++count;
        }
    }
    
    protected void writeRequest(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.writeRequest(HttpState, HttpConnection)");
        this.writeRequestLine(state, conn);
        this.writeRequestHeaders(state, conn);
        conn.writeLine();
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output("\r\n");
        }
        final HttpVersion ver = this.getParams().getVersion();
        final Header expectheader = this.getRequestHeader("Expect");
        String expectvalue = null;
        if (expectheader != null) {
            expectvalue = expectheader.getValue();
        }
        if (expectvalue != null && expectvalue.compareToIgnoreCase("100-continue") == 0) {
            if (ver.greaterEquals(HttpVersion.HTTP_1_1)) {
                conn.flushRequestOutputStream();
                final int readTimeout = conn.getParams().getSoTimeout();
                try {
                    conn.setSocketTimeout(3000);
                    this.readStatusLine(state, conn);
                    this.processStatusLine(state, conn);
                    this.readResponseHeaders(state, conn);
                    this.processResponseHeaders(state, conn);
                    if (this.statusLine.getStatusCode() != 100) {
                        return;
                    }
                    this.statusLine = null;
                    HttpMethodBase.LOG.debug((Object)"OK to continue received");
                }
                catch (final InterruptedIOException e) {
                    if (!ExceptionUtil.isSocketTimeoutException(e)) {
                        throw e;
                    }
                    this.removeRequestHeader("Expect");
                    HttpMethodBase.LOG.info((Object)"100 (continue) read timeout. Resume sending the request");
                }
                finally {
                    conn.setSocketTimeout(readTimeout);
                }
            }
            else {
                this.removeRequestHeader("Expect");
                HttpMethodBase.LOG.info((Object)"'Expect: 100-continue' handshake is only supported by HTTP/1.1 or higher");
            }
        }
        this.writeRequestBody(state, conn);
        conn.flushRequestOutputStream();
    }
    
    protected boolean writeRequestBody(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        return true;
    }
    
    protected void writeRequestHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.writeRequestHeaders(HttpState,HttpConnection)");
        this.addRequestHeaders(state, conn);
        final String charset = this.getParams().getHttpElementCharset();
        final Header[] headers = this.getRequestHeaders();
        for (int i = 0; i < headers.length; ++i) {
            final String s = headers[i].toExternalForm();
            if (Wire.HEADER_WIRE.enabled()) {
                Wire.HEADER_WIRE.output(s);
            }
            conn.print(s, charset);
        }
    }
    
    protected void writeRequestLine(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.writeRequestLine(HttpState, HttpConnection)");
        final String requestLine = this.getRequestLine(conn);
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output(requestLine);
        }
        conn.print(requestLine, this.getParams().getHttpElementCharset());
    }
    
    private String getRequestLine(final HttpConnection conn) {
        return generateRequestLine(conn, this.getName(), this.getPath(), this.getQueryString(), this.effectiveVersion.toString());
    }
    
    public HttpMethodParams getParams() {
        return this.params;
    }
    
    public void setParams(final HttpMethodParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }
    
    public HttpVersion getEffectiveVersion() {
        return this.effectiveVersion;
    }
    
    private static boolean canResponseHaveBody(final int status) {
        HttpMethodBase.LOG.trace((Object)"enter HttpMethodBase.canResponseHaveBody(int)");
        boolean result = true;
        if ((status >= 100 && status <= 199) || status == 204 || status == 304) {
            result = false;
        }
        return result;
    }
    
    public String getProxyAuthenticationRealm() {
        return this.proxyAuthState.getRealm();
    }
    
    public String getAuthenticationRealm() {
        return this.hostAuthState.getRealm();
    }
    
    protected String getContentCharSet(final Header contentheader) {
        HttpMethodBase.LOG.trace((Object)"enter getContentCharSet( Header contentheader )");
        String charset = null;
        if (contentheader != null) {
            final HeaderElement[] values = contentheader.getElements();
            if (values.length == 1) {
                final NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }
        if (charset == null) {
            charset = this.getParams().getContentCharset();
            if (HttpMethodBase.LOG.isDebugEnabled()) {
                HttpMethodBase.LOG.debug((Object)("Default charset used: " + charset));
            }
        }
        return charset;
    }
    
    public String getRequestCharSet() {
        return this.getContentCharSet(this.getRequestHeader("Content-Type"));
    }
    
    public String getResponseCharSet() {
        return this.getContentCharSet(this.getResponseHeader("Content-Type"));
    }
    
    public int getRecoverableExceptionCount() {
        return this.recoverableExceptionCount;
    }
    
    protected void responseBodyConsumed() {
        this.responseStream = null;
        if (this.responseConnection != null) {
            this.responseConnection.setLastResponseInputStream(null);
            if (this.shouldCloseConnection(this.responseConnection)) {
                this.responseConnection.close();
            }
            else {
                try {
                    if (this.responseConnection.isResponseAvailable()) {
                        final boolean logExtraInput = this.getParams().isParameterTrue("http.protocol.warn-extra-input");
                        if (logExtraInput) {
                            HttpMethodBase.LOG.warn((Object)"Extra response data detected - closing connection");
                        }
                        this.responseConnection.close();
                    }
                }
                catch (final IOException e) {
                    HttpMethodBase.LOG.warn((Object)e.getMessage());
                    this.responseConnection.close();
                }
            }
        }
        this.connectionCloseForced = false;
        this.ensureConnectionRelease();
    }
    
    private void ensureConnectionRelease() {
        if (this.responseConnection != null) {
            this.responseConnection.releaseConnection();
            this.responseConnection = null;
        }
    }
    
    public HostConfiguration getHostConfiguration() {
        final HostConfiguration hostconfig = new HostConfiguration();
        hostconfig.setHost(this.httphost);
        return hostconfig;
    }
    
    public void setHostConfiguration(final HostConfiguration hostconfig) {
        if (hostconfig != null) {
            this.httphost = new HttpHost(hostconfig.getHost(), hostconfig.getPort(), hostconfig.getProtocol());
        }
        else {
            this.httphost = null;
        }
    }
    
    public MethodRetryHandler getMethodRetryHandler() {
        return this.methodRetryHandler;
    }
    
    public void setMethodRetryHandler(final MethodRetryHandler handler) {
        this.methodRetryHandler = handler;
    }
    
    void fakeResponse(final StatusLine statusline, final HeaderGroup responseheaders, final InputStream responseStream) {
        this.used = true;
        this.statusLine = statusline;
        this.responseHeaders = responseheaders;
        this.responseBody = null;
        this.responseStream = responseStream;
    }
    
    public AuthState getHostAuthState() {
        return this.hostAuthState;
    }
    
    public AuthState getProxyAuthState() {
        return this.proxyAuthState;
    }
    
    public boolean isAborted() {
        return this.aborted;
    }
    
    public boolean isRequestSent() {
        return this.requestSent;
    }
    
    static {
        LOG = LogFactory.getLog(HttpMethodBase.class);
    }
}
