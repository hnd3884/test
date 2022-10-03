package com.sshtools.zlib;

public final class ZStream
{
    public byte[] next_in;
    public int next_in_index;
    public int avail_in;
    public long total_in;
    public byte[] next_out;
    public int next_out_index;
    public int avail_out;
    public long total_out;
    public String msg;
    Deflate d;
    e c;
    int e;
    public long adler;
    b b;
    
    public ZStream() {
        this.b = new b();
    }
    
    public int inflateInit() {
        return this.inflateInit(15);
    }
    
    public int inflateInit(final int n) {
        this.c = new e();
        return this.c.c(this, n);
    }
    
    public int inflate(final int n) {
        if (this.c == null) {
            return -2;
        }
        return this.c.b(this, n);
    }
    
    public int inflateEnd() {
        if (this.c == null) {
            return -2;
        }
        final int b = this.c.b(this);
        this.c = null;
        return b;
    }
    
    public int inflateSync() {
        if (this.c == null) {
            return -2;
        }
        return this.c.d(this);
    }
    
    public int inflateSetDictionary(final byte[] array, final int n) {
        if (this.c == null) {
            return -2;
        }
        return this.c.b(this, array, n);
    }
    
    public int deflateInit(final int n) {
        return this.deflateInit(n, 15);
    }
    
    public int deflateInit(final int n, final int n2) {
        this.d = new Deflate();
        return this.d.c(this, n, n2);
    }
    
    public int deflate(final int n) {
        if (this.d == null) {
            return -2;
        }
        return this.d.b(this, n);
    }
    
    public int deflateEnd() {
        if (this.d == null) {
            return -2;
        }
        final int k = this.d.k();
        this.d = null;
        return k;
    }
    
    public int deflateParams(final int n, final int n2) {
        if (this.d == null) {
            return -2;
        }
        return this.d.b(this, n, n2);
    }
    
    public int deflateSetDictionary(final byte[] array, final int n) {
        if (this.d == null) {
            return -2;
        }
        return this.d.b(this, array, n);
    }
    
    void b() {
        int n = this.d.q;
        if (n > this.avail_out) {
            n = this.avail_out;
        }
        if (n == 0) {
            return;
        }
        if (this.d.bc.length <= this.d.l || this.next_out.length <= this.next_out_index || this.d.bc.length < this.d.l + n || this.next_out.length < this.next_out_index + n) {
            System.out.println(this.d.bc.length + ", " + this.d.l + ", " + this.next_out.length + ", " + this.next_out_index + ", " + n);
            System.out.println("avail_out=" + this.avail_out);
        }
        System.arraycopy(this.d.bc, this.d.l, this.next_out, this.next_out_index, n);
        this.next_out_index += n;
        final Deflate d = this.d;
        d.l += n;
        this.total_out += n;
        this.avail_out -= n;
        final Deflate d2 = this.d;
        d2.q -= n;
        if (this.d.q == 0) {
            this.d.l = 0;
        }
    }
    
    int b(final byte[] array, final int n, final int n2) {
        int avail_in = this.avail_in;
        if (avail_in > n2) {
            avail_in = n2;
        }
        if (avail_in == 0) {
            return 0;
        }
        this.avail_in -= avail_in;
        if (this.d.hc == 0) {
            this.adler = this.b.b(this.adler, this.next_in, this.next_in_index, avail_in);
        }
        System.arraycopy(this.next_in, this.next_in_index, array, n, avail_in);
        this.next_in_index += avail_in;
        this.total_in += avail_in;
        return avail_in;
    }
    
    public void free() {
        this.next_in = null;
        this.next_out = null;
        this.msg = null;
        this.b = null;
    }
}
