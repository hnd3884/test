package sun.security.provider.certpath;

import java.util.HashMap;
import java.util.Collection;
import java.security.cert.X509CRLSelector;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509CertSelector;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertStore;
import java.net.URI;
import java.io.IOException;
import java.security.cert.CertStoreException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.NoSuchAlgorithmException;
import sun.security.util.Cache;
import java.util.Map;

public abstract class CertStoreHelper
{
    private static final int NUM_TYPES = 2;
    private static final Map<String, String> classMap;
    private static Cache<String, CertStoreHelper> cache;
    
    public static CertStoreHelper getInstance(final String s) throws NoSuchAlgorithmException {
        final CertStoreHelper certStoreHelper = CertStoreHelper.cache.get(s);
        if (certStoreHelper != null) {
            return certStoreHelper;
        }
        final String s2 = CertStoreHelper.classMap.get(s);
        if (s2 == null) {
            throw new NoSuchAlgorithmException(s + " not available");
        }
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<CertStoreHelper>)new PrivilegedExceptionAction<CertStoreHelper>() {
                @Override
                public CertStoreHelper run() throws ClassNotFoundException {
                    try {
                        final CertStoreHelper certStoreHelper = (CertStoreHelper)Class.forName(s2, true, null).newInstance();
                        CertStoreHelper.cache.put(s, certStoreHelper);
                        return certStoreHelper;
                    }
                    catch (final InstantiationException | IllegalAccessException ex) {
                        throw new AssertionError(ex);
                    }
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new NoSuchAlgorithmException(s + " not available", ex.getException());
        }
    }
    
    static boolean isCausedByNetworkIssue(final String s, final CertStoreException ex) {
        switch (s) {
            case "LDAP":
            case "SSLServer": {
                try {
                    return getInstance(s).isCausedByNetworkIssue(ex);
                }
                catch (final NoSuchAlgorithmException ex2) {
                    return false;
                }
            }
            case "URI": {
                final Throwable cause = ex.getCause();
                return cause != null && cause instanceof IOException;
            }
            default: {
                return false;
            }
        }
    }
    
    public abstract CertStore getCertStore(final URI p0) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public abstract X509CertSelector wrap(final X509CertSelector p0, final X500Principal p1, final String p2) throws IOException;
    
    public abstract X509CRLSelector wrap(final X509CRLSelector p0, final Collection<X500Principal> p1, final String p2) throws IOException;
    
    public abstract boolean isCausedByNetworkIssue(final CertStoreException p0);
    
    static {
        (classMap = new HashMap<String, String>(2)).put("LDAP", "sun.security.provider.certpath.ldap.LDAPCertStoreHelper");
        CertStoreHelper.classMap.put("SSLServer", "sun.security.provider.certpath.ssl.SSLServerCertStoreHelper");
        CertStoreHelper.cache = Cache.newSoftMemoryCache(2);
    }
}
