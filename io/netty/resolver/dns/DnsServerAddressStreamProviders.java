package io.netty.resolver.dns;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import io.netty.util.internal.logging.InternalLogger;

public final class DnsServerAddressStreamProviders
{
    private static final InternalLogger LOGGER;
    private static final Constructor<? extends DnsServerAddressStreamProvider> STREAM_PROVIDER_CONSTRUCTOR;
    private static final String MACOS_PROVIDER_CLASS_NAME = "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider";
    
    private DnsServerAddressStreamProviders() {
    }
    
    public static DnsServerAddressStreamProvider platformDefault() {
        if (DnsServerAddressStreamProviders.STREAM_PROVIDER_CONSTRUCTOR != null) {
            try {
                return (DnsServerAddressStreamProvider)DnsServerAddressStreamProviders.STREAM_PROVIDER_CONSTRUCTOR.newInstance(new Object[0]);
            }
            catch (final IllegalAccessException ex) {}
            catch (final InstantiationException ex2) {}
            catch (final InvocationTargetException ex3) {}
        }
        return unixDefault();
    }
    
    public static DnsServerAddressStreamProvider unixDefault() {
        return DefaultProviderHolder.DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER;
    }
    
    static {
        LOGGER = InternalLoggerFactory.getInstance(DnsServerAddressStreamProviders.class);
        Constructor<? extends DnsServerAddressStreamProvider> constructor = null;
        if (PlatformDependent.isOsx()) {
            try {
                final Object maybeProvider = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            return Class.forName("io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider", true, DnsServerAddressStreamProviders.class.getClassLoader());
                        }
                        catch (final Throwable cause) {
                            return cause;
                        }
                    }
                });
                if (!(maybeProvider instanceof Class)) {
                    throw (Throwable)maybeProvider;
                }
                final Class<? extends DnsServerAddressStreamProvider> providerClass = (Class<? extends DnsServerAddressStreamProvider>)maybeProvider;
                constructor = providerClass.getConstructor((Class<?>[])new Class[0]);
                constructor.newInstance(new Object[0]);
                DnsServerAddressStreamProviders.LOGGER.debug("{}: available", "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider");
            }
            catch (final ClassNotFoundException cause) {
                DnsServerAddressStreamProviders.LOGGER.warn("Can not find {} in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS.", "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider");
            }
            catch (final Throwable cause2) {
                DnsServerAddressStreamProviders.LOGGER.error("Unable to load {}, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS.", "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider", cause2);
                constructor = null;
            }
        }
        STREAM_PROVIDER_CONSTRUCTOR = constructor;
    }
    
    private static final class DefaultProviderHolder
    {
        private static final long REFRESH_INTERVAL;
        static final DnsServerAddressStreamProvider DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER;
        
        static {
            REFRESH_INTERVAL = TimeUnit.MINUTES.toNanos(5L);
            DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER = new DnsServerAddressStreamProvider() {
                private volatile DnsServerAddressStreamProvider currentProvider = this.provider();
                private final AtomicLong lastRefresh = new AtomicLong(System.nanoTime());
                
                @Override
                public DnsServerAddressStream nameServerAddressStream(final String hostname) {
                    final long last = this.lastRefresh.get();
                    DnsServerAddressStreamProvider current = this.currentProvider;
                    if (System.nanoTime() - last > DefaultProviderHolder.REFRESH_INTERVAL && this.lastRefresh.compareAndSet(last, System.nanoTime())) {
                        final DnsServerAddressStreamProvider provider = this.provider();
                        this.currentProvider = provider;
                        current = provider;
                    }
                    return current.nameServerAddressStream(hostname);
                }
                
                private DnsServerAddressStreamProvider provider() {
                    return PlatformDependent.isWindows() ? DefaultDnsServerAddressStreamProvider.INSTANCE : UnixResolverDnsServerAddressStreamProvider.parseSilently();
                }
            };
        }
    }
}
