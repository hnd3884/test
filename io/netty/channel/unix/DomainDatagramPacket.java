package io.netty.channel.unix;

import io.netty.util.ReferenceCounted;
import io.netty.channel.AddressedEnvelope;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultAddressedEnvelope;

public final class DomainDatagramPacket extends DefaultAddressedEnvelope<ByteBuf, DomainSocketAddress> implements ByteBufHolder
{
    public DomainDatagramPacket(final ByteBuf data, final DomainSocketAddress recipient) {
        super(data, recipient);
    }
    
    public DomainDatagramPacket(final ByteBuf data, final DomainSocketAddress recipient, final DomainSocketAddress sender) {
        super(data, recipient, sender);
    }
    
    @Override
    public DomainDatagramPacket copy() {
        return this.replace(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().copy());
    }
    
    @Override
    public DomainDatagramPacket duplicate() {
        return this.replace(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().duplicate());
    }
    
    @Override
    public DomainDatagramPacket replace(final ByteBuf content) {
        return new DomainDatagramPacket(content, ((DefaultAddressedEnvelope<M, DomainSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, DomainSocketAddress>)this).sender());
    }
    
    @Override
    public DomainDatagramPacket retain() {
        super.retain();
        return this;
    }
    
    @Override
    public DomainDatagramPacket retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public DomainDatagramPacket retainedDuplicate() {
        return this.replace(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().retainedDuplicate());
    }
    
    @Override
    public DomainDatagramPacket touch() {
        super.touch();
        return this;
    }
    
    @Override
    public DomainDatagramPacket touch(final Object hint) {
        super.touch(hint);
        return this;
    }
}
