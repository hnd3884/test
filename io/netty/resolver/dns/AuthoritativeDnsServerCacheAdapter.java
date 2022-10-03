package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.net.InetAddress;
import java.util.List;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.dns.DnsRecord;

final class AuthoritativeDnsServerCacheAdapter implements AuthoritativeDnsServerCache
{
    private static final DnsRecord[] EMPTY;
    private final DnsCache cache;
    
    AuthoritativeDnsServerCacheAdapter(final DnsCache cache) {
        this.cache = ObjectUtil.checkNotNull(cache, "cache");
    }
    
    @Override
    public DnsServerAddressStream get(final String hostname) {
        final List<? extends DnsCacheEntry> entries = this.cache.get(hostname, AuthoritativeDnsServerCacheAdapter.EMPTY);
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        if (((DnsCacheEntry)entries.get(0)).cause() != null) {
            return null;
        }
        final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(entries.size());
        int i = 0;
        do {
            final InetAddress addr = ((DnsCacheEntry)entries.get(i)).address();
            addresses.add(new InetSocketAddress(addr, 53));
        } while (++i < entries.size());
        return new SequentialDnsServerAddressStream(addresses, 0);
    }
    
    @Override
    public void cache(final String hostname, final InetSocketAddress address, final long originalTtl, final EventLoop loop) {
        if (!address.isUnresolved()) {
            this.cache.cache(hostname, AuthoritativeDnsServerCacheAdapter.EMPTY, address.getAddress(), originalTtl, loop);
        }
    }
    
    @Override
    public void clear() {
        this.cache.clear();
    }
    
    @Override
    public boolean clear(final String hostname) {
        return this.cache.clear(hostname);
    }
    
    static {
        EMPTY = new DnsRecord[0];
    }
}
