package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsQuestion;
import java.util.List;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;

final class NoopDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver
{
    static final NoopDnsQueryLifecycleObserver INSTANCE;
    
    private NoopDnsQueryLifecycleObserver() {
    }
    
    @Override
    public void queryWritten(final InetSocketAddress dnsServerAddress, final ChannelFuture future) {
    }
    
    @Override
    public void queryCancelled(final int queriesRemaining) {
    }
    
    @Override
    public DnsQueryLifecycleObserver queryRedirected(final List<InetSocketAddress> nameServers) {
        return this;
    }
    
    @Override
    public DnsQueryLifecycleObserver queryCNAMEd(final DnsQuestion cnameQuestion) {
        return this;
    }
    
    @Override
    public DnsQueryLifecycleObserver queryNoAnswer(final DnsResponseCode code) {
        return this;
    }
    
    @Override
    public void queryFailed(final Throwable cause) {
    }
    
    @Override
    public void querySucceed() {
    }
    
    static {
        INSTANCE = new NoopDnsQueryLifecycleObserver();
    }
}
