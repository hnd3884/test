package com.microsoft.sqlserver.jdbc;

final class ParsedSQLCacheItem
{
    String processedSQL;
    int[] parameterPositions;
    String procedureName;
    boolean bReturnValueSyntax;
    
    ParsedSQLCacheItem(final String processedSQL, final int[] parameterPositions, final String procedureName, final boolean bReturnValueSyntax) {
        this.processedSQL = processedSQL;
        this.parameterPositions = parameterPositions;
        this.procedureName = procedureName;
        this.bReturnValueSyntax = bReturnValueSyntax;
    }
}
