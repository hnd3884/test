package io.netty.resolver.dns;

import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.concurrent.Promise;
import java.net.InetAddress;

final class DnsAddressResolveContext extends DnsResolveContext<InetAddress>
{
    private final DnsCache resolveCache;
    private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
    private final boolean completeEarlyIfPossible;
    
    DnsAddressResolveContext(final DnsNameResolver parent, final Promise<?> originalPromise, final String hostname, final DnsRecord[] additionals, final DnsServerAddressStream nameServerAddrs, final int allowedQueries, final DnsCache resolveCache, final AuthoritativeDnsServerCache authoritativeDnsServerCache, final boolean completeEarlyIfPossible) {
        super(parent, originalPromise, hostname, 1, parent.resolveRecordTypes(), additionals, nameServerAddrs, allowedQueries);
        this.resolveCache = resolveCache;
        this.authoritativeDnsServerCache = authoritativeDnsServerCache;
        this.completeEarlyIfPossible = completeEarlyIfPossible;
    }
    
    @Override
    DnsResolveContext<InetAddress> newResolverContext(final DnsNameResolver parent, final Promise<?> originalPromise, final String hostname, final int dnsClass, final DnsRecordType[] expectedTypes, final DnsRecord[] additionals, final DnsServerAddressStream nameServerAddrs, final int allowedQueries) {
        return new DnsAddressResolveContext(parent, originalPromise, hostname, additionals, nameServerAddrs, allowedQueries, this.resolveCache, this.authoritativeDnsServerCache, this.completeEarlyIfPossible);
    }
    
    @Override
    InetAddress convertRecord(final DnsRecord record, final String hostname, final DnsRecord[] additionals, final EventLoop eventLoop) {
        return DnsAddressDecoder.decodeAddress(record, hostname, this.parent.isDecodeIdn());
    }
    
    @Override
    List<InetAddress> filterResults(final List<InetAddress> unfiltered) {
        Collections.sort(unfiltered, PreferredAddressTypeComparator.comparator(this.parent.preferredAddressType()));
        return unfiltered;
    }
    
    @Override
    boolean isCompleteEarly(final InetAddress resolved) {
        return this.completeEarlyIfPossible && this.parent.preferredAddressType().addressType() == resolved.getClass();
    }
    
    @Override
    boolean isDuplicateAllowed() {
        return false;
    }
    
    @Override
    void cache(final String hostname, final DnsRecord[] additionals, final DnsRecord result, final InetAddress convertedResult) {
        this.resolveCache.cache(hostname, additionals, convertedResult, result.timeToLive(), this.parent.ch.eventLoop());
    }
    
    @Override
    void cache(final String hostname, final DnsRecord[] additionals, final UnknownHostException cause) {
        this.resolveCache.cache(hostname, additionals, cause, this.parent.ch.eventLoop());
    }
    
    @Override
    void doSearchDomainQuery(final String hostname, final Promise<List<InetAddress>> nextPromise) {
        if (!DnsNameResolver.doResolveAllCached(hostname, this.additionals, nextPromise, this.resolveCache, this.parent.resolvedInternetProtocolFamiliesUnsafe())) {
            super.doSearchDomainQuery(hostname, nextPromise);
        }
    }
    
    @Override
    DnsCache resolveCache() {
        return this.resolveCache;
    }
    
    @Override
    AuthoritativeDnsServerCache authoritativeDnsServerCache() {
        return this.authoritativeDnsServerCache;
    }
}
