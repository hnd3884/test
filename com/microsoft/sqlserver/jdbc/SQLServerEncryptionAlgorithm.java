package com.microsoft.sqlserver.jdbc;

abstract class SQLServerEncryptionAlgorithm
{
    abstract byte[] encryptData(final byte[] p0) throws SQLServerException;
    
    abstract byte[] decryptData(final byte[] p0) throws SQLServerException;
}
