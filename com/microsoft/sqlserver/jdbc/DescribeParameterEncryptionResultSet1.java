package com.microsoft.sqlserver.jdbc;

enum DescribeParameterEncryptionResultSet1
{
    KeyOrdinal, 
    DbId, 
    KeyId, 
    KeyVersion, 
    KeyMdVersion, 
    EncryptedKey, 
    ProviderName, 
    KeyPath, 
    KeyEncryptionAlgorithm, 
    IsRequestedByEnclave, 
    EnclaveCMKSignature;
    
    int value() {
        return this.ordinal() + 1;
    }
}
