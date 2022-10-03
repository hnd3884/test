package io.netty.resolver.dns;

import java.util.Iterator;
import java.net.InetSocketAddress;
import java.util.List;

abstract class DefaultDnsServerAddresses extends DnsServerAddresses
{
    protected final List<InetSocketAddress> addresses;
    private final String strVal;
    
    DefaultDnsServerAddresses(final String type, final List<InetSocketAddress> addresses) {
        this.addresses = addresses;
        final StringBuilder buf = new StringBuilder(type.length() + 2 + addresses.size() * 16);
        buf.append(type).append('(');
        for (final InetSocketAddress a : addresses) {
            buf.append(a).append(", ");
        }
        buf.setLength(buf.length() - 2);
        buf.append(')');
        this.strVal = buf.toString();
    }
    
    @Override
    public String toString() {
        return this.strVal;
    }
}
