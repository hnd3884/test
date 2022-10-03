package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.net.InetSocketAddress;

public final class NoopAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache
{
    public static final NoopAuthoritativeDnsServerCache INSTANCE;
    
    private NoopAuthoritativeDnsServerCache() {
    }
    
    @Override
    public DnsServerAddressStream get(final String hostname) {
        return null;
    }
    
    @Override
    public void cache(final String hostname, final InetSocketAddress address, final long originalTtl, final EventLoop loop) {
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public boolean clear(final String hostname) {
        return false;
    }
    
    static {
        INSTANCE = new NoopAuthoritativeDnsServerCache();
    }
}
