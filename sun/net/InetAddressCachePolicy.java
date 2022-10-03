package sun.net;

import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;

public final class InetAddressCachePolicy
{
    private static final String cachePolicyProp = "networkaddress.cache.ttl";
    private static final String cachePolicyPropFallback = "sun.net.inetaddr.ttl";
    private static final String negativeCachePolicyProp = "networkaddress.cache.negative.ttl";
    private static final String negativeCachePolicyPropFallback = "sun.net.inetaddr.negative.ttl";
    public static final int FOREVER = -1;
    public static final int NEVER = 0;
    public static final int DEFAULT_POSITIVE = 30;
    private static int cachePolicy;
    private static int negativeCachePolicy;
    private static boolean propertySet;
    private static boolean propertyNegativeSet;
    
    public static synchronized int get() {
        return InetAddressCachePolicy.cachePolicy;
    }
    
    public static synchronized int getNegative() {
        return InetAddressCachePolicy.negativeCachePolicy;
    }
    
    public static synchronized void setIfNotSet(final int cachePolicy) {
        if (!InetAddressCachePolicy.propertySet) {
            checkValue(cachePolicy, InetAddressCachePolicy.cachePolicy);
            InetAddressCachePolicy.cachePolicy = cachePolicy;
        }
    }
    
    public static synchronized void setNegativeIfNotSet(final int negativeCachePolicy) {
        if (!InetAddressCachePolicy.propertyNegativeSet) {
            InetAddressCachePolicy.negativeCachePolicy = negativeCachePolicy;
        }
    }
    
    private static void checkValue(final int n, final int n2) {
        if (n == -1) {
            return;
        }
        if (n2 == -1 || n < n2 || n < -1) {
            throw new SecurityException("can't make InetAddress cache more lax");
        }
    }
    
    static {
        InetAddressCachePolicy.cachePolicy = -1;
        InetAddressCachePolicy.negativeCachePolicy = 0;
        final Integer n = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                try {
                    final String property = Security.getProperty("networkaddress.cache.ttl");
                    if (property != null) {
                        return Integer.valueOf(property);
                    }
                }
                catch (final NumberFormatException ex) {}
                try {
                    final String property2 = System.getProperty("sun.net.inetaddr.ttl");
                    if (property2 != null) {
                        return Integer.decode(property2);
                    }
                }
                catch (final NumberFormatException ex2) {}
                return null;
            }
        });
        if (n != null) {
            InetAddressCachePolicy.cachePolicy = n;
            if (InetAddressCachePolicy.cachePolicy < 0) {
                InetAddressCachePolicy.cachePolicy = -1;
            }
            InetAddressCachePolicy.propertySet = true;
        }
        else if (System.getSecurityManager() == null) {
            InetAddressCachePolicy.cachePolicy = 30;
        }
        final Integer n2 = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                try {
                    final String property = Security.getProperty("networkaddress.cache.negative.ttl");
                    if (property != null) {
                        return Integer.valueOf(property);
                    }
                }
                catch (final NumberFormatException ex) {}
                try {
                    final String property2 = System.getProperty("sun.net.inetaddr.negative.ttl");
                    if (property2 != null) {
                        return Integer.decode(property2);
                    }
                }
                catch (final NumberFormatException ex2) {}
                return null;
            }
        });
        if (n2 != null) {
            InetAddressCachePolicy.negativeCachePolicy = n2;
            if (InetAddressCachePolicy.negativeCachePolicy < 0) {
                InetAddressCachePolicy.negativeCachePolicy = -1;
            }
            InetAddressCachePolicy.propertyNegativeSet = true;
        }
    }
}
