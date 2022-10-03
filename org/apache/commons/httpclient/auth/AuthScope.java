package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.util.LangUtils;

public class AuthScope
{
    public static final String ANY_HOST;
    public static final int ANY_PORT = -1;
    public static final String ANY_REALM;
    public static final String ANY_SCHEME;
    public static final AuthScope ANY;
    private String scheme;
    private String realm;
    private String host;
    private int port;
    
    public AuthScope(final String host, final int port, final String realm, final String scheme) {
        this.scheme = null;
        this.realm = null;
        this.host = null;
        this.port = -1;
        this.host = ((host == null) ? AuthScope.ANY_HOST : host.toLowerCase());
        this.port = ((port < 0) ? -1 : port);
        this.realm = ((realm == null) ? AuthScope.ANY_REALM : realm);
        this.scheme = ((scheme == null) ? AuthScope.ANY_SCHEME : scheme.toUpperCase());
    }
    
    public AuthScope(final String host, final int port, final String realm) {
        this(host, port, realm, AuthScope.ANY_SCHEME);
    }
    
    public AuthScope(final String host, final int port) {
        this(host, port, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
    }
    
    public AuthScope(final AuthScope authscope) {
        this.scheme = null;
        this.realm = null;
        this.host = null;
        this.port = -1;
        if (authscope == null) {
            throw new IllegalArgumentException("Scope may not be null");
        }
        this.host = authscope.getHost();
        this.port = authscope.getPort();
        this.realm = authscope.getRealm();
        this.scheme = authscope.getScheme();
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    private static boolean paramsEqual(final String p1, final String p2) {
        if (p1 == null) {
            return p1 == p2;
        }
        return p1.equals(p2);
    }
    
    private static boolean paramsEqual(final int p1, final int p2) {
        return p1 == p2;
    }
    
    public int match(final AuthScope that) {
        int factor = 0;
        if (paramsEqual(this.scheme, that.scheme)) {
            ++factor;
        }
        else if (this.scheme != AuthScope.ANY_SCHEME && that.scheme != AuthScope.ANY_SCHEME) {
            return -1;
        }
        if (paramsEqual(this.realm, that.realm)) {
            factor += 2;
        }
        else if (this.realm != AuthScope.ANY_REALM && that.realm != AuthScope.ANY_REALM) {
            return -1;
        }
        if (paramsEqual(this.port, that.port)) {
            factor += 4;
        }
        else if (this.port != -1 && that.port != -1) {
            return -1;
        }
        if (paramsEqual(this.host, that.host)) {
            factor += 8;
        }
        else if (this.host != AuthScope.ANY_HOST && that.host != AuthScope.ANY_HOST) {
            return -1;
        }
        return factor;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthScope)) {
            return super.equals(o);
        }
        final AuthScope that = (AuthScope)o;
        return paramsEqual(this.host, that.host) && paramsEqual(this.port, that.port) && paramsEqual(this.realm, that.realm) && paramsEqual(this.scheme, that.scheme);
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        if (this.scheme != null) {
            buffer.append(this.scheme.toUpperCase());
            buffer.append(' ');
        }
        if (this.realm != null) {
            buffer.append('\'');
            buffer.append(this.realm);
            buffer.append('\'');
        }
        else {
            buffer.append("<any realm>");
        }
        if (this.host != null) {
            buffer.append('@');
            buffer.append(this.host);
            if (this.port >= 0) {
                buffer.append(':');
                buffer.append(this.port);
            }
        }
        return buffer.toString();
    }
    
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.port);
        hash = LangUtils.hashCode(hash, this.realm);
        hash = LangUtils.hashCode(hash, this.scheme);
        return hash;
    }
    
    static {
        ANY_HOST = null;
        ANY_REALM = null;
        ANY_SCHEME = null;
        ANY = new AuthScope(AuthScope.ANY_HOST, -1, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
    }
}
