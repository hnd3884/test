package com.microsoft.sqlserver.jdbc;

final class StreamSSPI extends StreamPacket
{
    byte[] sspiBlob;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    StreamSSPI() {
        super(237);
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (237 != tdsReader.readUnsignedByte() && !StreamSSPI.$assertionsDisabled) {
            throw new AssertionError();
        }
        final int blobLength = tdsReader.readUnsignedShort();
        tdsReader.readBytes(this.sspiBlob = new byte[blobLength], 0, blobLength);
    }
}
