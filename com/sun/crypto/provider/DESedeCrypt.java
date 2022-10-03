package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class DESedeCrypt extends DESCrypt implements DESConstants
{
    private byte[] key1;
    private byte[] key2;
    private byte[] key3;
    private byte[] buf1;
    private byte[] buf2;
    
    DESedeCrypt() {
        this.key1 = null;
        this.key2 = null;
        this.key3 = null;
        this.buf1 = new byte[8];
        this.buf2 = new byte[8];
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array) throws InvalidKeyException {
        if (!s.equalsIgnoreCase("DESede") && !s.equalsIgnoreCase("TripleDES")) {
            throw new InvalidKeyException("Wrong algorithm: DESede or TripleDES required");
        }
        if (array.length != 24) {
            throw new InvalidKeyException("Wrong key size");
        }
        final byte[] array2 = new byte[8];
        this.key1 = new byte[128];
        System.arraycopy(array, 0, array2, 0, 8);
        this.expandKey(array2);
        System.arraycopy(this.expandedKey, 0, this.key1, 0, 128);
        if (this.keyEquals(array2, 0, array, 16, 8)) {
            this.key3 = this.key1;
        }
        else {
            this.key3 = new byte[128];
            System.arraycopy(array, 16, array2, 0, 8);
            this.expandKey(array2);
            System.arraycopy(this.expandedKey, 0, this.key3, 0, 128);
        }
        this.key2 = new byte[128];
        System.arraycopy(array, 8, array2, 0, 8);
        this.expandKey(array2);
        System.arraycopy(this.expandedKey, 0, this.key2, 0, 128);
    }
    
    @Override
    void encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        this.expandedKey = this.key1;
        this.decrypting = false;
        this.cipherBlock(array, n, this.buf1, 0);
        this.expandedKey = this.key2;
        this.decrypting = true;
        this.cipherBlock(this.buf1, 0, this.buf2, 0);
        this.expandedKey = this.key3;
        this.decrypting = false;
        this.cipherBlock(this.buf2, 0, array2, n2);
    }
    
    @Override
    void decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        this.expandedKey = this.key3;
        this.decrypting = true;
        this.cipherBlock(array, n, this.buf1, 0);
        this.expandedKey = this.key2;
        this.decrypting = false;
        this.cipherBlock(this.buf1, 0, this.buf2, 0);
        this.expandedKey = this.key1;
        this.decrypting = true;
        this.cipherBlock(this.buf2, 0, array2, n2);
    }
    
    private boolean keyEquals(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) {
        for (int i = 0; i < n3; ++i) {
            if (array[i + n] != array2[i + n2]) {
                return false;
            }
        }
        return true;
    }
}
