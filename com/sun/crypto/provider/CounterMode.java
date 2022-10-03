package com.sun.crypto.provider;

import java.security.InvalidKeyException;

class CounterMode extends FeedbackCipher
{
    final byte[] counter;
    private final byte[] encryptedCounter;
    private int used;
    private byte[] counterSave;
    private byte[] encryptedCounterSave;
    private int usedSave;
    
    CounterMode(final SymmetricCipher symmetricCipher) {
        super(symmetricCipher);
        this.counterSave = null;
        this.encryptedCounterSave = null;
        this.usedSave = 0;
        this.counter = new byte[this.blockSize];
        this.encryptedCounter = new byte[this.blockSize];
    }
    
    @Override
    String getFeedback() {
        return "CTR";
    }
    
    @Override
    void reset() {
        System.arraycopy(this.iv, 0, this.counter, 0, this.blockSize);
        this.used = this.blockSize;
    }
    
    @Override
    void save() {
        if (this.counterSave == null) {
            this.counterSave = new byte[this.blockSize];
            this.encryptedCounterSave = new byte[this.blockSize];
        }
        System.arraycopy(this.counter, 0, this.counterSave, 0, this.blockSize);
        System.arraycopy(this.encryptedCounter, 0, this.encryptedCounterSave, 0, this.blockSize);
        this.usedSave = this.used;
    }
    
    @Override
    void restore() {
        System.arraycopy(this.counterSave, 0, this.counter, 0, this.blockSize);
        System.arraycopy(this.encryptedCounterSave, 0, this.encryptedCounter, 0, this.blockSize);
        this.used = this.usedSave;
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
    int encrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        return this.crypt(array, n, n2, array2, n3);
    }
    
    @Override
    int decrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        return this.crypt(array, n, n2, array2, n3);
    }
    
    private static void increment(final byte[] array) {
        for (int i = array.length - 1; i >= 0; --i) {
            final int n = i;
            if (++array[n] != 0) {
                break;
            }
        }
    }
    
    private int crypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n2 == 0) {
            return 0;
        }
        RangeUtil.nullAndBoundsCheck(array, n, n2);
        RangeUtil.nullAndBoundsCheck(array2, n3, n2);
        return this.implCrypt(array, n, n2, array2, n3);
    }
    
    private int implCrypt(final byte[] array, int n, int n2, final byte[] array2, int n3) {
        final int n4 = n2;
        while (n2-- > 0) {
            if (this.used >= this.blockSize) {
                this.embeddedCipher.encryptBlock(this.counter, 0, this.encryptedCounter, 0);
                increment(this.counter);
                this.used = 0;
            }
            array2[n3++] = (byte)(array[n++] ^ this.encryptedCounter[this.used++]);
        }
        return n4;
    }
}
