package sun.net.www.protocol.http;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.net.www.HeaderParser;
import java.net.URL;
import java.util.HashMap;
import java.net.PasswordAuthentication;

public abstract class AuthenticationInfo extends AuthCacheValue implements Cloneable
{
    static final long serialVersionUID = -2588378268010453259L;
    public static final char SERVER_AUTHENTICATION = 's';
    public static final char PROXY_AUTHENTICATION = 'p';
    static final boolean serializeAuth;
    protected transient PasswordAuthentication pw;
    private static HashMap<String, Thread> requests;
    char type;
    AuthScheme authScheme;
    String protocol;
    String host;
    int port;
    String realm;
    String path;
    String s1;
    String s2;
    
    public PasswordAuthentication credentials() {
        return this.pw;
    }
    
    public Type getAuthType() {
        return (this.type == 's') ? Type.Server : Type.Proxy;
    }
    
    @Override
    AuthScheme getAuthScheme() {
        return this.authScheme;
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
    
    public String getPath() {
        return this.path;
    }
    
    public String getProtocolScheme() {
        return this.protocol;
    }
    
    protected boolean useAuthCache() {
        return true;
    }
    
    private static boolean requestIsInProgress(final String s) {
        if (!AuthenticationInfo.serializeAuth) {
            return false;
        }
        synchronized (AuthenticationInfo.requests) {
            final Thread currentThread = Thread.currentThread();
            final Thread thread;
            if ((thread = AuthenticationInfo.requests.get(s)) == null) {
                AuthenticationInfo.requests.put(s, currentThread);
                return false;
            }
            if (thread == currentThread) {
                return false;
            }
            while (AuthenticationInfo.requests.containsKey(s)) {
                try {
                    AuthenticationInfo.requests.wait();
                }
                catch (final InterruptedException ex) {}
            }
        }
        return true;
    }
    
    private static void requestCompleted(final String s) {
        synchronized (AuthenticationInfo.requests) {
            final Thread thread = AuthenticationInfo.requests.get(s);
            if (thread != null && thread == Thread.currentThread()) {
                final boolean b = AuthenticationInfo.requests.remove(s) != null;
                assert b;
            }
            AuthenticationInfo.requests.notifyAll();
        }
    }
    
    public AuthenticationInfo(final char type, final AuthScheme authScheme, final String s, final int port, final String realm) {
        this.type = type;
        this.authScheme = authScheme;
        this.protocol = "";
        this.host = s.toLowerCase();
        this.port = port;
        this.realm = realm;
        this.path = null;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public AuthenticationInfo(final char type, final AuthScheme authScheme, final URL url, final String realm) {
        this.type = type;
        this.authScheme = authScheme;
        this.protocol = url.getProtocol().toLowerCase();
        this.host = url.getHost().toLowerCase();
        this.port = url.getPort();
        if (this.port == -1) {
            this.port = url.getDefaultPort();
        }
        this.realm = realm;
        final String path = url.getPath();
        if (path.length() == 0) {
            this.path = path;
        }
        else {
            this.path = reducePath(path);
        }
    }
    
    static String reducePath(final String s) {
        final int lastIndex = s.lastIndexOf(47);
        final int lastIndex2 = s.lastIndexOf(46);
        if (lastIndex == -1) {
            return s;
        }
        if (lastIndex < lastIndex2) {
            return s.substring(0, lastIndex + 1);
        }
        return s;
    }
    
    static AuthenticationInfo getServerAuth(final URL url) {
        int n = url.getPort();
        if (n == -1) {
            n = url.getDefaultPort();
        }
        return getAuth("s:" + url.getProtocol().toLowerCase() + ":" + url.getHost().toLowerCase() + ":" + n, url);
    }
    
    static String getServerAuthKey(final URL url, final String s, final AuthScheme authScheme) {
        int n = url.getPort();
        if (n == -1) {
            n = url.getDefaultPort();
        }
        return "s:" + authScheme + ":" + url.getProtocol().toLowerCase() + ":" + url.getHost().toLowerCase() + ":" + n + ":" + s;
    }
    
    static AuthenticationInfo getServerAuth(final String s) {
        AuthenticationInfo authenticationInfo = getAuth(s, null);
        if (authenticationInfo == null && requestIsInProgress(s)) {
            authenticationInfo = getAuth(s, null);
        }
        return authenticationInfo;
    }
    
    static AuthenticationInfo getAuth(final String s, final URL url) {
        if (url == null) {
            return (AuthenticationInfo)AuthenticationInfo.cache.get(s, null);
        }
        return (AuthenticationInfo)AuthenticationInfo.cache.get(s, url.getPath());
    }
    
    static AuthenticationInfo getProxyAuth(final String s, final int n) {
        return (AuthenticationInfo)AuthenticationInfo.cache.get("p::" + s.toLowerCase() + ":" + n, null);
    }
    
    static String getProxyAuthKey(final String s, final int n, final String s2, final AuthScheme authScheme) {
        return "p:" + authScheme + "::" + s.toLowerCase() + ":" + n + ":" + s2;
    }
    
    static AuthenticationInfo getProxyAuth(final String s) {
        AuthenticationInfo authenticationInfo = (AuthenticationInfo)AuthenticationInfo.cache.get(s, null);
        if (authenticationInfo == null && requestIsInProgress(s)) {
            authenticationInfo = (AuthenticationInfo)AuthenticationInfo.cache.get(s, null);
        }
        return authenticationInfo;
    }
    
    void addToCache() {
        final String cacheKey = this.cacheKey(true);
        if (this.useAuthCache()) {
            AuthenticationInfo.cache.put(cacheKey, this);
            if (this.supportsPreemptiveAuthorization()) {
                AuthenticationInfo.cache.put(this.cacheKey(false), this);
            }
        }
        endAuthRequest(cacheKey);
    }
    
    static void endAuthRequest(final String s) {
        if (!AuthenticationInfo.serializeAuth) {
            return;
        }
        synchronized (AuthenticationInfo.requests) {
            requestCompleted(s);
        }
    }
    
    void removeFromCache() {
        AuthenticationInfo.cache.remove(this.cacheKey(true), this);
        if (this.supportsPreemptiveAuthorization()) {
            AuthenticationInfo.cache.remove(this.cacheKey(false), this);
        }
    }
    
    public abstract boolean supportsPreemptiveAuthorization();
    
    public String getHeaderName() {
        if (this.type == 's') {
            return "Authorization";
        }
        return "Proxy-authorization";
    }
    
    public abstract String getHeaderValue(final URL p0, final String p1);
    
    public abstract boolean setHeaders(final HttpURLConnection p0, final HeaderParser p1, final String p2);
    
    public abstract boolean isAuthorizationStale(final String p0);
    
    String cacheKey(final boolean b) {
        if (b) {
            return this.type + ":" + this.authScheme + ":" + this.protocol + ":" + this.host + ":" + this.port + ":" + this.realm;
        }
        return this.type + ":" + this.protocol + ":" + this.host + ":" + this.port;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.pw = new PasswordAuthentication(this.s1, this.s2.toCharArray());
        this.s1 = null;
        this.s2 = null;
    }
    
    private synchronized void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        this.s1 = this.pw.getUserName();
        this.s2 = new String(this.pw.getPassword());
        objectOutputStream.defaultWriteObject();
    }
    
    static {
        serializeAuth = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("http.auth.serializeRequests"));
        AuthenticationInfo.requests = new HashMap<String, Thread>();
    }
}
