package com.microsoft.sqlserver.jdbc;

final class StreamColInfo extends StreamPacket
{
    private TDSReader tdsReader;
    private TDSReaderMark colInfoMark;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    StreamColInfo() {
        super(165);
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (165 != tdsReader.readUnsignedByte() && !StreamColInfo.$assertionsDisabled) {
            throw new AssertionError((Object)"Not a COLINFO token");
        }
        this.tdsReader = tdsReader;
        final int tokenLength = tdsReader.readUnsignedShort();
        this.colInfoMark = tdsReader.mark();
        tdsReader.skip(tokenLength);
    }
    
    int applyTo(final Column[] columns) throws SQLServerException {
        int numTables = 0;
        final TDSReaderMark currentMark = this.tdsReader.mark();
        this.tdsReader.reset(this.colInfoMark);
        for (final Column col : columns) {
            this.tdsReader.readUnsignedByte();
            col.setTableNum(this.tdsReader.readUnsignedByte());
            if (col.getTableNum() > numTables) {
                numTables = col.getTableNum();
            }
            col.setInfoStatus(this.tdsReader.readUnsignedByte());
            if (col.hasDifferentName()) {
                col.setBaseColumnName(this.tdsReader.readUnicodeString(this.tdsReader.readUnsignedByte()));
            }
        }
        this.tdsReader.reset(currentMark);
        return numTables;
    }
}
