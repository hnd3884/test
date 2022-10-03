package io.netty.resolver.dns;

import io.netty.util.internal.ObjectUtil;
import java.net.InetAddress;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Comparator;

public final class NameServerComparator implements Comparator<InetSocketAddress>, Serializable
{
    private static final long serialVersionUID = 8372151874317596185L;
    private final Class<? extends InetAddress> preferredAddressType;
    
    public NameServerComparator(final Class<? extends InetAddress> preferredAddressType) {
        this.preferredAddressType = ObjectUtil.checkNotNull(preferredAddressType, "preferredAddressType");
    }
    
    @Override
    public int compare(final InetSocketAddress addr1, final InetSocketAddress addr2) {
        if (addr1.equals(addr2)) {
            return 0;
        }
        if (!addr1.isUnresolved() && !addr2.isUnresolved()) {
            if (addr1.getAddress().getClass() == addr2.getAddress().getClass()) {
                return 0;
            }
            return this.preferredAddressType.isAssignableFrom(addr1.getAddress().getClass()) ? -1 : 1;
        }
        else {
            if (addr1.isUnresolved() && addr2.isUnresolved()) {
                return 0;
            }
            return addr1.isUnresolved() ? 1 : -1;
        }
    }
}
