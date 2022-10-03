package javax.crypto;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Enumeration;
import java.security.PermissionCollection;
import java.security.Permission;
import java.util.Locale;
import java.net.URL;
import java.util.concurrent.ConcurrentMap;
import java.util.Vector;

final class JceSecurityManager extends SecurityManager
{
    private static final CryptoPermissions defaultPolicy;
    private static final CryptoPermissions exemptPolicy;
    private static final CryptoAllPermission allPerm;
    private static final Vector<Class<?>> TrustedCallersCache;
    private static final ConcurrentMap<URL, CryptoPermissions> exemptCache;
    private static final CryptoPermissions CACHE_NULL_MARK;
    static final JceSecurityManager INSTANCE;
    
    private JceSecurityManager() {
    }
    
    CryptoPermission getCryptoPermission(String upperCase) {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        final CryptoPermission defaultPermission = this.getDefaultPermission(upperCase);
        if (defaultPermission == CryptoAllPermission.INSTANCE) {
            return defaultPermission;
        }
        final Class[] classContext = this.getClassContext();
        URL codeBase = null;
        int i;
        for (i = 0; i < classContext.length; ++i) {
            final Class clazz = classContext[i];
            codeBase = JceSecurity.getCodeBase(clazz);
            if (codeBase != null) {
                break;
            }
            if (!clazz.getName().startsWith("javax.crypto.")) {
                return defaultPermission;
            }
        }
        if (i == classContext.length) {
            return defaultPermission;
        }
        CryptoPermissions appPermissions = JceSecurityManager.exemptCache.get(codeBase);
        if (appPermissions == null) {
            synchronized (this.getClass()) {
                appPermissions = JceSecurityManager.exemptCache.get(codeBase);
                if (appPermissions == null) {
                    appPermissions = getAppPermissions(codeBase);
                    JceSecurityManager.exemptCache.putIfAbsent(codeBase, (appPermissions == null) ? JceSecurityManager.CACHE_NULL_MARK : appPermissions);
                }
            }
        }
        if (appPermissions == null || appPermissions == JceSecurityManager.CACHE_NULL_MARK) {
            return defaultPermission;
        }
        if (appPermissions.implies(JceSecurityManager.allPerm)) {
            return JceSecurityManager.allPerm;
        }
        final PermissionCollection permissionCollection = appPermissions.getPermissionCollection(upperCase);
        if (permissionCollection == null) {
            return defaultPermission;
        }
        final Enumeration<Permission> elements = permissionCollection.elements();
        while (elements.hasMoreElements()) {
            final CryptoPermission cryptoPermission = elements.nextElement();
            if (cryptoPermission.getExemptionMechanism() == null) {
                return cryptoPermission;
            }
        }
        final PermissionCollection permissionCollection2 = JceSecurityManager.exemptPolicy.getPermissionCollection(upperCase);
        if (permissionCollection2 == null) {
            return defaultPermission;
        }
        final Enumeration<Permission> elements2 = permissionCollection2.elements();
        while (elements2.hasMoreElements()) {
            final CryptoPermission cryptoPermission2 = elements2.nextElement();
            try {
                ExemptionMechanism.getInstance(cryptoPermission2.getExemptionMechanism());
                if (cryptoPermission2.getAlgorithm().equals("*")) {
                    CryptoPermission cryptoPermission3;
                    if (cryptoPermission2.getCheckParam()) {
                        cryptoPermission3 = new CryptoPermission(upperCase, cryptoPermission2.getMaxKeySize(), cryptoPermission2.getAlgorithmParameterSpec(), cryptoPermission2.getExemptionMechanism());
                    }
                    else {
                        cryptoPermission3 = new CryptoPermission(upperCase, cryptoPermission2.getMaxKeySize(), cryptoPermission2.getExemptionMechanism());
                    }
                    if (appPermissions.implies(cryptoPermission3)) {
                        return cryptoPermission3;
                    }
                }
                if (appPermissions.implies(cryptoPermission2)) {
                    return cryptoPermission2;
                }
                continue;
            }
            catch (final Exception ex) {}
        }
        return defaultPermission;
    }
    
    private static CryptoPermissions getAppPermissions(final URL url) {
        try {
            return JceSecurity.verifyExemptJar(url);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private CryptoPermission getDefaultPermission(final String s) {
        return JceSecurityManager.defaultPolicy.getPermissionCollection(s).elements().nextElement();
    }
    
    boolean isCallerTrusted() {
        final Class[] classContext = this.getClassContext();
        URL codeBase = null;
        int i;
        for (i = 0; i < classContext.length; ++i) {
            codeBase = JceSecurity.getCodeBase(classContext[i]);
            if (codeBase != null) {
                break;
            }
        }
        if (i == classContext.length) {
            return true;
        }
        if (JceSecurityManager.TrustedCallersCache.contains(classContext[i])) {
            return true;
        }
        try {
            JceSecurity.verifyProviderJar(codeBase);
        }
        catch (final Exception ex) {
            return false;
        }
        JceSecurityManager.TrustedCallersCache.addElement(classContext[i]);
        return true;
    }
    
    static {
        TrustedCallersCache = new Vector<Class<?>>(2);
        exemptCache = new ConcurrentHashMap<URL, CryptoPermissions>();
        CACHE_NULL_MARK = new CryptoPermissions();
        defaultPolicy = JceSecurity.getDefaultPolicy();
        exemptPolicy = JceSecurity.getExemptPolicy();
        allPerm = CryptoAllPermission.INSTANCE;
        INSTANCE = AccessController.doPrivileged((PrivilegedAction<JceSecurityManager>)new PrivilegedAction<JceSecurityManager>() {
            @Override
            public JceSecurityManager run() {
                return new JceSecurityManager(null);
            }
        });
    }
}
