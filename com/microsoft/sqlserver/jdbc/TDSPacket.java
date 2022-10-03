package com.microsoft.sqlserver.jdbc;

final class TDSPacket
{
    final byte[] header;
    final byte[] payload;
    int payloadLength;
    volatile TDSPacket next;
    
    @Override
    public final String toString() {
        return "TDSPacket(SPID:" + Util.readUnsignedShortBigEndian(this.header, 4) + " Seq:" + this.header[6] + ")";
    }
    
    TDSPacket(final int size) {
        this.header = new byte[8];
        this.payload = new byte[size];
        this.payloadLength = 0;
        this.next = null;
    }
    
    final boolean isEOM() {
        return 0x1 == (this.header[1] & 0x1);
    }
}
