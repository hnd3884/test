package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.ExtendedDigest;

public class MD2Digest implements ExtendedDigest, Memoable
{
    private static final int DIGEST_LENGTH = 16;
    private byte[] X;
    private int xOff;
    private byte[] M;
    private int mOff;
    private byte[] C;
    private int COff;
    private static final byte[] S;
    
    public MD2Digest() {
        this.X = new byte[48];
        this.M = new byte[16];
        this.C = new byte[16];
        this.reset();
    }
    
    public MD2Digest(final MD2Digest md2Digest) {
        this.X = new byte[48];
        this.M = new byte[16];
        this.C = new byte[16];
        this.copyIn(md2Digest);
    }
    
    private void copyIn(final MD2Digest md2Digest) {
        System.arraycopy(md2Digest.X, 0, this.X, 0, md2Digest.X.length);
        this.xOff = md2Digest.xOff;
        System.arraycopy(md2Digest.M, 0, this.M, 0, md2Digest.M.length);
        this.mOff = md2Digest.mOff;
        System.arraycopy(md2Digest.C, 0, this.C, 0, md2Digest.C.length);
        this.COff = md2Digest.COff;
    }
    
    public String getAlgorithmName() {
        return "MD2";
    }
    
    public int getDigestSize() {
        return 16;
    }
    
    public int doFinal(final byte[] array, final int n) {
        final byte b = (byte)(this.M.length - this.mOff);
        for (int i = this.mOff; i < this.M.length; ++i) {
            this.M[i] = b;
        }
        this.processCheckSum(this.M);
        this.processBlock(this.M);
        this.processBlock(this.C);
        System.arraycopy(this.X, this.xOff, array, n, 16);
        this.reset();
        return 16;
    }
    
    public void reset() {
        this.xOff = 0;
        for (int i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
        this.mOff = 0;
        for (int j = 0; j != this.M.length; ++j) {
            this.M[j] = 0;
        }
        this.COff = 0;
        for (int k = 0; k != this.C.length; ++k) {
            this.C[k] = 0;
        }
    }
    
    public void update(final byte b) {
        this.M[this.mOff++] = b;
        if (this.mOff == 16) {
            this.processCheckSum(this.M);
            this.processBlock(this.M);
            this.mOff = 0;
        }
    }
    
    public void update(final byte[] array, int n, int i) {
        while (this.mOff != 0 && i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
        while (i > 16) {
            System.arraycopy(array, n, this.M, 0, 16);
            this.processCheckSum(this.M);
            this.processBlock(this.M);
            i -= 16;
            n += 16;
        }
        while (i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
    }
    
    protected void processCheckSum(final byte[] array) {
        byte b = this.C[15];
        for (int i = 0; i < 16; ++i) {
            final byte[] c = this.C;
            final int n = i;
            c[n] ^= MD2Digest.S[(array[i] ^ b) & 0xFF];
            b = this.C[i];
        }
    }
    
    protected void processBlock(final byte[] array) {
        for (int i = 0; i < 16; ++i) {
            this.X[i + 16] = array[i];
            this.X[i + 32] = (byte)(array[i] ^ this.X[i]);
        }
        int n = 0;
        for (int j = 0; j < 18; ++j) {
            for (int k = 0; k < 48; ++k) {
                final byte[] x = this.X;
                final int n2 = k;
                final byte b = (byte)(x[n2] ^ MD2Digest.S[n]);
                x[n2] = b;
                n = (b & 0xFF);
            }
            n = (n + j) % 256;
        }
    }
    
    public int getByteLength() {
        return 16;
    }
    
    public Memoable copy() {
        return new MD2Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        this.copyIn((MD2Digest)memoable);
    }
    
    static {
        S = new byte[] { 41, 46, 67, -55, -94, -40, 124, 1, 61, 54, 84, -95, -20, -16, 6, 19, 98, -89, 5, -13, -64, -57, 115, -116, -104, -109, 43, -39, -68, 76, -126, -54, 30, -101, 87, 60, -3, -44, -32, 22, 103, 66, 111, 24, -118, 23, -27, 18, -66, 78, -60, -42, -38, -98, -34, 73, -96, -5, -11, -114, -69, 47, -18, 122, -87, 104, 121, -111, 21, -78, 7, 63, -108, -62, 16, -119, 11, 34, 95, 33, -128, 127, 93, -102, 90, -112, 50, 39, 53, 62, -52, -25, -65, -9, -105, 3, -1, 25, 48, -77, 72, -91, -75, -47, -41, 94, -110, 42, -84, 86, -86, -58, 79, -72, 56, -46, -106, -92, 125, -74, 118, -4, 107, -30, -100, 116, 4, -15, 69, -99, 112, 89, 100, 113, -121, 32, -122, 91, -49, 101, -26, 45, -88, 2, 27, 96, 37, -83, -82, -80, -71, -10, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, -93, 35, -35, 81, -81, 58, -61, 92, -7, -50, -70, -59, -22, 38, 44, 83, 13, 110, -123, 40, -124, 9, -45, -33, -51, -12, 65, -127, 77, 82, 106, -36, 55, -56, 108, -63, -85, -6, 36, -31, 123, 8, 12, -67, -79, 74, 120, -120, -107, -117, -29, 99, -24, 109, -23, -53, -43, -2, 59, 0, 29, 57, -14, -17, -73, 14, 102, 88, -48, -28, -90, 119, 114, -8, -21, 117, 75, 10, 49, 68, 80, -76, -113, -19, 31, 26, -37, -103, -115, 51, -97, 17, -125, 20 };
    }
}
