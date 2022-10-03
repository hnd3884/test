package com.microsoft.sqlserver.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class CekTableEntry
{
    private static final Logger aeLogger;
    List<EncryptionKeyInfo> columnEncryptionKeyValues;
    int ordinal;
    int databaseId;
    int cekId;
    int cekVersion;
    byte[] cekMdVersion;
    
    List<EncryptionKeyInfo> getColumnEncryptionKeyValues() {
        return this.columnEncryptionKeyValues;
    }
    
    int getOrdinal() {
        return this.ordinal;
    }
    
    int getDatabaseId() {
        return this.databaseId;
    }
    
    int getCekId() {
        return this.cekId;
    }
    
    int getCekVersion() {
        return this.cekVersion;
    }
    
    byte[] getCekMdVersion() {
        return this.cekMdVersion;
    }
    
    CekTableEntry(final int ordinalVal) {
        this.ordinal = ordinalVal;
        this.databaseId = 0;
        this.cekId = 0;
        this.cekVersion = 0;
        this.cekMdVersion = null;
        this.columnEncryptionKeyValues = new ArrayList<EncryptionKeyInfo>();
    }
    
    int getSize() {
        return this.columnEncryptionKeyValues.size();
    }
    
    void add(final byte[] encryptedKey, final int dbId, final int keyId, final int keyVersion, final byte[] mdVersion, final String keyPath, final String keyStoreName, final String algorithmName) {
        assert null != this.columnEncryptionKeyValues : "columnEncryptionKeyValues should already be initialized.";
        CekTableEntry.aeLogger.fine("Retrieving CEK values");
        final EncryptionKeyInfo encryptionKey = new EncryptionKeyInfo(encryptedKey, dbId, keyId, keyVersion, mdVersion, keyPath, keyStoreName, algorithmName);
        this.columnEncryptionKeyValues.add(encryptionKey);
        if (0 == this.databaseId) {
            this.databaseId = dbId;
            this.cekId = keyId;
            this.cekVersion = keyVersion;
            this.cekMdVersion = mdVersion;
        }
        else {
            assert this.databaseId == dbId;
            assert this.cekId == keyId;
            assert this.cekVersion == keyVersion;
            assert null != this.cekMdVersion && null != mdVersion && this.cekMdVersion.length == mdVersion.length;
        }
    }
    
    static {
        aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.AE");
    }
}
