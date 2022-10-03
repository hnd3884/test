package com.microsoft.sqlserver.jdbc;

enum DriverError
{
    NOT_SET(0);
    
    private final int errorCode;
    
    final int getErrorCode() {
        return this.errorCode;
    }
    
    private DriverError(final int errorCode) {
        this.errorCode = errorCode;
    }
}
