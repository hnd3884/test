package io.netty.resolver.dns;

import java.util.Arrays;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.ResolvedAddressTypes;
import java.net.SocketAddress;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;

public final class DnsNameResolverBuilder
{
    volatile EventLoop eventLoop;
    private ChannelFactory<? extends DatagramChannel> channelFactory;
    private ChannelFactory<? extends SocketChannel> socketChannelFactory;
    private DnsCache resolveCache;
    private DnsCnameCache cnameCache;
    private AuthoritativeDnsServerCache authoritativeDnsServerCache;
    private SocketAddress localAddress;
    private Integer minTtl;
    private Integer maxTtl;
    private Integer negativeTtl;
    private long queryTimeoutMillis;
    private ResolvedAddressTypes resolvedAddressTypes;
    private boolean completeOncePreferredResolved;
    private boolean recursionDesired;
    private int maxQueriesPerResolve;
    private boolean traceEnabled;
    private int maxPayloadSize;
    private boolean optResourceEnabled;
    private HostsFileEntriesResolver hostsFileEntriesResolver;
    private DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
    private DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;
    private String[] searchDomains;
    private int ndots;
    private boolean decodeIdn;
    
    public DnsNameResolverBuilder() {
        this.queryTimeoutMillis = -1L;
        this.resolvedAddressTypes = DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
        this.recursionDesired = true;
        this.maxQueriesPerResolve = -1;
        this.maxPayloadSize = 4096;
        this.optResourceEnabled = true;
        this.hostsFileEntriesResolver = HostsFileEntriesResolver.DEFAULT;
        this.dnsServerAddressStreamProvider = DnsServerAddressStreamProviders.platformDefault();
        this.dnsQueryLifecycleObserverFactory = NoopDnsQueryLifecycleObserverFactory.INSTANCE;
        this.ndots = -1;
        this.decodeIdn = true;
    }
    
    public DnsNameResolverBuilder(final EventLoop eventLoop) {
        this.queryTimeoutMillis = -1L;
        this.resolvedAddressTypes = DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
        this.recursionDesired = true;
        this.maxQueriesPerResolve = -1;
        this.maxPayloadSize = 4096;
        this.optResourceEnabled = true;
        this.hostsFileEntriesResolver = HostsFileEntriesResolver.DEFAULT;
        this.dnsServerAddressStreamProvider = DnsServerAddressStreamProviders.platformDefault();
        this.dnsQueryLifecycleObserverFactory = NoopDnsQueryLifecycleObserverFactory.INSTANCE;
        this.ndots = -1;
        this.decodeIdn = true;
        this.eventLoop(eventLoop);
    }
    
    public DnsNameResolverBuilder eventLoop(final EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        return this;
    }
    
    protected ChannelFactory<? extends DatagramChannel> channelFactory() {
        return this.channelFactory;
    }
    
    public DnsNameResolverBuilder channelFactory(final ChannelFactory<? extends DatagramChannel> channelFactory) {
        this.channelFactory = channelFactory;
        return this;
    }
    
    public DnsNameResolverBuilder channelType(final Class<? extends DatagramChannel> channelType) {
        return this.channelFactory(new ReflectiveChannelFactory<DatagramChannel>(channelType));
    }
    
    public DnsNameResolverBuilder socketChannelFactory(final ChannelFactory<? extends SocketChannel> channelFactory) {
        this.socketChannelFactory = channelFactory;
        return this;
    }
    
    public DnsNameResolverBuilder socketChannelType(final Class<? extends SocketChannel> channelType) {
        if (channelType == null) {
            return this.socketChannelFactory(null);
        }
        return this.socketChannelFactory(new ReflectiveChannelFactory<SocketChannel>(channelType));
    }
    
    public DnsNameResolverBuilder resolveCache(final DnsCache resolveCache) {
        this.resolveCache = resolveCache;
        return this;
    }
    
    public DnsNameResolverBuilder cnameCache(final DnsCnameCache cnameCache) {
        this.cnameCache = cnameCache;
        return this;
    }
    
    public DnsNameResolverBuilder dnsQueryLifecycleObserverFactory(final DnsQueryLifecycleObserverFactory lifecycleObserverFactory) {
        this.dnsQueryLifecycleObserverFactory = ObjectUtil.checkNotNull(lifecycleObserverFactory, "lifecycleObserverFactory");
        return this;
    }
    
    @Deprecated
    public DnsNameResolverBuilder authoritativeDnsServerCache(final DnsCache authoritativeDnsServerCache) {
        this.authoritativeDnsServerCache = new AuthoritativeDnsServerCacheAdapter(authoritativeDnsServerCache);
        return this;
    }
    
