package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;

public final class NoopDnsQueryLifecycleObserverFactory implements DnsQueryLifecycleObserverFactory
{
    public static final NoopDnsQueryLifecycleObserverFactory INSTANCE;
    
    private NoopDnsQueryLifecycleObserverFactory() {
    }
    
    @Override
    public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(final DnsQuestion question) {
        return NoopDnsQueryLifecycleObserver.INSTANCE;
    }
    
    static {
        INSTANCE = new NoopDnsQueryLifecycleObserverFactory();
    }
}
