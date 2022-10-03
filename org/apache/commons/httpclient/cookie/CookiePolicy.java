package org.apache.commons.httpclient.cookie;

import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import java.util.Map;

public abstract class CookiePolicy
{
    private static Map SPECS;
    public static final String BROWSER_COMPATIBILITY = "compatibility";
    public static final String NETSCAPE = "netscape";
    public static final String RFC_2109 = "rfc2109";
    public static final String RFC_2965 = "rfc2965";
    public static final String IGNORE_COOKIES = "ignoreCookies";
    public static final String DEFAULT = "default";
    public static final int COMPATIBILITY = 0;
    public static final int NETSCAPE_DRAFT = 1;
    public static final int RFC2109 = 2;
    public static final int RFC2965 = 3;
    private static int defaultPolicy;
    protected static final Log LOG;
    
    public static void registerCookieSpec(final String id, final Class clazz) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Cookie spec class may not be null");
        }
        CookiePolicy.SPECS.put(id.toLowerCase(), clazz);
    }
    
    public static void unregisterCookieSpec(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        CookiePolicy.SPECS.remove(id.toLowerCase());
    }
    
    public static CookieSpec getCookieSpec(final String id) throws IllegalStateException {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        final Class clazz = CookiePolicy.SPECS.get(id.toLowerCase());
        if (clazz != null) {
            try {
                return clazz.newInstance();
            }
            catch (final Exception e) {
                CookiePolicy.LOG.error((Object)("Error initializing cookie spec: " + id), (Throwable)e);
                throw new IllegalStateException(id + " cookie spec implemented by " + clazz.getName() + " could not be initialized");
            }
        }
        throw new IllegalStateException("Unsupported cookie spec " + id);
    }
    
    public static int getDefaultPolicy() {
        return CookiePolicy.defaultPolicy;
    }
    
    public static void setDefaultPolicy(final int policy) {
        CookiePolicy.defaultPolicy = policy;
    }
    
    public static CookieSpec getSpecByPolicy(final int policy) {
        switch (policy) {
            case 0: {
                return new CookieSpecBase();
            }
            case 1: {
                return new NetscapeDraftSpec();
            }
            case 2: {
                return new RFC2109Spec();
            }
            case 3: {
                return new RFC2965Spec();
            }
            default: {
                return getDefaultSpec();
            }
        }
    }
    
    public static CookieSpec getDefaultSpec() {
        try {
            return getCookieSpec("default");
        }
        catch (final IllegalStateException e) {
            CookiePolicy.LOG.warn((Object)"Default cookie policy is not registered");
            return new RFC2109Spec();
        }
    }
    
    public static CookieSpec getSpecByVersion(final int ver) {
        switch (ver) {
            case 0: {
                return new NetscapeDraftSpec();
            }
            case 1: {
                return new RFC2109Spec();
            }
            default: {
                return getDefaultSpec();
            }
        }
    }
    
    public static CookieSpec getCompatibilitySpec() {
        return getSpecByPolicy(0);
    }
    
    public static String[] getRegisteredCookieSpecs() {
        return (String[])CookiePolicy.SPECS.keySet().toArray(new String[CookiePolicy.SPECS.size()]);
    }
    
    static {
        CookiePolicy.SPECS = Collections.synchronizedMap(new HashMap<Object, Object>());
        registerCookieSpec("default", RFC2109Spec.class);
        registerCookieSpec("rfc2109", RFC2109Spec.class);
        registerCookieSpec("rfc2965", RFC2965Spec.class);
        registerCookieSpec("compatibility", CookieSpecBase.class);
        registerCookieSpec("netscape", NetscapeDraftSpec.class);
        registerCookieSpec("ignoreCookies", IgnoreCookiesSpec.class);
        CookiePolicy.defaultPolicy = 2;
        LOG = LogFactory.getLog(CookiePolicy.class);
    }
}
