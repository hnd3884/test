package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Permission;
import java.security.Principal;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import java.security.AccessControlContext;

public final class Krb5Helper
{
    private static final String IMPL_CLASS = "org.openjsse.legacy8ujsse.sun.security.ssl.krb5.Krb5ProxyImpl";
    private static final Krb5Proxy proxy;
    
    private Krb5Helper() {
    }
    
    public static boolean isAvailable() {
        return Krb5Helper.proxy != null;
    }
    
    private static void ensureAvailable() {
        if (Krb5Helper.proxy == null) {
            throw new AssertionError((Object)"Kerberos should have been available");
        }
    }
    
    public static Subject getClientSubject(final AccessControlContext acc) throws LoginException {
        ensureAvailable();
        return Krb5Helper.proxy.getClientSubject(acc);
    }
    
    public static Subject getServerSubject(final AccessControlContext acc) throws LoginException {
        ensureAvailable();
        return Krb5Helper.proxy.getServerSubject(acc);
    }
    
    public static Object getServiceCreds(final AccessControlContext acc) throws LoginException {
        ensureAvailable();
        return Krb5Helper.proxy.getServiceCreds(acc);
    }
    
    public static String getServerPrincipalName(final Object serviceCreds) {
        ensureAvailable();
        return Krb5Helper.proxy.getServerPrincipalName(serviceCreds);
    }
    
    public static String getPrincipalHostName(final Principal principal) {
        ensureAvailable();
        return Krb5Helper.proxy.getPrincipalHostName(principal);
    }
    
    public static Permission getServicePermission(final String principalName, final String action) {
        ensureAvailable();
        return Krb5Helper.proxy.getServicePermission(principalName, action);
    }
    
    public static boolean isRelated(final Subject subject, final Principal princ) {
        ensureAvailable();
        return Krb5Helper.proxy.isRelated(subject, princ);
    }
    
    static {
        proxy = AccessController.doPrivileged((PrivilegedAction<Krb5Proxy>)new PrivilegedAction<Krb5Proxy>() {
            @Override
            public Krb5Proxy run() {
                try {
                    final Class<?> c = Class.forName("org.openjsse.legacy8ujsse.sun.security.ssl.krb5.Krb5ProxyImpl");
                    return (Krb5Proxy)c.newInstance();
                }
                catch (final ClassNotFoundException cnf) {
                    return null;
                }
                catch (final InstantiationException e) {
                    throw new AssertionError((Object)e);
                }
                catch (final IllegalAccessException e2) {
                    throw new AssertionError((Object)e2);
                }
            }
        });
    }
}
