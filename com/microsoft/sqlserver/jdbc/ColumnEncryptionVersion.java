package com.microsoft.sqlserver.jdbc;

enum ColumnEncryptionVersion
{
    AE_NotSupported, 
    AE_v1, 
    AE_v2;
    
    int value() {
        return this.ordinal() + 1;
    }
}
