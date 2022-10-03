package io.netty.resolver.dns;

import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.util.internal.StringUtil;
import io.netty.channel.EventLoop;
import io.netty.resolver.AddressResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.ChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.util.internal.PlatformDependent;
import java.util.List;
import java.net.InetAddress;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.ConcurrentMap;
import java.net.InetSocketAddress;
import io.netty.resolver.AddressResolverGroup;

public class DnsAddressResolverGroup extends AddressResolverGroup<InetSocketAddress>
{
    private final DnsNameResolverBuilder dnsResolverBuilder;
    private final ConcurrentMap<String, Promise<InetAddress>> resolvesInProgress;
    private final ConcurrentMap<String, Promise<List<InetAddress>>> resolveAllsInProgress;
    
    public DnsAddressResolverGroup(final DnsNameResolverBuilder dnsResolverBuilder) {
        this.resolvesInProgress = PlatformDependent.newConcurrentHashMap();
        this.resolveAllsInProgress = PlatformDependent.newConcurrentHashMap();
        this.dnsResolverBuilder = dnsResolverBuilder.copy();
    }
    
    public DnsAddressResolverGroup(final Class<? extends DatagramChannel> channelType, final DnsServerAddressStreamProvider nameServerProvider) {
        this.resolvesInProgress = PlatformDependent.newConcurrentHashMap();
        this.resolveAllsInProgress = PlatformDependent.newConcurrentHashMap();
        this.dnsResolverBuilder = new DnsNameResolverBuilder();
        this.dnsResolverBuilder.channelType(channelType).nameServerProvider(nameServerProvider);
    }
    
    public DnsAddressResolverGroup(final ChannelFactory<? extends DatagramChannel> channelFactory, final DnsServerAddressStreamProvider nameServerProvider) {
        this.resolvesInProgress = PlatformDependent.newConcurrentHashMap();
        this.resolveAllsInProgress = PlatformDependent.newConcurrentHashMap();
        this.dnsResolverBuilder = new DnsNameResolverBuilder();
        this.dnsResolverBuilder.channelFactory(channelFactory).nameServerProvider(nameServerProvider);
    }
    
    @Override
    protected final AddressResolver<InetSocketAddress> newResolver(final EventExecutor executor) throws Exception {
        if (!(executor instanceof EventLoop)) {
            throw new IllegalStateException("unsupported executor type: " + StringUtil.simpleClassName(executor) + " (expected: " + StringUtil.simpleClassName(EventLoop.class));
        }
        final EventLoop loop = this.dnsResolverBuilder.eventLoop;
        return this.newResolver((loop == null) ? ((EventLoop)executor) : loop, this.dnsResolverBuilder.channelFactory(), this.dnsResolverBuilder.nameServerProvider());
    }
    
    @Deprecated
    protected AddressResolver<InetSocketAddress> newResolver(final EventLoop eventLoop, final ChannelFactory<? extends DatagramChannel> channelFactory, final DnsServerAddressStreamProvider nameServerProvider) throws Exception {
        final NameResolver<InetAddress> resolver = new InflightNameResolver<InetAddress>(eventLoop, this.newNameResolver(eventLoop, channelFactory, nameServerProvider), this.resolvesInProgress, this.resolveAllsInProgress);
        return this.newAddressResolver(eventLoop, resolver);
    }
    
    protected NameResolver<InetAddress> newNameResolver(final EventLoop eventLoop, final ChannelFactory<? extends DatagramChannel> channelFactory, final DnsServerAddressStreamProvider nameServerProvider) throws Exception {
        final DnsNameResolverBuilder builder = this.dnsResolverBuilder.copy();
        return builder.eventLoop(eventLoop).channelFactory(channelFactory).nameServerProvider(nameServerProvider).build();
    }
    
    protected AddressResolver<InetSocketAddress> newAddressResolver(final EventLoop eventLoop, final NameResolver<InetAddress> resolver) throws Exception {
        return new InetSocketAddressResolver(eventLoop, resolver);
    }
}
