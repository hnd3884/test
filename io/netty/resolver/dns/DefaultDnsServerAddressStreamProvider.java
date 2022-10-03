package io.netty.resolver.dns;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.net.Inet6Address;
import io.netty.util.NetUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayList;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.List;
import io.netty.util.internal.logging.InternalLogger;

public final class DefaultDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider
{
    private static final InternalLogger logger;
    public static final DefaultDnsServerAddressStreamProvider INSTANCE;
    private static final List<InetSocketAddress> DEFAULT_NAME_SERVER_LIST;
    private static final DnsServerAddresses DEFAULT_NAME_SERVERS;
    static final int DNS_PORT = 53;
    
    private DefaultDnsServerAddressStreamProvider() {
    }
    
    @Override
    public DnsServerAddressStream nameServerAddressStream(final String hostname) {
        return DefaultDnsServerAddressStreamProvider.DEFAULT_NAME_SERVERS.stream();
    }
    
    public static List<InetSocketAddress> defaultAddressList() {
        return DefaultDnsServerAddressStreamProvider.DEFAULT_NAME_SERVER_LIST;
    }
    
    public static DnsServerAddresses defaultAddresses() {
        return DefaultDnsServerAddressStreamProvider.DEFAULT_NAME_SERVERS;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultDnsServerAddressStreamProvider.class);
        INSTANCE = new DefaultDnsServerAddressStreamProvider();
        final List<InetSocketAddress> defaultNameServers = new ArrayList<InetSocketAddress>(2);
        if (!PlatformDependent.isAndroid()) {
            DirContextUtils.addNameServers(defaultNameServers, 53);
        }
        if (PlatformDependent.javaVersion() < 9 && defaultNameServers.isEmpty()) {
            try {
                final Class<?> configClass = Class.forName("sun.net.dns.ResolverConfiguration");
                final Method open = configClass.getMethod("open", (Class<?>[])new Class[0]);
                final Method nameservers = configClass.getMethod("nameservers", (Class<?>[])new Class[0]);
                final Object instance = open.invoke(null, new Object[0]);
                final List<String> list = (List<String>)nameservers.invoke(instance, new Object[0]);
                for (final String a : list) {
                    if (a != null) {
                        defaultNameServers.add(new InetSocketAddress(SocketUtils.addressByName(a), 53));
                    }
                }
            }
            catch (final Exception ex) {}
        }
        if (!defaultNameServers.isEmpty()) {
            if (DefaultDnsServerAddressStreamProvider.logger.isDebugEnabled()) {
                DefaultDnsServerAddressStreamProvider.logger.debug("Default DNS servers: {} (sun.net.dns.ResolverConfiguration)", defaultNameServers);
            }
        }
        else {
            if (NetUtil.isIpV6AddressesPreferred() || (NetUtil.LOCALHOST instanceof Inet6Address && !NetUtil.isIpV4StackPreferred())) {
                Collections.addAll(defaultNameServers, new InetSocketAddress[] { SocketUtils.socketAddress("2001:4860:4860::8888", 53), SocketUtils.socketAddress("2001:4860:4860::8844", 53) });
            }
            else {
                Collections.addAll(defaultNameServers, new InetSocketAddress[] { SocketUtils.socketAddress("8.8.8.8", 53), SocketUtils.socketAddress("8.8.4.4", 53) });
            }
            if (DefaultDnsServerAddressStreamProvider.logger.isWarnEnabled()) {
                DefaultDnsServerAddressStreamProvider.logger.warn("Default DNS servers: {} (Google Public DNS as a fallback)", defaultNameServers);
            }
        }
        DEFAULT_NAME_SERVER_LIST = Collections.unmodifiableList((List<? extends InetSocketAddress>)defaultNameServers);
        DEFAULT_NAME_SERVERS = DnsServerAddresses.sequential(DefaultDnsServerAddressStreamProvider.DEFAULT_NAME_SERVER_LIST);
    }
}
