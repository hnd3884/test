package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.ExtendedDigest;

public final class WhirlpoolDigest implements ExtendedDigest, Memoable
{
    private static final int BYTE_LENGTH = 64;
    private static final int DIGEST_LENGTH_BYTES = 64;
    private static final int ROUNDS = 10;
    private static final int REDUCTION_POLYNOMIAL = 285;
    private static final int[] SBOX;
    private static final long[] C0;
    private static final long[] C1;
    private static final long[] C2;
    private static final long[] C3;
    private static final long[] C4;
    private static final long[] C5;
    private static final long[] C6;
    private static final long[] C7;
    private final long[] _rc;
    private static final int BITCOUNT_ARRAY_SIZE = 32;
    private byte[] _buffer;
    private int _bufferPos;
    private short[] _bitCount;
    private long[] _hash;
    private long[] _K;
    private long[] _L;
    private long[] _block;
    private long[] _state;
    private static final short[] EIGHT;
    
    public WhirlpoolDigest() {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        for (int i = 0; i < 256; ++i) {
            final int n = WhirlpoolDigest.SBOX[i];
            final int maskWithReductionPolynomial = this.maskWithReductionPolynomial(n << 1);
            final int maskWithReductionPolynomial2 = this.maskWithReductionPolynomial(maskWithReductionPolynomial << 1);
            final int n2 = maskWithReductionPolynomial2 ^ n;
            final int maskWithReductionPolynomial3 = this.maskWithReductionPolynomial(maskWithReductionPolynomial2 << 1);
            final int n3 = maskWithReductionPolynomial3 ^ n;
            WhirlpoolDigest.C0[i] = this.packIntoLong(n, n, maskWithReductionPolynomial2, n, maskWithReductionPolynomial3, n2, maskWithReductionPolynomial, n3);
            WhirlpoolDigest.C1[i] = this.packIntoLong(n3, n, n, maskWithReductionPolynomial2, n, maskWithReductionPolynomial3, n2, maskWithReductionPolynomial);
            WhirlpoolDigest.C2[i] = this.packIntoLong(maskWithReductionPolynomial, n3, n, n, maskWithReductionPolynomial2, n, maskWithReductionPolynomial3, n2);
            WhirlpoolDigest.C3[i] = this.packIntoLong(n2, maskWithReductionPolynomial, n3, n, n, maskWithReductionPolynomial2, n, maskWithReductionPolynomial3);
            WhirlpoolDigest.C4[i] = this.packIntoLong(maskWithReductionPolynomial3, n2, maskWithReductionPolynomial, n3, n, n, maskWithReductionPolynomial2, n);
            WhirlpoolDigest.C5[i] = this.packIntoLong(n, maskWithReductionPolynomial3, n2, maskWithReductionPolynomial, n3, n, n, maskWithReductionPolynomial2);
            WhirlpoolDigest.C6[i] = this.packIntoLong(maskWithReductionPolynomial2, n, maskWithReductionPolynomial3, n2, maskWithReductionPolynomial, n3, n, n);
            WhirlpoolDigest.C7[i] = this.packIntoLong(n, maskWithReductionPolynomial2, n, maskWithReductionPolynomial3, n2, maskWithReductionPolynomial, n3, n);
        }
        this._rc[0] = 0L;
        for (int j = 1; j <= 10; ++j) {
            final int n4 = 8 * (j - 1);
            this._rc[j] = ((WhirlpoolDigest.C0[n4] & 0xFF00000000000000L) ^ (WhirlpoolDigest.C1[n4 + 1] & 0xFF000000000000L) ^ (WhirlpoolDigest.C2[n4 + 2] & 0xFF0000000000L) ^ (WhirlpoolDigest.C3[n4 + 3] & 0xFF00000000L) ^ (WhirlpoolDigest.C4[n4 + 4] & 0xFF000000L) ^ (WhirlpoolDigest.C5[n4 + 5] & 0xFF0000L) ^ (WhirlpoolDigest.C6[n4 + 6] & 0xFF00L) ^ (WhirlpoolDigest.C7[n4 + 7] & 0xFFL));
        }
    }
    
