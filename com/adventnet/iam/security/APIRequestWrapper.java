package com.adventnet.iam.security;

import java.io.ByteArrayInputStream;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.DispatcherType;
import javax.servlet.AsyncContext;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.nio.charset.Charset;
import javax.servlet.RequestDispatcher;
import java.io.BufferedReader;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import java.util.HashMap;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.Cookie;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

public class APIRequestWrapper implements HttpServletRequest
{
    private static final Logger LOGGER;
    private String url;
    private String requestMethod;
    private Map<String, String[]> parameterMap;
    private String remoteAddr;
    private String contextPath;
    private Map<String, Object> attributesMap;
    private static final Pattern IP_PATTERN;
    private Cookie[] cookies;
    private Map<String, List<String>> headerMap;
    private String charEncoding;
    private String contentType;
    private long contentLength;
    private InputStream inputstream;
    
    public APIRequestWrapper(final String url, final String contextPath, final Map<String, String[]> parameterMap) {
        this.url = null;
        this.requestMethod = "GET";
        this.parameterMap = null;
        this.remoteAddr = null;
        this.contextPath = "";
        this.attributesMap = null;
        this.cookies = null;
        this.headerMap = null;
        this.charEncoding = null;
        this.contentType = null;
        this.contentLength = -1L;
        this.url = url;
        this.parameterMap = parameterMap;
        this.setAttribute("ZSEC_API_CONTEXT_PATH", contextPath);
    }
    
    public APIRequestWrapper(final String url, final String contextPath, final Map<String, String[]> parameterMap, final String remoteAddr) {
        this(url, contextPath, parameterMap);
        this.setRemoteAddr(remoteAddr);
    }
    
    public String getRequestURI() {
        return this.url;
    }
    
