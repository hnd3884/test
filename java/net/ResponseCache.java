package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.security.Permission;
import sun.security.util.SecurityConstants;

public abstract class ResponseCache
{
    private static ResponseCache theResponseCache;
    
    public static synchronized ResponseCache getDefault() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.GET_RESPONSECACHE_PERMISSION);
        }
        return ResponseCache.theResponseCache;
    }
    
    public static synchronized void setDefault(final ResponseCache theResponseCache) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.SET_RESPONSECACHE_PERMISSION);
        }
        ResponseCache.theResponseCache = theResponseCache;
    }
    
    public abstract CacheResponse get(final URI p0, final String p1, final Map<String, List<String>> p2) throws IOException;
    
    public abstract CacheRequest put(final URI p0, final URLConnection p1) throws IOException;
}