    private long packIntoLong(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        return (long)n << 56 ^ (long)n2 << 48 ^ (long)n3 << 40 ^ (long)n4 << 32 ^ (long)n5 << 24 ^ (long)n6 << 16 ^ (long)n7 << 8 ^ (long)n8;
    }
    
    private int maskWithReductionPolynomial(final int n) {
        int n2 = n;
        if (n2 >= 256L) {
            n2 ^= 0x11D;
        }
        return n2;
    }
    
    public WhirlpoolDigest(final WhirlpoolDigest whirlpoolDigest) {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        this.reset(whirlpoolDigest);
    }
    
    public String getAlgorithmName() {
        return "Whirlpool";
    }
    
    public int getDigestSize() {
        return 64;
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        for (int i = 0; i < 8; ++i) {
            this.convertLongToByteArray(this._hash[i], array, n + i * 8);
        }
        this.reset();
        return this.getDigestSize();
    }
    
    public void reset() {
        this._bufferPos = 0;
        Arrays.fill(this._bitCount, (short)0);
        Arrays.fill(this._buffer, (byte)0);
        Arrays.fill(this._hash, 0L);
        Arrays.fill(this._K, 0L);
        Arrays.fill(this._L, 0L);
        Arrays.fill(this._block, 0L);
        Arrays.fill(this._state, 0L);
    }
    
    private void processFilledBuffer(final byte[] array, final int n) {
        for (int i = 0; i < this._state.length; ++i) {
            this._block[i] = this.bytesToLongFromBuffer(this._buffer, i * 8);
        }
        this.processBlock();
        this._bufferPos = 0;
        Arrays.fill(this._buffer, (byte)0);
    }
    
    private long bytesToLongFromBuffer(final byte[] array, final int n) {
        return ((long)array[n + 0] & 0xFFL) << 56 | ((long)array[n + 1] & 0xFFL) << 48 | ((long)array[n + 2] & 0xFFL) << 40 | ((long)array[n + 3] & 0xFFL) << 32 | ((long)array[n + 4] & 0xFFL) << 24 | ((long)array[n + 5] & 0xFFL) << 16 | ((long)array[n + 6] & 0xFFL) << 8 | ((long)array[n + 7] & 0xFFL);
    }
    
    private void convertLongToByteArray(final long n, final byte[] array, final int n2) {
        for (int i = 0; i < 8; ++i) {
            array[n2 + i] = (byte)(n >> 56 - i * 8 & 0xFFL);
        }
    }
    
