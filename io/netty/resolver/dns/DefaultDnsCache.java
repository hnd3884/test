package io.netty.resolver.dns;

import io.netty.util.internal.StringUtil;
import io.netty.channel.EventLoop;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.ObjectUtil;

public class DefaultDnsCache implements DnsCache
{
    private final Cache<DefaultDnsCacheEntry> resolveCache;
    private final int minTtl;
    private final int maxTtl;
    private final int negativeTtl;
    
    public DefaultDnsCache() {
        this(0, Cache.MAX_SUPPORTED_TTL_SECS, 0);
    }
    
    public DefaultDnsCache(final int minTtl, final int maxTtl, final int negativeTtl) {
        this.resolveCache = new Cache<DefaultDnsCacheEntry>() {
            @Override
            protected boolean shouldReplaceAll(final DefaultDnsCacheEntry entry) {
                return entry.cause() != null;
            }
            
            @Override
            protected boolean equals(final DefaultDnsCacheEntry entry, final DefaultDnsCacheEntry otherEntry) {
                if (entry.address() != null) {
                    return entry.address().equals(otherEntry.address());
                }
                return otherEntry.address() == null && entry.cause().equals(otherEntry.cause());
            }
        };
        this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(minTtl, "minTtl"));
        this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(maxTtl, "maxTtl"));
        if (minTtl > maxTtl) {
            throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
        }
        this.negativeTtl = ObjectUtil.checkPositiveOrZero(negativeTtl, "negativeTtl");
    }
    
    public int minTtl() {
        return this.minTtl;
    }
    
    public int maxTtl() {
        return this.maxTtl;
    }
    
    public int negativeTtl() {
        return this.negativeTtl;
    }
    
    @Override
    public void clear() {
        this.resolveCache.clear();
    }
    
    @Override
    public boolean clear(final String hostname) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        return this.resolveCache.clear(appendDot(hostname));
    }
    
    private static boolean emptyAdditionals(final DnsRecord[] additionals) {
        return additionals == null || additionals.length == 0;
    }
    
    @Override
    public List<? extends DnsCacheEntry> get(final String hostname, final DnsRecord[] additionals) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        if (!emptyAdditionals(additionals)) {
            return Collections.emptyList();
        }
        return this.resolveCache.get(appendDot(hostname));
    }
    
    @Override
    public DnsCacheEntry cache(final String hostname, final DnsRecord[] additionals, final InetAddress address, final long originalTtl, final EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(address, "address");
        ObjectUtil.checkNotNull(loop, "loop");
        final DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, address);
        if (this.maxTtl == 0 || !emptyAdditionals(additionals)) {
            return e;
        }
        this.resolveCache.cache(appendDot(hostname), e, Math.max(this.minTtl, (int)Math.min(this.maxTtl, originalTtl)), loop);
        return e;
    }
    
    @Override
    public DnsCacheEntry cache(final String hostname, final DnsRecord[] additionals, final Throwable cause, final EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(cause, "cause");
        ObjectUtil.checkNotNull(loop, "loop");
        final DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, cause);
        if (this.negativeTtl == 0 || !emptyAdditionals(additionals)) {
            return e;
        }
        this.resolveCache.cache(appendDot(hostname), e, this.negativeTtl, loop);
        return e;
    }
    
    @Override
    public String toString() {
        return "DefaultDnsCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", negativeTtl=" + this.negativeTtl + ", cached resolved hostname=" + this.resolveCache.size() + ')';
    }
    
    private static String appendDot(final String hostname) {
        return StringUtil.endsWith(hostname, '.') ? hostname : (hostname + '.');
    }
    
    private static final class DefaultDnsCacheEntry implements DnsCacheEntry
    {
        private final String hostname;
        private final InetAddress address;
        private final Throwable cause;
        
        DefaultDnsCacheEntry(final String hostname, final InetAddress address) {
            this.hostname = hostname;
            this.address = address;
            this.cause = null;
        }
        
        DefaultDnsCacheEntry(final String hostname, final Throwable cause) {
            this.hostname = hostname;
            this.cause = cause;
            this.address = null;
        }
        
        @Override
        public InetAddress address() {
            return this.address;
        }
        
        @Override
        public Throwable cause() {
            return this.cause;
        }
        
        String hostname() {
            return this.hostname;
        }
        
        @Override
        public String toString() {
            if (this.cause != null) {
                return this.hostname + '/' + this.cause;
            }
            return this.address.toString();
        }
    }
}
