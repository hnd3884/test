package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import io.netty.handler.codec.dns.DnsRecord;

public final class NoopDnsCache implements DnsCache
{
    public static final NoopDnsCache INSTANCE;
    
    private NoopDnsCache() {
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public boolean clear(final String hostname) {
        return false;
    }
    
    @Override
    public List<? extends DnsCacheEntry> get(final String hostname, final DnsRecord[] additionals) {
        return Collections.emptyList();
    }
    
    @Override
    public DnsCacheEntry cache(final String hostname, final DnsRecord[] additional, final InetAddress address, final long originalTtl, final EventLoop loop) {
        return new NoopDnsCacheEntry(address);
    }
    
    @Override
    public DnsCacheEntry cache(final String hostname, final DnsRecord[] additional, final Throwable cause, final EventLoop loop) {
        return null;
    }
    
    @Override
    public String toString() {
        return NoopDnsCache.class.getSimpleName();
    }
    
    static {
        INSTANCE = new NoopDnsCache();
    }
    
    private static final class NoopDnsCacheEntry implements DnsCacheEntry
    {
        private final InetAddress address;
        
        NoopDnsCacheEntry(final InetAddress address) {
            this.address = address;
        }
        
        @Override
        public InetAddress address() {
            return this.address;
        }
        
        @Override
        public Throwable cause() {
            return null;
        }
        
        @Override
        public String toString() {
            return this.address.toString();
        }
    }
}
