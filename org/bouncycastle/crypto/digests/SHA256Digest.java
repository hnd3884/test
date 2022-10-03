package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA256Digest extends GeneralDigest implements EncodableDigest
{
    private static final int DIGEST_LENGTH = 32;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private int[] X;
    private int xOff;
    static final int[] K;
    
    public SHA256Digest() {
        this.X = new int[64];
        this.reset();
    }
    
    public SHA256Digest(final SHA256Digest sha256Digest) {
        super(sha256Digest);
        this.X = new int[64];
        this.copyIn(sha256Digest);
    }
    
    private void copyIn(final SHA256Digest sha256Digest) {
        super.copyIn(sha256Digest);
        this.H1 = sha256Digest.H1;
        this.H2 = sha256Digest.H2;
        this.H3 = sha256Digest.H3;
        this.H4 = sha256Digest.H4;
        this.H5 = sha256Digest.H5;
        this.H6 = sha256Digest.H6;
        this.H7 = sha256Digest.H7;
        this.H8 = sha256Digest.H8;
        System.arraycopy(sha256Digest.X, 0, this.X, 0, sha256Digest.X.length);
        this.xOff = sha256Digest.xOff;
    }
    
    public SHA256Digest(final byte[] array) {
        super(array);
        this.X = new int[64];
        this.H1 = Pack.bigEndianToInt(array, 16);
        this.H2 = Pack.bigEndianToInt(array, 20);
        this.H3 = Pack.bigEndianToInt(array, 24);
        this.H4 = Pack.bigEndianToInt(array, 28);
        this.H5 = Pack.bigEndianToInt(array, 32);
        this.H6 = Pack.bigEndianToInt(array, 36);
        this.H7 = Pack.bigEndianToInt(array, 40);
        this.H8 = Pack.bigEndianToInt(array, 44);
        this.xOff = Pack.bigEndianToInt(array, 48);
        for (int i = 0; i != this.xOff; ++i) {
            this.X[i] = Pack.bigEndianToInt(array, 52 + i * 4);
        }
    }
    
    public String getAlgorithmName() {
        return "SHA-256";
    }
    
    public int getDigestSize() {
        return 32;
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
        this.X[15] = (int)(n & -1L);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        Pack.intToBigEndian(this.H1, array, n);
        Pack.intToBigEndian(this.H2, array, n + 4);
        Pack.intToBigEndian(this.H3, array, n + 8);
        Pack.intToBigEndian(this.H4, array, n + 12);
        Pack.intToBigEndian(this.H5, array, n + 16);
        Pack.intToBigEndian(this.H6, array, n + 20);
        Pack.intToBigEndian(this.H7, array, n + 24);
        Pack.intToBigEndian(this.H8, array, n + 28);
        this.reset();
        return 32;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.H1 = 1779033703;
        this.H2 = -1150833019;
        this.H3 = 1013904242;
        this.H4 = -1521486534;
        this.H5 = 1359893119;
        this.H6 = -1694144372;
        this.H7 = 528734635;
        this.H8 = 1541459225;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
    }
    
    @Override
    protected void processBlock() {
        for (int i = 16; i <= 63; ++i) {
            this.X[i] = this.Theta1(this.X[i - 2]) + this.X[i - 7] + this.Theta0(this.X[i - 15]) + this.X[i - 16];
        }
        int h1 = this.H1;
        int h2 = this.H2;
        int h3 = this.H3;
        int h4 = this.H4;
        int h5 = this.H5;
        int h6 = this.H6;
        int h7 = this.H7;
        int h8 = this.H8;
        int n = 0;
        for (int j = 0; j < 8; ++j) {
            final int n2 = h8 + (this.Sum1(h5) + this.Ch(h5, h6, h7) + SHA256Digest.K[n] + this.X[n]);
            final int n3 = h4 + n2;
            final int n4 = n2 + (this.Sum0(h1) + this.Maj(h1, h2, h3));
            ++n;
            final int n5 = h7 + (this.Sum1(n3) + this.Ch(n3, h5, h6) + SHA256Digest.K[n] + this.X[n]);
            final int n6 = h3 + n5;
            final int n7 = n5 + (this.Sum0(n4) + this.Maj(n4, h1, h2));
            ++n;
            final int n8 = h6 + (this.Sum1(n6) + this.Ch(n6, n3, h5) + SHA256Digest.K[n] + this.X[n]);
            final int n9 = h2 + n8;
            final int n10 = n8 + (this.Sum0(n7) + this.Maj(n7, n4, h1));
            ++n;
            final int n11 = h5 + (this.Sum1(n9) + this.Ch(n9, n6, n3) + SHA256Digest.K[n] + this.X[n]);
            final int n12 = h1 + n11;
            final int n13 = n11 + (this.Sum0(n10) + this.Maj(n10, n7, n4));
            ++n;
            final int n14 = n3 + (this.Sum1(n12) + this.Ch(n12, n9, n6) + SHA256Digest.K[n] + this.X[n]);
            h8 = n4 + n14;
            h4 = n14 + (this.Sum0(n13) + this.Maj(n13, n10, n7));
            ++n;
            final int n15 = n6 + (this.Sum1(h8) + this.Ch(h8, n12, n9) + SHA256Digest.K[n] + this.X[n]);
            h7 = n7 + n15;
            h3 = n15 + (this.Sum0(h4) + this.Maj(h4, n13, n10));
            ++n;
            final int n16 = n9 + (this.Sum1(h7) + this.Ch(h7, h8, n12) + SHA256Digest.K[n] + this.X[n]);
            h6 = n10 + n16;
            h2 = n16 + (this.Sum0(h3) + this.Maj(h3, h4, n13));
            ++n;
            final int n17 = n12 + (this.Sum1(h6) + this.Ch(h6, h7, h8) + SHA256Digest.K[n] + this.X[n]);
            h5 = n13 + n17;
            h1 = n17 + (this.Sum0(h2) + this.Maj(h2, h3, h4));
            ++n;
        }
        this.H1 += h1;
        this.H2 += h2;
        this.H3 += h3;
        this.H4 += h4;
        this.H5 += h5;
        this.H6 += h6;
        this.H7 += h7;
        this.H8 += h8;
        this.xOff = 0;
        for (int k = 0; k < 16; ++k) {
            this.X[k] = 0;
        }
    }
    
    private int Ch(final int n, final int n2, final int n3) {
        return (n & n2) ^ (~n & n3);
    }
    
    private int Maj(final int n, final int n2, final int n3) {
        return (n & n2) ^ (n & n3) ^ (n2 & n3);
    }
    
    private int Sum0(final int n) {
        return (n >>> 2 | n << 30) ^ (n >>> 13 | n << 19) ^ (n >>> 22 | n << 10);
    }
    
    private int Sum1(final int n) {
        return (n >>> 6 | n << 26) ^ (n >>> 11 | n << 21) ^ (n >>> 25 | n << 7);
    }
    
    private int Theta0(final int n) {
        return (n >>> 7 | n << 25) ^ (n >>> 18 | n << 14) ^ n >>> 3;
    }
    
    private int Theta1(final int n) {
        return (n >>> 17 | n << 15) ^ (n >>> 19 | n << 13) ^ n >>> 10;
    }
    
    public Memoable copy() {
        return new SHA256Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        this.copyIn((SHA256Digest)memoable);
    }
    
    public byte[] getEncodedState() {
        final byte[] array = new byte[52 + this.xOff * 4];
        super.populateState(array);
        Pack.intToBigEndian(this.H1, array, 16);
        Pack.intToBigEndian(this.H2, array, 20);
        Pack.intToBigEndian(this.H3, array, 24);
        Pack.intToBigEndian(this.H4, array, 28);
        Pack.intToBigEndian(this.H5, array, 32);
        Pack.intToBigEndian(this.H6, array, 36);
        Pack.intToBigEndian(this.H7, array, 40);
        Pack.intToBigEndian(this.H8, array, 44);
        Pack.intToBigEndian(this.xOff, array, 48);
        for (int i = 0; i != this.xOff; ++i) {
            Pack.intToBigEndian(this.X[i], array, 52 + i * 4);
        }
        return array;
    }
    
    static {
        K = new int[] { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
    }
}
