package io.netty.channel.epoll;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.SegmentedDatagramPacket;
import io.netty.channel.socket.DatagramPacket;
import java.net.UnknownHostException;
import java.net.Inet6Address;
import java.net.InetAddress;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBuf;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.IovArray;

final class NativeDatagramPacketArray
{
    private final NativeDatagramPacket[] packets;
    private final IovArray iovArray;
    private final byte[] ipv4Bytes;
    private final MyMessageProcessor processor;
    private int count;
    
    NativeDatagramPacketArray() {
        this.packets = new NativeDatagramPacket[Limits.UIO_MAX_IOV];
        this.iovArray = new IovArray();
        this.ipv4Bytes = new byte[4];
        this.processor = new MyMessageProcessor();
        for (int i = 0; i < this.packets.length; ++i) {
            this.packets[i] = new NativeDatagramPacket();
        }
    }
    
    boolean addWritable(final ByteBuf buf, final int index, final int len) {
        return this.add0(buf, index, len, 0, null);
    }
    
    private boolean add0(final ByteBuf buf, final int index, final int len, final int segmentLen, final InetSocketAddress recipient) {
        if (this.count == this.packets.length) {
            return false;
        }
        if (len == 0) {
            return true;
        }
        final int offset = this.iovArray.count();
        if (offset == Limits.IOV_MAX || !this.iovArray.add(buf, index, len)) {
            return false;
        }
        final NativeDatagramPacket p = this.packets[this.count];
        p.init(this.iovArray.memoryAddress(offset), this.iovArray.count() - offset, segmentLen, recipient);
        ++this.count;
        return true;
    }
    
    void add(final ChannelOutboundBuffer buffer, final boolean connected, final int maxMessagesPerWrite) throws Exception {
        this.processor.connected = connected;
        this.processor.maxMessagesPerWrite = maxMessagesPerWrite;
        buffer.forEachFlushedMessage(this.processor);
    }
    
    int count() {
        return this.count;
    }
    
    NativeDatagramPacket[] packets() {
        return this.packets;
    }
    
    void clear() {
        this.count = 0;
        this.iovArray.clear();
    }
    
    void release() {
        this.iovArray.release();
    }
    
    private static InetSocketAddress newAddress(final byte[] addr, final int addrLen, final int port, final int scopeId, final byte[] ipv4Bytes) throws UnknownHostException {
        InetAddress address;
        if (addrLen == ipv4Bytes.length) {
            System.arraycopy(addr, 0, ipv4Bytes, 0, addrLen);
            address = InetAddress.getByAddress(ipv4Bytes);
        }
        else {
            address = Inet6Address.getByAddress(null, addr, scopeId);
        }
        return new InetSocketAddress(address, port);
    }
    
    private final class MyMessageProcessor implements ChannelOutboundBuffer.MessageProcessor
    {
        private boolean connected;
        private int maxMessagesPerWrite;
        
        @Override
        public boolean processMessage(final Object msg) {
            boolean added;
            if (msg instanceof DatagramPacket) {
                final DatagramPacket packet = (DatagramPacket)msg;
                final ByteBuf buf = ((DefaultAddressedEnvelope<ByteBuf, A>)packet).content();
                int segmentSize = 0;
                if (packet instanceof SegmentedDatagramPacket) {
                    final int seg = ((SegmentedDatagramPacket)packet).segmentSize();
                    if (buf.readableBytes() > seg) {
                        segmentSize = seg;
                    }
                }
                added = NativeDatagramPacketArray.this.add0(buf, buf.readerIndex(), buf.readableBytes(), segmentSize, ((DefaultAddressedEnvelope<M, InetSocketAddress>)packet).recipient());
            }
            else if (msg instanceof ByteBuf && this.connected) {
                final ByteBuf buf2 = (ByteBuf)msg;
                added = NativeDatagramPacketArray.this.add0(buf2, buf2.readerIndex(), buf2.readableBytes(), 0, null);
            }
            else {
                added = false;
            }
            if (added) {
                --this.maxMessagesPerWrite;
                return this.maxMessagesPerWrite > 0;
            }
            return false;
        }
    }
    
    final class NativeDatagramPacket
    {
        private long memoryAddress;
        private int count;
        private final byte[] senderAddr;
        private int senderAddrLen;
        private int senderScopeId;
        private int senderPort;
        private final byte[] recipientAddr;
        private int recipientAddrLen;
        private int recipientScopeId;
        private int recipientPort;
        private int segmentSize;
        
        NativeDatagramPacket() {
            this.senderAddr = new byte[16];
            this.recipientAddr = new byte[16];
        }
        
        private void init(final long memoryAddress, final int count, final int segmentSize, final InetSocketAddress recipient) {
            this.memoryAddress = memoryAddress;
            this.count = count;
            this.segmentSize = segmentSize;
            this.senderScopeId = 0;
            this.senderPort = 0;
            this.senderAddrLen = 0;
            if (recipient == null) {
                this.recipientScopeId = 0;
                this.recipientPort = 0;
                this.recipientAddrLen = 0;
            }
            else {
                final InetAddress address = recipient.getAddress();
                if (address instanceof Inet6Address) {
                    System.arraycopy(address.getAddress(), 0, this.recipientAddr, 0, this.recipientAddr.length);
                    this.recipientScopeId = ((Inet6Address)address).getScopeId();
                }
                else {
                    NativeInetAddress.copyIpv4MappedIpv6Address(address.getAddress(), this.recipientAddr);
                    this.recipientScopeId = 0;
                }
                this.recipientAddrLen = this.recipientAddr.length;
                this.recipientPort = recipient.getPort();
            }
        }
        
        DatagramPacket newDatagramPacket(final ByteBuf buffer, InetSocketAddress recipient) throws UnknownHostException {
            final InetSocketAddress sender = newAddress(this.senderAddr, this.senderAddrLen, this.senderPort, this.senderScopeId, NativeDatagramPacketArray.this.ipv4Bytes);
            if (this.recipientAddrLen != 0) {
                recipient = newAddress(this.recipientAddr, this.recipientAddrLen, this.recipientPort, this.recipientScopeId, NativeDatagramPacketArray.this.ipv4Bytes);
            }
            buffer.writerIndex(this.count);
            if (this.segmentSize > 0) {
                return new io.netty.channel.epoll.SegmentedDatagramPacket(buffer, this.segmentSize, recipient, sender);
            }
            return new DatagramPacket(buffer, recipient, sender);
        }
    }
}
