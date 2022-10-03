package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.Writer;

public class Base64EncodingWriterOutputStream extends AbstractBase64EncodingOutputStream
{
    private final Writer writer;
    private final char[] buffer;
    private int len;
    
    public Base64EncodingWriterOutputStream(final Writer writer, final int bufferSize, final boolean ignoreFlush) {
        super(ignoreFlush);
        this.writer = writer;
        this.buffer = new char[bufferSize];
    }
    
    public Base64EncodingWriterOutputStream(final Writer writer, final int bufferSize) {
        this(writer, bufferSize, false);
    }
    
    public Base64EncodingWriterOutputStream(final Writer writer) {
        this(writer, 4096, false);
    }
    
    @Override
    protected void doWrite(final byte[] b) throws IOException {
        if (this.buffer.length - this.len < 4) {
            this.flushBuffer();
        }
        for (int i = 0; i < 4; ++i) {
            this.buffer[this.len++] = (char)(b[i] & 0xFF);
        }
    }
    
    @Override
    protected void flushBuffer() throws IOException {
        this.writer.write(this.buffer, 0, this.len);
        this.len = 0;
    }
    
    @Override
    protected void doFlush() throws IOException {
        this.writer.flush();
    }
    
    @Override
    protected void doClose() throws IOException {
        this.writer.close();
    }
}
