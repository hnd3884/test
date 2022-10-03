package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.ExtendedDigest;

public abstract class GeneralDigest implements ExtendedDigest, Memoable
{
    private static final int BYTE_LENGTH = 64;
    private final byte[] xBuf;
    private int xBufOff;
    private long byteCount;
    
    protected GeneralDigest() {
        this.xBuf = new byte[4];
        this.xBufOff = 0;
    }
    
    protected GeneralDigest(final GeneralDigest generalDigest) {
        this.xBuf = new byte[4];
        this.copyIn(generalDigest);
    }
    
    protected GeneralDigest(final byte[] array) {
        System.arraycopy(array, 0, this.xBuf = new byte[4], 0, this.xBuf.length);
        this.xBufOff = Pack.bigEndianToInt(array, 4);
        this.byteCount = Pack.bigEndianToLong(array, 8);
    }
    
    protected void copyIn(final GeneralDigest generalDigest) {
        System.arraycopy(generalDigest.xBuf, 0, this.xBuf, 0, generalDigest.xBuf.length);
        this.xBufOff = generalDigest.xBufOff;
        this.byteCount = generalDigest.byteCount;
    }
    
    public void update(final byte b) {
        this.xBuf[this.xBufOff++] = b;
        if (this.xBufOff == this.xBuf.length) {
            this.processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        ++this.byteCount;
    }
    
    public void update(final byte[] array, final int n, int max) {
        max = Math.max(0, max);
        int i = 0;
        if (this.xBufOff != 0) {
            while (i < max) {
                this.xBuf[this.xBufOff++] = array[n + i++];
                if (this.xBufOff == 4) {
                    this.processWord(this.xBuf, 0);
                    this.xBufOff = 0;
                    break;
                }
            }
        }
        while (i < (max - i & 0xFFFFFFFC) + i) {
            this.processWord(array, n + i);
            i += 4;
        }
        while (i < max) {
            this.xBuf[this.xBufOff++] = array[n + i++];
        }
        this.byteCount += max;
    }
    
    public void finish() {
        final long n = this.byteCount << 3;
        this.update((byte)(-128));
        while (this.xBufOff != 0) {
            this.update((byte)0);
        }
        this.processLength(n);
        this.processBlock();
    }
    
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; ++i) {
            this.xBuf[i] = 0;
        }
    }
    
    protected void populateState(final byte[] array) {
        System.arraycopy(this.xBuf, 0, array, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, array, 4);
        Pack.longToBigEndian(this.byteCount, array, 8);
    }
    
    public int getByteLength() {
        return 64;
    }
    
    protected abstract void processWord(final byte[] p0, final int p1);
    
    protected abstract void processLength(final long p0);
    
    protected abstract void processBlock();
}
