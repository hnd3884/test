package sun.net.www.protocol.http;

import sun.util.logging.PlatformLogger;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class NTLMAuthenticationProxy
{
    private static Method supportsTA;
    private static Method isTrustedSite;
    private static final String clazzStr = "sun.net.www.protocol.http.ntlm.NTLMAuthentication";
    private static final String supportsTAStr = "supportsTransparentAuth";
    private static final String isTrustedSiteStr = "isTrustedSite";
    static final NTLMAuthenticationProxy proxy;
    static final boolean supported;
    static final boolean supportsTransparentAuth;
    private final Constructor<? extends AuthenticationInfo> threeArgCtr;
    private final Constructor<? extends AuthenticationInfo> fiveArgCtr;
    
    private NTLMAuthenticationProxy(final Constructor<? extends AuthenticationInfo> threeArgCtr, final Constructor<? extends AuthenticationInfo> fiveArgCtr) {
        this.threeArgCtr = threeArgCtr;
        this.fiveArgCtr = fiveArgCtr;
    }
    
    AuthenticationInfo create(final boolean b, final URL url, final PasswordAuthentication passwordAuthentication) {
        try {
            return (AuthenticationInfo)this.threeArgCtr.newInstance(b, url, passwordAuthentication);
        }
        catch (final ReflectiveOperationException ex) {
            finest(ex);
            return null;
        }
    }
    
    AuthenticationInfo create(final boolean b, final String s, final int n, final PasswordAuthentication passwordAuthentication) {
        try {
            return (AuthenticationInfo)this.fiveArgCtr.newInstance(b, s, n, passwordAuthentication);
        }
        catch (final ReflectiveOperationException ex) {
            finest(ex);
            return null;
        }
    }
    
    private static boolean supportsTransparentAuth() {
        try {
            return (boolean)NTLMAuthenticationProxy.supportsTA.invoke(null, new Object[0]);
        }
        catch (final ReflectiveOperationException ex) {
            finest(ex);
            return false;
        }
    }
    
    public static boolean isTrustedSite(final URL url) {
        try {
            return (boolean)NTLMAuthenticationProxy.isTrustedSite.invoke(null, url);
        }
        catch (final ReflectiveOperationException ex) {
            finest(ex);
            return false;
        }
    }
    
    private static NTLMAuthenticationProxy tryLoadNTLMAuthentication() {
        try {
            final Class<?> forName = Class.forName("sun.net.www.protocol.http.ntlm.NTLMAuthentication", true, null);
            if (forName != null) {
                final Constructor constructor = forName.getConstructor(Boolean.TYPE, URL.class, PasswordAuthentication.class);
                final Constructor constructor2 = forName.getConstructor(Boolean.TYPE, String.class, Integer.TYPE, PasswordAuthentication.class);
                NTLMAuthenticationProxy.supportsTA = forName.getDeclaredMethod("supportsTransparentAuth", (Class[])new Class[0]);
                NTLMAuthenticationProxy.isTrustedSite = forName.getDeclaredMethod("isTrustedSite", URL.class);
                return new NTLMAuthenticationProxy(constructor, constructor2);
            }
        }
        catch (final ClassNotFoundException ex) {
            finest(ex);
        }
        catch (final ReflectiveOperationException ex2) {
            throw new AssertionError((Object)ex2);
        }
        return null;
    }
    
    static void finest(final Exception ex) {
        final PlatformLogger httpLogger = HttpURLConnection.getHttpLogger();
        if (httpLogger.isLoggable(PlatformLogger.Level.FINEST)) {
            httpLogger.finest("NTLMAuthenticationProxy: " + ex);
        }
    }
    
    static {
        proxy = tryLoadNTLMAuthentication();
        supported = (NTLMAuthenticationProxy.proxy != null);
        supportsTransparentAuth = (NTLMAuthenticationProxy.supported && supportsTransparentAuth());
    }
}
