package com.btr.proxy.selector.whitelist;

import java.net.URI;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.btr.proxy.util.UriFilter;

public class IpRangeFilter implements UriFilter
{
    private byte[] matchTo;
    int numOfBits;
    
    public IpRangeFilter(final String matchTo) {
        final String[] parts = matchTo.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("IP range is not valid:" + matchTo);
        }
        try {
            final InetAddress address = InetAddress.getByName(parts[0].trim());
            this.matchTo = address.getAddress();
        }
        catch (final UnknownHostException e) {
            throw new IllegalArgumentException("IP range is not valid:" + matchTo);
        }
        this.numOfBits = Integer.parseInt(parts[1].trim());
    }
    
    public boolean accept(final URI uri) {
        if (uri == null || uri.getHost() == null) {
            return false;
        }
        try {
            final InetAddress address = InetAddress.getByName(uri.getHost());
            final byte[] addr = address.getAddress();
            if (addr.length != this.matchTo.length) {
                return false;
            }
            int bit = 0;
            for (int nibble = 0; nibble < addr.length; ++nibble) {
                for (int nibblePos = 7; nibblePos >= 0; --nibblePos) {
                    final int mask = 1 << nibblePos;
                    if ((this.matchTo[nibble] & mask) != (addr[nibble] & mask)) {
                        return false;
                    }
                    if (++bit >= this.numOfBits) {
                        return true;
                    }
                }
            }
        }
        catch (final UnknownHostException ex) {}
        return false;
    }
}
