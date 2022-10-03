package com.maverick.crypto.digests;

public abstract class GeneralDigest implements Digest
{
    private byte[] b;
    private int c;
    private long d;
    
    protected GeneralDigest() {
        this.b = new byte[4];
        this.c = 0;
    }
    
    protected GeneralDigest(final GeneralDigest generalDigest) {
        this.b = new byte[generalDigest.b.length];
        System.arraycopy(generalDigest.b, 0, this.b, 0, generalDigest.b.length);
        this.c = generalDigest.c;
        this.d = generalDigest.d;
    }
    
    public void update(final byte b) {
        this.b[this.c++] = b;
        if (this.c == this.b.length) {
            this.processWord(this.b, 0);
            this.c = 0;
        }
        ++this.d;
    }
    
    public void update(final byte[] array, int n, int i) {
        while (this.c != 0 && i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
        while (i > this.b.length) {
            this.processWord(array, n);
            n += this.b.length;
            i -= this.b.length;
            this.d += this.b.length;
        }
        while (i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
    }
    
    public void finish() {
        final long n = this.d << 3;
        this.update((byte)(-128));
        while (this.c != 0) {
            this.update((byte)0);
        }
        this.processLength(n);
        this.processBlock();
    }
    
    public void reset() {
        this.d = 0L;
        this.c = 0;
        for (int i = 0; i < this.b.length; ++i) {
            this.b[i] = 0;
        }
    }
    
    protected abstract void processWord(final byte[] p0, final int p1);
    
    protected abstract void processLength(final long p0);
    
    protected abstract void processBlock();
    
    public abstract int getDigestSize();
    
    public abstract int doFinal(final byte[] p0, final int p1);
    
    public abstract String getAlgorithmName();
}
