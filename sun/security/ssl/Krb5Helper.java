package sun.security.ssl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Permission;
import java.security.Principal;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import java.security.AccessControlContext;

public final class Krb5Helper
{
    private static final String IMPL_CLASS = "sun.security.ssl.krb5.Krb5ProxyImpl";
    private static final Krb5Proxy proxy;
    
    private Krb5Helper() {
    }
    
    private static void ensureAvailable() {
        if (Krb5Helper.proxy == null) {
            throw new AssertionError((Object)"Kerberos should be available");
        }
    }
    
    public static Subject getClientSubject(final AccessControlContext accessControlContext) throws LoginException {
        ensureAvailable();
        return Krb5Helper.proxy.getClientSubject(accessControlContext);
    }
    
    public static Subject getServerSubject(final AccessControlContext accessControlContext) throws LoginException {
        ensureAvailable();
        return Krb5Helper.proxy.getServerSubject(accessControlContext);
    }
    
    public static Object getServiceCreds(final AccessControlContext accessControlContext) throws LoginException {
        ensureAvailable();
        return Krb5Helper.proxy.getServiceCreds(accessControlContext);
    }
    
    public static String getServerPrincipalName(final Object o) {
        ensureAvailable();
        return Krb5Helper.proxy.getServerPrincipalName(o);
    }
    
    public static String getPrincipalHostName(final Principal principal) {
        ensureAvailable();
        return Krb5Helper.proxy.getPrincipalHostName(principal);
    }
    
    public static Permission getServicePermission(final String s, final String s2) {
        ensureAvailable();
        return Krb5Helper.proxy.getServicePermission(s, s2);
    }
    
    public static boolean isRelated(final Subject subject, final Principal principal) {
        ensureAvailable();
        return Krb5Helper.proxy.isRelated(subject, principal);
    }
    
    static {
        proxy = AccessController.doPrivileged((PrivilegedAction<Krb5Proxy>)new PrivilegedAction<Krb5Proxy>() {
            @Override
            public Krb5Proxy run() {
                try {
                    return (Krb5Proxy)Class.forName("sun.security.ssl.krb5.Krb5ProxyImpl", true, null).newInstance();
                }
                catch (final ClassNotFoundException ex) {
                    return null;
                }
                catch (final InstantiationException ex2) {
                    throw new AssertionError((Object)ex2);
                }
                catch (final IllegalAccessException ex3) {
                    throw new AssertionError((Object)ex3);
                }
            }
        });
    }
}
