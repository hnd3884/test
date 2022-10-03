package org.apache.http.entity;

import org.apache.http.impl.io.EmptyInputStream;
import java.io.IOException;
import org.apache.http.util.Args;
import java.io.OutputStream;
import org.apache.http.util.Asserts;
import java.io.InputStream;

public class BasicHttpEntity extends AbstractHttpEntity
{
    private InputStream content;
    private long length;
    
    public BasicHttpEntity() {
        this.length = -1L;
    }
    
    @Override
    public long getContentLength() {
        return this.length;
    }
    
    @Override
    public InputStream getContent() throws IllegalStateException {
        Asserts.check(this.content != null, "Content has not been provided");
        return this.content;
    }
    
    @Override
    public boolean isRepeatable() {
        return false;
    }
    
    public void setContentLength(final long len) {
        this.length = len;
    }
    
    public void setContent(final InputStream inStream) {
        this.content = inStream;
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final InputStream inStream = this.getContent();
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = inStream.read(tmp)) != -1) {
                outStream.write(tmp, 0, l);
            }
        }
        finally {
            inStream.close();
        }
    }
    
    @Override
    public boolean isStreaming() {
        return this.content != null && this.content != EmptyInputStream.INSTANCE;
    }
}
