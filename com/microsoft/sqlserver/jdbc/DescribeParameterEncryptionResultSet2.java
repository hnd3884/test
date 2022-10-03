package com.microsoft.sqlserver.jdbc;

enum DescribeParameterEncryptionResultSet2
{
    ParameterOrdinal, 
    ParameterName, 
    ColumnEncryptionAlgorithm, 
    ColumnEncrytionType, 
    ColumnEncryptionKeyOrdinal, 
    NormalizationRuleVersion;
    
    int value() {
        return this.ordinal() + 1;
    }
}
