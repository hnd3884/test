package io.netty.resolver;

import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Locale;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public final class DefaultHostsFileEntriesResolver implements HostsFileEntriesResolver
{
    private final Map<String, List<InetAddress>> inet4Entries;
    private final Map<String, List<InetAddress>> inet6Entries;
    
    public DefaultHostsFileEntriesResolver() {
        this(parseEntries());
    }
    
    DefaultHostsFileEntriesResolver(final HostsFileEntriesProvider entries) {
        this.inet4Entries = entries.ipv4Entries();
        this.inet6Entries = entries.ipv6Entries();
    }
    
    @Override
    public InetAddress address(final String inetHost, final ResolvedAddressTypes resolvedAddressTypes) {
        final String normalized = this.normalize(inetHost);
        switch (resolvedAddressTypes) {
            case IPV4_ONLY: {
                return firstAddress(this.inet4Entries.get(normalized));
            }
            case IPV6_ONLY: {
                return firstAddress(this.inet6Entries.get(normalized));
            }
            case IPV4_PREFERRED: {
                final InetAddress inet4Address = firstAddress(this.inet4Entries.get(normalized));
                return (inet4Address != null) ? inet4Address : firstAddress(this.inet6Entries.get(normalized));
            }
            case IPV6_PREFERRED: {
                final InetAddress inet6Address = firstAddress(this.inet6Entries.get(normalized));
                return (inet6Address != null) ? inet6Address : firstAddress(this.inet4Entries.get(normalized));
            }
            default: {
                throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + resolvedAddressTypes);
            }
        }
    }
    
    public List<InetAddress> addresses(final String inetHost, final ResolvedAddressTypes resolvedAddressTypes) {
        final String normalized = this.normalize(inetHost);
        switch (resolvedAddressTypes) {
            case IPV4_ONLY: {
                return this.inet4Entries.get(normalized);
            }
            case IPV6_ONLY: {
                return this.inet6Entries.get(normalized);
            }
            case IPV4_PREFERRED: {
                final List<InetAddress> allInet4Addresses = this.inet4Entries.get(normalized);
                return (allInet4Addresses != null) ? allAddresses(allInet4Addresses, this.inet6Entries.get(normalized)) : this.inet6Entries.get(normalized);
            }
            case IPV6_PREFERRED: {
                final List<InetAddress> allInet6Addresses = this.inet6Entries.get(normalized);
                return (allInet6Addresses != null) ? allAddresses(allInet6Addresses, this.inet4Entries.get(normalized)) : this.inet4Entries.get(normalized);
            }
            default: {
                throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + resolvedAddressTypes);
            }
        }
    }
    
    String normalize(final String inetHost) {
        return inetHost.toLowerCase(Locale.ENGLISH);
    }
    
    private static List<InetAddress> allAddresses(final List<InetAddress> a, final List<InetAddress> b) {
        final List<InetAddress> result = new ArrayList<InetAddress>(a.size() + ((b == null) ? 0 : b.size()));
        result.addAll(a);
        if (b != null) {
            result.addAll(b);
        }
        return result;
    }
    
    private static InetAddress firstAddress(final List<InetAddress> addresses) {
        return (addresses != null && !addresses.isEmpty()) ? addresses.get(0) : null;
    }
    
    private static HostsFileEntriesProvider parseEntries() {
        if (PlatformDependent.isWindows()) {
            return HostsFileEntriesProvider.parser().parseSilently(Charset.defaultCharset(), CharsetUtil.UTF_16, CharsetUtil.UTF_8);
        }
        return HostsFileEntriesProvider.parser().parseSilently();
    }
}
