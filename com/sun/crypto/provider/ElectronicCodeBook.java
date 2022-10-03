package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class ElectronicCodeBook extends FeedbackCipher
{
    ElectronicCodeBook(final SymmetricCipher symmetricCipher) {
        super(symmetricCipher);
    }
    
    @Override
    String getFeedback() {
        return "ECB";
    }
    
    @Override
    void reset() {
    }
    
    @Override
    void save() {
    }
    
    @Override
    void restore() {
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array, final byte[] array2) throws InvalidKeyException {
        if (array == null || array2 != null) {
            throw new InvalidKeyException("Internal error");
        }
        this.embeddedCipher.init(b, s, array);
    }
    
    @Override
    int encrypt(final byte[] array, int n, final int n2, final byte[] array2, int n3) {
        RangeUtil.blockSizeCheck(n2, this.blockSize);
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        for (int i = n2; i >= this.blockSize; i -= this.blockSize) {
            this.embeddedCipher.encryptBlock(array, n, array2, n3);
            n += this.blockSize;
            n3 += this.blockSize;
        }
        return n2;
    }
    
    @Override
    int decrypt(final byte[] array, int n, final int n2, final byte[] array2, int n3) {
        RangeUtil.blockSizeCheck(n2, this.blockSize);
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        for (int i = n2; i >= this.blockSize; i -= this.blockSize) {
            this.embeddedCipher.decryptBlock(array, n, array2, n3);
            n += this.blockSize;
            n3 += this.blockSize;
        }
        return n2;
    }
}
