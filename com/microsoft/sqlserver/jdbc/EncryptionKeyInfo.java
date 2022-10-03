package com.microsoft.sqlserver.jdbc;

class EncryptionKeyInfo
{
    byte[] encryptedKey;
    int databaseId;
    int cekId;
    int cekVersion;
    byte[] cekMdVersion;
    String keyPath;
    String keyStoreName;
    String algorithmName;
    byte normalizationRuleVersion;
    
    EncryptionKeyInfo(final byte[] encryptedKeyVal, final int dbId, final int keyId, final int keyVersion, final byte[] mdVersion, final String keyPathVal, final String keyStoreNameVal, final String algorithmNameVal) {
        this.encryptedKey = encryptedKeyVal;
        this.databaseId = dbId;
        this.cekId = keyId;
        this.cekVersion = keyVersion;
        this.cekMdVersion = mdVersion;
        this.keyPath = keyPathVal;
        this.keyStoreName = keyStoreNameVal;
        this.algorithmName = algorithmNameVal;
    }
}
