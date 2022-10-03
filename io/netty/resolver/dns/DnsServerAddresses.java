package io.netty.resolver.dns;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

public abstract class DnsServerAddresses
{
    @Deprecated
    public static List<InetSocketAddress> defaultAddressList() {
        return DefaultDnsServerAddressStreamProvider.defaultAddressList();
    }
    
    @Deprecated
    public static DnsServerAddresses defaultAddresses() {
        return DefaultDnsServerAddressStreamProvider.defaultAddresses();
    }
    
    public static DnsServerAddresses sequential(final Iterable<? extends InetSocketAddress> addresses) {
        return sequential0(sanitize(addresses));
    }
    
    public static DnsServerAddresses sequential(final InetSocketAddress... addresses) {
        return sequential0(sanitize(addresses));
    }
    
    private static DnsServerAddresses sequential0(final List<InetSocketAddress> addresses) {
        if (addresses.size() == 1) {
            return singleton(addresses.get(0));
        }
        return new DefaultDnsServerAddresses("sequential", addresses) {
            @Override
            public DnsServerAddressStream stream() {
                return new SequentialDnsServerAddressStream(this.addresses, 0);
            }
        };
    }
    
    public static DnsServerAddresses shuffled(final Iterable<? extends InetSocketAddress> addresses) {
        return shuffled0(sanitize(addresses));
    }
    
    public static DnsServerAddresses shuffled(final InetSocketAddress... addresses) {
        return shuffled0(sanitize(addresses));
    }
    
    private static DnsServerAddresses shuffled0(final List<InetSocketAddress> addresses) {
        if (addresses.size() == 1) {
            return singleton(addresses.get(0));
        }
        return new DefaultDnsServerAddresses("shuffled", addresses) {
            @Override
            public DnsServerAddressStream stream() {
                return new ShuffledDnsServerAddressStream(this.addresses);
            }
        };
    }
    
    public static DnsServerAddresses rotational(final Iterable<? extends InetSocketAddress> addresses) {
        return rotational0(sanitize(addresses));
    }
    
    public static DnsServerAddresses rotational(final InetSocketAddress... addresses) {
        return rotational0(sanitize(addresses));
    }
    
    private static DnsServerAddresses rotational0(final List<InetSocketAddress> addresses) {
        if (addresses.size() == 1) {
            return singleton(addresses.get(0));
        }
        return new RotationalDnsServerAddresses(addresses);
    }
    
    public static DnsServerAddresses singleton(final InetSocketAddress address) {
        ObjectUtil.checkNotNull(address, "address");
        if (address.isUnresolved()) {
            throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + address);
        }
        return new SingletonDnsServerAddresses(address);
    }
    
    private static List<InetSocketAddress> sanitize(final Iterable<? extends InetSocketAddress> addresses) {
        ObjectUtil.checkNotNull(addresses, "addresses");
        List<InetSocketAddress> list;
        if (addresses instanceof Collection) {
            list = new ArrayList<InetSocketAddress>(((Collection)addresses).size());
        }
        else {
            list = new ArrayList<InetSocketAddress>(4);
        }
        for (final InetSocketAddress a : addresses) {
            if (a == null) {
                break;
            }
            if (a.isUnresolved()) {
                throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + a);
            }
            list.add(a);
        }
        return ObjectUtil.checkNonEmpty(list, "list");
    }
    
    private static List<InetSocketAddress> sanitize(final InetSocketAddress[] addresses) {
        ObjectUtil.checkNotNull(addresses, "addresses");
        final List<InetSocketAddress> list = new ArrayList<InetSocketAddress>(addresses.length);
        for (final InetSocketAddress a : addresses) {
            if (a == null) {
                break;
            }
            if (a.isUnresolved()) {
                throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + a);
            }
            list.add(a);
        }
        if (list.isEmpty()) {
            return DefaultDnsServerAddressStreamProvider.defaultAddressList();
        }
        return list;
    }
    
    public abstract DnsServerAddressStream stream();
}
