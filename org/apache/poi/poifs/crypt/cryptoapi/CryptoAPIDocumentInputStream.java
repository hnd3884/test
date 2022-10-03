package org.apache.poi.poifs.crypt.cryptoapi;

import javax.crypto.ShortBufferException;
import org.apache.poi.EncryptedDocumentException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.apache.poi.util.Internal;
import java.io.ByteArrayInputStream;

@Internal
class CryptoAPIDocumentInputStream extends ByteArrayInputStream
{
    private Cipher cipher;
    private final CryptoAPIDecryptor decryptor;
    private byte[] oneByte;
    
    public void seek(final int newpos) {
        if (newpos > this.count) {
            throw new ArrayIndexOutOfBoundsException(newpos);
        }
        this.pos = newpos;
        this.mark = newpos;
    }
    
    public void setBlock(final int block) throws GeneralSecurityException {
        this.cipher = this.decryptor.initCipherForBlock(this.cipher, block);
    }
    
    @Override
    public synchronized int read() {
        final int ch = super.read();
        if (ch == -1) {
            return -1;
        }
        this.oneByte[0] = (byte)ch;
        try {
            this.cipher.update(this.oneByte, 0, 1, this.oneByte);
        }
        catch (final ShortBufferException e) {
            throw new EncryptedDocumentException(e);
        }
        return this.oneByte[0] & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] b, final int off, final int len) {
        final int readLen = super.read(b, off, len);
        if (readLen == -1) {
            return -1;
        }
        try {
            this.cipher.update(b, off, readLen, b, off);
        }
        catch (final ShortBufferException e) {
            throw new EncryptedDocumentException(e);
        }
        return readLen;
    }
    
    public CryptoAPIDocumentInputStream(final CryptoAPIDecryptor decryptor, final byte[] buf) throws GeneralSecurityException {
        super(buf);
        this.oneByte = new byte[] { 0 };
        this.decryptor = decryptor;
        this.cipher = decryptor.initCipherForBlock(null, 0);
    }
}
