package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class PCBC extends FeedbackCipher
{
    private final byte[] k;
    private byte[] kSave;
    
    PCBC(final SymmetricCipher symmetricCipher) {
        super(symmetricCipher);
        this.kSave = null;
        this.k = new byte[this.blockSize];
    }
    
    @Override
    String getFeedback() {
        return "PCBC";
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
        System.arraycopy(this.iv, 0, this.k, 0, this.blockSize);
    }
    
    @Override
    void save() {
        if (this.kSave == null) {
            this.kSave = new byte[this.blockSize];
        }
        System.arraycopy(this.k, 0, this.kSave, 0, this.blockSize);
    }
    
    @Override
    void restore() {
        System.arraycopy(this.kSave, 0, this.k, 0, this.blockSize);
    }
    
    @Override
    int encrypt(final byte[] array, int i, final int n, final byte[] array2, int n2) {
        RangeUtil.blockSizeCheck(n, this.blockSize);
        RangeUtil.nullAndBoundsCheck(array, i, n);
        RangeUtil.nullAndBoundsCheck(array2, n2, n);
        while (i < i + n) {
            for (int j = 0; j < this.blockSize; ++j) {
                final byte[] k = this.k;
                final int n3 = j;
                k[n3] ^= array[j + i];
            }
            this.embeddedCipher.encryptBlock(this.k, 0, array2, n2);
            for (int l = 0; l < this.blockSize; ++l) {
                this.k[l] = (byte)(array[l + i] ^ array2[l + n2]);
            }
            i += this.blockSize;
            n2 += this.blockSize;
        }
        return n;
    }
    
    @Override
    int decrypt(final byte[] array, int i, final int n, final byte[] array2, int n2) {
        RangeUtil.blockSizeCheck(n, this.blockSize);
        RangeUtil.nullAndBoundsCheck(array, i, n);
        RangeUtil.nullAndBoundsCheck(array2, n2, n);
        while (i < i + n) {
            this.embeddedCipher.decryptBlock(array, i, array2, n2);
            for (int j = 0; j < this.blockSize; ++j) {
                final int n3 = j + n2;
                array2[n3] ^= this.k[j];
            }
            for (int k = 0; k < this.blockSize; ++k) {
                this.k[k] = (byte)(array2[k + n2] ^ array[k + i]);
            }
            n2 += this.blockSize;
            i += this.blockSize;
        }
        return n;
    }
}
