package io.netty.resolver.dns;

import io.netty.channel.EventLoop;

public final class NoopDnsCnameCache implements DnsCnameCache
{
    public static final NoopDnsCnameCache INSTANCE;
    
    private NoopDnsCnameCache() {
    }
    
    @Override
    public String get(final String hostname) {
        return null;
    }
    
    @Override
    public void cache(final String hostname, final String cname, final long originalTtl, final EventLoop loop) {
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public boolean clear(final String hostname) {
        return false;
    }
    
    static {
        INSTANCE = new NoopDnsCnameCache();
    }
}
