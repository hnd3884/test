package org.bouncycastle.crypto.macs;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.Mac;

public class DSTU7624Mac implements Mac
{
    private static final int BITS_IN_BYTE = 8;
    private byte[] buf;
    private int bufOff;
    private int macSize;
    private int blockSize;
    private DSTU7624Engine engine;
    private byte[] c;
    private byte[] cTemp;
    private byte[] kDelta;
    
    public DSTU7624Mac(final int n, final int n2) {
        this.engine = new DSTU7624Engine(n);
        this.blockSize = n / 8;
        this.macSize = n2 / 8;
        this.c = new byte[this.blockSize];
        this.kDelta = new byte[this.blockSize];
        this.cTemp = new byte[this.blockSize];
        this.buf = new byte[this.blockSize];
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof KeyParameter) {
            this.engine.init(true, cipherParameters);
            this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
            return;
        }
        throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Mac");
    }
    
    public String getAlgorithmName() {
        return "DSTU7624Mac";
    }
    
    public int getMacSize() {
        return this.macSize;
    }
    
    public void update(final byte b) {
        if (this.bufOff == this.buf.length) {
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = b;
    }
    
    public void update(final byte[] array, int n, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("can't have a negative input length!");
        }
        final int blockSize = this.engine.getBlockSize();
        final int n2 = blockSize - this.bufOff;
        if (i > n2) {
            System.arraycopy(array, n, this.buf, this.bufOff, n2);
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
            for (i -= n2, n += n2; i > blockSize; i -= blockSize, n += blockSize) {
                this.processBlock(array, n);
            }
        }
        System.arraycopy(array, n, this.buf, this.bufOff, i);
        this.bufOff += i;
    }
    
    private void processBlock(final byte[] array, final int n) {
        this.xor(this.c, 0, array, n, this.cTemp);
        this.engine.processBlock(this.cTemp, 0, this.c, 0);
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        if (this.bufOff % this.buf.length != 0) {
            throw new DataLengthException("input must be a multiple of blocksize");
        }
        this.xor(this.c, 0, this.buf, 0, this.cTemp);
        this.xor(this.cTemp, 0, this.kDelta, 0, this.c);
        this.engine.processBlock(this.c, 0, this.c, 0);
        if (this.macSize + n > array.length) {
            throw new OutputLengthException("output buffer too short");
        }
        System.arraycopy(this.c, 0, array, n, this.macSize);
        return this.macSize;
    }
    
    public void reset() {
        Arrays.fill(this.c, (byte)0);
        Arrays.fill(this.cTemp, (byte)0);
        Arrays.fill(this.kDelta, (byte)0);
        Arrays.fill(this.buf, (byte)0);
        this.engine.reset();
        this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
        this.bufOff = 0;
    }
    
    private void xor(final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3) {
        if (array.length - n < this.blockSize || array2.length - n2 < this.blockSize || array3.length < this.blockSize) {
            throw new IllegalArgumentException("some of input buffers too short");
        }
        for (int i = 0; i < this.blockSize; ++i) {
            array3[i] = (byte)(array[i + n] ^ array2[i + n2]);
        }
    }
}
