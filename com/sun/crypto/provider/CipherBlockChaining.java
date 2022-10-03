package com.sun.crypto.provider;

import java.security.InvalidKeyException;

class CipherBlockChaining extends FeedbackCipher
{
    protected byte[] r;
    private byte[] k;
    private byte[] rSave;
    
    CipherBlockChaining(final SymmetricCipher symmetricCipher) {
        super(symmetricCipher);
        this.rSave = null;
        this.k = new byte[this.blockSize];
        this.r = new byte[this.blockSize];
    }
    
    @Override
    String getFeedback() {
        return "CBC";
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array, final byte[] iv) throws InvalidKeyException {
        if (array == null || iv == null || iv.length != this.blockSize) {
            throw new InvalidKeyException("Internal error");
        }
        this.iv = iv;
        this.reset();
        this.embeddedCipher.init(b, s, array);
    }
    
    @Override
    void reset() {
        System.arraycopy(this.iv, 0, this.r, 0, this.blockSize);
    }
    
    @Override
    void save() {
        if (this.rSave == null) {
            this.rSave = new byte[this.blockSize];
        }
        System.arraycopy(this.r, 0, this.rSave, 0, this.blockSize);
    }
    
    @Override
    void restore() {
        System.arraycopy(this.rSave, 0, this.r, 0, this.blockSize);
    }
    
    @Override
    int encrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n2 <= 0) {
            return n2;
        }
        RangeUtil.blockSizeCheck(n2, this.blockSize);
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        return this.implEncrypt(array, n, n2, array2, n3);
    }
    
    private int implEncrypt(final byte[] array, int i, final int n, final byte[] array2, int n2) {
        while (i < i + n) {
            for (int j = 0; j < this.blockSize; ++j) {
                this.k[j] = (byte)(array[j + i] ^ this.r[j]);
            }
            this.embeddedCipher.encryptBlock(this.k, 0, array2, n2);
            System.arraycopy(array2, n2, this.r, 0, this.blockSize);
            i += this.blockSize;
            n2 += this.blockSize;
        }
        return n;
    }
    
    @Override
    int decrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n2 <= 0) {
            return n2;
        }
        RangeUtil.blockSizeCheck(n2, this.blockSize);
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        return this.implDecrypt(array, n, n2, array2, n3);
    }
    
    private int implDecrypt(final byte[] array, int i, final int n, final byte[] array2, int n2) {
        while (i < i + n) {
            this.embeddedCipher.decryptBlock(array, i, this.k, 0);
            for (int j = 0; j < this.blockSize; ++j) {
                array2[j + n2] = (byte)(this.k[j] ^ this.r[j]);
            }
            System.arraycopy(array, i, this.r, 0, this.blockSize);
            i += this.blockSize;
            n2 += this.blockSize;
        }
        return n;
    }
}
