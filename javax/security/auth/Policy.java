package javax.security.auth;

import java.security.PermissionCollection;
import java.security.CodeSource;
import sun.security.util.Debug;
import sun.security.util.ResourcesMgr;
import java.util.Objects;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.Permission;
import java.security.AccessController;
import java.security.AccessControlContext;

@Deprecated
public abstract class Policy
{
    private static Policy policy;
    private static final String AUTH_POLICY = "sun.security.provider.AuthPolicyFile";
    private final AccessControlContext acc;
    private static boolean isCustomPolicy;
    
    protected Policy() {
        this.acc = AccessController.getContext();
    }
    
    public static Policy getPolicy() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("getPolicy"));
        }
        return getPolicyNoCheck();
    }
    
    static Policy getPolicyNoCheck() {
        if (Policy.policy == null) {
            synchronized (Policy.class) {
                if (Policy.policy == null) {
                    String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                        @Override
                        public String run() {
                            return Security.getProperty("auth.policy.provider");
                        }
                    });
                    if (s == null) {
                        s = "sun.security.provider.AuthPolicyFile";
                    }
                    try {
                        final String s2 = s;
                        final Policy policy = AccessController.doPrivileged((PrivilegedExceptionAction<Policy>)new PrivilegedExceptionAction<Policy>() {
                            @Override
                            public Policy run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                                return (Policy)Class.forName(s2, false, Thread.currentThread().getContextClassLoader()).asSubclass(Policy.class).newInstance();
                            }
                        });
                        AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                            @Override
                            public Void run() {
                                Policy.setPolicy(policy);
                                Policy.isCustomPolicy = !s2.equals("sun.security.provider.AuthPolicyFile");
                                return null;
                            }
                        }, Objects.requireNonNull(policy.acc));
                    }
                    catch (final Exception ex) {
                        throw new SecurityException(ResourcesMgr.getString("unable.to.instantiate.Subject.based.policy"));
                    }
                }
            }
        }
        return Policy.policy;
    }
    
    public static void setPolicy(final Policy policy) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("setPolicy"));
        }
        Policy.policy = policy;
        Policy.isCustomPolicy = (policy != null);
    }
    
    static boolean isCustomPolicySet(final Debug debug) {
        if (Policy.policy != null) {
            if (debug != null && Policy.isCustomPolicy) {
                debug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + Policy.policy.toString());
            }
            return Policy.isCustomPolicy;
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("auth.policy.provider");
            }
        });
        if (s != null && !s.equals("sun.security.provider.AuthPolicyFile")) {
            if (debug != null) {
                debug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + s);
            }
            return true;
        }
        return false;
    }
    
    public abstract PermissionCollection getPermissions(final Subject p0, final CodeSource p1);
    
    public abstract void refresh();
}
