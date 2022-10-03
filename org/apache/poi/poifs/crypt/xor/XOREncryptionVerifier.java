package org.apache.poi.poifs.crypt.xor;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.crypt.EncryptionVerifier;

public class XOREncryptionVerifier extends EncryptionVerifier implements EncryptionRecord
{
    protected XOREncryptionVerifier() {
        this.setEncryptedKey(new byte[2]);
        this.setEncryptedVerifier(new byte[2]);
    }
    
    protected XOREncryptionVerifier(final LittleEndianInput is) {
        final byte[] key = new byte[2];
        is.readFully(key);
        this.setEncryptedKey(key);
        final byte[] verifier = new byte[2];
        is.readFully(verifier);
        this.setEncryptedVerifier(verifier);
    }
    
    protected XOREncryptionVerifier(final XOREncryptionVerifier other) {
        super(other);
    }
    
    @Override
    public void write(final LittleEndianByteArrayOutputStream bos) {
        bos.write(this.getEncryptedKey());
        bos.write(this.getEncryptedVerifier());
    }
    
    @Override
    public XOREncryptionVerifier copy() {
        return new XOREncryptionVerifier(this);
    }
    
    @Override
    protected final void setEncryptedVerifier(final byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }
    
    @Override
    protected final void setEncryptedKey(final byte[] encryptedKey) {
        super.setEncryptedKey(encryptedKey);
    }
}
