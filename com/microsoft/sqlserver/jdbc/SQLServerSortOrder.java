package com.microsoft.sqlserver.jdbc;

public enum SQLServerSortOrder
{
    Ascending(0), 
    Descending(1), 
    Unspecified(-1);
    
    final int value;
    
    private SQLServerSortOrder(final int sortOrderVal) {
        this.value = sortOrderVal;
    }
}
