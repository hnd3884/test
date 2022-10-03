package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class EthernetPacket
{
    private static final byte[] DUMMY_SOURCE_MAC_ADDRESS;
    private static final byte[] DUMMY_DESTINATION_MAC_ADDRESS;
    private static final int V4 = 2048;
    private static final int V6 = 34525;
    
    private EthernetPacket() {
    }
    
    static void writeIPv4(final ByteBuf byteBuf, final ByteBuf payload) {
        writePacket(byteBuf, payload, EthernetPacket.DUMMY_SOURCE_MAC_ADDRESS, EthernetPacket.DUMMY_DESTINATION_MAC_ADDRESS, 2048);
    }
    
    static void writeIPv6(final ByteBuf byteBuf, final ByteBuf payload) {
        writePacket(byteBuf, payload, EthernetPacket.DUMMY_SOURCE_MAC_ADDRESS, EthernetPacket.DUMMY_DESTINATION_MAC_ADDRESS, 34525);
    }
    
    private static void writePacket(final ByteBuf byteBuf, final ByteBuf payload, final byte[] srcAddress, final byte[] dstAddress, final int type) {
        byteBuf.writeBytes(dstAddress);
        byteBuf.writeBytes(srcAddress);
        byteBuf.writeShort(type);
        byteBuf.writeBytes(payload);
    }
    
    static {
        DUMMY_SOURCE_MAC_ADDRESS = new byte[] { 0, 0, 94, 0, 83, 0 };
        DUMMY_DESTINATION_MAC_ADDRESS = new byte[] { 0, 0, 94, 0, 83, -1 };
    }
}
