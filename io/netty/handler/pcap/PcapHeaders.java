package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class PcapHeaders
{
    private static final byte[] GLOBAL_HEADER;
    
    private PcapHeaders() {
    }
    
    public static void writeGlobalHeader(final ByteBuf byteBuf) {
        byteBuf.writeBytes(PcapHeaders.GLOBAL_HEADER);
    }
    
    static void writePacketHeader(final ByteBuf byteBuf, final int ts_sec, final int ts_usec, final int incl_len, final int orig_len) {
        byteBuf.writeInt(ts_sec);
        byteBuf.writeInt(ts_usec);
        byteBuf.writeInt(incl_len);
        byteBuf.writeInt(orig_len);
    }
    
    static {
        GLOBAL_HEADER = new byte[] { -95, -78, -61, -44, 0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 1 };
    }
}
