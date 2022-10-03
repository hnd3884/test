package io.netty.handler.proxy;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

public final class ProxyConnectionEvent
{
    private final String protocol;
    private final String authScheme;
    private final SocketAddress proxyAddress;
    private final SocketAddress destinationAddress;
    private String strVal;
    
    public ProxyConnectionEvent(final String protocol, final String authScheme, final SocketAddress proxyAddress, final SocketAddress destinationAddress) {
        this.protocol = ObjectUtil.checkNotNull(protocol, "protocol");
        this.authScheme = ObjectUtil.checkNotNull(authScheme, "authScheme");
        this.proxyAddress = ObjectUtil.checkNotNull(proxyAddress, "proxyAddress");
        this.destinationAddress = ObjectUtil.checkNotNull(destinationAddress, "destinationAddress");
    }
    
    public String protocol() {
        return this.protocol;
    }
    
    public String authScheme() {
        return this.authScheme;
    }
    
    public <T extends SocketAddress> T proxyAddress() {
        return (T)this.proxyAddress;
    }
    
    public <T extends SocketAddress> T destinationAddress() {
        return (T)this.destinationAddress;
    }
    
    @Override
    public String toString() {
        if (this.strVal != null) {
            return this.strVal;
        }
        final StringBuilder buf = new StringBuilder(128).append(StringUtil.simpleClassName(this)).append('(').append(this.protocol).append(", ").append(this.authScheme).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress).append(')');
        return this.strVal = buf.toString();
    }
}
