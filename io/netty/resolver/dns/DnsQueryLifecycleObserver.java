package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsQuestion;
import java.util.List;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;

public interface DnsQueryLifecycleObserver
{
    void queryWritten(final InetSocketAddress p0, final ChannelFuture p1);
    
    void queryCancelled(final int p0);
    
    DnsQueryLifecycleObserver queryRedirected(final List<InetSocketAddress> p0);
    
    DnsQueryLifecycleObserver queryCNAMEd(final DnsQuestion p0);
    
    DnsQueryLifecycleObserver queryNoAnswer(final DnsResponseCode p0);
    
    void queryFailed(final Throwable p0);
    
    void querySucceed();
}
