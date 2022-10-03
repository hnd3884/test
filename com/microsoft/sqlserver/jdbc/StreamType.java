package com.microsoft.sqlserver.jdbc;

enum StreamType
{
    NONE(JDBCType.UNKNOWN, "None"), 
    ASCII(JDBCType.LONGVARCHAR, "AsciiStream"), 
    BINARY(JDBCType.LONGVARBINARY, "BinaryStream"), 
    CHARACTER(JDBCType.LONGVARCHAR, "CharacterStream"), 
    NCHARACTER(JDBCType.LONGNVARCHAR, "NCharacterStream"), 
    SQLXML(JDBCType.SQLXML, "SQLXML");
    
    private final JDBCType jdbcType;
    private final String name;
    
    JDBCType getJDBCType() {
        return this.jdbcType;
    }
    
    private StreamType(final JDBCType jdbcType, final String name) {
        this.jdbcType = jdbcType;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    boolean convertsFrom(final TypeInfo typeInfo) {
        if (StreamType.ASCII == this) {
            if (SSType.XML == typeInfo.getSSType()) {
                return false;
            }
            if (null != typeInfo.getSQLCollation() && !typeInfo.getSQLCollation().supportsAsciiConversion()) {
                return false;
            }
        }
        return typeInfo.getSSType().convertsTo(this.jdbcType);
    }
    
    boolean convertsTo(final TypeInfo typeInfo) {
        if (StreamType.ASCII == this) {
            if (SSType.XML == typeInfo.getSSType()) {
                return false;
            }
            if (null != typeInfo.getSQLCollation() && !typeInfo.getSQLCollation().supportsAsciiConversion()) {
                return false;
            }
        }
        return this.jdbcType.convertsTo(typeInfo.getSSType());
    }
}
