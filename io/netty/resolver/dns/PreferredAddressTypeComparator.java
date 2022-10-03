package io.netty.resolver.dns;

import java.net.Inet6Address;
import java.net.Inet4Address;
import io.netty.channel.socket.InternetProtocolFamily;
import java.net.InetAddress;
import java.util.Comparator;

final class PreferredAddressTypeComparator implements Comparator<InetAddress>
{
    private static final PreferredAddressTypeComparator IPv4;
    private static final PreferredAddressTypeComparator IPv6;
    private final Class<? extends InetAddress> preferredAddressType;
    
    static PreferredAddressTypeComparator comparator(final InternetProtocolFamily family) {
        switch (family) {
            case IPv4: {
                return PreferredAddressTypeComparator.IPv4;
            }
            case IPv6: {
                return PreferredAddressTypeComparator.IPv6;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private PreferredAddressTypeComparator(final Class<? extends InetAddress> preferredAddressType) {
        this.preferredAddressType = preferredAddressType;
    }
    
    @Override
    public int compare(final InetAddress o1, final InetAddress o2) {
        if (o1.getClass() == o2.getClass()) {
            return 0;
        }
        return this.preferredAddressType.isAssignableFrom(o1.getClass()) ? -1 : 1;
    }
    
    static {
        IPv4 = new PreferredAddressTypeComparator(Inet4Address.class);
        IPv6 = new PreferredAddressTypeComparator(Inet6Address.class);
    }
}
