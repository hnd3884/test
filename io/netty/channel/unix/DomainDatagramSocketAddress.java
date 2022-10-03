package io.netty.channel.unix;

import io.netty.util.CharsetUtil;

public final class DomainDatagramSocketAddress extends DomainSocketAddress
{
    private static final long serialVersionUID = -5925732678737768223L;
    private final DomainDatagramSocketAddress localAddress;
    private final int receivedAmount;
    
    public DomainDatagramSocketAddress(final byte[] socketPath, final int receivedAmount, final DomainDatagramSocketAddress localAddress) {
        super(new String(socketPath, CharsetUtil.UTF_8));
        this.localAddress = localAddress;
        this.receivedAmount = receivedAmount;
    }
    
    public DomainDatagramSocketAddress localAddress() {
        return this.localAddress;
    }
    
    public int receivedAmount() {
        return this.receivedAmount;
    }
}
