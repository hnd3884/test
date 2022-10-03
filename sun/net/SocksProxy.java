package sun.net;

import java.net.SocketAddress;
import java.net.Proxy;

public final class SocksProxy extends Proxy
{
    private final int version;
    
    private SocksProxy(final SocketAddress socketAddress, final int version) {
        super(Type.SOCKS, socketAddress);
        this.version = version;
    }
    
    public static SocksProxy create(final SocketAddress socketAddress, final int n) {
        return new SocksProxy(socketAddress, n);
    }
    
    public int protocolVersion() {
        return this.version;
    }
}
