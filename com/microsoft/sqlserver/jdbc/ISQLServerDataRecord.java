package com.microsoft.sqlserver.jdbc;

public interface ISQLServerDataRecord
{
    SQLServerMetaData getColumnMetaData(final int p0);
    
    int getColumnCount();
    
    Object[] getRowData();
    
    boolean next();
}
