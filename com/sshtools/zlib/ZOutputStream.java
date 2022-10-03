package com.sshtools.zlib;

import java.io.IOException;
import java.io.OutputStream;

public class ZOutputStream extends OutputStream
{
    protected ZStream z;
    protected int bufsize;
    protected int flush;
    protected byte[] buf;
    protected byte[] buf1;
    protected boolean compress;
    private OutputStream b;
    
    public ZOutputStream(final OutputStream b) {
        this.z = new ZStream();
        this.bufsize = 512;
        this.flush = 0;
        this.buf = new byte[this.bufsize];
        this.buf1 = new byte[1];
        this.b = b;
        this.z.inflateInit();
        this.compress = false;
    }
    
    public ZOutputStream(final OutputStream b, final int n) {
        this.z = new ZStream();
        this.bufsize = 512;
        this.flush = 0;
        this.buf = new byte[this.bufsize];
        this.buf1 = new byte[1];
        this.b = b;
        this.z.deflateInit(n);
        this.compress = true;
    }
    
    public void write(final int n) throws IOException {
        this.buf1[0] = (byte)n;
        this.write(this.buf1, 0, 1);
    }
    
    public void write(final byte[] next_in, final int next_in_index, final int avail_in) throws IOException {
        if (avail_in == 0) {
            return;
        }
        this.z.next_in = next_in;
        this.z.next_in_index = next_in_index;
        this.z.avail_in = avail_in;
        do {
            this.z.next_out = this.buf;
            this.z.next_out_index = 0;
            this.z.avail_out = this.bufsize;
            int n;
            if (this.compress) {
                n = this.z.deflate(this.flush);
            }
            else {
                n = this.z.inflate(this.flush);
            }
            if (n != 0) {
                throw new ZStreamException((this.compress ? "de" : "in") + "flating: " + this.z.msg);
            }
            this.b.write(this.buf, 0, this.bufsize - this.z.avail_out);
        } while (this.z.avail_in > 0 || this.z.avail_out == 0);
    }
    
    public int getFlushMode() {
        return this.flush;
    }
    
    public void setFlushMode(final int flush) {
        this.flush = flush;
    }
    
    public void finish() throws IOException {
        do {
            this.z.next_out = this.buf;
            this.z.next_out_index = 0;
            this.z.avail_out = this.bufsize;
            int n;
            if (this.compress) {
                n = this.z.deflate(4);
            }
            else {
                n = this.z.inflate(4);
            }
            if (n != 1 && n != 0) {
                throw new ZStreamException((this.compress ? "de" : "in") + "flating: " + this.z.msg);
            }
            if (this.bufsize - this.z.avail_out <= 0) {
                continue;
            }
            this.b.write(this.buf, 0, this.bufsize - this.z.avail_out);
        } while (this.z.avail_in > 0 || this.z.avail_out == 0);
        this.flush();
    }
    
    public void end() {
        if (this.z == null) {
            return;
        }
        if (this.compress) {
            this.z.deflateEnd();
        }
        else {
            this.z.inflateEnd();
        }
        this.z.free();
        this.z = null;
    }
    
    public void close() throws IOException {
        try {
            this.finish();
        }
        catch (final IOException ex) {}
        finally {
            this.end();
            this.b.close();
            this.b = null;
        }
    }
    
    public long getTotalIn() {
        return this.z.total_in;
    }
    
    public long getTotalOut() {
        return this.z.total_out;
    }
    
    public void flush() throws IOException {
        this.b.flush();
    }
}
