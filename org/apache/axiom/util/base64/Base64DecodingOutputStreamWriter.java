package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;

public class Base64DecodingOutputStreamWriter extends AbstractBase64DecodingWriter
{
    private final OutputStream stream;
    
    public Base64DecodingOutputStreamWriter(final OutputStream stream) {
        this.stream = stream;
    }
    
    @Override
    protected void doWrite(final byte[] b, final int len) throws IOException {
        this.stream.write(b, 0, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
