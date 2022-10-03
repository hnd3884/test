package sun.security.krb5.internal.rcache;

import sun.security.krb5.internal.Krb5;
import java.util.Iterator;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KerberosTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import sun.security.krb5.internal.ReplayCache;

public class MemoryCache extends ReplayCache
{
    private static final int lifespan;
    private static final boolean DEBUG;
    private final Map<String, AuthList> content;
    
    public MemoryCache() {
        this.content = new ConcurrentHashMap<String, AuthList>();
    }
    
    @Override
    public synchronized void checkAndStore(final KerberosTime kerberosTime, final AuthTimeWithHash authTimeWithHash) throws KrbApErrException {
        final String string = authTimeWithHash.client + "|" + authTimeWithHash.server;
        this.content.computeIfAbsent(string, p0 -> new AuthList(MemoryCache.lifespan)).put(authTimeWithHash, kerberosTime);
        if (MemoryCache.DEBUG) {
            System.out.println("MemoryCache: add " + authTimeWithHash + " to " + string);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<AuthList> iterator = this.content.values().iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
        }
        return sb.toString();
    }
    
    static {
        lifespan = KerberosTime.getDefaultSkew();
        DEBUG = Krb5.DEBUG;
    }
}
