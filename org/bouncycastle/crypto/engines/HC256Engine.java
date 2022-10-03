package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;

public class HC256Engine implements StreamCipher
{
    private int[] p;
    private int[] q;
    private int cnt;
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private byte[] buf;
    private int idx;
    
    public HC256Engine() {
        this.p = new int[1024];
        this.q = new int[1024];
        this.cnt = 0;
        this.buf = new byte[4];
        this.idx = 0;
    }
    
    private int step() {
        final int n = this.cnt & 0x3FF;
        int n6;
        if (this.cnt < 1024) {
            final int n2 = this.p[n - 3 & 0x3FF];
            final int n3 = this.p[n - 1023 & 0x3FF];
            final int[] p = this.p;
            final int n4 = n;
            p[n4] += this.p[n - 10 & 0x3FF] + (rotateRight(n2, 10) ^ rotateRight(n3, 23)) + this.q[(n2 ^ n3) & 0x3FF];
            final int n5 = this.p[n - 12 & 0x3FF];
            n6 = (this.q[n5 & 0xFF] + this.q[(n5 >> 8 & 0xFF) + 256] + this.q[(n5 >> 16 & 0xFF) + 512] + this.q[(n5 >> 24 & 0xFF) + 768] ^ this.p[n]);
        }
        else {
            final int n7 = this.q[n - 3 & 0x3FF];
            final int n8 = this.q[n - 1023 & 0x3FF];
            final int[] q = this.q;
            final int n9 = n;
            q[n9] += this.q[n - 10 & 0x3FF] + (rotateRight(n7, 10) ^ rotateRight(n8, 23)) + this.p[(n7 ^ n8) & 0x3FF];
            final int n10 = this.q[n - 12 & 0x3FF];
            n6 = (this.p[n10 & 0xFF] + this.p[(n10 >> 8 & 0xFF) + 256] + this.p[(n10 >> 16 & 0xFF) + 512] + this.p[(n10 >> 24 & 0xFF) + 768] ^ this.q[n]);
        }
        this.cnt = (this.cnt + 1 & 0x7FF);
        return n6;
    }
    
    private void init() {
        if (this.key.length != 32 && this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128/256 bits long");
        }
        if (this.iv.length < 16) {
            throw new IllegalArgumentException("The IV must be at least 128 bits long");
        }
        if (this.key.length != 32) {
            final byte[] key = new byte[32];
            System.arraycopy(this.key, 0, key, 0, this.key.length);
            System.arraycopy(this.key, 0, key, 16, this.key.length);
            this.key = key;
        }
        if (this.iv.length < 32) {
            final byte[] iv = new byte[32];
            System.arraycopy(this.iv, 0, iv, 0, this.iv.length);
            System.arraycopy(this.iv, 0, iv, this.iv.length, iv.length - this.iv.length);
            this.iv = iv;
        }
        this.idx = 0;
        this.cnt = 0;
        final int[] array = new int[2560];
        for (int i = 0; i < 32; ++i) {
            final int[] array2 = array;
            final int n = i >> 2;
            array2[n] |= (this.key[i] & 0xFF) << 8 * (i & 0x3);
        }
        for (int j = 0; j < 32; ++j) {
            final int[] array3 = array;
            final int n2 = (j >> 2) + 8;
            array3[n2] |= (this.iv[j] & 0xFF) << 8 * (j & 0x3);
        }
        for (int k = 16; k < 2560; ++k) {
            final int n3 = array[k - 2];
            final int n4 = array[k - 15];
            array[k] = (rotateRight(n3, 17) ^ rotateRight(n3, 19) ^ n3 >>> 10) + array[k - 7] + (rotateRight(n4, 7) ^ rotateRight(n4, 18) ^ n4 >>> 3) + array[k - 16] + k;
        }
        System.arraycopy(array, 512, this.p, 0, 1024);
        System.arraycopy(array, 1536, this.q, 0, 1024);
        for (int l = 0; l < 4096; ++l) {
            this.step();
        }
        this.cnt = 0;
    }
    
    public String getAlgorithmName() {
        return "HC-256";
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
        throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + cipherParameters.getClass().getName());
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
    
    private static int rotateRight(final int n, final int n2) {
        return n >>> n2 | n << -n2;
    }
}
