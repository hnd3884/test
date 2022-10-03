package com.lowagie.text.pdf;

import com.lowagie.text.pdf.crypto.AESCipher;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;

public class StandardDecryption
{
    protected ARCFOUREncryption arcfour;
    protected AESCipher cipher;
    private byte[] key;
    private static final int AES_128 = 4;
    private boolean aes;
    private boolean initiated;
    private byte[] iv;
    private int ivptr;
    
    public StandardDecryption(final byte[] key, final int off, final int len, final int revision) {
        this.iv = new byte[16];
        this.aes = (revision == 4);
        if (this.aes) {
            System.arraycopy(key, off, this.key = new byte[len], 0, len);
        }
        else {
            (this.arcfour = new ARCFOUREncryption()).prepareARCFOURKey(key, off, len);
        }
    }
    
    public byte[] update(final byte[] b, int off, int len) {
        if (!this.aes) {
            final byte[] b2 = new byte[len];
            this.arcfour.encryptARCFOUR(b, off, len, b2, 0);
            return b2;
        }
        if (this.initiated) {
            return this.cipher.update(b, off, len);
        }
        final int left = Math.min(this.iv.length - this.ivptr, len);
        System.arraycopy(b, off, this.iv, this.ivptr, left);
        off += left;
        len -= left;
        this.ivptr += left;
        if (this.ivptr == this.iv.length) {
            this.cipher = new AESCipher(false, this.key, this.iv);
            this.initiated = true;
            if (len > 0) {
                return this.cipher.update(b, off, len);
            }
        }
        return null;
    }
    
    public byte[] finish() {
        if (this.aes) {
            return this.cipher.doFinal();
        }
        return null;
    }
}
