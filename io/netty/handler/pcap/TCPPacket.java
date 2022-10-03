package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class TCPPacket
{
    private static final short OFFSET = 20480;
    
    private TCPPacket() {
    }
    
    static void writePacket(final ByteBuf byteBuf, final ByteBuf payload, final int segmentNumber, final int ackNumber, final int srcPort, final int dstPort, final TCPFlag... tcpFlags) {
        byteBuf.writeShort(srcPort);
        byteBuf.writeShort(dstPort);
        byteBuf.writeInt(segmentNumber);
        byteBuf.writeInt(ackNumber);
        byteBuf.writeShort(0x5000 | TCPFlag.getFlag(tcpFlags));
        byteBuf.writeShort(65535);
        byteBuf.writeShort(1);
        byteBuf.writeShort(0);
        if (payload != null) {
            byteBuf.writeBytes(payload);
        }
    }
    
    enum TCPFlag
    {
        FIN(1), 
        SYN(2), 
        RST(4), 
        PSH(8), 
        ACK(16), 
        URG(32), 
        ECE(64), 
        CWR(128);
        
        private final int value;
        
        private TCPFlag(final int value) {
            this.value = value;
        }
        
        static int getFlag(final TCPFlag... tcpFlags) {
            int flags = 0;
            for (final TCPFlag tcpFlag : tcpFlags) {
                flags |= tcpFlag.value;
            }
            return flags;
        }
    }
}
