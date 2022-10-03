package sun.net.www.protocol.http;

import java.net.PasswordAuthentication;
import java.io.Serializable;

public abstract class AuthCacheValue implements Serializable
{
    static final long serialVersionUID = 735249334068211611L;
    protected static AuthCache cache;
    
    public static void setAuthCache(final AuthCache cache) {
        AuthCacheValue.cache = cache;
    }
    
    AuthCacheValue() {
    }
    
    abstract Type getAuthType();
    
    abstract AuthScheme getAuthScheme();
    
    abstract String getHost();
    
    abstract int getPort();
    
    abstract String getRealm();
    
    abstract String getPath();
    
    abstract String getProtocolScheme();
    
    abstract PasswordAuthentication credentials();
    
    static {
        AuthCacheValue.cache = new AuthCacheImpl();
    }
    
    public enum Type
    {
        Proxy, 
        Server;
    }
}
