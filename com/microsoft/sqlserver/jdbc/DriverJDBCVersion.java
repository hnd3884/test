package com.microsoft.sqlserver.jdbc;

import java.sql.BatchUpdateException;

final class DriverJDBCVersion
{
    static final int major = 4;
    static final int minor = 2;
    
    static final void checkSupportsJDBC43() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }
    
    static final void throwBatchUpdateException(final SQLServerException lastError, final long[] updateCounts) throws BatchUpdateException {
        throw new BatchUpdateException(lastError.getMessage(), lastError.getSQLState(), lastError.getErrorCode(), updateCounts, new Throwable(lastError.getMessage()));
    }
}
