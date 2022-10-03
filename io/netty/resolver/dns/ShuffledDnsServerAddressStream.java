package io.netty.resolver.dns;

import java.util.Collection;
import java.util.Collections;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.List;

final class ShuffledDnsServerAddressStream implements DnsServerAddressStream
{
    private final List<InetSocketAddress> addresses;
    private int i;
    
    ShuffledDnsServerAddressStream(final List<InetSocketAddress> addresses) {
        this.addresses = addresses;
        this.shuffle();
    }
    
    private ShuffledDnsServerAddressStream(final List<InetSocketAddress> addresses, final int startIdx) {
        this.addresses = addresses;
        this.i = startIdx;
    }
    
    private void shuffle() {
        Collections.shuffle(this.addresses, PlatformDependent.threadLocalRandom());
    }
    
    @Override
    public InetSocketAddress next() {
        int i = this.i;
        final InetSocketAddress next = this.addresses.get(i);
        if (++i < this.addresses.size()) {
            this.i = i;
        }
        else {
            this.i = 0;
            this.shuffle();
        }
        return next;
    }
    
    @Override
    public int size() {
        return this.addresses.size();
    }
    
    @Override
    public ShuffledDnsServerAddressStream duplicate() {
        return new ShuffledDnsServerAddressStream(this.addresses, this.i);
    }
    
    @Override
    public String toString() {
        return SequentialDnsServerAddressStream.toString("shuffled", this.i, this.addresses);
    }
}