    public DnsNameResolverBuilder authoritativeDnsServerCache(final AuthoritativeDnsServerCache authoritativeDnsServerCache) {
        this.authoritativeDnsServerCache = authoritativeDnsServerCache;
        return this;
    }
    
    public DnsNameResolverBuilder localAddress(final SocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }
    
    public DnsNameResolverBuilder ttl(final int minTtl, final int maxTtl) {
        this.maxTtl = maxTtl;
        this.minTtl = minTtl;
        return this;
    }
    
    public DnsNameResolverBuilder negativeTtl(final int negativeTtl) {
        this.negativeTtl = negativeTtl;
        return this;
    }
    
    public DnsNameResolverBuilder queryTimeoutMillis(final long queryTimeoutMillis) {
        this.queryTimeoutMillis = queryTimeoutMillis;
        return this;
    }
    
    public static ResolvedAddressTypes computeResolvedAddressTypes(final InternetProtocolFamily... internetProtocolFamilies) {
        if (internetProtocolFamilies == null || internetProtocolFamilies.length == 0) {
            return DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
        }
        if (internetProtocolFamilies.length > 2) {
            throw new IllegalArgumentException("No more than 2 InternetProtocolFamilies");
        }
        switch (internetProtocolFamilies[0]) {
            case IPv4: {
                return (internetProtocolFamilies.length >= 2 && internetProtocolFamilies[1] == InternetProtocolFamily.IPv6) ? ResolvedAddressTypes.IPV4_PREFERRED : ResolvedAddressTypes.IPV4_ONLY;
            }
            case IPv6: {
                return (internetProtocolFamilies.length >= 2 && internetProtocolFamilies[1] == InternetProtocolFamily.IPv4) ? ResolvedAddressTypes.IPV6_PREFERRED : ResolvedAddressTypes.IPV6_ONLY;
            }
            default: {
                throw new IllegalArgumentException("Couldn't resolve ResolvedAddressTypes from InternetProtocolFamily array");
            }
        }
    }
    
    public DnsNameResolverBuilder resolvedAddressTypes(final ResolvedAddressTypes resolvedAddressTypes) {
        this.resolvedAddressTypes = resolvedAddressTypes;
        return this;
    }
    
    public DnsNameResolverBuilder completeOncePreferredResolved(final boolean completeOncePreferredResolved) {
        this.completeOncePreferredResolved = completeOncePreferredResolved;
        return this;
    }
    
    public DnsNameResolverBuilder recursionDesired(final boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }
    
    public DnsNameResolverBuilder maxQueriesPerResolve(final int maxQueriesPerResolve) {
        this.maxQueriesPerResolve = maxQueriesPerResolve;
        return this;
    }
    
    @Deprecated
    public DnsNameResolverBuilder traceEnabled(final boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
        return this;
    }
    
    public DnsNameResolverBuilder maxPayloadSize(final int maxPayloadSize) {
        this.maxPayloadSize = maxPayloadSize;
        return this;
    }
    
    public DnsNameResolverBuilder optResourceEnabled(final boolean optResourceEnabled) {
        this.optResourceEnabled = optResourceEnabled;
        return this;
    }
    
    public DnsNameResolverBuilder hostsFileEntriesResolver(final HostsFileEntriesResolver hostsFileEntriesResolver) {
        this.hostsFileEntriesResolver = hostsFileEntriesResolver;
        return this;
    }
    
    protected DnsServerAddressStreamProvider nameServerProvider() {
        return this.dnsServerAddressStreamProvider;
    }
    
    public DnsNameResolverBuilder nameServerProvider(final DnsServerAddressStreamProvider dnsServerAddressStreamProvider) {
        this.dnsServerAddressStreamProvider = ObjectUtil.checkNotNull(dnsServerAddressStreamProvider, "dnsServerAddressStreamProvider");
        return this;
    }
    
    public DnsNameResolverBuilder searchDomains(final Iterable<String> searchDomains) {
        ObjectUtil.checkNotNull(searchDomains, "searchDomains");
        final List<String> list = new ArrayList<String>(4);
        for (final String f : searchDomains) {
            if (f == null) {
                break;
            }
            if (list.contains(f)) {
                continue;
            }
            list.add(f);
        }
        this.searchDomains = list.toArray(new String[0]);
        return this;
    }
    
    public DnsNameResolverBuilder ndots(final int ndots) {
        this.ndots = ndots;
        return this;
    }
    
    private DnsCache newCache() {
        return new DefaultDnsCache(ObjectUtil.intValue(this.minTtl, 0), ObjectUtil.intValue(this.maxTtl, Integer.MAX_VALUE), ObjectUtil.intValue(this.negativeTtl, 0));
    }
    
