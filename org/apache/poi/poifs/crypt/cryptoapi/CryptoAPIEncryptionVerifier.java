package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionHeader;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier;

public class CryptoAPIEncryptionVerifier extends StandardEncryptionVerifier
{
    protected CryptoAPIEncryptionVerifier(final LittleEndianInput is, final CryptoAPIEncryptionHeader header) {
        super(is, header);
    }
    
    protected CryptoAPIEncryptionVerifier(final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        super(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }
    
    protected CryptoAPIEncryptionVerifier(final CryptoAPIEncryptionVerifier other) {
        super(other);
    }
    
    @Override
    protected void setSalt(final byte[] salt) {
        super.setSalt(salt);
    }
    
    @Override
    protected void setEncryptedVerifier(final byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }
    
    @Override
    protected void setEncryptedVerifierHash(final byte[] encryptedVerifierHash) {
        super.setEncryptedVerifierHash(encryptedVerifierHash);
    }
    
    @Override
    public CryptoAPIEncryptionVerifier copy() {
        return new CryptoAPIEncryptionVerifier(this);
    }
}
