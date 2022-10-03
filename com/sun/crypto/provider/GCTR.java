package com.sun.crypto.provider;

import javax.crypto.IllegalBlockSizeException;

final class GCTR extends CounterMode
{
    GCTR(final SymmetricCipher symmetricCipher, final byte[] iv) {
        super(symmetricCipher);
        if (iv.length != 16) {
            throw new RuntimeException("length of initial counter block (" + iv.length + ") not equal to AES_BLOCK_SIZE (" + 16 + ")");
        }
        this.iv = iv;
        this.reset();
    }
    
    @Override
    String getFeedback() {
        return "GCTR";
    }
    
    int update(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n2 - n > array.length) {
            throw new RuntimeException("input length out of bound");
        }
        if (n2 < 0 || n2 % 16 != 0) {
            throw new RuntimeException("input length unsupported");
        }
        if (array2.length - n3 < n2) {
            throw new RuntimeException("output buffer too small");
        }
        return this.encrypt(array, n, n2, array2, n3);
    }
    
    int doFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException {
        try {
            if (n2 < 0) {
                throw new IllegalBlockSizeException("Negative input size!");
            }
            if (n2 > 0) {
                final int n4 = n2 % 16;
                final int n5 = n2 - n4;
                this.update(array, n, n5, array2, n3);
                if (n4 != 0) {
                    final byte[] array3 = new byte[16];
                    this.embeddedCipher.encryptBlock(this.counter, 0, array3, 0);
                    for (int i = 0; i < n4; ++i) {
                        array2[n3 + n5 + i] = (byte)(array[n + n5 + i] ^ array3[i]);
                    }
                }
            }
        }
        finally {
            this.reset();
        }
        return n2;
    }
}