    private AuthoritativeDnsServerCache newAuthoritativeDnsServerCache() {
        return new DefaultAuthoritativeDnsServerCache(ObjectUtil.intValue(this.minTtl, 0), ObjectUtil.intValue(this.maxTtl, Integer.MAX_VALUE), new NameServerComparator(DnsNameResolver.preferredAddressType(this.resolvedAddressTypes).addressType()));
    }
    
    private DnsCnameCache newCnameCache() {
        return new DefaultDnsCnameCache(ObjectUtil.intValue(this.minTtl, 0), ObjectUtil.intValue(this.maxTtl, Integer.MAX_VALUE));
    }
    
    public DnsNameResolverBuilder decodeIdn(final boolean decodeIdn) {
        this.decodeIdn = decodeIdn;
        return this;
    }
    
    public DnsNameResolver build() {
        if (this.eventLoop == null) {
            throw new IllegalStateException("eventLoop should be specified to build a DnsNameResolver.");
        }
        if (this.resolveCache != null && (this.minTtl != null || this.maxTtl != null || this.negativeTtl != null)) {
            throw new IllegalStateException("resolveCache and TTLs are mutually exclusive");
        }
        if (this.authoritativeDnsServerCache != null && (this.minTtl != null || this.maxTtl != null || this.negativeTtl != null)) {
            throw new IllegalStateException("authoritativeDnsServerCache and TTLs are mutually exclusive");
        }
        final DnsCache resolveCache = (this.resolveCache != null) ? this.resolveCache : this.newCache();
        final DnsCnameCache cnameCache = (this.cnameCache != null) ? this.cnameCache : this.newCnameCache();
        final AuthoritativeDnsServerCache authoritativeDnsServerCache = (this.authoritativeDnsServerCache != null) ? this.authoritativeDnsServerCache : this.newAuthoritativeDnsServerCache();
        return new DnsNameResolver(this.eventLoop, this.channelFactory, this.socketChannelFactory, resolveCache, cnameCache, authoritativeDnsServerCache, this.localAddress, this.dnsQueryLifecycleObserverFactory, this.queryTimeoutMillis, this.resolvedAddressTypes, this.recursionDesired, this.maxQueriesPerResolve, this.traceEnabled, this.maxPayloadSize, this.optResourceEnabled, this.hostsFileEntriesResolver, this.dnsServerAddressStreamProvider, this.searchDomains, this.ndots, this.decodeIdn, this.completeOncePreferredResolved);
    }
    
    public DnsNameResolverBuilder copy() {
        final DnsNameResolverBuilder copiedBuilder = new DnsNameResolverBuilder();
        if (this.eventLoop != null) {
            copiedBuilder.eventLoop(this.eventLoop);
        }
        if (this.channelFactory != null) {
            copiedBuilder.channelFactory(this.channelFactory);
        }
        if (this.socketChannelFactory != null) {
            copiedBuilder.socketChannelFactory(this.socketChannelFactory);
        }
        if (this.resolveCache != null) {
            copiedBuilder.resolveCache(this.resolveCache);
        }
        if (this.cnameCache != null) {
            copiedBuilder.cnameCache(this.cnameCache);
        }
        if (this.maxTtl != null && this.minTtl != null) {
            copiedBuilder.ttl(this.minTtl, this.maxTtl);
        }
        if (this.negativeTtl != null) {
            copiedBuilder.negativeTtl(this.negativeTtl);
        }
        if (this.authoritativeDnsServerCache != null) {
            copiedBuilder.authoritativeDnsServerCache(this.authoritativeDnsServerCache);
        }
        if (this.dnsQueryLifecycleObserverFactory != null) {
            copiedBuilder.dnsQueryLifecycleObserverFactory(this.dnsQueryLifecycleObserverFactory);
        }
        copiedBuilder.queryTimeoutMillis(this.queryTimeoutMillis);
        copiedBuilder.resolvedAddressTypes(this.resolvedAddressTypes);
        copiedBuilder.recursionDesired(this.recursionDesired);
        copiedBuilder.maxQueriesPerResolve(this.maxQueriesPerResolve);
        copiedBuilder.traceEnabled(this.traceEnabled);
        copiedBuilder.maxPayloadSize(this.maxPayloadSize);
        copiedBuilder.optResourceEnabled(this.optResourceEnabled);
        copiedBuilder.hostsFileEntriesResolver(this.hostsFileEntriesResolver);
        if (this.dnsServerAddressStreamProvider != null) {
            copiedBuilder.nameServerProvider(this.dnsServerAddressStreamProvider);
        }
        if (this.searchDomains != null) {
            copiedBuilder.searchDomains(Arrays.asList(this.searchDomains));
        }
        copiedBuilder.ndots(this.ndots);
        copiedBuilder.decodeIdn(this.decodeIdn);
        copiedBuilder.completeOncePreferredResolved(this.completeOncePreferredResolved);
        return copiedBuilder;
    }
}
