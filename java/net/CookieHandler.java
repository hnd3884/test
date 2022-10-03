package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.security.Permission;
import sun.security.util.SecurityConstants;

public abstract class CookieHandler
{
    private static CookieHandler cookieHandler;
    
    public static synchronized CookieHandler getDefault() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.GET_COOKIEHANDLER_PERMISSION);
        }
        return CookieHandler.cookieHandler;
    }
    
    public static synchronized void setDefault(final CookieHandler cookieHandler) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.SET_COOKIEHANDLER_PERMISSION);
        }
        CookieHandler.cookieHandler = cookieHandler;
    }
    
    public abstract Map<String, List<String>> get(final URI p0, final Map<String, List<String>> p1) throws IOException;
    
    public abstract void put(final URI p0, final Map<String, List<String>> p1) throws IOException;
}
