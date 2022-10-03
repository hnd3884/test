package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.sql.ResultSetMetaData;

public interface ISQLServerResultSetMetaData extends ResultSetMetaData, Serializable
{
    boolean isSparseColumnSet(final int p0) throws SQLServerException;
}
