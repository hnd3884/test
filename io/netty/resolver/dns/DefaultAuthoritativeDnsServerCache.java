package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.Comparator;

public class DefaultAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache
{
    private final int minTtl;
    private final int maxTtl;
    private final Comparator<InetSocketAddress> comparator;
    private final Cache<InetSocketAddress> resolveCache;
    
    public DefaultAuthoritativeDnsServerCache() {
        this(0, Cache.MAX_SUPPORTED_TTL_SECS, null);
    }
    
    public DefaultAuthoritativeDnsServerCache(final int minTtl, final int maxTtl, final Comparator<InetSocketAddress> comparator) {
        this.resolveCache = new Cache<InetSocketAddress>() {
            @Override
            protected boolean shouldReplaceAll(final InetSocketAddress entry) {
                return false;
            }
            
            @Override
            protected boolean equals(final InetSocketAddress entry, final InetSocketAddress otherEntry) {
                if (PlatformDependent.javaVersion() >= 7) {
                    return entry.getHostString().equalsIgnoreCase(otherEntry.getHostString());
                }
                return entry.getHostName().equalsIgnoreCase(otherEntry.getHostName());
            }
            
            @Override
            protected void sortEntries(final String hostname, final List<InetSocketAddress> entries) {
                if (DefaultAuthoritativeDnsServerCache.this.comparator != null) {
                    Collections.sort(entries, DefaultAuthoritativeDnsServerCache.this.comparator);
                }
            }
        };
        this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(minTtl, "minTtl"));
        this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositive(maxTtl, "maxTtl"));
        if (minTtl > maxTtl) {
            throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
        }
        this.comparator = comparator;
    }
    
    @Override
    public DnsServerAddressStream get(final String hostname) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        final List<? extends InetSocketAddress> addresses = this.resolveCache.get(hostname);
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return new SequentialDnsServerAddressStream(addresses, 0);
    }
    
    @Override
    public void cache(final String hostname, final InetSocketAddress address, final long originalTtl, final EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(address, "address");
        ObjectUtil.checkNotNull(loop, "loop");
        if (PlatformDependent.javaVersion() >= 7 && address.getHostString() == null) {
            return;
        }
        this.resolveCache.cache(hostname, address, Math.max(this.minTtl, (int)Math.min(this.maxTtl, originalTtl)), loop);
    }
    
    @Override
    public void clear() {
        this.resolveCache.clear();
    }
    
    @Override
    public boolean clear(final String hostname) {
        return this.resolveCache.clear(ObjectUtil.checkNotNull(hostname, "hostname"));
    }
    
    @Override
    public String toString() {
        return "DefaultAuthoritativeDnsServerCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", cached nameservers=" + this.resolveCache.size() + ')';
    }
}
