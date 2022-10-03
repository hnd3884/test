package com.microsoft.sqlserver.jdbc;

public final class SQLServerDataColumn
{
    String columnName;
    int javaSqlType;
    int precision;
    int scale;
    int numberOfDigitsIntegerPart;
    
    public SQLServerDataColumn(final String columnName, final int sqlType) {
        this.precision = 0;
        this.scale = 0;
        this.numberOfDigitsIntegerPart = 0;
        this.columnName = columnName;
        this.javaSqlType = sqlType;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public int getColumnType() {
        return this.javaSqlType;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.javaSqlType;
        hash = 31 * hash + this.precision;
        hash = 31 * hash + this.scale;
        hash = 31 * hash + this.numberOfDigitsIntegerPart;
        hash = 31 * hash + ((null != this.columnName) ? this.columnName.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null != object && object.getClass() == SQLServerDataColumn.class) {
            final SQLServerDataColumn aSQLServerDataColumn = (SQLServerDataColumn)object;
            if (this.hashCode() == aSQLServerDataColumn.hashCode()) {
                if (null == this.columnName) {
                    if (null != aSQLServerDataColumn.columnName) {
                        return false;
                    }
                }
                else if (!this.columnName.equals(aSQLServerDataColumn.columnName)) {
                    return false;
                }
                if (this.javaSqlType == aSQLServerDataColumn.javaSqlType && this.numberOfDigitsIntegerPart == aSQLServerDataColumn.numberOfDigitsIntegerPart && this.precision == aSQLServerDataColumn.precision && this.scale == aSQLServerDataColumn.scale) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
