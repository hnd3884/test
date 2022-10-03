package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.ExtendedDigest;

public class GOST3411Digest implements ExtendedDigest, Memoable
{
    private static final int DIGEST_LENGTH = 32;
    private byte[] H;
    private byte[] L;
    private byte[] M;
    private byte[] Sum;
    private byte[][] C;
    private byte[] xBuf;
    private int xBufOff;
    private long byteCount;
    private BlockCipher cipher;
    private byte[] sBox;
    private byte[] K;
    byte[] a;
    short[] wS;
    short[] w_S;
    byte[] S;
    byte[] U;
    byte[] V;
    byte[] W;
    private static final byte[] C2;
    
    public GOST3411Digest() {
        this.H = new byte[32];
        this.L = new byte[32];
        this.M = new byte[32];
        this.Sum = new byte[32];
        this.C = new byte[4][32];
        this.xBuf = new byte[32];
        this.cipher = new GOST28147Engine();
        this.K = new byte[32];
        this.a = new byte[8];
        this.wS = new short[16];
        this.w_S = new short[16];
        this.S = new byte[32];
        this.U = new byte[32];
        this.V = new byte[32];
        this.W = new byte[32];
        this.sBox = GOST28147Engine.getSBox("D-A");
        this.cipher.init(true, new ParametersWithSBox(null, this.sBox));
        this.reset();
    }
    
    public GOST3411Digest(final byte[] array) {
        this.H = new byte[32];
        this.L = new byte[32];
        this.M = new byte[32];
        this.Sum = new byte[32];
        this.C = new byte[4][32];
        this.xBuf = new byte[32];
        this.cipher = new GOST28147Engine();
        this.K = new byte[32];
        this.a = new byte[8];
        this.wS = new short[16];
        this.w_S = new short[16];
        this.S = new byte[32];
        this.U = new byte[32];
        this.V = new byte[32];
        this.W = new byte[32];
        this.sBox = Arrays.clone(array);
        this.cipher.init(true, new ParametersWithSBox(null, this.sBox));
        this.reset();
    }
    
    public GOST3411Digest(final GOST3411Digest gost3411Digest) {
        this.H = new byte[32];
        this.L = new byte[32];
        this.M = new byte[32];
        this.Sum = new byte[32];
        this.C = new byte[4][32];
        this.xBuf = new byte[32];
        this.cipher = new GOST28147Engine();
        this.K = new byte[32];
        this.a = new byte[8];
        this.wS = new short[16];
        this.w_S = new short[16];
        this.S = new byte[32];
        this.U = new byte[32];
        this.V = new byte[32];
        this.W = new byte[32];
        this.reset(gost3411Digest);
    }
    
    public String getAlgorithmName() {
        return "GOST3411";
    }
    
    public int getDigestSize() {
        return 32;
    }
    
    public void update(final byte b) {
        this.xBuf[this.xBufOff++] = b;
        if (this.xBufOff == this.xBuf.length) {
            this.sumByteArray(this.xBuf);
            this.processBlock(this.xBuf, 0);
            this.xBufOff = 0;
        }
        ++this.byteCount;
    }
    
