package com.sun.crypto.provider;

import javax.crypto.ShortBufferException;

final class ISO10126Padding implements Padding
{
    private int blockSize;
    
    ISO10126Padding(final int blockSize) {
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
        final byte b = (byte)(n2 & 0xFF);
        final byte[] array2 = new byte[n2 - 1];
        SunJCE.getRandom().nextBytes(array2);
        System.arraycopy(array2, 0, array, n, n2 - 1);
        array[addExact - 1] = b;
    }
    
    @Override
    public int unpad(final byte[] array, final int n, final int n2) {
        if (array == null || n2 == 0) {
            return 0;
        }
        final int addExact = Math.addExact(n, n2);
        final int n3 = array[addExact - 1] & 0xFF;
        if (n3 < 1 || n3 > this.blockSize) {
            return -1;
        }
        final int n4 = addExact - n3;
        if (n4 < n) {
            return -1;
        }
        return n4;
    }
    
    @Override
    public int padLength(final int n) {
        return this.blockSize - n % this.blockSize;
    }
}
