package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;

public class HC128Engine implements StreamCipher
{
    private int[] p;
    private int[] q;
    private int cnt;
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private byte[] buf;
    private int idx;
    
    public HC128Engine() {
        this.p = new int[512];
        this.q = new int[512];
        this.cnt = 0;
        this.buf = new byte[4];
        this.idx = 0;
    }
    
    private static int f1(final int n) {
        return rotateRight(n, 7) ^ rotateRight(n, 18) ^ n >>> 3;
    }
    
    private static int f2(final int n) {
        return rotateRight(n, 17) ^ rotateRight(n, 19) ^ n >>> 10;
    }
    
    private int g1(final int n, final int n2, final int n3) {
        return (rotateRight(n, 10) ^ rotateRight(n3, 23)) + rotateRight(n2, 8);
    }
    
    private int g2(final int n, final int n2, final int n3) {
        return (rotateLeft(n, 10) ^ rotateLeft(n3, 23)) + rotateLeft(n2, 8);
    }
    
    private static int rotateLeft(final int n, final int n2) {
        return n << n2 | n >>> -n2;
    }
    
    private static int rotateRight(final int n, final int n2) {
        return n >>> n2 | n << -n2;
    }
    
    private int h1(final int n) {
        return this.q[n & 0xFF] + this.q[(n >> 16 & 0xFF) + 256];
    }
    
    private int h2(final int n) {
        return this.p[n & 0xFF] + this.p[(n >> 16 & 0xFF) + 256];
    }
    
    private static int mod1024(final int n) {
        return n & 0x3FF;
    }
    
    private static int mod512(final int n) {
        return n & 0x1FF;
    }
    
    private static int dim(final int n, final int n2) {
        return mod512(n - n2);
    }
    
    private int step() {
        final int mod512 = mod512(this.cnt);
        int n2;
        if (this.cnt < 512) {
            final int[] p = this.p;
            final int n = mod512;
            p[n] += this.g1(this.p[dim(mod512, 3)], this.p[dim(mod512, 10)], this.p[dim(mod512, 511)]);
            n2 = (this.h1(this.p[dim(mod512, 12)]) ^ this.p[mod512]);
        }
        else {
            final int[] q = this.q;
            final int n3 = mod512;
            q[n3] += this.g2(this.q[dim(mod512, 3)], this.q[dim(mod512, 10)], this.q[dim(mod512, 511)]);
            n2 = (this.h2(this.q[dim(mod512, 12)]) ^ this.q[mod512]);
        }
        this.cnt = mod1024(this.cnt + 1);
        return n2;
    }
    
    private void init() {
        if (this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128 bits long");
        }
        this.idx = 0;
        this.cnt = 0;
        final int[] array = new int[1280];
        for (int i = 0; i < 16; ++i) {
            final int[] array2 = array;
            final int n = i >> 2;
            array2[n] |= (this.key[i] & 0xFF) << 8 * (i & 0x3);
        }
        System.arraycopy(array, 0, array, 4, 4);
        for (int n2 = 0; n2 < this.iv.length && n2 < 16; ++n2) {
            final int[] array3 = array;
            final int n3 = (n2 >> 2) + 8;
            array3[n3] |= (this.iv[n2] & 0xFF) << 8 * (n2 & 0x3);
        }
        System.arraycopy(array, 8, array, 12, 4);
        for (int j = 16; j < 1280; ++j) {
            array[j] = f2(array[j - 2]) + array[j - 7] + f1(array[j - 15]) + array[j - 16] + j;
        }
        System.arraycopy(array, 256, this.p, 0, 512);
        System.arraycopy(array, 768, this.q, 0, 512);
        for (int k = 0; k < 512; ++k) {
            this.p[k] = this.step();
        }
        for (int l = 0; l < 512; ++l) {
            this.q[l] = this.step();
        }
        this.cnt = 0;
    }
    
    public String getAlgorithmName() {
        return "HC-128";
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
        CipherParameters parameters = cipherParameters;
        if (cipherParameters instanceof ParametersWithIV) {
            this.iv = ((ParametersWithIV)cipherParameters).getIV();
            parameters = ((ParametersWithIV)cipherParameters).getParameters();
        }
        else {
            this.iv = new byte[0];
        }
        if (parameters instanceof KeyParameter) {
            this.key = ((KeyParameter)parameters).getKey();
            this.init();
            this.initialised = true;
            return;
        }
        throw new IllegalArgumentException("Invalid parameter passed to HC128 init - " + cipherParameters.getClass().getName());
    }
    
    private byte getByte() {
        if (this.idx == 0) {
            final int step = this.step();
            this.buf[0] = (byte)(step & 0xFF);
            final int n = step >> 8;
            this.buf[1] = (byte)(n & 0xFF);
            final int n2 = n >> 8;
            this.buf[2] = (byte)(n2 & 0xFF);
            this.buf[3] = (byte)(n2 >> 8 & 0xFF);
        }
        final byte b = this.buf[this.idx];
        this.idx = (this.idx + 1 & 0x3);
        return b;
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n3 + n2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            array2[n3 + i] = (byte)(array[n + i] ^ this.getByte());
        }
        return n2;
    }
    
    public void reset() {
        this.init();
    }
    
    public byte returnByte(final byte b) {
        return (byte)(b ^ this.getByte());
    }
}
