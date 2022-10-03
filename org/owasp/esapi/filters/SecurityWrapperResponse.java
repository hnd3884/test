package org.owasp.esapi.filters;

import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import java.util.Locale;
import java.io.IOException;
import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.ValidationErrorList;
import javax.servlet.http.Cookie;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityWrapperResponse extends HttpServletResponseWrapper implements HttpServletResponse
{
    private final Logger logger;
    private String mode;
    
    public SecurityWrapperResponse(final HttpServletResponse response) {
        super(response);
        this.logger = ESAPI.getLogger("SecurityWrapperResponse");
        this.mode = "log";
    }
    
    public SecurityWrapperResponse(final HttpServletResponse response, final String mode) {
        super(response);
        this.logger = ESAPI.getLogger("SecurityWrapperResponse");
        this.mode = "log";
        this.mode = mode;
    }
    
    private HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse)super.getResponse();
    }
    
    public void addCookie(final Cookie cookie) {
        final String name = cookie.getName();
        final String value = cookie.getValue();
        final int maxAge = cookie.getMaxAge();
        final String domain = cookie.getDomain();
        final String path = cookie.getPath();
        final boolean secure = cookie.getSecure();
        final ValidationErrorList errors = new ValidationErrorList();
        final String cookieName = ESAPI.validator().getValidInput("cookie name", name, "HTTPCookieName", 50, false, errors);
        final String cookieValue = ESAPI.validator().getValidInput("cookie value", value, "HTTPCookieValue", ESAPI.securityConfiguration().getMaxHttpHeaderSize(), false, errors);
        if (errors.size() == 0) {
            final String header = this.createCookieHeader(name, value, maxAge, domain, path, secure);
            this.addHeader("Set-Cookie", header);
            return;
        }
        if (this.mode.equals("skip")) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to add unsafe data to cookie (skip mode). Skipping cookie and continuing.");
            return;
        }
        if (this.mode.equals("log")) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to add unsafe data to cookie (log mode). Adding unsafe cookie anyway and continuing.");
            this.getHttpServletResponse().addCookie(cookie);
            return;
        }
        if (this.mode.equals("sanitize")) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to add unsafe data to cookie (sanitize mode). Sanitizing cookie and continuing.");
            final String header = this.createCookieHeader(cookieName, cookieValue, maxAge, domain, path, secure);
            this.addHeader("Set-Cookie", header);
            return;
        }
        throw new IntrusionException("Security error", "Attempt to add unsafe data to cookie (throw mode)");
    }
    
    private String createCookieHeader(final String name, final String value, final int maxAge, final String domain, final String path, final boolean secure) {
        String header = name + "=" + value;
        header = header + "; Max-Age=" + maxAge;
        if (domain != null) {
            header = header + "; Domain=" + domain;
        }
        if (path != null) {
            header = header + "; Path=" + path;
        }
        if (secure || ESAPI.securityConfiguration().getForceSecureCookies()) {
            header += "; Secure";
        }
        if (ESAPI.securityConfiguration().getForceHttpOnlyCookies()) {
            header += "; HttpOnly";
        }
        return header;
    }
    
    public void addDateHeader(final String name, final long date) {
        try {
            final String safeName = ESAPI.validator().getValidInput("safeSetDateHeader", name, "HTTPHeaderName", 20, false);
            this.getHttpServletResponse().addDateHeader(safeName, date);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set invalid date header name denied", e);
        }
    }
    
    public void addHeader(final String name, final String value) {
        try {
            final String strippedName = StringUtilities.stripControls(name);
            final String strippedValue = StringUtilities.stripControls(value);
            final String safeName = ESAPI.validator().getValidInput("addHeader", strippedName, "HTTPHeaderName", 20, false);
            final String safeValue = ESAPI.validator().getValidInput("addHeader", strippedValue, "HTTPHeaderValue", ESAPI.securityConfiguration().getMaxHttpHeaderSize(), false);
            this.getHttpServletResponse().addHeader(safeName, safeValue);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to add invalid header denied", e);
        }
    }
    
    public void addIntHeader(final String name, final int value) {
        try {
            final String safeName = ESAPI.validator().getValidInput("safeSetDateHeader", name, "HTTPHeaderName", 20, false);
            this.getHttpServletResponse().addIntHeader(safeName, value);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set invalid int header name denied", e);
        }
    }
    
    public boolean containsHeader(final String name) {
        return this.getHttpServletResponse().containsHeader(name);
    }
    
    @Deprecated
    public String encodeRedirectUrl(final String url) {
        return url;
    }
    
    public String encodeRedirectURL(final String url) {
        return url;
    }
    
    @Deprecated
    public String encodeUrl(final String url) {
        return url;
    }
    
    public String encodeURL(final String url) {
        return url;
    }
    
    public void flushBuffer() throws IOException {
        this.getHttpServletResponse().flushBuffer();
    }
    
    public int getBufferSize() {
        return this.getHttpServletResponse().getBufferSize();
    }
    
    public String getCharacterEncoding() {
        return this.getHttpServletResponse().getCharacterEncoding();
    }
    
    public String getContentType() {
        return this.getHttpServletResponse().getContentType();
    }
    
    public Locale getLocale() {
        return this.getHttpServletResponse().getLocale();
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return this.getHttpServletResponse().getOutputStream();
    }
    
    public PrintWriter getWriter() throws IOException {
        return this.getHttpServletResponse().getWriter();
    }
    
    public boolean isCommitted() {
        return this.getHttpServletResponse().isCommitted();
    }
    
    public void reset() {
        this.getHttpServletResponse().reset();
    }
    
    public void resetBuffer() {
        this.getHttpServletResponse().resetBuffer();
    }
    
    public void sendError(final int sc) throws IOException {
        this.getHttpServletResponse().sendError(200, this.getHTTPMessage(sc));
    }
    
    public void sendError(final int sc, final String msg) throws IOException {
        this.getHttpServletResponse().sendError(200, ESAPI.encoder().encodeForHTML(msg));
    }
    
    public void sendRedirect(final String location) throws IOException {
        if (!ESAPI.validator().isValidRedirectLocation("Redirect", location, false)) {
            this.logger.fatal(Logger.SECURITY_FAILURE, "Bad redirect location: " + location);
            throw new IOException("Redirect failed");
        }
        this.getHttpServletResponse().sendRedirect(location);
    }
    
    public void setBufferSize(final int size) {
        this.getHttpServletResponse().setBufferSize(size);
    }
    
    public void setCharacterEncoding(final String charset) {
        this.getHttpServletResponse().setCharacterEncoding(ESAPI.securityConfiguration().getCharacterEncoding());
    }
    
    public void setContentLength(final int len) {
        this.getHttpServletResponse().setContentLength(len);
    }
    
    public void setContentType(final String type) {
        this.getHttpServletResponse().setContentType(type);
    }
    
    public void setDateHeader(final String name, final long date) {
        try {
            final String safeName = ESAPI.validator().getValidInput("safeSetDateHeader", name, "HTTPHeaderName", 20, false);
            this.getHttpServletResponse().setDateHeader(safeName, date);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set invalid date header name denied", e);
        }
    }
    
    public void setHeader(final String name, final String value) {
        try {
            final String strippedName = StringUtilities.stripControls(name);
            final String strippedValue = StringUtilities.stripControls(value);
            final String safeName = ESAPI.validator().getValidInput("setHeader", strippedName, "HTTPHeaderName", 50, false);
            final String safeValue = ESAPI.validator().getValidInput("setHeader", strippedValue, "HTTPHeaderValue", ESAPI.securityConfiguration().getMaxHttpHeaderSize(), false);
            this.getHttpServletResponse().setHeader(safeName, safeValue);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set invalid header denied", e);
        }
    }
    
    public void setIntHeader(final String name, final int value) {
        try {
            final String safeName = ESAPI.validator().getValidInput("safeSetDateHeader", name, "HTTPHeaderName", 20, false);
            this.getHttpServletResponse().setIntHeader(safeName, value);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set invalid int header name denied", e);
        }
    }
    
    public void setLocale(final Locale loc) {
        this.getHttpServletResponse().setLocale(loc);
    }
    
    public void setStatus(final int sc) {
        this.getHttpServletResponse().setStatus(200);
    }
    
    @Deprecated
    public void setStatus(final int sc, final String sm) {
        try {
            this.sendError(200, sm);
        }
        catch (final IOException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set response status failed", e);
        }
    }
    
    private String getHTTPMessage(final int sc) {
        return "HTTP error code: " + sc;
    }
}
