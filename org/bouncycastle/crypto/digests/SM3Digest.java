package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Memoable;

public class SM3Digest extends GeneralDigest
{
    private static final int DIGEST_LENGTH = 32;
    private static final int BLOCK_SIZE = 16;
    private int[] V;
    private int[] inwords;
    private int xOff;
    private int[] W;
    private static final int[] T;
    
    public SM3Digest() {
        this.V = new int[8];
        this.inwords = new int[16];
        this.W = new int[68];
        this.reset();
    }
    
    public SM3Digest(final SM3Digest sm3Digest) {
        super(sm3Digest);
        this.V = new int[8];
        this.inwords = new int[16];
        this.W = new int[68];
        this.copyIn(sm3Digest);
    }
    
    private void copyIn(final SM3Digest sm3Digest) {
        System.arraycopy(sm3Digest.V, 0, this.V, 0, this.V.length);
        System.arraycopy(sm3Digest.inwords, 0, this.inwords, 0, this.inwords.length);
        this.xOff = sm3Digest.xOff;
    }
    
    public String getAlgorithmName() {
        return "SM3";
    }
    
    public int getDigestSize() {
        return 32;
    }
    
    public Memoable copy() {
        return new SM3Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        final SM3Digest sm3Digest = (SM3Digest)memoable;
        super.copyIn(sm3Digest);
        this.copyIn(sm3Digest);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.V[0] = 1937774191;
        this.V[1] = 1226093241;
        this.V[2] = 388252375;
        this.V[3] = -628488704;
        this.V[4] = -1452330820;
        this.V[5] = 372324522;
        this.V[6] = -477237683;
        this.V[7] = -1325724082;
        this.xOff = 0;
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        Pack.intToBigEndian(this.V, array, n);
        this.reset();
        return 32;
    }
    
    @Override
    protected void processWord(final byte[] array, int n) {
        this.inwords[this.xOff] = ((array[n] & 0xFF) << 24 | (array[++n] & 0xFF) << 16 | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF));
        ++this.xOff;
        if (this.xOff >= 16) {
            this.processBlock();
        }
    }
    
    @Override
    protected void processLength(final long n) {
        if (this.xOff > 14) {
            this.inwords[this.xOff] = 0;
            ++this.xOff;
            this.processBlock();
        }
        while (this.xOff < 14) {
            this.inwords[this.xOff] = 0;
            ++this.xOff;
        }
        this.inwords[this.xOff++] = (int)(n >>> 32);
        this.inwords[this.xOff++] = (int)n;
    }
    
    private int P0(final int n) {
        return n ^ (n << 9 | n >>> 23) ^ (n << 17 | n >>> 15);
    }
    
    private int P1(final int n) {
        return n ^ (n << 15 | n >>> 17) ^ (n << 23 | n >>> 9);
    }
    
    private int FF0(final int n, final int n2, final int n3) {
        return n ^ n2 ^ n3;
    }
    
    private int FF1(final int n, final int n2, final int n3) {
        return (n & n2) | (n & n3) | (n2 & n3);
    }
    
    private int GG0(final int n, final int n2, final int n3) {
        return n ^ n2 ^ n3;
    }
    
    private int GG1(final int n, final int n2, final int n3) {
        return (n & n2) | (~n & n3);
    }
    
    @Override
    protected void processBlock() {
        for (int i = 0; i < 16; ++i) {
            this.W[i] = this.inwords[i];
        }
        for (int j = 16; j < 68; ++j) {
            final int n = this.W[j - 3];
            final int n2 = n << 15 | n >>> 17;
            final int n3 = this.W[j - 13];
            this.W[j] = (this.P1(this.W[j - 16] ^ this.W[j - 9] ^ n2) ^ (n3 << 7 | n3 >>> 25) ^ this.W[j - 6]);
        }
        int n4 = this.V[0];
        int n5 = this.V[1];
        int n6 = this.V[2];
        int n7 = this.V[3];
        int n8 = this.V[4];
        int n9 = this.V[5];
        int n10 = this.V[6];
        int n11 = this.V[7];
        for (int k = 0; k < 16; ++k) {
            final int n12 = n4 << 12 | n4 >>> 20;
            final int n13 = n12 + n8 + SM3Digest.T[k];
            final int n14 = n13 << 7 | n13 >>> 25;
            final int n15 = n14 ^ n12;
            final int n16 = this.W[k];
            final int n17 = this.FF0(n4, n5, n6) + n7 + n15 + (n16 ^ this.W[k + 4]);
            final int n18 = this.GG0(n8, n9, n10) + n11 + n14 + n16;
            n7 = n6;
            n6 = (n5 << 9 | n5 >>> 23);
            n5 = n4;
            n4 = n17;
            n11 = n10;
            n10 = (n9 << 19 | n9 >>> 13);
            n9 = n8;
            n8 = this.P0(n18);
        }
        for (int l = 16; l < 64; ++l) {
            final int n19 = n4 << 12 | n4 >>> 20;
            final int n20 = n19 + n8 + SM3Digest.T[l];
            final int n21 = n20 << 7 | n20 >>> 25;
            final int n22 = n21 ^ n19;
            final int n23 = this.W[l];
            final int n24 = this.FF1(n4, n5, n6) + n7 + n22 + (n23 ^ this.W[l + 4]);
            final int n25 = this.GG1(n8, n9, n10) + n11 + n21 + n23;
            n7 = n6;
            n6 = (n5 << 9 | n5 >>> 23);
            n5 = n4;
            n4 = n24;
            n11 = n10;
            n10 = (n9 << 19 | n9 >>> 13);
            n9 = n8;
            n8 = this.P0(n25);
        }
        final int[] v = this.V;
        final int n26 = 0;
        v[n26] ^= n4;
        final int[] v2 = this.V;
        final int n27 = 1;
        v2[n27] ^= n5;
        final int[] v3 = this.V;
        final int n28 = 2;
        v3[n28] ^= n6;
        final int[] v4 = this.V;
        final int n29 = 3;
        v4[n29] ^= n7;
        final int[] v5 = this.V;
        final int n30 = 4;
        v5[n30] ^= n8;
        final int[] v6 = this.V;
        final int n31 = 5;
        v6[n31] ^= n9;
        final int[] v7 = this.V;
        final int n32 = 6;
        v7[n32] ^= n10;
        final int[] v8 = this.V;
        final int n33 = 7;
        v8[n33] ^= n11;
        this.xOff = 0;
    }
    
    static {
        T = new int[64];
        for (int i = 0; i < 16; ++i) {
            final int n = 2043430169;
            SM3Digest.T[i] = (n << i | n >>> 32 - i);
        }
        for (int j = 16; j < 64; ++j) {
            final int n2 = j % 32;
            final int n3 = 2055708042;
            SM3Digest.T[j] = (n3 << n2 | n3 >>> 32 - n2);
        }
    }
}
