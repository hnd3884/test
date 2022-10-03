package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA1Digest extends GeneralDigest implements EncodableDigest
{
    private static final int DIGEST_LENGTH = 20;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int[] X;
    private int xOff;
    private static final int Y1 = 1518500249;
    private static final int Y2 = 1859775393;
    private static final int Y3 = -1894007588;
    private static final int Y4 = -899497514;
    
    public SHA1Digest() {
        this.X = new int[80];
        this.reset();
    }
    
    public SHA1Digest(final SHA1Digest sha1Digest) {
        super(sha1Digest);
        this.X = new int[80];
        this.copyIn(sha1Digest);
    }
    
    public SHA1Digest(final byte[] array) {
        super(array);
        this.X = new int[80];
        this.H1 = Pack.bigEndianToInt(array, 16);
        this.H2 = Pack.bigEndianToInt(array, 20);
        this.H3 = Pack.bigEndianToInt(array, 24);
        this.H4 = Pack.bigEndianToInt(array, 28);
        this.H5 = Pack.bigEndianToInt(array, 32);
        this.xOff = Pack.bigEndianToInt(array, 36);
        for (int i = 0; i != this.xOff; ++i) {
            this.X[i] = Pack.bigEndianToInt(array, 40 + i * 4);
        }
    }
    
    private void copyIn(final SHA1Digest sha1Digest) {
        this.H1 = sha1Digest.H1;
        this.H2 = sha1Digest.H2;
        this.H3 = sha1Digest.H3;
        this.H4 = sha1Digest.H4;
        this.H5 = sha1Digest.H5;
        System.arraycopy(sha1Digest.X, 0, this.X, 0, sha1Digest.X.length);
        this.xOff = sha1Digest.xOff;
    }
    
    public String getAlgorithmName() {
        return "SHA-1";
    }
    
    public int getDigestSize() {
        return 20;
    }
    
    @Override
    protected void processWord(final byte[] array, int n) {
        this.X[this.xOff] = (array[n] << 24 | (array[++n] & 0xFF) << 16 | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF));
        if (++this.xOff == 16) {
            this.processBlock();
        }
    }
    
    @Override
    protected void processLength(final long n) {
        if (this.xOff > 14) {
            this.processBlock();
        }
        this.X[14] = (int)(n >>> 32);
        this.X[15] = (int)n;
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        Pack.intToBigEndian(this.H1, array, n);
        Pack.intToBigEndian(this.H2, array, n + 4);
        Pack.intToBigEndian(this.H3, array, n + 8);
        Pack.intToBigEndian(this.H4, array, n + 12);
        Pack.intToBigEndian(this.H5, array, n + 16);
        this.reset();
        return 20;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.H1 = 1732584193;
        this.H2 = -271733879;
        this.H3 = -1732584194;
        this.H4 = 271733878;
        this.H5 = -1009589776;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
    }
    
    private int f(final int n, final int n2, final int n3) {
        return (n & n2) | (~n & n3);
    }
    
    private int h(final int n, final int n2, final int n3) {
        return n ^ n2 ^ n3;
    }
    
    private int g(final int n, final int n2, final int n3) {
        return (n & n2) | (n & n3) | (n2 & n3);
    }
    
    @Override
    protected void processBlock() {
        for (int i = 16; i < 80; ++i) {
            final int n = this.X[i - 3] ^ this.X[i - 8] ^ this.X[i - 14] ^ this.X[i - 16];
            this.X[i] = (n << 1 | n >>> 31);
        }
        int h1 = this.H1;
        int h2 = this.H2;
        int h3 = this.H3;
        int h4 = this.H4;
        int h5 = this.H5;
        int n2 = 0;
        for (int j = 0; j < 4; ++j) {
            final int n3 = h5 + ((h1 << 5 | h1 >>> 27) + this.f(h2, h3, h4) + this.X[n2++] + 1518500249);
            final int n4 = h2 << 30 | h2 >>> 2;
            final int n5 = h4 + ((n3 << 5 | n3 >>> 27) + this.f(h1, n4, h3) + this.X[n2++] + 1518500249);
            final int n6 = h1 << 30 | h1 >>> 2;
            final int n7 = h3 + ((n5 << 5 | n5 >>> 27) + this.f(n3, n6, n4) + this.X[n2++] + 1518500249);
            h5 = (n3 << 30 | n3 >>> 2);
            h2 = n4 + ((n7 << 5 | n7 >>> 27) + this.f(n5, h5, n6) + this.X[n2++] + 1518500249);
            h4 = (n5 << 30 | n5 >>> 2);
            h1 = n6 + ((h2 << 5 | h2 >>> 27) + this.f(n7, h4, h5) + this.X[n2++] + 1518500249);
            h3 = (n7 << 30 | n7 >>> 2);
        }
        for (int k = 0; k < 4; ++k) {
            final int n8 = h5 + ((h1 << 5 | h1 >>> 27) + this.h(h2, h3, h4) + this.X[n2++] + 1859775393);
            final int n9 = h2 << 30 | h2 >>> 2;
            final int n10 = h4 + ((n8 << 5 | n8 >>> 27) + this.h(h1, n9, h3) + this.X[n2++] + 1859775393);
            final int n11 = h1 << 30 | h1 >>> 2;
            final int n12 = h3 + ((n10 << 5 | n10 >>> 27) + this.h(n8, n11, n9) + this.X[n2++] + 1859775393);
            h5 = (n8 << 30 | n8 >>> 2);
            h2 = n9 + ((n12 << 5 | n12 >>> 27) + this.h(n10, h5, n11) + this.X[n2++] + 1859775393);
            h4 = (n10 << 30 | n10 >>> 2);
            h1 = n11 + ((h2 << 5 | h2 >>> 27) + this.h(n12, h4, h5) + this.X[n2++] + 1859775393);
            h3 = (n12 << 30 | n12 >>> 2);
        }
        for (int l = 0; l < 4; ++l) {
            final int n13 = h5 + ((h1 << 5 | h1 >>> 27) + this.g(h2, h3, h4) + this.X[n2++] - 1894007588);
            final int n14 = h2 << 30 | h2 >>> 2;
            final int n15 = h4 + ((n13 << 5 | n13 >>> 27) + this.g(h1, n14, h3) + this.X[n2++] - 1894007588);
            final int n16 = h1 << 30 | h1 >>> 2;
            final int n17 = h3 + ((n15 << 5 | n15 >>> 27) + this.g(n13, n16, n14) + this.X[n2++] - 1894007588);
            h5 = (n13 << 30 | n13 >>> 2);
            h2 = n14 + ((n17 << 5 | n17 >>> 27) + this.g(n15, h5, n16) + this.X[n2++] - 1894007588);
            h4 = (n15 << 30 | n15 >>> 2);
            h1 = n16 + ((h2 << 5 | h2 >>> 27) + this.g(n17, h4, h5) + this.X[n2++] - 1894007588);
            h3 = (n17 << 30 | n17 >>> 2);
        }
        for (int n18 = 0; n18 <= 3; ++n18) {
            final int n19 = h5 + ((h1 << 5 | h1 >>> 27) + this.h(h2, h3, h4) + this.X[n2++] - 899497514);
            final int n20 = h2 << 30 | h2 >>> 2;
            final int n21 = h4 + ((n19 << 5 | n19 >>> 27) + this.h(h1, n20, h3) + this.X[n2++] - 899497514);
            final int n22 = h1 << 30 | h1 >>> 2;
            final int n23 = h3 + ((n21 << 5 | n21 >>> 27) + this.h(n19, n22, n20) + this.X[n2++] - 899497514);
            h5 = (n19 << 30 | n19 >>> 2);
            h2 = n20 + ((n23 << 5 | n23 >>> 27) + this.h(n21, h5, n22) + this.X[n2++] - 899497514);
            h4 = (n21 << 30 | n21 >>> 2);
            h1 = n22 + ((h2 << 5 | h2 >>> 27) + this.h(n23, h4, h5) + this.X[n2++] - 899497514);
            h3 = (n23 << 30 | n23 >>> 2);
        }
        this.H1 += h1;
        this.H2 += h2;
        this.H3 += h3;
        this.H4 += h4;
        this.H5 += h5;
        this.xOff = 0;
        for (int n24 = 0; n24 < 16; ++n24) {
            this.X[n24] = 0;
        }
    }
    
    public Memoable copy() {
        return new SHA1Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        final SHA1Digest sha1Digest = (SHA1Digest)memoable;
        super.copyIn(sha1Digest);
        this.copyIn(sha1Digest);
    }
    
    public byte[] getEncodedState() {
        final byte[] array = new byte[40 + this.xOff * 4];
        super.populateState(array);
        Pack.intToBigEndian(this.H1, array, 16);
        Pack.intToBigEndian(this.H2, array, 20);
        Pack.intToBigEndian(this.H3, array, 24);
        Pack.intToBigEndian(this.H4, array, 28);
        Pack.intToBigEndian(this.H5, array, 32);
        Pack.intToBigEndian(this.xOff, array, 36);
        for (int i = 0; i != this.xOff; ++i) {
            Pack.intToBigEndian(this.X[i], array, 40 + i * 4);
        }
        return array;
    }
}
