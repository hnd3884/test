package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.auth.AuthScope;
import java.util.Date;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpState
{
    protected HashMap credMap;
    protected HashMap proxyCred;
    protected ArrayList cookies;
    private boolean preemptive;
    private int cookiePolicy;
    public static final String PREEMPTIVE_PROPERTY = "httpclient.authentication.preemptive";
    public static final String PREEMPTIVE_DEFAULT = "false";
    private static final Log LOG;
    
    public HttpState() {
        this.credMap = new HashMap();
        this.proxyCred = new HashMap();
        this.cookies = new ArrayList();
        this.preemptive = false;
        this.cookiePolicy = -1;
    }
    
    public synchronized void addCookie(final Cookie cookie) {
        HttpState.LOG.trace((Object)"enter HttpState.addCookie(Cookie)");
        if (cookie != null) {
            final Iterator it = this.cookies.iterator();
            while (it.hasNext()) {
                final Cookie tmp = it.next();
                if (cookie.equals(tmp)) {
                    it.remove();
                    break;
                }
            }
            if (!cookie.isExpired()) {
                this.cookies.add(cookie);
            }
        }
    }
    
    public synchronized void addCookies(final Cookie[] cookies) {
        HttpState.LOG.trace((Object)"enter HttpState.addCookies(Cookie[])");
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                this.addCookie(cookies[i]);
            }
        }
    }
    
    public synchronized Cookie[] getCookies() {
        HttpState.LOG.trace((Object)"enter HttpState.getCookies()");
        return this.cookies.toArray(new Cookie[this.cookies.size()]);
    }
    
    public synchronized Cookie[] getCookies(final String domain, final int port, final String path, final boolean secure) {
        HttpState.LOG.trace((Object)"enter HttpState.getCookies(String, int, String, boolean)");
        final CookieSpec matcher = CookiePolicy.getDefaultSpec();
        final ArrayList list = new ArrayList(this.cookies.size());
        for (int i = 0, m = this.cookies.size(); i < m; ++i) {
            final Cookie cookie = this.cookies.get(i);
            if (matcher.match(domain, port, path, secure, cookie)) {
                list.add(cookie);
            }
        }
        return list.toArray(new Cookie[list.size()]);
    }
    
    public synchronized boolean purgeExpiredCookies() {
        HttpState.LOG.trace((Object)"enter HttpState.purgeExpiredCookies()");
        return this.purgeExpiredCookies(new Date());
    }
    
    public synchronized boolean purgeExpiredCookies(final Date date) {
        HttpState.LOG.trace((Object)"enter HttpState.purgeExpiredCookies(Date)");
        boolean removed = false;
        final Iterator it = this.cookies.iterator();
        while (it.hasNext()) {
            if (it.next().isExpired(date)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }
    
    public int getCookiePolicy() {
        return this.cookiePolicy;
    }
    
    public void setAuthenticationPreemptive(final boolean value) {
        this.preemptive = value;
    }
    
    public boolean isAuthenticationPreemptive() {
        return this.preemptive;
    }
    
    public void setCookiePolicy(final int policy) {
        this.cookiePolicy = policy;
    }
    
    public synchronized void setCredentials(final String realm, final String host, final Credentials credentials) {
        HttpState.LOG.trace((Object)"enter HttpState.setCredentials(String, String, Credentials)");
        this.credMap.put(new AuthScope(host, -1, realm, AuthScope.ANY_SCHEME), credentials);
    }
    
    public synchronized void setCredentials(final AuthScope authscope, final Credentials credentials) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        HttpState.LOG.trace((Object)"enter HttpState.setCredentials(AuthScope, Credentials)");
        this.credMap.put(authscope, credentials);
    }
    
    private static Credentials matchCredentials(final HashMap map, final AuthScope authscope) {
        Credentials creds = map.get(authscope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            final Iterator items = map.keySet().iterator();
            while (items.hasNext()) {
                final AuthScope current = items.next();
                final int factor = authscope.match(current);
                if (factor > bestMatchFactor) {
                    bestMatchFactor = factor;
                    bestMatch = current;
                }
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }
    
    public synchronized Credentials getCredentials(final String realm, final String host) {
        HttpState.LOG.trace((Object)"enter HttpState.getCredentials(String, String");
        return matchCredentials(this.credMap, new AuthScope(host, -1, realm, AuthScope.ANY_SCHEME));
    }
    
    public synchronized Credentials getCredentials(final AuthScope authscope) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        HttpState.LOG.trace((Object)"enter HttpState.getCredentials(AuthScope)");
        return matchCredentials(this.credMap, authscope);
    }
    
    public synchronized void setProxyCredentials(final String realm, final String proxyHost, final Credentials credentials) {
        HttpState.LOG.trace((Object)"enter HttpState.setProxyCredentials(String, String, Credentials");
        this.proxyCred.put(new AuthScope(proxyHost, -1, realm, AuthScope.ANY_SCHEME), credentials);
    }
    
    public synchronized void setProxyCredentials(final AuthScope authscope, final Credentials credentials) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        HttpState.LOG.trace((Object)"enter HttpState.setProxyCredentials(AuthScope, Credentials)");
        this.proxyCred.put(authscope, credentials);
    }
    
    public synchronized Credentials getProxyCredentials(final String realm, final String proxyHost) {
        HttpState.LOG.trace((Object)"enter HttpState.getCredentials(String, String");
        return matchCredentials(this.proxyCred, new AuthScope(proxyHost, -1, realm, AuthScope.ANY_SCHEME));
    }
    
    public synchronized Credentials getProxyCredentials(final AuthScope authscope) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        HttpState.LOG.trace((Object)"enter HttpState.getProxyCredentials(AuthScope)");
        return matchCredentials(this.proxyCred, authscope);
    }
    
    public synchronized String toString() {
        final StringBuffer sbResult = new StringBuffer();
        sbResult.append("[");
        sbResult.append(getCredentialsStringRepresentation(this.proxyCred));
        sbResult.append(" | ");
        sbResult.append(getCredentialsStringRepresentation(this.credMap));
        sbResult.append(" | ");
        sbResult.append(getCookiesStringRepresentation(this.cookies));
        sbResult.append("]");
        final String strResult = sbResult.toString();
        return strResult;
    }
    
    private static String getCredentialsStringRepresentation(final Map credMap) {
        final StringBuffer sbResult = new StringBuffer();
        final Iterator iter = credMap.keySet().iterator();
        while (iter.hasNext()) {
            final Object key = iter.next();
            final Credentials cred = credMap.get(key);
            if (sbResult.length() > 0) {
                sbResult.append(", ");
            }
            sbResult.append(key);
            sbResult.append("#");
            sbResult.append(cred.toString());
        }
        return sbResult.toString();
    }
    
    private static String getCookiesStringRepresentation(final List cookies) {
        final StringBuffer sbResult = new StringBuffer();
        final Iterator iter = cookies.iterator();
        while (iter.hasNext()) {
            final Cookie ck = iter.next();
            if (sbResult.length() > 0) {
                sbResult.append("#");
            }
            sbResult.append(ck.toExternalForm());
        }
        return sbResult.toString();
    }
    
    public void clearCredentials() {
        this.credMap.clear();
    }
    
    public void clearProxyCredentials() {
        this.proxyCred.clear();
    }
    
    public synchronized void clearCookies() {
        this.cookies.clear();
    }
    
    public void clear() {
        this.clearCookies();
        this.clearCredentials();
        this.clearProxyCredentials();
    }
    
    static {
        LOG = LogFactory.getLog(HttpState.class);
    }
}
