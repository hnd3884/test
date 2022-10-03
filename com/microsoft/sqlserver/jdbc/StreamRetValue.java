package com.microsoft.sqlserver.jdbc;

final class StreamRetValue extends StreamPacket
{
    private String paramName;
    private int ordinalOrLength;
    private int status;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    final int getOrdinalOrLength() {
        return this.ordinalOrLength;
    }
    
    StreamRetValue() {
        super(172);
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (172 != tdsReader.readUnsignedByte() && !StreamRetValue.$assertionsDisabled) {
            throw new AssertionError();
        }
        this.ordinalOrLength = tdsReader.readUnsignedShort();
        this.paramName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.status = tdsReader.readUnsignedByte();
    }
    
    CryptoMetadata getCryptoMetadata(final TDSReader tdsReader) throws SQLServerException {
        final CryptoMetadata cryptoMeta = new StreamColumns().readCryptoMetadata(tdsReader);
        return cryptoMeta;
    }
}
