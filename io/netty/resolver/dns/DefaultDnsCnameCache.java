package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.util.List;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.AsciiString;

public final class DefaultDnsCnameCache implements DnsCnameCache
{
    private final int minTtl;
    private final int maxTtl;
    private final Cache<String> cache;
    
    public DefaultDnsCnameCache() {
        this(0, Cache.MAX_SUPPORTED_TTL_SECS);
    }
    
    public DefaultDnsCnameCache(final int minTtl, final int maxTtl) {
        this.cache = new Cache<String>() {
            @Override
            protected boolean shouldReplaceAll(final String entry) {
                return true;
            }
            
            @Override
            protected boolean equals(final String entry, final String otherEntry) {
                return AsciiString.contentEqualsIgnoreCase(entry, otherEntry);
            }
        };
        this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(minTtl, "minTtl"));
        this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositive(maxTtl, "maxTtl"));
        if (minTtl > maxTtl) {
            throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
        }
    }
    
    @Override
    public String get(final String hostname) {
        final List<? extends String> cached = this.cache.get(ObjectUtil.checkNotNull(hostname, "hostname"));
        if (cached == null || cached.isEmpty()) {
            return null;
        }
        return (String)cached.get(0);
    }
    
    @Override
    public void cache(final String hostname, final String cname, final long originalTtl, final EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(cname, "cname");
        ObjectUtil.checkNotNull(loop, "loop");
        this.cache.cache(hostname, cname, Math.max(this.minTtl, (int)Math.min(this.maxTtl, originalTtl)), loop);
    }
    
    @Override
    public void clear() {
        this.cache.clear();
    }
    
    @Override
    public boolean clear(final String hostname) {
        return this.cache.clear(ObjectUtil.checkNotNull(hostname, "hostname"));
    }
}
