package com.microsoft.sqlserver.jdbc;

public abstract class SQLServerColumnEncryptionKeyStoreProvider
{
    public abstract void setName(final String p0);
    
    public abstract String getName();
    
    public abstract byte[] decryptColumnEncryptionKey(final String p0, final String p1, final byte[] p2) throws SQLServerException;
    
    public abstract byte[] encryptColumnEncryptionKey(final String p0, final String p1, final byte[] p2) throws SQLServerException;
    
    public abstract boolean verifyColumnMasterKeyMetadata(final String p0, final boolean p1, final byte[] p2) throws SQLServerException;
}
