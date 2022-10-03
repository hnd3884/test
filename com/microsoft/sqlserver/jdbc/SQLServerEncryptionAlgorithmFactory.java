package com.microsoft.sqlserver.jdbc;

abstract class SQLServerEncryptionAlgorithmFactory
{
    abstract SQLServerEncryptionAlgorithm create(final SQLServerSymmetricKey p0, final SQLServerEncryptionType p1, final String p2) throws SQLServerException;
}
