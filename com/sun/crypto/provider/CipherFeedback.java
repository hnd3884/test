package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class CipherFeedback extends FeedbackCipher
{
    private final byte[] k;
    private final byte[] register;
    private int numBytes;
    private byte[] registerSave;
    
    CipherFeedback(final SymmetricCipher symmetricCipher, int blockSize) {
        super(symmetricCipher);
        this.registerSave = null;
        if (blockSize > this.blockSize) {
            blockSize = this.blockSize;
        }
        this.numBytes = blockSize;
        this.k = new byte[this.blockSize];
        this.register = new byte[this.blockSize];
    }
    
    @Override
    String getFeedback() {
        return "CFB";
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array, final byte[] iv) throws InvalidKeyException {
        if (array == null || iv == null || iv.length != this.blockSize) {
            throw new InvalidKeyException("Internal error");
        }
        this.iv = iv;
        this.reset();
        this.embeddedCipher.init(false, s, array);
    }
    
    @Override
    void reset() {
        System.arraycopy(this.iv, 0, this.register, 0, this.blockSize);
    }
    
    @Override
    void save() {
        if (this.registerSave == null) {
            this.registerSave = new byte[this.blockSize];
        }
        System.arraycopy(this.register, 0, this.registerSave, 0, this.blockSize);
    }
    
    @Override
    void restore() {
        System.arraycopy(this.registerSave, 0, this.register, 0, this.blockSize);
    }
    
    @Override
    int encrypt(final byte[] array, int n, final int n2, final byte[] array2, int n3) {
        RangeUtil.blockSizeCheck(n2, this.numBytes);
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        final int n4 = this.blockSize - this.numBytes;
        for (int i = n2 / this.numBytes; i > 0; --i) {
            this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
            if (n4 != 0) {
                System.arraycopy(this.register, this.numBytes, this.register, 0, n4);
            }
            for (int j = 0; j < this.numBytes; ++j) {
                this.register[n4 + j] = (array2[j + n3] = (byte)(this.k[j] ^ array[j + n]));
            }
            n += this.numBytes;
            n3 += this.numBytes;
        }
        return n2;
    }
    
    @Override
    int encryptFinal(final byte[] array, int n, final int n2, final byte[] array2, int n3) {
        final int n4 = n2 % this.numBytes;
        final int encrypt = this.encrypt(array, n, n2 - n4, array2, n3);
        n += encrypt;
        n3 += encrypt;
        if (n4 != 0) {
            this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
            for (int i = 0; i < n4; ++i) {
                array2[i + n3] = (byte)(this.k[i] ^ array[i + n]);
            }
        }
        return n2;
    }
    
    @Override
    int decrypt(final byte[] array, int n, final int n2, final byte[] array2, int n3) {
        RangeUtil.blockSizeCheck(n2, this.numBytes);
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        final int n4 = this.blockSize - this.numBytes;
        for (int i = n2 / this.numBytes; i > 0; --i) {
            this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
            if (n4 != 0) {
                System.arraycopy(this.register, this.numBytes, this.register, 0, n4);
            }
            for (int j = 0; j < this.numBytes; ++j) {
                this.register[j + n4] = array[j + n];
                array2[j + n3] = (byte)(array[j + n] ^ this.k[j]);
            }
            n3 += this.numBytes;
            n += this.numBytes;
        }
        return n2;
    }
    
    @Override
    int decryptFinal(final byte[] array, int n, final int n2, final byte[] array2, int n3) {
        final int n4 = n2 % this.numBytes;
        final int decrypt = this.decrypt(array, n, n2 - n4, array2, n3);
        n += decrypt;
        n3 += decrypt;
        if (n4 != 0) {
            this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
            for (int i = 0; i < n4; ++i) {
                array2[i + n3] = (byte)(array[i + n] ^ this.k[i]);
            }
        }
        return n2;
    }
}
