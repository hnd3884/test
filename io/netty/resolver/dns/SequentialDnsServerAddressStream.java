package io.netty.resolver.dns;

import java.util.Iterator;
import java.util.Collection;
import java.net.InetSocketAddress;
import java.util.List;

final class SequentialDnsServerAddressStream implements DnsServerAddressStream
{
    private final List<? extends InetSocketAddress> addresses;
    private int i;
    
    SequentialDnsServerAddressStream(final List<? extends InetSocketAddress> addresses, final int startIdx) {
        this.addresses = addresses;
        this.i = startIdx;
    }
    
    @Override
    public InetSocketAddress next() {
        int i = this.i;
        final InetSocketAddress next = (InetSocketAddress)this.addresses.get(i);
        if (++i < this.addresses.size()) {
            this.i = i;
        }
        else {
            this.i = 0;
        }
        return next;
    }
    
    @Override
    public int size() {
        return this.addresses.size();
    }
    
    @Override
    public SequentialDnsServerAddressStream duplicate() {
        return new SequentialDnsServerAddressStream(this.addresses, this.i);
    }
    
    @Override
    public String toString() {
        return toString("sequential", this.i, this.addresses);
    }
    
    static String toString(final String type, final int index, final Collection<? extends InetSocketAddress> addresses) {
        final StringBuilder buf = new StringBuilder(type.length() + 2 + addresses.size() * 16);
        buf.append(type).append("(index: ").append(index);
        buf.append(", addrs: (");
        for (final InetSocketAddress a : addresses) {
            buf.append(a).append(", ");
        }
        buf.setLength(buf.length() - 2);
        buf.append("))");
        return buf.toString();
    }
}
