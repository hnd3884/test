package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;
import java.util.Set;
import java.io.Serializable;

public interface ISQLServerBulkData extends Serializable
{
    Set<Integer> getColumnOrdinals();
    
    String getColumnName(final int p0);
    
    int getColumnType(final int p0);
    
    int getPrecision(final int p0);
    
    int getScale(final int p0);
    
    Object[] getRowData() throws SQLException;
    
    boolean next() throws SQLException;
}
