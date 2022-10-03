package cryptix.jce.provider.md;

import java.security.DigestException;
import java.security.MessageDigestSpi;

abstract class PaddingMD extends MessageDigestSpi
{
    private static final int DEFAULT_BLOCKSIZE = 64;
    static final int MODE_MD = 0;
    static final int MODE_SHA = 1;
    static final int MODE_TIGER = 2;
    private final int blockSize;
    private final int hashSize;
    private final byte[] buf;
    private int bufOff;
    private long byteCount;
    private final int mode;
    
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("You have just found a bug!");
    }
    
    protected int engineGetDigestLength() {
        return this.hashSize;
    }
    
    protected void engineUpdate(final byte input) {
        ++this.byteCount;
        this.buf[this.bufOff++] = input;
        if (this.bufOff == this.blockSize) {
            this.coreUpdate(this.buf, 0);
            this.bufOff = 0;
        }
    }
    
    protected void engineUpdate(final byte[] input, int offset, int length) {
        this.byteCount += length;
        int n;
        while (length >= (n = this.blockSize - this.bufOff)) {
            System.arraycopy(input, offset, this.buf, this.bufOff, n);
            this.coreUpdate(this.buf, 0);
            length -= n;
            offset += n;
            this.bufOff = 0;
        }
        System.arraycopy(input, offset, this.buf, this.bufOff, length);
        this.bufOff += length;
    }
    
    protected byte[] engineDigest() {
        final byte[] tmp = new byte[this.hashSize];
        this.privateDigest(tmp, 0, this.hashSize);
        return tmp;
    }
    
    protected int engineDigest(final byte[] buf, final int offset, final int len) throws DigestException {
        if (len < this.hashSize) {
            throw new DigestException();
        }
        return this.privateDigest(buf, offset, len);
    }
    
    private int privateDigest(final byte[] buf, final int offset, final int len) {
        this.buf[this.bufOff++] = (byte)((this.mode == 2) ? 1 : -128);
        final int lenOfBitLen = (this.blockSize == 128) ? 16 : 8;
        final int C = this.blockSize - lenOfBitLen;
        if (this.bufOff > C) {
            while (this.bufOff < this.blockSize) {
                this.buf[this.bufOff++] = 0;
            }
            this.coreUpdate(this.buf, 0);
            this.bufOff = 0;
        }
        while (this.bufOff < C) {
            this.buf[this.bufOff++] = 0;
        }
        final long bitCount = this.byteCount * 8L;
        if (this.blockSize == 128) {
            for (int i = 0; i < 8; ++i) {
                this.buf[this.bufOff++] = 0;
            }
        }
        if (this.mode == 1) {
            for (int i = 56; i >= 0; i -= 8) {
                this.buf[this.bufOff++] = (byte)(bitCount >>> i);
            }
        }
        else {
            for (int i = 0; i < 64; i += 8) {
                this.buf[this.bufOff++] = (byte)(bitCount >>> i);
            }
        }
        this.coreUpdate(this.buf, 0);
        this.coreDigest(buf, offset);
        this.engineReset();
        return this.hashSize;
    }
    
    protected void engineReset() {
        this.bufOff = 0;
        this.byteCount = 0L;
        this.coreReset();
    }
    
    protected abstract void coreDigest(final byte[] p0, final int p1);
    
    protected abstract void coreReset();
    
    protected abstract void coreUpdate(final byte[] p0, final int p1);
    
    protected PaddingMD(final int hashSize, final int mode) {
        this(64, hashSize, mode);
    }
    
    protected PaddingMD(final int blockSize, final int hashSize, final int mode) {
        if (blockSize != 64 && blockSize != 128) {
            throw new RuntimeException("blockSize must be 64 or 128!");
        }
        this.blockSize = blockSize;
        this.hashSize = hashSize;
        this.buf = new byte[blockSize];
        this.bufOff = 0;
        this.byteCount = 0L;
        this.mode = mode;
    }
    
    protected PaddingMD(final PaddingMD src) {
        this.blockSize = src.blockSize;
        this.hashSize = src.hashSize;
        this.buf = src.buf.clone();
        this.bufOff = src.bufOff;
        this.byteCount = src.byteCount;
        this.mode = src.mode;
    }
}
