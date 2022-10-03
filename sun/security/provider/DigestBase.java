package sun.security.provider;

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
    protected final void engineUpdate(final byte[] array, int implCompressMultiBlock, int bufOfs) {
        if (bufOfs == 0) {
            return;
        }
        if (implCompressMultiBlock < 0 || bufOfs < 0 || implCompressMultiBlock > array.length - bufOfs) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (this.bytesProcessed < 0L) {
            this.engineReset();
        }
        this.bytesProcessed += bufOfs;
        if (this.bufOfs != 0) {
            final int min = Math.min(bufOfs, this.blockSize - this.bufOfs);
            System.arraycopy(array, implCompressMultiBlock, this.buffer, this.bufOfs, min);
            this.bufOfs += min;
            implCompressMultiBlock += min;
            bufOfs -= min;
            if (this.bufOfs >= this.blockSize) {
                this.implCompress(this.buffer, 0);
                this.bufOfs = 0;
            }
        }
        if (bufOfs >= this.blockSize) {
            final int n = implCompressMultiBlock + bufOfs;
            implCompressMultiBlock = this.implCompressMultiBlock(array, implCompressMultiBlock, n - this.blockSize);
            bufOfs = n - implCompressMultiBlock;
        }
        if (bufOfs > 0) {
            System.arraycopy(array, implCompressMultiBlock, this.buffer, 0, bufOfs);
            this.bufOfs = bufOfs;
        }
    }
    
    private int implCompressMultiBlock(final byte[] array, final int n, final int n2) {
        this.implCompressMultiBlockCheck(array, n, n2);
        return this.implCompressMultiBlock0(array, n, n2);
    }
    
    private int implCompressMultiBlock0(final byte[] array, int i, final int n) {
        while (i <= n) {
            this.implCompress(array, i);
            i += this.blockSize;
        }
        return i;
    }
    
    private void implCompressMultiBlockCheck(final byte[] array, final int n, final int n2) {
        if (n2 < 0) {
            return;
        }
        Objects.requireNonNull(array);
        if (n < 0 || n >= array.length) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        final int n3 = n2 / this.blockSize * this.blockSize + this.blockSize - 1;
        if (n3 >= array.length) {
            throw new ArrayIndexOutOfBoundsException(n3);
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
        final byte[] array = new byte[this.digestLength];
        try {
            this.engineDigest(array, 0, array.length);
        }
        catch (final DigestException ex) {
            throw (ProviderException)new ProviderException("Internal error").initCause(ex);
        }
        return array;
    }
    
    @Override
    protected final int engineDigest(final byte[] array, final int n, final int n2) throws DigestException {
        if (n2 < this.digestLength) {
            throw new DigestException("Length must be at least " + this.digestLength + " for " + this.algorithm + "digests");
        }
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new DigestException("Buffer too short to store digest");
        }
        if (this.bytesProcessed < 0L) {
            this.engineReset();
        }
        this.implDigest(array, n);
        this.bytesProcessed = -1L;
        return this.digestLength;
    }
    
    abstract void implCompress(final byte[] p0, final int p1);
    
    abstract void implDigest(final byte[] p0, final int p1);
    
    abstract void implReset();
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final DigestBase digestBase = (DigestBase)super.clone();
        digestBase.buffer = digestBase.buffer.clone();
        return digestBase;
    }
    
    static {
        (padding = new byte[136])[0] = -128;
    }
}
