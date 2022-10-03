package com.microsoft.sqlserver.jdbc;

abstract class ColumnFilter
{
    abstract Object apply(final Object p0, final JDBCType p1) throws SQLServerException;
}
