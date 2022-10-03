package io.netty.resolver.dns;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.util.ReferenceCounted;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.internal.StringUtil;
import java.net.IDN;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.NetUtil;
import io.netty.handler.codec.dns.DnsRawRecord;
import java.util.Collection;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.buffer.Unpooled;
import java.net.Inet4Address;
import java.util.ArrayList;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.concurrent.Promise;
import io.netty.resolver.DefaultHostsFileEntriesResolver;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;
import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.EventLoop;
import java.lang.reflect.Method;
import java.util.Collections;
import io.netty.util.internal.PlatformDependent;
import java.util.List;
import java.util.Enumeration;
import java.net.SocketException;
import java.net.Inet6Address;
import java.net.NetworkInterface;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelFactory;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.util.concurrent.FastThreadLocal;
import java.net.InetSocketAddress;
import java.util.Comparator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsRecord;
import java.net.InetAddress;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.resolver.InetNameResolver;

public class DnsNameResolver extends InetNameResolver
{
    private static final InternalLogger logger;
    private static final String LOCALHOST = "localhost";
    private static final String WINDOWS_HOST_NAME;
    private static final InetAddress LOCALHOST_ADDRESS;
    private static final DnsRecord[] EMPTY_ADDITIONALS;
    private static final DnsRecordType[] IPV4_ONLY_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
    private static final DnsRecordType[] IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
    private static final DnsRecordType[] IPV6_ONLY_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
    private static final DnsRecordType[] IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
    static final ResolvedAddressTypes DEFAULT_RESOLVE_ADDRESS_TYPES;
    static final String[] DEFAULT_SEARCH_DOMAINS;
    private static final UnixResolverOptions DEFAULT_OPTIONS;
    private static final DatagramDnsResponseDecoder DATAGRAM_DECODER;
    private static final DatagramDnsQueryEncoder DATAGRAM_ENCODER;
    private static final TcpDnsQueryEncoder TCP_ENCODER;
    final Future<Channel> channelFuture;
    final Channel ch;
    private final Comparator<InetSocketAddress> nameServerComparator;
    final DnsQueryContextManager queryContextManager;
    private final DnsCache resolveCache;
    private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
    private final DnsCnameCache cnameCache;
    private final FastThreadLocal<DnsServerAddressStream> nameServerAddrStream;
    private final long queryTimeoutMillis;
    private final int maxQueriesPerResolve;
    private final ResolvedAddressTypes resolvedAddressTypes;
    private final InternetProtocolFamily[] resolvedInternetProtocolFamilies;
    private final boolean recursionDesired;
    private final int maxPayloadSize;
    private final boolean optResourceEnabled;
    private final HostsFileEntriesResolver hostsFileEntriesResolver;
    private final DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
    private final String[] searchDomains;
    private final int ndots;
    private final boolean supportsAAAARecords;
    private final boolean supportsARecords;
    private final InternetProtocolFamily preferredAddressType;
    private final DnsRecordType[] resolveRecordTypes;
    private final boolean decodeIdn;
    private final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;
    private final boolean completeOncePreferredResolved;
    private final ChannelFactory<? extends SocketChannel> socketChannelFactory;
    
