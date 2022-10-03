package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DefaultDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.concurrent.Promise;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import java.net.InetSocketAddress;
import io.netty.channel.Channel;

final class TcpDnsQueryContext extends DnsQueryContext
{
    private final Channel channel;
    
    TcpDnsQueryContext(final DnsNameResolver parent, final Channel channel, final InetSocketAddress nameServerAddr, final DnsQuestion question, final DnsRecord[] additionals, final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise) {
        super(parent, nameServerAddr, question, additionals, promise);
        this.channel = channel;
    }
    
    @Override
    protected DnsQuery newQuery(final int id) {
        return new DefaultDnsQuery(id);
    }
    
    @Override
    protected Channel channel() {
        return this.channel;
    }
    
    @Override
    protected String protocol() {
        return "TCP";
    }
}
