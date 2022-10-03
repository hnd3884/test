package io.netty.channel.epoll;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBuf;

@Deprecated
public final class SegmentedDatagramPacket extends io.netty.channel.unix.SegmentedDatagramPacket
{
    public SegmentedDatagramPacket(final ByteBuf data, final int segmentSize, final InetSocketAddress recipient) {
        super(data, segmentSize, recipient);
        checkIsSupported();
    }
    
    public SegmentedDatagramPacket(final ByteBuf data, final int segmentSize, final InetSocketAddress recipient, final InetSocketAddress sender) {
        super(data, segmentSize, recipient, sender);
        checkIsSupported();
    }
    
    public static boolean isSupported() {
        return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
    }
    
    @Override
    public SegmentedDatagramPacket copy() {
        return new SegmentedDatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().copy(), this.segmentSize(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket duplicate() {
        return new SegmentedDatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().duplicate(), this.segmentSize(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket retainedDuplicate() {
        return new SegmentedDatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().retainedDuplicate(), this.segmentSize(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public SegmentedDatagramPacket replace(final ByteBuf content) {
        return new SegmentedDatagramPacket(content, this.segmentSize(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
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
    
    private static void checkIsSupported() {
        if (!isSupported()) {
            throw new IllegalStateException();
        }
    }
}
