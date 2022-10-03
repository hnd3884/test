package io.netty.resolver.dns;

import java.net.InetSocketAddress;

final class SingletonDnsServerAddresses extends DnsServerAddresses
{
    private final InetSocketAddress address;
    private final DnsServerAddressStream stream;
    
    SingletonDnsServerAddresses(final InetSocketAddress address) {
        this.stream = new DnsServerAddressStream() {
            @Override
            public InetSocketAddress next() {
                return SingletonDnsServerAddresses.this.address;
            }
            
            @Override
            public int size() {
                return 1;
            }
            
            @Override
            public DnsServerAddressStream duplicate() {
                return this;
            }
            
            @Override
            public String toString() {
                return SingletonDnsServerAddresses.this.toString();
            }
        };
        this.address = address;
    }
    
    @Override
    public DnsServerAddressStream stream() {
        return this.stream;
    }
    
    @Override
    public String toString() {
        return "singleton(" + this.address + ")";
    }
}