    protected void processBlock() {
        for (int i = 0; i < 8; ++i) {
            this._state[i] = (this._block[i] ^ (this._K[i] = this._hash[i]));
        }
        for (int j = 1; j <= 10; ++j) {
            for (int k = 0; k < 8; ++k) {
                this._L[k] = 0L;
                final long[] l = this._L;
                final int n = k;
                l[n] ^= WhirlpoolDigest.C0[(int)(this._K[k - 0 & 0x7] >>> 56) & 0xFF];
                final long[] m = this._L;
                final int n2 = k;
                m[n2] ^= WhirlpoolDigest.C1[(int)(this._K[k - 1 & 0x7] >>> 48) & 0xFF];
                final long[] l2 = this._L;
                final int n3 = k;
                l2[n3] ^= WhirlpoolDigest.C2[(int)(this._K[k - 2 & 0x7] >>> 40) & 0xFF];
                final long[] l3 = this._L;
                final int n4 = k;
                l3[n4] ^= WhirlpoolDigest.C3[(int)(this._K[k - 3 & 0x7] >>> 32) & 0xFF];
                final long[] l4 = this._L;
                final int n5 = k;
                l4[n5] ^= WhirlpoolDigest.C4[(int)(this._K[k - 4 & 0x7] >>> 24) & 0xFF];
                final long[] l5 = this._L;
                final int n6 = k;
                l5[n6] ^= WhirlpoolDigest.C5[(int)(this._K[k - 5 & 0x7] >>> 16) & 0xFF];
                final long[] l6 = this._L;
                final int n7 = k;
                l6[n7] ^= WhirlpoolDigest.C6[(int)(this._K[k - 6 & 0x7] >>> 8) & 0xFF];
                final long[] l7 = this._L;
                final int n8 = k;
                l7[n8] ^= WhirlpoolDigest.C7[(int)this._K[k - 7 & 0x7] & 0xFF];
            }
            System.arraycopy(this._L, 0, this._K, 0, this._K.length);
            final long[] k2 = this._K;
            final int n9 = 0;
            k2[n9] ^= this._rc[j];
            for (int n10 = 0; n10 < 8; ++n10) {
                this._L[n10] = this._K[n10];
                final long[] l8 = this._L;
                final int n11 = n10;
                l8[n11] ^= WhirlpoolDigest.C0[(int)(this._state[n10 - 0 & 0x7] >>> 56) & 0xFF];
                final long[] l9 = this._L;
                final int n12 = n10;
                l9[n12] ^= WhirlpoolDigest.C1[(int)(this._state[n10 - 1 & 0x7] >>> 48) & 0xFF];
                final long[] l10 = this._L;
                final int n13 = n10;
                l10[n13] ^= WhirlpoolDigest.C2[(int)(this._state[n10 - 2 & 0x7] >>> 40) & 0xFF];
                final long[] l11 = this._L;
                final int n14 = n10;
                l11[n14] ^= WhirlpoolDigest.C3[(int)(this._state[n10 - 3 & 0x7] >>> 32) & 0xFF];
                final long[] l12 = this._L;
                final int n15 = n10;
                l12[n15] ^= WhirlpoolDigest.C4[(int)(this._state[n10 - 4 & 0x7] >>> 24) & 0xFF];
                final long[] l13 = this._L;
                final int n16 = n10;
                l13[n16] ^= WhirlpoolDigest.C5[(int)(this._state[n10 - 5 & 0x7] >>> 16) & 0xFF];
                final long[] l14 = this._L;
                final int n17 = n10;
                l14[n17] ^= WhirlpoolDigest.C6[(int)(this._state[n10 - 6 & 0x7] >>> 8) & 0xFF];
                final long[] l15 = this._L;
                final int n18 = n10;
                l15[n18] ^= WhirlpoolDigest.C7[(int)this._state[n10 - 7 & 0x7] & 0xFF];
            }
            System.arraycopy(this._L, 0, this._state, 0, this._state.length);
        }
        for (int n19 = 0; n19 < 8; ++n19) {
            final long[] hash = this._hash;
            final int n20 = n19;
            hash[n20] ^= (this._state[n19] ^ this._block[n19]);
        }
    }
    
    public void update(final byte b) {
        this._buffer[this._bufferPos] = b;
        ++this._bufferPos;
        if (this._bufferPos == this._buffer.length) {
            this.processFilledBuffer(this._buffer, 0);
        }
        this.increment();
    }
    
    private void increment() {
        int n = 0;
        for (int i = this._bitCount.length - 1; i >= 0; --i) {
            final int n2 = (this._bitCount[i] & 0xFF) + WhirlpoolDigest.EIGHT[i] + n;
            n = n2 >>> 8;
            this._bitCount[i] = (short)(n2 & 0xFF);
        }
    }
    
