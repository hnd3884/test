package io.netty.channel.unix;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

public class SegmentedDatagramPacket extends DatagramPacket
{
    private final int segmentSize;
    
    public SegmentedDatagramPacket(final ByteBuf data, final int segmentSize, final InetSocketAddress recipient) {
        super(data, recipient);
        this.segmentSize = ObjectUtil.checkPositive(segmentSize, "segmentSize");
    }
    
    public SegmentedDatagramPacket(final ByteBuf data, final int segmentSize, final InetSocketAddress recipient, final InetSocketAddress sender) {
        super(data, recipient, sender);
        this.segmentSize = ObjectUtil.checkPositive(segmentSize, "segmentSize");
    }
    
    public int segmentSize() {
        return this.segmentSize;
    }
    
    @Override
    public SegmentedDatagramPacket copy() {
        return new SegmentedDatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().copy(), this.segmentSize, ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket duplicate() {
        return new SegmentedDatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().duplicate(), this.segmentSize, ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket retainedDuplicate() {
        return new SegmentedDatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().retainedDuplicate(), this.segmentSize, ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket replace(final ByteBuf content) {
        return new SegmentedDatagramPacket(content, this.segmentSize, ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket retain() {
        super.retain();
        return this;
    }
    
    @Override
    public SegmentedDatagramPacket retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public SegmentedDatagramPacket touch() {
        super.touch();
        return this;
    }
    
    @Override
    public SegmentedDatagramPacket touch(final Object hint) {
        super.touch(hint);
        return this;
    }
}
