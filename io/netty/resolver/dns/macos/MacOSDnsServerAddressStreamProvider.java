package io.netty.resolver.dns.macos;

import io.netty.util.internal.ClassInitializerUtil;
import java.util.concurrent.TimeUnit;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.resolver.dns.DnsServerAddressStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicLong;
import io.netty.resolver.dns.DnsServerAddresses;
import java.util.Map;
import io.netty.util.internal.logging.InternalLogger;
import java.util.Comparator;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;

public final class MacOSDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider
{
    private static final Comparator<DnsResolver> RESOLVER_COMPARATOR;
    private static final Throwable UNAVAILABILITY_CAUSE;
    private static final InternalLogger logger;
    private static final long REFRESH_INTERVAL;
    private volatile Map<String, DnsServerAddresses> currentMappings;
    private final AtomicLong lastRefresh;
    
    private static void loadNativeLibrary() {
        if (!PlatformDependent.isOsx()) {
            throw new IllegalStateException("Only supported on MacOS/OSX");
        }
        final String staticLibName = "netty_resolver_dns_native_macos";
        final String sharedLibName = staticLibName + '_' + PlatformDependent.normalizedArch();
        final ClassLoader cl = PlatformDependent.getClassLoader(MacOSDnsServerAddressStreamProvider.class);
        try {
            NativeLibraryLoader.load(sharedLibName, cl);
        }
        catch (final UnsatisfiedLinkError e1) {
            try {
                NativeLibraryLoader.load(staticLibName, cl);
                MacOSDnsServerAddressStreamProvider.logger.debug("Failed to load {}", sharedLibName, e1);
            }
            catch (final UnsatisfiedLinkError e2) {
                ThrowableUtil.addSuppressed(e1, e2);
                throw e1;
            }
        }
    }
    
    public static boolean isAvailable() {
        return MacOSDnsServerAddressStreamProvider.UNAVAILABILITY_CAUSE == null;
    }
    
    public static void ensureAvailability() {
        if (MacOSDnsServerAddressStreamProvider.UNAVAILABILITY_CAUSE != null) {
            throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(MacOSDnsServerAddressStreamProvider.UNAVAILABILITY_CAUSE);
        }
    }
    
    public static Throwable unavailabilityCause() {
        return MacOSDnsServerAddressStreamProvider.UNAVAILABILITY_CAUSE;
    }
    
    public MacOSDnsServerAddressStreamProvider() {
        this.currentMappings = retrieveCurrentMappings();
        this.lastRefresh = new AtomicLong(System.nanoTime());
        ensureAvailability();
    }
    
    private static Map<String, DnsServerAddresses> retrieveCurrentMappings() {
        final DnsResolver[] resolvers = resolvers();
        if (resolvers == null || resolvers.length == 0) {
            return Collections.emptyMap();
        }
        Arrays.sort(resolvers, MacOSDnsServerAddressStreamProvider.RESOLVER_COMPARATOR);
        final Map<String, DnsServerAddresses> resolverMap = new HashMap<String, DnsServerAddresses>(resolvers.length);
        for (final DnsResolver resolver : resolvers) {
            if (!"mdns".equalsIgnoreCase(resolver.options())) {
                final InetSocketAddress[] nameservers = resolver.nameservers();
                if (nameservers != null) {
                    if (nameservers.length != 0) {
                        String domain = resolver.domain();
                        if (domain == null) {
                            domain = "";
                        }
                        final InetSocketAddress[] servers = resolver.nameservers();
                        for (int a = 0; a < servers.length; ++a) {
                            final InetSocketAddress address = servers[a];
                            if (address.getPort() == 0) {
                                int port = resolver.port();
                                if (port == 0) {
                                    port = 53;
                                }
                                servers[a] = new InetSocketAddress(address.getAddress(), port);
                            }
                        }
                        resolverMap.put(domain, DnsServerAddresses.sequential(servers));
                    }
                }
            }
        }
        return resolverMap;
    }
    
    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        final long last = this.lastRefresh.get();
        Map<String, DnsServerAddresses> resolverMap = this.currentMappings;
        if (System.nanoTime() - last > MacOSDnsServerAddressStreamProvider.REFRESH_INTERVAL && this.lastRefresh.compareAndSet(last, System.nanoTime())) {
            final Map<String, DnsServerAddresses> retrieveCurrentMappings = retrieveCurrentMappings();
            this.currentMappings = retrieveCurrentMappings;
            resolverMap = retrieveCurrentMappings;
        }
        final String originalHostname = hostname;
        while (true) {
            final int i = hostname.indexOf(46, 1);
            if (i < 0 || i == hostname.length() - 1) {
                final DnsServerAddresses addresses = resolverMap.get("");
                if (addresses != null) {
                    return addresses.stream();
                }
                return DnsServerAddressStreamProviders.unixDefault().nameServerAddressStream(originalHostname);
            }
            else {
                final DnsServerAddresses addresses = resolverMap.get(hostname);
                if (addresses != null) {
                    return addresses.stream();
                }
                hostname = hostname.substring(i + 1);
            }
        }
    }
    
    private static native DnsResolver[] resolvers();
    
    static {
        RESOLVER_COMPARATOR = new Comparator<DnsResolver>() {
            @Override
            public int compare(final DnsResolver r1, final DnsResolver r2) {
                return (r1.searchOrder() < r2.searchOrder()) ? 1 : ((r1.searchOrder() == r2.searchOrder()) ? 0 : -1);
            }
        };
        logger = InternalLoggerFactory.getInstance(MacOSDnsServerAddressStreamProvider.class);
        REFRESH_INTERVAL = TimeUnit.SECONDS.toNanos(10L);
        ClassInitializerUtil.tryLoadClasses(MacOSDnsServerAddressStreamProvider.class, byte[].class, String.class);
        Throwable cause = null;
        try {
            loadNativeLibrary();
        }
        catch (final Throwable error) {
            cause = error;
        }
        UNAVAILABILITY_CAUSE = cause;
    }
}
