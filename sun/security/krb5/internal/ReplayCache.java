package sun.security.krb5.internal;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.krb5.internal.rcache.DflCache;
import sun.security.krb5.internal.rcache.MemoryCache;

public abstract class ReplayCache
{
    public static ReplayCache getInstance(final String s) {
        if (s == null) {
            return new MemoryCache();
        }
        if (s.equals("dfl") || s.startsWith("dfl:")) {
            return new DflCache(s);
        }
        if (s.equals("none")) {
            return new ReplayCache() {
                @Override
                public void checkAndStore(final KerberosTime kerberosTime, final AuthTimeWithHash authTimeWithHash) throws KrbApErrException {
                }
            };
        }
        throw new IllegalArgumentException("Unknown type: " + s);
    }
    
    public static ReplayCache getInstance() {
        return getInstance(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.krb5.rcache")));
    }
    
    public abstract void checkAndStore(final KerberosTime p0, final AuthTimeWithHash p1) throws KrbApErrException;
}
