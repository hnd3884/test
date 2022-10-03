package org.bouncycastle.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Set;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.security.AccessControlException;

public class Properties
{
    private static final ThreadLocal threadProperties;
    
    private Properties() {
    }
    
    public static boolean isOverrideSet(final String s) {
        try {
            final String fetchProperty = fetchProperty(s);
            return fetchProperty != null && "true".equals(Strings.toLowerCase(fetchProperty));
        }
        catch (final AccessControlException ex) {
            return false;
        }
    }
    
    public static boolean setThreadOverride(final String s, final boolean b) {
        final boolean overrideSet = isOverrideSet(s);
        Map map = Properties.threadProperties.get();
        if (map == null) {
            map = new HashMap();
        }
        map.put(s, b ? "true" : "false");
        Properties.threadProperties.set(map);
        return overrideSet;
    }
    
    public static boolean removeThreadOverride(final String s) {
        final boolean overrideSet = isOverrideSet(s);
        final Map map = Properties.threadProperties.get();
        if (map == null) {
            return false;
        }
        map.remove(s);
        if (map.isEmpty()) {
            Properties.threadProperties.remove();
        }
        else {
            Properties.threadProperties.set(map);
        }
        return overrideSet;
    }
    
    public static BigInteger asBigInteger(final String s) {
        final String fetchProperty = fetchProperty(s);
        if (fetchProperty != null) {
            return new BigInteger(fetchProperty);
        }
        return null;
    }
    
    public static Set<String> asKeySet(final String s) {
        final HashSet set = new HashSet();
        final String fetchProperty = fetchProperty(s);
        if (fetchProperty != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(fetchProperty, ",");
            while (stringTokenizer.hasMoreElements()) {
                set.add(Strings.toLowerCase(stringTokenizer.nextToken()).trim());
            }
        }
        return (Set<String>)Collections.unmodifiableSet((Set<?>)set);
    }
    
    private static String fetchProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                final Map map = Properties.threadProperties.get();
                if (map != null) {
                    return map.get(s);
                }
                return System.getProperty(s);
            }
        });
    }
    
    static {
        threadProperties = new ThreadLocal();
    }
}
