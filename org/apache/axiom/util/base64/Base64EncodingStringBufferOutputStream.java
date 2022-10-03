package org.apache.axiom.util.base64;

import java.io.IOException;

public class Base64EncodingStringBufferOutputStream extends AbstractBase64EncodingOutputStream
{
    private final Appendable buffer;
    
    public Base64EncodingStringBufferOutputStream(final StringBuffer buffer) {
        this.buffer = buffer;
    }
    
    public Base64EncodingStringBufferOutputStream(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    @Override
    protected void doWrite(final byte[] b) throws IOException {
        for (int i = 0; i < 4; ++i) {
            this.buffer.append((char)(b[i] & 0xFF));
        }
    }
    
    @Override
    protected void flushBuffer() throws IOException {
    }
    
    @Override
    protected void doClose() throws IOException {
    }
    
    @Override
    protected void doFlush() throws IOException {
    }
}
