package com.sun.crypto.provider;

import java.security.ProviderException;

final class GHASH
{
    private static final int AES_BLOCK_SIZE = 16;
    private final long[] subkeyH;
    private final long[] state;
    private long stateSave0;
    private long stateSave1;
    
    private static long getLong(final byte[] array, final int n) {
        long n2 = 0L;
        for (int n3 = n + 8, i = n; i < n3; ++i) {
            n2 = (n2 << 8) + (array[i] & 0xFF);
        }
        return n2;
    }
    
    private static void putLong(final byte[] array, final int n, long n2) {
        for (int i = n + 8 - 1; i >= n; --i) {
            array[i] = (byte)n2;
            n2 >>= 8;
        }
    }
    
    private static void blockMult(final long[] array, final long[] array2) {
        long n = 0L;
        long n2 = 0L;
        long n3 = array2[0];
        long n4 = array2[1];
        long n5 = array[0];
        for (int i = 0; i < 64; ++i) {
            final long n6 = n5 >> 63;
            n ^= (n3 & n6);
            n2 ^= (n4 & n6);
            final long n7 = n4 << 63 >> 63;
            final long n8 = n3 & 0x1L;
            final long n9 = n3 >>> 1;
            n4 = (n4 >>> 1 | n8 << 63);
            n3 = (n9 ^ (0xE100000000000000L & n7));
            n5 <<= 1;
        }
        long n10 = array[1];
        for (int j = 64; j < 127; ++j) {
            final long n11 = n10 >> 63;
            n ^= (n3 & n11);
            n2 ^= (n4 & n11);
            final long n12 = n4 << 63 >> 63;
            final long n13 = n3 & 0x1L;
            final long n14 = n3 >>> 1;
            n4 = (n4 >>> 1 | n13 << 63);
            n3 = (n14 ^ (0xE100000000000000L & n12));
            n10 <<= 1;
        }
        final long n15 = n10 >> 63;
        final long n16 = n ^ (n3 & n15);
        final long n17 = n2 ^ (n4 & n15);
        array[0] = n16;
        array[1] = n17;
    }
    
    GHASH(final byte[] array) throws ProviderException {
        if (array == null || array.length != 16) {
            throw new ProviderException("Internal error");
        }
        this.state = new long[2];
        (this.subkeyH = new long[2])[0] = getLong(array, 0);
        this.subkeyH[1] = getLong(array, 8);
    }
    
    void reset() {
        this.state[0] = 0L;
        this.state[1] = 0L;
    }
    
    void save() {
        this.stateSave0 = this.state[0];
        this.stateSave1 = this.state[1];
    }
    
    void restore() {
        this.state[0] = this.stateSave0;
        this.state[1] = this.stateSave1;
    }
    
    private static void processBlock(final byte[] array, final int n, final long[] array2, final long[] array3) {
        final int n2 = 0;
        array2[n2] ^= getLong(array, n);
        final int n3 = 1;
        array2[n3] ^= getLong(array, n + 8);
        blockMult(array2, array3);
    }
    
    void update(final byte[] array) {
        this.update(array, 0, array.length);
    }
    
    void update(final byte[] array, final int n, final int n2) {
        if (n2 == 0) {
            return;
        }
        ghashRangeCheck(array, n, n2, this.state, this.subkeyH);
        processBlocks(array, n, n2 / 16, this.state, this.subkeyH);
    }
    
    private static void ghashRangeCheck(final byte[] array, final int n, final int n2, final long[] array2, final long[] array3) {
        if (n2 < 0) {
            throw new RuntimeException("invalid input length: " + n2);
        }
        if (n < 0) {
            throw new RuntimeException("invalid offset: " + n);
        }
        if (n2 > array.length - n) {
            throw new RuntimeException("input length out of bound: " + n2 + " > " + (array.length - n));
        }
        if (n2 % 16 != 0) {
            throw new RuntimeException("input length/block size mismatch: " + n2);
        }
        if (array2.length != 2) {
            throw new RuntimeException("internal state has invalid length: " + array2.length);
        }
        if (array3.length != 2) {
            throw new RuntimeException("internal subkeyH has invalid length: " + array3.length);
        }
    }
    
    private static void processBlocks(final byte[] array, final int n, int i, final long[] array2, final long[] array3) {
        for (int n2 = n; i > 0; --i, n2 += 16) {
            processBlock(array, n2, array2, array3);
        }
    }
    
    byte[] digest() {
        final byte[] array = new byte[16];
        putLong(array, 0, this.state[0]);
        putLong(array, 8, this.state[1]);
        this.reset();
        return array;
    }
}
