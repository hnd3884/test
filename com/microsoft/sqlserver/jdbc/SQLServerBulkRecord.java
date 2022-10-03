package com.microsoft.sqlserver.jdbc;

import java.util.Set;
import java.util.Iterator;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.format.DateTimeFormatter;
import java.util.Map;

abstract class SQLServerBulkRecord implements ISQLServerBulkRecord
{
    private static final long serialVersionUID = -170992637946357449L;
    protected String[] columnNames;
    protected Map<Integer, ColumnMetadata> columnMetadata;
    protected DateTimeFormatter dateTimeFormatter;
    protected DateTimeFormatter timeFormatter;
    protected String loggerPackageName;
    protected static Logger loggerExternal;
    
    SQLServerBulkRecord() {
        this.columnNames = null;
        this.dateTimeFormatter = null;
        this.timeFormatter = null;
        this.loggerPackageName = "com.microsoft.jdbc.SQLServerBulkRecord";
    }
    
    @Override
    public void addColumnMetadata(final int positionInSource, final String name, final int jdbcType, final int precision, final int scale, final DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        this.addColumnMetadataInternal(positionInSource, name, jdbcType, precision, scale, dateTimeFormatter);
    }
    
    @Override
    public void addColumnMetadata(final int positionInSource, final String name, final int jdbcType, final int precision, final int scale) throws SQLServerException {
        this.addColumnMetadataInternal(positionInSource, name, jdbcType, precision, scale, null);
    }
    
    void addColumnMetadataInternal(final int positionInSource, final String name, final int jdbcType, final int precision, final int scale, final DateTimeFormatter dateTimeFormatter) throws SQLServerException {
    }
    
    @Override
    public void setTimestampWithTimezoneFormat(final String dateTimeFormat) {
        SQLServerBulkRecord.loggerExternal.entering(this.loggerPackageName, "setTimestampWithTimezoneFormat", dateTimeFormat);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        SQLServerBulkRecord.loggerExternal.exiting(this.loggerPackageName, "setTimestampWithTimezoneFormat");
    }
    
    @Override
    public void setTimestampWithTimezoneFormat(final DateTimeFormatter dateTimeFormatter) {
        if (SQLServerBulkRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkRecord.loggerExternal.entering(this.loggerPackageName, "setTimestampWithTimezoneFormat", new Object[] { dateTimeFormatter });
        }
        this.dateTimeFormatter = dateTimeFormatter;
        SQLServerBulkRecord.loggerExternal.exiting(this.loggerPackageName, "setTimestampWithTimezoneFormat");
    }
    
    @Override
    public void setTimeWithTimezoneFormat(final String timeFormat) {
        SQLServerBulkRecord.loggerExternal.entering(this.loggerPackageName, "setTimeWithTimezoneFormat", timeFormat);
        this.timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        SQLServerBulkRecord.loggerExternal.exiting(this.loggerPackageName, "setTimeWithTimezoneFormat");
    }
    
    @Override
    public void setTimeWithTimezoneFormat(final DateTimeFormatter dateTimeFormatter) {
        if (SQLServerBulkRecord.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkRecord.loggerExternal.entering(this.loggerPackageName, "setTimeWithTimezoneFormat", new Object[] { dateTimeFormatter });
        }
        this.timeFormatter = dateTimeFormatter;
        SQLServerBulkRecord.loggerExternal.exiting(this.loggerPackageName, "setTimeWithTimezoneFormat");
    }
    
    protected void throwInvalidArgument(final String argument) throws SQLServerException {
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
        final Object[] msgArgs = { argument };
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
    }
    
    protected void checkDuplicateColumnName(final int positionInTable, final String colName) throws SQLServerException {
        if (null != colName && colName.trim().length() != 0) {
            for (final Map.Entry<Integer, ColumnMetadata> entry : this.columnMetadata.entrySet()) {
                if (null != entry && entry.getKey() != positionInTable && null != entry.getValue() && colName.trim().equalsIgnoreCase(entry.getValue().columnName)) {
                    throw new SQLServerException(SQLServerException.getErrString("R_BulkDataDuplicateColumn"), (Throwable)null);
                }
            }
        }
    }
    
    @Override
    public DateTimeFormatter getColumnDateTimeFormatter(final int column) {
        return this.columnMetadata.get(column).dateTimeFormatter;
    }
    
    @Override
    public Set<Integer> getColumnOrdinals() {
        return this.columnMetadata.keySet();
    }
    
    @Override
    public String getColumnName(final int column) {
        return this.columnMetadata.get(column).columnName;
    }
    
    @Override
    public int getColumnType(final int column) {
        return this.columnMetadata.get(column).columnType;
    }
    
    @Override
    public int getPrecision(final int column) {
        return this.columnMetadata.get(column).precision;
    }
    
    @Override
    public int getScale(final int column) {
        return this.columnMetadata.get(column).scale;
    }
    
    @Override
    public boolean isAutoIncrement(final int column) {
        return false;
    }
    
    static {
        SQLServerBulkRecord.loggerExternal = Logger.getLogger("com.microsoft.jdbc.SQLServerBulkRecord");
    }
    
    protected class ColumnMetadata
    {
        String columnName;
        int columnType;
        int precision;
        int scale;
        DateTimeFormatter dateTimeFormatter;
        
        ColumnMetadata(final String name, final int type, final int precision, final int scale, final DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = null;
            this.columnName = name;
            this.columnType = type;
            this.precision = precision;
            this.scale = scale;
            this.dateTimeFormatter = dateTimeFormatter;
        }
    }
}
