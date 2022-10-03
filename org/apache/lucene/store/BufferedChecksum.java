package org.apache.lucene.store;

import java.util.zip.Checksum;

public class BufferedChecksum implements Checksum
{
    private final Checksum in;
    private final byte[] buffer;
    private int upto;
    public static final int DEFAULT_BUFFERSIZE = 256;
    
    public BufferedChecksum(final Checksum in) {
        this(in, 256);
    }
    
    public BufferedChecksum(final Checksum in, final int bufferSize) {
        this.in = in;
        this.buffer = new byte[bufferSize];
    }
    
    @Override
    public void update(final int b) {
        if (this.upto == this.buffer.length) {
            this.flush();
        }
        this.buffer[this.upto++] = (byte)b;
    }
    
    @Override
    public void update(final byte[] b, final int off, final int len) {
        if (len >= this.buffer.length) {
            this.flush();
            this.in.update(b, off, len);
        }
        else {
            if (this.upto + len > this.buffer.length) {
                this.flush();
            }
            System.arraycopy(b, off, this.buffer, this.upto, len);
            this.upto += len;
        }
    }
    
    @Override
    public long getValue() {
        this.flush();
        return this.in.getValue();
    }
    
    @Override
    public void reset() {
        this.upto = 0;
        this.in.reset();
    }
    
    private void flush() {
        if (this.upto > 0) {
            this.in.update(this.buffer, 0, this.upto);
        }
        this.upto = 0;
    }
}
