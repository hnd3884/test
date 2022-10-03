package com.microsoft.sqlserver.jdbc;

abstract class StreamPacket
{
    int packetType;
    
    final int getTokenType() {
        return this.packetType;
    }
    
    StreamPacket() {
        this.packetType = 0;
    }
    
    StreamPacket(final int packetType) {
        this.packetType = packetType;
    }
    
    abstract void setFromTDS(final TDSReader p0) throws SQLServerException;
}
