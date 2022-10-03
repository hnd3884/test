package com.sun.crypto.provider;

import java.util.Arrays;
import javax.crypto.ShortBufferException;

final class PKCS5Padding implements Padding
{
    private int blockSize;
    
    PKCS5Padding(final int blockSize) {
        this.blockSize = blockSize;
    }
    
    @Override
    public void padWithLen(final byte[] array, final int n, final int n2) throws ShortBufferException {
        if (array == null) {
            return;
        }
        final int addExact = Math.addExact(n, n2);
        if (addExact > array.length) {
            throw new ShortBufferException("Buffer too small to hold padding");
        }
        Arrays.fill(array, n, addExact, (byte)(n2 & 0xFF));
    }
    
    @Override
    public int unpad(final byte[] array, final int n, final int n2) {
        if (array == null || n2 == 0) {
            return 0;
        }
        final int addExact = Math.addExact(n, n2);
        final byte b = array[addExact - 1];
        final int n3 = b & 0xFF;
        if (n3 < 1 || n3 > this.blockSize) {
            return -1;
        }
        final int n4 = addExact - n3;
        if (n4 < n) {
            return -1;
        }
        for (int i = n4; i < addExact; ++i) {
            if (array[i] != b) {
                return -1;
            }
        }
        return n4;
    }
    
    @Override
    public int padLength(final int n) {
        return this.blockSize - n % this.blockSize;
    }
}
