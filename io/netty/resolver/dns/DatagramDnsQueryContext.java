package io.netty.resolver.dns;

import io.netty.channel.Channel;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.concurrent.Promise;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import java.net.InetSocketAddress;

final class DatagramDnsQueryContext extends DnsQueryContext
{
    DatagramDnsQueryContext(final DnsNameResolver parent, final InetSocketAddress nameServerAddr, final DnsQuestion question, final DnsRecord[] additionals, final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise) {
        super(parent, nameServerAddr, question, additionals, promise);
    }
    
    @Override
    protected DnsQuery newQuery(final int id) {
        return new DatagramDnsQuery(null, this.nameServerAddr(), id);
    }
    
    @Override
    protected Channel channel() {
        return this.parent().ch;
    }
    
    @Override
    protected String protocol() {
        return "UDP";
    }
}
