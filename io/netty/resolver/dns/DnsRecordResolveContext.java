package io.netty.resolver.dns;

import java.net.UnknownHostException;
import java.util.List;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.concurrent.Promise;
import io.netty.handler.codec.dns.DnsRecord;

final class DnsRecordResolveContext extends DnsResolveContext<DnsRecord>
{
    DnsRecordResolveContext(final DnsNameResolver parent, final Promise<?> originalPromise, final DnsQuestion question, final DnsRecord[] additionals, final DnsServerAddressStream nameServerAddrs, final int allowedQueries) {
        this(parent, originalPromise, question.name(), question.dnsClass(), new DnsRecordType[] { question.type() }, additionals, nameServerAddrs, allowedQueries);
    }
    
    private DnsRecordResolveContext(final DnsNameResolver parent, final Promise<?> originalPromise, final String hostname, final int dnsClass, final DnsRecordType[] expectedTypes, final DnsRecord[] additionals, final DnsServerAddressStream nameServerAddrs, final int allowedQueries) {
        super(parent, originalPromise, hostname, dnsClass, expectedTypes, additionals, nameServerAddrs, allowedQueries);
    }
    
    @Override
    DnsResolveContext<DnsRecord> newResolverContext(final DnsNameResolver parent, final Promise<?> originalPromise, final String hostname, final int dnsClass, final DnsRecordType[] expectedTypes, final DnsRecord[] additionals, final DnsServerAddressStream nameServerAddrs, final int allowedQueries) {
        return new DnsRecordResolveContext(parent, originalPromise, hostname, dnsClass, expectedTypes, additionals, nameServerAddrs, allowedQueries);
    }
    
    @Override
    DnsRecord convertRecord(final DnsRecord record, final String hostname, final DnsRecord[] additionals, final EventLoop eventLoop) {
        return ReferenceCountUtil.retain(record);
    }
    
    @Override
    List<DnsRecord> filterResults(final List<DnsRecord> unfiltered) {
        return unfiltered;
    }
    
    @Override
    boolean isCompleteEarly(final DnsRecord resolved) {
        return false;
    }
    
    @Override
    boolean isDuplicateAllowed() {
        return true;
    }
    
    @Override
    void cache(final String hostname, final DnsRecord[] additionals, final DnsRecord result, final DnsRecord convertedResult) {
    }
    
    @Override
    void cache(final String hostname, final DnsRecord[] additionals, final UnknownHostException cause) {
    }
    
    @Override
    DnsCnameCache cnameCache() {
        return NoopDnsCnameCache.INSTANCE;
    }
}