    public String getParameter(final String param) {
        if (this.parameterMap != null) {
            final String[] values = this.parameterMap.get(param);
            if (values != null) {
                return values[0];
            }
        }
        return null;
    }
    
    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }
    
    public Enumeration<String> getParameterNames() {
        if (this.parameterMap != null) {
            final Set<String> paramNames = this.parameterMap.keySet();
            final ArrayList<String> list = new ArrayList<String>();
            list.addAll(paramNames);
            return Collections.enumeration(list);
        }
        return null;
    }
    
    public String[] getParameterValues(final String param) {
        if (this.parameterMap != null) {
            return this.parameterMap.get(param);
        }
        return null;
    }
    
    public String getMethod() {
        return this.requestMethod;
    }
    
    public void setRemoteAddr(final String addr) {
        if (!SecurityUtil.isValid(addr) || !SecurityUtil.matchPattern(addr, APIRequestWrapper.IP_PATTERN)) {
            throw new IAMSecurityException("Invalid IP Address");
        }
        this.remoteAddr = addr;
    }
    
    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            try {
                this.remoteAddr = InetAddress.getAllByName("localhost")[0].getHostAddress();
            }
            catch (final UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return this.remoteAddr;
    }
    
    public void setCookie(final Cookie[] cookies) {
        this.cookies = cookies;
    }
    
    public void setHeader(final String headerName, final String headerValue) {
        if (this.headerMap == null) {
            this.headerMap = new HashMap<String, List<String>>();
        }
        if (this.headerMap.containsKey(headerName)) {
            this.headerMap.get(headerName).add(headerValue);
        }
        else {
            final List<String> headers = new ArrayList<String>();
            headers.add(headerValue);
            this.headerMap.put(headerName, headers);
        }
    }
    
    public String getContextPath() {
        return this.contextPath;
    }
    
    public Object getAttribute(final String name) {
        if (this.attributesMap != null) {
            return this.attributesMap.get(name);
        }
        return null;
    }
    
    public Enumeration<String> getAttributeNames() {
        if (this.attributesMap != null) {
            final Set<String> attributeNames = this.attributesMap.keySet();
            final ArrayList<String> list = new ArrayList<String>();
            list.addAll(attributeNames);
            return Collections.enumeration(list);
        }
        return null;
    }
    
    public void removeAttribute(final String name) {
        if (this.attributesMap != null) {
            this.attributesMap.remove(name);
        }
    }
    
    public void setAttribute(final String name, final Object value) {
        if (this.attributesMap == null) {
            this.attributesMap = new HashMap<String, Object>();
        }
        this.attributesMap.put(name, value);
    }
    
    public String getCharacterEncoding() {
        if (this.charEncoding == null) {
            this.charEncoding = getCharsetFromContentType(this.getContentType());
        }
        return this.charEncoding;
    }
    
    public int getContentLength() {
        final long length = this.getContentLengthLong();
        if (length < 2147483647L) {
            return (int)length;
        }
        return -1;
    }
    
    public String getContentType() {
        if (this.contentType == null) {
            this.contentType = this.getHeader("Content-Type");
        }
        return this.contentType;
    }
    
    public void setContentType(final String type) {
        this.contentType = type;
    }
    
    public ServletInputStream getInputStream() throws IOException {
        if (this.hasInputStream()) {
            return new CachedServletInputStream(this.inputstream);
        }
        return null;
    }
    
    public String getLocalAddr() {
        return null;
    }
    
    public String getLocalName() {
        return null;
    }
    
    public int getLocalPort() {
        return 0;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public Enumeration<Locale> getLocales() {
        return null;
    }
    
    public String getProtocol() {
        return null;
    }
    
    public BufferedReader getReader() throws IOException {
        return null;
    }
    
    public String getRealPath(final String arg0) {
        return null;
    }
    
    public String getRemoteHost() {
        return null;
    }
    
    public int getRemotePort() {
        return 0;
    }
    
    public RequestDispatcher getRequestDispatcher(final String arg0) {
        return null;
    }
    
    public String getScheme() {
        return null;
    }
    
    public String getServerName() {
        return null;
    }
    
    public int getServerPort() {
        return 0;
    }
    
    public boolean isSecure() {
        return false;
    }
    
    public void setCharacterEncoding(final String enc) throws UnsupportedEncodingException {
        if (enc == null) {
            return;
        }
        try {
            this.charEncoding = Charset.forName(enc).name();
        }
        catch (final Exception e) {
            APIRequestWrapper.LOGGER.log(Level.WARNING, "IAMSecurityException ErrorCode: {0} Error Message: {1}", new Object[] { "INVALID_CHARACTER_ENCODING", e.getMessage() });
            throw new IAMSecurityException("INVALID_CHARACTER_ENCODING");
        }
    }
    
    public String getAuthType() {
        return null;
    }
    
    public Cookie[] getCookies() {
        return this.cookies;
    }
    
    public long getDateHeader(final String arg0) {
        return 0L;
    }
    
    public String getHeader(final String headerName) {
        if (this.headerMap != null && this.headerMap.containsKey(headerName)) {
            return this.headerMap.get(headerName).get(0);
        }
        return null;
    }
    
    private String getUniqueHeaderValue(final String headerName) {
        if (this.headerMap != null) {
            final List<String> headerValues = this.headerMap.get(headerName);
            if (headerValues != null) {
                if (headerValues.size() == 1) {
                    return headerValues.get(0);
                }
                if (headerValues.size() > 1) {
                    return null;
                }
            }
        }
        return null;
    }
    
    public Enumeration<String> getHeaderNames() {
        if (this.headerMap != null) {
            final Set<String> paramNames = this.headerMap.keySet();
            final ArrayList<String> list = new ArrayList<String>();
            list.addAll(paramNames);
            return Collections.enumeration(list);
        }
        return null;
    }
    
    public Enumeration<String> getHeaders(final String headerName) {
        if (this.headerMap != null && this.headerMap.containsKey(headerName)) {
            final List<String> headerValues = this.headerMap.get(headerName);
            return Collections.enumeration(headerValues);
        }
        return null;
    }
    
    public int getIntHeader(final String arg0) {
        return 0;
    }
    
    public String getPathInfo() {
        return null;
    }
    
    public String getPathTranslated() {
        return null;
    }
    
    public String getQueryString() {
        return null;
    }
    
    public String getRemoteUser() {
        return null;
    }
    
    public StringBuffer getRequestURL() {
        return null;
    }
    
    public String getRequestedSessionId() {
        return null;
    }
    
    public String getServletPath() {
        return this.url;
    }
    
    public HttpSession getSession() {
        return null;
    }
    
    public HttpSession getSession(final boolean arg0) {
        return null;
    }
    
    public Principal getUserPrincipal() {
        return null;
    }
    
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }
    
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }
    
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }
    
    public boolean isRequestedSessionIdValid() {
        return false;
    }
    
    public boolean isUserInRole(final String arg0) {
        return false;
    }
    
    public void setMethod(final String method) {
        if (SecurityUtil.isValid(method)) {
            this.requestMethod = method;
        }
    }
    
    public AsyncContext getAsyncContext() {
        return null;
    }
    
    public DispatcherType getDispatcherType() {
        return null;
    }
    
    public ServletContext getServletContext() {
        return null;
    }
    
    public boolean isAsyncStarted() {
        return false;
    }
    
    public boolean isAsyncSupported() {
        return false;
    }
    
    public AsyncContext startAsync() {
        return null;
    }
    
    public AsyncContext startAsync(final ServletRequest arg0, final ServletResponse arg1) {
        return null;
    }
    
    public boolean authenticate(final HttpServletResponse arg0) throws IOException, ServletException {
        return false;
    }
    
    public Part getPart(final String arg0) throws IOException, IllegalStateException, ServletException {
        return null;
    }
    
    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        return null;
    }
    
    public void login(final String arg0, final String arg1) throws ServletException {
    }
    
    public void logout() throws ServletException {
    }
    
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        return null;
    }
    
    public String changeSessionId() {
        return null;
    }
    
    public long getContentLengthLong() {
        if (this.contentLength > -1L) {
            return this.contentLength;
        }
        final String clH = this.getUniqueHeaderValue("Content-Length");
        try {
            this.contentLength = (SecurityUtil.isValid(clH) ? Long.parseLong(clH) : -1L);
        }
        catch (final NumberFormatException nfe) {
            APIRequestWrapper.LOGGER.log(Level.WARNING, String.format("NumberFormateException occured for content-length header \"%s\"", clH), nfe);
        }
        return this.contentLength;
    }
    
    private static String getCharsetFromContentType(final String contentType) {
        if (contentType == null) {
            return null;
        }
        final int start = contentType.indexOf("charset=");
        if (start < 0) {
            return null;
        }
        String encoding = contentType.substring(start + 8);
        final int end = encoding.indexOf(59);
        if (end >= 0) {
            encoding = encoding.substring(0, end);
        }
        encoding = encoding.trim();
        if (encoding.length() > 2 && encoding.startsWith("\"") && encoding.endsWith("\"")) {
            encoding = encoding.substring(1, encoding.length() - 1);
        }
        return encoding.trim();
    }
    
    public void setInputStream(final byte[] streamAsBytes) {
        this.setInputStream(new ByteArrayInputStream(streamAsBytes));
    }
    
    public void setInputStream(final InputStream inputstream) {
        this.inputstream = inputstream;
    }
    
    boolean hasInputStream() {
        return this.inputstream != null;
    }
    
    static {
        LOGGER = Logger.getLogger(APIRequestWrapper.class.getName());
        IP_PATTERN = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}");
    }
}
