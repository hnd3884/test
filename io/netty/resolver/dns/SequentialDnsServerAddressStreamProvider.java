package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public final class SequentialDnsServerAddressStreamProvider extends UniSequentialDnsServerAddressStreamProvider
{
    public SequentialDnsServerAddressStreamProvider(final InetSocketAddress... addresses) {
        super(DnsServerAddresses.sequential(addresses));
    }
    
    public SequentialDnsServerAddressStreamProvider(final Iterable<? extends InetSocketAddress> addresses) {
        super(DnsServerAddresses.sequential(addresses));
    }
}
