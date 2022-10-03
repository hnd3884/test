package com.microsoft.sqlserver.jdbc;

final class StreamInfo extends StreamPacket
{
    final SQLServerError msg;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    StreamInfo() {
        super(171);
        this.msg = new SQLServerError();
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (171 != tdsReader.readUnsignedByte() && !StreamInfo.$assertionsDisabled) {
            throw new AssertionError();
        }
        this.msg.setContentsFromTDS(tdsReader);
    }
}