    private static boolean anyInterfaceSupportsIpV6() {
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface iface = interfaces.nextElement();
                final Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress inetAddress = addresses.nextElement();
                    if (inetAddress instanceof Inet6Address && !inetAddress.isAnyLocalAddress() && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return true;
                    }
                }
            }
        }
        catch (final SocketException e) {
            DnsNameResolver.logger.debug("Unable to detect if any interface supports IPv6, assuming IPv4-only", e);
        }
        return false;
    }
    
    private static List<String> getSearchDomainsHack() throws Exception {
        if (PlatformDependent.javaVersion() < 9) {
            final Class<?> configClass = Class.forName("sun.net.dns.ResolverConfiguration");
            final Method open = configClass.getMethod("open", (Class<?>[])new Class[0]);
            final Method nameservers = configClass.getMethod("searchlist", (Class<?>[])new Class[0]);
            final Object instance = open.invoke(null, new Object[0]);
            return (List)nameservers.invoke(instance, new Object[0]);
        }
        return Collections.emptyList();
    }
    
    @Deprecated
    public DnsNameResolver(final EventLoop eventLoop, final ChannelFactory<? extends DatagramChannel> channelFactory, final DnsCache resolveCache, final DnsCache authoritativeDnsServerCache, final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, final long queryTimeoutMillis, final ResolvedAddressTypes resolvedAddressTypes, final boolean recursionDesired, final int maxQueriesPerResolve, final boolean traceEnabled, final int maxPayloadSize, final boolean optResourceEnabled, final HostsFileEntriesResolver hostsFileEntriesResolver, final DnsServerAddressStreamProvider dnsServerAddressStreamProvider, final String[] searchDomains, final int ndots, final boolean decodeIdn) {
        this(eventLoop, channelFactory, resolveCache, new AuthoritativeDnsServerCacheAdapter(authoritativeDnsServerCache), dnsQueryLifecycleObserverFactory, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, dnsServerAddressStreamProvider, searchDomains, ndots, decodeIdn);
    }
    
    @Deprecated
    public DnsNameResolver(final EventLoop eventLoop, final ChannelFactory<? extends DatagramChannel> channelFactory, final DnsCache resolveCache, final AuthoritativeDnsServerCache authoritativeDnsServerCache, final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, final long queryTimeoutMillis, final ResolvedAddressTypes resolvedAddressTypes, final boolean recursionDesired, final int maxQueriesPerResolve, final boolean traceEnabled, final int maxPayloadSize, final boolean optResourceEnabled, final HostsFileEntriesResolver hostsFileEntriesResolver, final DnsServerAddressStreamProvider dnsServerAddressStreamProvider, final String[] searchDomains, final int ndots, final boolean decodeIdn) {
        this(eventLoop, channelFactory, null, resolveCache, NoopDnsCnameCache.INSTANCE, authoritativeDnsServerCache, dnsQueryLifecycleObserverFactory, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, dnsServerAddressStreamProvider, searchDomains, ndots, decodeIdn, false);
    }
    
    DnsNameResolver(final EventLoop eventLoop, final ChannelFactory<? extends DatagramChannel> channelFactory, final ChannelFactory<? extends SocketChannel> socketChannelFactory, final DnsCache resolveCache, final DnsCnameCache cnameCache, final AuthoritativeDnsServerCache authoritativeDnsServerCache, final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, final long queryTimeoutMillis, final ResolvedAddressTypes resolvedAddressTypes, final boolean recursionDesired, final int maxQueriesPerResolve, final boolean traceEnabled, final int maxPayloadSize, final boolean optResourceEnabled, final HostsFileEntriesResolver hostsFileEntriesResolver, final DnsServerAddressStreamProvider dnsServerAddressStreamProvider, final String[] searchDomains, final int ndots, final boolean decodeIdn, final boolean completeOncePreferredResolved) {
        this(eventLoop, channelFactory, socketChannelFactory, resolveCache, cnameCache, authoritativeDnsServerCache, null, dnsQueryLifecycleObserverFactory, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, dnsServerAddressStreamProvider, searchDomains, ndots, decodeIdn, completeOncePreferredResolved);
    }
    
    DnsNameResolver(final EventLoop eventLoop, final ChannelFactory<? extends DatagramChannel> channelFactory, final ChannelFactory<? extends SocketChannel> socketChannelFactory, final DnsCache resolveCache, final DnsCnameCache cnameCache, final AuthoritativeDnsServerCache authoritativeDnsServerCache, final SocketAddress localAddress, final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, final long queryTimeoutMillis, final ResolvedAddressTypes resolvedAddressTypes, final boolean recursionDesired, final int maxQueriesPerResolve, final boolean traceEnabled, final int maxPayloadSize, final boolean optResourceEnabled, final HostsFileEntriesResolver hostsFileEntriesResolver, final DnsServerAddressStreamProvider dnsServerAddressStreamProvider, final String[] searchDomains, final int ndots, final boolean decodeIdn, final boolean completeOncePreferredResolved) {
        super(eventLoop);
        this.queryContextManager = new DnsQueryContextManager();
        this.nameServerAddrStream = new FastThreadLocal<DnsServerAddressStream>() {
            @Override
            protected DnsServerAddressStream initialValue() {
                return DnsNameResolver.this.dnsServerAddressStreamProvider.nameServerAddressStream("");
            }
        };
        this.queryTimeoutMillis = ((queryTimeoutMillis > 0L) ? queryTimeoutMillis : TimeUnit.SECONDS.toMillis(DnsNameResolver.DEFAULT_OPTIONS.timeout()));
        this.resolvedAddressTypes = ((resolvedAddressTypes != null) ? resolvedAddressTypes : DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES);
        this.recursionDesired = recursionDesired;
        this.maxQueriesPerResolve = ((maxQueriesPerResolve > 0) ? maxQueriesPerResolve : DnsNameResolver.DEFAULT_OPTIONS.attempts());
        this.maxPayloadSize = ObjectUtil.checkPositive(maxPayloadSize, "maxPayloadSize");
        this.optResourceEnabled = optResourceEnabled;
        this.hostsFileEntriesResolver = ObjectUtil.checkNotNull(hostsFileEntriesResolver, "hostsFileEntriesResolver");
        this.dnsServerAddressStreamProvider = ObjectUtil.checkNotNull(dnsServerAddressStreamProvider, "dnsServerAddressStreamProvider");
        this.resolveCache = ObjectUtil.checkNotNull(resolveCache, "resolveCache");
        this.cnameCache = ObjectUtil.checkNotNull(cnameCache, "cnameCache");
        this.dnsQueryLifecycleObserverFactory = (traceEnabled ? ((dnsQueryLifecycleObserverFactory instanceof NoopDnsQueryLifecycleObserverFactory) ? new LoggingDnsQueryLifeCycleObserverFactory() : new BiDnsQueryLifecycleObserverFactory(new LoggingDnsQueryLifeCycleObserverFactory(), dnsQueryLifecycleObserverFactory)) : ObjectUtil.checkNotNull(dnsQueryLifecycleObserverFactory, "dnsQueryLifecycleObserverFactory"));
        this.searchDomains = ((searchDomains != null) ? searchDomains.clone() : DnsNameResolver.DEFAULT_SEARCH_DOMAINS);
        this.ndots = ((ndots >= 0) ? ndots : DnsNameResolver.DEFAULT_OPTIONS.ndots());
        this.decodeIdn = decodeIdn;
        this.completeOncePreferredResolved = completeOncePreferredResolved;
        this.socketChannelFactory = socketChannelFactory;
        switch (this.resolvedAddressTypes) {
            case IPV4_ONLY: {
                this.supportsAAAARecords = false;
                this.supportsARecords = true;
                this.resolveRecordTypes = DnsNameResolver.IPV4_ONLY_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = DnsNameResolver.IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
                break;
            }
            case IPV4_PREFERRED: {
                this.supportsAAAARecords = true;
                this.supportsARecords = true;
                this.resolveRecordTypes = DnsNameResolver.IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = DnsNameResolver.IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
                break;
            }
            case IPV6_ONLY: {
                this.supportsAAAARecords = true;
                this.supportsARecords = false;
                this.resolveRecordTypes = DnsNameResolver.IPV6_ONLY_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = DnsNameResolver.IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
                break;
            }
            case IPV6_PREFERRED: {
                this.supportsAAAARecords = true;
                this.supportsARecords = true;
                this.resolveRecordTypes = DnsNameResolver.IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = DnsNameResolver.IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + resolvedAddressTypes);
            }
        }
        this.preferredAddressType = preferredAddressType(this.resolvedAddressTypes);
        this.authoritativeDnsServerCache = ObjectUtil.checkNotNull(authoritativeDnsServerCache, "authoritativeDnsServerCache");
        this.nameServerComparator = new NameServerComparator(this.preferredAddressType.addressType());
        final Bootstrap b = new Bootstrap();
        b.group(this.executor());
        b.channelFactory(channelFactory);
        ((AbstractBootstrap<AbstractBootstrap, Channel>)b).option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, true);
        final DnsResponseHandler responseHandler = new DnsResponseHandler(this.executor().newPromise());
        b.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(final DatagramChannel ch) {
                ch.pipeline().addLast(DnsNameResolver.DATAGRAM_ENCODER, DnsNameResolver.DATAGRAM_DECODER, responseHandler);
            }
        });
        this.channelFuture = responseHandler.channelActivePromise;
        ChannelFuture future;
        if (localAddress == null) {
            future = b.register();
        }
        else {
            future = b.bind(localAddress);
        }
        final Throwable cause = future.cause();
        if (cause == null) {
            this.ch = future.channel();
            this.ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(maxPayloadSize));
            this.ch.closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) {
                    resolveCache.clear();
                    cnameCache.clear();
                    authoritativeDnsServerCache.clear();
                }
            });
            return;
        }
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new IllegalStateException("Unable to create / register Channel", cause);
    }
    
    static InternetProtocolFamily preferredAddressType(final ResolvedAddressTypes resolvedAddressTypes) {
        switch (resolvedAddressTypes) {
            case IPV4_ONLY:
            case IPV4_PREFERRED: {
                return InternetProtocolFamily.IPv4;
            }
            case IPV6_ONLY:
            case IPV6_PREFERRED: {
                return InternetProtocolFamily.IPv6;
            }
            default: {
                throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + resolvedAddressTypes);
            }
        }
    }
    
    InetSocketAddress newRedirectServerAddress(final InetAddress server) {
        return new InetSocketAddress(server, 53);
    }
    
    final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory() {
        return this.dnsQueryLifecycleObserverFactory;
    }
    
    protected DnsServerAddressStream newRedirectDnsServerStream(final String hostname, final List<InetSocketAddress> nameservers) {
        final DnsServerAddressStream cached = this.authoritativeDnsServerCache().get(hostname);
        if (cached == null || cached.size() == 0) {
            Collections.sort(nameservers, this.nameServerComparator);
            return new SequentialDnsServerAddressStream(nameservers, 0);
        }
        return cached;
    }
    
    public DnsCache resolveCache() {
        return this.resolveCache;
    }
    
    public DnsCnameCache cnameCache() {
        return this.cnameCache;
    }
    
    public AuthoritativeDnsServerCache authoritativeDnsServerCache() {
        return this.authoritativeDnsServerCache;
    }
    
    public long queryTimeoutMillis() {
        return this.queryTimeoutMillis;
    }
    
    public ResolvedAddressTypes resolvedAddressTypes() {
        return this.resolvedAddressTypes;
    }
    
    InternetProtocolFamily[] resolvedInternetProtocolFamiliesUnsafe() {
        return this.resolvedInternetProtocolFamilies;
    }
    
    final String[] searchDomains() {
        return this.searchDomains;
    }
    
    final int ndots() {
        return this.ndots;
    }
    
    final boolean supportsAAAARecords() {
        return this.supportsAAAARecords;
    }
    
    final boolean supportsARecords() {
        return this.supportsARecords;
    }
    
    final InternetProtocolFamily preferredAddressType() {
        return this.preferredAddressType;
    }
    
    final DnsRecordType[] resolveRecordTypes() {
        return this.resolveRecordTypes;
    }
    
    final boolean isDecodeIdn() {
        return this.decodeIdn;
    }
    
    public boolean isRecursionDesired() {
        return this.recursionDesired;
    }
    
    public int maxQueriesPerResolve() {
        return this.maxQueriesPerResolve;
    }
    
    public int maxPayloadSize() {
        return this.maxPayloadSize;
    }
    
    public boolean isOptResourceEnabled() {
        return this.optResourceEnabled;
    }
    
    public HostsFileEntriesResolver hostsFileEntriesResolver() {
        return this.hostsFileEntriesResolver;
    }
    
    @Override
    public void close() {
        if (this.ch.isOpen()) {
            this.ch.close();
        }
    }
    
    @Override
    protected EventLoop executor() {
        return (EventLoop)super.executor();
    }
    
    private InetAddress resolveHostsFileEntry(final String hostname) {
        if (this.hostsFileEntriesResolver == null) {
            return null;
        }
        final InetAddress address = this.hostsFileEntriesResolver.address(hostname, this.resolvedAddressTypes);
        return (address == null && isLocalWindowsHost(hostname)) ? DnsNameResolver.LOCALHOST_ADDRESS : address;
    }
    
    private List<InetAddress> resolveHostsFileEntries(final String hostname) {
        if (this.hostsFileEntriesResolver == null) {
            return null;
        }
        List<InetAddress> addresses;
        if (this.hostsFileEntriesResolver instanceof DefaultHostsFileEntriesResolver) {
            addresses = ((DefaultHostsFileEntriesResolver)this.hostsFileEntriesResolver).addresses(hostname, this.resolvedAddressTypes);
        }
        else {
            final InetAddress address = this.hostsFileEntriesResolver.address(hostname, this.resolvedAddressTypes);
            addresses = ((address != null) ? Collections.singletonList(address) : null);
        }
        return (addresses == null && isLocalWindowsHost(hostname)) ? Collections.singletonList(DnsNameResolver.LOCALHOST_ADDRESS) : addresses;
    }
    
    private static boolean isLocalWindowsHost(final String hostname) {
        return PlatformDependent.isWindows() && ("localhost".equalsIgnoreCase(hostname) || (DnsNameResolver.WINDOWS_HOST_NAME != null && DnsNameResolver.WINDOWS_HOST_NAME.equalsIgnoreCase(hostname)));
    }
    
    public final Future<InetAddress> resolve(final String inetHost, final Iterable<DnsRecord> additionals) {
        return this.resolve(inetHost, additionals, this.executor().newPromise());
    }
    
    public final Future<InetAddress> resolve(final String inetHost, final Iterable<DnsRecord> additionals, final Promise<InetAddress> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        final DnsRecord[] additionalsArray = toArray(additionals, true);
        try {
            this.doResolve(inetHost, additionalsArray, promise, this.resolveCache);
            return promise;
        }
        catch (final Exception e) {
            return promise.setFailure(e);
        }
    }
    
    public final Future<List<InetAddress>> resolveAll(final String inetHost, final Iterable<DnsRecord> additionals) {
        return this.resolveAll(inetHost, additionals, this.executor().newPromise());
    }
    
    public final Future<List<InetAddress>> resolveAll(final String inetHost, final Iterable<DnsRecord> additionals, final Promise<List<InetAddress>> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        final DnsRecord[] additionalsArray = toArray(additionals, true);
        try {
            this.doResolveAll(inetHost, additionalsArray, promise, this.resolveCache);
            return promise;
        }
        catch (final Exception e) {
            return promise.setFailure(e);
        }
    }
    
    @Override
    protected void doResolve(final String inetHost, final Promise<InetAddress> promise) throws Exception {
        this.doResolve(inetHost, DnsNameResolver.EMPTY_ADDITIONALS, promise, this.resolveCache);
    }
    
    public final Future<List<DnsRecord>> resolveAll(final DnsQuestion question) {
        return this.resolveAll(question, DnsNameResolver.EMPTY_ADDITIONALS, this.executor().newPromise());
    }
    
    public final Future<List<DnsRecord>> resolveAll(final DnsQuestion question, final Iterable<DnsRecord> additionals) {
        return this.resolveAll(question, additionals, this.executor().newPromise());
    }
    
    public final Future<List<DnsRecord>> resolveAll(final DnsQuestion question, final Iterable<DnsRecord> additionals, final Promise<List<DnsRecord>> promise) {
        final DnsRecord[] additionalsArray = toArray(additionals, true);
        return this.resolveAll(question, additionalsArray, promise);
    }
    
    private Future<List<DnsRecord>> resolveAll(final DnsQuestion question, final DnsRecord[] additionals, final Promise<List<DnsRecord>> promise) {
        ObjectUtil.checkNotNull(question, "question");
        ObjectUtil.checkNotNull(promise, "promise");
        final DnsRecordType type = question.type();
        final String hostname = question.name();
        if (type == DnsRecordType.A || type == DnsRecordType.AAAA) {
            final List<InetAddress> hostsFileEntries = this.resolveHostsFileEntries(hostname);
            if (hostsFileEntries != null) {
                final List<DnsRecord> result = new ArrayList<DnsRecord>();
                for (final InetAddress hostsFileEntry : hostsFileEntries) {
                    ByteBuf content = null;
                    if (hostsFileEntry instanceof Inet4Address) {
                        if (type == DnsRecordType.A) {
                            content = Unpooled.wrappedBuffer(hostsFileEntry.getAddress());
                        }
                    }
                    else if (hostsFileEntry instanceof Inet6Address && type == DnsRecordType.AAAA) {
                        content = Unpooled.wrappedBuffer(hostsFileEntry.getAddress());
                    }
                    if (content != null) {
                        result.add(new DefaultDnsRawRecord(hostname, type, 86400L, content));
                    }
                }
                if (!result.isEmpty()) {
                    trySuccess(promise, result);
                    return promise;
                }
            }
        }
        final DnsServerAddressStream nameServerAddrs = this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname);
        new DnsRecordResolveContext(this, promise, question, additionals, nameServerAddrs, this.maxQueriesPerResolve).resolve(promise);
        return promise;
    }
    
    private static DnsRecord[] toArray(final Iterable<DnsRecord> additionals, final boolean validateType) {
        ObjectUtil.checkNotNull(additionals, "additionals");
        if (additionals instanceof Collection) {
            final Collection<DnsRecord> records = (Collection)additionals;
            for (final DnsRecord r : additionals) {
                validateAdditional(r, validateType);
            }
            return records.toArray(new DnsRecord[records.size()]);
        }
        final Iterator<DnsRecord> additionalsIt = additionals.iterator();
        if (!additionalsIt.hasNext()) {
            return DnsNameResolver.EMPTY_ADDITIONALS;
        }
        final List<DnsRecord> records2 = new ArrayList<DnsRecord>();
        do {
            final DnsRecord r = additionalsIt.next();
            validateAdditional(r, validateType);
            records2.add(r);
        } while (additionalsIt.hasNext());
        return records2.toArray(new DnsRecord[records2.size()]);
    }
    
    private static void validateAdditional(final DnsRecord record, final boolean validateType) {
        ObjectUtil.checkNotNull(record, "record");
        if (validateType && record instanceof DnsRawRecord) {
            throw new IllegalArgumentException("DnsRawRecord implementations not allowed: " + record);
        }
    }
    
    private InetAddress loopbackAddress() {
        return this.preferredAddressType().localhost();
    }
    
    protected void doResolve(final String inetHost, final DnsRecord[] additionals, final Promise<InetAddress> promise, final DnsCache resolveCache) throws Exception {
        if (inetHost == null || inetHost.isEmpty()) {
            promise.setSuccess(this.loopbackAddress());
            return;
        }
        final byte[] bytes = NetUtil.createByteArrayFromIpAddressString(inetHost);
        if (bytes != null) {
            promise.setSuccess(InetAddress.getByAddress(bytes));
            return;
        }
        final String hostname = hostname(inetHost);
        final InetAddress hostsFileEntry = this.resolveHostsFileEntry(hostname);
        if (hostsFileEntry != null) {
            promise.setSuccess(hostsFileEntry);
            return;
        }
        if (!this.doResolveCached(hostname, additionals, promise, resolveCache)) {
            this.doResolveUncached(hostname, additionals, promise, resolveCache, true);
        }
    }
    
    private boolean doResolveCached(final String hostname, final DnsRecord[] additionals, final Promise<InetAddress> promise, final DnsCache resolveCache) {
        final List<? extends DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
        if (cachedEntries == null || cachedEntries.isEmpty()) {
            return false;
        }
        final Throwable cause = ((DnsCacheEntry)cachedEntries.get(0)).cause();
        if (cause == null) {
            final int numEntries = cachedEntries.size();
            for (final InternetProtocolFamily f : this.resolvedInternetProtocolFamilies) {
                for (int i = 0; i < numEntries; ++i) {
                    final DnsCacheEntry e = (DnsCacheEntry)cachedEntries.get(i);
                    if (f.addressType().isInstance(e.address())) {
                        trySuccess(promise, e.address());
                        return true;
                    }
                }
            }
            return false;
        }
        tryFailure(promise, cause);
        return true;
    }
    
    static <T> boolean trySuccess(final Promise<T> promise, final T result) {
        final boolean notifiedRecords = promise.trySuccess(result);
        if (!notifiedRecords) {
            DnsNameResolver.logger.trace("Failed to notify success ({}) to a promise: {}", result, promise);
        }
        return notifiedRecords;
    }
    
    private static void tryFailure(final Promise<?> promise, final Throwable cause) {
        if (!promise.tryFailure(cause)) {
            DnsNameResolver.logger.trace("Failed to notify failure to a promise: {}", promise, cause);
        }
    }
    
    private void doResolveUncached(final String hostname, final DnsRecord[] additionals, final Promise<InetAddress> promise, final DnsCache resolveCache, final boolean completeEarlyIfPossible) {
        final Promise<List<InetAddress>> allPromise = this.executor().newPromise();
        this.doResolveAllUncached(hostname, additionals, promise, allPromise, resolveCache, true);
        allPromise.addListener((GenericFutureListener<? extends Future<? super List<InetAddress>>>)new FutureListener<List<InetAddress>>() {
            @Override
            public void operationComplete(final Future<List<InetAddress>> future) {
                if (future.isSuccess()) {
                    DnsNameResolver.trySuccess(promise, future.getNow().get(0));
                }
                else {
                    tryFailure(promise, future.cause());
                }
            }
        });
    }
    
    @Override
    protected void doResolveAll(final String inetHost, final Promise<List<InetAddress>> promise) throws Exception {
        this.doResolveAll(inetHost, DnsNameResolver.EMPTY_ADDITIONALS, promise, this.resolveCache);
    }
    
    protected void doResolveAll(final String inetHost, final DnsRecord[] additionals, final Promise<List<InetAddress>> promise, final DnsCache resolveCache) throws Exception {
        if (inetHost == null || inetHost.isEmpty()) {
            promise.setSuccess(Collections.singletonList(this.loopbackAddress()));
            return;
        }
        final byte[] bytes = NetUtil.createByteArrayFromIpAddressString(inetHost);
        if (bytes != null) {
            promise.setSuccess(Collections.singletonList(InetAddress.getByAddress(bytes)));
            return;
        }
        final String hostname = hostname(inetHost);
        final List<InetAddress> hostsFileEntries = this.resolveHostsFileEntries(hostname);
        if (hostsFileEntries != null) {
            promise.setSuccess(hostsFileEntries);
            return;
        }
        if (!doResolveAllCached(hostname, additionals, promise, resolveCache, this.resolvedInternetProtocolFamilies)) {
            this.doResolveAllUncached(hostname, additionals, promise, promise, resolveCache, this.completeOncePreferredResolved);
        }
    }
    
    static boolean doResolveAllCached(final String hostname, final DnsRecord[] additionals, final Promise<List<InetAddress>> promise, final DnsCache resolveCache, final InternetProtocolFamily[] resolvedInternetProtocolFamilies) {
        final List<? extends DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
        if (cachedEntries == null || cachedEntries.isEmpty()) {
            return false;
        }
        final Throwable cause = ((DnsCacheEntry)cachedEntries.get(0)).cause();
        if (cause != null) {
            tryFailure(promise, cause);
            return true;
        }
        List<InetAddress> result = null;
        final int numEntries = cachedEntries.size();
        for (final InternetProtocolFamily f : resolvedInternetProtocolFamilies) {
            for (int i = 0; i < numEntries; ++i) {
                final DnsCacheEntry e = (DnsCacheEntry)cachedEntries.get(i);
                if (f.addressType().isInstance(e.address())) {
                    if (result == null) {
                        result = new ArrayList<InetAddress>(numEntries);
                    }
                    result.add(e.address());
                }
            }
        }
        if (result != null) {
            trySuccess(promise, result);
            return true;
        }
        return false;
    }
    
    private void doResolveAllUncached(final String hostname, final DnsRecord[] additionals, final Promise<?> originalPromise, final Promise<List<InetAddress>> promise, final DnsCache resolveCache, final boolean completeEarlyIfPossible) {
        final EventExecutor executor = this.executor();
        if (executor.inEventLoop()) {
            this.doResolveAllUncached0(hostname, additionals, originalPromise, promise, resolveCache, completeEarlyIfPossible);
        }
        else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DnsNameResolver.this.doResolveAllUncached0(hostname, additionals, originalPromise, promise, resolveCache, completeEarlyIfPossible);
                }
            });
        }
    }
    
    private void doResolveAllUncached0(final String hostname, final DnsRecord[] additionals, final Promise<?> originalPromise, final Promise<List<InetAddress>> promise, final DnsCache resolveCache, final boolean completeEarlyIfPossible) {
        assert this.executor().inEventLoop();
        final DnsServerAddressStream nameServerAddrs = this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname);
        new DnsAddressResolveContext(this, originalPromise, hostname, additionals, nameServerAddrs, this.maxQueriesPerResolve, resolveCache, this.authoritativeDnsServerCache, completeEarlyIfPossible).resolve(promise);
    }
    
    private static String hostname(final String inetHost) {
        String hostname = IDN.toASCII(inetHost);
        if (StringUtil.endsWith(inetHost, '.') && !StringUtil.endsWith(hostname, '.')) {
            hostname += ".";
        }
        return hostname;
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final DnsQuestion question) {
        return this.query(this.nextNameServerAddress(), question);
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final DnsQuestion question, final Iterable<DnsRecord> additionals) {
        return this.query(this.nextNameServerAddress(), question, additionals);
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final DnsQuestion question, final Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query(this.nextNameServerAddress(), question, (Iterable<DnsRecord>)Collections.emptyList(), promise);
    }
    
    private InetSocketAddress nextNameServerAddress() {
        return this.nameServerAddrStream.get().next();
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final InetSocketAddress nameServerAddr, final DnsQuestion question) {
        return this.query0(nameServerAddr, question, DnsNameResolver.EMPTY_ADDITIONALS, true, this.ch.newPromise(), this.ch.eventLoop().newPromise());
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final InetSocketAddress nameServerAddr, final DnsQuestion question, final Iterable<DnsRecord> additionals) {
        return this.query0(nameServerAddr, question, toArray(additionals, false), true, this.ch.newPromise(), this.ch.eventLoop().newPromise());
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final InetSocketAddress nameServerAddr, final DnsQuestion question, final Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query0(nameServerAddr, question, DnsNameResolver.EMPTY_ADDITIONALS, true, this.ch.newPromise(), promise);
    }
    
    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(final InetSocketAddress nameServerAddr, final DnsQuestion question, final Iterable<DnsRecord> additionals, final Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query0(nameServerAddr, question, toArray(additionals, false), true, this.ch.newPromise(), promise);
    }
    
    public static boolean isTransportOrTimeoutError(final Throwable cause) {
        return cause != null && cause.getCause() instanceof DnsNameResolverException;
    }
    
    public static boolean isTimeoutError(final Throwable cause) {
        return cause != null && cause.getCause() instanceof DnsNameResolverTimeoutException;
    }
    
    final void flushQueries() {
        this.ch.flush();
    }
    
    final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(final InetSocketAddress nameServerAddr, final DnsQuestion question, final DnsRecord[] additionals, final boolean flush, final ChannelPromise writePromise, final Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        assert !writePromise.isVoid();
        final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> castPromise = cast(ObjectUtil.checkNotNull(promise, "promise"));
        try {
            new DatagramDnsQueryContext(this, nameServerAddr, question, additionals, castPromise).query(flush, writePromise);
            return castPromise;
        }
        catch (final Exception e) {
            return castPromise.setFailure(e);
        }
    }
    
    private static Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> cast(final Promise<?> promise) {
        return (Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>>)promise;
    }
    
    final DnsServerAddressStream newNameServerAddressStream(final String hostname) {
        return this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);
        EMPTY_ADDITIONALS = new DnsRecord[0];
        IPV4_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.A };
        IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv4 };
        IPV4_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.A, DnsRecordType.AAAA };
        IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv4, InternetProtocolFamily.IPv6 };
        IPV6_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.AAAA };
        IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv6 };
        IPV6_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.AAAA, DnsRecordType.A };
        IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv6, InternetProtocolFamily.IPv4 };
        if (NetUtil.isIpV4StackPreferred() || !anyInterfaceSupportsIpV6()) {
            DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_ONLY;
            LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
        }
        else if (NetUtil.isIpV6AddressesPreferred()) {
            DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV6_PREFERRED;
            LOCALHOST_ADDRESS = NetUtil.LOCALHOST6;
        }
        else {
            DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_PREFERRED;
            LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
        }
        String hostName;
        try {
            hostName = (PlatformDependent.isWindows() ? InetAddress.getLocalHost().getHostName() : null);
        }
        catch (final Exception ignore) {
            hostName = null;
        }
        WINDOWS_HOST_NAME = hostName;
        String[] searchDomains;
        try {
            final List<String> list = PlatformDependent.isWindows() ? getSearchDomainsHack() : UnixResolverDnsServerAddressStreamProvider.parseEtcResolverSearchDomains();
            searchDomains = list.toArray(new String[0]);
        }
        catch (final Exception ignore) {
            searchDomains = EmptyArrays.EMPTY_STRINGS;
        }
        DEFAULT_SEARCH_DOMAINS = searchDomains;
        UnixResolverOptions options;
        try {
            options = UnixResolverDnsServerAddressStreamProvider.parseEtcResolverOptions();
        }
        catch (final Exception ignore2) {
            options = UnixResolverOptions.newBuilder().build();
        }
        DEFAULT_OPTIONS = options;
        DATAGRAM_DECODER = new DatagramDnsResponseDecoder() {
            @Override
            protected DnsResponse decodeResponse(final ChannelHandlerContext ctx, final DatagramPacket packet) throws Exception {
                final DnsResponse response = super.decodeResponse(ctx, packet);
                if (((DefaultAddressedEnvelope<ByteBuf, A>)packet).content().isReadable()) {
                    response.setTruncated(true);
                    if (DnsNameResolver.logger.isDebugEnabled()) {
                        DnsNameResolver.logger.debug("{} RECEIVED: UDP truncated packet received, consider adjusting maxPayloadSize for the {}.", ctx.channel(), StringUtil.simpleClassName(DnsNameResolver.class));
                    }
                }
                return response;
            }
        };
        DATAGRAM_ENCODER = new DatagramDnsQueryEncoder();
        TCP_ENCODER = new TcpDnsQueryEncoder();
    }
    
    private final class DnsResponseHandler extends ChannelInboundHandlerAdapter
    {
        private final Promise<Channel> channelActivePromise;
        
        DnsResponseHandler(final Promise<Channel> channelActivePromise) {
            this.channelActivePromise = channelActivePromise;
        }
        
        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
            final DatagramDnsResponse res = (DatagramDnsResponse)msg;
            final int queryId = res.id();
            if (DnsNameResolver.logger.isDebugEnabled()) {
                DnsNameResolver.logger.debug("{} RECEIVED: UDP [{}: {}], {}", DnsNameResolver.this.ch, queryId, res.sender(), res);
            }
            final DnsQueryContext qCtx = DnsNameResolver.this.queryContextManager.get(res.sender(), queryId);
            if (qCtx == null) {
                DnsNameResolver.logger.debug("Received a DNS response with an unknown ID: UDP [{}: {}]", DnsNameResolver.this.ch, queryId);
                res.release();
                return;
            }
            if (!res.isTruncated() || DnsNameResolver.this.socketChannelFactory == null) {
                qCtx.finish(res);
                return;
            }
            final Bootstrap bs = new Bootstrap();
            ((AbstractBootstrap<Bootstrap, C>)((AbstractBootstrap<Bootstrap, C>)bs).option(ChannelOption.SO_REUSEADDR, true)).group(DnsNameResolver.this.executor()).channelFactory(DnsNameResolver.this.socketChannelFactory).handler(DnsNameResolver.TCP_ENCODER);
            bs.connect(res.sender()).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) {
                    if (!future.isSuccess()) {
                        if (DnsNameResolver.logger.isDebugEnabled()) {
                            DnsNameResolver.logger.debug("Unable to fallback to TCP [{}]", (Object)queryId, future.cause());
                        }
                        qCtx.finish(res);
                        return;
                    }
                    final Channel channel = future.channel();
                    final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise = channel.eventLoop().newPromise();
                    final TcpDnsQueryContext tcpCtx = new TcpDnsQueryContext(DnsNameResolver.this, channel, (InetSocketAddress)channel.remoteAddress(), qCtx.question(), DnsNameResolver.EMPTY_ADDITIONALS, promise);
                    channel.pipeline().addLast(new TcpDnsResponseDecoder());
                    channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
                            final Channel channel = ctx.channel();
                            final DnsResponse response = (DnsResponse)msg;
                            final int queryId = response.id();
                            if (DnsNameResolver.logger.isDebugEnabled()) {
                                DnsNameResolver.logger.debug("{} RECEIVED: TCP [{}: {}], {}", channel, queryId, channel.remoteAddress(), response);
                            }
                            final DnsQueryContext foundCtx = DnsNameResolver.this.queryContextManager.get(res.sender(), queryId);
                            if (foundCtx == tcpCtx) {
                                tcpCtx.finish(new AddressedEnvelopeAdapter((InetSocketAddress)ctx.channel().remoteAddress(), (InetSocketAddress)ctx.channel().localAddress(), response));
                            }
                            else {
                                response.release();
                                tcpCtx.tryFailure("Received TCP DNS response with unexpected ID", null, false);
                                DnsNameResolver.logger.debug("Received a DNS response with an unexpected ID: TCP [{}: {}]", channel, queryId);
                            }
                        }
                        
                        @Override
                        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
                            if (tcpCtx.tryFailure("TCP fallback error", cause, false) && DnsNameResolver.logger.isDebugEnabled()) {
                                DnsNameResolver.logger.debug("{} Error during processing response: TCP [{}: {}]", ctx.channel(), queryId, ctx.channel().remoteAddress(), cause);
                            }
                        }
                    });
                    promise.addListener((GenericFutureListener<? extends Future<? super AddressedEnvelope<DnsResponse, InetSocketAddress>>>)new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
                        @Override
                        public void operationComplete(final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
                            channel.close();
                            if (future.isSuccess()) {
                                qCtx.finish(future.getNow());
                                res.release();
                            }
                            else {
                                qCtx.finish(res);
                            }
                        }
                    });
                    tcpCtx.query(true, future.channel().newPromise());
                }
            });
        }
        
        @Override
        public void channelActive(final ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            this.channelActivePromise.setSuccess(ctx.channel());
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
            if (cause instanceof CorruptedFrameException) {
                DnsNameResolver.logger.debug("Unable to decode DNS response: UDP [{}]", ctx.channel(), cause);
            }
            else {
                DnsNameResolver.logger.warn("Unexpected exception: UDP [{}]", ctx.channel(), cause);
            }
        }
    }
    
    private static final class AddressedEnvelopeAdapter implements AddressedEnvelope<DnsResponse, InetSocketAddress>
    {
        private final InetSocketAddress sender;
        private final InetSocketAddress recipient;
        private final DnsResponse response;
        
        AddressedEnvelopeAdapter(final InetSocketAddress sender, final InetSocketAddress recipient, final DnsResponse response) {
            this.sender = sender;
            this.recipient = recipient;
            this.response = response;
        }
        
        @Override
        public DnsResponse content() {
            return this.response;
        }
        
        @Override
        public InetSocketAddress sender() {
            return this.sender;
        }
        
        @Override
        public InetSocketAddress recipient() {
            return this.recipient;
        }
        
        @Override
        public AddressedEnvelope<DnsResponse, InetSocketAddress> retain() {
            this.response.retain();
            return this;
        }
        
        @Override
        public AddressedEnvelope<DnsResponse, InetSocketAddress> retain(final int increment) {
            this.response.retain(increment);
            return this;
        }
        
        @Override
        public AddressedEnvelope<DnsResponse, InetSocketAddress> touch() {
            this.response.touch();
            return this;
        }
        
        @Override
        public AddressedEnvelope<DnsResponse, InetSocketAddress> touch(final Object hint) {
            this.response.touch(hint);
            return this;
        }
        
        @Override
        public int refCnt() {
            return this.response.refCnt();
        }
        
        @Override
        public boolean release() {
            return this.response.release();
        }
        
        @Override
        public boolean release(final int decrement) {
            return this.response.release(decrement);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AddressedEnvelope)) {
                return false;
            }
            final AddressedEnvelope<?, SocketAddress> that = (AddressedEnvelope<?, SocketAddress>)obj;
            if (this.sender() == null) {
                if (that.sender() != null) {
                    return false;
                }
            }
            else if (!this.sender().equals(that.sender())) {
                return false;
            }
            if (this.recipient() == null) {
                if (that.recipient() != null) {
                    return false;
                }
            }
            else if (!this.recipient().equals(that.recipient())) {
                return false;
            }
            return this.response.equals(obj);
        }
        
        @Override
        public int hashCode() {
            int hashCode = this.response.hashCode();
            if (this.sender() != null) {
                hashCode = hashCode * 31 + this.sender().hashCode();
            }
            if (this.recipient() != null) {
                hashCode = hashCode * 31 + this.recipient().hashCode();
            }
            return hashCode;
        }
    }
}
