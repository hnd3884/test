package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public class LookaheadInputStream extends InputStream
{
    private final byte[] buffer;
    private InputStream stream;
    private int buffered;
    private int position;
    private int mark;
    
    public LookaheadInputStream(final InputStream stream, final int n) {
        this.buffered = 0;
        this.position = 0;
        this.mark = 0;
        this.stream = stream;
        this.buffer = new byte[n];
        if (stream != null) {
            stream.mark(n);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.reset();
            this.stream = null;
        }
    }
    
    private void fill() throws IOException {
        if (this.available() == 0 && this.buffered < this.buffer.length && this.stream != null) {
            final int n = this.stream.read(this.buffer, this.buffered, this.buffer.length - this.buffered);
            if (n != -1) {
                this.buffered += n;
            }
            else {
                this.close();
            }
        }
    }
    
    @Override
    public int read() throws IOException {
        this.fill();
        if (this.buffered > this.position) {
            return 0xFF & this.buffer[this.position++];
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) throws IOException {
        this.fill();
        if (this.buffered > this.position) {
            len = Math.min(len, this.buffered - this.position);
            System.arraycopy(this.buffer, this.position, b, off, len);
            this.position += len;
            return len;
        }
        return -1;
    }
    
    @Override
    public long skip(long n) throws IOException {
        this.fill();
        n = Math.min(n, this.available());
        this.position += (int)n;
        return n;
    }
    
    @Override
    public int available() {
        return this.buffered - this.position;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.mark = this.position;
    }
    
    @Override
    public synchronized void reset() {
        this.position = this.mark;
    }
}
