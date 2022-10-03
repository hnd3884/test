package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;

public class Base64EncodingOutputStream extends AbstractBase64EncodingOutputStream
{
    private final OutputStream parent;
    private final byte[] buffer;
    private int len;
    
    public Base64EncodingOutputStream(final OutputStream parent, final int bufferSize) {
        this.parent = parent;
        this.buffer = new byte[bufferSize];
    }
    
    public Base64EncodingOutputStream(final OutputStream parent) {
        this(parent, 4096);
    }
    
    @Override
    protected void doWrite(final byte[] b) throws IOException {
        if (this.buffer.length - this.len < 4) {
            this.flushBuffer();
        }
        System.arraycopy(b, 0, this.buffer, this.len, 4);
        this.len += 4;
    }
    
    @Override
    protected void flushBuffer() throws IOException {
        this.parent.write(this.buffer, 0, this.len);
        this.len = 0;
    }
    
    @Override
    protected void doFlush() throws IOException {
        this.parent.flush();
    }
    
    @Override
    protected void doClose() throws IOException {
        this.parent.close();
    }
}
