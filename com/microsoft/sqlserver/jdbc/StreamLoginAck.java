package com.microsoft.sqlserver.jdbc;

final class StreamLoginAck extends StreamPacket
{
    String sSQLServerVersion;
    int tdsVersion;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    StreamLoginAck() {
        super(173);
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (173 != tdsReader.readUnsignedByte() && !StreamLoginAck.$assertionsDisabled) {
            throw new AssertionError();
        }
        tdsReader.readUnsignedShort();
        tdsReader.readUnsignedByte();
        this.tdsVersion = tdsReader.readIntBigEndian();
        tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        final int serverMajorVersion = tdsReader.readUnsignedByte();
        final int serverMinorVersion = tdsReader.readUnsignedByte();
        final int serverBuildNumber = tdsReader.readUnsignedByte() << 8 | tdsReader.readUnsignedByte();
        this.sSQLServerVersion = serverMajorVersion + "." + ((serverMinorVersion <= 9) ? "0" : "") + serverMinorVersion + "." + serverBuildNumber;
    }
}
