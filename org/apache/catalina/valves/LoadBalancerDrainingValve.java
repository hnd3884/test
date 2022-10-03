package org.apache.catalina.valves;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;

public class LoadBalancerDrainingValve extends ValveBase
{
    public static final String ATTRIBUTE_KEY_JK_LB_ACTIVATION = "JK_LB_ACTIVATION";
    private int _redirectStatusCode;
    private String _ignoreCookieName;
    private String _ignoreCookieValue;
    
    public LoadBalancerDrainingValve() {
        super(true);
        this._redirectStatusCode = 307;
    }
    
    public void setRedirectStatusCode(final int code) {
        this._redirectStatusCode = code;
    }
    
    public String getIgnoreCookieName() {
        return this._ignoreCookieName;
    }
    
    public void setIgnoreCookieName(final String cookieName) {
        this._ignoreCookieName = cookieName;
    }
    
    public String getIgnoreCookieValue() {
        return this._ignoreCookieValue;
    }
    
    public void setIgnoreCookieValue(final String cookieValue) {
        this._ignoreCookieValue = cookieValue;
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        if ("DIS".equals(request.getAttribute("JK_LB_ACTIVATION")) && !request.isRequestedSessionIdValid()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"Load-balancer is in DISABLED state; draining this node");
            }
            boolean ignoreRebalance = false;
            Cookie sessionCookie = null;
            final Cookie[] cookies = request.getCookies();
            final String sessionCookieName = SessionConfig.getSessionCookieName(request.getContext());
            if (null != cookies) {
                for (final Cookie cookie : cookies) {
                    final String cookieName = cookie.getName();
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace((Object)("Checking cookie " + cookieName + "=" + cookie.getValue()));
                    }
                    if (sessionCookieName.equals(cookieName) && request.getRequestedSessionId().equals(cookie.getValue())) {
                        sessionCookie = cookie;
                    }
                    else if (null != this._ignoreCookieName && this._ignoreCookieName.equals(cookieName) && null != this._ignoreCookieValue && this._ignoreCookieValue.equals(cookie.getValue())) {
                        ignoreRebalance = true;
                    }
                }
            }
            if (ignoreRebalance) {
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug((Object)("Client is presenting a valid " + this._ignoreCookieName + " cookie, re-balancing is being skipped"));
                }
                this.getNext().invoke(request, response);
                return;
            }
            if (null != sessionCookie) {
                sessionCookie.setPath(SessionConfig.getSessionCookiePath(request.getContext()));
                sessionCookie.setMaxAge(0);
                sessionCookie.setValue("");
                final SessionCookieConfig sessionCookieConfig = request.getContext().getServletContext().getSessionCookieConfig();
                sessionCookie.setSecure(request.isSecure() || sessionCookieConfig.isSecure());
                response.addCookie(sessionCookie);
            }
            String uri = request.getRequestURI();
            final String sessionURIParamName = SessionConfig.getSessionUriParamName(request.getContext());
            if (uri.contains(";" + sessionURIParamName + "=")) {
                uri = uri.replaceFirst(";" + sessionURIParamName + "=[^&?]*", "");
            }
            final String queryString = request.getQueryString();
            if (null != queryString) {
                uri = uri + "?" + queryString;
            }
            response.setHeader("Location", uri);
            response.setStatus(this._redirectStatusCode);
        }
        else {
            this.getNext().invoke(request, response);
        }
    }
}
