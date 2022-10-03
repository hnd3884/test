package sun.net.www.protocol.http;

public interface AuthCache
{
    void put(final String p0, final AuthCacheValue p1);
    
    AuthCacheValue get(final String p0, final String p1);
    
    void remove(final String p0, final AuthCacheValue p1);
}
