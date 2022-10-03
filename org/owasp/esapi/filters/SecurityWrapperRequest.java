package org.owasp.esapi.filters;

import java.io.UnsupportedEncodingException;
import org.owasp.esapi.errors.AccessControlException;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import java.io.BufferedReader;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.Cookie;
import org.owasp.esapi.errors.ValidationException;
import java.util.Enumeration;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class SecurityWrapperRequest extends HttpServletRequestWrapper implements HttpServletRequest
{
    private final Logger logger;
    private String allowableContentRoot;
    
    public SecurityWrapperRequest(final HttpServletRequest request) {
        super(request);
        this.logger = ESAPI.getLogger("SecurityWrapperRequest");
        this.allowableContentRoot = "WEB-INF";
    }
    
    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest)super.getRequest();
    }
    
    public Object getAttribute(final String name) {
        return this.getHttpServletRequest().getAttribute(name);
    }
    
    public Enumeration getAttributeNames() {
        return this.getHttpServletRequest().getAttributeNames();
    }
    
    public String getAuthType() {
        return this.getHttpServletRequest().getAuthType();
    }
    
    public String getCharacterEncoding() {
        return this.getHttpServletRequest().getCharacterEncoding();
    }
    
    public int getContentLength() {
        return this.getHttpServletRequest().getContentLength();
    }
    
    public String getContentType() {
        return this.getHttpServletRequest().getContentType();
    }
    
    public String getContextPath() {
        final String path = this.getHttpServletRequest().getContextPath();
        if (path == null || "".equals(path.trim())) {
            return "";
        }
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP context path: " + path, path, "HTTPContextPath", 150, false);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public Cookie[] getCookies() {
        final Cookie[] cookies = this.getHttpServletRequest().getCookies();
        if (cookies == null) {
            return new Cookie[0];
        }
        final List<Cookie> newCookies = new ArrayList<Cookie>();
        for (final Cookie c : cookies) {
            try {
                final String name = ESAPI.validator().getValidInput("Cookie name: " + c.getName(), c.getName(), "HTTPCookieName", 150, true);
                final String value = ESAPI.validator().getValidInput("Cookie value: " + c.getValue(), c.getValue(), "HTTPCookieValue", 1000, true);
                final int maxAge = c.getMaxAge();
                final String domain = c.getDomain();
                final String path = c.getPath();
                final Cookie n = new Cookie(name, value);
                n.setMaxAge(maxAge);
                if (domain != null) {
                    n.setDomain(ESAPI.validator().getValidInput("Cookie domain: " + domain, domain, "HTTPHeaderValue", 200, false));
                }
                if (path != null) {
                    n.setPath(ESAPI.validator().getValidInput("Cookie path: " + path, path, "HTTPHeaderValue", 200, false));
                }
                newCookies.add(n);
            }
            catch (final ValidationException e) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Skipping bad cookie: " + c.getName() + "=" + c.getValue(), e);
            }
        }
        return newCookies.toArray(new Cookie[newCookies.size()]);
    }
    
    public long getDateHeader(final String name) {
        return this.getHttpServletRequest().getDateHeader(name);
    }
    
    public String getHeader(final String name) {
        final String value = this.getHttpServletRequest().getHeader(name);
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP header value: " + value, value, "HTTPHeaderValue", 200, true);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public Enumeration getHeaderNames() {
        final Vector<String> v = new Vector<String>();
        final Enumeration en = this.getHttpServletRequest().getHeaderNames();
        while (en.hasMoreElements()) {
            try {
                final String name = en.nextElement();
                final String clean = ESAPI.validator().getValidInput("HTTP header name: " + name, name, "HTTPHeaderName", 150, true);
                v.add(clean);
            }
            catch (final ValidationException e) {}
        }
        return v.elements();
    }
    
    public Enumeration getHeaders(final String name) {
        final Vector<String> v = new Vector<String>();
        final Enumeration en = this.getHttpServletRequest().getHeaders(name);
        while (en.hasMoreElements()) {
            try {
                final String value = en.nextElement();
                final String clean = ESAPI.validator().getValidInput("HTTP header value (" + name + "): " + value, value, "HTTPHeaderValue", 200, true);
                v.add(clean);
            }
            catch (final ValidationException e) {}
        }
        return v.elements();
    }
    
    public ServletInputStream getInputStream() throws IOException {
        return this.getHttpServletRequest().getInputStream();
    }
    
    public int getIntHeader(final String name) {
        return this.getHttpServletRequest().getIntHeader(name);
    }
    
    public String getLocalAddr() {
        return this.getHttpServletRequest().getLocalAddr();
    }
    
    public Locale getLocale() {
        return this.getHttpServletRequest().getLocale();
    }
    
    public Enumeration getLocales() {
        return this.getHttpServletRequest().getLocales();
    }
    
    public String getLocalName() {
        return this.getHttpServletRequest().getLocalName();
    }
    
    public int getLocalPort() {
        return this.getHttpServletRequest().getLocalPort();
    }
    
    public String getMethod() {
        return this.getHttpServletRequest().getMethod();
    }
    
    public String getParameter(final String name) {
        return this.getParameter(name, true);
    }
    
    public String getParameter(final String name, final boolean allowNull) {
        return this.getParameter(name, allowNull, 2000, "HTTPParameterValue");
    }
    
    public String getParameter(final String name, final boolean allowNull, final int maxLength) {
        return this.getParameter(name, allowNull, maxLength, "HTTPParameterValue");
    }
    
    public String getParameter(final String name, final boolean allowNull, final int maxLength, final String regexName) {
        final String orig = this.getHttpServletRequest().getParameter(name);
        String clean = null;
        try {
            clean = ESAPI.validator().getValidInput("HTTP parameter name: " + name, orig, regexName, maxLength, allowNull);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public Map getParameterMap() {
        final Map<String, String[]> map = this.getHttpServletRequest().getParameterMap();
        final Map<String, String[]> cleanMap = new HashMap<String, String[]>();
        for (final Object o : map.entrySet()) {
            try {
                final Map.Entry e = (Map.Entry)o;
                final String name = e.getKey();
                final String cleanName = ESAPI.validator().getValidInput("HTTP parameter name: " + name, name, "HTTPParameterName", 100, true);
                final String[] value = e.getValue();
                final String[] cleanValues = new String[value.length];
                for (int j = 0; j < value.length; ++j) {
                    final String cleanValue = ESAPI.validator().getValidInput("HTTP parameter value: " + value[j], value[j], "HTTPParameterValue", 2000, true);
                    cleanValues[j] = cleanValue;
                }
                cleanMap.put(cleanName, cleanValues);
            }
            catch (final ValidationException ex) {}
        }
        return cleanMap;
    }
    
    public Enumeration getParameterNames() {
        final Vector<String> v = new Vector<String>();
        final Enumeration en = this.getHttpServletRequest().getParameterNames();
        while (en.hasMoreElements()) {
            try {
                final String name = en.nextElement();
                final String clean = ESAPI.validator().getValidInput("HTTP parameter name: " + name, name, "HTTPParameterName", 150, true);
                v.add(clean);
            }
            catch (final ValidationException e) {}
        }
        return v.elements();
    }
    
    public String[] getParameterValues(final String name) {
        final String[] values = this.getHttpServletRequest().getParameterValues(name);
        if (values == null) {
            return null;
        }
        final List<String> newValues = new ArrayList<String>();
        for (final String value : values) {
            try {
                final String cleanValue = ESAPI.validator().getValidInput("HTTP parameter value: " + value, value, "HTTPParameterValue", 2000, true);
                newValues.add(cleanValue);
            }
            catch (final ValidationException e) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Skipping bad parameter");
            }
        }
        return newValues.toArray(new String[newValues.size()]);
    }
    
    public String getPathInfo() {
        final String path = this.getHttpServletRequest().getPathInfo();
        if (path == null) {
            return null;
        }
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP path: " + path, path, "HTTPPath", 150, true);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public String getPathTranslated() {
        return this.getHttpServletRequest().getPathTranslated();
    }
    
    public String getProtocol() {
        return this.getHttpServletRequest().getProtocol();
    }
    
    public String getQueryString() {
        final String query = this.getHttpServletRequest().getQueryString();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP query string: " + query, query, "HTTPQueryString", 2000, true);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public BufferedReader getReader() throws IOException {
        return this.getHttpServletRequest().getReader();
    }
    
    @Deprecated
    public String getRealPath(final String path) {
        return this.getHttpServletRequest().getRealPath(path);
    }
    
    public String getRemoteAddr() {
        return this.getHttpServletRequest().getRemoteAddr();
    }
    
    public String getRemoteHost() {
        return this.getHttpServletRequest().getRemoteHost();
    }
    
    public int getRemotePort() {
        return this.getHttpServletRequest().getRemotePort();
    }
    
    public String getRemoteUser() {
        return ESAPI.authenticator().getCurrentUser().getAccountName();
    }
    
    public RequestDispatcher getRequestDispatcher(final String path) {
        if (path.startsWith(this.allowableContentRoot)) {
            return this.getHttpServletRequest().getRequestDispatcher(path);
        }
        return null;
    }
    
    public String getRequestedSessionId() {
        final String id = this.getHttpServletRequest().getRequestedSessionId();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("Requested cookie: " + id, id, "HTTPJSESSIONID", 50, false);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public String getRequestURI() {
        final String uri = this.getHttpServletRequest().getRequestURI();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP URI: " + uri, uri, "HTTPURI", 2000, false);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public StringBuffer getRequestURL() {
        final String url = this.getHttpServletRequest().getRequestURL().toString();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP URL: " + url, url, "HTTPURL", 2000, false);
        }
        catch (final ValidationException ex) {}
        return new StringBuffer(clean);
    }
    
    public String getScheme() {
        final String scheme = this.getHttpServletRequest().getScheme();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP scheme: " + scheme, scheme, "HTTPScheme", 10, false);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public String getServerName() {
        final String name = this.getHttpServletRequest().getServerName();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP server name: " + name, name, "HTTPServerName", 100, false);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public int getServerPort() {
        int port = this.getHttpServletRequest().getServerPort();
        if (port < 0 || port > 65535) {
            this.logger.warning(Logger.SECURITY_FAILURE, "HTTP server port out of range: " + port);
            port = 0;
        }
        return port;
    }
    
    public String getServletPath() {
        final String path = this.getHttpServletRequest().getServletPath();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP servlet path: " + path, path, "HTTPServletPath", 100, false);
        }
        catch (final ValidationException ex) {}
        return clean;
    }
    
    public HttpSession getSession() {
        final HttpSession session = this.getHttpServletRequest().getSession();
        if (ESAPI.securityConfiguration().getForceHttpOnlySession() && session.getAttribute("HTTP_ONLY") == null) {
            session.setAttribute("HTTP_ONLY", (Object)"set");
            final Cookie cookie = new Cookie(ESAPI.securityConfiguration().getHttpSessionIdName(), session.getId());
            cookie.setPath(this.getHttpServletRequest().getContextPath());
            cookie.setMaxAge(-1);
            final HttpServletResponse response = ESAPI.currentResponse();
            if (response != null) {
                ESAPI.currentResponse().addCookie(cookie);
            }
        }
        return session;
    }
    
    public HttpSession getSession(final boolean create) {
        final HttpSession session = this.getHttpServletRequest().getSession(create);
        if (session == null) {
            return null;
        }
        if (ESAPI.securityConfiguration().getForceHttpOnlySession() && session.getAttribute("HTTP_ONLY") == null) {
            session.setAttribute("HTTP_ONLY", (Object)"set");
            final Cookie cookie = new Cookie(ESAPI.securityConfiguration().getHttpSessionIdName(), session.getId());
            cookie.setMaxAge(-1);
            cookie.setPath(this.getHttpServletRequest().getContextPath());
            final HttpServletResponse response = ESAPI.currentResponse();
            if (response != null) {
                ESAPI.currentResponse().addCookie(cookie);
            }
        }
        return session;
    }
    
    public Principal getUserPrincipal() {
        return ESAPI.authenticator().getCurrentUser();
    }
    
    public boolean isRequestedSessionIdFromCookie() {
        return this.getHttpServletRequest().isRequestedSessionIdFromCookie();
    }
    
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return this.getHttpServletRequest().isRequestedSessionIdFromUrl();
    }
    
    public boolean isRequestedSessionIdFromURL() {
        return this.getHttpServletRequest().isRequestedSessionIdFromURL();
    }
    
    public boolean isRequestedSessionIdValid() {
        return this.getHttpServletRequest().isRequestedSessionIdValid();
    }
    
    public boolean isSecure() {
        try {
            ESAPI.httpUtilities().assertSecureChannel();
        }
        catch (final AccessControlException e) {
            return false;
        }
        return true;
    }
    
    public boolean isUserInRole(final String role) {
        return ESAPI.authenticator().getCurrentUser().isInRole(role);
    }
    
    public void removeAttribute(final String name) {
        this.getHttpServletRequest().removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object o) {
        this.getHttpServletRequest().setAttribute(name, o);
    }
    
    public void setCharacterEncoding(final String enc) throws UnsupportedEncodingException {
        this.getHttpServletRequest().setCharacterEncoding(ESAPI.securityConfiguration().getCharacterEncoding());
    }
    
    public String getAllowableContentRoot() {
        return this.allowableContentRoot;
    }
    
    public void setAllowableContentRoot(final String allowableContentRoot) {
        this.allowableContentRoot = (allowableContentRoot.startsWith("/") ? allowableContentRoot : ("/" + allowableContentRoot));
    }
}
