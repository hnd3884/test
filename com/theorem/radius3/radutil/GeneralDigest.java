package com.theorem.radius3.radutil;

public abstract class GeneralDigest implements Digest
{
    private byte[] a;
    private int b;
    private long c;
    
    protected GeneralDigest() {
        this.a = new byte[4];
        this.b = 0;
    }
    
    public final void update(final byte b) {
        this.a[this.b++] = b;
        if (this.b == this.a.length) {
            this.a(this.a, 0);
            this.b = 0;
        }
        ++this.c;
    }
    
    public final void update(final byte[] array, int n, int i) {
        while (this.b != 0 && i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
        while (i > this.a.length) {
            this.a(array, n);
            n += this.a.length;
            i -= this.a.length;
            this.c += this.a.length;
        }
        while (i > 0) {
            this.update(array[n]);
            ++n;
            --i;
        }
    }
    
    public final void finish() {
        final long n = this.c << 3;
        this.update((byte)(-128));
        while (this.b != 0) {
            this.update((byte)0);
        }
        this.a(n);
        this.a();
    }
    
    public final void reset() {
        this.c = 0L;
        this.b = 0;
        for (int i = 0; i < this.a.length; ++i) {
            this.a[i] = 0;
        }
    }
    
    protected abstract void a(final byte[] p0, final int p1);
    
    protected abstract void a(final long p0);
    
    protected abstract void a();
}
