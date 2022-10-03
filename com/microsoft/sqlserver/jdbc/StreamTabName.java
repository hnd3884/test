package com.microsoft.sqlserver.jdbc;

final class StreamTabName extends StreamPacket
{
    private TDSReader tdsReader;
    private TDSReaderMark tableNamesMark;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    StreamTabName() {
        super(164);
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (164 != tdsReader.readUnsignedByte() && !StreamTabName.$assertionsDisabled) {
            throw new AssertionError((Object)"Not a TABNAME token");
        }
        this.tdsReader = tdsReader;
        final int tokenLength = tdsReader.readUnsignedShort();
        this.tableNamesMark = tdsReader.mark();
        tdsReader.skip(tokenLength);
    }
    
    void applyTo(final Column[] columns, final int numTables) throws SQLServerException {
        final TDSReaderMark currentMark = this.tdsReader.mark();
        this.tdsReader.reset(this.tableNamesMark);
        final SQLIdentifier[] tableNames = new SQLIdentifier[numTables];
        for (int i = 0; i < numTables; ++i) {
            tableNames[i] = this.tdsReader.readSQLIdentifier();
        }
        for (final Column col : columns) {
            if (col.getTableNum() > 0) {
                col.setTableName(tableNames[col.getTableNum() - 1]);
            }
        }
        this.tdsReader.reset(currentMark);
    }
}
