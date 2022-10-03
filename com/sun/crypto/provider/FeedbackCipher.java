package com.sun.crypto.provider;

import javax.crypto.AEADBadTagException;
import javax.crypto.ShortBufferException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

abstract class FeedbackCipher
{
    final SymmetricCipher embeddedCipher;
    final int blockSize;
    byte[] iv;
    
    FeedbackCipher(final SymmetricCipher embeddedCipher) {
        this.embeddedCipher = embeddedCipher;
        this.blockSize = embeddedCipher.getBlockSize();
    }
    
    final SymmetricCipher getEmbeddedCipher() {
        return this.embeddedCipher;
    }
    
    final int getBlockSize() {
        return this.blockSize;
    }
    
    abstract String getFeedback();
    
    abstract void save();
    
    abstract void restore();
    
    abstract void init(final boolean p0, final String p1, final byte[] p2, final byte[] p3) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    final byte[] getIV() {
        return this.iv;
    }
    
    abstract void reset();
    
    abstract int encrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4);
    
    int encryptFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException {
        return this.encrypt(array, n, n2, array2, n3);
    }
    
    abstract int decrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4);
    
    int decryptFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, AEADBadTagException, ShortBufferException {
        return this.decrypt(array, n, n2, array2, n3);
    }
    
    void updateAAD(final byte[] array, final int n, final int n2) {
        throw new IllegalStateException("No AAD accepted");
    }
    
    int getBufferedLength() {
        return 0;
    }
}
