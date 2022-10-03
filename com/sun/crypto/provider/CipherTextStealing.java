package com.sun.crypto.provider;

import javax.crypto.IllegalBlockSizeException;

final class CipherTextStealing extends CipherBlockChaining
{
    CipherTextStealing(final SymmetricCipher symmetricCipher) {
        super(symmetricCipher);
    }
    
    @Override
    String getFeedback() {
        return "CTS";
    }
    
    @Override
    int encryptFinal(final byte[] array, int n, final int n2, final byte[] array2, int n3) throws IllegalBlockSizeException {
        if (n2 < this.blockSize) {
            throw new IllegalBlockSizeException("input is too short!");
        }
        if (n2 == this.blockSize) {
            this.encrypt(array, n, n2, array2, n3);
        }
        else {
            final int n4 = n2 % this.blockSize;
            if (n4 == 0) {
                this.encrypt(array, n, n2, array2, n3);
                final int n5 = n3 + n2 - this.blockSize;
                final int n6 = n5 - this.blockSize;
                final byte[] array3 = new byte[this.blockSize];
                System.arraycopy(array2, n5, array3, 0, this.blockSize);
                System.arraycopy(array2, n6, array2, n5, this.blockSize);
                System.arraycopy(array3, 0, array2, n6, this.blockSize);
            }
            else {
                final int n7 = n2 - (this.blockSize + n4);
                if (n7 > 0) {
                    this.encrypt(array, n, n7, array2, n3);
                    n += n7;
                    n3 += n7;
                }
                final byte[] array4 = new byte[this.blockSize];
                for (int i = 0; i < this.blockSize; ++i) {
                    array4[i] = (byte)(array[n + i] ^ this.r[i]);
                }
                final byte[] array5 = new byte[this.blockSize];
                this.embeddedCipher.encryptBlock(array4, 0, array5, 0);
                System.arraycopy(array5, 0, array2, n3 + this.blockSize, n4);
                for (int j = 0; j < n4; ++j) {
                    array5[j] ^= array[n + this.blockSize + j];
                }
                this.embeddedCipher.encryptBlock(array5, 0, array2, n3);
            }
        }
        return n2;
    }
    
    @Override
    int decryptFinal(final byte[] array, int n, final int n2, final byte[] array2, int n3) throws IllegalBlockSizeException {
        if (n2 < this.blockSize) {
            throw new IllegalBlockSizeException("input is too short!");
        }
        if (n2 == this.blockSize) {
            this.decrypt(array, n, n2, array2, n3);
        }
        else {
            final int n4 = n2 % this.blockSize;
            if (n4 == 0) {
                final int n5 = n + n2 - this.blockSize;
                final int n6 = n + n2 - 2 * this.blockSize;
                final byte[] array3 = new byte[2 * this.blockSize];
                System.arraycopy(array, n5, array3, 0, this.blockSize);
                System.arraycopy(array, n6, array3, this.blockSize, this.blockSize);
                final int n7 = n2 - 2 * this.blockSize;
                this.decrypt(array, n, n7, array2, n3);
                this.decrypt(array3, 0, 2 * this.blockSize, array2, n3 + n7);
            }
            else {
                final int n8 = n2 - (this.blockSize + n4);
                if (n8 > 0) {
                    this.decrypt(array, n, n8, array2, n3);
                    n += n8;
                    n3 += n8;
                }
                final byte[] array4 = new byte[this.blockSize];
                this.embeddedCipher.decryptBlock(array, n, array4, 0);
                for (int i = 0; i < n4; ++i) {
                    array2[n3 + this.blockSize + i] = (byte)(array[n + this.blockSize + i] ^ array4[i]);
                }
                System.arraycopy(array, n + this.blockSize, array4, 0, n4);
                this.embeddedCipher.decryptBlock(array4, 0, array2, n3);
                for (int j = 0; j < this.blockSize; ++j) {
                    array2[n3 + j] ^= this.r[j];
                }
            }
        }
        return n2;
    }
}
