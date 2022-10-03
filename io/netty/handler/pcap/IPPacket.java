package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class IPPacket
{
    private static final byte MAX_TTL = -1;
    private static final short V4_HEADER_SIZE = 20;
    private static final byte TCP = 6;
    private static final byte UDP = 17;
    private static final int IPV6_VERSION_TRAFFIC_FLOW = 60000000;
    
    private IPPacket() {
    }
    
    static void writeUDPv4(final ByteBuf byteBuf, final ByteBuf payload, final int srcAddress, final int dstAddress) {
        writePacketv4(byteBuf, payload, 17, srcAddress, dstAddress);
    }
    
    static void writeUDPv6(final ByteBuf byteBuf, final ByteBuf payload, final byte[] srcAddress, final byte[] dstAddress) {
        writePacketv6(byteBuf, payload, 17, srcAddress, dstAddress);
    }
    
    static void writeTCPv4(final ByteBuf byteBuf, final ByteBuf payload, final int srcAddress, final int dstAddress) {
        writePacketv4(byteBuf, payload, 6, srcAddress, dstAddress);
    }
    
    static void writeTCPv6(final ByteBuf byteBuf, final ByteBuf payload, final byte[] srcAddress, final byte[] dstAddress) {
        writePacketv6(byteBuf, payload, 6, srcAddress, dstAddress);
    }
    
    private static void writePacketv4(final ByteBuf byteBuf, final ByteBuf payload, final int protocol, final int srcAddress, final int dstAddress) {
        byteBuf.writeByte(69);
        byteBuf.writeByte(0);
        byteBuf.writeShort(20 + payload.readableBytes());
        byteBuf.writeShort(0);
        byteBuf.writeShort(0);
        byteBuf.writeByte(-1);
        byteBuf.writeByte(protocol);
        byteBuf.writeShort(0);
        byteBuf.writeInt(srcAddress);
        byteBuf.writeInt(dstAddress);
        byteBuf.writeBytes(payload);
    }
    
    private static void writePacketv6(final ByteBuf byteBuf, final ByteBuf payload, final int protocol, final byte[] srcAddress, final byte[] dstAddress) {
        byteBuf.writeInt(60000000);
        byteBuf.writeShort(payload.readableBytes());
        byteBuf.writeByte(protocol & 0xFF);
        byteBuf.writeByte(-1);
        byteBuf.writeBytes(srcAddress);
        byteBuf.writeBytes(dstAddress);
        byteBuf.writeBytes(payload);
    }
}
