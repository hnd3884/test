package com.microsoft.sqlserver.jdbc;

import java.time.format.DateTimeFormatter;

@Deprecated
public interface ISQLServerBulkRecord extends ISQLServerBulkData
{
    boolean isAutoIncrement(final int p0);
    
    void addColumnMetadata(final int p0, final String p1, final int p2, final int p3, final int p4, final DateTimeFormatter p5) throws SQLServerException;
    
    void addColumnMetadata(final int p0, final String p1, final int p2, final int p3, final int p4) throws SQLServerException;
    
    void setTimestampWithTimezoneFormat(final String p0);
    
    void setTimestampWithTimezoneFormat(final DateTimeFormatter p0);
    
    void setTimeWithTimezoneFormat(final String p0);
    
    void setTimeWithTimezoneFormat(final DateTimeFormatter p0);
    
    DateTimeFormatter getColumnDateTimeFormatter(final int p0);
}
