package com.microsoft.sqlserver.jdbc;

class CryptoMetadata
{
    TypeInfo baseTypeInfo;
    CekTableEntry cekTableEntry;
    byte cipherAlgorithmId;
    String cipherAlgorithmName;
    SQLServerEncryptionType encryptionType;
    byte normalizationRuleVersion;
    SQLServerEncryptionAlgorithm cipherAlgorithm;
    EncryptionKeyInfo encryptionKeyInfo;
    short ordinal;
    
    CekTableEntry getCekTableEntry() {
        return this.cekTableEntry;
    }
    
    void setCekTableEntry(final CekTableEntry cekTableEntryObj) {
        this.cekTableEntry = cekTableEntryObj;
    }
    
    TypeInfo getBaseTypeInfo() {
        return this.baseTypeInfo;
    }
    
    void setBaseTypeInfo(final TypeInfo baseTypeInfoObj) {
        this.baseTypeInfo = baseTypeInfoObj;
    }
    
    SQLServerEncryptionAlgorithm getEncryptionAlgorithm() {
        return this.cipherAlgorithm;
    }
    
    void setEncryptionAlgorithm(final SQLServerEncryptionAlgorithm encryptionAlgorithmObj) {
        this.cipherAlgorithm = encryptionAlgorithmObj;
    }
    
    EncryptionKeyInfo getEncryptionKeyInfo() {
        return this.encryptionKeyInfo;
    }
    
    void setEncryptionKeyInfo(final EncryptionKeyInfo encryptionKeyInfoObj) {
        this.encryptionKeyInfo = encryptionKeyInfoObj;
    }
    
    byte getEncryptionAlgorithmId() {
        return this.cipherAlgorithmId;
    }
    
    String getEncryptionAlgorithmName() {
        return this.cipherAlgorithmName;
    }
    
    SQLServerEncryptionType getEncryptionType() {
        return this.encryptionType;
    }
    
    byte getNormalizationRuleVersion() {
        return this.normalizationRuleVersion;
    }
    
    short getOrdinal() {
        return this.ordinal;
    }
    
    CryptoMetadata(final CekTableEntry cekTableEntryObj, final short ordinalVal, final byte cipherAlgorithmIdVal, final String cipherAlgorithmNameVal, final byte encryptionTypeVal, final byte normalizationRuleVersionVal) throws SQLServerException {
        this.cipherAlgorithm = null;
        this.cekTableEntry = cekTableEntryObj;
        this.ordinal = ordinalVal;
        this.cipherAlgorithmId = cipherAlgorithmIdVal;
        this.cipherAlgorithmName = cipherAlgorithmNameVal;
        this.encryptionType = SQLServerEncryptionType.of(encryptionTypeVal);
        this.normalizationRuleVersion = normalizationRuleVersionVal;
        this.encryptionKeyInfo = null;
    }
    
    boolean IsAlgorithmInitialized() {
        return null != this.cipherAlgorithm;
    }
}
