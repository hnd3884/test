package com.microsoft.sqlserver.jdbc;

final class XMLTDSHeader
{
    private final String databaseName;
    private final String owningSchema;
    private final String xmlSchemaCollection;
    
    XMLTDSHeader(final TDSReader tdsReader) throws SQLServerException {
        if (0 != tdsReader.readUnsignedByte()) {
            this.databaseName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
            this.owningSchema = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
            this.xmlSchemaCollection = tdsReader.readUnicodeString(tdsReader.readUnsignedShort());
        }
        else {
            this.xmlSchemaCollection = null;
            this.owningSchema = null;
            this.databaseName = null;
        }
    }
}
