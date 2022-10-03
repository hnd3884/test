package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public final class SingletonDnsServerAddressStreamProvider extends UniSequentialDnsServerAddressStreamProvider
{
    public SingletonDnsServerAddressStreamProvider(final InetSocketAddress address) {
        super(DnsServerAddresses.singleton(address));
    }
}
