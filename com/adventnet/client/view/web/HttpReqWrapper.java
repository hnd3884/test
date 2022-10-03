package com.adventnet.client.view.web;

import javax.servlet.http.HttpUpgradeHandler;
import java.util.Collection;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.DispatcherType;
import javax.servlet.AsyncContext;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import java.io.BufferedReader;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class HttpReqWrapper implements HttpServletRequest
{
    private Map<String, Object> defaultParam;
    private Map<String, Object> defaultAttrib;
    private String contextPath;
    
    public HttpReqWrapper(final String contextPath) {
        this.defaultParam = new HashMap<String, Object>();
        this.defaultAttrib = new HashMap<String, Object>();
        this.contextPath = contextPath;
    }
    
    public HttpReqWrapper(final String contextPath, final Map parameterProp, final Map attributeMap) {
        this.defaultParam = new HashMap<String, Object>();
        this.defaultAttrib = new HashMap<String, Object>();
        this.contextPath = contextPath;
        this.defaultParam = parameterProp;
        this.defaultAttrib = attributeMap;
    }
    
    private HttpReqWrapper() {
        this.defaultParam = new HashMap<String, Object>();
        this.defaultAttrib = new HashMap<String, Object>();
    }
    
    public Object getAttribute(final String str) {
        return this.defaultAttrib.get(str);
    }
    
    public Enumeration getAttributeNames() {
        return new EnumForIterator(this.defaultAttrib.keySet().iterator());
    }
    
    @Deprecated
    public String getAuthType() {
        return null;
    }
    
    public String getCharacterEncoding() {
        return null;
    }
    
    public int getContentLength() {
        return -1;
    }
    
    public String getContentType() {
        return null;
    }
    
    public String getContextPath() {
        return this.contextPath;
    }
    
    public Cookie[] getCookies() {
        return null;
    }
    
    public long getDateHeader(final String str) {
        return -1L;
    }
    
    public String getHeader(final String str) {
        return null;
    }
    
    public Enumeration getHeaderNames() {
        return null;
    }
    
    public Enumeration getHeaders(final String str) {
        return null;
    }
    
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }
    
    public int getIntHeader(final String str) {
        return -1;
    }
    
    public String getLocalAddr() {
        return null;
    }
    
    public String getLocalName() {
        return null;
    }
    
    public int getLocalPort() {
        return -1;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public Enumeration getLocales() {
        return null;
    }
    
    public String getMethod() {
        return null;
    }
    
    public String getParameter(final String str) {
        final Object ob = this.defaultParam.get(str);
        if (ob instanceof String) {
            return (String)ob;
        }
        if (ob instanceof String[] && ((String[])ob).length > 0) {
            final String[] arr = (String[])ob;
            return arr[0];
        }
        if (ob != null) {
            return ob.toString();
        }
        return null;
    }
    
    public void setParameter(final String str, final String val) {
        this.defaultParam.put(str, val);
    }
    
    public Map getParameterMap() {
        return this.defaultParam;
    }
    
    public Enumeration getParameterNames() {
        return new EnumForIterator(this.defaultParam.keySet().iterator());
    }
    
    public String[] getParameterValues(final String str) {
        final Object ob = this.defaultParam.get(str);
        if (ob instanceof String) {
            return new String[] { (String)ob };
        }
        if (ob instanceof String[]) {
            return (String[])ob;
        }
        if (ob != null) {
            return new String[] { ob.toString() };
        }
        return null;
    }
    
    public String getPathInfo() {
        return null;
    }
    
    public String getPathTranslated() {
        return null;
    }
    
    public String getProtocol() {
        return null;
    }
    
    public String getQueryString() {
        return null;
    }
    
    public BufferedReader getReader() throws IOException {
        return null;
    }
    
    public String getRealPath(final String str) {
        return null;
    }
    
    public String getRemoteAddr() {
        return null;
    }
    
    public String getRemoteHost() {
        return null;
    }
    
    public int getRemotePort() {
        return -1;
    }
    
    public String getRemoteUser() {
        return null;
    }
    
    public RequestDispatcher getRequestDispatcher(final String str) {
        return null;
    }
    
    public String getRequestURI() {
        return null;
    }
    
    public StringBuffer getRequestURL() {
        return null;
    }
    
    public String getRequestedSessionId() {
        return null;
    }
    
    public String getScheme() {
        return null;
    }
    
    public String getServerName() {
        return null;
    }
    
    public int getServerPort() {
        return -1;
    }
    
    public String getServletPath() {
        return null;
    }
    
    public HttpSession getSession() {
        return null;
    }
    
    public HttpSession getSession(final boolean param) {
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
    
    public boolean isSecure() {
        return false;
    }
    
    public boolean isUserInRole(final String str) {
        return false;
    }
    
    public void removeAttribute(final String str) {
        this.defaultAttrib.remove(str);
    }
    
    public void setAttribute(final String str, final Object obj) {
        this.defaultAttrib.put(str, obj);
    }
    
    public void setCharacterEncoding(final String str) throws UnsupportedEncodingException {
    }
    
    public AsyncContext getAsyncContext() {
        return null;
    }
    
    public long getContentLengthLong() {
        return 0L;
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
    
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }
    
    public AsyncContext startAsync(final ServletRequest arg0, final ServletResponse arg1) throws IllegalStateException {
        return null;
    }
    
    public boolean authenticate(final HttpServletResponse arg0) throws IOException, ServletException {
        return false;
    }
    
    public String changeSessionId() {
        return null;
    }
    
    public Part getPart(final String arg0) throws IOException, ServletException {
        return null;
    }
    
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }
    
    public void login(final String arg0, final String arg1) throws ServletException {
    }
    
    public void logout() throws ServletException {
    }
    
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> arg0) throws IOException, ServletException {
        return null;
    }
    
    class EnumForIterator implements Enumeration
    {
        private Iterator iterator;
        
        EnumForIterator(final Iterator iterator) {
            this.iterator = null;
            this.iterator = iterator;
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }
        
        @Override
        public Object nextElement() {
            return this.iterator.next();
        }
    }
}