    public void update(final byte[] array, int n, int i) {
        while (i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
    }
    
    private void finish() {
        final byte[] copyBitLength = this.copyBitLength();
        final byte[] buffer = this._buffer;
        final int n = this._bufferPos++;
        buffer[n] |= (byte)128;
        if (this._bufferPos == this._buffer.length) {
            this.processFilledBuffer(this._buffer, 0);
        }
        if (this._bufferPos > 32) {
            while (this._bufferPos != 0) {
                this.update((byte)0);
            }
        }
        while (this._bufferPos <= 32) {
            this.update((byte)0);
        }
        System.arraycopy(copyBitLength, 0, this._buffer, 32, copyBitLength.length);
        this.processFilledBuffer(this._buffer, 0);
    }
    
    private byte[] copyBitLength() {
        final byte[] array = new byte[32];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (byte)(this._bitCount[i] & 0xFF);
        }
        return array;
    }
    
    public int getByteLength() {
        return 64;
    }
    
    public Memoable copy() {
        return new WhirlpoolDigest(this);
    }
    
    public void reset(final Memoable memoable) {
        final WhirlpoolDigest whirlpoolDigest = (WhirlpoolDigest)memoable;
        System.arraycopy(whirlpoolDigest._rc, 0, this._rc, 0, this._rc.length);
        System.arraycopy(whirlpoolDigest._buffer, 0, this._buffer, 0, this._buffer.length);
        this._bufferPos = whirlpoolDigest._bufferPos;
        System.arraycopy(whirlpoolDigest._bitCount, 0, this._bitCount, 0, this._bitCount.length);
        System.arraycopy(whirlpoolDigest._hash, 0, this._hash, 0, this._hash.length);
        System.arraycopy(whirlpoolDigest._K, 0, this._K, 0, this._K.length);
        System.arraycopy(whirlpoolDigest._L, 0, this._L, 0, this._L.length);
        System.arraycopy(whirlpoolDigest._block, 0, this._block, 0, this._block.length);
        System.arraycopy(whirlpoolDigest._state, 0, this._state, 0, this._state.length);
    }
    
    static {
        SBOX = new int[] { 24, 35, 198, 232, 135, 184, 1, 79, 54, 166, 210, 245, 121, 111, 145, 82, 96, 188, 155, 142, 163, 12, 123, 53, 29, 224, 215, 194, 46, 75, 254, 87, 21, 119, 55, 229, 159, 240, 74, 218, 88, 201, 41, 10, 177, 160, 107, 133, 189, 93, 16, 244, 203, 62, 5, 103, 228, 39, 65, 139, 167, 125, 149, 216, 251, 238, 124, 102, 221, 23, 71, 158, 202, 45, 191, 7, 173, 90, 131, 51, 99, 2, 170, 113, 200, 25, 73, 217, 242, 227, 91, 136, 154, 38, 50, 176, 233, 15, 213, 128, 190, 205, 52, 72, 255, 122, 144, 95, 32, 104, 26, 174, 180, 84, 147, 34, 100, 241, 115, 18, 64, 8, 195, 236, 219, 161, 141, 61, 151, 0, 207, 43, 118, 130, 214, 27, 181, 175, 106, 80, 69, 243, 48, 239, 63, 85, 162, 234, 101, 186, 47, 192, 222, 28, 253, 77, 146, 117, 6, 138, 178, 230, 14, 31, 98, 212, 168, 150, 249, 197, 37, 89, 132, 114, 57, 76, 94, 120, 56, 140, 209, 165, 226, 97, 179, 33, 156, 30, 67, 199, 252, 4, 81, 153, 109, 13, 250, 223, 126, 36, 59, 171, 206, 17, 143, 78, 183, 235, 60, 129, 148, 247, 185, 19, 44, 211, 231, 110, 196, 3, 86, 68, 127, 169, 42, 187, 193, 83, 220, 11, 157, 108, 49, 116, 246, 70, 172, 137, 20, 225, 22, 58, 105, 9, 112, 182, 208, 237, 204, 66, 152, 164, 40, 92, 248, 134 };
        C0 = new long[256];
        C1 = new long[256];
        C2 = new long[256];
        C3 = new long[256];
        C4 = new long[256];
        C5 = new long[256];
        C6 = new long[256];
        C7 = new long[256];
        (EIGHT = new short[32])[31] = 8;
    }
}
