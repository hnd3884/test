package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class NameResolver
{
    private static final String JVM_PROPERTY_POSITIVE_ADDRESS_CACHE_TTL_SECONDS = "networkaddress.cache.ttl";
    private static final String JVM_PROPERTY_NEGATIVE_ADDRESS_CACHE_TTL_SECONDS = "networkaddress.cache.negative.ttl";
    
    protected NameResolver() {
    }
    
    public InetAddress getByName(final String host) throws UnknownHostException, SecurityException {
        return InetAddress.getByName(host);
    }
    
    public InetAddress[] getAllByName(final String host) throws UnknownHostException, SecurityException {
        return InetAddress.getAllByName(host);
    }
    
    public String getHostName(final InetAddress inetAddress) {
        return inetAddress.getHostName();
    }
    
    public String getCanonicalHostName(final InetAddress inetAddress) {
        return inetAddress.getCanonicalHostName();
    }
    
    public InetAddress getLocalHost() throws UnknownHostException, SecurityException {
        return InetAddress.getLocalHost();
    }
    
    public InetAddress getLoopbackAddress() {
        return InetAddress.getLoopbackAddress();
    }
    
    public static void setJVMSuccessfulLookupCacheTTLSeconds(final int seconds) {
        if (seconds < 0) {
            StaticUtils.setSystemProperty("networkaddress.cache.ttl", "-1");
        }
        else {
            StaticUtils.setSystemProperty("networkaddress.cache.ttl", String.valueOf(seconds));
        }
    }
    
    public static void setJVMUnsuccessfulLookupCacheTTLSeconds(final int seconds) {
        if (seconds < 0) {
            StaticUtils.setSystemProperty("networkaddress.cache.negative.ttl", "-1");
        }
        else {
            StaticUtils.setSystemProperty("networkaddress.cache.negative.ttl", String.valueOf(seconds));
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
}
