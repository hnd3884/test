package com.microsoft.sqlserver.jdbc;

final class StreamRetStatus extends StreamPacket
{
    private int status;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    final int getStatus() {
        return this.status;
    }
    
    StreamRetStatus() {
        super(121);
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (121 != tdsReader.readUnsignedByte() && !StreamRetStatus.$assertionsDisabled) {
            throw new AssertionError();
        }
        this.status = tdsReader.readInt();
    }
}