    public void update(final byte[] array, int n, int i) {
        while (this.xBufOff != 0 && i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
        while (i > this.xBuf.length) {
            System.arraycopy(array, n, this.xBuf, 0, this.xBuf.length);
            this.sumByteArray(this.xBuf);
            this.processBlock(this.xBuf, 0);
            n += this.xBuf.length;
            i -= this.xBuf.length;
            this.byteCount += this.xBuf.length;
        }
        while (i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
    }
    
    private byte[] P(final byte[] array) {
        for (int i = 0; i < 8; ++i) {
            this.K[4 * i] = array[i];
            this.K[1 + 4 * i] = array[8 + i];
            this.K[2 + 4 * i] = array[16 + i];
            this.K[3 + 4 * i] = array[24 + i];
        }
        return this.K;
    }
    
    private byte[] A(final byte[] array) {
        for (int i = 0; i < 8; ++i) {
            this.a[i] = (byte)(array[i] ^ array[i + 8]);
        }
        System.arraycopy(array, 8, array, 0, 24);
        System.arraycopy(this.a, 0, array, 24, 8);
        return array;
    }
    
    private void E(final byte[] array, final byte[] array2, final int n, final byte[] array3, final int n2) {
        this.cipher.init(true, new KeyParameter(array));
        this.cipher.processBlock(array3, n2, array2, n);
    }
    
    private void fw(final byte[] array) {
        this.cpyBytesToShort(array, this.wS);
        this.w_S[15] = (short)(this.wS[0] ^ this.wS[1] ^ this.wS[2] ^ this.wS[3] ^ this.wS[12] ^ this.wS[15]);
        System.arraycopy(this.wS, 1, this.w_S, 0, 15);
        this.cpyShortToBytes(this.w_S, array);
    }
    
    protected void processBlock(final byte[] array, final int n) {
        System.arraycopy(array, n, this.M, 0, 32);
        System.arraycopy(this.H, 0, this.U, 0, 32);
        System.arraycopy(this.M, 0, this.V, 0, 32);
        for (int i = 0; i < 32; ++i) {
            this.W[i] = (byte)(this.U[i] ^ this.V[i]);
        }
        this.E(this.P(this.W), this.S, 0, this.H, 0);
        for (int j = 1; j < 4; ++j) {
            final byte[] a = this.A(this.U);
            for (int k = 0; k < 32; ++k) {
                this.U[k] = (byte)(a[k] ^ this.C[j][k]);
            }
            this.V = this.A(this.A(this.V));
            for (int l = 0; l < 32; ++l) {
                this.W[l] = (byte)(this.U[l] ^ this.V[l]);
            }
            this.E(this.P(this.W), this.S, j * 8, this.H, j * 8);
        }
        for (int n2 = 0; n2 < 12; ++n2) {
            this.fw(this.S);
        }
        for (int n3 = 0; n3 < 32; ++n3) {
            this.S[n3] ^= this.M[n3];
        }
        this.fw(this.S);
        for (int n4 = 0; n4 < 32; ++n4) {
            this.S[n4] ^= this.H[n4];
        }
        for (int n5 = 0; n5 < 61; ++n5) {
            this.fw(this.S);
        }
        System.arraycopy(this.S, 0, this.H, 0, this.H.length);
    }
    
    private void finish() {
        Pack.longToLittleEndian(this.byteCount * 8L, this.L, 0);
        while (this.xBufOff != 0) {
            this.update((byte)0);
        }
        this.processBlock(this.L, 0);
        this.processBlock(this.Sum, 0);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        System.arraycopy(this.H, 0, array, n, this.H.length);
        this.reset();
        return 32;
    }
    
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.H.length; ++i) {
            this.H[i] = 0;
        }
        for (int j = 0; j < this.L.length; ++j) {
            this.L[j] = 0;
        }
        for (int k = 0; k < this.M.length; ++k) {
            this.M[k] = 0;
        }
        for (int l = 0; l < this.C[1].length; ++l) {
            this.C[1][l] = 0;
        }
        for (int n = 0; n < this.C[3].length; ++n) {
            this.C[3][n] = 0;
        }
        for (int n2 = 0; n2 < this.Sum.length; ++n2) {
            this.Sum[n2] = 0;
        }
        for (int n3 = 0; n3 < this.xBuf.length; ++n3) {
            this.xBuf[n3] = 0;
        }
        System.arraycopy(GOST3411Digest.C2, 0, this.C[2], 0, GOST3411Digest.C2.length);
    }
    
    private void sumByteArray(final byte[] array) {
        int n = 0;
        for (int i = 0; i != this.Sum.length; ++i) {
            final int n2 = (this.Sum[i] & 0xFF) + (array[i] & 0xFF) + n;
            this.Sum[i] = (byte)n2;
            n = n2 >>> 8;
        }
    }
    
    private void cpyBytesToShort(final byte[] array, final short[] array2) {
        for (int i = 0; i < array.length / 2; ++i) {
            array2[i] = (short)((array[i * 2 + 1] << 8 & 0xFF00) | (array[i * 2] & 0xFF));
        }
    }
    
    private void cpyShortToBytes(final short[] array, final byte[] array2) {
        for (int i = 0; i < array2.length / 2; ++i) {
            array2[i * 2 + 1] = (byte)(array[i] >> 8);
            array2[i * 2] = (byte)array[i];
        }
    }
    
    public int getByteLength() {
        return 32;
    }
    
    public Memoable copy() {
        return new GOST3411Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        final GOST3411Digest gost3411Digest = (GOST3411Digest)memoable;
        this.sBox = gost3411Digest.sBox;
        this.cipher.init(true, new ParametersWithSBox(null, this.sBox));
        this.reset();
        System.arraycopy(gost3411Digest.H, 0, this.H, 0, gost3411Digest.H.length);
        System.arraycopy(gost3411Digest.L, 0, this.L, 0, gost3411Digest.L.length);
        System.arraycopy(gost3411Digest.M, 0, this.M, 0, gost3411Digest.M.length);
        System.arraycopy(gost3411Digest.Sum, 0, this.Sum, 0, gost3411Digest.Sum.length);
        System.arraycopy(gost3411Digest.C[1], 0, this.C[1], 0, gost3411Digest.C[1].length);
        System.arraycopy(gost3411Digest.C[2], 0, this.C[2], 0, gost3411Digest.C[2].length);
        System.arraycopy(gost3411Digest.C[3], 0, this.C[3], 0, gost3411Digest.C[3].length);
        System.arraycopy(gost3411Digest.xBuf, 0, this.xBuf, 0, gost3411Digest.xBuf.length);
        this.xBufOff = gost3411Digest.xBufOff;
        this.byteCount = gost3411Digest.byteCount;
    }
    
    static {
        C2 = new byte[] { 0, -1, 0, -1, 0, -1, 0, -1, -1, 0, -1, 0, -1, 0, -1, 0, 0, -1, -1, 0, -1, 0, 0, -1, -1, 0, 0, 0, -1, -1, 0, -1 };
    }
}
