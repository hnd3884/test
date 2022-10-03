package org.apache.catalina.authenticator;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.SessionListener;
import java.security.Principal;
import org.apache.catalina.Realm;
import org.apache.catalina.Manager;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import java.util.Iterator;
import java.util.Set;
import org.apache.catalina.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.Cookie;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.apache.catalina.Engine;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.valves.ValveBase;

public class SingleSignOn extends ValveBase
{
    private static final StringManager sm;
    private Engine engine;
    protected Map<String, SingleSignOnEntry> cache;
    private boolean requireReauthentication;
    private String cookieDomain;
    private String cookieName;
    
    public SingleSignOn() {
        super(true);
        this.cache = new ConcurrentHashMap<String, SingleSignOnEntry>();
        this.requireReauthentication = false;
        this.cookieName = Constants.SINGLE_SIGN_ON_COOKIE;
    }
    
    public String getCookieDomain() {
        return this.cookieDomain;
    }
    
    public void setCookieDomain(final String cookieDomain) {
        if (cookieDomain != null && cookieDomain.trim().length() == 0) {
            this.cookieDomain = null;
        }
        else {
            this.cookieDomain = cookieDomain;
        }
    }
    
    public String getCookieName() {
        return this.cookieName;
    }
    
    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }
    
    public boolean getRequireReauthentication() {
        return this.requireReauthentication;
    }
    
    public void setRequireReauthentication(final boolean required) {
        this.requireReauthentication = required;
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        request.removeNote("org.apache.catalina.request.SSOID");
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.invoke", new Object[] { request.getRequestURI() }));
        }
        if (request.getUserPrincipal() != null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.hasPrincipal", new Object[] { request.getUserPrincipal().getName() }));
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.cookieCheck"));
        }
        Cookie cookie = null;
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie value : cookies) {
                if (this.cookieName.equals(value.getName())) {
                    cookie = value;
                    break;
                }
            }
        }
        if (cookie == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.cookieNotFound"));
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.principalCheck", new Object[] { cookie.getValue() }));
        }
        final SingleSignOnEntry entry = this.cache.get(cookie.getValue());
        if (entry != null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.principalFound", new Object[] { (entry.getPrincipal() != null) ? entry.getPrincipal().getName() : "", entry.getAuthType() }));
            }
            request.setNote("org.apache.catalina.request.SSOID", cookie.getValue());
            if (!this.getRequireReauthentication()) {
                request.setAuthType(entry.getAuthType());
                request.setUserPrincipal(entry.getPrincipal());
            }
        }
        else {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.principalNotFound", new Object[] { cookie.getValue() }));
            }
            cookie.setValue("REMOVE");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            final String domain = this.getCookieDomain();
            if (domain != null) {
                cookie.setDomain(domain);
            }
            cookie.setSecure(request.isSecure());
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            response.addCookie(cookie);
        }
        this.getNext().invoke(request, response);
    }
    
    public void sessionDestroyed(final String ssoId, final Session session) {
        if (!this.getState().isAvailable()) {
            return;
        }
        if ((session.getMaxInactiveInterval() > 0 && session.getIdleTimeInternal() >= session.getMaxInactiveInterval() * 1000) || !session.getManager().getContext().getState().isAvailable()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.sessionTimeout", new Object[] { ssoId, session }));
            }
            this.removeSession(ssoId, session);
        }
        else {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.sessionLogout", new Object[] { ssoId, session }));
            }
            this.removeSession(ssoId, session);
            if (this.cache.containsKey(ssoId)) {
                this.deregister(ssoId);
            }
        }
    }
    
    protected boolean associate(final String ssoId, final Session session) {
        final SingleSignOnEntry sso = this.cache.get(ssoId);
        if (sso == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.associateFail", new Object[] { ssoId, session }));
            }
            return false;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.associate", new Object[] { ssoId, session }));
        }
        sso.addSession(this, ssoId, session);
        return true;
    }
    
    protected void deregister(final String ssoId) {
        final SingleSignOnEntry sso = this.cache.remove(ssoId);
        if (sso == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.deregisterFail", new Object[] { ssoId }));
            }
            return;
        }
        final Set<SingleSignOnSessionKey> ssoKeys = sso.findSessions();
        if (ssoKeys.size() == 0 && this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.deregisterNone", new Object[] { ssoId }));
        }
        for (final SingleSignOnSessionKey ssoKey : ssoKeys) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.deregister", new Object[] { ssoKey, ssoId }));
            }
            this.expire(ssoKey);
        }
    }
    
    private void expire(final SingleSignOnSessionKey key) {
        if (this.engine == null) {
            this.containerLog.warn((Object)SingleSignOn.sm.getString("singleSignOn.sessionExpire.engineNull", new Object[] { key }));
            return;
        }
        final Container host = this.engine.findChild(key.getHostName());
        if (host == null) {
            this.containerLog.warn((Object)SingleSignOn.sm.getString("singleSignOn.sessionExpire.hostNotFound", new Object[] { key }));
            return;
        }
        final Context context = (Context)host.findChild(key.getContextName());
        if (context == null) {
            this.containerLog.warn((Object)SingleSignOn.sm.getString("singleSignOn.sessionExpire.contextNotFound", new Object[] { key }));
            return;
        }
        final Manager manager = context.getManager();
        if (manager == null) {
            this.containerLog.warn((Object)SingleSignOn.sm.getString("singleSignOn.sessionExpire.managerNotFound", new Object[] { key }));
            return;
        }
        Session session = null;
        try {
            session = manager.findSession(key.getSessionId());
        }
        catch (final IOException e) {
            this.containerLog.warn((Object)SingleSignOn.sm.getString("singleSignOn.sessionExpire.managerError", new Object[] { key }), (Throwable)e);
            return;
        }
        if (session == null) {
            this.containerLog.warn((Object)SingleSignOn.sm.getString("singleSignOn.sessionExpire.sessionNotFound", new Object[] { key }));
            return;
        }
        session.expire();
    }
    
    protected boolean reauthenticate(final String ssoId, final Realm realm, final Request request) {
        if (ssoId == null || realm == null) {
            return false;
        }
        boolean reauthenticated = false;
        final SingleSignOnEntry entry = this.cache.get(ssoId);
        if (entry != null && entry.getCanReauthenticate()) {
            final String username = entry.getUsername();
            if (username != null) {
                final Principal reauthPrincipal = realm.authenticate(username, entry.getPassword());
                if (reauthPrincipal != null) {
                    reauthenticated = true;
                    request.setAuthType(entry.getAuthType());
                    request.setUserPrincipal(reauthPrincipal);
                }
            }
        }
        return reauthenticated;
    }
    
    protected void register(final String ssoId, final Principal principal, final String authType, final String username, final String password) {
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.register", new Object[] { ssoId, (principal != null) ? principal.getName() : "", authType }));
        }
        this.cache.put(ssoId, new SingleSignOnEntry(principal, authType, username, password));
    }
    
    protected boolean update(final String ssoId, final Principal principal, final String authType, final String username, final String password) {
        final SingleSignOnEntry sso = this.cache.get(ssoId);
        if (sso != null && !sso.getCanReauthenticate()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.update", new Object[] { ssoId, authType }));
            }
            sso.updateCredentials(principal, authType, username, password);
            return true;
        }
        return false;
    }
    
    protected void removeSession(final String ssoId, final Session session) {
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)SingleSignOn.sm.getString("singleSignOn.debug.removeSession", new Object[] { session, ssoId }));
        }
        final SingleSignOnEntry entry = this.cache.get(ssoId);
        if (entry == null) {
            return;
        }
        entry.removeSession(session);
        if (entry.findSessions().size() == 0) {
            this.deregister(ssoId);
        }
    }
    
    protected SessionListener getSessionListener(final String ssoId) {
        return new SingleSignOnListener(ssoId);
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        Container c;
        for (c = this.getContainer(); c != null && !(c instanceof Engine); c = c.getParent()) {}
        if (c != null) {
            this.engine = (Engine)c;
        }
        super.startInternal();
    }
    
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.engine = null;
    }
    
    static {
        sm = StringManager.getManager((Class)SingleSignOn.class);
    }
}
