package org.owasp.esapi.waf.internal;

import javax.servlet.http.Cookie;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Writer;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.owasp.esapi.waf.rules.Rule;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.rules.AddHTTPOnlyFlagRule;
import org.owasp.esapi.waf.rules.AddSecureFlagRule;
import java.util.List;
import javax.servlet.http.HttpServletResponseWrapper;

public class InterceptingHTTPServletResponse extends HttpServletResponseWrapper
{
    private InterceptingPrintWriter ipw;
    private InterceptingServletOutputStream isos;
    private String contentType;
    private List<AddSecureFlagRule> addSecureFlagRules;
    private List<AddHTTPOnlyFlagRule> addHTTPOnlyFlagRules;
    private boolean alreadyCalledWriter;
    private boolean alreadyCalledOutputStream;
    
    public InterceptingHTTPServletResponse(final HttpServletResponse response, final boolean buffering, final List<Rule> cookieRules) throws IOException {
        super(response);
        this.addSecureFlagRules = null;
        this.addHTTPOnlyFlagRules = null;
        this.alreadyCalledWriter = false;
        this.alreadyCalledOutputStream = false;
        this.contentType = response.getContentType();
        this.isos = new InterceptingServletOutputStream(response.getOutputStream(), buffering);
        this.ipw = new InterceptingPrintWriter(new PrintWriter((OutputStream)this.isos));
        this.addSecureFlagRules = new ArrayList<AddSecureFlagRule>();
        this.addHTTPOnlyFlagRules = new ArrayList<AddHTTPOnlyFlagRule>();
        for (int i = 0; i < cookieRules.size(); ++i) {
            final Rule r = cookieRules.get(i);
            if (r instanceof AddSecureFlagRule) {
                this.addSecureFlagRules.add((AddSecureFlagRule)r);
            }
            else if (r instanceof AddHTTPOnlyFlagRule) {
                this.addHTTPOnlyFlagRules.add((AddHTTPOnlyFlagRule)r);
            }
        }
    }
    
    public boolean isUsingWriter() {
        return this.alreadyCalledWriter;
    }
    
    public InterceptingServletOutputStream getInterceptingServletOutputStream() {
        return this.isos;
    }
    
    public ServletOutputStream getOutputStream() throws IllegalStateException, IOException {
        if (this.alreadyCalledWriter) {
            throw new IllegalStateException();
        }
        this.alreadyCalledOutputStream = true;
        return this.isos;
    }
    
    public PrintWriter getWriter() throws IOException {
        if (this.alreadyCalledOutputStream) {
            throw new IllegalStateException();
        }
        this.alreadyCalledWriter = true;
        return this.ipw;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setContentType(final String s) {
        this.contentType = s;
    }
    
    public void flush() {
        this.ipw.flush();
    }
    
    public void commit() throws IOException {
        if (this.alreadyCalledWriter) {
            this.ipw.flush();
        }
        this.isos.commit();
    }
    
    public void addCookie(final Cookie cookie) {
        this.addCookie(cookie, cookie.getMaxAge() <= 0);
    }
    
    public void addCookie(final Cookie cookie, final boolean isSession) {
        boolean addSecureFlag = cookie.getSecure();
        boolean addHTTPOnlyFlag = false;
        if (!cookie.getSecure() && this.addSecureFlagRules != null) {
            for (int i = 0; i < this.addSecureFlagRules.size(); ++i) {
                final AddSecureFlagRule asfr = this.addSecureFlagRules.get(i);
                if (asfr.doesCookieMatch(cookie.getName())) {
                    addSecureFlag = true;
                }
            }
        }
        if (this.addHTTPOnlyFlagRules != null) {
            for (int i = 0; i < this.addHTTPOnlyFlagRules.size(); ++i) {
                final AddHTTPOnlyFlagRule ashr = this.addHTTPOnlyFlagRules.get(i);
                if (ashr.doesCookieMatch(cookie.getName())) {
                    addHTTPOnlyFlag = true;
                }
            }
        }
        final String cookieValue = this.createCookieHeader(cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getDomain(), cookie.getPath(), addSecureFlag, addHTTPOnlyFlag, isSession);
        this.addHeader("Set-Cookie", cookieValue);
    }
    
    private String createCookieHeader(final String name, final String value, final int maxAge, final String domain, final String path, final boolean secure, final boolean httpOnly, final boolean isTemporary) {
        String header = name + "=" + value;
        if (!isTemporary) {
            header = header + "; Max-Age=" + maxAge;
        }
        if (domain != null) {
            header = header + "; Domain=" + domain;
        }
        if (path != null) {
            header = header + "; Path=" + path;
        }
        if (secure) {
            header += "; Secure";
        }
        if (httpOnly) {
            header += "; HttpOnly";
        }
        return header;
    }
}
