package com.microsoft.sqlserver.jdbc;

class SqlVariant
{
    private int baseType;
    private int precision;
    private int scale;
    private int maxLength;
    private SQLCollation collation;
    private boolean isBaseTypeTime;
    private JDBCType baseJDBCType;
    
    SqlVariant(final int baseType) {
        this.isBaseTypeTime = false;
        this.baseType = baseType;
    }
    
    boolean isBaseTypeTimeValue() {
        return this.isBaseTypeTime;
    }
    
    void setIsBaseTypeTimeValue(final boolean isBaseTypeTime) {
        this.isBaseTypeTime = isBaseTypeTime;
    }
    
    void setBaseType(final int baseType) {
        this.baseType = baseType;
    }
    
    int getBaseType() {
        return this.baseType;
    }
    
    void setBaseJDBCType(final JDBCType baseJDBCType) {
        this.baseJDBCType = baseJDBCType;
    }
    
    JDBCType getBaseJDBCType() {
        return this.baseJDBCType;
    }
    
    void setScale(final int scale) {
        this.scale = scale;
    }
    
    int getScale() {
        return this.scale;
    }
    
    void setPrecision(final int precision) {
        this.precision = precision;
    }
    
    int getPrecision() {
        return this.precision;
    }
    
    void setCollation(final SQLCollation collation) {
        this.collation = collation;
    }
    
    SQLCollation getCollation() {
        return this.collation;
    }
    
    void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    int getMaxLength() {
        return this.maxLength;
    }
}
