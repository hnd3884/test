package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;

public final class DatagramSocketAddress extends InetSocketAddress
{
    private static final long serialVersionUID = 3094819287843178401L;
    private final int receivedAmount;
    private final DatagramSocketAddress localAddress;
    
    DatagramSocketAddress(final byte[] addr, final int scopeId, final int port, final int receivedAmount, final DatagramSocketAddress local) throws UnknownHostException {
        super(newAddress(addr, scopeId), port);
        this.receivedAmount = receivedAmount;
        this.localAddress = local;
    }
    
    public DatagramSocketAddress localAddress() {
        return this.localAddress;
    }
    
    public int receivedAmount() {
        return this.receivedAmount;
    }
    
    private static InetAddress newAddress(final byte[] bytes, final int scopeId) throws UnknownHostException {
        if (bytes.length == 4) {
            return InetAddress.getByAddress(bytes);
        }
        return Inet6Address.getByAddress(null, bytes, scopeId);
    }
}
