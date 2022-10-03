package org.openjsse.sun.security.provider;

import java.security.DigestException;
import java.security.ProviderException;
import java.util.Arrays;
import java.util.Objects;
import java.security.MessageDigestSpi;

abstract class DigestBase extends MessageDigestSpi implements Cloneable
{
    private byte[] oneByte;
    private final String algorithm;
    private final int digestLength;
    private final int blockSize;
    byte[] buffer;
    private int bufOfs;
    long bytesProcessed;
    static final byte[] padding;
    
    DigestBase(final String algorithm, final int digestLength, final int blockSize) {
        this.algorithm = algorithm;
        this.digestLength = digestLength;
        this.blockSize = blockSize;
        this.buffer = new byte[blockSize];
    }
    
    @Override
    protected final int engineGetDigestLength() {
        return this.digestLength;
    }
    
    @Override
    protected final void engineUpdate(final byte b) {
        if (this.oneByte == null) {
            this.oneByte = new byte[1];
        }
        this.oneByte[0] = b;
        this.engineUpdate(this.oneByte, 0, 1);
    }
    
    @Override
    protected final void engineUpdate(final byte[] b, int ofs, int len) {
        if (len == 0) {
            return;
        }
        if (ofs < 0 || len < 0 || ofs > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (this.bytesProcessed < 0L) {
            this.engineReset();
        }
        this.bytesProcessed += len;
        if (this.bufOfs != 0) {
            final int n = Math.min(len, this.blockSize - this.bufOfs);
            System.arraycopy(b, ofs, this.buffer, this.bufOfs, n);
            this.bufOfs += n;
            ofs += n;
            len -= n;
            if (this.bufOfs >= this.blockSize) {
                this.implCompress(this.buffer, 0);
                this.bufOfs = 0;
            }
        }
        if (len >= this.blockSize) {
            final int limit = ofs + len;
            ofs = this.implCompressMultiBlock(b, ofs, limit - this.blockSize);
            len = limit - ofs;
        }
        if (len > 0) {
            System.arraycopy(b, ofs, this.buffer, 0, len);
            this.bufOfs = len;
        }
    }
    
    private int implCompressMultiBlock(final byte[] b, final int ofs, final int limit) {
        this.implCompressMultiBlockCheck(b, ofs, limit);
        return this.implCompressMultiBlock0(b, ofs, limit);
    }
    
    private int implCompressMultiBlock0(final byte[] b, int ofs, final int limit) {
        while (ofs <= limit) {
            this.implCompress(b, ofs);
            ofs += this.blockSize;
        }
        return ofs;
    }
    
    private void implCompressMultiBlockCheck(final byte[] b, final int ofs, final int limit) {
        if (limit < 0) {
            return;
        }
        Objects.requireNonNull(b);
        if (ofs < 0 || ofs >= b.length) {
            throw new ArrayIndexOutOfBoundsException(ofs);
        }
        final int endIndex = limit / this.blockSize * this.blockSize + this.blockSize - 1;
        if (endIndex >= b.length) {
            throw new ArrayIndexOutOfBoundsException(endIndex);
        }
    }
    
    @Override
    protected final void engineReset() {
        if (this.bytesProcessed == 0L) {
            return;
        }
        this.implReset();
        this.bufOfs = 0;
        this.bytesProcessed = 0L;
        Arrays.fill(this.buffer, (byte)0);
    }
    
    @Override
    protected final byte[] engineDigest() {
        final byte[] b = new byte[this.digestLength];
        try {
            this.engineDigest(b, 0, b.length);
        }
        catch (final DigestException e) {
            throw (ProviderException)new ProviderException("Internal error").initCause(e);
        }
        return b;
    }
    
    @Override
    protected final int engineDigest(final byte[] out, final int ofs, final int len) throws DigestException {
        if (len < this.digestLength) {
            throw new DigestException("Length must be at least " + this.digestLength + " for " + this.algorithm + "digests");
        }
        if (ofs < 0 || len < 0 || ofs > out.length - len) {
            throw new DigestException("Buffer too short to store digest");
        }
        if (this.bytesProcessed < 0L) {
            this.engineReset();
        }
        this.implDigest(out, ofs);
        this.bytesProcessed = -1L;
        return this.digestLength;
    }
    
    abstract void implCompress(final byte[] p0, final int p1);
    
    abstract void implDigest(final byte[] p0, final int p1);
    
    abstract void implReset();
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final DigestBase copy = (DigestBase)super.clone();
        copy.buffer = copy.buffer.clone();
        return copy;
    }
    
    static {
        (padding = new byte[136])[0] = -128;
    }
}
