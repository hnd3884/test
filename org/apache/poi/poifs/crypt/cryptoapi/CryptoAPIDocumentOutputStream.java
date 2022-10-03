package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.EncryptedDocumentException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.apache.poi.util.Internal;
import java.io.ByteArrayOutputStream;

@Internal
class CryptoAPIDocumentOutputStream extends ByteArrayOutputStream
{
    private final Cipher cipher;
    private final CryptoAPIEncryptor encryptor;
    private final byte[] oneByte;
    
    public CryptoAPIDocumentOutputStream(final CryptoAPIEncryptor encryptor) throws GeneralSecurityException {
        this.oneByte = new byte[] { 0 };
        this.encryptor = encryptor;
        this.cipher = encryptor.initCipherForBlock(null, 0);
    }
    
    public byte[] getBuf() {
        return this.buf;
    }
    
    public void setSize(final int count) {
        this.count = count;
    }
    
    public void setBlock(final int block) throws GeneralSecurityException {
        this.encryptor.initCipherForBlock(this.cipher, block);
    }
    
    @Override
    public synchronized void write(final int b) {
        try {
            this.oneByte[0] = (byte)b;
            this.cipher.update(this.oneByte, 0, 1, this.oneByte, 0);
            super.write(this.oneByte);
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }
    
    @Override
    public synchronized void write(final byte[] b, final int off, final int len) {
        try {
            this.cipher.update(b, off, len, b, off);
            super.write(b, off, len);
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }
}
