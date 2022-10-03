package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.net.InetSocketAddress;

public interface AuthoritativeDnsServerCache
{
    DnsServerAddressStream get(final String p0);
    
    void cache(final String p0, final InetSocketAddress p1, final long p2, final EventLoop p3);
    
    void clear();
    
    boolean clear(final String p0);
}
