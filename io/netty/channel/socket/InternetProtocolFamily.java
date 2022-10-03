package io.netty.channel.socket;

import java.net.Inet6Address;
import java.net.Inet4Address;
import io.netty.util.NetUtil;
import java.net.InetAddress;

public enum InternetProtocolFamily
{
    IPv4((Class<? extends InetAddress>)Inet4Address.class, 1), 
    IPv6((Class<? extends InetAddress>)Inet6Address.class, 2);
    
    private final Class<? extends InetAddress> addressType;
    private final int addressNumber;
    
    private InternetProtocolFamily(final Class<? extends InetAddress> addressType, final int addressNumber) {
        this.addressType = addressType;
        this.addressNumber = addressNumber;
    }
    
    public Class<? extends InetAddress> addressType() {
        return this.addressType;
    }
    
    public int addressNumber() {
        return this.addressNumber;
    }
    
    public InetAddress localhost() {
        switch (this) {
            case IPv4: {
                return NetUtil.LOCALHOST4;
            }
            case IPv6: {
                return NetUtil.LOCALHOST6;
            }
            default: {
                throw new IllegalStateException("Unsupported family " + this);
            }
        }
    }
    
    public static InternetProtocolFamily of(final InetAddress address) {
        if (address instanceof Inet4Address) {
            return InternetProtocolFamily.IPv4;
        }
        if (address instanceof Inet6Address) {
            return InternetProtocolFamily.IPv6;
        }
        throw new IllegalArgumentException("address " + address + " not supported");
    }
}
