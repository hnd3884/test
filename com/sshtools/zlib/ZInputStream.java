package com.sshtools.zlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class ZInputStream extends FilterInputStream
{
    protected ZStream z;
    protected int bufsize;
    protected int flush;
    protected byte[] buf;
    protected byte[] buf1;
    protected boolean compress;
    private InputStream b;
    private boolean c;
    
    public ZInputStream(final InputStream b) {
        super(b);
        this.z = new ZStream();
        this.bufsize = 512;
        this.flush = 0;
        this.buf = new byte[this.bufsize];
        this.buf1 = new byte[1];
        this.b = null;
        this.c = false;
        this.b = b;
        this.z.inflateInit();
        this.compress = false;
        this.z.next_in = this.buf;
        this.z.next_in_index = 0;
        this.z.avail_in = 0;
    }
    
    public ZInputStream(final InputStream b, final int n) {
        super(b);
        this.z = new ZStream();
        this.bufsize = 512;
        this.flush = 0;
        this.buf = new byte[this.bufsize];
        this.buf1 = new byte[1];
        this.b = null;
        this.c = false;
        this.b = b;
        this.z.deflateInit(n);
        this.compress = true;
        this.z.next_in = this.buf;
        this.z.next_in_index = 0;
        this.z.avail_in = 0;
    }
    
    public int read() throws IOException {
        if (this.read(this.buf1, 0, 1) == -1) {
            return -1;
        }
        return this.buf1[0] & 0xFF;
    }
    
    public int read(final byte[] next_out, final int next_out_index, final int avail_out) throws IOException {
        if (avail_out == 0) {
            return 0;
        }
        this.z.next_out = next_out;
        this.z.next_out_index = next_out_index;
        this.z.avail_out = avail_out;
        int n;
        do {
            if (this.z.avail_in == 0 && !this.c) {
                this.z.next_in_index = 0;
                this.z.avail_in = this.b.read(this.buf, 0, this.bufsize);
                if (this.z.avail_in == -1) {
                    this.z.avail_in = 0;
                    this.c = true;
                }
            }
            if (this.compress) {
                n = this.z.deflate(this.flush);
            }
            else {
                n = this.z.inflate(this.flush);
            }
            if (this.c && n == -5) {
                return -1;
            }
            if (n != 0 && n != 1) {
                throw new ZStreamException((this.compress ? "de" : "in") + "flating: " + this.z.msg);
            }
            if ((this.c || n == 1) && this.z.avail_out == avail_out) {
                return -1;
            }
        } while (this.z.avail_out == avail_out && n == 0);
        return avail_out - this.z.avail_out;
    }
    
    public long skip(final long n) throws IOException {
        int n2 = 512;
        if (n < n2) {
            n2 = (int)n;
        }
        return this.read(new byte[n2]);
    }
    
    public int getFlushMode() {
        return this.flush;
    }
    
    public void setFlushMode(final int flush) {
        this.flush = flush;
    }
    
    public long getTotalIn() {
        return this.z.total_in;
    }
    
    public long getTotalOut() {
        return this.z.total_out;
    }
    
    public void close() throws IOException {
        this.b.close();
    }
}
