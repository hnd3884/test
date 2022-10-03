package com.microsoft.sqlserver.jdbc;

final class TDSReaderMark
{
    final TDSPacket packet;
    final int payloadOffset;
    
    TDSReaderMark(final TDSPacket packet, final int payloadOffset) {
        this.packet = packet;
        this.payloadOffset = payloadOffset;
    }
}
