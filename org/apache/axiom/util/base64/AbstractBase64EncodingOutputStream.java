package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractBase64EncodingOutputStream extends OutputStream
{
    private final boolean ignoreFlush;
    private final byte[] in;
    private final byte[] out;
    private int rest;
    private boolean completed;
    
    public AbstractBase64EncodingOutputStream(final boolean ignoreFlush) {
        this.in = new byte[3];
        this.out = new byte[4];
        this.ignoreFlush = ignoreFlush;
    }
    
    public AbstractBase64EncodingOutputStream() {
        this(false);
    }
    
    @Override
    public final void write(final byte[] b, int off, int len) throws IOException {
        if (this.completed) {
            throw new IOException("Attempt to write data after base64 encoding has been completed");
        }
        if (this.rest > 0) {
            while (len > 0 && this.rest < 3) {
                this.in[this.rest++] = b[off++];
                --len;
            }
            if (this.rest == 3) {
                this.encode(this.in, 0, 3);
                this.rest = 0;
            }
        }
        while (len >= 3) {
            this.encode(b, off, 3);
            off += 3;
            len -= 3;
        }
        while (len > 0) {
            this.in[this.rest++] = b[off++];
            --len;
        }
    }
    
    @Override
    public final void write(final int b) throws IOException {
        this.in[this.rest++] = (byte)b;
        if (this.rest == 3) {
            this.encode(this.in, 0, 3);
            this.rest = 0;
        }
    }
    
    public final void complete() throws IOException {
        if (!this.completed) {
            if (this.rest > 0) {
                this.encode(this.in, 0, this.rest);
            }
            this.flushBuffer();
            this.completed = true;
        }
    }
    
    private void encode(final byte[] data, final int off, final int len) throws IOException {
        if (len == 1) {
            final int i = data[off] & 0xFF;
            this.out[0] = Base64Constants.S_BASE64CHAR[i >> 2];
            this.out[1] = Base64Constants.S_BASE64CHAR[i << 4 & 0x3F];
            this.out[2] = 61;
            this.out[3] = 61;
        }
        else if (len == 2) {
            final int i = ((data[off] & 0xFF) << 8) + (data[off + 1] & 0xFF);
            this.out[0] = Base64Constants.S_BASE64CHAR[i >> 10];
            this.out[1] = Base64Constants.S_BASE64CHAR[i >> 4 & 0x3F];
            this.out[2] = Base64Constants.S_BASE64CHAR[i << 2 & 0x3F];
            this.out[3] = 61;
        }
        else {
            final int i = ((data[off] & 0xFF) << 16) + ((data[off + 1] & 0xFF) << 8) + (data[off + 2] & 0xFF);
            this.out[0] = Base64Constants.S_BASE64CHAR[i >> 18];
            this.out[1] = Base64Constants.S_BASE64CHAR[i >> 12 & 0x3F];
            this.out[2] = Base64Constants.S_BASE64CHAR[i >> 6 & 0x3F];
            this.out[3] = Base64Constants.S_BASE64CHAR[i & 0x3F];
        }
        this.doWrite(this.out);
    }
    
    @Override
    public final void flush() throws IOException {
        if (!this.ignoreFlush) {
            this.flushBuffer();
            this.doFlush();
        }
    }
    
    @Override
    public final void close() throws IOException {
        this.complete();
        this.doClose();
    }
    
    protected abstract void doWrite(final byte[] p0) throws IOException;
    
    protected abstract void flushBuffer() throws IOException;
    
    protected abstract void doFlush() throws IOException;
    
    protected abstract void doClose() throws IOException;
}
