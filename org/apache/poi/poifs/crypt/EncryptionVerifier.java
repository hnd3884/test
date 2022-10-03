package org.apache.poi.poifs.crypt;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;

public abstract class EncryptionVerifier implements GenericRecord, Duplicatable
{
    private byte[] salt;
    private byte[] encryptedVerifier;
    private byte[] encryptedVerifierHash;
    private byte[] encryptedKey;
    private int spinCount;
    private CipherAlgorithm cipherAlgorithm;
    private ChainingMode chainingMode;
    private HashAlgorithm hashAlgorithm;
    
    protected EncryptionVerifier() {
    }
    
    protected EncryptionVerifier(final EncryptionVerifier other) {
        this.salt = (byte[])((other.salt == null) ? null : ((byte[])other.salt.clone()));
        this.encryptedVerifier = (byte[])((other.encryptedVerifier == null) ? null : ((byte[])other.encryptedVerifier.clone()));
        this.encryptedVerifierHash = (byte[])((other.encryptedVerifierHash == null) ? null : ((byte[])other.encryptedVerifierHash.clone()));
        this.encryptedKey = (byte[])((other.encryptedKey == null) ? null : ((byte[])other.encryptedKey.clone()));
        this.spinCount = other.spinCount;
        this.cipherAlgorithm = other.cipherAlgorithm;
        this.chainingMode = other.chainingMode;
        this.hashAlgorithm = other.hashAlgorithm;
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    public byte[] getEncryptedVerifier() {
        return this.encryptedVerifier;
    }
    
    public byte[] getEncryptedVerifierHash() {
        return this.encryptedVerifierHash;
    }
    
    public int getSpinCount() {
        return this.spinCount;
    }
    
    public byte[] getEncryptedKey() {
        return this.encryptedKey;
    }
    
    public CipherAlgorithm getCipherAlgorithm() {
        return this.cipherAlgorithm;
    }
    
    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public ChainingMode getChainingMode() {
        return this.chainingMode;
    }
    
    protected void setSalt(final byte[] salt) {
        this.salt = (byte[])((salt == null) ? null : ((byte[])salt.clone()));
    }
    
    protected void setEncryptedVerifier(final byte[] encryptedVerifier) {
        this.encryptedVerifier = (byte[])((encryptedVerifier == null) ? null : ((byte[])encryptedVerifier.clone()));
    }
    
    protected void setEncryptedVerifierHash(final byte[] encryptedVerifierHash) {
        this.encryptedVerifierHash = (byte[])((encryptedVerifierHash == null) ? null : ((byte[])encryptedVerifierHash.clone()));
    }
    
    protected void setEncryptedKey(final byte[] encryptedKey) {
        this.encryptedKey = (byte[])((encryptedKey == null) ? null : ((byte[])encryptedKey.clone()));
    }
    
    protected void setSpinCount(final int spinCount) {
        this.spinCount = spinCount;
    }
    
    protected void setCipherAlgorithm(final CipherAlgorithm cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }
    
    protected void setChainingMode(final ChainingMode chainingMode) {
        this.chainingMode = chainingMode;
    }
    
    protected void setHashAlgorithm(final HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
    
    @Override
    public abstract EncryptionVerifier copy();
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        final Map<String, Supplier<?>> m = new LinkedHashMap<String, Supplier<?>>();
        m.put("salt", this::getSalt);
        m.put("encryptedVerifier", this::getEncryptedVerifier);
        m.put("encryptedVerifierHash", this::getEncryptedVerifierHash);
        m.put("encryptedKey", this::getEncryptedKey);
        m.put("spinCount", this::getSpinCount);
        m.put("cipherAlgorithm", this::getCipherAlgorithm);
        m.put("chainingMode", this::getChainingMode);
        m.put("hashAlgorithm", this::getHashAlgorithm);
        return Collections.unmodifiableMap((Map<? extends String, ? extends Supplier<?>>)m);
    }
}
