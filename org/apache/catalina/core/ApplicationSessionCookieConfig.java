package org.apache.catalina.core;

import org.apache.catalina.util.SessionConfig;
import javax.servlet.http.Cookie;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.tomcat.util.res.StringManager;
import javax.servlet.SessionCookieConfig;

public class ApplicationSessionCookieConfig implements SessionCookieConfig
{
    private static final StringManager sm;
    private boolean httpOnly;
    private boolean secure;
    private int maxAge;
    private String comment;
    private String domain;
    private String name;
    private String path;
    private StandardContext context;
    
    public ApplicationSessionCookieConfig(final StandardContext context) {
        this.maxAge = -1;
        this.context = context;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public boolean isHttpOnly() {
        return this.httpOnly;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public void setComment(final String comment) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "comment", this.context.getPath() }));
        }
        this.comment = comment;
    }
    
    public void setDomain(final String domain) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "domain name", this.context.getPath() }));
        }
        this.domain = domain;
    }
    
    public void setHttpOnly(final boolean httpOnly) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "HttpOnly", this.context.getPath() }));
        }
        this.httpOnly = httpOnly;
    }
    
    public void setMaxAge(final int maxAge) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "max age", this.context.getPath() }));
        }
        this.maxAge = maxAge;
    }
    
    public void setName(final String name) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "name", this.context.getPath() }));
        }
        this.name = name;
    }
    
    public void setPath(final String path) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "path", this.context.getPath() }));
        }
        this.path = path;
    }
    
    public void setSecure(final boolean secure) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(ApplicationSessionCookieConfig.sm.getString("applicationSessionCookieConfig.ise", new Object[] { "secure", this.context.getPath() }));
        }
        this.secure = secure;
    }
    
    public static Cookie createSessionCookie(final Context context, final String sessionId, final boolean secure) {
        final SessionCookieConfig scc = context.getServletContext().getSessionCookieConfig();
        final Cookie cookie = new Cookie(SessionConfig.getSessionCookieName(context), sessionId);
        cookie.setMaxAge(scc.getMaxAge());
        cookie.setComment(scc.getComment());
        if (context.getSessionCookieDomain() == null) {
            if (scc.getDomain() != null) {
                cookie.setDomain(scc.getDomain());
            }
        }
        else {
            cookie.setDomain(context.getSessionCookieDomain());
        }
        if (scc.isSecure() || secure) {
            cookie.setSecure(true);
        }
        if (scc.isHttpOnly() || context.getUseHttpOnly()) {
            cookie.setHttpOnly(true);
        }
        cookie.setPath(SessionConfig.getSessionCookiePath(context));
        return cookie;
    }
    
    static {
        sm = StringManager.getManager((Class)ApplicationSessionCookieConfig.class);
    }
}
