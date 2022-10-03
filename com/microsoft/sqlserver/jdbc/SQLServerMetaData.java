package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;

public class SQLServerMetaData
{
    String columnName;
    int javaSqlType;
    int precision;
    int scale;
    boolean useServerDefault;
    boolean isUniqueKey;
    SQLServerSortOrder sortOrder;
    int sortOrdinal;
    private SQLCollation collation;
    static final int defaultSortOrdinal = -1;
    
    public SQLServerMetaData(final String columnName, final int sqlType) {
        this.columnName = null;
        this.precision = 0;
        this.scale = 0;
        this.useServerDefault = false;
        this.isUniqueKey = false;
        this.sortOrder = SQLServerSortOrder.Unspecified;
        this.columnName = columnName;
        this.javaSqlType = sqlType;
    }
    
    public SQLServerMetaData(final String columnName, final int sqlType, final int precision, final int scale) {
        this.columnName = null;
        this.precision = 0;
        this.scale = 0;
        this.useServerDefault = false;
        this.isUniqueKey = false;
        this.sortOrder = SQLServerSortOrder.Unspecified;
        this.columnName = columnName;
        this.javaSqlType = sqlType;
        this.precision = precision;
        this.scale = scale;
    }
    
    public SQLServerMetaData(final String columnName, final int sqlType, final int length) {
        this.columnName = null;
        this.precision = 0;
        this.scale = 0;
        this.useServerDefault = false;
        this.isUniqueKey = false;
        this.sortOrder = SQLServerSortOrder.Unspecified;
        this.columnName = columnName;
        this.javaSqlType = sqlType;
        this.precision = length;
    }
    
    public SQLServerMetaData(final String columnName, final int sqlType, final int precision, final int scale, final boolean useServerDefault, final boolean isUniqueKey, final SQLServerSortOrder sortOrder, final int sortOrdinal) throws SQLServerException {
        this.columnName = null;
        this.precision = 0;
        this.scale = 0;
        this.useServerDefault = false;
        this.isUniqueKey = false;
        this.sortOrder = SQLServerSortOrder.Unspecified;
        this.columnName = columnName;
        this.javaSqlType = sqlType;
        this.precision = precision;
        this.scale = scale;
        this.useServerDefault = useServerDefault;
        this.isUniqueKey = isUniqueKey;
        this.sortOrder = sortOrder;
        this.sortOrdinal = sortOrdinal;
        this.validateSortOrder();
    }
    
    public SQLServerMetaData(final SQLServerMetaData sqlServerMetaData) {
        this.columnName = null;
        this.precision = 0;
        this.scale = 0;
        this.useServerDefault = false;
        this.isUniqueKey = false;
        this.sortOrder = SQLServerSortOrder.Unspecified;
        this.columnName = sqlServerMetaData.columnName;
        this.javaSqlType = sqlServerMetaData.javaSqlType;
        this.precision = sqlServerMetaData.precision;
        this.scale = sqlServerMetaData.scale;
        this.useServerDefault = sqlServerMetaData.useServerDefault;
        this.isUniqueKey = sqlServerMetaData.isUniqueKey;
        this.sortOrder = sqlServerMetaData.sortOrder;
        this.sortOrdinal = sqlServerMetaData.sortOrdinal;
    }
    
    public String getColumName() {
        return this.columnName;
    }
    
    public int getSqlType() {
        return this.javaSqlType;
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    public boolean useServerDefault() {
        return this.useServerDefault;
    }
    
    public boolean isUniqueKey() {
        return this.isUniqueKey;
    }
    
    public SQLServerSortOrder getSortOrder() {
        return this.sortOrder;
    }
    
    public int getSortOrdinal() {
        return this.sortOrdinal;
    }
    
    SQLCollation getCollation() {
        return this.collation;
    }
    
    void validateSortOrder() throws SQLServerException {
        if (SQLServerSortOrder.Unspecified == this.sortOrder != (-1 == this.sortOrdinal)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPMissingSortOrderOrOrdinal"));
            throw new SQLServerException(form.format(new Object[] { this.sortOrder, this.sortOrdinal }), null, 0, null);
        }
    }
}
