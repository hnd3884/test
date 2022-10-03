package com.microsoft.sqlserver.jdbc;

class SQLServerSymmetricKey
{
    private byte[] rootKey;
    
    SQLServerSymmetricKey(final byte[] rootKey) throws SQLServerException {
        if (null == rootKey) {
            throw new SQLServerException(this, SQLServerException.getErrString("R_NullColumnEncryptionKey"), null, 0, false);
        }
        if (0 == rootKey.length) {
            throw new SQLServerException(this, SQLServerException.getErrString("R_EmptyColumnEncryptionKey"), null, 0, false);
        }
        this.rootKey = rootKey;
    }
    
    byte[] getRootKey() {
        return this.rootKey;
    }
    
    int length() {
        return this.rootKey.length;
    }
    
    void zeroOutKey() {
        for (int i = 0; i < this.rootKey.length; ++i) {
            this.rootKey[i] = 0;
        }
    }
}
